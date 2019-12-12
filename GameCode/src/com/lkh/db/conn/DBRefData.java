package com.lkh.db.conn;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.lkh.annotation.db.DBAutoLoad;
import com.lkh.annotation.db.DBFiled;
import com.lkh.db.conn.dao.AsynchronousBatchDB;
import com.lkh.db.conn.dao.ConnectionException;
import com.lkh.db.conn.dao.Database;
import com.lkh.db.conn.dao.JDBCUtil;
import com.lkh.server.instance.InstanceFactory;
import com.lkh.util.LocalDateTimeUtil;
import com.lkh.util.StringUtil;

public class DBRefData {

	final private static Logger log = LoggerFactory.getLogger(DBRefData.class); 
	
	private Class<?> clas;
	private String tableName;
	private String keyName;
	private Map<String,MethodHandle> getter;
	//set方法映射, key是数据库中名称,val为对应的set方法句柄
	private Map<String,MethodHandle> setter;
	//字段名与数据库名对应映射
	private BiMap<String,String> nameMaps;
	//特殊处理转换方法
	private MethodHandle specliConvert;
	//查询单个数据初始化sql语句
	private String selectOneSql;
	//数据库中列名
	private String rowNames;
	
	private Database database;
	
	private Queue<AsynchronousBatchDB> source;
	
