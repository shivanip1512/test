package com.cannontech.core.dynamic.impl;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.cannontech.core.dynamic.PointDataListener;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.dynamic.MockDispatchConnection;
import com.cannontech.message.dispatch.message.PointData;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

public class AsyncDynamicDataSourceImplTest {
    private AsyncDynamicDataSourceImpl impl;
    private MockDispatchConnection dispatchConnection;
    @Before
    public void setUp() throws Exception {
        impl = new AsyncDynamicDataSourceImpl();
        DispatchProxy dispatchProxy = new DispatchProxy();
        dispatchConnection = new MockDispatchConnection();
        dispatchProxy.setDispatchConnection(dispatchConnection);
        impl.setDispatchProxy(dispatchProxy);
    }

    @Test
    public void testRegisterForPointData() {
        PointDataListenerCounter listener1 = new PointDataListenerCounter();
        PointDataListenerCounter listener2 = new PointDataListenerCounter();
        
        impl.registerForPointData(listener1, ImmutableSet.of(1,2,3,4));
        assertEquals(1, dispatchConnection.messagesWritten.size());
        impl.registerForPointData(listener2, ImmutableSet.of(3,4,5,6));
        assertEquals(2, dispatchConnection.messagesWritten.size());
        
        // send point listener 1 is registered for
        impl.handlePointData(createPointData(1));
        assertEquals(1, listener1.list.size());
        assertEquals(0, listener2.list.size());

        // send point listener 2 is registered for
        impl.handlePointData(createPointData(6));
        assertEquals(1, listener1.list.size());
        assertEquals(1, listener2.list.size());
        
        // send point both listeners are registered for
        impl.handlePointData(createPointData(3));
        assertEquals(2, listener1.list.size());
        assertEquals(2, listener2.list.size());
        
        // unregister listener 1 for point 1
        impl.unRegisterForPointData(listener1, ImmutableSet.of(1));
        
        // send point listener 1 was registered for
        impl.handlePointData(createPointData(1));
        assertEquals(2, listener1.list.size());
        assertEquals(2, listener2.list.size());
        
        // completely unregister listener 1
        impl.unRegisterForPointData(listener1);
        
        // send point listener 1 was registered for
        impl.handlePointData(createPointData(2));
        assertEquals(2, listener1.list.size());
        assertEquals(2, listener2.list.size());
        
        // send point both listeners were registered for
        impl.handlePointData(createPointData(3));
        assertEquals(2, listener1.list.size());
        assertEquals(3, listener2.list.size());
        
        // send point listener 2 was registered for
        impl.handlePointData(createPointData(5));
        assertEquals(2, listener1.list.size());
        assertEquals(4, listener2.list.size());
        
        // reregister listener 1 for stuff listener 2 is already on
        impl.registerForPointData(listener1, ImmutableSet.of(4,5));
        // dispatch should NOT send a new registration
        assertEquals(2, dispatchConnection.messagesWritten.size());
        
        // send point no one is registered for
        impl.handlePointData(createPointData(99));
        assertEquals(2, listener1.list.size());
        assertEquals(4, listener2.list.size());
        

    }
    
    public static PointData createPointData(int id) {
        PointData data = new PointData();
        data.setId(id);
        return data;
    }

    private class PointDataListenerCounter implements PointDataListener {
        public List<PointValueQualityHolder> list = Lists.newArrayList();
        @Override
        public void pointDataReceived(PointValueQualityHolder pointData) {
            list.add(pointData);
        }
        
    }

}
