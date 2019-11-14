package com.zhu.zookeeper.curator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
//创建10个线程，同时去执行代码
public class NoLock {
    public static void main(String[] args) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        for(int i=0;i<10;i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        countDownLatch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String orderNo = new SimpleDateFormat("HH:mm:ss|SSS").format(new Date());
                    System.out.println("生成的订单号是"+orderNo);
                }
            }).start();
        }
        countDownLatch.countDown();


    }
}
