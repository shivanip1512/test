package com.cannontech.macs.gui;

/**
 * This type was created in VisualAge.
 */
import java.awt.Color;
import java.awt.Font;
import java.util.List;
import java.util.Vector;

import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.macs.message.DeleteSchedule;
import com.cannontech.message.macs.message.Schedule;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageListener;

public class ScheduleTableModel extends javax.swing.table.AbstractTableModel implements MessageListener, com.cannontech.common.gui.util.SortableTableModel 
{
	private java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("MM-dd-yyyy E HH:mm");
	private Font modelFont = new Font("dialog", Font.PLAIN, 12);
	
	/* ROW DATA HERE */
	private Vector allSchedules = null;
	private List currentSchedules = null;
	private java.util.Hashtable filterTable = new java.util.Hashtable(10); 

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
			String thisVal = ((Schedule)o1).getCategoryName();
			String anotherVal = ((Schedule)o2).getCategoryName();
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
	filterTable.clear();

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
	Schedule schedule = getSchedule(row);

	if( schedule != null && col < COLUMN_NAMES.length )
	{
		synchronized( schedule ) 
		{			
			String status = schedule.getCurrentState();

			if( status.equals(Schedule.STATE_WAITING) )
			{
				return cellColors[0];	
			}
			else
			if( status.equals(Schedule.STATE_RUNNING) )
			{
				return cellColors[1];
			}
			else
			if( status.equals(Schedule.STATE_DISABLED) )
			{
				return cellColors[2];
			}
			else
			if( status.equals(Schedule.STATE_PENDING ) )
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
public Schedule getSchedule(int rowIndex) 
{
	
	if( rowIndex < 0 || rowIndex >= getRowCount() )
		return null;

	return (Schedule)getCurrentSchedules().get(rowIndex);		
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
	Schedule sched = getSchedule( row );


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
			if( sched.getCurrentState().equals( Schedule.STATE_RUNNING ) )
			{
				if( sched.getLastRunTime().getTime() <= Schedule.INVALID_DATE )
					return " ----";
				else
					dateString = formatter.format( sched.getLastRunTime() );
			}
			else
			{
				if( sched.getNextRunTime().getTime() <= Schedule.INVALID_DATE )
					return " ----";
				else
					dateString = formatter.format( sched.getNextRunTime() );
			}
				
			dateString = " " + dateString;					
			return dateString;
			
		case STOP_TIME:
			if( sched.getNextStopTime().getTime() <= Schedule.INVALID_DATE )
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

		
   //need to refresh all of our schedules   
   if( ALL_FILTER.equalsIgnoreCase(getFilter()) )
   {
		currentSchedules = getAllSchedules();
   }
	else
	{
		java.util.List l = (java.util.List)filterTable.get( getFilter() );
		if( l != null )
		{
			//we already have a sublist for this filter, use it!
			currentSchedules = l;
		}
		else
		{
			int start = -1, stop = getAllSchedules().size();
			for( int i = 0; i < getAllSchedules().size(); i++ )
			{
				Schedule realSched = (Schedule)getAllSchedules().get(i);
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
				currentSchedules = getAllSchedules();
				com.cannontech.clientutils.CTILogger.info("*** Could not find Schedule with the cateogyr = " 
						+ getFilter() );
			}
			else  //this locks down AllSubBuses and disallows any structural modification to AllSubBuses
				currentSchedules = getAllSchedules().subList(
											start, 
											(stop < 0 || stop > getAllSchedules().size() ? start+1 : stop) );
						
			filterTable.put( getFilter(), currentSchedules );
		}
	}


   fireTableChanged(
   	new com.cannontech.macs.events.MACSGenericTableModelEvent( this,
	   com.cannontech.macs.events.MACSGenericTableModelEvent.FILTER_CHANGE) );
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

private int findScheduleIndx( Schedule s )
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
	Message in = e.getMessage();

	if( in instanceof Schedule
	    || in instanceof DeleteSchedule )
	{		
		int oldRowCount = getRowCount();		
		boolean changeSize = false;
		
		if( in instanceof Schedule )
		{		
			Schedule sched = (Schedule)in;
				
			boolean found = false;
	
			for( int j = 0 ; j < getAllSchedules().size(); j++ )
			{
				Schedule row = (Schedule)getAllSchedules().get(j);
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
		else if( in instanceof DeleteSchedule )
		{
			DeleteSchedule dSched = (DeleteSchedule)in;

			for( int j = 0 ; j < getAllSchedules().size(); j++ )
			{
				Schedule row = (Schedule)getAllSchedules().get(j);
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
			//since we increased the size of AllSubBuses, we must release all filter sublist
		  	filterTable.clear();
	
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

		tmp = getAllSchedules().get(i);
		getAllSchedules().set( i, getAllSchedules().get(j) );
		getAllSchedules().set( j, tmp );

	}
}
