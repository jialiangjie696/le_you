package com.leyou.item.controller;


import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.BrandDTO;
import com.leyou.item.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/brand")
public class BrandController {


    @Autowired
    private BrandService brandService;


    @GetMapping(value = "/page",name = "分类查询品牌")
    public ResponseEntity<PageResult<BrandDTO>> findBrandByPage(

          @RequestParam(value = "page",defaultValue = "1")  Integer page,  //当前页
          @RequestParam(value = "rows",defaultValue = "10")   Integer rows,//每页大小
          @RequestParam(value = "key",required = false) String key, // 搜索的关键字
          @RequestParam(value = "sortBy",required = false)  String sortBy,//排序字段
          @RequestParam(value = "desc",defaultValue = "false")  boolean desc //是否为降序

    ){


        return ResponseEntity.ok(brandService.findBrandByPage(page, rows, key, sortBy, desc));

    }


    @PostMapping(name = "新增品牌")
    public ResponseEntity<Void> saveBrand(BrandDTO brand, @RequestParam("cids")List<Long> cids){
            brandService.saveBrand(brand,cids);
            return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    /**
     *修改品牌
     * @param brandDTO
     * @param cids
     * @return
     */
    @PutMapping
    public ResponseEntity<Void> update(BrandDTO brandDTO, @RequestParam("cids")List<Long> cids){
        brandService.update(brandDTO,cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(value = "/of/category",name = "根据分类id查询品牌数据")
    public ResponseEntity<List<BrandDTO>> findBrandByCategoryId(@RequestParam("id") Long id){
        List<BrandDTO> brandDTOList = brandService.findBrandByCategoryId(id);
        return ResponseEntity.ok(brandDTOList);
    }


    /**
     * 根据brandids查询brand对象集合
     * @param ids
     * @return
     */
    @GetMapping(value = "/list",name = "根据分类id集合查询品牌对象集合")
    public ResponseEntity<List<BrandDTO>> findBrandByBrandIds(@RequestParam("ids") List<Long> ids){
        List<BrandDTO> brandDTOList = brandService.findBrandByBrandIds(ids);
        return ResponseEntity.ok(brandDTOList);
    }



}
