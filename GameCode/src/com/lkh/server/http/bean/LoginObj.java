package com.lkh.server.http.bean;

public class LoginObj {
	private long plfUserid;
	private String channelId;
	private String channelName;
	public String getChannelId() {
		return channelId;
	}
	public String getChannelName() {
		return channelName;
	}
	public long getPlfUserid() {
		return plfUserid;
	}
	public void setPlfUserid(long plfUserid) {
		this.plfUserid = plfUserid;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}
	@Override
	public String toString() {
		return "LoginObj [plfUserid=" + plfUserid + ", channelId=" + channelId
				+ ", channelName=" + channelName + "]";
	}
}
