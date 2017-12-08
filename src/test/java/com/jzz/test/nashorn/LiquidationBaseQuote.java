package com.jzz.test.nashorn;

public class LiquidationBaseQuote{
	/**
	 * 引用别名(必填项)
	 */
	private String quoteName = null;
	/**
	 * 引用值(必填项)
	 */
	private String quoteValue = null;
	/**
	 * 值类型(必填项)
	 * 1 string, 2 number
	 */
	private int valueType = -1;
	public String getQuoteName() {
		return quoteName;
	}
	public void setQuoteName(String quoteName) {
		this.quoteName = quoteName;
	}
	public String getQuoteValue() {
		return quoteValue;
	}
	public void setQuoteValue(String quoteValue) {
		this.quoteValue = quoteValue;
	}
	public int getValueType() {
		return valueType;
	}
	public void setValueType(int valueType) {
		this.valueType = valueType;
	}
	public LiquidationBaseQuote(String quoteName, String quoteValue, int valueType) {
		super();
		this.quoteName = quoteName;
		this.quoteValue = quoteValue;
		this.valueType = valueType;
	}
}