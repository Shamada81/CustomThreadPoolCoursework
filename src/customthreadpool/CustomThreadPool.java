package customthreadpool;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomThreadPool implements CustomExecutor {
    private final int corePoolSize;
    private final int maxPoolSize;
    private final long keepAliveTime;
    private final TimeUnit timeUnit;
    private final int minSpareThreads;
    private final BlockingQueue<Runnable> taskQueue;
    private final Set<Worker> workers = ConcurrentHashMap.newKeySet();
    private final CustomThreadFactory threadFactory;
    private final AtomicInteger currentPoolSize = new AtomicInteger(0);
    private volatile boolean isShutdown = false;

    public CustomThreadPool(int corePoolSize, int maxPoolSize, long keepAliveTime, TimeUnit timeUnit, int queueSize, int minSpareThreads) {
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.keepAliveTime = keepAliveTime;
        this.timeUnit = timeUnit;
        this.minSpareThreads = minSpareThreads;
        this.taskQueue = new LinkedBlockingQueue<>(queueSize);
        this.threadFactory = new CustomThreadFactory("MyPool");

        for (int i = 0; i < corePoolSize; i++) {
            addWorker();
        }
    }

    @Override
    public void execute(Runnable command) {
        if (isShutdown) return;

        if (!taskQueue.offer(command)) {
            if (currentPoolSize.get() < maxPoolSize) {
                addWorker();
                taskQueue.offer(command);
            } else {
                System.out.println("[Rejected] Task " + command + " was rejected due to overload!");
            }
        } else {
            System.out.println("[Pool] Task accepted into queue: " + command);
        }
    }

    @Override
    public <T> Future<T> submit(Callable<T> callable) {
        FutureTask<T> task = new FutureTask<>(callable);
        execute(task);
        return task;
    }

    private void addWorker() {
        if (isShutdown) return;
        Worker worker = new Worker(taskQueue, this, threadFactory.newThread(() -> {}).getName());
        workers.add(worker);
        currentPoolSize.incrementAndGet();
        worker.start();
    }

    public boolean shouldTerminateWorker(Worker worker) {
        return currentPoolSize.get() > corePoolSize && taskQueue.isEmpty();
    }

    public void workerTerminated(Worker worker) {
        workers.remove(worker);
        currentPoolSize.decrementAndGet();
    }

    @Override
    public void shutdown() {
        isShutdown = true;
        System.out.println("[Main] Pool shutdown initiated.");
    }

    @Override
    public void shutdownNow() {
        isShutdown = true;
        for (Worker w : workers) w.interrupt();
    }

    public boolean isShutdown() {
        return isShutdown;
    }

    public long getKeepAliveTime() {
        return keepAliveTime;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }
}
