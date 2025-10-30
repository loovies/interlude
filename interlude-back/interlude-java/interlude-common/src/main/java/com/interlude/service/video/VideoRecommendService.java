package com.interlude.service.video;

import com.interlude.entity.vo.PaginationResultVO;
import java.util.List;
import com.interlude.entity.po.video.VideoRecommend;
import com.interlude.entity.query.video.VideoRecommendQuery;
/**
 * @Description:视频推荐表Service
 * @auther:dazhi
 * @date:2025/10/30
 */

public interface VideoRecommendService{

	/**
	 * 根据条件查询列表
	 */
	List<VideoRecommend> findListByParam(VideoRecommendQuery param);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(VideoRecommendQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<VideoRecommend> findListByPage(VideoRecommendQuery param);

	/**
	 * 新增
	 */
	Integer add(VideoRecommend bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<VideoRecommend> listBean);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<VideoRecommend> listBean);

	/**
	 * 根据Id查询
	 */
	VideoRecommend getVideoRecommendById(Long id);

	/**
	 * 根据Id更新
	 */
	Integer updateVideoRecommendById(VideoRecommend bean, Long id);

	/**
	 * 根据Id删除
	 */
	Integer deleteVideoRecommendById(Long id);

	/**
	 * 根据VideoIdAndRecommendType查询
	 */
	VideoRecommend getVideoRecommendByVideoIdAndRecommendType(Long videoId, Integer recommendType);

	/**
	 * 根据VideoIdAndRecommendType更新
	 */
	Integer updateVideoRecommendByVideoIdAndRecommendType(VideoRecommend bean, Long videoId, Integer recommendType);

	/**
	 * 根据VideoIdAndRecommendType删除
	 */
	Integer deleteVideoRecommendByVideoIdAndRecommendType(Long videoId, Integer recommendType);

}