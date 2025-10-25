package com.interlude.enums;

public enum DateTimePatterEnum {
    YYYY_MM_DD_HH_MM_SS("yyyy-MM-dd HH:mm:ss"), YYYY_MM_DD("yyyy-MM-dd"),YYYY_MM("yyyyMM");

    private String pattern;

    DateTimePatterEnum(String pattern) {
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }
}
