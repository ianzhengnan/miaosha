package com.ian.miaosha.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ian.miaosha.domain.User;
import com.ian.miaosha.rabbitmq.MQSender;
import com.ian.miaosha.redis.UserKey;
import com.ian.miaosha.result.CodeMsg;
import com.ian.miaosha.result.Result;
import com.ian.miaosha.service.RedisService;
import com.ian.miaosha.service.UserService;

@Controller
@RequestMapping("/demo")
public class DemoController {

	@Autowired
	UserService userService;
	
	@Autowired
	RedisService redisService;
	
	@Autowired
	MQSender sender;
	
//	@RequestMapping("/mq")
//	@ResponseBody
//	public Result<String> mq() {
//		sender.send("hello, Ian!");
//		return Result.success("hello, ian");
//	}
//	
//	@RequestMapping("/mq/topic")
//	@ResponseBody
//	public Result<String> topic() {
//		sender.topicSend("hello, Ian!");
//		return Result.success("hello, ian");
//	}
//	
//	@RequestMapping("/mq/fanout")
//	@ResponseBody
//	public Result<String> fanout() {
//		sender.fanoutSend("hello, Ian!");
//		return Result.success("hello, ian");
//	}
//	
//	@RequestMapping("/mq/header")
//	@ResponseBody
//	public Result<String> header() {
//		sender.headerSend("hello, Ian Header!");
//		return Result.success("hello, ian Header.");
//	}
	
	@RequestMapping("/hello")
	@ResponseBody
	public Result<String> hello() {
		return Result.success("hello, ian");
	}
	
	@RequestMapping("/helloError")
	@ResponseBody
	public Result<String> helloError(){
		return Result.error(CodeMsg.SERVICE_ERROR);
	}
	
	@RequestMapping("/thymeleaf")
	public String thymeleaf(Model model) {
		model.addAttribute("name", "Ian");
		return "hello";
	}
	
	@RequestMapping("/db/get/{id}")
	@ResponseBody
	public Result<User> dbGet(@PathVariable int id){
		User user = userService.getUserById(id);
		if (user == null) {
			return Result.error(CodeMsg.SERVICE_ERROR);
		}
		return Result.success(user);
	}
	
	@RequestMapping("/db/tx")
	@ResponseBody
	public Result<Boolean> dbTx(){
		userService.tx();
		return Result.success(true);
	}
	
	@RequestMapping("/redis/get/{key}")
	@ResponseBody
	public Result<User> redisGet(@PathVariable String key){
		User user = redisService.get(UserKey.getById, key, User.class);
		return Result.success(user);
	}
	
	@RequestMapping("/redis/set")
	@ResponseBody
	public Result<Boolean> redisSet(){
		User user = new User();
		user.setId(1);
		user.setName("zhengnan");
		Boolean bool = redisService.set(UserKey.getById, "" + 1, user);
		return Result.success(bool);
	}
	
	
}
