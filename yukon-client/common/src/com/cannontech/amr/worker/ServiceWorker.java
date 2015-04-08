package com.cannontech.amr.worker;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PreDestroy;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.google.common.collect.ImmutableList;

/**
 * This is a class that does asynchronous work by pulling from a queue
 * of ServiceWorkerQueueObject's. Make use of this class for your service
 * by extending it in your service's implementation.
 * 
 * How to use this guy:
 *  1) Extend it
 *  2) Implement required abstract methods (defined below)
 *  3) Initialize this class by calling initServiceWorker() in either your service's
 *     constructor or in a @PostConstruct method
 *  4) Add objects to be executed by calling addObjectToQueue(T)
 *  5) Call checkWorkerInterrupted() in your innermost loop of your processWorkerObject() method
 *     so that this class can properly cancel any work being done on a given object
 *     if another object with the same ID is queued up
 */
public abstract class ServiceWorker<T extends ServiceWorkerQueueObject> {
    
    private static final Logger log = YukonLogManager.getLogger(ServiceWorker.class);

    private List<Worker> workers;
    private Map<Integer, Boolean> objIdIsBeingWorkedOn = new ConcurrentHashMap<>();
    
    // map of workerIndex to objectId
    // if a worker isn't working on anything then it's value will be null
    private Map<Integer, Integer> workerWorkingOn;
    private Map<Integer, Boolean> isWorkerInterrupted;
    
    // keeps track of how many objects are on the queue of each worker
    private Map<Integer, Integer> workerQueueSize;

    private static final Comparator<Entry<Integer, Integer>> minMapValueComparator = new Comparator<Entry<Integer, Integer>>() {
        @Override
        public int compare(Entry<Integer, Integer> entry1, Entry<Integer, Integer> entry2) {
            return entry1.getValue().compareTo(entry2.getValue());
        }
    };

    /**
     * This method needs to be called in your implementing service's @PostConstruct method
     */
    protected void initServiceWorker() {
        int workerCount = getWorkerCount();
        int queueSize = getQueueSize();
        
        isWorkerInterrupted = new ConcurrentHashMap<>(workerCount);
        workerWorkingOn = new ConcurrentHashMap<>(workerCount);
        workerQueueSize = new ConcurrentHashMap<>(workerCount);
        
        // setup as many workers as requested
        ImmutableList.Builder<Worker> workerBuilder = ImmutableList.builder();
        for (int i = 0; i < workerCount; ++i) {
            isWorkerInterrupted.put(i, false);
            workerQueueSize.put(i, 0);

            Worker worker = new Worker(i, queueSize);
            workerBuilder.add(worker);
            worker.start();
        }
        workers = workerBuilder.build();
    }

    /**
     * Does your asynchronous work for a given ServiceWorkerQueueObject
     * 
     * The checkWorkerInterrupted() method should be called in your innermost loop of "work"
     * so that this class can properly cancel any work being done on a given object
     * if another object with the same ID is queued up
     * 
     * @throws InterruptedException
     */
    protected abstract void processWorkerObject(T obj) throws InterruptedException;
    /**
     * Returns the amount of workers this should use
     */
    protected abstract int getWorkerCount();
    /**
     * Returns how large the queue (of each worker) should be
     */
    protected abstract int getQueueSize();
    /**
     * Used for logging purposes
     */
    protected abstract String getWorkerName();
    
    private class Worker extends Thread {
        private ArrayBlockingQueue<T> inQueue;
        private volatile boolean shutdown = false;
        
        private Worker(int workerNumber, int queueSize) {
            super(getWorkerName() + ": " + workerNumber);
            inQueue = new ArrayBlockingQueue<>(queueSize);
        }
        
        private void queue(T obj) {
            if (shutdown) {
                throw new IllegalStateException("worker has already shutdown");
            }
            
            // stop any active workers for this object
            interruptWorkerWorkingOnObject(obj);
            
            // remove any workers for this object that may be on the queue
            removeMatchingObjectFromQueue(obj);
            
            // now officially working on this object (even though it is really only being put on the queue)
            setWorkingOnObject(obj, true);

            try {
                inQueue.put(obj);
            } catch (InterruptedException e) {
                log.warn("caught exception in queue", e);
            }

            // update our workerqueue size map
            updateWorkerQueueSizeMap(this);

            if (shutdown) {
                drainQueue();
            }
        }
        
        private void drainQueue() {
            inQueue.clear();
        }
        
