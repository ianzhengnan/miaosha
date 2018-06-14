package com.ian.miaosha.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ian.miaosha.dao.MiaoshaUserDao;
import com.ian.miaosha.domain.MiaoshaUser;
import com.ian.miaosha.exception.GlobalException;
import com.ian.miaosha.result.CodeMsg;
import com.ian.miaosha.utils.MD5Util;
import com.ian.miaosha.vo.LoginVo;

@Service
public class MiaoshaUserService {

	@Autowired
	MiaoshaUserDao miaoshaUserDao;
	
	public MiaoshaUser getById(long id) {
		return miaoshaUserDao.getById(id);
	}

	public boolean login(LoginVo loginVo) {
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
		return true;
		
	}
	
}
