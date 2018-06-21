package com.ian.miaosha.service;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ian.miaosha.domain.MiaoshaOrder;
import com.ian.miaosha.domain.MiaoshaUser;
import com.ian.miaosha.domain.OrderInfo;
import com.ian.miaosha.redis.MiaoshaKey;
import com.ian.miaosha.utils.MD5Util;
import com.ian.miaosha.utils.UUIDUtil;
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

	public boolean checkPath(MiaoshaUser user, long goodsId, String path) {
		if (user == null || StringUtils.isEmpty(path)) {
			return false;
		}
		String str = redisService.get(MiaoshaKey.getMiaoshaPath, ""+user.getId()+"_"+goodsId, String.class);
		return path.equals(str);
	}

	public String createMiaoshaPath(MiaoshaUser user, long goodsId) {
		String str = MD5Util.md5(UUIDUtil.uuid()+"Q1w2e3r4");
		redisService.set(MiaoshaKey.getMiaoshaPath, ""+user.getId()+"_"+goodsId, str);
		return str;
	}

	public BufferedImage createMiaoshaVerifyCode(MiaoshaUser user, long goodsId) {
		if (user == null || goodsId <= 0) {
			return null;
		}
		int width = 80;
		int height = 32;
		// create image
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics();
		// set background color
		g.setColor(new Color(0xDCDCDC));
		g.fillRect(0, 0, width, height);
		// draw the border
		g.setColor(Color.black);
		g.drawRect(0, 0, width-1, height-1);
		// create a random instance to generate the codes
		Random rdm = new Random();
		// make some confusion
		for (int i = 0; i < 50; i++) {
			int x = rdm.nextInt(width);
			int y = rdm.nextInt(height);
			g.drawOval(x, y, 0, 0); // 生成干扰点
		}
		// generate a random code
		String verifyCode = generateVerifyCode(rdm);
		g.setColor(new Color(0, 100, 0));
		g.setFont(new Font("Candara", Font.BOLD, 24));
		g.drawString(verifyCode, 8, 24);
		g.dispose();
		// save verify code to redis
		int rnd = calc(verifyCode);
		redisService.set(MiaoshaKey.getMiaoshaVerifyCode, user.getId()+","+goodsId, rnd);
		
		return image;
	}

	private int calc(String verifyCode) {
		try {
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine engine = manager.getEngineByName("JavaScript");
			return (Integer)engine.eval(verifyCode);
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	private static char[] ops = new char[] {'+','-','*'};
	
	private String generateVerifyCode(Random rdm) {
		int num1 = rdm.nextInt(10);
		int num2 = rdm.nextInt(10);
		int num3 = rdm.nextInt(10);
		char op1 = ops[rdm.nextInt(3)];
		char op2 = ops[rdm.nextInt(3)];
		String exp = "" + num1 + op1 + num2 + op2 + num3;
		return exp;
	}

	public int checkVerifyCode(MiaoshaUser user, long goodsId, int verifyCode) {
		if (user == null || goodsId <= 0) {
			return 0; // no verify code or code is expired
		}
		Integer oldVerifyCode = redisService.get(MiaoshaKey.getMiaoshaVerifyCode, user.getId()+","+goodsId, Integer.class);
		if (oldVerifyCode == null) {
			return 0; // no verify code or code is expired
		}else if(oldVerifyCode - verifyCode != 0) {
			return -1; // code inconsistent  
		}
		redisService.delete(MiaoshaKey.getMiaoshaVerifyCode, user.getId()+","+goodsId);
		return 1; // code consistent
	}

}
