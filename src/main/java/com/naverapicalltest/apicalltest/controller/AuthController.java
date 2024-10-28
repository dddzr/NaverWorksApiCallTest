package com.naverapicalltest.apicalltest.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.naverapicalltest.apicalltest.config.AuthConfig;
import com.naverapicalltest.apicalltest.config.JwtUtil;

import jakarta.servlet.http.HttpSession;


@RestController
public class AuthController {
    private final AuthConfig authConfig;
    private final String response_type = "code";
    private final String state = "test";

    @Autowired
    public AuthController(AuthConfig authConfig) {
        this.authConfig = authConfig;
    }

    /*
     * 로그인 화면에서 인증에 성공하면 지정한 Redirect URL(redirect_uri)로 리다이렉트된다. 
     */
    @GetMapping("/authorize")
    public String oAuthAuthorize(HttpSession session) throws IOException, InterruptedException {
        System.out.println("----- oAuthAuthorize strated. -----" );
        
        String refreshToken = getRefreshToken(session);
        if( refreshToken != null) {
            refreshAccessToken(refreshToken, session);
        }

        String url = String.format(
            "https://auth.worksmobile.com/oauth2/v2.0/authorize?client_id=%s&redirect_uri=%s&scope=%s&response_type=%s&state=%s",
            URLEncoder.encode(authConfig.getClientId(), StandardCharsets.UTF_8),
            URLEncoder.encode(authConfig.getRedirectUri(), StandardCharsets.UTF_8),
            URLEncoder.encode(authConfig.getScope(), StandardCharsets.UTF_8),
            URLEncoder.encode(response_type, StandardCharsets.UTF_8),
            URLEncoder.encode(state, StandardCharsets.UTF_8)            
        );
        System.out.println(url);

        // 전송 준비
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
   }

   @GetMapping("/redirect")
   public String handleRedirect(HttpSession session, @RequestParam("code") String authorizationCode, @RequestParam("state") String state) {	
       System.out.println("----- redirected. -----" );		
       System.out.println("code: " +  authorizationCode);
       
       // Authorization Code를 사용하여 Access Token 요청
       return getToken(session, authorizationCode, "OAuth");
    }

    
   @GetMapping("/jwtauthorize")
   public String jwtAuthorize(HttpSession session) throws Exception {
       System.out.println("----- jwtAuthorize strated. -----" );

       //{header BASE64 URL 인코딩}.{JSON Claim set BASE64 URL 인코딩}.{signature BASE64 URL 인코딩}
       String jwtToken = JwtUtil.generateJwtToken(authConfig.getClientId(), authConfig.getServerAccount(), 3600);
        // iss: Client ID, sub: Service Account, exp: 만료 시간 (Unix time, 초)
       String refreshToken = getRefreshToken(session);
       if( refreshToken != null) {
           refreshAccessToken(refreshToken, session);
       }

       return getToken(session, jwtToken, "JWT");
   }

    private String getToken(HttpSession session, String code, String tokenType) {
        System.out.println("----- getToken strated. -----" );		
        String url = "https://auth.worksmobile.com/oauth2/v2.0/token";
        
        HttpClient httpClient = HttpClient.newHttpClient();
        
        String requestBody = "";

        if(tokenType.equals("OAuth")){
            requestBody = String.format("grant_type=%s&client_id=%s&client_secret=%s&code=%s",
                "authorization_code",
                authConfig.getClientId(),
                authConfig.getClientSecret(),
                code
            );
        }else if(tokenType.equals("JWT")){
            requestBody = String.format("grant_type=%s&client_id=%s&client_secret=%s&assertion=%s&scope=%s",
                URLEncoder.encode("urn:ietf:params:oauth:grant-type:jwt-bearer", StandardCharsets.UTF_8),
                URLEncoder.encode(authConfig.getClientId(), StandardCharsets.UTF_8),
                URLEncoder.encode(authConfig.getClientSecret(), StandardCharsets.UTF_8),
                URLEncoder.encode(code, StandardCharsets.UTF_8),
                URLEncoder.encode(authConfig.getScope(), StandardCharsets.UTF_8)
            );
        }

        System.out.println(requestBody);	

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .uri(URI.create(url))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());  
            ObjectMapper objectMapper = new ObjectMapper();
            if(response.statusCode() != 200){
                return response.statusCode() + "Error: " + response.body();
            }

            String responseBody = response.body();

            JsonNode jsonNode = objectMapper.readTree(responseBody);
            String accessToken = jsonNode.get("access_token").asText();
            setAccessToken(session, accessToken);
            String refreshToken = jsonNode.get("refresh_token").asText();
            setRefreshToken(session, refreshToken);
            return accessToken; //response.body(); 여기서 토큰을 리턴 하여 redirect -> authrize -> 요청 api까지 리턴.
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
             return  "Error: " + e.getMessage();
        }
    }

    @GetMapping("/revokeToken/{tokenName}")
    String revokeToken(HttpSession session, @PathVariable String tokenName) {//tokenName: refresh/access
        String url = "https://auth.worksmobile.com/oauth2/v2.0/revoke";
        
        HttpClient httpClient = HttpClient.newHttpClient();

        String token = "";
        if(tokenName.equals("refresh")){
            token = getRefreshToken(session);
        } else if(tokenName.equals("access")){
            token = getAccessToken(session);
        }else{
            return "Error: PathVariable is invaild. tokenName(refresh/access) is required.";
        }
        if(token == null) {
            return "toekn is null.";
        }
        
        String requestBody = String.format("client_id=%s&client_secret=%s&token=%s",
            authConfig.getClientId(),
            authConfig.getClientSecret(),
            token
        );

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .uri(URI.create(url))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());  
            if(response.statusCode() != 200){
                session.removeAttribute("accessToken");
                return response.body();
            }else{
                if(tokenName.equals("refresh")){
                    session.removeAttribute("refreshToken");
                    session.removeAttribute("accessToken");
                } else if(tokenName.equals("access")){
                    session.removeAttribute("accessToken");
                }               
                
                return response.body();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    @GetMapping("/setAccessToken")
    public void setAccessToken(HttpSession session, String token) {
        session.setAttribute("accessToken", token);
    }

    @GetMapping("/getAccessToken")
    public String getAccessToken(HttpSession session) {
        return (String) session.getAttribute("accessToken");
    }

    @GetMapping("/setRefreshToken")
    public void setRefreshToken(HttpSession session, String token) {
        session.setAttribute("refreshToken", token);
    }

    @GetMapping("/getRefreshToken")
    public String getRefreshToken(HttpSession session) {
        return (String) session.getAttribute("refreshToken");
    }

    @GetMapping("/refreshAccessToken")
    String refreshAccessToken(String refreshToken, HttpSession session ) {
        String url = "https://auth.naverworks.com/oauth2/v2.0/token";
        
        HttpClient httpClient = HttpClient.newHttpClient();
        
        String requestBody = String.format("grant_type=%s&client_id=%s&client_secret=%s&refresh_token=%s",
            "authorization_code",
            authConfig.getClientId(),
            authConfig.getClientId(),
            refreshToken
        );
        System.out.println(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .uri(URI.create(url))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());  
            System.out.println(response.statusCode());
            if(response.statusCode() != 200){
                session.removeAttribute("refreshToken");
                session.removeAttribute("accessToken");
                return "redirect:/jwtauthorize";
            }

            ObjectMapper objectMapper = new ObjectMapper();
            String responseBody = response.body();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            String accessToken = jsonNode.get("access_token").asText();
            setAccessToken(session, accessToken);
            return response.body();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }



}