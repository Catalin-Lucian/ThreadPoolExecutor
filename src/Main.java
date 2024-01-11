import threadexecutor.ThreadPoolExecutor;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        // Scenario 1: Basic Task Execution
        System.out.println("Scenario 1: Basic Task Execution");
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 4, 4000, 2);

        for (int i = 0; i < 8; i++) {

            executor.execute(() -> {
                System.out.println("Executing Task ...");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        Thread.sleep(20000); // Allow time for tasks to complete
        System.out.println("Shutting down executor");
        executor.shutdown();
    }
}
