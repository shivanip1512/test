package com.cannontech.cbc.gui;

/**
 * This type was created in VisualAge.
 */
import java.awt.Color;
import java.awt.Font;
import java.util.Observable;

import com.cannontech.cbc.data.CBCClientConnection;
import com.cannontech.cbc.data.CapBankDevice;
import com.cannontech.cbc.data.CapControlConst;
import com.cannontech.cbc.data.SubBus;
import com.cannontech.cbc.tablemodelevents.CBCGenericTableModelEvent;
import com.cannontech.cbc.tablemodelevents.StateTableModelEvent;

public class SubBusTableModel extends javax.swing.table.AbstractTableModel implements java.util.Observer, com.cannontech.tdc.alarms.gui.AlarmTableModel, com.cannontech.common.gui.util.SortableTableModel
{
	private CBCClientConnection connection = null;

	/* ROW DATA */
	private java.util.Hashtable filterTable = new java.util.Hashtable(10);
	private java.util.Vector allSubBuses = null;
	private java.util.List currentSubBuses = null;
	
	//keys are Integer rowNumbers and values are SubBus

	/* END - ROW DATA */

   // the string for filtering all areas
   public static final String ALL_FILTER = "All Areas";

   // the holder for the current filter, default to all
   private String filter = ALL_FILTER;

	//The columns and their column index	
	public static final int AREA_NAME_COLUMN  = 0;
	public static final int SUB_NAME_COLUMN = 1;
	public static final int CURRENT_STATE_COLUMN  = 2;
  	public static final int TARGET_COLUMN  = 3;
  	public static final int CURRENT_VAR_LOAD_COLUMN  = 4;
  	public static final int TIME_STAMP_COLUMN  = 5;
	public static final int POWER_FACTOR_COLUMN = 6;
  	public static final int WATTS_COLUMN  = 7;
  	public static final int ESTIMATED_VARS_COLUMN  = 8;
  	public static final int DAILY_OPERATIONS_COLUMN  = 9;
	
	//The column names based on their column index
	private static final String[] COLUMN_NAMES =
	{
		"Area Name",
		"Sub Name",
		"State",
		"Target",
		"VAR Load",
		"Time",		
		"PFactor",
		"Watts",
		"Estimated VARS",
		"Daily/Max Ops"
	};
	
	//The color schemes - based on the schedule status
	private static Color[] cellColors =
	{
		//Enabled subbus
		Color.green,
		//Disabled subbus
		Color.red,
		//Pending subbus
		Color.yellow
	};

   //the default font of our model	
   private Font cellFont = new Font("dialog", Font.PLAIN, 12);

	public static final java.util.Comparator SUB_AREA_COMPARATOR = new java.util.Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			String thisVal = ((SubBus)o1).getCcArea();
			String anotherVal = ((SubBus)o2).getCcArea();
			return( thisVal.compareToIgnoreCase(anotherVal) );
		}
	};

/**
 * ScheduleTableModel constructor comment.
 */
public SubBusTableModel() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (3/20/00 11:40:31 AM)
 */
public void clear() 
{
	filterTable.clear();

	getAllSubBuses().removeAllElements();

	currentSubBuses = getAllSubBuses();

	fireTableDataChanged();
}
/**
 * Insert the method's description here.
 * Creation date: (5/23/00 2:18:04 PM)
 * Version: <version>
 * @param value boolean
 */
public void forcePaintTableRowUpdated( int minLocation, int maxLocation )
{
	fireTableRowsUpdated( minLocation, maxLocation );
}
/**
 * Insert the method's description here.
 * Creation date: (4/9/2002 10:12:03 AM)
 * @return Vector
 */
