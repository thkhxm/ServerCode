package com.lkh.server;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.lkh.manager.RPCManager;
import com.lkh.server.proto.Request;
import com.lkh.server.proto.Response;
import com.lkh.server.rpc.RPCClientKeepAlive;
import com.lkh.server.rpc.RPCServer;
import com.lkh.server.rpc.RPCSource;
import com.lkh.server.rpc.coder.RPCCodecFactory;
import com.lkh.server.rpc.handler.RPCHandler;
import com.lkh.tool.log.LoggerManager;

public class RPCMessageManager {
	
	private static ThreadLocal<RPCSource> source = new ThreadLocal<RPCSource>();
	
	private static List<RPCServer> routeServerList = Lists.newArrayList();
	private static AtomicLong index = new AtomicLong(0);
	
	public static void addRouteServer(RPCServer server,RPCServer local,boolean replace){
		if(!server.isOpen()) return;
		RPCServer old = routeServerList.stream()
				.filter(s->s.getServerName().equals(server.getServerName()))
				.findFirst().orElse(null);
		//
		if(old == null || old.getSession()==null || !old.getSession().isActive()){//与路由节点断开，重新创建路由的session连接
			IoConnector connector = new NioSocketConnector();
			connector.setHandler(new RPCHandler());
			connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new RPCCodecFactory()));
			KeepAliveFilter kaf = new KeepAliveFilter(new RPCClientKeepAlive(), IdleStatus.READER_IDLE,new KeepAliveRequestTimeoutHandler() {
				@Override
				public void keepAliveRequestTimedOut(KeepAliveFilter arg0, IoSession arg1)
						throws Exception {
					LoggerManager.error("节点与路由之间的链接断开，触发重连[{}]", arg1);
					addRouteServer(server, local, replace);
				}
			});
			kaf.setForwardEvent(true);
			kaf.setRequestInterval(5);
			kaf.setRequestTimeout(20);
			connector.getFilterChain().addLast("heart", kaf);
			ConnectFuture connFuture = connector.connect(new InetSocketAddress(server.getIp(), server.getPort()));
			connFuture.awaitUninterruptibly();
			IoSession session = connFuture.getSession();
			LoggerManager.debug("client is conn,ip[{}],port[{}]",server.getIp(), server.getPort());
			server.setSession(session);
			routeServerList.add(server);
		}
		
//		if(!replace && old!=null) return; //不用替换,并且已经添加过的路由节点,不重复添加
		LoggerManager.debug("节点连接创建成功，准备注册路由信息->");
		server.getSession().write(local);
		LoggerManager.debug("节点连接创建成功，完成注册路由信息->");
	}
	
	static final Response req = new Response(Request.Socket_Server_KeepAlive, new byte[0],false, 0);
	static final Response res = new Response(Request.Socket_Server_KeepAlive_CallBack, new byte[0],false, 0);
	class KeepAliveMessageFactoryImpl implements KeepAliveMessageFactory{
		
		@Override
		public Object getRequest(IoSession arg0) {
			return req;
		}

		@Override
		public Object getResponse(IoSession arg0, Object arg1) {
			return res;
		}

		@Override
		public boolean isRequest(IoSession arg0, Object arg1) {
			return (arg1 instanceof Request && ((Request)arg1).getMethodCode() == Request.Socket_KeepAlive);
		}

		@Override
		public boolean isResponse(IoSession arg0, Object arg1) {
			return (arg1 instanceof Request && ((Request)arg1).getMethodCode() == Request.Socket_KeepAlive_CallBack);
		}
		
	}
	
	
	public static void checkRouteServer(List<String> serverName){
		routeServerList = routeServerList.stream()
				.filter(route->serverName.contains(route.getServerName()))
				.collect(Collectors.toList());
	}
	
	public static void initSource(RPCSource s){
		source.set(s);
	}
	
	public static RPCSource get(){
		return source.get();
	}
	
	/**
	 * 发送请求，到指定节点，无返回
	 * 
	 */
	public static void sendMessage(int code,String node,Serializable... args){
		RPCServer route = getRouteServer();
		if(route == null) return;
		RPCSource source = new RPCSource(GameSource.pool,GameSource.serverName,node==null?null:Sets.newHashSet(node),code,0,args);
		route.getSession().write(source);
	}
	/**
	 * 发送请求，到指定节点，无返回
	 * 
	 */
	public static void sendMessageNodes(int code,Set<String> nodes,Serializable... args){
		RPCServer route = getRouteServer();
		if(route == null) return;
		RPCSource source = new RPCSource(GameSource.pool,GameSource.serverName,nodes,code,0,args);
		route.getSession().write(source);
	}
	
	public static void sendLinkedTaskMessage(int code,String node,int id,Serializable... args){
		RPCServer route = getRouteServer();
		if(route == null) return;
		RPCSource source = new RPCSource(GameSource.pool,GameSource.serverName,node==null?null:Sets.newHashSet(node),code,id,args);
		route.getSession().write(source);
	}
	
	/**
	 * 发送请求，到指定节点，
	 * 节点间响应请求，也使用该方法进行响应
	 */
	public static void responseMessage(Serializable... args){
		RPCServer route = getRouteServer();
		if(route == null) return;
		RPCSource local  = get();
		if(local == null) return;
		RPCSource source = new RPCSource(GameSource.pool,GameSource.serverName,Sets.newHashSet(local.getSender()),RPCManager.CallBack,local.getReqId(),args);
		LoggerManager.debug("回调一次异步请求{}",local.getReqId());
		route.getSession().write(source);
	}
	
	
	
	public static void register(RPCServer local){
//		routeServerList.stream().filter(route->route.isActive()).forEach(route->);
	}
	
	//递归取存活的路由服务器
	private static RPCServer getRouteServer(){
		if(routeServerList.size() == 0) return null;
		int i = (int)(index.incrementAndGet()%routeServerList.size());
		RPCServer s = routeServerList.get(i);
		if(!s.isOpen()){//判断该路由是否有效
			return getRouteServer();
		}
		return s;
	}
	
	

}
