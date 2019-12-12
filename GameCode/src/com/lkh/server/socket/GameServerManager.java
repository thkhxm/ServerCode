package com.lkh.server.socket;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lkh.db.conn.dao.DBManager;
import com.lkh.manager.CloseOperation;
import com.lkh.server.ServerStat;
import com.lkh.server.instance.InstanceFactory;
import com.lkh.server.proto.Request;
import com.lkh.server.proto.Response;
import com.lkh.server.proto.ResponseSession;
import com.lkh.server.rpc.RPCSource;
import com.lkh.server.syn.SynInvokeThread;
import com.lkh.tool.log.LoggerManager;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

public class GameServerManager {
    private static final Logger log = LoggerFactory.getLogger(GameServerManager.class);
    static final int MAX_THREAD = 100;

    private static ExecutorService[] synPool = new ExecutorService[MAX_THREAD];//服务器逻辑处理数组

    private static ExecutorService rpcPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);//服务器逻辑处理数组
    private static BlockingQueue<Request> moMap = new LinkedBlockingQueue<Request>(5000 * 50);//客户端请求队列
    private static BlockingQueue<ResponseSession> mtMap = new LinkedBlockingQueue<ResponseSession>(5000 * 50);//服务器响应队列

    public GameServerManager() {
    }

    public static void start() {
        for (int i = 0; i < MAX_THREAD; i++) {
            synPool[i] = Executors.newFixedThreadPool(1);
        }
        new MoSynThread("MOThread").start();

        new MtThread("MTThread").start();

        InstanceFactory.get().addCloseOperationList(() -> DBManager.run(true));

        //钩子
        Runtime.getRuntime().addShutdownHook(new ServerCloseThread());
    }

    /**
     * 获得等待运行的请求数量
     *
     * @return
     */
    public static int getWaitRunMoSize() {
        int num = 0;
        for (ExecutorService es : synPool) {
            ThreadPoolExecutor tp = (ThreadPoolExecutor) es;
            num += tp.getQueue().size();
        }
        //		ThreadPoolExecutor tp = (ThreadPoolExecutor)synPool;
        //		num+=tp.getQueue().size();
        return num;
    }

    /**
     * 获得等待运行的响应数量
     *
     * @return
     */
    public static int getWaitRunMtSize() {
        return mtMap.size();
    }

    public static void putMo(Request req) {
        try {
            moMap.put(req);
        } catch (InterruptedException e) {
            LoggerManager.error("GameServerManager put error:", e);
        }
    }

    public static void putMt(Response res, IoSession session) {
        try {
            ServerStat.incMt(res.getServiceCode(), res.getBytesSize());
            mtMap.put(new ResponseSession(session, res));
        } catch (InterruptedException e) {
            LoggerManager.error("GameServerManager put error:", e);
        }
    }

    // 异步处理
    private static class MoSynThread extends Thread {

        public MoSynThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            AtomicLong sd = new AtomicLong();
            while (true) {
                try {
                    Request req = moMap.take();
                    // 开启线程处理客户端的业务逻辑请求
                    //					synPool.submit(new SynInvokeThread(req));
                    if (req instanceof RPCSource) {
                        rpcPool.submit(new SynInvokeThread(req));
                    } else {
                        synPool[(int) (req.getTeamId() % MAX_THREAD)]
                                .submit(new SynInvokeThread(req));
                        //						synPool[(int) (sd.incrementAndGet() % MAX_THREAD)]
                        //								.submit(new SynInvokeThread(req));
                    }
                } catch (Exception e) {
                    LoggerManager.error("MoSynThread:", e);
                }
            }
        }
    }

    // 下行线程
    private static class MtThread extends Thread {

        public MtThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    ResponseSession res = mtMap.take();
                    if (res.getSession() != null
                            && res.getSession().isConnected()) {
                        res.getSession().write(res.getResponse());
                    }
                } catch (Exception e) {
                    LoggerManager.error("MtThread:", e);
                }
            }
        }
    }

    private static class ServerCloseThread extends Thread {

        @Override
        public void run() {
            List<CloseOperation> closeList = InstanceFactory.get().getCloseOperationList();
            for (CloseOperation obj : closeList) {
                try {
                    log.info("Close Operation Class ->[{}]", obj.getClass().getSimpleName());
                    obj.close();
                } catch (Exception e) {
                    log.error("Server Close Error:" + e.getMessage(), e);
                }
            }
        }

    }

}
