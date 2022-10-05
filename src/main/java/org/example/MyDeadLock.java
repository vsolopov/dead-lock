package org.example;

import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;

public class MyDeadLock {

    private static final String LOCK_1 = "1", LOCK_2 = "2";

    @SneakyThrows
    public static void main(String[] args) {

        Runnable runnableThread1 = () -> {
            synchronized (LOCK_1) {
                System.out.println("Thread 1: locked 1");

                //perform something
                List<Integer> integers = new ArrayList<>();
                for(int i=0; i< 10_000; i++){
                    integers.add(i);
                }


                synchronized (LOCK_2) {
                    System.out.println("Thread 1: locked 2");
                }
            }
        };

        Runnable runnableThread2 = () -> {
            synchronized (LOCK_2) {
                System.out.println("Thread 2: locked 1");

                //perform something
                List<Integer> integers = new ArrayList<>();
                for(int i=0; i< 10_000; i++){
                    integers.add(i);
                }

                synchronized (LOCK_1) {
                    System.out.println("Thread 2: locked 2");
                }
            }
        };

        Thread thread1 = new Thread(runnableThread1, "Thread 1");
        Thread thread2 = new Thread(runnableThread2, "Thread 2");
        Thread threadStateWatcher = new Thread(() -> {
            for (; ; ) {
                System.out.println(thread1.getName() + " state:" + thread1.getState());
                System.out.println(thread2.getName() + " state:" + thread2.getState());

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        thread1.start();
        thread2.start();
        threadStateWatcher.start();

        thread1.join();
        thread2.join();
    }
}