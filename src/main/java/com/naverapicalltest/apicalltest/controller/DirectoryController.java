package com.naverapicalltest.apicalltest.controller;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpSession;

@RestController
public class DirectoryController {
    private final AuthController authController;

    @Autowired
    public DirectoryController(AuthController authController) {
        this.authController = authController;
    }

    @GetMapping("/userDetail/{userId}")
    public ResponseEntity<Object> getUserDetail(@PathVariable String userId, HttpSession session) throws Exception{
        System.out.println("-----getUserDetail strated.-----" );	
        // String userId = "sm.28091@smdomain.by-works.com";

        // Access Token 조회
        String accessToken = authController.getAccessToken(session); // TokenStore.getAccessToken();

        if (accessToken == null) {
            accessToken = authController.jwtAuthorize(session);
            String userInfo = fetchUserInfo(accessToken, userId);
            return ResponseEntity.ok(userInfo);
            // 아래는 OAuth(구성원 계정으로 인증)테스트를 위한 코드.
            // 원래 로그인 페이지를 보여주기 위해 쓰는 api같다. 그래서 바로 accessToken을 받아 올 수는 없다.
            // return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/authorize")).build();
        } else {
            String userInfo = fetchUserInfo(accessToken, userId);
            return ResponseEntity.ok(userInfo);
        }
    }

    private String fetchUserInfo(String accessToken, String userId) {
        String userInfoUrl = "https://www.worksapis.com/v1.0/users/"+ userId; //URLEncoder.encode(userId, StandardCharsets.UTF_8); //+ path Parameters (메일ID/리소스ID/외부키)

        HttpClient httpClient = HttpClient.newHttpClient();
        
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(userInfoUrl))
                .header("Authorization", "Bearer " + accessToken) //header Parameters
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            // JSON 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode data = objectMapper.readTree(response.body());

            // 응답 코드 확인
            int statusCode = response.statusCode();

            // 상태에 따라 처리
            if (statusCode == 200) {
                return data.toString();
            } else {
                return "Error: " + data.toString();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
    
    
}
