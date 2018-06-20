package com.ian.miaosha.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ian.miaosha.domain.MiaoshaOrder;
import com.ian.miaosha.domain.MiaoshaUser;
import com.ian.miaosha.domain.OrderInfo;
import com.ian.miaosha.redis.MiaoshaKey;
import com.ian.miaosha.vo.GoodsVo;

@Service
public class MiaoshaService {

	@Autowired // 一般要导入其他表的service 而不是 dao
	GoodsService goodsService;
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	RedisService redisService;
	
	/**
	 * 减库存 下订单 写入秒杀订单 必须放在事务中 成功失败一起 原子操作
	 * @param user
	 * @param goods
	 * @return
	 */
	@Transactional
	public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods) {
		
		//减库存 
		boolean success = goodsService.reduceStock(goods);
		if (success) {
			//下订单 
			//写入order_info, miaosha_order
			return orderService.createOrder(user, goods);
		}else {
			setGoodsOver(goods.getId()); // 设置卖完标志
			return null;
		}
		
	}

	public long getMiaoshaResult(long userId, long goodsId) {
		MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdAndGoodsId(userId, goodsId);
		if (miaoshaOrder != null) { // 秒杀成功
			return miaoshaOrder.getOrderId();
		}else {
			boolean isOver = getGoodsOver(goodsId);
			if (isOver) {
				return -1; // 失败
			}else { 
				return 0; // 需要继续轮询
			}
		}
	}

	private void setGoodsOver(Long goodsId) {
		redisService.set(MiaoshaKey.isGoodsOver, ""+goodsId, true);
	}
	
	private boolean getGoodsOver(long goodsId) {
		return redisService.exists(MiaoshaKey.isGoodsOver, ""+goodsId);
	}

	
}
