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

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.core.dao.SimplePointAccessDao;
import com.cannontech.database.data.lite.LitePoint;
import com.google.common.util.concurrent.AtomicDouble;

/**
 * Spring Bean that monitors the JVM CPU usage and reports it as Point data.
 */
@ManagedResource
public class SystemMetricsImpl extends Observable implements Runnable, SystemMetrics {

    @Autowired private AttributeService attributeService;
    @Autowired private SimplePointAccessDao pointAccessDao;

    private Logger log = YukonLogManager.getLogger(this.getClass());

    private int paoId;
    private PaoIdentifier paoIdentifier;
    private BuiltInAttribute attribute;

    class SystemMetricsThreadFactory implements ThreadFactory {
        public Thread newThread(Runnable r) {
            return new Thread(r, "SystemMetrics");
        }
    }

    private final ScheduledExecutorService scheduler = Executors
        .newScheduledThreadPool(1, new SystemMetricsThreadFactory());
    private final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
    private final OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();

    private ScheduledFuture<?> task;
    private LitePoint point;

    private Instant lastSampleTime;
    private int processors;

    private Map<Long, Long> lastThreadTimes = new HashMap<Long, Long>();
    private AtomicDouble loadAverage = new AtomicDouble(0.0);

    /**
     * Spring (default) constructor.
     * 
     * Initialize the MXBeans (which provide load information).
     * Scan through the threads and record the current CPU time usage.
     * Save the start time for elapsed time calculation.
     * Schedule the run() method every 60 seconds.
     */
    public SystemMetricsImpl() {
        log.debug("SystemMetricsImpl.SystemMetricsImpl()");

        threadMXBean.setThreadCpuTimeEnabled(true);
        processors = osMXBean.getAvailableProcessors();

        for (long thread : threadMXBean.getAllThreadIds()) {
            long threadCpuTime = threadMXBean.getThreadCpuTime(thread) / 1000;
            lastThreadTimes.put(thread, threadCpuTime);
        }

        lastSampleTime = Instant.now();
        task = scheduler.scheduleAtFixedRate(this, 60, 60, TimeUnit.SECONDS);
    }

    /**
     * Setter that takes the PaoID and sets up the point id.
     * 
     * @param paoId PaoId for Load Statistics
     */
    @Override
    public void setPaoId(int paoId) {
        log.debug("setPaoId(" + paoId + ")");
        this.paoId = paoId;
    }

    /**
     * Setter that takes the attribute Id.
     * 
     * @param attribute attribute Id.
     */
    @Override
    public void setAttribute(BuiltInAttribute attribute) {
        log.debug("setAttribute(" + attribute + ")");
        this.attribute = attribute;
    }

    /**
     * After everything is set, we need to do some one time lookups.
     */
    public void init() {
        log.debug("SystemMetricsImpl.init()");
        paoIdentifier = new PaoIdentifier(paoId, PaoType.SYSTEM);
        point = attributeService.getPointForAttribute(paoIdentifier, attribute);
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
        log.debug("SystemMetricsImpl.run()");

        try {
            double loadAverage = calculateLoadAverage();
            pointAccessDao.setPointValue(point, Instant.now(), loadAverage);
        } catch (IllegalUseOfAttribute e) {
            log.warn("caught exception in SystemMetrics.run", e);
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
}
