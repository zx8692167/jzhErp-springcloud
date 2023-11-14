import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author soberw
 * @Classname TurnNumber
 * @Description 编写程序，实现三个线程，运行输出 A1 B2 C3 A4 B5 C6 …..  用公平锁
 * @Date 2021-12-28 14:09
 */
public class TurnNumber {
    AtomicInteger num = new AtomicInteger(0);
    //int num=0;
    private final ReentrantLock rl = new ReentrantLock(true);

    public  void show() {

            for (; ; ) {
            //for (int h = 0; h <3 ; h++) {

            //synchronized (this) {
                    rl.lock();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String tn = Thread.currentThread().getName();
                int i = num.incrementAndGet();
                //int i = num++;
                String s = String.format("%s%d", tn, i);
                System.out.print(s + "  ");
                if ("C".equals(tn)) {
                    System.out.println();
                }
                     rl.unlock();
        //    }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        TurnNumber tn = new TurnNumber();
        Thread a = new Thread(tn::show, "A");
        Thread b = new Thread(tn::show, "B");
        Thread c = new Thread(tn::show, "C");
        a.setPriority(Thread.MAX_PRIORITY);
        a.start();
        b.setPriority(Thread.NORM_PRIORITY);
        b.start();
        c.setPriority(Thread.MIN_PRIORITY);
        c.start();



    //    a.join();
    //    b.join();
    //    c.join();


    }
}