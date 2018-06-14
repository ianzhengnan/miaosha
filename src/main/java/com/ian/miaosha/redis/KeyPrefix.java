package com.ian.miaosha.redis;

public interface KeyPrefix {

	int expireSeconds();
	
	String getPrefix();
}
