package com.ian.miaosha.utils;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;



public class DBUtil {

	private static Properties properties;
	
	static {
		try {
			InputStream inputStream = DBUtil.class.getClassLoader().getResourceAsStream("application.properties");
			properties = new Properties();
			properties.load(inputStream);
			inputStream.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Connection getConnection() throws Exception{
		String url = properties.getProperty("spring.datasource.url");
		String username = properties.getProperty("spring.datasource.username");
		String password = properties.getProperty("spring.datasource.password");
		String driver = properties.getProperty("spring.datasource.driver-class-name");
		Class.forName(driver);
		return DriverManager.getConnection(url, username, password);
	}
}
