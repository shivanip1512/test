package com.cannontech.notif.voice;

import com.cannontech.common.concurrent.WaitableBoolean;

/**
 * This thread pulls items off the notification queue, creates Call objects, and
 * submits them to the CallPool.
 */
public class WorkerThread extends Thread {
    private NotificationQueue _notificationQueue;

    private CallPool _callPool;
    
    private final WaitableBoolean _threadComplete = new WaitableBoolean(false);

    public WorkerThread(NotificationQueue notificationQueue, CallPool callPool) {
        super("VoiceNotifWorkerThread");
        _notificationQueue = notificationQueue;
        _callPool = callPool;
        //setDaemon(true);
    }

    public void run() {
        Call nextCall = null;
        try {
            while (true) {
                SingleNotification nextNotification = _notificationQueue.getNext();
                nextCall = nextNotification.createNewCall();
                try {
                    _callPool.submitCall(nextCall);
                } catch (InterruptedException e1) {
                    nextCall.changeState(new com.cannontech.notif.voice.callstates.UnknownError(e1));
                    throw e1;
                }
            }
        } catch (InterruptedException e) {
            // someone has requested thread shutdown
        }
        
        _threadComplete.set(true);

    }
    
    public void shutdown() throws InterruptedException {
        interrupt();
        _threadComplete.whenTrue(null);
    }

}
