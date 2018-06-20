package com.ian.miaosha.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ian.miaosha.domain.MiaoshaOrder;
import com.ian.miaosha.domain.MiaoshaUser;
import com.ian.miaosha.service.GoodsService;
import com.ian.miaosha.service.MiaoshaService;
import com.ian.miaosha.service.OrderService;
import com.ian.miaosha.service.RedisService;
import com.ian.miaosha.vo.GoodsVo;

@Service
public class MQReceiver {

	private static Logger log = LoggerFactory.getLogger(MQReceiver.class);
	
	@Autowired
	GoodsService goodsService;
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	MiaoshaService miaoshaService;
	
	@RabbitListener(queues=MQConfig.MIAOSHA_QUEUE)
	public void receive(String message) {
		log.info("receive message: " + message);
		MiaoshaMessage miaoshaMessage = RedisService.stringToBean(message, MiaoshaMessage.class);
		MiaoshaUser user = miaoshaMessage.getUser();
		long goodsId = miaoshaMessage.getGoodsId();
		GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
		int stock = goods.getStockCount();
		// 判断是否有库存
		if (stock <= 0) {
			return;
		}
		// 判断是否秒杀到了
		MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdAndGoodsId(user.getId(), goodsId);
		if (order != null) {
			return;
		}
		
		//减库存 下订单 写入秒杀订单 必须放在事务中 成功失败一起 原子操作
		miaoshaService.miaosha(user, goods);
	}
	
	/*@RabbitListener(queues=MQConfig.QUEUE)
	public void receive(String message) {
		log.info("Received message: " + message);
	}
	
	@RabbitListener(queues=MQConfig.TOPIC_QUEUE1)
	public void receiveTopic1(String message) {
		log.info("Received topic message1: " + message);
	}
	
	@RabbitListener(queues=MQConfig.TOPIC_QUEUE2)
	public void receiveTopic2(String message) {
		log.info("Received topic message2: " + message);
	}
	
	@RabbitListener(queues=MQConfig.HEADER_QUEUE)
	public void receiveHeaderQueue(byte[] message) {
		log.info("Header queue message: " + new String(message));
	}*/
	
}
