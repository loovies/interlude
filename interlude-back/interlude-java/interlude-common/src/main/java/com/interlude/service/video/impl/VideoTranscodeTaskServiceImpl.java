package com.interlude.service.video.impl;

import com.interlude.entity.query.SimplePage;
import com.interlude.enums.PageSize;
import com.interlude.entity.vo.PaginationResultVO;
import java.util.List;
import com.interlude.entity.po.video.VideoTranscodeTask;
import com.interlude.entity.query.video.VideoTranscodeTaskQuery;
import com.interlude.mapper.video.VideoTranscodeTaskMapper;
import com.interlude.service.video.VideoTranscodeTaskService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;/**
 * @Description:转码任务表Service
 * @auther:dazhi
 * @date:2025/10/30
 */

@Service("videoTranscodeTaskService")
public class VideoTranscodeTaskServiceImpl implements VideoTranscodeTaskService{

	@Resource
	private VideoTranscodeTaskMapper<VideoTranscodeTask,VideoTranscodeTaskQuery> videoTranscodeTaskMapper;
	/**
	 * 根据条件查询列表
	 */
	public List<VideoTranscodeTask> findListByParam(VideoTranscodeTaskQuery query) {
		return this.videoTranscodeTaskMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(VideoTranscodeTaskQuery query) {
		return this.videoTranscodeTaskMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<VideoTranscodeTask> findListByPage(VideoTranscodeTaskQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(),count,pageSize);
		query.setSimplePage(page);
		List<VideoTranscodeTask> listByParam = this.findListByParam(query);
		PaginationResultVO<VideoTranscodeTask> result = new PaginationResultVO(count,page.getPageSize(),page.getPageNo(),page.getPageTotal(),listByParam);
		return result;
	}

	/**
	 * 新增
	 */
	public Integer add(VideoTranscodeTask bean) {
		return this.videoTranscodeTaskMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<VideoTranscodeTask> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.videoTranscodeTaskMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或修改
	 */
	public Integer addOrUpdateBatch(List<VideoTranscodeTask> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.videoTranscodeTaskMapper.insertOrUpdateBatch(listBean);
	}


	/**
	 * 根据TaskId查询
	 */
	public VideoTranscodeTask getVideoTranscodeTaskByTaskId(Long taskId) {
		return this.videoTranscodeTaskMapper.selectByTaskId(taskId);
	}

	/**
	 * 根据TaskId更新
	 */
	public Integer updateVideoTranscodeTaskByTaskId(VideoTranscodeTask bean, Long taskId) {
		return this.videoTranscodeTaskMapper.updateByTaskId(bean, taskId);
	}

	/**
	 * 根据TaskId删除
	 */
	public Integer deleteVideoTranscodeTaskByTaskId(Long taskId) {
		return this.videoTranscodeTaskMapper.deleteByTaskId(taskId);
	}
}