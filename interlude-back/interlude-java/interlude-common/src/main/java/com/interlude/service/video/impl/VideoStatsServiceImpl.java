package com.interlude.service.video.impl;

import com.interlude.entity.query.SimplePage;
import com.interlude.enums.PageSize;
import com.interlude.entity.vo.PaginationResultVO;
import java.util.List;
import com.interlude.entity.po.video.VideoStats;
import com.interlude.entity.query.video.VideoStatsQuery;
import com.interlude.mapper.video.VideoStatsMapper;
import com.interlude.service.video.VideoStatsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;/**
 * @Description:视频统计表Service
 * @auther:dazhi
 * @date:2025/10/30
 */

@Service("videoStatsService")
public class VideoStatsServiceImpl implements VideoStatsService{

	@Resource
	private VideoStatsMapper<VideoStats,VideoStatsQuery> videoStatsMapper;
	/**
	 * 根据条件查询列表
	 */
	public List<VideoStats> findListByParam(VideoStatsQuery query) {
		return this.videoStatsMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(VideoStatsQuery query) {
		return this.videoStatsMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<VideoStats> findListByPage(VideoStatsQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(),count,pageSize);
		query.setSimplePage(page);
		List<VideoStats> listByParam = this.findListByParam(query);
		PaginationResultVO<VideoStats> result = new PaginationResultVO(count,page.getPageSize(),page.getPageNo(),page.getPageTotal(),listByParam);
		return result;
	}

	/**
	 * 新增
	 */
	public Integer add(VideoStats bean) {
		return this.videoStatsMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<VideoStats> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.videoStatsMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或修改
	 */
	public Integer addOrUpdateBatch(List<VideoStats> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.videoStatsMapper.insertOrUpdateBatch(listBean);
	}


	/**
	 * 根据VideoId查询
	 */
	public VideoStats getVideoStatsByVideoId(Long videoId) {
		return this.videoStatsMapper.selectByVideoId(videoId);
	}

	/**
	 * 根据VideoId更新
	 */
	public Integer updateVideoStatsByVideoId(VideoStats bean, Long videoId) {
		return this.videoStatsMapper.updateByVideoId(bean, videoId);
	}

	/**
	 * 根据VideoId删除
	 */
	public Integer deleteVideoStatsByVideoId(Long videoId) {
		return this.videoStatsMapper.deleteByVideoId(videoId);
	}

	@Override
	public Integer increasePlayCount(Long videoId) {
		return this.videoStatsMapper.increasePlayCount(videoId);
	}
}
