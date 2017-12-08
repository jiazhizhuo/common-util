package com.jzz.test.mozilla.rhino;

import java.util.HashMap;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Scriptable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jzz.test.nashorn.LiquidationRule;
import com.jzz.test.nashorn.OrderInfo;

public class TestCompileJs {
	private static Logger logger = LoggerFactory.getLogger(TestCompileJs.class);
	
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

	public static void main(String[] args) {
		String conditionsJs = "o[m['GOODS_CATALOG']] == 'T_001'";
		String expressionJs= "o[m['GOODS_PRICES']] * 0.9 - 5";
		//清算规则
		LiquidationRule lr = new LiquidationRule(conditionsJs, expressionJs);
		
		// 订单对象
		OrderInfo o = new OrderInfo("T_001", (double) 98, "商品001");
		
		// 订单基础信息映射<别名，接口字段>
		Map<String,String> m = new HashMap<String,String>();
		m.put("GOODS_CATALOG", "goodsCatalog");
		m.put("GOODS_PRICES", "goodsPrices");
		m.put("GOODS_NAME", "goodsName");
		
		
		Context ctx = ContextFactory.getGlobal().enterContext();
		Function func = null;
		func = generateLiquidationFunc(lr.getConditionsJs(), lr.getExpressionJs());// 把 invocable 缓存到清算规则对象里
		
		System.out.println(func.call(ctx, scope, func, new Object[]{1,m,o}));
		System.out.println(func.call(ctx, scope, func, new Object[]{2,m,o}));
		
	}
	
	public static Function generateLiquidationFunc(String conditions,String expression) {
		Context ctx = ContextFactory.getGlobal().enterContext();
		ctx.setOptimizationLevel(9);
		
		Function fun = null;
		fun = ctx.compileFunction(scope,
				"function(opr,m,o){ "
						+"logger.info(m.get('GOODS_CATALOG'));"
						+"logger.info(m['GOODS_CATALOG']);"
						+"logger.info(m);"
						+"logger.info(o);"
						
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
