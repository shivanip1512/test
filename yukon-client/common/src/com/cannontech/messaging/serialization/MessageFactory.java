package com.cannontech.messaging.serialization;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MessageFactory {

    protected Map<String, MessageSerializer<?>> byMessageTypeSerializerMap;
    protected Map<Class<?>, MessageSerializer<?>> byMessageClassSerializerMap;
    private String name;

    public MessageFactory() {
        this(null);
    }

    public MessageFactory(Collection<? extends MessageSerializer<?>> serializerList) {
        byMessageTypeSerializerMap = new HashMap<String, MessageSerializer<?>>();
        byMessageClassSerializerMap = new HashMap<Class<?>, MessageSerializer<?>>();
        registerSerializers(serializerList);
        this.name = "";
    }

    public void registerSerializers(Collection<? extends MessageSerializer<?>> serializerList) {
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

    public SerializationResult decodeMessage(String messageType, byte[] msgData) {
        SerializationResult result = new SerializationResult();
        result.setMessageType(messageType);
        result.setMessagePayload(msgData);

        MessageSerializer<?> serializer = byMessageTypeSerializerMap.get(messageType);

        if (serializer == null) {
            result
                .setException(new SerializationException("No serializer found for message type '" + messageType + "'"));
            return result;
        }

        try {
            Object msg = serializer.deserialize(this, msgData);
            
            if (msg != null) {
                result.setMessageObject(msg);
                result.setMessageClass(msg.getClass());
                result.setValid(true);
            }
        }
        catch (SerializationException e) {
            result.setException(e);
        }
        catch (Exception e) {
            result.setException(new SerializationException("Error while deserializing message, type=" +
                                                           result.getMessageType() + " ,class=" +
                                                           result.getMessageClass(), e));
        }

        return result;
    }

    public <T> SerializationResult encodeMessage(T message) {
        SerializationResult result = new SerializationResult();

        if (message == null) {
            return result;
        }

        result.setMessageObject(message);
        result.setMessageClass(message.getClass());

        try {
            @SuppressWarnings("unchecked")
            MessageSerializer<T> serializer = (MessageSerializer<T>) findSerializer(message.getClass());

            if (serializer == null) {
                result.setException(new SerializationException("Could not find a suitable serializer for type' " +
                                                               message.getClass() + "'"));
            }
            else {
                result.setMessageType(serializer.getMessageType());
                result.setMessagePayload(serializer.serialize(this, message));
                result.setValid(result.getMessagePayload() != null && result.getException() == null);
            }
        }
        catch (SerializationException se) {
            result.setException(se);
        }
        catch (Exception e) {
            result.setException(new SerializationException("Error while serializing message, type=" +
                                                           result.getMessageType() + " ,class=" +
                                                           result.getMessageClass(), e));
        }

        return result;
    }

    public MessageSerializer<?> findSerializer(Class<?> targetClass) {
        return byMessageClassSerializerMap.get(targetClass);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
