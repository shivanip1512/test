package com.cannontech.tdc;

/**
 * Insert the type's description here
 * Creation date: (1/20/00 2:25:13 PM)
 * @author: Ryan (THIS IS A TEST)
 */
public class Clock implements Runnable 
{
	private Thread timer = null;
	private TDCMainPanel caller = null;

;
	
/**
 * Another Clock constructor comment.
 */
public Clock( TDCMainPanel origin ) {
	super();

	caller = origin;
	
	timer = new Thread( this, "TDCClock" );
	timer.setDaemon( true );
	timer.start();
}
/**
 * Insert the method's description here.
 * Creation date: (10/13/00 1:50:28 PM)
 */
public void interruptClockThread() 
{
	timer.interrupt();	
}
/**
 * run method comment.
 */
public void run() 
{

   final java.text.SimpleDateFormat dateformatter
	   = new java.text.SimpleDateFormat ("MM-dd-yyyy");
		  
   final java.text.SimpleDateFormat timeformatter
	   = new java.text.SimpleDateFormat ("HH:mm:ss");
	

	while ( true )
	{

		javax.swing.SwingUtilities.invokeLater( new Runnable()
		{
			public void run()
			{
				java.util.Date date = new java.util.Date();
				
				caller.getJLabelDate().setText(dateformatter.format(date));
				caller.getJLabelTime().setText(timeformatter.format(date));
			}
			
		});
			
		try 
		{
			if( Thread.currentThread().isInterrupted() )
				break; //we were interrupted while not in the Thread.sleep() mehod
			else
				Thread.currentThread().sleep(1000); 
		}
		catch (InterruptedException e) 
		{
			//System.out.println("com.cannontech.tdc.Clock thread interrupted");
			break; // we were interrupted, lets skidaddle
		}

	}	
	
}
}
