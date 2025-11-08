package com.interlude.entity.po.video;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.interlude.enums.DateTimePatterEnum;
import com.interlude.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * @Description:视频文件表
 * @auther:dazhi
 * @date:2025/11/08
 */
public class VideoFile implements Serializable {
	/**
	 * 文件ID
	 */
	private Long fileId;

	/**
	 * 视频ID
	 */
	private Long videoId;

	/**
	 * 草稿ID

	 */
	private Long draftId;

	/**
	 * 用户ID
	 */
	private String userId;

	/**
	 * 文件名
	 */
	private String fileName;

	/**
	 * 文件大小
	 */
	private Long fileSize;

	/**
	 * 文件路径
	 */
	private String filePath;

	/**
	 * CDN访问地址
	 */
	private String fileUrl;

	/**
	 * 持续时长
	 */
	private Integer duration;

	/**
	 * 清晰度 1:LD 2:SD 3:HD 4:FHD 5:2K 6:4K
	 */
	private Integer quality;

	/**
	 * 
	 */
	private String format;

	/**
	 * 
	 */
	private Integer width;

	/**
	 * 
	 */
	private Integer height;

	/**
	 * 码率(bps)
	 */
	private Integer bitrate;

	/**
	 * 
	 */
	private String videoCodec;

	/**
	 * 
	 */
	private String audioCodec;

	/**
	 * “1:上传”、“2:已上传”、“3:转码”、“4:准备就绪”、“5:失败”、“6:已删除”
	 */
	private Integer fileStatus;

	/**
	 * 是否主文件
	 */
	private Integer isPrimary;

	/**
	 * 上传会话ID
	 */
	private String uploadId;

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

	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}

	public Long getFileId() {
		return this.fileId;
	}

	public void setVideoId(Long videoId) {
		this.videoId = videoId;
	}

	public Long getVideoId() {
		return this.videoId;
	}

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

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	public Long getFileSize() {
		return this.fileSize;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFilePath() {
		return this.filePath;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public String getFileUrl() {
		return this.fileUrl;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public Integer getDuration() {
		return this.duration;
	}

	public void setQuality(Integer quality) {
		this.quality = quality;
	}

	public Integer getQuality() {
		return this.quality;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getFormat() {
		return this.format;
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

	public void setVideoCodec(String videoCodec) {
		this.videoCodec = videoCodec;
	}

	public String getVideoCodec() {
		return this.videoCodec;
	}

	public void setAudioCodec(String audioCodec) {
		this.audioCodec = audioCodec;
	}

	public String getAudioCodec() {
		return this.audioCodec;
	}

	public void setFileStatus(Integer fileStatus) {
		this.fileStatus = fileStatus;
	}

	public Integer getFileStatus() {
		return this.fileStatus;
	}

	public void setIsPrimary(Integer isPrimary) {
		this.isPrimary = isPrimary;
	}

	public Integer getIsPrimary() {
		return this.isPrimary;
	}

	public void setUploadId(String uploadId) {
		this.uploadId = uploadId;
	}

	public String getUploadId() {
		return this.uploadId;
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

}