package com.cannontech.messaging.serialization.thrift;

import java.util.Collection;

import org.apache.thrift.TBase;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TMemoryInputTransport;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import com.cannontech.messaging.connection.transport.TransportException;
import com.cannontech.messaging.serialization.MessageFactory;
import com.cannontech.messaging.serialization.SerializationException;
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

    public ThriftMessageFactory(Collection<ThriftSerializer<?, ?>> collection) {
        super(collection);
        converterHelper = new ThriftConverterHelper(this);
    }

    protected static TProtocol createDefaultProtocol(TTransport transport) {
        return protocolFactory.getProtocol(transport);
    }

    public static TProtocol createDefaultProtocol(byte[] data) {
        try {
            return createDefaultProtocol(new TMemoryInputTransport(data));
        } catch (TTransportException e) {
            throw new TransportException(e);
        }
    }

    protected static YukonTMemoryBuffer createDefaultMemoryBufferTransport() {
        return new YukonTMemoryBuffer(512);
    }

    @SuppressWarnings("unchecked")
    public <T, TE extends TBase<TE, ?>> ThriftTypeConverter<T, TE> getConverter(Class<T> targetClass) {
        ThriftTypeConverter<T, TE> converter =
            (ThriftTypeConverter<T, TE>) byMessageClassSerializerMap.get(targetClass);

        if (converter == null) {
            throw new SerializationException("Thrift converter for '" + targetClass + "' not found");
        }

        return converter;
    }

    public ThriftConverterHelper getConverterHelper() {
        return converterHelper;
    }
}
