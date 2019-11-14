package com.zhu.zookeeper.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.retry.ExponentialBackoffRetry;

//master选举实际上是多个客户端同时为一个节点去创建子节点，成功的作为master,监听子节点的创建删除
public class MasterNodeListener {

    static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("192.168.0.138:2181")
            .sessionTimeoutMs(5000)
            .connectionTimeoutMs(3000)
            .retryPolicy(new ExponentialBackoffRetry(1000,3))

            .build();
    static String path = "/master-path";
    public static void main(String[] args) throws Exception {
        client.start();

        PathChildrenCache childrenCache = new PathChildrenCache(client,path,false);
        childrenCache.start(PathChildrenCache.StartMode.NORMAL);
        childrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                switch (event.getType()){
                    case CHILD_ADDED:
                        System.out.println(event.getData().getPath()+" created");
                        break;
                    case CHILD_REMOVED:
                        System.out.println(event.getData().getPath()+" removed");
                        break;
                    default:
                        break;
                }
            }
        });
        Thread.sleep(Integer.MAX_VALUE);

    }
}
