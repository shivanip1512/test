package com.cannontech.yukon.conns;

/**
 * @author ryan
 *
 */
public class NotifRequestException extends RuntimeException
{

	/**
	 * 
	 */
	public NotifRequestException()
	{
		super();
	}

	/**
	 * @param message
	 */
	public NotifRequestException(String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public NotifRequestException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public NotifRequestException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
