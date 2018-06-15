package com.ian.miaosha.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ian.miaosha.domain.MiaoshaUser;
import com.ian.miaosha.result.Result;

@Controller
@RequestMapping("/user")
public class UserController {

	@RequestMapping("/info")
	@ResponseBody
	public Result<MiaoshaUser> info(Model model, MiaoshaUser user){
		return Result.success(user);
	}
}
