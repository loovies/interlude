package com.interlude.web.service.video;

import com.interlude.entity.po.CategoryInfo;
import com.interlude.entity.po.UserInfo;
import com.interlude.entity.po.video.VideoFile;
import com.interlude.entity.po.video.VideoInfo;
import com.interlude.entity.po.video.VideoRecommend;
import com.interlude.entity.po.video.VideoStats;
import com.interlude.entity.query.video.VideoFileQuery;
import com.interlude.entity.query.video.VideoInfoQuery;
import com.interlude.entity.query.video.VideoRecommendQuery;
import com.interlude.entity.query.video.VideoStatsQuery;
import com.interlude.entity.vo.PaginationResultVO;
import com.interlude.entity.vo.video.PlayListInfoVo;
import com.interlude.enums.DateTimePatterEnum;
import com.interlude.enums.ResponseCodeEnum;
import com.interlude.enums.VideoQualityEnum;
import com.interlude.enums.VideoStatusEnum;
import com.interlude.exception.BusinessException;
import com.interlude.service.CategoryInfoService;
import com.interlude.service.UserInfoService;
import com.interlude.service.video.VideoFileService;
import com.interlude.service.video.VideoInfoService;
import com.interlude.service.video.VideoRecommendService;
import com.interlude.service.video.VideoStatsService;
import com.interlude.utils.DateUtils;
import com.interlude.utils.StringTools;
import com.interlude.web.entity.vo.video.WebVideoAuthorVO;
import com.interlude.web.entity.vo.video.WebVideoCardVO;
import com.interlude.web.entity.vo.video.WebVideoCategoryVO;
import com.interlude.web.entity.vo.video.WebVideoDetailVO;
import com.interlude.web.entity.vo.video.WebVideoQualityVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * Web 端视频查询聚合服务。
 */
@Service
public class WebVideoQueryService {

    private static final int DEFAULT_PAGE_NO = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 30;

    private static final int VISIBILITY_PUBLIC = 1;
    private static final int RECOMMEND_TYPE_HOME = 1;

    @Resource
    private VideoInfoService videoInfoService;

    @Resource
    private VideoFileService videoFileService;

    @Resource
    private VideoStatsService videoStatsService;

    @Resource
    private VideoRecommendService videoRecommendService;

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private CategoryInfoService categoryInfoService;

    /**
     * 推荐流：优先读取推荐表，无数据时回退到最新发布。
     */
    public PaginationResultVO<WebVideoCardVO> getRecommendFeed(Integer pageNo, Integer pageSize) {
        int safePageNo = normalizePageNo(pageNo);
        int safePageSize = normalizePageSize(pageSize);

        PaginationResultVO<VideoRecommend> recommendPage = loadRecommendPage(safePageNo, safePageSize);
        List<VideoInfo> videos = loadPublishedVideosFromRecommend(recommendPage.getList());
        if (videos.isEmpty()) {
            return getLatestFeed(safePageNo, safePageSize);
        }

        List<WebVideoCardVO> cards = toCards(limitVideos(videos, safePageSize));
        Integer total = recommendPage.getTotalCount() == null ? cards.size() : recommendPage.getTotalCount();
        return buildPage(cards, safePageNo, safePageSize, total);
    }

    /**
     * 最新发布流：按发布时间倒序。
     */
    public PaginationResultVO<WebVideoCardVO> getLatestFeed(Integer pageNo, Integer pageSize) {
        int safePageNo = normalizePageNo(pageNo);
        int safePageSize = normalizePageSize(pageSize);

        VideoInfoQuery query = buildPublicVideoQuery(safePageNo, safePageSize);
        query.setOrderBy("publish_time desc, create_time desc");

        PaginationResultVO<VideoInfo> page = videoInfoService.findListByPage(query);
        List<WebVideoCardVO> cards = toCards(page == null ? null : page.getList());
        Integer total = page == null || page.getTotalCount() == null ? cards.size() : page.getTotalCount();
        return buildPage(cards, safePageNo, safePageSize, total);
    }

