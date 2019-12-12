package com.lkh.server.rpc.zk;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;

import com.lkh.tool.zookeep.ZookeepServer;

/**
 * @author tim.huang
 * 2017年4月13日
 * 节点监听
 */
@Deprecated
public class NodeWatch implements Watcher {

	
	private ZookeepServer zk;
	
	public NodeWatch(ZookeepServer zk){
		this.zk = zk;
	}
	
	@Override
	public void process(WatchedEvent event) {
//		LoggerManager.err("逻辑节点变动，触发监听[{}]", event.getPath());
		if(event.getType() == EventType.None) return;
		
//		if(event.getType() == EventType.NodeChildrenChanged){//子节点变动
//			
//		}
		
		
//		System.err.println("触发了监听类型为:"+event.getType());
//		
//		System.err.println("逻辑节点变动，触发监听["+event.getPath()+"]");
//		
	}
	
	

}
