package com.lkh.server.websocket;

import com.lkh.server.websocket.handler.WsServerHandler;
import com.lkh.tool.log.LoggerManager;
import com.lkh.util.LocalDateTimeUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * 创建一个网关消息队列
 * 
 * @author admin
 *
 */
public class WsGateway {

	//private String ip;
	private int port;

	/** 用于分配处理业务线程的线程组个数 */
	protected int bizGroupSize = Runtime.getRuntime().availableProcessors() * 2;

	/** 业务出现线程大小 */
	protected int bizThreadSize = 4;

	private EventLoopGroup bossGroup = new NioEventLoopGroup(bizGroupSize);

	private EventLoopGroup workerGroup = new NioEventLoopGroup(bizThreadSize);

	/**
	 * 创建一个网关消息队列
	 * 
	 * @param port
	 */
	public WsGateway(int port) {
		//this.ip = ip;
		this.port = port;

		try {
			run();
		} catch (Exception e) {
			LoggerManager.error("[WsGateway]-[WsGateway] error",e);
		}
		LoggerManager.debug("[WsGateway]-[WsGateway] Create WsService ip =[{}] , port =[{}] , at time = [{}] !", "....",port,
				LocalDateTimeUtil.formatDate());
	}

	private void run() throws Exception {


		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).childOption(ChannelOption.SO_REUSEADDR, true);
			b.channel(NioServerSocketChannel.class);
			b.childHandler(new ChannelInitializer<SocketChannel>() {
				
				//TODO 心跳未调，读写闲置时间设得很长
				// 读超时
				private static final int READ_IDEL_TIME_OUT = 300;
				// 写超时
				private static final int WRITE_IDEL_TIME_OUT = 300;
				// 写读超时
				private static final int ALL_IDEL_TIME_OUT = 600;

				@Override
				public void initChannel(SocketChannel ch) throws Exception {

					ChannelPipeline pipeline = ch.pipeline();
					pipeline.addLast(new IdleStateHandler(READ_IDEL_TIME_OUT, WRITE_IDEL_TIME_OUT, ALL_IDEL_TIME_OUT,
							TimeUnit.SECONDS));

					// pipeline.addLast(new AcceptorIdleStateTrigger());
					//pipeline.addLast(new LoggingHandler(LogLevel.ERROR));

					pipeline.addLast("http-codec", new HttpServerCodec());
					pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
					pipeline.addLast("http-chunked", new ChunkedWriteHandler());

					pipeline.addLast("handler", new WsGatewayServerHandler());
				}
			});
			
			b.bind( port).sync();
			LoggerManager.debug("[WsGateway]-[run] websocket 服务器已启动 port = " + port );
		} catch (Exception e) {
			shutdown();
		}

	}

	public void shutdown() {
		workerGroup.shutdownGracefully();
		bossGroup.shutdownGracefully();
	}

}
