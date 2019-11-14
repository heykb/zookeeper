package com.zhu.zookeeper.nativeApi;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

//异步获取节点数据
public class ZookeeperGetDataASync implements Watcher {

    private static CountDownLatch connectedSemapthore = new CountDownLatch(1);
    private static ZooKeeper zooKeeper;
    private static Stat stat = new Stat();
    public static void main(String[] args) {
        try {
            zooKeeper =
                    new ZooKeeper("192.168.0.138:2181",5000,new ZookeeperGetDataASync());

            System.out.println(zooKeeper.getState());
            connectedSemapthore.await();
            System.out.println("zookeeper session created");

            String path = zooKeeper.create("/zk-test1","value".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

            zooKeeper.getData(path,true,new IDataCallback(),null);

            zooKeeper.setData(path,"value".getBytes(),-1);
            Thread.sleep(7000);
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
        String path = watchedEvent.getPath();
        if(Event.KeeperState.SyncConnected == watchedEvent.getState()){

            if(watchedEvent.getType()== Event.EventType.None && path==null ) {
                //解除主程序阻塞
                connectedSemapthore.countDown();
            }else if(watchedEvent.getType() == Event.EventType.NodeDataChanged){

                try {
                    byte[] value = zooKeeper.getData(path,true,stat);
                    System.out.println("get node "+path+":"+new String(value));
                    System.out.println(stat.getCzxid()+", "+stat.getMzxid()+", "+stat.getVersion());
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    static  class IDataCallback implements AsyncCallback.DataCallback{

        @Override
        public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
            System.out.println("get children znode result: [response code :"+rc+", param path: "+path+
                    ",ctx: "+ctx+"data :"+new String(data)+", stat: "+stat);
        }
    }
}
