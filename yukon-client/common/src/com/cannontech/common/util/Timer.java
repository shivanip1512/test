/*
 * Created on Sep 21, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.common.util;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Timer extends Thread
{
	public static final int STOPPED = 0;
	public static final int RUNNING = 1;
	
	private int status = STOPPED;
	/* Time in Millis to run before stopping */
	private long timeOut = 0;
	/* Time in Millis thread has run */
	private long currentRunTime = 0;
	/**
	 * @param name
	 */
	public Timer(String name)
	{
		super(name);
	}

	public void run()
	{
		while(true)
		{
			try
			{
				Thread.sleep(1000);
				currentRunTime +=1000;	//increment in millis
				if( currentRunTime > getTimeOut())
				{
					setStatus(STOPPED);
					this.interrupt();
				}
			}
			catch( InterruptedException e )
			{ 
				return; 
			}
		}

	}
	
	public void start(long timeOutInMillis)
	{
		currentRunTime = 0;	//init
		setTimeOut(timeOutInMillis);
		setStatus(RUNNING);
		super.start();
	}
	/**
	 * @return
	 */
	public long getTimeOut()
	{
		return timeOut;
	}

	/**
	 * @param l
	 */
	public void setTimeOut(long l)
	{
		timeOut = l;
	}

	/**
	 * @return
	 */
	public int getStatus()
	{
		return status;
	}

	/**
	 * @param i
	 */
	public void setStatus(int i)
	{
		status = i;
	}

}
