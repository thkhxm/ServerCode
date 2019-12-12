package com.lkh.tool.redis;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.jredis.ri.alphazero.support.DefaultCodec;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.lkh.tool.log.LoggerManager;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.ShardedJedisPool;

/**
 * @author tim.huang
 * 2017年5月15日
 *
 */
public class JedisUtil {
	
	 /**
	 * 缓存生存时间
	 */
	 private final int expire = 60000;
//	 /** 操作Key的方法 */
//	 public Keys KEYS;
//	 /** 对存储结构为String类型的操作 */
//	 public Strings STRINGS;
//	 /** 对存储结构为List类型的操作 */
//	 public Lists LISTS;
// 	 /** 对存储结构为Set类型的操作 */
//	 public Sets SETS;
//	 /** 对存储结构为HashMap类型的操作 */
//	 public Hash HASH;
//	 /** 对存储结构为Set(排序的)类型的操作 */
//	 public SortSet SORTSET;
	 private JedisPool jedisPool = null; 
  	 private ShardedJedisPool shardedJedisPool = null;
	
	 public JedisUtil(Jredis j) {
		 init(j);
	 }
     	
	  /**
	     * 构建redis连接池
	     * @param ip
	     * @param port
	     * @return JedisPool
	     */
	   private void init(Jredis j) {
//	    	ResourceBundle bundle = ResourceBundle.getBundle("redis");
//			if (bundle == null) {
//				throw new IllegalArgumentException(
//						"[redis.properties] is not found!");
//			}
	        if (jedisPool == null) { 
	            JedisPoolConfig config = new JedisPoolConfig();
	            config.setMaxIdle(RedisConfig.MAX_IDLE);
	            config.setMaxTotal(j.getConnectionCount());
	            config.setTestOnBorrow(true);
	            config.setTestOnReturn(true);
	            jedisPool = new JedisPool(config, j.getHost(), j.getPort(),RedisConfig.TIMEOUT,j.getPassword(),j.getDatabase()); 
	        }
//	        KEYS = new Keys();
//	        STRINGS = new Strings();
//	        LISTS = new Lists();
//	        SETS = new Sets();
//	        HASH = new Hash();
//	        SORTSET = new SortSet();
	    }
	   
	   public static void main(String[] args) {
		   Jredis j = new Jredis();
		   j.setPort(6379);
		   j.setConnectionCount(2);
		   j.setDatabase(0);
		   j.setHost("192.168.10.181");
		   j.setPassword("zgame2017");
		   JedisUtil util = new JedisUtil(j);
		   
		   Map<Integer, Integer> map = Maps.newConcurrentMap();
		   map.put(1, 2);
		   util.putMapAllValue("aaa111", map);
		   System.err.println(util.getMapAllKeyValues("aaa111").toString());
//			try(Jedis jd = util.getJedis()){
//				
//				byte[] keys = DefaultCodec.encode("112233");
//				byte[] values = DefaultCodec.encode("111");
//				Long rank = jd.zrevrank(keys, values);
//				System.err.println(rank);
				//取start到end的数据
//				byte[] keys = DefaultCodec.encode("sortTest2");
//				byte[] mapkeys = DefaultCodec.encode("hmapTest2");
//				byte[] data = DefaultCodec.encode("10001");
//				byte[] data2 = DefaultCodec.encode("10002");
//				byte[] data3 = DefaultCodec.encode("10003");
//				jd.zadd(keys, 10,data);
//				jd.zadd(keys, 40,data2);
//				jd.zadd(keys, 5,data3);
//				List<byte[]> resultList = DefaultCodec.decode());
				//从map中取对应的数据
//				List<byte[]> source =new ArrayList<byte[]>(jd.zrevrange(keys, 5, 10));
//				jd.hset(mapkeys, data, DefaultCodec.encode("33333"));
//				jd.hset(mapkeys, data2, DefaultCodec.encode("44444"));
//				jd.hset(mapkeys, data3, DefaultCodec.encode("55555"));
//				byte[][] fileds = new byte[source.size()][];
//				for(int i = 0 ; i < source.size() ; i++){
//					fileds[i] = source.get(i);
//				}
//				List<byte[]> ss = jd.hmget(mapkeys, fileds);
//				if(ss == null || ss.size() <= 0)
//					System.err.println("空数据");
//				else{
//					List<Long> datas = DefaultCodec.toLong(ss);
//					datas.forEach(s->System.err.println(s));
//				}
//			}
			
			
//		   try(Jedis jd =  util.getJedis()){
//			   ArrayList<String> ss = new ArrayList<String>();
//			   ss.add("22");
//			   jd.hset(DefaultCodec.encode("mapTest"), DefaultCodec.encode("rank"), DefaultCodec.encode(ss));
//			   byte [] ff = jd.hget(DefaultCodec.encode("mapTest"), DefaultCodec.encode("rank"));
//			   System.err.println(DefaultCodec.decode(ff));
//		   }
//		   JedisUtil util = JedisUtil.getInstance();
//		   Jedis j = util.getJedis();
//		   for(int i = 0 ; i < 20 ; i++){
//			   j.set("jedisTest", "tim");
//			   System.err.println(j.get("jedisTest"));
//			   util.STRINGS.set("jedisTest", "tim");
//			   System.err.println(util.STRINGS.get("jedisTest"));
//		   }
	   }
	 
