package com.interlude.entity.po.video;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.interlude.enums.DateTimePatterEnum;
import com.interlude.utils.DateUtils;


/**
 * @Description:视频草稿表
 * @auther:dazhi
 * @date:2025/11/01
 */
public class VideoDraft implements Serializable {
	/**
	 * 草稿ID
	 */
	private Long draftId;

	/**
	 * 用户Id
	 */
	private String userId;

	/**
	 * Redis草稿key
	 */
	private String draftKey;

	/**
	 * 视频名称
	 */
	private String videoName;

	/**
	 * 一级分类
	 */
	private Integer pCategoryId;

	/**
	 * 二级分类
	 */
	private Integer categoryId;

	/**
	 * 1:原创、2:转发
	 */
	private Integer videoType;

	/**
	 * 1:草稿、2:已提交
	 */
	private Integer draftStatus;

	/**
	 * 0:未开始、1:上传中、2:成功、3:失败
	 */
	private Integer uploadStatus;

	/**
	 *
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;

	/**
	 *
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;

	public void setDraftId(Long draftId) {
		this.draftId = draftId;
	}

	public Long getDraftId() {
		return this.draftId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setDraftKey(String draftKey) {
		this.draftKey = draftKey;
	}

	public String getDraftKey() {
		return this.draftKey;
	}

	public void setVideoName(String videoName) {
		this.videoName = videoName;
	}

	public String getVideoName() {
		return this.videoName;
	}

	public void setPCategoryId(Integer pCategoryId) {
		this.pCategoryId = pCategoryId;
	}

	public Integer getPCategoryId() {
		return this.pCategoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public Integer getCategoryId() {
		return this.categoryId;
	}

	public void setVideoType(Integer videoType) {
		this.videoType = videoType;
	}

	public Integer getVideoType() {
		return this.videoType;
	}

	public void setDraftStatus(Integer draftStatus) {
		this.draftStatus = draftStatus;
	}

	public Integer getDraftStatus() {
		return this.draftStatus;
	}

	public void setUploadStatus(Integer uploadStatus) {
		this.uploadStatus = uploadStatus;
	}

	public Integer getUploadStatus() {
		return this.uploadStatus;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Date getUpdateTime() {
		return this.updateTime;
	}

	@Override
	public String toString() {
		return "草稿ID:" + (draftId == null ? "空" : draftId) + ",用户Id:" + (userId == null ? "空" : userId) + ",Redis草稿key:" + (draftKey == null ? "空" : draftKey) + ",视频名称:" + (videoName == null ? "空" : videoName) + ",一级分类:" + (pCategoryId == null ? "空" : pCategoryId) + ",二级分类:" + (categoryId == null ? "空" : categoryId) + ",1:原创、2:转发:" + (videoType == null ? "空" : videoType) + ",1:草稿、2:已提交:" + (draftStatus == null ? "空" : draftStatus) + ",0:未开始、1:上传中、2:成功、3:失败:" + (uploadStatus == null ? "空" : uploadStatus) + ",:" + (createTime == null ? "空" : DateUtils.format(createTime, DateTimePatterEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ",:" + (updateTime == null ? "空" : DateUtils.format(updateTime, DateTimePatterEnum.YYYY_MM_DD_HH_MM_SS.getPattern()));
	}
}