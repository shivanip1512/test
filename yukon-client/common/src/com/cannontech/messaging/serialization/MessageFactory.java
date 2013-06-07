package com.cannontech.messaging.serialization;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageFactory {

    protected Map<String, MessageSerializer<?>> byMessageTypeSerializerMap;
    protected Map<Class<?>, MessageSerializer<?>> byMessageClassSerializerMap;
    private String name;

    public MessageFactory() {
        this(null);
    }

    public MessageFactory(List<? extends MessageSerializer<?>> serializerList) {
        byMessageTypeSerializerMap = new HashMap<String, MessageSerializer<?>>();
        byMessageClassSerializerMap = new HashMap<Class<?>, MessageSerializer<?>>();
        registerSerializers(serializerList);
        this.name = "";
    }

    public void registerSerializers(List<? extends MessageSerializer<?>> serializerList) {
        if (serializerList != null) {
            for (MessageSerializer<?> serializer : serializerList) {
                registerSerializer(serializer);
            }
        }
    }

    public void registerSerializer(MessageSerializer<?> serializer) {
        byMessageClassSerializerMap.put(serializer.getTargetMessageClass(), serializer);
        byMessageTypeSerializerMap.put(serializer.getMessageType(), serializer);
    }

    public String resolveMessageType(Class<?> clazz) {
        MessageSerializer<?> serializer = byMessageClassSerializerMap.get(clazz);

        if (serializer != null) {
            return serializer.getMessageType();
        }

        return null;
    }

    public Object decodeMessage(String messageType, byte[] msgData) {
        Object result = null;
        MessageSerializer<?> serializer = byMessageTypeSerializerMap.get(messageType);

        if (serializer != null) {
            result = serializer.deserialize(this, msgData);
        }

        return result;
    }

    public <T> SerializationResult encodeMessage(T message) {
        SerializationResult result = new SerializationResult();

        try {
            @SuppressWarnings("unchecked")
            MessageSerializer<T> serializer = (MessageSerializer<T>) findSerializer(message.getClass());

            if (serializer == null) {
                result.setException(new SerializationException("Could not find a suitable serializer for type' " +
                                                               message.getClass() + "'"));
            }
            else {
                result.setPayload(serializer.serialize(this, message));
                result.setMessageType(serializer.getMessageType());
                result.setValid(result.getPayload() != null);
            }
        }
        catch (SerializationException se) {
            result.setException(se);
        }
        catch (Exception e) {
            result.setException(new SerializationException(e));
        }

        return result;
    }

    public MessageSerializer<?> findSerializer(Class<?> targetClass) {
        return byMessageClassSerializerMap.get(targetClass);
    }

    public MessageSerializer<?> findSerializer(String messageType) {
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
