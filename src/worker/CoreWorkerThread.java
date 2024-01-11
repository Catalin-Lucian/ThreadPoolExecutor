package worker;


import threadexecutor.ThreadPoolExecutor;

public class CoreWorkerThread extends WorkerThread {

    public CoreWorkerThread(ThreadPoolExecutor executor) {
        super(executor);
    }

    public CoreWorkerThread(ThreadPoolExecutor executor, Runnable task) {
        super(executor, task);
    }

    @Override
    public void run() {
        // loop run until worker is stopped
        while (!isStopped) {
            try {
                if (currentTask == null) {
                    System.out.println("[" + Thread.currentThread().getName() + "] looking for new task.");
                    currentTask = executor.taskQueue.take();
                }
            } catch (InterruptedException e) {
                System.out.println("[" + Thread.currentThread().getName() + "] interrupted.");
                continue;
            }

            // if task is found, execute it
            System.out.println("[" + Thread.currentThread().getName() + "] executing task.");
            currentTask.run();
            currentTask = null;
        }

        System.out.println("[" + Thread.currentThread().getName() + "] stopped.");
        executor.removeCoreWorker(this);
    }
}
