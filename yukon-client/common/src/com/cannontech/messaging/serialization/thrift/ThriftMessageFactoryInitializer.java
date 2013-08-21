package com.cannontech.messaging.serialization.thrift;

import java.util.List;

public class ThriftMessageFactoryInitializer {

    private ThriftMessageFactory messageFactory;

    private List<ThriftSerializer<?, ?>> serializerList;

    public void setMessageFactory(ThriftMessageFactory messageFactory) {
        this.messageFactory = messageFactory;
    }

    public void setSerializerList(List<ThriftSerializer<?, ?>> serializerList) {
        this.serializerList = serializerList;
    }

    void init() {
        for (ThriftSerializer<?, ?> serializer : serializerList) {
            messageFactory.registerSerializer(serializer);
        }
    }
}
