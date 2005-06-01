package com.cannontech.database.data.notification;

/**
 * @author ryan
 *
 */
public class CustomerNotifGroupMap extends NotifMap {

	public static final String TABLE_NAME = "CustomerNotifGroupMap";

	public CustomerNotifGroupMap( int id )
	{
		super( id );
	}

	public CustomerNotifGroupMap( int id, String attribs )
	{
		super( id, attribs );
	}


	/**
	 * @return
	 */
	public int getCustomerID()
	{
		return getID();
	}

	/**
	 * @param i
	 */
	public void setCustomerID(int i)
	{
		setID(i);
	}

}
