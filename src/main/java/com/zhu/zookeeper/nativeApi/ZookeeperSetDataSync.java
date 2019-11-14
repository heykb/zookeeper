package com.zhu.zookeeper.nativeApi;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

//同步修改数据节点
public class ZookeeperSetDataSync implements Watcher {

    private static CountDownLatch connectedSemapthore = new CountDownLatch(1);
    private static ZooKeeper zooKeeper;
    private static Stat stat = new Stat();
    public static void main(String[] args) {
        try {
            zooKeeper =
                    new ZooKeeper("192.168.0.138:2181",5000,new ZookeeperSetDataSync());

            System.out.println(zooKeeper.getState());
            connectedSemapthore.await();
            System.out.println("zookeeper session created");

            String path = zooKeeper.create("/zk-test","values".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);

            //-1表示更新最新的版本
            Stat stat = zooKeeper.setData(path,"value2".getBytes(),-1);

            System.out.println(stat.getCzxid()+","+stat.getMzxid() +","+stat.getVersion());

            stat = zooKeeper.setData(path,"value2".getBytes(),stat.getVersion());

            System.out.println(stat.getCzxid()+","+stat.getMzxid() +","+stat.getVersion());

            //更新一个旧版本的数据，报错 KeeperErrorCode = BadVersion for /zk-test
            stat = zooKeeper.setData(path,"value2".getBytes(),stat.getVersion()-1);
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
