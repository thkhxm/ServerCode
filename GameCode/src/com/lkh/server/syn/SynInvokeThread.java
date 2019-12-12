package com.lkh.server.syn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lkh.server.MessageManager;
import com.lkh.server.RPCMessageManager;
import com.lkh.server.ServerStat;
import com.lkh.server.instance.InstanceFactory;
import com.lkh.server.proto.Request;
import com.lkh.server.rpc.RPCSource;

/**
 * 处理逻辑线程
 *
 * @author tim.huang
 * 2015年12月9日
 */
public class SynInvokeThread implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(SynInvokeThread.class);
    final Request req;

    public SynInvokeThread(Request req) {
        this.req = req;
    }

    @Override
    public void run() {
        try {
            //执行逻辑前，初始化当前玩家数据源
            req.start();
            if (req instanceof RPCSource) {
                RPCMessageManager.initSource((RPCSource) req);
            } else {
                MessageManager.initTeam(req);
            }

            if (req.getTeamId() > 0 || InstanceFactory.get().isUnCheck(req.getMethodCode())) {//还没登录
                if (log.isDebugEnabled()) {
                    log.debug("req 玩家->[{}]调用方法->[{}]参数[{}]", MessageManager.getPlayerId(),
                            req.getServerMethod() == null ? req.getMethodCode() : req.getServerMethod().getName(), req.getAttrsSimpleString());
                }
                req.invoke();
            }
            //记录请求消耗时间
            ServerStat.make(req);
        } catch (Exception e) {
            log.error("SynInvokeThread error code :" + req.getMethodCode() + " " + e.getMessage(), e);
        } finally {

        }

    }

}
