package com.ian.miaosha.redis;

public class BasePrefix implements KeyPrefix{

	
	private int expireSeconds;
	
	private String prefix;
	
	public BasePrefix(String prefix) { // 0 for ever
		this(0, prefix);
	}
	
	public BasePrefix(int expireSeconds, String prefix) {
		this.expireSeconds = expireSeconds;
		this.prefix = prefix;
	}
	
	@Override
	public int expireSeconds() { // 0 for ever
		return 0;
	}

	@Override
	public String getPrefix() {
		String className = getClass().getSimpleName();
		return className + ":" + prefix;
	}


}
