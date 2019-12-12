package com.lkh.tool.quartz;

import com.lkh.util.LocalDateTimeUtil;
import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.lkh.tool.log.LoggerManager;
import com.lkh.tool.quartz.annotation.JobExpression;
import com.lkh.tool.quartz.job.AsynchronousDBJob;
import com.lkh.tool.quartz.job.RPCLinkedJob;
import com.lkh.tool.quartz.job.RouteNodeLiveJob;
import com.lkh.tool.quartz.job.SystemStatJob;

import java.time.LocalDateTime;


/**
 * 任务调度服务
 * @author tim.huang
 * 2015年12月14日
 */
public class QuartzServer {

	private SchedulerFactory sf = new StdSchedulerFactory();
	
	public static QuartzServer get(){
		return QuartzServerInstance.instance;
	}

	private static class QuartzServerInstance{
		private static QuartzServer instance = new QuartzServer();
		static {
			//10秒钟同步一次数据库
			JobDetail job = JobBuilder.newJob(AsynchronousDBJob.class)
					.withIdentity("AsynchronousDBJob", JobExpression.SYSTEM)
					.build();
			
			Trigger trigger = TriggerBuilder
			.newTrigger()
			.withIdentity("AsynchronousDBJob",JobExpression.SYSTEM)
			.withSchedule(CronScheduleBuilder.cronSchedule("0/10 * * * * ?"))
			.build();
			//10秒钟同步一次数据库
			JobDetail job3 = JobBuilder.newJob(SystemStatJob.class)
					.withIdentity("SystemStatJob", JobExpression.SYSTEM)
					.build();
			
			Trigger trigger3 = TriggerBuilder
					.newTrigger()
					.withIdentity("SystemStatJob",JobExpression.SYSTEM)
					.withSchedule(CronScheduleBuilder.cronSchedule("0/10 * * * * ?"))
					.build();
			
			//10秒钟同步一次数据库
			JobDetail job2 = JobBuilder.newJob(RPCLinkedJob.class)
					.withIdentity("RPCLinkedJob", JobExpression.SYSTEM)
					.build();
			
			Trigger trigger2 = TriggerBuilder
					.newTrigger()
					.withIdentity("RPCLinkedJob",JobExpression.SYSTEM)
					.withSchedule(CronScheduleBuilder.cronSchedule("0/30 * * * * ?"))
					.build();
			
			//10秒钟同步一次数据库
			JobDetail job4 = JobBuilder.newJob(RouteNodeLiveJob.class)
					.withIdentity("RouteNodeLiveJob", JobExpression.SYSTEM)
					.build();
			
			Trigger trigger4 = TriggerBuilder
					.newTrigger()
					.withIdentity("RouteNodeLiveJob",JobExpression.SYSTEM)
					.withSchedule(CronScheduleBuilder.cronSchedule("0/10 * * * * ?"))
					.build();
			
			//10分钟清理一次离线用户数据
//			JobDetail job2 = JobBuilder.newJob(OfflineJob.class)
//					.withIdentity("OfflineJob", JobExpression.SYSTEM)
//					.build();
//			
//			Trigger trigger2 = TriggerBuilder
//			.newTrigger()
//			.withIdentity("OfflineJob",JobExpression.SYSTEM)
//			.withSchedule(CronScheduleBuilder.cronSchedule("* 0/10/20/30/40/50 * * * ?"))
//			.build();
			
			try {
				instance.addJob(job, trigger);
				instance.addJob(job2, trigger2);
				instance.addJob(job3, trigger3);
				instance.addJob(job4, trigger4);
			} catch (SchedulerException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	public void addJob(JobDetail job,Trigger trigger) throws SchedulerException{
		LoggerManager.debug("QuartzServer addJob[{}]",job.getKey().getName());
		Scheduler sd = sf.getScheduler();
		sd.scheduleJob(job, trigger);
	}
	
	public void addJob(LocalDateTime runTime, Class<? extends Job> cla, String name){
		JobDetail jd = JobBuilder.newJob(cla)
				.withIdentity(name, JobExpression.GAME)
				.build();
		Trigger trigger = TriggerBuilder
				.newTrigger()
				.withIdentity(name,JobExpression.GAME)
				.startAt(LocalDateTimeUtil.conver2Date(runTime))
				.build();
		LoggerManager.debug("QuartzServer addJob[{}]--->required",jd.getKey().getName());
		Scheduler sd;
		try {
			sd = sf.getScheduler();
			sd.scheduleJob(jd, trigger);
		} catch (SchedulerException e) {
			e.printStackTrace();
			LoggerManager.error("QuartzServer addJob error--->[{}]",name);
		}
	}
	
	public void start() throws SchedulerException{
		LoggerManager.debug("QuartzServer is ready..");
		sf.getScheduler().start();
		LoggerManager.debug("QuartzServer is start..");
	}

}
