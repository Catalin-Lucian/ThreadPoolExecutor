package worker;


import threadexecutor.ThreadPoolExecutor;

public abstract class WorkerThread extends Thread {
    protected boolean isStopped;
    protected ThreadPoolExecutor executor;
    protected Runnable currentTask;

    public WorkerThread(ThreadPoolExecutor executor) {
        this.executor = executor;
        this.isStopped = false;
        this.currentTask = null;
    }

    public WorkerThread(ThreadPoolExecutor executor, Runnable task) {
        this.executor = executor;
        this.isStopped = false;
        this.currentTask = task;
    }

    public void stopThread() {
        this.isStopped = true;
        this.interrupt();
    }
}