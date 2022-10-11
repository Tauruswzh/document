package demo.code.util;

import java.util.concurrent.CountDownLatch;

public class Test {

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(10);

        for (int i = 0; i < countDownLatch.getCount(); i++) {
            System.out.println("======数量"+countDownLatch.getCount());
            ThreadPoolUtil.getInstance().execute(() -> {
                try {
                    System.out.println("数量"+(countDownLatch.getCount())+"线程名"+Thread.currentThread().getName());
                    Thread.sleep(10000);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    //每次减一
                    countDownLatch.countDown();
                    System.out.println("&&&&&&&&&&&数量"+countDownLatch.getCount());
                }
            });
        }
        //当countDownLatch等于0才能往下走
        countDownLatch.await();

        System.out.println("开始执行。。。。。。");
    }


}
