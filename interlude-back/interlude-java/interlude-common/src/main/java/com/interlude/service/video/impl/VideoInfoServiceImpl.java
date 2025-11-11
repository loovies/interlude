package com.interlude.service.video.impl;

import com.interlude.component.RedisComponent;
import com.interlude.entity.config.AppConfig;
import com.interlude.entity.constants.Constants;
import com.interlude.entity.dto.UploadResultDto;
import com.interlude.entity.po.video.VideoAudit;
import com.interlude.entity.po.video.VideoFile;
import com.interlude.entity.po.video.VideoTranscodeTask;
import com.interlude.entity.query.SimplePage;
import com.interlude.entity.query.video.VideoAuditQuery;
import com.interlude.entity.query.video.VideoFileQuery;
import com.interlude.entity.query.video.VideoTranscodeTaskQuery;
import com.interlude.enums.*;
import com.interlude.entity.vo.PaginationResultVO;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.interlude.entity.po.video.VideoInfo;
import com.interlude.entity.query.video.VideoInfoQuery;
import com.interlude.exception.BusinessException;
import com.interlude.mapper.video.VideoAuditMapper;
import com.interlude.mapper.video.VideoFileMapper;
import com.interlude.mapper.video.VideoInfoMapper;
import com.interlude.mapper.video.VideoTranscodeTaskMapper;
import com.interlude.service.video.VideoInfoService;
import com.interlude.utils.FFmpegUtils;
import com.interlude.utils.StringTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;/**
 * @Description:视频主表Service
 * @auther:dazhi
 * @date:2025/10/30
 */

@Service("videoInfoService")
@Slf4j
public class VideoInfoServiceImpl implements VideoInfoService{

	@Resource
	private VideoInfoMapper<VideoInfo,VideoInfoQuery> videoInfoMapper;

	@Resource
	private VideoAuditMapper<VideoAudit, VideoAuditQuery> videoAuditMapper;

	@Resource
	private VideoTranscodeTaskMapper<VideoTranscodeTask, VideoTranscodeTaskQuery> videoTranscodeTaskMapper;

    @Resource
    private VideoFileMapper<VideoFile, VideoFileQuery> videoFileMapper;

	@Resource
	private RedisComponent redisComponent;

	@Resource
	private AppConfig appConfig;

	@Resource
	private FFmpegUtils fFmpegUtils;

