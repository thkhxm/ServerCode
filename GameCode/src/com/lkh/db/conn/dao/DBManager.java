package com.lkh.db.conn.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lkh.db.conn.DataCache;
import com.lkh.server.instance.InstanceFactory;

/**
 * @author tim.huang
 * 2016年2月22日
 * 部分DB处理
 */
public class DBManager {
	private static final Logger log = LoggerFactory.getLogger("DBManager");
//	private static Map<String,List<AsynchronousBatchDB>> asynchronousGameMap = new ConcurrentHashMap<String, List<AsynchronousBatchDB>>();
//	private static Map<String,List<AsynchronousBatchDB>> asynchronousDMap = new ConcurrentHashMap<String, List<AsynchronousBatchDB>>();
	private static List<String> gameDelSql = new ArrayList<String>();
	private static List<String> dataDelSql = new ArrayList<String>();
	private static AtomicLong num = new AtomicLong(0);
	private static AtomicBoolean ab = new AtomicBoolean(true);
	private static AtomicInteger lo = new AtomicInteger(0);
	private static GameConnectionDAO game;
	private static DataConnectionDAO data;
	
//	public synchronized static void putAsynchronousDB(AsynchronousBatchDB db){
//		List<AsynchronousBatchDB> list = asynchronousGameMap.get(db.getTableName());
//		if(list == null){
//			list = new ArrayList<AsynchronousBatchDB>();
//			asynchronousGameMap.put(db.getTableName(),list);
//		}
//		list.add(db);
//		DataCache.get().addResource(db);
//	}
	
//	public synchronized static void putAsynchronousDataDB(AsynchronousBatchDB db){
//		List<AsynchronousBatchDB> list = asynchronousDMap.get(db.getTableName());
//		if(list == null){
//			list = new ArrayList<AsynchronousBatchDB>();
//			asynchronousDMap.put(db.getTableName(),list);
//		}
//		list.add(db);
//	}
	
	public static void putGameDelSql(String sql){
		gameDelSql.add(sql);
	}
	public static void putDataDelSql(String sql){
		dataDelSql.add(sql);
	}
	
	/**
	 * 执行数据处理
	 */
	public static void run(boolean isStop){
		if(!isStop && lo.incrementAndGet()<10 && !ab.getAndSet(false)) return;
		num.incrementAndGet();
		lo.set(0);
		long now = System.currentTimeMillis();
		if(game == null){
			game = new GameConnectionDAO();
			game.database = InstanceFactory.get().getDataBaseByKey(ResourceType.DB_game.getResName());
		}
		DataCache.get().updateDBResource();
		if(isStop || num.get()%10==0){
			gameDelSql.stream().forEach(sql->game.execute(sql));
		}
		//
		if(data == null){
			data = new DataConnectionDAO();
			data.database = InstanceFactory.get().getDataBaseByKey(ResourceType.DB_data.getResName());
		}
		if(isStop || num.get()%10==0){
			dataDelSql.stream().forEach(sql->data.execute(sql));
		}
		log.debug("批量更新数据耗时------------------>"+(System.currentTimeMillis()-now));
		ab.set(true);
	}
}
