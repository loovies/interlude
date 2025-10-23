package com.interlude.service;

import com.interlude.entity.vo.PaginationResultVO;
import java.util.List;
import com.interlude.entity.po.UserLog;
import com.interlude.entity.query.UserLogQuery;
/**
 * @Description:用户日志表Service
 * @auther:dazhi
 * @date:2025/10/14
 */

public interface UserLogService{

	/**
	 * 根据条件查询列表
	 */
	List<UserLog> findListByParam(UserLogQuery param);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(UserLogQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<UserLog> findListByPage(UserLogQuery param);

	/**
	 * 新增
	 */
	Integer add(UserLog bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<UserLog> listBean);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<UserLog> listBean);

	/**
	 * 根据LogId查询
	 */
	UserLog getUserLogByLogId(Integer logId);

	/**
	 * 根据LogId更新
	 */
	Integer updateUserLogByLogId(UserLog bean, Integer logId);

	/**
	 * 根据LogId删除
	 */
	Integer deleteUserLogByLogId(Integer logId);

}