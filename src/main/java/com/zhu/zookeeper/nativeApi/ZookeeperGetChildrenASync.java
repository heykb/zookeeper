package com.zhu.zookeeper.nativeApi;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

//异步获取子节点列表
public class ZookeeperGetChildrenASync implements Watcher {

    private static CountDownLatch connectedSemapthore = new CountDownLatch(1);
    private static ZooKeeper zooKeeper;
    public static void main(String[] args) {
        try {
            zooKeeper =
                    new ZooKeeper("192.168.112.144:2181",5000,new ZookeeperGetChildrenASync());

            System.out.println(zooKeeper.getState());
            connectedSemapthore.await();
            System.out.println("zookeeper session created");



            //true:使用创建会话时的默认watch 接受节点列表更改事件,
            //IChildren2Callback 异步获取Future
            zooKeeper.getChildren("/",true,new IChildren2Callback(),null);


            //创建新节点，发送通知测试
            zooKeeper.create("/zk-node","value".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT_SEQUENTIAL);

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
                    System.out.println("reGet children: "+zooKeeper.getChildren(watchedEvent.getPath(),true));
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        }

    }

    static class IChildren2Callback implements AsyncCallback.Children2Callback{

        @Override
        public void processResult(int rc, String path, Object ctx, List<String> childrenList, Stat stat) {
            System.out.println("get children znode result: [response code :"+rc+", param path: "+path+
                    ",ctx: "+ctx+"childrenList:"+childrenList+", stat: "+stat);
        }
    }
}
