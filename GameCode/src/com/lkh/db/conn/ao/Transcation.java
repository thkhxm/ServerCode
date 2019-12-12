package com.lkh.db.conn.ao;

import com.lkh.db.conn.dao.ConnectionHolder;


public class Transcation {
	/**是否需要事物*/
	private boolean needTranscation;
	/**数据库连接对象*/
	private ConnectionHolder connection;
	//
	public Transcation(){
		this.needTranscation = false;
	}
	
	public boolean isNeedTranscation() {
		return needTranscation;
	}

	public void setNeedTranscation(boolean needTranscation) {
		this.needTranscation = needTranscation;
	}

	public ConnectionHolder getConnection() {
		return connection;
	}

	public void setConnection(ConnectionHolder connection) {
		this.connection = connection;
	}

}
