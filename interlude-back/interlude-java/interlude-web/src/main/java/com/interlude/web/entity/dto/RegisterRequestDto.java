package com.interlude.web.entity.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 前台注册请求。
 */
public class RegisterRequestDto {

    @NotBlank(message = "email can not be blank")
    @Email(message = "email format is invalid")
    private String email;

    @NotBlank(message = "nickName can not be blank")
    @Size(max = 50, message = "nickName is too long")
    private String nickName;

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

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
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
