# NaverWorksApiCallTest

![사용자정보요청 drawio](https://github.com/user-attachments/assets/ee736fb4-e686-4676-89e5-3b17a31d4d6f)

실행하기 위해 아래 파일 추가가 필요합니다.
 - git ignore 된 파일
  - /src/main/resources/private_20241025105710.key : JWT 생성에 필요합니다. naver works developer console > Client App > Service Account 생성 > Private Key 발급
  - /src/main/resources/keystore.p12 : ssl key
  - /src/main/resources/application.properties : naver works의 인증 정보(client Id 등)과 ssl key 정보를 작성해야합니다.
