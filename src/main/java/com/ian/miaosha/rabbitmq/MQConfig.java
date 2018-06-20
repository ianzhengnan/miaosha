package com.ian.miaosha.rabbitmq;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQConfig {
	
	public static final String QUEUE = "queue";
	public static final String TOPIC_QUEUE1 = "topic.queue1";
	public static final String TOPIC_QUEUE2 = "topic.queue2";
	public static final String HEADER_QUEUE = "header.queue";
	public static final String TOPIC_EXCHANGE = "topicExchange";
	public static final String FANOUT_EXCHANGE = "fanoutExchange";
	public static final String HEADERS_EXCHANGE = "headersExchange";
//	public static final String ROUTING_KEY1 = "topic.key1";
//	public static final String ROUTING_KEY2 = "topic.#";// *代表一个单词，#代表0个或者多个单词
	/**
	 * Direct模式 Exchange交换机(四种模式)
	 * 
	 */
	@Bean
	public Queue queue() {
		return new Queue(QUEUE, true);
	}
	
	/**
	 * Topic模式 Exchange交换机(四种模式)
	 * 根据rounting key来路由哪些queue能收到信息
	 */
	@Bean
	public Queue topicQueue1() {
		return new Queue(TOPIC_QUEUE1,true);
	}
	@Bean
	public Queue topicQueue2() {
		return new Queue(TOPIC_QUEUE2,true);
	}
	@Bean
	public TopicExchange topicExchange() {
		return new TopicExchange(TOPIC_EXCHANGE);
	}
	/**
	 * 绑定queue, exchange, routing key
	 * @return
	 */
	@Bean
	public Binding topicBinding1() {
		// routing key为topic.key1时，绑定topicQueue1到topicExchange
		return BindingBuilder.bind(topicQueue1()).to(topicExchange()).with("topic.key1");
	}
	
	@Bean 
	public Binding topicBinding2() {
		// routing key为topic.#时，绑定topicQueue2到topicExchange
		return BindingBuilder.bind(topicQueue2()).to(topicExchange()).with("topic.#");
	}
	
	/**
	 * Fanout模式 Exchange交换机(四种模式)
	 * 广播模式
	 */
	@Bean
	public FanoutExchange fanoutExchange() {
		return new FanoutExchange(FANOUT_EXCHANGE);
	}
	@Bean
	public Binding fanoutBinding1() {
		// 绑定topicQueue1到fanoutExchange
		return BindingBuilder.bind(topicQueue1()).to(fanoutExchange());
	}
	@Bean
	public Binding fanoutBinding2() {
		// 绑定topicQueue2到fanoutExchange
		return BindingBuilder.bind(topicQueue2()).to(fanoutExchange());
	}
	
	/**
	 * Headers模式 Exchange交换机(四种模式)
	 * 根据特定条件选择发送什么数据到队列
	 */
	@Bean
	public HeadersExchange headersExchange() {
		return new HeadersExchange(HEADERS_EXCHANGE);
	}
	@Bean
	public Queue headerQueue() {
		return new Queue(HEADER_QUEUE, true);
	}
	@Bean
	public Binding headerBinding() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("header1", "value1");
		map.put("header2", "value2");
		return BindingBuilder.bind(headerQueue()).to(headersExchange()).whereAll(map).match();
	}
}
