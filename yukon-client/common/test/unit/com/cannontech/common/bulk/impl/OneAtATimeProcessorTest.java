package com.cannontech.common.bulk.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cannontech.common.bulk.callbackResult.CollectingBulkProcessorCallback;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.bulk.processor.Processor;
import com.cannontech.common.util.ObjectMapper;

/**
 * Test class for OneAtATimeProcessor
 */
public class OneAtATimeProcessorTest {

    private OneAtATimeProcessor oneAtATimeProcessor = null;
    private ObjectMapper<String, String> mapper = null;
    private Processor<String> processor = null;

    @BeforeEach
    protected void setUp() throws Exception {

        oneAtATimeProcessor = new OneAtATimeProcessor();
        oneAtATimeProcessor.setExecutor(new Executor() {

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
                    throw new ProcessingException("processing error", "processingError");
                }
            }

            public void process(Collection<String> objectCollection) throws ProcessingException {
                throw new UnsupportedOperationException("This method should not be called by the OneAtATime processor");
            }
        };

    }

    @Test
    public void testBackgroundBulkProcess() {

        // Test one at a time processor with functioning mapper and processor
        List<String> testList = new ArrayList<String>();
        testList.add("one");
        testList.add("two");
        testList.add("three");

        CollectingBulkProcessorCallback<String, String> holder;
        {
            CollectingBulkProcessorCallback<String, String> callback = new CollectingBulkProcessorCallback<String, String>();

            oneAtATimeProcessor.backgroundBulkProcess(testList.iterator(), mapper, processor, callback);
            holder = callback;
        }

        assertEquals(3, holder.getProcessingExceptionCount() + holder.getSuccessCount(), 
                "Didn't process correct number of items");

        assertEquals(1, holder.getSuccessCount(),
                     "Didn't process the correct number items successfully");

        assertEquals(2, holder.getProcessingExceptionCount(),
                "Didn't process the correct number items unsuccessfully ");

        assertEquals(2, holder.getProcessingExceptionList().size(), 
                "Processing exception list not populated");

        assertTrue(holder.isComplete(), "Should be complete");

        assertTrue(holder.isSuccessfull(), "Should have succeeded");
        assertFalse(holder.isProcessingFailed(), "Should not have failed");

        // Test one at a time processor with functioning processor and failing
        // mapper
        ObjectMapper<String, String> failingMapper = new ObjectMapper<String, String>() {
            public String map(String from) throws ObjectMappingException {
                throw new RuntimeException("mapping error");
            }
        };

        {
            CollectingBulkProcessorCallback<String, String> callback = new CollectingBulkProcessorCallback<String, String>();

            oneAtATimeProcessor.backgroundBulkProcess(testList.iterator(),
                                                      failingMapper,
                                                      processor,
                                                      callback);
            holder = callback;
        }

        assertNotNull(holder.getFailedException(), "There should be a fail exception");

        assertTrue(holder.isComplete(), "Should be complete");

        assertTrue(holder.isProcessingFailed(), "Should have failed");
        assertFalse(holder.isSuccessfull(), "Should have not succeeded");

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

        {
            CollectingBulkProcessorCallback<String, String> callback = new CollectingBulkProcessorCallback<String, String>();

            oneAtATimeProcessor.backgroundBulkProcess(testList.iterator(),
                                                      mapper,
                                                      failingProcessor,
                                                      callback);
            holder = callback;
        }

        assertNotNull(holder.getFailedException(), "There should be a fail exception");

        assertTrue(holder.isComplete(), "Should be complete");

        assertTrue(holder.isProcessingFailed(), "Should have failed");
        assertFalse(holder.isSuccessfull(), "Should not have succeeded");

    }

}
