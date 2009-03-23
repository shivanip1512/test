package com.cannontech.notif.voice;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.UnknownRolePropertyException;
import com.cannontech.notif.voice.callstates.*;


/**
 * This pool manages the current calls. It is created
 * with a Dialer to actually make the calls and a 
 * specific numberOfChannels. This class will manage up
 * to numberOfChannels threads to actually do the work
 * of making the calls. This number should match the number
 * of physical outgoing phone lines that are available.
 */
public class CallPool implements PropertyChangeListener, CallPoolMBean {

    private static final int MAX_SHUTDOWN_WAIT = 120; // in seconds
    protected ThreadPoolExecutor _threadPool;
    protected Map<String, Call> _pendingCalls;
    Timer _timer = new Timer();
    private Dialer _dialer;
    private Map<Call, TimerTask> _timerTasks;
    private static AtomicInteger _callThreadId = new AtomicInteger(1);
    private int _callTimeoutSeconds;
    private boolean _shutdown = false;
    private int _numberOfChannels;

    public void initialize() throws UnknownRolePropertyException {

        _timerTasks = new ConcurrentHashMap<Call, TimerTask>(30, .75f, _numberOfChannels + 1);
        _pendingCalls = new ConcurrentHashMap<String, Call>(30, .75f, _numberOfChannels + 1);
        
        final ThreadGroup threadGroup = new ThreadGroup("Threads for " + _dialer);
        ThreadFactory threadFactory = new ThreadFactory() {
            public Thread newThread(Runnable r) {
                String name = "Dialer-" + _callThreadId.getAndIncrement();
                return new Thread(threadGroup, r, name);
            }
        };
        _threadPool = new ThreadPoolExecutor(_numberOfChannels,
                                             _numberOfChannels,
                                             5 * 60,
                                             TimeUnit.SECONDS,
                                             new LinkedBlockingQueue<Runnable>(),
                                             threadFactory);
        
    }

    /**
     * Submits a call to be called when a thread is available.
     * @param call The Call object to be called.
     * @throws InterruptedException Thrown if the caller's thread is blocked.
     */
    public void submitCall(final Call call) {
        if (_shutdown) {
            CTILogger.warn("Someone tried to submit call after shutdown: " + call);
            return;
        }

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
                _timer.schedule(task, _callTimeoutSeconds * 1000);
            } else if (oldState instanceof Connecting) {
                TimerTask task = _timerTasks.get(call);
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
     * @throws UnknownCallTokenException 
     */
    public Call getCall(String token) {
        Call call = _pendingCalls.get(token);
        return call;
    }

    public void shutdown() {
        _shutdown  = true;
        
        _threadPool.shutdown();
        
        // The voice server API doesn't respond to interrupts, so there
        // really isn't any way to force an in-progress call to terminate
        try {
            _threadPool.awaitTermination(MAX_SHUTDOWN_WAIT, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // ignore
        }
        if (!_threadPool.isTerminated()) {
            CTILogger.error("Unable to cleanly shutdown notification call pool. Forcing shutdown.");
            _threadPool.shutdownNow();
        }
        
        // cancel timer
        _timer.cancel();
        
        // transition all pending calls to the Unconfirmed state
        for (Iterator<Call> iter = _pendingCalls.values().iterator(); iter.hasNext();) {
            Call pendingCall = iter.next();
            pendingCall.removePropertyChangeListener(this);
            pendingCall.changeState(new Unconfirmed("Server shutdown"));
        }
    }
    
    public int getNumberPendingCalls() {
        return _pendingCalls.size();
    }
    
    public void setDialer(Dialer dialer) {
        _dialer = dialer;
    }
    
    public void setCallTimeoutSeconds(int callTimeoutSeconds) {
        _callTimeoutSeconds = callTimeoutSeconds;
    }
    
    public void setNumberOfChannels(int numberOfChannels) {
        _numberOfChannels = numberOfChannels;
    }
}
