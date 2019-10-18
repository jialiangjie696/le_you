package com.leyou.auth.service;



import com.leyou.auth.config.JwtProperties;
import com.leyou.common.auth.entity.Payload;
import com.leyou.common.auth.entity.UserInfo;
import com.leyou.common.auth.utils.JwtUtils;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utlis.CookieUtils;
import com.leyou.user.clent.UserClent;
import com.leyou.user.dto.UserDTO;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class Authservice {


    @Autowired
    private UserClent userClent;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 用户登录跳转页面
     * @param username
     * @param password
     */
    public void login(String username, String password, HttpServletResponse response) {
//         1、根据用户名和密码从用户中心中查询用户信息
        try {
            UserDTO userDTO = userClent.queryByUserNameAndPassword(username, password);
            if(userDTO==null){
                throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
            }
//         2、jwt加密
            UserInfo userInfo = new UserInfo(userDTO.getId(),userDTO.getUsername(),"guest");
//            PrivateKey privateKey = RsaUtils.getPrivateKey(jwtProperties.getPriKeyPath());
            putTokenToCookie(response, userInfo, 30);
        } catch (Exception e) {
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }

    }


    /**
     * 获取用户信息     从token中获取用户信息UserInfo
     * @param request
     * @return
     */
    public UserInfo verifyUser(HttpServletRequest request,HttpServletResponse response) {

        try {


//        1.token怎么获取      从cookie中获取
            String token_cookie = CookieUtils.getCookieValue(request, jwtProperties.getUser().getCookieName());
//            PublicKey publicKey = RsaUtils.getPublicKey(jwtProperties.getPubKeyPath());
            Payload<UserInfo> payload = JwtUtils.getInfoFromToken(token_cookie, jwtProperties.getPublicKey(),UserInfo.class);

//            判断token是否失效
            Boolean key = redisTemplate.hasKey(payload.getId());
            if (key){  //为true   表示redis中有此数据

                throw new LyException(ExceptionEnum.UNAUTHORIZED);
            }
            UserInfo userInfo = payload.getUserInfo();
            //cookie的失效时间
            Date expiration = payload.getExpiration();
            Date nowDate = new Date();//当前时间
//            如果发现token即将失效(时间剩余10分钟),重新生成token    即将失效时间-10分钟<=当前时间
            DateTime dateTime = new DateTime().minusMinutes(10);//即将失效的时间-10分钟的结果
            if (dateTime.isBeforeNow()){//即将失效时间-10是小于当前时间
                //重新生成token
                putTokenToCookie(response, userInfo, jwtProperties.getUser().getExpire());
            }




            return userInfo;
        } catch (Exception e) {
            e.printStackTrace();
            throw new LyException(ExceptionEnum.UNAUTHORIZED);
        }
    }


    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 退出登录    从数据的本质上来说就是删除cookie
     * @param request
     * @param response
     */
    public void logout(HttpServletRequest request, HttpServletResponse response) {

        //从浏览器中获取token
        String token = CookieUtils.getCookieValue(request, jwtProperties.getUser().getCookieName());
//        解析token  拿出载荷
        Payload<UserInfo> payload = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey(), UserInfo.class);
        String payloadId = payload.getId();


        //删除cookie
        CookieUtils.deleteCookie(jwtProperties.getUser().getCookieName(),jwtProperties.getUser().getCookieDomain(),response);

        //把此token放到redis中表示失效
        redisTemplate.boundValueOps(payloadId).set("",30, TimeUnit.MINUTES);

    }

    /**
     * 生成token的代码
     * @param response
     * @param userInfo
     * @param i
     */
    private void putTokenToCookie(HttpServletResponse response, UserInfo userInfo, int i) {
        String token = JwtUtils.generateTokenExpireInMinutes(userInfo, jwtProperties.getPrivateKey(), i);
//         3、加密后的token放到cookie中
        CookieUtils.newCookieBuilder()
                .name(jwtProperties.getUser().getCookieName()) //cookie名称
                .value(token)   //cookie的值
                .httpOnly(true) //防止XSS攻击  ：可以使用js脚本篡改cookie信息
                .domain(jwtProperties.getUser().getCookieDomain())
                .response(response)
                .maxAge(60 * 60 * 24)  //cookie的存活时间
                .build();
    }




}
