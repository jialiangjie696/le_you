package com.leyou.search.test;


import com.leyou.common.vo.PageResult;
import com.leyou.item.client.ItemClient;
import com.leyou.item.dto.SkuDTO;

import com.leyou.item.dto.SpuDTO;
import com.leyou.search.pojo.Goods;
import com.leyou.search.respository.GoodsRespository;
import com.leyou.search.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SearchTest {

@Autowired
private ElasticsearchTemplate esTemplate;

   /* @Test
    public void test(){
        esTemplate.createIndex(Goods.class);
        esTemplate.putMapping(Goods.class);
    }*/



   @Autowired
   private GoodsRespository respository;


   @Autowired
   private ItemClient itemClient;



   @Autowired
   private SearchService searchService;

   @Test
   public void initData(){

//       像索引库中保存Goods

//       查询所有上架spu的数据

       int page = 1;
       while (true){  //因为数据量特别大所以我们循环查询
           PageResult<SpuDTO> pageResult = itemClient.findSpuByPage(1, 100, null, true);
           List<SpuDTO> spuDTOList = pageResult.getItems();


//           什么时候跳出循环
           if (CollectionUtils.isEmpty(spuDTOList)){
                break;
           }
           for (SpuDTO spuDTO : spuDTOList) {
               Goods goods = searchService.buildGoods(spuDTO);

               respository.save(goods);
           }

           page++;

       }


   }
}
