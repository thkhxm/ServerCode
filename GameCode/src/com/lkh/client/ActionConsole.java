package com.lkh.client;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lkh.client.robot.BaseRobot;
import com.lkh.server.instance.InstanceFactory;
import com.lkh.tool.log.LoggerManager;
import com.lkh.util.PathUtil;
import com.lkh.util.RandomUtil;

public class ActionConsole {
	
	public static Map<Integer,BaseAction<BaseRobot>> actionMap = Maps.newHashMap();
	public static Map<Integer,List<BaseAction<BaseRobot>>> actionTypeMap = Maps.newHashMap();
	
	public static void append(BaseAction<BaseRobot> action){
		actionMap.put(action.getServiceCode(), action);
		actionTypeMap.computeIfAbsent(action.getActionType(), key->Lists.newArrayList()).add(action);
	}
	
	public static BaseAction<BaseRobot> getAction(int serviceCode){
		return actionMap.get(serviceCode);
	}
	
	public static List<BaseAction<BaseRobot>> getActionList(int type){
		return actionTypeMap.get(type);
	}
	
	public static BaseAction<BaseRobot> getRanAction(int type){
		return actionTypeMap.get(type).get(RandomUtil.ran(actionTypeMap.get(type).size()));
	}
	
	
	public static void initAction()throws Exception{
		String osName = System.getProperties().getProperty("os.name");
		String path = ActionConsole.class.getResource("/").getPath();
		LoggerManager.debug("系统环境[{}],行为路径[{}]",osName,path);
		initInstance("com.ftkj.action","BaseAction",path,ActionConsole.class.getClassLoader());
		actionMap.values().forEach(ba->ba.init());
	}
	
	/**
	 * 初始化
	 * @param servicePath
	 * @throws Exception
	 */
	private static void initInstance(String servicePath,String superClassName,String path,ClassLoader cl) throws Exception {
		if("".equals(servicePath)) return;
		//
		LoggerManager.debug("初始化包路径:[{}],项目路径:[{}]",servicePath,path);
		List<String> cmdName = PathUtil.getAllName(servicePath);
//		LoggerManager.debug("实例化路径:[{}]",cmdName);
		String replaceStr = getReplaceStr(path);
		for (String cmd : cmdName) {
			cmd = cmd.replace(replaceStr, "").replace("/", ".");
			Class<?> cla =  cl.loadClass(cmd);
			Class<?> father = cla.getSuperclass();
			while(father != null){
				if (father!=null && father.getSimpleName().equals(superClassName)) {
					InstanceFactory.get().put(cla.newInstance());
					break;
				}else{
					father = father.getSuperclass();
				}
			}
		}
	}
	
	private static String getReplaceStr(String source){
		String osName = System.getProperties().getProperty("os.name");
		if(osName.toLowerCase().indexOf("windows") != -1){ //windows环境去掉开头的/
			source = source.substring(1, source.length());
		}
		return source;
	}
	
	
}
