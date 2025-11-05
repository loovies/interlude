package com.interlude.service.impl;

import com.interlude.entity.constants.Constants;
import com.interlude.component.RedisComponent;
import com.interlude.entity.dto.TokenUserInfoDto;
import com.interlude.entity.po.UserRoleRelation;
import com.interlude.entity.query.SimplePage;
import com.interlude.entity.query.UserRoleRelationQuery;
import com.interlude.enums.PageSize;
import com.interlude.entity.vo.PaginationResultVO;

import java.util.Date;
import java.util.List;
import com.interlude.entity.po.UserInfo;
import com.interlude.entity.query.UserInfoQuery;
import com.interlude.enums.ResponseCodeEnum;
import com.interlude.enums.RoleRelationEnum;
import com.interlude.enums.UserStatusEnum;
import com.interlude.exception.BusinessException;
import com.interlude.mapper.UserInfoMapper;
import com.interlude.mapper.UserRoleRelationMapper;
import com.interlude.service.UserInfoService;
import com.interlude.utils.CopyTools;
import com.interlude.utils.StringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import static com.interlude.entity.constants.Constants.REDIS_ADMIN_TOKEN;

/**
 * @Description:用户信息表
Service
 * @auther:dazhi
 * @date:2025/10/14
 */

@Service("userInfoService")
public class UserInfoServiceImpl implements UserInfoService{

	private static final Logger log = LoggerFactory.getLogger(UserInfoServiceImpl.class);
	@Resource
	private UserInfoMapper<UserInfo,UserInfoQuery> userInfoMapper;

	@Resource
	private UserRoleRelationMapper<UserRoleRelation, UserRoleRelationQuery> userRoleRelationMapper;

