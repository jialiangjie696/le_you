package com.leyou.item.mapper;

import com.leyou.common.mapper.LyBaseMapper;
import com.leyou.item.entity.Category;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.common.IdsMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CategoryMapper extends LyBaseMapper<Category> {



    @Select("select c.* from tb_category c,tb_category_brand cb where c.id=cb.category_id and cb.brand_id=#{id}")
    List<Category> findCategoryByBrandId(Long id);
}
