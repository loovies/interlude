package com.interlude.service;

import com.interlude.entity.dto.TokenUserInfoDto;
import com.interlude.entity.vo.PaginationResultVO;
import java.util.List;
import com.interlude.entity.po.UserInfo;
import com.interlude.entity.query.UserInfoQuery;
import com.interlude.exception.BusinessException;

import javax.servlet.http.HttpServletResponse;

/**
 * @Description:用户信息表
Service
 * @auther:dazhi
 * @date:2025/10/14
 */

public interface UserInfoService{

	/**
	 * 根据条件查询列表
	 */
	List<UserInfo> findListByParam(UserInfoQuery param);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(UserInfoQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<UserInfo> findListByPage(UserInfoQuery param);

	/**
	 * 新增
	 */
	Integer add(UserInfo bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<UserInfo> listBean);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<UserInfo> listBean);

	/**
	 * 根据UserId查询
	 */
	UserInfo getUserInfoByUserId(String userId);

	/**
	 * 根据UserId更新
	 */
	Integer updateUserInfoByUserId(UserInfoQuery bean, String userId);

	/**
	 * 根据UserId删除
	 */
	Integer deleteUserInfoByUserId(String userIds);

	/**
	 * 根据NickName查询
	 */
	UserInfo getUserInfoByNickName(String nickName);

	/**
	 * 根据NickName更新
	 */
	Integer updateUserInfoByNickName(UserInfo bean, String nickName);

	/**
	 * 根据NickName删除
	 */
	Integer deleteUserInfoByNickName(String nickName);

	/**
	 * 根据Phone查询
	 */
	UserInfo getUserInfoByPhone(String phone);

	/**
	 * 根据Phone更新
	 */
	Integer updateUserInfoByPhone(UserInfo bean, String phone);

	/**
	 * 根据Phone删除
	 */
	Integer deleteUserInfoByPhone(String phone);

	/**
	 * 根据Email查询
	 */
	UserInfo getUserInfoByEmail(String email);

	/**
	 * 根据Email更新
	 */
	Integer updateUserInfoByEmail(UserInfo bean, String email);

	/**
	 * 根据Email删除
	 */
	Integer deleteUserInfoByEmail(String email);

	UserInfo login(String account, String password, HttpServletResponse response) ;

	TokenUserInfoDto login4Web(String account, String password);

	Integer addOrUpdateUserInfo(UserInfoQuery query, TokenUserInfoDto tokenUserInfo);

	String resetPassword(String userId);
}
