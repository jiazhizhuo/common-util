package com.jzz.test.mozilla.rhino;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.Invocable;
import javax.script.ScriptException;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Scriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jzz.test.nashorn.LiquidationBaseQuote;
import com.jzz.test.nashorn.LiquidationRule;
import com.jzz.test.nashorn.OrderInfo;
import com.jzz.test.nashorn.TestNashorn;
import com.jzz.test.nashorn.TestNashornPrecompile3;

public class TestPerformance {
	public static final String _expression = "_expression";
	public static final String _conditions = "_conditions";
	private static Logger logger = LoggerFactory.getLogger(TestPerformance.class);
	
	public static final Scriptable scope;
	static {
		Scriptable tmp = null;
		try {
			logger.info("初始化脚本引擎");
			
			Context ctx = ContextFactory.getGlobal().enterContext();
			ctx.setOptimizationLevel(9);
			tmp = new ImporterTopLevel(ctx);
			tmp.put("logger", tmp, logger);
		} catch (Exception e) {
			tmp = null;
			logger.error("初始化脚本引擎失败", e);
		} finally {
			scope = tmp;
			Context.exit();
		}
	}

	public static void main(String[] args) throws NoSuchMethodException, ScriptException {
//		String conditionsJs = "o[m['GOODS_CATALOG']] == 'T_001'";
//		String expressionJs= "o[m['GOODS_PRICES']] * 0.9 - 5";
		String conditionsJs = "o.get(m.get('GOODS_CATALOG')) == 'T_001'";
		String expressionJs= "o.get(m.get('GOODS_PRICES')) * 0.9 - 5";
		//清算规则
		LiquidationRule lr = new LiquidationRule(conditionsJs, expressionJs);
		
		// 订单对象
//		OrderInfo o = new OrderInfo("T_001", (double) 98, "商品001");
		Map<String,Object> o = new HashMap<String,Object>();
		o.put("goodsCatalog", "T_001");
		o.put("goodsPrices", 98);
		o.put("goodsName", "商品001");
		
		// 订单基础信息映射<别名，接口字段>
		Map<String,String> m = new HashMap<String,String>();
		m.put("GOODS_CATALOG", "goodsCatalog");
		m.put("GOODS_PRICES", "goodsPrices");
		m.put("GOODS_NAME", "goodsName");
		
		
		Context ctx = ContextFactory.getGlobal().enterContext();
		Function func = null;
		func = generateLiquidationFunc(lr.getConditionsJs(), lr.getExpressionJs());// 把 invocable 缓存到清算规则对象里
		// ------------------------------------------------------------------------------------------------------------------------------------
		Integer times = 1000000;
		Long time = System.currentTimeMillis();
		logger.info("mozilla rhino Function.call() ------------------------------------------");
		logger.info( func.call(ctx, scope, func, new Object[]{1,m,o}).toString() );
		logger.info( func.call(ctx, scope, func, new Object[]{2,m,o}).toString() );
		for(int i=0;i<times;i++) {
			func.call(ctx, scope, func, new Object[]{1,m,o});
		}
		logger.info(time- System.currentTimeMillis() + "ms");
		time = System.currentTimeMillis();
		for(int i=0;i<times;i++) {
			func.call(ctx, scope, func, new Object[]{2,m,o});
		}
		logger.info(time- System.currentTimeMillis() + "ms");
		
		
		
		// ------------------------------------------------------------------------------------------------------------------------------------
		String conditionsJs1 = "o[m['GOODS_CATALOG']] == 'T_001'";
		String expressionJs1= "o[m['GOODS_PRICES']] * 0.9 - 5";
		// 订单对象
		OrderInfo orderInfo = new OrderInfo("T_001", (double) 98, "商品001");
		// 订单基础信息映射<别名，接口字段>
		Map<String,String> mapping = new HashMap<String,String>();
		mapping.put("GOODS_CATALOG", "goodsCatalog");
		mapping.put("GOODS_PRICES", "goodsPrices");
		mapping.put("GOODS_NAME", "goodsName");
		Invocable jsInvocable = null;
		jsInvocable = (Invocable)TestNashornPrecompile3.generateLiquidationFuction(conditionsJs1, expressionJs1);// 把 invocable 缓存到清算规则对象里

		
		logger.info("nashorn Invocable.invokeFunction() ------------------------------------------");
		logger.info( jsInvocable.invokeFunction(_conditions, mapping, orderInfo).toString() );
		logger.info( jsInvocable.invokeFunction(_expression, mapping, orderInfo).toString() );
		time = System.currentTimeMillis();
		for(int i=0;i<times;i++) {
			jsInvocable.invokeFunction(_conditions, mapping, orderInfo);
		}
		logger.info(time- System.currentTimeMillis() + "ms");
		time = System.currentTimeMillis();
		for(int i=0;i<times;i++) {
			jsInvocable.invokeFunction(_expression, mapping, orderInfo);
		}
		logger.info(time- System.currentTimeMillis() + "ms");
		
		
		// ------------------------------------------------------------------------------------------------------------------------------------
		String conditionsJs2 = "GOODS_CATALOG=='T_001'";
		String expressionJs2 = "GOODS_PRICES*0.9-5";
		
		time = System.currentTimeMillis();
		List<LiquidationBaseQuote> liquidationFactors = new ArrayList<LiquidationBaseQuote>();
		liquidationFactors.add(new LiquidationBaseQuote("GOODS_CATALOG", "goodsCatalog",1));
		liquidationFactors.add(new LiquidationBaseQuote("GOODS_PRICES", "goodsPrices",2));
		
		Map<String, Object> orderInfo2 = new HashMap<String,Object>();
		orderInfo2.put("goodsCatalog", "T_001");
		orderInfo2.put("goodsPrices", 98);
		orderInfo2.put("goodsName", "商品001");
		StringBuffer initLiquidationFactorJS = TestNashorn.generateVariableInit(liquidationFactors, orderInfo2);
		logger.info("nashorn engine.eval() ------------------------------------------");
		logger.info( TestNashorn.jsCalculate(initLiquidationFactorJS + conditionsJs2).toString() );
		logger.info( TestNashorn.jsCalculate(initLiquidationFactorJS + expressionJs2).toString() );
		for(int i=0;i<times/10000;i++) {
			TestNashorn.jsCalculate(initLiquidationFactorJS + conditionsJs2);
			TestNashorn.jsCalculate(initLiquidationFactorJS + expressionJs2);
		}
		logger.info(time- System.currentTimeMillis() + "ms");
				


	}
	
	public static Function generateLiquidationFunc(String conditions,String expression) {
		Context ctx = ContextFactory.getGlobal().enterContext();
		ctx.setOptimizationLevel(9);
		
		Function fun = null;
		fun = ctx.compileFunction(scope,
				"function(opr,m,o){ "
						+ "if(opr==1){"
						+ "return "+conditions+";"
						+ "}"
						+ "if(opr==2){"
						+ "return "+expression+";"
						+ "}"
				+ "}", "liquidationRuleFunc", 1, null);
		return fun;
	}
	
	
	
}
