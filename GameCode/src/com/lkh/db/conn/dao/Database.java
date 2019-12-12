package com.lkh.db.conn.dao;

import java.sql.Connection;
import java.sql.SQLException;

import com.lkh.server.GameSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class Database {
	HikariDataSource db;
//	DataSource db;
	
	public Database(Jdbc j){
		try{
			//System.out.println("===jdbc:"+j.toString());
//			Properties p = new Properties();
//			if(GameSource.isDebug) return;
			Class.forName(j.getDriver());
			System.err.println("init dbconfig-----------"+j.getConfigName());
			HikariConfig config = new HikariConfig(j.getConfigName());
			System.out.println("init dbconfig-----------"+j.getConfigName());
			System.out.println(config.getJdbcUrl());
			System.out.println(config.getConnectionTimeout());
			System.out.println(config.getMaximumPoolSize());
			System.out.println(config.getMinimumIdle());
			System.out.println(config.getUsername());
			System.out.println(config.getPassword());
			System.out.println("init dbconfig-----------"+j.getConfigName());
			db = new HikariDataSource(config);
//			HashMap<String,Object> map = new HashMap<String,Object>();
//			p.setProperty("user", j.getUsername());
//			p.setProperty("password", j.getPassword());			
//			p.setProperty("driverClass", j.getDriver());
//			Class.forName("com.mysql.jdbc.Driver");
//			map.put("maxPoolSize", 30);
//			map.put("minPoolSize", 10);
//			map.put("checkoutTimeout", 1000*5);
//			map.put("idleConnectionTestPeriod", 60*10);
//			map.put("maxIdleTime", 60*3);
//			map.put("maxStatements", 100);
//			db = DataSources.pooledDataSource(DataSources.unpooledDataSource(j.getUrl(), p), map);
//			BoneCPConfig config = new BoneCPConfig();
//			config.setJdbcUrl(j.getUrl()); 
//			config.setUsername(j.getUsername()); 
//			config.setPassword(j.getPassword());
			//每个分区最小连接数量
//			config.setMinConnectionsPerPartition(j.getMinConnectionsPerPartition());
			//每个分区最大连接数量
//			config.setMaxConnectionsPerPartition(j.getMaxConnectionsPerPartition());
			//连接耗尽时,一次性创建的连接数
//			config.setAcquireIncrement(j.getAcquireIncrement());
			//分区数量
//			config.setPartitionCount(j.getPartitionCount());
			
//			db = new BoneCP(config); // setup the connection pool
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public Connection getConnection()throws ConnectionException{
		try {
			Connection connection = db.getConnection();
			return connection;
		} catch (SQLException e) {
			throw new ConnectionException(e.toString());
		}
	}
	
	
}
