package me.dery.ultimatebans.taks;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class ConcurrentTask {

    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
            1, 4, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>()
    );

    public static void runAsync(Runnable runnable) {
        CompletableFuture.runAsync(runnable, EXECUTOR);
    }

}
