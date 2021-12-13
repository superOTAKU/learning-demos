package org.otaku;

import cn.hutool.core.thread.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * thenCompose
 */
public class CompletableFuture2 {
    private static final Logger logger = LoggerFactory.getLogger(CompletableFuture2.class);

    public static void main(String[] args) {
        ExecutorService service = Executors.newCachedThreadPool(new NamedThreadFactory("service-", true));
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            logger.info("generate id {}", 1);
            return 1;
        }, service)
                //thenCompose的作用是，将CompletableFuture<V>转为CompletableFuture<U>
                //有点类似工厂流水线之间产物的流转
                .thenComposeAsync(i -> CompletableFuture.supplyAsync(() -> "user-" + i, service), service);
        logger.info("final result is {}", future.join());
    }

}