private java.util.Vector getAllSubBuses() 
{	
	if( allSubBuses == null )
		allSubBuses = new java.util.Vector(20);

	return allSubBuses;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Color
 * @param row int
 * @param col int
 */
public java.awt.Color getCellBackgroundColor(int row, int col)
{
	//since we dont alamr yet, just return the default color
	return CapControlTableModel.DEFUALT_BGCOLOR;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Font
 * @param row int
 * @param col int
 */
public Font getCellFont(int row, int col) 
{
	return cellFont;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Color
 * @param row int
 * @param col int
 */
public java.awt.Color getCellForegroundColor(int row, int col) 
{
	if( getRowCount() > row 
		 && col <= getColumnCount()
		 && getRowAt(row) != null )
	{
		if( getRowAt(row).getCcDisableFlag().booleanValue() )
		{
			return cellColors[1]; //disabled color
		}
		else if( getRowAt(row).getRecentlyControlledFlag().booleanValue() )
		{
			return cellColors[2]; //pending color
		}
		else
		{
			return cellColors[0];
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
 */
private CBCClientConnection getConnection() {
	return connection;
}
/**
 * Insert the method's description here.
 * Creation date: (4/11/2002 1:25:18 PM)
 * @return java.util.List
 */
private java.util.List getCurrentSubBuses() 
{
	if( currentSubBuses == null )
		currentSubBuses = getAllSubBuses();

	return currentSubBuses;
}
/**
 * Insert the method's description here.
 * Creation date: (2/5/2001 1:47:29 PM)
 * @return java.lang.String
 */
public java.lang.String getFilter() {
	return filter;
}
/**
 * This method returns the value of a row in the form of a SubBus object.
 * @param rowIndex int
 */
public synchronized SubBus getRowAt(int rowIndex) 
{
	if( rowIndex >= 0 && rowIndex < getRowCount() )
		return (SubBus)getCurrentSubBuses().get( rowIndex );
	else
		return null;
}
/**
 * getRowCount method comment.
 */
public int getRowCount() 
{
	return getCurrentSubBuses().size();
}
/**
 * Insert the method's description here.
 * Creation date: (1/2/2001 2:01:14 PM)
 */
private String getSubBusPendingState( SubBus sub ) 
{
	for( int i = 0; i < sub.getCcFeeders().size(); i++ )
	{
		com.cannontech.cbc.data.Feeder feeder =
			(com.cannontech.cbc.data.Feeder)sub.getCcFeeders().get(i);

		int size = feeder.getCcCapBanks().size();
		for( int j = 0; j < size; j++ )
		{
			CapBankDevice capBank = ((CapBankDevice)feeder.getCcCapBanks().elementAt(j));
			
			if( capBank.getControlStatus().intValue() == CapControlConst.BANK_CLOSE_PENDING )
				return CapBankTableModel.getStateNames()[CapControlConst.BANK_CLOSE_PENDING];
				
			if( capBank.getControlStatus().intValue() == CapControlConst.BANK_OPEN_PENDING )
				return CapBankTableModel.getStateNames()[CapControlConst.BANK_OPEN_PENDING];
		}

	}

	// we are not pending
	return null;
}
/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) 
{
	SubBus sub = getRowAt(row);
	if( sub == null )
		return "<<NULL>>";

	switch( col )
	{
	 	case SUB_NAME_COLUMN:
			return sub.getCcName();

	 	case AREA_NAME_COLUMN:
			return sub.getCcArea();

		case CURRENT_STATE_COLUMN:
		{
			String state = null;
			
         if( sub.getCcDisableFlag().booleanValue() )
         {
            state = "DISABLED";
         }
			else if( sub.getRecentlyControlledFlag().booleanValue() )
			{
				state = getSubBusPendingState( sub );
				
				if( state == null )
				{
					//only print the below msg out 1 time per row
					if( row == 0 )
						com.cannontech.clientutils.CTILogger.info("***MINOR ERROR*** Expecting " + sub.getCcName() + " to have at least 1 capbank in the same pending state.");

					state = "PENDING"; //we only know its pending for sure
				}
				
			}
			else
				state = "ENABLED";
				
			return state;
		}

		case TARGET_COLUMN:
		{
			// decide which set Point we are to use
			if( sub.getPeakTimeFlag().booleanValue() )
			{
            if( sub.isPowerFactorControlled() )
            {
               return getPowerFactorText(sub) + " Pk";
            }
            else
				  return(sub.getPeakSetPoint().doubleValue() - sub.getLowerBandWidth().doubleValue()) +
   						 " to " + 
   						 (sub.getUpperBandWidth().doubleValue() + sub.getPeakSetPoint().doubleValue()) + 
   						 " Pk";
			}
			else
			{
            if( sub.isPowerFactorControlled() )
            {
               return getPowerFactorText(sub) + " OffPk";
            }
            else
   				return(sub.getOffPeakSetPoint().doubleValue() - sub.getLowerBandWidth().doubleValue()) +
   						 " to " + 
   						 (sub.getUpperBandWidth().doubleValue() + sub.getOffPeakSetPoint().doubleValue()) + 
   						 " OffPk";
			}

		}
			
		case DAILY_OPERATIONS_COLUMN:
			return new String(sub.getCurrentDailyOperations() + " / " + 
				(sub.getMaxDailyOperation().intValue() <= 0 
					? "NA" 
					: sub.getMaxDailyOperation().toString()) );
		
		case CURRENT_VAR_LOAD_COLUMN:
			return com.cannontech.clientutils.CommonUtils.formatDecimalPlaces( 
						sub.getCurrentVarLoadPointValue().doubleValue(), sub.getDecimalPlaces().intValue() );
			
		case ESTIMATED_VARS_COLUMN:
			return com.cannontech.clientutils.CommonUtils.formatDecimalPlaces( 
						sub.getEstimatedVarLoadPointValue().doubleValue(), sub.getDecimalPlaces().intValue() );

		case POWER_FACTOR_COLUMN:
      {
         return getPowerFactorText(sub);
      }
			
		case WATTS_COLUMN:
         return com.cannontech.clientutils.CommonUtils.formatDecimalPlaces( 
                  sub.getCurrentWattLoadPointValue().doubleValue(), sub.getDecimalPlaces().intValue() );
			
		case TIME_STAMP_COLUMN:
			if( sub.getLastCurrentVarPointUpdateTime().getTime() <= 
					com.cannontech.common.util.CtiUtilities.get1990GregCalendar().getTime().getTime() )
				return "  ----";
			else
				return new com.cannontech.clientutils.commonutils.ModifiedDate( sub.getLastCurrentVarPointUpdateTime().getTime() );

		default:
			return null;
	}

	
}

private String getPowerFactorText( SubBus sub )
{   
   if( sub.getPowerFactorValue().doubleValue() <= CapControlConst.PF_INVALID_VALUE )
      return "  NA";
   else
      return com.cannontech.clientutils.CommonUtils.formatDecimalPlaces(
            sub.getPowerFactorValue().doubleValue() * 100, 1 ) + "%"; //get percent   
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
 * Creation date: (2/1/2001 1:02:05 PM)
 * @return boolean
 */
public boolean isPlayingSound() 
{
	return false;
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
 * Creation date: (1/8/2001 4:54:40 PM)
 * @return boolean
 */
public static boolean isSubBusPending(SubBus sub) 
{
	return sub.getRecentlyControlledFlag().booleanValue();
}
/**
 * Insert the method's description here.
 * Creation date: (4/14/00 11:33:17 AM)
 * Version: <version>
 */
public synchronized void rowDataSwap( int i, int j )
{
	Object tmp = null;

	tmp = getCurrentSubBuses().get(i);
	getCurrentSubBuses().set( i, getCurrentSubBuses().get(j) );
	getCurrentSubBuses().set( j, tmp );

}
/**
 * Insert the method's description here.
 * Creation date: (1/12/2001 1:06:00 PM)
 * @return boolean
 * @param rowNumber int
 * @param color int
 */
public boolean setBGRowColor(int rowNumber, int color) 
{
	//This TableModel does not alarm directly, for now....
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (8/8/00 1:56:41 PM)
 */
public void setConnection(CBCClientConnection newConnection) {
	connection = newConnection;
}
/**
 * Insert the method's description here.
 * Creation date: (2/5/2001 1:47:29 PM)
 * @param newFilter java.lang.String
 */
public synchronized void setFilter(java.lang.String newFilter)
{
   filter = newFilter;

   //need to refresh all of our sublists   
   if( ALL_FILTER.equalsIgnoreCase(getFilter()) )
   {
		currentSubBuses = getAllSubBuses();
   }
	else
	{
		java.util.List l = (java.util.List)filterTable.get( getFilter() );
		if( l != null )
		{
			//we already have a sublist for this filter, use it!
			currentSubBuses = l;
		}
		else
		{
			int start = -1, stop = -1;
			for( int i = 0; i < getAllSubBuses().size(); i++ )
			{
				SubBus realBus = (SubBus)getAllSubBuses().get(i);
				if( start <= -1 && realBus.getCcArea().equalsIgnoreCase(getFilter()) )
					start = i;
				else if( start >= 0 && !realBus.getCcArea().equalsIgnoreCase(getFilter()) )
				{
					stop = i;
					break;
				}

			}

			
			if( start < 0 ) //should not occur
			{
				currentSubBuses = getAllSubBuses();
				com.cannontech.clientutils.CTILogger.info("*** Could not find SubBus with the area = " + getFilter() );
			}
			else  //this locks down AllSubBuses and disallows any structural modification to AllSubBuses
				currentSubBuses = getAllSubBuses().subList(
											start, 
											(stop < 0 ? start+1 : stop) );
						
			filterTable.put( getFilter(), currentSubBuses );
		}
	}

	javax.swing.SwingUtilities.invokeLater( new Runnable()
	{
		public void run()
		{
		   CBCGenericTableModelEvent e = 
		   	new CBCGenericTableModelEvent( 
			   	SubBusTableModel.this,
			   	CBCGenericTableModelEvent.SUBBUS_FILTER_CHANGE );

		   fireTableChanged(e);
		}
	});
}
/**
 * Insert the method's description here.
 * Creation date: (8/14/00 2:16:54 PM)
 * @param int
 */
public void setFontValues(String name, int size) 
{
	cellFont = new Font( 
                  name, 
                  cellFont.getStyle(), 
                  size );
}
/**
 * This method was created in VisualAge.
 * @param source Observable
 * @param obj java.lang.Object
 */
public synchronized void update(Observable source, Object obj ) 
{
	if( source instanceof CBCClientConnection )
	{
		int oldRowCount = getRowCount();

		if( obj instanceof com.cannontech.database.db.state.State[] )
		{
			StateTableModelEvent e = 
				new StateTableModelEvent(this, 0, getRowCount()-1,
						javax.swing.event.TableModelEvent.ALL_COLUMNS, 
						javax.swing.event.TableModelEvent.UPDATE);
			
			e.setStates( (com.cannontech.database.db.state.State[])obj );

			fireTableChanged( e );
		}
		else if( obj instanceof SubBus[] )
		{
			updateSubBuses( (SubBus[])obj );
		}

		//by using fireTableRowsUpdated(int,int) we do not clear the table selection		
		if( oldRowCount == getRowCount() )
			fireTableRowsUpdated( 0, getRowCount()-1 );
		else
			fireTableDataChanged();
	}

}
/**
 * Insert the method's description here.
 * Creation date: (4/10/2002 12:26:57 PM)
 * @param newBus com.cannontech.cbc.data.SubBus[]
 */
private synchronized void updateSubBuses(SubBus[] newBuses)
{
	boolean changeSize = false;

	for( int i = 0; i < newBuses.length; i++ )
	{
		SubBus newBus = newBuses[i];
		
		boolean found = false;

		for( int j = 0 ; j < getAllSubBuses().size(); j++ )
		{
			SubBus row = (SubBus)getAllSubBuses().get(j);
			if( row.equals(newBus) )
			{
				//we may have to redo our Sublists if the Area changed
				if( !row.getCcArea().equalsIgnoreCase( newBus.getCcArea() ) )
					changeSize = true;

				getAllSubBuses().setElementAt( newBus, j );
				found = true;
			}
		}

		if( !found )
		{
			changeSize = true;

			//always keep our main list in order by the SubBusArea
			// find the first SubBus with the same AreaName
			int indx = java.util.Collections.binarySearch( 
					getAllSubBuses(), 
					newBus, 
					SUB_AREA_COMPARATOR );

			if( indx < 0 )
				getAllSubBuses().add( newBus );
			else
				getAllSubBuses().add( indx, newBus );
		}
	}

	
	if( changeSize )
	{	
		//since we increased the size of AllSubBuses, we must release all filter sublist
	  	filterTable.clear();

	   setFilter( getFilter() );
	}

}
}
