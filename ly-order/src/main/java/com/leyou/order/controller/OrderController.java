package com.leyou.order.controller;


import com.leyou.order.dto.OrderDTO;
import com.leyou.order.service.OrderService;
import com.leyou.order.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {


    @Autowired
    private OrderService orderService;

    /**
     * 下单
     * @param
     * @return
     */
    @PostMapping("/order")
    public ResponseEntity<Long> createOrder(@RequestBody OrderDTO orderDTO){

       Long orderId =  orderService.createOrder(orderDTO);
       return ResponseEntity.ok().body(orderId);
    }

    @PostMapping(value = "/order/{id}",name = "根据id查询订单的信息")
    public ResponseEntity<OrderVO> findById(@PathVariable("id") Long id){

        OrderVO orderVO =  orderService.findById(id);
        return ResponseEntity.ok().body(orderVO);
    }

}
