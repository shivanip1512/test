package com.cannontech.cbc.gui;

/**
 * This type was created in VisualAge.
 */
import java.awt.Color;

import com.cannontech.cbc.data.CapBankDevice;
import com.cannontech.cbc.data.CapControlConst;
import com.cannontech.cbc.data.Feeder;
import com.cannontech.cbc.data.SubBus;
import com.cannontech.cbc.tablemodelevents.CBCGenericTableModelEvent;

public class FeederTableModel extends javax.swing.table.AbstractTableModel implements com.cannontech.tdc.alarms.gui.AlarmTableModel, javax.swing.event.TableModelListener, CapControlTableModel, com.cannontech.common.gui.util.SortableTableModel
{
	private String fontName = "dialog";
	private int fontSize = 12;
	private SubBus currentSubBus = null;
	private int subBusRowSelected = -1;

	/* ROW SPECIFIC DATA */
	private java.util.Vector rows = null;
	//private com.cannontech.cbc.capbankeditor.ObservableCapBankRow observableRow = null;
	/* END --- ROW SPECIFIC DATA */

	//The columns and their column index	
	public static final int AREA_NAME_COLUMN  = 0;
	public static final int NAME_COLUMN = 1;
	public static final int CURRENT_STATE_COLUMN  = 2;
  	public static final int TARGET_COLUMN  = 3;
  	public static final int CURRENT_VAR_LOAD_COLUMN  = 4;
  	public static final int TIME_STAMP_COLUMN  = 5;
  	public static final int WATTS_COLUMN  = 6;
   public static final int POWER_FACTOR_COLUMN = 7;
  	public static final int ESTIMATED_VARS_COLUMN  = 8;
  	public static final int DAILY_OPERATIONS_COLUMN  = 9;

	//The column names based on their column index
	private static final String[] COLUMN_NAMES =
	{
		"Area Name",
		"Feeder Name",		
		"State",
		"Target",
		"VAR Load",
		"Time",
		"Watts",		
      "PFactor/Estimated",
		"Estimated VARS",
		"Daily Ops"
	};
	
