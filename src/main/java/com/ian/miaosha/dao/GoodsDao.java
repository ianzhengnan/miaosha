package com.ian.miaosha.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.ian.miaosha.domain.MiaoshaGoods;
import com.ian.miaosha.vo.GoodsVo;

@Mapper
public interface GoodsDao {

	@Select("select g.*, mg.stock_count, mg.miaosha_price, mg.start_date, mg.end_date from miaosha_goods mg left join goods g on mg.goods_id = g.id")
	List<GoodsVo> listGoodsVo();

	@Select("select g.*, mg.stock_count, mg.miaosha_price, mg.start_date, mg.end_date from miaosha_goods mg left join goods g on mg.goods_id = g.id where g.id = #{goodsId}")
	GoodsVo getGoodsVoByGoodsId(@Param("goodsId")long goodsId);
	
	@Update("update miaosha_goods set stock_count = stock_count - 1 where goods_id = #{goodsId}")
	int reduceStock(MiaoshaGoods g);
}
