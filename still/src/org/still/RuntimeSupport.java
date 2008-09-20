package org.still;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.still.obj.CallableStillObject;
import org.still.obj.JavaStillObject;
import org.still.obj.PrototypeStillObject;
import org.still.obj.StillFunction;
import org.still.obj.StillObject;
import org.still.src.Expression;
import org.still.src.Symbol;

public class RuntimeSupport {
	public static RuntimeContext initDefault() {
		final RuntimeContext ctx = new RuntimeContext();
		ctx.set(Symbol.get("__runtime-suport"), new StillObject() {
			@Override
			public StillObject get(final Symbol name) {
				return new CallableStillObject() {
					@Override
					public StillObject apply(StillObject thisRef, List<StillObject> params) {
						if(name.value.equals("post-wrap-integer")) {
							return postWrapInteger((JavaStillObject) params.get(0));
						}
						if(name.value.equals("post-wrap-string")) {
							return postWrapString((JavaStillObject) params.get(0));
						}
						throw new RuntimeException("Unknown method call.");
					}

					@Override
					public StillObject get(Symbol name) {
						throw new UnsupportedOperationException();
					}

					@Override
					public StillObject set(Symbol name, StillObject obj) {
						throw new UnsupportedOperationException();
					}};
			}

			@Override
			public StillObject set(Symbol name, StillObject obj) {
				throw new UnsupportedOperationException();
			}});
		return ctx;
	}
	
	private static final Expression INT_ADD_EXPRESSION = Context.get().parser.parseExpression("(thisCtx.__runtime-suport).post-wrap-integer[(this.__wrap-value).add[other.__wrap-value]]");
	private static final Expression INT_MUL_EXPRESSION = Context.get().parser.parseExpression("(thisCtx.__runtime-suport).post-wrap-integer[(this.__wrap-value).multiply[other.__wrap-value]]");
	private static final Expression INT_TO_STRING = Context.get().parser.parseExpression("this.__wrap-value");
	
	public static StillObject newInteger(BigInteger value) {
		JavaStillObject wrappedValue = new JavaStillObject(value);
		return postWrapInteger(wrappedValue);
	}

	private static StillObject postWrapInteger(JavaStillObject wrappedValue) {
		StillObject obj = new PrototypeStillObject();
		obj.set(Symbol.get("__wrap-value"), wrappedValue);
		
		List<Symbol> params = new ArrayList<Symbol>();
		params.add(Symbol.get("other"));
		
		RuntimeContext rootCtx = Context.get().rootCtx;
		
		obj.set(Symbol.get("+"), new StillFunction(rootCtx, params, INT_ADD_EXPRESSION));
		obj.set(Symbol.get("add"), new StillFunction(rootCtx, params, INT_ADD_EXPRESSION));
		
		obj.set(Symbol.get("*"), new StillFunction(rootCtx, params, INT_MUL_EXPRESSION));
		obj.set(Symbol.get("mul"), new StillFunction(rootCtx, params, INT_MUL_EXPRESSION));
		obj.set(Symbol.get("multiply"), new StillFunction(rootCtx, params, INT_MUL_EXPRESSION));
		
		obj.set(Symbol.get("to-string"), new StillFunction(rootCtx, Collections.<Symbol>emptyList(), INT_TO_STRING));
		
		return obj;
	}
	
	private static final Expression STR_CONCAT_EXPRESSION = Context.get().parser.parseExpression("(thisCtx.__runtime-suport).post-wrap-string[(this.__wrap-value).concat[other.__wrap-value]]");
	private static final Expression STR_TO_STRING = Context.get().parser.parseExpression("this.__wrap-value");
	
	public static StillObject newString(String value) {
		JavaStillObject wrappedValue = new JavaStillObject(value);
		return postWrapString(wrappedValue);
	}

	private static StillObject postWrapString(JavaStillObject wrappedValue) {
		StillObject obj = new PrototypeStillObject();
		obj.set(Symbol.get("__wrap-value"), wrappedValue);
		
		List<Symbol> params = new ArrayList<Symbol>();
		params.add(Symbol.get("other"));

		RuntimeContext rootCtx = Context.get().rootCtx;
		
		obj.set(Symbol.get("+"), new StillFunction(rootCtx, params, STR_CONCAT_EXPRESSION));
		obj.set(Symbol.get("concat"), new StillFunction(rootCtx, params, STR_CONCAT_EXPRESSION));

		obj.set(Symbol.get("to-string"), new StillFunction(rootCtx, Collections.<Symbol>emptyList(), STR_TO_STRING));
		
		return obj;
	}
}
