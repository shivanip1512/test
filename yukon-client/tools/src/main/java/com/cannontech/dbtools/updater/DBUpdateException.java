package com.cannontech.dbtools.updater;

/**
 * @author rneuharth
 *
 * An exception that is seen when a problem occurs while updating a DB.
 */
public class DBUpdateException extends Exception
{

	/**
	 * 
	 */
	public DBUpdateException()
	{
		super();
	}

	/**
	 * @param message
	 */
	public DBUpdateException(String message)
	{
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DBUpdateException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public DBUpdateException(Throwable cause)
	{
		super(cause);
	}

}
