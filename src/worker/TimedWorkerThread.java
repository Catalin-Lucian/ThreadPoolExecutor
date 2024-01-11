package worker;

import threadexecutor.ThreadPoolExecutor;

public class TimedWorkerThread extends WorkerThread {
    private final long keepAliveTime;

    public TimedWorkerThread(ThreadPoolExecutor executor, long keepAliveTime) {
        super(executor);
        this.keepAliveTime = keepAliveTime;
    }

    public TimedWorkerThread(ThreadPoolExecutor executor, Runnable task, long keepAliveTime) {
        super(executor, task);
        this.keepAliveTime = keepAliveTime;
    }

    @Override
    public void run() {
        while (!isStopped) {
            try {
                if (currentTask == null) {
                    System.out.println("[" + Thread.currentThread().getName() + "] looking for new task.");
                    currentTask = executor.taskQueue.poll(keepAliveTime);
                }

                // if no task was found after waiting for keepAliveTime, stop the thread
                if (currentTask == null) {
                    System.out.println("[" + Thread.currentThread().getName() + "] no task found.");
                    break;
                }
            }
            // if interrupted while waiting for a task, stop the thread
            catch (InterruptedException e) {
                System.out.println("[" + Thread.currentThread().getName() + "] interrupted.");
                continue;
            }

            // if task is found, execute it
            System.out.println("[" + Thread.currentThread().getName() + "] executing task.");
            currentTask.run();
            currentTask = null;
        }

        System.out.println("[" + Thread.currentThread().getName() + "] stopped.");
        executor.removeExtraWorker(this);
    }
}
