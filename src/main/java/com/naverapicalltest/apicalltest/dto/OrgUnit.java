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
public class OrgUnit {
    @NotNull
    private Integer domainId;
    private String orgUnitId; //readonly. 자동 부여
    private String orgUnitExternalKey;
    @NotNull
    private String orgUnitName;
    private List<OrgUniti18nName> i18nNames;
    private String email;
    private String description;
    private boolean visible;
    private String parentOrgUnitId;
    private String parentExternalKey; //readonly.
    private Integer displayOrder; // 수정시에는 해당 값은 무시된다.
    private Integer displayLevel; //readonly.
    private List<String> aliasEmails;
    private boolean canReceiveExternalMail = false;
    private boolean useMessage =false;
    private boolean useNote = false;
    private boolean useCalendar = false;
    private boolean useTask = false;
    private boolean useFolder = false;
    private boolean useServiceNotification = false;
    private List<OrgUnitAllowedMember> membersAllowedToUseOrgUnitEmailAsRecipient;
    private List<OrgUnitAllowedMember> membersAllowedToUseOrgUnitEmailAsSender;

    @Data
    public static class OrgUniti18nName {
        @Pattern(regexp = "ko_KR|ja_JP|zh_CN|zh_TW|en_US", message = "Allowed values in(ko_KR, ja_JP, zh_CN, zh_TW, en_US)).")
        private String language;
        @NotNull
        private String name;
    }
    
    @Data
    public static class OrgUnitAllowedMember {
        @NotNull
        private String name;
    }

}

