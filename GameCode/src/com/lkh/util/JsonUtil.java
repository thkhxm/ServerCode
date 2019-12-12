package com.lkh.util;

import com.alibaba.fastjson.JSON;

import java.lang.reflect.Type;

/**
 * @author tim.huang
 * 2016年11月7日
 *
 */
public class JsonUtil {	
	


	public static <T> T toObj(String son,Class<? extends T> c){
		return fromJson(son,c);
	}
	public static String toJson(Object src) {
//		if ( src == null ) {
//			return "null";
//		}
//		return gson.toJson(src);
		return JSON.toJSONString(src);
	}

//	/**
//	 * 转成json并格式化
//	 * @param src
//	 * @return
//	 */
//	public static String toJsonFormat(Object src) {
//
//		String jsonStr = toJson(src);
//		if ( jsonStr == null ) {
//			return "null";
//		}
//		return FormatUtil.formatJson(jsonStr);
//	}


	public static <T> T fromJson(String json, Class<T> clazz) {
		return JSON.parseObject(json, clazz);
	}

	public static <T> T fromJson(String json, Type typeOfT) {
		return JSON.parseObject(json, typeOfT);
	}


}
