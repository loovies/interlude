package com.interlude.web.entity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 忘记密码后的重置请求。
 */
public class ResetPasswordRequestDto {

    @NotBlank(message = "email can not be blank")
    @Email(message = "email format is invalid")
    private String email;

    @NotBlank(message = "password can not be blank")
    @Size(min = 6, max = 32, message = "password length must be between 6 and 32")
    private String password;

    @NotBlank(message = "emailCode can not be blank")
    private String emailCode;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmailCode() {
        return emailCode;
    }

    public void setEmailCode(String emailCode) {
        this.emailCode = emailCode;
    }
}
