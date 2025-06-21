package customthreadpool;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomThreadFactory implements ThreadFactory {
    private final String poolName;
    private final AtomicInteger count = new AtomicInteger(1);

    public CustomThreadFactory(String poolName) {
        this.poolName = poolName;
    }

    @Override
    public Thread newThread(Runnable r) {
        String name = poolName + "-worker-" + count.getAndIncrement();
        System.out.println("[ThreadFactory] Creating new thread: " + name);
        Thread t = new Thread(r, name);
        t.setUncaughtExceptionHandler((t1, e) -> System.out.println("[Error] Thread " + t1.getName() + " crashed: " + e));
        return t;
    }
}
