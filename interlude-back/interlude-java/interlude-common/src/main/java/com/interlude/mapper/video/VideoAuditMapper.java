package com.interlude.mapper.video;

import com.interlude.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description:视频审核表 Mapper
 * @auther:dazhi
 * @date:2025/10/30
 */
public interface VideoAuditMapper<T, P> extends BaseMapper {
	/**
	 * 根据AuditId查询
	 */
	T selectByAuditId(@Param("auditId") Long auditId);

	/**
	 * 根据AuditId更新
	 */
	Integer updateByAuditId(@Param("bean") T t, @Param("auditId") Long auditId);

	/**
	 * 根据AuditId删除
	 */
	Integer deleteByAuditId(@Param("auditId") Long auditId);

	/**
	 * 根据VideoId查询
	 */
	T selectByVideoId(@Param("videoId") Long videoId);

	/**
	 * 根据VideoId更新
	 */
	Integer updateByVideoId(@Param("bean") T t, @Param("videoId") Long videoId);

	/**
	 * 根据VideoId删除
	 */
	Integer deleteByVideoId(@Param("videoId") Long videoId);


}