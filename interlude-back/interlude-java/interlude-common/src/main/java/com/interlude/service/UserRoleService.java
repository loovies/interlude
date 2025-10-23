package com.interlude.service;

import com.interlude.entity.vo.PaginationResultVO;
import java.util.List;
import com.interlude.entity.po.UserRole;
import com.interlude.entity.query.UserRoleQuery;
/**
 * @Description:Service
 * @auther:dazhi
 * @date:2025/10/14
 */

public interface UserRoleService{

	/**
	 * 根据条件查询列表
	 */
	List<UserRole> findListByParam(UserRoleQuery param);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(UserRoleQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<UserRole> findListByPage(UserRoleQuery param);

	/**
	 * 新增
	 */
	Integer add(UserRole bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<UserRole> listBean);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<UserRole> listBean);

	/**
	 * 根据RoleId查询
	 */
	UserRole getUserRoleByRoleId(Long roleId);

	/**
	 * 根据RoleId更新
	 */
	Integer updateUserRoleByRoleId(UserRole bean, Long roleId);

	/**
	 * 根据RoleId删除
	 */
	Integer deleteUserRoleByRoleId(Long roleId);

}