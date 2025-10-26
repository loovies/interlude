package com.interlude.service.impl;

import com.interlude.entity.query.SimplePage;
import com.interlude.enums.PageSize;
import com.interlude.entity.vo.PaginationResultVO;
import java.util.List;
import com.interlude.entity.po.UserRoleRelation;
import com.interlude.entity.query.UserRoleRelationQuery;
import com.interlude.mapper.UserRoleRelationMapper;
import com.interlude.service.UserRoleRelationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;/**
 * @Description:Service
 * @auther:dazhi
 * @date:2025/10/14
 */

@Service("userRoleRelationService")
public class UserRoleRelationServiceImpl implements UserRoleRelationService{

	@Resource
	private UserRoleRelationMapper<UserRoleRelation,UserRoleRelationQuery> userRoleRelationMapper;
	/**
	 * 根据条件查询列表
	 */
	public List<UserRoleRelation> findListByParam(UserRoleRelationQuery query) {
		return this.userRoleRelationMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(UserRoleRelationQuery query) {
		return this.userRoleRelationMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<UserRoleRelation> findListByPage(UserRoleRelationQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(),count,pageSize);
		query.setSimplePage(page);
		List<UserRoleRelation> listByParam = this.findListByParam(query);
		PaginationResultVO<UserRoleRelation> result = new PaginationResultVO(count,page.getPageSize(),page.getPageNo(),page.getPageTotal(),listByParam);
		return result;
	}

	/**
	 * 新增
	 */
	public Integer add(UserRoleRelation bean) {
		return this.userRoleRelationMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<UserRoleRelation> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userRoleRelationMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或修改
	 */
	public Integer addOrUpdateBatch(List<UserRoleRelation> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userRoleRelationMapper.insertOrUpdateBatch(listBean);
	}


	/**
	 * 根据Id查询
	 */
	public UserRoleRelation getUserRoleRelationById(Integer id) {
		return this.userRoleRelationMapper.selectById(id);
	}

	/**
	 * 根据Id更新
	 */
	public Integer updateUserRoleRelationByUserId(UserRoleRelation bean, String userId) {
		return this.userRoleRelationMapper.updateById(bean, userId);
	}

	/**
	 * 根据Id删除
	 */
	public Integer deleteUserRoleRelationById(Integer id) {
		return this.userRoleRelationMapper.deleteById(id);
	}

	@Override
	public UserRoleRelation getRoleRelationByUserId(String userId) {
		return this.userRoleRelationMapper.selectByUserId(userId);
	}
}