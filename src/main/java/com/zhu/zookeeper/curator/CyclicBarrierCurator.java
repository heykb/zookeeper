package com.zhu.zookeeper.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.barriers.DistributedBarrier;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.Random;
import java.util.concurrent.*;

//DistributedBarrier分布式Barrier,不同于CyclicBarrier : 几个人做启动项目，没有互相等待，一个人吹口哨直接启动当前准备好的，那些还没准备好的，只有等下次吹口哨
public class CyclicBarrierCurator {

    public static String path = "/distributed_barrier";
    public static DistributedBarrier distributedBarrier;
    public static Random random = new Random();
    public static void main(String[] args) throws Exception {
        ExecutorService threadPool =
                new ThreadPoolExecutor(3,3,0L, TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(10));
        threadPool.submit(new Runner("1号选手"));
        threadPool.submit(new Runner("2号选手"));
        threadPool.submit(new Runner("3号选手"));
        while(true){
            Thread.sleep(2000);
            if(distributedBarrier != null){
                System.out.println(111);
                //直接释放当前所有阻塞的任务
                distributedBarrier.removeBarrier();
                break;
            }
        }
        //新加入的阻塞，需要再次释放才行
        threadPool.submit(new Runner("4号选手"));
        Thread.sleep(5000);
        distributedBarrier.removeBarrier();

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
                        .connectString("192.168.112.144:2181")
                        .retryPolicy(new ExponentialBackoffRetry(1000,5))
                        /* //有密码的人才能加入此次赛跑
                         .authorization("digest","foo:true".getBytes())*/
                        .build();
                client.start();

                distributedBarrier = new DistributedBarrier(client,path);
                distributedBarrier.setBarrier();
                System.out.println(name+" 准备....");
                distributedBarrier.waitOnBarrier();

                System.out.println(name+" 起跑");
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }
}