	/**
	 * 根据条件查询列表
	 */
	public List<VideoInfo> findListByParam(VideoInfoQuery query) {
		return this.videoInfoMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(VideoInfoQuery query) {
		return this.videoInfoMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<VideoInfo> findListByPage(VideoInfoQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(),count,pageSize);
		query.setSimplePage(page);
		List<VideoInfo> listByParam = this.findListByParam(query);
		PaginationResultVO<VideoInfo> result = new PaginationResultVO(count,page.getPageSize(),page.getPageNo(),page.getPageTotal(),listByParam);
		return result;
	}

	/**
	 * 新增
	 */
	public Integer add(VideoInfo bean) {
		return this.videoInfoMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<VideoInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.videoInfoMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或修改
	 */
	public Integer addOrUpdateBatch(List<VideoInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.videoInfoMapper.insertOrUpdateBatch(listBean);
	}


	/**
	 * 根据VideoId查询
	 */
	public VideoInfo getVideoInfoByVideoId(Long videoId) {
		return this.videoInfoMapper.selectByVideoId(videoId);
	}

	/**
	 * 根据VideoId更新
	 */
	public Integer updateVideoInfoByVideoId(VideoInfo bean, Long videoId) {
		return this.videoInfoMapper.updateByVideoId(bean, videoId);
	}

	/**
	 * 根据VideoId删除
	 */
	public Integer deleteVideoInfoByVideoId(Long videoId) {
		return this.videoInfoMapper.deleteByVideoId(videoId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveVideoInfo(VideoInfo videoInfo, String uploadId) {

		// 判断视频是否存在
		if(videoInfo.getVideoId() != 0){
			// 判断此视频有没有删除
			VideoInfo info = videoInfoMapper.selectByVideoId(videoInfo.getVideoId());
			if(info == null){
				throw new BusinessException(ResponseCodeEnum.CODE_600);
			}
			// TODO 判断视频文件状态 是否为转码中待审核
		}

		Date currentDate = new Date();
		VideoTranscodeTask videoTranscodeTask = new VideoTranscodeTask();
		VideoFile videoFile = new VideoFile();

		// 添加视频
		if(videoInfo.getVideoId() == 0){
			videoInfo.setVideoId(Long.parseLong(StringTools.getRandomNumber(Constants.NUMBER_15)));
			videoInfoMapper.insert(videoInfo);

			// 更新video_file的 video_id
			videoFile = videoFileMapper.selectByUploadIdAndUserId(uploadId, videoInfo.getUserId());
			if (videoFile == null){
				throw new BusinessException(ResponseCodeEnum.CODE_600);
			}
			videoFile.setVideoId(videoInfo.getVideoId());
			videoFile.setFileStatus(FileStatusEnum.TRANSCODING.getStatus());
			videoFileMapper.updateByFileId(videoFile,videoFile.getFileId());

			// 添加文件转码表
			videoTranscodeTask.setVideoId(videoInfo.getVideoId());
			videoTranscodeTask.setUserId(videoInfo.getUserId());
			videoTranscodeTask.setTargetQuality("LD");
			videoTranscodeTask.setSourceFileId(videoFile.getFileId());
			videoTranscodeTask.setTaskStatus(VideoTaskEnum.PROCESSING.getStatus());
			videoTranscodeTask.setProgress(Constants.NUMBER_ZERO);
			videoTranscodeTask.setStartTime(currentDate);
			videoTranscodeTask.setCreateTime(currentDate);
			videoTranscodeTaskMapper.insert(videoTranscodeTask);
		}else{

		}

		redisComponent.addFile2TransferQueue(videoFile);
	}

	@Override
	public void transferVideoFile(VideoFile fileTransferQueue) {
		VideoFile videoFile = new VideoFile();
		VideoTranscodeTask videoTranscodeTask = new VideoTranscodeTask();
		try {
			// 获取 从Redis队列获取任务得到 要上传的文件数据
			UploadResultDto fileInfo = redisComponent.getUploadVideoFileInfo(fileTransferQueue.getUserId(), fileTransferQueue.getUploadId());

			// 获取临时目录
			String tempFilePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER + Constants.FILE_FOLDER_TEMP + fileInfo.getFilePath();
			File tempFile = new File(tempFilePath);		// 临时目录

			// 目标目录
			String targetFilePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER + Constants.FILE_VIDEO + fileInfo.getFilePath();
			File targetFile = new File(targetFilePath);
			if(targetFile.exists()){
				targetFile.mkdirs();
			}

			// 将临时目录的视频文件复制到目标文件
			FileUtils.copyDirectory(tempFile,targetFile);

			// 删除临时目录
			FileUtils.forceDelete(tempFile);

			// 删除redis中的上传数据
			redisComponent.delVideoFileInfo(fileTransferQueue.getUserId(),fileTransferQueue.getUploadId());

			String fileName = File.separator + fileInfo.getFileName()+"@"+fileInfo.getUploadId() + Constants.MP4_SUFFIX;
			//合并文件
			String completeVideo = targetFilePath + fileName;
			this.union(targetFilePath,completeVideo,fileName,true);

			// 获取播放时长
			Integer duration = fFmpegUtils.getVideoInfoDuration(completeVideo);
			videoFile.setDuration(duration);
			videoFile.setFileSize(new File(completeVideo).length());
			videoFile.setFilePath(Constants.FILE_VIDEO + fileInfo.getFilePath());
			videoFile.setFileName(fileName);
			videoFile.setFileStatus(FileStatusEnum.READY.getStatus());

			videoTranscodeTask.setTaskStatus(VideoTaskEnum.SUCCESS.getStatus());
		}catch (Exception e){
			log.error("文件转码失败",e);
			videoFile.setFileStatus(FileStatusEnum.FAILED.getStatus());
			videoTranscodeTask.setTaskStatus(VideoTaskEnum.FAILURE.getStatus());
			videoTranscodeTask.setErrorMessage(e.getMessage());
		}finally {
			// 更新数据库记录
			videoFileMapper.updateByUploadIdAndUserId(videoFile,fileTransferQueue.getUploadId(),fileTransferQueue.getUserId());

			// 更新文件转码表
			videoTranscodeTask.setEndTime(new Date());
			videoTranscodeTask.setProgress(100);
			videoTranscodeTaskMapper.updateByVideoId2UserId2FileId(videoTranscodeTask, fileTransferQueue.getVideoId(), fileTransferQueue.getUserId(), fileTransferQueue.getFileId());
		}
	}

	/**
	 *  将视频转换为多清晰度的 HLS 格式
	 *  使用枚举配置管理清晰度
	 * @param completeVideo 视频目标地址
	 */
	private void convertVideoToMultiQualityHLS(String completeVideo){
		File videoFile = new File(completeVideo);
		File outputFolder = videoFile.getParentFile();

		// 步骤1: 检测视频编码，如果是 HEVC 则先转换
		String codec = fFmpegUtils.getVideoCodec(completeVideo);
		String processingVideoPath = completeVideo;
		if (Constants.VIDEO_CODE_HEVC.equals(codec)) {
			String tempFileName = completeVideo + Constants.VIDEO_CODE_TEMP_FILE_SUFFIX;

			// 重命名原文件
			boolean renamed = new File(completeVideo).renameTo(new File(tempFileName));
			if (!renamed) {
				throw new RuntimeException("重命名原文件失败: " + completeVideo);
			}

			// 转换为 H.264
			fFmpegUtils.convertHevc2Mp4(tempFileName, completeVideo);
			// 删除临时文件
			File tempFile = new File(tempFileName);
			if (tempFile.exists()) {
				boolean deleted = tempFile.delete();
				if (!deleted) {
					System.err.println("警告: 无法删除临时文件: " + tempFileName);
				}
			}
			// 步骤2: 生成多清晰度 HLS 流
			// 可以根据需要选择特定的清晰度，这里使用所有支持的清晰度
			fFmpegUtils.convertVideoToMultiQualityHLS(outputFolder, processingVideoPath);

			// 步骤3: 生成主播放列表
			fFmpegUtils.generateMasterPlaylist(outputFolder);
		}
	}

	/**
	 * 自定义清晰度转换 - 只生成指定的清晰度
	 */
	private void convertVideoToSpecificQualities(String completeVideo, List<VideoQualityEnum> targetQualities) {
		File videoFile = new File(completeVideo);
		File outputFolder = videoFile.getParentFile();

		System.out.println("开始处理视频: " + completeVideo);
		System.out.println("目标清晰度: " + targetQualities.stream()
				.map(VideoQualityEnum::getDescription)
				.collect(Collectors.joining(", ")));

		// 编码检测和转换逻辑（同上）...

		// 只生成指定的清晰度
		fFmpegUtils.convertVideoToMultiQualityHLS(outputFolder, completeVideo, targetQualities);

		// 生成主播放列表（只包含指定的清晰度）
		fFmpegUtils.generateMasterPlaylist(outputFolder, targetQualities);

		System.out.println("指定清晰度转换完成");
	}

	// 合并文件
	// dirPath 指定分片文件所在的目录路径
	// toFilePath  指定合并后的文件输出路径
	// fileName 合并后的文件名
	// delSource 布尔值, 指示是否合并完成后删除原始分片文件
	private void union(String dirPath, String toFilePath,String fileName, Boolean delSource) {
		File dir = new File(dirPath);
		// 检查指定分片文件所在的目录路径是否存在
		if (!dir.exists()) {
			throw new BusinessException("目录不存在");
		}
		// 获取指定目录 dir 下的所有文件和子目录，并将它们存储在一个 File 类型的数组中，即 fileList。每个元素代表目录中的一个文件或子目录的路径。
		File[] fileList = dir.listFiles();
		//  初始化输出文件
		File targetFile = new File(toFilePath);
		// 创建了一个 RandomAccessFile 对象 writeFile，用于写入合并后的数据到指定的文件 toFilePath。使用 "rw" 模式允许读写操作
		try(RandomAccessFile writeFile = new RandomAccessFile(targetFile, "rw")) {

			// 临时存储从文件分片中读取的数据
			byte[] b = new byte[1024 * 10];
			for (int i = 0; i < fileList.length; i++) {
				int len = -1;
				File chunkFile = new File(dirPath + File.separator + i);
				RandomAccessFile readFile = null;
				try {
					// 对于每个分片，创建一个 RandomAccessFile 对象 readFile 以只读模式打开分片文件，并读取数据到字节数组 b，然后将数据写入到 writeFile 中。
					// readFile.read(b): 从当前打开的 readFile 中读取数据，将读取的内容存储到字节数组 b 中，并返回读取的字节数 len。如果已经到达文件末尾，则返回 -1。
					// while((len = readFile.read(b)) != -1): 这是一个循环语句，当成功读取数据且未到达文件末尾时，继续执行循环体内的操作。
					// writeFile.write(b,0,len): 将字节数组 b 中从索引 0 开始，长度为 len 的数据写入到 writeFile 中。
					// 这样实现了从分片文件逐段读取数据并写入到合并后的目标文件中的过程。
					readFile = new RandomAccessFile(chunkFile, "r");
					while ((len = readFile.read(b)) != -1) {
						writeFile.write(b, 0, len);
					}
				} catch (Exception e) {
					log.error("文件分片失败", e);
					throw new BusinessException("合并分片失败");
				} finally {
					readFile.close();
				}
			}
		} catch (Exception e) {
			log.error("合并文件:{}失败", fileName, e);
			throw new BusinessException("合并文件" + fileName + "出错了");
		} finally {
			// 根据 delSource 标志删除原始分片文件夹
			if (delSource) {
				for (int i=0;i<fileList.length;i++){
					fileList[i].delete();
				}
			}
		}
	}
}