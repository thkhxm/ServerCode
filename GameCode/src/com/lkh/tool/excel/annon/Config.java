package com.lkh.tool.excel.annon;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.lkh.tool.excel.IBaseConfigBean;

@Target(value=ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Config {
	String fileName();
	String sheetName()default "Sheet1";
	String[] initNode()default "";//用逗号分隔进程名称
}
