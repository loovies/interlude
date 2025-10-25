package com.interlude.admin.controller;

import com.interlude.component.RedisComponent;
import com.interlude.entity.config.AppConfig;
import com.interlude.entity.constants.Constants;
import com.interlude.entity.dto.TokenUserInfoDto;
import com.interlude.enums.ResponseCodeEnum;;

import com.interlude.entity.vo.ResponseVO;
import com.interlude.utils.StringTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;;import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static com.interlude.entity.constants.Constants.REDIS_ADMIN_TOKEN;

@Slf4j
public class ABaseController {

    protected static final String STATUC_SUCCESS = "success";

    protected static final String STATUC_ERROR = "error";

    @Resource
    private AppConfig appConfig;

    @Resource
    private RedisComponent redisComponent;

    protected <T> ResponseVO getSuccessResponseVO(T t) {
        ResponseVO<T> responseVO = new ResponseVO<>();
        responseVO.setStatus(STATUC_SUCCESS);
        responseVO.setCode(ResponseCodeEnum.CODE_200.getCode());
        responseVO.setInfo(ResponseCodeEnum.CODE_200.getMsg());
        responseVO.setData(t);
        return responseVO;
    }

    protected void saveTokenAdminCookie(HttpServletResponse response, String token){
        Cookie cookie = new Cookie(REDIS_ADMIN_TOKEN, token);
        cookie.setPath("/");
        cookie.setMaxAge(Constants.REDIS_TIME_ONE_DAY * 7);
        response.addCookie(cookie);
    }

    protected void readFile(HttpServletResponse response, String path){
        if(!StringTools.pathIsOk(path)){
            return;
        }
        File file = new File(appConfig.getProjectFolder() + Constants.FILE_FOLDER + path);
        if(!file.exists()){
            file.mkdirs();
        }
        try(OutputStream os = response.getOutputStream(); InputStream is = new FileInputStream(file)){
            byte[] buffer = new byte[1024];
            int len = 0;
            while((len = is.read(buffer)) != -1){
                os.write(buffer, 0, len);
            }
            os.flush();
        }catch (Exception e){
            log.error("读取文件异常",e);
        }
    }

    protected TokenUserInfoDto getTokenUserInfo(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader(REDIS_ADMIN_TOKEN);
        return redisComponent.getAdmin4Token(token);
    }
}
