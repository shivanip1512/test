package com.cannontech.database.data.notification;

import com.cannontech.common.util.CtiUtilities;

/**
 * @author ryan
 *
 */
public class NotifDestinationMap extends NotifMap {

	public static final String TABLE_NAME = "NotificationDestination";
    private int id = CtiUtilities.NONE_ZERO_ID;

	public NotifDestinationMap( int id )
	{
		setRecipientID(id);
	}

	public NotifDestinationMap( int id, String attribs )
	{
		super( attribs );
        setRecipientID(id);
	}

	/**
	 * @return
	 */
	public int getRecipientID()
	{
		return id;
	}

	/**
	 * @param i
	 */
	public void setRecipientID(int i)
	{
		id = i;
	}

}
