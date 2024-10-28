package com.naverapicalltest.apicalltest.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.NotNull;

import java.util.List;

@Data
public class Level {
    @NotNull
    Integer domainId;

    @NotNull
    Integer displayOrder; //정렬 순서 format : int32

    @NotNull
    String levelName;

    String levelExternalKey;

    @NotNull
    boolean executive; //임원 여부

    // List<i18nNames> i18nNames;

}

