package com.interlude.service.video;

import com.interlude.entity.vo.PaginationResultVO;
import java.util.List;
import com.interlude.entity.po.video.VideoDraft;
import com.interlude.entity.query.video.VideoDraftQuery;
/**
 * @Description:视频草稿表Service
 * @auther:dazhi
 * @date:2025/10/30
 */

public interface VideoDraftService{

	/**
	 * 根据条件查询列表
	 */
	List<VideoDraft> findListByParam(VideoDraftQuery param);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(VideoDraftQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<VideoDraft> findListByPage(VideoDraftQuery param);

	/**
	 * 新增
	 */
	Integer add(VideoDraft bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<VideoDraft> listBean);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<VideoDraft> listBean);

	/**
	 * 根据DraftId查询
	 */
	VideoDraft getVideoDraftByDraftId(Long draftId);

	/**
	 * 根据DraftId更新
	 */
	Integer updateVideoDraftByDraftId(VideoDraft bean, Long draftId);

	/**
	 * 根据DraftId删除
	 */
	Integer deleteVideoDraftByDraftId(Long draftId);

}