		public JedisPool getPool() {
			return jedisPool;
		}

		public ShardedJedisPool getShardedJedisPool() {
			return shardedJedisPool;
		}
		
        /**
         * 从jedis连接池中获取获取jedis对象  
         * @return
         */
	    public Jedis getJedis() { 
			return jedisPool.getResource();
		}
	
	    /**
	     * 回收jedis
	     * @param jedis
	     */
	    public void returnJedis(Jedis jedis) {
	    	jedis.close();
//			jedisPool.returnResource(jedis);
		}

	    
	    /**
		 * 设置过期时间
		 * 
		 * @author ruan 2013-4-11
		 * @param key
		 * @param seconds
		 */
		public void expire(String key, int seconds) {
			if (seconds <= 0) {
				return;
			}
			Jedis jedis = getJedis();
			jedis.expire(key, seconds);
			returnJedis(jedis);
		}

		/**
		 * 设置默认过期时间
		 * 
		 * @author ruan 2013-4-11
		 * @param key
		 */
		public void expire(String key) {
			expire(key, expire);
		}
		
		public void del(String key){
			try(Jedis jedis = getJedis()){
				byte[] keybyte = DefaultCodec.encode(key);
				jedis.del(keybyte);
			}catch(Exception e){
				LoggerManager.error("Redis Error->key[{}],stack[{}]",key,e);
			}
		}
		
		//对List操作的命令	
		public <T extends Serializable>void pushxx(String key,List<T> obj,int expire){
			try(Jedis jedis = getJedis()){
				byte[] keybyte = DefaultCodec.encode(key);
				jedis.set(keybyte, DefaultCodec.encode(new ListObj<T>(obj)));
				jedis.expire(keybyte, expire);
			}catch(Exception e){
				LoggerManager.error("Redis Error->key[{}],stack[{}]",key,e);
			}
		}
		
		//对List操作的命令	
		public <T extends Serializable>void pushxx(String key,List<T> obj){
			try(Jedis jedis = getJedis()){
				byte[] keybyte = DefaultCodec.encode(key);
				jedis.set(keybyte, DefaultCodec.encode(new ListObj<T>(obj)));
				jedis.expire(keybyte, expire);
			}catch(Exception e){
				LoggerManager.error("Redis Error->key[{}],stack[{}]",key,e);
			}
		}
		
		public <T extends Serializable> void set(String key, T obj, int expire){
			try(Jedis jedis = getJedis()){
				byte[] keybyte = DefaultCodec.encode(key);
				jedis.set(keybyte, DefaultCodec.encode(obj));
				jedis.expire(keybyte, expire);
			}catch(Exception e){
				LoggerManager.error("Redis Error->key[{}],stack[{}]",key,e);
			}
		}
		
