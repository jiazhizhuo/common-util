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
public class TestNashornPrecompile2 {

	@SuppressWarnings("restriction")
	public static void main(String[] args) throws ScriptException, NoSuchMethodException {
//		String js ="var fun1 = function(name) {\n"+
//				"    print('Hi there from Javascript, ' + name);\n"+
//				"    return 'greetings from javascript';\n"+
//				"};\n"+
//				"\n"+
//				"var fun2 = function (object) {\n"+
//				"    print('JS Class Definition: ' + Object.prototype.toString.call(object));\n"+
//				"};\n";
		String js ="var conditions = function(m,o) {\n"+
		"return 1+2;\n"+
		"};\n";
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
		engine.eval(js);

		Invocable invocable = (Invocable) engine;

//		Object result = invocable.invokeFunction("fun1", "Peter Parker");
		Object result = invocable.invokeFunction("conditions", "Peter Parker","");
		System.out.println(result);
		System.out.println(result.getClass());

	}
	
	
}

