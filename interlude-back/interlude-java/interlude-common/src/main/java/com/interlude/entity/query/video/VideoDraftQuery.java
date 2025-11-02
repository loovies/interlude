package com.interlude.entity.query.video;

import com.interlude.entity.query.BaseParam;

import java.util.Date;


/**
 * @Description:视频草稿表查询对象
 * @auther:dazhi
 * @date:2025/11/01
 */
public class VideoDraftQuery extends BaseParam{
	/**
	 * 草稿ID
	 */
	private Long draftId;

	/**
	 * 用户Id
	 */
	private Long userId;

	/**
	 * Redis草稿key
	 */
	private String draftKey;

	private String draftKeyFuzzy;

	/**
	 * 视频名称
	 */
	private String videoName;

	private String videoNameFuzzy;

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
	private Date createTime;

	private String createTimeStart;

	private String createTimeEnd;

	/**
	 *
	 */
	private Date updateTime;

	private String updateTimeStart;

	private String updateTimeEnd;

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

	public void setDraftKeyFuzzy(String draftKeyFuzzy) {
		this.draftKeyFuzzy = draftKeyFuzzy;
	}

	public String getDraftKeyFuzzy() {
		return this.draftKeyFuzzy;
	}

	public void setVideoNameFuzzy(String videoNameFuzzy) {
		this.videoNameFuzzy = videoNameFuzzy;
	}

	public String getVideoNameFuzzy() {
		return this.videoNameFuzzy;
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