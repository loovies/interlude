package com.interlude.entity.po.video;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.interlude.enums.DateTimePatterEnum;
import com.interlude.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * @Description:视频草稿表
 * @auther:dazhi
 * @date:2025/10/30
 */
public class VideoDraft implements Serializable {
	/**
	 * 草稿ID
	 */
	private Long draftId;

	/**
	 * 用户Id
	 */
	private Long userId;

	/**
	 * 视频名称
	 */
	private String videoName;

	/**
	 * 视频封面
	 */
	private String videoCover;

	/**
	 * 视频描述
	 */
	private String description;

	/**
	 * 一级分类
	 */
	private Integer pCategoryId;

	/**
	 * 二级分类
	 */
	private Integer categoryId;

	/**
	 * “1:原创”、“2:转发”
	 */
	private Integer videoType;

	/**
	 * 原作者(转载时使用)
	 */
	private String originAuthor;

	/**
	 * 原视频链接
	 */
	private String originUrl;

	/**
	 * 标签列表JSON
	 */
	private String tags;

	/**
	 * 交互设置
	 */
	private String interactionSettings;

	/**
	 * 关联的文件ID列表
	 */
	private String fileIds;

	/**
	 * “1:草稿”、“2:已提交”
	 */
	private Integer draftStatus;

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

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getUserId() {
		return this.userId;
	}

	public void setVideoName(String videoName) {
		this.videoName = videoName;
	}

	public String getVideoName() {
		return this.videoName;
	}

	public void setVideoCover(String videoCover) {
		this.videoCover = videoCover;
	}

	public String getVideoCover() {
		return this.videoCover;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return this.description;
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

	public void setOriginAuthor(String originAuthor) {
		this.originAuthor = originAuthor;
	}

	public String getOriginAuthor() {
		return this.originAuthor;
	}

	public void setOriginUrl(String originUrl) {
		this.originUrl = originUrl;
	}

	public String getOriginUrl() {
		return this.originUrl;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getTags() {
		return this.tags;
	}

	public void setInteractionSettings(String interactionSettings) {
		this.interactionSettings = interactionSettings;
	}

	public String getInteractionSettings() {
		return this.interactionSettings;
	}

	public void setFileIds(String fileIds) {
		this.fileIds = fileIds;
	}

	public String getFileIds() {
		return this.fileIds;
	}

	public void setDraftStatus(Integer draftStatus) {
		this.draftStatus = draftStatus;
	}

	public Integer getDraftStatus() {
		return this.draftStatus;
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
		return "草稿ID:" + (draftId == null ? "空" : draftId) + ",用户Id:" + (userId == null ? "空" : userId) + ",视频名称:" + (videoName == null ? "空" : videoName) + ",视频封面:" + (videoCover == null ? "空" : videoCover) + ",视频描述:" + (description == null ? "空" : description) + ",一级分类:" + (pCategoryId == null ? "空" : pCategoryId) + ",二级分类:" + (categoryId == null ? "空" : categoryId) + ",“1:原创”、“2:转发”:" + (videoType == null ? "空" : videoType) + ",原作者(转载时使用):" + (originAuthor == null ? "空" : originAuthor) + ",原视频链接:" + (originUrl == null ? "空" : originUrl) + ",标签列表JSON:" + (tags == null ? "空" : tags) + ",交互设置:" + (interactionSettings == null ? "空" : interactionSettings) + ",关联的文件ID列表:" + (fileIds == null ? "空" : fileIds) + ",“1:草稿”、“2:已提交”:" + (draftStatus == null ? "空" : draftStatus) + ",:" + (createTime == null ? "空" : DateUtils.format(createTime, DateTimePatterEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ",:" + (updateTime == null ? "空" : DateUtils.format(updateTime, DateTimePatterEnum.YYYY_MM_DD_HH_MM_SS.getPattern()));
	}
}