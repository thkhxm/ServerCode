package com.lkh.server.instance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lkh.annotation.IOC;
import com.lkh.db.conn.ao.AOInvocationHandler;
import com.lkh.db.conn.ao.BaseAO;
import com.lkh.db.conn.ao.SynAO;
import com.lkh.db.conn.dao.Database;
import com.lkh.enums.ERPCType;
import com.lkh.manager.CloseOperation;
import com.lkh.manager.IConsoleInit;
import com.lkh.manager.OfflineOperation;
import com.lkh.script.ScriptEngine;
import com.lkh.script.StartupContextImpl;
import com.lkh.server.GameSource;
import com.lkh.server.rpc.IZKMaster;
import com.lkh.server.rpc.RPCServerMethod;
import com.lkh.server.socket.ServerMethod;
import com.lkh.tool.excel.ExcelConsole;
import com.lkh.tool.excel.ExcelModule;
import com.lkh.tool.log.LoggerManager;
import com.lkh.tool.redis.JRedisServer;

/**
 * 服务器对象工厂
 * @author tim.huang
 * 2015年11月27日
 */
public class InstanceFactory {
	private static final Logger log = LoggerFactory.getLogger(InstanceFactory.class);
	//
	private Map<Integer,ServerMethod> METHOD_MAP = Maps.newHashMap();
	//
	private Map<String,Database> DATABASE_MAP =Maps.newHashMap();
	//
	private Map<String,Object> IOC_MAP = Maps.newHashMap();
	//
	private List<CloseOperation> CLOSE_LIST = Lists.newArrayList();
	//无需登录可以调用的协议
	private List<Integer> CODE_LIST = Lists.newArrayList();
	//离线操作
	private List<OfflineOperation> OFFLINE_LIST = Lists.newArrayList();
	
	private List<InstanceOperation> instanceOperation_List = Lists.newArrayList();

	private InstanceFactory(){}
	
	public static InstanceFactory get(){
		return ObjectFactoryInstance.instance;
	}

	private static class ObjectFactoryInstance{
		private static InstanceFactory instance = new InstanceFactory();
	}
	
	void executAfter(){
		List<Object> instanceList = new ArrayList<Object>(IOC_MAP.values());
		instanceOperation_List = new ArrayList<InstanceOperation>();
		//增加ao层的动态代理
		for(Object obj : instanceList){
			if(obj instanceof BaseAO){
				AOInvocationHandler handler = new AOInvocationHandler((BaseAO)obj);
				Class<?> [] ifs = obj.getClass().getInterfaces();
				Arrays.stream(ifs).forEach(cla->{
					Object go =  obj;
					if(cla.getSuperclass()!=null && cla.getSuperclass().getSimpleName().equals(SynAO.class.getSimpleName())){
						go = Proxy.newProxyInstance(obj.getClass().getClassLoader()
								,new Class<?>[]{cla}, handler);
					}
					InstanceFactory.get().put(cla.getSimpleName(),go);
				});
			}
		}
		
		List<IZKMaster> materInitList = Lists.newArrayList();
		//注入各个类的成员变量属性
		for(Object obj : instanceList){
			if(obj instanceof InstanceOperation){
				instanceOperation_List.add((InstanceOperation)obj);
			}
			
			if(obj instanceof IZKMaster){
				materInitList.add((IZKMaster)obj);
			}
			
			Class<?> cla = obj.getClass();
			for(;cla != Object.class;cla = cla.getSuperclass()){
				Field[] fields = cla.getDeclaredFields();
				IOC  ioc = null;
				for(Field field : fields){
					ioc = field.getAnnotation(IOC.class);
					if(ioc == null) continue;
						try {
							field.setAccessible(true);
							Object val = InstanceFactory.get().getInstance(field.getType());
							field.set(obj, val);
						} catch (IllegalArgumentException e) {
							LoggerManager.error("instance error:",e);
						} catch (IllegalAccessException e) {
							LoggerManager.error("instance error:",e);
						}
				}
			}
		}
		//
		Collections.sort(instanceOperation_List, Comparator.comparingInt(InstanceOperation::getOrder));
		Collections.sort(CLOSE_LIST, Comparator.comparingInt(CloseOperation::getOrder));
		ExcelConsole.get().reload();
		for(InstanceOperation io : instanceOperation_List){
			io.instanceAfter();
			io.initConfig();
		}
		for(IZKMaster master : materInitList){
			master.masterInit(GameSource.serverName);
		}
		
	}
	
	public void resetConfig(){
		instanceOperation_List.stream().forEach(io->io.initConfig());
	}
	
