package com.jzz.test.nashorn;

import java.util.*;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import jdk.nashorn.api.scripting.*;

/**
 * 测试 java 的 js 引擎
 * @author JZZ
 */
public class TestNashornPrecompile {

	@SuppressWarnings("restriction")
	public static void main(String[] args) throws ScriptException, NoSuchMethodException {
		
		String conditionsJs = "o[m['GOODS_CATALOG']] == 'T_001'";
//		String conditionsJs = "o.m['GOODS_CATALOG'] == 'T_001'";
		String expressionJs= "o[m['GOODS_PRICES']] * 0.9 - 5";
		
//		List<LiquidationBaseQuote> liquidationFactors = new ArrayList<LiquidationBaseQuote>();
//		liquidationFactors.add(new LiquidationBaseQuote("GOODS_CATALOG", "goodsCatalog",1));
//		liquidationFactors.add(new LiquidationBaseQuote("GOODS_PRICES", "goodsPrices",2));
		
//		Map<String, Object> orderInfo = new HashMap<String,Object>();
//		orderInfo.put("goodsCatalog", "T_001");
//		orderInfo.put("goodsPrices", 98);
//		orderInfo.put("goodsName", "商品001");
		OrderInfo orderInfo = new OrderInfo("T_001", (double) 98, "商品001");
		
		Object result = null;
		
		
		Map<String,String> mapping = new HashMap<String,String>();
		mapping.put("GOODS_CATALOG", "goodsCatalog");
		mapping.put("GOODS_PRICES", "goodsPrices");
		mapping.put("GOODS_NAME", "goodsName");
		
		Invocable invocable = (Invocable)generateLiquidationFuction(conditionsJs, expressionJs);
		System.out.println(invocable.invokeFunction("conditions", mapping, orderInfo));
		System.out.println(invocable.invokeFunction("expression", mapping, orderInfo));

		
//		ScriptObjectMirror invJs = generateJsFuction(conditionsJs);
//		result = invJs.call(null, mapping, orderInfo);
//		System.out.println( result.getClass() + "\t" + result );
//		System.out.println( result.getClass() + "\t" + result );
	}
	
	
	// 生成 js 方法
	private static ScriptObjectMirror generateJsFuction(String expression) throws ScriptException {//Liquidation
		StringBuffer sb = new StringBuffer(128);
		sb.append("function rule(m,o) {")
		    .append("return ").append(expression).append(";")
	    .append("}");

		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("javascript");//nashorn javascript
		
		System.out.println(sb.toString());
		ScriptObjectMirror inv = (ScriptObjectMirror) engine.eval(sb.toString());
		return inv;
	}
	// 生成 js 方法
	private static Invocable  generateLiquidationFuction(String conditions,String expression) throws ScriptException, NoSuchMethodException {//Liquidation
		StringBuffer sb = new StringBuffer(128);
		sb.append("var conditions = function (m,o) {")
		.append("print(m);print(o);\n")
		.append("print(m['GOODS_NAME']);\n")
		.append("print(o.goodsName);\n")
		.append("print(o.getClass());\n")
		.append("print(o['goodsName']);\n")
		    .append("\nreturn ").append(conditions).append(";\n")
	    .append("};\n");
		sb.append("var expression = function (m,o) {")
		    .append("\nreturn ").append(expression).append(";\n")
	    .append("};");

		ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");//nashorn javascript
		
//		System.out.println(sb.toString()); 
		engine.eval(sb.toString());
		return (Invocable) engine;
	}
}