    /**
     * 热门流：按热度分排序。
     */
    public PaginationResultVO<WebVideoCardVO> getHotFeed(Integer pageNo, Integer pageSize) {
        return getRankFeed("hot", pageNo, pageSize);
    }

    /**
     * 排行榜流：支持 hot/play/like 三种指标。
     */
    public PaginationResultVO<WebVideoCardVO> getRankFeed(String type, Integer pageNo, Integer pageSize) {
        int safePageNo = normalizePageNo(pageNo);
        int safePageSize = normalizePageSize(pageSize);

        VideoStatsQuery statsQuery = new VideoStatsQuery();
        statsQuery.setPageNo(safePageNo);
        statsQuery.setPageSize(safePageSize * 2);
        statsQuery.setOrderBy(resolveRankOrder(type));

        PaginationResultVO<VideoStats> statsPage = videoStatsService.findListByPage(statsQuery);
        List<VideoInfo> videos = loadPublishedVideosByStats(statsPage == null ? null : statsPage.getList());
        if (videos.isEmpty()) {
            return getLatestFeed(safePageNo, safePageSize);
        }

        List<WebVideoCardVO> cards = toCards(limitVideos(videos, safePageSize));
        Integer total = statsPage == null || statsPage.getTotalCount() == null ? cards.size() : statsPage.getTotalCount();
        return buildPage(cards, safePageNo, safePageSize, total);
    }

    /**
     * 搜索视频流：按标题模糊匹配。
     */
    public PaginationResultVO<WebVideoCardVO> search(String keyword, Integer pageNo, Integer pageSize) {
        int safePageNo = normalizePageNo(pageNo);
        int safePageSize = normalizePageSize(pageSize);

        if (StringTools.isEmpty(keyword)) {
            return getLatestFeed(safePageNo, safePageSize);
        }

        VideoInfoQuery query = buildPublicVideoQuery(safePageNo, safePageSize);
        query.setVideoNameFuzzy(keyword.trim());
        query.setOrderBy("publish_time desc, create_time desc");

        PaginationResultVO<VideoInfo> page = videoInfoService.findListByPage(query);
        List<WebVideoCardVO> cards = toCards(page == null ? null : page.getList());
        Integer total = page == null || page.getTotalCount() == null ? cards.size() : page.getTotalCount();
        return buildPage(cards, safePageNo, safePageSize, total);
    }

    /**
     * 分类流：支持一级分类和二级分类筛选。
     */
    public PaginationResultVO<WebVideoCardVO> getCategoryFeed(Integer pCategoryId,
                                                               Integer categoryId,
                                                               Integer pageNo,
                                                               Integer pageSize) {
        int safePageNo = normalizePageNo(pageNo);
        int safePageSize = normalizePageSize(pageSize);

        VideoInfoQuery query = buildPublicVideoQuery(safePageNo, safePageSize);
        query.setOrderBy("publish_time desc, create_time desc");
        if (pCategoryId != null) {
            query.setPCategoryId(pCategoryId);
        }
        if (categoryId != null) {
            query.setCategoryId(categoryId);
        }

        PaginationResultVO<VideoInfo> page = videoInfoService.findListByPage(query);
        List<WebVideoCardVO> cards = toCards(page == null ? null : page.getList());
        Integer total = page == null || page.getTotalCount() == null ? cards.size() : page.getTotalCount();
        return buildPage(cards, safePageNo, safePageSize, total);
    }

