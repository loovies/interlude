package com.interlude.service.video;

import com.interlude.entity.vo.PaginationResultVO;
import java.util.List;
import com.interlude.entity.po.video.VideoAuditLog;
import com.interlude.entity.query.video.VideoAuditLogQuery;
/**
 * @Description:审核日志表Service
 * @auther:dazhi
 * @date:2025/10/30
 */

public interface VideoAuditLogService{

	/**
	 * 根据条件查询列表
	 */
	List<VideoAuditLog> findListByParam(VideoAuditLogQuery param);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(VideoAuditLogQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<VideoAuditLog> findListByPage(VideoAuditLogQuery param);

	/**
	 * 新增
	 */
	Integer add(VideoAuditLog bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<VideoAuditLog> listBean);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<VideoAuditLog> listBean);

	/**
	 * 根据LogId查询
	 */
	VideoAuditLog getVideoAuditLogByLogId(Long logId);

	/**
	 * 根据LogId更新
	 */
	Integer updateVideoAuditLogByLogId(VideoAuditLog bean, Long logId);

	/**
	 * 根据LogId删除
	 */
	Integer deleteVideoAuditLogByLogId(Long logId);

	VideoAuditLog getVideoIdByNewLogInfo(Long videoId);

}