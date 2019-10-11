package com.leyou.item.controller;


import com.leyou.item.dto.SpecGroupDTO;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.item.entity.SpecParam;
import com.leyou.item.service.SpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/spec")
public class SpecCotroller {



    @Autowired
    private SpecService specService;


    /**
     * 根据分类id 查询规格组
     * @param id
     * @return
     */
    @GetMapping("/groups/of/category")
    public ResponseEntity<List<SpecGroupDTO>> findSpecGroupBycategoryId(@RequestParam("id") Long id){

       List<SpecGroupDTO> specGroupDTOList =  specService.findSpecGroupBycategoryId(id);

       return ResponseEntity.ok(specGroupDTOList);
    }


    /**
     * 根据规格组或分类id查询规格参数
     * @param gid
     * @return
     */
    @GetMapping("/params")
    public ResponseEntity<List<SpecParamDTO>> findSpecParamByCategoryIdOrGroupId(
            @RequestParam(value = "cid",required = false) Long cid,
            @RequestParam(value = "gid",required = false) Long gid,
            @RequestParam(value = "searching",required = false) Boolean searching

    ){

        List<SpecParamDTO> SpecParamDTOList =  specService.findSpecParamByCategoryIdOrGroupId(cid,gid,searching);

        return ResponseEntity.ok(SpecParamDTOList);
    }




}
