package com.lkh.server.instance;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.lkh.annotation.AutoLoad;
import com.lkh.annotation.db.DBAutoLoad;
import com.lkh.db.conn.DataCache;
import com.lkh.db.conn.ao.AOSynManager;
import com.lkh.manager.IConsoleInit;
import com.lkh.server.socket.GameServerManager;
import com.lkh.server.socket.SocketServerConfig;
import com.lkh.tool.excel.ExcelConsole;
import com.lkh.tool.excel.ExcelModule;
import com.lkh.tool.excel.annon.Config;
import com.lkh.tool.log.LoggerManager;
import com.lkh.tool.quartz.QuartzServer;
import com.lkh.tool.quartz.annotation.JobExpression;
import com.lkh.util.PathUtil;

/**
 * 服务器初始化
 * @author tim.huang
 * 2015年11月27日
 */
public class InitServer {
	private static Logger log = LoggerFactory.getLogger(InitServer.class);
	
	public static void init(SocketServerConfig config,boolean isRoute)throws Throwable{
		String osName = System.getProperties().getProperty("os.name");
		LoggerManager.debug("系统环境[{}]",osName);
		AOSynManager.start();
		//加载Service层所有对象
		initInstance(config.getPath(),config.getPackagePath(),config.getClassLoader());
		//
		if(!isRoute){
			InstanceFactory.get().executAfter();
			QuartzServer.get().start();
		}
		GameServerManager.start();
		
	}
	
	/**
	 * 初始化
	 * @param servicePath
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private static void initInstance(String path,String packagePath,ClassLoader cl) throws Exception {
		if("".equals(packagePath)) return;
		//
		LoggerManager.debug("初始化包路径:[{}],项目路径:[{}]",packagePath,path);
		List<String> cmdName = PathUtil.getAllName(packagePath);
		LoggerManager.debug("实例化路径:[{}]",cmdName);
		String replaceStr = getReplaceStr(path);
		List<ExcelModule> excelList = Lists.newArrayList();
		List<IConsoleInit> consoleList = Lists.newArrayList();
		for (String cmd : cmdName) {
			cmd = cmd.replace(replaceStr, "").replace("/", ".");
			Class<?> cla =  cl.loadClass(cmd);
			AutoLoad al = cla.getAnnotation(AutoLoad.class);
			if(al!=null) {//注入单例管理类
				InstanceFactory.get().put(cla.newInstance());
//				log.error("实例化单例对象----------------->"+cmd);
				continue;
			}
			//
			DBAutoLoad da = cla.getAnnotation(DBAutoLoad.class);
			if(da!=null) {//注入数据处理相关对象
				DataCache.get().addRefData(cla);
				log.error("添加数据库操作对象----------------->"+cmd);
				continue;
			}
			//
			JobExpression expression = cla.getAnnotation(JobExpression.class);
			if(expression!=null){//定时任务初始化
				JobDetail job = JobBuilder.newJob((Class<? extends Job>) cla)
												.withIdentity(expression.name(), expression.group())
												.build();
				Trigger trigger = TriggerBuilder
						.newTrigger()
						.withIdentity(expression.name(),expression.group())
						.withSchedule(CronScheduleBuilder.cronSchedule(expression.expression()))
						.build();
				QuartzServer.get().addJob(job, trigger);
				continue;
			}
			//
			Config config = cla.getAnnotation(Config.class);
			if(config!=null) {//配置序列化数据
				excelList.add(new ExcelModule(cla,config));
				continue;
			}
			//
			boolean has = Arrays.stream(cla.getInterfaces()).filter(inter->IConsoleInit.class.equals(inter)).findFirst().isPresent();
			if(has) {//
				Lookup lu = MethodHandles.lookup();
				MethodHandle mh = lu.findStatic(cla, "get", MethodType.methodType(cla));
				try {
					Object res = mh.invoke();
					consoleList.add((IConsoleInit)res);
				} catch (Throwable e) {
					log.error("序列化配置管理类异常#{}#{}",cla.getName(),e);
				}
				continue;
			}
		}
		//排序
		consoleList = consoleList.stream().sorted(Comparator.comparing(IConsoleInit::getOrder)).collect(Collectors.toList());
		//初始启动初始的数据配置Class,和Console管理类
		ExcelConsole.get().init(excelList, consoleList);
	}
	
	private static String getReplaceStr(String source){
		String osName = System.getProperties().getProperty("os.name");
		if(osName.toLowerCase().indexOf("windows") != -1){ //windows环境去掉开头的/
			source = source.substring(1, source.length());
		}
		return source;
	}
	
}
