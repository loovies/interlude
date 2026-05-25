package com.interlude.web.controller;

import com.interlude.component.RedisComponent;
import com.interlude.entity.config.AppConfig;
import com.interlude.entity.constants.Constants;
import com.interlude.entity.dto.TokenUserInfoDto;
import com.interlude.entity.vo.ResponseVO;
import com.interlude.enums.ResponseCodeEnum;
import com.interlude.utils.StringTools;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Base controller for web APIs.
 * Provides token/cookie helpers and success response builder.
 */
public class WebBaseController {

    protected static final String STATUS_SUCCESS = "success";
    protected static final String STATUS_ERROR = "error";

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private AppConfig appConfig;

    /**
     * Build a standard success response.
     */
    protected <T> ResponseVO<T> getSuccessResponseVO(T data) {
        ResponseVO<T> responseVO = new ResponseVO<>();
        responseVO.setStatus(STATUS_SUCCESS);
        responseVO.setCode(ResponseCodeEnum.CODE_200.getCode());
        responseVO.setInfo(ResponseCodeEnum.CODE_200.getMsg());
        responseVO.setData(data);
        return responseVO;
    }

    /**
     * Get token user info from current request context.
     */
    protected TokenUserInfoDto getTokenUserInfo() {
        return redisComponent.getWebToken(getTokenFromCurrentRequest());
    }

    /**
     * Read token from current request in RequestContextHolder.
     */
    protected String getTokenFromCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        return getTokenFromRequest(attributes.getRequest());
    }

    /**
     * Read token from header first, then fallback to cookie.
     */
    protected String getTokenFromRequest(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        String token = request.getHeader(Constants.REDIS_WEB_TOKEN);
        if (!StringTools.isEmpty(token)) {
            return token;
        }

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (Constants.REDIS_WEB_TOKEN.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    /**
     * Save token in cookie.
     */
    protected void saveTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(Constants.REDIS_WEB_TOKEN, token);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(Constants.REDIS_TIME_ONE_DAY / Constants.REDIS_TIME_ONE_SECOND * 7);
        response.addCookie(cookie);
    }

    /**
     * Clear current token and delete cookie.
     */
    protected void cleanCurrentToken(HttpServletResponse response) {
        String token = getTokenFromCurrentRequest();
        if (!StringTools.isEmpty(token)) {
            redisComponent.cleanToken(token);
        }

        Cookie cookie = new Cookie(Constants.REDIS_WEB_TOKEN, "");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    /**
     * Read a stored file and write it to the current response.
     */
    protected boolean readFile(HttpServletResponse response, String path) {
        if (response == null || StringTools.isEmpty(path) || !StringTools.pathIsOk(path)) {
            return false;
        }

        File file = resolveStoredFile(path);
        if (!file.exists() || !file.isFile()) {
            return false;
        }

        try (OutputStream os = response.getOutputStream(); InputStream is = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            os.flush();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private File resolveStoredFile(String path) {
        String projectFolder = appConfig.getProjectFolder();
        File directFile = new File(projectFolder, path);
        if (directFile.exists() && directFile.isFile()) {
            return directFile;
        }

        return new File(projectFolder + Constants.FILE_FOLDER + path);
    }
}
