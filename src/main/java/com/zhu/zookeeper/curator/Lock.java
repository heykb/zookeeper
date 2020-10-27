package com.zhu.zookeeper.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMultiLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class Lock {

    static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("192.168.112.144:2181")
            .sessionTimeoutMs(5000)
            .connectionTimeoutMs(3000)
            .retryPolicy(new ExponentialBackoffRetry(1000,3))
            .build();
    static String path = "/lock_path";
    public static void main(String[] args) throws Exception {
        client.start();

        final InterProcessMutex lock = new InterProcessMutex(client,path);
        final CyclicBarrier cyclicBarrier = new CyclicBarrier(10);
        for(int i=0;i<10;i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //等待其他线程创建
                        cyclicBarrier.await();
                        //获取锁
                        lock.acquire();

                        String orderNo = new SimpleDateFormat("HH:mm:ss|SSS").format(new Date());
                        System.out.println("生成的订单号是"+orderNo);

                        //释放锁
                        lock.release();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        }


    }
}
