package com.leyou.page.test;


import com.leyou.common.vo.PageResult;
import com.leyou.item.client.ItemClient;
import com.leyou.item.dto.SpuDTO;
import com.leyou.page.service.PageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


@SpringBootTest
@RunWith(SpringRunner.class)
public class PageTest {

    //随便给某个商品生成静态页面

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private ItemClient itemClient;

    @Autowired
    private PageService pageService;

    @Test
    public void testCreateHtml(){

        List<Long> longList = Arrays.asList(141L, 88L, 145L);
        longList.forEach(spuId->{
            Map map = pageService.loadItemData(spuId);
            Context context = new Context();
            context.setVariables(map);//把数据放入上下文

            FileWriter filterWriter = null;
            try {
                filterWriter = new FileWriter("F:\\sort\\nginx\\nginx-1.13.12\\nginx-1.13.12\\html\\item\\"+spuId+".html");
            } catch (IOException e) {
                e.printStackTrace();
            }
//        参数说明:  p1模板名称  p2 上下文 (数据)  p3 静态页面输出的路径
            templateEngine.process("item",context,filterWriter);

        });

    }

    @Test
    public void testCreateAllHtml(){

        PageResult<SpuDTO> spuByPage = itemClient.findSpuByPage(1, 200, null, true);
        List<SpuDTO> items = spuByPage.getItems();
        //List<Long> longList = Arrays.asList(141L, 88L, 145L);
        items.forEach(item->{
            Map map = pageService.loadItemData(item.getId());
            Context context = new Context();
            context.setVariables(map);//把数据放入上下文

            FileWriter filterWriter = null;
            try {
                filterWriter = new FileWriter("F:\\sort\\nginx\\nginx-1.13.12\\nginx-1.13.12\\html\\item\\"+item.getId()+".html");
            } catch (IOException e) {
                e.printStackTrace();
            }
//        参数说明:  p1模板名称  p2 上下文 (数据)  p3 静态页面输出的路径
            templateEngine.process("item",context,filterWriter);

        });

    }
}
