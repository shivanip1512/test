package com.cannontech.messaging.serialization.thrift.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cannontech.messaging.serialization.thrift.ThriftSerializer;

public class SerializerContextTest extends ContextAwareTestBase{
    
    @Override
    protected String[] getContextUri() {        
        return new String[]{"com/cannontech/messaging/testThriftConnectionContext.xml"};
    }
    
    @Test
    public void allSerializersAreUsed() {        
        Collection<ThriftSerializer> allSerializerList =
            new ArrayList<>(appContext.getBeansOfType(ThriftSerializer.class).values());

        Collection<ThriftSerializer> usedSerializers = new HashSet<>();

        populateUsedSerializers(usedSerializers);
        
        for (ThriftSerializer serializer : allSerializerList) {
            if (!usedSerializers.remove(serializer)) {
                Assert.fail("Serializer'" + serializer.getClass() + "' is never used in a MessageFactory");
            }
        }
    }

    protected void populateUsedSerializers(Collection<ThriftSerializer> usedSerializerSet) {
        usedSerializerSet.addAll(appContext.getBean("messaging.thrift.CommonSerializerList", List.class));
        usedSerializerSet.addAll(appContext.getBean("messaging.thrift.CapcontrolSerializerList", List.class));
        usedSerializerSet.addAll(appContext.getBean("messaging.thrift.DispatchSerializerList", List.class));
        usedSerializerSet.addAll(appContext.getBean("messaging.thrift.MacSerializerList", List.class));
        usedSerializerSet.addAll(appContext.getBean("messaging.thrift.NotifSerializerList", List.class));
        usedSerializerSet.addAll(appContext.getBean("messaging.thrift.PorterSerializerList", List.class));
    }
    
}
