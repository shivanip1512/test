package com.cannontech.cbc.gui;

/**
 * This type was created in VisualAge.
 */
import java.awt.Color;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import com.cannontech.cbc.data.CBCClientConnection;
import com.cannontech.cbc.data.CapBankDevice;
import com.cannontech.cbc.data.CapControlConst;
import com.cannontech.cbc.data.SubBus;
import com.cannontech.cbc.messages.CBCStates;
import com.cannontech.cbc.messages.CBCSubAreaNames;
import com.cannontech.cbc.messages.CBCSubstationBuses;
import com.cannontech.cbc.tablemodelevents.CBCGenericTableModelEvent;
import com.cannontech.cbc.tablemodelevents.StateTableModelEvent;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageListener;
import com.cannontech.roles.application.TDCRole;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.CommonUtils;
import com.cannontech.clientutils.commonutils.ModifiedDate;
import com.cannontech.common.login.ClientSession;

public class SubBusTableModel extends javax.swing.table.AbstractTableModel implements MessageListener, com.cannontech.tdc.alarms.gui.AlarmTableModel, com.cannontech.common.gui.util.SortableTableModel
{
	private CBCClientConnection connection = null;

	/* ROW DATA */
	private java.util.Vector allSubBuses = null;
	private java.util.List currentSubBuses = null;	
	/* END - ROW DATA */

    private final Vector areaNames = new Vector(32);
	public static final String STR_NA = "  NA";

	// the holder for the current filter, default to all
    private String filter = null; //ALL_FILTER;

	//The columns and their column index	
	public static final int AREA_NAME_COLUMN  = 0;
	public static final int SUB_NAME_COLUMN = 1;
	public static final int CURRENT_STATE_COLUMN  = 2;
  	public static final int TARGET_COLUMN  = 3;
  	public static final int VAR_LOAD_COLUMN  = 4;
  	//public static final int ESTIMATED_VARS_COLUMN  = 5;
  	public static final int WATTS_COLUMN  = 5;
	public static final int POWER_FACTOR_COLUMN = 6;
  	public static final int TIME_STAMP_COLUMN  = 7;
  	public static final int DAILY_OPERATIONS_COLUMN  = 8;

  	public static final String DASH_LINE = "  ----";

    //which LiteYukonUser owns this data
    private LiteYukonUser ownerUser = null;

    
	//The column names based on their column index
	private static final String[] COLUMN_NAMES =
	{
		"Area Name",
		"Sub Name",
		"State",
		"Target",
		"VAR Load / Est.",
		"Watts",
      "PFactor / Est.",
		"Date/Time",		
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

	public static final java.util.Comparator SUB_AREA_COMPARATOR = new java.util.Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			try
			{
				String thisArea = ((SubBus)o1).getCcArea();
				String anotherArea = ((SubBus)o2).getCcArea();
				
				if( !thisArea.equalsIgnoreCase(anotherArea) )
					return( thisArea.compareToIgnoreCase(anotherArea) );
				
				//if the Area Names	are equal, we need to sort by SubName
				String thisName = ((SubBus)o1).getCcName();
				String anotherName = ((SubBus)o2).getCcName();
				
				return( thisName.compareToIgnoreCase(anotherName) );				
			}
			catch( Exception e )
			{
				CTILogger.error( "Something went wrong with sorting, ignoring sorting rules", e );
				return 0; 
			}
			
		}
	};

/**
 * SubBusTableModel constructor comment.
 */
public SubBusTableModel()
{
    //by default, use the master user account
	this( ClientSession.getInstance().getUser() );
}

/**
 * SubBusTableModel constructor comment.
 */
public SubBusTableModel( LiteYukonUser yukUser )
{
    super();
    
    if( yukUser == null )
        throw new IllegalArgumentException("Do not use a NULL YukonUser for ownership");

    ownerUser = yukUser;
}

/**
 * Insert the method's description here.
 * Creation date: (3/20/00 11:40:31 AM)
 */
public void clear() 
{
	getAllSubBuses().removeAllElements();

	currentSubBuses = getAllSubBuses();

	fireTableDataChanged();
}

