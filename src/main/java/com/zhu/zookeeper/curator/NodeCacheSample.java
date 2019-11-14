package com.zhu.zookeeper.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

//监听数据节点变化
public class NodeCacheSample {

    static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("192.168.0.138:2181")
            .sessionTimeoutMs(5000)
            .connectionTimeoutMs(3000)
            .retryPolicy(new ExponentialBackoffRetry(1000,3))
            .build();
    static String path = "/zk-curator/nodecache";
    public static void main(String[] args) throws Exception {
        client.start();

        NodeCache nodeCache = new NodeCache(client,path,false);
        //是否在第一次启动就读取节点最新值，缓存起来
        nodeCache.start(false);
        nodeCache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {

                if( nodeCache.getCurrentData() != null){

                    System.out.println(nodeCache.getPath()+" changed: " + new String(nodeCache.getCurrentData().getData()));
                }else{
                    System.out.println(nodeCache.getPath()+" deleted");
                }

            }
        });

        //NodeCacheListener可以监听节点被创建删除、节点数据变化
        client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .forPath(path,"init".getBytes());

        Thread.sleep(1000);
        client.setData()
                .forPath(path,"newData".getBytes());

        Thread.sleep(1000);
        client.delete()
                .guaranteed()
                .deletingChildrenIfNeeded()
                .forPath(path);

        Thread.sleep(3000);

    }
}
