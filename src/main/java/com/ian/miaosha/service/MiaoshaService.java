package com.ian.miaosha.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ian.miaosha.domain.MiaoshaUser;
import com.ian.miaosha.domain.OrderInfo;
import com.ian.miaosha.vo.GoodsVo;

@Service
public class MiaoshaService {

	@Autowired // 一般要导入其他表的service 而不是 dao
	GoodsService goodsService;
	
	@Autowired
	OrderService orderService;
	
	/**
	 * 减库存 下订单 写入秒杀订单 必须放在事务中 成功失败一起 原子操作
	 * @param user
	 * @param goods
	 * @return
	 */
	@Transactional
	public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods) {
		
		//减库存 
		goodsService.reduceStock(goods);
		
		//下订单 
		//写入order_info, miaosha_order
		return orderService.createOrder(user, goods);
	}

	
}
