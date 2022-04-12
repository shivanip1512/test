package com.cannontech.multispeak.data;

import java.util.List;
import java.util.NoSuchElementException;

import org.apache.logging.log4j.Logger;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.msp.beans.v3.MspObject;
import com.google.common.collect.Iterables;

public abstract class MspReturnList {

	private final Logger log = YukonLogManager.getLogger(MspReturnList.class);
	
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
     * Helper method to populate lastSent and objectsRemaining from a list of objects.
     * ObjectsRemaining is -1 when size of objects is >= maxRecords (expectMore) but exact number unknown, else 0.
     * LastSent is the determined by getLastSentObjectId using last in object list.
     * Note: Expect objects to be an ordered list (by MeterNumber values for example)
     * @param maxRecords
     * @param meters
     */
    public void setReturnFields(List<?> objects, int maxRecords) {

        try {
            // Set the lastSet with the last meternumber in meters; meters is ordered by meterNumber
            Object lastObject = Iterables.getLast(objects);
            setReturnFields(lastObject, objects.size(), maxRecords);
        } catch (NoSuchElementException e) {
            // skip...we don't have any lastSent item to report.
            // objectsRemaining will be 0 by default
        }
    }
    
    /**
     * Helper method to populate lastSent and objectsRemaining from a list of objects.
     * ObjectsRemaining is -1 when size of objects is >= maxRecords (expectMore) but exact number unknown, else 0.
     * LastSent is the determined by getLastSentObjectId using last in object list.
     * Note: Expect objects to be an ordered list (by MeterNumber values for example)
     * @param maxRecords
     * @param meters
     */
    public void setReturnFields(Object lastObject, int size, int maxRecords) {

        // Set the lastSet with the last meternumber in meters; meters is ordered by meterNumber
        String lastSentObjectId = getLastSentObjectId(lastObject);

        setLastSent(lastSentObjectId);
        
        // Set the objectsRemaining, counting Meters (not elements of the extending class's return List).  
        int numberRemaining = (size >= maxRecords ? -1 : 0); 
        setObjectsRemaining(numberRemaining);
    }

    /**
     * Helper method to parse specific instance type of lastObject and return a single string value
     *  representing this object's MultiSpeak identifier. In most cases, this will be meter number or service location.
     * @param lastObject
     */
    private String getLastSentObjectId(Object lastObject) {
        if (lastObject == null) {
            return "";
        } else if (lastObject instanceof YukonMeter) {
            return ((YukonMeter)lastObject).getMeterNumber();
        } else if (lastObject instanceof com.cannontech.msp.beans.v3.Meter) {
            return ((com.cannontech.msp.beans.v3.Meter)lastObject).getMeterNo();
        } else if (lastObject instanceof MspObject) {
            return ((MspObject) lastObject).getObjectID();
        
        } else if (lastObject instanceof com.cannontech.msp.beans.v4.MeterBase) {
            return ((com.cannontech.msp.beans.v4.MeterBase) lastObject).getMeterID().getMeterNo();
        
        } else if (lastObject instanceof com.cannontech.msp.beans.v5.multispeak.MspObject) {
            return ((com.cannontech.msp.beans.v5.multispeak.MspObject) lastObject).getPrimaryIdentifier().getValue();
        } else {
            log.error("Object unrecognized for parsing lastSent value. Returning toString of object:" + lastObject.toString());
            return lastObject.toString();
        }
    }
}