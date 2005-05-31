package com.cannontech.database.data.notification;


/**
 * @author ryan
 *
 */
public class ContactNotifGroupMap extends NotifMap
{
	public ContactNotifGroupMap( int id )
	{
		super( id );
	}

	public ContactNotifGroupMap( int id, String attribs )
	{
		super( id, attribs );
	}

	/**
	 * @return
	 */
	public int getContactID()
	{
		return getID();
	}

	/**
	 * @param i
	 */
	public void setContactID(int i)
	{
		setID(i);
	}

}
