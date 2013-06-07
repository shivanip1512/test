package com.cannontech.messaging.serialization.thrift.util;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.thrift.TBase;

import com.cannontech.messaging.serialization.SerializationResult;
import com.cannontech.messaging.serialization.thrift.ThriftTypeConverter;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.GenericMessage;

public class ThriftConverterHelper {

    private final ThriftMessageFactory messageFactory;

    public ThriftConverterHelper(ThriftMessageFactory messageFactory) {
        this.messageFactory = messageFactory;
    }

    public <T, TE extends TBase<TE, ?>> TE convertToEntity(T msg, Class<TE> clazz) {
        try {
            @SuppressWarnings("unchecked")
            ThriftTypeConverter<T, TE> converter = messageFactory.findConverter((Class<T>) msg.getClass());
            return converter.convertToThriftEntity(messageFactory, msg);
        }
        catch (Exception e) {
            // TODO Throw ?
            return null;
        }
    }

    public <T, TE extends TBase<TE, ?>> T convertToMessage(TE entity, Class<T> clazz) {
        ThriftTypeConverter<T, TE> converter = messageFactory.findConverter(clazz);

        if (converter == null) {
            // TODO Should we throw ?
            return null;
        }
        return converter.convertToMessage(messageFactory, entity);
    }

    public Object convertFromGeneric(GenericMessage entity) {
        return messageFactory.decodeMessage(entity.get_messageType(), entity.get_payload());
    }
    
    public <T> GenericMessage convertToGeneric(T msg) {
        if (msg == null) {
            return null;
        }

        SerializationResult result = messageFactory.encodeMessage(msg);
        return new GenericMessage(result.getMessageType(), ByteBuffer.wrap(result.getPayload()));
    }

    public <T, TE extends TBase<TE, ?>> List<TE> convertToEntityList(List<T> objList, Class<TE> clazz) {
        List<TE> resultList = new ArrayList<TE>();

        try {
            if (objList != null && objList.size() == 0) {
                @SuppressWarnings("unchecked")
                Class<T> msgClass = (Class<T>) objList.get(0).getClass();
                ThriftTypeConverter<T, TE> converter = messageFactory.findConverter(msgClass);

                if (converter != null) {
                    for (T msg : objList) {
                        resultList.add(converter.convertToThriftEntity(messageFactory, msg));
                    }
                }
            }
        }
        catch (Exception e) {}

        return resultList;
    }

    public <T, TE extends TBase<TE, ?>> List<T> convertToMessageList(List<TE> entityList, Class<T> clazz) {
        ArrayList<T> resultList = new ArrayList<T>();
        convertToMessageCollection(entityList, clazz, resultList);
        return resultList;
    }

    public <T, TE extends TBase<TE, ?>> Vector<T> convertToMessageVector(List<TE> entityList, Class<T> clazz) {
        Vector<T> resultList = new Vector<T>();
        convertToMessageCollection(entityList, clazz, resultList);
        return resultList;
    }

    private <T, TE extends TBase<TE, ?>> void convertToMessageCollection(List<TE> entityList, Class<T> clazz,
                                                                         List<T> resultList) {

        if (entityList != null && entityList.size() == 0) {

            ThriftTypeConverter<T, TE> converter = messageFactory.findConverter(clazz);

            if (converter != null) {
                for (TE entity : entityList) {
                    resultList.add(converter.convertToMessage(messageFactory, entity));
                }
            }
        }
    }
}
