package com.lkh.server.websocket;

import com.lkh.manager.User;
import com.lkh.server.GameSource;
import com.lkh.server.ServerStat;
import com.lkh.server.ServiceManager;
import com.lkh.tool.log.LoggerManager;
import com.lkh.util.DateTimeUtil;
import com.lkh.util.LocalDateTimeUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

/**
 *  tim.huang
 *
 */
public class WsGatewayServerHandler extends SimpleChannelInboundHandler<Object> {

//    private static  byte [] HEARTBEAT_SEQUENCE = new HeartbeatMqData().toByteArrays();
    
	private static AttributeKey<Long> userKey = AttributeKey.valueOf("teamId");

	private WebSocketServerHandshaker handshaker;

	public WsGatewayServerHandler() {
	}

	
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		super.userEventTriggered(ctx,evt);

		//子服不做心跳检测
		if (evt instanceof IdleStateEvent) {
	        IdleStateEvent e = (IdleStateEvent) evt;
	        if ( IdleState.ALL_IDLE == e.state()) {
				interrupt(ctx.channel());

	        }
	    }
	}


	private void interrupt(Channel channel){
		if(channel.hasAttr(userKey)){
			Long teamId = channel.attr(userKey).get();
			if(teamId!=null){
				LoggerManager.debug("连接断开，玩家ID[{}]",(Long)teamId);
				if(teamId != null){
					ServiceManager.offline((Long)teamId);
					User user = GameSource.offline((Long)teamId,channel);
				}
				ServerStat.decOnline();
			}
		}
	}


	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
		cause.printStackTrace();
		LoggerManager.debug("[WsGatewayServerHandler]-[exceptionCaught] 客户端断开连接 :[{}]",ctx.channel().localAddress());
		interrupt(ctx.channel());
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		
		InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
		String ip = insocket.getAddress().getHostAddress();
		int port = insocket.getPort();
		String key = ip + ":" + port;
		System.out.println("create client "+key);
//		NettyUtils.setKey(ctx, key);
//		NettyUtils.autoRegUid(ctx);
//		NettyUtils.regClient(ctx);
		
		
		insocket = (InetSocketAddress) ctx.channel().localAddress();
		String ip2 = insocket != null ? insocket.getAddress().getHostAddress() : "null";
		
		LoggerManager.debug("[WsGatewayServerHandler]-[channelActive] localAddress::{}, remoteAddress::{}",ip2,ip  );
	}

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		super.handlerAdded(ctx);
		
//		NettyUtils.setProto(ctx, NettyUtils.WEB_SOCKET);

		InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
		if (insocket != null) {
			String ip = insocket.getAddress().getHostAddress();
			int port = insocket.getPort();
			String key = ip + ":" + port;
//			logger.trace("[WsGatewayServerHandler]-[handlerAdded] {} :: 创建连接{}",getClass().getSimpleName(),key);
		} else {
//			logger.trace("[WsGatewayServerHandler]-[handlerAdded] 创建连接但，[{}] ctx.channel().remoteAddress() = null ".getClass().getSimpleName());
		}

	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		LoggerManager.debug("正常断线  wsgate ctx = [{}] time = [{}]  ",ctx.channel().localAddress(), LocalDateTimeUtil.formatDate());
		interrupt(ctx.channel());
	}
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof FullHttpRequest) {
			handleHttpRequest(ctx, ((FullHttpRequest) msg));
		} else if (msg instanceof WebSocketFrame) {
			handlerWebSocketFrame(ctx, (WebSocketFrame) msg);
		}

	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@SuppressWarnings("static-access")
	private void handlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {

		// 判断是否关闭链路的指令
		if (frame instanceof CloseWebSocketFrame) {
			handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
			return;
		}

		// 判断是否ping消息
		if (frame instanceof PingWebSocketFrame) {
			ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
			return;
		}else if (frame instanceof TextWebSocketFrame) {
//			logger.trace("[WsGatewayServerHandler]-[handlerWebSocketFrame] 主动断开连接, 因为错误的通讯格式 TextWebSocketFrame ， ip:port [{}]",NettyUtils.getKey(ctx));
//			NettyUtils.closeCtxConnect(ctx);
			return;
		}

		BinaryWebSocketFrame binary = (BinaryWebSocketFrame) frame;

		ByteBuf bf = binary.content();

		byte[] datas = new byte[bf.readableBytes()];
		bf.readBytes(datas);
		
		//System.out.println( Arrays.toString(datas));
		
		
		
//		OriginalPackage omsg = new OriginalPackage();
//		omsg.readBytes(datas);
//
//		MessageBox box = new MessageBox(String.valueOf(omsg.getCmdId() / 100), NettyUtils.getTableId(ctx), omsg.getProtoData() );
//		box.setCmdId(omsg.getCmdId());
//		box.setType(TypeLMessage.ClientToService.getValue());
//		box.setCustomerId(NettyUtils.getCustomerId(ctx));
		
//		if (box.getCmdId() != SystemMsgId.HeartBEAT.getValue() ) {
//
//			//自动补充客户端发布的消息规则
//			CtxPlayerStatus cps = NettyUtils.getPlayerStatus(ctx);
//			String key = NettyUtils.autoKey(ctx);
//			box.setKey(key);
//			box.setUserId( cps.getUserId() );
//			box.setMainId( String.valueOf( box.getCmdId() / 100));
//			box.setSubId(cps.getTableId());
//
//			logger.trace("[WsGatewayServerHandler]-[handlerWebSocketFrame] gateway 收到client消息! [{}] msg = [{}]",key,box.getCmdId());
//
//			if ( box.getCmdId() == SystemMsgId.Handshake.getValue() ) {
//				return;
//			}
//			box.setType( TypeLMessage.ClientToService.getValue());
//			accepter.acceptMsg(box, ctx);
//		}else{
//			//System.out.println("收到心跳并回复" + NettyUtils.getKey(ctx) +" , uid = "+ NettyUtils.getUserId(ctx)+ " time = "+ TimeUtil.getDateFormat(new Date()));
//			NettyUtils.sendMsgToCtx( ctx , HEARTBEAT_SEQUENCE , SystemMsgId.HeartBEAT.getValue() );
//		}

	}

	private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {

		//!"websocket".equals(req.headers().get(HttpHeaderNames.UPGRADE)
		if (!req.decoderResult().isSuccess() || (!"websocket".equals(req.headers().get("Upgrade")))) {
			sendHttpResponse(ctx, req,
					new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
			return;
		}

		WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
				"ws://localhost:16766", null, false);

		handshaker = wsFactory.newHandshaker(req);

		if (handshaker == null) {
			WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
		} else {
			handshaker.handshake(ctx.channel(), req);
			String key = req.headers().get("Host");
//			NettyUtils.setKey(ctx, key);
		}
	}
	
	private static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, DefaultFullHttpResponse res) {
		// 返回应答给客户端
		if (res.status().code() != 200) {
			ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
			res.content().writeBytes(buf);
			buf.release();
		}
		// 如果是非Keep-Alive，关闭连接
		ChannelFuture f = ctx.channel().writeAndFlush(res);
		if (!isKeepAlive(req) || res.status().code() != 200) {
			f.addListener(ChannelFutureListener.CLOSE);
		}
	}

	private static boolean isKeepAlive(FullHttpRequest req) {
		return false;
	}

}