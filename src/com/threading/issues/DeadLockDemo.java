package com.threading.issues;

import java.util.concurrent.CountDownLatch;

/*
Deadlocks happen when two or more threads aren't able to make any progress because the resource required by the first thread is held by the second and the resource required by the second thread is held by the first.

Ability of a program or an application to execute in a timely manner is called liveness. If a program experiences a deadlock then it's not exhibiting liveness.

A live-lock happens when two threads keep taking actions in response to the other thread instead of making any progress.

Other than a deadlock, an application thread can also experience starvation, where it never gets CPU time or access to shared resources because other "greedy" threads hog the resources.
 */

public class DeadLockDemo {

    public static void main(String args[]) {
        Deadlock deadlock = new Deadlock();
        try {
            deadlock.runTest();
        } catch (InterruptedException ie) {
        }
    }
}

class Deadlock {

    private int counter = 0;
    private Object lock1 = new Object();
    private Object lock2 = new Object();
    CountDownLatch latch = new CountDownLatch(2);

    Runnable incrementer = new Runnable() {

        @Override
        public void run() {
            try {
                for (int i = 0; i < 100; i++) {
                    incrementCounter();
                    System.out.println("Incrementing " + i);
                }
            } catch (InterruptedException ie) {
            }
        }
    };

    Runnable decrementer = new Runnable() {

        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                decrementCounter();
                System.out.println("Decrementing " + i);
            }
        }
    };

    public void runTest() throws InterruptedException {

        Thread thread1 = new Thread(incrementer);
        Thread thread2 = new Thread(decrementer);

        thread1.start();
        // sleep to make sure thread 1 gets a chance to acquire lock1
        Thread.sleep(100);
        thread2.start();

        thread1.join();
        thread2.join();

        System.out.println("Done : " + counter);
    }

    void incrementCounter() throws InterruptedException {
        synchronized (lock1) {
            latch.countDown();
            System.out.println("Acquired lock1");
            latch.await();
            synchronized (lock2) {
                counter++;
            }
        }
    }

    void decrementCounter() {
        synchronized (lock2) {
            System.out.println("Acquired lock2");
            latch.countDown();
            synchronized (lock1) {
                counter--;
            }
        }
    }
}