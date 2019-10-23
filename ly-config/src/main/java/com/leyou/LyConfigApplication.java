package com.leyou;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer  //开启了配置中心
public class LyConfigApplication {

    public static void main(String[] args) {
        SpringApplication.run(LyConfigApplication.class,args);
    }

}