		/**
		 * 不支持基础类型，请传字符串
		 * @param key
		 * @param obj
		 */
		public <T extends Serializable> void set(String key, T obj){
			try(Jedis jedis = getJedis()){
				byte[] keybyte = DefaultCodec.encode(key);
//				if(obj instanceof Integer){
//					int val = (int) obj;
//					jedis.set(keybyte, DefaultCodec.encode(""+val));
//					return;
//				}
				
				jedis.set(keybyte, DefaultCodec.encode(obj));
			}catch(Exception e){
				LoggerManager.error("Redis Error->key[{}],stack[{}]",key,e);
			}
		}
		
		
		//取数据	
		public <T extends Serializable> List<T> listxx(String key){
			try(Jedis jedis = getJedis()){
				byte[] keybyte = DefaultCodec.encode(key);
				byte[] databyte = jedis.get(keybyte);
				if(databyte == null) return null;
				ListObj<T> res = DefaultCodec.decode(databyte);
				return res.getList();
			}catch(Exception e){
				LoggerManager.error("Redis Error->key[{}],stack[{}]",key,e);
			}
			return null;
		}
		

		//返回数据库中名称为key的string的value
		public String getStr(String key){
			try(Jedis jedis = getJedis()){
				byte[] keybyte = DefaultCodec.encode(key);
				byte[] databyte = jedis.get(keybyte);
				if(databyte == null) return null;
				String res = DefaultCodec.decode(databyte);
				return res;
			}catch(Exception e){
				LoggerManager.error("Redis Error->key[{}],stack[{}]",key,e);
			}
			return null;
		}
		
		/**
		 * 返回数据库中名称为key的string的value
		 * @param key
		 * @return 默认空返回-1
		 */
		public int getInt(String key){
			try(Jedis jedis = getJedis()){
				byte[] keybyte = DefaultCodec.encode(key);
				byte[] databyte = jedis.get(keybyte);
				if(databyte == null) return -1;
				String res = DefaultCodec.decode(databyte)	;
//				return res;
				return Ints.tryParse(res);
			}catch(Exception e){
				LoggerManager.error("Redis Error->key[{}],stack[{}]",key,e);
			}
			return -1;
		}
		
		/**
		 * 如果空就返回0
		 * 注意，不能存-1的值，-1当做0来处理
		 * @param key
		 * @return
		 */
		public int getIntNullIsZero(String key) {
			int value = getInt(key); 
			return value == -1 ? 0 : value;
		}
		
		public <T extends Serializable>T getObj(String key){
			try(Jedis jedis = getJedis()){
				byte[] keybyte = DefaultCodec.encode(key);
				byte[] databyte = jedis.get(keybyte);
				if(databyte == null) return null;
				T res = DefaultCodec.decode(databyte);
				return res;
			}catch(Exception e){
				LoggerManager.error("Redis Error->key[{}],stack[{}]",key,e);
			}
			return null;
		}
		
		public long getLong(String key){
			try(Jedis jedis = getJedis()){
				byte[] keybyte = DefaultCodec.encode(key);
				byte[] databyte = jedis.get(keybyte);
				if(databyte == null) return 0;
				String res = DefaultCodec.decode(databyte);
				return Longs.tryParse(res);
			}catch(Exception e){
				LoggerManager.error("Redis Error->key[{}],stack[{}]",key,e);
			}
			
			return 0;
		}
		
		//确认一个key是否存在
		public boolean exits(String key){
			try(Jedis jedis = getJedis()){
				byte[] keybyte = DefaultCodec.encode(key);
				return jedis.exists(keybyte);
			}catch(Exception e){
				LoggerManager.error("Redis Error->key[{}],stack[{}]",key,e);
			}
			return true;
		}
		
		public long incr(String key){
			try(Jedis jedis = getJedis()){
				byte[] keybyte = DefaultCodec.encode(key);
				return jedis.incr(keybyte);
			}catch(Exception e){
				LoggerManager.error("Redis Error->key[{}],stack[{}]",key,e);
			}
			return 0;
		}
		
		public long incr(String key ,int timeout){
			try(Jedis jedis = getJedis()){
				byte[] keybyte = DefaultCodec.encode(key);
				long num = jedis.incr(keybyte);
				jedis.expire(keybyte, timeout);
				return num;
			}catch(Exception e){
				LoggerManager.error("Redis Error->key[{}],stack[{}]",key,e);
			}
			return 0;
		}
		
