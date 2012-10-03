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
		//default
		Color.BLACK,

		//conflict
		Color.RED,

		//disable / non editable
		Color.GRAY
	};

	
	
	//contains com.cannontech.database.data.lite.LiteYukonGroup;
	private Vector rows = null;
	
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
		SelectableGroupRow s = new SelectableGroupRow();
		s.setLiteYukonGroup( yukonGroup_ );
		s.selected( selected_ );
		getRows().add( s );
	}


	public Color getCellBackgroundColor(int row, int col)
	{
		return Color.BLACK;
	}

	public Color getCellForegroundColor(int row, int col)
	{
		if( getRowAt(row).hasConflict() )
			return CELL_COLORS[1];
		else
			return CELL_COLORS[0];
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
	public SelectableGroupRow getRowAt( int row ) 
	{
		if( row <= getRows().size() )
		{
			return (SelectableGroupRow)getRows().get(row);
		}
		else
			return null;
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
		for( int i = 0; i < getRowCount(); i++ )
		{
			if( getRowAt(i).getLiteYukonGroup().equals(group_) )
				return i;
		}
		
		return -1;
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
			SelectableGroupRow sr = getRowAt(row);
			
			switch( col )
			{
				case COL_SELECTED:
					return sr.isSelected();
	
			 	case COL_NAME:
					return 
						sr.getLiteYukonGroup().toString() +
						(sr.hasConflict() ? "  (role conflict)" : "");
					
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
		return( column == COL_SELECTED);
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
			SelectableGroupRow sRow = getRowAt(row);
			switch( col )
			{
			 	case COL_SELECTED:
					sRow.selected( new Boolean(value.toString()) );
					break;
			}
		}
	}

}
