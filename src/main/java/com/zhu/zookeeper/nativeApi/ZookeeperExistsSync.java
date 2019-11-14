package com.zhu.zookeeper.nativeApi;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZookeeperExistsSync implements Watcher {

    private static CountDownLatch connectedSemapthore = new CountDownLatch(1);
    private static ZooKeeper zooKeeper;
    private static Stat stat = new Stat();
    public static void main(String[] args) {
        try {
            zooKeeper =
                    new ZooKeeper("192.168.0.138:2181",5000,new ZookeeperExistsSync());

            System.out.println(zooKeeper.getState());
            connectedSemapthore.await();
            System.out.println("zookeeper session created");


            stat = zooKeeper.exists("/zk-test",true);

            String path = zooKeeper.create("/zk-test","value".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);

            stat = zooKeeper.setData(path,"value".getBytes(),-1);

            zooKeeper.delete(path,stat.getVersion());

            Thread.sleep(Integer.MAX_VALUE);

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
        try {
            System.out.println("接受到watched event:" + watchedEvent);
            if (Event.KeeperState.SyncConnected == watchedEvent.getState()) {
                if (watchedEvent.getType() == Event.EventType.None && watchedEvent.getPath() == null) {
                    //解除主程序阻塞
                    connectedSemapthore.countDown();
                } else if (watchedEvent.getType() == Event.EventType.NodeCreated) {
                    System.out.println("node created: " + watchedEvent.getPath());
                    zooKeeper.exists(watchedEvent.getPath(), true);
                } else if (watchedEvent.getType() == Event.EventType.NodeDataChanged) {
                    System.out.println("node changed: "+watchedEvent.getPath());
                    zooKeeper.exists(watchedEvent.getPath(), true);
                }else if (watchedEvent.getType() == Event.EventType.NodeDeleted) {
                    System.out.println("node deleted: "+watchedEvent.getPath());
                    zooKeeper.exists(watchedEvent.getPath(), true);
                }

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }
}
