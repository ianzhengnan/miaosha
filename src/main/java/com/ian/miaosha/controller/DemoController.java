package com.ian.miaosha.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ian.miaosha.domain.User;
import com.ian.miaosha.result.CodeMsg;
import com.ian.miaosha.result.Result;
import com.ian.miaosha.service.UserService;

@Controller
public class DemoController {

	@Autowired
	UserService userService;
	
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
}
