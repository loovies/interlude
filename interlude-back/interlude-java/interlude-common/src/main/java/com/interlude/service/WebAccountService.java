package com.interlude.service;

import com.interlude.entity.dto.TokenUserInfoDto;
import com.interlude.entity.dto.UserWebInfoDto;
import com.interlude.entity.dto.UserWebInfoUpdateDto;

/**
 * 前台账号领域服务接口。
 */
public interface WebAccountService {

    void sendRegisterEmailCode(String email);

    void sendResetPasswordEmailCode(String email);

    void sendUpdateEmailCode(String userId, String email);

    TokenUserInfoDto register(String email, String nickName, String password, String emailCode);

    void resetPassword(String email, String password, String emailCode);

    UserWebInfoDto getCurrentUserInfo(String userId);

    UserWebInfoDto updateCurrentUserInfo(String userId, UserWebInfoUpdateDto updateDto);

    UserWebInfoDto updateEmail(String userId, String email, String emailCode);

    UserWebInfoDto bindPhone(String userId, String phone);

    UserWebInfoDto updateAvatar(String userId, String avatar);

    void updatePassword(String userId, String oldPassword, String newPassword);

    void closeAccount(String userId, String password);
}