	//The color schemes - based on the schedule status (foreGround, bgColor)
	private static Color[] cellColors =
	{
		//Enabled feeder
		Color.green,
		//Disabled feeder
		Color.red,
		//Pending color
		Color.yellow
	};
	

/**
 * ScheduleTableModel constructor comment.
 */
public FeederTableModel() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (3/20/00 11:40:31 AM)
 */
public void clear() 
{
	//getRows().removeAllElements();
	rows = null;
	currentSubBus = null;
	
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
 * @return java.awt.Color
 * @param row int
 * @param col int
 */
public java.awt.Color getCellForegroundColor(int row, int col) 
{
	if( getRows() != null && getRowCount() > row && col <= getColumnCount() )
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
 * Creation date: (8/23/00 11:42:06 AM)
 */
private SubBus getCurrentSubBus()
{
	return currentSubBus;
}
/**
 * Insert the method's description here.
 * Creation date: (1/2/2001 2:01:14 PM)
 */
private String getFeederPendingState( Feeder feeder )
{
	int size = feeder.getCcCapBanks().size();
	for( int j = 0; j < size; j++ )
	{
		CapBankDevice capBank = ((CapBankDevice)feeder.getCcCapBanks().elementAt(j));
		
		if( capBank.getControlStatus().intValue() == CapControlConst.BANK_CLOSE_PENDING )
			return CapBankTableModel.getStateNames()[CapControlConst.BANK_CLOSE_PENDING];
			
		if( capBank.getControlStatus().intValue() == CapControlConst.BANK_OPEN_PENDING )
			return CapBankTableModel.getStateNames()[CapControlConst.BANK_OPEN_PENDING];
	}

	// we are not pending
	return null;
}
/**
 * This method returns the value of a row in the form of a Feeder object.
 * @param rowIndex int
 */
public synchronized Feeder getRowAt(int rowIndex) 
{
	if( rowIndex >= 0 && rowIndex < getRowCount() )
		return (Feeder)getRows().get(rowIndex);
	else
		return null;
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
 * @return java.util.Vector
 */
public java.util.Vector getRows() 
{
	if( rows == null )
		rows = new java.util.Vector(10);
		
	return rows;
}
/**
 * Insert the method's description here.
 * Creation date: (12/3/2001 3:11:07 PM)
 * @return int
 */
private int getSubBusRowSelected() {
	return subBusRowSelected;
}
/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) 
{
	if( row < getRowCount() )
	{
		Feeder feeder = getRowAt(row);

		switch( col )
		{
		 	case AREA_NAME_COLUMN:
				return feeder.getCcArea();

		 	case NAME_COLUMN:
				return feeder.getCcName();

			case CURRENT_STATE_COLUMN:
			{
				String state = null;
				
            if( feeder.getCcDisableFlag().booleanValue() )
            {
               state = "DISABLED";
            }
				else if( feeder.getRecentlyControlledFlag().booleanValue() )
				{
					state = getFeederPendingState( feeder );
					
					if( state == null )
					{
						if( row == 0 )
							com.cannontech.clientutils.CTILogger.info("***MINOR ERROR*** Expecting " + feeder.getCcName() + " to have at least 1 capbank in the same pending state. (feeder)");

						state = "PENDING";  //we dont know what Pending state its in
					}
					
				}
				else
					state = "ENABLED";
					
				return state;
			}

			case TARGET_COLUMN:
			{
				// decide which set Point we are to use
				if( getCurrentSubBus().getPeakTimeFlag().booleanValue() )
				{
               if( getCurrentSubBus().isPowerFactorControlled() )
               {
                  return getPowerFactorText(feeder.getPeakSetPoint().doubleValue()) + " Pk";
               }
               else
   					return (feeder.getPeakSetPoint().doubleValue() - feeder.getLowerBandWidth().doubleValue()) +
   							 " to " + 
   							 (feeder.getUpperBandWidth().doubleValue() + feeder.getPeakSetPoint().doubleValue()) + 
   							 " Pk";
				}
				else
				{
               if( getCurrentSubBus().isPowerFactorControlled() )
               {
                  return getPowerFactorText(feeder.getPeakSetPoint().doubleValue()) + " OffPk";
               }
               else
   					return (feeder.getOffPeakSetPoint().doubleValue() - feeder.getLowerBandWidth().doubleValue()) +
   							 " to " + 
   							 (feeder.getUpperBandWidth().doubleValue() + feeder.getOffPeakSetPoint().doubleValue()) + 
   							 " OffPk";
				}

			}

         case POWER_FACTOR_COLUMN:
         {
            return getPowerFactorText( feeder.getPowerFactorValue().doubleValue() )
                    + " / " +
                    getPowerFactorText( feeder.getEstimatedPFValue().doubleValue() );
         }
         
         
			case DAILY_OPERATIONS_COLUMN:
				return feeder.getCurrentDailyOperations();
				
			case CURRENT_VAR_LOAD_COLUMN:
				return com.cannontech.clientutils.CommonUtils.formatDecimalPlaces( 
							feeder.getCurrentVarLoadPointValue().doubleValue(), getCurrentSubBus().getDecimalPlaces().intValue() );
				
			case ESTIMATED_VARS_COLUMN:
				return com.cannontech.clientutils.CommonUtils.formatDecimalPlaces( 
							feeder.getEstimatedVarLoadPointValue().doubleValue(), getCurrentSubBus().getDecimalPlaces().intValue() );

			case WATTS_COLUMN:
	      {
	         return new Double( com.cannontech.clientutils.CommonUtils.formatDecimalPlaces( 
	                  feeder.getCurrentWattLoadPointValue().doubleValue(), getCurrentSubBus().getDecimalPlaces().intValue() ) );
	      }

			case TIME_STAMP_COLUMN:
				if( feeder.getLastCurrentVarPointUpdateTime().getTime() <= 
						com.cannontech.common.util.CtiUtilities.get1990GregCalendar().getTime().getTime() )
					return "  ----";
				else
					return new com.cannontech.clientutils.commonutils.ModifiedDate( feeder.getLastCurrentVarPointUpdateTime().getTime() );
	
			default:
				return null;
		}

	}
	else
		return null; /// MAYBE NOT A GOOD IDEA!!
	
}

private String getPowerFactorText( double value )
{
   if( value <= CapControlConst.PF_INVALID_VALUE )
      return "  NA";
   else   
      return com.cannontech.clientutils.CommonUtils.formatDecimalPlaces(
            value * 100, 1 ) + "%"; //get percent   
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
 * Creation date: (1/8/2001 4:54:40 PM)
 * @return boolean
 */
public static boolean isFeederPending(Feeder feeder) 
{
	return feeder.getRecentlyControlledFlag().booleanValue();
}
/**
 * Insert the method's description here.
 * Creation date: (10/31/00 2:06:22 PM)
 * @return boolean
 * @param obj java.lang.Object
 */
private boolean isObjectFeeder(Object obj) 
{
	return( obj instanceof Feeder );
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
 * Creation date: (4/14/00 11:33:17 AM)
 * Version: <version>
 */
public synchronized void rowDataSwap( int i, int j )
{
	Object tmp = null;

	tmp = getRows().elementAt(i);
	getRows().setElementAt( getRows().elementAt(j), i );
	getRows().setElementAt( tmp, j );
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
 * Creation date: (8/23/00 11:42:06 AM)
 * @param devices java.util.Vector
 */
public synchronized void setCurrentSubBus( final com.cannontech.cbc.data.SubBus subBus_ )
{
	currentSubBus = subBus_;
	java.util.Vector newRows = null;
	
	if( currentSubBus != null )
	{
		newRows = currentSubBus.getCcFeeders();
		int oldRowCount = -1;

		for( int i = 0; i < newRows.size(); i++ )
		{
			if( !isObjectFeeder(newRows.elementAt(i)) )
				throw new IllegalStateException("Only " + com.cannontech.cbc.data.Feeder.class.getName() + " should be in a Feeder vector");
		}

		//this block of code will preserve the order of the feeders
		if( rows != null )
		{
			oldRowCount = rows.size();

			for( int i = 0 ; i < rows.size(); i++ )
			{
				int indx = -1;
				if( (indx = newRows.indexOf( rows.get(i) )) >= 0
					 && indx != i )
				{
					Object o = newRows.remove( indx );
					if( i >= 0 && i < newRows.size() )
						newRows.insertElementAt( o, i );
					else
						newRows.add( o );
				}

			}
		}

		rows = newRows;

		//by using fireTableRowsUpdated(int,int) we do not clear the table selection
		if( oldRowCount == getRowCount() && oldRowCount >= 0 )	
			fireTableRowsUpdated( 0, getRowCount()-1 );
		else
			fireTableDataChanged();
	}
	else
		clear();
}
/**
 * Insert the method's description here.
 * Creation date: (12/3/2001 3:11:07 PM)
 * @param newSubBusRowSelected int
 */
public void setSubBusRowSelected(int newSubBusRowSelected) {
	subBusRowSelected = newSubBusRowSelected;
}
/**
 * This method was created in VisualAge.
 * @param event javax.swing.event.TableModelEvent
 */
public void tableChanged(javax.swing.event.TableModelEvent event ) 
{
	// This should be an event fired from the SubBusTableModel
	if( event.getSource() instanceof SubBusTableModel )
	{
		if( event instanceof CBCGenericTableModelEvent )
		{
			CBCGenericTableModelEvent change = (CBCGenericTableModelEvent)event;

			if( change.getChangeid() == CBCGenericTableModelEvent.SUBBUS_FILTER_CHANGE )
				setSubBusRowSelected( ReceiverMainPanel.NO_ROW_SELECTED );
		}

		if( getSubBusRowSelected() >= 0 )
		{
			setCurrentSubBus( 
				((SubBusTableModel)event.getSource()).getRowAt(getSubBusRowSelected()) );
		}
		else
			clear();

		//fireTableDataChanged();
		fireTableRowsUpdated( 0, getRowCount()-1 );
	}

}
/**
 * Insert the method's description here.
 * Creation date: (12/12/2001 1:45:17 PM)
 */
public void toggleAlarms( boolean toggle )
{
	//do nothing since we dont alarm on this table yet
}
}
