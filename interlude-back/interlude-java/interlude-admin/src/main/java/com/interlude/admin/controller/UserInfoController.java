package com.interlude.admin.controller;

import com.interlude.entity.dto.TokenUserInfoDto;
import com.interlude.entity.po.UserInfo;
import com.interlude.entity.po.UserRoleRelation;
import com.interlude.entity.query.UserRoleQuery;
import com.interlude.entity.vo.PaginationResultVO;
import com.interlude.entity.vo.ResponseVO;

import java.util.ArrayList;
import java.util.Date;
import com.interlude.entity.query.UserInfoQuery;
import com.interlude.service.UserInfoService;
import com.interlude.service.UserRoleRelationService;
import com.interlude.service.UserRoleService;
import com.interlude.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;

/**
 * @Description:用户信息表
Controller
 * @auther:dazhi
 * @date:2025/10/21
 */
@RestController
@RequestMapping("/user")
@Validated
public class UserInfoController extends ABaseController{

	private static final Logger log = LoggerFactory.getLogger(UserInfoController.class);
	@Resource
	private UserInfoService UserInfoService;

	@Resource
	private UserRoleService userRoleService;

	@Resource
	private UserRoleRelationService userRoleRelationService;

	/**
	 * 根据分页条件查询
	 */
	@RequestMapping("loadDataList")
	public ResponseVO loadDataList(UserInfoQuery query) {
		ArrayList<Date> createTimeArray = query.getCreateTimeArray();
		ArrayList<String> createFormatArray = new ArrayList<>();
		// 查询多个创建时间
		if(createTimeArray != null && !createTimeArray.isEmpty()){
			for(Date item : createTimeArray){
				String format = DateUtils.format(item, "yyyy-MM-dd");
				createFormatArray.add(format);
			}
			query.setCreateTimeFormatArray(createFormatArray);
		}
		// 更改创建日期排序
		if(query.getIsCreatTimeDesc().equalsIgnoreCase("desc")){
			query.setOrderBy("u.create_time desc");
		}else{
			query.setOrderBy("u.create_time asc");
		}

		PaginationResultVO<UserInfo> listByPage = UserInfoService.findListByPage(query);
		listByPage.getList().stream().forEach(item -> {
			UserRoleRelation roleRelation = userRoleRelationService.getRoleRelationByUserId(item.getUserId());
			if (roleRelation != null){
				item.setRoleId(roleRelation.getRoleId());
			}
		});
		return getSuccessResponseVO(listByPage);
	}

	/**
	 * 获取所有的用户职位
	 * @param query
	 * @return
	 */
	@RequestMapping("loadUserRole")
	public ResponseVO getRoleName(UserRoleQuery query) {
		return getSuccessResponseVO(userRoleService.findListByParam(query));
	}

	/**
	 * 根据用户ID获取用户职位
	 * @return
	 */
	@RequestMapping("getRoleByUserId")
	public ResponseVO getRoleName(@NotNull String userId) {
		return getSuccessResponseVO(userRoleRelationService.getRoleRelationByUserId(userId));
	}

	/**
	 * 批量新增或修改
	 */
	@RequestMapping("addOrUpdateBatch")
	public ResponseVO addOrUpdateBatch(UserInfoQuery query) {
		TokenUserInfoDto tokenUserInfo = getTokenUserInfo();
		System.out.println(tokenUserInfo.getUserId());
		this.UserInfoService.addOrUpdateUserInfo(query,tokenUserInfo);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据UserId批量删除
	 */
	@RequestMapping("deleteUserInfoByUserId")
	public ResponseVO deleteUserInfoByUserId(@NotNull String userIds) {
		this.UserInfoService.deleteUserInfoByUserId(userIds);
		return getSuccessResponseVO(null);
	}


	/**
	 * 根据UserId删除
	 */
	@RequestMapping("updateUserRelation")
	public ResponseVO updateUserRelation(@NotNull String userId,@NotNull Long roleId) {
		UserRoleRelation relation = new UserRoleRelation();
		relation.setRoleId(roleId);
		relation.setUserId(userId);
		this.userRoleRelationService.updateUserRoleRelationByUserId(relation,userId);
		return getSuccessResponseVO(null);
	}

	/**
	 * 重置密码
	 * @param userId
	 * @return
	 */
	@RequestMapping("getResetPassword")
	public ResponseVO getResetPassword(@NotNull String userId){
		String newPwd = UserInfoService.resetPassword(userId);
		return getSuccessResponseVO(newPwd);
	}
}