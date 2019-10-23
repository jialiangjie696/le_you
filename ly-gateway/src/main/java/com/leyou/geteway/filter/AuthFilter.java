package com.leyou.geteway.filter;

import com.leyou.common.auth.entity.Payload;
import com.leyou.common.auth.entity.UserInfo;
import com.leyou.common.auth.utils.JwtUtils;

import com.leyou.common.utlis.CookieUtils;
import com.leyou.geteway.config.FilterProperties;
import com.leyou.geteway.config.JwtProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.sun.xml.internal.bind.v2.TODO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.FORM_BODY_WRAPPER_FILTER_ORDER;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

@Component
public class AuthFilter extends ZuulFilter {


    @Autowired
    private FilterProperties filterProperties;


    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FORM_BODY_WRAPPER_FILTER_ORDER-1;
    }

    @Override
    public boolean shouldFilter() {
        List<String> allowPaths =  filterProperties.getAllowPaths(); //不用校验token的方法
/*              - /api/auth/login
                - /api/search
                - /api/user/register
                - /api/user/check
                - /api/user/code
                - /api/item*/
//        判断请求中是否带有token 的参数
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String requestURI = request.getRequestURI();//当前的请求地址  /api/auth/verify   /api/search?page=1
//        api/user/check/zhangsan/2
        for (String allowPath : allowPaths) {
            if( requestURI.startsWith(allowPath)){
                return false;  //不需要校验  放行
            }
        }

        return true;  //需要校验
    }

    @Autowired
    private JwtProperties jwtProperties;
    @Override
    public Object run() throws ZuulException {
//        判断请求中是否带有token 的参数
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String token = CookieUtils.getCookieValue(request, jwtProperties.getUser().getCookieName());
        try {
            Payload<UserInfo> payload = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey(), UserInfo.class);
            UserInfo userInfo = payload.getUserInfo();
            //获取用户的Id
            Long userId = userInfo.getId();
            ctx.addZuulRequestHeader("USER_ID",userId.toString());

            String role = userInfo.getRole();
//            TODO 判断角色
        } catch (Exception e) {
//            原因有：token为空  token是伪造的
            e.printStackTrace();
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(HttpStatus.FORBIDDEN.value());//返回一个状态码
        }
        return null;//表示正常 进入到微服务了
    }
}
