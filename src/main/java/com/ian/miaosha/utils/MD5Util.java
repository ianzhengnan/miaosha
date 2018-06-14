package com.ian.miaosha.utils;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {

	private static final String SALT = "q1w2e3r4t5y6u7";
	
	public static String md5(String src) {
		return DigestUtils.md5Hex(src);
	}
	
	public static String inputPassToFormPass(String inputPass) {
		String src = "" + SALT.charAt(0) + SALT.charAt(2) + inputPass + SALT.charAt(5) + SALT.charAt(4);
		return md5(src);
	}
	
	public static String formPassToDBPass(String formPass, String salt) {
		String src = "" + salt.charAt(0) + salt.charAt(2) + formPass + salt.charAt(5) + salt.charAt(4);
		return md5(src);
	}
	
	public static String inputPassToDbPass(String input, String saltDB) {
		String formPass = inputPassToFormPass(input);
		String dbPass = formPassToDBPass(formPass, saltDB);
		return dbPass;
	}
	
	public static void main(String[] args) {
//		System.out.println(inputPassFromPass("11223123"));
		System.out.println(inputPassToFormPass("1234567"));
		System.out.println(inputPassToDbPass("1234567", "P0o9i8u7"));
	}
	
}