    /**
     * 闅忔満鎾斁鍒楄〃锛堣烦杞埌棣栭〉鎾斁锛夛紝鍙互浼犲叆璧峰瑙嗛 ID 鎺掔銆?
     */
    public PaginationResultVO<WebVideoCardVO> getRandomFeed(Long seedVideoId,
                                                            Integer pageNo,
                                                            Integer pageSize) {
        int safePageNo = normalizePageNo(pageNo);
        int safePageSize = normalizePageSize(pageSize);

        VideoInfoQuery query = buildPublicVideoQuery(DEFAULT_PAGE_NO, safePageSize * 5);
        query.setOrderBy("publish_time desc, create_time desc");
        PaginationResultVO<VideoInfo> page = videoInfoService.findListByPage(query);

        List<VideoInfo> candidates = page == null || page.getList() == null
                ? new ArrayList<>()
                : new ArrayList<>(page.getList());
        Collections.shuffle(candidates);

        List<VideoInfo> randomized = new ArrayList<>();
        if (seedVideoId != null) {
            VideoInfo seedVideo = videoInfoService.getVideoInfoByVideoId(seedVideoId);
            if (isPublicPublishedVideo(seedVideo)) {
                randomized.add(seedVideo);
            }
        }

        for (VideoInfo item : candidates) {
            if (item == null) {
                continue;
            }
            if (seedVideoId != null && seedVideoId.equals(item.getVideoId())) {
                continue;
            }
            randomized.add(item);
            if (randomized.size() >= safePageSize) {
                break;
            }
        }

        if (randomized.isEmpty()) {
            randomized.addAll(candidates);
        }

        List<WebVideoCardVO> cards = toCards(limitVideos(randomized, safePageSize));
        return buildPage(cards, safePageNo, safePageSize, cards.size());
    }

    /**
     * 获取单个已发布视频的完整详情。
     */
    public WebVideoDetailVO getVideoDetail(Long videoId) {
        VideoInfo videoInfo = getPublishedVideoOrThrow(videoId);
        return toDetail(videoInfo);
    }

    /**
     * Get the cover path of a published video by video ID.
     */
    public String getVideoCover(Long videoId) {
        VideoInfo videoInfo = getPublishedVideoOrThrow(videoId);
        return videoInfo.getVideoCover();
    }

    /**
     * 按视频 ID 组装播放器清晰度与播放地址。
     */
    public PlayListInfoVo getPlayList(Long videoId) {
        VideoInfo videoInfo = getPublishedVideoOrThrow(videoId);
        List<VideoFile> files = getVideoFiles(videoInfo.getVideoId());
        if (files.isEmpty()) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        PlayListInfoVo playListInfoVo = new PlayListInfoVo();
        playListInfoVo.setVideoId(videoInfo.getVideoId());
        playListInfoVo.setTitle(videoInfo.getVideoName());

        List<Map<String, Object>> qualities = new ArrayList<>();
        Integer duration = 0;
        for (int i = 0; i < files.size(); i++) {
            VideoFile file = files.get(i);
            Map<String, Object> quality = new HashMap<>();
            quality.put("quality", resolveQualityName(file.getQuality()));
            quality.put("m3u8Url", buildVideoM3u8Url(file.getFilePath()));
            quality.put("resolution", buildResolution(file.getWidth(), file.getHeight()));
            quality.put("bitrate", file.getBitrate() == null ? null : file.getBitrate() + "kbps");
            quality.put("recommended", i == 0);
            qualities.add(quality);
            if (file.getDuration() != null) {
                duration = file.getDuration();
            }
        }

        playListInfoVo.setDuration(duration);
        playListInfoVo.setQualities(qualities);
        return playListInfoVo;
    }

    /**
     * 相关推荐：优先同分类，不足时补充最新视频。
     */
    public List<WebVideoCardVO> getRelatedVideos(Long videoId, Integer limit) {
        VideoInfo currentVideo = getPublishedVideoOrThrow(videoId);
        int safeLimit = normalizeRelatedLimit(limit);

        VideoInfoQuery query = buildPublicVideoQuery(1, safeLimit * 3);
        query.setCategoryId(currentVideo.getCategoryId());
        query.setOrderBy("publish_time desc, create_time desc");

        List<VideoInfo> relatedCandidatesPage = videoInfoService.findListByPage(query).getList();
        List<VideoInfo> relatedVideos = new ArrayList<>();
        if (relatedCandidatesPage != null) {
            for (VideoInfo video : relatedCandidatesPage) {
                if (video == null || video.getVideoId() == null || video.getVideoId().equals(videoId)) {
                    continue;
                }
                relatedVideos.add(video);
                if (relatedVideos.size() >= safeLimit) {
                    break;
                }
            }
        }

        if (relatedVideos.size() < safeLimit) {
            VideoInfoQuery latestQuery = buildPublicVideoQuery(1, safeLimit * 5);
            latestQuery.setOrderBy("publish_time desc, create_time desc");
            List<VideoInfo> latestList = videoInfoService.findListByPage(latestQuery).getList();
            if (latestList != null) {
                for (VideoInfo item : latestList) {
                    if (item == null || item.getVideoId() == null || item.getVideoId().equals(videoId)) {
                        continue;
                    }
                    if (containsVideoId(relatedVideos, item.getVideoId())) {
                        continue;
                    }
                    relatedVideos.add(item);
                    if (relatedVideos.size() >= safeLimit) {
                        break;
                    }
                }
            }
        }

        return toCards(limitVideos(relatedVideos, safeLimit));
    }

