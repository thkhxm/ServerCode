package com.lkh.client;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.lkh.client.robot.BaseRobot;
import com.lkh.tool.log.LoggerManager;

public class ClientHandler extends IoHandlerAdapter{

	private BaseRobot robot;
	public ClientHandler(BaseRobot robot){
		this.robot = robot;
	}
	
	
	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		LoggerManager.error("----------------------------------------------------------{}",cause);
	}
	

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		if(message instanceof ClientResponse){
			ClientResponse cr = (ClientResponse)message;
//			LoggerManager.info("接受到服务器返回协议[{}]-耗时[{}]",cr.getKey(),robot.getAction().end());
			ClientStat.make(cr.getKey(), robot.getAction().end());
			if(robot.getAction().getServiceCode()==cr.getKey())
				robot.getAction().callback(cr, robot);
//			DemoPB.DemoDataMain dm = DemoPB.DemoDataMain.parseFrom(cr.getData());
//			log.info("callback message Name:[{}],Num:[{}]",dm.getName(),dm.getNum());
		}
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		super.messageSent(session, message);
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		LoggerManager.error("----------------------------Close------------------------------");
		super.sessionClosed(session);
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		super.sessionCreated(session);
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		super.sessionIdle(session, status);
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		super.sessionOpened(session);
	}


	public BaseRobot getRobot() {
		return robot;
	}
	
	
}
