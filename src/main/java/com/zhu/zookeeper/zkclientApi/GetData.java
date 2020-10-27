package com.zhu.zookeeper.zkclientApi;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.Watcher;

public class GetData {
    public static void main(String[] args) throws Exception {
        ZkClient zkClient = new ZkClient("192.168.112.144:2181",20000);
        System.out.println("zookeeper session created");
        String path = "/zk-data";
        zkClient.createEphemeral(path,"123");
        zkClient.subscribeDataChanges(path, new IZkDataListener() {
            @Override
            public void handleDataChange(String path, Object data) throws Exception {
                System.out.println(String.format("node [%s] changed, new Data : %s",path,data));
            }

            @Override
            public void handleDataDeleted(String path) throws Exception {
                System.out.println(String.format("node[%s] deleted",path));
            }
        });

        String data = zkClient.readData(path);
        System.out.println(data);
        zkClient.writeData(path,"456");
        Thread.sleep(1000);
        zkClient.delete(path);
        Thread.sleep(6000);
    }
}
