package com.cannontech.web.updater.point;

import static org.easymock.EasyMock.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.easymock.IAnswer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointDataListener;
import com.cannontech.message.dispatch.message.PointData;

public class PointUpdateBackingServiceTest {

    private final PointUpdateBackingService impl = new PointUpdateBackingService();
    final Set<Integer> unregisteredPoints = new HashSet<>();
    private final int cacheMaxSize = 5000;

    @Before
    public void setup() {
        unregisteredPoints.clear();
        AsyncDynamicDataSource asyncDataSourceMock = createNiceMock(AsyncDynamicDataSource.class);
        asyncDataSourceMock.unRegisterForPointData(anyObject(PointDataListener.class), anyObject(Set.class));
        expectLastCall().andAnswer(new IAnswer<Object>() {
            @Override
            public Object answer() throws Throwable {
                unregisteredPoints.addAll(((Collection) getCurrentArguments()[1]));
                return null;
            }
        }).anyTimes();

        replay(asyncDataSourceMock);
        ReflectionTestUtils.setField(impl, "asyncDataSource", asyncDataSourceMock);
    }

    @Test
    public void test_pointDataReceived_pointsUnregisteredCorrectly() throws InterruptedException {
        int numberPoints = 10_000;
        for (int i = 0; i < numberPoints; i++) {
            PointData pointData = new PointData();
            pointData.setId(i);
            impl.pointDataReceived(pointData);
        }

        int numberShouldHaveUnregistered = (numberPoints - cacheMaxSize);
        int numberUnregistered = unregisteredPoints.size();

        Assert.assertTrue(
            "PointUpdateBackingService failed to unregister the points it removed from the cache. Should have unregistered "
                + numberShouldHaveUnregistered + " but was " + numberUnregistered + ".",
            numberUnregistered >= numberShouldHaveUnregistered);
    }

    @Test
    public void test_pointDataReceived_concurrency() throws InterruptedException {

        final List<Exception> errors = Collections.synchronizedList(new ArrayList<Exception>());
        startAndWait(5, new Runnable() {
            @Override
            public void run() {
                try {
                    for (int e = 0; e < 10_000; e++) {
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
