package com.ian.miaosha.access;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSON;
import com.ian.miaosha.domain.MiaoshaUser;
import com.ian.miaosha.redis.AccessKey;
import com.ian.miaosha.result.CodeMsg;
import com.ian.miaosha.result.Result;
import com.ian.miaosha.service.MiaoshaUserService;
import com.ian.miaosha.service.RedisService;

@Service
public class AccessInterceptor extends HandlerInterceptorAdapter{

	@Autowired
	MiaoshaUserService userService;
	
	@Autowired
	RedisService redisService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		if (handler instanceof HandlerMethod) {
			MiaoshaUser user = getUser(request, response);
			UserContext.setUser(user); //有可能是空的，但是也把它放到UserContext里面去
			
			HandlerMethod hm = (HandlerMethod)handler;
			AccessLimit aLimit = hm.getMethodAnnotation(AccessLimit.class);
			if (aLimit == null) {
				return true;  // 没加注释，表示没有这个限制
			}
			int seconds = aLimit.seconds();
			int maxCount = aLimit.maxCount();
			boolean needLogin = aLimit.needLogin();
			
			String key = request.getRequestURI();
			if (needLogin) {
				if (user == null) {
					render(response, CodeMsg.SESSION_ERROR); // 这里能不能通过抛出异常来解决？可以试试
					return false;
				}
				key += "_" + user.getId();
			}else {
				// do nothing
			}
			// 查询访问次数 seconds秒钟最多访问maxCount次
			AccessKey ak = AccessKey.withExpire(seconds);
			Integer count = redisService.get(ak, key, Integer.class);
			if (count == null) {
				redisService.set(ak, key, 1);
			}else if(count < maxCount){
				redisService.incr(ak, key);
			}else {
				 render(response, CodeMsg.ACCESS_LIMIT); // 请求太频繁
				return false;
			}
		}
		return true;
	}

	private void render(HttpServletResponse response, CodeMsg cm) throws IOException {
		response.setContentType("application/json;charset=UTF-8");
		OutputStream out = response.getOutputStream();
		String str = JSON.toJSONString(Result.error(cm));
		out.write(str.getBytes("UTF-8"));
		out.flush();
		out.close();
	}

	private MiaoshaUser getUser(HttpServletRequest request, HttpServletResponse response) {
		String paramToken = request.getParameter(MiaoshaUserService.COOKIE_NAME);
		String cookieToken = getCooikeValue(request, MiaoshaUserService.COOKIE_NAME);
		
		if (StringUtils.isEmpty(paramToken) && StringUtils.isEmpty(cookieToken)) {
			return null;
		}
		String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
		
		return userService.getByToken(response, token);
	}
	
	private String getCooikeValue(HttpServletRequest request, String cookieName) {
		Cookie[] cookies = request.getCookies();
		if(cookies == null || cookies.length <= 0) {
			return null;
		}
		for (int i = 0; i < cookies.length; i++) {
			if (cookies[i].getName().equals(cookieName)) {
				return cookies[i].getValue();
			}
		}
		return null;
	}
}
