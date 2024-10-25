package com.naverapicalltest.apicalltest.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.io.FileInputStream;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

public class JwtUtil {

    // RSA 개인 키 파일의 경로를 지정합니다.
    private static final String PRIVATE_KEY_PATH = "src/main/resources/private_20241025105710.key";

    public static String generateJwtToken(String issuer, String subject, long expirationTimeInSeconds) {
        try {
            // RSA 개인 키를 로드
            RSAPublicKey publicKey = null;
            RSAPrivateKey privateKey = getPrivateKey(PRIVATE_KEY_PATH);

            // JWT 생성
            Algorithm algorithm = Algorithm.RSA256(publicKey, privateKey);
            String token = JWT.create()
                    .withIssuer(issuer) // 발급자
                    .withSubject(subject) // 토큰의 주제
                    .withIssuedAt(new Date()) // iat: JWT 생성 시간
                    .withExpiresAt(new Date(System.currentTimeMillis() + expirationTimeInSeconds * 1000)) // exp: JWT 만료 시간
                    .sign(algorithm); // 서명 생성

            return token;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("JWT 토큰 생성 중 오류 발생", e);
        }
    }

    // RSA 개인 키를 로드하는 메서드
    private static RSAPrivateKey getPrivateKey(String filePath) throws Exception {
        FileInputStream fis = new FileInputStream(filePath);
        byte[] keyBytes = new byte[fis.available()];
        fis.read(keyBytes);
        fis.close();

        String privateKeyPEM = new String(keyBytes);
        privateKeyPEM = privateKeyPEM.replace("-----BEGIN PRIVATE KEY-----", "")
                                     .replace("-----END PRIVATE KEY-----", "")
                                     .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(privateKeyPEM);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }

    // public static String base64Encoding(String data) throws Exception {
    //     return Base64.getEncoder().encodeToString(data.getBytes());
    // }

    public static void main(String[] args) {
        // 예제 실행
        // String issuer = "your-client-id"; // 클라이언트 ID
        // String subject = "your-subject"; // 토큰의 주제
        // long expirationTimeInSeconds = 3600; // 1시간

        // String jwtToken = generateJwtToken(issuer, subject, expirationTimeInSeconds);
        // System.out.println("Generated JWT Token: " + jwtToken);
    }
}
