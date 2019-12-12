package com.lkh.db.conn.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.lkh.db.conn.DataCache;

/**
 * @author tim.huang
 * 2016年2月19日
 * 批量异步db操作抽象父类
 */
public abstract class AsynchronousBatchDB{
	
	/**
	 * 
	 */
	protected boolean sourceStatus = false;
//	/**
//	 * 操作数据拼接
//	 * 数据用【\t】分割,用【\n】结束
//	 * @return
//	 */
//	public abstract String getSource();
//	
//	/**
//	 * 数据操作列名拼接
//	 * 列名用【，】分割
//	 * @return
//	 */
//	public abstract String getRowNames();
//	
//	/**
//	 * 获得数据表名
//	 * @return
//	 */
//	public abstract String getTableName();
	
	public abstract void del();
	
	/**
	 * 调用save方法，表示该数据需要保存
	 */
	public synchronized void save(){
		if(this.sourceStatus){
			return;
		}
		this.sourceStatus = true;
		DataCache.get().addResource(this);
	}
	
	/**
	 * 调用unSave方法，表示该数据已经执行过操作
	 */
	public synchronized void unSave(){
		this.sourceStatus = false;
	}
	
	
	public synchronized boolean isSave(){
		return this.sourceStatus;
	}
	
	/**
	 * 特殊数据转换
	 */
	public abstract void specialConvert(ResultSet rs)throws SQLException;
	
}
