package com.cannontech.loadcontrol.datamodels;

/**
 * This type was created in VisualAge.
 */
import java.awt.Color;
import java.util.Observer;
import java.util.Observable;
import java.awt.Font;
import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.loadcontrol.data.LMProgramBase;

public class ProgramTableModel extends javax.swing.table.AbstractTableModel implements SelectableLMTableModel, javax.swing.event.TableModelListener
{
	private String fontName = "dialog";
	private int fontSize = 12;
	private java.text.DecimalFormat numberFormatter = null;

	private java.util.Vector rows = null;

  	// the holder for the current LMControlArea
	private LMControlArea currentControlArea = null;
	
	//The columns and their column index	
	public static final int PROGRAM_NAME = 0;
	public static final int CURRENT_STATUS = 1;
	public static final int START_TIME = 2;
	public static final int STOP_TIME = 3;
  	public static final int REDUCTION = 4;
  	
	//The column names based on their column index
	public static final String[] columnNames =
	{
		"Program Name",
		"Status",
		"Start Time",
		"Stop Time",
		"Reduction"
	};

	//The color schemes - based on the program status (foreGround, backGround)
	// We will mostly likely later make these a StateGroup in the Database!!! :)
	private Color[] cellColors =
	{
		//Inactive
		Color.white,
		//Active, Manual Active & Fully Active
		Color.green,
		//Scheduled
		Color.cyan,
		//Notified
		Color.orange,
		//Stopping
		Color.yellow,


		//Disabled program
		Color.red
	};

/**
 * ScheduleTableModel constructor comment.
 */
public ProgramTableModel() {
	super();

	if( numberFormatter == null )
	{
		numberFormatter = new java.text.DecimalFormat();
		numberFormatter.setMaximumFractionDigits( 3 );
		numberFormatter.setMinimumFractionDigits( 1 );
	}
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
 * Creation date: (8/23/00 10:01:03 AM)
 */
public LMProgramBase[] getAllRows()
{
	LMProgramBase[] temp = new LMProgramBase[getRowCount()];
	
	rows.toArray(temp);
	return temp;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Color
 * @param row int
 * @param col int
 */
public java.awt.Color getCellBackgroundColor(int row, int col) 
{
	return Color.black;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Color
 * @param row int
 * @param col int
 */
public java.awt.Color getCellForegroundColor(int row, int col) 
{
	if( row >= 0 && row < getRowCount() && col <= getColumnCount() )
	{
		LMProgramBase prg = (LMProgramBase)getRowAt(row);

		if( prg.getDisableFlag().booleanValue() )
		{
			return cellColors[5];
		}
		else if( prg.getProgramStatus().intValue() == LMProgramBase.STATUS_INACTIVE )
		{
			return cellColors[0];
		}
		else if( prg.getProgramStatus().intValue() == LMProgramBase.STATUS_ACTIVE
					|| prg.getProgramStatus().intValue() == LMProgramBase.STATUS_FULL_ACTIVE
					|| prg.getProgramStatus().intValue() == LMProgramBase.STATUS_MANUAL_ACTIVE )
		{
			return cellColors[1];
		}
		else if( prg.getProgramStatus().intValue() == LMProgramBase.STATUS_NOTIFIED)
		{
			return cellColors[2];
		}
		else if( prg.getProgramStatus().intValue() == LMProgramBase.STATUS_SCHEDULED)
		{
			return cellColors[3];
		}
		else if( prg.getProgramStatus().intValue() == LMProgramBase.STATUS_STOPPING )
		{
			return cellColors[4];
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
 * Creation date: (4/6/2001 10:08:28 AM)
 * @return com.cannontech.loadcontrol.data.LMControlArea
 */
public com.cannontech.loadcontrol.data.LMControlArea getCurrentControlArea() {
	return currentControlArea;
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
 * This method returns the value of a row in the form of 
 * an Object, but its really a LMProgramBase object.
 */
public synchronized Object getRowAt(int rowIndex) 
{
	if( rowIndex < 0 || rowIndex >= getRowCount() )
		return null;
		
	//return (LMProgramBase)getRows().get(rowIndex);
	return (LMProgramBase)getRows().get(rowIndex);
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

	if( row <= getRowCount() && isProgramValid((LMProgramBase)getRowAt(row)) )
	{
		LMProgramBase prg = (LMProgramBase)getRowAt(row);

		switch( col )
		{
			//this need to be the entire LMProgramBase Object!
		 	case PROGRAM_NAME:
				return prg;

		 	case CURRENT_STATUS:
		 		if( prg.getDisableFlag().booleanValue() )				
					return "DISABLED: " + LMProgramBase.getProgramStatusString( prg.getProgramStatus().intValue() );
		 		else
					return LMProgramBase.getProgramStatusString( prg.getProgramStatus().intValue() );
	
			case START_TIME:
				if( prg.getDisableFlag().booleanValue() )
					return "  ----";
				else
				{
					if( prg.getStartTime() == null
						 || prg.getStartTime().before(com.cannontech.common.util.CtiUtilities.get1990GregCalendar()) )
						return " ----";
					else
						return new com.cannontech.clientutils.commonutils.ModifiedDate( prg.getStartTime().getTime().getTime() );
				}

			case STOP_TIME:
				if( prg.getDisableFlag().booleanValue() )
					return "  ----";
				else
				{
					if( prg.getStopTime() == null
						|| prg.getStopTime().before(com.cannontech.common.util.CtiUtilities.get1990GregCalendar()) )
						return " ----";
					else
						return new com.cannontech.clientutils.commonutils.ModifiedDate( prg.getStopTime().getTime().getTime() );
				}
			
			case REDUCTION:
				return numberFormatter.format(prg.getReductionTotal());
				
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
 * Creation date: (7/30/2001 3:16:01 PM)
 * @return boolean
 * @param prg com.cannontech.loadcontrol.data.LMProgramBase
 */
private boolean isProgramValid(LMProgramBase prg) 
{
	if( prg instanceof com.cannontech.loadcontrol.data.LMProgramDirect 
		 || prg instanceof com.cannontech.loadcontrol.data.LMProgramCurtailment
		 || prg instanceof com.cannontech.loadcontrol.data.LMProgramEnergyExchange )
	{
		return true;
	}
	else if( prg == null )
		return false;
	else
		throw new IllegalArgumentException("Found an instance of " + prg.getClass().getName() + " in our model : " + this.getClass().getName() );
}
/**
 * Insert the method's description here.
 * Creation date: (4/6/2001 10:08:28 AM)
 * @param newCurrentControlArea com.cannontech.loadcontrol.data.LMControlArea
 */
public synchronized void setCurrentControlArea(com.cannontech.loadcontrol.data.LMControlArea newCurrentControlArea) 
{
	currentControlArea = newCurrentControlArea;

	if( getCurrentControlArea() == null || getCurrentControlArea().getLmProgramVector() == null )
	{
		clear();
	}
	else
	{		
		fireTableRowsDeleted( 0, getLastRowIndex() );
		rows = getCurrentControlArea().getLmProgramVector();
	}

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
