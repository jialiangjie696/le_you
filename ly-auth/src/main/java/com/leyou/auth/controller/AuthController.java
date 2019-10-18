package com.leyou.auth.controller;


import com.leyou.auth.service.Authservice;
import com.leyou.common.auth.entity.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class AuthController {


    @Autowired
    private Authservice authservice;

    @PostMapping(value = "/login",name = "登录")
    public ResponseEntity<Void> login(@RequestParam("username") String username, @RequestParam("password")String password,
                                      HttpServletResponse response){

        authservice.login(username,password,response);

        return ResponseEntity.ok().build();

    }

    /**
     * 获取用户信息
     * @param request
     * @param response
     * @return
     */
    @GetMapping(value = "/verify",name = "获取用户信息")
    public ResponseEntity<UserInfo> verifyUser(HttpServletRequest request,HttpServletResponse response){

       UserInfo userInfo =  authservice.verifyUser(request,response);

        return ResponseEntity.ok(userInfo);

    }

    /**
     * 退出登录
     * @param
     * @param
     * @return
     */
    @PostMapping(value = "/logout",name = "退出登录")
    public ResponseEntity<Void> logout(HttpServletRequest request,HttpServletResponse response){

         authservice.logout(request,response);

        return ResponseEntity.ok().build();

    }

}
