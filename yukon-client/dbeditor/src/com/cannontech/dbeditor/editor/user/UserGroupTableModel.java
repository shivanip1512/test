package com.cannontech.dbeditor.editor.user;

/**
 * Insert the type's description here.
 * Creation date: (11/9/00 4:55:25 PM)
 * @author: 
 */
import java.awt.Color;
import java.util.Vector;

import com.cannontech.common.gui.table.ICTITableRenderer;
import com.cannontech.database.data.lite.LiteYukonGroup;

public class UserGroupTableModel extends javax.swing.table.AbstractTableModel implements ICTITableRenderer
{
	public final static int COL_SELECTED = 0;
	public final static int COL_NAME = 1;
	
	private String[] COLUMN_NAMES = 
	{
		" ",  //no name for now
		"Group Name" 
	};

	private Class[] COLUMN_CLASSES = { Boolean.class, String.class };

	//The color schemes - based on the schedule status
	private static final Color[] CELL_COLORS =
	{
		//selected row
		Color.BLACK,

		//non selected row
		Color.BLACK		
	};

	
	
	//contains com.cannontech.database.data.lite.LiteYukonGroup;
	private Vector rows = null;
	
	//parallel vector to say what has been selected (contains Boolean)
	private Vector selectedRows = null;
	
	
	/**
	 * UserGroupTableModel constructor comment.
	 */
	public UserGroupTableModel() {
		super();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (11/9/00 5:17:31 PM)
	 * @param alarm java.lang.String
	 * @param generate boolean
	 * @param notify boolean
	 */
	public void addRowValue( LiteYukonGroup yukonGroup_, Boolean selected_ ) 
	{
		getRows().add( yukonGroup_ );
		getSelectedRows().add( selected_ );
	}


	public Color getCellBackgroundColor(int row, int col)
	{
		return Color.BLACK;
	}

	public Color getCellForegroundColor(int row, int col)
	{
		Boolean isSelected = getSelectedRowAt(row);
		if( isSelected.booleanValue() )
		{
			return CELL_COLORS[0]; //we have a non selected row
		}
		else
			return CELL_COLORS[1];
	}

	/**
	 * This method was created in VisualAge.
	 * @return java.lang.String
	 * @param index int
	 */
	public Class getColumnClass(int index) {
		return COLUMN_CLASSES[index];
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
	 * getRowAt method comment.
	 */
	public LiteYukonGroup getRowAt( int row ) 
	{
		if( row <= getRows().size() )
		{
			return (LiteYukonGroup)getRows().get(row);
		}
		else
			return null;
	}

	/**
	 * getSelectedRowAt method comment.
	 */
	public Boolean getSelectedRowAt( int row ) 
	{
		if( row <= getRows().size() )
		{
			return (Boolean)getSelectedRows().get(row);
		}
		else
			return null;
	}
	
	/**
	 * getSelectedRows method comment.
	 */
	private Vector getSelectedRows() 
	{
		if( selectedRows == null )
			selectedRows = new Vector(16);
		
		return selectedRows;
	}

	/**
	 * getRowCount method comment.
	 */
	public int getRowCount() {
		return getRows().size();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (11/9/00 5:07:51 PM)
	 * @return java.util.Vector
	 */
	private java.util.Vector getRows() 
	{
		if( rows == null )
			rows = new Vector(10);
			
		return rows;
	}
	
	public int indexOf( LiteYukonGroup group_ )
	{
		return getRows().indexOf( group_ );
	}
	
	/**
	 * getValueAt method comment.
	 */
	public Object getValueAt(int row, int col) 
	{
		if( getRows() == null )
			return null;
	
		if( row <= getRows().size() )
		{
			LiteYukonGroup rowVal = (LiteYukonGroup)getRows().get(row);
			Boolean sel = (Boolean)getSelectedRows().get(row);
			
			switch( col )
			{
				case COL_SELECTED:
					return sel;
	
			 	case COL_NAME:
					return rowVal.toString();
					
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
	public boolean isCellEditable(int row, int column)
	{
		return( column == COL_SELECTED );
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (11/9/00 5:17:31 PM)
	 * @param rowNumber int
	 */
	public void removeRow(int rowNumber )
	{
		if( rowNumber >=0 && rowNumber < getRowCount() )
		{
			getRows().remove( rowNumber );
			getSelectedRows().remove( rowNumber );
		}
	}

	/**
	 * setValueAt method comment.
	 */
	// THIS METHOD IS NEEDED SINCE I AM USING A DEFAULTCELLEDITOR AND A CELLRENDERRER
	//   THAT I CREATED, WHEN AN EDITING OCCURS, THIS METHOD IS CALLED.
	public void setValueAt(Object value, int row, int col) 
	{
		if( row <= getRows().size() && col < getColumnCount() )
		{
	
			switch( col )
			{
			 	case COL_SELECTED:
					getSelectedRows().setElementAt( new Boolean(value.toString()), row );
					break;
			}
		}
	}

}
