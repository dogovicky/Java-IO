package nonBlockingIO;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class WebFluxSimulation {

    public void webFluxSimulation() throws InterruptedException {
        System.out.println("=== Simulating 1000 Concurrent Requests ===\n");

        // Simulate blocking approach
        blockingApproach();

        Thread.sleep(2000);
        System.out.println("\n---\n");

        // Simulate non-blocking approach
        nonBlockingApproach();
    }

    // ============= BLOCKING APPROACH =============
    public void blockingApproach() throws InterruptedException {
        System.out.println("======== Blocking approach (Spring MVC) =========");
        System.out.println("Thread per request model\n");

        int requests = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(200); // Tomcat default
        AtomicInteger completed = new AtomicInteger(0);

        long start = System.currentTimeMillis();

        for (int i = 0; i < requests; i++) {
            int requestId = i;
            executorService.submit(() -> {
                try {
                    // Simulate Blocking I/O (database query, HTTP call etc.)
                    Thread.sleep(100); // 100ms blocking operation

                    int done = completed.incrementAndGet();
                    if (done % 100 == 0) {
                        System.out.println("Completed: " + done + " requests");
                    }

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(30, TimeUnit.SECONDS);

        long duration = System.currentTimeMillis() - start;

        System.out.println("\nResults:");
        System.out.println("  Total requests: " + requests);
        System.out.println("  Thread pool size: 200");
        System.out.println("  Time taken: " + duration + "ms");
        System.out.println("  Throughput: " + (requests * 1000 / duration) + " req/s");
        System.out.println("  Peak threads: 200 (limited by pool)");

    }

    // ================= NON BLOCKING APPROACH ===============
    public void nonBlockingApproach() throws InterruptedException {
        System.out.println("NON-BLOCKING APPROACH (Spring WebFlux)");
        System.out.println("Event loop model (like Selectors)\n");

        int requests = 1000;
        int eventLoopThreads = 8; // Typical for reactive apps
        ExecutorService executorService = Executors.newFixedThreadPool(eventLoopThreads);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

        AtomicInteger completed = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(requests);

        long start = System.currentTimeMillis();

        for (int i = 0; i < requests; i++) {
            int requestId = i;

            // Submit to event loop
            executorService.submit(() -> {

                // Instead of blocking, schedule for later
               scheduler.schedule(() -> {
                   //Callback executed after "I/O Completes"
                   int done = completed.incrementAndGet();
                   if (done % 100 == 0) {
                       System.out.println("Completed: " + done + " requests");
                   }
                   latch.countDown();
               }, 100, TimeUnit.MILLISECONDS);
            });
        }

        latch.await(30, TimeUnit.SECONDS);

        long duration = System.currentTimeMillis() - start;

        System.out.println("\nResults:");
        System.out.println("  Total requests: " + requests);
        System.out.println("  Event loop threads: " + eventLoopThreads);
        System.out.println("  Time taken: " + duration + "ms");
        System.out.println("  Throughput: " + (requests * 1000 / duration) + " req/s");
        System.out.println("  Peak threads: " + eventLoopThreads + " (minimal!)");

        executorService.shutdown();
        scheduler.shutdown();

    }

}
