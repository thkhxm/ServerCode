package com.lkh.tool.excel;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author tim.huang
 * 2018年5月28日
 */
public abstract class ABaseConfigCache {
	
	protected ABaseConfigCache () {
//		Field[] allField = this.getClass().getDeclaredFields();
//		
//		
//		Config config = null;
//		for(Field field : allField) {
//			config = field.getAnnotation(Config.class);
//			if(config == null) continue;
//			
//			
//		}
		autoList = new HashMap<>();
	}
	
	private Map<String,Consumer<ExcelConfigBean>> autoList ;
	
	public void addAutoInit(String key,Consumer<ExcelConfigBean> function){
		autoList.put(key, function);
	}
	
	public void init() {
		autoList.forEach((key,auto)->auto.accept(ConfigCacheConsole.get().getConfigBean(key)));
	}
	
}
