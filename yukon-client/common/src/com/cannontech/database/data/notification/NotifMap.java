package com.cannontech.database.data.notification;

import java.util.Iterator;
import java.util.NoSuchElementException;


/**
 * @author ryan
 *
 *	Common base class for notifcations
 */
public class NotifMap implements java.io.Serializable, Iterable<NotifType>
{
	//Y or N fields for the notifcation attributes:
	// [0]:send email, [1]:make phone call, [2]:send short email
	private String attribs = DEF_ATTRIBS;
	public static final String DEF_ATTRIBS = "YNYNNNNNNNNNNNNN";

	public NotifMap()
	{
	}

	public NotifMap( String attribs )
	{
		setAttribs( attribs );
	}
    
    @Override
    public Iterator<NotifType> iterator() {
        return new Iterator<NotifType>() {
            private int nextIndex = 0;
            @Override
            public boolean hasNext() {
                int tempNextIndex = nextIndex;
                while (tempNextIndex < NotifType.values().length && !supportsMethod(NotifType.values()[tempNextIndex])) {
                    tempNextIndex++;
                }
                return (tempNextIndex < NotifType.values().length);
            }
            @Override
            public NotifType next() {
                while (nextIndex < NotifType.values().length && !supportsMethod(NotifType.values()[nextIndex])) {
                    nextIndex++;
                }
                if (nextIndex < NotifType.values().length) {
                    return NotifType.values()[nextIndex++];
                }
                throw new NoSuchElementException();
            }
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
    
    public boolean supportsMethod(NotifType notificationMethod) {
        return getAttribs().charAt(notificationMethod.getAttribPosition()) == 'Y';
    }
    
    public void setSupportsMethod(NotifType notificationMethod, boolean support) {
        setChars(notificationMethod.getAttribPosition(), support?'Y':'N');
    }

    public boolean isSendEmails()
    {
        return supportsMethod(NotifType.EMAIL);
    }

    public boolean isSendSms()
    {
        return supportsMethod(NotifType.SMS);
    }

	public boolean isSendOutboundCalls()
	{
		return supportsMethod(NotifType.VOICE);
	}

    public void setSendEmails( boolean t )
    {
        setChars( NotifType.EMAIL.getAttribPosition(), (t ? 'Y' : 'N') );
    }

    public void setSendSms( boolean t )
    {
        setChars( NotifType.SMS.getAttribPosition(), (t ? 'Y' : 'N') );
    }

	public void setSendOutboundCalls( boolean t )
	{
		setChars( NotifType.VOICE.getAttribPosition(), (t ? 'Y' : 'N') );
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
	 * @param string
	 */
	public void setAttribs(String string)
	{
		if( attribs == null || attribs.length() != DEF_ATTRIBS.length() )
			throw new IllegalStateException("The attribs attribute can not be null and must be " + DEF_ATTRIBS.length() + " characters long");

		attribs = string;
	}

}
