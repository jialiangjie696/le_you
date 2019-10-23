package com.leyou.order.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utlis.BeanHelper;
import com.leyou.common.utlis.IdWorker;
import com.leyou.item.client.ItemClient;
import com.leyou.item.dto.SkuDTO;
import com.leyou.order.dto.CartDTO;
import com.leyou.order.dto.OrderDTO;
import com.leyou.order.entity.Order;
import com.leyou.order.entity.OrderDetail;
import com.leyou.order.entity.UserHolder;
import com.leyou.order.enums.OrderStatusEnum;
import com.leyou.order.mapper.OrderDetailMapper;
import com.leyou.order.mapper.OrderMapper;
import com.leyou.order.vo.OrderVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {



    @Autowired
    private IdWorker idWorker;

    @Autowired
    private ItemClient itemClient;
    @Autowired
    private OrderMapper  orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;

    /**
     * 下单
     * @param orderDTO
     * @return
     */
    public Long createOrder(OrderDTO orderDTO) {



        long orderId = idWorker.nextId();


//        1.订单表  tb_order
        Order order = new Order();
        //支付类型
        order.setPaymentType(orderDTO.getPaymentType());
//        订单来源,2是pc端
        order.setSourceType(2);
        order.setOrderId(orderId);
        order.setStatus(OrderStatusEnum.INIT.value());
        String userId = UserHolder.getUserId();
        if (StringUtils.isBlank(userId)){
            throw new LyException(ExceptionEnum.UNAUTHORIZED);
        }
        order.setUserId(Long.parseLong(userId));


//        总金额: 商品的总金额
         List<CartDTO> carts = orderDTO.getCarts();
//         商品的数量和skuid放入map集合
        Map<Long, Integer> skuNumMap = carts.stream().collect(Collectors.toMap(CartDTO::getSkuId, CartDTO::getNum));


//         获取商品skuId的集合
        List<Long> skuIdList = carts.stream().map(CartDTO::getSkuId).collect(Collectors.toList());

        List<SkuDTO> skuDTOList = itemClient.findSkusBySkuids(skuIdList);

        //计算商品的总金额
        Long total = 0L;
        for (SkuDTO sku : skuDTOList) {
            Long skuId = sku.getId();
            Integer num = skuNumMap.get(skuId);
            total+= sku.getPrice()*num;//每个商品的单价乘以数量

            //        2.订单详情表 tb_order_detail
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setSkuId(skuId);
            orderDetail.setNum(num);
            orderDetail.setOrderId(order.getOrderId());
            orderDetail.setTitle(sku.getTitle());
            orderDetail.setOwnSpec(sku.getOwnSpec());
            orderDetail.setPrice(sku.getPrice());
           String image =  sku.getImages().split(",")[0];//获取sku第一张图片的地址
           orderDetail.setImage(image);

           orderDetailMapper.insertSelective(orderDetail);


        }

        order.setTotalFee(total);
        order.setActualFee(total); //真是付款价格 = 商品的总价格+运费-优惠

        orderMapper.insertSelective(order);
//        2.订单详情表 tb_order_detail


//        3.物流表   TODO

//        4.减库存  调用itemClent


        itemClient.minusStock(skuNumMap);

        return orderId;
    }


    /**
     * 根据id查询订单的信息
     * @param id
     * @return
     */
    public OrderVO findById(Long id) {

        Order order = orderMapper.selectByPrimaryKey(id);


        OrderVO orderVO = BeanHelper.copyProperties(order,OrderVO.class);

        orderVO.setLogistics();


    }
}
