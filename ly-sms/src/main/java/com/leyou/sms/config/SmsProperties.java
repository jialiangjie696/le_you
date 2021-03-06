package com.leyou.sms.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "ly.sms")
public class SmsProperties {



    private String accessKeyID;
    private String accessKeySecret;

}
