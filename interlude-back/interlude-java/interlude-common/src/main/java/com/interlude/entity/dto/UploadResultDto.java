package com.interlude.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * 文件上传返回结果类
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UploadResultDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String fileName;
    private String uploadId;    // 文件上传id
    private String filePath;
    private Integer chunkIndex; // 分片索引
    private Integer chunks; // 总分片数量
    private Long fileSize = 0L;

    // 内容相关
    private String description;  //视频描述内容
    private String videoCover;  // 视频封面
    private String originAuthor; // 原作者名称
    private String originUrl;   // 原作者地址

    // JSON格式数据
    private String tags;
    private String interactionSettings;

    //上传进度
    private Integer uploadProgress = 0;

    // 临时数据
    private String tempData;    // 临时存储的编辑数据
    private String last_edit_content;  // 最后一次编辑的未保存内容

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Integer getChunkIndex() {
        return chunkIndex;
    }

    public void setChunkIndex(Integer chunkIndex) {
        this.chunkIndex = chunkIndex;
    }

    public Integer getChunks() {
        return chunks;
    }

    public void setChunks(Integer chunks) {
        this.chunks = chunks;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoCover() {
        return videoCover;
    }

    public void setVideoCover(String videoCover) {
        this.videoCover = videoCover;
    }

    public String getOriginAuthor() {
        return originAuthor;
    }

    public void setOriginAuthor(String originAuthor) {
        this.originAuthor = originAuthor;
    }

    public String getOriginUrl() {
        return originUrl;
    }

    public void setOriginUrl(String originUrl) {
        this.originUrl = originUrl;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getInteractionSettings() {
        return interactionSettings;
    }

    public void setInteractionSettings(String interactionSettings) {
        this.interactionSettings = interactionSettings;
    }

    public Integer getUploadProgress() {
        return uploadProgress;
    }

    public void setUploadProgress(Integer uploadProgress) {
        this.uploadProgress = uploadProgress;
    }

    public String getTempData() {
        return tempData;
    }

    public void setTempData(String tempData) {
        this.tempData = tempData;
    }

    public String getLast_edit_content() {
        return last_edit_content;
    }

    public void setLast_edit_content(String last_edit_content) {
        this.last_edit_content = last_edit_content;
    }
}
