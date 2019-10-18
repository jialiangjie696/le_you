package com.leyou.sms.listener;

import com.leyou.common.constants.MQConstants;
import com.leyou.sms.Utils.SmsHelper;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.leyou.common.constants.MQConstants.Exchange.SMS_EXCHANGE_NAME;
import static com.leyou.common.constants.MQConstants.Queue.SMS_CODE_QUEUE;
import static com.leyou.common.constants.MQConstants.RoutingKey.ITEM_UP_KEY;
import static com.leyou.common.constants.MQConstants.RoutingKey.SMS_CODE_KEY;

@Component
public class SmsListener {



    @Autowired
    private SmsHelper smsHelper;


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = SMS_CODE_QUEUE,durable = "true"),
            exchange = @Exchange(
                    name = SMS_EXCHANGE_NAME,
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC
            ),
            key = {SMS_CODE_KEY}
    ))
    public void sendSms(Map<String ,String> map){
        String phone = map.get("phone");
        String signName = map.get("signName");
        String templateCode = map.get("TemplateCode");
        String templateParam = map.get("TemplateParam");

//        String phone,String signName,String templateCode,String templateParam

        smsHelper.sendSms(phone,signName,templateCode,templateParam);
    }
}
