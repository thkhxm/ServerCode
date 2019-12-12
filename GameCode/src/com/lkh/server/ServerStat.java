package com.lkh.server;

import com.google.common.collect.Maps;
import com.lkh.db.conn.ao.AOSynManager;
import com.lkh.server.proto.Request;
import com.lkh.server.socket.GameServerManager;
import com.lkh.tool.log.LoggerManager;
import com.lkh.util.StringUtil;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 服务器状态对象
 * @author tim.huang
 * 2015年11月28日
 */
public class ServerStat {
	
	private static AtomicLong moSize = new AtomicLong(0);//请求数量
	private static AtomicLong mtSize = new AtomicLong(0);//响应数量
	private static AtomicInteger onlinePlayerSize = new AtomicInteger(0);//在线人数
	private static AtomicLong[] moTime = new AtomicLong[]{new AtomicLong(0),new AtomicLong(0),
		new AtomicLong(0),new AtomicLong(0),new AtomicLong(0)};
	private static Map<Integer,MethodStat> methodMap =  Maps.newConcurrentMap();
	private static MessageStat messageStat;

	public static void make(Request req){
		long endTime = req.end();
		if(GameSource.stat){
//			if(endTime>=500){
//				LoggerManager.info("耗时{}---{}----{}", System.currentTimeMillis(),req.getStartTime(),endTime);
//			}
			methodMap.computeIfAbsent(req.getMethodCode(), key->new MethodStat(key))
			.update(endTime);
		}
		if(messageStat != null && req.getMethodCode() > 0){
			messageStat.addReceivedMsgStat(req.getMethodCode(), req.getMsgLength(), (int) endTime);
		}
	}

	public static void setMessageStat(MessageStat messageStat) {
		ServerStat.messageStat = messageStat;
	}

	public static class MethodStat{
		private int serviceId;
		private AtomicInteger count;
		private AtomicLong totalMill;
		private long maxMill;
		
		public MethodStat(int serviceId) {
			super();
			this.serviceId = serviceId;
			this.count = new AtomicInteger();
			this.totalMill = new AtomicLong();
		}
		
		public void update(long mill){
			if(mill>this.maxMill){
				this.maxMill = mill;
			}
			count.incrementAndGet();
			totalMill.addAndGet(mill);
		}
		
		public int getServiceId() {
			return serviceId;
		}
		public AtomicInteger getCount() {
			return count;
		}
		public AtomicLong getTotalMill() {
			return totalMill;
		}
		public long getMaxMill() {
			return maxMill;
		}
		
		public long getAverageMill(){
			return this.totalMill.get()/this.count.get();
		}
		
	}
	
	
	public static void incMo(){
		moSize.incrementAndGet();
	}
	public static void incMt(int code, int bytesSize){
		mtSize.incrementAndGet();
		if(messageStat != null){
			messageStat.addSendMsgStat(code, bytesSize);
		}
	}

	public static void incOnline(){
		onlinePlayerSize.incrementAndGet();
	}
	public static void decOnline(){
		onlinePlayerSize.decrementAndGet();
	}
	
	public static void saveTime(long time){
		if(time<10){
			moTime[0].incrementAndGet();
		}else if(time>=10 && time<50){
			moTime[1].incrementAndGet();
		}else if(time>=50 && time<100){
			moTime[2].incrementAndGet();
		}else if(time>=100 && time<500){
			moTime[3].incrementAndGet();
		}else if(time>=500){
			moTime[4].incrementAndGet();
		}
	}
	
	private static String memoryInfo(){
		return StringUtil.formatString("空闲内存:[{}mb],总内存:[{}mb],最大内存:[{}mb],已占用内存:[{}mb]\n"
				+ "============================分割线============================\n", Runtime.getRuntime().freeMemory()/1024/1024
				,Runtime.getRuntime().totalMemory()/1024/1024,Runtime.getRuntime().maxMemory()/1024/1024
				,(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1024/1024);
	}
	
	private static String methodInfo(){
		StringBuffer sb = new StringBuffer();
		methodMap.forEach((key,val)->sb.append(StringUtil.formatString("{}:调用次数[{}],最长耗时[{}],平均耗时[{}]\n"
											, key,val.getCount(),val.getMaxMill(),val.getAverageMill())));
		return sb.toString();
	}
	
	public static void print(){
		LoggerManager.info(StringUtil.formatString("\n请求数量总数:[{}],当前剩余执行的请求数量:[{}]\n"
				+ "响应数量总数:[{}],当前剩余执行的响应数量:[{}]\n"
				+ "AO异步请求总数:[{}],当前剩余执行的AO执行数量:[{}]\n"
				+ "当前在线玩家人数[{}]\n"
				+ "请求时间 0-10:[{}],10-50[{}],50-100[{}],100-500[{}],500以上[{}]\n"
				+ memoryInfo()
				+ methodInfo()
				,moSize.get(),GameServerManager.getWaitRunMoSize()
				,mtSize.get(),GameServerManager.getWaitRunMtSize()
				,AOSynManager.getAOTotalSize(),AOSynManager.getAOSize()
				,onlinePlayerSize.get(),moTime[0].get(),moTime[1].get(),moTime[2].get()
				,moTime[3].get(),moTime[4].get()));
	}
	
}
