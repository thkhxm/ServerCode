package com.lkh.util.lambda;

import java.io.Serializable;

public class LBFloat implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private float val;
	
	public void sum(float v){
		this.val+=v;
	}
	
	public float getVal() {
		return val;
	}

	public void setVal(float val) {
		this.val = val;
	}
	
}
