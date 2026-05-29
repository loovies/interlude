package com.interlude.auth.controller;

import com.interlude.component.RedisComponent;
import com.interlude.entity.config.AppConfig;
import com.interlude.entity.constants.Constants;
import com.interlude.entity.dto.TokenUserInfoDto;
import com.interlude.entity.dto.UserWebInfoDto;
import com.interlude.entity.dto.UserWebInfoUpdateDto;
import com.interlude.entity.vo.ResponseVO;
import com.interlude.enums.DateTimePatterEnum;
import com.interlude.enums.ResponseCodeEnum;
import com.interlude.exception.BusinessException;
import com.interlude.service.UserInfoService;
import com.interlude.service.WebAccountService;
import com.interlude.utils.DateUtils;
import com.interlude.utils.StringTools;
import com.interlude.auth.entity.dto.*;
import com.wf.captcha.SpecCaptcha;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * Account APIs for web clients.
 * The controller focuses on validation, token/cookie flow, and response shaping.
 */
@RestController
@RequestMapping("/account")
@Validated
public class AccountController extends WebBaseController {

    private static final String MESSAGE_SEND_SUCCESS = "send success";

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private WebAccountService webAccountService;

    @Resource
    private AppConfig appConfig;

    /**
     * Generate captcha image and cache key.
     */
    @GetMapping("/checkCode")
    public ResponseVO<CheckCodeResponseDto> checkCode() {
        SpecCaptcha captcha = new SpecCaptcha(160, 42);
        captcha.setLen(5);
        String code = captcha.text();
        String codeKey = redisComponent.saveCheckCode(code);

        CheckCodeResponseDto responseDto = new CheckCodeResponseDto();
        responseDto.setCode(captcha.toBase64());
        responseDto.setCodeKey(codeKey);
        // Keep legacy field for backward compatibility.
        responseDto.setCheckCodeKey(codeKey);
        return getSuccessResponseVO(responseDto);
    }

    /**
     * Login by account and password.
     */
    @PostMapping("/login")
    public ResponseVO<TokenUserInfoDto> login(@Valid @RequestBody LoginRequestDto requestDto,
                                              HttpServletRequest request,
                                              HttpServletResponse response) {
        validateImageCheckCode(requestDto.getCheckCodeKey(), requestDto.getCheckCode());
        String oldToken = getTokenFromRequest(request);
        try {
            TokenUserInfoDto tokenUserInfoDto = userInfoService.login4Web(requestDto.getAccount(), requestDto.getPassword());
            saveTokenAndCleanOldToken(response, oldToken, tokenUserInfoDto.getToken());
            return getSuccessResponseVO(tokenUserInfoDto);
        } finally {
            redisComponent.delCheckCode(requestDto.getCheckCodeKey());
        }
    }

    /**
     * Send register email code.
     */
    @PostMapping("/sendEmailCode")
    public ResponseVO<String> sendEmailCode(@Valid @RequestBody SendEmailCodeRequestDto requestDto) {
        return sendEmailCodeWithImageCheck(requestDto, webAccountService::sendRegisterEmailCode);
    }

    /**
     * Send reset-password email code.
     */
    @PostMapping("/sendResetPasswordEmailCode")
    public ResponseVO<String> sendResetPasswordEmailCode(@Valid @RequestBody SendEmailCodeRequestDto requestDto) {
        return sendEmailCodeWithImageCheck(requestDto, webAccountService::sendResetPasswordEmailCode);
    }

    /**
     * Send update-email code for current user.
     */
    @PostMapping("/sendUpdateEmailCode")
    public ResponseVO<String> sendUpdateEmailCode(@Valid @RequestBody SendEmailCodeRequestDto requestDto) {
        TokenUserInfoDto tokenUserInfoDto = requireLoginUser();
        return sendEmailCodeWithImageCheck(requestDto,
                email -> webAccountService.sendUpdateEmailCode(tokenUserInfoDto.getUserId(), email));
    }

    /**
     * Register and login immediately.
     */
    @PostMapping("/register")
    public ResponseVO<TokenUserInfoDto> register(@Valid @RequestBody RegisterRequestDto requestDto,
                                                 HttpServletRequest request,
                                                 HttpServletResponse response) {
        String oldToken = getTokenFromRequest(request);
        TokenUserInfoDto tokenUserInfoDto = webAccountService.register(
                requestDto.getEmail(),
                requestDto.getNickName(),
                requestDto.getPassword(),
                requestDto.getEmailCode()
        );
        saveTokenAndCleanOldToken(response, oldToken, tokenUserInfoDto.getToken());
        return getSuccessResponseVO(tokenUserInfoDto);
    }

    /**
     * Reset password by email code.
     */
    @PostMapping("/resetPassword")
    public ResponseVO<String> resetPassword(@Valid @RequestBody ResetPasswordRequestDto requestDto) {
        webAccountService.resetPassword(
                requestDto.getEmail(),
                requestDto.getPassword(),
                requestDto.getEmailCode()
        );
        return getSuccessResponseVO("reset success");
    }