    /**
     * 按给定视频ID顺序返回可公开展示的视频卡片。
     */
    public List<WebVideoCardVO> getCardsByVideoIds(List<Long> videoIds) {
        List<WebVideoCardVO> cards = new ArrayList<>();
        if (videoIds == null || videoIds.isEmpty()) {
            return cards;
        }
        for (Long videoId : videoIds) {
            if (videoId == null) {
                continue;
            }
            VideoInfo videoInfo = videoInfoService.getVideoInfoByVideoId(videoId);
            if (!isPublicPublishedVideo(videoInfo)) {
                continue;
            }
            cards.add(toCard(videoInfo));
        }
        return cards;
    }

    /**
     * 获取发现页所需分类树。
     */
    public List<WebVideoCategoryVO> getCategoryTree() {
        List<CategoryInfo> categoryTree = categoryInfoService.getALlCategoryInfo();
        List<WebVideoCategoryVO> result = new ArrayList<>();
        if (categoryTree == null) {
            return result;
        }
        for (CategoryInfo categoryInfo : categoryTree) {
            result.add(toCategoryVo(categoryInfo));
        }
        return result;
    }

    private PaginationResultVO<VideoRecommend> loadRecommendPage(int pageNo, int pageSize) {
        VideoRecommendQuery query = new VideoRecommendQuery();
        query.setRecommendType(RECOMMEND_TYPE_HOME);
        query.setPageNo(pageNo);
        query.setPageSize(pageSize * 2);
        query.setOrderBy("recommend_weight desc, create_time desc");
        return videoRecommendService.findListByPage(query);
    }

    private List<VideoInfo> loadPublishedVideosFromRecommend(List<VideoRecommend> recommends) {
        List<VideoInfo> videos = new ArrayList<>();
        if (recommends == null || recommends.isEmpty()) {
            return videos;
        }

        Date now = new Date();
        LinkedHashSet<Long> orderedIds = new LinkedHashSet<>();
        for (VideoRecommend recommend : recommends) {
            if (recommend == null || recommend.getVideoId() == null) {
                continue;
            }
            if (recommend.getStartTime() != null && now.before(recommend.getStartTime())) {
                continue;
            }
            if (recommend.getEndTime() != null && now.after(recommend.getEndTime())) {
                continue;
            }
            orderedIds.add(recommend.getVideoId());
        }

        for (Long id : orderedIds) {
            VideoInfo videoInfo = videoInfoService.getVideoInfoByVideoId(id);
            if (isPublicPublishedVideo(videoInfo)) {
                videos.add(videoInfo);
            }
        }
        return videos;
    }

    private List<VideoInfo> loadPublishedVideosByStats(List<VideoStats> statsList) {
        List<VideoInfo> videos = new ArrayList<>();
        if (statsList == null || statsList.isEmpty()) {
            return videos;
        }

        LinkedHashSet<Long> orderedIds = new LinkedHashSet<>();
        for (VideoStats stats : statsList) {
            if (stats == null || stats.getVideoId() == null) {
                continue;
            }
            orderedIds.add(stats.getVideoId());
        }

        for (Long id : orderedIds) {
            VideoInfo videoInfo = videoInfoService.getVideoInfoByVideoId(id);
            if (isPublicPublishedVideo(videoInfo)) {
                videos.add(videoInfo);
            }
        }
        return videos;
    }

