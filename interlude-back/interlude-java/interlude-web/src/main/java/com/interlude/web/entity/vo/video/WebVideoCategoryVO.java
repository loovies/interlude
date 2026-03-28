package com.interlude.web.entity.vo.video;

import java.util.ArrayList;
import java.util.List;

/**
 * 发现页与分类 Tab 使用的分类节点。
 */
public class WebVideoCategoryVO {

    private Integer categoryId;
    private String categoryCode;
    private String categoryName;
    private Integer pCategoryId;
    private Integer sort;
    private List<WebVideoCategoryVO> children = new ArrayList<>();

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getpCategoryId() {
        return pCategoryId;
    }

    public void setpCategoryId(Integer pCategoryId) {
        this.pCategoryId = pCategoryId;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public List<WebVideoCategoryVO> getChildren() {
        return children;
    }

    public void setChildren(List<WebVideoCategoryVO> children) {
        this.children = children;
    }
}
