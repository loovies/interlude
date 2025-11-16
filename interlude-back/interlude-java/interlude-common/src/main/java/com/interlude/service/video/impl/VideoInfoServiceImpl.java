package com.interlude.service.video.impl;

import com.interlude.component.RedisComponent;
import com.interlude.entity.config.AppConfig;
import com.interlude.entity.constants.Constants;
import com.interlude.entity.dto.UploadResultDto;
import com.interlude.entity.po.video.*;
import com.interlude.entity.query.SimplePage;
import com.interlude.entity.query.video.*;
import com.interlude.enums.*;
import com.interlude.entity.vo.PaginationResultVO;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.interlude.exception.BusinessException;
import com.interlude.mapper.video.*;
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
    private VideoFileMapper<VideoFile, VideoFileQuery> videoFileMapper;

	@Resource
	private VideoDraftMapper<VideoDraft, VideoDraftQuery> videoDraftMapper;

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


		// 添加视频
		if(videoInfo.getVideoId() == 0){
			Long videoId = Long.parseLong(StringTools.getRandomNumber(Constants.NUMBER_15));
			videoInfo.setVideoId(videoId);

			// 判断文件信息是否存在, 将草稿表对应的redis里的文件信息存进 转码方法
			String key = Constants.REDIS_KEY_UPLOADING_FILE + videoInfo.getUserId() + uploadId;
			VideoDraft videoDraft = videoDraftMapper.selectByDraftKey(key);
			if(videoDraft == null){
				throw new BusinessException(ResponseCodeEnum.CODE_600);
			}
			if(!videoDraft.getUserId().equals(videoInfo.getUserId()) && videoDraft.getDraftStatus() == 2){
				throw new BusinessException(ResponseCodeEnum.CODE_600);
			}
			UploadResultDto uploadVideoFileInfoByKey = redisComponent.getUploadVideoFileInfoByKey(key);
			if(uploadVideoFileInfoByKey == null){
				throw new BusinessException(ResponseCodeEnum.CODE_600);
			}
			videoInfoMapper.insert(videoInfo);
			uploadVideoFileInfoByKey.setVideoId(videoId);
			redisComponent.addFile2TransferQueue(uploadVideoFileInfoByKey);

			// 新增审核表信息
			VideoAudit videoAudit = videoAuditMapper.selectByVideoId(videoId);
			if(videoAudit != null){
				throw new BusinessException(ResponseCodeEnum.CODE_600);
			}
			VideoAudit audit = new VideoAudit();
			audit.setVideoId(videoId);
			audit.setUserId(videoInfo.getUserId());
			audit.setAuditStatus(VideoAuditEnum.PENDING.getStatus());
			audit.setSubmitTime(new Date());
			videoAuditMapper.insert(audit);
		}else{

		}
	}

	@Override
	public void transferVideoFile(UploadResultDto resultDto) {
		try {

			// 获取临时目录
			String tempFilePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER + Constants.FILE_FOLDER_TEMP + resultDto.getFilePath();
			File tempFile = new File(tempFilePath);		// 临时目录

			// 目标目录
			String targetFilePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER + Constants.FILE_VIDEO + resultDto.getFilePath();
			File targetFile = new File(targetFilePath);
			if(targetFile.exists()){
				targetFile.mkdirs();
			}

			// 将临时目录的视频文件复制到目标文件
			FileUtils.copyDirectory(tempFile,targetFile);

			// 删除临时目录
			FileUtils.forceDelete(tempFile);

			// 删除redis中的上传数据
			redisComponent.delVideoFileInfo(resultDto.getUserId(),resultDto.getUploadId());

			String fileName = File.separator + resultDto.getFileName()+"@"+resultDto.getUploadId() + Constants.MP4_SUFFIX;
			//合并文件
			String completeVideo = targetFilePath + fileName;
			this.union(targetFilePath,completeVideo,fileName,true);

			convertVideoToMultiQualityHLS(completeVideo,resultDto);
		}catch (Exception e){
			log.error("文件转码失败",e);
		}finally {
			String key = Constants.REDIS_KEY_UPLOADING_FILE + resultDto.getUserId() + resultDto.getUploadId();
			// 更新草稿表记录为已提交
			VideoDraft draft = videoDraftMapper.selectByDraftKey(key);
			if(draft != null){
				draft.setDraftStatus(Constants.NUMBER_TWO);
				videoDraftMapper.updateByDraftKey(draft,key);
			}
		}
	}

	/**
	 *  将视频转换为多清晰度的 HLS 格式
	 *  使用枚举配置管理清晰度
	 * @param completeVideo 视频目标地址
	 */
	private void convertVideoToMultiQualityHLS(String completeVideo,UploadResultDto resultDto){
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
		}

		// 步骤2: 生成多清晰度 HLS 流
		// 可以根据需要选择特定的清晰度，这里使用480p, 720p, 1080p的清晰度
		fFmpegUtils.convertVideoToMultiQualityHLS(outputFolder, processingVideoPath,VideoQualityEnum.getMainstreamQualities(),resultDto);

		// 步骤3: 生成主播放列表
		fFmpegUtils.generateMasterPlaylist(outputFolder);
	}

	/**
	 * 自定义清晰度转换 - 只生成指定的清晰度
	 */
	private void convertVideoToSpecificQualities(String completeVideo, List<VideoQualityEnum> targetQualities,UploadResultDto resultDto) {
		File videoFile = new File(completeVideo);
		File outputFolder = videoFile.getParentFile();

		System.out.println("开始处理视频: " + completeVideo);
		System.out.println("目标清晰度: " + targetQualities.stream()
				.map(VideoQualityEnum::getDescription)
				.collect(Collectors.joining(", ")));

		// 编码检测和转换逻辑（同上）...

		// 只生成指定的清晰度
		fFmpegUtils.convertVideoToMultiQualityHLS(outputFolder, completeVideo, targetQualities,resultDto);

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