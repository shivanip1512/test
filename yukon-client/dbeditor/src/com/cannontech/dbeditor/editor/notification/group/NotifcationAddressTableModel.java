package com.cannontech.dbeditor.editor.notification.group;

import java.awt.Color;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.gui.table.ICTITableRenderer;
import com.cannontech.database.cache.functions.YukonListFuncs;
import com.cannontech.database.data.lite.LiteContactNotification;

/**
 * @author rneuharth
 *
 * A model used to display elements of types
 * that are in a NotificationGroup
 * 
 */
public class NotifcationAddressTableModel extends AbstractTableModel implements ICTITableRenderer
{
	private Vector allRows = null;

	//The columns and their column index	
	public static final int COL_ADDRESS = 0;
	public static final int COL_TYPE = 1;

	//The column names based on their column index
	private static final String[] COLUMN_NAMES =
	{
		"Address",
		"Type"
	};
	
	//The color schemes - based on the schedule status
	private static final Color[] CELL_COLORS =
	{
		//Emails
		Color.BLACK,
		//Phone Numbers
		Color.BLUE		
	};


	/**
	 * RolePropertyTableModel constructor comment.
	 */
	public NotifcationAddressTableModel() 
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

	/**
	 * Adds a new row to the table.
	 */
	public void addRow( LiteContactNotification lcn ) 
	{
		getAllRows().add( lcn );
		fireTableRowsInserted( getRowCount()-1, getRowCount()-1 );
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
	 */
	public LiteContactNotification getRowAt(int rowIndex) 
	{
		if( rowIndex >= 0 && rowIndex < getRowCount() )
			return (LiteContactNotification)getAllRows().get( rowIndex );
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
		LiteContactNotification lcn = getRowAt(row);
		
		if( lcn == null )
			return "<<NULL>>";
	
		switch( col )
		{
		 	case COL_ADDRESS:
				return lcn.getNotification();
	
		 	case COL_TYPE:
		 		return 
		 			YukonListFuncs.getYukonListEntry(
		 				lcn.getNotificationCategoryID() );

			default:
				return null;
		}
	
		
	}

	/**
	 *  What to store when a cell is edited.
	 */
	public void setValueAt(Object aValue, int row, int col) 
	{
//		if( row >= getRowCount() || col >= getColumnCount() )
//			return;
//	
//		switch( col )
//		{
//			case COL_VALUE:
//				getRowAt(row).setValue( aValue.toString() );
//				
//				//only add a change if it is not the default setting, sotred as (none) in DB
//				if( !aValue.equals(getRowAt(row).getLiteProperty().getDefaultValue()) )
//				{
//					RolePropertyRow rowProp = (RolePropertyRow)changedValues.get( getRowAt(row) );				
//					if( rowProp == null )
//					{
//						changedValues.put( getRowAt(row), getRowAt(row) );
//					}
//					else
//					{
//						//just update the value
//						rowProp.setValue( aValue.toString() );					
//					}
//				}
//				else  //we do have a default value, make sure we remove this entry
//					changedValues.remove( getRowAt(row) );
//								
//
//				break;
//		}
	
	}

	public Color getCellBackgroundColor(int row, int col)
	{
		return Color.BLACK;
	}

	public Color getCellForegroundColor(int row, int col)
	{
		LiteContactNotification lcn = getRowAt(row);
		YukonListEntry entry =
			YukonListFuncs.getYukonListEntry( lcn.getNotificationCategoryID() );
		
		switch( entry.getYukonDefID() )
		{
			case YukonListEntryTypes.YUK_DEF_ID_PHONE:
				return CELL_COLORS[1];

			case YukonListEntryTypes.YUK_DEF_ID_EMAIL:
			default:
				return CELL_COLORS[0];
		}

	}

	/**
	 * This method was created in VisualAge.
	 * @return boolean
	 * @param row int
	 * @param column int
	 */
	public boolean isCellEditable(int row, int col) 
	{
		return false;
	}

}
