package com.cannontech.common.bulk.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import com.cannontech.common.bulk.CollectingBulkProcessorCallback;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.bulk.processor.Processor;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.ScheduledExecutor;

/**
 * Test class for OneAtATimeProcessor
 */
public class OneAtATimeProcessorTest extends TestCase {

    private OneAtATimeProcessor oneAtATimeProcessor = null;
    private ObjectMapper<String, String> mapper = null;
    private Processor<String> processor = null;

    @Override
    protected void setUp() throws Exception {

        oneAtATimeProcessor = new OneAtATimeProcessor();
        oneAtATimeProcessor.setScheduledExecutor(new ScheduledExecutor() {

            public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
                return null;
            }

            public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
                return null;
            }

            public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay,
                    long period, TimeUnit unit) {
                return null;
            }

            public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay,
                    long delay, TimeUnit unit) {
                return null;
            }

            public void execute(Runnable command) {
                command.run();
            }
        });

        mapper = new ObjectMapper<String, String>() {

            private int callCount = 0;

            public String map(String from) throws ObjectMappingException {

                if (callCount++ == 1) {
                    throw new ObjectMappingException("mapping error");
                }
                return from;
            }
        };

        processor = new Processor<String>() {

            private int callCount = 0;

            public void process(String object) throws ProcessingException {
                if (callCount++ == 0) {
                    throw new ProcessingException("processing error");
                }
            }

            public void process(Collection<String> objectCollection) throws ProcessingException {
                throw new UnsupportedOperationException("This method should not be called by the OneAtATime processor");
            }
        };

    }

    public void testBackgroundBulkProcess() {

        // Test one at a time processor with functioning mapper and processor
        List<String> testList = new ArrayList<String>();
        testList.add("one");
        testList.add("two");
        testList.add("three");

        CollectingBulkProcessorCallback callback = new CollectingBulkProcessorCallback();

        oneAtATimeProcessor.backgroundBulkProcess(testList.iterator(), mapper, processor, callback);

        assertEquals("Didn't process correct number of items",
                     3,
                     callback.getTotalObjectsProcessedCount());

        assertEquals("Didn't process the correct number items successfully",
                     1,
                     callback.getSuccessfulObjectsProcessedCount());

        assertEquals("Didn't process the correct number items unsuccessfully ",
                     2,
                     callback.getUnsuccessfulObjectsProcessedCount());

        assertEquals("Mapping exception list not populated", 1, callback.getMappingExceptionList()
                                                                        .size());

        assertEquals("Processing exception list not populated",
                     1,
                     callback.getProcessingExceptionList().size());

        assertEquals("Processing exception list not populated",
                     1,
                     callback.getProcessingExceptionList().size());

        assertTrue("Should be complete", callback.isComplete());

        assertFalse("Should not have failed", callback.isProcessingFailed());

        // Test one at a time processor with functioning processor and failing
        // mapper
        ObjectMapper<String, String> failingMapper = new ObjectMapper<String, String>() {
            public String map(String from) throws ObjectMappingException {
                throw new RuntimeException("mapping error");
            }
        };

        callback = new CollectingBulkProcessorCallback();

        oneAtATimeProcessor.backgroundBulkProcess(testList.iterator(),
                                                  failingMapper,
                                                  processor,
                                                  callback);

        assertNotNull("There should be a fail exception", callback.getFailedException());

        assertTrue("Should be complete", callback.isComplete());

        assertTrue("Should have failed", callback.isProcessingFailed());

        // Test one at a time processor with functioning mapper and failing
        // processor
        Processor<String> failingProcessor = new Processor<String>() {
            public void process(String object) throws ProcessingException {
                throw new RuntimeException("processing error");
            }

            public void process(Collection<String> objectCollection) throws ProcessingException {
                throw new UnsupportedOperationException("This method should not be called by the OneAtATime processor");
            }
        };

        callback = new CollectingBulkProcessorCallback();

        oneAtATimeProcessor.backgroundBulkProcess(testList.iterator(),
                                                  mapper,
                                                  failingProcessor,
                                                  callback);

        assertNotNull("There should be a fail exception", callback.getFailedException());

        assertTrue("Should be complete", callback.isComplete());

        assertTrue("Should have failed", callback.isProcessingFailed());

    }

}
