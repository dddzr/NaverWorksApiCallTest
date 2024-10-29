package com.naverapicalltest.apicalltest.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.NotNull;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    @NotNull
    private Integer domainId;
    private String userExternalKey;

    /*  
        readOnly 
        setter 일일이 만들기 귀찮아서 일단 Data 씀. 세터 안 만들게 수정 필요. (다른 dto도)
    */  
    private String userId; // 자동 부여.
    private boolean isAdministrator;
    private boolean isPending;
    private boolean isSuspended;
    private boolean isDeleted;
    private boolean isAwaiting;
    private LeaveOfAbsence leaveOfAbsence;
    private String suspendedReason;

    @NotNull
    private String email;
    @Valid
    @NotNull
    private UserName userName; // Object
    private List<Useri18nName> i18nNames;
    private String nickName;
    private String privateEmail; //SSO 사용 안 하고, passwordConfig.passwordCreationType = "MEMBER" 이면 필수
    private List<String> aliasEmails;
    private String employmentTypeId;
    private String userTypeId;
    private boolean searchable;
    private PasswordConfig passwordConfig;    
    @Valid
    private List<UserOrganization> organizations;
    private String telephone;
    private String cellPhone;
    private String location;
    private String task;
    private Messenger messenger;
    private String birthdayCalendarType;
    private String birthday;
    private String locale;
    private String hiredDate;
    private String timeZone;
    private List<UserCustomField> customFields;
    private List<UserRelation> relations;
    private String activationDate;
    private String employeeNumber;

    @Data
    public static class LeaveOfAbsence {
        private String startTime;
        private String endTime;
        private boolean isLeaveOfAbsence;
    }

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
    public static class Messenger {
        @Pattern(regexp = "LINE|FACEBOOK|TWITTER|CUSTOM", message = "Allowed values in (LINE, FACEBOOK, TWITTER, CUSTOM)")
        private String protocol;
        private String customProtocol;
        private String messengerId;
    }

    @Data
    public static class UserOrganization { //static 빼면 "Type definition error: [simple type, class com.naverapicalltest.apicalltest.dto.User$UserOrganization]",
        @NotNull(message = "Domain ID is required.")
        private Integer domainId; // 도메인 (=회사)
        private Boolean primary; // 대표 도메인 여부
        private String userExternalKey;
        private String email;
        private String levelId; // 직급 (사업장, 팀장, 구성원 ...)
        private List<OrgUnit> orgUnits; // 조직 목록

        @Data
        public static class OrgUnit {// 사용자 정보 조회 할 때 실제 조직, 직책과 매칭되어 보여지지 x, ID 그대로 보여짐. & 없는 ID 입력해도 등록됨.
            @NotNull
            private String orgUnitId; //조직 (팀)
            @NotNull
            private boolean primary;
            private String positionId; //직책 (사장, 이사, 부장, 과장 ...)
            private boolean isManager = false;
            private boolean visible = true;
            private boolean useTeamFeature = true;
        }
    }

    @Data
    public static class UserCustomField {
        @NotNull
        private String customFieldId;
        private String value;
        private String link;
    }

    @Data
    public static class UserRelation {
        private String relationUserId;
        private String relationName;
    }

}

