package com.cannontech.tdc;

import java.util.Date;
import java.util.GregorianCalendar;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.tdc.data.Display;

/**
 * Insert the type's description here
 * Creation date: (1/20/00 2:25:13 PM)
 * @author: Ryan (THIS IS A TEST)
 */
public class Clock implements Runnable 
{
	private Thread timer = null;
	private TDCMainFrame mainFrame = null;
	private GregorianCalendar gc = new GregorianCalendar();

;
	
/**
 * yet another constructor comment.
 */
public Clock( TDCMainFrame origin ) {
	super();

	mainFrame = origin;
	
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
				Date date = new Date();
				gc.setTime( date );

				
				mainFrame.getMainPanel().getJLabelDate().setText(dateformatter.format(date));
				mainFrame.getMainPanel().getJLabelTime().setText(timeformatter.format(date));
				
				//if we are looking at todays date AND we are about to flip 
				if( gc.get(GregorianCalendar.HOUR_OF_DAY) == 23
				    && gc.get(GregorianCalendar.MINUTE) == 59
				    && gc.get(GregorianCalendar.SECOND) == 59
					 && Display.isTodaysDate(
					 	mainFrame.getMainPanel().getTableDataModel().getCurrentDate()) )
				{
					if( mainFrame.getAlarmToolBar().setSelectedDate(new Date()) )
						CTILogger.info("AUTO-CHANGED: Changed current display to the rolled over new date: " + date );
				}
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
			break; // we were interrupted, lets skidaddle
		}

	}	
	
}

}
