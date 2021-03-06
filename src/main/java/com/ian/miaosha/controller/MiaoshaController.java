package com.ian.miaosha.controller;

import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ian.miaosha.access.AccessLimit;
import com.ian.miaosha.domain.MiaoshaOrder;
import com.ian.miaosha.domain.MiaoshaUser;
import com.ian.miaosha.rabbitmq.MQSender;
import com.ian.miaosha.rabbitmq.MiaoshaMessage;
import com.ian.miaosha.redis.GoodsKey;
import com.ian.miaosha.result.CodeMsg;
import com.ian.miaosha.result.Result;
import com.ian.miaosha.service.GoodsService;
import com.ian.miaosha.service.MiaoshaService;
import com.ian.miaosha.service.OrderService;
import com.ian.miaosha.service.RedisService;
import com.ian.miaosha.vo.GoodsVo;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean{
	
//	private static final Logger log = LoggerFactory.getLogger(MiaoshaController.class);

	
	@Autowired
	GoodsService goodsService;
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	MiaoshaService miaoshaService;

	@Autowired
	RedisService redisService;
	
	@Autowired
	MQSender sender;
	
	private Map<Long, Boolean> localOverMap = new HashMap<Long, Boolean>(); // 这里不应该用线程安全集合吗？
	
	/**
	 * 系统初始化时回调
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		List<GoodsVo> goodsList = goodsService.listGoodsVo();
		if (goodsList == null) {
			return;
		}
		for (GoodsVo goods : goodsList) {
			redisService.set(GoodsKey.getMiaoshaGoodsStock, ""+goods.getId(), goods.getStockCount());
			localOverMap.put(goods.getId(), false); // 不会内存溢出吗？
		}
	}
	
	/**
	 * QPS 326
	 * 5000 * 10
	 * QPS 638 after performance tuning 1st
	 * QPS 485 after performance tuning 2st by adding rabbitmq 
	 * @param model
	 * @param user
	 * @param goodsId
	 * @return
	 */
	@RequestMapping(value="/{path}/do_miaosha", method=RequestMethod.POST)
	@ResponseBody
	public Result<Integer> miaosha(MiaoshaUser user, 
			@RequestParam("goodsId")long goodsId,
			@PathVariable("path") String path){

		if(user == null) {
			return Result.error(CodeMsg.SESSION_ERROR);
		}
//		log.info(user.getId().toString());
		
		//验证Path
		boolean check = miaoshaService.checkPath(user, goodsId, path);
		if (!check) {
			return Result.error(CodeMsg.REQUEST_ILLEGAL);
		}
		
		boolean isOver = localOverMap.get(goodsId);
		if (isOver) {
			return Result.error(CodeMsg.MIAOSHA_OVER);
		}
		
		// 在redis中预减库存
		long stock = redisService.desc(GoodsKey.getMiaoshaGoodsStock, ""+goodsId);
		if(stock < 0) {
			localOverMap.put(goodsId, true);
			return Result.error(CodeMsg.MIAOSHA_OVER);
		}
		
		// 判断是否秒杀到了
		MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdAndGoodsId(user.getId(), goodsId);
		if (order != null) {
			return Result.error(CodeMsg.MIAOSHA_REPEATE);
		}
		
		// 入队
		MiaoshaMessage miaoshaMessage = new MiaoshaMessage();
		miaoshaMessage.setGoodsId(goodsId);
		miaoshaMessage.setUser(user);
		sender.sendMiaoshaMessage(miaoshaMessage);
		
		return Result.success(0); // 0表示排队中
		
	}

	/**
	 * 轮询查看下单结果，看是否下单成功
	 * @param user
	 * @param goodsId
	 * @return orderId: 成功, -1L: 秒杀失败, 0: 排队中
	 */
	@RequestMapping(value="/result", method=RequestMethod.GET)
	@ResponseBody
	public Result<Long> result(MiaoshaUser user, @RequestParam("goodsId")long goodsId){
		
		if (user == null) {
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		
		long result = miaoshaService.getMiaoshaResult(user.getId(), goodsId);
		return Result.success(result);
	}
	
	@AccessLimit(seconds=5, maxCount=5, needLogin=true) // 自定义限流注解
	@RequestMapping(value="/path", method=RequestMethod.GET)
	@ResponseBody
	public Result<String> getMiaoshaPath(
			HttpServletRequest request,
			MiaoshaUser user, 
			@RequestParam("goodsId")long goodsId,
			@RequestParam(value="verifyCode", defaultValue="0")int verifyCode){
		if (user == null) {
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		
		int check = miaoshaService.checkVerifyCode(user, goodsId, verifyCode);
		if (check == 0) {
			return Result.error(CodeMsg.VERIFY_CODE_EXPIRE);
		}else if (check == -1) {
			return Result.error(CodeMsg.VERIFY_CODE_FAIL);
		}
		String path = miaoshaService.createMiaoshaPath(user, goodsId);
		return Result.success(path); 
	}
	
	@RequestMapping("/verifyCode")
	@ResponseBody
	public Result<String> getVerifyCode(HttpServletResponse response,
			MiaoshaUser user, @RequestParam("goodsId")long goodsId){
		if (user == null) {
			return Result.error(CodeMsg.SESSION_ERROR);
		}
		
		BufferedImage image = miaoshaService.createMiaoshaVerifyCode(user, goodsId);
		
		try {
			OutputStream out = response.getOutputStream();
			ImageIO.write(image, "JPEG", out);
			out.flush();
			out.close();
			return null;
		}catch (Exception e) {
			e.printStackTrace();
			return Result.error(CodeMsg.MIAOSHA_FAIL);
		}

	}
	
}
