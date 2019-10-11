package com.leyou.item.client;

import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "item-service")
public interface ItemClient {

    @GetMapping(value = "/spu/page",name = "分页查询spu列表数据")
    public PageResult<SpuDTO> findSpuByPage(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5")  Integer rows,
            @RequestParam(value = "key", required = false)   String key,
            @RequestParam(value = "saleable", required = false)  Boolean saleable
    );

    @PostMapping(value = "/goods",name = "保存商品")
    public void saveGoods(@RequestBody SpuDTO spuDTO);


    @PutMapping(value = "/spu/saleable",name = "商品上下架")
    public void updateSaleable(
            @RequestParam("id") Long id,
            @RequestParam("saleable")  Boolean saleable
    );


    @GetMapping(value = "/spu/detail",name = "根据spuId查询SPUDetail")
    public  SpuDetailDTO  findSpuDetailBySpuId(@RequestParam("id") Long id);

    @GetMapping(value = "/sku/of/spu",name = "根据spuId查询sku集合")
    public  List<SkuDTO>  findSkuDTOListBySpuId(@RequestParam("id") Long id);

    @PutMapping(value = "/goods",name = "修改商品")
    public void  updateGoods(@RequestBody SpuDTO spuDTO);


    /**
     * 跟规格组id或分类id查询规格参数
     * @param
     * @return
     */
    @GetMapping("/spec/params")
    public List<SpecParamDTO>  findSpecParamByCategoryIdOrGroupId(
            @RequestParam(value = "cid",required = false) Long cid,
            @RequestParam(value = "gid",required = false) Long gid,
            @RequestParam(value = "searching",required = false) Boolean searching
    );

    @GetMapping(value = "/category/list",name = "根据分类id集合查询分类对象集合")
//    @CrossOrigin(origins = "http://manage.leyou.com")  //跨域资源共享   声明此方法可以有哪些网站可以进入
    public List<CategoryDTO> findCategoryByCids(@RequestParam(name = "ids") List<Long> ids);


    /**
     * 根据brandids查询brand对象集合
     * @param ids
     * @return
     */
    @GetMapping(value = "/brand/list",name = "根据分类id集合查询品牌对象集合")
    public List<BrandDTO> findBrandByBrandIds(@RequestParam("ids") List<Long> ids);

}
