package org.otaku;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * ForkJoinTask框架实现了任务分隔，任务窃取的功能，目的是便于实现并行的计算
 */
public class ForkJoinTaskDemo {

    public static void main(String[] args) {
        //生成1000_0000个整数
        List<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < 1000_0000; i++) {
            numbers.add(ThreadLocalRandom.current().nextInt(0, 10));
        }
        System.out.println("finish number gen...");
        //求和
        ForkJoinPool pool = new ForkJoinPool(8);
        Long result = calculateTime(() -> pool.invoke(new SumTask(numbers, 0, numbers.size())));
        System.out.println("result is " + result);

        Long result2 = calculateTime(() -> {
            long r = 0L;
            for (Integer number : numbers) {
                r += number;
            }
            return r;
        });
        System.out.println("result2 is " + result2);
    }

    private static <V> V calculateTime(Supplier<V> task) {
        long start = System.nanoTime();
        V result = task.get();
        long end = System.nanoTime();
        System.out.println("耗时：" + (end - start) + "纳秒");
        return result;
    }

    private static class SumTask extends RecursiveTask<Long> {
        private final List<Integer> numbers;
        //inclusive
        private final int start;
        //exclusive
        private final int end;

        public SumTask(List<Integer> numbers, int start, int end) {
            this.numbers = numbers;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Long compute() {
            //10000个数直接单线程计算
            if (end - start <= 10000) {
                long result = 0L;
                for (int i = start; i < end; i++) {
                    result += numbers.get(i);
                }
                return result;
            }
            //二分
            int mid = (end - start) / 2 + start;
            SumTask task1 = new SumTask(numbers, start, mid);
            SumTask task2 = new SumTask(numbers, mid, end);
            task1.fork();
            task2.fork();
            Long result1 = task1.join();
            Long result2 = task2.join();
            return result1 + result2;
        }
    }

}
