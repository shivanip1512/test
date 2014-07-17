/*
 * Created on Aug 5, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.fdemulator.common;

/**
 * @author asolberg
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class FdeLogger implements Runnable
{

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */

	private String message;
	private int color;
	TraceLogPanel log;

	public FdeLogger(TraceLogPanel logger, String messageString, int colorcode)
	{
		log = logger;
		message = messageString;
		color = colorcode;
	}

	public void run()
	{
		log.append(message, color);
	}

}
