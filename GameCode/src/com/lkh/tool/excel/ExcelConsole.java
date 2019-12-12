package com.lkh.tool.excel;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.lkh.manager.IConsoleInit;
import com.lkh.server.GameSource;
import com.lkh.tool.excel.ExcelConfigBean.RowData;
import com.lkh.tool.excel.annon.Config;
import com.lkh.tool.zookeep.ZookeepServer;

/**
 * @author tim.huang
 * 2016年1月13日
 * 缓存配置管理
 */ 
public class ExcelConsole{
	private Logger log = LoggerFactory.getLogger(ExcelConsole.class); 
	
	//
	static class ManagerInstace{
		private static ExcelConsole manager;
		static {
			manager = new ExcelConsole();
		}
		static private ExcelConsole get() {
			return manager;
		}
	}
	static public final ExcelConsole get() {
		return ManagerInstace.get();
	}
	private ExcelConsole() {
	}
	//
	
	private List<ExcelModule> excelList;
	private List<IConsoleInit> consoleList;
	
	public void init(List<ExcelModule> excelList,List<IConsoleInit> consoleList) {
		this.excelList = excelList;
		this.consoleList = consoleList;
	}
	
	
	//
	public void reload() {
		
		
		Watcher w = new Watcher() {
			@Override
			public void process(WatchedEvent event) {
				reloadConfig(true);
				// reloadConfig();
				ZookeepServer.getInstance().exists(
						ZookeepServer.getConfigPath(), this);
			}
		};
		if(ZookeepServer.getInstance() != null){
			ZookeepServer.getInstance().exists(ZookeepServer.getConfigPath(), w);
		}else{
			log.error("zookeep 未链接");
		}
		// 监听配置文件父节点
		reloadConfig(false);
	}

	private void reloadConfig(boolean zkServer) {
		init(zkServer);
		resetCache();
	}

	/**
	 * 调试excel配置文件路径
	 */
	public String debugExcelPath = ExcelConsole.class.getResource("/").getPath()
			+ "../excel";

	/**
	 * excel文件名称
	 * 
	 * @param key
	 * @param isZKServer
	 *            当false时读取本地，方便测试
	 * @return
	 */
	public byte[] getExcelByte(boolean isZKServer, String key) {
		// 从zk服务器取
		if (isZKServer) {
			return ZookeepServer.getInstance().getBytes(
					ZookeepServer.getConfigPath() + "/" + key);
		}
		// 本地配置
		String fileName = debugExcelPath + File.separatorChar + key + ".xlsx";
		log.debug("读取配置#配置地址:{}",fileName);
		try {
			return toByteArray(fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void init(boolean isZKServer){
		return;
////		Field[] fileds = manager.getClass().getDeclaredFields();
//		List<String> allFileName = this.excelList.stream()
//		.map(f->f.getConfig())
//		.filter(config->config!=null)
//		.map(config->config.fileName())
//		.distinct()
//		.collect(Collectors.toList());
//
//		Map<String, byte[]> configSource = allFileName
//				.stream()
//				.collect(
//						Collectors.toMap((key) -> key,
//								(key) -> getExcelByte(isZKServer, key)));
//		//将数据加载到内存中，使用默认格式
//		ConfigCacheConsole.get().init(configSource);
//		//
//		for (ExcelModule field : excelList) {
//			Config config =  field.getConfig();
//			if(config == null || !checkServerName(config)) continue;
//			List<Object> result = Lists.newArrayList();
//
//			ExcelConfigBean configBean = ConfigCacheConsole.get().getConfigBean(config.fileName());
//			List<RowData> rowList = configBean.getRowList(config.sheetName());
//			if(rowList == null){
//				log.error("Excel File is empty#{}-{}",config.fileName(),config.sheetName());
//				continue;
//			}
//			for(RowData row : rowList) {
//				try {
//					Object obj = field.getCla().newInstance();
//
//					Class<?> clas = field.getCla();
//					do {
//						Field[] objField = clas.getDeclaredFields();
//						for(Field tmp : objField) {
//							Object val = row.getValue(tmp.getName());
//							if(val == null) continue;
//							tmp.setAccessible(true);
//							tmp.set(obj, val);
//
//							if(GameSource.isDebug)
//								log.debug(tmp.getName()+"="+val);
//						}
//						try {
//							Method method = clas.getMethod("convert", RowData.class);
//							if(method!=null) {
//								method.invoke(obj, row);
//							}
//						} catch (Exception e) {
//						}
//						clas = clas.getSuperclass();
//					}while(clas!=Object.class);
//
//					result.add(obj);
//					if(GameSource.isDebug)
//						log.error("{}",obj);
//				} catch (Exception e) {
//					log.error("初始化配置#配置:{}#{}",config.fileName(),e);
//					continue;
//				}
//			}
//			ConfigCacheConsole.get().addConfigTmpData(field.getCla(), result);
//		}
	}
	
	private boolean checkServerName(Config config) {
		for (String name :config.initNode()) {
			if(GameSource.pool == name
					|| Strings.isNullOrEmpty(name)) return true;
		}
		return false;
	}

	public void clear() {
		// 加完完后要释放掉；

	}

	/**
	 * 文件转byte数组
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public byte[] toByteArray(String filename) throws IOException {
		log.debug("读本地excel[{}]", filename);
		File f = new File(filename);
		if (!f.exists()) {
			throw new FileNotFoundException(filename);
		}

		ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length());
		BufferedInputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(f));
			int buf_size = 1024;
			byte[] buffer = new byte[buf_size];
			int len = 0;
			while (-1 != (len = in.read(buffer, 0, buf_size))) {
				bos.write(buffer, 0, len);
			}
			return bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			bos.close();
		}
	}
	
	
	public void resetCache(){
		initCache();
	}
	
	public void initCache(){
		consoleList.stream().forEach(console->console.init());
	}
}
