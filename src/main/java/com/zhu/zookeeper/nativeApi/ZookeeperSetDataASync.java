package com.zhu.zookeeper.nativeApi;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

//异步修改数据节点
public class ZookeeperSetDataASync implements Watcher {

    private static CountDownLatch connectedSemapthore = new CountDownLatch(1);
    private static ZooKeeper zooKeeper;
    private static Stat stat = new Stat();
    public static void main(String[] args) {
        try {
            zooKeeper =
                    new ZooKeeper("192.168.112.144:2181",5000,new ZookeeperSetDataASync());

            System.out.println(zooKeeper.getState());
            connectedSemapthore.await();
            System.out.println("zookeeper session created");

            String path = zooKeeper.create("/zk-test","values".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);

            //-1表示更新最新的版本
             zooKeeper.setData(path,"value2".getBytes(),-1,new IStatCallback(),null);

             Thread.sleep(5000);
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

    static class IStatCallback implements AsyncCallback.StatCallback{

        @Override
        public void processResult(int rc, String path, Object ctx, Stat stat) {
            System.out.println("get children znode result: [response code :"+rc+", param path: "+path+
                    ",ctx: "+ctx+", stat: "+stat);
        }
    }
}
