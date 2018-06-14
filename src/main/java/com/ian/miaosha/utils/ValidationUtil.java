package com.ian.miaosha.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class ValidationUtil {

	private static final Pattern MOBILE_PATTERN_PATTERN = Pattern.compile("1\\d{10}");
	
	public static boolean isMobile(String mobile) {
		if (StringUtils.isEmpty(mobile)) {
			return false;
		}
		Matcher matcher = MOBILE_PATTERN_PATTERN.matcher(mobile);
		return matcher.matches();
	}
	
//	public static void main(String[] args) {
//		System.out.println(isMobile("137616338sdf"));
//	}
}
