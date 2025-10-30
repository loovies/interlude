package com.interlude.service.video;

import com.interlude.entity.vo.PaginationResultVO;
import java.util.List;
import com.interlude.entity.po.video.VideoStats;
import com.interlude.entity.query.video.VideoStatsQuery;
/**
 * @Description:视频统计表Service
 * @auther:dazhi
 * @date:2025/10/30
 */

public interface VideoStatsService{

	/**
	 * 根据条件查询列表
	 */
	List<VideoStats> findListByParam(VideoStatsQuery param);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(VideoStatsQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<VideoStats> findListByPage(VideoStatsQuery param);

	/**
	 * 新增
	 */
	Integer add(VideoStats bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<VideoStats> listBean);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<VideoStats> listBean);

	/**
	 * 根据VideoId查询
	 */
	VideoStats getVideoStatsByVideoId(Long videoId);

	/**
	 * 根据VideoId更新
	 */
	Integer updateVideoStatsByVideoId(VideoStats bean, Long videoId);

	/**
	 * 根据VideoId删除
	 */
	Integer deleteVideoStatsByVideoId(Long videoId);

}