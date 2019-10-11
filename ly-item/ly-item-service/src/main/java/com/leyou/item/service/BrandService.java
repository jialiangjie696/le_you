package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utlis.BeanHelper;
import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.BrandDTO;
import com.leyou.item.entity.Brand;

import com.leyou.item.entity.CategoryBrand;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.mapper.CategoryBrandMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;



@Service
public class BrandService {


    @Autowired
    private BrandMapper  brandMapper;

    @Autowired
    private CategoryBrandMapper categoryBrandMapper;

    public PageResult<BrandDTO> findBrandByPage(Integer page, Integer rows, String key, String sortBy, boolean desc) {


        //过滤条件  关键字查询  select * from tb_brand where name like '%H%' or letter = 'H' order by name desc

        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(key)) {  //判断key不为空的情况下执行
            criteria.orLike("name", "%" + key + "%");
            criteria.orEqualTo("letter", key);
        }

//        sortBy 根据字段排序
//        example.setOrderByClause(sortBy+(desc? "desc":"asc"));  //字符串前加空格
        example.setOrderByClause(sortBy+(desc?" desc":" asc"));

        //分页
        PageHelper.startPage(page,rows);


        List<Brand> brands = brandMapper.selectByExample(example);
        PageInfo<Brand> pageInfo = new PageInfo<>(brands);

//        替换泛型
        List<BrandDTO> brandDTOList = BeanHelper.copyWithCollection(brands, BrandDTO.class);



        return new PageResult<>(pageInfo.getTotal(), brandDTOList);
    }


    @Transactional
    public void saveBrand(BrandDTO brandDto, List<Long> cids) {


//        转换泛型
        Brand brand = BeanHelper.copyProperties(brandDto, Brand.class);
//        实现商品插入

        brandMapper.insertSelective(brand);
//        插入分类中间表数据
        CategoryBrand categoryBrand = null;
        for (Long cid : cids) {
            categoryBrand =  new CategoryBrand(cid,brand.getId());
            categoryBrandMapper.insert(categoryBrand);
        }


    }


    /**
     * 品牌的修改     先删除以前的数据在重新赋值
     * @param brandDTO
     * @param cids
     */

    public void update(BrandDTO brandDTO, List<Long> cids) {

//        转换泛型
        Brand brand = BeanHelper.copyProperties(brandDTO, Brand.class);

        brandMapper.updateByPrimaryKeySelective(brand);
        Long bid = brand.getId();

        CategoryBrand categoryBrand = new CategoryBrand();
        categoryBrand.setBrandId(bid);
        categoryBrandMapper.delete(categoryBrand);

//        插入分类中间表数据
        for (Long cid : cids) {
            categoryBrand =  new CategoryBrand(cid,brand.getId());
            categoryBrandMapper.insert(categoryBrand);
        }


    }

//    根据cartegoryid查询品牌
    public List<BrandDTO> findBrandByCategoryId(Long id) {

        List<Brand> brandList= brandMapper.findBrandByCategoryId(id);
//        判断是否为空
        if (CollectionUtils.isEmpty(brandList)){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }

        return BeanHelper.copyWithCollection(brandList,BrandDTO.class);
    }

    /**
     * 根据品牌ids查询品牌对象集合
     * @param ids
     * @return
     */
    public List<BrandDTO> findBrandByBrandIds(List<Long> ids) {

        List<Brand> brandList = brandMapper.selectByIdList(ids);
        if (CollectionUtils.isEmpty(brandList)){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }

        return BeanHelper.copyWithCollection(brandList,BrandDTO.class);
    }
}
