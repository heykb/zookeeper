package com.zhu.zookeeper.nativeApi;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZookeeperExistsASync implements Watcher {

    private static CountDownLatch connectedSemapthore = new CountDownLatch(1);
    private static ZooKeeper zooKeeper;
    private static Stat stat = new Stat();
    public static void main(String[] args) {
        try {
            zooKeeper =
                    new ZooKeeper("192.168.112.144:2181",5000,new ZookeeperExistsASync());

            System.out.println(zooKeeper.getState());
            connectedSemapthore.await();
            System.out.println("zookeeper session created");


            zooKeeper.exists("/zk-test",true,new IStatCallback(),null);

            String path = zooKeeper.create("/zk-test","value".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);

            stat = zooKeeper.setData(path,"value".getBytes(),-1);

            //子节点变化,不会发起通知
            String childrenPath = zooKeeper.create(path+"/c1","value".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);

            zooKeeper.delete(childrenPath,-1);

            zooKeeper.delete(path,-1);

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

    static class IStatCallback implements AsyncCallback.StatCallback{

        @Override
        public void processResult(int rc, String path, Object ctx, Stat stat) {
            System.out.println("exists znode result: [response code :"+rc+", param path: "+path+
                    ",ctx: "+ctx+", stat: "+stat);
        }
    }
}
