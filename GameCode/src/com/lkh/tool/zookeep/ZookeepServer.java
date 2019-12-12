package com.lkh.tool.zookeep;

import java.nio.charset.Charset;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import com.google.common.collect.Lists;
import com.lkh.server.GameSource;
import com.lkh.server.RPCMessageManager;
import com.lkh.server.instance.InstanceFactory;
import com.lkh.server.rpc.RPCServer;
import com.lkh.util.JsonUtil;


/**
 * @author tim.huang
 * 2017年3月15日
 */
public class ZookeepServer {
	
	public final static Logger log = LogManager.getLogger(ZookeepServer.class);
	
	private ZooKeeper zookeeper;
	private List<ACL> auth;
	private RPCServer server;
	
	public static final String Logic = "/node";
	public static final String Config = "/config";
	public static final String Route = "/route";
	public static final String Master = "/master";
	
	private static ZookeepServer zkServer;
	
	
	
	public ZookeepServer(ZookeepConfig config,Watcher watcher) throws Exception{
		zookeeper = new ZooKeeper(config.getIpPort(), 3000, watcher);
		String pwd = config.getUser()+":"+config.getPwd();
//		zookeeper.addAuthInfo("digest", pwd.getBytes());
		Id id1 = new Id("digest", DigestAuthenticationProvider.generateDigest(pwd));  
		auth = Lists.newArrayList();
//		auth.add(new ACL(ZooDefs.Perms.ALL, id1));  
	}
	
	public static ZookeepServer getInstance(){
		return zkServer;
	}
	public static ZookeepServer createZK(RPCServer server){
		if(zkServer == null){
			zkServer = new ZookeepServer(server);
		}
		return zkServer;
	}
	
	
	/**
	 * 创建一个节点zk
	 * @param ip
	 * @param port
	 * @return
	 */
	public static ZookeepServer createNodeZK(ZookeepServer zk){
		ZookeepConfig config = InstanceFactory.get().getInstance(ZookeepConfig.class);
		try {
			zk.zookeeper = new ZooKeeper(config.getIpPort(), 3000, (event)->{
//				log.err("逻辑节点变动，触发监听[{}]", event.getPath());
				
				//先注册主节点，并且修改本地服务信息
				if(zk.server.isOpen() && (event.getPath() == null || (ZookeepServer.getMasterPath()+"/"+GameSource.pool).equals(event.getPath()))){
					zk.server.setMaster(true);
					String io = zk.create(ZookeepServer.getMasterPath()+"/"+GameSource.pool, zk.server.toJson());
					if("".equals(io)){
						zk.server.setMaster(false);
					}
					String pathMaster = ZookeepServer.getMasterPath()+"/"+GameSource.pool;
					Watcher masterWatcher = new Watcher() {
						@Override
						public void process(WatchedEvent et) {
							if(et.getType() == EventType.NodeDeleted
									&& !zk.server.isMaster()){
								zk.server.setMaster(true);
								String ok = zk.create(pathMaster, zk.server.toJson());
								log.debug("尝试创建主节点:{}",pathMaster);
								if(!"".equals(ok)) {//选主成功，修改自身节点信息，让路由收到节点信息
									//将新的数据推送到路由服务器
									//修改自身节点信息，触发路由节点的节点变动监听
									zk.set(ZookeepServer.getLogicPath()+"/"+GameSource.serverName, zk.server.toJson());
//									List<String> routeList = zk.getChild(ZookeepServer.getRoutePath());
//									zk.addRouteServer(routeList,true);
									log.debug("主节点创建成功:{}",pathMaster);
								}else{
									zk.server.setMaster(false);
									log.debug("主节点创建失败:{}",pathMaster);
								}
							}
							zk.exists(pathMaster,this);
						}
					};
					//创建监听事件,监听主节点的删除行为
					zk.exists(ZookeepServer.getMasterPath()+"/"+GameSource.pool,masterWatcher);
				}
				
				//首次创建，创建逻辑节点
				if(event.getType() == EventType.None){
					//创建逻辑节点，
					zk.create(ZookeepServer.getLogicPath()+"/"+GameSource.serverName, zk.server.toJson());
					log.debug("创建zk节点:{}",ZookeepServer.getLogicPath()+"/"+GameSource.serverName);
					//监听路由节点,路由更新,重新发送消息到路由节点
					Watcher routeWatcher = new Watcher() {
						@Override
						public void process(WatchedEvent et) {
							log.debug("触发了路由节点发生变动:[{}]--类型为[{}]",et.getPath(),et.getType());
							if(et.getType() == EventType.NodeChildrenChanged){
								log.debug("路由节点发生变动:{}",et.getPath());
								//将新的数据推送到路由服务器
								List<String> list = zk.getChild(et.getPath(),this);
								zk.addRouteServer(list,false);
								//刷新自身路由列表
								RPCMessageManager.checkRouteServer(list);
							}
						}
					};
					

					
					List<String> routeList = zk.getChild(ZookeepServer.getRoutePath(),routeWatcher);
					//将新的数据推送到路由服务器
					zk.addRouteServer(routeList,false);
					//刷新自身路由列表
					RPCMessageManager.checkRouteServer(routeList);
				}
				
				if(KeeperState.Expired == event.getState()){//断线重连
					log.error("ZK触发断线重连---->");
					ZookeepServer.createNodeZK(zk);
					log.error("ZK断线重连结束---->");
					return;
				}
				
				log.debug("触发了监听类型为:{}",event.getType());
				log.debug("逻辑节点变动，触发监听[{}]",event.getPath());
			});
			
			String pwd = config.getUser()+":"+config.getPwd();
//			zookeeper.addAuthInfo("digest", pwd.getBytes());
			Id id1 = new Id("digest", DigestAuthenticationProvider.generateDigest(pwd));
			zk.auth = Lists.newArrayList();
//			auth.add(new ACL(ZooDefs.Perms.ALL, id1));
			return zk;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("ZK启动失败---->{}", e);
		}
		return null;
	}
	
	
	/**
	 * 创建路由zk
	 * @param server
	 * @return
	 */
	public static ZookeepServer createRouteZK(ZookeepServer zk){
		ZookeepConfig config = InstanceFactory.get().getInstance(ZookeepConfig.class);
		try {
			zk.zookeeper = new ZooKeeper(config.getIpPort(), 3000, (event)->{
				if(event.getType() == EventType.None){
					//创建逻辑节点，
					zk.create(ZookeepServer.getRoutePath()+"/"+GameSource.serverName, zk.getServer().toJson());
					
//					Watcher logicWatcher = new Watcher() {
//						@Override
//						public void process(WatchedEvent et) {
//							if(event.getType() == EventType.NodeChildrenChanged){
//								//将新的数据推送到路由服务器
//								List<String> list = zk.getChild(et.getPath(),this);
//								
//								zk.addRouteServer(list);
//								//刷新自身路由列表
//								RPCMessageManager.checkRouteServer(list);
//							}
//						}
//					};
//					
//					zk.getChild(ZookeepServer.getLogicPath(),logicWatcher) ;
				}
				
				if(KeeperState.Expired == event.getState()){//断线重连
					log.error("ZK触发断线重连---->");
					ZookeepServer.createRouteZK(zk);
					log.error("ZK断线重连结束---->");
					return;
				}
				
				log.info("路由ZK启动---->{}", event.getPath());
			});
			String pwd = config.getUser()+":"+config.getPwd();
//			zookeeper.addAuthInfo("digest", pwd.getBytes());
				Id id1 = new Id("digest", DigestAuthenticationProvider.generateDigest(pwd));  
				zk.auth = Lists.newArrayList();
//			auth.add(new ACL(ZooDefs.Perms.ALL, id1)); 
				//	
				return zk;
		}
			catch (Exception e) {
				e.printStackTrace();
				log.error("ZK启动失败---->{}", e);
			}
		return null;
	}
	
