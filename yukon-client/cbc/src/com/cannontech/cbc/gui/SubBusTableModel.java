package com.cannontech.cbc.gui;

/**
 * This type was created in VisualAge.
 */
import java.awt.Color;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import com.cannontech.cbc.tablemodelevents.CBCGenericTableModelEvent;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.commonutils.ModifiedDate;
import com.cannontech.common.login.ClientSession;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageListener;
import com.cannontech.yukon.cbc.CBCCommand;
import com.cannontech.yukon.cbc.CBCSubAreaNames;
import com.cannontech.yukon.cbc.CBCSubstationBuses;
import com.cannontech.yukon.cbc.CBCUtils;
import com.cannontech.yukon.cbc.SubBus;

public class SubBusTableModel extends javax.swing.table.AbstractTableModel implements MessageListener, com.cannontech.tdc.alarms.gui.AlarmTableModel, com.cannontech.common.gui.util.SortableTableModel
{
	/* ROW DATA */
	private java.util.Vector allSubBuses = null;
	private java.util.List currentSubBuses = null;	
	/* END ROW DATA */

    private final Vector areaNames = new Vector(32);
    
	// the holder for the current filter, default to all
    private String filter = null;

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
		"Watts / Volts",
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
        return getCellColor( getRowAt(row) );
	}

	return Color.white;
}


/**
 * This method was created in VisualAge.
 * @return java.awt.Color
 * @param row int
 * @param col int
 */
public java.awt.Color getCellColor( SubBus subBus ) 
{
    if( subBus.getCcDisableFlag().booleanValue() )
    {
        return cellColors[1]; //disabled color
    }
    else if( subBus.getRecentlyControlledFlag().booleanValue() )
    {
        return cellColors[2]; //pending color
    }
    else
    {
        return cellColors[0];
    }
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
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) 
{
	SubBus sub = getRowAt(row);
    return CBCUtils.CBC_DISPLAY.getSubBusValueAt( sub, col );
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
		CTILogger.info("*** Could not find SubBus with the area = " + getFilter() );
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


/**
 * Removes a subBus from our list
 */
private void handleDeletedSub( int subID )
{
	for( int i = 0; i < getAllSubBuses().size(); i++ ) {

		SubBus existingSub = (SubBus)getAllSubBuses().get(i);
		
		if( subID == existingSub.getCcId().intValue() ) {
			removeSubBus( existingSub );
		}

	}	
}

/**
 * Removes the specified subbus
 * @param SubBus
 */
private void removeSubBus( SubBus bus_ )
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


	if( in instanceof CBCSubstationBuses )
	{
		CBCSubstationBuses busesMsg = (CBCSubstationBuses)in;
        
        for( int i = (busesMsg.getNumberOfBuses()-1); i >= 0; i-- )
		{
            CTILogger.info(new ModifiedDate(new Date().getTime()).toString()
                    + " : Received SubBus - " + busesMsg.getSubBusAt(i).getCcName() 
                    + "/" + busesMsg.getSubBusAt(i).getCcArea() );

            //if the user can not see this sub, let us remove it
            if( !DaoFactory.getAuthDao().userHasAccessPAO( ownerUser, busesMsg.getSubBusAt(i).getCcId().intValue() ) )
                busesMsg.removeSubBusAt( i );

        }


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
	else if( in instanceof CBCCommand )
	{
		CBCCommand cmd = (CBCCommand)in;

		//since this is all the subs, lets just clear what we currently have
		if( cmd.getCommand() == CBCCommand.DELETE_ITEM )
			handleDeletedSub( cmd.getDeviceID() );

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

	//be sure that this new list of SubBuses is sorted
	Arrays.sort( newBuses, CBCUtils.SUB_AREA_COMPARATOR );

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
				break;
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
					CBCUtils.SUB_AREA_COMPARATOR );

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
