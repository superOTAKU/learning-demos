package org.otaku;

import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.thread.ThreadUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * thenApply vs thenCompose
 */
public class CompletableFuture4 {
    private static final Logger logger = LoggerFactory.getLogger(CompletableFuture4.class);

    private static final ExecutorService service = Executors.newCachedThreadPool(new NamedThreadFactory("service-", true));

    public static void main(String[] args) {
        //两者没有本质的区别，只是形式上有所不同
        List<User> users = getUserIdsAsync().thenComposeAsync(CompletableFuture4::getUserDetailsAsync, service).join();
        logger.info("users: {}", users);
        List<User> users2 = getUserIdsAsync().thenApplyAsync(ids -> getUserDetailsAsync(ids).join(), service).join();
        logger.info("users: {}", users2);
    }

    @Data
    @AllArgsConstructor
    public static class User {
        private Integer id;
        private String name;
    }

    private static List<Integer> getUserIds() {
        logger.info("retrieving user id...");
        ThreadUtil.sleep(1000);
        return List.of(1, 2, 3);
    }

    private static User getUserDetail(Integer id) {
        logger.info("retrieving user {} detail", id);
        ThreadUtil.sleep(1000);
        return new User(id, "user-" + id);
    }

    private static CompletableFuture<List<Integer>> getUserIdsAsync() {
        return CompletableFuture.supplyAsync(CompletableFuture4::getUserIds, service);
    }

    private static CompletableFuture<User> getUserDetailAsync(Integer id) {
        return CompletableFuture.supplyAsync(() -> getUserDetail(id), service);
    }

    //这个方法本身返回CompletableFuture，于是使用thenCompose可以很容易链接起来
    private static CompletableFuture<List<User>> getUserDetailsAsync(List<Integer> userIds) {
        List<CompletableFuture<User>> futures = userIds.stream().map(CompletableFuture4::getUserDetailAsync).collect(Collectors.toList());
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream().map(CompletableFuture::join).collect(Collectors.toList()));
    }

}
