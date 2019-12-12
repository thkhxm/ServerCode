package com.lkh.tool.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import com.lkh.server.GameSource;
import com.lkh.util.IPUtil;



/**
 * @author tim.huang
 * 2017年3月7日
 * 
 */
public class LoggerManager {
	
	public final static Logger log = LogManager.getLogger(LoggerManager.class);
	
	public static void initThreadContext(){
        ThreadContext.put("shardId", ""+GameSource.shardId);
        ThreadContext.put("ip", GameSource.serverName);
        ThreadContext.put("platform", ""+GameSource.platform);
	}
	
	
	public static void main(String[] args) {
		
		
	}
	/**
	 * @param msg
	 * @param v
	 * 严重的异常
	 */
	public static void error(String msg,Object...v){
		log.error(msg,v);
	}

	public static void warn(String msg,Object...v){
		log.warn(msg,v);
	}
	
	/**
	 * @param msg
	 * @param v
	 * 调试信息打印
	 */
	public static void debug(String msg,Object...v){
		if(GameSource.isDebug)
			log.debug(msg,v);
	}
	
	/**
	 * @param msg
	 * @param v
	 * 游戏信息打印
	 */
	public static void info(String msg,Object...v){
		if(GameSource.isDebug)
			log.info(msg,v);
	}
	
	/**
	 * @param msg
	 * @param v
	 * 游戏信息打印
	 */
	public static void gameInfo(String msg,Object...v){
		if(GameSource.isDebug)
			log.trace(msg,v);
	}

}
