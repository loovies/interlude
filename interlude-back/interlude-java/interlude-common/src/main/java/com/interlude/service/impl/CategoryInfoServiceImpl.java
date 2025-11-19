package com.interlude.service.impl;

import com.interlude.component.RedisComponent;
import com.interlude.entity.query.SimplePage;
import com.interlude.enums.PageSize;
import com.interlude.entity.vo.PaginationResultVO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.interlude.entity.po.CategoryInfo;
import com.interlude.entity.query.CategoryInfoQuery;
import com.interlude.enums.ResponseCodeEnum;
import com.interlude.exception.BusinessException;
import com.interlude.mapper.CategoryInfoMapper;
import com.interlude.service.CategoryInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;/**
 * @Description:分类信息Service
 * @auther:dazhi
 * @date:2025/10/27
 */

@Service("categoryInfoService")
public class CategoryInfoServiceImpl implements CategoryInfoService{

	@Resource
	private CategoryInfoMapper<CategoryInfo,CategoryInfoQuery> categoryInfoMapper;

	@Resource
	private RedisComponent redisComponent;

	/**
	 * 根据条件查询列表
	 */
	public List<CategoryInfo> findListByParam(CategoryInfoQuery query) {
		List<CategoryInfo> list = this.categoryInfoMapper.selectList(query);
		if(query.getConvert2Tree() != null && query.getConvert2Tree()) {
			list = convertLine2Tree(list,0);
		}
		return list;
	}

	// 递归生成树状结构 根据pid, 用pid递归查找相同的categoryId, 相同就存进children列表中
	private  List<CategoryInfo> convertLine2Tree(List<CategoryInfo> list, Integer pid) {
		List<CategoryInfo> children = new ArrayList<CategoryInfo>();
		for (CategoryInfo info : list) {
			if (info.getPCategoryId() != null&& info.getCategoryId() != null && info.getPCategoryId().equals(pid)) {
				info.setChildren(convertLine2Tree(list,info.getCategoryId()));
				children.add(info);
			}
		}
		return children;
	}

	/**
	 * 根据条件查询数量
	 */
	public Integer findCountByParam(CategoryInfoQuery query) {
		return this.categoryInfoMapper.selectCount(query);
	}

