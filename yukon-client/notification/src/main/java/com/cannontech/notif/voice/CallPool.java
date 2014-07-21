package com.cannontech.notif.voice;

import java.util.Map;
import java.util.Timer;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.UnknownRolePropertyException;


/**
 * This pool manages the current calls. It is created
 * with a Dialer to actually make the calls and a 
 * specific numberOfChannels. This class will manage up
 * to numberOfChannels threads to actually do the work
 * of making the calls. This number should match the number
 * of physical outgoing phone lines that are available.
 */
public class CallPool implements CallPoolMBean {

    private static final int MAX_SHUTDOWN_WAIT = 120; // in seconds
    private Logger log = YukonLogManager.getLogger(CallPool.class);
    protected ThreadPoolExecutor threadPool;
    protected Map<String, Call> pendingCalls;
    Timer timer = new Timer();
    private Dialer dialer;
    private static AtomicInteger callThreadId = new AtomicInteger(1);
    private boolean shutdown = false;
    private int numberOfChannels;

    public void initialize() throws UnknownRolePropertyException {

        pendingCalls = new ConcurrentHashMap<String, Call>(30, .75f, numberOfChannels + 1);
        
        final ThreadGroup threadGroup = new ThreadGroup("Threads for " + dialer);
        ThreadFactory threadFactory = new ThreadFactory() {
            public Thread newThread(Runnable r) {
                String name = "Dialer-" + callThreadId.getAndIncrement();
                return new Thread(threadGroup, r, name);
            }
        };
        threadPool = new ThreadPoolExecutor(numberOfChannels,
                                             numberOfChannels,
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
        if (shutdown) {
            log.warn("Someone tried to submit call after shutdown: " + call);
            return;
        } else {
            log.debug("Call submitted: " + call);
        }

        // add call to our pending list
        pendingCalls.put(call.getToken(), call);
        // add listener to call so that we can remove it from pending list
        call.addCompletionCallback(new Runnable() {
            public void run() {
                pendingCalls.remove(call.getToken());
            }
        });
        
        // create task to make the call
        Runnable callTask = new Runnable() {
            public void run() {
                dialer.makeCall(call);
            }
        };
        
        threadPool.execute(callTask);
        log.debug("Call task added to queue, current queue size is " + threadPool.getQueue().size());
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
        Call call = pendingCalls.get(token);
        return call;
    }

    public void shutdown() {
        shutdown  = true;
        
        threadPool.shutdown();
        
        // The voice server API doesn't respond to interrupts, so there
        // really isn't any way to force an in-progress call to terminate
        try {
            threadPool.awaitTermination(MAX_SHUTDOWN_WAIT, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // ignore
        }
        if (!threadPool.isTerminated()) {
            CTILogger.error("Unable to cleanly shutdown notification call pool. Forcing shutdown.");
            threadPool.shutdownNow();
        }
        
        // cancel timer
        timer.cancel();
        
        // transition all pending calls to the Unconfirmed state
        for (Call pendingCall : pendingCalls.values()) {
            pendingCall.handleFailure("Server shutdown");
        }
    }
    
    public int getNumberPendingCalls() {
        return pendingCalls.size();
    }
    
    public void setDialer(Dialer dialer) {
        this.dialer = dialer;
    }
    
    public void setNumberOfChannels(int numberOfChannels) {
        this.numberOfChannels = numberOfChannels;
    }
}
