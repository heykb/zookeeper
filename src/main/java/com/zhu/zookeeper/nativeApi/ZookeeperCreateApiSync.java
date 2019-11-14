package com.zhu.zookeeper.nativeApi;



import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

//创建节点，使用同步接口
public class ZookeeperCreateApiSync implements Watcher {

    private static CountDownLatch connectedSemapthore = new CountDownLatch(1);
    public static void main(String[] args) {
        try {
            ZooKeeper zooKeeper =
                    new ZooKeeper("192.168.0.138:2181",5000,new ZookeeperCreateApiSync());

            System.out.println(zooKeeper.getState());
            connectedSemapthore.await();
            System.out.println("zookeeper session created");
            //创建不受权限控制的临时节点
            String path1 = zooKeeper.create("/zk-test-","value".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            System.out.println("success create node: "+path1);

            //创建不受权限控制的临时顺序节点，会在节点路径后自动添加一个递增的数字
            String path2 = zooKeeper.create("/zk-test-","value".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            System.out.println("success create node: "+path2);



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
