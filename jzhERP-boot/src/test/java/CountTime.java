import java.util.concurrent.CountDownLatch;

/**
 * @author soberw
 * @Classname CountTime
 * @Description 开十个线程打印输出1~10000中偶数的值，计算总耗时    用闭锁（门栓）
 * @Date 2021-12-28 15:22
 */
public class CountTime {
    static CountDownLatch cdl = new CountDownLatch(10);

    void show() {
        for (int i = 0; i < 10000; i++) {
            if (i % 2 == 0) {
                System.out.println(Thread.currentThread().getName() + ":" + i);
            }
        }
        cdl.countDown();
    }

    public static void main(String[] args) {
        CountTime ct = new CountTime();
        long start = System.currentTimeMillis();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 10; i++) {
            new Thread(ct::show).start();
        }
        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println((end - start - 100) + "--------------");
    }
}
