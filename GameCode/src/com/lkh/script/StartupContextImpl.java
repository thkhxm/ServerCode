package com.lkh.script;

import com.lkh.db.conn.dao.ResourceType;
import com.lkh.enums.EVersion;
import com.lkh.server.GameSource;
import com.lkh.server.instance.InstanceFactory;


public class StartupContextImpl implements StartupContext{
	
	public StartupContextImpl() {
		
	}
	
	public void addResource(ResourceType resName,Object obj){
		InstanceFactory.get().addDataBaseResource(resName.getResName(), obj);
	}
	
	public void setServerName(String serverName){
		GameSource.serverName = serverName;
	}
	
	@Override
	public void setPool(String pool) {
		GameSource.pool = pool;
	}

	public void setZKPath(String path){
		GameSource.zkPath = path;
	}
	
	public void setShardid(int shardid){
		GameSource.shardId = shardid;
	}
	public void setCharset(String charset) {
		GameSource.charset = EVersion.valueOf(charset);
	}

}
