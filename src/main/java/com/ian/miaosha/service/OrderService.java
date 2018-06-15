package com.ian.miaosha.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ian.miaosha.dao.OrderDao;
import com.ian.miaosha.domain.MiaoshaOrder;
import com.ian.miaosha.domain.MiaoshaUser;
import com.ian.miaosha.domain.OrderInfo;
import com.ian.miaosha.vo.GoodsVo;

@Service
public class OrderService {
	
	@Autowired
	OrderDao orderDao;

	public MiaoshaOrder getMiaoshaOrderByUserIdAndGoodsId(long id, long goodsId) {
		return orderDao.getMiaoshaOrderByUserIdAndGoodsId(id, goodsId);
	}

	@Transactional
	public OrderInfo createOrder(MiaoshaUser user, GoodsVo goods) {
		
		OrderInfo orderInfo = new OrderInfo();
		orderInfo.setCreateDate(new Date());
		orderInfo.setDeliveryAddrId(0L);
		orderInfo.setGoodsCount(1);
		orderInfo.setGoodsId(goods.getId());
		orderInfo.setGoodsName(goods.getGoodsName());
		orderInfo.setGoodsPrice(goods.getMiaoshaPrice());
		orderInfo.setOrderChannel(1); // 最好用枚举类型
		orderInfo.setUserId(user.getId());
		orderInfo.setStatus(0); // 最好用枚举类型
		long orderId = orderDao.insert(orderInfo);
		
		MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
		miaoshaOrder.setGoodsId(goods.getId());
		miaoshaOrder.setOrderId(orderId);
		miaoshaOrder.setUserId(user.getId());
		orderDao.insertMiaoshaOrder(miaoshaOrder);
		
		return orderInfo;
	}

	
}
