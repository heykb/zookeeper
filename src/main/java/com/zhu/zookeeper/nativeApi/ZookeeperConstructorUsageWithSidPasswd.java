package com.zhu.zookeeper.nativeApi;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZookeeperConstructorUsageWithSidPasswd implements Watcher {

    private static CountDownLatch connectedSemapthore = new CountDownLatch(1);
    public static void main(String[] args) {
        try {
            ZooKeeper zooKeeper =
                    new ZooKeeper("192.168.112.144:2181",5000,new ZookeeperConstructorUsageWithSidPasswd());

            System.out.println(zooKeeper.getState());
            connectedSemapthore.await();
            System.out.println("zookeeper session created");
            Long sessionId = zooKeeper.getSessionId();
            byte[] passwd = zooKeeper.getSessionPasswd();

            //指定sessionId和session passwd复用会话
            zooKeeper = new ZooKeeper("192.168.112.144:2181",5000,
                    new ZookeeperConstructorUsageWithSidPasswd(),
                    1L,"test".getBytes());
            //指定sessionId和session passwd复用会话
            zooKeeper = new ZooKeeper("192.168.112.144:2181",5000,
                    new ZookeeperConstructorUsageWithSidPasswd(),
                    sessionId,passwd);
            Thread.sleep(Integer.MAX_VALUE);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
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
