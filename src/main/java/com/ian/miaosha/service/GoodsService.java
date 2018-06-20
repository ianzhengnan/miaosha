package com.ian.miaosha.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ian.miaosha.dao.GoodsDao;
import com.ian.miaosha.domain.MiaoshaGoods;
//import com.ian.miaosha.exception.GlobalException;
//import com.ian.miaosha.result.CodeMsg;
import com.ian.miaosha.vo.GoodsVo;

@Service
public class GoodsService {

	@Autowired
	GoodsDao goodsDao;
	
	public List<GoodsVo> listGoodsVo(){
		return goodsDao.listGoodsVo();
	}

	public GoodsVo getGoodsVoByGoodsId(long goodsId) {
		return goodsDao.getGoodsVoByGoodsId(goodsId);
	}

	public boolean reduceStock(GoodsVo goods) {
		MiaoshaGoods g = new MiaoshaGoods();
		g.setGoodsId(goods.getId());
		int ret = goodsDao.reduceStock(g);
		// 视频里没有这个检查，但是没有这个检查，会出现订单超卖情况发生
//		if (ret <= 0) {
//			throw new GlobalException(CodeMsg.MIAOSHA_OVER);
//		}
		return ret > 0;
	}
}
