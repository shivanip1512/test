/**
 * 
 */
package com.cannontech.common.systemMetrics;


import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.core.dynamic.exception.DispatchNotConnectedException;
import com.google.common.util.concurrent.AtomicDouble;

/**
 * Spring Bean that monitors the JVM CPU usage and Memory Usage.
 */
@ManagedResource
public abstract class SystemMetricsBase extends Observable implements Runnable, SystemMetrics {

    private Logger log = YukonLogManager.getLogger(this.getClass());

    /** Attribute to access process memory usage */
    protected BuiltInAttribute memoryAttribute;
    /** Attribute to access process CPU usage */
    protected Attribute loadAverageAttribute;

    /**
     * System Property com.cannontech.common.systemMetrics.SystemMetricsImpl.reportFrequency sets
     * how often a log report is made as category Info. This is a multiplier to metricRate. Defaults
     * to every 30 minutes
     */
    private int reportFrequency =
        Integer.getInteger("com.cannontech.common.systemMetrics.SystemMetricsImpl.reportFrequency",
                           30);
    /** Counter for reports */
    private int reportFrequencyCounter = 0;

    /**
     * System Property com.cannontech.common.systemMetrics.SystemMetricsImpl.metricRate sets
     * how often the metrics are checked in seconds. Defaults to every minute
     */
    private final int metricRate =
        Integer.getInteger("com.cannontech.common.systemMetrics.SystemMetricsImpl.metricRate", 60);

    private final ScheduledExecutorService scheduler = Executors
        .newScheduledThreadPool(1, new SystemMetricsThreadFactory());
    private final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
    private final OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();

    private ScheduledFuture<?> task;

    private Instant lastSampleTime;
    private int processors;

    private Map<Long, Long> lastThreadTimes = new HashMap<Long, Long>();
    
    /** Allow loadAverage access from MBean viewer. */
    private AtomicDouble loadAverage = new AtomicDouble(0.0);

    /** Factory to force a thread name */
    class SystemMetricsThreadFactory implements ThreadFactory {
        public Thread newThread(Runnable r) {
            return new Thread(r, "SystemMetrics");
        }
    }

    /**
     * Spring (default) constructor.
     * 
     * Initialize the MXBeans (which provide load information).
     * Scan through the threads and record the current CPU time usage.
     * Save the start time for elapsed time calculation.
     * Schedule the run() method every 60 seconds.
     */
    public SystemMetricsBase() {
        log.debug("SystemMetricsImpl.SystemMetricsImpl()");

        threadMXBean.setThreadCpuTimeEnabled(true);
        processors = osMXBean.getAvailableProcessors();

        for (long thread : threadMXBean.getAllThreadIds()) {
            long threadCpuTime = threadMXBean.getThreadCpuTime(thread) / 1000;
            lastThreadTimes.put(thread, threadCpuTime);
        }

        lastSampleTime = Instant.now();
        task = scheduler.scheduleAtFixedRate(this, metricRate, metricRate, TimeUnit.SECONDS);
    }

    /**
     * Setter that takes the load average attribute Id.
     * 
     * @param loadAverageAttribute attribute Id.
     */
    @Override
    public void setLoadAverageAttribute(BuiltInAttribute loadAverageAttribute) {
        log.debug("setLoadAverageAttribute(" + loadAverageAttribute + ")");
        this.loadAverageAttribute = loadAverageAttribute;
    }

    /**
     * Setter that takes the memory attribute Id.
     * 
     * @param memoryAttribute attribute Id.
     */
    @Override
    public void setMemoryAttribute(BuiltInAttribute memoryAttribute) {
        log.debug("setMemoryAttribute(" + memoryAttribute + ")");
        this.memoryAttribute = memoryAttribute;
    }

    /**
     * Shut down the calculation scheduled task.
     */
    public void destroy() {
        log.debug("SystemMetricsImpl.destroy()");
        task.cancel(true);
    }

    /**
     * Since we went through all the trouble of calculating the process load, and to get rid of a
     * pesky warning, we export this so MBean can see it.
     * 
     * @return current load from this process.
     */
    
    @Override
    @ManagedAttribute
    public final double getloadAverage() {
        return loadAverage.get();
    }

