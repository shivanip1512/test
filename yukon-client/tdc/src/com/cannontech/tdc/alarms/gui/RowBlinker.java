package com.cannontech.tdc.alarms.gui;

/**
 * Insert the type's description here.
 * Creation date: (4/12/00 4:35:21 PM)
 * @author: 
 * @Version: <version>
 */
 
import com.cannontech.tdc.utils.TDCDefines;
import com.cannontech.clientutils.CTILogger;


public class RowBlinker implements Runnable 
{
	private java.applet.AudioClip alarmSound = null;
	private AlarmingRowVector alarmedRows = null;
	private AlarmTableModel model = null;
	private Thread runningThread = null;

	/**
	 * Insert the type's description here.
	 * Creation date: (7/28/00 5:28:12 PM)
	 * @author: 
	 */

	public class AlarmTableModelPainter implements Runnable 
	{
		private int min = 0;
		private int max = 0;
		private AlarmTableModel model = null;
      


		/**
		 * RunnablePainter constructor comment.
		 */
		public AlarmTableModelPainter( AlarmTableModel modelHolder, int minimum, int maximum ) 
		{
			super();

			model = modelHolder;
			min = minimum;
			max = maximum;
		}

		public int getMax() 
		{
			return max;
		}

		public int getMin() 
		{
			return min;
		}

		public void setModel(AlarmTableModel newModel) 
		{
			model = newModel;
		}

		public void setMin(int newMin) 
		{
			min = newMin;
		}

		public void setMax(int newMax) 
		{
			max = newMax;
		}
		
		public AlarmTableModel getModel() 
		{
			return model;
		}

		public void run() {}
		
	}
	
/**
 * RowBlinker constructor comment.
 */
public RowBlinker( AlarmTableModel dataModel, AlarmingRowVector alarmedRows )
{
	super();

	this.alarmedRows = alarmedRows;
	this.model = dataModel;
}

public void start()
{
	runningThread = new Thread( this , "TDCAlarmColorAndSound" );
	runningThread.setDaemon(true);
	runningThread.start();
}

/**
 * Insert the method's description here.
 * Creation date: (2/7/2001 3:16:14 PM)
 */
public void destroy() 
{
   if( runningThread != null )
   {
   	runningThread.interrupt();
   	runningThread = null;	
   }
   
}
/**
 * Insert the method's description here.
 * Creation date: (4/4/00 5:01:45 PM)
 * Version: <version>
 */
private java.applet.AudioClip getAlarmSound() 
{	
	if( alarmSound == null )
	{
		java.net.URL url = null;
		
		try
		{
			url = new java.net.URL( "file:/" + TDCDefines.ALARM_SOUND_FILE );
		}
		catch( java.net.MalformedURLException e )
		{
			handleException( e );
		}
		
		alarmSound = java.applet.Applet.newAudioClip( url );
	}
	
	return alarmSound;
}
/**
 * Insert the method's description here.
 * Creation date: (1/12/2001 1:08:12 PM)
 * @return com.cannontech.tdc.alarms.gui.AlarmTableModel
 */
private AlarmTableModel getModel() {
	return model;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) 
{
	/* Uncomment the following lines to print uncaught exceptions to stdout */
	CTILogger.info("--------- UNCAUGHT EXCEPTION --------- " + Thread.currentThread().toString() );
	CTILogger.error( exception.getMessage(), exception );;	
}
private synchronized void processAlarmColors()
{
	int maxLoc = 0, minLoc = 0;

	for( int i = 0; i < alarmedRows.size(); i++ )
	{
		if( alarmedRows.elementAt(i).isBlinking() )
		{			
			int loc = alarmedRows.elementAt(i).getRowNumber();

			//only do things one time in here
			if( i == 0 )
			{
				//we cant be muted and there has to be at least 1 unsilenced row
				if( !model.isMuted() && alarmedRows.isAnyRowUnSilenced() )
					getAlarmSound().play();

				maxLoc = loc;
				minLoc = loc;
			}
				
			if( loc > maxLoc )
				maxLoc = loc;
			else if( loc < minLoc )
				minLoc = loc;


			getModel().setBGRowColor( loc, alarmedRows.elementAt(i).getAlarmColor() );			
		}								
	}

	javax.swing.SwingUtilities.invokeLater( new AlarmTableModelPainter( model, minLoc, maxLoc )
		{	public void run()
			{
				getModel().forcePaintTableRowUpdated( getMin(), getMax() );
			}				
		}			
	);
}
private synchronized void processOriginalColors()
{
	int maxLoc = 0, minLoc = 0;
	
	for( int i = 0; i < alarmedRows.size(); i++ )
	{
		if( alarmedRows.elementAt(i).isBlinking() )
		{			
			int loc = alarmedRows.elementAt(i).getRowNumber();

			if( i == 0 )
			{
				maxLoc = loc;
				minLoc = loc;
			}
				
			if( loc > maxLoc )
				maxLoc = loc;
			else if( loc < minLoc )
				minLoc = loc;

			getModel().setBGRowColor( loc , alarmedRows.elementAt( i ).getOriginalColor() );
		}
	}

	javax.swing.SwingUtilities.invokeLater( new AlarmTableModelPainter( model, minLoc, maxLoc )
		{	public void run()
			{
				getModel().forcePaintTableRowUpdated( getMin(), getMax() );
			}				
		}			
	);

}


/**
 * run method comment.
 */
public void run() 
{
	try
	{
		while( alarmedRows != null && alarmedRows.size() > 0 
					&& runningThread != null && !runningThread.isInterrupted() )
		{
			int size = 0;
			
         if( !TDCDefines.isAlarmColorHidden(TDCDefines.USER_RIGHTS) )
         {
   			synchronized( alarmedRows )
   			{
   				size = alarmedRows.size();
   
   				if( size == 0 )
   				{
   					runningThread = null;
   					return;
   				}
   				
   				try
   				{
   					processAlarmColors();
   						
   					Thread.sleep( 700 ); //length of time to be Alarm Color				
   				}
   				catch ( InterruptedException e )
   				{
   					CTILogger.debug("Thread " + Thread.currentThread().getName() + " was inturrupted during AlarmColor.");
   				}
   				finally
   				{
   					processOriginalColors();
   					
   				} //end of finally
   			} // end Synch alarmedRows
         }
			
			try
			{
				Thread.sleep( 1200 );// length of time to be original BG color
			}
			catch( InterruptedException e )
			{
				processOriginalColors();
				CTILogger.debug("Thread " + Thread.currentThread().getName() + " was inturrupted during OriginalColor.");
			}
		}
		
	}
	catch( Throwable t )
	{
		handleException(t);
	}

	runningThread = null;
}
}
