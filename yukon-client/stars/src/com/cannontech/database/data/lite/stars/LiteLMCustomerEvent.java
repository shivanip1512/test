package com.cannontech.database.data.lite.stars;

import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteTypes;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public abstract class LiteLMCustomerEvent extends LiteBase {
	
	private int eventTypeID = com.cannontech.common.util.CtiUtilities.NONE_ZERO_ID;
	private int actionID = com.cannontech.common.util.CtiUtilities.NONE_ZERO_ID;
	private long eventDateTime = 0;
	private String notes = null;
	
	public LiteLMCustomerEvent() {
		super();
	}
	
	public LiteLMCustomerEvent(int eventID) {
		super();
		setEventID( eventID );
		setLiteType( LiteTypes.STARS_LMCUSTOMER_EVENT );
	}
	
	public int getEventID() {
		return getLiteID();
	}
	
	public void setEventID(int eventID) {
		setLiteID( eventID );
	}

	/**
	 * Returns the actionID.
	 * @return int
	 */
	public int getActionID() {
		return actionID;
	}

	/**
	 * Returns the eventDateTime.
	 * @return long
	 */
	public long getEventDateTime() {
		return eventDateTime;
	}

	/**
	 * Returns the eventTypeID.
	 * @return int
	 */
	public int getEventTypeID() {
		return eventTypeID;
	}

	/**
	 * Returns the notes.
	 * @return String
	 */
	public String getNotes() {
		return notes;
	}

	/**
	 * Sets the actionID.
	 * @param actionID The actionID to set
	 */
	public void setActionID(int actionID) {
		this.actionID = actionID;
	}

	/**
	 * Sets the eventDateTime.
	 * @param eventDateTime The eventDateTime to set
	 */
	public void setEventDateTime(long eventDateTime) {
		this.eventDateTime = eventDateTime;
	}

	/**
	 * Sets the eventTypeID.
	 * @param eventTypeID The eventTypeID to set
	 */
	public void setEventTypeID(int eventTypeID) {
		this.eventTypeID = eventTypeID;
	}

	/**
	 * Sets the notes.
	 * @param notes The notes to set
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}

}
