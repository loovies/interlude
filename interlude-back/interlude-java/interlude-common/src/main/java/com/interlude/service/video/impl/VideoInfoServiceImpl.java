package com.interlude.service.video.impl;

import com.interlude.entity.query.SimplePage;
import com.interlude.enums.PageSize;
import com.interlude.entity.vo.PaginationResultVO;
import java.util.List;
import com.interlude.entity.po.video.VideoInfo;
import com.interlude.entity.query.video.VideoInfoQuery;
import com.interlude.mapper.video.VideoInfoMapper;
import com.interlude.service.video.VideoInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;/**
 * @Description:视频主表Service
 * @auther:dazhi
 * @date:2025/10/30
 */

@Service("videoInfoService")
public class VideoInfoServiceImpl implements VideoInfoService{

	@Resource
	private VideoInfoMapper<VideoInfo,VideoInfoQuery> videoInfoMapper;
	/**
	 * 根据条件查询列表
	 */
	public List<VideoInfo> findListByParam(VideoInfoQuery query) {
		return this.videoInfoMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(VideoInfoQuery query) {
		return this.videoInfoMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<VideoInfo> findListByPage(VideoInfoQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(),count,pageSize);
		query.setSimplePage(page);
		List<VideoInfo> listByParam = this.findListByParam(query);
		PaginationResultVO<VideoInfo> result = new PaginationResultVO(count,page.getPageSize(),page.getPageNo(),page.getPageTotal(),listByParam);
		return result;
	}

	/**
	 * 新增
	 */
	public Integer add(VideoInfo bean) {
		return this.videoInfoMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<VideoInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.videoInfoMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或修改
	 */
	public Integer addOrUpdateBatch(List<VideoInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.videoInfoMapper.insertOrUpdateBatch(listBean);
	}


	/**
	 * 根据VideoId查询
	 */
	public VideoInfo getVideoInfoByVideoId(Long videoId) {
		return this.videoInfoMapper.selectByVideoId(videoId);
	}

	/**
	 * 根据VideoId更新
	 */
	public Integer updateVideoInfoByVideoId(VideoInfo bean, Long videoId) {
		return this.videoInfoMapper.updateByVideoId(bean, videoId);
	}

	/**
	 * 根据VideoId删除
	 */
	public Integer deleteVideoInfoByVideoId(Long videoId) {
		return this.videoInfoMapper.deleteByVideoId(videoId);
	}

	@Override
	public void saveVideoInfo(VideoInfo videoInfo) {

	}
}