package com.leyou.common.auth.test;

import com.leyou.common.auth.entity.Payload;
import com.leyou.common.auth.entity.UserInfo;
import com.leyou.common.auth.utils.JwtUtils;
import com.leyou.common.auth.utils.RsaUtils;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

public class AuthTest {

    private String publicKeyFilename="F:\\class-103-1\\ssh\\id_rsa.pub";
    private String privateKeyFilename="F:\\class-103-1\\ssh\\id_rsa";


    /**
     * 测试生成公钥和私钥
     * @throws Exception
     */
    @Test
    public void generatePublickeyAndprivateKey()throws Exception{
        RsaUtils.generateKey(publicKeyFilename,privateKeyFilename,"class_103",2048);


    }


    /**
     * 测试生成token   用私钥生成
     */
    @Test
    public void generateToken()throws Exception{

        UserInfo userInfo = new UserInfo(100L,"张三","VIP");

        PrivateKey privateKey = RsaUtils.getPrivateKey(privateKeyFilename);
        String token = JwtUtils.generateTokenExpireInMinutes(userInfo, privateKey, 1);
        System.out.println(token);

    }

    /**
     * 解密token从token中获取用户信息
     * @throws Exception
     */
    @Test
    public void getUserInfoFromToken()throws Exception{

        String token = "eyJhbGciOiJSUzI1NiJ9.eyJ1c2VyIjoie1wiaWRcIjoxMDAsXCJ1c2VybmFtZVwiOlwi5byg5LiJXCIsXCJyb2xlXCI6XCJWSVBcIn0iLCJqdGkiOiJOV0kyTjJZME16RXROVFprTlMwME1tVmpMV0V6Wm1ZdE16WTBOV1F3TmpRMVpXVmoiLCJleHAiOjE1NzEyOTk2Mjh9.ABcHNE2ZYUpFpCvxGxe5aEKzEyGOjsmy57Oe1P6d-NRh2T4Wng_X5fUUeQq4ggMMxbUIUJihiiml30Gap5Ek2dLcoNRdafJ-U3MX0N8mm5QkIRAVMrrwYZGYVitjRuclhRhSTNrgOWwYCaFX3TMw13T1y6cnrG83CZHnmPYwLt-hCvFggQSKHE4i4aE3ksWz7_mWDKoUhb8SjDjUhE_OAg9AZAEBEsNUjqgDOFEysInZ63WgpaF_dTSDe4QnYK-4ViuZhDbpy8C8MPz-r_sgpKqlf3a9FfgkRG6XPaldHwAVjNIuEwNcoqmyafxF7QAuYOHIlL8WIK0Cw3p0ifQNQw";
        PublicKey publicKey = RsaUtils.getPublicKey(publicKeyFilename);
        Payload<UserInfo> payload = JwtUtils.getInfoFromToken(token, publicKey,UserInfo.class);
        UserInfo userInfo = payload.getUserInfo();
        System.out.println(userInfo);

    }
}
