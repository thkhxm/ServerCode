package com.lkh.server.rpc.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.lkh.server.RPCMessageManager;
import com.lkh.server.rpc.RPCServer;
import com.lkh.server.rpc.RPCSource;
import com.lkh.server.socket.GameServerManager;
import com.lkh.tool.log.LoggerManager;

/**
 * @author tim.huang
 * 2017年3月16日
 * 服务提供者
 */
public class RPCHandler extends IoHandlerAdapter{
	
	public final static Logger log = LogManager.getLogger(RPCHandler.class);
	
	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		//收到请求服务消息，执行本地逻辑
		if(message instanceof RPCSource){
			RPCSource source = (RPCSource)message;
			GameServerManager.putMo(source);
		}
	}
	
	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		log.error("Session Exception [{}]--{}",session.getServiceAddress(),cause);
		super.exceptionCaught(session, cause);
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		super.messageSent(session, message);
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		log.error("Session Closed [{}]--{}",session.getServiceAddress());
		super.sessionClosed(session);
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		log.debug("Session Idle [{}]--{}",session,status);
		super.sessionIdle(session, status);
	}


	@Override
	public void sessionOpened(IoSession session) throws Exception {
		log.error("Session Opened [{}]--{}",session.getServiceAddress());
		super.sessionOpened(session);
	}


	@Override
	public void sessionCreated(IoSession session) throws Exception {
		log.error("Session Created [{}]--{}",session.getServiceAddress());
	}
	
}