public Vector getAreaNames()
{
  return areaNames;
}

/**
* This method assumes we have that all the Subs that we care about in memory.
* It then only adds areas to our list that we have Subs for.
* 
* @param areaNames com.cannontech.cbc.messages.CBCSubAreaNames
*/
private synchronized void updateAreaList(CBCSubAreaNames areaNames_) 
{
  // remove all the values in the list
  getAreaNames().removeAllElements();

  //create the temporary map to have the needed areas in it
  HashMap areaMap = new HashMap( areaNames_.getNumberOfAreas() );
  for( int i = 0; i < getAllSubBuses().size(); i++ )
      areaMap.put(
              ((SubBus)getAllSubBuses().get(i)).getCcArea(), "JUNK" );

  
  // add all area names to the list   
  for( int i = 0; i < areaNames_.getNumberOfAreas(); i++ )
      if( areaMap.containsKey(areaNames_.getAreaName(i)) )
          getAreaNames().add( areaNames_.getAreaName(i) );
}


/**
 * Used to clear all filters and forces the model to recreate them.
 * Creation date: (2/24/03 11:40:31 AM)
 */
private void clearFilter()
{
   //always keep our main list in order by the SubBusArea
   java.util.Collections.sort( 
         getAllSubBuses(), 
         SUB_AREA_COMPARATOR );
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
 * This method returns the subbus with the specified PAO ID
 * @param paoID_ int
 */
public synchronized SubBus getSubBus( int suBusID_ ) 
{
	for( int i = 0; i < getRowCount(); i++ )
	{
		SubBus bus = getRowAt(i);
		if( bus.getCcId().intValue() == suBusID_ )
			return bus;		
	}

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


			//show waived with a W at the end of the state
			if( sub.getWaiveControlFlag().booleanValue() )
				state += "-W";

			return state;
		}

		case TARGET_COLUMN:
		{
			// decide which set Point we are to use
			if( sub.isPowerFactorControlled() )
			{
				return getPowerFactorText(sub.getPeakSetPoint().doubleValue(), false);
			}
			else if( sub.getLowerBandWidth().doubleValue() == 0
						 && sub.getUpperBandWidth().doubleValue() == 0 )
			{
				return STR_NA;
			}
			else if( sub.getPeakTimeFlag().booleanValue() )
			{
				return
					CommonUtils.formatDecimalPlaces(sub.getPeakSetPoint().doubleValue() - sub.getLowerBandWidth().doubleValue(), 0) +
					" to " + 
					CommonUtils.formatDecimalPlaces(sub.getUpperBandWidth().doubleValue() + sub.getPeakSetPoint().doubleValue(), 0) + 
					" Pk";
			}
			else
			{
				return
					CommonUtils.formatDecimalPlaces(sub.getOffPeakSetPoint().doubleValue() - sub.getLowerBandWidth().doubleValue(), 0) +
					" to " + 
					CommonUtils.formatDecimalPlaces(sub.getUpperBandWidth().doubleValue() + sub.getOffPeakSetPoint().doubleValue(), 0) + 
					" OffPk";
			}

		}
			
		case DAILY_OPERATIONS_COLUMN:
			return new String(sub.getCurrentDailyOperations() + " / " + 
				(sub.getMaxDailyOperation().intValue() <= 0 
					? STR_NA 
					: sub.getMaxDailyOperation().toString()) );
		
		case VAR_LOAD_COLUMN:
      {
      	String retVal = DASH_LINE; //default just in case

         if( sub.getCurrentVarLoadPointID().intValue() <= PointTypes.SYS_PID_SYSTEM )
            retVal = DASH_LINE;
         else 
         {                        
         	if( sub.getDecimalPlaces().intValue() == 0 )
					retVal =  CommonUtils.formatDecimalPlaces( 
	               sub.getCurrentVarLoadPointValue().doubleValue(), sub.getDecimalPlaces().intValue() );         	
         	else
					retVal = CommonUtils.formatDecimalPlaces( 
	               sub.getCurrentVarLoadPointValue().doubleValue(), sub.getDecimalPlaces().intValue() );
         }
         
			retVal += " / ";

         if( sub.getCurrentVarLoadPointID().intValue() <= PointTypes.SYS_PID_SYSTEM )
				retVal += DASH_LINE;
         else 
         {               
         	if( sub.getDecimalPlaces().intValue() == 0 )
					retVal += CommonUtils.formatDecimalPlaces( 
	               sub.getEstimatedVarLoadPointValue().doubleValue(), sub.getDecimalPlaces().intValue() );         	
         	else
					retVal += CommonUtils.formatDecimalPlaces( 
						sub.getEstimatedVarLoadPointValue().doubleValue(), sub.getDecimalPlaces().intValue() );
      	}
      	
      	return retVal;
      }
      
		case POWER_FACTOR_COLUMN:
      {
         return getPowerFactorText( sub.getPowerFactorValue().doubleValue(), true )
                 + " / " +
                 getPowerFactorText( sub.getEstimatedPFValue().doubleValue(), true );
      }
			
		case WATTS_COLUMN:
      {
         if( sub.getCurrentWattLoadPointID().intValue() <= PointTypes.SYS_PID_SYSTEM )
            return DASH_LINE;
         else {
         	if( sub.getDecimalPlaces().intValue() == 0 )
					return new Integer( CommonUtils.formatDecimalPlaces( 
	               sub.getCurrentWattLoadPointValue().doubleValue(), sub.getDecimalPlaces().intValue() ) );         	
         	else
		         return new Double( CommonUtils.formatDecimalPlaces( 
	                  sub.getCurrentWattLoadPointValue().doubleValue(), sub.getDecimalPlaces().intValue() ) );
         }
      }
      	
		case TIME_STAMP_COLUMN:
			if( sub.getLastCurrentVarPointUpdateTime().getTime() <= 
					com.cannontech.common.util.CtiUtilities.get1990GregCalendar().getTime().getTime() )
				return DASH_LINE;
			else
				return new ModifiedDate( sub.getLastCurrentVarPointUpdateTime().getTime(), ModifiedDate.FRMT_NOSECS );

		default:
			return null;
	}

	
}