		/**
		 * 在尾部添加
		 * @param key
		 * @param value
		 */
		public <T extends Serializable> void addListValueR(String key, T value){
			try(Jedis jedis = getJedis()){
				byte[] keybyte = DefaultCodec.encode(key);
				jedis.rpush(keybyte, DefaultCodec.encode(value));
			}catch(Exception e){
				LoggerManager.error("Redis Error->key[{}],stack[{}]",key,e);
			}
		}
		
		/**
		 * 在头部添加
		 * @param key
		 * @param value
		 */
		public <T extends Serializable> void addListValueL(String key, T value){
			try(Jedis jedis = getJedis()){
				byte[] keybyte = DefaultCodec.encode(key);
				jedis.lpush(keybyte, DefaultCodec.encode(value));
			}catch(Exception e){
				LoggerManager.error("Redis Error->key[{}],stack[{}]",key,e);
			}
		}
		
		
		
		//取数据	
		public <T extends Serializable>List<T> getList(String key){
			return getList(key, 0, -1);
		}
		
		
		//取数据	
		public <T extends Serializable>List<T> getList(String key,int start,int end){
			try(Jedis jedis = getJedis()){
				byte[] keybyte = DefaultCodec.encode(key);
				List<byte[]> databyte = jedis.lrange(keybyte, start, end);
				if(databyte == null) return Lists.newArrayList();
				return DefaultCodec.decode(databyte);
			}catch(Exception e){
				LoggerManager.error("Redis Error->key[{}],stack[{}]",key,e);
			}
			return Lists.newArrayList();
		}
		
		
		public <T extends Serializable> long removeListValue(String key,T value){
			if(value == null) return 0;
			try(Jedis jedis = getJedis()){
				byte[] keybyte = DefaultCodec.encode(key);
				byte[] valuebyte = DefaultCodec.encode(value);
				long count = jedis.lrem(keybyte, 0, valuebyte);
//				List<byte[]> databyte = jedis.lrange(keybyte, 0, -1);
//				if(databyte == null) return null;
				return count;
			}catch(Exception e){
				LoggerManager.error("Redis Error->key[{}],stack[{}]",key,e);
			}
			return 0;
		}
		
		public <T extends Serializable>void push(String key,List<T> obj){
			try(Jedis jedis = getJedis()){
				byte[] keybyte = DefaultCodec.encode(key);
				jedis.del(keybyte);
				for(T t : obj){
					jedis.rpush(keybyte, DefaultCodec.encode(t));
				}
			}catch(Exception e){
				LoggerManager.error("Redis Error->key[{}],stack[{}]",key,e);
			}
		}
		
		
		public <T extends Serializable,K extends Serializable> T getMapValue(String key,K field){
			try(Jedis jedis = getJedis()){
				byte [] source = jedis.hget(DefaultCodec.encode(key), DefaultCodec.encode(field));
				if(source == null) return null;
				return DefaultCodec.decode(source);
			}catch(Exception e){
				LoggerManager.error("Redis Error->key[{}],stack[{}]",key,e);
			}
			return null;
		}
		
		
		public <T extends Serializable> T getMapValue(byte[] key,byte[] field){
			try(Jedis jedis = getJedis()){
				byte [] source = jedis.hget(key, field);
				if(source == null) return null;
				return DefaultCodec.decode(source);
			}catch(Exception e){
				LoggerManager.error("Redis Error->key[{}],stack[{}]",key,e);
			}
			return null;
		}
		
		public <T extends Serializable,K extends Serializable> void putMapValue(String key,K field,T value){
			try(Jedis jedis = getJedis()){
				jedis.hset(DefaultCodec.encode(key), DefaultCodec.encode(field), DefaultCodec.encode(value));
			}catch(Exception e){
				LoggerManager.error("Redis Error->key[{}],stack[{}]",key,e);
			}
		}
		
		public <T extends Serializable,K extends Serializable> void removeMapValue(String key,K field){
			try(Jedis jedis = getJedis()){
				jedis.hdel(DefaultCodec.encode(key), DefaultCodec.encode(field));
			}catch(Exception e){
				LoggerManager.error("Redis Error->key[{}],stack[{}]",key,e);
			}
		}
		
