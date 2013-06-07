package com.cannontech.macs.gui;

/**
 * This type was created in VisualAge.
 */
import java.awt.Color;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Vector;

import com.cannontech.macs.events.MACSGenericTableModelEvent;
import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.macs.DeleteScheduleMessage;
import com.cannontech.messaging.message.macs.ScheduleMessage;
import com.cannontech.messaging.util.MessageEvent;
import com.cannontech.messaging.util.MessageListener;

public class ScheduleTableModel extends javax.swing.table.AbstractTableModel implements MessageListener, com.cannontech.common.gui.util.SortableTableModel 
{
	private SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy E HH:mm");
	private Font modelFont = new Font("dialog", Font.PLAIN, 12);
	
	/* ROW DATA HERE */
	private Vector allSchedules = null;
	private List currentSchedules = null;

  	// the string for filtering all areas
  	public static final String ALL_FILTER = "All Categories";
  	// the holder for the current filter, default to all
	private String filter = ALL_FILTER;

	//The columns and their column index
	private static final int CATEGORY_NAME = 0;
	private static final int SCHEDULE_NAME = 1;
	private static final int CURRENT_STATE = 2;
	private static final int START_TIME = 3;
  	private static final int STOP_TIME = 4;
 
	//The column names based on their column index
	private static final String[] COLUMN_NAMES =
	{
		"Category Name",
		"Schedule Name",
		"Current State",
		"Start Time",
		"Stop Time"
	};

	// default BG color	
	private Color backGroundColor = Color.black;

	//The color schemes - based on the schedule status
	private Color[] cellColors =
	{
		//Waiting schedule
		Color.white,
		//Running schedule
		Color.green,
		//Disabled schedule
		Color.red,
		//Pending schedule
		Color.yellow
	};

	public static final java.util.Comparator SCHED_CAT_COMPARATOR = new java.util.Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			String thisVal = ((ScheduleMessage)o1).getCategoryName();
			String anotherVal = ((ScheduleMessage)o2).getCategoryName();
			return( thisVal.compareToIgnoreCase(anotherVal) );
		}
	};

	
/**
 * ScheduleTableModel constructor comment.
 */
public ScheduleTableModel() {
	super();
	setFilter( ALL_FILTER );
}
/**
 * Insert the method's description here.
 * Creation date: (3/20/00 11:40:31 AM)
 */
public void clear() 
{
	getAllSchedules().removeAllElements();

	currentSchedules = getAllSchedules();
	
	fireTableDataChanged();
}
/**
 * This method was created in VisualAge.
 * @param event TableModelEvent
 */
