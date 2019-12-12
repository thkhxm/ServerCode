package com.lkh.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IPUtil {

	
	public static String localIP = "";
	public static String testServerIP = "";
	
	static {
		localIP = getLocalIp();
		testServerIP = getTestServerIp();
	}
	
	/**
	 * 本地IP
	 * @return
	 */
	public static String getLocalIp() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return "127.0.0.1";
	}
	
	/**
	 * 测试服务器IP
	 * @return
	 */
	public static String getTestServerIp(){
		return "192.168.10.181";
	}
	
}
