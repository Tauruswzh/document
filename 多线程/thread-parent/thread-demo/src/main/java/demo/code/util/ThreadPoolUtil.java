package demo.code.util;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
* 文件名: ThreadPoolUtil.java
* 作者: xiahao
* 时间: 2021/2/1 下午5:33
* 描述: 线程池工具类
*/
public class ThreadPoolUtil {
    //双重检锁
    private volatile static ThreadPool instance = null;

    // 获取单例的线程池对象
    public static ThreadPool getInstance() {
        if (instance == null) {
            synchronized (ThreadPoolUtil.class) {
                if (instance == null) {
                    int cpuNum = Runtime.getRuntime().availableProcessors();// 获取处理器数量
                    instance = new ThreadPool(cpuNum + 1, cpuNum * 2, 60,50);
                }
            }
        }
        return instance;
    }


    public static class ThreadPool {
        private ThreadPoolExecutor mExecutor;
        private final int corePoolSize;
        private final int maximumPoolSize;
        private final long keepAliveTime;
        private final int wattingCount;

        private ThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, int wattingCount) {
            this.corePoolSize = corePoolSize;
            this.maximumPoolSize = maximumPoolSize;
            this.keepAliveTime = keepAliveTime;
            this.wattingCount = wattingCount;
        }

        public void execute(Runnable runnable) {
            if (runnable == null) {
                return;
            }
            if (mExecutor == null) {
                mExecutor = new ThreadPoolExecutor(
                        corePoolSize,               // 核心线程数
                        maximumPoolSize,            // 最大线程数
                        keepAliveTime,              // 闲置线程存活时间
                        TimeUnit.SECONDS,           // 时间单位
                        new LinkedBlockingDeque<Runnable>(wattingCount), // 线程队列
                        Executors.defaultThreadFactory(),               // 线程工厂
                        new ThreadPoolExecutor.AbortPolicy() {          // 队列已满,而且当前线程数已经超过最大线程数时的异常处理策略
                            @Override
                            public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {//丢弃任务并抛出RejectedExecutionException
                                super.rejectedExecution(r, e);
                            }
                        }
                );
            }
            mExecutor.execute(runnable);
        }

        // 从线程队列中移除对象
        public void cancel(Runnable runnable) {
            if (mExecutor != null) {
                mExecutor.getQueue().remove(runnable);
            }
        }
    }
}
