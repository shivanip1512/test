package com.cannontech.core.dynamic;

import java.util.Map;
import java.util.Set;

import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.database.cache.DBChangeLiteListener;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.dispatch.message.Signal;

/**
 * AsyncDynamicDataSource provides a method to receive dynamic point, signal/alarm,
 * and database change data asynchronously.
 * 
 * @author alauinger
 *
 */
public interface AsyncDynamicDataSource {

    /**
     * Registers a listener to receive PointData events for the given
     * points, returns the current value.
     * 
     * @see removePointDataListener
     * @param l the listener to add, use AllPointDataListener to receive all values
     * @param pointIds the pointIds the listener will receive events for
     */
    Set<? extends PointValueQualityHolder> getAndRegisterForPointData(PointDataListener l, Set<Integer> pointIds);

    /**
     * Registers a listener to receive PointData events for all points.
     * 
     * @param l the listener to add, use AllPointDataListener to receive all values
     */
    void registerForAllPointData(PointDataListener l);

    /**
     * Registers a listener to receive PointData events for the given
     * list of points.
     * 
     * @see removePointDataListener
     * @param l the listener to add, use AllPointDataListener to receive all values
     * @param pointIds the list of point ids the listener will receive events for
     */
    void registerForPointData(PointDataListener l, Set<Integer> pointIds);

    /**
     * Removes a list of points from the given listeners registrations
     * 
     * @param l the listener to unregister
     * @param pointIds the point ids to un register for
     */
    void unRegisterForPointData(PointDataListener l, Set<Integer> pointIds);

    /**
     * Removes the listener completely, no further point data will be sent to the
     * given listener
     * 
     * @param l the listener to unregister
     */
    void unRegisterForPointData(PointDataListener l);

    void registerForAllAlarms(SignalListener listener);

    /**
     * Adds a listner that will receive DBChange events
     * 
     * @see removeDBChangeListener
     * @param l the listener to add
     */
    void addDBChangeListener(DBChangeListener l);

    /**
     * Removes a listner previously added with addDBChangeListener
     * 
     * @param l the listener to remove
     */
    void removeDBChangeListener(DBChangeListener l);

    /**
     * This is similar to addDBChangeListener, but uses a cleaner interface.
     */
    void addDatabaseChangeEventListener(DatabaseChangeEventListener listener);

    /**
     * Register for DB changes of a specific type.
     */
    void addDatabaseChangeEventListener(DbChangeCategory changeCategory, DatabaseChangeEventListener listener);

    void addDatabaseChangeEventListener(DbChangeCategory changeCategory, Set<DbChangeType> types,
            DatabaseChangeEventListener listener);

    /**
     * Processes a DBChangeMsg locally and then queue to the
     * dispatch connection. This method is ONLY provided for
     * the DBPersistentDaoImpl. NO OTHER CODE SHOULD EVER
     * CALL THIS!
     */
    void publishDbChange(DBChangeMsg dbChange);

    /**
     * Adds a listner that will receive DBChange events
     * 
     * @see removeDBChangeListener
     * @param l the listener to add
     */
    @Deprecated
    void addDBChangeLiteListener(DBChangeLiteListener l);

    /**
     * Sends point data to dispatch.
     */
    void putValue(PointData pointData);

    /**
     * Sends point data to dispatch.
     */
    void putValues(Iterable<PointData> pointDatas);

    Set<? extends PointValueQualityHolder> getPointValues(Set<Integer> pointIds);
    
    Set<? extends PointValueQualityTagHolder> getPointValuesAndTags(Set<Integer> pointIds);

    PointValueQualityTagHolder getPointValueAndTags(int pointId);
    
    long getTags(int pointId);

    PointValueQualityHolder getPointValue(int pointId);

    Set<Signal> getSignals(int pointId);

    /**
     * If no signals exist empty set will be returned.
     */
    Set<Signal> getCachedSignals(int pointId);

    /**
     * If no signals exist empty set will be returned.
     */
    Set<Signal> getCachedSignalsByCategory(int alarmCategoryId);

    /**
     * Returns a map of point id to set of signals. If no signals exist the map value will be
     * an empty set.
     */
    Map<Integer, Set<Signal>> getSignals(Set<Integer> pointIds);

    Set<Signal> getSignalsByCategory(int alarmCategoryId);

    /**
     * This method is used for debugging
     */
    void logListenerInfo(int pointId);

    /**
     * This method gets the point data for given points
     */
    Set<? extends PointValueQualityHolder> getPointDataOnce(Set<Integer> pointIds);
}
