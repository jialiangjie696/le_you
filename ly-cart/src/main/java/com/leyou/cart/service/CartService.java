package com.leyou.cart.service;


import com.leyou.cart.entity.Cart;
import com.leyou.cart.entity.CartUser;
import com.leyou.cart.entity.UserHolder;
import com.leyou.common.utlis.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {


    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private String REFIX_KEY="ly:cart";



    /**
     * 用户登陆后添加购物车的方法
     * @param cart
     */
    public void addCart(Cart cart) {

        String skuId = cart.getSkuId().toString();

        //从ThreadLocal中获取userId
        String userId = UserHolder.getUserId();

        BoundHashOperations<String, String, String> hashOps = stringRedisTemplate.boundHashOps(REFIX_KEY + userId);
//        放入cart对象

//        原购物车中是否有此商品
        Boolean hasSku = hashOps.hasKey(userId);
        if (hasSku){
            //取出redis中的cart对象
            String cartString = hashOps.get(userId);
            Cart cartRedis = JsonUtils.toBean(cartString, Cart.class);
//            累加商品的数量
            cartRedis.setNum(cartRedis.getNum()+cart.getNum());
//            存入redis中
            hashOps.put(skuId,JsonUtils.toString(cartRedis));
        }else {//即将添加的商品,在redis中没有
            hashOps.put(skuId,JsonUtils.toString(cart));

        }




    }


    /**
     *查询购物车的数据
     * @return
     */
    public List<Cart> findCartFromRedis() {
        //从ThreadLocal中获取userId
        String userId = UserHolder.getUserId();

        BoundHashOperations<String, String, String> hashOps = stringRedisTemplate.boundHashOps(REFIX_KEY + userId);
        //获取所有的值  cart对象
        List<String> cartListJsonString = hashOps.values();

        List<Cart> cartList = cartListJsonString.stream().map(cartJsonString -> JsonUtils.toBean(cartJsonString, Cart.class)).collect(Collectors.toList());
        return cartList;


    }


    /**
     * 合并登陆前和登陆后的购物车数据
     * @param cartList_store
     */
    public void mergeCartList(List<Cart> cartList_store) {

        for (Cart cart : cartList_store) {
            this.addCart(cart);
        }

    }
}





