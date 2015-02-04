package com.cannontech.multispeak.client;

import org.springframework.ws.context.MessageContext;

/**
 * This class is used to hold the MessageContext.
 */
public final class MessageContextHolder {

    private static ThreadLocal<MessageContext> threadLocal =

    new ThreadLocal<MessageContext>() {
        @Override
        protected MessageContext initialValue() {
            return null;
        }

    };

    private MessageContextHolder() {
    }

    public static MessageContext getMessageContext() {
        return threadLocal.get();
    }

    public static void setMessageContext(MessageContext context) {
        threadLocal.set(context);
    }

    public static void removeMessageContext() {
        threadLocal.remove();
    }

}