package com.lkh.client;

import java.util.Map;

import com.google.common.collect.Maps;
import com.lkh.server.ServerStat.MethodStat;
import com.lkh.util.StringUtil;

public class ClientStat {

	private static Map<Integer,MethodStat> methodStatMap = Maps.newConcurrentMap();
	
	public static void make(int serviceCode,long time){
		methodStatMap.computeIfAbsent(serviceCode, key->new MethodStat(key))
		.update(time);
	}
	
	public  static String methodInfo(){
		StringBuffer sb = new StringBuffer();
		methodStatMap.forEach((key,val)->sb.append(StringUtil.formatString("{}:调用次数[{}],最长耗时[{}],平均耗时[{}]\n"
											, key,val.getCount(),val.getMaxMill(),val.getAverageMill())));
		return sb.toString();
	}
	
	
}
