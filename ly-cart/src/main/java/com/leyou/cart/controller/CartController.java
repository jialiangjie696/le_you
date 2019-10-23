package com.leyou.cart.controller;


import com.leyou.cart.entity.Cart;
import com.leyou.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class CartController {


    @Autowired
     private CartService cartService;

    /**
     * 用户登录后添加购物车的方法
     * @param cart
     * @return
     */
    @PostMapping("/")
    public ResponseEntity<Void> addCart(@RequestBody Cart cart){

        cartService.addCart(cart);

        return ResponseEntity.ok().build();

    }

    /**
     * 查询redis中的购物车数据
     * @param
     * @param
     * @return
     */
    @GetMapping(value = "/list",name = "从redis中获取购物车数据")
    public ResponseEntity<List<Cart>> findCartFromRedis(){

       List<Cart>  cartList = cartService.findCartFromRedis();

        return ResponseEntity.ok(cartList);

    }


    /**
     * 合并登陆前的购物车和登陆后的购物车数据
     * @return
     */
    @PostMapping(value = "/list",name = "合并购物车数据")
    public ResponseEntity<Void> mergeCartList(@RequestBody List<Cart> cartList_store){


         cartService.mergeCartList(cartList_store);

        return ResponseEntity.ok().build();

    }

}
