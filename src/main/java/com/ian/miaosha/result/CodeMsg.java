package com.ian.miaosha.result;

public class CodeMsg {

	private int code;
	private String msg;
	
	public static CodeMsg SUCCESS = new CodeMsg(0, "success");
	
	// 通用异常
	public static CodeMsg SERVICE_ERROR = new CodeMsg(500100, "服务端异常");
	
	
	// 登录模块 5002xx
	
	// 商品模块 5003xx
	
	// 订单模块 5004xx
	
	// 秒杀模块 5005xx
	
	
	
	private CodeMsg(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}
	
	public int getCode() {
		return code;
	}
	public String getMsg() {
		return msg;
	}
	
}
