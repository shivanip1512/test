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
	// [0]:send email, [1]:make phone call, [2]:send short email
	private String attribs = DEF_ATTRIBS;
	public static final String DEF_ATTRIBS = "YNYNNNNNNNNNNNNN";
    public static final int METHOD_EMAIL = 0;
    public static final int METHOD_VOICE = 1;
    public static final int METHOD_SMS = 2;


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
    
    public boolean supportsMethod(int notificationMethod) {
        return getAttribs().charAt(notificationMethod) == 'Y';
    }

    public boolean isSendEmails()
    {
        return supportsMethod(METHOD_EMAIL);
    }

    public boolean isSendSms()
    {
        return supportsMethod(METHOD_SMS);
    }

	public boolean isSendOutboundCalls()
	{
		return supportsMethod(METHOD_VOICE);
	}

    public void setSendEmails( boolean t )
    {
        setChars( METHOD_EMAIL, (t ? 'Y' : 'N') );
    }

    public void setSendSms( boolean t )
    {
        setChars( METHOD_SMS, (t ? 'Y' : 'N') );
    }

	public void setSendOutboundCalls( boolean t )
	{
		setChars( METHOD_VOICE, (t ? 'Y' : 'N') );
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
