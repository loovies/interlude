package com.interlude.component;

import com.interlude.entity.constants.Constants;
import com.interlude.redis.RedisUtils;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.UUID;

@Configuration
public class RedisComponent {

    @Resource
    private RedisUtils redisUtils;

    // 保存验证码 保存6分钟
    public String saveCheckCode(String code){
        String codeKey = UUID.randomUUID().toString();
        redisUtils.setex(Constants.REDIS_CHECKCODE_PREFIX+codeKey,code,Constants.REDIS_TIME_ONE_MINUTE * 6);
        return codeKey;
    }

    // 获取验证码
    public String getCheckCode(String code){
        String codeKey = Constants.REDIS_CHECKCODE_PREFIX+code;
        return  (String) redisUtils.get(codeKey);
    }

    // 删除验证码
    public void delCheckCode(String code){
        String codeKey = Constants.REDIS_CHECKCODE_PREFIX+code;
        redisUtils.delete(codeKey);
    }

    // 保存token 到redis
    public String saveAdmin4Token(String account){
        String token = UUID.randomUUID().toString();
        redisUtils.setex(Constants.REDIS_TOKEN_ADMIN_KEY+token,account,Constants.REDIS_TIME_ONE_DAY);
        return token;
    }

    // 获取token
    public String getAdmin4Token(String token){
        return (String) redisUtils.get(Constants.REDIS_TOKEN_ADMIN_KEY + token);
    }
}
