package com.cannontech.core.dynamic;

import java.util.Set;

import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.database.cache.DBChangeLiteListener;

/**
 * AsyncDynamicDataSource provides a method to receive dynamic point, signal/alarm, 
 * and database change data asynchronously.  
 * 
 * If modifying this class, look at PointUpdateBackingService to see if some
 * of that code could be moved into here or into DynamicDataSource.
 * @author alauinger
 *
 */
public interface AsyncDynamicDataSource {
    
    /**
     * Registers a listener to receive PointData events for the given
     * points, returns the current value.
     * @see removePointDataListener
     * @param l         the listener to add
     * @param pointId   the pointId the listener will receive events for
     */
    public PointValueHolder getAndRegisterForPointData(PointDataListener l, int pointId);
    
    /**
     * Registers a listener to receive PointData events for the given
     * list of points.
     * @see removePointDataListener
     * @param l         the listener to add
     * @param pointIds  the list of point ids the listener will receive events for
     */
    public void registerForPointData(PointDataListener l, Set<Integer> pointIds);
    
    /**
     * Removes a list of points from the given listeners registrations
     * @param l         the listener to unregister
     * @param pointIds  the point ids to un register for
     */
    public void unRegisterForPointData(PointDataListener l, Set<Integer> pointIds);
    
    /**
     * Removes the listener completely, no further point data will be sent to the
     * given listener
     * @param l         the listener to unregister
     */
    public void unRegisterForPointData(PointDataListener l);
        
    /**
     * Adds a listener that will receive Signal events for the given
     * set of points
     * @see removeSignalListener 
     * @param l         the listener to add
     * @param pointIds  the set of point ids the listener will receive events for
     */
    public void registerForSignals(SignalListener l, Set<Integer> pointIds);
    
    /**
     * Removes a list of points from the given listeners registration
     * @param l
     * @param pointIds
     */
    public void unRegisterForSignals(SignalListener l, Set<Integer> pointIds);
    
    /**
     * Removes the given listener completely, no further signals will be sent to the
     * given listener
     * @param l
     */
    public void unRegisterForSignals(SignalListener l);
    
    
    /**
     * Adds a listner that will receive DBChange events
     * @see removeDBChangeListener
     * @param l         the listener to add
     */
    public void addDBChangeListener(DBChangeListener l);
    
    /**
     * Removes a listner previously added with addDBChangeListener
     * @param l         the listener to remove
     */
    public void removeDBChangeListener(DBChangeListener l);
    
    /**
     * Adds a listner that will receive DBChange events
     * @see removeDBChangeListener
     * @param l         the listener to add
     */
    @Deprecated
    public void addDBChangeLiteListener(DBChangeLiteListener l);
    
    /**
     * Removes a listner previously added with addDBChangeListener
     * @param l         the listener to remove
     */
    @Deprecated
    public void removeDBChangeLiteListener(DBChangeLiteListener l);
}
