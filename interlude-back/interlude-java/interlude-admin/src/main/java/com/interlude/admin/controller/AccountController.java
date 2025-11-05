package com.interlude.admin.controller;

import com.interlude.component.RedisComponent;
import com.interlude.entity.constants.Constants;
import com.interlude.entity.dto.TokenUserInfoDto;
import com.interlude.entity.po.UserInfo;
import com.interlude.entity.vo.ResponseVO;

import java.util.HashMap;
import java.util.Map;

import com.interlude.entity.query.UserInfoQuery;
import com.interlude.exception.BusinessException;
import com.interlude.service.UserInfoService;
import com.interlude.utils.StringTools;
import com.wf.captcha.ArithmeticCaptcha;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;

/**
 * @Description:用户信息表
Controller
 * @auther:dazhi
 * @date:2025/10/14
 */
@RestController
@RequestMapping("/account")
@Validated  // 校验参数的组件, 给参数增加, 非空,最大值,正则等规则
public class AccountController extends ABaseController{

	private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

	@Resource
	private UserInfoService userInfoService;

	@Resource
	private RedisComponent redisComponent;

	/**
	 * 获取验证码
	 */
	@RequestMapping("/checkCode")
	public ResponseVO checkCode() {
		ArithmeticCaptcha captcha = new ArithmeticCaptcha(160, 42);
		String code = captcha.text();
		// 储存验证码到redis,并生成唯一ID成key, 防止验证码覆盖
		String codeKey = redisComponent.saveCheckCode(code);
		String checkCodeBase64 = captcha.toBase64();

		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put("code", checkCodeBase64);
		resultMap.put("codeKey", codeKey);
		return getSuccessResponseVO(resultMap);
	}

	@RequestMapping("/login")
	public ResponseVO login(
							HttpServletResponse response,
							HttpServletRequest request,
							@NotEmpty String account,
							@NotEmpty String password,
							@NotEmpty String checkCodeKey,
							@NotEmpty String checkCode) {
		try {
			if(!checkCode.equalsIgnoreCase(redisComponent.getCheckCode(checkCodeKey))){
				throw new BusinessException("图片验证码不正确");
			}
			UserInfo userInfo = userInfoService.login(account, password,response);
			return getSuccessResponseVO(userInfo);
		}catch (BusinessException e) {
			throw new RuntimeException(e);
		}finally {
			redisComponent.delCheckCode(checkCodeKey);
			Cookie[] cookies = request.getCookies();
			if (cookies != null){
				String token = null;
				for (Cookie cookie : cookies) {
					if(cookie.getName().equals(Constants.REDIS_ADMIN_TOKEN)){
						token = cookie.getValue();
					}
				}
				if(!StringTools.isEmpty(token)){
					redisComponent.cleanToken(token);;
				}
			}
		}
	}

	@RequestMapping("/autoLogin")
	public ResponseVO autologin(HttpServletResponse response){
		TokenUserInfoDto tokenUserInfoDto = getTokenUserInfo();

		if(tokenUserInfoDto == null){
			return getSuccessResponseVO(null);
		}

		if(tokenUserInfoDto.getExpireAt() - System.currentTimeMillis() < Constants.REDIS_TIME_ONE_DAY){
			redisComponent.saveAdmin4Token(tokenUserInfoDto);
			saveTokenAdminCookie(response, tokenUserInfoDto.getToken());
		}
		saveTokenAdminCookie(response, tokenUserInfoDto.getToken());
		return getSuccessResponseVO(tokenUserInfoDto);
	}

	@RequestMapping("/logout")
	public ResponseVO logout(HttpServletResponse response){
		this.cleanToken2Cookie(response);
		return getSuccessResponseVO("退出成功");
	}
}