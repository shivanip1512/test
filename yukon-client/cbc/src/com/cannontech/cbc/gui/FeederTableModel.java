package com.cannontech.cbc.gui;

/**
 * This type was created in VisualAge.
 */
import java.awt.Color;

import com.cannontech.cbc.tablemodelevents.CBCGenericTableModelEvent;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.Feeder;
import com.cannontech.yukon.cbc.SubBus;

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

	//The column names based on their column index
	private static final String[] COLUMN_NAMES =
	{
		"Feeder Name",		
		"State",
		"Target",
		"VAR Load / Est.",
		"Watts",		
		"PFactor / Est.",
		"Date/Time",
		"Daily Ops"
	};
	
	//The color schemes - based on the schedule status (foreGround, bgColor)
	private static final Color[] CELL_COLORS =
	{
		//Enabled feeder
		Color.GREEN,
		//Disabled feeder
		Color.RED,
		//Pending color
		Color.YELLOW		
	};
	

/**
 * FeederTableModel constructor comment.
 */
public FeederTableModel()
{
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

public Feeder getCapBankFeederOwner( CapBankDevice capBank )
{
	for( int i = 0; i < getRowCount(); i++ )
	{
		java.util.List banks = getRowAt(i).getCcCapBanks();
		for( int j = 0; j < banks.size(); j++ )
			if( banks.get(j).equals(capBank) )
				return getRowAt(i);
	}
			
	return null;
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
		return getCellColor( getRowAt(row) );
	}

	return Color.WHITE;
}

public java.awt.Color getCellColor( Feeder feeder ) 
{
    if( feeder.getCcDisableFlag().booleanValue() )
    {
        return CELL_COLORS[1]; //disabled color
    }
    else if( feeder.getRecentlyControlledFlag().booleanValue() )
    {
        return CELL_COLORS[2]; //pending color
    }
    else
    {
        return CELL_COLORS[0];
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
 * Creation date: (8/23/00 11:42:06 AM)
 */
private SubBus getCurrentSubBus()
{
	return currentSubBus;
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
 * This method returns the feeder with the specified PAO ID
 * @param paoID_ int
 */
public synchronized Feeder getFeeder( int feederID_ ) 
{
	for( int i = 0; i < getRowCount(); i++ )
	{
		Feeder feeder = getRowAt(i);
		if( feeder.getCcId().intValue() == feederID_ )
			return feeder;		
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
        return Feeder.CBC_DISPLAY.getFeederValueAt( feeder, col, getCurrentSubBus() );
	}
	else
		return null; /// MAYBE NOT A GOOD IDEA!!	
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
private boolean isObjectFeeder(Object obj) 
{
	return( obj instanceof Feeder );
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
public synchronized void setCurrentSubBus( final com.cannontech.yukon.cbc.SubBus subBus_ )
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
				throw new IllegalStateException("Only " + com.cannontech.yukon.cbc.Feeder.class.getName() + " should be in a Feeder vector");
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