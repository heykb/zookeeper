package com.zhu.zookeeper.curator;

import java.util.Random;
import java.util.concurrent.*;

//CyclicBarrier模拟赛跑
public class CyclicBarrierJdk {
    public static CyclicBarrier cyclicBarrier = new CyclicBarrier(3);
    public static Random random = new Random();
    public static void main(String[] args) {
        ExecutorService threedPool =
                new ThreadPoolExecutor(3,3,0L, TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(10));
        threedPool.submit(new Runner("1号选手"));
        threedPool.submit(new Runner("2号选手"));
        threedPool.submit(new Runner("3号选手"));
    }
    static class Runner implements Runnable{
        private String name;
        public Runner(String name){
            this.name=name;
        }
        @Override
        public void run() {

            try {
                System.out.println(name+" 准备好了");
                cyclicBarrier.await();
                System.out.println(name+" 起跑");
                Thread.sleep(random.nextInt(3)*1000);
                System.out.println(name+" 到终点了");
                cyclicBarrier.await();
                System.out.println(name+" 吃饭");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }

        }
    }
}
