package com.lkh.server.proto;

import org.apache.mina.core.session.IoSession;

import com.lkh.manager.RPCManager;
import com.lkh.server.GameSource;
import com.lkh.server.instance.InstanceFactory;
import com.lkh.server.rpc.RPCSource;
import com.lkh.server.socket.ServerMethod;
import com.lkh.tool.log.LoggerManager;
import com.lkh.util.StringUtil;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Request implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	IoSession session;	
	long startTime;
	long playerId = 0;
	int reqId;
	int methodCode;
//	LogicMethod method;
	ServerMethod serverMethod = null;
	Serializable[] attributeArray = null;
	transient int msgLength;
	
//	public static final String SERVICE_CODE_KEY = "servicecode";
	private static final String INT = int.class.getSimpleName();
	private static final String FLOAT = float.class.getSimpleName();
	private static final String LONG = long.class.getSimpleName();
	
	//客户端心跳请求
	public static final int Socket_KeepAlive = -99;
	
	//客户端心跳响应
	public static final int Socket_KeepAlive_CallBack = -100;
	
	//服务器心跳请求
	public static final int Socket_Server_KeepAlive = -97;
	
	//服务器心跳响应
	public static final int Socket_Server_KeepAlive_CallBack = -98;
	
	
	
	public Request(int methodCode,int rid,Serializable[] attributeArray){
		this.methodCode = methodCode;
		this.attributeArray = attributeArray;
		this.reqId = rid;
	}
	
	public Request(IoSession session,String datas,boolean isInit,int reqId,int serviceCode, int msgLength){
		setSession(session);
		this.reqId = reqId;
		this.methodCode = serviceCode;
		//在放入队列前，初始化所需信息，降低吞吐量，提高业务处理速度
		if(isInit && serviceCode!=Socket_KeepAlive) {
			setData(datas, serviceCode);
		}
		this.msgLength = msgLength;
	}
	
	public void invoke()throws Exception{
		if(serverMethod == null){//RPC推送，method默认为空，从本地注册的方法中重新取出来
			serverMethod = InstanceFactory.get().getServerMethodByCode(methodCode);
		}
		
		if(serverMethod==null){//本地调用，RPC推送都没找到，异常code
			LoggerManager.error("发现无效接口------------>[{}]", methodCode);
		}else if(methodCode == RPCManager.CallBack && this instanceof RPCSource){//路由转发过来的链接异步响应
			RPCManager.requestExceute(getReqId(), getAttributeArray());
		}else{
			serverMethod.getMethod().invoke(serverMethod.getInstanceObject(), (Object[]) attributeArray);
		}
		LoggerManager.debug("{}-请求执行耗时:{}",methodCode, end());
	}
	
//	public Object invokeCallBack() throws Exception{
//		return method.invokeCallBack();
//	}
	
//	public int getKeyCode(){
//		return this.methodCode;
////		return getMethod().getMethod().getKeyCode();
//	}
//	
//	private LogicMethod getMethod() {
//		return method;
//	}

	private void setData(String datas,int serviceCode) {
		String[] s = StringUtil.toStringArray(datas, "[¤]");
		String value="";
		serverMethod = InstanceFactory.get().getServerMethodByCode(serviceCode);
		if(serverMethod.getKeyCode()==0) return;
		if(serverMethod.getAttributeType().length<=0) return;
		attributeArray = new Serializable[serverMethod.getAttributeType().length];
//		LoggerManager.debug("测试前台传参{},{}", serviceCode,datas);
 		for (int i = 0; i < s.length; i++) {
			value = s[i];
			attributeArray[i] = getAttirbute(
					serverMethod.getAttributeType()[i], value);
		}
//		method = new LogicMethod(serverMethod, attributeArray);
	}
	
	private Serializable getAttirbute(Class<?> cla,String val){
		String name = cla.getSimpleName();
		if(name.equals(Request.INT)){
			return Integer.parseInt(val);
		}else if(name.equals(Request.FLOAT)){
			return Float.parseFloat(val);
		}else if(name.equals(Request.LONG)){
			return Long.parseLong(val);
		}else return val;
	}
	
	private void setSession(IoSession session) {
		this.session = session;		
		Long teamid = (Long)session.getAttribute(GameSource.Player_Session_Key);
		if(teamid!=null){
			this.playerId = teamid;
		}
//		else if(GameSource.isDebug){
//			teamid = Math.abs(new Random().nextLong());
//			this.teamId = teamid;
//			session.setAttribute("teamId",teamid);
//		}
	}
	
	public int getMethodCode() {
		return methodCode;
	}

	public void setMethodCode(int methodCode) {
		this.methodCode = methodCode;
	}
	public long getTeamId() {
		return playerId;
	}
	public void setTeamId(long teamId) {
		this.playerId = teamId;
	}
	public IoSession getSession() {
		return session;
	}
	
	public int getReqId() {
		return reqId;
	}

	public void setReqId(int reqId) {
		this.reqId = reqId;
	}

	public ServerMethod getServerMethod() {
		return serverMethod;
	}

	public Serializable[] getAttributeArray() {
		return attributeArray;
	}

	public void start(){
		this.startTime = System.currentTimeMillis();
	}
	
	public long end(){
		return System.currentTimeMillis()-this.startTime;
	}

	public int getMsgLength() {
		return msgLength;
	}

	public String getAttrsSimpleString(){
		return attrsStr(attributeArray);
	}

	private static final int STR_MAX_ELEMENT = 12;

	public static String attrsStr(Object[] attrs) {
		if (attrs == null || attrs.length == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		int len = Math.min(attrs.length, STR_MAX_ELEMENT);
		for (int i = 0; i < len; i++) {//pre 12
			Object obj = attrs[i];
			if (obj instanceof List) {
				List<?> list = (List<?>) obj;
				int size = list.size();
				if (size > STR_MAX_ELEMENT) {
					sb.append(list.subList(0, STR_MAX_ELEMENT))
							.append(" and more ")
							.append(size - STR_MAX_ELEMENT)
							.append(" elements");
				} else {
					sb.append(list);
				}
			} else if (obj instanceof Set) {
				Set<?> set = (Set<?>) obj;
				sb.append(setToString(set));
			} else {
				sb.append(obj);
			}

			if (i < len - 1) {
				sb.append(',').append(' ');
			}
		}
		return sb.toString();
	}

	private static String setToString(Set<?> set) {
		Iterator<?> it = set.iterator();
		if (!it.hasNext()) {
			return "[]";
		}

		StringBuilder sb = new StringBuilder();
		sb.append('[');
		int count = 0;
		while (it.hasNext() && count < STR_MAX_ELEMENT) {
			Object e = it.next();
			sb.append(e == set ? "(this Collection)" : e);
			if (!it.hasNext()) {
				return sb.append(']').toString();
			}
			sb.append(',').append(' ');
			count++;
		}
		if (it.hasNext()) {
			sb.append("and more elements");
		}
		return sb.toString();
	}

}
