/*
 * Created on Apr 5, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.dbeditor.wizard.tou;

import java.util.Vector;
import javax.swing.table.AbstractTableModel;
import com.cannontech.database.data.lite.LiteGear;
import com.cannontech.database.data.lite.LiteYukonPAObject;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class MadTOUDiseaseTableModel extends AbstractTableModel 
{
	public final static int OFFSET_COLUMN = 0;
	public final static int RATE_COLUMN = 1;
	
	private String[] COLUMN_NAMES = 
	{
		"Offset", 
		"Rate"
	};
	
	private Class[] COLUMN_CLASSES = {String.class, String.class};
	
	private Vector rows = null;
	
	private class RowValue
	{
		private String offset = null;
		private String rate = null;
		
		public RowValue(String offset, String rate )
		{
			super();
			this.offset = offset;
			this.rate = rate;
		}

		// All getters
		public String getOffset()
			{ return offset; }
		
		public String getRate()
			{ return rate; }

		// All setters
		public void setOffset(String val)
			{ offset = val; }

		public void setRate(String val)
			{ rate = val; }
		
		}
		
	public MadTOUDiseaseTableModel()
	{
		super();
	}
	
	public void addRowValue(String offset, String rate) 
	{
		getRows().addElement( new RowValue(offset, rate) );
	}
	
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
	 * getValueAt method comment.
	 */
	public String getOffsetAt(int row ) 
	{
		if( getRows() == null )
			return null;

		if( row <= getRows().size() )
		{
			return ((RowValue)getRows().elementAt(row)).getOffset();
		}
		else
			return null;
	}
		
	public String getRateAt(int row ) 
	{
		if( getRows() == null )
			return null;

		if( row <= getRows().size() )
		{
			return ((RowValue)getRows().elementAt(row)).getRate();
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
			rows = new Vector();
		
		return rows;
	}	

	public Object getValueAt(int row, int col) 
	{
		if( getRows() == null )
			return null;

		if( row <= getRows().size() )
		{
			RowValue rowVal = ((RowValue)getRows().elementAt(row));
		
			switch( col )
			{
				case OFFSET_COLUMN:
					return rowVal.getOffset();
	
				case RATE_COLUMN:
					return rowVal.getRate();
				
				default:
					return null;
			}
				
		}
		else
			return null;
	}
	
	public boolean isCellEditable(int row, int column)
	{
		if (column == OFFSET_COLUMN && row == 0)
			return false;
		else
			return true;
	}
	
	public void removeRowValue(int rowNumber )
	{
		if( rowNumber >=0 && rowNumber < getRowCount() )
		{
			getRows().removeElementAt( rowNumber );
		}
	}
	
	public void setRowValue(int rowNumber, String offset, String rate) 
	{
		if( rowNumber >=0 && rowNumber < getRowCount() )
		{
			getRows().setElementAt( new RowValue(offset, rate), rowNumber );
		}
	}
	
	public void setValueAt(Object value, int row, int col) 
	{
		if( row <= getRows().size() && col < getColumnCount() )
		{

			switch( col )
			{
				case OFFSET_COLUMN:
					if(value.toString().compareTo("") != 0)
						((RowValue)getRows().elementAt(row)).setOffset(value.toString());
					break;
				
				case RATE_COLUMN:
					((RowValue)getRows().elementAt(row)).setRate(value.toString());
					break;
				
				default:
					com.cannontech.clientutils.CTILogger.info(this.getClass() + " tried to set value for an invalid column, column number " + col );
			}
		}
	}
	
}
