package com.miaosha.service;

import com.miaosha.dao.GoodsDao;
import com.miaosha.domain.Goods;
import com.miaosha.domain.MiaoshaUser;
import com.miaosha.domain.OrderInfo;
import com.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class MiaoshaService {

    @Resource
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Transactional
    public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods) {
        //减少库存
        goodsService.reduceStock(goods);

        //下订单order_info,写入秒杀订单miaosha_order
        return orderService.createOrder(user,goods);
    }
}
