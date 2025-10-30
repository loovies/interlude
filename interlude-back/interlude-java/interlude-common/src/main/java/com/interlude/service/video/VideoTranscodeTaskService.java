package com.interlude.service.video;

import com.interlude.entity.vo.PaginationResultVO;
import java.util.List;
import com.interlude.entity.po.video.VideoTranscodeTask;
import com.interlude.entity.query.video.VideoTranscodeTaskQuery;
/**
 * @Description:转码任务表Service
 * @auther:dazhi
 * @date:2025/10/30
 */

public interface VideoTranscodeTaskService{

	/**
	 * 根据条件查询列表
	 */
	List<VideoTranscodeTask> findListByParam(VideoTranscodeTaskQuery param);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(VideoTranscodeTaskQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<VideoTranscodeTask> findListByPage(VideoTranscodeTaskQuery param);

	/**
	 * 新增
	 */
	Integer add(VideoTranscodeTask bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<VideoTranscodeTask> listBean);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<VideoTranscodeTask> listBean);

	/**
	 * 根据TaskId查询
	 */
	VideoTranscodeTask getVideoTranscodeTaskByTaskId(Long taskId);

	/**
	 * 根据TaskId更新
	 */
	Integer updateVideoTranscodeTaskByTaskId(VideoTranscodeTask bean, Long taskId);

	/**
	 * 根据TaskId删除
	 */
	Integer deleteVideoTranscodeTaskByTaskId(Long taskId);

}