package com.leyou.search.listener;


import com.leyou.common.constants.MQConstants;
import com.leyou.common.vo.PageResult;
import com.leyou.item.client.ItemClient;
import com.leyou.item.dto.SpuDTO;
import com.leyou.search.pojo.Goods;
import com.leyou.search.respository.GoodsRespository;
import com.leyou.search.service.SearchService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.leyou.common.constants.MQConstants.Exchange.ITEM_EXCHANGE_NAME;
import static com.leyou.common.constants.MQConstants.Queue.SEARCH_ITEM_DOWN;
import static com.leyou.common.constants.MQConstants.Queue.SEARCH_ITEM_UP;
import static com.leyou.common.constants.MQConstants.RoutingKey.ITEM_DOWN_KEY;
import static com.leyou.common.constants.MQConstants.RoutingKey.ITEM_UP_KEY;

@Component
public class ItemListener {

    @Autowired
    private GoodsRespository goodsRespository;

    @Autowired
    private SearchService searchService;
    @Autowired
    private ItemClient itemClient;


    /**
     * MQ向索引库里添加文件
     * @param id
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = SEARCH_ITEM_UP,durable = "true"),
            exchange = @Exchange(
                    value = ITEM_EXCHANGE_NAME,
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC
            ),
            key = {ITEM_UP_KEY}
    ))
    public void ItemUp(Long id){

//        添加索引文件
        SpuDTO spuDTO = itemClient.findSpuDTOBySpuId(id);
        Goods goods = searchService.buildGoods(spuDTO);
        goodsRespository.save(goods);
    }


    /**
     * MQ删除索引库文件
     * @param id
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = SEARCH_ITEM_DOWN,durable = "true"),
            exchange = @Exchange(
                    value = ITEM_EXCHANGE_NAME,
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC
            ),
            key = {ITEM_DOWN_KEY}
    ))
    public void ItemDown(Long id){
//        删除索引文件

        goodsRespository.deleteById(id);
    }

}
