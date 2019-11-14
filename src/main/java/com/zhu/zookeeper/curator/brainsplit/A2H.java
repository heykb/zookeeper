package com.zhu.zookeeper.curator.brainsplit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;

import java.util.concurrent.CountDownLatch;

//监听数据节点变化
public class A2H {

    static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("192.168.0.138:2181")
            .sessionTimeoutMs(5000)
            .connectionTimeoutMs(3000)
            .retryPolicy(new ExponentialBackoffRetry(1000,3))
            .authorization("digest","foo:true".getBytes())
            .build();
    static String path = "/ha-lock/lock";
    static CountDownLatch countDownLatch = new CountDownLatch(1);
    public static void main(String[] args) throws Exception {
        client.start();

        client.delete().inBackground(new BackgroundCallback() {
            @Override
            public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
                System.out.println(event.getPath() + "deleted");
            }
        }).forPath(path);
        Thread.sleep(1000);
        client.create()
                .withMode(CreateMode.EPHEMERAL)
                .withACL(ZooDefs.Ids.CREATOR_ALL_ACL)
                .inBackground(new BackgroundCallback() {
                    @Override
                    public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
                        System.out.println(event.getPath() + "acl created");
                    }
                })
                .forPath(path);
        Thread.sleep(Integer.MAX_VALUE);


    }
}
