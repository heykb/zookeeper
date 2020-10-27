package com.zhu.zookeeper.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

//master选举机制
public class MasterSelect {

    static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("192.168.112.144:2181")
            .sessionTimeoutMs(5000)
            .connectionTimeoutMs(3000)
            .retryPolicy(new ExponentialBackoffRetry(1000,3))

            .build();
    static String path = "/master-path";
    public static void main(String[] args) throws Exception {
        client.start();

        LeaderSelector selector = new LeaderSelector(client, path, new LeaderSelectorListenerAdapter() {
            @Override
            public void takeLeadership(CuratorFramework curatorFramework) throws Exception {
                System.out.println("成为master角色");
                Thread.sleep(5000);
                System.out.println("完成master操作，释放master权利");
            }
        });
        selector.autoRequeue();
        selector.start();




        Thread.sleep(Integer.MAX_VALUE);

    }
}
