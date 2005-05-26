package com.cannontech.notif.voice;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.notif.voice.callstates.*;

import edu.emory.mathcs.backport.java.util.concurrent.*;
import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicInteger;

/**
 * This pool manages the current calls. It is created
 * with a Dialer to actually make the calls and a 
 * specific numberOfChannels. This class will manage up
 * to numberOfChannels threads to actually do the work
 * of making the calls. This number should match the number
 * of physical outgoing phone lines that are available.
 */
public class CallPool implements PropertyChangeListener {

    final protected ThreadPoolExecutor _threadPool;
    final protected Map _pendingCalls = new HashMap();
    final Timer _timer = new Timer();
    private Dialer _dialer;
    private HashMap _timerTasks = new HashMap();
    private static final long CALL_TIMEOUT_SECONDS = 3 * 60; // 3 minutes
    private static AtomicInteger _callThreadId = new AtomicInteger();

    public CallPool(Dialer dialer, int numberOfChannels) {
        _dialer = dialer;
        
        final ThreadGroup threadGroup = new ThreadGroup("Threads for " + dialer);
        ThreadFactory threadFactory = new ThreadFactory() {
            public Thread newThread(Runnable r) {
                String name = "Dialer-" + _callThreadId.getAndIncrement();
                return new Thread(threadGroup, r, name);
            }
        };
        
        _threadPool = new ThreadPoolExecutor(numberOfChannels,
                                             numberOfChannels,
                                             5,
                                             TimeUnit.MINUTES,
                                             new LinkedBlockingQueue(),
                                             threadFactory);
    }

    /**
     * Submits a call to be called when a thread is available. This method will
     * block until a thread (and thus, a channel) is available to handle the
     * call.
     * @param call The Call object to be called.
     * @throws InterruptedException Thrown if the caller's thread is blocked.
     */
    public void submitCall(final Call call) throws InterruptedException {

        // add call to our pending list
        _pendingCalls.put(call.getToken(), call);
        // add listener to call so that we can remove it from pending list
        call.addPropertyChangeListener(this);
        
        // create task to make the call
        Runnable callTask = new Runnable() {
            public void run() {
                _dialer.makeCall(call);
            }
        };
        
        // the following will block until there is an available thread
        _threadPool.execute(callTask);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(Call.CALL_STATE)) {
            CallState newState = (CallState) evt.getNewValue();
            CallState oldState = (CallState) evt.getOldValue();
            final Call call = (Call) evt.getSource();
            if (newState.isDone()) {
                _pendingCalls.remove(call.getToken());
            } 
            if (newState instanceof Connecting) {
                TimerTask task = new TimerTask() {
                    public void run() {
                        call.handleTimeout();
                    }
                };
                _timerTasks.put(call, task);
                _timer.schedule(task, CALL_TIMEOUT_SECONDS * 1000);
            } else if (oldState instanceof Connecting) {
                TimerTask task = (TimerTask) _timerTasks.get(call);
                task.cancel();
                _timerTasks.remove(call);
            }
        }
    }

    /**
     * Returns a reference to a Call object based on the
     * token. The token is passed to the voice response
     * system when the call is dialed.
     * @param token The unique identifier of the call
     * @return The Call object matching the token
     */
    public Call getCall(String token) {
        return (Call) _pendingCalls.get(token);
    }

    public void shutdown() throws InterruptedException {
        _threadPool.shutdown();
        
        // this is odd, even if we do a shutdownNow, the phone calls will still
        // continue and a couple of threads will hang around which prevent
        // the JVM from exiting... so it is probably best just to do a 
        // threadPool.shutdownAfterProcessingCurrentlyQueuedTasks so that
        // we can monitor the process...
        _threadPool.awaitTermination(120,TimeUnit.SECONDS);
        if (!_threadPool.isTerminated()) {
            CTILogger.error("Unable to cleanly shutdown notification call pool. Forcing shutdown.");
            _threadPool.shutdownNow();
        }
        
        // cancel timer
        _timer.cancel();
        
        // transition all pending calls to the Unconfirmed state
        for (Iterator iter = _pendingCalls.values().iterator(); iter.hasNext();) {
            Call pendingCall = (Call) iter.next();
            pendingCall.removePropertyChangeListener(this);
            pendingCall.changeState(new Unconfirmed("Server shutdown"));
        }
    }
}