	//
	public void put(Object service){
		put(service.getClass().getSimpleName(), service);
	}
	
	public void put(String key,Object service){
		LoggerManager.debug("对象已经加载成功:[{}]",key);
		IOC_MAP.put(key, service);
//		LoggerManager.debug("=======================================================");
		if(service instanceof CloseOperation){
			LoggerManager.debug("添加服务器停服钩子操作[{}]",key);
			CLOSE_LIST.add((CloseOperation)service);
		}
		if(service instanceof OfflineOperation){
			LoggerManager.debug("添加玩家离线操作[{}]",key);
			OFFLINE_LIST.add((OfflineOperation)service);
		}
	}
	
	public void putMethod(int key,Method method,Object instance){
		String name = instance.getClass().getSimpleName()+"-["+method.getName()+"]";
		LoggerManager.debug("Service接口加载成功:"+name+"-code->"+key);
		METHOD_MAP.put(key, new ServerMethod(key, method, instance,name));
	}
	
	public void putRPCMethod(int key,Method method,Object instance,String pool,ERPCType type){
		String name = instance.getClass().getSimpleName()+"-["+method.getName()+"]";
		LoggerManager.debug("Service接口加载成功:"+name+"-code->"+key);
		METHOD_MAP.put(key, new RPCServerMethod(key, method, instance,name,pool,type));
	}
	
	public void putUnCheck(int key){
		LoggerManager.debug("无需验证接口加载成功:"+key);
		CODE_LIST.add(key);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends ServerMethod> T  getServerMethodByCode(int keyCode){
		return (T)METHOD_MAP.getOrDefault(keyCode,ServerMethod.REJECT_METHOD);
	}
	
	public Database getDataBaseByKey(String keyCode){
		return DATABASE_MAP.get(keyCode);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getInstance(Class<T> cla){
		T t = (T)IOC_MAP.get(cla.getSimpleName());
		if(t == null){
			try {
//				if(cla.getInterfaces()[0] != null && cla.getInterfaces()[0].equals(IRPCService.class)){//RPC接口动态代理实例化
//					RPCInvocationHandler handler = new RPCInvocationHandler();
//					t = (T)Proxy.newProxyInstance(IRPCService.class.getClassLoader(), new Class<?>[] { cla }, handler);
//				}else{//走正常实例化
					t = cla.newInstance();
//				}
				put(t);
			} catch (InstantiationException | IllegalAccessException e) {
				LoggerManager.error("InstanceFactory-getInstance: "+ e.getMessage(),e);
				throw new RuntimeException(e);
			}
		}
		return t;
	}
	
	/**
	 * 取某个接口的所有实现类
	 * @param cal
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> getInstanceList(Class<T> cal) {
		List<T> list = Lists.newArrayList();
		for(Object o : IOC_MAP.values()) {
			if(Arrays.stream(o.getClass().getInterfaces()).anyMatch(i-> i == cal)) {
				list.add((T) o);
			}
		}
		return list;
	}
	
	public List<CloseOperation> getCloseOperationList(){
		return this.CLOSE_LIST;
	}
	public List<CloseOperation> addCloseOperationList(CloseOperation co){
		CLOSE_LIST.add(co);
		CLOSE_LIST.sort(Comparator.comparingInt(CloseOperation::getOrder));
		return this.CLOSE_LIST;
	}

	public List<OfflineOperation> getOfflineList(){
		return this.OFFLINE_LIST;
	}
	
	public void addDataBaseResource(String key,Object obj){
		if(obj instanceof JRedisServer){
			put(obj);
		}else if(obj instanceof Database){
			DATABASE_MAP.put(key, (Database)obj);
		}else {
			put(obj);
		}
	}
	
	private void startupContainer(String rcFile)throws Exception{
		StartupContextImpl scc=new StartupContextImpl();
		ScriptEngine se=new ScriptEngine();
		if(!new File(rcFile).exists()){
			LoggerManager.error("can not found config file:"+rcFile);
			return;
		}
		BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream(rcFile),"UTF-8"));  		
		se.execute(in, scc);
	}
	
	public void runInitScript(String rcFile){
		try {
			LoggerManager.debug("run config file:[{}]",rcFile);
			startupContainer(rcFile);
			//初始化好服务器基础属性，初始化日志全局属性
			LoggerManager.initThreadContext();
		} catch (Exception e) {
			LoggerManager.error("error when run command.[{}]",e.getMessage());
		}
	}
	
	/**
	 * 是否无需验证
	 * @param key
	 * @return
	 */
	public boolean isUnCheck(int key){
		return this.CODE_LIST.contains(key);
	}
	
}
