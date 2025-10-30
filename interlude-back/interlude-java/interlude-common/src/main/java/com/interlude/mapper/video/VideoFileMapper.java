package com.interlude.mapper.video;

import com.interlude.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description:视频文件表 Mapper
 * @auther:dazhi
 * @date:2025/10/30
 */
public interface VideoFileMapper<T, P> extends BaseMapper {
	/**
	 * 根据FileId查询
	 */
	T selectByFileId(@Param("fileId") Long fileId);

	/**
	 * 根据FileId更新
	 */
	Integer updateByFileId(@Param("bean") T t, @Param("fileId") Long fileId);

	/**
	 * 根据FileId删除
	 */
	Integer deleteByFileId(@Param("fileId") Long fileId);

	/**
	 * 根据UploadIdAndUserId查询
	 */
	T selectByUploadIdAndUserId(@Param("uploadId") String uploadId, @Param("userId") Long userId);

	/**
	 * 根据UploadIdAndUserId更新
	 */
	Integer updateByUploadIdAndUserId(@Param("bean") T t, @Param("uploadId") String uploadId, @Param("userId") Long userId);

	/**
	 * 根据UploadIdAndUserId删除
	 */
	Integer deleteByUploadIdAndUserId(@Param("uploadId") String uploadId, @Param("userId") Long userId);


}