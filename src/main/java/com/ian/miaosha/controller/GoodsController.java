package com.ian.miaosha.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ian.miaosha.domain.MiaoshaUser;
import com.ian.miaosha.service.GoodsService;
import com.ian.miaosha.service.MiaoshaUserService;
import com.ian.miaosha.vo.GoodsVo;

@Controller
@RequestMapping("/goods")
public class GoodsController {

	@Autowired
	MiaoshaUserService userService;
	
	@Autowired
	GoodsService goodsService;
	
	@RequestMapping("/to_list")
	public String list(Model model, MiaoshaUser user) {
		// 获得登录用户
		model.addAttribute("user", user);
		
		// 查询商品列表
		List<GoodsVo> goodsList = goodsService.listGoodsVo();
		model.addAttribute("goodsList", goodsList);
		return "goods_list";
	}
	
	@RequestMapping("/to_detail/{goodsId}")
	public String detail(Model model, MiaoshaUser user, @PathVariable("goodsId")long goodsId) {
		
		model.addAttribute("user", user);
		
		GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
		
		model.addAttribute("goods", goods);
		
		//获得秒杀开始结束时间
		long startAt = goods.getStartDate().getTime();
		long endAt = goods.getEndDate().getTime();
		long now = System.currentTimeMillis();
		
		// 秒杀状态
		int miaoshaStatus = 0;
		int remainSeconds = 0;
		
		if(now < startAt) { // 秒杀还没开始，倒计时
			miaoshaStatus = 0;
			remainSeconds = (int) ((startAt - now) / 1000);
		}else if (now > endAt) { // 秒杀已经结束
			miaoshaStatus = 2;
			remainSeconds = -1;
		}else{ // 秒杀进行中
			miaoshaStatus = 1;
			remainSeconds = 0;
		}
		
		model.addAttribute("miaoshaStatus", miaoshaStatus);
		model.addAttribute("remainSeconds", remainSeconds);
		
		return "goods_detail";
	}
	
	
}
