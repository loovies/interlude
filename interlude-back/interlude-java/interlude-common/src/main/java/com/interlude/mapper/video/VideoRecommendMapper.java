package com.interlude.mapper.video;

import com.interlude.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description:视频推荐表 Mapper
 * @auther:dazhi
 * @date:2025/10/30
 */
public interface VideoRecommendMapper<T, P> extends BaseMapper {
	/**
	 * 根据Id查询
	 */
	T selectById(@Param("id") Long id);

	/**
	 * 根据Id更新
	 */
	Integer updateById(@Param("bean") T t, @Param("id") Long id);

	/**
	 * 根据Id删除
	 */
	Integer deleteById(@Param("id") Long id);

	/**
	 * 根据VideoIdAndRecommendType查询
	 */
	T selectByVideoIdAndRecommendType(@Param("videoId") Long videoId, @Param("recommendType") Integer recommendType);

	/**
	 * 根据VideoIdAndRecommendType更新
	 */
	Integer updateByVideoIdAndRecommendType(@Param("bean") T t, @Param("videoId") Long videoId, @Param("recommendType") Integer recommendType);

	/**
	 * 根据VideoIdAndRecommendType删除
	 */
	Integer deleteByVideoIdAndRecommendType(@Param("videoId") Long videoId, @Param("recommendType") Integer recommendType);


}