package com.zhu.zookeeper.curator.server;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;

import java.io.File;

public class TestingServerSample {
    public static void main(String[] args) throws Exception {
        TestingServer server = new TestingServer(2181);
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("localhost:2181")
                .retryPolicy(new ExponentialBackoffRetry(1000,3))
                .build();
        client.start();
        System.out.println(client.getData().forPath("/"));
        Thread.sleep(5000);
        server.close();
    }
}
