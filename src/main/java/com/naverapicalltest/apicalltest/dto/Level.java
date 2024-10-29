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
public class Level {
    @NotNull
    private Integer domainId;
    private String levelId; //readOnly. 자동 부여
    @NotNull
    private Integer displayOrder; //정렬 순서 format : int32
    @NotNull
    private String levelName;
    private String levelExternalKey;
    @NotNull
    private boolean executive; //임원 여부
    private List<i18nNames> i18nNames;

    @Data
    public static class i18nNames {
        @Pattern(regexp = "ko_KR|ja_JP|zh_CN|zh_TW|en_US", message = "Allowed values in(ko_KR, ja_JP, zh_CN, zh_TW, en_US)).")
        private String language;
        @NotNull
        private String name;
    }

}

