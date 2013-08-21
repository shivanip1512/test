package com.cannontech.event;

public interface Event<T extends EventHandler> {

    /**
     * Adds a handler to the list of registered handlers. If the this handler already exists it is not added twice.
     * Passing null to this method has no effect, if you need to unregister a handler use
     * {@link #unregisterHandler(EventHandler) unregisterHandler}
     */
    void registerHandler(T handler);

    /**
     * Removes a registered handler for the registered handler list
     * @param handler The handler to unregister
     */
    void unregisterHandler(T handler);
}
