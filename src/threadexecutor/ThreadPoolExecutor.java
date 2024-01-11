package threadexecutor;

import queue.BlockingQueue;
import worker.CoreWorkerThread;
import worker.TimedWorkerThread;
import worker.WorkerThread;

import java.util.ArrayList;
import java.util.List;

public class ThreadPoolExecutor {
    public final BlockingQueue<Runnable> taskQueue;
    private final List<CoreWorkerThread> coreWorkers;
    private final List<TimedWorkerThread> extraWorkers;
    private final int corePoolSize;
    private final int extraPoolSize;

    private final long keepAliveTime;
    private boolean isShutdown = false;


    public ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long timeoutMillis, int queueSize) {
        this.taskQueue = new BlockingQueue<>(queueSize);

        this.corePoolSize = corePoolSize;
        this.extraPoolSize = maximumPoolSize - corePoolSize;

        this.coreWorkers = new ArrayList<>(corePoolSize);
        this.extraWorkers = new ArrayList<>(extraPoolSize);

        this.keepAliveTime = timeoutMillis;
    }

    @SuppressWarnings("unused")
    public void preStartCoreWorkers() {
        for (int i = 0; i < corePoolSize; i++) {
            var workerThread = new CoreWorkerThread(this);
            coreWorkers.add(workerThread);
            workerThread.start();
        }
    }

    public void execute(Runnable task) {
        if (this.isShutdown) {
            throw new IllegalStateException("ThreadPoolExecutor is stopped");
        }

        //If fewer than corePoolSize threads are running,
        if (getCorePoolSize() < corePoolSize) {
            //The executor always prefers adding a new thread rather than queuing.
            System.out.println("Adding new core worker thread.");
            var workerThread = new CoreWorkerThread(this, task);
            addCoreWorker(workerThread);
            workerThread.start();
            return;
        }

        //If corePoolSize or more threads are running,
        try {
            System.out.println("Trying to add task to queue...");
            this.taskQueue.add(task);
            System.out.println("Task added.");
        }
        //If a request cannot be queued,
        catch (IllegalStateException e) {
            //A new thread is created unless this would exceed maximumPoolSize,
            if (getExtraPoolSize() < extraPoolSize) {
                System.out.println("Adding new extra worker thread.");
                var workerThread = new TimedWorkerThread(this, task, keepAliveTime);
                addExtraWorker(workerThread);
                workerThread.start();
            }
            //in which case, the task will be rejected.
            else {
                System.out.println("Rejected task. The queue is full.");
            }
        }
    }


    public void addCoreWorker(CoreWorkerThread worker) {
        synchronized (coreWorkers) {
            coreWorkers.add(worker);
        }
    }

    public void removeCoreWorker(CoreWorkerThread worker) {
        synchronized (coreWorkers) {
            coreWorkers.remove(worker);
        }
    }

    public int getCorePoolSize() {
        synchronized (coreWorkers) {
            return coreWorkers.size();
        }
    }

    public void addExtraWorker(TimedWorkerThread worker) {
        synchronized (extraWorkers) {
            extraWorkers.add(worker);
        }
    }

    public void removeExtraWorker(TimedWorkerThread worker) {
        synchronized (extraWorkers) {
            extraWorkers.remove(worker);
        }
    }

    public int getExtraPoolSize() {
        synchronized (extraWorkers) {
            return extraWorkers.size();
        }
    }

    public void shutdown() {
        for (WorkerThread worker : coreWorkers) {
            worker.stopThread();
        }
        for (WorkerThread worker : extraWorkers) {
            worker.stopThread();
        }

        this.isShutdown = true;
        taskQueue.clear();

        System.out.println("ThreadPoolExecutor is stopped.");
    }
}
