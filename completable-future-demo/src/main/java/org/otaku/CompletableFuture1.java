package org.otaku;

import cn.hutool.core.thread.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 执行n个任务，有成功有失败，全部完成后做处理
 */
public class CompletableFuture1 {
    private static final Logger logger = LoggerFactory.getLogger(CompletableFuture1.class);
    //控制是否有任务出错
    private static final boolean makeError = true;

    public static void main(String[] args) {
        ExecutorService service = Executors.newCachedThreadPool(new NamedThreadFactory("service-", true));
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            futures.add(CompletableFuture.runAsync(new Task(), service));
        }
        CompletableFuture<Void> future = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenRunAsync(() -> {
                    logger.info("run when all futures success");
                }, service)
                .exceptionally(e -> {
                    logger.error("run if any future fail", e);
                    return null;
                })
                .whenCompleteAsync((v, e) -> {
                    logger.info("futures complete, value is " + v);
                    if (e != null) {
                        logger.error("errors in futures", e);
                    }
                }, service);
        future.join();
        logger.info("all task finish!");
    }

    public static class Task implements Runnable {
        private static final AtomicInteger counter = new AtomicInteger();

        private final int id;

        public Task() {
            this.id = counter.incrementAndGet();
        }

        @Override
        public void run() {
            logger.info("task {} executed", id);
            if (!makeError) {
                return;
            }
            if (id % 2 == 0) {
                throw new IllegalStateException(String.format("task %s illegal", id));
            }
        }
    }

}