    /**
     * Auto login and refresh token TTL when needed.
     */
    @GetMapping("/autoLogin")
    public ResponseVO<TokenUserInfoDto> autoLogin(HttpServletResponse response) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();
        if (tokenUserInfoDto == null) {
            return getSuccessResponseVO(null);
        }
        if (shouldRefreshToken(tokenUserInfoDto)) {
            redisComponent.saveWebToken(tokenUserInfoDto);
        }
        saveTokenCookie(response, tokenUserInfoDto.getToken());
        return getSuccessResponseVO(tokenUserInfoDto);
    }

    /**
     * Logout and clear current token.
     */
    @PostMapping("/logout")
    public ResponseVO<String> logout(HttpServletResponse response) {
        cleanCurrentToken(response);
        return getSuccessResponseVO("logout success");
    }

    /**
     * Get current user profile.
     */
    @GetMapping("/getUserInfo")
    public ResponseVO<UserWebInfoDto> getUserInfo() {
        TokenUserInfoDto tokenUserInfoDto = requireLoginUser();
        return getSuccessResponseVO(webAccountService.getCurrentUserInfo(tokenUserInfoDto.getUserId()));
    }

    /**
     * Update current user profile.
     */
    @PostMapping("/updateUserInfo")
    public ResponseVO<UserWebInfoDto> updateUserInfo(@Valid @RequestBody UpdateUserInfoRequestDto requestDto,
                                                     HttpServletResponse response) {
        TokenUserInfoDto tokenUserInfoDto = requireLoginUser();
        UserWebInfoDto userWebInfoDto = webAccountService.updateCurrentUserInfo(
                tokenUserInfoDto.getUserId(),
                buildUserInfoUpdateDto(requestDto)
        );
        refreshCurrentToken(tokenUserInfoDto, userWebInfoDto, response);
        return getSuccessResponseVO(userWebInfoDto);
    }

    /**
     * Update current user email.
     */
    @PostMapping("/updateEmail")
    public ResponseVO<UserWebInfoDto> updateEmail(@Valid @RequestBody UpdateEmailRequestDto requestDto,
                                                  HttpServletResponse response) {
        TokenUserInfoDto tokenUserInfoDto = requireLoginUser();
        UserWebInfoDto userWebInfoDto = webAccountService.updateEmail(
                tokenUserInfoDto.getUserId(),
                requestDto.getEmail(),
                requestDto.getEmailCode()
        );
        refreshCurrentToken(tokenUserInfoDto, userWebInfoDto, response);
        return getSuccessResponseVO(userWebInfoDto);
    }

    /**
     * Bind phone for current user.
     */
    @PostMapping("/bindPhone")
    public ResponseVO<UserWebInfoDto> bindPhone(@Valid @RequestBody BindPhoneRequestDto requestDto,
                                                HttpServletResponse response) {
        TokenUserInfoDto tokenUserInfoDto = requireLoginUser();
        UserWebInfoDto userWebInfoDto = webAccountService.bindPhone(tokenUserInfoDto.getUserId(), requestDto.getPhone());
        refreshCurrentToken(tokenUserInfoDto, userWebInfoDto, response);
        return getSuccessResponseVO(userWebInfoDto);
    }

    /**
     * Upload avatar and update user profile.
     */
    @PostMapping("/uploadAvatar")
    public ResponseVO<UserWebInfoDto> uploadAvatar(@RequestParam("file") MultipartFile file,
                                                   HttpServletResponse response) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("avatar file can not be empty");
        }
        TokenUserInfoDto tokenUserInfoDto = requireLoginUser();
        String avatarPath = saveAvatarFile(file, tokenUserInfoDto.getUserId());
        UserWebInfoDto userWebInfoDto = webAccountService.updateAvatar(tokenUserInfoDto.getUserId(), avatarPath);
        refreshCurrentToken(tokenUserInfoDto, userWebInfoDto, response);
        return getSuccessResponseVO(userWebInfoDto);
    }

    /**
     * Update current user password.
     */
    @PostMapping("/updatePassword")
    public ResponseVO<String> updatePassword(@Valid @RequestBody UpdatePasswordRequestDto requestDto) {
        TokenUserInfoDto tokenUserInfoDto = requireLoginUser();
        webAccountService.updatePassword(tokenUserInfoDto.getUserId(), requestDto.getOldPassword(), requestDto.getNewPassword());
        return getSuccessResponseVO("update success");
    }

    /**
     * Close current account.
     */
    @PostMapping("/closeAccount")
    public ResponseVO<String> closeAccount(@Valid @RequestBody CloseAccountRequestDto requestDto,
                                           HttpServletResponse response) {
        TokenUserInfoDto tokenUserInfoDto = requireLoginUser();
        webAccountService.closeAccount(tokenUserInfoDto.getUserId(), requestDto.getPassword());
        cleanCurrentToken(response);
        return getSuccessResponseVO("close success");
    }

    /**
     * Shared flow: validate image code, run action, and clear image code key.
     */
    private ResponseVO<String> sendEmailCodeWithImageCheck(SendEmailCodeRequestDto requestDto,
                                                           Consumer<String> sendAction) {
        try {
            validateImageCheckCode(requestDto.getCheckCodeKey(), requestDto.getCheckCode());
            sendAction.accept(requestDto.getEmail());
            return getSuccessResponseVO(MESSAGE_SEND_SUCCESS);
        } finally {
            redisComponent.delCheckCode(requestDto.getCheckCodeKey());
        }
    }

    /**
     * Save new token and clear old token when token changed.
     */
    private void saveTokenAndCleanOldToken(HttpServletResponse response, String oldToken, String newToken) {
        saveTokenCookie(response, newToken);
        if (!StringTools.isEmpty(oldToken) && !oldToken.equals(newToken)) {
            redisComponent.cleanToken(oldToken);
        }
    }

    /**
     * Require a logged-in user and throw 901 when token is missing.
     */
    private TokenUserInfoDto requireLoginUser() {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();
        if (tokenUserInfoDto == null || StringTools.isEmpty(tokenUserInfoDto.getUserId())) {
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }
        return tokenUserInfoDto;
    }

    /**
     * Validate image captcha value.
     */
    private void validateImageCheckCode(String checkCodeKey, String checkCode) {
        if (StringTools.isEmpty(checkCodeKey) || StringTools.isEmpty(checkCode)) {
            throw new BusinessException("check code is invalid");
        }
        String cacheCode = redisComponent.getCheckCode(checkCodeKey);
        if (StringTools.isEmpty(cacheCode) || !cacheCode.equalsIgnoreCase(checkCode)) {
            throw new BusinessException("check code is invalid");
        }
    }

    /**
     * Sync profile fields back to token and cookie after update.
     */
    private void refreshCurrentToken(TokenUserInfoDto tokenUserInfoDto, UserWebInfoDto userWebInfoDto,
                                     HttpServletResponse response) {
        tokenUserInfoDto.setNickName(userWebInfoDto.getNickName());
        tokenUserInfoDto.setAvatar(userWebInfoDto.getAvatar());
        tokenUserInfoDto.setEmail(userWebInfoDto.getEmail());
        tokenUserInfoDto.setPhone(userWebInfoDto.getPhone());
        redisComponent.saveWebToken(tokenUserInfoDto);
        saveTokenCookie(response, tokenUserInfoDto.getToken());
    }

    /**
     * Build update DTO from request payload.
     */
    private UserWebInfoUpdateDto buildUserInfoUpdateDto(UpdateUserInfoRequestDto requestDto) {
        UserWebInfoUpdateDto updateDto = new UserWebInfoUpdateDto();
        updateDto.setAvatar(requestDto.getAvatar());
        updateDto.setNickName(requestDto.getNickName());
        updateDto.setSex(requestDto.getSex());
        updateDto.setBirthday(requestDto.getBirthday());
        updateDto.setPhone(requestDto.getPhone());
        updateDto.setAddress(requestDto.getAddress());
        updateDto.setSchool(requestDto.getSchool());
        updateDto.setPersonIntroduction(requestDto.getPersonIntroduction());
        updateDto.setTheme(requestDto.getTheme());
        return updateDto;
    }

    /**
     * Check whether token is close to expiration.
     */
    private boolean shouldRefreshToken(TokenUserInfoDto tokenUserInfoDto) {
        return tokenUserInfoDto.getExpireAt() != null
                && tokenUserInfoDto.getExpireAt() - System.currentTimeMillis() < Constants.REDIS_TIME_ONE_DAY;
    }

    /**
     * Save avatar to disk and return relative avatar path.
     */
    private String saveAvatarFile(MultipartFile file, String userId) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String fileSuffix = StringTools.getFileSuffix(originalFilename);
        if (!isImageSuffix(fileSuffix)) {
            throw new BusinessException("avatar file type is invalid");
        }

        String month = DateUtils.format(new Date(), DateTimePatterEnum.YYYY_MM.getPattern());
        String folder = appConfig.getProjectFolder() + Constants.FILE_FOLDER + Constants.FILE_AVATAR + month;
        File folderFile = new File(folder);
        if (!folderFile.exists() && !folderFile.mkdirs()) {
            throw new BusinessException("create avatar folder failed");
        }

        String realFileName = userId + "_" + StringTools.getRandomString(20) + fileSuffix.toLowerCase(Locale.ROOT);
        File targetFile = new File(folder + File.separator + realFileName);
        file.transferTo(targetFile);
        return Constants.FILE_AVATAR + month + "/" + realFileName;
    }

    /**
     * Image suffix whitelist for avatar upload.
     */
    private boolean isImageSuffix(String fileSuffix) {
        if (StringTools.isEmpty(fileSuffix)) {
            return false;
        }
        String suffix = fileSuffix.toLowerCase(Locale.ROOT);
        return ".jpg".equals(suffix) || ".jpeg".equals(suffix) || ".png".equals(suffix)
                || ".gif".equals(suffix) || ".webp".equals(suffix);
    }
}
