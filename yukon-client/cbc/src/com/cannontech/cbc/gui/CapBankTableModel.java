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
import com.cannontech.cbc.tablemodelevents.StateTableModelEvent;
import com.cannontech.common.gui.util.Colors;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.db.state.State;
import com.cannontech.message.dispatch.message.Signal;
import com.cannontech.tdc.alarms.gui.AlarmingRow;
import com.cannontech.tdc.alarms.gui.AlarmingRowVector;
import com.cannontech.tdc.alarms.gui.RowBlinker;
import com.cannontech.clientutils.tags.TagUtils;


public class CapBankTableModel extends javax.swing.table.AbstractTableModel implements javax.swing.event.TableModelListener, com.cannontech.tdc.alarms.gui.AlarmTableModel, CapControlTableModel, com.cannontech.common.gui.util.SortableTableModel
{
	/* ROW SPECIFIC DATA */
	private java.util.Vector rows = null;
	private int[] currentRowBGColors = null;
	private AlarmingRowVector alarmingRowVector = null;
	/* END --- ROW SPECIFIC DATA */
	
	private int subBusRowSelected = -1;
	private int feederRowSelected = -1;
	
	private RowBlinker currentBlinkingAlarms = null;
	
	private boolean muted = false;
	
	private boolean showingAlarms = true;
	
	private String fontName = "dialog";
	private int fontSize = 12;

	public static final String UNKNOWN_STATE = "Unknown";
	public static final Color MOVED_BANK_BG_COLOR = new Color( 30, 50, 110);

	//The columns and their column index
	public static final int CB_NAME_COLUMN = 0;
	public static final int BANK_ADDRESS_COLUMN = 1;
 	public static final int BANK_SIZE_COLUMN = 2;
	public static final int STATUS_COLUMN = 3;
  	public static final int TIME_STAMP_COLUMN = 4;
 	public static final int OP_COUNT_COLUMN = 5;
  		
	//The column names based on their column index
	private static final String[] COLUMN_NAMES =
	{
		"CB Name (Order)",
		"Bank Address",
		"Bank Size",
		"Status",
		"Date/Time",
		"Op Count",
	};

