package com.naverapicalltest.apicalltest.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.NotNull;

import java.util.List;

@Data
public class User {
    @NotNull
    Integer domainId;

    String userExternalKey;

    @NotNull
    String email;

    @Valid
    @NotNull
    private UserName userName; // Object;

    List<Useri18nName> i18nNames;
    String nickName;
    String privateEmail; //SSO 사용 안 하고, passwordConfig.passwordCreationType = "MEMBER" 이면 필수
    List<String> aliasEmails;
    String employmentTypeId;
    String userTypeId;
    boolean searchable;
    private PasswordConfig passwordConfig;
    
    @Valid
    private List<UserOrganization> organizations;
    String telephone;
    String cellPhone;
    String location;
    String task;
    // Object messenger;
    String birthdayCalendarType;
    String birthday;
    String locale;
    String hiredDate;
    String timeZone;
    // List<UserCustomField> customFields;
    // List<UserRelation> relations;
    String activationDate;
    String employeeNumber;

    @Data
    public static class UserName {
        private String lastName;
        private String firstName;
        private String phoneticLastName;
        private String phoneticFirstName;
    }

    @Data
    public static class Useri18nName {
        @Pattern(regexp = "ko_KR|ja_JP|zh_CN|zh_TW|en_US", message = "Allowed values in(ko_KR, ja_JP, zh_CN, zh_TW, en_US)).")
        private String language;
        private String firstName;
        private boolean lastName;
    }

    @Data
    public static class PasswordConfig {
        @Pattern(regexp = "ADMIN|MEMBER", message = "Allowed values are ADMIN or MEMBER.")
        private String passwordCreationType = "MEMBER";
        private String password; //passwordCreationType이 ADMIN이면 필수
        private boolean changePasswordAtNextLogin = true;
    }

    @Data
    public static class UserOrganization { //static 빼면 "Type definition error: [simple type, class com.naverapicalltest.apicalltest.dto.User$UserOrganization]",
        @NotNull(message = "Domain ID is required.")
        private Integer domainId; // 도메인 (=회사)
        private Boolean primary; // 대표 도메인 여부
        private String userExternalKey;
        private String email;
        private String levelId; // 직급
        private List<OrgUnit> orgUnits; // 조직 목록

        @Data
        public static class OrgUnit {
            @NotNull
            private String orgUnitId;
            @NotNull
            private boolean primary;
            private String positionId;
            private boolean isManager = false;
            private boolean visible = true;
            private boolean useTeamFeature = true;
        }
    }

}

