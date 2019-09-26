package com.leyou.geteway.config;

import com.leyou.geteway.CORSProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class GlobalCORSConfig {

    @Autowired
    private CORSProperties corsProperties;

    @Bean
    public CorsFilter corsFilter() {
        //1.添加CORS配置信息
        CorsConfiguration config = new CorsConfiguration();
        //1) 允许的域,不要写*，否则cookie就无法使用了
//        config.addAllowedOrigin("http://manage.leyou.com");
//        config.addAllowedOrigin("http://item.leyou.com");
//        config.addAllowedOrigin("http://cart.leyou.com");
//        config.addAllowedOrigin("http://pay.leyou.com");
        List<String> allowedOrigins = corsProperties.getAllowedOrigins();
//        for (String allowedOrigin : allowedOrigins) {
//            config.addAllowedOrigin(allowedOrigin);
//        }


        //2) 是否发送Cookie信息
//        config.setAllowCredentials(true);
        allowedOrigins.forEach(config::addAllowedOrigin);




















        //3) 允许的请求方式
//        config.addAllowedMethod("OPTIONS");
//        config.addAllowedMethod("HEAD");
//        config.addAllowedMethod("GET");
//        config.addAllowedMethod("PUT");
//        config.addAllowedMethod("POST");
//        config.addAllowedMethod("DELETE");
//        List<String> allowedMethods = corsProperties.getAllowedMethods();

        corsProperties.getAllowedMethods().forEach(config::addAllowedMethod);











        // 4）允许的头信息
//        config.addAllowedHeader("*");

        config.setAllowedHeaders(corsProperties.getAllowedHeaders());












        // 5）有效期
//        config.setMaxAge(360000L);
//        config.setMaxAge(corsProperties.getMaxAge());










        //2.添加映射路径，我们拦截一切请求
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration(corsProperties.getFilterPath(), config);



        //3.返回新的CORSFilter.
        return new CorsFilter(configSource);
    }
}