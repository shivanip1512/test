package com.cannontech.loadcontrol.datamodels;

/**
 * This type was created in VisualAge.
 */
import java.awt.Color;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.commonutils.ModifiedDate;
import com.cannontech.common.login.ClientSession;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.loadcontrol.data.ILMGroup;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.loadcontrol.data.LMCurtailCustomer;
import com.cannontech.loadcontrol.data.LMGroupBase;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.roles.application.TDCRole;

public class GroupTableModel extends javax.swing.table.AbstractTableModel implements javax.swing.event.TableModelListener, ISelectableLMTableModel
{
	private LMControlArea currentControlArea = null;
	private java.util.Vector rows = null;
	
	//The columns and their column index	
	public static final int GROUP_NAME = 0;
	public static final int GROUP_STATE = 1;
	public static final int TIME = 2;
  	public static final int STATS = 3;
  	public static final int REDUCTION = 4;
  	
	//The column names based on their column index
	public static String[] columnNames =
	{
		"Group Name",
		"State",
		"Start Date/Time",
		"Day/Month/Season/Year Hrs",
		
		//optional column to show (keep this column last!)
		"Reduction"
	};
	
	static
	{
	   Boolean showRedCol = Boolean.TRUE;

	   try
	   {
	      showRedCol = 
	         Boolean.valueOf(
	            ClientSession.getInstance().getRolePropertyValue(
	               TDCRole.LC_REDUCTION_COL, "true") );
	   }
	   catch( Exception e)
	   {}
   	
   	if( !showRedCol.booleanValue() )
   	{
   		String[] temp = new String[ columnNames.length - 1 ];
   		System.arraycopy( columnNames, 0, temp, 0, temp.length );
   		columnNames = temp;
   	}
   	
	}

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


	public static final java.util.Comparator GROUP_NAME_COMPARATOR = new java.util.Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			try
			{
				String thisVal = ((ILMGroup)o1).getName();
				String anotherVal = ((ILMGroup)o2).getName();
				return( thisVal.compareToIgnoreCase(anotherVal) );
			}
			catch( Exception e )
			{
				CTILogger.error( "Something went wrong with sorting, ignoring sorting rules", e );
				return 0; 
			}
			
		}
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
	//do NOT remove the elements, but just set the reference to our list to a
	//  new reference...setting it to NULL would work (getRows().removeAllElements();)
	rows = new java.util.Vector(10);

	currentControlArea = null;


	//we can drop the current row selection here
	fireTableDataChanged();
}

public void setStatsColumnName( String name )
{
	//we may need this some day for Curtailment  --RWN 11-6-2002
	columnNames[STATS] = name;
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
		ILMGroup rowValue = (ILMGroup)getRowAt(row);
		String state = rowValue.getGroupControlStateString();
		
		if( rowValue.getDisableFlag().booleanValue() )
		{
			return cellColors[4];	
		}
		else if( state.equalsIgnoreCase(LMGroupBase.CURRENT_STATES[LMGroupBase.STATE_INACTIVE])
					|| state.equalsIgnoreCase(LMCurtailCustomer.ACK_UNACKNOWLEDGED) )
		{
			return cellColors[0];
		}
		else if( state.equalsIgnoreCase(LMGroupBase.CURRENT_STATES[LMGroupBase.STATE_ACTIVE])
					|| state.equalsIgnoreCase(LMCurtailCustomer.ACK_ACKNOWLEDGED) )
		{
			return cellColors[1];
		}
		else if( state.equalsIgnoreCase(LMGroupBase.CURRENT_STATES[LMGroupBase.STATE_ACTIVE_PENDING])
					|| state.equalsIgnoreCase(LMCurtailCustomer.ACK_NOT_REQUIRED) )
		{
			return cellColors[2];
		}
		else if( state.equalsIgnoreCase(LMGroupBase.CURRENT_STATES[LMGroupBase.STATE_INACTIVE_PENDING])
					|| state.equalsIgnoreCase(LMCurtailCustomer.ACK_VERBAL) )
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
 * This method returns the value of a row in the form of a LoadControlGroup object.
 * @param rowIndex int
 */
public synchronized Object getRowAt(int rowIndex) 
{
	if( rowIndex < 0 || rowIndex >= getRowCount() )
		return null;

	return getRows().get(rowIndex);
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
		ILMGroup rowValue = (ILMGroup)getRowAt(row);
		
		switch( col )
		{
		 	case GROUP_NAME:
				return rowValue.getName();

		 	case GROUP_STATE:
		 		if( rowValue.getDisableFlag().booleanValue() )
		 			return "DISABLED: " + rowValue.getGroupControlStateString();
		 		else
					return rowValue.getGroupControlStateString();
	
			case TIME:
			{
				if( rowValue.getGroupTime().getTime() > com.cannontech.common.util.CtiUtilities.get1990GregCalendar().getTime().getTime() )
					return new ModifiedDate( rowValue.getGroupTime().getTime() );
				else
					return CtiUtilities.STRING_DASH_LINE;
			}
			
			case STATS:
				return rowValue.getStatistics();
				
		 	case REDUCTION:
				return rowValue.getReduction();

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
public synchronized void setCurrentData(LMControlArea cntrArea_, LMProgramBase prgBse_ ) 
{
	currentControlArea = cntrArea_;
	int oldRowCount = getRowCount();
		
	if( currentControlArea != null && currentControlArea.getLmProgramVector() != null )
	{
		getRows().removeAllElements();

		for( int i = 0; i < currentControlArea.getLmProgramVector().size(); i++ )
		{
			LMProgramBase prg = 
				(LMProgramBase)currentControlArea.getLmProgramVector().get(i);

			if( prgBse_ != null )
			{
				if( prgBse_.equals(prg) ) 
				{
					getRows().addAll( rows.size(), prg.getLoadControlGroupVector() );
					break; //we only want to see this programs groups, get out now	
				}
			}			
			else
				getRows().addAll( rows.size(), prg.getLoadControlGroupVector() );
		}
	
		//always keep our list in order by the Group Name
		// this will sort the references ONLY
		java.util.Collections.sort( 
				rows,
				GROUP_NAME_COMPARATOR );				
	}
	else
		clear();

	//by using fireTableRowsUpdated(int,int) we do not clear the table selection
	if( oldRowCount == getRowCount() && oldRowCount >= 0 )	
		fireTableRowsUpdated( 0, getRowCount()-1 );
	else
		fireTableDataChanged();
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
