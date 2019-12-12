package com.lkh.db.conn.dao;

import java.sql.Connection;
import java.sql.SQLException;

import com.lkh.annotation.db.Resource;



public class DataConnectionDAO extends BaseDAO{
	@Resource(value = ResourceType.DB_data)
	public Database database;
	/**
	 * Method getConnection.
	 * @return Connection
	 * @throws SQLException 
	 */
	protected Connection getRealConnection() {
		return database.getConnection();
	}
		
}
