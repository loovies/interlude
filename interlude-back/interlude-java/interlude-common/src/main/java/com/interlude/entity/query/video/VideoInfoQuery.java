package com.interlude.entity.query.video;

import com.interlude.entity.query.BaseParam;

import java.util.Date;


/**
 * @Description:视频主表查询对象
 * @auther:dazhi
 * @date:2025/10/30
 */
public class VideoInfoQuery extends BaseParam {
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

	private String videoNameFuzzy;

	/**
	 * 封面图URL
	 */
	private String videoCover;

	private String videoCoverFuzzy;

	/**
	 * 视频描述
	 */
	private String description;

	private String descriptionFuzzy;

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

	private String originAuthorFuzzy;

	/**
	 * 原视频链接
	 */
	private String originUrl;

	private String originUrlFuzzy;

	/**
	 * 标签列表JSON
	 */
	private String tags;

	private String tagsFuzzy;

	/**
	 * 互动设置JSON
	 */
	private String interactionSettings;

	private String interactionSettingsFuzzy;

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
	private Date publishTime;

	private String publishTimeStart;

	private String publishTimeEnd;

	/**
	 * 
	 */
	private Date createTime;

	private String createTimeStart;

	private String createTimeEnd;

	/**
	 * 
	 */
	private Date updateTime;

	private String updateTimeStart;

	private String updateTimeEnd;

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

	public void setVideoNameFuzzy(String videoNameFuzzy) {
		this.videoNameFuzzy = videoNameFuzzy;
	}

	public String getVideoNameFuzzy() {
		return this.videoNameFuzzy;
	}

	public void setVideoCoverFuzzy(String videoCoverFuzzy) {
		this.videoCoverFuzzy = videoCoverFuzzy;
	}

	public String getVideoCoverFuzzy() {
		return this.videoCoverFuzzy;
	}

	public void setDescriptionFuzzy(String descriptionFuzzy) {
		this.descriptionFuzzy = descriptionFuzzy;
	}

	public String getDescriptionFuzzy() {
		return this.descriptionFuzzy;
	}

	public void setOriginAuthorFuzzy(String originAuthorFuzzy) {
		this.originAuthorFuzzy = originAuthorFuzzy;
	}

	public String getOriginAuthorFuzzy() {
		return this.originAuthorFuzzy;
	}

	public void setOriginUrlFuzzy(String originUrlFuzzy) {
		this.originUrlFuzzy = originUrlFuzzy;
	}

	public String getOriginUrlFuzzy() {
		return this.originUrlFuzzy;
	}

	public void setTagsFuzzy(String tagsFuzzy) {
		this.tagsFuzzy = tagsFuzzy;
	}

	public String getTagsFuzzy() {
		return this.tagsFuzzy;
	}


	public void setInteractionSettingsFuzzy(String interactionSettingsFuzzy) {
		this.interactionSettingsFuzzy = interactionSettingsFuzzy;
	}

	public String getInteractionSettingsFuzzy() {
		return this.interactionSettingsFuzzy;
	}

	public void setPublishTimeStart(String publishTimeStart) {
		this.publishTimeStart = publishTimeStart;
	}

	public String getPublishTimeStart() {
		return this.publishTimeStart;
	}

	public void setPublishTimeEnd(String publishTimeEnd) {
		this.publishTimeEnd = publishTimeEnd;
	}

	public String getPublishTimeEnd() {
		return this.publishTimeEnd;
	}

	public void setCreateTimeStart(String createTimeStart) {
		this.createTimeStart = createTimeStart;
	}

	public String getCreateTimeStart() {
		return this.createTimeStart;
	}

	public void setCreateTimeEnd(String createTimeEnd) {
		this.createTimeEnd = createTimeEnd;
	}

	public String getCreateTimeEnd() {
		return this.createTimeEnd;
	}

	public void setUpdateTimeStart(String updateTimeStart) {
		this.updateTimeStart = updateTimeStart;
	}

	public String getUpdateTimeStart() {
		return this.updateTimeStart;
	}

	public void setUpdateTimeEnd(String updateTimeEnd) {
		this.updateTimeEnd = updateTimeEnd;
	}

	public String getUpdateTimeEnd() {
		return this.updateTimeEnd;
	}

}