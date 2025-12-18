package com.interlude.service.video.impl;

import com.interlude.entity.query.SimplePage;
import com.interlude.enums.PageSize;
import com.interlude.entity.vo.PaginationResultVO;
import java.util.List;
import com.interlude.entity.po.video.VideoFile;
import com.interlude.entity.query.video.VideoFileQuery;
import com.interlude.mapper.video.VideoFileMapper;
import com.interlude.service.video.VideoFileService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;/**
 * @Description:视频文件表Service
 * @auther:dazhi
 * @date:2025/10/30
 */

@Service("videoFileService")
public class VideoFileServiceImpl implements VideoFileService{

	@Resource
	private VideoFileMapper<VideoFile,VideoFileQuery> videoFileMapper;
	/**
	 * 根据条件查询列表
	 */
	public List<VideoFile> findListByParam(VideoFileQuery query) {
		return this.videoFileMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(VideoFileQuery query) {
		return this.videoFileMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<VideoFile> findListByPage(VideoFileQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(),count,pageSize);
		query.setSimplePage(page);
		List<VideoFile> listByParam = this.findListByParam(query);
		PaginationResultVO<VideoFile> result = new PaginationResultVO(count,page.getPageSize(),page.getPageNo(),page.getPageTotal(),listByParam);
		return result;
	}

	/**
	 * 新增
	 */
	public Integer add(VideoFile bean) {
		return this.videoFileMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<VideoFile> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.videoFileMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或修改
	 */
	public Integer addOrUpdateBatch(List<VideoFile> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.videoFileMapper.insertOrUpdateBatch(listBean);
	}


	/**
	 * 根据FileId查询
	 */
	public VideoFile getVideoFileByFileId(Long fileId) {
		return this.videoFileMapper.selectByFileId(fileId);
	}

	/**
	 * 根据FileId更新
	 */
	public Integer updateVideoFileByFileId(VideoFile bean, Long fileId) {
		return this.videoFileMapper.updateByFileId(bean, fileId);
	}

	/**
	 * 根据FileId删除
	 */
	public Integer deleteVideoFileByFileId(Long fileId) {
		return this.videoFileMapper.deleteByFileId(fileId);
	}

	/**
	 * 根据UploadIdAndUserId查询
	 */
	public List<VideoFile> getVideoFileByUploadIdAndUserId(String uploadId, String userId) {
		return this.videoFileMapper.selectByUploadIdAndUserId(uploadId, userId);
	}

	/**
	 * 根据UploadIdAndUserId更新
	 */
	public Integer updateVideoFileByUploadIdAndUserId(VideoFile bean, String uploadId, String userId) {
		return this.videoFileMapper.updateByUploadIdAndUserId(bean, uploadId, userId);
	}

	/**
	 * 根据UploadIdAndUserId删除
	 */
	public Integer deleteVideoFileByUploadIdAndUserId(String uploadId, String userId) {
		return this.videoFileMapper.deleteByUploadIdAndUserId(uploadId, userId);
	}
}