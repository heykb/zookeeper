package com.zhu.zookeeper.nativeApi;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

//同步获取子节点列表
public class ZookeeperGetChildrenSync implements Watcher {

    private static CountDownLatch connectedSemapthore = new CountDownLatch(1);
    private static ZooKeeper zooKeeper;
    public static void main(String[] args) {
        try {
            zooKeeper =
                    new ZooKeeper("192.168.112.144:2181",5000,new ZookeeperGetChildrenSync());

            System.out.println(zooKeeper.getState());
            connectedSemapthore.await();
            System.out.println("zookeeper session created");


            Stat stat = new Stat();

            //true:使用创建会话时的默认watch
            List<String> childrenList = zooKeeper.getChildren("/",true,stat);
            for (String s:childrenList){
                System.out.println(s);
            }
            System.out.println("stat: "+stat);
            //创建新节点，发送通知测试
            zooKeeper.create("/zk-node","value".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);

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
            if(Event.EventType.None == watchedEvent.getType() && null == watchedEvent.getPath()){
                //解除主程序阻塞
                connectedSemapthore.countDown();
            }else if(Event.EventType.NodeChildrenChanged == watchedEvent.getType()){

                try {
                    System.out.println("reGet children: "+zooKeeper.getChildren("/",true));
                    System.out.println("last!!!!!!!!!!!!!!!");
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        }

    }
}
