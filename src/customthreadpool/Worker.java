package customthreadpool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class Worker extends Thread {
    private final BlockingQueue<Runnable> taskQueue;
    private final CustomThreadPool pool;

    public Worker(BlockingQueue<Runnable> taskQueue, CustomThreadPool pool, String name) {
        super(name);
        this.taskQueue = taskQueue;
        this.pool = pool;
    }

    @Override
    public void run() {
        try {
            while (!pool.isShutdown()) {
                Runnable task = taskQueue.poll(pool.getKeepAliveTime(), pool.getTimeUnit());
                if (task != null) {
                    System.out.println("[Worker] " + getName() + " executes " + task);
                    task.run();
                } else if (pool.shouldTerminateWorker(this)) {
                    System.out.println("[Worker] " + getName() + " idle timeout, stopping.");
                    break;
                }
            }
        } catch (InterruptedException ignored) {
        } finally {
            pool.workerTerminated(this);
            System.out.println("[Worker] " + getName() + " terminated.");
        }
    }
}
