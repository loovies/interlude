package com.interlude.entity.po.video;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.interlude.enums.DateTimePatterEnum;
import com.interlude.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * @Description:视频主表
 * @auther:dazhi
 * @date:2025/10/30
 */
public class VideoInfo implements Serializable {
	/**
	 * 视频ID
	 */
	private Long videoId;

	/**
	 * 用户ID
	 */
	private Long userId;

	/**
	 * 视频标题
	 */
	private String videoName;

	/**
	 * 封面图URL
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
	 * 视频类型 1:原视频, 2转载视频
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
	 * 时长(秒)
	 */
	private Integer duration;

	/**
	 * 视频宽度
	 */
	private Integer width;

	/**
	 * 视频高度
	 */
	private Integer height;

	/**
	 * 码率
	 */
	private Integer bitrate;

	/**
	 * 视频格式
	 */
	private String format;

	/**
	 * 互动设置JSON
	 */
	private String interactionSettings;

	/**
	 * 状态 1:已发布  2:已离线  3:已删除
	 */
	private Integer status;

	/**
	 * 可见性 1:公共、2: 私人、3:仅限好友
	 */
	private Integer visibility;

	/**
	 * 发布时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date publishTime;

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

	/**
	 * 
	 */
	private Integer version;

	public void setVideoId(Long videoId) {
		this.videoId = videoId;
	}

	public Long getVideoId() {
		return this.videoId;
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

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public Integer getDuration() {
		return this.duration;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getWidth() {
		return this.width;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public Integer getHeight() {
		return this.height;
	}

	public void setBitrate(Integer bitrate) {
		this.bitrate = bitrate;
	}

	public Integer getBitrate() {
		return this.bitrate;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getFormat() {
		return this.format;
	}

	public void setInteractionSettings(String interactionSettings) {
		this.interactionSettings = interactionSettings;
	}

	public String getInteractionSettings() {
		return this.interactionSettings;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setVisibility(Integer visibility) {
		this.visibility = visibility;
	}

	public Integer getVisibility() {
		return this.visibility;
	}

	public void setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
	}

	public Date getPublishTime() {
		return this.publishTime;
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

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Integer getVersion() {
		return this.version;
	}

	@Override
	public String toString() {
		return "视频ID:" + (videoId == null ? "空" : videoId) + ",用户ID:" + (userId == null ? "空" : userId) + ",视频标题:" + (videoName == null ? "空" : videoName) + ",封面图URL:" + (videoCover == null ? "空" : videoCover) + ",视频描述:" + (description == null ? "空" : description) + ",一级分类:" + (pCategoryId == null ? "空" : pCategoryId) + ",二级分类:" + (categoryId == null ? "空" : categoryId) + ",视频类型 1:原视频, 2转载视频:" + (videoType == null ? "空" : videoType) + ",原作者(转载时使用):" + (originAuthor == null ? "空" : originAuthor) + ",原视频链接:" + (originUrl == null ? "空" : originUrl) + ",标签列表JSON:" + (tags == null ? "空" : tags) + ",时长(秒):" + (duration == null ? "空" : duration) + ",视频宽度:" + (width == null ? "空" : width) + ",视频高度:" + (height == null ? "空" : height) + ",码率:" + (bitrate == null ? "空" : bitrate) + ",视频格式:" + (format == null ? "空" : format) + ",互动设置JSON:" + (interactionSettings == null ? "空" : interactionSettings) + ",状态 1:已发布  2:已离线  3:已删除:" + (status == null ? "空" : status) + ",可见性 1:公共、2: 私人、3:仅限好友:" + (visibility == null ? "空" : visibility) + ",发布时间:" + (publishTime == null ? "空" : DateUtils.format(publishTime, DateTimePatterEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ",:" + (createTime == null ? "空" : DateUtils.format(createTime, DateTimePatterEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ",:" + (updateTime == null ? "空" : DateUtils.format(updateTime, DateTimePatterEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ",:" + (version == null ? "空" : version);
	}
}