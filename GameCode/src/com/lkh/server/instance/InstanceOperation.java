package com.lkh.server.instance;

/**
 * 实例操作
 * @author tim.huang
 * 2015年12月11日
 */
public interface InstanceOperation {
	
	/**
	 * 实例化之后执行的方法
	 */
	public void instanceAfter();
	
	default public void initConfig(){
		
	}
	
	public int getOrder();
	
}