	@Resource
	private RedisComponent redisComponent;
	/**
	 * 根据条件查询列表
	 */
	public List<UserInfo> findListByParam(UserInfoQuery query) {
		return this.userInfoMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(UserInfoQuery query) {
		return this.userInfoMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<UserInfo> findListByPage(UserInfoQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(),count,pageSize);
		query.setSimplePage(page);
		List<UserInfo> listByParam = this.findListByParam(query);
		PaginationResultVO<UserInfo> result = new PaginationResultVO(count,page.getPageSize(),page.getPageNo(),page.getPageTotal(),listByParam);
		return result;
	}

	/**
	 * 新增
	 */
	public Integer add(UserInfo bean) {
		return this.userInfoMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<UserInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userInfoMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或修改
	 */
	public Integer addOrUpdateBatch(List<UserInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userInfoMapper.insertOrUpdateBatch(listBean);
	}


	/**
	 * 根据UserId查询
	 */
	public UserInfo getUserInfoByUserId(String userId) {
		return this.userInfoMapper.selectByUserId(userId);
	}


	/**
	 * 根据UserId更新
	 */
	public Integer updateUserInfoByUserId(UserInfoQuery bean, String userId) {
		return this.userInfoMapper.updateByUserId(bean, userId);
	}

	/**
	 * 根据UserId删除
	 */
	public Integer deleteUserInfoByUserId(String userIds) {
		if (StringTools.isEmpty(userIds)) {
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		int i = userIds.indexOf(",");
		if(i != -1){
			String[] split = userIds.split(",");
			this.userInfoMapper.deleteByUserIds(split);
		}else{
			this.userInfoMapper.deleteByUserId(userIds);
		}

		return 1;
	}

	/**
	 * 根据NickName查询
	 */
	public UserInfo getUserInfoByNickName(String nickName) {
		return this.userInfoMapper.selectByNickName(nickName);
	}

	/**
	 * 根据NickName更新
	 */
	public Integer updateUserInfoByNickName(UserInfo bean, String nickName) {
		return this.userInfoMapper.updateByNickName(bean, nickName);
	}

	/**
	 * 根据NickName删除
	 */
	public Integer deleteUserInfoByNickName(String nickName) {
		return this.userInfoMapper.deleteByNickName(nickName);
	}

	/**
	 * 根据Phone查询
	 */
	public UserInfo getUserInfoByPhone(String phone) {
		return this.userInfoMapper.selectByPhone(phone);
	}

	/**
	 * 根据Phone更新
	 */
	public Integer updateUserInfoByPhone(UserInfo bean, String phone) {
		return this.userInfoMapper.updateByPhone(bean, phone);
	}

	/**
	 * 根据Phone删除
	 */
	public Integer deleteUserInfoByPhone(String phone) {
		return this.userInfoMapper.deleteByPhone(phone);
	}

	/**
	 * 根据Email查询
	 */
	public UserInfo getUserInfoByEmail(String email) {
		return this.userInfoMapper.selectByEmail(email);
	}

	/**
	 * 根据Email更新
	 */
	public Integer updateUserInfoByEmail(UserInfo bean, String email) {
		return this.userInfoMapper.updateByEmail(bean, email);
	}

	/**
	 * 根据Email删除
	 */
	public Integer deleteUserInfoByEmail(String email) {
		return this.userInfoMapper.deleteByEmail(email);
	}

	@Override
	public UserInfo login(String account, String password , HttpServletResponse response) {
		UserInfoQuery query = new UserInfoQuery();
		query.setAccount(account);
		query.setPassword(password);
		UserInfo userInfo = userInfoMapper.selectByParam(query);
		if(userInfo == null){
			throw new BusinessException("账号密码不正确");
		}
		if(userInfo.getEnabled() == UserStatusEnum.DISABLE.getStatus()){
			throw new BusinessException("该账号已被封禁");
		}
		UserRoleRelation userRoleRelation = userRoleRelationMapper.selectByUserId(userInfo.getUserId());
		if(userRoleRelation == null || userRoleRelation.getRoleId() == RoleRelationEnum.USER.getStatus()){
			throw new BusinessException("无效账号,无法登录");
		}
		//将用户的数据(生成的token)Dto 存在redis中
		TokenUserInfoDto tokenUserInfoDto = CopyTools.copy(userInfo, TokenUserInfoDto.class);
		String token = redisComponent.saveAdmin4Token(tokenUserInfoDto);

		// 保存token到 cookie
		saveTokenAdminCookie(response,token);

		// 更新更新时间
		UserInfoQuery updateQuery = new UserInfoQuery();
		updateQuery.setUpdateTime(new Date());
		userInfoMapper.updateByUserId(updateQuery,userInfo.getUserId());
		return userInfo;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Integer addOrUpdateUserInfo(UserInfoQuery query,TokenUserInfoDto tokenUserInfo) {
		if (query == null){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		if(query.getUserId() == null){
			// 新增用户
			String userId = StringTools.getRandomString(Constants.NUMBER_10);
			query.setUserId(userId);
			query.setCreateTime(new Date());
			query.setUpdateTime(new Date());
			query.setPwdResetTime(new Date());
			query.setTheme(1);
			query.setCreateBy(tokenUserInfo.getNickName());
			query.setUpdateBy(tokenUserInfo.getNickName());
			userInfoMapper.insert(query);

			// 增加角色
			UserRoleRelationQuery relationQuery = new UserRoleRelationQuery();
			relationQuery.setUserId(userId);
			relationQuery.setRoleId(query.getRoleNameIndex());
			userRoleRelationMapper.insert(relationQuery);
		}else{
			UserInfo userInfo = userInfoMapper.selectByUserId(query.getUserId());
			//修改用户
			query.setUpdateTime(new Date());
			query.setCreateBy(tokenUserInfo.getNickName());
			query.setUpdateBy(tokenUserInfo.getNickName());
			if(!userInfo.getPassword().equalsIgnoreCase(query.getPassword())){
				query.setPwdResetTime(new Date());
			}
			query.setUpdateBy(tokenUserInfo.getNickName());
			userInfoMapper.updateByUserId(query,query.getUserId());
		}
		return 0;
	}

	@Override
	public String resetPassword(String userId) {
		UserInfo userInfo = userInfoMapper.selectByUserId(userId);
		if(userInfo.getEnabled() == Constants.NUMBER_ZERO){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		UserRoleRelation userRoleRelation = userRoleRelationMapper.selectByUserId(userInfo.getUserId());
		if(userRoleRelation.getRoleId() == RoleRelationEnum.SUPERADMIN.getStatus()){
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		UserInfoQuery query = new UserInfoQuery();
		String randomString = StringTools.getRandomString(Constants.NUMBER_15);
		query.setPwdResetTime(new Date());
		query.setPassword(randomString);
		userInfoMapper.updateByUserId(query,userInfo.getUserId());
		return randomString;
	}

	protected void saveTokenAdminCookie(HttpServletResponse response, String token){
		Cookie cookie = new Cookie(Constants.REDIS_ADMIN_TOKEN, token);
		cookie.setPath("/");
		cookie.setMaxAge(Constants.REDIS_TIME_ONE_DAY * 7);
		response.addCookie(cookie);
	}
}