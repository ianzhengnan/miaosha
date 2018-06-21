package com.ian.miaosha.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.ian.miaosha.access.AccessInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer{

	@Autowired
	UserArgumentResolver userArgResolver;
	
	@Autowired
	AccessInterceptor accessInterceptor;
	
	// 注册参数拦截器
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(userArgResolver);
	}

	// 注册访问拦截器
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(accessInterceptor);
	}
	
	
	
}
