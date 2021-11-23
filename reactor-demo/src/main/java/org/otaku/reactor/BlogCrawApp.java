package org.otaku.reactor;

import cn.hutool.core.thread.NamedThreadFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class BlogCrawApp {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Blog {
        private String title;
    }

    public interface BlogListener {
        public void onCreate(Blog blog);
    }

    public static class BlogCreator {
        private final List<BlogListener> listeners = new CopyOnWriteArrayList<>();
        private final ExecutorService service = Executors.newSingleThreadExecutor(new NamedThreadFactory("blog-producer-", false));
        private final AtomicInteger counter = new AtomicInteger(0);

        public void start() {
            service.execute(this::createBlogs);
        }

        public void createBlogs() {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    //ignored
                }
                System.out.println(String.format("t[%s] create a new blog...", Thread.currentThread().getName()));
                var blog = new Blog("blog-" + counter.incrementAndGet());
                listeners.forEach(listener -> listener.onCreate(blog));
            }
        }

        public void registerListener(BlogListener listener) {
            listeners.add(listener);
        }

    }

    public static void main(String[] args) {
        BlogCreator blogCreator = new BlogCreator();
        blogCreator.start();
        ExecutorService service = Executors.newFixedThreadPool(4, new NamedThreadFactory("blog-consumer-", false));
        //在create阶段，我们仅仅注册了listener，listener被调用则触发sink.next，进而触发Subscriber
        Flux.<Blog>create(sink -> blogCreator.registerListener(sink::next))
                .parallel(4)
                .runOn(Schedulers.fromExecutor(service))
                .subscribe(b -> {
                    try {
                        Thread.sleep(5000);
                    } catch (Exception e) {
                        //ignored
                    }
                    System.out.println(String.format("t[%s] consuming blog %s", Thread.currentThread().getName(), b));
                });
    }

}
