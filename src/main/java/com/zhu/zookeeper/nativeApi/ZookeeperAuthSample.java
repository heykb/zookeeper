package com.zhu.zookeeper.nativeApi;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZookeeperAuthSample implements Watcher{

    private static CountDownLatch connectedSemapthore = new CountDownLatch(1);
    public static void main(String[] args) {
        try {
            ZooKeeper zooKeeper =
                    new ZooKeeper("192.168.0.138:2181",5000,new ZookeeperAuthSample());
            System.out.println(zooKeeper.getState());
            connectedSemapthore.await();
            System.out.println("zookeeper session created");
            zooKeeper.addAuthInfo("digest","foo:true".getBytes());

            String path = zooKeeper.create("/zk-auth-test","value".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.EPHEMERAL);

            byte[] data = zooKeeper.getData(path,false,null);
            System.out.println(new String(data));

            //使用未添加权限信息或错误权限信息zookeeper 访问有权限的节点
            ZooKeeper zooKeeper2 = new ZooKeeper("192.168.0.138:2181",5000,new ZookeeperAuthSample());
            connectedSemapthore = new CountDownLatch(1);
            connectedSemapthore.await();
            System.out.println("zookeeper session2 created");

            //使用错误权限信息
            //zooKeeper.addAuthInfo("digest","foo:tru".getBytes());
            byte[] data2 = zooKeeper2.getData(path,false,null);
            System.out.println(new String(data2));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }

    }

    //会话建立成功事件回调
    @Override
    public void process(WatchedEvent watchedEvent) {
        System.out.println("接受到watched event:"+watchedEvent);
        if(Event.KeeperState.SyncConnected == watchedEvent.getState()){
            //解除主程序阻塞
            connectedSemapthore.countDown();
        }

    }
}
