package com.cannontech.message.notif;

import com.cannontech.message.util.Message;

/**
 * Used to represents an email attachment 
 */
public class NotifEmailAttchMsg extends Message
{
	private String fileName = null;
	private char[] fileContents = null; 

	/**
	 * NotifEmailAttchMsg constructor comment.
	 */
	public NotifEmailAttchMsg() 
	{
		super();
	}

	/**
	 * NotifEmailAttchMsg constructor comment.
	 */
	public NotifEmailAttchMsg( String fName_, char[] fContents_ ) 
	{
		this();
		setFileName( fName_ );
		setFileContents( fContents_ );
	}


	/**
	 * @return
	 */
	public char[] getFileContents()
	{
		return fileContents;
	}

	/**
	 * @return
	 */
	public String getFileName()
	{
		return fileName;
	}

	/**
	 * @param cs
	 */
	public void setFileContents(char[] cs)
	{
		fileContents = cs;
	}

	/**
	 * @param string
	 */
	public void setFileName(String string)
	{
		fileName = string;
	}

}
