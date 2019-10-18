package com.leyou.test;


import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.leyou.common.constants.MQConstants.Exchange.SMS_EXCHANGE_NAME;
import static com.leyou.common.constants.MQConstants.Queue.SMS_CODE_QUEUE;
import static com.leyou.common.constants.MQConstants.RoutingKey.SMS_CODE_KEY;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SmsTest {


    @Autowired
    private AmqpTemplate amqpTemplate;

    @Test
    public void test() throws InterruptedException {

        String s = RandomStringUtils.randomNumeric(4);
        Map<String ,String>map = new HashMap<>();
        map.put("phone","13311231597");
        map.put("signName","良杰旅游网");
        map.put("TemplateCode","SMS_171113243");
        map.put("TemplateParam","{\"code\":\""+s+"\"}");
        amqpTemplate.convertAndSend(SMS_EXCHANGE_NAME,SMS_CODE_KEY,map);
        System.out.println(s);

        Thread.sleep(10000L);
    }
}
