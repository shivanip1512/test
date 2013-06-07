package com.cannontech.event;

public interface Event<T extends EventHandler> {

    void registerHandler(T handler);

    void unregisterHandler(T handler);

    boolean isUniqueHandlerEvent();

    int getHandlerCount();
}
