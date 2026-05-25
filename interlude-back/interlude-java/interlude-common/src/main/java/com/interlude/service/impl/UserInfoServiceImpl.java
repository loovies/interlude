package com.interlude.service.impl;

import com.interlude.component.RedisComponent;
import com.interlude.entity.constants.Constants;
import com.interlude.entity.dto.TokenUserInfoDto;
import com.interlude.entity.po.UserInfo;
import com.interlude.entity.po.UserRoleRelation;
import com.interlude.entity.query.SimplePage;
import com.interlude.entity.query.UserInfoQuery;
import com.interlude.entity.query.UserRoleRelationQuery;
import com.interlude.entity.vo.PaginationResultVO;
import com.interlude.enums.PageSize;
import com.interlude.enums.ResponseCodeEnum;
import com.interlude.enums.RoleRelationEnum;
import com.interlude.enums.UserStatusEnum;
import com.interlude.exception.BusinessException;
import com.interlude.mapper.UserInfoMapper;
import com.interlude.mapper.UserRoleRelationMapper;
import com.interlude.service.UserInfoService;
import com.interlude.utils.CopyTools;
import com.interlude.utils.StringTools;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

@Service("userInfoService")
public class UserInfoServiceImpl implements UserInfoService {

    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;

    @Resource
    private UserRoleRelationMapper<UserRoleRelation, UserRoleRelationQuery> userRoleRelationMapper;

    @Resource
    private RedisComponent redisComponent;

    @Override
    public List<UserInfo> findListByParam(UserInfoQuery query) {
        return this.userInfoMapper.selectList(query);
    }

    @Override
    public Integer findCountByParam(UserInfoQuery query) {
        return this.userInfoMapper.selectCount(query);
    }

