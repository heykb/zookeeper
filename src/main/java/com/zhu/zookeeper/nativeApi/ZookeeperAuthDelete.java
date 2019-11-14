package com.zhu.zookeeper.nativeApi;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZookeeperAuthDelete implements Watcher{

    private static CountDownLatch connectedSemapthore = new CountDownLatch(1);
    public static void main(String[] args) {
        try {
            ZooKeeper zooKeeper =
                    new ZooKeeper("192.168.0.138:2181",5000,new ZookeeperAuthDelete());
            System.out.println(zooKeeper.getState());
            connectedSemapthore.await();
            System.out.println("zookeeper session created");
            zooKeeper.addAuthInfo("digest","foo:true".getBytes());

            String pathP = zooKeeper.create("/zk-auth-delete","value".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.PERSISTENT_SEQUENTIAL);
            String pathC = zooKeeper.create(pathP+"/c1","value".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.EPHEMERAL);


            //使用未添加权限信息或错误权限信息zookeeper 删除有权限的节点
            ZooKeeper zooKeeper2 = new ZooKeeper("192.168.0.138:2181",5000,new ZookeeperAuthDelete());
            connectedSemapthore = new CountDownLatch(1);
            connectedSemapthore.await();
            System.out.println("zookeeper session2 created");


            try {
                zooKeeper2.delete(pathC, -1);
                System.out.println("delete node " + pathC +" success");
            }catch (Exception e){
                System.out.println("delete node " + pathC +" failed");
            }
            try {
                //先删除子节点才能测试删除父节点
                zooKeeper.delete(pathC, -1);
                zooKeeper2.delete(pathP, -1);
                System.out.println("delete node " + pathP +" success");
            }catch (Exception e){
                System.out.println("delete node " + pathP +" failed");
            }

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
