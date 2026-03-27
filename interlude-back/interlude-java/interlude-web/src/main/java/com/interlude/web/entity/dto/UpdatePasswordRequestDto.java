package com.interlude.web.entity.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 已登录用户主动修改密码请求。
 */
public class UpdatePasswordRequestDto {

    @NotBlank(message = "oldPassword can not be blank")
    private String oldPassword;

    @NotBlank(message = "newPassword can not be blank")
    @Size(min = 6, max = 32, message = "newPassword length must be between 6 and 32")
    private String newPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
