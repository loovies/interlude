package com.interlude.service.video.impl;

import com.interlude.entity.query.SimplePage;
import com.interlude.enums.PageSize;
import com.interlude.entity.vo.PaginationResultVO;
import java.util.List;
import com.interlude.entity.po.video.VideoRecommend;
import com.interlude.entity.query.video.VideoRecommendQuery;
import com.interlude.mapper.video.VideoRecommendMapper;
import com.interlude.service.video.VideoRecommendService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;/**
 * @Description:视频推荐表Service
 * @auther:dazhi
 * @date:2025/10/30
 */

@Service("videoRecommendService")
public class VideoRecommendServiceImpl implements VideoRecommendService{

	@Resource
	private VideoRecommendMapper<VideoRecommend,VideoRecommendQuery> videoRecommendMapper;
	/**
	 * 根据条件查询列表
	 */
	public List<VideoRecommend> findListByParam(VideoRecommendQuery query) {
		return this.videoRecommendMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(VideoRecommendQuery query) {
		return this.videoRecommendMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<VideoRecommend> findListByPage(VideoRecommendQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(),count,pageSize);
		query.setSimplePage(page);
		List<VideoRecommend> listByParam = this.findListByParam(query);
		PaginationResultVO<VideoRecommend> result = new PaginationResultVO(count,page.getPageSize(),page.getPageNo(),page.getPageTotal(),listByParam);
		return result;
	}

	/**
	 * 新增
	 */
	public Integer add(VideoRecommend bean) {
		return this.videoRecommendMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<VideoRecommend> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.videoRecommendMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或修改
	 */
	public Integer addOrUpdateBatch(List<VideoRecommend> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.videoRecommendMapper.insertOrUpdateBatch(listBean);
	}


	/**
	 * 根据Id查询
	 */
	public VideoRecommend getVideoRecommendById(Long id) {
		return this.videoRecommendMapper.selectById(id);
	}

	/**
	 * 根据Id更新
	 */
	public Integer updateVideoRecommendById(VideoRecommend bean, Long id) {
		return this.videoRecommendMapper.updateById(bean, id);
	}

	/**
	 * 根据Id删除
	 */
	public Integer deleteVideoRecommendById(Long id) {
		return this.videoRecommendMapper.deleteById(id);
	}

	/**
	 * 根据VideoIdAndRecommendType查询
	 */
	public VideoRecommend getVideoRecommendByVideoIdAndRecommendType(Long videoId, Integer recommendType) {
		return this.videoRecommendMapper.selectByVideoIdAndRecommendType(videoId, recommendType);
	}

	/**
	 * 根据VideoIdAndRecommendType更新
	 */
	public Integer updateVideoRecommendByVideoIdAndRecommendType(VideoRecommend bean, Long videoId, Integer recommendType) {
		return this.videoRecommendMapper.updateByVideoIdAndRecommendType(bean, videoId, recommendType);
	}

	/**
	 * 根据VideoIdAndRecommendType删除
	 */
	public Integer deleteVideoRecommendByVideoIdAndRecommendType(Long videoId, Integer recommendType) {
		return this.videoRecommendMapper.deleteByVideoIdAndRecommendType(videoId, recommendType);
	}
}