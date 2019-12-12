package com.lkh.server.socket;

/**
 * 服务器配置
 * @author tim.huang
 * 2015年12月1日
 */
public interface SocketServerConfig {
	
	/**
	 * 获得Service层路径
	 * @return
	 */
//	String getServicePackgePath();
//	String getManagerPackgePath();
//	String getCommonPackgePath();
//	String getActiveManagerPackgePath();
//	String getAOPackgePath();
//	String getDAOPackgePath();
	String getPackagePath();
	String getJSScriptPath();
	String getIP();
	int getPort();
	String getPath();
	ClassLoader getClassLoader();
	
}
