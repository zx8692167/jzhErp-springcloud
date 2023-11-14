import java.util.concurrent.locks.ReentrantLock;

public class Reentrantlooktest {
    public static void main(String[] args) {
        ReentrantLockDemo reentrantLockDemo=new ReentrantLockDemo();
        try {
            reentrantLockDemo.outer();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }



}
  class ReentrantLockDemo {
    private final ReentrantLock lock = new ReentrantLock();
    public void outer() throws InterruptedException{
        lock.lock(); // 第一次获取锁
        System.out.println(lock.getHoldCount());
        inner();
        lock.unlock(); // 释放锁
    }
    public void inner() throws InterruptedException{
        lock.lock(); // 第二次获取锁

        System.out.println(lock.getHoldCount()+" Hello World!");
        lock.unlock(); // 释放锁
    }
}
