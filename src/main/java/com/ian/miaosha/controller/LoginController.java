package com.ian.miaosha.controller;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ian.miaosha.result.Result;
import com.ian.miaosha.service.MiaoshaUserService;
import com.ian.miaosha.service.RedisService;
import com.ian.miaosha.vo.LoginVo;

@Controller
@RequestMapping("/login")
public class LoginController {
	
	private static Logger log = LoggerFactory.getLogger(LoginController.class);
	
	@Autowired
	MiaoshaUserService UserService;
	
	@Autowired
	RedisService redisService;

	@RequestMapping("/to_login")
	public String toLogin() {
		return "login";
	}
	
	@RequestMapping("/do_login")
	@ResponseBody
	public Result<Boolean> doLogin(HttpServletResponse rep, @Valid LoginVo loginVo){
		log.info(loginVo.toString());
		
		// 登录
		UserService.login(rep, loginVo);
		
		return Result.success(true);
		
	}
	
	
	
	
	
	
}
