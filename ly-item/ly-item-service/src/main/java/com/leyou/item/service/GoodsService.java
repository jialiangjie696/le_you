package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utlis.BeanHelper;
import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.SkuDTO;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.dto.SpuDetailDTO;
import com.leyou.item.entity.*;
import com.leyou.item.mapper.*;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsService {



    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private CategoryMapper categoryMapper;




    public PageResult<SpuDTO> findSpuByPage(Integer page, Integer rows, String key, Boolean saleable) {

//        构建条件
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        //关键字查询
        if (StringUtils.isNotBlank(key)){    //关键字查询
            criteria.andLike("name","%"+key+"%");

        }
//        是否上下架
        if (saleable!=null){
            criteria.andEqualTo("saleable",saleable);
        }

//        排序  按照更新时间
        example.setOrderByClause("update_time desc");  //排序字段的写是列名  不是字段名



        PageHelper.startPage(page,rows);

        List<Spu> spuList = spuMapper.selectByExample(example);
        PageInfo pageInfo = new PageInfo(spuList);

//        转换类型
        List<SpuDTO> spuDTOList = BeanHelper.copyWithCollection(spuList, SpuDTO.class);

//        把brandId和categoryIds转成名称
        spuDTOList.forEach(spuDTO -> {
            Long brandId = spuDTO.getBrandId();
//            根据brandid查询brand对象
            Brand brand = brandMapper.selectByPrimaryKey(brandId);

            spuDTO.setBrandName(brand.getName());
            List<Long> categoryIds = spuDTO.getCategoryIds();

//            根据一个id的集合查询
            List<Category> categoryList = categoryMapper.selectByIdList(categoryIds);
//            取集合中每个对象的名称  并且把名称用","分割
            String categoryName = categoryList.stream().map(Category::getName).collect(Collectors.joining("/"));
            spuDTO.setCategoryName(categoryName);

        });
        return new PageResult<SpuDTO>(pageInfo.getTotal(),spuDTOList) ;
    }





    @Autowired
    private SpuDetailMapper spuDetailMapper;


    @Autowired
    private SkuMapper skuMapper;

    /**
     * 保存商品   涉及到三张表
     *   tb_spu       tb_spudetail   tb_sku
     *
     * @param spuDTO
     */
    @Transactional
    public void saveGoods(SpuDTO spuDTO) {

        try {
//        tb_spu表    数据来自SpuDTO
            Spu spu = BeanHelper.copyProperties(spuDTO, Spu.class);

            spuMapper.insertSelective(spu);

//        tb_spudetail   数据来自  SpuDTO.getSpudetail
            SpuDetail spuDetail = BeanHelper.copyProperties(spuDTO.getSpuDetail(), SpuDetail.class);
            spuDetail.setSpuId(spu.getId());
            spuDetailMapper.insertSelective(spuDetail);


//        tb_sku    数据来自SpuDTO.getskus
            List<SkuDTO> skuDTOList = spuDTO.getSkus();
            List<Sku> skuList = BeanHelper.copyWithCollection(skuDTOList, Sku.class);
            for (Sku sku : skuList) {
                sku.setSpuId(spu.getId());
                skuMapper.insertSelective(sku);
            }
        } catch (Exception e) {
            throw  new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }


    }

    /**
     * 商品的上下架
     *     修改saleable的状态
     * @param id
     * @param saleable
     */
    public void updateSaleable(Long id, Boolean saleable) {

        Spu spu = new Spu();
        spu.setId(id);
        spu.setSaleable(saleable);
//        根据id更改saleable
        spuMapper.updateByPrimaryKeySelective(spu);



    }


    /**
     * 商品修改时数据的回显方法1
     * @param id
     * @return
     */
    public SpuDetailDTO findSpuDetailDTOBySpuId(Long id) {

        SpuDetail spuDetail = spuDetailMapper.selectByPrimaryKey(id);

        if (spuDetail==null){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        SpuDetailDTO spuDetailDTO = BeanHelper.copyProperties(spuDetail, SpuDetailDTO.class);
        return spuDetailDTO;
    }


    /**
     * 商品修改时数据的回显方法2
     * @param id
     * @return
     */
    public List<SkuDTO> findSkuDTOListBySpuId(Long id) {


        Sku sku = new Sku();
        sku.setSpuId(id);
        List<Sku> skuList = skuMapper.select(sku);
        if (CollectionUtils.isEmpty(skuList)){

            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);

        }
        List<SkuDTO> skuDTOList = BeanHelper.copyWithCollection(skuList, SkuDTO.class);

        return skuDTOList;

    }

    /**
     * 商品的修改
     *    涉及到spu的更新
     *    涉及到spudetail的更新
     *    涉及到sku的删除  以及修改
     *
     *
     * @param spuDTO
     */
    public void updateGoods(SpuDTO spuDTO) {



        try {
//        tb_spu表    数据来自SpuDTO
            Spu spu = BeanHelper.copyProperties(spuDTO, Spu.class);

            spuMapper.updateByPrimaryKeySelective(spu);

//        tb_spudetail   数据来自  SpuDTO.getSpudetail
            SpuDetail spuDetail = BeanHelper.copyProperties(spuDTO.getSpuDetail(), SpuDetail.class);

            spuDetailMapper.updateByPrimaryKeySelective(spuDetail);

//            先删除后新增
            Sku sku1 = new Sku();
            sku1.setSpuId(spu.getId());
            skuMapper.delete(sku1);

//        tb_sku    数据来自SpuDTO.getskus
            List<SkuDTO> skuDTOList = spuDTO.getSkus();
            List<Sku> skuList = BeanHelper.copyWithCollection(skuDTOList, Sku.class);
            for (Sku sku : skuList) {
                sku.setSpuId(spu.getId());
                skuMapper.insertSelective(sku);
            }
        } catch (Exception e) {
            throw  new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }




    }
}