    @Override
    public PaginationResultVO<UserInfo> findListByPage(UserInfoQuery query) {
        Integer count = this.findCountByParam(query);
        Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
        SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
        query.setSimplePage(page);
        List<UserInfo> listByParam = this.findListByParam(query);
        return new PaginationResultVO<>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), listByParam);
    }

    @Override
    public Integer add(UserInfo bean) {
        return this.userInfoMapper.insert(bean);
    }

    @Override
    public Integer addBatch(List<UserInfo> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.userInfoMapper.insertBatch(listBean);
    }

    @Override
    public Integer addOrUpdateBatch(List<UserInfo> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.userInfoMapper.insertOrUpdateBatch(listBean);
    }

    @Override
    public UserInfo getUserInfoByUserId(String userId) {
        return this.userInfoMapper.selectByUserId(userId);
    }

    @Override
    public Integer updateUserInfoByUserId(UserInfoQuery bean, String userId) {
        return this.userInfoMapper.updateByUserId(bean, userId);
    }

    @Override
    public Integer deleteUserInfoByUserId(String userIds) {
        if (StringTools.isEmpty(userIds)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        if (userIds.contains(",")) {
            this.userInfoMapper.deleteByUserIds(userIds.split(","));
        } else {
            this.userInfoMapper.deleteByUserId(userIds);
        }
        return 1;
    }

    @Override
    public UserInfo getUserInfoByNickName(String nickName) {
        return this.userInfoMapper.selectByNickName(nickName);
    }

    @Override
    public Integer updateUserInfoByNickName(UserInfo bean, String nickName) {
        return this.userInfoMapper.updateByNickName(bean, nickName);
    }

    @Override
    public Integer deleteUserInfoByNickName(String nickName) {
        return this.userInfoMapper.deleteByNickName(nickName);
    }

    @Override
    public UserInfo getUserInfoByPhone(String phone) {
        return this.userInfoMapper.selectByPhone(phone);
    }

    @Override
    public Integer updateUserInfoByPhone(UserInfo bean, String phone) {
        return this.userInfoMapper.updateByPhone(bean, phone);
    }

    @Override
    public Integer deleteUserInfoByPhone(String phone) {
        return this.userInfoMapper.deleteByPhone(phone);
    }

    @Override
    public UserInfo getUserInfoByEmail(String email) {
        return this.userInfoMapper.selectByEmail(email);
    }

    @Override
    public Integer updateUserInfoByEmail(UserInfo bean, String email) {
        return this.userInfoMapper.updateByEmail(bean, email);
    }

    @Override
    public Integer deleteUserInfoByEmail(String email) {
        return this.userInfoMapper.deleteByEmail(email);
    }

    @Override
    public UserInfo login(String account, String password, HttpServletResponse response) {
        UserInfo userInfo = getLoginUser(account, password);
        UserRoleRelation userRoleRelation = getUserRoleRelation(userInfo.getUserId());
        if (userRoleRelation == null || userRoleRelation.getRoleId() == null
                || userRoleRelation.getRoleId().intValue() == RoleRelationEnum.USER.getStatus()) {
            throw new BusinessException("invalid admin account");
        }

        TokenUserInfoDto tokenUserInfoDto = buildTokenUserInfo(userInfo, userRoleRelation);
        String token = redisComponent.saveAdmin4Token(tokenUserInfoDto);
        saveTokenAdminCookie(response, token);

        UserInfoQuery updateQuery = new UserInfoQuery();
        updateQuery.setUpdateTime(new Date());
        userInfoMapper.updateByUserId(updateQuery, userInfo.getUserId());
        return userInfo;
    }

    @Override
    // 前台登录不关心后台角色，只负责校验账号状态并签发 web token。
    public TokenUserInfoDto login4Web(String account, String password) {
        UserInfo userInfo = getLoginUser(account, password);
        UserRoleRelation userRoleRelation = getUserRoleRelation(userInfo.getUserId());
        TokenUserInfoDto tokenUserInfoDto = buildTokenUserInfo(userInfo, userRoleRelation);
        redisComponent.saveWebToken(tokenUserInfoDto);
        return tokenUserInfoDto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer addOrUpdateUserInfo(UserInfoQuery query, TokenUserInfoDto tokenUserInfo) {
        if (query == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        if (query.getUserId() == null) {
            String userId = StringTools.getRandomString(Constants.NUMBER_10);
            query.setUserId(userId);
            query.setCreateTime(new Date());
            query.setUpdateTime(new Date());
            query.setPwdResetTime(new Date());
            query.setTheme(1);
            query.setCreateBy(tokenUserInfo.getNickName());
            query.setUpdateBy(tokenUserInfo.getNickName());
            userInfoMapper.insert(query);

            UserRoleRelationQuery relationQuery = new UserRoleRelationQuery();
            relationQuery.setUserId(userId);
            relationQuery.setRoleId(query.getRoleNameIndex());
            userRoleRelationMapper.insert(relationQuery);
        } else {
            UserInfo userInfo = userInfoMapper.selectByUserId(query.getUserId());
            query.setUpdateTime(new Date());
            query.setCreateBy(tokenUserInfo.getNickName());
            query.setUpdateBy(tokenUserInfo.getNickName());
            if (!userInfo.getPassword().equalsIgnoreCase(query.getPassword())) {
                query.setPwdResetTime(new Date());
            }
            query.setUpdateBy(tokenUserInfo.getNickName());
            userInfoMapper.updateByUserId(query, query.getUserId());
        }
        return 0;
    }

    @Override
    public String resetPassword(String userId) {
        UserInfo userInfo = userInfoMapper.selectByUserId(userId);
        if (userInfo.getEnabled() == Constants.NUMBER_ZERO) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        UserRoleRelation userRoleRelation = userRoleRelationMapper.selectByUserId(userInfo.getUserId());
        if (userRoleRelation.getRoleId() == RoleRelationEnum.SUPERADMIN.getStatus()) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        UserInfoQuery query = new UserInfoQuery();
        String randomString = StringTools.getRandomString(Constants.NUMBER_15);
        query.setPwdResetTime(new Date());
        query.setPassword(randomString);
        userInfoMapper.updateByUserId(query, userInfo.getUserId());
        return randomString;
    }

    protected void saveTokenAdminCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(Constants.REDIS_ADMIN_TOKEN, token);
        cookie.setPath("/");
        cookie.setMaxAge(Constants.REDIS_TIME_ONE_DAY / Constants.REDIS_TIME_ONE_SECOND * 7);
        response.addCookie(cookie);
    }

    private UserInfo getLoginUser(String account, String password) {
        if (StringTools.isEmpty(account) || StringTools.isEmpty(password)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        UserInfo userInfo = findLoginUser(account, password);
        if (userInfo == null) {
            throw new BusinessException("account or password is invalid");
        }
        if (userInfo.getEnabled() != null && userInfo.getEnabled().intValue() == UserStatusEnum.DISABLE.getStatus()) {
            throw new BusinessException("account is disabled");
        }
        return userInfo;
    }

    private UserInfo findLoginUser(String account, String password) {
        UserInfo userInfo = findLoginUserByPassword(account, password);
        if (userInfo != null) {
            return userInfo;
        }
        // 兼容历史上可能直接存明文的账号数据。
        String encodePassword = StringTools.encodeByMd5(password);
        if (StringTools.isEmpty(encodePassword) || encodePassword.equals(password)) {
            return null;
        }
        return findLoginUserByPassword(account, encodePassword);
    }

    private UserInfo findLoginUserByPassword(String account, String password) {
        UserInfoQuery query = new UserInfoQuery();
        query.setAccount(account);
        query.setPassword(password);
        return userInfoMapper.selectByParam(query);
    }

    private UserRoleRelation getUserRoleRelation(String userId) {
        return userRoleRelationMapper.selectByUserId(userId);
    }

    private TokenUserInfoDto buildTokenUserInfo(UserInfo userInfo, UserRoleRelation userRoleRelation) {
        TokenUserInfoDto tokenUserInfoDto = CopyTools.copy(userInfo, TokenUserInfoDto.class);
        if (userRoleRelation != null) {
            tokenUserInfoDto.setRoleId(userRoleRelation.getRoleId());
        }
        return tokenUserInfoDto;
    }
}
