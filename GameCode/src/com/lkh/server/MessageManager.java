package com.lkh.server;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.mina.core.session.IoSession;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.GeneratedMessage;
import com.lkh.manager.User;
import com.lkh.server.proto.Request;
import com.lkh.server.proto.Response;
import com.lkh.server.socket.GameServerManager;


/**
 * 协议和消息管理
 * @author tim.huang
 * 2015年12月4日
 */
public class MessageManager {

	private static ThreadLocal<PlayerGameInfo> team = new ThreadLocal<PlayerGameInfo>();
	
	public static long getPlayerId(){
		if(team.get()==null) return 0;
		return team.get().getTeamId();
	}
	
	public static IoSession getSession(){
		return team.get().getSession();
	}
	
	public static int getKeyCode(){
		return team.get().getServiceCode();
	}
	
	public static int getReqId(){
		return team.get().getReqId();
	}
	
	public static void initTeam(Request req){
		PlayerGameInfo info = team.get();
		if(info==null){
			info = new PlayerGameInfo(req.getTeamId(),req.getMethodCode(),req.getSession(),req.getReqId());
			team.set(info);
		}
		info.reset(req.getTeamId(),req.getMethodCode(),req.getSession(),req.getReqId());
//		
	}
	
	public static void remove(){
		team.remove();
	}
	
	
	/**
	 * 推送数据到客户端
	 * @param data
	 */
	public static void sendMessage(GeneratedMessage data){
		PlayerGameInfo info = team.get();
		GameServerManager.putMt(new Response(info.getServiceCode(), data,info.getReqId()),info.getSession());
	}
	
	@Deprecated
	public static void sendMessage(GeneratedMessage data,int serviceCode){
		PlayerGameInfo info = team.get();
		GameServerManager.putMt(new Response(serviceCode, data,info.getReqId()),info.getSession());
	}
	
	public static void sendMessage(long teamId,GeneratedMessage data,int serviceCode,int rid){
		User user = GameSource.getUser(teamId);
		if(user == null) return;
		GameServerManager.putMt(new Response(serviceCode, data,rid),user.getSession());
	}
	
	public static void sendMessage(long teamId,GeneratedMessage data,int serviceCode){
		User user = GameSource.getUser(teamId);
		if(user == null) return;
		GameServerManager.putMt(new Response(serviceCode, data,0),user.getSession());
	}
	
	public static void sendMessageTeamIds(List<Long> teamIds,GeneratedMessage data,int serviceCode){
		for(long teamId : teamIds) {
			User user = GameSource.getUser(teamId);
			if(user == null) continue;
			GameServerManager.putMt(new Response(serviceCode, data,0),user.getSession());
		}
	}
	
	public static void sendMessage(List<User> users,GeneratedMessage data,int serviceCode){
		byte[] b = data.toByteArray();
		Response res = new Response(serviceCode, b,true,0);
		for(User user : users){
			GameServerManager.putMt(res,user.getSession());
		}
	}
	public static void sendMessage(String service, List<Long> filterTeam, GeneratedMessage data,int serviceCode){
		Set<User> users = ServiceManager.getUsers(service);
		byte[] b = data.toByteArray();
		Response res = new Response(serviceCode, b,true,0);
		for(User user : users){
			if(user == null || filterTeam.contains(user.getTeamId())) continue;
			GameServerManager.putMt(res,user.getSession());
		}
	}
	public static void sendMessage(String service,GeneratedMessage data,int serviceCode){
		Set<User> users = ServiceManager.getUsers(service);
		byte[] b = data.toByteArray();
		Response res = new Response(serviceCode, b,true,0);
		
		Iterator<User> iter = users.iterator();
		while(iter.hasNext()){
			User u = iter.next();
			if(u==null) continue;
			GameServerManager.putMt(res,u.getSession());
		}
//		for(User user : users){
//			if(user == null) continue;
//			GameServerManager.putMt(res,user.getSession());
//		}
	}
	public static void sendMessage(long teamId,String service,GeneratedMessage data,int serviceCode){
		Set<User> users = ServiceManager.getUsers(service);
		byte[] b = data.toByteArray();
		Response res = new Response(serviceCode, b,true,0);
		for(User user : users){
			if(user == null || user.getTeamId()==teamId) continue;
			GameServerManager.putMt(res,user.getSession());
		}
	}
	
	static class PlayerGameInfo {
		private long teamId;
		private int serviceCode;
		private IoSession session;
		private int reqId;
		private long startTime;
		
		protected PlayerGameInfo(long teamId,IoSession session,int reqId){
			this(teamId,0,session,reqId);
		}
		
		protected PlayerGameInfo(long teamId,int serviceCode,IoSession session,int reqId){
			this.teamId = teamId;
			this.serviceCode = serviceCode;
			this.session = session;
			this.startTime = System.currentTimeMillis();
			this.reqId = reqId;
		}
		
		public void reset(long teamId,int serviceCode,IoSession session,int reqId){
			this.teamId = teamId;
			this.serviceCode = serviceCode;
			this.session = session;
			this.startTime = System.currentTimeMillis();
			this.reqId = reqId;
		}

		private long getTeamId() {
			return teamId;
		}

		public int getServiceCode() {
			return serviceCode;
		}

		private IoSession getSession() {
			return session;
		}
		
		public int getReqId() {
			return reqId;
		}

		/**
		 * 耗时
		 * @return
		 */
		public long getTime(){
			return System.currentTimeMillis()-startTime;
		}
		
		
	}
	
}
