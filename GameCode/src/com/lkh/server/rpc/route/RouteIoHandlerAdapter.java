package com.lkh.server.rpc.route;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.lkh.server.rpc.RPCServer;
import com.lkh.server.rpc.RPCSource;
import com.lkh.server.rpc.handler.RPCHandler;
import com.lkh.tool.log.LoggerManager;

/**
 * @author tim.huang
 * 2017年4月7日
 */
public class RouteIoHandlerAdapter extends IoHandlerAdapter{

	public final static Logger log = LogManager.getLogger(RouteIoHandlerAdapter.class);
	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		if(message instanceof RPCSource){//跨服节点数据类型
			//将数据放入消息处理线程，增加路由吞吐量
			RouteServerManager.submit((RPCSource)message);
		}else if(message instanceof RPCServer){//
			RPCServer server = (RPCServer)message;
			server.setSession(session);
			RouteServerManager.putRPCServer(server);
		}
	}
	
	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		log.error("Session Exception [{}]--{}",session,cause);
		super.exceptionCaught(session, cause);
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		super.messageSent(session, message);
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		log.error("Session Closed [{}]--{}",session);
		super.sessionClosed(session);
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		
		super.sessionIdle(session, status);
	}


	@Override
	public void sessionOpened(IoSession session) throws Exception {
		log.error("Session Opened [{}]--{}",session);
		super.sessionOpened(session);
	}


	@Override
	public void sessionCreated(IoSession session) throws Exception {
		log.error("Session Created [{}]--{}",session);
	}
	
	
	
	
}
