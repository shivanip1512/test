package com.cannontech.tdc.roweditor;

import java.awt.Color;

import com.cannontech.clientutils.commonutils.ModifiedDate;
import com.cannontech.common.gui.table.ICTITableRenderer;
import com.cannontech.common.gui.util.Colors;
import com.cannontech.database.cache.functions.TagFuncs;
import com.cannontech.database.data.lite.LiteTag;
import com.cannontech.tags.Tag;

/**
 * @author rneuharth
 *
 * A table model used to show tags for a given point
 *
 */
public class TagTableModel extends javax.swing.table.AbstractTableModel implements ICTITableRenderer
{
	/* ROW DATA */
	//contains com.cannontech.tags.Tag
	private java.util.Vector _allTags = null;	
	/* END - ROW DATA */


	//The columns and their column index	
	public static final int GROUP_COL = 0;
	public static final int DESC_COL = 1;			
	public static final int TIME_COL = 2;
	public static final int USER_NAME_COL = 3;

	//The column names based on their column index
	private static final String[] COLUMN_NAMES =
	{
		"Tag Group",
		"Description",
		"Timestamp",
		"Tagged By",
	};
	
	public static final Color BG_COLOR = Color.LIGHT_GRAY;

	/**
	 * ScheduleTableModel constructor comment.
	 */
	public TagTableModel() {
		super();
	}
	
	public void addRow( Tag tag )
	{
		getAllTags().add( tag );
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (3/20/00 11:40:31 AM)
	 */
	public void clear() 
	{
		getAllTags().removeAllElements();

		fireTableDataChanged();
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (4/9/2002 10:12:03 AM)
	 * @return Vector
	 */
	private java.util.Vector getAllTags() 
	{	
		if( _allTags == null )
			_allTags = new java.util.Vector(32);
	
		return _allTags;
	}

	/**
	 * This method was created in VisualAge.
	 * @return java.awt.Color
	 * @param row int
	 * @param col int
	 */
	public java.awt.Color getCellBackgroundColor(int row, int col)
	{
		return BG_COLOR;
	}
	
	/**
	 * This method was created in VisualAge.
	 * @return java.awt.Color
	 * @param row int
	 * @param col int
	 */
	public java.awt.Color getCellForegroundColor(int row, int col) 
	{
		LiteTag tagGrp = TagFuncs.getLiteTag( 
				getRowAt(row).getTagID() );

		return Colors.getColor( tagGrp.getColorID() );
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
	 * This method returns the value of a row in the form of a Tag object.
	 * @param rowIndex int
	 */
	public synchronized Tag getRowAt(int rowIndex) 
	{
		if( rowIndex >= 0 && rowIndex < getRowCount() )
			return (Tag)getAllTags().get( rowIndex );
		else
			return null;
	}


	/**
	 * getRowCount method comment.
	 */
	public int getRowCount() 
	{
		return getAllTags().size();
	}

	/**
	 * getValueAt method comment.
	 */
	public Object getValueAt(int row, int col) 
	{
		Tag tag = getRowAt(row);
		LiteTag tagGrp = TagFuncs.getLiteTag( tag.getTagID() );
		
		if( tag == null || tagGrp == null )
			return null;
	
		switch( col )
		{
			case GROUP_COL:
				return tagGrp;

			case DESC_COL:
				return tag.getDescriptionStr();

			case TIME_COL:
				return new ModifiedDate( tag.getTagTime().getTime() );

			case USER_NAME_COL:
				return tag.getUsername();

			default:
				return null;
		}		
	}


	/**
	 * This method was created in VisualAge.
	 * @return boolean
	 * @param row int
	 * @param column int
	 */
	public boolean isCellEditable(int row, int column) 
	{
		return false;
	}


}
