package com.interlude.service;

import com.interlude.entity.vo.PaginationResultVO;
import java.util.List;
import com.interlude.entity.po.UserRoleRelation;
import com.interlude.entity.query.UserRoleRelationQuery;
/**
 * @Description:Service
 * @auther:dazhi
 * @date:2025/10/14
 */

public interface UserRoleRelationService{

	/**
	 * 根据条件查询列表
	 */
	List<UserRoleRelation> findListByParam(UserRoleRelationQuery param);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(UserRoleRelationQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<UserRoleRelation> findListByPage(UserRoleRelationQuery param);

	/**
	 * 新增
	 */
	Integer add(UserRoleRelation bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<UserRoleRelation> listBean);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<UserRoleRelation> listBean);

	/**
	 * 根据Id查询
	 */
	UserRoleRelation getUserRoleRelationById(Integer id);

	/**
	 * 根据Id更新
	 */
	public Integer updateUserRoleRelationByUserId(UserRoleRelation bean, String userId);

	/**
	 * 根据Id删除
	 */
	Integer deleteUserRoleRelationById(Integer id);

    UserRoleRelation getRoleRelationByUserId(String userId);
}