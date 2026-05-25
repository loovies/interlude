package com.interlude.web.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 绑定或更新手机号请求。
 */
public class BindPhoneRequestDto {

    @NotBlank(message = "phone can not be blank")
    @Size(max = 32, message = "phone is too long")
    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
