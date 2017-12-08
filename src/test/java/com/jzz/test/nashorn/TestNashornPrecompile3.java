package com.jzz.test.nashorn;

import java.util.HashMap;
import java.util.Map;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * 测试 java 的 js 引擎
 * @author JZZ
 */
public class TestNashornPrecompile3 {
	
	public static final String _expression = "_expression";
	public static final String _conditions = "_conditions";

	@SuppressWarnings("restriction")
	public static void main(String[] args) throws ScriptException, NoSuchMethodException {
		
		String conditionsJs = "o[m['GOODS_CATALOG']] == 'T_001'";
		String expressionJs= "o[m['GOODS_PRICES']] * 0.9 - 5";
		//清算规则
		LiquidationRule lr = new LiquidationRule(conditionsJs, expressionJs);
		
		// 订单对象
		OrderInfo orderInfo = new OrderInfo("T_001", (double) 98, "商品001");
		
		// 订单基础信息映射<别名，接口字段>
		Map<String,String> mapping = new HashMap<String,String>();
		mapping.put("GOODS_CATALOG", "goodsCatalog");
		mapping.put("GOODS_PRICES", "goodsPrices");
		mapping.put("GOODS_NAME", "goodsName");
		
		
		Invocable jsInvocable = null;
		if (lr.getJsInvocable() == null) {
			jsInvocable = (Invocable)generateLiquidationFuction(lr.getConditionsJs(), lr.getExpressionJs());// 把 invocable 缓存到清算规则对象里
			lr.setJsInvocable(jsInvocable);
		} else {
			jsInvocable = lr.getJsInvocable();
		// 对条件公式、计算公式 进行js拼接，编译。
		}
		
		
		Object result = null;
		// 执行 条件公式
		result = jsInvocable.invokeFunction(_conditions, mapping, orderInfo);
		System.out.println(result.getClass() +"\t"+ result.toString());
		// 执行 条件公式
		result = jsInvocable.invokeFunction(_expression, mapping, orderInfo);
		System.out.println(result.getClass() +"\t"+ result.toString());
	}
	
	// 生成清算规则的计算对象
	public static Invocable  generateLiquidationFuction(String conditions,String expression) throws ScriptException, NoSuchMethodException {//Liquidation
		StringBuffer sb = new StringBuffer(128);
		sb.append("var ").append(_conditions).append(" = function (m,o) {")
		    .append("\nreturn ").append(conditions).append(";\n")
	    .append("};\n");
		sb.append("var ").append(_expression).append(" = function (m,o) {")
		    .append("\nreturn ").append(expression).append(";\n")
	    .append("};");

		ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");//nashorn javascript
		
		engine.eval(sb.toString());
		return (Invocable) engine;
	}
}
