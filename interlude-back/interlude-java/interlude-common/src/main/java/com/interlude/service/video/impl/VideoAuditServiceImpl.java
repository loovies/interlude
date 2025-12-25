package com.interlude.service.video.impl;

import com.interlude.entity.po.video.VideoInfo;
import com.interlude.entity.query.SimplePage;
import com.interlude.entity.query.video.VideoInfoQuery;
import com.interlude.enums.PageSize;
import com.interlude.entity.vo.PaginationResultVO;
import java.util.List;
import com.interlude.entity.po.video.VideoAudit;
import com.interlude.entity.query.video.VideoAuditQuery;
import com.interlude.mapper.video.VideoAuditMapper;
import com.interlude.mapper.video.VideoInfoMapper;
import com.interlude.service.video.VideoAuditService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;/**
 * @Description:视频审核表Service
 * @auther:dazhi
 * @date:2025/10/30
 */

@Service("videoAuditService")
public class VideoAuditServiceImpl implements VideoAuditService{

	@Resource
	private VideoAuditMapper<VideoAudit,VideoAuditQuery> videoAuditMapper;

	@Resource
	private VideoInfoMapper<VideoInfo, VideoInfoQuery> videoInfoMapper;
	/**
	 * 根据条件查询列表
	 */
	public List<VideoAudit> findListByParam(VideoAuditQuery query) {
		return this.videoAuditMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(VideoAuditQuery query) {
		return this.videoAuditMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<VideoAudit> findListByPage(VideoAuditQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(),count,pageSize);
		query.setSimplePage(page);
		List<VideoAudit> listByParam = this.findListByParam(query);
		PaginationResultVO<VideoAudit> result = new PaginationResultVO(count,page.getPageSize(),page.getPageNo(),page.getPageTotal(),listByParam);
		return result;
	}

	/**
	 * 新增
	 */
	public Integer add(VideoAudit bean) {
		return this.videoAuditMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<VideoAudit> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.videoAuditMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或修改
	 */
	public Integer addOrUpdateBatch(List<VideoAudit> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.videoAuditMapper.insertOrUpdateBatch(listBean);
	}


	/**
	 * 根据AuditId查询
	 */
	public VideoAudit getVideoAuditByAuditId(Long auditId) {
		return this.videoAuditMapper.selectByAuditId(auditId);
	}

	/**
	 * 根据AuditId更新
	 */
	public Integer updateVideoAuditByAuditId(VideoAudit bean, Long auditId) {
		return this.videoAuditMapper.updateByAuditId(bean, auditId);
	}

	/**
	 * 根据AuditId删除
	 */
	public Integer deleteVideoAuditByAuditId(Long auditId) {
		return this.videoAuditMapper.deleteByAuditId(auditId);
	}

	/**
	 * 根据VideoId查询
	 */
	public VideoAudit getVideoAuditByVideoId(Long videoId) {
		return this.videoAuditMapper.selectByVideoId(videoId);
	}

	/**
	 * 根据VideoId更新
	 */
	public Integer updateVideoAuditByVideoId(VideoAudit bean, Long videoId) {
		return this.videoAuditMapper.updateByVideoId(bean, videoId);
	}

	/**
	 * 根据VideoId删除
	 */
	public Integer deleteVideoAuditByVideoId(Long videoId) {
		return this.videoAuditMapper.deleteByVideoId(videoId);
	}

	@Override
	public Integer selectVideoIdByExist(Long videoId,String userId) {
		if(videoId == null || videoId <= 0) {
			return 0;
		}
		return videoInfoMapper.selectVideoIdByExist(videoId,userId);
	}
}