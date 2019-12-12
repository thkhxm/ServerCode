package com.lkh.server.rpc;

import java.io.Serializable;

import org.apache.mina.core.session.IoSession;

import com.lkh.server.GameSource;
import com.lkh.tool.log.LoggerManager;
import com.lkh.util.JsonUtil;

/**
 * @author tim.huang
 * 2017年3月16日
 * 服务器信息
 */
public class RPCServer implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private String pool;//分布式池名称
	private String serverName;//节点名称
	private String ip;//节点所在IP
	private int port;//节点端口
	private int open;//是否开启   0关闭，1开启
	private boolean master;//是否主服务器
	private int sid;
	private IoSession session;
	
	public RPCServer(){}
	
	public RPCServer(String pool,String serverName, String ip, int port, int open,int sid) {
		super();
		this.pool = pool;
		this.serverName = serverName;
		this.ip = ip;
		this.port = port;
		this.open = open;
		this.sid = sid;
		this.master = false;
	}
	
	public String getAddress(){
		if(GameSource.net){
			return serverName+"."+pool+"."+GameSource.topAddress;
		}else{
			return ip;
		}
		
	}
	
	public void reset(RPCServer server){
		this.open = server.getOpen();
		this.master = server.isMaster();
	}
	
	public boolean isMaster() {
		return master;
	}

	public void setMaster(boolean master) {
		this.master = master;
	}

	public void send(Serializable obj){
		try {
			this.session.write(obj);
		} catch (Exception e) {
			LoggerManager.error("跨服协议发送异常{}", obj.toString());
		}
	}
	
	public int getSid() {
		return sid;
	}

	public boolean isOpen(){
		return this.open==1;
	}
	
	public boolean isActive(){
		boolean active = this.session!=null?this.session.isActive():false;
		LoggerManager.debug("[{}]--[{}]",this.serverName,active);
		return isOpen() && active;
	}
	
	public IoSession getSession() {
		return session;
	}

	public void setSession(IoSession session) {
		this.session = session;
	}

	public String getPool() {
		return pool;
	}

	public void setPool(String pool) {
		this.pool = pool;
	}


	public String toJson(){
		return JsonUtil.toJson(this);
	}
	
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public void setOpen(int open) {
		this.open = open;
	}
	public String getServerName() {
		return serverName;
	}
	public String getIp() {
		return ip;
	}
	public int getPort() {
		return port;
	}
	public int getOpen() {
		return open;
	}

	@Override
	public String toString() {
		return "RPCServer [pool=" + pool + ", serverName=" + serverName
				+ ", ip=" + ip + ", port=" + port + ", open=" + open
				+ ", master=" + master + ", session=" + session + "]";
	}
	
	
	
	
}
