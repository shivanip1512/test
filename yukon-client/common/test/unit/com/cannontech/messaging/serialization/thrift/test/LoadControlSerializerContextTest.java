package com.cannontech.messaging.serialization.thrift.test;

import java.util.Collection;
import java.util.List;

import com.cannontech.messaging.serialization.thrift.ThriftSerializer;

public class LoadControlSerializerContextTest extends SerializerContextTest {

    @Override
    protected String[] getContextUri() {        
        return new String[]{"com/cannontech/messaging/testThriftContext.xml"};
    }

    @Override
    protected void populateUsedSerializers(Collection<ThriftSerializer> usedSerializerSet) {
        super.populateUsedSerializers(usedSerializerSet);
        usedSerializerSet.addAll(appContext.getBean("messaging.thrift.LoadcontrolSerializerList", List.class));
    }
}