	public DBRefData(Class<?> clas) {
		this.getter = Maps.newHashMap();
		this.setter =Maps.newHashMap();
		this.nameMaps = HashBiMap.create();
		this.clas = clas;
		this.source = Queues.newLinkedBlockingDeque(2000);
		
		DBAutoLoad db = clas.getAnnotation(DBAutoLoad.class);
		this.tableName = db.tableName();
		this.database = InstanceFactory.get().getDataBaseByKey(db.dbData().getResName());
		if(this.database == null) {
			throw new NullPointerException();
		}
		try {
			initFiled();
			this.selectOneSql = StringUtil.formatString(
					"select * from {} where {} = ?"
						, this.tableName,this.keyName);
		} catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException e) {
			log.error("Db initFiled error#{}", e);
		}
	}
	
	//初始化所有缓存数据
	private void initFiled() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException {
		Field[] fields = this.clas.getDeclaredFields();
		DBFiled dbf = null;
		Lookup lu = MethodHandles.lookup();
		this.specliConvert = lu.findVirtual(this.clas, "specialConvert", MethodType.methodType(void.class,ResultSet.class));
		String key = "";
		for(Field field : fields) {
			dbf = field.getAnnotation(DBFiled.class);
			if(dbf == null) continue;
			key = Strings.isNullOrEmpty(dbf.columnName())?field.getName():dbf.columnName();
			this.nameMaps.put(field.getName(), key);
			if(dbf.customBean()) {//字段属于自定义对象,特殊处理
				MethodHandle mh = lu.findVirtual(this.clas,"_get"+StringUtil.upperCase(field.getName()), 
						MethodType.methodType(String.class));
				if(mh == null) {
					log.error("有自定义对象缺少Get方法#{}",this.clas.getName());
					System.exit(0);
				}
				this.getter.put(key,mh);
			}else {
				this.setter.put(key,lu.findVirtual(this.clas,"set"+StringUtil.upperCase(field.getName()), MethodType.methodType(void.class, field.getType())));
				if(field.getType()==boolean.class) {
					this.getter.put(key,lu.findVirtual(this.clas,"is"+StringUtil.upperCase(field.getName()), MethodType.methodType(field.getType())));
				}else {
					this.getter.put(key,lu.findVirtual(this.clas,"get"+StringUtil.upperCase(field.getName()), MethodType.methodType(field.getType())));
				}
			}
			if(dbf.key()) {
				this.keyName = key;
			}
		}
		//
		this.rowNames = this.nameMaps.values().stream().collect(Collectors.joining(","));
	}
	
	public void add(AsynchronousBatchDB db) {
		this.source.offer(db);
	}
	
	public String getResource(Object obj) {
		String str = this.nameMaps.values().stream().map(rowName->{
			try {
				Object result = this.getter.get(rowName).invoke(obj);
				return convertSqlString(result);
			} catch (Throwable e) {
				log.error("getSourceError#{}",e);
			}
			return "";
		}).collect(Collectors.joining("\t"));
		return str + "\n";
	}
	
	private String convertSqlString(Object obj) {
		String result = "";
		if(obj instanceof Date){
			LocalDateTime da = LocalDateTimeUtil.toLocalDateTime((Date)obj);
			result = LocalDateTimeUtil.formatDate(da);
		}else if(obj instanceof LocalDateTime){
			LocalDateTime da = (LocalDateTime) obj;
			result = LocalDateTimeUtil.formatDate(da);
		}else if(obj instanceof LocalDateTime){
			LocalDateTime la = (LocalDateTime)obj;
			result = LocalDateTimeUtil.formatDate(la);
		}else if(obj instanceof Boolean){
			boolean bl = (boolean)obj;
			result = ""+(bl?1:0);
		}else{
			result = obj.toString();
		}
		return result;
	}
	
	public String getRowNames() {
		return this.rowNames;
	}
	
	public String getTableName() {
		return this.tableName;
	}
	
	@SuppressWarnings("unchecked")
	public <DB> DB select(Object key) {
		try (Connection conn = database.getConnection(); 
				PreparedStatement ps = buildPs(conn,key); 
				ResultSet rs = ps.executeQuery();) {
			if (rs.next()) {
				DB obj = (DB)this.clas.newInstance();
				autoSetter(rs, obj);
				specliConvert.invoke(obj,rs);
				return obj;
			} else {
				return null;
			}
		} catch (ConnectionException | SQLException e) {
			log.error("Db connection error#{}", e);
		} catch (InstantiationException | IllegalAccessException e) {
			log.error("Db ref error#{}", e);
		} catch (Throwable e) {
			log.error("Db invoke error#{}", e);
		}
		return null;
	}
	
	
	@SuppressWarnings("unchecked")
	public <DB> List<DB> selectList(Object key) {
		List<DB> result = Lists.newArrayList();
		try (Connection conn = database.getConnection(); 
				PreparedStatement ps = buildPs(conn,key); 
				ResultSet rs = ps.executeQuery();) {
			while (rs.next()) {
				DB obj = (DB)this.clas.newInstance();
				autoSetter(rs, obj);
				specliConvert.invoke(obj,rs);
				result.add(obj);
			} 
		} catch (ConnectionException | SQLException e) {
			log.error("Db connection error#{}", e);
		} catch (InstantiationException | IllegalAccessException e) {
			log.error("Db ref error#{}", e);
		} catch (Throwable e) {
			log.error("Db invoke error#{}", e);
		}
		return result;
	}
	private void autoSetter(ResultSet rs,Object obj) {
		this.setter.forEach((key,val)->invoke(key, val, obj, rs));
	}
	
	private void invoke(String key,MethodHandle val,Object obj,ResultSet rs) {
			Object res;
			try {
				res = readValue(val.type().parameterType(1), rs, key);
				val.invoke(obj,res);
			} catch (SQLException e) {
				log.error("Db sql invoke error#{}", e);
			} catch (Throwable e) {
				log.error("Db ref invoke error#{}", e);
			}
	}
	
	
	@SuppressWarnings("unchecked")
	public <T> T readValue(Class<T> t, ResultSet set, String columnIndex) throws SQLException {
	    if (t == null || set == null) {
	        return null;
	    }
	     
	    if (t == Object.class) {
	        return (T)set.getObject(columnIndex);
	    }
	    if (t == Integer.class) {
	        Object val = set.getInt(columnIndex);
	        return (T)val;
	    }
	    if (t == int.class) {
	        Object val = set.getInt(columnIndex);
	        return (T)val;
	    }
	    if (t == short.class) {
	        Object val = set.getShort(columnIndex);
	        return (T)val;
	    }
	    if (t == Boolean.class) {
	        Object val = set.getBoolean(columnIndex);
	        return (T)val;
	    }
	    if (t == long.class) {
	        Object val = set.getLong(columnIndex);
	        return (T)val;
	    }
	    if (t == float.class) {
	        Object val = set.getFloat(columnIndex);
	        return (T)val;
	    }
	    if (t == double.class) {
	        Object val = set.getDouble(columnIndex);
	        return (T)val;
	    }
	    if (t == String.class) {
	        Object val = set.getString(columnIndex);
	        return (T)val;
	    }
	    if (t == java.sql.Date.class) {
	        Object val = set.getDate(columnIndex);
	        return (T)val;
	    }
	    if (t == java.sql.Time.class) {
	        Object val = set.getTime(columnIndex);
	        return (T)val;
	    }
	    if (t == java.sql.Timestamp.class) {
	        Object val = set.getTimestamp(columnIndex);
	        return (T)val;
	    }
	    if (t == LocalDateTime.class) {
	    	Timestamp ts = set.getTimestamp(columnIndex);
	    	Object val = LocalDateTimeUtil.converLocalDateTime(ts.getTime());
	        return (T)val;
	    }
	    if (t == Byte.class) {
	        Object val = set.getByte(columnIndex);
	        return (T)val;
	    }
	    return (T)set.getObject(columnIndex);      
	}
	
	private void loadDataLocal(InputStream is) {
		com.mysql.jdbc.PreparedStatement mysqlStatement = null;
		try(Connection 	conn = database.getConnection();
			PreparedStatement statement = conn.prepareStatement(StringUtil.formatString(
					"LOAD DATA LOCAL INFILE '{}.csv' REPLACE INTO TABLE {} ({})", tableName,tableName,rowNames));
			) {
			int result = 0;
			if (statement.isWrapperFor(com.mysql.jdbc.Statement.class)) {
				mysqlStatement = statement.unwrap(com.mysql.jdbc.PreparedStatement.class); 
				mysqlStatement.setLocalInfileInputStream(is);
				result = mysqlStatement.executeUpdate(); 
				log.debug(StringUtil.formatString("批量更新数据表[{}]，更新数据行[{}].", tableName,result));
			}
//			statement.execute();
		} catch (ConnectionException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
//			JDBCUtil.close(null,statement,conn);	
		}
	}
	
	
	public void updateData() {
		int size = this.source.size();
		if(size<=0) return;
		log.debug("批量更新对象类型[{}]",this.clas.getName());
		final StringBuffer sb = new StringBuffer("");
		String sql = ""; 
		String now = LocalDateTimeUtil.formatDate();
		AsynchronousBatchDB tmpDB = null;
		for(int i = 0; i<size;i++) {
			tmpDB = this.source.poll();
			if(tmpDB==null||!tmpDB.isSave()) break;
			tmpDB.unSave();
			sql = getResource(tmpDB);
			sb.append(sql);
			log.info("{}#{}#{}",this.tableName,sql,now);
		}
		InputStream is = new ByteArrayInputStream(sb.toString().getBytes());
		loadDataLocal(is);
		
	}
	
	
	
	
	
	private PreparedStatement buildPs(Connection conn,Object key) throws SQLException {
		PreparedStatement result = conn.prepareStatement(this.selectOneSql);
		JDBCUtil.set(conn, result, key);
		return result;
	}
}
