package com.interlude.service.video;

import com.interlude.entity.vo.PaginationResultVO;
import java.util.List;
import com.interlude.entity.po.video.VideoAudit;
import com.interlude.entity.query.video.VideoAuditQuery;
/**
 * @Description:视频审核表Service
 * @auther:dazhi
 * @date:2025/10/30
 */

public interface VideoAuditService{

	/**
	 * 根据条件查询列表
	 */
	List<VideoAudit> findListByParam(VideoAuditQuery param);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(VideoAuditQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<VideoAudit> findListByPage(VideoAuditQuery param);

	/**
	 * 新增
	 */
	Integer add(VideoAudit bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<VideoAudit> listBean);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<VideoAudit> listBean);

	/**
	 * 根据AuditId查询
	 */
	VideoAudit getVideoAuditByAuditId(Long auditId);

	/**
	 * 根据AuditId更新
	 */
	Integer updateVideoAuditByAuditId(VideoAudit bean, Long auditId);

	/**
	 * 根据AuditId删除
	 */
	Integer deleteVideoAuditByAuditId(Long auditId);

	/**
	 * 根据VideoId查询
	 */
	VideoAudit getVideoAuditByVideoId(Long videoId);

	/**
	 * 根据VideoId更新
	 */
	Integer updateVideoAuditByVideoId(VideoAudit bean, Long videoId);

	/**
	 * 根据VideoId删除
	 */
	Integer deleteVideoAuditByVideoId(Long videoId);

	Integer selectVideoIdByExist(Long videoId, String userId);
}