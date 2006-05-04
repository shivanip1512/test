package com.cannontech.database.data.notification;

import com.cannontech.common.util.CtiUtilities;

/**
 * @author ryan
 *
 */
public class CustomerNotifGroupMap extends NotifMap {

	public static final String TABLE_NAME = "CustomerNotifGroupMap";
    private int id = CtiUtilities.NONE_ZERO_ID;

	public CustomerNotifGroupMap( int id )
	{
		setCustomerID(id);
	}

	public CustomerNotifGroupMap( int id, String attribs )
	{
		super( attribs );
        setCustomerID(id);
	}


	/**
	 * @return
	 */
	public int getCustomerID()
	{
		return id;
	}

	/**
	 * @param i
	 */
	public void setCustomerID(int i)
	{
		id = i;
	}

}
