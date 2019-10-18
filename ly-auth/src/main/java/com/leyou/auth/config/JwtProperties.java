package com.leyou.auth.config;

import com.leyou.common.auth.utils.RsaUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.PublicKey;

@Component
@Data
@Slf4j
@ConfigurationProperties("ly.jwt")
public class JwtProperties implements InitializingBean,BeanNameAware {


    private String pubKeyPath;

    private String priKeyPath;

    private UserProperties user;

    private PublicKey publicKey;

    private PrivateKey privateKey;


    public JwtProperties(){
        System.out.println("00000000000000");
    }
    @Override
    public void afterPropertiesSet()  {
        try {
            System.out.println("22222222222");
            privateKey = RsaUtils.getPrivateKey(priKeyPath);
            publicKey = RsaUtils.getPublicKey(pubKeyPath);
        } catch (Exception e) {
           log.error("公钥和私钥对象初始化失败");
        }
    }

    @Override
    public void setBeanName(String s) {

        System.out.println("11111111111");
    }
}
