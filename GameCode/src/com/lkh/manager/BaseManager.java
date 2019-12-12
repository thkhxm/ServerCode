package com.lkh.manager;

import java.lang.reflect.Method;
import java.util.List;

import com.google.protobuf.GeneratedMessage;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lkh.annotation.RPCMethod;
import com.lkh.annotation.UnCheck;
import com.lkh.server.MessageManager;
import com.lkh.server.instance.InstanceFactory;
import com.lkh.server.instance.InstanceOperation;
import com.lkh.tool.redis.JedisUtil;

/**
 * 逻辑管理基类
 * @author tim.huang
 * 2015年11月27日
 */
public abstract class BaseManager implements InstanceOperation{
	protected final Logger log = LoggerFactory.getLogger(getClass());
	protected JedisUtil redis;
//	static private Map<Class<?>,BaseManager> managerMapper = Maps.newConcurrentMap();
	protected BaseManager(){
		redis = InstanceFactory.get().getInstance(JedisUtil.class);
//		redis = JedisUtil.getInstance();
		initMethod();
//		managerMapper.put(getClass(), this);
	}
	
//	static final protected <T extends BaseManager> T  getManager(Class<?> key){
//		return (T)managerMapper.get(key);
//	}
	
	/**
	 * 初始化客户端接口
	 */
	private void initMethod(){
		//                                                                                                                      
//		String objectName = this.getClass().getSimpleName();
		for(Method method : getClass().getDeclaredMethods()){
			method.setAccessible(true);
			ClientMethod cm = method.getAnnotation(ClientMethod.class);
			if(cm != null){
//				String key = objectName+PathUtil.POINT+method.getName();
				InstanceFactory.get().putMethod(cm.code(), method, this);
				UnCheck uc = method.getAnnotation(UnCheck.class);
				if(uc != null){
					InstanceFactory.get().putUnCheck(cm.code());
				}
			}
			//初始化跨服节点接口实现类
			RPCMethod rm = method.getAnnotation(RPCMethod.class);
			if(rm != null){
				InstanceFactory.get().putRPCMethod(rm.code(), method, this,rm.pool(),rm.type());
			}
		}
		//
	}
	
	@Override
	public int getOrder() {
		return Integer.MAX_VALUE;
	}
	
	protected long getPlayerId(){
		return MessageManager.getPlayerId();
	}
	
	protected IoSession getSession(){
		return MessageManager.getSession();
	}
	
	protected int getRid(){
		return MessageManager.getReqId();
	}
	protected void sendMessage(GeneratedMessage data){
		MessageManager.sendMessage(data);
	}

	@Deprecated
	protected void sendMessage(GeneratedMessage data,int serviceCode){
		MessageManager.sendMessage(data, serviceCode);
	}

	protected void sendMessage(long teamId,GeneratedMessage data,int serviceCode){
		MessageManager.sendMessage(teamId, data, serviceCode);
	}

	protected void sendMessage(long teamId,GeneratedMessage data,int serviceCode,int rid){
		MessageManager.sendMessage(teamId, data, serviceCode, rid);
	}
	protected void sendMessage(List<User> users,GeneratedMessage data,int serviceCode){
		MessageManager.sendMessage(users, data, serviceCode);
	}
	protected void sendMessageTeamIds(List<Long> teamIds, GeneratedMessage data, int serviceCode){
		MessageManager.sendMessageTeamIds(teamIds, data, serviceCode);
	}
	protected void sendMessage(String service, GeneratedMessage data, int serviceCode){
		MessageManager.sendMessage(service, data, serviceCode);
	}
	protected void sendMessage(long teamId,String service,GeneratedMessage data,int serviceCode){
		MessageManager.sendMessage(teamId,service, data, serviceCode);
	}

	protected void sendMessage(String service, List<Long> filterTeam, GeneratedMessage data,int serviceCode){
		MessageManager.sendMessage(service, filterTeam, data, serviceCode);
	}


//	protected void sendMessage(GeneratedMessageV3 data){
//		MessageManager.sendMessage(data);
//	}
//
//	@Deprecated
//	protected void sendMessage(GeneratedMessageV3 data,int serviceCode){
//		MessageManager.sendMessage(data, serviceCode);
//	}
//
//	protected void sendMessage(long teamId,GeneratedMessageV3 data,int serviceCode){
//		MessageManager.sendMessage(teamId, data, serviceCode);
//	}
//
//	protected void sendMessage(long teamId,GeneratedMessageV3 data,int serviceCode,int rid){
//		MessageManager.sendMessage(teamId, data, serviceCode, rid);
//	}
//	protected void sendMessage(List<User> users,GeneratedMessageV3 data,int serviceCode){
//		MessageManager.sendMessage(users, data, serviceCode);
//	}
//	protected void sendMessageTeamIds(List<Long> teamIds, GeneratedMessageV3 data, int serviceCode){
//		MessageManager.sendMessageTeamIds(teamIds, data, serviceCode);
//	}
//	protected void sendMessage(String service,GeneratedMessageV3 data,int serviceCode){
//		MessageManager.sendMessage(service, data, serviceCode);
//	}
//	protected void sendMessage(long teamId,String service,GeneratedMessageV3 data,int serviceCode){
//		MessageManager.sendMessage(teamId,service, data, serviceCode);
//	}
//
//	protected void sendMessage(String service, List<Long> filterTeam, GeneratedMessageV3 data,int serviceCode){
//		MessageManager.sendMessage(service, filterTeam, data, serviceCode);
//	}
}
