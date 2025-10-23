package com.interlude.service.impl;

import com.interlude.entity.query.SimplePage;
import com.interlude.enums.PageSize;
import com.interlude.entity.vo.PaginationResultVO;
import java.util.List;
import com.interlude.entity.po.UserRole;
import com.interlude.entity.query.UserRoleQuery;
import com.interlude.mapper.UserRoleMapper;
import com.interlude.service.UserRoleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;/**
 * @Description:Service
 * @auther:dazhi
 * @date:2025/10/14
 */

@Service("userRoleService")
public class UserRoleServiceImpl implements UserRoleService{

	@Resource
	private UserRoleMapper<UserRole,UserRoleQuery> userRoleMapper;
	/**
	 * 根据条件查询列表
	 */
	public List<UserRole> findListByParam(UserRoleQuery query) {
		return this.userRoleMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(UserRoleQuery query) {
		return this.userRoleMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<UserRole> findListByPage(UserRoleQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(),count,pageSize);
		query.setSimplePage(page);
		List<UserRole> listByParam = this.findListByParam(query);
		PaginationResultVO<UserRole> result = new PaginationResultVO(count,page.getPageSize(),page.getPageNo(),page.getPageTotal(),listByParam);
		return result;
	}

	/**
	 * 新增
	 */
	public Integer add(UserRole bean) {
		return this.userRoleMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<UserRole> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userRoleMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或修改
	 */
	public Integer addOrUpdateBatch(List<UserRole> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userRoleMapper.insertOrUpdateBatch(listBean);
	}


	/**
	 * 根据RoleId查询
	 */
	public UserRole getUserRoleByRoleId(Long roleId) {
		return this.userRoleMapper.selectByRoleId(roleId);
	}

	/**
	 * 根据RoleId更新
	 */
	public Integer updateUserRoleByRoleId(UserRole bean, Long roleId) {
		return this.userRoleMapper.updateByRoleId(bean, roleId);
	}

	/**
	 * 根据RoleId删除
	 */
	public Integer deleteUserRoleByRoleId(Long roleId) {
		return this.userRoleMapper.deleteByRoleId(roleId);
	}
}