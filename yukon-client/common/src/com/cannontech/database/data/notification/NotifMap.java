package com.cannontech.database.data.notification;

import com.cannontech.common.util.CtiUtilities;

/**
 * @author ryan
 *
 *	Common base class for notifcations
 */
public class NotifMap implements java.io.Serializable
{
	private int id = CtiUtilities.NONE_ZERO_ID;

	//Y or N fields for the notifcation attributes:
	// [0]:send email, [1]:make phone call
	private String attribs = DEF_ATTRIBS;
	public static final String DEF_ATTRIBS = "YNNNNNNNNNNNNNNN";


	public NotifMap( int id )
	{
		super();
		setID( id );
	}

	public NotifMap( int id, String attribs )
	{
		this( id );
		setAttribs( attribs );
	}

	/**
	 * @return
	 */
	public boolean isSendEmails()
	{
		return getAttribs().charAt(0) == 'Y';
	}

	/**
	 * @return
	 */
	public boolean isSendOutboundCalls()
	{
		return getAttribs().charAt(1) == 'Y';
	}

	/**
	 * @return
	 */
	public void setSendOutboundCalls( boolean t )
	{
		setChars( 1, (t ? 'Y' : 'N') );
	}

	/**
	 * @return
	 */
	public void setSendEmails( boolean t )
	{
		setChars( 2, (t ? 'Y' : 'N') );
	}
	
	private void setChars( int indx, char theChar )
	{
		StringBuffer buff = new StringBuffer(getAttribs());			
		buff.setCharAt( indx, theChar );
		setAttribs( buff.toString() );
	}


	/**
	 * @return
	 */
	public String getAttribs()
	{
		return attribs;
	}

	/**
	 * @return
	 */
	protected int getID()
	{
		return id;
	}

	/**
	 * @param string
	 */
	public void setAttribs(String string)
	{
		if( attribs == null || attribs.length() != DEF_ATTRIBS.length() )
			throw new IllegalStateException("The attribs attribute can not be null and must be " + DEF_ATTRIBS.length() + " characters long");

		attribs = string;
	}

	/**
	 * @param i
	 */
	protected void setID(int i)
	{
		id = i;
	}

}
