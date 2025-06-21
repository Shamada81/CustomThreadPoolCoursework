package customthreadpool;

import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        CustomThreadPool pool = new CustomThreadPool(
                2, 4,
                5, TimeUnit.SECONDS,
                5, 1
        );

        for (int i = 1; i <= 10; i++) {
            int taskId = i;
            pool.execute(() -> {
                System.out.println("[Task] Start Task " + taskId);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {}
                System.out.println("[Task] End Task " + taskId);
            });
        }

        Thread.sleep(15000);
        pool.shutdown();
    }
}
