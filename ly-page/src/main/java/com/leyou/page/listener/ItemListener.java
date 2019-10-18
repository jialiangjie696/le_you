package com.leyou.page.listener;


import com.leyou.item.client.ItemClient;
import com.leyou.page.service.PageService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.leyou.common.constants.MQConstants.Exchange.ITEM_EXCHANGE_NAME;
import static com.leyou.common.constants.MQConstants.Queue.*;
import static com.leyou.common.constants.MQConstants.RoutingKey.ITEM_DOWN_KEY;
import static com.leyou.common.constants.MQConstants.RoutingKey.ITEM_UP_KEY;

@Component
public class ItemListener {


    @Autowired
    private PageService pageService;

    @Autowired
    private ItemClient itemClient;


    /**
     * MQ向索引库里添加静态页面
     * @param id
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = PAGE_ITEM_UP,durable = "true"),
            exchange = @Exchange(
                    value = ITEM_EXCHANGE_NAME,
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC
            ),
            key = {ITEM_UP_KEY}
    ))
    public void ItemUp(Long id){

//        添加静态页面
        pageService.CreateHtmlBySpuId(id);
    }


    /**
     * MQ删除静态页面
     * @param id
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = PAGE_ITEM_DOWN,durable = "true"),
            exchange = @Exchange(
                    value = ITEM_EXCHANGE_NAME,
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC
            ),
            key = {ITEM_DOWN_KEY}
    ))
    public void ItemDown(Long id){
//        删除静态页面

        pageService.deleteHtmlBySpuId(id);

    }

}
