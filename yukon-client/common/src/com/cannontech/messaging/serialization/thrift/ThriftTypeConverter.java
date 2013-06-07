package com.cannontech.messaging.serialization.thrift;

import org.apache.thrift.TBase;

public interface ThriftTypeConverter<T, TE extends TBase<TE, ?>> {

    public TE convertToThriftEntity(ThriftMessageFactory factory, T msg);

    public T convertToMessage(ThriftMessageFactory factory, TE entity);
}
