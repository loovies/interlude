package com.interlude.web.entity.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * 发送邮箱验证码请求，注册和找回密码都复用这份入参。
 */
public class SendEmailCodeRequestDto {

    @NotBlank(message = "email can not be blank")
    @Email(message = "email format is invalid")
    private String email;

    @NotBlank(message = "checkCodeKey can not be blank")
    private String checkCodeKey;

    @NotBlank(message = "checkCode can not be blank")
    private String checkCode;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
