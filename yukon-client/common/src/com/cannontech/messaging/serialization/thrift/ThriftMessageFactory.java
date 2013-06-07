package com.cannontech.messaging.serialization.thrift;

import java.util.List;

import org.apache.thrift.TBase;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TMemoryBuffer;
import org.apache.thrift.transport.TMemoryInputTransport;
import org.apache.thrift.transport.TTransport;

import com.cannontech.messaging.serialization.MessageFactory;
import com.cannontech.messaging.serialization.thrift.util.ThriftConverterHelper;

public class ThriftMessageFactory extends MessageFactory {

    private static TBinaryProtocol.Factory protocolFactory;

    private final ThriftConverterHelper converterHelper;

    static {
        protocolFactory = new TBinaryProtocol.Factory();
    }

    public ThriftMessageFactory() {
        this(null);
    }

    public ThriftMessageFactory(List<ThriftSerializer<?, ?>> serializerList) {
        super(serializerList);
        converterHelper = new ThriftConverterHelper(this);
    }

    protected static TProtocol createDefaultProtocol(TTransport transport) {
        return protocolFactory.getProtocol(transport);
    }

    public static TProtocol createDefaultProtocol(byte[] data) {
        return createDefaultProtocol(new TMemoryInputTransport(data));
    }

    protected static TMemoryBuffer createDefaultMemoryBufferTransport() {
        return new TMemoryBuffer(512);
    }

    @SuppressWarnings("unchecked")
    public <T, TE extends TBase<TE, ?>> ThriftTypeConverter<T, TE> findConverter(Class<T> targetClass) {
        return (ThriftTypeConverter<T, TE>) byMessageClassSerializerMap.get(targetClass);
    }

    public ThriftConverterHelper getConverterHelper() {
        return converterHelper;
    }
}
