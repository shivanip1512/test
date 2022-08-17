package com.cannontech.common.bulk.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;

import junit.framework.TestCase;

import com.cannontech.common.bulk.callbackResult.CollectingBulkProcessorCallback;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.bulk.processor.Processor;
import com.cannontech.common.util.ObjectMapper;

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

        assertEquals("Didn't process correct number of items",
                     3,
                     holder.getProcessingExceptionCount() + holder.getSuccessCount());

        assertEquals("Didn't process the correct number items successfully",
                     1,
                     holder.getSuccessCount());

        assertEquals("Didn't process the correct number items unsuccessfully ",
                     2,
                     holder.getProcessingExceptionCount());

        assertEquals("Processing exception list not populated",
                     2,
                     holder.getProcessingExceptionList().size());

        assertTrue("Should be complete", holder.isComplete());

        assertTrue("Should have succeeded", holder.isSuccessfull());
        assertFalse("Should not have failed", holder.isProcessingFailed());

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

        assertNotNull("There should be a fail exception", holder.getFailedException());

        assertTrue("Should be complete", holder.isComplete());

        assertTrue("Should have failed", holder.isProcessingFailed());
        assertFalse("Should have not succeeded", holder.isSuccessfull());

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

        assertNotNull("There should be a fail exception", holder.getFailedException());

        assertTrue("Should be complete", holder.isComplete());

        assertTrue("Should have failed", holder.isProcessingFailed());
        assertFalse("Should not have succeeded", holder.isSuccessfull());

    }

}
