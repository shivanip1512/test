package com.cannontech.message.dispatch.message;

/**
 * Insert the type's description here.
 * Creation date: (5/1/2002 10:58:58 AM)
 * @author: 
 */
public class EmailMsg extends com.cannontech.message.util.Message 
{
	public final int DEFAULT_EMAIL_TYPE = 0;
	public final int CICUSTOMER_EMAIL_TYPE = 1;
	public final int DEVICEID_EMAIL_TYPE = 2;
	public final int POINTID_EMAIL_TYPE = 3;
	public final int NGROUPID_EMAIL_TYPE = 4;
	public final int LOCATION_EMAIL_TYPE = 5;
	public final int INVALID_EMAIL_TYPE = 6;
	
	//class ID = 1597
	private long id;	
	private int type;	//id type
	private String sender = "";
	private String subject = "";
	private String text = "";
/**
 * EmailMsg constructor comment.
 */
public EmailMsg() {
	super();
}
public long getId()
{
	return id;
}
public String getSender()
{
	return sender;
}
public String getSubject()
{
	return subject;
}
public String getText()
{
	return text;
}
public int getType()
{
	return type;
}
public void setId(long newId)
{
	id = newId;
}
public void setSender(String newSender)
{
	sender = newSender;
}
public void setSubject(String newSubject)
{
	subject = newSubject;
}
public void setText(String newText)
{
	text = newText;
}
public void setType(int newType)
{
	type = newType;
}
/**
 * Insert the method's description here.
 * Creation date: (1/31/00 4:44:25 PM)
 * @return java.lang.String
 */
public String toString()
{
	String retStr = this.getClass().toString() + ":  \n";

	retStr += "Id:  " + getId() + "\n";
	retStr += "Type:  " + getType() + "\n";
	retStr += "Sender:  " + getSender() + "\n";
	retStr += "Subject:  " + getSubject() + "\n";
	retStr += "Text:  " + getText() + "\n";

	return retStr;
}
}
