package com.leyou.page.service;


import com.leyou.item.client.ItemClient;
import com.leyou.item.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PageService {


    @Autowired
    private ItemClient itemClient;

    public Map loadItemData(Long spuId) {

        Map itemDatas = new HashMap();
//        查询spu
        SpuDTO spu = itemClient.findSpuDTOBySpuId(spuId);

        //brand   品牌对象
        Long brandId = spu.getBrandId();
        BrandDTO brand = itemClient.findBrandByBrandId(brandId);
        itemDatas.put("brand",brand);

        //spuName      商品名称
        String name = spu.getName();
        itemDatas.put("spuName",name);
        //subTitle         商品子标题
        itemDatas.put("subTitle",spu.getSubTitle());

        //categories      三级分类的对象集合
        List<Long> categoryIds = spu.getCategoryIds();
        List<CategoryDTO> categories = itemClient.findCategoryByCids(categoryIds);
        itemDatas.put("categories",categories);
        //detail  商品详情的对象

        SpuDetailDTO spuDetail = itemClient.findSpuDetailBySpuId(spuId);
        itemDatas.put("detail",spuDetail);

        //skus         spu下所有sku的集合
        List<SkuDTO> skuDTOList = itemClient.findSkuDTOListBySpuId(spuId);
        itemDatas.put("skus",skuDTOList);

        //specs               规格组的集合每个规格组带有规格参数的集合
        Long cid3 = spu.getCid3();
//        根据第三级分类查询规格组
        List<SpecGroupDTO> specGroupList = itemClient.findSpecGroupBycategoryId(cid3);

        //给group集合中的每一个对象setparams
        specGroupList.forEach(specGroup -> {
            List<SpecParamDTO> specParams = itemClient.findSpecParamByCategoryIdOrGroupId(null,specGroup.getId(), null);
            specGroup.setParams(specParams);
        });

        itemDatas.put("specs",specGroupList);

        return itemDatas;

    }


    @Autowired
    private TemplateEngine templateEngine;

    /**
     * 添加静态页面
     * @param spuId
     */
    public void CreateHtmlBySpuId(Long spuId){


            Map map = this.loadItemData(spuId);
            Context context = new Context();
            context.setVariables(map);//把数据放入上下文


            try {
                PrintWriter filterWriter = new PrintWriter("F:\\sort\\nginx\\nginx-1.13.12\\nginx-1.13.12\\html\\item\\"+spuId+".html");
                templateEngine.process("item",context,filterWriter);
            } catch (IOException e) {
                e.printStackTrace();
            }
//        参数说明:  p1模板名称  p2 上下文 (数据)  p3 静态页面输出的路径


    }

    /**
     * 删除静态页面的方法
     * @param spuId
     */
    public void deleteHtmlBySpuId(Long spuId) {
        File file = new File("F:\\sort\\nginx\\nginx-1.13.12\\nginx-1.13.12\\html\\item\\" + spuId + ".html");
        if (file.exists()){
            file.delete();
        }

    }
}
