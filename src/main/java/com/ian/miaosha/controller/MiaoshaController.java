package com.ian.miaosha.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ian.miaosha.domain.MiaoshaOrder;
import com.ian.miaosha.domain.MiaoshaUser;
import com.ian.miaosha.domain.OrderInfo;
import com.ian.miaosha.result.CodeMsg;
import com.ian.miaosha.result.Result;
import com.ian.miaosha.service.GoodsService;
import com.ian.miaosha.service.MiaoshaService;
import com.ian.miaosha.service.OrderService;
import com.ian.miaosha.vo.GoodsVo;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController {
	
	@Autowired
	GoodsService goodsService;
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	MiaoshaService miaoshaService;

	/**
	 * QPS 326
	 * 5000 * 10
	 * QPS 638 after performance tuning 1st
	 * @param model
	 * @param user
	 * @param goodsId
	 * @return
	 */
	@RequestMapping(value="/do_miaosha", method=RequestMethod.POST)
	@ResponseBody
	public Result<OrderInfo> miaosha(MiaoshaUser user, 
			@RequestParam("goodsId")long goodsId){

		if(user == null) {
			System.err.println(CodeMsg.SESSION_ERROR);
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		
		// 判断库存
		GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
		int stock = goods.getStockCount();
		if (stock <= 0) {
			return Result.error(CodeMsg.MIAOSHA_OVER);
		}
		
		// 判断是否秒杀到了
		MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdAndGoodsId(user.getId(), goodsId);
		if (order != null) {
			return Result.error(CodeMsg.MIAOSHA_REPEATE);
		}
		//减库存 下订单 写入秒杀订单 必须放在事务中 成功失败一起 原子操作
		OrderInfo orderInfo = miaoshaService.miaosha(user, goods);
		
		return Result.success(orderInfo);
	}
	
}
