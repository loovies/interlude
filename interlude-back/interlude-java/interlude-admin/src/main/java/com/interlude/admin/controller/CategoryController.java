package com.interlude.admin.controller;

import com.interlude.entity.po.CategoryInfo;
import com.interlude.entity.query.CategoryInfoQuery;
import com.interlude.entity.vo.ResponseVO;
import com.interlude.service.CategoryInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;  
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/category")
@Validated
public class CategoryController extends ABaseController{

    @Resource
    private CategoryInfoService categoryInfoService;

    @RequestMapping("loadCategoryInfo")
    public ResponseVO loadCategoryInfo(CategoryInfoQuery query) {
        query.setOrderBy("sort asc");
        query.setConvert2Tree(true);
        List<CategoryInfo> listByParam = categoryInfoService.findListByParam(query);
        return getSuccessResponseVO(listByParam);
    }

    @RequestMapping("saveCategory")
    public ResponseVO saveCategory(@NotNull String categoryCode,
                                   @NotNull String categoryName,
                                   @NotNull Integer pCategoryId,
                                   Integer categoryId) {
        CategoryInfo categoryInfo = new CategoryInfo();
        categoryInfo.setCategoryCode(categoryCode);
        categoryInfo.setCategoryName(categoryName);
        categoryInfo.setPCategoryId(pCategoryId);
        categoryInfo.setCategoryId(categoryId);
        categoryInfoService.saveCategory(categoryInfo);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/delCategory")
    public ResponseVO delCategory(@NotNull Integer categoryId){

        categoryInfoService.delCategory(categoryId);
        return getSuccessResponseVO(null);
    }
}
