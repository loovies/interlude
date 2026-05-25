package com.interlude.web.entity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * 修改邮箱请求。
 */
public class UpdateEmailRequestDto {

    @NotBlank(message = "email can not be blank")
    @Email(message = "email format is invalid")
    private String email;

    @NotBlank(message = "emailCode can not be blank")
    private String emailCode;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailCode() {
        return emailCode;
    }

    public void setEmailCode(String emailCode) {
        this.emailCode = emailCode;
    }
}
