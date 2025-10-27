package com.interlude.entity.po;

import java.io.Serializable;
import java.util.List;


/**
 * @Description:分类信息
 * @auther:dazhi
 * @date:2025/10/27
 */
public class CategoryInfo implements Serializable {
	/**
	 * 自增分类ID
	 */
	private Integer categoryId;

	/**
	 * 分类编码
	 */
	private String categoryCode;

	/**
	 * 分类名称
	 */
	private String categoryName;

	/**
	 * 父级分类ID
	 */
	private Integer pCategoryId;

	/**
	 * 排序号
	 */
	private Integer sort;

	private List<CategoryInfo> children;

	public List<CategoryInfo> getChildren() {
		return children;
	}

	public void setChildren(List<CategoryInfo> children) {
		this.children = children;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public Integer getpCategoryId() {
		return pCategoryId;
	}

	public void setpCategoryId(Integer pCategoryId) {
		this.pCategoryId = pCategoryId;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public String getCategoryCode() {
		return this.categoryCode;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getCategoryName() {
		return this.categoryName;
	}

	public void setPCategoryId(Integer pCategoryId) {
		this.pCategoryId = pCategoryId;
	}

	public Integer getPCategoryId() {
		return this.pCategoryId;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Integer getSort() {
		return this.sort;
	}

	@Override
	public String toString() {
		return "自增分类ID:" + (categoryId == null ? "空" : categoryId) + ",分类编码:" + (categoryCode == null ? "空" : categoryCode) + ",分类名称:" + (categoryName == null ? "空" : categoryName) + ",父级分类ID:" + (pCategoryId == null ? "空" : pCategoryId) + ",排序号:" + (sort == null ? "空" : sort);
	}
}