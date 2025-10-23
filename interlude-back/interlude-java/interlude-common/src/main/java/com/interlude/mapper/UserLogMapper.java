package com.interlude.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * @Description:用户日志表 Mapper
 * @auther:dazhi
 * @date:2025/10/14
 */
public interface UserLogMapper<T, P> extends BaseMapper {
	/**
	 * 根据LogId查询
	 */
	T selectByLogId(@Param("logId") Integer logId);

	/**
	 * 根据LogId更新
	 */
	Integer updateByLogId(@Param("bean") T t, @Param("logId") Integer logId);

	/**
	 * 根据LogId删除
	 */
	Integer deleteByLogId(@Param("logId") Integer logId);


}