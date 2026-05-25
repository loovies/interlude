package com.interlude.service.impl;

import com.interlude.component.RedisComponent;
import com.interlude.entity.config.AppConfig;
import com.interlude.entity.constants.Constants;
import com.interlude.entity.dto.SysSettingDto;
import com.interlude.entity.dto.TokenUserInfoDto;
import com.interlude.entity.dto.UserWebInfoDto;
import com.interlude.entity.dto.UserWebInfoUpdateDto;
import com.interlude.entity.po.UserEmailCode;
import com.interlude.entity.po.UserInfo;
import com.interlude.entity.po.UserRoleRelation;
import com.interlude.entity.query.UserInfoQuery;
import com.interlude.enums.RoleRelationEnum;
import com.interlude.enums.UserStatusEnum;
import com.interlude.exception.BusinessException;
import com.interlude.service.UserEmailCodeService;
import com.interlude.service.UserInfoService;
import com.interlude.service.UserRoleRelationService;
import com.interlude.service.WebAccountService;
import com.interlude.utils.CopyTools;
import com.interlude.utils.StringTools;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.Date;

/**
 * 前台账号能力统一收口到这里，避免 controller 直接操作 mapper。
 */
@Service("webAccountService")
public class WebAccountServiceImpl implements WebAccountService {

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private UserEmailCodeService userEmailCodeService;

    @Resource
    private UserRoleRelationService userRoleRelationService;

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private AppConfig appConfig;

    @Autowired
    private ObjectProvider<JavaMailSender> javaMailSenderProvider;

    @Override
    // 注册验证码要求邮箱未被占用。
    public void sendRegisterEmailCode(String email) {
        checkEmailCanRegister(email);
        saveAndSendEmailCode(email);
    }

    @Override
    // 找回密码验证码要求邮箱必须已注册。
    public void sendResetPasswordEmailCode(String email) {
        UserInfo userInfo = getUserInfoByEmail(email);
        if (userInfo == null) {
            throw new BusinessException("email is not registered");
        }
        if (userInfo.getEnabled() != null && userInfo.getEnabled().intValue() == UserStatusEnum.DISABLE.getStatus()) {
            throw new BusinessException("account is disabled");
        }
        saveAndSendEmailCode(email);
    }

