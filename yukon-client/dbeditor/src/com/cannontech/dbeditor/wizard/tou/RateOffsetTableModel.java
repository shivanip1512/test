/*
 * Created on Apr 5, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.dbeditor.wizard.tou;

import java.util.Vector;
import javax.swing.table.AbstractTableModel;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class RateOffsetTableModel extends AbstractTableModel 
{
	public final static int SWITCH_RATE_COLUMN = 0;
	public final static int SWITCH_OFFSET_COLUMN = 1;
	
	private String[] COLUMN_NAMES = 
	{
		"Switch Rate", 
		"Switch Offset"
	};
	
	private Class[] COLUMN_CLASSES = {String.class, String.class};
	
	private Vector rows = null;
	
	private class RowValue
	{
		private String switchRate = null;
		private String switchOffset = null;
		
		public RowValue(String switchRate, String switchOffset)
		{
			super();
			this.switchRate = switchRate;
			this.switchOffset = switchOffset;
		}

		// All getters
		public String getSwitchRate()
			{ return switchRate; }

		public String getSwitchOffset()
			{ return switchOffset; }
			
		// All setters
		public void setSwitchRate(String val)
			{ switchRate = val; }
			
		public void setSwitchOffset(String val)
			{ switchOffset = val; }

		}
		
	public RateOffsetTableModel()
	{
		super();
	}
	
	public void addRowValue(String rate, String offset) 
	{
		getRows().addElement( new RowValue(rate, offset) );
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
	public String getSwitchRateAt(int row ) 
	{
		if( getRows() == null )
			return null;

		if( row <= getRows().size() )
		{
			return ((RowValue)getRows().elementAt(row)).getSwitchRate();
		}
		else
			return null;
	}
	
	public String getSwitchOffsetAt(int row ) 
	{
		if( getRows() == null )
			return null;

		if( row <= getRows().size() )
		{
			return ((RowValue)getRows().elementAt(row)).getSwitchOffset();
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
				case SWITCH_RATE_COLUMN:
					return rowVal.getSwitchRate();
					
				case SWITCH_OFFSET_COLUMN:
					return rowVal.getSwitchOffset();
	
				default:
					return null;
			}
				
		}
		else
			return null;
	}
	
	public boolean isCellEditable(int row, int column)
	{
		return true;
	}
	
	public void removeRowValue(int rowNumber )
	{
		if( rowNumber >=0 && rowNumber < getRowCount() )
		{
			getRows().removeElementAt( rowNumber );
		}
	}
	
	public void setRowValue(int rowNumber, String rate, String offset) 
	{
		if( rowNumber >=0 && rowNumber < getRowCount() )
		{
			getRows().setElementAt( new RowValue(rate, offset), rowNumber );
		}
	}
	
	public void setValueAt(Object value, int row, int col) 
	{
		if( row <= getRows().size() && col < getColumnCount() )
		{

			switch( col )
			{
				case SWITCH_RATE_COLUMN:
					if(value.toString().compareTo("") != 0)
						((RowValue)getRows().elementAt(row)).setSwitchRate(value.toString());
					break;
					
				case SWITCH_OFFSET_COLUMN:
					if(value.toString().compareTo("") != 0)
						((RowValue)getRows().elementAt(row)).setSwitchOffset(value.toString());
					break;
				
				default:
					com.cannontech.clientutils.CTILogger.info(this.getClass() + " tried to set value for an invalid column, column number " + col );
			}
		}
	}
	
}
