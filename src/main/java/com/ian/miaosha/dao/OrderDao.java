package com.ian.miaosha.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;

import com.ian.miaosha.domain.MiaoshaOrder;
import com.ian.miaosha.domain.OrderInfo;

@Mapper
public interface OrderDao {

	@Select("select * from miaosha_order where user_id = #{userId} and goods_id = #{goodsId}")
	MiaoshaOrder getMiaoshaOrderByUserIdAndGoodsId(@Param("userId")long userId, @Param("goodsId")long goodsId);

	@Insert("insert into order_info(user_id, goods_id, goods_name, goods_count, goods_price, order_channel, status, create_date) "
			+ "values(#{userId}, #{goodsId}, #{goodsName}, #{goodsCount}, #{goodsPrice}, #{orderChannel}, #{status}, #{createDate})")
	@SelectKey(keyColumn="id", keyProperty="id", resultType=long.class, before=false, statement="select last_insert_id()")
	long insert(OrderInfo orderInfo); 
	
	@Insert("insert into miaosha_order(user_id, goods_id, order_id) values(#{userId}, #{goodsId}, #{orderId}) ")
	//@SelectKey(keyColumn="id", keyProperty="id", resultType=long.class, before=false, statement="select last_insert_id()")
	int insertMiaoshaOrder(MiaoshaOrder miaoshaOrder);

	@Select("select * from order_info where id = #{orderId}")
	OrderInfo getOrderById(@Param("orderId")long orderId);

	
}
