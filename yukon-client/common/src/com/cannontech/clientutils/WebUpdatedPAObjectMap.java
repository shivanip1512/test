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
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class WebUpdatedPAObjectMap<E> implements WebUpdatedDAO<E> {
	private Map<E,Long> timestampMap = new HashMap<E,Long>();

	public WebUpdatedPAObjectMap() {
	}

    @Override
    public synchronized void remove(E e) {
        timestampMap.remove(e);
    }
	
	@Override
	public synchronized void remove(Collection<E> ids) {
	    timestampMap.keySet().removeAll(ids);
	}
	
	@Override
    public synchronized boolean containsKey(E e) {
        boolean result = timestampMap.containsKey(e);
        return result;
    }
	
    @Override
    public synchronized List<E> getUpdatedIdsSince(Date timeStamp, E... keyList) {
        final List<E> updatedIds = new ArrayList<E>(keyList.length);
        for (final E key : keyList) {
            if (hasObjectBeenUpdatedSince(timeStamp, key)) {
                updatedIds.add(key);
            }
        }
        return updatedIds;
    }

    @Override
    public synchronized void manualUpdate(Iterable<E> ids) {
        long currentTimeMillis = System.currentTimeMillis();
        for (final E e : ids) {
            timestampMap.put(e, currentTimeMillis);                                     
        }    
    }

	private synchronized boolean hasObjectBeenUpdatedSince(Date timeStamp, E e) {
	    if (e == null) return false;
	    
	    Long date = timestampMap.get(e);
	    if (date == null) {
	        return false;
	    }
	    
	    boolean result = date >= timeStamp.getTime();
	    return result;
	}

    protected synchronized void updateMap(E e) {
        manualUpdate(Collections.singleton(e));
    }

}