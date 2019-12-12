package com.lkh.annotation.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Tim
 * 数据库字段自动注入标识
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DBFiled {
	//字段是否为主键，一个实体对象只能有一个字段为true。
	boolean key() default false;
	//字段对应列名，如果留空则为使用字段默认命名
	String columnName() default "";
	//是否自定义数据对象，如果是自定义数据对象，需要添加返回Stirng数据类型的get方法，用于自动化
	boolean customBean() default false;
	
	
	
	
	
}
