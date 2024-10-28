package com.naverapicalltest.apicalltest.controller;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

//dto
import com.naverapicalltest.apicalltest.dto.Level;
import com.naverapicalltest.apicalltest.dto.User;
import com.naverapicalltest.apicalltest.dto.OrgUnit;

import jakarta.servlet.http.HttpSession;

@RestController
public class DirectoryController {
    private final AuthController authController;

    @Autowired
    public DirectoryController(AuthController authController) {
        this.authController = authController;
    }

    /* 구성원 */

    @GetMapping("/getUserList")
    public String getUserList(HttpSession session) throws Exception{
        System.out.println("-----getUserList strated.-----" );	

        // Access Token 조회
        String accessToken = authController.getAccessToken(session);
        if (accessToken == null) {
            accessToken = authController.jwtAuthorize(session);
        }
        
        String url = "https://www.worksapis.com/v1.0/users";

        HttpClient httpClient = HttpClient.newHttpClient();        
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + accessToken)
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();        
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
        String url = "https://www.worksapis.com/v1.0/users/"+ userId; //URLEncoder.encode(userId, StandardCharsets.UTF_8); //+ path Parameters (메일ID/리소스ID/외부키)

        HttpClient httpClient = HttpClient.newHttpClient();        
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
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

    @PostMapping("/addUser")
    public String addUser(@RequestBody User user, HttpSession session) throws Exception{ //void
        System.out.println("-----addUser strated.-----" );	

        String url = "https://www.worksapis.com/v1.0/users";
        HttpClient httpClient = HttpClient.newHttpClient();

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonUser = objectMapper.writeValueAsString(user);
        
        String accessToken = authController.getAccessToken(session);
        if (accessToken == null) {
            accessToken = authController.jwtAuthorize(session);
        }

        HttpRequest request = HttpRequest.newBuilder()
        .POST(HttpRequest.BodyPublishers.ofString(jsonUser))
        .header("Authorization", "Bearer " + accessToken)
        .header("Content-Type", "application/json")
        .uri(URI.create(url))
        .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
    
    @DeleteMapping("/deleteUser/{userId}") //ID는 추가 시 자동으로 부여됨.
    public String deleteUser(@PathVariable String userId, HttpSession session) throws Exception{
        System.out.println("-----deleteUser strated.-----" );	

        String url = "https://www.worksapis.com/v1.0/users/" + userId;
        HttpClient httpClient = HttpClient.newHttpClient();

        String accessToken = authController.getAccessToken(session);
        if (accessToken == null) {
            accessToken = authController.jwtAuthorize(session);
        }

        HttpRequest request = HttpRequest.newBuilder()
        .DELETE()
        .header("Authorization", "Bearer " + accessToken)
        .uri(URI.create(url))
        .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    @DeleteMapping("/forceDeleteUser/{userId}") //ID는 추가 시 자동으로 부여됨.
    public String forceDeleteUser(@PathVariable String userId, HttpSession session) throws Exception{
        System.out.println("-----forceDeleteUser strated.-----" );	

        String url = "https://www.worksapis.com/v1.0/users/" + userId + "/forcedelete";
        HttpClient httpClient = HttpClient.newHttpClient();

        String accessToken = authController.getAccessToken(session);
        if (accessToken == null) {
            accessToken = authController.jwtAuthorize(session);
        }

        HttpRequest request = HttpRequest.newBuilder()
        .DELETE()
        .header("Authorization", "Bearer " + accessToken)
        .uri(URI.create(url))
        .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    /* 조직 */

    @GetMapping("/getOrgunitList")
    public String getOrgunitList(HttpSession session) throws Exception{
        System.out.println("-----getOrgunitList strated.-----" );	

        // Access Token 조회
        String accessToken = authController.getAccessToken(session);
        if (accessToken == null) {
            accessToken = authController.jwtAuthorize(session);
        }
        
        String url = "https://www.worksapis.com/v1.0/orgunits";

        HttpClient httpClient = HttpClient.newHttpClient();        
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + accessToken)
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();        
    }

    @PostMapping("/addOrgunits")
    public String addOrgunits(@RequestBody OrgUnit orgunit, HttpSession session) throws Exception{
        System.out.println("-----addOrgunits strated.-----" );	

        String url = "https://www.worksapis.com/v1.0/orgunits";
        HttpClient httpClient = HttpClient.newHttpClient();

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonUser = objectMapper.writeValueAsString(orgunit);
        
        String accessToken = authController.getAccessToken(session);
        if (accessToken == null) {
            accessToken = authController.jwtAuthorize(session);
        }

        HttpRequest request = HttpRequest.newBuilder()
        .POST(HttpRequest.BodyPublishers.ofString(jsonUser))
        .header("Authorization", "Bearer " + accessToken)
        .header("Content-Type", "application/json")
        .uri(URI.create(url))
        .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    @DeleteMapping("/deleteOrgunits/{orgUnitId}")
    public String deleteOrgunits(@PathVariable String orgUnitId, HttpSession session) throws Exception{
        System.out.println("-----deleteOrgunits strated.-----" );	

        String url = "https://www.worksapis.com/v1.0/orgunits/" + orgUnitId;
        HttpClient httpClient = HttpClient.newHttpClient();

        String accessToken = authController.getAccessToken(session);
        if (accessToken == null) {
            accessToken = authController.jwtAuthorize(session);
        }

        HttpRequest request = HttpRequest.newBuilder()
        .DELETE()
        .header("Authorization", "Bearer " + accessToken)
        .uri(URI.create(url))
        .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    /* 직급 */

    @GetMapping("/getLevelList")
    public String getLevelList(HttpSession session) throws Exception{
        System.out.println("-----getLevelList strated.-----" );	

        // Access Token 조회
        String accessToken = authController.getAccessToken(session);
        if (accessToken == null) {
            accessToken = authController.jwtAuthorize(session);
        }
        
        String url = "https://www.worksapis.com/v1.0/directory/levels";

        HttpClient httpClient = HttpClient.newHttpClient();        
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + accessToken)
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();        
    }

    @PostMapping("/addLevels")
    public String addLevels(@RequestBody Level level, HttpSession session) throws Exception{
        System.out.println("-----addLevels strated.-----" );	

        String url = "https://www.worksapis.com/v1.0/directory/levels";
        HttpClient httpClient = HttpClient.newHttpClient();

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonUser = objectMapper.writeValueAsString(level);
        
        String accessToken = authController.getAccessToken(session);
        if (accessToken == null) {
            accessToken = authController.jwtAuthorize(session);
        }

        HttpRequest request = HttpRequest.newBuilder()
        .POST(HttpRequest.BodyPublishers.ofString(jsonUser))
        .header("Authorization", "Bearer " + accessToken)
        .header("Content-Type", "application/json")
        .uri(URI.create(url))
        .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    @DeleteMapping("/deleteLevels/{levelId}")
    public String useLevels(@PathVariable String levelId, HttpSession session) throws Exception{
        System.out.println("-----deleteLevels strated.-----" );	

        String url = "https://www.worksapis.com/v1.0/directory/levels/" + levelId;
        HttpClient httpClient = HttpClient.newHttpClient();

        String accessToken = authController.getAccessToken(session);
        if (accessToken == null) {
            accessToken = authController.jwtAuthorize(session);
        }

        HttpRequest request = HttpRequest.newBuilder()
        .DELETE()
        .header("Authorization", "Bearer " + accessToken)
        .uri(URI.create(url))
        .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    /* 직급 */

    @GetMapping("/getPositionList")
    public String getPositionList(HttpSession session) throws Exception{
        System.out.println("-----getPositionList strated.-----" );	

        // Access Token 조회
        String accessToken = authController.getAccessToken(session);
        if (accessToken == null) {
            accessToken = authController.jwtAuthorize(session);
        }
        
        String url = "https://www.worksapis.com/v1.0/directory/positions";

        HttpClient httpClient = HttpClient.newHttpClient();        
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + accessToken)
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();        
    }
}
