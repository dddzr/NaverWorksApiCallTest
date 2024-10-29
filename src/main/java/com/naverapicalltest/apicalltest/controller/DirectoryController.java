package com.naverapicalltest.apicalltest.controller;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
            // 원래 로그인 페이지(HTML)를 보여주기 위해 쓰는 api같다. 그래서 바로 accessToken을 받아 올 수는 없다. 
            // (response가 accsse token 이 아닌 로그인 html)
            // return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/authorize")).build();
        } else {
            String userInfo = fetchUserInfo(accessToken, userId);
            return ResponseEntity.ok(userInfo);
        }
    }

    private String fetchUserInfo(String accessToken, String userId) {
        String url = "https://www.worksapis.com/v1.0/users/"+ userId; // path Parameters (메일ID/리소스ID/외부키)

        HttpClient httpClient = HttpClient.newHttpClient();        
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + accessToken) // header Parameters
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            // JSON 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode data = objectMapper.readTree(response.body());
            // User user = objectMapper.readValue(response.body(), User.class); => DTO로 매핑하여 사용 할 때.

            // 응답 코드 확인
            int statusCode = response.statusCode();

            // 상태에 따라 처리 -> 수정 필요
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

    @PatchMapping("/patchUser/{userId}") //ID는 추가 시 자동으로 부여됨.
    public String patchUser(@PathVariable String userId, @RequestBody User user, HttpSession session) throws Exception{
        System.out.println("-----patchUser strated.-----" );	

        String url = "https://www.worksapis.com/v1.0/users/" + userId;
        HttpClient httpClient = HttpClient.newHttpClient();
        
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonUser = objectMapper.writeValueAsString(user);

        String accessToken = authController.getAccessToken(session);
        if (accessToken == null) {
            accessToken = authController.jwtAuthorize(session);
        }

        HttpRequest request = HttpRequest.newBuilder()
        .method("PATCH", HttpRequest.BodyPublishers.ofString(jsonUser))
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

    /* 
     * 조직
     * 조직 추가/수정/부분 수정/이동 API는 도메인당 반드시 단일 스레드로 1초에 한 번, 순서대로 호출한다. -> 본인 코드 따라 처리할거 많으면 시간 늘림.
     */
    private final ExecutorService orgUnitExecutor = Executors.newSingleThreadExecutor();
    private long lastOrgUnitExecutionTime = System.currentTimeMillis();

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

    @PostMapping("/addOrgunit")
    public ResponseEntity<String> addOrgunit(@RequestBody OrgUnit orgunit, HttpSession session) throws Exception{
        System.out.println("-----addOrgunit strated.-----" );	

        // ExecutorService를 통해 단일 스레드로 처리
        orgUnitExecutor.submit(() -> {
            try {
                // 1초 대기
                long currentTime = System.currentTimeMillis();
                long waitTime = 1000 - (currentTime - lastOrgUnitExecutionTime);
                if (waitTime > 0) {
                    TimeUnit.MILLISECONDS.sleep(waitTime);
                }
                lastOrgUnitExecutionTime = System.currentTimeMillis();

                // API 요청 코드
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
                System.out.println("Response: " + response.body());

            } catch (Exception e) {
                e.printStackTrace();
                // 예외 처리 로직 추가 필요
            }
        });

        return ResponseEntity.accepted().body("Request is being processed."); //스레드가 완료되기를 기다리지 않고 요청이 진행중이라는 메세지 반환.
    }

    
    @PatchMapping("/patchOrgunit/{orgUnitId}")
    public ResponseEntity<String> patchOrgunit(@PathVariable String orgUnitId, @RequestBody OrgUnit orgUnit, HttpSession session) throws Exception{
        System.out.println("-----patchOrgunit strated.-----" );	

        orgUnitExecutor.submit(() -> {
            try {
                long currentTime = System.currentTimeMillis();
                long waitTime = 1000 - (currentTime - lastOrgUnitExecutionTime);
                if (waitTime > 0) {
                    TimeUnit.MILLISECONDS.sleep(waitTime);
                }
                lastOrgUnitExecutionTime = System.currentTimeMillis();

                
                String url = "https://www.worksapis.com/v1.0/orgunits/" + orgUnitId;
                HttpClient httpClient = HttpClient.newHttpClient();
                
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonOrgUnit = objectMapper.writeValueAsString(orgUnit);
        
                String accessToken = authController.getAccessToken(session);
                if (accessToken == null) {
                    accessToken = authController.jwtAuthorize(session);
                }
        
                HttpRequest request = HttpRequest.newBuilder()
                .method("PATCH", HttpRequest.BodyPublishers.ofString(jsonOrgUnit))
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .uri(URI.create(url))
                .build();
        
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println("Response: " + response.body());

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        return ResponseEntity.accepted().body("Request is being processed."); 
    }

    @DeleteMapping("/deleteOrgunit/{orgUnitId}")
    public String deleteOrgunit(@PathVariable String orgUnitId, HttpSession session) throws Exception{
        System.out.println("-----deleteOrgunit strated.-----" );	

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

    /* 직급 (팀원, 팀장...)*/
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

    /* 직책 (사원, 대리...) */
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
