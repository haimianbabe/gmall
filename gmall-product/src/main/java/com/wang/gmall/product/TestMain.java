package com.wang.gmall.product;

import java.util.concurrent.*;

/**
 * 测试异步相关
 */
public class TestMain {

    public static ExecutorService executor = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        /**
         * int corePoolSize,
         * int maximumPoolSize,
         * long keepAliveTime,
         * TimeUnit unit,
         * BlockingQueue<Runnable> workQueue,
         * ThreadFactory threadFactory,
         * RejectedExecutionHandler handler
         */
//        ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 20, 10, TimeUnit.SECONDS,
//                new LinkedBlockingDeque<>(),
//                Executors.defaultThreadFactory(),
//                new ThreadPoolExecutor.AbortPolicy());
//        executor.execute(()->{
//            System.out.println("new executor");
//        });
//        executor.shutdown();

        //提交声明异步执行
        CompletableFuture<String> completableFuture1 = CompletableFuture.supplyAsync(()->"thread01......");
        CompletableFuture<String> completableFuture2 = CompletableFuture.supplyAsync(()->"thread02......");
        //获取上一步的值，有返回值
        CompletableFuture<Void> completableFuture3 = completableFuture2.thenAcceptBoth(completableFuture1,(t,u)->{
            System.out.println("获取到线程："+t+"");
        });

    }

    /**
     * 实现线程四种方式
     */
    public static class Thread01 extends Thread{
        @Override
        public void run() {
            System.out.println("new thread01");
        }
    }

    public static class Runnable01 implements Runnable{
        @Override
        public void run() {
            System.out.println("new runnable01");
        }
    }

    public static class Callable01 implements Callable<String>{
        @Override
        public String call() throws Exception {
            System.out.println("new callable01");
            return "new callable01";
        }
    }

}