	private void addRouteServer(List<String> routeList,boolean replace){
		routeList.forEach(node->RPCMessageManager.addRouteServer(JsonUtil.toObj(get(ZookeepServer.getRoutePath()+"/"+node), RPCServer.class),server,replace));
	}
	
	
	public static String getLogicPath(){
		return GameSource.zkPath + Logic;
	}
	public static String getConfigPath(){
		return GameSource.zkPath + Config;
	}
	public static String getRoutePath(){
		return GameSource.zkPath + Route;
	}
	public static String getMasterPath(){
		return GameSource.zkPath + Master;
	}
	
	public ZookeepServer(RPCServer server){
		this.server = server;
//		ZookeepConfig config = InstanceFactory.get().getInstance(ZookeepConfig.class);
//		try {
//			zookeeper = new ZooKeeper(config.getIpPort(), 3000, (event)->{
//				 System.out.println("hello zookeeper");
//			     System.out.println(String.format("hello event! type=%s, stat=%s, path=%s",event.getType(),event.getState(),event.getPath()));
//			     if(event.getType() == EventType.NodeChildrenChanged){
//			    	 System.out.println("触发子节点变动监听");
//			    	 getChild(event.getPath());
//			     }else if(event.getType() == EventType.NodeDeleted){
//			    	 System.out.println("触发节点移除变动监听");
//			    	 exists(event.getPath());
//			     }
//			});
//			
//			String pwd = config.getUser()+":"+config.getPwd();
////		zookeeper.addAuthInfo("digest", pwd.getBytes());
//			Id id1 = new Id("digest", DigestAuthenticationProvider.generateDigest(pwd));  
//			auth = Lists.newArrayList();
////		auth.add(new ACL(ZooDefs.Perms.ALL, id1)); 
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.err("ZK启动失败---->{}", e);
//		}
	}
	
