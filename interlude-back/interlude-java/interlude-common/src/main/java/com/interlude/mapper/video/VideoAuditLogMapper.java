package com.interlude.mapper.video;

import com.interlude.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description:审核日志表 Mapper
 * @auther:dazhi
 * @date:2025/10/30
 */
public interface VideoAuditLogMapper<T, P> extends BaseMapper {
	/**
	 * 根据LogId查询
	 */
	T selectByLogId(@Param("logId") Long logId);

	/**
	 * 根据LogId更新
	 */
	Integer updateByLogId(@Param("bean") T t, @Param("logId") Long logId);

	/**
	 * 根据LogId删除
	 */
	Integer deleteByLogId(@Param("logId") Long logId);


}