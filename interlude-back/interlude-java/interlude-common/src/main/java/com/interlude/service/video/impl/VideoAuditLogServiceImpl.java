package com.interlude.service.video.impl;

import com.interlude.entity.query.SimplePage;
import com.interlude.enums.PageSize;
import com.interlude.entity.vo.PaginationResultVO;
import java.util.List;
import com.interlude.entity.po.video.VideoAuditLog;
import com.interlude.entity.query.video.VideoAuditLogQuery;
import com.interlude.mapper.video.VideoAuditLogMapper;
import com.interlude.service.video.VideoAuditLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;/**
 * @Description:审核日志表Service
 * @auther:dazhi
 * @date:2025/10/30
 */

@Service("videoAuditLogService")
public class VideoAuditLogServiceImpl implements VideoAuditLogService{

	@Resource
	private VideoAuditLogMapper<VideoAuditLog,VideoAuditLogQuery> videoAuditLogMapper;
	/**
	 * 根据条件查询列表
	 */
	public List<VideoAuditLog> findListByParam(VideoAuditLogQuery query) {
		return this.videoAuditLogMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(VideoAuditLogQuery query) {
		return this.videoAuditLogMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<VideoAuditLog> findListByPage(VideoAuditLogQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(),count,pageSize);
		query.setSimplePage(page);
		List<VideoAuditLog> listByParam = this.findListByParam(query);
		PaginationResultVO<VideoAuditLog> result = new PaginationResultVO(count,page.getPageSize(),page.getPageNo(),page.getPageTotal(),listByParam);
		return result;
	}

	/**
	 * 新增
	 */
	public Integer add(VideoAuditLog bean) {
		return this.videoAuditLogMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<VideoAuditLog> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.videoAuditLogMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或修改
	 */
	public Integer addOrUpdateBatch(List<VideoAuditLog> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.videoAuditLogMapper.insertOrUpdateBatch(listBean);
	}


	/**
	 * 根据LogId查询
	 */
	public VideoAuditLog getVideoAuditLogByLogId(Long logId) {
		return this.videoAuditLogMapper.selectByLogId(logId);
	}

	/**
	 * 根据LogId更新
	 */
	public Integer updateVideoAuditLogByLogId(VideoAuditLog bean, Long logId) {
		return this.videoAuditLogMapper.updateByLogId(bean, logId);
	}

	/**
	 * 根据LogId删除
	 */
	public Integer deleteVideoAuditLogByLogId(Long logId) {
		return this.videoAuditLogMapper.deleteByLogId(logId);
	}
}