package org.otaku;

import cn.hutool.core.thread.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * thenCombine
 */
public class CompletableFuture3 {
    private static final Logger logger = LoggerFactory.getLogger(CompletableFuture3.class);

    public static void main(String[] args) {
        ExecutorService service = Executors.newCachedThreadPool(new NamedThreadFactory("service-", true));
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            logger.info("step1");
            return 1;
        }, service)
                .thenCombineAsync(CompletableFuture.supplyAsync(() -> {
                    logger.info("step2");
                    return 2;
                }, service), Integer::sum, service);
        logger.info("final result: {}", future.join());
    }

}
