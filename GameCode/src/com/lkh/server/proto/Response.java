package com.lkh.server.proto;


import java.util.Arrays;

import com.google.protobuf.GeneratedMessage;
import com.lkh.tool.log.LoggerManager;
import com.lkh.util.ByteUtil;

public class Response {
	public static final byte[] EMPTY = new byte[0];
	private int reqId;
	private boolean iszip;
	private int serviceCode;
	private byte[] bytes = EMPTY;
	
	public Response(int serviceCode,GeneratedMessage data,int reqId){
		this.serviceCode = serviceCode;
		this.iszip = false;
		this.reqId = reqId;
		byte[] b = data.toByteArray();
		if(b.length>1020){
			this.iszip = true;
			LoggerManager.debug("压缩前[{}]", b.length);
			this.bytes = ByteUtil.compress(b);
			LoggerManager.debug("压缩后[{}]", this.bytes.length);
			
		}else{
			this.bytes = b;
//		this.bytes = data.toByteArray();
		}
	}
	
	public Response(int serviceCode,byte[] bytes,boolean iszip,int reqId){
		this.serviceCode = serviceCode;
		this.iszip = iszip;
		this.reqId = reqId;
		if(iszip&&bytes.length>1020){
			this.iszip = true;
			LoggerManager.debug("压缩前[{}]", bytes.length);
			byte[] b = ByteUtil.compress(bytes);
			LoggerManager.debug("压缩后[{}]", b.length);
			this.bytes = b;
		}else{
			this.bytes = bytes;
			this.iszip = false;
		}
	}
	
	public boolean isIszip() {
		return iszip;
	}
	public void setIszip(boolean iszip) {
		this.iszip = iszip;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public int getBytesSize() {
		return bytes.length;
	}

	public int getReqId() {
		return reqId;
	}

	public int getServiceCode() {
		return serviceCode;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	@Override
	public String toString() {
		return "Response [reqId=" + reqId + ", iszip=" + iszip
				+ ", serviceCode=" + serviceCode + ", bytes="
				+ Arrays.toString(bytes) + "]";
	}
	
	
}