private String getPowerFactorText( double value, boolean compute )
{   
   int decPlaces = 1;
   try
   {
      decPlaces = 
         Integer.parseInt(
				ClientSession.getInstance().getRolePropertyValue(
               TDCRole.PFACTOR_DECIMAL_PLACES, 
               "1") );
   }
   catch( Exception e)
   {}
	
   if( value <= CapControlConst.PF_INVALID_VALUE )
      return STR_NA;
   else
      return CommonUtils.formatDecimalPlaces(
            value * (compute ? 100 : 1), decPlaces ) + "%"; //get percent   
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

public boolean isMuted() 
{
	return false;
}

public void setMuted( boolean b ){}
public void silenceAlarms() {}


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

	int start = -1, stop = getAllSubBuses().size();

	for( int i = 0; i < getAllSubBuses().size(); i++ )
	{
		SubBus realBus = (SubBus)getAllSubBuses().get(i);
		if( start <= -1 
			 && realBus.getCcArea().equalsIgnoreCase(getFilter()) )
		{
			start = i;
		}
		else if( start >= 0 && !realBus.getCcArea().equalsIgnoreCase(getFilter()) )
		{
			stop = i;
			break;
		}

	}
	
	if( start < 0 ) //should not occur
	{
		//currentSubBuses = getAllSubBuses();
		//clear();
        currentSubBuses = new Vector();
		com.cannontech.clientutils.CTILogger.info("*** Could not find SubBus with the area = " + getFilter() );
	}
	else  //this locks down AllSubBuses and disallows any structural modification to AllSubBuses
		currentSubBuses = getAllSubBuses().subList(
									start, 
									(stop < 0 || stop > getAllSubBuses().size() ? start+1 : stop) );		
				

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


private void handleDeletedSub( CBCSubstationBuses msg )
{
	Vector deleteSubs = new Vector( msg.getNumberOfBuses() );
	
	for( int i = 0; i < getAllSubBuses().size(); i++ )
	{
		boolean fnd = false;
		SubBus existingSub = (SubBus)getAllSubBuses().get(i);

		for( int j = 0; j  < msg.getNumberOfBuses(); j++ )
		{
			if( msg.getSubBusAt(j).equals(existingSub) )
			{
				fnd = true;
				break;
			}
		}
				
		if( !fnd )
			deleteSubs.add( existingSub );
	}

	for( int i = (deleteSubs.size()-1); i >=0; i-- )
	{
		removeControlArea( (SubBus)deleteSubs.get(i) );
	}
	
}

/**
 * Removes the specified subbus
 * @param SubBus
 */
private void removeControlArea( SubBus bus_ )
{
	int loc = getAllSubBuses().indexOf( bus_ );
		
	//be sure we can find the area specified
	if( loc >= 0 )
	{
		getAllSubBuses().remove( loc );
		setFilter( getFilter() );  //this will fire the change event
	}
	
}

/**
 * This method was created in VisualAge.
 * @param source Observable
 * @param obj java.lang.Object
 */
public void messageReceived( com.cannontech.message.util.MessageEvent e )
{
	Message in = e.getMessage();
	int oldRowCount = getRowCount();


	if( in instanceof CBCStates )
	{			
		CBCStates cbcStates = (CBCStates)in;
		CTILogger.info( new ModifiedDate(new java.util.Date().getTime()).toString()
				+ " : Got a CapBank State Message with " + cbcStates.getNumberOfStates()
				+ " states" );

		
		com.cannontech.database.db.state.State[] states =
				new com.cannontech.database.db.state.State[cbcStates.getNumberOfStates()];
		
		synchronized ( states ) 
		{		
			for( int i = 0; i < cbcStates.getNumberOfStates(); i++ )
			{
				cbcStates.getState(i).setRawState( new Integer(i) ); // set the rawstate value
				states[i] = cbcStates.getState(i);
			}		
		}
		

		StateTableModelEvent stMe =
			new StateTableModelEvent(SubBusTableModel.this, 0, getRowCount()-1,
					javax.swing.event.TableModelEvent.ALL_COLUMNS, 
					javax.swing.event.TableModelEvent.UPDATE);
		
		stMe.setStates( states );

		fireTableChanged( stMe );
	}
	else if( in instanceof CBCSubstationBuses )
	{
		CBCSubstationBuses busesMsg = (CBCSubstationBuses)in;
        
        for( int i = (busesMsg.getNumberOfBuses()-1); i >= 0; i-- )
		{
            CTILogger.info(new ModifiedDate(new Date().getTime()).toString()
                    + " : Received SubBus - " + busesMsg.getSubBusAt(i).getCcName() 
                    + "/" + busesMsg.getSubBusAt(i).getCcArea() );

            //if the user can not see this sub, let us remove it
            if( !AuthFuncs.userHasAccessPAO( ownerUser, busesMsg.getSubBusAt(i).getCcId().intValue() ) )
                busesMsg.removeSubBusAt( i );

        }
		
		
		//since this is all the subs, lets just clear what we currently have
		if( busesMsg.isSubDeleted() )
			handleDeletedSub( busesMsg );


        //only add the subs we are allowed to see
		SubBus[] allBuses = new SubBus[busesMsg.getNumberOfBuses()];
		for( int i = 0; i < busesMsg.getNumberOfBuses(); i++ )
		    allBuses[i] = busesMsg.getSubBusAt(i);


		updateSubBuses( allBuses );
	}
    else if( in instanceof CBCSubAreaNames )
    {
        updateAreaList( (CBCSubAreaNames)in );

        CBCGenericTableModelEvent areasChng = 
            new CBCGenericTableModelEvent( 
                SubBusTableModel.this,
                CBCGenericTableModelEvent.SUBBUS_AREA_CHANGE );

        fireTableChanged( areasChng );
    }


	//by using fireTableRowsUpdated(int,int) we do not clear the table selection		
	if( oldRowCount == getRowCount() )
		fireTableRowsUpdated( 0, getRowCount()-1 );
	else
		fireTableDataChanged();

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
	   setFilter( getFilter() );
	}

}
}
