package com.leyou.item.mapper;

import com.leyou.common.mapper.LyBaseMapper;
import com.leyou.item.dto.BrandDTO;
import com.leyou.item.entity.Brand;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandMapper extends LyBaseMapper<Brand> {

    void insertCategoryBrand(@Param("cids") List<Long> cids ,@Param("bid") Long bid);

    @Select("select b.* from tb_brand b ,tb_category_brand cb where b.id=cb.brand_id and cb.category_id=#{id}")
    List<Brand> findBrandByCategoryId(@Param("id") Long id);
}
