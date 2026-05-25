package com.interlude.web.entity.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 注销账号确认请求。
 */
public class CloseAccountRequestDto {

    @NotBlank(message = "password can not be blank")
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
