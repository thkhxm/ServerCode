package com.lkh.util.exception;

/**
 * @author tim.huang
 * 主要字段为空异常
 * 2018年9月7日
 */
public class RequiredNullException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		return "目标对象中,主要字段中,存在空值";
	}
	
	
	
	
}