    private List<WebVideoCardVO> toCards(List<VideoInfo> videos) {
        List<WebVideoCardVO> cards = new ArrayList<>();
        if (videos == null || videos.isEmpty()) {
            return cards;
        }
        for (VideoInfo videoInfo : videos) {
            if (videoInfo == null) {
                continue;
            }
            cards.add(toCard(videoInfo));
        }
        return cards;
    }

    private WebVideoCardVO toCard(VideoInfo videoInfo) {
        WebVideoCardVO card = new WebVideoCardVO();
        card.setVideoId(videoInfo.getVideoId());
        card.setTitle(videoInfo.getVideoName());
        card.setDescription(videoInfo.getDescription());
        card.setCoverUrl(buildCoverUrl(videoInfo));
        card.setPublishTime(formatDate(videoInfo.getPublishTime() == null ? videoInfo.getCreateTime() : videoInfo.getPublishTime()));
        card.setTags(videoInfo.getTags());
        card.setInteractionSettings(videoInfo.getInteractionSettings());
        card.setpCategoryId(videoInfo.getPCategoryId());
        card.setCategoryId(videoInfo.getCategoryId());

        fillCategoryNames(videoInfo, card);
        fillAuthor(videoInfo, card);
        fillStats(videoInfo, card);
        fillVideoFiles(videoInfo, card);

        return card;
    }

    private WebVideoDetailVO toDetail(VideoInfo videoInfo) {
        WebVideoCardVO card = toCard(videoInfo);
        WebVideoDetailVO detailVO = new WebVideoDetailVO();

        detailVO.setVideoId(card.getVideoId());
        detailVO.setTitle(card.getTitle());
        detailVO.setDescription(card.getDescription());
        detailVO.setCoverUrl(card.getCoverUrl());
        detailVO.setPublishTime(card.getPublishTime());
        detailVO.setpCategoryId(card.getpCategoryId());
        detailVO.setpCategoryName(card.getpCategoryName());
        detailVO.setCategoryId(card.getCategoryId());
        detailVO.setCategoryName(card.getCategoryName());
        detailVO.setTags(card.getTags());
        detailVO.setInteractionSettings(card.getInteractionSettings());
        detailVO.setDuration(card.getDuration());
        detailVO.setQualities(card.getQualities());
        detailVO.setPlayCount(card.getPlayCount());
        detailVO.setLikeCount(card.getLikeCount());
        detailVO.setCommentCount(card.getCommentCount());
        detailVO.setShareCount(card.getShareCount());
        detailVO.setCollectCount(card.getCollectCount());
        detailVO.setHotScore(card.getHotScore());
        detailVO.setAuthor(card.getAuthor());

        detailVO.setVisibility(videoInfo.getVisibility());
        detailVO.setVideoType(videoInfo.getVideoType());
        detailVO.setOriginAuthor(videoInfo.getOriginAuthor());
        detailVO.setOriginUrl(videoInfo.getOriginUrl());
        return detailVO;
    }

    private void fillCategoryNames(VideoInfo videoInfo, WebVideoCardVO card) {
        if (videoInfo.getCategoryId() == null && videoInfo.getPCategoryId() == null) {
            return;
        }
        List<CategoryInfo> categories = categoryInfoService.selectCategoryById(videoInfo.getPCategoryId(), videoInfo.getCategoryId());
        if (categories == null || categories.isEmpty()) {
            return;
        }

        if (videoInfo.getPCategoryId() != null && videoInfo.getPCategoryId().intValue() == 0) {
            card.setCategoryName(categories.get(0).getCategoryName());
            return;
        }

        if (categories.size() == 1) {
            card.setCategoryName(categories.get(0).getCategoryName());
            return;
        }

        card.setCategoryName(categories.get(0).getCategoryName());
        card.setpCategoryName(categories.get(1).getCategoryName());
    }

    private void fillAuthor(VideoInfo videoInfo, WebVideoCardVO card) {
        if (StringTools.isEmpty(videoInfo.getUserId())) {
            return;
        }
        UserInfo userInfo = userInfoService.getUserInfoByUserId(videoInfo.getUserId());
        if (userInfo == null) {
            return;
        }
        WebVideoAuthorVO author = new WebVideoAuthorVO();
        author.setUserId(userInfo.getUserId());
        author.setNickName(userInfo.getNickName());
        author.setAvatar(userInfo.getAvatar());
        card.setAuthor(author);
    }

