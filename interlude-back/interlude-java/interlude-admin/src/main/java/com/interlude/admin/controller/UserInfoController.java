package com.interlude.admin.controller;

import com.fasterxml.jackson.databind.util.BeanUtil;
import com.interlude.entity.po.UserRole;
import com.interlude.entity.query.UserRoleQuery;
import com.interlude.entity.vo.ResponseVO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.interlude.entity.po.UserInfo;
import com.interlude.entity.query.UserInfoQuery;
import com.interlude.service.UserInfoService;
import com.interlude.service.UserRoleService;
import com.interlude.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
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


	/**
	 * 根据分页条件查询
	 */
	@RequestMapping("loadDataList")
	public ResponseVO loadDataList(UserInfoQuery query) {
		ArrayList<Date> createTimeArray = query.getCreateTimeArray();
		ArrayList<String> createFormatArray = new ArrayList<>();
		if(createTimeArray != null && !createTimeArray.isEmpty()){
			for(Date item : createTimeArray){
				String format = DateUtils.format(item, "yyyy-MM-dd");
				createFormatArray.add(format);
			}
			query.setCreateTimeFormatArray(createFormatArray);
		}

		return getSuccessResponseVO(UserInfoService.findListByPage(query));
	}

	/**
	 * 获取用户职位
	 * @param query
	 * @return
	 */
	@RequestMapping("loadUserRole")
	public ResponseVO getRoleName(UserRoleQuery query) {
		return getSuccessResponseVO(userRoleService.findListByParam(query));
	}



	/**
	 * 批量新增或修改
	 */
	@RequestMapping("addOrUpdateBatch")
	public ResponseVO addOrUpdateBatch(UserInfoQuery query) {
		this.UserInfoService.addOrUpdateUserInfo(query);
		return getSuccessResponseVO(null);
	}

}