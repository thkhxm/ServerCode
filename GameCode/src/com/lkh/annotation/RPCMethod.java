package com.lkh.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.lkh.enums.ERPCType;

/**
 * @author tim.huang
 * 2017年4月5日
 * rpc方法注解
 * 初始化方法属性
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RPCMethod {
	int code();
	ERPCType type();
	String pool();
}
