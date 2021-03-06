package com.ian.miaosha.result;

public class CodeMsg {

	private int code;
	private String msg;
	
	public static CodeMsg SUCCESS = new CodeMsg(0, "success");
	
	// 通用异常
	public static CodeMsg SERVICE_ERROR = new CodeMsg(500100, "服务端异常");
	public static CodeMsg BIND_ERROR = new CodeMsg(500101, "参数校验异常: %s");
	public static CodeMsg REQUEST_ILLEGAL = new CodeMsg(500102, "请求非法");
	public static CodeMsg ACCESS_LIMIT = new CodeMsg(500103, "请求太频繁");
	
	// 登录模块 5002xx
	public static CodeMsg SESSION_ERROR = new CodeMsg(500200, "Session不存在或者已经失效");
	public static CodeMsg PASSWORD_EMPTY = new CodeMsg(500201, "登录密码不能为空");
	public static CodeMsg MOBILE_EMPTY = new CodeMsg(500202, "手机号不能为空");
	public static CodeMsg MOBILE_ERROR = new CodeMsg(500203, "手机号格式错误");
	public static CodeMsg MOBILE_NOT_EXISTS = new CodeMsg(500204, "手机号码不存在");
	public static CodeMsg PASSWORD_ERROR = new CodeMsg(500205, "密码输入错误");
	
	
	// 商品模块 5003xx
	
	// 订单模块 5004xx
	public static CodeMsg ORDER_NOT_EXISTS = new CodeMsg(500400, "订单不存在");
	
	// 秒杀模块 5005xx
	public static CodeMsg MIAOSHA_OVER = new CodeMsg(500500, "商品已经秒杀完毕");
	public static CodeMsg MIAOSHA_REPEATE = new CodeMsg(500501, "不能重复秒杀");
	public static CodeMsg MIAOSHA_FAIL = new CodeMsg(500502, "秒杀失败");
	public static CodeMsg VERIFY_CODE_FAIL = new CodeMsg(500503, "验证码错误");
	public static CodeMsg VERIFY_CODE_EXPIRE = new CodeMsg(500504, "验证码已过期");
	
	
	private CodeMsg(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}
	
	public CodeMsg fillArgs(Object...args) {
		int code = this.code;
		String message = String.format(this.msg, args);
		return new CodeMsg(code, message);
	}
	
	public int getCode() {
		return code;
	}
	public String getMsg() {
		return msg;
	}

	@Override
	public String toString() {
		return "CodeMsg [code=" + code + ", msg=" + msg + "]";
	}
	
	
	
}
