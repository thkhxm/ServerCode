package com.lkh.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lkh.enums.EVersion;
import com.lkh.manager.User;
import com.lkh.tool.log.LoggerManager;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.apache.mina.core.session.IoSession;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


/**
 * 游戏数据源
 * @author tim.huang
 * 2015年12月1日
 */
public class GameSource {
	//服务器id
	public static int shardId = 100;
	//语言版本
	public static EVersion charset;
	//是否调试模式
	public static boolean isDebug = true;
	//节点名称
	public static String serverName ="Tim";
	//zk父级路径
	public static String zkPath = "/ygame";
	//节点池名称
	public static String pool = "logic";
	//顶级域名
	public static String topAddress = "lkh.app";
	//平台
	public static String platform = "ios";

	
	//是否外网服务器环境
	public static boolean net = false;
	//是否统计调用记录
	public static boolean stat = false;
	//是否开启系统信息打印
	public static boolean statJob = false;
	/** 消息统计信息 */
	public static boolean MESSAGE_STATS = true;
	
	
	
	
	/**玩家id标记*/
	static final public String Player_Session_Key = "playerId";

	/**
	 * 服务器状态
	 */
	public static boolean isOpen = true;
	public static AtomicInteger cacheVersion = new AtomicInteger(0);
//	private static Logger log = LoggerFactory.getLogger(GameSource.class);
	/**
	 * 在线玩家列表
	 */
	private static Map<Long,User> oline_team = Maps.newConcurrentMap();
	/**
	 * 已经离线玩家列表
	 */
	private static List<Long> offline_team = Lists.newArrayList();
//	private static List<Long> remove_team = Lists.newArrayList();
	/**
	 * 不在线需要回收内存的玩家
	 */
	private static List<Long> collect_team = Lists.newArrayList();
	
	
	public static User getUser(long teamId){
		return oline_team.get(teamId);
	}
	public static long getTeamId(long userId){
		return shardId * 100000000000l + userId;
	}

	public static long getTeamId(long shardId, long userId){
		return shardId * 100000000000l + userId;
	}
	
	public static int getSid(long teamId){
		return Math.round(teamId/100000000000l);
	}
	
	public static long getUserId(long teamId){
		return teamId - shardId * 100000000000l;
	}
	
	
	public static boolean isOline(long teamId){
		return oline_team.containsKey(teamId);
	}
	
	public static void offlineUser(long teamId){
		User user = oline_team.remove(teamId);
		if(user!=null && user.getSession()!=null && user.getSession().isActive()){
			user.getSession().closeNow();
		}
	}
	
	public static List<User> getUsers(){
		return Lists.newArrayList(oline_team.values());
	}
	
	public static List<Long> removeAllOffLine(){
		List<Long> result = offline_team;
		offline_team = Lists.newArrayList();
		//
		result.addAll(collect_team);
		collect_team = Lists.newArrayList();
		return result.stream().distinct().collect(Collectors.toList());
	}
	
	
	public static boolean isNPC(long teamId){
		return teamId<10000000;
	}
	
	public static boolean isHero(int heroId){
		return heroId<100000;
	}
	
	public static boolean isSuperHero(int heroId){
		return heroId<100;
	}
	
	/**
	 * 离线
	 */
	public static User offline(long teamId,IoSession session){
		offline_team.add(teamId);
		User user = getUser(teamId);
		if(user!=null && session == user.getSession()){
			oline_team.remove(teamId);
		}	
//		ServerStat.decOnline();
		LoggerManager.debug("用户离线[{}]",teamId);
		return user;
	}

	/**
	 * 离线
	 */
	public static User offline(long teamId, Channel session){
		offline_team.add(teamId);
		User user = getUser(teamId);
		if(user!=null && session == user.getSession()){
			oline_team.remove(teamId);
		}
		if (user!=null && session != null && !isClose(session)) {
			session.close();
		}
//		ServerStat.decOnline();
		LoggerManager.debug("用户离线[{}]",teamId);
		return user;
	}

	/**
	 * 是否已经关闭
	 *
	 * @param channel
	 * @return
	 */
	static public boolean isClose(Channel channel) {
		if (channel == null) {
			return true;
		}
		return !channel.isActive() || !channel.isOpen();
	}
	
	/**
	 * 检查是否回收数据
	 * 不在线的玩家才会加入回收列表
	 * @param teamId
	 */
	public static void checkGcData(long teamId) {
		if(!isNPC(teamId) && !isOline(teamId) && !collect_team.contains(teamId)){
			collect_team.add(teamId);
		}
	}
	
	
	public static void online(long teamId,IoSession session){
		User user = new User(teamId,session);
		oline_team.put(teamId, user);
		offline_team.remove(teamId);
//		remove_team.remove(teamId);
		user.login();
//		ServerStat.incOnline();
		LoggerManager.debug("用户上线[{}]",teamId);
	}
	
	public static void updateDebug(boolean debug){
		isDebug = debug;
	}
	
	public static void updateOpen(boolean open){
		isOpen = open;
	}
	
	/**
	 * 获得在线数据
	 * @return
	 */
	public static int getOlineCount(){
		return oline_team.size();
	}
	
}
