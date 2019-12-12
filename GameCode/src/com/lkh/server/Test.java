package com.lkh.server;

import com.google.common.primitives.Ints;
import com.lkh.server.proto.Request;
import com.lkh.server.rpc.RPCServer;
import com.lkh.server.rpc.RPCSource;



public class Test{
	
	private RPCServer server;
	
	public Test(RPCServer server){
		this.server = server;
	}
	
	

	public static void main(String[] args) throws Throwable{
		int s = 128;
		System.err.println(Ints.toByteArray(128));
	}
	
}
