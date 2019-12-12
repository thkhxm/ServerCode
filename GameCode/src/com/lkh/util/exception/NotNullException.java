package com.lkh.util.exception;

/**
 * @author tim.huang
 * 非空异常
 * 2018年9月7日
 */
public class NotNullException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		return "试图为一个非空的对象赋值";
	}
	
}
