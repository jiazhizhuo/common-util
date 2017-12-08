package com.jzz.test.nashorn;

public class OrderInfo {
	public String goodsCatalog;
	public Double goodsPrices;
	public String goodsName;
	public OrderInfo(String goodsCatalog, Double goodsPrices, String goodsName) {
		super();
		this.goodsCatalog = goodsCatalog;
		this.goodsPrices = goodsPrices;
		this.goodsName = goodsName;
	}
	public String getGoodsCatalog() {
		return goodsCatalog;
	}
	public void setGoodsCatalog(String goodsCatalog) {
		this.goodsCatalog = goodsCatalog;
	}
	public Double getGoodsPrices() {
		return goodsPrices;
	}
	public void setGoodsPrices(Double goodsPrices) {
		this.goodsPrices = goodsPrices;
	}
	public String getGoodsName() {
		return goodsName;
	}
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	@Override
	public String toString() {
		return "OrderInfo [goodsCatalog=" + goodsCatalog + ", goodsPrices=" + goodsPrices + ", goodsName=" + goodsName
				+ "]";
	}
}

