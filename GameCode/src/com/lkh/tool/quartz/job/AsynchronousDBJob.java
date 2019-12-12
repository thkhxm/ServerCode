package com.lkh.tool.quartz.job;

import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.lkh.db.conn.dao.DBManager;
import com.lkh.manager.OfflineOperation;
import com.lkh.server.GameSource;
import com.lkh.server.instance.InstanceFactory;
import com.lkh.tool.log.LoggerManager;

/**
 * @author tim.huang
 * 2016年2月25日
 * 系统异步更新数据定时器
 */
public class AsynchronousDBJob implements Job {

	private static int count = 0;
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		DBManager.run(false);
		if(++count%60==0){
			List<Long> removeTeamList = GameSource.removeAllOffLine();
			List<OfflineOperation> objList = InstanceFactory.get().getOfflineList();
			if(removeTeamList == null) {
				LoggerManager.error("移除离线玩家信息异常------------->");
				return;
			}
			removeTeamList.stream()
			.filter(teamId->teamId!=null)
			.forEach(teamId->{
				objList.stream().forEach(obj->obj.offLine(teamId));
				LoggerManager.debug("移除玩家信息->[{}]",teamId);
			});
		}
	}

}