    @Override
    // 修改邮箱时给新邮箱发送验证码，避免把已存在邮箱改进来。
    public void sendUpdateEmailCode(String userId, String email) {
        UserInfo currentUser = getUserById(userId);
        checkEmailCanUse(email, userId);
        if (email.equalsIgnoreCase(currentUser.getEmail())) {
            throw new BusinessException("new email can not be same as current email");
        }
        saveAndSendEmailCode(email);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    // 注册完成后直接复用现有登录流程生成前台 token。
    public TokenUserInfoDto register(String email, String nickName, String password, String emailCode) {
        checkEmailCanRegister(email);
        if (userInfoService.getUserInfoByNickName(nickName) != null) {
            throw new BusinessException("nick name already exists");
        }
        checkRegisterEmailCode(email, emailCode);

        Date now = new Date();
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(generateUserId());
        userInfo.setEmail(email);
        userInfo.setNickName(nickName);
        userInfo.setPassword(StringTools.encodeByMd5(password));
        userInfo.setTheme(Constants.NUMBER_ONE);
        userInfo.setEnabled(UserStatusEnum.ENABLE.getStatus());
        userInfo.setCreateTime(now);
        userInfo.setUpdateTime(now);
        userInfo.setPwdResetTime(now);
        userInfo.setCreateBy(nickName);
        userInfo.setUpdateBy(nickName);
        userInfoService.add(userInfo);

        UserRoleRelation userRoleRelation = new UserRoleRelation();
        userRoleRelation.setUserId(userInfo.getUserId());
        userRoleRelation.setRoleId(Long.valueOf(RoleRelationEnum.USER.getStatus()));
        userRoleRelationService.add(userRoleRelation);

        markEmailCodeUsed(email, emailCode);
        return userInfoService.login4Web(email, password);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(String email, String password, String emailCode) {
        UserInfo userInfo = getUserInfoByEmail(email);
        if (userInfo == null) {
            throw new BusinessException("email is not registered");
        }
        checkEmailCode(email, emailCode);

        UserInfoQuery updateQuery = new UserInfoQuery();
        updateQuery.setPassword(StringTools.encodeByMd5(password));
        updateQuery.setPwdResetTime(new Date());
        updateQuery.setUpdateTime(new Date());
        updateQuery.setUpdateBy(email);
        userInfoService.updateUserInfoByUserId(updateQuery, userInfo.getUserId());
        markEmailCodeUsed(email, emailCode);
    }

    @Override
    // 当前用户信息统一从数据库回读，避免 token 信息不完整。
    public UserWebInfoDto getCurrentUserInfo(String userId) {
        UserInfo userInfo = userInfoService.getUserInfoByUserId(userId);
        if (userInfo == null) {
            throw new BusinessException("user not found");
        }
        UserWebInfoDto userWebInfoDto = CopyTools.copy(userInfo, UserWebInfoDto.class);
        UserRoleRelation userRoleRelation = userRoleRelationService.getRoleRelationByUserId(userId);
        if (userRoleRelation != null) {
            userWebInfoDto.setRoleId(userRoleRelation.getRoleId());
        }
        return userWebInfoDto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserWebInfoDto updateCurrentUserInfo(String userId, UserWebInfoUpdateDto updateDto) {
        getUserById(userId);
        if (!hasProfileUpdates(updateDto)) {
            throw new BusinessException("no user info fields to update");
        }
        checkNickNameCanUse(updateDto.getNickName(), userId);
        checkPhoneCanUse(updateDto.getPhone(), userId);

        UserInfoQuery updateQuery = buildUserInfoUpdateQuery(updateDto, userId);
        userInfoService.updateUserInfoByUserId(updateQuery, userId);
        return getCurrentUserInfo(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserWebInfoDto updateEmail(String userId, String email, String emailCode) {
        UserInfo currentUser = getUserById(userId);
        if (email.equalsIgnoreCase(currentUser.getEmail())) {
            throw new BusinessException("new email can not be same as current email");
        }
        checkEmailCanUse(email, userId);
        checkEmailCode(email, emailCode);

        UserInfoQuery updateQuery = new UserInfoQuery();
        updateQuery.setEmail(email);
        updateQuery.setUpdateTime(new Date());
        updateQuery.setUpdateBy(userId);
        userInfoService.updateUserInfoByUserId(updateQuery, userId);
        markEmailCodeUsed(email, emailCode);
        return getCurrentUserInfo(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserWebInfoDto bindPhone(String userId, String phone) {
        getUserById(userId);
        checkPhoneCanUse(phone, userId);

        UserInfoQuery updateQuery = new UserInfoQuery();
        updateQuery.setPhone(phone);
        updateQuery.setUpdateTime(new Date());
        updateQuery.setUpdateBy(userId);
        userInfoService.updateUserInfoByUserId(updateQuery, userId);
        return getCurrentUserInfo(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserWebInfoDto updateAvatar(String userId, String avatar) {
        getUserById(userId);
        if (StringTools.isEmpty(avatar)) {
            throw new BusinessException("avatar can not be blank");
        }

        UserInfoQuery updateQuery = new UserInfoQuery();
        updateQuery.setAvatar(avatar);
        updateQuery.setUpdateTime(new Date());
        updateQuery.setUpdateBy(userId);
        userInfoService.updateUserInfoByUserId(updateQuery, userId);
        return getCurrentUserInfo(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(String userId, String oldPassword, String newPassword) {
        UserInfo userInfo = getUserById(userId);
        if (!passwordMatches(userInfo.getPassword(), oldPassword)) {
            throw new BusinessException("old password is invalid");
        }
        if (passwordMatches(userInfo.getPassword(), newPassword)) {
            throw new BusinessException("new password can not be same as old password");
        }

        UserInfoQuery updateQuery = new UserInfoQuery();
        updateQuery.setPassword(StringTools.encodeByMd5(newPassword));
        updateQuery.setPwdResetTime(new Date());
        updateQuery.setUpdateTime(new Date());
        updateQuery.setUpdateBy(userId);
        userInfoService.updateUserInfoByUserId(updateQuery, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closeAccount(String userId, String password) {
        UserInfo userInfo = getUserById(userId);
        if (!passwordMatches(userInfo.getPassword(), password)) {
            throw new BusinessException("password is invalid");
        }

        UserInfoQuery updateQuery = new UserInfoQuery();
        updateQuery.setEnabled(UserStatusEnum.DISABLE.getStatus());
        updateQuery.setUpdateTime(new Date());
        updateQuery.setUpdateBy(userId);
        userInfoService.updateUserInfoByUserId(updateQuery, userId);
    }

    private void checkEmailCanRegister(String email) {
        checkEmailCanUse(email, null);
    }

    private void checkRegisterEmailCode(String email, String emailCode) {
        checkEmailCode(email, emailCode);
    }

    // 邮箱验证码统一做未使用和有效期校验。
    private void checkEmailCode(String email, String emailCode) {
        UserEmailCode userEmailCode = userEmailCodeService.getUserEmailCodeByEmailAndCode(email, emailCode);
        if (userEmailCode == null) {
            throw new BusinessException("email code is invalid");
        }
        if (userEmailCode.getStatus() != null && userEmailCode.getStatus().intValue() == Constants.NUMBER_ONE) {
            throw new BusinessException("email code has already been used");
        }
        if (userEmailCode.getCreateTime() == null
                || System.currentTimeMillis() - userEmailCode.getCreateTime().getTime() > Constants.EMAIL_CODE_EXPIRE_MILLISECONDS) {
            throw new BusinessException("email code has expired");
        }
    }

    private void markEmailCodeUsed(String email, String emailCode) {
        UserEmailCode updateCode = new UserEmailCode();
        updateCode.setStatus(Constants.NUMBER_ONE);
        userEmailCodeService.updateUserEmailCodeByEmailAndCode(updateCode, email, emailCode);
    }

    // 发送邮件前先做一分钟级别的基础频控，再落库保存验证码。
    private void saveAndSendEmailCode(String email) {
        Long sendCount = redisComponent.incrementSendEmailCodeCount(email);
        if (sendCount != null && sendCount > 1) {
            throw new BusinessException("email code was sent too frequently");
        }

        String code = StringTools.getRandomNumber(Constants.NUMBER_SIX);
        sendEmail(email, code);

        UserEmailCode userEmailCode = new UserEmailCode();
        userEmailCode.setEmail(email);
        userEmailCode.setCode(code);
        userEmailCode.setCreateTime(new Date());
        userEmailCode.setStatus(Constants.NUMBER_ZERO);
        userEmailCodeService.add(userEmailCode);
    }

    // 邮件发送配置缺失时直接失败，避免接口假成功。
    private void sendEmail(String email, String code) {
        JavaMailSender javaMailSender = javaMailSenderProvider.getIfAvailable();
        if (javaMailSender == null || StringTools.isEmpty(appConfig.getSendUserName())) {
            throw new BusinessException("mail service is not configured");
        }
        SysSettingDto sysSettingDto = redisComponent.getSysSetting();
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(appConfig.getSendUserName());
        mailMessage.setTo(email);
        mailMessage.setSubject(sysSettingDto.getRegisterEMailTitle());
        mailMessage.setText(String.format(sysSettingDto.getRegisterEmailContent(), code));
        try {
            javaMailSender.send(mailMessage);
        } catch (Exception e) {
            throw new BusinessException("send email failed", e);
        }
    }

    private UserInfo getUserInfoByEmail(String email) {
        return userInfoService.getUserInfoByEmail(email);
    }

    private UserInfo getUserById(String userId) {
        UserInfo userInfo = userInfoService.getUserInfoByUserId(userId);
        if (userInfo == null) {
            throw new BusinessException("user not found");
        }
        return userInfo;
    }

    private void checkEmailCanUse(String email, String excludeUserId) {
        UserInfo userInfo = userInfoService.getUserInfoByEmail(email);
        if (userInfo != null && (excludeUserId == null || !excludeUserId.equals(userInfo.getUserId()))) {
            throw new BusinessException("email already registered");
        }
    }

    private void checkNickNameCanUse(String nickName, String userId) {
        if (StringTools.isEmpty(nickName)) {
            return;
        }
        UserInfo userInfo = userInfoService.getUserInfoByNickName(nickName);
        if (userInfo != null && !userId.equals(userInfo.getUserId())) {
            throw new BusinessException("nick name already exists");
        }
    }

    private void checkPhoneCanUse(String phone, String userId) {
        if (StringTools.isEmpty(phone)) {
            return;
        }
        UserInfo userInfo = userInfoService.getUserInfoByPhone(phone);
        if (userInfo != null && !userId.equals(userInfo.getUserId())) {
            throw new BusinessException("phone already exists");
        }
    }

    private UserInfoQuery buildUserInfoUpdateQuery(UserWebInfoUpdateDto updateDto, String userId) {
        UserInfoQuery updateQuery = new UserInfoQuery();
        updateQuery.setAvatar(updateDto.getAvatar());
        updateQuery.setNickName(updateDto.getNickName());
        updateQuery.setSex(updateDto.getSex());
        updateQuery.setBirthday(updateDto.getBirthday());
        updateQuery.setPhone(updateDto.getPhone());
        updateQuery.setAddress(updateDto.getAddress());
        updateQuery.setSchool(updateDto.getSchool());
        updateQuery.setPersonIntroduction(updateDto.getPersonIntroduction());
        updateQuery.setTheme(updateDto.getTheme());
        updateQuery.setUpdateTime(new Date());
        updateQuery.setUpdateBy(userId);
        return updateQuery;
    }

    private boolean hasProfileUpdates(UserWebInfoUpdateDto updateDto) {
        return updateDto != null
                && (updateDto.getAvatar() != null
                || updateDto.getNickName() != null
                || updateDto.getSex() != null
                || updateDto.getBirthday() != null
                || updateDto.getPhone() != null
                || updateDto.getAddress() != null
                || updateDto.getSchool() != null
                || updateDto.getPersonIntroduction() != null
                || updateDto.getTheme() != null);
    }

    // 兼容库里历史明文密码和新写入的 MD5 密码。
    private boolean passwordMatches(String savedPassword, String inputPassword) {
        if (StringTools.isEmpty(savedPassword) || StringTools.isEmpty(inputPassword)) {
            return false;
        }
        String encodePassword = StringTools.encodeByMd5(inputPassword);
        return savedPassword.equals(inputPassword) || savedPassword.equalsIgnoreCase(encodePassword);
    }

    // 用户主键当前还是随机串，先沿用现有项目约定生成。
    private String generateUserId() {
        String userId = StringTools.getRandomString(Constants.NUMBER_10);
        while (userInfoService.getUserInfoByUserId(userId) != null) {
            userId = StringTools.getRandomString(Constants.NUMBER_10);
        }
        return userId;
    }
}
