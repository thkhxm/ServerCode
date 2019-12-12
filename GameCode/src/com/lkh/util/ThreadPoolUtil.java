package com.lkh.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author tim.huang
 * 2017年2月17日
 * 线程池工具
 */
public class ThreadPoolUtil {
	
	private static Map<String,ScheduledExecutorService> poolMap = new HashMap<String, ScheduledExecutorService>();
	
	public static ScheduledExecutorService getScheduledPool(String name){
		return poolMap.get(name);
	}
	
	public static ScheduledExecutorService newScheduledPool(String name,int code){
		poolMap.put(name, Executors.newScheduledThreadPool(code,new ThreadPoolFactory(name,code)));
		return getScheduledPool(name);
	}
	
	
	
	static class ThreadPoolFactory implements ThreadFactory {
		private AtomicInteger num;
		private String name;
		private int max;
		
		public ThreadPoolFactory(String name,int max) {
			super();
			this.name = name;
			this.max = max;
			this.num = new AtomicInteger();
		}

		@Override
		public Thread newThread(Runnable r) {
			return new Thread(r, this.name + "-" + this.max + "-" + num.incrementAndGet());
		}
		
		
	}
}
