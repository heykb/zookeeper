package com.zhu.zookeeper.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicInteger;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;
;

/**
 * 分布式计数器
 * @author heykb
 */
public class DistAtomicInt {

    static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("192.168.112.144:2181")
            .sessionTimeoutMs(5000)
            .connectionTimeoutMs(3000)
            .retryPolicy(new ExponentialBackoffRetry(1000,3))
            .build();
    static String path = "/distatomicint";
    public static void main(String[] args) throws Exception {
        client.start();

        DistributedAtomicInteger atomicInteger = new DistributedAtomicInteger(client,path,new RetryNTimes(3,1000));
        AtomicValue<Integer> rc = atomicInteger.add(8);
        System.out.println("Result : "+rc.succeeded());
        System.out.println("new : "+rc.postValue());

        Thread.sleep(3000);

    }
}
