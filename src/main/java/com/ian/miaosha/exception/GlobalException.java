package com.ian.miaosha.exception;

import com.ian.miaosha.result.CodeMsg;

// 业务异常, 抛出的异常由业务处理器GlobalExceptionHandler处理
public class GlobalException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	private CodeMsg codeMsg;
	
	public GlobalException(CodeMsg codeMsg) {
		super(codeMsg.toString());
		this.codeMsg = codeMsg;
	}

	public CodeMsg getCodeMsg() {
		return codeMsg;
	}
	
	
	
	
}
