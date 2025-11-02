package com.interlude.service.video.impl;

import com.interlude.entity.query.SimplePage;
import com.interlude.enums.PageSize;
import com.interlude.entity.vo.PaginationResultVO;
import java.util.List;
import com.interlude.entity.po.video.VideoDraft;
import com.interlude.entity.query.video.VideoDraftQuery;
import com.interlude.mapper.video.VideoDraftMapper;
import com.interlude.service.video.VideoDraftService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;/**
 * @Description:视频草稿表Service
 * @auther:dazhi
 * @date:2025/10/30
 */

@Service("videoDraftService")
public class VideoDraftServiceImpl implements VideoDraftService{

	@Resource
	private VideoDraftMapper<VideoDraft,VideoDraftQuery> videoDraftMapper;
	/**
	 * 根据条件查询列表
	 */
	public List<VideoDraft> findListByParam(VideoDraftQuery query) {
		return this.videoDraftMapper.selectList(query);
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(VideoDraftQuery query) {
		return this.videoDraftMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<VideoDraft> findListByPage(VideoDraftQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(),count,pageSize);
		query.setSimplePage(page);
		List<VideoDraft> listByParam = this.findListByParam(query);
		PaginationResultVO<VideoDraft> result = new PaginationResultVO(count,page.getPageSize(),page.getPageNo(),page.getPageTotal(),listByParam);
		return result;
	}

	/**
	 * 新增
	 */
	public Integer add(VideoDraft bean) {
		return this.videoDraftMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<VideoDraft> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.videoDraftMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或修改
	 */
	public Integer addOrUpdateBatch(List<VideoDraft> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.videoDraftMapper.insertOrUpdateBatch(listBean);
	}


	/**
	 * 根据DraftId查询
	 */
	public VideoDraft getVideoDraftByDraftId(Long draftId) {
		return this.videoDraftMapper.selectByDraftId(draftId);
	}

	/**
	 * 根据DraftId更新
	 */
	public Integer updateVideoDraftByDraftId(VideoDraft bean, Long draftId) {
		return this.videoDraftMapper.updateByDraftId(bean, draftId);
	}

	/**
	 * 根据DraftId删除
	 */
	public Integer deleteVideoDraftByDraftId(Long draftId) {
		return this.videoDraftMapper.deleteByDraftId(draftId);
	}

	/**
	 * 根据DraftKey查询
	 */
	public VideoDraft getVideoDraftByDraftKey(String draftKey) {
		return this.videoDraftMapper.selectByDraftKey(draftKey);
	}

	/**
	 * 根据DraftKey更新
	 */
	public Integer updateVideoDraftByDraftKey(VideoDraft bean, String draftKey) {
		return this.videoDraftMapper.updateByDraftKey(bean, draftKey);
	}

	/**
	 * 根据DraftKey删除
	 */
	public Integer deleteVideoDraftByDraftKey(String draftKey) {
		return this.videoDraftMapper.deleteByDraftKey(draftKey);
	}
}