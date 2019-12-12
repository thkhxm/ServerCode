package com.lkh.db.conn;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.lkh.db.conn.dao.AsynchronousBatchDB;

/**
 * @author Tim
 * 数据库缓存数据
 */
public class DataCache {
	private static Logger log = LoggerFactory.getLogger(DataCache.class); 
	//
	static class ManagerInstace{
		private static DataCache manager;
		static {
			manager = new DataCache();
		}
		static private DataCache get() {
			return manager;
		}
	}
	static public final DataCache get() {
		return ManagerInstace.get();
	}
	
	private DataCache() {
		dbRefDataMap = Maps.newHashMap();
	}
	//
	
	private Map<Class<?>,DBRefData> dbRefDataMap;
	
	public <T> T select(Class<T> cls,Object key) {
		DBRefData data = this.dbRefDataMap.getOrDefault(cls, null);
		if (data == null) {
			log.error("存在未注册的Class#{}", cls.getName());
			return null;
		}
		return data.select(key);
	}
	
	public <T> List<T> selectList(Class<T> cls,Object key) {
		DBRefData data = this.dbRefDataMap.getOrDefault(cls, null);
		if (data == null) {
			log.error("存在未注册的Class#{}", cls.getName());
			return null;
		}
		return data.selectList(key);
	}
	
	//更新所有数据到db
	public void updateDBResource(){
		this.dbRefDataMap.values().parallelStream().forEach(ref->ref.updateData());
	}
	
	public void addResource(AsynchronousBatchDB db){
		this.dbRefDataMap.get(db.getClass()).add(db);
	}
	
	public void addRefData(Class<?> clas) {
		dbRefDataMap.put(clas, new DBRefData(clas));
	}
	
}
