package com.lkh.server.rpc.zk;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import com.lkh.tool.log.LoggerManager;

/**
 * @author tim.huang
 * 2017年3月29日
 * 观察路由节点
 */
@Deprecated
public class RouteWatcher implements Watcher {
	
	
	@Override
	public void process(WatchedEvent event) {
//		LoggerManager.err("路由节点变动，触发监听[{}]", event.getPath());
		
//		System.err.println("路由节点变动，触发监听["+event.getPath()+"]");
	}

}
