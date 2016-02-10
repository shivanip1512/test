package com.cannontech.event;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;

/**
 * Base implementation of an Event. Sub classes should implement {@link #notifyHandler(Object, Object, EventHandler)
 * notifyHandler} to complete the event notifying functionality. Event specialization goes in pair with the
 * corresponding EventHandler interface implementation. Example:
 * 
 * <pre>
 * <code>
 * 
 * public class MyEvent extends EventBase<Thread, String, MyEventHandler> {
 *     @Override
 *     protected void notifyHandler(Thread source, String message, MyEventHandler handler) {
 *         handler.onMyEvent(connection, state);
 *     }
 * }
 * 
 * public interface MyEventHandler extends EventHandler {
 *     void onMyEvent(Thread source, String message);
 * }
 * 
 * </code>
 * </pre>
 * 
 * This class is thread safe. When fired, an Event will notify each of the handlers that where registered at the time of
 * the {@code fire} Method call. During execution of the event handling process, handlers can be (un)registered without
 * affecting the ongoing handling process. Any modification of the registered Handlers list during the handling process
 * will only be effective on the next event firing.
 * @param <S> The type of the source of the event (the object type that fires the event)
 * @param <A> The type of the argument passed along when an Event is fired
 * @param <T> The type of handler that can register to and handle this Event.
 */
public abstract class EventBase<S, A, T extends EventHandler> implements Event<T> {

    // create a logger for instances of this class and its subclasses
    protected Logger logger = YukonLogManager.getLogger(this.getClass());

    private Set<T> handlers;

    protected EventBase() {
        handlers = new HashSet<T>(1);
    }

    /**
     * Fires the event. All handlers subscribed at the time of this call will be invoked with the given parameters.
     * @param eventSource The source of this event firing
     * @param eventArg The Value conveyed by this firing
     */
    public void fire(S eventSource, A eventArg) {
        // A copy of the handler collection is created in case any of one of
        // them tries to (un)registered during handlers invocation
        List<T> handlersCopy;
        synchronized (this) {
            handlersCopy = new ArrayList<T>(handlers);
        }

        for (T handler : handlersCopy) {
            try {
                notifyHandler(eventSource, eventArg, handler);
            }
            catch (Exception e) {
                logger.error(e);
            }
        }
    }

    /**
     * Notifies the handler that the event as been triggered. It is the responsibility of the implementing sub class to
     * notify the handler according to its interface defined by the generic Event Handler type T.(see
     * {@link #EventHandler EventHandler})
     * @param eventSrc The source of this event firing
     * @param eventArg The Value conveyed by this event firing.
     * @param handler A registered handler to notify
     */
    protected abstract void notifyHandler(S eventSrc, A eventArg, T handler);

    /**
     * Returns the public interface of this event. It is the interface that should should be shown to handler. This way
     * only the owner of this event can actually fire an event.
     * @return public (external) interface to show to handlers
     */
    public Event<T> getEventInterface() {
        return this;
    }

    @Override
    public synchronized void registerHandler(T handler) {
        if (handler != null) {
            handlers.add(handler);
        }
    }

    @Override
    public synchronized void unregisterHandler(T handler) {
        if (handler != null) {
            handlers.remove(handler);
        }
    }

    /**
     * Removes all registered handler for the registered handler list
     */
    public synchronized void clearHandlers() {
        handlers.clear();
    }
}
