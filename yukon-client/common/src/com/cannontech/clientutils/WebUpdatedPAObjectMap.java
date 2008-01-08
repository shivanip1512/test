package com.cannontech.clientutils;

/**
 * class contains implemtation of a strategy to lighten the client-server 
 * AJAX updates (see @WebUpdatedDAO).
 * Every class extending WebUpdatedPAObjectMap will a method to get all the updated ids since
 * the last interaction and the data structure to hold the updated map (timestampMap). 
 * The extending class then must implement handlePAOChangedEvent() method
 * to  store the ids into the map
 */
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;

public abstract class WebUpdatedPAObjectMap<E> implements WebUpdatedDAO<E> {
	private Map<E,Date> timestampMap = new HashMap<E,Date>();

	public WebUpdatedPAObjectMap() {
	}
	
    @Override
    public List<E> getUpdatedIdsSince(Date timeStamp, E... keyList) {
        final List<E> updatedIds = new ArrayList<E>();
        for (final E key : keyList) {
            if (hasObjectBeenUpdatedSince(key, timeStamp)) updatedIds.add(key);
        }
        return updatedIds;
    }

    @Override
    public void manualUpdate(Date timeStamp, E... ids) {
        for (final E e : ids) {
            updateMap(e, timeStamp);
        }    
    }

	private boolean hasObjectBeenUpdatedSince(E e, Date timeStamp) {
	    if (e == null) return false;
	    
	    Date date = timestampMap.get(e);
	    if (date != null) {
	        boolean result = date.after(timeStamp);
	        return result;
	    }
	    
		return false;
	}

    protected void updateMap(E e, Date date) {
        getTimestampMap().put(e, date);                                     
    }

	private Map<E,Date> getTimestampMap() {
		return timestampMap;
	}

}