package com.leyou.item.service;


import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utlis.BeanHelper;
import com.leyou.item.dto.CategoryDTO;
import com.leyou.item.entity.Category;
import com.leyou.item.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    public List<CategoryDTO> findCategoryByparentId(Long pid) {


        Category category = new Category();
        category.setParentId(pid);  //设置属性根据查询
        List<Category> categoryList = categoryMapper.select(category);
        List<CategoryDTO> categoryDTOS = BeanHelper.copyWithCollection(categoryList, CategoryDTO.class);
        if (CollectionUtils.isEmpty(categoryDTOS)){   //判断是否集合为空
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }

        return categoryDTOS;
    }
}
