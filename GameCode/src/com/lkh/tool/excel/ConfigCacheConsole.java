package com.lkh.tool.excel;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.lkh.server.GameSource;

/**
 * @author tim.huang
 * 2018年5月28日
 * 配置文件缓存管理
 */
public class ConfigCacheConsole {

	static class ManagerInstace{
		private static ConfigCacheConsole manager;
		static {
			manager = new ConfigCacheConsole();
		}
		static private ConfigCacheConsole get() {
			return manager;
		}
	}
	static public final ConfigCacheConsole get() {
		return ManagerInstace.get();
	}
	private ConfigCacheConsole() {
		this.tempMaps = new HashMap<>();
		this.configTmpMap = Maps.newHashMap();
	}
	//
	private Map<String,ExcelConfigBean> tempMaps;
	
	private Map<Class<?>,List<Object>> configTmpMap;
	
	private Logger log = LoggerFactory.getLogger(ConfigCacheConsole.class);
	public void put(ExcelConfigBean bean) {
		this.tempMaps.put(bean.getKey(), bean);
	}
	
	public ExcelConfigBean getConfigBean(String key) {
		return this.tempMaps.getOrDefault(key, null);
	}
	
	public void init(Map<String, byte[]> excelBytes) {
		excelBytes.forEach((key,val)->{
			try {
				put(ExcelUtil.getExcelConfigBean(new ByteArrayInputStream(val), key));
			} catch (Exception e) {
				log.error("加载配置文件异常#{}",e);
			}
		});
		if(GameSource.isDebug)
			log.debug("配置数据打印#{}",tempMaps);
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> getList(Class<?> cla) {
		return (List<T>)this.configTmpMap.getOrDefault(cla, null);
	}
	
	public void addConfigTmpData(Class<?> cla,List<Object> data) {
		this.configTmpMap.put(cla, data);
	}
}
