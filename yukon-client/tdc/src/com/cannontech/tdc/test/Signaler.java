package com.cannontech.tdc.test;

/**
 * This class provides a signaling mechanism for objects.
 * An object wishing to send a signal calls the signal method.
 * An object receiving the signal would wait for a signal with
 * waitForSignal.  If there is no signal pending, waitForSignal
 * will wait for one.  If there are multiple signals sent, the
 * class will keep track of how many were sent and will not call
 * wait until there are no more pending signals.
 * There should only be one object waiting for a signal at any given
 * time.
 * @version 1.0
 * @author Mark Wutka
 */
public class Signaler extends Object
{

	 protected int signalCount;     // the number of pending signals

	 protected boolean isWaiting;     // is an object waiting right now?

	 protected boolean sentNotify;     // Did someone send a notify?
/**

 * Creates an instance of a signaler

 */

	 public Signaler()

	 {

		  signalCount = 0;     // no pending signals

		  isWaiting = false;     // no one waiting

	 }
/**
 * Sends a signal to the object waiting for a signal.
 * @exception Exception if there is an error sending a notification
 */
public synchronized void signal() throws Exception
{
	  signalCount++;     // Increment the number of pending signals
	  
	  if (isWaiting)     // If an object is waiting, notify it
	  {
		   try 
		   {
				sentNotify = true;
				notify();

		   } 
		   catch (Exception IllegalMonitorStateException) 
		   {
					throw new Exception("Error sending notification");
		   }
	  }
}
/**
 * Waits for a signal.  If there are signals pending, this method will
 * return immediately.
 */
public synchronized void waitForSignal()
{

	while (signalCount == 0)     // If there are no signals pending, wait for a signal
	{
		sentNotify = false;
		isWaiting = true;     // Yes, someone is waiting
		
		// Want to keep looping until a notify is actually sent, it is possible
		// for wait to return without a notify, so use sentNotify to see if we
		// should go back to waiting again.
		while (!sentNotify)
		{
			try 
			{
				wait();
			} 
			catch (Exception waitError) 
			{
						 // Shouldn't really ignore this, but...
			}
		}
		
		isWaiting = false;     // I'm not waiting any more

	}
	signalCount--;     // one fewer signal pending
}
}
