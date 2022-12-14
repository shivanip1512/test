package com.cannontech.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class ThreadCachingScheduledExecutorServiceTest {
    private class ForeverTask implements Runnable {
        final String name;
        ForeverTask(String name) {
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
        final String name;
        final int secondsToRun;

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
            log("ShortTask " + name + "; run, all done; thread=" + Thread.currentThread().getName());
        }
    }

    /**
     * This is a slow running test (about 65 seconds) that does not need to be run often.
     * Comment out @Ignore below to run.
     */
    @Test
    @Disabled
    public void testScheduleRunnableLongTimeUnit() {
        ThreadCachingScheduledExecutorService service = new ThreadCachingScheduledExecutorService("test");

        service.schedule(new ForeverTask("one"), 0, TimeUnit.SECONDS);
        service.schedule(new ForeverTask("two"), 0, TimeUnit.SECONDS);
        service.schedule(new ForeverTask("three"), 0, TimeUnit.SECONDS);
        service.schedule(new ForeverTask("four"), 0, TimeUnit.SECONDS);

        service.schedule(new ShortTask("uno", 2), 0, TimeUnit.SECONDS);
        service.schedule(new ShortTask("dos", 2), 0, TimeUnit.SECONDS);
        service.schedule(new ShortTask("tres", 2), 0, TimeUnit.SECONDS);
        service.schedule(new ShortTask("cuatro", 3), 0, TimeUnit.SECONDS);
        service.schedule(new ShortTask("cinco", 3), 0, TimeUnit.SECONDS);
        service.schedule(new ShortTask("seis", 3), 0, TimeUnit.SECONDS);
        service.schedule(new ShortTask("siete", 4), 0, TimeUnit.SECONDS);
        service.schedule(new ShortTask("ocho", 4), 0, TimeUnit.SECONDS);
        service.schedule(new ShortTask("nueve", 4), 0, TimeUnit.SECONDS);
        for (int count = 0; count < 10; count++) {
            service.schedule(new ShortTask("loop " + count, 1), 0, TimeUnit.SECONDS);
        }

        // A really bogged down system could cause this to fail but it shouldn't as a rule.  We don't need to
        // run these tests as part of the build anyway.
        int numRunningTasks = 4 + 9 + 10;
        sleep(500);
        assertEquals(numRunningTasks, service.getActiveCount(), "all tasks started");
        sleep(1000); numRunningTasks -= 10;
        assertEquals(numRunningTasks, service.getActiveCount(), "1 second tasks all done");
        sleep(1000); numRunningTasks -= 3;
        assertEquals(numRunningTasks, service.getActiveCount(), "2 second tasks all done");
        sleep(1000); numRunningTasks -= 3;
        assertEquals(numRunningTasks, service.getActiveCount(), "3 second tasks all done");
        sleep(1000); numRunningTasks -= 3;
        assertEquals(numRunningTasks, service.getActiveCount(), "4 second tasks all done");
        assertEquals(4, numRunningTasks, "double check final number of running tasks");
        assertTrue(service.getPoolSize() <= 4 + 9 + 10, "pool size no greater than max threads running");
        sleep(60 * 1000);
        assertEquals(4, service.getPoolSize(), "pool size reduced after timeout");
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
