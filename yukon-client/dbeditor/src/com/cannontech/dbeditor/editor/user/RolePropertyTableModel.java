package com.cannontech.dbeditor.editor.user;

import java.awt.Color;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import com.cannontech.common.gui.table.ICTITableRenderer;
import com.cannontech.database.data.lite.LiteYukonRoleProperty;

/**
 * @author rneuharth
 *
 * A model used to display elements of type
 * com.cannontech.database.data.lite.LiteYukonRoleProperty.
 * 
 */
public class RolePropertyTableModel extends AbstractTableModel implements ICTITableRenderer
{
	/* ROW DATA */
	private Vector allRows = null;

	/* END - ROW DATA */

	//a list of changes to the property values
	// contains instances of RolePropertyRow
	private HashMap changedValues = new HashMap();
	

	//The columns and their column index	
	public static final int COL_KEY			= 0;
	public static final int COL_VALUE		= 1;

	//The column names based on their column index
	private static final String[] COLUMN_NAMES =
	{
		"Key",
		"Value"
	};
	
	//The color schemes - based on the schedule status
	private static final Color[] CELL_COLORS =
	{
		//Non-changed value column (uses default value)
		Color.BLACK,
		//Changed value column
		Color.BLUE,		
		//Key column color
		Color.RED		
	};


	/**
	 * RolePropertyTableModel constructor comment.
	 */
	public RolePropertyTableModel() 
	{
		super();
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (3/20/00 11:40:31 AM)
	 */
	public void clear() 
	{
		allRows = new Vector(10);

		fireTableDataChanged();
	}


private void debugJunk()
{
	java.util.Iterator c = changedValues.values().iterator();
	while( c.hasNext() )
	{
		RolePropertyRow r = (RolePropertyRow)c.next();
		System.out.println("   " + 
				r.getLiteProperty().getKeyName() + " = " +
				r.getValue() );
	}
}

	/**
	 * Adds a new row to the table.
	 * Creation date: (3/20/00 11:40:31 AM)
	 */
	public void addRolePropertyRow( LiteYukonRoleProperty prop_, String value_ ) 
	{
		RolePropertyRow row = new RolePropertyRow( prop_ );
		
		//check to see if the user set the value of this property already
		RolePropertyRow rowProp = (RolePropertyRow)changedValues.get(row);
		if( rowProp != null )
		{
			//this means the user has changed this value
			row.setValue( rowProp.getValue() );
		}
		else
		{
			row.setValue( value_ );
		}
		
		getAllRows().add( row );

		fireTableRowsInserted( getRowCount()-1, getRowCount()-1 );
	}

	/**
	 * Returns a the new value for a property if it changed, else a null is returned.
	 * Creation date: (3/20/00 11:40:31 AM)
	 */
	public String getPropertyChange( LiteYukonRoleProperty prop_ ) 
	{
		RolePropertyRow row = new RolePropertyRow( prop_ );
		
		//check to see if the user set the value of this property already
		RolePropertyRow rowProp = (RolePropertyRow)changedValues.get(row);
		if( rowProp != null )
		{
			//this means the user has changed this value
			return rowProp.getValue();
		}
		else
		{
			return null;
		}

	}

	/**
	 * Adds a proprty to the local map
	 * Creation date: (3/20/00 11:40:31 AM)
	 */
	public void addPropertyValue( LiteYukonRoleProperty prop_, String value_ ) 
	{
		RolePropertyRow row = new RolePropertyRow( prop_ );
		row.setValue( value_ );
		
		//check to see if the user set the value of this property already
		RolePropertyRow rowProp = (RolePropertyRow)changedValues.get(row);
		if( rowProp == null )
		{
			changedValues.put( row, row );
		}
		else
		{
			//found it, we may need to change the value
			rowProp.setValue( value_ );			
		}

	}


	/**
	 * Insert the method's description here.
	 * Creation date: (4/9/2002 10:12:03 AM)
	 * @return Vector
	 */
	private Vector getAllRows() 
	{	
		if( allRows == null )
			allRows = new Vector(20);
	
		return allRows;
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
	 * This method returns the value of a row in the form of a SubBus object.
	 * @param rowIndex int
	 */
	public RolePropertyRow getRowAt(int rowIndex) 
	{
		if( rowIndex >= 0 && rowIndex < getRowCount() )
			return (RolePropertyRow)getAllRows().get( rowIndex );
		else
			return null;
	}

	/**
	 * getRowCount method comment.
	 */
	public int getRowCount() 
	{
		return getAllRows().size();
	}
	
	/**
	 * getValueAt method comment.
	 */
	public Object getValueAt(int row, int col) 
	{
		RolePropertyRow prop = getRowAt(row);
		if( prop == null )
			return "<<NULL>>";
	
		switch( col )
		{
		 	case COL_KEY:
				return prop.getLiteProperty().getKeyName();
	
		 	case COL_VALUE:
		 		return prop.getValue();
				//return prop.getDefaultValue();

			default:
				return null;
		}
	
		
	}

	/**
	 *  What to store when a cell is edited.
	 */
	public void setValueAt(Object aValue, int row, int col) 
	{
		if( row >= getRowCount() || col >= getColumnCount() )
			return;
	
		switch( col )
		{
			case COL_VALUE:
				getRowAt(row).setValue( aValue.toString() );
				
				//only add a change if it is not the default setting, sotred as (none) in DB
				if( !aValue.equals(getRowAt(row).getLiteProperty().getDefaultValue()) )
				{
					RolePropertyRow rowProp = (RolePropertyRow)changedValues.get( getRowAt(row) );				
					if( rowProp == null )
					{
						changedValues.put( getRowAt(row), getRowAt(row) );
					}
					else
					{
						//just update the value
						rowProp.setValue( aValue.toString() );					
					}
				}
				else  //we do have a default value, make sure we remove this entry
					changedValues.remove( getRowAt(row) );
								

				break;
		}
	
	}

	public Color getCellBackgroundColor(int row, int col)
	{
		return Color.BLACK;
	}

	public Color getCellForegroundColor(int row, int col)
	{
		RolePropertyRow rowProp = getRowAt(row);
		if( rowProp.getLiteProperty().getDefaultValue().equals(rowProp.getValue()) )
		{
			return CELL_COLORS[0]; //we have a defaulted value
		}
		else
			return CELL_COLORS[1];
	}

	/**
	 * This method was created in VisualAge.
	 * @return boolean
	 * @param row int
	 * @param column int
	 */
	public boolean isCellEditable(int row, int col) 
	{
		return (col == COL_VALUE);
	}

}