    /*
     * Get the load average and send it as a point value.
     */
    @Override
    public void run() {
        try {
            log.debug("SystemMetricsImpl.run()");

            // Bump this counter every cycle, report statistic on when it matches reportFrequency
            reportFrequencyCounter++;

            try {
                double loadAverage = calculateLoadAverage();
                
                insertPointData(loadAverageAttribute, loadAverage);
                // Report load as log.info every reportFrequency or if loadAverage > 70%
                String message = "Process CPU Utilization: " + loadAverage + "%";
                if (reportFrequencyCounter % reportFrequency == 0 || loadAverage > 70) {
                    log.info(message);
                } else {
                    log.debug(message);
                }
            } catch (IllegalUseOfAttribute e) {
                log.warn("caught exception in SystemMetrics.run", e);
            }
            
            try {
                double memory = Runtime.getRuntime().totalMemory() / 1024 / 1024;
                double maxMemory = Runtime.getRuntime().maxMemory() / 1024 / 1024;
                
                insertPointData(memoryAttribute, memory);

                // Report memory as log.info every reportFrequency or if within 70% of max memory
                String message = "Process Memory Utilization: " + (long) memory + " MB, Max:"+ (long) maxMemory;
                if (reportFrequencyCounter % reportFrequency == 0 || memory / maxMemory > .70) {
                    log.info(message);
                } else {
                    log.debug(message);
                }
                
            } catch (IllegalUseOfAttribute e) {
                log.warn("caught exception in SystemMetrics.run", e);
            }
        } catch (DispatchNotConnectedException e) {
            log.debug("dispatch is down right now, try again later");
        } catch (Exception e) {
            log.error("caught exception in run()", e);
        }
    }

    /**
     * Total the elapsed CPU time from all threads. This is a bit tricky since some threads have
     * ended and new ones have begun.
     * 
     * Walk through the process's threads.
     * 
     * Any old threads, the previous CPU time is subtracted from the current CPU time and added to
     * the total.
     * 
     * Any new threads, the current CPU time and added to the total.
     * 
     * Any threads that are gone are not available, so C'est la vie
     * 
     * After calculating the elapsed CPU time, we calculate the elapsed wall time. Then divide to
     * get CPU load. We also divide by the number of processors to get average per processor.
     * 
     * To confuse things even more, CPU time is reported in nano seconds, wall time in milliseconds
     * so we convert everything to microseconds.
     * 
     * @return process load average
     */
    private double calculateLoadAverage() {
        Map<Long, Long> newThreadTimes = new HashMap<Long, Long>();

        long elapsedThreadsCPUTime = 0;

        // Walk the threads
        for (long thread : threadMXBean.getAllThreadIds()) {
            // get CPU time in microseconds
            long threadCpuTime = threadMXBean.getThreadCpuTime(thread) / 1000;

            // Get total of elapsed CPU times from each thread
            if (lastThreadTimes.containsKey(thread)) { 
                // Old thread
                log.debug("existing thread =" + thread + ", cpuTime=" + threadCpuTime +
                          ", oldCPUTime=" + lastThreadTimes.get(thread) + ", elapsed="
                          + (threadCpuTime - lastThreadTimes.get(thread)));

                elapsedThreadsCPUTime += threadCpuTime - lastThreadTimes.get(thread);
                newThreadTimes.put(thread, threadCpuTime);
            } else { 
                // New thread
                log.debug("new thread =" + thread + ", cpuTime=" + threadCpuTime);

                elapsedThreadsCPUTime += threadCpuTime;
                newThreadTimes.put(thread, threadCpuTime);
            }
        }

        // replace old times with new for the next cycle
        lastThreadTimes = newThreadTimes;

        // Calculate wall time
        Long elapsedWallTime = Instant.now().minus(lastSampleTime.getMillis()).getMillis() * 1000;
        lastSampleTime = Instant.now();

        double elapsedPerProcessor = ((double) elapsedThreadsCPUTime) / processors;

        // Set the process (JVM) load average
        loadAverage.set((elapsedPerProcessor / elapsedWallTime) * 100);

        log.debug("elapsedCPUTimePerProcessor=" + elapsedThreadsCPUTime + ", elapsedWallTime="
                  + elapsedWallTime + ", loadAverage=" + loadAverage);

        return loadAverage.get();
    }
    
    public abstract void insertPointData(Attribute attribute, double value);
    
}
