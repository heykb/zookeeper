package com.zhu.zookeeper.zkclientApi;

import org.I0Itec.zkclient.ZkClient;

public class CreateSessionSample {
    public static void main(String[] args) {
        ZkClient zkClient = new ZkClient("192.168.112.144:2181",20000);
        System.out.println("zookeeper session created");

    }
}
