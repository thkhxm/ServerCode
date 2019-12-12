package com.lkh.annotation.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.lkh.db.conn.dao.ResourceType;

/**
 * @author Tim
 * 数据库自动加载
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DBAutoLoad {
	//表名
	String tableName();
	//是否分表
	boolean splTable() default false;
	//数据源配置
	ResourceType dbData();
	
}
