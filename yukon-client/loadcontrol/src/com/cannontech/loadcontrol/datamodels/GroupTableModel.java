package com.cannontech.loadcontrol.datamodels;

/**
 * This type was created in VisualAge.
 */
import java.awt.Color;
import java.util.Observer;
import java.util.Observable;
import com.cannontech.loadcontrol.data.LMGroupBase;
import com.cannontech.loadcontrol.data.LMControlArea;

public class GroupTableModel extends javax.swing.table.AbstractTableModel implements javax.swing.event.TableModelListener, SelectableLMTableModel
{
	private LMControlArea currentControlArea = null;
	java.util.Vector rows = null;
	
	//The columns and their column index	
	public static final int GROUP_NAME = 0;
	public static final int GROUP_STATE = 1;
	public static final int TIME = 2;
  	public static final int STATS = 3;
  	public static final int REDUCTION = 4;
  	
	//The column names based on their column index
	public static final String[] columnNames =
	{
		"Group Name",
		"State",
		"Time",
		"Statistics",
		"Reduction"
	};

	// default BG color	
	Color backGroundColor = Color.black;

	//The color schemes - based on the schedule status
	private Color[] cellColors =
	{
		//Inactive groups
		Color.white,
		//Active groups
		Color.green,
		//Active Pending groups
		Color.yellow,
		//Inctive Pending groups
		Color.blue,

		//Disabled Groups
		Color.red
	};

/**
 * ScheduleTableModel constructor comment.
 */
public GroupTableModel() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (3/20/00 11:40:31 AM)
 */
public void clear() 
{
	getRows().removeAllElements();
	currentControlArea = null;


	//we can drop the current row selection here
	fireTableDataChanged();
}
/**
 * Insert the method's description here.
 * Creation date: (9/28/00 11:49:20 AM)
 * @return java.lang.String
 * @param millisecs long
 */
private String displayControlTime(long millisecs) 
{
	long secs = 0, minutes = 0, hours = 0;
	
	secs = millisecs / 1000;
	minutes = secs / 60;

	if( minutes >= 60 )
	{
		long tmpMins = minutes;
		minutes %= 60;		
		hours = tmpMins / 60;
	}


	return new String( hours + "hrs " + minutes +"mins");
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
	if( getRowAt(row) != null && getRowCount() > row && col < getColumnCount() )
	{
		LMGroupBase rowValue = (LMGroupBase)getRowAt(row);
		String state = rowValue.getGroupControlStateString();
		
		if( rowValue.getDisableFlag().booleanValue() )
		{
			return cellColors[4];	
		}
		else if( state.equalsIgnoreCase(LMGroupBase.CURRENT_STATES[LMGroupBase.STATE_INACTIVE])
					|| state.equalsIgnoreCase(com.cannontech.loadcontrol.data.LMCurtailCustomer.ACK_UNACKNOWLEDGED) )
		{
			return cellColors[0];
		}
		else if( state.equalsIgnoreCase(LMGroupBase.CURRENT_STATES[LMGroupBase.STATE_ACTIVE])
					|| state.equalsIgnoreCase(com.cannontech.loadcontrol.data.LMCurtailCustomer.ACK_ACKNOWLEDGED) )
		{
			return cellColors[1];
		}
		else if( state.equalsIgnoreCase(LMGroupBase.CURRENT_STATES[LMGroupBase.STATE_ACTIVE_PENDING])
					|| state.equalsIgnoreCase(com.cannontech.loadcontrol.data.LMCurtailCustomer.ACK_NOT_REQUIRED) )
		{
			return cellColors[2];
		}
		else if( state.equalsIgnoreCase(LMGroupBase.CURRENT_STATES[LMGroupBase.STATE_INACTIVE_PENDING])
					|| state.equalsIgnoreCase(com.cannontech.loadcontrol.data.LMCurtailCustomer.ACK_VERBAL) )
		{
			return cellColors[3];
		}
	}

	
	return Color.white;
}
/**
 * getColumnCount method comment.
 */
public int getColumnCount() {
	return columnNames.length;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param index int
 */
public String getColumnName(int index) {
	return columnNames[index];
}
/**
 * Insert the method's description here.
 * Creation date: (1/11/2002 10:45:49 AM)
 * @return int
 */
private synchronized int getLastRowIndex() 
{
	return (getRowCount() == 0 ? 0 : getRowCount());
}
/**
 * This method returns the value of a row in the form of a LoadControlGroup object.
 * @param rowIndex int
 */
public synchronized Object getRowAt(int rowIndex) 
{
	if( rowIndex < 0 || rowIndex >= getRowCount() )
		return null;

	return (LMGroupBase)getRows().get(rowIndex);
}
/**
 * getRowCount method comment.
 */
public int getRowCount() 
{
	return getRows().size();
}
/**
 * Insert the method's description here.
 * Creation date: (8/23/00 10:01:03 AM)
 * @return Vector
 */
private java.util.Vector getRows()
{
	if( rows == null )
		rows = new java.util.Vector(10);
		
	return rows;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Color
 * @param row int
 * @param col int
 */
public java.awt.Color getSelectedRowColor(int row, int col) 
{
	return getCellForegroundColor(row, col);
}
/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) 
{
	if( row < getRowCount() && row >= 0 )
	{
		LMGroupBase rowValue = (LMGroupBase)getRowAt(row);
		
		switch( col )
		{
		 	case GROUP_NAME:
				return rowValue.getYukonName();

		 	case GROUP_STATE:
		 		if( rowValue.getDisableFlag().booleanValue() )
		 			return "DISABLED: " + rowValue.getGroupControlStateString();
		 		else
					return rowValue.getGroupControlStateString();
	
			case TIME:
			{
				if( rowValue.getGroupTime().getTime() > com.cannontech.common.util.CtiUtilities.get1990GregCalendar().getTime().getTime() )
					return new com.cannontech.clientutils.commonutils.ModifiedDate( rowValue.getGroupTime().getTime() );
				else
					return "  ---";
			}
			
			case STATS:
				return rowValue.getStatistics();
				
		 	case REDUCTION:
				return rowValue.getKwCapacity();

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
 * Creation date: (4/6/2001 10:08:28 AM)
 * @param newCurrentControlArea com.cannontech.loadcontrol.data.LMControlArea
 */
public synchronized void setCurrentControlArea(com.cannontech.loadcontrol.data.LMControlArea newCurrentControlArea) 
{
	currentControlArea = newCurrentControlArea;
		
	if( newCurrentControlArea != null && newCurrentControlArea.getLmProgramVector() != null )
	{
		fireTableRowsDeleted( 0, getLastRowIndex() );
		getRows().removeAllElements();
		
		for( int i = 0; i < currentControlArea.getLmProgramVector().size(); i++ )
		{
			com.cannontech.loadcontrol.data.LMProgramBase prg = 
				(com.cannontech.loadcontrol.data.LMProgramBase)currentControlArea.getLmProgramVector().get(i);

			getRows().addAll( rows.size(), prg.getLoadControlGroupVector() );
		}
				
	}
	else
		clear();

	fireTableRowsInserted( 0, getLastRowIndex() );
}
/**
 * This method was created in VisualAge.
 * @param event javax.swing.event.TableModelEvent
 */
public void tableChanged(javax.swing.event.TableModelEvent event ) 
{
	if( event instanceof com.cannontech.loadcontrol.events.LCGenericTableModelEvent )
	{
		if( ((com.cannontech.loadcontrol.events.LCGenericTableModelEvent)event).getType()
			 == com.cannontech.loadcontrol.events.LCGenericTableModelEvent.TYPE_CLEAR )
		{
			clear();
		}

	}

	//fireTableDataChanged();
	fireTableChanged( event );
}
}
