package com.interlude.service.video;

import com.interlude.entity.dto.UploadResultDto;
import com.interlude.entity.po.video.VideoFile;
import com.interlude.entity.vo.PaginationResultVO;
import java.util.List;
import com.interlude.entity.po.video.VideoInfo;
import com.interlude.entity.query.video.VideoInfoQuery;
/**
 * @Description:视频主表Service
 * @auther:dazhi
 * @date:2025/10/30
 */

public interface VideoInfoService{

	/**
	 * 根据条件查询列表
	 */
	List<VideoInfo> findListByParam(VideoInfoQuery param);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(VideoInfoQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<VideoInfo> findListByPage(VideoInfoQuery param);

	/**
	 * 新增
	 */
	Integer add(VideoInfo bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<VideoInfo> listBean);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<VideoInfo> listBean);

	/**
	 * 根据VideoId查询
	 */
	VideoInfo getVideoInfoByVideoId(Long videoId);

	/**
	 * 根据VideoId更新
	 */
	Integer updateVideoInfoByVideoId(VideoInfo bean, Long videoId);

	/**
	 * 根据VideoId删除
	 */
	Integer deleteVideoInfoByVideoId(Long videoId);

    void saveVideoInfo(VideoInfo videoInfo,String uploadId);

	void transferVideoFile(UploadResultDto resultDto, String userId);
}