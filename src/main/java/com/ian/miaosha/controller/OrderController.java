package com.ian.miaosha.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ian.miaosha.domain.MiaoshaUser;
import com.ian.miaosha.domain.OrderInfo;
import com.ian.miaosha.result.CodeMsg;
import com.ian.miaosha.result.Result;
import com.ian.miaosha.service.GoodsService;
import com.ian.miaosha.service.OrderService;
import com.ian.miaosha.vo.GoodsVo;
import com.ian.miaosha.vo.OrderDetailVo;

@Controller
@RequestMapping("/order")
public class OrderController {

	@Autowired
	OrderService orderService;
	
	@Autowired
	GoodsService goodsService;
	
	@RequestMapping("/detail")
	@ResponseBody
	public Result<OrderDetailVo> orderDetail(MiaoshaUser user, @RequestParam("orderId")long orderId){
		if (user == null) {
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		
		OrderInfo orderInfo = orderService.getOrderById(orderId);
		if(orderInfo == null) {
			return Result.error(CodeMsg.ORDER_NOT_EXISTS);
		}
		long goodsId = orderInfo.getGoodsId();
		GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
		OrderDetailVo orderDetailVo = new OrderDetailVo();
		orderDetailVo.setGoods(goods);
		orderDetailVo.setOrder(orderInfo);
		return Result.success(orderDetailVo);
	}
}
