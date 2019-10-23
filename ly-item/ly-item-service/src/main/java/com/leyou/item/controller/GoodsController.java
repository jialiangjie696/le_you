package com.leyou.item.controller;

import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.SkuDTO;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.dto.SpuDetailDTO;
import com.leyou.item.entity.SpuDetail;
import com.leyou.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * spu和sku共用一个controller
 */

@RestController
public class GoodsController {


    @Autowired
    private GoodsService goodsService;

    @GetMapping(value = "/spu/page",name = "分页查询spu列表数据")
    public ResponseEntity<PageResult<SpuDTO>> findSpuByPage(
          @RequestParam(value = "page",defaultValue = "1") Integer page,
          @RequestParam(value = "rows",defaultValue = "5") Integer rows,
          @RequestParam(value = "key",required = false) String key,
          @RequestParam(value = "saleable",required = false)  Boolean saleable


    ){

        PageResult<SpuDTO> spuByPage = goodsService.findSpuByPage(page,rows,key,saleable);

      return ResponseEntity.ok(spuByPage);
    }


    @PostMapping(value = "/goods",name = "保存商品")
    public ResponseEntity<Void> saveGoods(@RequestBody SpuDTO spuDTO){


        goodsService.saveGoods(spuDTO);
        return ResponseEntity.ok().build();
    }


    @PutMapping(value = "/spu/saleable",name = "商品上下架")
    public ResponseEntity<Void> updateSaleable(

            @RequestParam("id") Long id,
            @RequestParam("saleable") Boolean saleable

    ){

        goodsService.updateSaleable(id,saleable);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/spu/detail",name = "商品修改时回显数据")
    public ResponseEntity<SpuDetailDTO> findSpuDetailDTOBySpuId(

            @RequestParam("id") Long id

    ){


      SpuDetailDTO spuDetailDTO =   goodsService.findSpuDetailDTOBySpuId(id);
        return ResponseEntity.ok(spuDetailDTO);
    }

    @GetMapping(value = "/sku/of/spu",name = "商品修改时回显数据")
    public ResponseEntity<List<SkuDTO>> findSkuDTOListBySpuId(

            @RequestParam("id") Long id

    ){


        List<SkuDTO> skuDTOList =   goodsService.findSkuDTOListBySpuId(id);
        return ResponseEntity.ok(skuDTOList);
    }


    @PutMapping(value = "/goods",name = "商品的修改")
    public ResponseEntity<Void> updateGoods(@RequestBody SpuDTO spuDTO){


        goodsService.updateGoods(spuDTO);
        return ResponseEntity.ok().build();
    }


    @GetMapping(value = "/spu/{id}",name = "根据spuId查询spu对象")
    public ResponseEntity<SpuDTO> findSpuDTOBySpuId(@PathVariable("id") Long id){


       SpuDTO spuDTO =  goodsService.findSpuDTOBySpuId(id);

        return ResponseEntity.ok(spuDTO);

    }

    @GetMapping(value = "/sku/list",name = "根据skuid的集合获取sku集合的信息")
    public ResponseEntity<List<SkuDTO>> findSkusBySkuids(@RequestParam("ids")List<Long> ids){



       List<SkuDTO> skuDTOS =  goodsService.findSkusBySkuids(ids);

        return ResponseEntity.ok(skuDTOS);

    }

    @PutMapping(value = "/stock/minus",name = "根据skuId减库存")
    public ResponseEntity<Void> minusStock(@RequestBody Map<Long ,Integer> skuNumMap){
        goodsService.minusStock(skuNumMap);
        return ResponseEntity.ok().build();
    }
}
