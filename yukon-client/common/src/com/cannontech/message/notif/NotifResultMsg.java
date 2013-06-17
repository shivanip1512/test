package com.cannontech.message.notif;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.message.util.Message;

/**
 * Use the base classes message string to 
 * give a description of the error.
 */
public class NotifResultMsg extends Message
{
	public static final int RESULT_SUCCESS = 0;
	public static final int RESULT_FAILED = 1;
	
	private int result = RESULT_FAILED;
	private String message = null;

	/**
	 * NotifErrorMsg constructor comment.
	 */
	public NotifResultMsg() {
		super();
	}

	/**
	 * NotifErrorMsg constructor comment.
	 */
	public NotifResultMsg( String msg_, int retResult_ ) 
	{
		super();
		setMessage( msg_ );
		setResult( retResult_ );
		
		//always print out this message to our logger
		CTILogger.debug( msg_ );
	}
	
	/**
	 * @return
	 */
	public int getResult()
	{
		return result;
	}

	/**
	 * @param i
	 */
	public void setResult(int i)
	{
		result = i;
	}

	/**
	 * @return
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * @param string
	 */
	public void setMessage(String string)
	{
		message = string;
	}

}
