package com.zhu.zookeeper.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import sun.nio.ch.ThreadPool;

import java.util.concurrent.*;

public class CreateNodeAsync {

    static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("192.168.0.138:2181")
            .sessionTimeoutMs(5000)
            .connectionTimeoutMs(3000)
            .retryPolicy(new ExponentialBackoffRetry(1000,3))
            //创建此session的根节点
            .namespace("base")
            .build();
    static String path = "/zk-curator/c1";

    //static ExecutorService threadPool = Executors.newFixedThreadPool(2);
    static ExecutorService threadPool =
            new ThreadPoolExecutor(2,3,0L, TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(10));
    static CountDownLatch semphore = new CountDownLatch(2);
    public static void main(String[] args) throws Exception {
        client.start();

        //异步创建节点，并且事件回调使用我们自己创建的工作线程处理
        client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .inBackground(new BackgroundCallback() {
                    @Override
                    public void processResult(CuratorFramework client, CuratorEvent curatorEvent) throws Exception {
                        System.out.println(String.format("event [code:%d, type:%s ]",curatorEvent.getResultCode(),curatorEvent.getType()));
                        System.out.println("Thread name: " + Thread.currentThread().getName());
                        semphore.countDown();
                    }
                },threadPool)
                .forPath(path,"init".getBytes());


        //异步创建节点，并且事件回调使用默认的EventThread
        Thread.sleep(1000);
        client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .inBackground(new BackgroundCallback() {
                    @Override
                    public void processResult(CuratorFramework client, CuratorEvent curatorEvent) throws Exception {
                        System.out.println(String.format("event [code:%d, type:%s ]",curatorEvent.getResultCode(),curatorEvent.getType()));
                        System.out.println("Thread name: " + Thread.currentThread().getName());
                        semphore.countDown();
                    }
                })
                .forPath(path,"init".getBytes());
        semphore.await();

    }
}