	//The string states of the cap bank
	public static String[] stateNames =
	{
		"Open",
		"Close",
		"Open Questionable",
		"Close Questionable",
		"Open Fail",
		"Close Fail",
		"Open Pending",
		"Close Pending"
	};
	
		
	//The color schemes - based on the capbanks stateNames
	// colors are [fg][bg]
	private static Color[][] stateColors =
	{
		{Colors.getColor(Colors.GREEN_ID), Colors.getColor(Colors.BLACK_ID)}, // Open
		{Colors.getColor(Colors.GREEN_ID), Colors.getColor(Colors.BLACK_ID)}, // Close

		{Colors.getColor(Colors.YELLOW_ID), Colors.getColor(Colors.BLACK_ID)}, // OpenQuestionable
		{Colors.getColor(Colors.YELLOW_ID), Colors.getColor(Colors.BLACK_ID)}, // CloseQuestionable
		
		{Colors.getColor(Colors.RED_ID), Colors.getColor(Colors.BLACK_ID)}, // OpenFail
		{Colors.getColor(Colors.RED_ID), Colors.getColor(Colors.BLACK_ID)}, // CloseFail

		{Colors.getColor(Colors.CYAN_ID), Colors.getColor(Colors.BLACK_ID)}, // OpenPending
		{Colors.getColor(Colors.CYAN_ID), Colors.getColor(Colors.BLACK_ID)}  // ClosePending
	};	
/**
 * ScheduleTableModel constructor comment.
 */
public CapBankTableModel() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (3/20/00 11:40:31 AM)
 */
public void clear() 
{
	setSubBusRowSelected( ReceiverMainPanel.NO_ROW_SELECTED );
	setFeederRowSelected( ReceiverMainPanel.NO_ROW_SELECTED );

	currentRowBGColors = null;
	
	if( currentBlinkingAlarms != null )
		currentBlinkingAlarms.destroy();
		
	currentBlinkingAlarms = null;
	getAlarmingRowVector().removeAllElements();
	//getRows().removeAllElements();
	rows = null;

	fireTableDataChanged();
}
/**
 * Insert the method's description here.
 * Creation date: (3/28/00 3:43:14 PM)
 */
protected void finalize() throws Throwable
{
	super.finalize();  // NEVER FORGET THIS!!

	clear();	
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
 * Insert the method's description here.
 * Creation date: (1/12/2001 2:22:49 PM)
 * @return com.cannontech.tdc.alarms.gui.AlarmingRowVector
 */
public synchronized AlarmingRowVector getAlarmingRowVector() 
{
	if( alarmingRowVector == null )
		alarmingRowVector = new AlarmingRowVector();

	return alarmingRowVector;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Color
 * @param row int
 * @param col int
 */
public java.awt.Color getCellBackgroundColor(int row, int col) 
{	
	if( row < getRowCount() && col <= getColumnCount() && getRowAt(row) != null)
	{		
		
		if( getRowAt(row).isBankMoved() )
		{
			return MOVED_BANK_BG_COLOR; //mark moved banks as a different BG Color
		}
		else
			return Colors.getColor( getCurrentRowBGColors(row) );
	
	}

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
	if( row < getRowCount() && col <= getColumnCount() && getRowAt(row) != null)
	{
		int status = getRowAt(row).getControlStatus().intValue();

		synchronized( stateNames )
		{
			if( status >= 0 && status < getStateNames().length )
			{
				return getStateColors()[status][0];
			}
		}

	}

	// Unknown state, return a crazy color
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
 * Creation date: (1/12/2001 3:10:19 PM)
 * @return int[]
 */
private int getCurrentRowBGColors( int rowNumber )
{
	if( rowNumber < 0 )
		return Colors.getColorID( CapControlTableModel.DEFUALT_BGCOLOR );

	try
	{	
		if( currentRowBGColors == null || rowNumber >= currentRowBGColors.length )  // reinit our currentBGColors
		{

			synchronized( rows )
			{
				if( getRowCount() > 0 )
				{
					currentRowBGColors = new int[getRowCount()];

					// try to initialize our row colors
					synchronized( currentRowBGColors )
					{							
						for( int i = 0; i < getRowCount(); i++ )
						{
							int status = getRowAt(i).getControlStatus().intValue();

							//make sure our status is a valid array index into StateColors
							if( status >= 0 && status < getStateColors().length )
								currentRowBGColors[i] = Colors.getColorID(getStateColors()[status][1]);
							else
								currentRowBGColors[i] = Colors.getColorID( CapControlTableModel.DEFUALT_BGCOLOR );
						}
					}

					// we could still throw an ArrayIndexOutOfBoundsException here
					return currentRowBGColors[rowNumber];
				}
			}
		}
		else
			return currentRowBGColors[rowNumber];  // success, everything went well
	}
	catch( ArrayIndexOutOfBoundsException e )
	{
		com.cannontech.clientutils.CTILogger.info("***** " + this.getClass() + " : getCurrentRowBGColors(int), using DEFAULT_BGCOLOR for row number " + rowNumber + " : " + e.getMessage()	);
		return Colors.getColorID( CapControlTableModel.DEFUALT_BGCOLOR );
	}  // dont do really anything, just return the DEFAULT_BGCOLOR
		
	return Colors.getColorID( CapControlTableModel.DEFUALT_BGCOLOR );
}
/**
 * Insert the method's description here.
 * Creation date: (4/5/2002 2:10:04 PM)
 * @return int
 */
public int getFeederRowSelected() {
	return feederRowSelected;
}

/**
 * Insert the method's description here.
 * Creation date: (1/2/2001 2:01:14 PM)
 */
public String getPendingState() 
{
	for( int i = 0; i < getRowCount(); i++ )
	{
		CapBankDevice rowValue = getRowAt(i);
		
		if( rowValue.getControlStatus().intValue() == CapControlConst.BANK_CLOSE_PENDING )
			return getStateNames()[CapControlConst.BANK_CLOSE_PENDING];
			
		if( rowValue.getControlStatus().intValue() == CapControlConst.BANK_OPEN_PENDING )
			return getStateNames()[CapControlConst.BANK_OPEN_PENDING];
	}

	// we are not pending
	return null;
}
/**
 * This method returns the value of a row in the form of a CapBankDevice object.
 * @param rowIndex int
 */
public synchronized CapBankDevice getRowAt(int rowIndex) 
{
	if( rowIndex < 0 || rowIndex > (getRowCount() - 1) )
		return null;

	return (CapBankDevice)getRows().get(rowIndex);		
}
/**
 * This method returns the capbank with the specified PAO ID
 * @param paoID_ int
 */
public synchronized CapBankDevice getCapbank( int capID_ ) 
{
	for( int i = 0; i < getRowCount(); i++ )
	{
		CapBankDevice cap = getRowAt(i);
		if( cap.getCcId().intValue() == capID_ )
			return cap;		
	}

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
 * Creation date: (1/12/2001 3:15:35 PM)
 * @return java.awt.Color[][]
 */
public static java.awt.Color[][] getStateColors() {
	return stateColors;
}
/**
 * Insert the method's description here.
 * Creation date: (1/11/2001 4:01:26 PM)
 * @return java.lang.String[]
 */
public static java.lang.String[] getStateNames() {
	return stateNames;
}
/**
 * Insert the method's description here.
 * Creation date: (10/31/00 3:39:45 PM)
 * @return int
 */
public int getSubBusRowSelected() {
	return subBusRowSelected;
}
/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) 
{
	if( row < getRowCount() && row >= 0 )
	{
		CapBankDevice rowValue = getRowAt(row);
		
		switch( col )
		{
		 	case CB_NAME_COLUMN :
				return rowValue.getCcName() + " (" + rowValue.getControlOrder() + ")";

		 	case BANK_ADDRESS_COLUMN :
				return rowValue.getCcArea();
	
			case STATUS_COLUMN :
				synchronized( stateNames ) //THIS METHOD IS CALLED BY THE EVENT THREAD(REPAINTS THE SCREEN), BUT WE
				{									// NEED TO PUT A LOCK ON THE stateNames DATA???!!!! COULD BE DANGEROUS???!!!
					if( rowValue.getControlStatus().intValue() < 0 ||
						rowValue.getControlStatus().intValue() >= getStateNames().length )
					{
						com.cannontech.clientutils.CTILogger.info("*** A CapBank state was found that has no corresponding status.");
						return UNKNOWN_STATE + " (" + rowValue.getControlStatus().intValue() +")" ;
					}
					else
					{
						if( rowValue.getCcDisableFlag().booleanValue() == true )
							return "DISABLED : " + (rowValue.getOperationalState().equalsIgnoreCase(com.cannontech.database.data.capcontrol.CapBank.FIXED_OPSTATE) 
															? com.cannontech.database.data.capcontrol.CapBank.FIXED_OPSTATE 
															: getStateNames()[rowValue.getControlStatus().intValue()]);
						else
							return (rowValue.getOperationalState().equalsIgnoreCase(com.cannontech.database.data.capcontrol.CapBank.FIXED_OPSTATE) 
										? com.cannontech.database.data.capcontrol.CapBank.FIXED_OPSTATE + " : " 
										: "") + getStateNames()[rowValue.getControlStatus().intValue()];
					}
				}
					
			case OP_COUNT_COLUMN :
				return rowValue.getCurrentDailyOperations();
				
			case BANK_SIZE_COLUMN :
				return rowValue.getBankSize();

			case TIME_STAMP_COLUMN :
				if( rowValue.getLastStatusChangeTime().getTime() <= 
					   com.cannontech.common.util.CtiUtilities.get1990GregCalendar().getTime().getTime() )
					return "  ----";
				else
					return new com.cannontech.clientutils.commonutils.ModifiedDate( rowValue.getLastStatusChangeTime().getTime() );

			default:
				return null;
		}				
	}
	else
		return null;
	
}
/**
 * Insert the method's description here.
 * Creation date: (1/12/2001 4:26:43 PM)
 * @param capBankDev com.cannontech.cbc.data.CapBankDevice
 */
private void handleAlarms(CapBankDevice capBankDevice, int rowNumber ) 
{
	if( TagUtils.isAlarmUnacked(capBankDevice.getTagControlStatus().intValue()) )
	{
		setRowAlarmed( rowNumber );
	}
	else   //if the row was alarming, set it unAlarmed
		setRowUnAlarmed( new Integer(rowNumber) );
}
/**
 * Insert the method's description here.
 * Creation date: (1/2/2001 1:57:19 PM)
 */
public boolean isAnyCapBankInPendingState() 
{
	for( int i = 0; i < getRowCount(); i++ )
	{
		CapBankDevice rowValue = getRowAt(i);
		
		if( rowValue.getControlStatus().intValue() == CapControlConst.BANK_CLOSE_PENDING 
			 || rowValue.getControlStatus().intValue() == CapControlConst.BANK_OPEN_PENDING )
		{
			return true;
		}
	}

	return false;
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
 * Creation date: (10/31/00 2:06:22 PM)
 * @return boolean
 * @param obj java.lang.Object
 */
private boolean isObjectCapBank(Object obj) 
{
	return ( obj instanceof CapBankDevice );
}
/**
 * Insert the method's description here.
 * Creation date: (2/1/2001 1:02:05 PM)
 * @return boolean
 */
public boolean isMuted() 
{
	return muted;
}

public void silenceAlarms() 
{
	synchronized( getAlarmingRowVector() )
	{
		getAlarmingRowVector().setAllSilenced( true );
	}
}

/**
 * Insert the method's description here.
 * Creation date: (3/30/00 9:29:03 AM)
 * Version: <version>
 * @return boolean
 * @param rowNumber int
 */
public boolean isRowInAlarmVector( int rowNumber )
{
	return getAlarmingRowVector().contains( new Integer( rowNumber ) );
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
 * Creation date: (1/12/2001 5:02:53 PM)
 */
public void reInitTable() 
{
	getAlarmingRowVector().removeAllElements();

	if( currentBlinkingAlarms != null )
		currentBlinkingAlarms.destroy();
	
	currentBlinkingAlarms = null;
		
	currentRowBGColors = null;
	//getCurrentRowBGColors(0);
}
/**
 * Insert the method's description here.
 * Creation date: (4/14/00 11:33:17 AM)
 * Version: <version>
 */
public synchronized void rowDataSwap( int i, int j )
{
	Object tmp = null;

	// handles alarmed rows
	if( isRowInAlarmVector(i) || isRowInAlarmVector(j) )
	{
		synchronized( getAlarmingRowVector() )
		{
			if( isRowInAlarmVector(i) && isRowInAlarmVector(j) )
			{
				int iRow = getAlarmingRowVector().getAlarmingRowLocation( i );
				int jRow = getAlarmingRowVector().getAlarmingRowLocation( j );

				tmp = getAlarmingRowVector().getAlarmingRow(i);
				getAlarmingRowVector().setElementAt( getAlarmingRowVector().elementAt(jRow), iRow );
				getAlarmingRowVector().setElementAt( (AlarmingRow)tmp, jRow );
			}
			else if( isRowInAlarmVector(i) )
			{
				// set row i unalarmed and set row j to alarmed
				tmp = getAlarmingRowVector().getAlarmingRow(i);
				((AlarmingRow)tmp).setRowNumber( j );
			}
			else if( isRowInAlarmVector(j) )
			{
				// set row j unalarmed and set row i to alarmed				
				tmp = getAlarmingRowVector().getAlarmingRow(j);
				((AlarmingRow)tmp).setRowNumber( i );
			}						
		}
	}

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
	synchronized( currentRowBGColors )
	{	
		try
		{
			setCurrentRowBGColors(rowNumber, color);
			return true;
		}
		catch( ArrayIndexOutOfBoundsException ex )
		{
			/* This happens when the user switches displays, no big deal */
			return false;
		}
	} // end synch
	
}
/**
 * Insert the method's description here.
 * Creation date: (8/23/00 11:42:06 AM)
 * @param devices java.util.Vector
 */
public synchronized void setCapBankDevices(java.util.Vector newRows) 
{
	if( newRows == null )
	{
		clear();
	}
	else
	{
		reInitTable();
		int oldRowCount = -1;

		//this block of code will preserve the order of the capbanks
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


		for( int i = 0; i < getRowCount(); i++ )
		{
			if( !isObjectCapBank(newRows.elementAt(i)) )
				throw new IllegalStateException("Only CapBankDevices should be in a CapBankDevice vector");

			handleAlarms( (CapBankDevice)rows.elementAt(i), i );
		}
		
		//by using fireTableRowsUpdated(int,int) we do not clear the table selection
		if( oldRowCount == getRowCount() && oldRowCount >= 0 )
			fireTableRowsUpdated( 0, getRowCount()-1 );
		else
			fireTableDataChanged();
			
	}

}
/**
 * Insert the method's description here.
 * Creation date: (1/12/2001 3:10:19 PM)
 */
private void setCurrentRowBGColors( int rowNumber, int color )
{
	try
	{	
		if( currentRowBGColors != null && rowNumber >= 0 && rowNumber < currentRowBGColors.length )
			currentRowBGColors[rowNumber] = color;
			
	}
	catch( ArrayIndexOutOfBoundsException e )
	{
		com.cannontech.clientutils.CTILogger.info("***** " + this.getClass() + " : setCurrentRowBGColor() " + e.getMessage() );
	}  // dont do really anything
		
}
/**
 * Insert the method's description here.
 * Creation date: (4/5/2002 2:10:04 PM)
 * @param newFeederRowSelected int
 */
public void setFeederRowSelected(int newFeederRowSelected) {
	feederRowSelected = newFeederRowSelected;
}


/**
 * Insert the method's description here.
 * Creation date: (3/29/00 2:23:38 PM)
 * Version: <version>
 */
private void setRowAlarmed( int rowNumber ) 
{
	// see if the point is in our display
	if( rowNumber >= 0 && rowNumber < getRowCount() )
	{
		Signal sig = new Signal();
		sig.setPointID( getRowAt(rowNumber).getStatusPointID().intValue() );
		sig.setTimeStamp( new java.util.Date() );
		sig.setTags( getRowAt(rowNumber).getTagControlStatus().intValue() );
		sig.setAction("Automatically created signal from CBC Client");
		sig.setDescription("Alarm signal used in CBC client table");
		sig.setUserName( CtiUtilities.getUserName() );
		
		synchronized( getAlarmingRowVector() )
		{
			if( !getAlarmingRowVector().contains( new Integer(rowNumber) ) )
			{
				AlarmingRow alRow = new AlarmingRow(
													rowNumber,
													Colors.getColorID( CapControlTableModel.DEFAULT_ALARMCOLOR),  // use this for every alarm for now
													getCurrentRowBGColors(rowNumber) );

				alRow.updateSignal( sig );

				getAlarmingRowVector().addElement( alRow );
			}
			else
			{
				getAlarmingRowVector().getAlarmingRow(rowNumber).updateSignal( sig );
			}
	
	
			if( currentBlinkingAlarms == null )
			{
				if( showingAlarms )
				{
					currentBlinkingAlarms = new RowBlinker( this, getAlarmingRowVector() );
					currentBlinkingAlarms.start();
				}
				else
					toggleAlarms( false ); //we should just kill any possible alarm blinkers
													// if they are present
			}
		}
		
	}		
}

public boolean isRowAlarmed( int rowNumber )
{
	synchronized( getAlarmingRowVector() )
	{
		return getAlarmingRowVector().contains( new Integer(rowNumber) );
	}
	
}

/**
 * Insert the method's description here.
 * Creation date: (3/29/00 2:23:38 PM)
 * Version: <version>
 */
public void setRowUnAlarmed( Integer rowNumber ) 
{
	// make sure our display has the row in it
	if( rowNumber.intValue() >= 0 && rowNumber.intValue() < getRowCount() )
	{
		synchronized( getAlarmingRowVector() )
		{
			if( getAlarmingRowVector().contains( rowNumber ) )	
				getAlarmingRowVector().removeElement( rowNumber );
			else
				return;
		}


		if( getAlarmingRowVector().size() == 0 )
		{
			currentBlinkingAlarms.destroy();
			currentBlinkingAlarms = null;
		}

	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (1/11/2001 3:52:13 PM)
 * @param newCellColors java.awt.Color[][]
 */
public static synchronized void setStates(Color[][] newStateColors, String[] newStateNames ) 
{
	stateColors = newStateColors;
	stateNames = newStateNames;
}

/**
 * Insert the method's description here.
 * Creation date: (10/31/00 3:39:45 PM)
 * @param newStrategyRowSelected int
 */
public void setSubBusRowSelected(int newSubBusRowSelected) 
{
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
			{
				setSubBusRowSelected( ReceiverMainPanel.NO_ROW_SELECTED );
				setFeederRowSelected( ReceiverMainPanel.NO_ROW_SELECTED );
			}

		}

		//get the selected SubBus from the SubBusTableModel and add the needed CapBanks
		tableChanged_HandleSubBus( ((SubBusTableModel)event.getSource()).getRowAt(getSubBusRowSelected()) );

		if( event instanceof StateTableModelEvent )
		{
			State[] states = ((StateTableModelEvent)event).getStates();
			Color[][] colors = new Color[states.length][2];
			String[] stateNames = new String[states.length];
			
			for( int i = 0; i < states.length; i++ )
			{
				stateNames[i] = states[i].getText();
				colors[i][0] = com.cannontech.common.gui.util.Colors.getColor( states[i].getForegroundColor().intValue() );
				colors[i][1] = com.cannontech.common.gui.util.Colors.getColor( states[i].getBackgroundColor().intValue() );
			}

			setStates( colors, stateNames );
		}
		
		//fireTableDataChanged();
		fireTableRowsUpdated( 0, getRowCount()-1 );
	}
}
/**
 * This method was created in VisualAge.
 * @param event javax.swing.event.TableModelEvent
 */
private void tableChanged_HandleSubBus( SubBus subBus ) 
{
	if( getSubBusRowSelected() >= 0 )
	{
		if( subBus != null )
		{
			synchronized( subBus )
			{
				java.util.Vector tempVector = new java.util.Vector(getRowCount());

				if( getFeederRowSelected() == ReceiverMainPanel.NO_ROW_SELECTED )
				{
					for( int i = 0; i < subBus.getCcFeeders().size(); i++ )
						tempVector.addAll( ((Feeder)subBus.getCcFeeders().get(i)).getCcCapBanks() );
				}
				else
				{
					if( getFeederRowSelected() >= 0 
						 && getFeederRowSelected() < subBus.getCcFeeders().size() )
					{
						tempVector.addAll( ((Feeder)subBus.getCcFeeders().get(getFeederRowSelected())).getCcCapBanks() );
					}
				}

				setCapBankDevices( tempVector );				
			}
		}
		else
			setCapBankDevices( null ); // do this to clear the table			
	}
	else
		setCapBankDevices( null ); // do this to clear the table
}
/**
 * Insert the method's description here.
 * Creation date: (12/12/2001 1:45:17 PM)
 */
public void toggleAlarms( boolean toggle )
{
	showingAlarms = toggle;

	if( toggle ) // turn the alarms on if there are any
	{
		synchronized( getAlarmingRowVector() )
		{
			if( getAlarmingRowVector().size() > 0 )
				if( currentBlinkingAlarms == null )
				{
					currentBlinkingAlarms = new RowBlinker( this, getAlarmingRowVector() );
					currentBlinkingAlarms.start();
				}

		}
		
	}
	else //turn any alarms off
	{
		if( currentBlinkingAlarms != null )
			currentBlinkingAlarms.destroy();
		
		currentBlinkingAlarms = null;
	}

}


public void setMuted( boolean muted_ )
{
	muted = muted_;
}

}