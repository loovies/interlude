package com.interlude.service.video;

import com.interlude.entity.vo.PaginationResultVO;
import java.util.List;
import com.interlude.entity.po.video.VideoFile;
import com.interlude.entity.query.video.VideoFileQuery;
/**
 * @Description:视频文件表Service
 * @auther:dazhi
 * @date:2025/10/30
 */

public interface VideoFileService{

	/**
	 * 根据条件查询列表
	 */
	List<VideoFile> findListByParam(VideoFileQuery param);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(VideoFileQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<VideoFile> findListByPage(VideoFileQuery param);

	/**
	 * 新增
	 */
	Integer add(VideoFile bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<VideoFile> listBean);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<VideoFile> listBean);

	/**
	 * 根据FileId查询
	 */
	VideoFile getVideoFileByFileId(Long fileId);

	/**
	 * 根据FileId更新
	 */
	Integer updateVideoFileByFileId(VideoFile bean, Long fileId);

	/**
	 * 根据FileId删除
	 */
	Integer deleteVideoFileByFileId(Long fileId);

	/**
	 * 根据UploadIdAndUserId查询
	 */
	VideoFile getVideoFileByUploadIdAndUserId(String uploadId, Long userId);

	/**
	 * 根据UploadIdAndUserId更新
	 */
	Integer updateVideoFileByUploadIdAndUserId(VideoFile bean, String uploadId, Long userId);

	/**
	 * 根据UploadIdAndUserId删除
	 */
	Integer deleteVideoFileByUploadIdAndUserId(String uploadId, Long userId);

}