package com.interlude.entity.query.video;

import com.interlude.entity.query.BaseParam;

import java.util.Date;


/**
 * @Description:视频文件表查询对象
 * @auther:dazhi
 * @date:2025/10/30
 */
public class VideoFileQuery extends BaseParam {
	/**
	 * 文件ID
	 */
	private Long fileId;

	/**
	 * 视频ID
	 */
	private Long videoId;

	/**
	 * 用户ID
	 */
	private Long userId;

	/**
	 * 文件名
	 */
	private String fileName;

	private String fileNameFuzzy;

	/**
	 * 文件大小
	 */
	private Long fileSize;

	/**
	 * 文件路径
	 */
	private String filePath;

	private String filePathFuzzy;

	/**
	 * CDN访问地址
	 */
	private String fileUrl;

	private String fileUrlFuzzy;

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

	private String formatFuzzy;

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

	private String videoCodecFuzzy;

	/**
	 * 
	 */
	private String audioCodec;

	private String audioCodecFuzzy;

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

	private String uploadIdFuzzy;

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

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getUserId() {
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

	public void setFileNameFuzzy(String fileNameFuzzy) {
		this.fileNameFuzzy = fileNameFuzzy;
	}

	public String getFileNameFuzzy() {
		return this.fileNameFuzzy;
	}

	public void setFilePathFuzzy(String filePathFuzzy) {
		this.filePathFuzzy = filePathFuzzy;
	}

	public String getFilePathFuzzy() {
		return this.filePathFuzzy;
	}

	public void setFileUrlFuzzy(String fileUrlFuzzy) {
		this.fileUrlFuzzy = fileUrlFuzzy;
	}

	public String getFileUrlFuzzy() {
		return this.fileUrlFuzzy;
	}

	public void setFormatFuzzy(String formatFuzzy) {
		this.formatFuzzy = formatFuzzy;
	}

	public String getFormatFuzzy() {
		return this.formatFuzzy;
	}

	public void setVideoCodecFuzzy(String videoCodecFuzzy) {
		this.videoCodecFuzzy = videoCodecFuzzy;
	}

	public String getVideoCodecFuzzy() {
		return this.videoCodecFuzzy;
	}

	public void setAudioCodecFuzzy(String audioCodecFuzzy) {
		this.audioCodecFuzzy = audioCodecFuzzy;
	}

	public String getAudioCodecFuzzy() {
		return this.audioCodecFuzzy;
	}

	public void setUploadIdFuzzy(String uploadIdFuzzy) {
		this.uploadIdFuzzy = uploadIdFuzzy;
	}

	public String getUploadIdFuzzy() {
		return this.uploadIdFuzzy;
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