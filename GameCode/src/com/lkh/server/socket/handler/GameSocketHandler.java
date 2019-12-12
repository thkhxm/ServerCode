package com.lkh.server.socket.handler;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import com.lkh.manager.User;
import com.lkh.server.GameSource;
import com.lkh.server.ServerStat;
import com.lkh.server.ServiceManager;
import com.lkh.server.proto.Request;
import com.lkh.server.socket.GameServerManager;
import com.lkh.server.socket.ServerMethod;
import com.lkh.tool.log.LoggerManager;

public class GameSocketHandler extends IoHandlerAdapter{

	
	private ISessionClose sessionClose;
	
	public GameSocketHandler() {
	}
	
	public void appendClose(ISessionClose sessionClose){
		this.sessionClose = sessionClose;
	}

	//-----------------------------

	//连接
	public void sessionOpened( IoSession session ){
		LoggerManager.debug("有一个连接，IP地址:[{}]",session.getRemoteAddress());
		ServerStat.incOnline();
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		LoggerManager.debug("有一个连接创建，IP地址:[{}]",session.getRemoteAddress());
	}

	//断开
	public void sessionClosed(IoSession session){
		Object teamId = session.getAttribute("teamId");
		LoggerManager.debug("连接断开，玩家ID[{}]",(Long)teamId);
		if(teamId != null){
			ServiceManager.offline((Long)teamId);
			User user = GameSource.offline((Long)teamId,session);
			if(user!=null && sessionClose!=null){
				sessionClose.close(user);
			}
		}
		ServerStat.decOnline();
	}

	public void exceptionCaught(IoSession session, Throwable cause) {
//		ServerStat.decOnline();
		LoggerManager.debug("conntion session is :[{}],conntion exception:[{}]",session.getServiceAddress(),cause);
	} 	

	//接收数据
	public void messageReceived(IoSession session, Object message){
		try{
			if(message instanceof Request){
				ServerStat.incMo();
				Request req = (Request)message;
				if(GameSource.isOpen){
					//服务器开启时才接收处理客户端请求
					exec(req);
				}else{
					
					
				}
			}
			if(message instanceof String){
//				String msg = (String)message;
//				//logger.info("====="+message);
//				if(msg.indexOf(GoogleDecoder.gamegm)!=-1){
//					Request req = new Request(-5);
//					//req.setStartTime(System.currentTimeMillis());
//					req.setDataN(msg.replaceAll(GoogleDecoder.gamegm, ""));
//					req.setSession(session);
//					req.setType(req.getAsInt("servicecode"));
//					statPool.execute(new MoWorker(req));
//					return;
//				}
			}
		}catch(Exception e){
			LoggerManager.error("messageReceived:", e);			
		}
	} 
   
	/**
	 * 将客户端请求放入处理队列
	 * @param req
	 */
	private void exec(Request req){
		if(ServerMethod.REJECT_METHOD.getKeyCode()==req.getMethodCode())//废弃的客户端请求
			return;
		GameServerManager.putMo(req);
	}
	

}



