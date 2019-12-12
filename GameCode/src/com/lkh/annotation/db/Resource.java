package com.lkh.annotation.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.lkh.db.conn.dao.ResourceType;


/**
 * @author tim.huang
 * 2016年12月28日
 * 数据源辨识注解，运用在多数据库
 */
@Target(ElementType.FIELD)   
@Retention(RetentionPolicy.RUNTIME)   
public @interface Resource {
	public ResourceType value();   
}
