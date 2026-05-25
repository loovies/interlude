package com.interlude.service.impl;

import com.interlude.entity.query.SimplePage;
import com.interlude.enums.PageSize;
import com.interlude.entity.vo.PaginationResultVO;
import java.util.List;
import com.interlude.entity.po.UserLog;
import com.interlude.entity.query.UserLogQuery;
import com.interlude.mapper.UserLogMapper;
import com.interlude.service.UserLogService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;/**
 * @Description:用户日志表Service
 * @auther:dazhi
 * @date:2025/10/14
 */

@Service("userLogService")
public class UserLogServiceImpl implements UserLogService{

	@Resource
	private UserLogMapper<UserLog,UserLogQuery> userLogMapper;
	/**
	 * 根据条件查询列表
	 */
	public List<UserLog> findListByParam(UserLogQuery query) {
		return this.userLogMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(UserLogQuery query) {
		return this.userLogMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<UserLog> findListByPage(UserLogQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(),count,pageSize);
		query.setSimplePage(page);
		List<UserLog> listByParam = this.findListByParam(query);
		PaginationResultVO<UserLog> result = new PaginationResultVO(count,page.getPageSize(),page.getPageNo(),page.getPageTotal(),listByParam);
		return result;
	}

	/**
	 * 新增
	 */
	public Integer add(UserLog bean) {
		return this.userLogMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<UserLog> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userLogMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或修改
	 */
	public Integer addOrUpdateBatch(List<UserLog> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userLogMapper.insertOrUpdateBatch(listBean);
	}


	/**
	 * 根据LogId查询
	 */
	public UserLog getUserLogByLogId(Integer logId) {
		return this.userLogMapper.selectByLogId(logId);
	}

	/**
	 * 根据LogId更新
	 */
	public Integer updateUserLogByLogId(UserLog bean, Integer logId) {
		return this.userLogMapper.updateByLogId(bean, logId);
	}

	/**
	 * 根据LogId删除
	 */
	public Integer deleteUserLogByLogId(Integer logId) {
		return this.userLogMapper.deleteByLogId(logId);
	}
}