package com.jzz.test.nashorn;

import java.util.*;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * 测试 java 的 js 引擎
 * @author JZZ
 */
public class TestNashorn {

	public static void main(String[] args) throws ScriptException, NoSuchMethodException {
		
		String conditionsJs = "GOODS_CATALOG=='T_001'";
		String expressionJs= "GOODS_PRICES*0.9-5";
		
		List<LiquidationBaseQuote> liquidationFactors = new ArrayList<LiquidationBaseQuote>();
		liquidationFactors.add(new LiquidationBaseQuote("GOODS_CATALOG", "goodsCatalog",1));
		liquidationFactors.add(new LiquidationBaseQuote("GOODS_PRICES", "goodsPrices",2));
		
		
		//mapping GOODS_CATALOG:goodsCatalog:string; GOODS_PRICES:goodsPrices,number;
		Map<String, Object> orderInfo = new HashMap<String,Object>();
		orderInfo.put("goodsCatalog", "T_001");
		orderInfo.put("goodsPrices", 98);
		orderInfo.put("goodsName", "商品001");
		
		//初始化清算因子
		StringBuffer initLiquidationFactorJS = generateVariableInit(liquidationFactors, orderInfo);
		
		
		Object result = null;
		
		System.out.println("execute 适用条件: "+ initLiquidationFactorJS + conditionsJs);
		result = jsCalculate(initLiquidationFactorJS + conditionsJs);
		System.out.println( result.getClass() + "\t" + result );
		
		System.out.println("execute 计算公式: "+ initLiquidationFactorJS + expressionJs);
		result = jsCalculate(initLiquidationFactorJS + expressionJs);
		System.out.println( result.getClass() + "\t" + result );
		
	}
	

	public static StringBuffer generateVariableInit(List<LiquidationBaseQuote> liquidationFactors,
			Map<String, Object> orderInfo) {
		StringBuffer sb = new StringBuffer(128);
		String quoteValue = null;
		for(LiquidationBaseQuote quote : liquidationFactors) {
			quoteValue = quote.getQuoteValue();
			orderInfo.get( quoteValue );
			
			switch (quote.getValueType()) {
			case 1://string
				sb.append(quote.getQuoteName()).append("='").append(orderInfo.get( quoteValue )).append("';");
				break;
			case 2://number
				sb.append(quote.getQuoteName()).append("=").append(orderInfo.get( quoteValue )).append(";");
				break;
			default:
				new RuntimeException("不识别的类型");
				break;
			}
			
		}
		return sb;
	}


	/**
	   * JS计算
	   * 
	   * @param script
	   * @return
	   */
	  public static Object jsCalculate(String script) {
	    ScriptEngineManager manager = new ScriptEngineManager();
	    ScriptEngine engine = manager.getEngineByName("nashorn");
	    try {
	      return (Object) engine.eval(script);
	    } catch (ScriptException ex) {
	      ex.printStackTrace();
	    }
	    return null;
	  }
}

