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

    /**
     * 根据品牌id查询分类的数据
     * 表  select c.* tb_category c,tb_category_brand  cb  where c.id=cb.category_id and cb.brand_id=?
     * @param id
     * @return
     */

    public List<CategoryDTO> findCategoryByBrandId(Long id) {




        List<Category> categoryList = categoryMapper.findCategoryByBrandId(id);

        if (CollectionUtils.isEmpty(categoryList)){
            throw new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }

        return BeanHelper.copyWithCollection(categoryList,CategoryDTO.class);

    }


    /**
     * 根据categoryIds查询对象
     *
     * @param ids
     * @return
     */
    public List<CategoryDTO> findCategoryByCids(List<Long> ids) {

        List<Category> categoryList = categoryMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(categoryList)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }

        return  BeanHelper.copyWithCollection(categoryList, CategoryDTO.class);
    }
}
