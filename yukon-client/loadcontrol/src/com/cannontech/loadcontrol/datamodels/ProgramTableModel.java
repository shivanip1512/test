package com.cannontech.loadcontrol.datamodels;

/**
 * This type was created in VisualAge.
 */
import java.awt.Color;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.login.ClientSession;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.loadcontrol.data.IGearProgram;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.data.LMProgramDirectGear;
import com.cannontech.loadcontrol.events.LCGenericTableModelEvent;
import com.cannontech.roles.application.TDCRole;

public class ProgramTableModel extends javax.swing.table.AbstractTableModel implements javax.swing.event.TableModelListener, IProgramTableModel
{
	private String fontName = "dialog";
	private int fontSize = 12;

	private java.util.Vector rows = null;

  	// the holder for the current LMControlArea
	private LMControlArea currentControlArea = null;
	
	//The columns and their column index	
	public static final int PROGRAM_NAME			= 0;
	public static final int CURRENT_STATUS			= 1;
	public static final int START_TIME				= 2;
	public static final int STOP_TIME				= 3;
  	public static final int CURRENT_GEAR			= 4;
  	public static final int PRIORITY					= 5;

  	public static final int REDUCTION				= 6;
  	
  	
  	
	//The column names based on their column index
	public static String[] columnNames =
	{
		"Program Name",
		"Status",
		"Start Date/Time",
		"Stop Date/Time",
		"Current Gear",
		"Priority",
		
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
	               TDCRole.LC_REDUCTION_COL, 
	               "true") );
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

	public static final java.util.Comparator PROGRAM_NAME_COMPARATOR = new java.util.Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			try
			{
				String thisVal = ((LMProgramBase)o1).getYukonName();
				String anotherVal = ((LMProgramBase)o2).getYukonName();
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
	 * ProgramTableModel constructor comment.
	 */
	public ProgramTableModel() {
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
	/**
	 * Insert the method's description here.
	 * Creation date: (8/23/00 10:01:03 AM)
	 */
	public LMProgramBase[] getAllRows()
	{
		LMProgramBase[] temp = new LMProgramBase[getRowCount()];
		
		getRows().toArray(temp);
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
			else if( prg.getProgramStatus().intValue() == LMProgramBase.STATUS_SCHEDULED
						 || prg.getProgramStatus().intValue() == LMProgramBase.STATUS_CNTRL_ATTEMPT )
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
	private com.cannontech.loadcontrol.data.LMControlArea getCurrentControlArea() {
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
	
	private String getCurrentGear( IGearProgram dPrg )
	{
		LMProgramDirectGear gear = null;
		
		//get the current gear we are in
		for( int i = 0; i < dPrg.getDirectGearVector().size(); i++ )
		{			
			gear = (LMProgramDirectGear)dPrg.getDirectGearVector().get(i);
	
			if( dPrg.getCurrentGearNumber().intValue() == gear.getGearNumber().intValue() )
			{
				return gear.getGearName();
			}			
		}
	
		//should not get here
		com.cannontech.clientutils.CTILogger.info("*** Unable to find gear #: " + 
				gear.getGearNumber() + " was not found.");
	
		return "(Gear #" + gear.getGearNumber() + " not Found)";	
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
	 * This method returns the value of a row in the form of 
	 * a LMProgramBase object.
	 */
	public synchronized LMProgramBase getProgramAt(int rowIndex) 
	{
		return (LMProgramBase)getRowAt(rowIndex);
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
			 	case PROGRAM_NAME:
					return prg.getYukonName();
	
			 	case CURRENT_STATUS:
			 		if( prg.getDisableFlag().booleanValue() )				
						return "DISABLED: " + LMProgramBase.getProgramStatusString( prg.getProgramStatus().intValue() );
			 		else
						return LMProgramBase.getProgramStatusString( prg.getProgramStatus().intValue() );
		
				case START_TIME:
					if( prg.getDisableFlag().booleanValue() )
						return CtiUtilities.STRING_DASH_LINE;
					else
					{
						if( prg.getStartTime() == null
							 || prg.getStartTime().before(com.cannontech.common.util.CtiUtilities.get1990GregCalendar()) )
							return CtiUtilities.STRING_DASH_LINE;
						else
							return new com.cannontech.clientutils.commonutils.ModifiedDate( prg.getStartTime().getTime().getTime() );
					}
	
				case CURRENT_GEAR:
				{
					if( prg instanceof IGearProgram ) 
					{
						return getCurrentGear( (IGearProgram)prg );
					}
					else
						return CtiUtilities.STRING_DASH_LINE;
				}
				
				case STOP_TIME:
					if( prg.getDisableFlag().booleanValue() )
						return CtiUtilities.STRING_DASH_LINE;
					else
					{
						if( prg.getStopTime() == null
							|| prg.getStopTime().before(com.cannontech.common.util.CtiUtilities.get1990GregCalendar()) )
							return CtiUtilities.STRING_DASH_LINE;
						else
							return new com.cannontech.clientutils.commonutils.ModifiedDate( prg.getStopTime().getTime().getTime() );
					}
				
				case PRIORITY:
					return 
						( prg.getDefaultPriority().intValue() <= 0
						? new Integer(1)
						: prg.getDefaultPriority() );

				case REDUCTION:
					return prg.getReductionTotal();
					
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
		int oldRowCount = getRowCount();
		
		if( getCurrentControlArea() == null 
			 || getCurrentControlArea().getLmProgramVector() == null )
		{
			clear();
		}
		else
		{		
			rows = getCurrentControlArea().getLmProgramVector();
	
			//always keep our list in order by the Program Name
			// this will sort the references ONLY
			java.util.Collections.sort( 
					getRows(),
					PROGRAM_NAME_COMPARATOR );
		}
	
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
		if( event instanceof LCGenericTableModelEvent )
		{
			if( ((LCGenericTableModelEvent)event).getType()
				 == LCGenericTableModelEvent.TYPE_CLEAR )
			{
				clear();
			}
	
		}
	
		//fireTableDataChanged();
		fireTableChanged( event );
	}

	/** 
	 * Tells us if we should show a waiting GUI or not while updating
	 * @return boolean
	 */
	public boolean showWaiting( LMControlArea newCntrlArea )
	{
		return false;
	}

}