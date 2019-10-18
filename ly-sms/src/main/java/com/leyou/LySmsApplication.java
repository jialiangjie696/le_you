package com.leyou;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class LySmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(LySmsApplication.class,args);
    }
}
