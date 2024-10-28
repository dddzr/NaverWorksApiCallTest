package com.naverapicalltest.apicalltest.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.NotNull;

import java.util.List;

@Data
public class OrgUnit {
    @NotNull
    Integer domainId;

    String orgUnitId; //readonly
    String orgUnitExternalKey;

    @NotNull
    String orgUnitName;
    // List<i18nNames> i18nNames;
    String email;
    String description;
    boolean visible;
    String parentOrgUnitId;
    String parentExternalKey; //readonly.
    Integer displayOrder;
    Integer displayLevel; //readonly.. setter 일일이 만들기 귀찮아서 일단 냅둠
    List<String> aliasEmails;
    boolean canReceiveExternalMail = false;
    boolean useMessage =false;
    boolean useNote = false;
    boolean useCalendar = false;
    boolean useTask = false;
    boolean useFolder = false;
    boolean useServiceNotification = false;
    // List<OrgUnitAllowedMember> membersAllowedToUseOrgUnitEmailAsRecipient;
    // List<OrgUnitAllowedMember> membersAllowedToUseOrgUnitEmailAsSender;


}

