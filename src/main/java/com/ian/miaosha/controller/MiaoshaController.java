package com.ian.miaosha.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ian.miaosha.domain.MiaoshaOrder;
import com.ian.miaosha.domain.MiaoshaUser;
import com.ian.miaosha.domain.OrderInfo;
import com.ian.miaosha.result.CodeMsg;
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

	@RequestMapping("/do_miaosha")
	public String miaosha(Model model, MiaoshaUser user, 
			@RequestParam("goodsId")long goodsId){
		model.addAttribute("user", user);
		if(user == null) {
			return "login";
		}
		
		// 判断库存
		GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
		int stock = goods.getStockCount();
		if (stock <= 0) {
			model.addAttribute("errmsg", CodeMsg.MIAOSHA_OVER.getMsg());
			return "miaosha_fail";
		}
		
		// 判断是否秒杀到了
		MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdAndGoodsId(user.getId(), goodsId);
		if (order != null) {
			model.addAttribute("errmsg", CodeMsg.MIAOSHA_REPEATE.getMsg());
			return "miaosha_fail";
		}
		//减库存 下订单 写入秒杀订单 必须放在事务中 成功失败一起 原子操作
		OrderInfo orderInfo = miaoshaService.miaosha(user, goods);
		model.addAttribute("orderInfo", orderInfo);
		model.addAttribute("goods", goods);
		
		return "order_detail";
	}
	
}
