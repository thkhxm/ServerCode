package com.lkh.server.rpc.task;

import java.io.Serializable;
import java.util.Map;

import com.lkh.util.lambda.TMap;



@FunctionalInterface
public interface IRPCTask {
	
	void execute(int id,TMap maps,Serializable[] objects);
	
}
