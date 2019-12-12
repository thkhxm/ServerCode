package com.lkh.server.rpc;

import java.lang.reflect.Method;

import com.lkh.enums.ERPCType;
import com.lkh.server.socket.ServerMethod;

public class RPCServerMethod extends ServerMethod {
	
	private String pool;
	private ERPCType type;
	
	public RPCServerMethod(int keyCode, Method method, Object instanceObject,
			String name,String pool,ERPCType type) {
		super(keyCode, method, instanceObject, name);
		this.pool = pool;
		this.type = type;
	}

	public String getPool() {
		return pool;
	}

	public ERPCType getType() {
		return type;
	}
	
	
	
}
