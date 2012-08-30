package com.cannontech.core.dynamic;

import java.util.Set;

import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.database.cache.DBChangeLiteListener;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeType;

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
     * @param l         the listener to add, use AllPointDataListener to receive all values
     * @param pointId   the pointId the listener will receive events for
     */
    public PointValueQualityHolder getAndRegisterForPointData(PointDataListener l, int pointId);
    
    /**
     * Registers a listener to receive PointData events for the given
     * points, returns the current value.
     * @see removePointDataListener
     * @param l         the listener to add, use AllPointDataListener to receive all values
     * @param pointIds   the pointIds the listener will receive events for
     */    
    public Set<? extends PointValueQualityHolder> getAndRegisterForPointData(PointDataListener l, Set<Integer> pointIds);
    
    /**
     * Registers a listener to receive PointData events for all points.
     * @param l         the listener to add, use AllPointDataListener to receive all values
     */
    public void registerForAllPointData(PointDataListener l);
    
    /**
     * Registers a listener to receive PointData events for the given
     * list of points.
     * @see removePointDataListener
     * @param l         the listener to add, use AllPointDataListener to receive all values
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
    
    public void registerForAllAlarms(SignalListener listener);

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
     * This is similar to addDBChangeListener, but uses a cleaner interface.
     * @param listener
     */
    public void addDatabaseChangeEventListener(DatabaseChangeEventListener listener);
    
    /**
     * Register for DB changes of a specific type.
     * @param changeCategory
     * @param listener
     */
    public void addDatabaseChangeEventListener(DbChangeCategory changeCategory, DatabaseChangeEventListener listener);
    
    public void addDatabaseChangeEventListener(DbChangeCategory changeCategory,
                                        Set<DbChangeType> types,
                                        DatabaseChangeEventListener listener);
    
    /**
     * Processes a DBChangeMsg locally and then queue to the 
     * dispatch connection. This method is ONLY provided for
     * the DBPersistentDaoImpl. NO OTHER CODE SHOULD EVER
     * CALL THIS!
     * @param dbChange
     */
    public void publishDbChange(DBChangeMsg dbChange);
    
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
