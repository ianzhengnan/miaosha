package com.ian.miaosha.service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ian.miaosha.dao.MiaoshaUserDao;
import com.ian.miaosha.domain.MiaoshaUser;
import com.ian.miaosha.exception.GlobalException;
import com.ian.miaosha.redis.MiaoshaUserKey;
import com.ian.miaosha.result.CodeMsg;
import com.ian.miaosha.utils.MD5Util;
import com.ian.miaosha.utils.UUIDUtil;
import com.ian.miaosha.vo.LoginVo;

@Service
public class MiaoshaUserService {

	public static final String COOKIE_NAME = "token";
	
	@Autowired
	MiaoshaUserDao miaoshaUserDao;
	
	@Autowired
	RedisService redisService;
	
	public MiaoshaUser getById(long id) {
		//取缓存
		 MiaoshaUser user = redisService.get(MiaoshaUserKey.getById, ""+id, MiaoshaUser.class);
		if (user != null) {
			return user;
		}
		//取数据库放缓存
		user = miaoshaUserDao.getById(id);
		if(user != null) {
			redisService.set(MiaoshaUserKey.getById, ""+id, user);
		}
		return user;
	}
	
	public boolean updatePassword(String token, long id, String formPass) {
		//取user
		MiaoshaUser user = getById(id);
		if (user == null) {
			throw new GlobalException(CodeMsg.MOBILE_NOT_EXISTS);
		}
		//为了不更新所有字段，所以新建了个to be update user
		MiaoshaUser toBeUpdate = new MiaoshaUser();
		toBeUpdate.setId(id);
		toBeUpdate.setPassword(MD5Util.formPassToDBPass(formPass, user.getSalt()));
		miaoshaUserDao.update(toBeUpdate);
		//处理缓存
		redisService.delete(MiaoshaUserKey.getById, ""+id);
		user.setPassword(toBeUpdate.getPassword());
		redisService.set(MiaoshaUserKey.token, token, user);
		return true;
	}

	public String login(HttpServletResponse rep, LoginVo loginVo) {
		if (loginVo == null) {
			throw new GlobalException(CodeMsg.SERVICE_ERROR);
		}
		String mobile = loginVo.getMobile();
		String formPass = loginVo.getPassword();
		
		MiaoshaUser user = getById(Long.parseLong(mobile));
		if (user == null) {
			throw new GlobalException(CodeMsg.MOBILE_NOT_EXISTS);
		}
		// 验证密码
		String dbPass = user.getPassword();
		String slatDb = user.getSalt();
		String calcPass = MD5Util.formPassToDBPass(formPass, slatDb);
		if(!calcPass.equals(dbPass)) {
			throw new GlobalException(CodeMsg.PASSWORD_ERROR);
		}
		 
		// 生成cookie
		String token = UUIDUtil.uuid();
		addCookie(rep, token, user);
		return token;
		
	}

	public MiaoshaUser getByToken(HttpServletResponse rep, String token) {
		if(StringUtils.isEmpty(token)) {
			return null;
		}
		MiaoshaUser user = redisService.get(MiaoshaUserKey.token, token, MiaoshaUser.class);
		//延长有效期： 比如30分钟过期，如果在30分钟内再次登录则为登录时间再加30分钟为过期时间
		if (user != null) {
			addCookie(rep, token, user);
		}
		
		return user;
	}
	
	public void addCookie(HttpServletResponse rep, String token, MiaoshaUser user) {

		redisService.set(MiaoshaUserKey.token, token, user); 
		Cookie cookie = new Cookie(COOKIE_NAME, token);
		cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds()); // 这里设计巧妙
		cookie.setPath("/");
		rep.addCookie(cookie);
	}
	
}