	/**
	 * 分页查询
	 */
	public PaginationResultVO<CategoryInfo> findListByPage(CategoryInfoQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(),count,pageSize);
		query.setSimplePage(page);
		List<CategoryInfo> listByParam = this.findListByParam(query);
		PaginationResultVO<CategoryInfo> result = new PaginationResultVO(count,page.getPageSize(),page.getPageNo(),page.getPageTotal(),listByParam);
		return result;
	}

	/**
	 * 新增
	 */
	public Integer add(CategoryInfo bean) {
		return this.categoryInfoMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	public Integer addBatch(List<CategoryInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.categoryInfoMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或修改
	 */
	public Integer addOrUpdateBatch(List<CategoryInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.categoryInfoMapper.insertOrUpdateBatch(listBean);
	}


	/**
	 * 根据CategoryId查询
	 */
	public CategoryInfo getCategoryInfoByCategoryId(Integer categoryId) {
		return this.categoryInfoMapper.selectByCategoryId(categoryId);
	}

	/**
	 * 根据CategoryId更新
	 */
	public Integer updateCategoryInfoByCategoryId(CategoryInfo bean, Integer categoryId) {
		return this.categoryInfoMapper.updateByCategoryId(bean, categoryId);
	}

	/**
	 * 根据CategoryId删除
	 */
	public Integer deleteCategoryInfoByCategoryId(Integer categoryId) {
		return this.categoryInfoMapper.deleteByCategoryId(categoryId);
	}

	/**
	 * 根据CategoryCode查询
	 */
	public CategoryInfo getCategoryInfoByCategoryCode(String categoryCode) {
		return this.categoryInfoMapper.selectByCategoryCode(categoryCode);
	}

	/**
	 * 根据CategoryCode更新
	 */
	public Integer updateCategoryInfoByCategoryCode(CategoryInfo bean, String categoryCode) {
		return this.categoryInfoMapper.updateByCategoryCode(bean, categoryCode);
	}

	/**
	 * 根据CategoryCode删除
	 */
	public Integer deleteCategoryInfoByCategoryCode(String categoryCode) {
		return this.categoryInfoMapper.deleteByCategoryCode(categoryCode);
	}

	@Override
	public void saveCategory(CategoryInfo categoryInfo) {
		CategoryInfo dbCategoryInfo = this.categoryInfoMapper.selectByCategoryCode(categoryInfo.getCategoryCode());
		if (categoryInfo.getCategoryId() == null && dbCategoryInfo != null ||
				categoryInfo.getPCategoryId() != null && dbCategoryInfo != null &&
						!categoryInfo.getCategoryId().equals(dbCategoryInfo.getCategoryId())
		) {

			throw new BusinessException("分类编号已存在");
		}
		if(categoryInfo.getCategoryId() == null){
			Integer sortMax = categoryInfoMapper.selectMaxSort(categoryInfo.getPCategoryId());
			categoryInfo.setSort(sortMax+1);
			categoryInfoMapper.insert(categoryInfo);
		}else{
			categoryInfoMapper.updateByCategoryId(categoryInfo,categoryInfo.getCategoryId());
		}
		save2Redis();
	}

	@Override
	public void delCategory(Integer categoryId) {
		// TODO 查询分类下是否有视频

		CategoryInfoQuery query = new CategoryInfoQuery();
		query.setCategoryIdOrPCategoryId(categoryId);
		categoryInfoMapper.deleteByParam(query);
		save2Redis();
	}

	/**
	 * 交换排序
	 * @param pCategoryId
	 * @param categoryIds
	 */
	@Override
	public void changeSort(Integer pCategoryId, String categoryIds) {
		String[] categoryIdList = categoryIds.split(",");
		List<CategoryInfo> categoryInfoList = new ArrayList<>();
		Integer sort = 0;
		for (String categoryId : categoryIdList) {
			CategoryInfo categoryInfo = new CategoryInfo();
			categoryInfo.setCategoryId(Integer.parseInt(categoryId));
			categoryInfo.setPCategoryId(pCategoryId);
			categoryInfo.setSort(++sort);
			categoryInfoList.add(categoryInfo);
		}
		categoryInfoMapper.updateBatchSort(categoryInfoList);
		save2Redis();
	}

	@Override
	public List<CategoryInfo> getALlCategoryInfo() {
		List<CategoryInfo> categoryInfoList = redisComponent.getCategoryList();
		if (categoryInfoList == null || categoryInfoList.isEmpty()){
			save2Redis();
		}
		return redisComponent.getCategoryList();
	}

	@Override
	public List<CategoryInfo> selectCategoryById(Integer pCategoryId, Integer categoryId) {
		List<CategoryInfo> categoryInfoList = new ArrayList<>();
		if(pCategoryId == 0){
			CategoryInfo categoryInfo = categoryInfoMapper.selectByCategoryId(categoryId);
			if(categoryInfo == null){
				throw new BusinessException(ResponseCodeEnum.CODE_600);
			}
			categoryInfoList.add(categoryInfo);
		}else{
			CategoryInfo categoryInfo = categoryInfoMapper.selectByCategoryId(categoryId);
			if(categoryInfo == null){
				throw new BusinessException(ResponseCodeEnum.CODE_600);
			}
			categoryInfoList.add(categoryInfo);
			categoryInfo = categoryInfoMapper.selectByCategoryId(pCategoryId);
			if(categoryInfo == null){
				throw new BusinessException(ResponseCodeEnum.CODE_600);
			}
			categoryInfoList.add(categoryInfo);
		}
		return categoryInfoList;
	}

	//刷新缓存
	public void save2Redis(){
		CategoryInfoQuery categoryInfoQuery = new CategoryInfoQuery();
		categoryInfoQuery.setOrderBy("sort asc");
		categoryInfoQuery.setConvert2Tree(true);
		List<CategoryInfo> list = this.findListByParam(categoryInfoQuery);
		redisComponent.saveCategoryList(list);
	}
}