	public RPCServer getServer(){
		return this.server;
	}
	
	public String create(String node,String msg){
		try {
//			zookeeper.create(node, msg.getBytes(Charset.forName("UTF8")), auth, CreateMode.EPHEMERAL);
			log.info("创建一个zookeep节点[{}],节点信息[{}]", node,msg);
			String path = zookeeper.create(node, msg.getBytes(Charset.forName("UTF8")), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
			return path;
		} catch (Exception e) {
			log.error(e);
			return "";
		}  
	}
	public String create(String node,byte[] msg){
		try {
//			zookeeper.create(node, msg.getBytes(Charset.forName("UTF8")), auth, CreateMode.EPHEMERAL);
			log.info("创建一个zookeep节点[{}],节点信息", node);
			String path = zookeeper.create(node, msg, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			return path;
		} catch (Exception e) {
			log.error(e);
			return "";
		} 
	}
	
	public String get(String node){
		String msg="";
		try {
			msg = new String(zookeeper.getData(node, true, null));
		} catch (KeeperException | InterruptedException e) {
			e.printStackTrace();
			log.error(e);
		}
		return msg;
	}
	
	public byte[] getBytes(String node){
		byte[] msg= null;
		try {
			msg = zookeeper.getData(node, true, null);
		} catch (KeeperException | InterruptedException e) {
			e.printStackTrace();
			log.error(e);
		}
		return msg;
	}
	
	public Stat exists(String node,Watcher watcher){
		try {
			return zookeeper.exists(node, watcher);
		} catch (KeeperException | InterruptedException e) {
			e.printStackTrace();
			log.error(e);
			return null;
		}
	}
	public Stat exists(String node){
		try {
			return zookeeper.exists(node, true);
		} catch (KeeperException | InterruptedException e) {
			e.printStackTrace();
			log.error(e);
			return null;
		}
	}
	
	public List<String> getChild(String node,Watcher watcher) {
		List<String> nodeList = Lists.newArrayList();
		try {
			nodeList = zookeeper.getChildren(node, watcher);
		} catch (KeeperException | InterruptedException e) {
			e.printStackTrace();
			log.error(e);
		}
		return nodeList;
	}
	
	public List<String> getChild(String node){
		List<String> nodeList = Lists.newArrayList();
		try {
			nodeList = zookeeper.getChildren(node, true);
		} catch (KeeperException | InterruptedException e) {
			e.printStackTrace();
			log.error(e);
		}
		return nodeList;
	}
	
	public void del(String node){
		try {
			zookeeper.delete(node, -1);
		} catch (InterruptedException | KeeperException e) {
			e.printStackTrace();
			log.error(e);
		}
	}
	
	public void set(String node,String data){
		try {
			zookeeper.setData(node, data.getBytes(Charset.forName("UTF8")), -1);
		} catch (InterruptedException | KeeperException e) {
			e.printStackTrace();
			log.error(e);
		}
	}
	public void set(String node,byte[] data){
		try {
			zookeeper.setData(node, data, -1);
		} catch (InterruptedException | KeeperException e) {
			e.printStackTrace();
			log.error(e);
		}
	}
	
	public void close(){
		try {
			zookeeper.close();
		} catch (InterruptedException e) {
			e.printStackTrace();
			log.error(e);
		}
	}
	
	
	public static void main(String[] args) throws Exception {
//		File file = new File("E:/016.xlsx");
//		System.err.println(file.getName());
		
//		ZookeepConfig config = new ZookeepConfig();
//		config.setIpPort("192.168.10.181:2181");
//		config.setUser("tim");
//		config.setPwd("xgame2016");
//		ZookeepServer zk = new ZookeepServer(config,(event)->{System.err.println("进入主监听");});
//		byte[] by = ByteUtil.toByteArray(file);
//		zk.create("/zgame/config/test", by);
//		Thread.sleep(10000);
//		System.err.println(new String(zk.getBytes("/zgame/config/test")));
//		zk.getChild("/zgame/logic", (event)->System.err.println("进入子监听"));
//		zk.getChild("/zgame/logic");
		
//		zk.exists("/zgame/logic/logic-1");
//		zk.create("/zgame/logic/logic-1", "123321");
//		logic-pool
//		zk.create("/zgame/logic/logic-1", "222222");
//		zk.getChild("/zgame/logic").forEach(name->System.err.println(name));
//		zk.set("/zgame/logic/logic-1", "555");
//		zk.getChild("/xgame",null).stream().forEach(data->System.err.println(data));
//		zk.del("/zgame/logic/logic-1");
//		zk.close();
//		System.err.println(zk.get("/xgame/a2"));
	}
	
	
	
}
