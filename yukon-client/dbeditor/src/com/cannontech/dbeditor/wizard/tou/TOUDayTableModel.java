/*
 * Created on Dec 7, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.dbeditor.wizard.tou;

import java.util.Vector;
import javax.swing.table.AbstractTableModel;
import com.cannontech.database.data.tou.TOUDay;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TOUDayTableModel extends AbstractTableModel 
{
	public final static int DAY_OF_WEEK_COLUMN = 0;
	public final static int ASSIGNED_TOUDAY_COLUMN = 1;
	
	private String[] COLUMN_NAMES = 
	{
		"Day of Week", 
		"Assigned TOU Day"
	};
	
	private Class[] COLUMN_CLASSES = {String.class, TOUDay.class};
	
	private Vector rows = null;
	
	private class RowValue
	{
		private String dayOfWeek = null;
		private TOUDay assignedTOUDay = null;
		
		public RowValue(String dayOfWeek, TOUDay assignedTOUDay)
		{
			super();
			this.dayOfWeek = dayOfWeek;
			this.assignedTOUDay = assignedTOUDay;
		}

		// All getters
		public String getDayOfWeek()
			{ return dayOfWeek; }

		public TOUDay getAssignedTOUDay()
			{ return assignedTOUDay; }
			
		// All setters
		public void setDayOfWeek(String val)
			{ dayOfWeek = val; }
			
		public void setAssignedTOUDay(TOUDay val)
			{ assignedTOUDay = val; }

		}
		
	public TOUDayTableModel()
	{
		super();
	}
	
	public void addRowValue(String weekDay, TOUDay touDay ) 
	{
		getRows().addElement( new RowValue(weekDay, touDay) );
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
	public String getDayOfWeekAt(int row ) 
	{
		if( getRows() == null )
			return null;

		if( row <= getRows().size() )
		{
			return ((RowValue)getRows().elementAt(row)).getDayOfWeek();
		}
		else
			return null;
	}
	
	public TOUDay getAssignedTOUDayAt(int row ) 
	{
		if( getRows() == null )
			return null;

		if( row <= getRows().size() )
		{
			return ((RowValue)getRows().elementAt(row)).getAssignedTOUDay();
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
	 * Creation date: (12/7/04 5:07:51 PM)
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
				case DAY_OF_WEEK_COLUMN:
					return rowVal.getDayOfWeek();
					
				case ASSIGNED_TOUDAY_COLUMN:
					return rowVal.getAssignedTOUDay();
	
				default:
					return null;
			}
				
		}
		else
			return null;
	}
	
	public boolean isCellEditable(int row, int column)
	{
		if(column == DAY_OF_WEEK_COLUMN )
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
	
	public void setRowValue(int rowNumber, String weekDay, TOUDay day) 
	{
		if( rowNumber >=0 && rowNumber < getRowCount() )
		{
			getRows().setElementAt( new RowValue(weekDay, day), rowNumber );
		}
	}
	
	public void setValueAt(Object value, int row, int col) 
	{
		if( row <= getRows().size() && col < getColumnCount() )
		{

			switch( col )
			{
				case DAY_OF_WEEK_COLUMN:
					if(value.toString().compareTo("") != 0)
						((RowValue)getRows().elementAt(row)).setDayOfWeek(value.toString());
					break;
					
				case ASSIGNED_TOUDAY_COLUMN:
					if(value.toString().compareTo("") != 0)
						((RowValue)getRows().elementAt(row)).setAssignedTOUDay((TOUDay)value);
					break;
				
				default:
					com.cannontech.clientutils.CTILogger.info(this.getClass() + " tried to set value for an invalid column, column number " + col );
			}
		}
	}
	
}
