package com.interlude.mapper.video;

import com.interlude.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description:转码任务表 Mapper
 * @auther:dazhi
 * @date:2025/10/30
 */
public interface VideoTranscodeTaskMapper<T, P> extends BaseMapper {
	/**
	 * 根据TaskId查询
	 */
	T selectByTaskId(@Param("taskId") Long taskId);

	/**
	 * 根据TaskId更新
	 */
	Integer updateByTaskId(@Param("bean") T t, @Param("taskId") Long taskId);

	/**
	 * 根据TaskId删除
	 */
	Integer deleteByTaskId(@Param("taskId") Long taskId);


    Integer updateByVideoId2UserId2FileId(@Param("bean") T t,@Param("videoId") Long videoId, @Param("userId") String userId, @Param("fileId") Long fileId);
}