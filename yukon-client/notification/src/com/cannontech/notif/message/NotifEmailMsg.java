package com.cannontech.notif.message;

import java.util.Vector;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.message.util.Message;

/**
 * Use the base classes message string to 
 * give a description of the error.
 */
public class NotifEmailMsg extends Message
{
	private String to = ""; //a comma separated string of email addresses.
	
	//if notifGroupID != CtiUtilities.NONE_ZERO_ID, then all valid values for the group is used in the email
	private int notifGroupID = CtiUtilities.NONE_ZERO_ID; //a notification group id
	
	private String subject = "";	//email subject text
	private String body = "";	//email body text
	
	private String to_CC = ""; //a comma separated string of email addresses for CC
	private String to_BCC = ""; //a comma separated string of email addresses for BC

	private Vector attachments = null; //contains NotifEmailAttchMsg instances 

	/**
	 * NotifEmailMsg constructor comment.
	 */
	public NotifEmailMsg() {
		super();
	}

	/**
	 * Contains NotifEmailAttchMsg instances
	 * @return
	 */
	public Vector getAttachments()
	{
		if( attachments == null )
			attachments = new Vector(8);

		return attachments;
	}

	/**
	 * Gets specific attachment instance
	 * @return NotifEmailAttchMsg
	 */
	public NotifEmailAttchMsg getAttachmentAt( int i )
	{
		return (NotifEmailAttchMsg)attachments.get(i);
	}

	/**
	 * @return
	 */
	public String getBody()
	{
		return body;
	}

	/**
	 * @return
	 */
	public String getSubject()
	{
		return subject;
	}

	/**
	 * @return
	 */
	public String getTo()
	{
		return to;
	}

	/**
	 * @return
	 */
	public String getTo_BCC()
	{
		return to_BCC;
	}

	/**
	 * @return
	 */
	public String getTo_CC()
	{
		return to_CC;
	}

	/**
	 * @param vector
	 */
	public void setAttachments(Vector vector)
	{
		attachments = vector;
	}

	/**
	 * @param string
	 */
	public void setBody(String string)
	{
		body = string;
	}

	/**
	 * @param string
	 */
	public void setSubject(String string)
	{
		subject = string;
	}

	/**
	 * @param string
	 */
	public void setTo(String string)
	{
		to = string;
	}

	/**
	 * @param string
	 */
	public void setTo_BCC(String string)
	{
		to_BCC = string;
	}

	/**
	 * @param string
	 */
	public void setTo_CC(String string)
	{
		to_CC = string;
	}

	/**
	 * @return
	 */
	public int getNotifGroupID()
	{
		return notifGroupID;
	}

	/**
	 * @param i
	 */
	public void setNotifGroupID(int i)
	{
		notifGroupID = i;
	}

}
