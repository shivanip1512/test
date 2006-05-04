package com.cannontech.database.data.notification;

import java.util.Iterator;
import java.util.NoSuchElementException;


/**
 * @author ryan
 *
 *	Common base class for notifcations
 */
public class NotifMap implements java.io.Serializable, Iterable<Integer>
{
	//Y or N fields for the notifcation attributes:
	// [0]:send email, [1]:make phone call, [2]:send short email
	private String attribs = DEF_ATTRIBS;
	public static final String DEF_ATTRIBS = "YNYNNNNNNNNNNNNN";
    public static final int METHOD_EMAIL = 0;
    public static final int METHOD_VOICE = 1;
    public static final int METHOD_SMS = 2;
    
    public static final int[] ALL_METHODS = {METHOD_EMAIL,METHOD_VOICE,METHOD_SMS};


	public NotifMap()
	{
	}

	public NotifMap( String attribs )
	{
		setAttribs( attribs );
	}
    
    public Iterator<Integer> iterator() {
        return new Iterator<Integer>() {
            private int nextIndex = 0;
            public boolean hasNext() {
                int tempNextIndex = nextIndex;
                while (tempNextIndex < ALL_METHODS.length && !supportsMethod(ALL_METHODS[tempNextIndex])) {
                    tempNextIndex++;
                }
                return (tempNextIndex < ALL_METHODS.length);
            }
            public Integer next() {
                while (nextIndex < ALL_METHODS.length && !supportsMethod(ALL_METHODS[nextIndex])) {
                    nextIndex++;
                }
                if (nextIndex < ALL_METHODS.length) {
                    return ALL_METHODS[nextIndex++];
                }
                throw new NoSuchElementException();
            }
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
    
    public boolean supportsMethod(int notificationMethod) {
        return getAttribs().charAt(notificationMethod) == 'Y';
    }
    
    public void setSupportsMethod(int notificationMethod, boolean support) {
        setChars(notificationMethod, support?'Y':'N');
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
	 * @param string
	 */
	public void setAttribs(String string)
	{
		if( attribs == null || attribs.length() != DEF_ATTRIBS.length() )
			throw new IllegalStateException("The attribs attribute can not be null and must be " + DEF_ATTRIBS.length() + " characters long");

		attribs = string;
	}

}