        @Override
        public void run() {
            while (true) {
                boolean requeue = false;
                try {
                    T message = inQueue.take();
                    
                    // set this worker as working on this object
                    setWorkerWorkingOnObject(message);
                    
                    try {
                        processWorkerObject(message);
                    } catch (InterruptedException ie) {
                        // this worker thread has been interrupted b/c the same obj has been put on the worker queue
                        int currentWorkerIndex = getCurrentWorkerIndex();
                        isWorkerInterrupted.put(currentWorkerIndex, false);
                        log.debug("caught interrupted exception on worker queue " + currentWorkerIndex + " for obj " + message.getId());
                    } catch (Exception e) {
                        if (getRecoverableExceptions().contains(e.getClass())) {
                            int currentWorkerIndex = getCurrentWorkerIndex();
                            isWorkerInterrupted.put(currentWorkerIndex, false);
                            log.warn("Recoverable exception caught. Worker queue " + currentWorkerIndex + " for obj " + message.getId() + ". Message will be put back on queue.", e);
                            requeue = true;
                        } else {
                            throw e;
                        }
                    }
                    
                    // work for this object is now completed
                    setWorkingOnObject(message, false);
                    
                    // clear this worker as not working on anything
                    clearWorkerWorkingOn();
                    
                    updateWorkerQueueSizeMap(this);
                    LogHelper.debug(log, "done working on obj with id %s. Queue size is %s", message.getId(), workerQueueSize.get(getCurrentWorkerIndex()));

                    if (requeue) {
                        queue(message);
                    }
                } catch (InterruptedException e) {
                    log.warn("received shutdown signal, queue size: " + inQueue.size());
                    break;
                } catch (Exception e) {
                    log.warn("Unknown exception while processing request", e);
                }
            }
        }
        
        @PreDestroy
        private void shutdown() {
            shutdown = true;
            this.interrupt();
            drainQueue();
        }
    }
    
    protected void addObjectToQueue(T obj) {
        Worker worker = getMostAvailableWorker();
        worker.queue(obj);
    }

    protected synchronized boolean isWorkingOnObject(Integer objId) {
        if (objId == null) return false;
        
        Boolean workingOnObj = false;
        if (!objIdIsBeingWorkedOn.containsKey(objId)) {
            objIdIsBeingWorkedOn.put(objId, workingOnObj);
            return workingOnObj;
        }
        workingOnObj = objIdIsBeingWorkedOn.get(objId);
        return workingOnObj;
    }

    /**
     * If this worker thread has been interrupted/cancelled then throw an exception
     * 
     * This method should generally be called every iteration of whatever worker loop your
     * processWorkerObject executes
     * 
     * @throws InterruptedException
     */
    protected void checkWorkerInterrupted() throws InterruptedException {
        int currentWorkerIndex = getCurrentWorkerIndex();
        if (isWorkerInterrupted.get(currentWorkerIndex)) {
            throw new InterruptedException();
        }
    }

    /**
     * A collection of exceptions that can be caught such that the service worker thread
     *  will cleanup the current worker and then (re) queue the message.
     */
    protected abstract Collection<Class<? extends Exception>> getRecoverableExceptions();

    /**
     * Returns the worker that is "most available" (worker with the lowest queue size)
     */
    private Worker getMostAvailableWorker() {
        Entry<Integer, Integer> mostAvailWorker = Collections.min(workerQueueSize.entrySet(), minMapValueComparator);
        return workers.get(mostAvailWorker.getKey());
    }
    
    /**
     * Used to keep our worker queue size map up to date
     */
    private void updateWorkerQueueSizeMap(Worker worker) {
        // keep our map of queue size up to date
        int currentWorkerIndex = getWorkerIndex(worker);
        workerQueueSize.put(currentWorkerIndex, worker.inQueue.size());
    }

    /**
     * Iterate over our worker queues and remove any that match our passed in object
     */
    private void removeMatchingObjectFromQueue(T obj) {
        for (Worker worker: workers) {
            for (Iterator<T> objIterator = worker.inQueue.iterator(); objIterator.hasNext();) {
                T queueMsg = objIterator.next();
                if (queueMsg.getId() != null && queueMsg.getId() == obj.getId()) {
                    // no point keeping this guy in the queue
                    log.debug("removing object [" + obj.getId() + "] from worker queue");
                    worker.inQueue.remove(queueMsg);
                }
            }
        }
    }

    private void setWorkingOnObject(T obj, boolean beingWorkedOn) {
        if (obj.getId() == null) return;
        objIdIsBeingWorkedOn.put(obj.getId(), beingWorkedOn);
    }
    
    private void setWorkerWorkingOnObject(T obj) {
        if (obj.getId() == null) return;
        int currentWorkerIndex = getCurrentWorkerIndex();
        workerWorkingOn.put(currentWorkerIndex, obj.getId());
        LogHelper.debug(log, "working on obj with id %s. Queue size is %s", obj.getId(), workerQueueSize.get(currentWorkerIndex));
    }
    
    private void clearWorkerWorkingOn() {
        int currentWorkerIndex = getCurrentWorkerIndex();
        workerWorkingOn.remove(currentWorkerIndex);
    }
    
    private int getCurrentWorkerIndex() {
        return workers.indexOf(Thread.currentThread());
    }
    
    private int getWorkerIndex(Worker worker) {
        return workers.indexOf(worker);
    }
    
    /**
     * Interrupt the workers for a given object. This should be done right before we put a new object
     * onto our worker queue (if we are currently "working" on the same object)
     */
    private void interruptWorkerWorkingOnObject(T obj) {
        for (Entry<Integer, Integer> workerIndexToObjId : workerWorkingOn.entrySet()) {
            if (workerIndexToObjId.getValue() != null && obj.getId() != null && workerIndexToObjId.getValue().equals(obj.getId())) {
                log.debug("interrupting worker for object [" + obj.getId() + "] on worker thread index: " + workerIndexToObjId.getKey());
                isWorkerInterrupted.put(workerIndexToObjId.getKey(), true);
            }
        }
    }
}