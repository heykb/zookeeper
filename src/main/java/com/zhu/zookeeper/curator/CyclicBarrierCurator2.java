package com.zhu.zookeeper.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.barriers.DistributedBarrier;
import org.apache.curator.framework.recipes.barriers.DistributedDoubleBarrier;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

//distributedDoubleBarrier,几个人互相等待一起做，然后做完再互相等待， 一起离开
public class CyclicBarrierCurator2 {

    public static String path = "/distributed_barrier";
    public static Random random = new Random();
    public static void main(String[] args) throws Exception {
        ExecutorService threadPool =
                new ThreadPoolExecutor(3,3,0L, TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(10));
        threadPool.submit(new Runner("1号选手"));
        threadPool.submit(new Runner("2号选手"));
        threadPool.submit(new Runner("3号选手"));

    }
    static class Runner implements Runnable{
        private String name;
        public Runner(String name){
            this.name=name;
        }
        @Override
        public void run() {
            try {
                CuratorFramework client = CuratorFrameworkFactory.builder()
                        .connectString("192.168.0.138:2181")
                        .retryPolicy(new ExponentialBackoffRetry(1000,5))
                        /* //有密码的人才能加入此次赛跑
                         .authorization("digest","foo:true".getBytes())*/
                        .build();
                client.start();

                DistributedDoubleBarrier distributedDoubleBarrier = new DistributedDoubleBarrier(client,path,3);

                System.out.println(name+" 准备....");
                //进入栅栏。互相等待
                distributedDoubleBarrier.enter();

                System.out.println(name+" 起跑");
                Thread.sleep(1000*random.nextInt(3));
                System.out.println(name+" 到终点");


                //准备离开栅栏，互相等待
                distributedDoubleBarrier.leave();
                System.out.println(name+" 吃饭");

            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }
}
