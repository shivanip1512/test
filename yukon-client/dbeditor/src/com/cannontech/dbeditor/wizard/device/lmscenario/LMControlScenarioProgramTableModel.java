/*
 * Created on Apr 5, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.dbeditor.wizard.device.lmscenario;

import java.util.Vector;
import javax.swing.table.AbstractTableModel;
import com.cannontech.database.data.lite.LiteGear;

import com.cannontech.database.db.device.lm.LMControlScenarioProgram;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LMControlScenarioProgramTableModel extends AbstractTableModel 
{
	public final static int PROGRAMNAME_COLUMN = 0;
	public final static int STARTOFFSET_COLUMN = 1;
	public final static int STARTGEAR_COLUMN = 2;
	
	private String[] COLUMN_NAMES = 
	{
		"Program", 
		"Start Offset", 
		"Start Gear"
	};
	
	private Class[] COLUMN_CLASSES = {String.class, Integer.class, Integer.class, LiteGear.class};
	
	private Vector rows = null;
	
	private class RowValue
	{
		private String programName = null;
		private Integer startOffset = null;
		private LiteGear initialGear = null;
		
		public RowValue(String programName, Integer startOffset, LiteGear initialGear )
		{
			super();
			this.programName = programName;
			this.startOffset = startOffset;
			this.initialGear = initialGear;
		}

		// All getters
		public String getProgramName()
			{ return programName; }

		public Integer getStartOffset()
			{ return startOffset; }
		
		public LiteGear getStartGear()
			{ return initialGear; }

		// All setters
		public void setProgramName(String val)
			{ programName = val; }

		public void setStartOffset(Integer val)
			{ startOffset = val; }

		public void setStartGear(LiteGear val)
			{ initialGear = val; }
		
		}
		
	public LMControlScenarioProgramTableModel()
	{
		super();
	}
	
	public void addRowValue(String programName, Integer startOffset, LiteGear initialGear) 
	{
		getRows().addElement( new RowValue(programName, startOffset, initialGear) );
	}
	
	public String getProgramNameAt(int row ) 
	{
		if( getRows() == null )
			return null;

		if( row <= getRows().size() )
		{
			return ((RowValue)getRows().elementAt(row)).getProgramName();		
		}
		else
			return null;
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
	public Integer getStartOffsetAt(int row ) 
	{
		if( getRows() == null )
			return null;

		if( row <= getRows().size() )
		{
			return ((RowValue)getRows().elementAt(row)).getStartOffset();
		}
		else
			return null;
	}
		
	public LiteGear getStartGearAt(int row ) 
	{
		if( getRows() == null )
			return null;

		if( row <= getRows().size() )
		{
			return ((RowValue)getRows().elementAt(row)).getStartGear();
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
				case PROGRAMNAME_COLUMN:
					return rowVal.getProgramName();

				case STARTOFFSET_COLUMN:
					return rowVal.getStartOffset();
	
				case STARTGEAR_COLUMN:
					return rowVal.getStartGear();
				
				default:
					return null;
			}
				
		}
		else
			return null;
	}
	
	public boolean isCellEditable(int row, int column)
	{
		if (column == PROGRAMNAME_COLUMN)
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
	
	public void setRowValue(int rowNumber, String name, Integer start, Integer stop, LiteGear gear) 
	{
		if( rowNumber >=0 && rowNumber < getRowCount() )
		{
			getRows().setElementAt( new RowValue(name, start, gear), rowNumber );
		}
	}
	
	public void setValueAt(Object value, int row, int col) 
	{
		if( row <= getRows().size() && col < getColumnCount() )
		{

			switch( col )
			{
				case PROGRAMNAME_COLUMN:
					((RowValue)getRows().elementAt(row)).setProgramName(value.toString());
					break;
				
				case STARTOFFSET_COLUMN:
					if(value.toString().compareTo("") != 0)
						((RowValue)getRows().elementAt(row)).setStartOffset(new Integer(value.toString()));
					break;
				
				case STARTGEAR_COLUMN:
					((RowValue)getRows().elementAt(row)).setStartGear((LiteGear)value);
					break;
				
				default:
					com.cannontech.clientutils.CTILogger.info(this.getClass() + " tried to set value for an invalid column, column number " + col );
			}
		}
	}
	
}
