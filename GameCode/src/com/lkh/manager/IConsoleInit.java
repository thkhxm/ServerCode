package com.lkh.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tim
 *
 *	配置管理初始化接口，Console类需要使用单例，提供一个无参的静态get方法获取该console对象
 */
public interface IConsoleInit {
	public static Logger log = LoggerFactory.getLogger("Console");
	void init();
	default public int getOrder() {
		return Integer.MAX_VALUE;
	}
	
	static int Prop = 10;
}
