package com.cannontech.database.data.notification;

/**
 * @author ryan
 *
 */
public class NotifDestinationMap extends NotifMap {

	public static final String TABLE_NAME = "NotificationDestination";

	public NotifDestinationMap( int id )
	{
		super( id );
	}

	public NotifDestinationMap( int id, String attribs )
	{
		super( id, attribs );
	}

	/**
	 * @return
	 */
	public int getRecipientID()
	{
		return getID();
	}

	/**
	 * @param i
	 */
	public void setRecipientID(int i)
	{
		setID(i);
	}

}
