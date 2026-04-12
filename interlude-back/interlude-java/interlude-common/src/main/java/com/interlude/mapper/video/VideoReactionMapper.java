package com.interlude.mapper.video;

import com.interlude.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper for video_reactions table.
 */
public interface VideoReactionMapper<T, P> extends BaseMapper {

    /**
     * 根据主键查询
     */
    T selectByReactionId(@Param("reactionId") Long reactionId);

    /**
     * 根据主键更新
     */
    Integer updateByReactionId(@Param("bean") T t, @Param("reactionId") Long reactionId);

    /**
     * 根据主键删除
     */
    Integer deleteByReactionId(@Param("reactionId") Long reactionId);

    /**
     * 根据视频+用户+类型查询
     */
    T selectByVideoUserAndType(@Param("videoId") Long videoId,
                               @Param("userId") String userId,
                               @Param("reactionType") String reactionType);

    /**
     * 根据视频+用户+类型删除
     */
    Integer deleteByVideoUserAndType(@Param("videoId") Long videoId,
                                     @Param("userId") String userId,
                                     @Param("reactionType") String reactionType);

    /**
     * 查询用户点赞视频ID列表（按最近互动时间倒序）
     */
    List<Long> selectLikedVideoIdsByUser(@Param("userId") String userId,
                                         @Param("offset") Integer offset,
                                         @Param("limit") Integer limit);

    /**
     * 统计用户点赞视频数量
     */
    Integer countLikedVideosByUser(@Param("userId") String userId);
}
