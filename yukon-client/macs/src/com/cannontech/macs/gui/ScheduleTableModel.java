package com.cannontech.macs.gui;

/**
 * This type was created in VisualAge.
 */
import java.awt.Color;
import java.awt.Font;

import java.util.Observer;
import java.util.Observable;

import com.cannontech.message.macs.message.Schedule;
import com.cannontech.macs.MACSClientConnection;

public class ScheduleTableModel extends javax.swing.table.AbstractTableModel implements java.util.Observer 
{
	private java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("MM-dd-yyyy E HH:mm");
	private Font modelFont = new Font("dialog", Font.PLAIN, 12);
	
	private MACSClientConnection connection = null;

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
	
/**
 * ScheduleTableModel constructor comment.
 */
public ScheduleTableModel() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (3/20/00 11:40:31 AM)
 */
public void clear() 
{
	getConnection().clearSchedules();
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
 * Creation date: (8/8/00 1:56:41 PM)
 * @return com.cannontech.macs.ClientConnection
 */
public com.cannontech.macs.MACSClientConnection getConnection() {
	return connection;
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
	if( getSchedules() != null )
		return getSchedules().length;
	else
		return 0;
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

	return getSchedules()[rowIndex];		
}
/**
 * This method returns the value of a row in the form of a Schedule object.
 * @return com.cannontech.macs.Schedule
 */
private Schedule[] getSchedules()
{
	synchronized( getConnection().retrieveSchedules() )
	{
		Schedule[] schedules = getConnection().retrieveSchedules();
		
		if( getFilter().equalsIgnoreCase(ALL_FILTER) )
		{
			return getConnection().retrieveSchedules();
		}
		else
		{
			if( getConnection().getCategoryNames() != null )
			{
				if( getConnection().getCategoryNames().get(getFilter()) != null )
				{
					java.util.ArrayList list = (java.util.ArrayList)getConnection().getCategoryNames().get(getFilter());
					Schedule[] scheds = new Schedule[list.size()];
					list.toArray( scheds );
					
					return scheds;
				}
			}
		}
		
	}
	
	return null;	
}
/**
 * getValueAt method comment.
 */
public Object getValueAt(int arg1, int arg2) 
{
	Schedule[] schedules = getSchedules();

	if( arg1 < schedules.length )
	{		
		switch( arg2 )
		{
		 	case CATEGORY_NAME:
				return schedules[arg1].getCategoryName();
				
		 	case SCHEDULE_NAME:
				return schedules[arg1].getScheduleName();
				
			case CURRENT_STATE:
			{
				String state = schedules[arg1].getCurrentState();								
				return state;
			}
				
			case START_TIME:
				String dateString;
				if( schedules[arg1].getCurrentState().equals( Schedule.STATE_RUNNING ) )
				{
					if( schedules[arg1].getLastRunTime().getTime() <= Schedule.INVALID_DATE )
						return " ----";
					else
						dateString = formatter.format( schedules[arg1].getLastRunTime() );
				}
				else
				{
					if( schedules[arg1].getNextRunTime().getTime() <= Schedule.INVALID_DATE )
						return " ----";
					else
						dateString = formatter.format( schedules[arg1].getNextRunTime() );
				}
					
				dateString = " " + dateString;					
				return dateString;
				
			case STOP_TIME:
				if( schedules[arg1].getNextStopTime().getTime() <= Schedule.INVALID_DATE )
					return " ----";
				else
				{
					dateString = formatter.format( schedules[arg1].getNextStopTime() );
					dateString = " " + dateString;
					return dateString;
				}
				
			default:
				return null;
		}
				
	}
	else
		return null;
	
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
 * Creation date: (8/8/00 1:56:41 PM)
 * @param newConnection com.cannontech.macs.ClientConnection
 */
public void setConnection(com.cannontech.macs.MACSClientConnection newConnection) {
	connection = newConnection;
}
/**
 * Insert the method's description here.
 * Creation date: (2/23/2001 11:16:00 AM)
 * @param newFilter java.lang.String
 */
public void setFilter(java.lang.String newFilter) 
{
	filter = newFilter;

   fireTableChanged(new com.cannontech.macs.events.MACSGenericTableModelEvent( this,
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
/**
 * This method was created in VisualAge.
 * @param source Observable
 * @param obj java.lang.Object
 */
public void update(Observable source, Object obj ) 
{
	//if( getConnection() != null && getConnection().isValid() )
		//schedules = getConnection().retrieveSchedules();

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
