package com.zhu.zookeeper.nativeApi;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

//创建节点，使用异步接口
public class ZookeeperCreateApiASync implements Watcher {


    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    public static void main(String[] args) {
        try {
            /**
             *
             */
            ZooKeeper zooKeeper =
                    new ZooKeeper("192.168.112.144:2181",5000,new ZookeeperCreateApiASync());

            System.out.println(zooKeeper.getState());
            connectedSemaphore.await();
            System.out.println("zookeeper session created");
            // 异步创建不受权限控制的临时节点
            zooKeeper.create("/zk-test-","value".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.EPHEMERAL,new IStringCallback(),"success create node %s: %s");


            // 异步创建不受权限控制的临时顺序节点，会在节点路径后自动添加一个递增的数字
            zooKeeper.create("/zk-test-","value".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.EPHEMERAL_SEQUENTIAL,new IStringCallback(),"success create node %s: %s");



            Thread.sleep(5000);
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
            connectedSemaphore.countDown();
        }
    }

    static class IStringCallback implements AsyncCallback.StringCallback{

        @Override
        public void processResult(int i, String s, Object o, String s1) {
            if(i == 0){
                System.out.println(String.format(o.toString(),s,s1));
            }
        }
    }
}