public void fireTableChanged(javax.swing.event.TableModelEvent event) {
	super.fireTableChanged(event);
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Color
 * @param row int
 * @param col int
 */
public java.awt.Color getCellBackgroundColor(int row, int col) 
{
	return backGroundColor;
	
/*	if( schedules != null && schedules.length > row && col < 5 )
	{
		String status = schedules[row].getState();

		if( status.equals(Schedule.Waiting) )
		{
			return cellColors[0][1];	
		}
		else
		if( status.equals(Schedule.Running)  )
		{
			return cellColors[1][1];
		}
		else
		if( status.equals(Schedule.Deactivated) )
		{
			return cellColors[2][1];
		}
		else
		if( status.equals(Schedule.Pending ) )
		{
			return cellColors[3][1];
		}	
	}

	return Color.black;
*/
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Color
 * @param row int
 * @param col int
 */
public java.awt.Color getCellForegroundColor(int row, int col) 
{
	ScheduleMessage schedule = getSchedule(row);

	if( schedule != null && col < COLUMN_NAMES.length )
	{
		synchronized( schedule ) 
		{			
			String status = schedule.getCurrentState();

			if( status.equals(ScheduleMessage.STATE_WAITING) )
			{
				return cellColors[0];	
			}
			else
			if( status.equals(ScheduleMessage.STATE_RUNNING) )
			{
				return cellColors[1];
			}
			else
			if( status.equals(ScheduleMessage.STATE_DISABLED) )
			{
				return cellColors[2];
			}
			else
			if( status.equals(ScheduleMessage.STATE_PENDING ) )
			{
				return cellColors[3];
			}
		}	
	
	}

	return Color.white;
}
/**
 * getColumnCount method comment.
 */
public int getColumnCount() {
	return COLUMN_NAMES.length;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param index int
 */
public String getColumnName(int index) {
	return COLUMN_NAMES[index];
}

/**
 * Insert the method's description here.
 * Creation date: (2/23/2001 11:16:00 AM)
 * @return java.lang.String
 */
public java.lang.String getFilter() {
	return filter;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Font
 * @param row int
 * @param col int
 */
public Font getModelFont() 
{
	return modelFont;
}
/**
 * getRowCount method comment.
 */
public int getRowCount() 
{
	return getCurrentSchedules().size();
}
/**
 * This method returns the value of a row in the form of a Schedule object.
 * @return com.cannontech.macs.Schedule
 * @param rowIndex int
 */
public ScheduleMessage getSchedule(int rowIndex) 
{
	
	if( rowIndex < 0 || rowIndex >= getRowCount() )
		return null;

	return (ScheduleMessage)getCurrentSchedules().get(rowIndex);		
}

private synchronized Vector getAllSchedules()
{
	if( allSchedules == null )
		allSchedules = new Vector(10);
		
	return allSchedules;
}

private List getCurrentSchedules()
{
	if( currentSchedules == null )
		currentSchedules = getAllSchedules();

	return currentSchedules;
}

/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) 
{
	ScheduleMessage sched = getSchedule( row );


	switch( col )
	{
	 	case CATEGORY_NAME:
			return sched.getCategoryName();
			
	 	case SCHEDULE_NAME:
			return sched.getScheduleName();
			
		case CURRENT_STATE:
		{
			String state = sched.getCurrentState();								
			return state;
		}
			
		case START_TIME:
			String dateString;
			if( sched.getCurrentState().equals( ScheduleMessage.STATE_RUNNING ) )
			{
				if( sched.getLastRunTime().getTime() <= ScheduleMessage.INVALID_DATE )
					return " ----";
				else
					dateString = formatter.format( sched.getLastRunTime() );
			}
			else
			{
				if( sched.getNextRunTime().getTime() <= ScheduleMessage.INVALID_DATE )
					return " ----";
				else
					dateString = formatter.format( sched.getNextRunTime() );
			}
				
			dateString = " " + dateString;					
			return dateString;
			
		case STOP_TIME:
			if( sched.getNextStopTime().getTime() <= ScheduleMessage.INVALID_DATE )
				return " ----";
			else
			{
				dateString = formatter.format( sched.getNextStopTime() );
				dateString = " " + dateString;
				return dateString;
			}
			
		default:
			return null;
	}
				
	
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param row int
 * @param column int
 */
public boolean isCellEditable(int row, int column) {
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 1:13:45 PM)
 * @param newBackGroundColor java.awt.Color
 */
public void setBackGroundColor(java.awt.Color newBackGroundColor) 
{
	backGroundColor = newBackGroundColor;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 1:11:46 PM)
 * @param newCellColors java.awt.Color[]
 */
public void setCellColors(java.awt.Color[] newCellColors) 
{
	if( newCellColors.length >= getColumnCount() )
		cellColors = newCellColors;
}

/**
 * Insert the method's description here.
 * Creation date: (2/23/2001 11:16:00 AM)
 * @param newFilter java.lang.String
 */
public void setFilter(java.lang.String newFilter) 
{
	filter = newFilter;

	//ensure our list is prior to any category name grouping
	java.util.Collections.sort(
		getAllSchedules(),
		SCHED_CAT_COMPARATOR );

		
   //need to refresh all of our schedules   
   if( ALL_FILTER.equalsIgnoreCase(getFilter()) )
   {
		currentSchedules = getAllSchedules();
   }
	else
	{
		int start = -1, stop = getAllSchedules().size();
		for( int i = 0; i < getAllSchedules().size(); i++ )
		{
			ScheduleMessage realSched = (ScheduleMessage)getAllSchedules().get(i);
			if( start <= -1 && realSched.getCategoryName().equalsIgnoreCase(getFilter()) )
			{
				start = i;
			}
			else if( start >= 0 
						 && !realSched.getCategoryName().equalsIgnoreCase(getFilter()) )
			{
				stop = i;
				break;
			}

		}

		
		if( start < 0 ) //should not occur
		{
			currentSchedules = new Vector();
			com.cannontech.clientutils.CTILogger.info("*** Could not find Schedule with the cateogyr = " 
					+ getFilter() );
		}
		else  //this locks down AllSubBuses and disallows any structural modification to AllSubBuses
			currentSchedules = getAllSchedules().subList(
										start, 
										(stop < 0 || stop > getAllSchedules().size() ? start+1 : stop) );
					
	}

	fireTableChanged( new MACSGenericTableModelEvent(
		this, MACSGenericTableModelEvent.FILTER_CHANGE) );
}

/**
 * Insert the method's description here.
 * Creation date: (8/14/00 2:16:54 PM)
 * @param int
 */
public void setModelFont(String name, int size) 
{
	modelFont = new Font( name, Font.PLAIN, size );
}

private int findScheduleIndx( ScheduleMessage s )
{
	for( int i = 0; i < getRowCount(); i++ )
		if( s.getId() == getSchedule(i).getId() )
			return i;
	
	return -1;
}

/**
 * This method was created in VisualAge.
 * 
 */
//public synchronized void update(Observable source, Object obj )
public void messageReceived( MessageEvent e ) 
{
	BaseMessage in = e.getMessage();

	if( in instanceof ScheduleMessage
	    || in instanceof DeleteScheduleMessage )
	{		
		int oldRowCount = getRowCount();		
		boolean changeSize = false;
		
		if( in instanceof ScheduleMessage )
		{		
			ScheduleMessage sched = (ScheduleMessage)in;
				
			boolean found = false;
	
			for( int j = 0 ; j < getAllSchedules().size(); j++ )
			{
				ScheduleMessage row = (ScheduleMessage)getAllSchedules().get(j);
				if( row.equals(sched) )
				{
					//we may have to redo our Sublists if the Category changed
					if( !row.getCategoryName().equalsIgnoreCase( sched.getCategoryName() ) )
					{
						getAllSchedules().remove( row );
						found = false;
						break;  //act like we did not find the schedule
					}
	
					getAllSchedules().setElementAt( sched, j );
					found = true;
				}
			}
	
			if( !found )
			{
				changeSize = true;
	
				//always keep our main list in order by the Category
				// find the first Schedule with the same Category
				int indx = java.util.Collections.binarySearch( 
						getAllSchedules(), 
						sched, 
						SCHED_CAT_COMPARATOR );
	
				if( indx < 0 )
					getAllSchedules().add( sched );
				else
					getAllSchedules().add( indx, sched );
			}
	
		}
		else if( in instanceof DeleteScheduleMessage )
		{
			DeleteScheduleMessage dSched = (DeleteScheduleMessage)in;

			for( int j = 0 ; j < getAllSchedules().size(); j++ )
			{
				ScheduleMessage row = (ScheduleMessage)getAllSchedules().get(j);
				if( row.getId() == dSched.getScheduleId() )
				{
					changeSize = true;
					
					getAllSchedules().remove( j );
					break;
				}
			}
			
		}
		
		
		if( changeSize )
		{	
		   setFilter( getFilter() );
		}


		//by using fireTableRowsUpdated(int,int) we do not clear the table selection		
		if( oldRowCount == getRowCount() )
			fireTableRowsUpdated( 0, getRowCount()-1 );
		else
			fireTableDataChanged();		
	}
	else
	{
		javax.swing.SwingUtilities.invokeLater( new Runnable()
		{
			public void run()
			{
				// if we fireTableDataChanged(), we loose our selected schedule (JVM 1.3)
				fireTableDataChanged();
			}
		});
	}
	
}


	/**
	 * This method add a Blank row to the table
	 */
	 
	public boolean isRowSelectedBlank( int location )
	{
		return false;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (4/14/00 11:33:17 AM)
	 * Version: <version>
	 */
	public synchronized void rowDataSwap( int i, int j )
	{
		Object tmp = null;

		tmp = getCurrentSchedules().get(i);
		getCurrentSchedules().set( i, getCurrentSchedules().get(j) );
		getCurrentSchedules().set( j, tmp );

	}
}
