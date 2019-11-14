package com.zhu.zookeeper.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class CreatSessionSampleFluent {
    public static void main(String[] args) throws InterruptedException {
        //创建重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("192.168.0.138:2181")
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(3000)
                .retryPolicy(retryPolicy)
                //创建此session的根节点
                .namespace("base")
                .build();
        client.start();
        Thread.sleep(3000);

    }
}
