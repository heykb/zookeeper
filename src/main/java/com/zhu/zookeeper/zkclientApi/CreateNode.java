package com.zhu.zookeeper.zkclientApi;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;

import java.util.List;

public class CreateNode {
    public static void main(String[] args) {
        try {
            ZkClient zkClient = new ZkClient("192.168.112.144:2181",20000);
            System.out.println("zookeeper session created");

            List<String> childrenList = zkClient.getChildren("/");
            System.out.println(childrenList);

            String path  = "/test";
            //注册子节点变化监听器
            zkClient.subscribeChildChanges(path, new IZkChildListener() {
                @Override
                public void handleChildChange(String path, List<String> childrenList) throws Exception {
                    System.out.println(path + " Child changed reGet :" +childrenList);
                }
            });

            //添加节点
            zkClient.create(path,"value", CreateMode.PERSISTENT);
            System.out.println(zkClient.getChildren(path));

            //添加子节点
            zkClient.createEphemeral(path+"/c1");
            Thread.sleep(1000);

            //删除子节点
            zkClient.delete(path+"/c1");
            Thread.sleep(1000);
            //删除节点
            zkClient.delete(path);
            Thread.sleep(1000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
