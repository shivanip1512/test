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


public abstract class WebUpdatedPAObjectMap implements WebUpdatedDAO {

	//<Integer> <Date>
	private HashMap timestampMap = new HashMap();


	public WebUpdatedPAObjectMap() {
		super();
	}

	/* (non-Javadoc)
	 * @see com.cannontech.cbc.web.WebUpdatedDAO#getUpdatedIdsSince(java.lang.String[], java.util.Date)
	 */
	public String[] getUpdatedIdsSince(String[] ids, Date timeStamp) {
		ArrayList updatedIds = new ArrayList();	
		for (int i = 0; i < ids.length; i++) {
			String id = ids[i];
			if ( hasObjectBeenUpdatedSince(id, timeStamp)){
				updatedIds.add(id);
			}				
		}	
		return (String[]) updatedIds.toArray(new String [updatedIds.size()]);
	}

	/**
	 * mechanism to determine if object has been updated 
	 * @param id
	 * @param timeStamp
	 * @return
	 */
	private boolean hasObjectBeenUpdatedSince(String id, Date timeStamp) {
		//see if the timestamp of the object was updated 
		Date date = ((Date)timestampMap.get(new Integer (id )));
		if (date != null) {
			if (date.after(timeStamp))
				return true;
		}
		return false;
	}

	private HashMap getTimestampMap() {
		return timestampMap;
	}

	protected void updateMap(Integer id, Date date) {
		getTimestampMap().put(id, date);										
	}
}