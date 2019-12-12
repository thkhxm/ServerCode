package com.lkh.server.rpc;

import java.io.Serializable;
import java.util.Set;

import com.lkh.server.proto.Request;

/**
 * @author tim.huang
 * 2017年3月31日
 * RPC服务器之间通讯使用的对象
 */
public class RPCSource extends Request implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 发送者集群池名称
	 */
	private String pool;
	
	/**
	 * 发送者节点名称
	 */
	private String sender;
	/**
	 * 接收者节点名称
	 * ALL，集群所有节点
	 * MASTER. 集群主节点
	 * XXX-01. 集群单节点 
	 */
	private Set<String> receive;
	
	public RPCSource(String pool, String sender, Set<String> receive,
			int methodCode,int rid, Serializable... args) {
		super(methodCode,rid,args);
		this.pool = pool;
		this.sender = sender;
		this.receive = receive;
	}

	public String getPool() {
		return pool;
	}

	public void setPool(String pool) {
		this.pool = pool;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public Set<String> getReceive() {
		return receive;
	}

	public void setReceive(Set<String> receive) {
		this.receive = receive;
	}

	@Override
	public String toString() {
		return "RPCSource [pool=" + pool + ", sender=" + sender + ", receive="
				+ receive + "]";
	}
	
}
