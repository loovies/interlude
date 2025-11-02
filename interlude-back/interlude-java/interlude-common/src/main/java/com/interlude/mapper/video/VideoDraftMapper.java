package com.interlude.mapper.video;

import com.interlude.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description:视频草稿表 Mapper
 * @auther:dazhi
 * @date:2025/10/30
 */
public interface VideoDraftMapper<T, P> extends BaseMapper {
	/**
	 * 根据DraftId查询
	 */
	T selectByDraftId(@Param("draftId") Long draftId);

	/**
	 * 根据DraftId更新
	 */
	Integer updateByDraftId(@Param("bean") T t, @Param("draftId") Long draftId);

	/**
	 * 根据DraftId删除
	 */
	Integer deleteByDraftId(@Param("draftId") Long draftId);

	/**
	 * 根据DraftKey查询
	 */
	T selectByDraftKey(@Param("draftKey") String draftKey);

	/**
	 * 根据DraftKey更新
	 */
	Integer updateByDraftKey(@Param("bean") T t, @Param("draftKey") String draftKey);

	/**
	 * 根据DraftKey删除
	 */
	Integer deleteByDraftKey(@Param("draftKey") String draftKey);

}