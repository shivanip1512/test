package com.cannontech.web.updater.point;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.message.dispatch.message.PointData;

public class PointUpdateBackingServiceTest {

    private PointUpdateBackingService impl = new PointUpdateBackingService();

    @Before
    public void setup() {
        AsyncDynamicDataSource asyncDataSourceMock = 
                EasyMock.createNiceMock(AsyncDynamicDataSource.class);
        EasyMock.replay(asyncDataSourceMock);
        ReflectionTestUtils.setField(impl, "asyncDataSource", asyncDataSourceMock);
    }

    @Test
    public void test_pointDataReceived_concurrency() throws InterruptedException {
        for(int i=0; i<15000; i++) {
            PointData pointData = new PointData();
            pointData.setId(i);
            impl.pointDataReceived(pointData);
        }

        final List<Exception> errors = Collections.synchronizedList(new ArrayList<Exception>());
        startAndWait(5, new Runnable() {
            @Override
            public void run() {
                try {
                    for(int e=0; e<1000000; e++) {
                        PointData pointData = new PointData();
                        pointData.setId(e);
                        impl.pointDataReceived(pointData);
                    }
                } catch (Exception e) {
                    errors.add(e);
                }
            }
        });

        if(!errors.isEmpty()) {
            Assert.fail("Exception thrown in a thread: " + errors.get(0));
        }
    }

    private void startAndWait(int number, Runnable runnable) throws InterruptedException {
        Set<Thread> threads = new HashSet<>();
        for (int i=0; i<number; i++) {
            Thread thread = new Thread(runnable);
            threads.add(thread);
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join(5000);
        }
    }
}
