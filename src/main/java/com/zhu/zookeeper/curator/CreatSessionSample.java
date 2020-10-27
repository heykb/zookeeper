package com.zhu.zookeeper.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class CreatSessionSample {
    public static void main(String[] args) throws InterruptedException {
        //创建重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);
        CuratorFramework client = CuratorFrameworkFactory.
                newClient("192.168.112.144:2181",5000,3000,retryPolicy);
        client.start();
        Thread.sleep(Integer.MAX_VALUE);

    }
}
