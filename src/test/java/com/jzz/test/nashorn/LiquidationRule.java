package com.jzz.test.nashorn;

import javax.script.Invocable;

/**
 * 清算规则
 */
public class LiquidationRule {
	//规则信息

	//条件公式
	private String conditionsJs ;
	//计算公式
	private String expressionJs ;

	// js对象缓存
	private Invocable jsInvocable ;

	public LiquidationRule(String conditionsJs, String expressionJs) {
		super();
		this.conditionsJs = conditionsJs;
		this.expressionJs = expressionJs;
	}
	
	

	public String getConditionsJs() {
		return conditionsJs;
	}
	public void setConditionsJs(String conditionsJs) {
		this.conditionsJs = conditionsJs;
	}
	public String getExpressionJs() {
		return expressionJs;
	}
	public void setExpressionJs(String expressionJs) {
		this.expressionJs = expressionJs;
	}
	public Invocable getJsInvocable() {
		return jsInvocable;
	}
	public void setJsInvocable(Invocable jsInvocable) {
		this.jsInvocable = jsInvocable;
	}

}
