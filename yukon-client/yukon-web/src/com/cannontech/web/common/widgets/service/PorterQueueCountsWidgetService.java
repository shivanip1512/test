/**
 * Interface PorterQueueCoutnsWidgetService
 * 
 * This service is meant to get data for the Porter Queue Counts widget and to control how often
 * that data is updated.
 */
package com.cannontech.web.common.widgets.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.Instant;

import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public interface PorterQueueCountsWidgetService {
    /**
     * Return a map of Porter Queue Count point Id's to their corresponding port PAO.
     * The idea behind this function is for the PorterQueueCountsWidget to be able to take portIds
     * as a widget parameter, and get the pao info to display in the trend graph (e.g. port name).
     * @param Integer list of portIds
     * @return a map of the Porter Queue Counts point Id's to port PAO's
     */
    Map<Integer, LiteYukonPAObject> getPointIdToPaoMap(List<Integer> portIds);
    
    /**
     * rawPointHistoryDataProvider
     * Get point values from RawPointHistory for a set of pointIds and return a
     * map of those pointIds to their corresponding List<PointValueHolder>. 
     * @param pointId used to get data from RawPointHistory
     * @return {@link List<PointValueHolder>}
     */
    Map<Integer, List<PointValueHolder>> rawPointHistoryDataProvider(Set<Integer> pointIds);
    
    /**
     * graphDataProvider
     * This data provider takes a List<PointValueHolder>, and provides the point's timestamp/value
     * combos to be rendered to the trendline.
     * @param data {@link List<PointValueHolder>} is the unique identifier for the range of entries.
     * @return List<Object[]> containing {point timestamp, point value}
     */
    List<Object[]> graphDataProvider(List<PointValueHolder> data);

    /**
     * Gets the next available refresh time.
     * @param Instant value indicating the most recent time the data was loaded for this graph
     * @return Instant the next refresh time
     */
    Instant getNextRefreshTime(Instant lastGraphDataRefreshTime);
    /**
     * Gets the milliseconds for refresh time
     * @return long the refresh time
     */
    long getRefreshMilliseconds();

    /**
     * Gets the lite port paos for all of the ports in Yukon
     * @return List<LiteYukonPAObject> of port paos
     */
    List<LiteYukonPAObject> getAllPortPaos();

    /**
     * Return a map of Porter Queue Count point Id's to their corresponding port PAO.
     * @param LiteYukonPAObject list of portPaos
     * @return a map of the Porter Queue Counts point Id's to port PAO's
     */
    Map<Integer, LiteYukonPAObject> makeAndGetPointIdToPaoMap(List<LiteYukonPAObject> portPaos);
}
