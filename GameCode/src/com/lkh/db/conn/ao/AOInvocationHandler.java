package com.lkh.db.conn.ao;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.lkh.server.socket.LogicMethod;

/**
 * AO层动态代理
 * @author tim.huang
 * 2015年12月4日
 */
public class AOInvocationHandler implements InvocationHandler {

	private BaseAO ao;
	
	public AOInvocationHandler (BaseAO ao){
		this.ao = ao;
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
//		Syn syn = method.getAnnotation(Syn.class);
//		Object result = null;
//		if(syn!=null){//异步处理
		AOSynManager.put(new LogicMethod(method, ao, args));
//		}
//		else{
//			result = method.invoke(ao, args);
//		}
		return null;
	}

}
