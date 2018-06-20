package com.ian.miaosha.exception;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ian.miaosha.result.CodeMsg;
import com.ian.miaosha.result.Result;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

	// 定义拦截的异常类型
	@ExceptionHandler(value=Exception.class)
	public Result<String> exceptionHander(HttpServletRequest req, Exception exp){
		exp.printStackTrace(); // 如果碰到console里没有错误消息的时候，可以把这句放出来，就有出错信息了。
		// 这里统一处理抛出的业务异常
		if (exp instanceof GlobalException) {
			
			GlobalException globalExp = (GlobalException)exp;
			return Result.error(globalExp.getCodeMsg());
		
		// 这里统一处理绑定异常，就是jsr303校验异常
		} else if (exp instanceof BindException) {
			BindException bindExp = (BindException)exp;
			
			List<ObjectError> errors = bindExp.getAllErrors();
			
			ObjectError error = errors.get(0);
			
			String msg = error.getDefaultMessage();
			
			return Result.error(CodeMsg.BIND_ERROR.fillArgs(msg));
		}else {
			return Result.error(CodeMsg.SERVICE_ERROR);
		}
	}
	
}
