package com.ian.miaosha.controller;


import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import com.ian.miaosha.domain.MiaoshaUser;
import com.ian.miaosha.redis.GoodsKey;
import com.ian.miaosha.service.GoodsService;
import com.ian.miaosha.service.MiaoshaUserService;
import com.ian.miaosha.service.RedisService;
import com.ian.miaosha.vo.GoodsVo;

@Controller
@RequestMapping("/goods")
public class GoodsController {

	@Autowired
	MiaoshaUserService userService;
	
	@Autowired
	GoodsService goodsService;
	
	@Autowired
	RedisService redisService;
	
	@Autowired
	ThymeleafViewResolver thymeleafViewResolver;
	
	/**
	 * QPS: 181
	 * 5000 * 10 线程数 * 循环次数
	 * @param model
	 * @param user
	 * @return
	 */
	@RequestMapping(value="/to_list", produces="text/html")
	@ResponseBody
	public String list(HttpServletRequest request, 
			HttpServletResponse response, Model model, MiaoshaUser user) {
		
		// 获得登录用户
		model.addAttribute("user", user);
		// 取缓存
		String html = redisService.get(GoodsKey.getGoodsList, "", String.class);
		if(!StringUtils.isEmpty(html)) {
			return html;
		}
		
		// 查询商品列表
		List<GoodsVo> goodsList = goodsService.listGoodsVo();
		model.addAttribute("goodsList", goodsList);
//		return "goods_list";
		
		// 手动渲染
		WebContext ctx = new WebContext(request, response, 
				request.getServletContext(), request.getLocale(), model.asMap());
		html = thymeleafViewResolver.getTemplateEngine().process("goods_list", ctx);
		if (!StringUtils.isEmpty(html)) {
			// 保存页面到缓存
			redisService.set(GoodsKey.getGoodsList, "", html);
		}
		return html;
	}
	
	@RequestMapping(value="/to_detail/{goodsId}", produces="text/html")
	@ResponseBody
	public String detail(HttpServletRequest request, 
			HttpServletResponse response,Model model, 
			MiaoshaUser user, @PathVariable("goodsId")long goodsId) {
		
		model.addAttribute("user", user);
		// 取缓存
		String html = redisService.get(GoodsKey.getGoodsDetail, ""+goodsId, String.class);
		if(!StringUtils.isEmpty(html)) {
			return html;
		}
		
		
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
		
		// 手动渲染
		WebContext ctx = new WebContext(request, response, 
				request.getServletContext(), request.getLocale(), model.asMap());
		html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", ctx);
		if (!StringUtils.isEmpty(html)) {
			// 保存页面到缓存
			redisService.set(GoodsKey.getGoodsDetail, ""+goodsId, html);
		}
		return html;
	}
	
	
}
