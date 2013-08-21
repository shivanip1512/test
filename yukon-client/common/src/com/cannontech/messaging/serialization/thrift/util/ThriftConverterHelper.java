package com.cannontech.messaging.serialization.thrift.util;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.thrift.TBase;

import com.cannontech.messaging.serialization.SerializationException;
import com.cannontech.messaging.serialization.SerializationResult;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.ThriftTypeConverter;
import com.cannontech.messaging.serialization.thrift.generated.GenericMessage;

public class ThriftConverterHelper {

    private final ThriftMessageFactory messageFactory;

    public ThriftConverterHelper(ThriftMessageFactory messageFactory) {
        this.messageFactory = messageFactory;
    }

    @SuppressWarnings("unchecked")
    public <T, TE extends TBase<TE, ?>> TE convertToEntity(T msg, Class<TE> clazz) {
        ThriftTypeConverter<T, TE> converter = messageFactory.getConverter((Class<T>) msg.getClass());
        return converter.convertToThriftEntity(messageFactory, msg);
    }

    public <T, TE extends TBase<TE, ?>> T convertToMessage(TE entity, Class<T> clazz) {
        ThriftTypeConverter<T, TE> converter = messageFactory.getConverter(clazz);

        return converter.convertToMessage(messageFactory, entity);
    }

    public Object convertFromGeneric(GenericMessage entity) {
        return convertFromGeneric(entity, Object.class);
    }
    
    public <T> T convertFromGeneric(GenericMessage entity, Class<T> clazz) {
        SerializationResult result = messageFactory.decodeMessage(entity.get_messageType(), entity.get_payload());

        if (!result.isValid()) {
            throw new SerializationException("Error while deserializing from nested GenericMessage of type '" +
                                             entity.get_messageType() + "'", result.getException());
        }

        return clazz.cast(result.getMessageObject());
    }

    public <T> GenericMessage convertToGeneric(T msg) {
        if (msg == null) {
            throw new SerializationException("Object to serialize should not be null");
        }

        SerializationResult result = messageFactory.encodeMessage(msg);

        if (!result.isValid()) {
            throw new SerializationException("Error while serializing to nested GenericMessage of class '" +
                                             result.getMessageClass() + "'", result.getException());
        }

        return new GenericMessage(result.getMessageType(), ByteBuffer.wrap(result.getMessagePayload()));
    }

    public <T> List<GenericMessage> convertToGenericList(List<T> msgList) {
        List<GenericMessage> genericList = new ArrayList<GenericMessage>(msgList.size());

        for (T msg : msgList) {
            genericList.add(convertToGeneric(msg));
        }

        return genericList;
    }

    public <T, TE extends TBase<TE, ?>> List<TE> convertToEntityList(List<T> objList, Class<TE> clazz) {
        List<TE> resultList = new ArrayList<TE>();

        if (objList != null && objList.size() != 0) {
            @SuppressWarnings("unchecked") Class<T> msgClass = (Class<T>) objList.get(0).getClass();
            ThriftTypeConverter<T, TE> converter = messageFactory.getConverter(msgClass);

            for (T msg : objList) {
                resultList.add(converter.convertToThriftEntity(messageFactory, msg));
            }
        }

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
        if (entityList != null && entityList.size() != 0) {

            for (TE entity : entityList) {

                if (entity instanceof GenericMessage) {
                    resultList.add(convertFromGeneric((GenericMessage) entity, clazz));
                }
                else {
                    ThriftTypeConverter<T, TE> converter = messageFactory.getConverter(clazz);
                    resultList.add(converter.convertToMessage(messageFactory, entity));
                }
            }
        }
    }
}
