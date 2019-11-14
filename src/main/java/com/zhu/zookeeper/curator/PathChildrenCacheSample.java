package com.zhu.zookeeper.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

//监听子节点变化
public class PathChildrenCacheSample {

    static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("192.168.0.138:2181")
            .sessionTimeoutMs(5000)
            .connectionTimeoutMs(3000)
            .retryPolicy(new ExponentialBackoffRetry(1000,3))
            .build();
    static String path = "/pathchildrencache";
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


        PathChildrenCache childrenCache = new PathChildrenCache(client,path,true);
        /*
        如果不填写这个参数，则无法监听到子节点的数据更新
        如果参数为PathChildrenCache.StartMode.BUILD_INITIAL_CACHE，则会预先创建之前指定的/super节点
        如果参数为PathChildrenCache.StartMode.POST_INITIALIZED_EVENT，效果与BUILD_INITIAL_CACHE相同，只是不会预先创建/super节点
        参数为PathChildrenCache.StartMode.NORMAL时，与不填写参数是同样的效果，不会监听子节点的数据更新操作*/
        childrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
        childrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                switch (event.getType()){
                    case CHILD_ADDED:
                        System.out.println("child added :" + event.getData().getPath());break;
                    case CHILD_UPDATED:
                        System.out.println("child updated :"+event.getData().getPath()+": "+new String(event.getData().getData()));break;
                    case CHILD_REMOVED:
                        System.out.println("child removed :"+event.getData().getPath());break;
                    default:
                        break;
                }
            }
        });
        client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(path+"/c1","init".getBytes());
        Thread.sleep(2000);

        client.setData().forPath(path+"/c1","newData".getBytes());
        Thread.sleep(2000);

        client.delete().forPath(path+"/c1");
        Thread.sleep(2000);


        client.delete().deletingChildrenIfNeeded().forPath(path);
        //client.delete().forPath(path);
        Thread.sleep(5000);
    }
}
