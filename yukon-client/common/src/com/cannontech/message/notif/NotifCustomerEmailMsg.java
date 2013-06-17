package com.cannontech.message.notif;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.message.util.Message;

/**
 * This is a replacement for the old CtiEmailMsg and should go away someday.
 * Don't use this for new code!
 * Use the base classes message string to 
 * give a description of the error.
 */
public class NotifCustomerEmailMsg extends Message
{
	//if notifGroupID != CtiUtilities.NONE_ZERO_ID, then all valid values for the group is used in the email
	private int customerID = CtiUtilities.NONE_ZERO_ID; 
	
	private String subject = "";	//email subject text
	private String body = "";	//email body text
	
	public NotifCustomerEmailMsg() {
		super();
	}

	public String getBody()
	{
		return body;
	}

	public String getSubject()
	{
		return subject;
	}

	public void setBody(String string)
	{
		body = string;
	}

	public void setSubject(String string)
	{
		subject = string;
	}

	public int getCustomerID() {
		return customerID;
	}

	public void setCustomerID(int customerID) {
		this.customerID = customerID;
	}

}
