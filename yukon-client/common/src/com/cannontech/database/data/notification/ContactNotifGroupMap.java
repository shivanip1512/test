package com.cannontech.database.data.notification;

import com.cannontech.common.util.CtiUtilities;


/**
 * @author ryan
 *
 */
public class ContactNotifGroupMap extends NotifMap {

	public static final String TABLE_NAME = "ContactNotifGroupMap";
    private int id = CtiUtilities.NONE_ZERO_ID;
	
	public ContactNotifGroupMap( int id )
	{
		setContactID(id);
	}

	public ContactNotifGroupMap( int id, String attribs )
	{
		super( attribs );
        setContactID(id);
	}

	/**
	 * @return
	 */
	public int getContactID()
	{
		return id;
	}

	/**
	 * @param i
	 */
	public void setContactID(int i)
	{
		id = i;
	}

}
