package com.lkh.server.rpc.task;

import java.io.Serializable;


import cn.hutool.core.date.DateTime;
import com.lkh.manager.RPCManager;
import com.lkh.tool.log.LoggerManager;
import com.lkh.util.lambda.TMap;

/**
 * @author tim.huang
 * 2017年4月19日
 * RPC链式任务
 * 
 * RPCLinkedTask.build().appendTask((tid,maps,args)->{
			RPCMessageManager.sendLinkedTaskMessage(cid, null, tid,arg);
		});
 */
public class RPCLinkedTask {
	
	private volatile int index;
	
	private int maxInde;
	private IRPCTask task;
	
	private RPCLinkedTask next;
	
	private int id;
	
	private long startTime;


	
	private TMap maps;
	
	private static final long Time_Out = 1000*5;
	
	private RPCLinkedTask(int id,IRPCTask task){
		this(id,new TMap(),task);
	}
	
	private RPCLinkedTask (int id,TMap maps,IRPCTask task ){
		this.id = id;
		this.task = task;
		this.maps = maps;
	}
	
	public boolean isEnd(){
		return this.index>this.maxInde;
	}
	
	public static RPCLinkedTask build(){
		RPCLinkedTask rlt = new RPCLinkedTask(RPCManager.getId(),null);
		rlt.index = 1;
		rlt.startTime = System.currentTimeMillis();
		return rlt;
	}
	
	
	public RPCLinkedTask appendTask(IRPCTask task){
		if(this.next!=null) {
			this.next.appendTask(task);
		}
		else {
			this.next = new RPCLinkedTask(this.id,this.maps,task);
		}
		maxInde++;
		return this;
	}
	
	public void start(Serializable...objects){
		RPCManager.putAndExceute(this, objects);
	}

	public RPCLinkedTask next(){
		RPCLinkedTask rlt = this;
		for(int i = 0 ; i < this.index ; i++){
			if(rlt==null){
				LoggerManager.debug("");
			}
			rlt = rlt.next;
		}
		this.index++;
		return rlt;
	}

	public void exceute(Serializable[] objects){
		this.task.execute(this.id,this.maps,objects);
	}
	
	public boolean isTimeOut(){
		return System.currentTimeMillis()-this.startTime > RPCLinkedTask.Time_Out;
	}

	public int getId() {
		return id;
	}
	
}
