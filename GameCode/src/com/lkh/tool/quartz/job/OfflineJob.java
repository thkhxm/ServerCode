package com.lkh.tool.quartz.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
@Deprecated
public class OfflineJob implements Job {

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		
	}
//
//	private static Logger log = LoggerFactory.getLogger(OfflineJob.class);
//	@Override
//	public void execute(JobExecutionContext arg0) throws JobExecutionException {
//		List<Long> removeTeamList = GameSource.removeAllOffLine();
//		List<OfflineOperation> objList = InstanceFactory.get().getOfflineList();
//		removeTeamList.stream().forEach(teamId->{
//			objList.stream().forEach(obj->obj.offLine(teamId));
//			log.info("移除玩家信息->[{}]",teamId);
//		});
//	}

}
