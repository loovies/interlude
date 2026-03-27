package com.interlude.web.entity.dto;

/**
 * 图形验证码返回对象，兼容 codeKey/checkCodeKey 两种前端字段名。
 */
public class CheckCodeResponseDto {

    private String code;
    private String codeKey;
    private String checkCodeKey;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCodeKey() {
        return codeKey;
    }

    public void setCodeKey(String codeKey) {
        this.codeKey = codeKey;
    }

    public String getCheckCodeKey() {
        return checkCodeKey;
    }

    public void setCheckCodeKey(String checkCodeKey) {
        this.checkCodeKey = checkCodeKey;
    }
}
