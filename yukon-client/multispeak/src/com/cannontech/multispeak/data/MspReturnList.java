package com.cannontech.multispeak.data;

import java.util.List;
import java.util.NoSuchElementException;

import com.cannontech.amr.meter.model.Meter;
import com.google.common.collect.Iterables;

public abstract class MspReturnList {

    private int objectsRemaining = 0;
    private String lastSent = null;

    public int getObjectsRemaining() {
        return objectsRemaining;
    }
    public void setObjectsRemaining(int objectsRemaining) {
        this.objectsRemaining = objectsRemaining;
    }
    public String getLastSent() {
        return lastSent;
    }
    public void setLastSent(String lastSent) {
        this.lastSent = lastSent;
    }
    
    /**
     * Helper method to populate lastSent and objectsMemaining from a list of meters.
     * ObjectsRemaining is -1 when size of meters is >= maxRecords (expectMore) but exact number unknown, else 0.
     * LastSent is the meterNumber of the last object in meters list.
     * Note: Expect meters to be an ordered list (by MeterNumber values)
     * @param maxRecords
     * @param meters
     */
    public void setReturnFields(List<Meter> meters, int maxRecords) {

        try {
            // Set the lastSet with the last meternumber in meters; meters is ordered by meterNumber
            Meter lastMeter= Iterables.getLast(meters);
            setLastSent(lastMeter.getMeterNumber());
            
            // Set the objectsRemaining, counting Meters (not elements of the extending class's return List).  
            int numberRemaining = (meters.size() >= maxRecords ? -1 : 0); 
            setObjectsRemaining(numberRemaining);

        } catch (NoSuchElementException e) {
            // skip...we don't have any lastSent item to report.
            // objectsRemaining will be 0 by default
        }
    }
}
