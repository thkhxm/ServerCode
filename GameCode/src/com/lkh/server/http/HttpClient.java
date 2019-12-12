package com.lkh.server.http;

import java.net.URLDecoder;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClient {
	//	public static final int MAX_HTTP_COUNT = 2000;
//	public static final int MAX_RESPONSE_QUEUE_SIZE = 1024*16*Runtime.getRuntime().availableProcessors();
	private static final int HTTP_TIME_OUT = 20*1000;
//	private static Queue<HttpResponse> responseList = Queues.newArrayBlockingQueue(MAX_RESPONSE_QUEUE_SIZE);
	static private Logger log = LoggerFactory.getLogger(HttpClient.class);

	public static String BASE_URL;


	public static <T> T get(String url,Class<T> clas,Object... parms) {
		try {
			url = formatUrlParms(url,true,parms);
			String context = get(url);
			T result = JSONObject.parseObject(context, clas);
			if (result == null) {
				log.error(url + "\t请求异常,请boss端查收\t" + JSONObject.toJSONString(parms));
			}
			return result;
		} catch (Exception e) {
			log.error("get rest url: {} error:",url,e);
		}
		return null;
	}

	public static <T> List<T> getList(String url, Class<T> clas) {
		try {
			String context = get(url);
			List<T> result = JSONArray.parseArray(context, clas);
			return result;
		} catch (Exception e) {
			log.error("getList rest url: {} error:",url,e);
		}
		return null;
	}

	public static <T> List<T> getList(String url,Class<T> clas,Object... parms) {
		try {
			url = formatUrlParms(url,true,parms);
			String context = get(url);
			List<T> result = JSONArray.parseArray(context, clas);
			if (result == null) {
				log.error(url + "\t请求异常,请boss端查收\t" + JSONObject.toJSONString(parms));
			}
			return result;
		} catch (Exception e) {
			log.error("get rest url: {} error:",url,e);
		}
		return null;
	}


	public static <T> T post(String url,Class<T> clas,Object... parms) {
		try {
			String body = formatUrlParms("",false,parms);
			String context = post(url,body);
			T result = JSONObject.parseObject(context, clas);
			if (result == null) {
				log.error(url + "\t请求异常,请boss端查收\t" + JSONObject.toJSONString(parms));
			}
			return result;
		} catch (Exception e) {
			log.error("post rest url: {} error:",url,e);
		}
		return null;
	}

	public static <T> T post(String url,String body,Class<T> clas) {
		try {
			String context = post(url,body);
			T result = JSONObject.parseObject(context, clas);
			return result;
		} catch (Exception e) {
			log.error("post rest url: {} , body:{},error:",url,body,e);
		}
		return null;
	}

//    public static <T> T post(String url, String body, TypeReference<T> reference) {
//    	try {
//    		log.trace("post url:{},data:{}",url,body);
//		    String context = post(url,body);
//	        T result = JSONObject.parseObject(context, reference);
//	        return result;
//		} catch (Exception e) {
//			log.error("get rest url: {} error:",url,e);
//		}
//    	return null;
//	}


	private static String get(String url) {
		String context = null;
		try {
			context = HttpUtil.get(url, HTTP_TIME_OUT);
		} catch (Exception e) {
			log.error("get url["+url+"] Error:",e);
		}
		return context;
	}

	private static String post(String url,String body) {
		String context = null;
		try {
			log.trace("post url:{},data:{}",url,body);
			context = HttpUtil.post(url, body,HTTP_TIME_OUT);
		} catch (Exception e) {
			log.error("post url["+url+"] Error:",e);
		}
		return context;
	}

	private static String patch(String url) {
		String body = HttpUtil.get(url, HTTP_TIME_OUT);
		return body;
	}

	private static String delete(String url) {
		String body = HttpUtil.get(url, HTTP_TIME_OUT);
		return body;
	}

	private static String formatUrlParms(String base,boolean get,Object... parms) {
		String result = base + (get?"?":"");
		List<String> tmpList = Lists.newArrayList();
		for(int i =0 ; i < parms.length ; ) {
			tmpList.add(parms[i]+"="+parms[i+1]);
			i+=2;
		}
		result += tmpList.stream().collect(Collectors.joining("&")).toString();
		log.trace("格式化url:{}",result);
		return result;
	}
	
	
	
	
}
