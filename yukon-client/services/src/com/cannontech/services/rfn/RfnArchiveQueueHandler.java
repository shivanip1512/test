package com.cannontech.services.rfn;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;

public class RfnArchiveQueueHandler {
    private static final Logger log = YukonLogManager.getLogger(RfnArchiveQueueHandler.class);
    /*
     * This queue contains the reference to the processor and the object to be processed.
     * When thread takes and object from the queue, it tells the processor to process it.
     */
    private BlockingQueue<Pair<RfnArchiveProcessor, Object>> queue = new LinkedBlockingQueue<>();
    @Autowired private ConfigurationSource configurationSource;
    
    @PostConstruct
    public void initialize() {
        int threadCount = configurationSource.getInteger("RFN_METER_DATA_WORKER_COUNT", 5);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        //create processors (default 5)
        IntStream.range(0, threadCount).forEach(i -> executorService.submit(new Processor(queue, i)));
    }
    
    /**
     * Adds message to queue
     */
    public void add(RfnArchiveProcessor processor, Object obj) {
        try {
            queue.put(Pair.of(processor, obj));
        } catch (InterruptedException e) {
            log.error("Failed to add processor {} entry {} to queue", processor.getClass().getSimpleName(), e);
        }
    }
    
    private class Processor extends Thread {
        private BlockingQueue<Pair<RfnArchiveProcessor, Object>> queue;
        Processor(BlockingQueue<Pair<RfnArchiveProcessor, Object>> queue, int count) {
            this.queue = queue;
            this.setName("Rfn Archive Cache Thread #"+ (count + 1));
        }

        @Override
        public void run() {
            while(true) {
                try {
                    Pair<RfnArchiveProcessor, Object> entry = queue.take();
                    log.debug("{} (remaining {}) processor {} processing {}", this.getName(), queue.size(),
                        entry.getKey().getClass().getSimpleName(), entry.getValue());
                    //Tells the processor to process the object, the thread name is passed for debugging
                    entry.getKey().process(entry.getValue(), this.getName());
                } catch (Exception e) {
                    log.error("Processor {}", this.getName(), e);
                }
            }
        }
    }
}
