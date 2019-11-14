package com.zhu.zookeeper.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

public class SetDataFluent {

    static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("192.168.0.138:2181")
            .sessionTimeoutMs(5000)
            .connectionTimeoutMs(3000)
            .retryPolicy(new ExponentialBackoffRetry(1000,3))
            //创建此session的根节点
            .namespace("base")
            .build();
    static String path = "/zk-curator/c1";
    public static void main(String[] args) throws Exception {
        client.start();

        client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(path,"init".getBytes());

        Stat stat = new Stat();
        byte[] data = client.getData().storingStatIn(stat).forPath(path);
        System.out.println(path+":"+new String(data));

        client.setData().withVersion(stat.getVersion()).forPath(path,"updateData".getBytes());
        data = client.getData().storingStatIn(stat).forPath(path);

        System.out.println(path+":"+new String(data));
        Thread.sleep(3000);
    }
}
