package com.interlude.service;

import com.interlude.entity.vo.PaginationResultVO;
import java.util.List;
import com.interlude.entity.po.CategoryInfo;
import com.interlude.entity.query.CategoryInfoQuery;
/**
 * @Description:分类信息Service
 * @auther:dazhi
 * @date:2025/10/27
 */

public interface CategoryInfoService{

	/**
	 * 根据条件查询列表
	 */
	List<CategoryInfo> findListByParam(CategoryInfoQuery param);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(CategoryInfoQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<CategoryInfo> findListByPage(CategoryInfoQuery param);

	/**
	 * 新增
	 */
	Integer add(CategoryInfo bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<CategoryInfo> listBean);

	/**
	 * 批量新增或修改
	 */
	Integer addOrUpdateBatch(List<CategoryInfo> listBean);

	/**
	 * 根据CategoryId查询
	 */
	CategoryInfo getCategoryInfoByCategoryId(Integer categoryId);

	/**
	 * 根据CategoryId更新
	 */
	Integer updateCategoryInfoByCategoryId(CategoryInfo bean, Integer categoryId);

	/**
	 * 根据CategoryId删除
	 */
	Integer deleteCategoryInfoByCategoryId(Integer categoryId);

	/**
	 * 根据CategoryCode查询
	 */
	CategoryInfo getCategoryInfoByCategoryCode(String categoryCode);

	/**
	 * 根据CategoryCode更新
	 */
	Integer updateCategoryInfoByCategoryCode(CategoryInfo bean, String categoryCode);

	/**
	 * 根据CategoryCode删除
	 */
	Integer deleteCategoryInfoByCategoryCode(String categoryCode);

	void saveCategory(CategoryInfo categoryInfo);

	void delCategory(Integer categoryId);

	void changeSort(Integer pCategoryId, String categoryIds);

	List<CategoryInfo> getALlCategoryInfo();

	List<CategoryInfo> selectCategoryById(Integer pCategoryId, Integer categoryId);
}