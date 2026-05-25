package com.interlude.web.entity.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 前台登录请求。
 */
public class LoginRequestDto {

    @NotBlank(message = "account can not be blank")
    private String account;

    @NotBlank(message = "password can not be blank")
    private String password;

    @NotBlank(message = "checkCodeKey can not be blank")
    private String checkCodeKey;

    @NotBlank(message = "checkCode can not be blank")
    private String checkCode;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCheckCodeKey() {
        return checkCodeKey;
    }

    public void setCheckCodeKey(String checkCodeKey) {
        this.checkCodeKey = checkCodeKey;
    }

    public String getCheckCode() {
        return checkCode;
    }

    public void setCheckCode(String checkCode) {
        this.checkCode = checkCode;
    }
}
