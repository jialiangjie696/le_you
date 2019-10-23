package com.leyou;


import com.leyou.geteway.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;



@SpringCloudApplication
@EnableZuulProxy
@EnableConfigurationProperties(JwtProperties.class)
public class LyGateway {

    public static void main(String[] args) {
        SpringApplication.run(LyGateway.class,args);
    }
}