    private void fillStats(VideoInfo videoInfo, WebVideoCardVO card) {
        VideoStats stats = videoStatsService.getVideoStatsByVideoId(videoInfo.getVideoId());
        if (stats == null) {
            card.setPlayCount(0L);
            card.setLikeCount(0);
            card.setCommentCount(0);
            card.setShareCount(0);
            card.setCollectCount(0);
            card.setHotScore(0);
            return;
        }
        card.setPlayCount(stats.getPlayCount() == null ? 0L : stats.getPlayCount());
        card.setLikeCount(stats.getLikeCount() == null ? 0 : stats.getLikeCount());
        card.setCommentCount(stats.getCommentCount() == null ? 0 : stats.getCommentCount());
        card.setShareCount(stats.getShareCount() == null ? 0 : stats.getShareCount());
        card.setCollectCount(stats.getCollectCount() == null ? 0 : stats.getCollectCount());
        card.setHotScore(stats.getHotScore() == null ? 0 : stats.getHotScore());
    }

    private void fillVideoFiles(VideoInfo videoInfo, WebVideoCardVO card) {
        List<VideoFile> files = getVideoFiles(videoInfo.getVideoId());
        if (files.isEmpty()) {
            card.setDuration(0);
            card.setQualities(new ArrayList<>());
            return;
        }

        VideoFile first = files.get(0);
        card.setDuration(first.getDuration() == null ? 0 : first.getDuration());

        List<WebVideoQualityVO> qualities = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            VideoFile file = files.get(i);
            WebVideoQualityVO qualityVO = new WebVideoQualityVO();
            qualityVO.setQuality(resolveQualityName(file.getQuality()));
            qualityVO.setUrl(buildVideoM3u8Url(file.getFilePath()));
            qualityVO.setResolution(buildResolution(file.getWidth(), file.getHeight()));
            qualityVO.setBitrate(file.getBitrate());
            qualityVO.setRecommended(i == 0);
            qualities.add(qualityVO);
        }
        card.setQualities(qualities);
    }

    private List<VideoFile> getVideoFiles(Long videoId) {
        VideoFileQuery query = new VideoFileQuery();
        query.setVideoId(videoId);
        query.setOrderBy("is_primary desc, quality desc, create_time desc");
        List<VideoFile> files = videoFileService.findListByParam(query);
        return files == null ? new ArrayList<>() : files;
    }

    private VideoInfoQuery buildPublicVideoQuery(int pageNo, int pageSize) {
        VideoInfoQuery query = new VideoInfoQuery();
        query.setPageNo(pageNo);
        query.setPageSize(pageSize);
        query.setStatus(VideoStatusEnum.PUBLISHED.getStatus());
        query.setVisibility(VISIBILITY_PUBLIC);
        return query;
    }

    private PaginationResultVO<WebVideoCardVO> buildPage(List<WebVideoCardVO> list,
                                                         int pageNo,
                                                         int pageSize,
                                                         Integer totalCount) {
        PaginationResultVO<WebVideoCardVO> page = new PaginationResultVO<>();
        int safeTotal = totalCount == null ? 0 : Math.max(totalCount, 0);
        page.setList(list == null ? new ArrayList<>() : list);
        page.setPageNo(pageNo);
        page.setPageSize(pageSize);
        page.setTotalCount(safeTotal);

        if (pageSize <= 0) {
            page.setPageTotal(0);
        } else {
            page.setPageTotal((safeTotal + pageSize - 1) / pageSize);
        }
        return page;
    }

    private VideoInfo getPublishedVideoOrThrow(Long videoId) {
        if (videoId == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        VideoInfo videoInfo = videoInfoService.getVideoInfoByVideoId(videoId);
        if (!isPublicPublishedVideo(videoInfo)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        return videoInfo;
    }

    private boolean isPublicPublishedVideo(VideoInfo videoInfo) {
        return videoInfo != null
                && videoInfo.getStatus() != null
                && videoInfo.getStatus().intValue() == VideoStatusEnum.PUBLISHED.getStatus().intValue()
                && videoInfo.getVisibility() != null
                && videoInfo.getVisibility().intValue() == VISIBILITY_PUBLIC;
    }

    private int normalizePageNo(Integer pageNo) {
        if (pageNo == null || pageNo <= 0) {
            return DEFAULT_PAGE_NO;
        }
        return pageNo;
    }

    private int normalizePageSize(Integer pageSize) {
        if (pageSize == null || pageSize <= 0) {
            return DEFAULT_PAGE_SIZE;
        }
        return Math.min(pageSize, MAX_PAGE_SIZE);
    }

    private int normalizeRelatedLimit(Integer limit) {
        if (limit == null || limit <= 0) {
            return 10;
        }
        return Math.min(limit, 30);
    }

    private String resolveRankOrder(String type) {
        if ("play".equalsIgnoreCase(type)) {
            return "play_count desc, update_time desc";
        }
        if ("like".equalsIgnoreCase(type)) {
            return "like_count desc, update_time desc";
        }
        return "hot_score desc, play_count desc, update_time desc";
    }

    private String resolveQualityName(Integer quality) {
        if (quality == null) {
            return VideoQualityEnum.getDefault().getName();
        }
        VideoQualityEnum qualityEnum = VideoQualityEnum.getByCode(Math.max(0, quality - 1));
        return qualityEnum.getName();
    }

    private String buildVideoM3u8Url(String filePath) {
        if (StringTools.isEmpty(filePath)) {
            return "";
        }
        String normalized = normalizePath(filePath);
        if (normalized.startsWith("http://") || normalized.startsWith("https://")) {
            return normalized;
        }
        return "/file/video/" + normalized + "/index.m3u8";
    }

    private String buildCoverUrl(VideoInfo videoInfo) {
        if (videoInfo == null || videoInfo.getVideoId() == null) {
            return "";
        }

        String coverPath = videoInfo.getVideoCover();
        if (StringTools.isEmpty(coverPath)) {
            return "";
        }

        String normalized = coverPath.trim();
        if (normalized.startsWith("http://") || normalized.startsWith("https://")) {
            return normalized;
        }

        return "/api/video/discover/" + videoInfo.getVideoId() + "/cover";
    }

    private String normalizePath(String path) {
        String normalized = path.replace("\\", "/");
        while (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }
        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }

    private String buildResolution(Integer width, Integer height) {
        if (width == null || height == null) {
            return null;
        }
        return width + "x" + height;
    }

    private String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        return DateUtils.format(date, DateTimePatterEnum.YYYY_MM_DD_HH_MM_SS.getPattern());
    }

    private WebVideoCategoryVO toCategoryVo(CategoryInfo categoryInfo) {
        WebVideoCategoryVO vo = new WebVideoCategoryVO();
        if (categoryInfo == null) {
            return vo;
        }

        vo.setCategoryId(categoryInfo.getCategoryId());
        vo.setCategoryCode(categoryInfo.getCategoryCode());
        vo.setCategoryName(categoryInfo.getCategoryName());
        vo.setpCategoryId(categoryInfo.getpCategoryId());
        vo.setSort(categoryInfo.getSort());

        List<WebVideoCategoryVO> children = new ArrayList<>();
        if (categoryInfo.getChildren() != null) {
            for (CategoryInfo child : categoryInfo.getChildren()) {
                children.add(toCategoryVo(child));
            }
        }
        vo.setChildren(children);
        return vo;
    }

    private List<VideoInfo> limitVideos(List<VideoInfo> videos, int limit) {
        if (videos == null || videos.isEmpty()) {
            return new ArrayList<>();
        }
        int end = Math.min(limit, videos.size());
        return new ArrayList<>(videos.subList(0, end));
    }

    private boolean containsVideoId(List<VideoInfo> list, Long videoId) {
        if (list == null || videoId == null) {
            return false;
        }
        for (VideoInfo info : list) {
            if (info != null && info.getVideoId() != null && info.getVideoId().equals(videoId)) {
                return true;
            }
        }
        return false;
    }
}