		public byte[] getMapBytes(String key,String field){
			try(Jedis jedis = getJedis()){
				return jedis.hget(DefaultCodec.encode(key), DefaultCodec.encode(field));
			}catch(Exception e){
				LoggerManager.error("Redis Error->key[{}],stack[{}]",key,e);
			}
			return null;
		}
		
		public <T extends Serializable> List<T> getMapValues(String key){
			try(Jedis jedis = getJedis()){
				return DefaultCodec.decode(jedis.hvals(DefaultCodec.encode(key)));
			}catch(Exception e){
				LoggerManager.error("Redis Error->key[{}],stack[{}]",key,e);
			}
			return null;
		}
		
		
		
		public <T extends Serializable,K extends Serializable> Map<T,K> getMapAllKeyValues(String key){
			try(Jedis jedis = getJedis()){
				Map<byte[],byte[]> mapData = jedis.hgetAll(DefaultCodec.encode(key));
				List<T> keys = DefaultCodec.decode(Lists.newArrayList(mapData.keySet()));
				Map<T,K> result = Maps.toMap(keys, k->DefaultCodec.decode(mapData.get(DefaultCodec.encode(k))));
				// FIXME 这里的Map不是线程安全的
				return Maps.newHashMap(result);
			}catch(Exception e){
				LoggerManager.error("Redis Error->key[{}],stack[{}]",key,e);
			}
			return null;
		}
		
		public <T extends Serializable,K extends Serializable> void putMapAllValue(String key, Map<T,K> map){
			try(Jedis jedis = getJedis()){
				List<byte[]> keyList = map.keySet().stream().map(k-> DefaultCodec.encode(k)).collect(Collectors.toList());
				Map<byte[],byte[]> mapData = Maps.toMap(keyList, k->DefaultCodec.encode(map.get(DefaultCodec.decode(k))));
				jedis.hmset(DefaultCodec.encode(key), mapData);
			}catch(Exception e){
				LoggerManager.error("Redis Error->key[{}],stack[{}]",key,e);
			}
		}
		
		
		public <T extends Serializable>long getSetIndex(String key,T value){
			try(Jedis jedis = getJedis()){
				byte[] keys = DefaultCodec.encode(key);
				byte[] values = DefaultCodec.encode(value);
				Long rank = jedis.zrevrank(keys, values);
				return (int)(rank==null?-1:rank);
			}catch(Exception e){
				LoggerManager.error("Redis Error->key[{}],stack[{}]",key,e);
			}
			return -1;
		}
		
		
		public <T extends Serializable> long getSetScore(String key,T value){
			try(Jedis jedis = getJedis()){
				byte[] keys = DefaultCodec.encode(key);
				byte[] values = DefaultCodec.encode(value);
				Double rank = jedis.zscore(keys, values);
				return (int)(rank==null?-1:rank);
			}catch(Exception e){
				LoggerManager.error("Redis Error->key[{}],stack[{}]",key,e);
			}
			return -1;
		}
		
		public <T extends Serializable> void zadd(String key,double score,T value){
			
			try(Jedis jedis = getJedis()){
				byte[] keys = DefaultCodec.encode(key);
				byte[] values = DefaultCodec.encode(value);
				jedis.zadd(keys, score, values);
			}catch(Exception e){
				LoggerManager.error("Redis Error->key[{}],stack[{}]",key,e);
			}
		}
		

		public void delRedisCache(String key){
			try (Jedis j = getJedis()){
				Set<String> keys = j.keys("*"+key+"*");
				keys.forEach(k->j.del(DefaultCodec.encode(k)));
				LoggerManager.error("总共清理了{}个{}相关的缓存数据",keys.size(),key);
			}
		}
		
		
		
		public class RedisConfig
		{
		    //可用连接实例的最大数目，默认值为8；
		    //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
		    public static final int MAX_ACTIVE = 1024;
		 
		    //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
		    public static final int MAX_IDLE = 200;
		 
		    //等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
		    public static final int MAX_WAIT = 10000;
		 
		    public static final int TIMEOUT = 10000;
		 
		    public static final int RETRY_NUM = 5;
		}
		
}