package com.ian.miaosha;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.builder.SpringApplicationBuilder;
//import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Hello world!
 *
 */
// 继承SpringBootServletInitializer并重写configure方法可以让这个项目既可以main函数方式运行，也可以通过war包， 在tomcat server中运行。
@SpringBootApplication
public class App 
{
	
	public static void main( String[] args ) throws Exception
    {
    	SpringApplication.run(App.class, args);
    }
}
