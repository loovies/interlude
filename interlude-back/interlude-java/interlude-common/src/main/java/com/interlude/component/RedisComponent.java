package com.interlude.component;

import com.interlude.entity.constants.Constants;
import com.interlude.entity.dto.TokenUserInfoDto;
import com.interlude.entity.po.CategoryInfo;
import com.interlude.redis.RedisUtils;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.List;
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
    public String saveAdmin4Token(TokenUserInfoDto tokenUserInfoDto){
        String token = UUID.randomUUID().toString();
        tokenUserInfoDto.setToken(token);
        tokenUserInfoDto.setExpireAt(System.currentTimeMillis() + Constants.REDIS_TIME_ONE_DAY * 7);
        redisUtils.setex(Constants.REDIS_TOKEN_ADMIN_KEY+token,tokenUserInfoDto,Constants.REDIS_TIME_ONE_DAY * 7);
        return token;
    }

    // 获取token
    public TokenUserInfoDto getAdmin4Token(String token){
        return (TokenUserInfoDto) redisUtils.get(Constants.REDIS_TOKEN_ADMIN_KEY + token);
    }

    // 清除token
    public void cleanToken(String token){
        redisUtils.delete(Constants.REDIS_TOKEN_ADMIN_KEY+token);
    }

    // 保存分类信息
    public void saveCategoryList(List<CategoryInfo> categoryInfoList){
        redisUtils.set(Constants.REDIS_KEY_CATEGORY_LIST,categoryInfoList);
    }

    // 获取分类信息
    public List<CategoryInfo> getCategoryList(){
        return  (List<CategoryInfo>) redisUtils.get(Constants.REDIS_KEY_CATEGORY_LIST);
    }
}
