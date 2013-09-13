package com.cannontech.jobs.service.impl;

import java.util.concurrent.TimeUnit;

import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.common.util.ThreadCachingScheduledExecutorService;

// TODO:  can we make this into a proper test of ThreadCachingScheduledExecutorService?
public class ExecutorTest {
    private class ForeverTask implements Runnable {
        private final String name;
        private ForeverTask(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            log("ForeverTask " + name + "; run, enter; thread=" + Thread.currentThread().getName());
            while (true) {
                sleep(1000);
                log("ForeverTask " + name + "; run, iterc; thread=" + Thread.currentThread().getName());
            }
        }
    }

    private class ShortTask implements Runnable {
        private final String name;
        private final int secondsToRun;

        private ShortTask(String name, int secondsToRun) {
            this.name = name;
            this.secondsToRun = secondsToRun;
        }

        @Override
        public void run() {
            log("ShortTask " + name + "; run, enter; thread=" + Thread.currentThread().getName());
            for (int count = 0; count < secondsToRun; count++) {
                sleep(1000);
                log("ShortTask " + name + "; run, iterc; thread=" + Thread.currentThread().getName());
            }
            Thread.currentThread().interrupt();
            log("ShortTask " + name + "; run, all done; thread=" + Thread.currentThread().getName());
        }
    }

//    private ScheduledExecutorService service = Executors.newScheduledThreadPool(10);
//    private ScheduledExecutorService service = new ScheduledThreadPoolExecutor(500);
    private ScheduledExecutor service = new ThreadCachingScheduledExecutorService();

    private ExecutorTest(String[] args) {
        service.schedule(new ForeverTask("one"), 0, TimeUnit.SECONDS);
        service.schedule(new ForeverTask("two"), 0, TimeUnit.SECONDS);
        service.schedule(new ForeverTask("three"), 0, TimeUnit.SECONDS);
        service.schedule(new ForeverTask("four"), 0, TimeUnit.SECONDS);
        service.schedule(new ShortTask("uno", 2), 0, TimeUnit.SECONDS);
        service.schedule(new ShortTask("dos", 3), 0, TimeUnit.SECONDS);
        service.schedule(new ShortTask("tres", 3), 0, TimeUnit.SECONDS);
        service.schedule(new ShortTask("cuatro", 4), 0, TimeUnit.SECONDS);
        service.schedule(new ShortTask("cinco", 4), 0, TimeUnit.SECONDS);
        service.schedule(new ShortTask("seis", 4), 0, TimeUnit.SECONDS);
        service.schedule(new ShortTask("siete", 5), 0, TimeUnit.SECONDS);
        service.schedule(new ShortTask("ocho", 5), 0, TimeUnit.SECONDS);
        service.schedule(new ShortTask("nueve", 5), 0, TimeUnit.SECONDS);
        for (int count = 0; count < 10; count++) {
            service.schedule(new ShortTask("loop " + count, 1), 0, TimeUnit.SECONDS);
        }
        while (true) {
            log("*** There are " + Thread.activeCount() + " active threads.");
            sleep(1000);
        }
    }

    public static void main(String[] args) {
        new ExecutorTest(args);
    }

    private void log(String msg) {
        System.out.println(msg);
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
