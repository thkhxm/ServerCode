package com.lkh.script;

import com.lkh.db.conn.dao.ResourceType;
import com.lkh.server.GameSource;

public interface StartupContext {
	
	void addResource(ResourceType resName,Object obj);
	
	void setShardid(int shardid);
	
	void setCharset(String charset);
	
	public void setZKPath(String path);
	
	public void setServerName(String serverName);
	public void setPool(String pool);
	
	
}
