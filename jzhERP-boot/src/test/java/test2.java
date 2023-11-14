import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class test2 {
    public static void  main(String [] args){
        new ThreadPoolExecutor.AbortPolicy();

        ExecutorService service=new ThreadPoolExecutor(3, 3, 10, TimeUnit.SECONDS, new SynchronousQueue<>(), new ThreadFactory() {
            private  final AtomicInteger poolNumber = new AtomicInteger(1);
            private final ThreadGroup group=System.getSecurityManager()!=null?System.getSecurityManager().getThreadGroup():Thread.currentThread().getThreadGroup();
            private final AtomicInteger threadNumber = new AtomicInteger(1);
            private final String namePrefix="业务1-" +
                    poolNumber.getAndIncrement() +
                    "-thread-";;
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(group, r,
                        namePrefix + threadNumber.getAndIncrement(),
                        0);
                if (t.isDaemon()){
                    t.setDaemon(false);
                }
                if (t.getPriority() != Thread.NORM_PRIORITY){
                    t.setPriority(Thread.NORM_PRIORITY);
                }
                return t;
            }
        }, new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                    System.out.println("这个是我的策略");
            }
        });
        service.execute(()->{
            System.out.println("popl "+Thread.currentThread().getName());
        });
        service.execute(()->{
            System.out.println("pop2 "+Thread.currentThread().getName());
        });
    }
}
