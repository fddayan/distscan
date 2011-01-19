package com.brilig;

import junit.framework.TestCase;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

public class RhinoSpikeTest extends TestCase {

	public void test_embeded() {
		String code = "function f(x){return x+1} f(7)";
		Context cx = Context.enter();
		Scriptable scope = cx.initStandardObjects();
		
		Object result = cx.evaluateString(scope, code, "<cmd>", 1, null);
		
		System.out.println(cx.toString(result));
		cx.exit();
	}
	
	
	public void test_call_function() {
		Context cx = Context.enter();
		Scriptable scope = cx.initStandardObjects();
		String code = "function f(x){return x+1}";
		Object result = cx.evaluateString(scope, code, "<cmd>", 1, null);
		Function f = (Function)scope.get("f",scope);
		Object fresult = f.call(cx, scope, scope, new Object[]{7});
	    String report = "f('my args') = " + Context.toString(fresult);
	    System.out.println(report);
	}
	
	public void test_call_function_and_collect_variable() {
		Context cx = Context.enter();
		Scriptable scope = cx.initStandardObjects();
		String code = "x=0;function foreach(arg1){x+=arg1}";
		
		Object result = cx.evaluateString(scope, code, "<cmd>", 1, null);
		
		Function f = (Function)scope.get("foreach",scope);
		for (int i=0;i<3;i++)
			f.call(cx, scope, scope, new Object[]{2});
		
		System.out.println(Context.toString(scope.get("x",scope)));
	}
	
	public void test_call_function_with_record() {
		Context cx = Context.enter();
		Scriptable scope = cx.initStandardObjects();
		String code = "x=0;function foreach(arg1){x+=arg1}";
		
		Object result = cx.evaluateString(scope, code, "<cmd>", 1, null);
		
		Function f = (Function)scope.get("foreach",scope);
		for (int i=0;i<3;i++)
			f.call(cx, scope, scope, new Object[]{2});
		
		System.out.println(Context.toString(scope.get("x",scope)));
	}
	
//	public void test_call_object_method_and_collect_variable() {
//		Context cx = Context.enter();
//		Scriptable scope = cx.initStandardObjects();
//		String code = "{initial: { csum: 0 },reduce: function(obj,prev) { prev.csum += obj.c; }}";
//		
//		Object result = cx.evaluateString(scope, code, "<cmd>", 1, null);
//		
//		Function f = (Function)scope.get("f",scope);
//		Object fresult = f.call(cx, scope, scope, new Object[]{7});
//		fresult = f.call(cx, scope, scope, new Object[]{7});
//		fresult = f.call(cx, scope, scope, new Object[]{7});
//		
//		String report = "f('my args') = " + Context.toString(fresult);
//		System.out.println(report);
//		System.out.println(Context.toString(scope.get("x",scope)));
//	}
	
}
