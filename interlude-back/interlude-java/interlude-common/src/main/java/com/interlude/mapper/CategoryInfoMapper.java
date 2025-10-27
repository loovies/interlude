package com.interlude.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * @Description:分类信息 Mapper
 * @auther:dazhi
 * @date:2025/10/27
 */
public interface CategoryInfoMapper<T, P> extends BaseMapper {
	/**
	 * 根据CategoryId查询
	 */
	T selectByCategoryId(@Param("categoryId") Integer categoryId);

	/**
	 * 根据CategoryId更新
	 */
	Integer updateByCategoryId(@Param("bean") T t, @Param("categoryId") Integer categoryId);

	/**
	 * 根据CategoryId删除
	 */
	Integer deleteByCategoryId(@Param("categoryId") Integer categoryId);

	/**
	 * 根据CategoryCode查询
	 */
	T selectByCategoryCode(@Param("categoryCode") String categoryCode);

	/**
	 * 根据CategoryCode更新
	 */
	Integer updateByCategoryCode(@Param("bean") T t, @Param("categoryCode") String categoryCode);

	/**
	 * 根据CategoryCode删除
	 */
	Integer deleteByCategoryCode(@Param("categoryCode") String categoryCode);

	Integer deleteByParam(@Param("query") P query);

	Integer selectMaxSort(@Param("pCategoryId") Integer pCategoryId);
}