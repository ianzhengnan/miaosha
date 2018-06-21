package com.ian.miaosha.config;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.ian.miaosha.access.UserContext;
import com.ian.miaosha.domain.MiaoshaUser;

@Service
public class UserArgumentResolver implements HandlerMethodArgumentResolver{

	
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		Class<?> clazz = parameter.getParameterType();
		return clazz == MiaoshaUser.class;
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		// 把获取用户的代码放在拦截器中， 因为一个请求是在一个线程中处理完成的
		return UserContext.getUser(); // 可否在这里通过报异常的方式判断user是否取到了？
	}

}
