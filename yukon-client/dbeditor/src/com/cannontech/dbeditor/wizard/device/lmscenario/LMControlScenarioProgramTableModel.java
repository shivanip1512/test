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
import com.cannontech.database.data.lite.LiteYukonPAObject;

import com.cannontech.database.db.device.lm.LMControlScenarioProgram;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LMControlScenarioProgramTableModel extends AbstractTableModel 
{
	public final static int PROGRAMLITEPAO_COLUMN = 0;
	public final static int STARTOFFSET_COLUMN = 1;
	public final static int STOPOFFSET_COLUMN = 2;
	public final static int STARTGEAR_COLUMN = 3;
	
	private String[] COLUMN_NAMES = 
	{
		"Program", 
		"Start Offset",
		"Stop Offset", 
		"Start Gear"
	};
	
	private Class[] COLUMN_CLASSES = {LiteYukonPAObject.class, String.class, String.class, LiteGear.class};
	
	private Vector rows = null;
	
	private class RowValue
	{
		private LiteYukonPAObject programLitePAO = null;
		private String startOffset = null;
		private String stopOffset = null;
		private LiteGear initialGear = null;
		
		public RowValue(LiteYukonPAObject programLitePAO, String startOffset, String stopOffset, LiteGear initialGear )
		{
			super();
			this.programLitePAO = programLitePAO;
			this.startOffset = startOffset;
			this.stopOffset = stopOffset;
			this.initialGear = initialGear;
		}

		// All getters
		public LiteYukonPAObject getProgramLitePAO()
			{ return programLitePAO; }

		public String getStartOffset()
			{ return startOffset; }
			
		public String getStopOffset()
			{ return stopOffset; }
		
		public LiteGear getStartGear()
			{ return initialGear; }

		// All setters
		public void setProgramLitePAO(LiteYukonPAObject val)
			{ programLitePAO = val; }

		public void setStartOffset(String val)
			{ startOffset = val; }
			
		public void setStopOffset(String val)
			{ stopOffset = val; }

		public void setStartGear(LiteGear val)
			{ initialGear = val; }
		
		}
		
	public LMControlScenarioProgramTableModel()
	{
		super();
	}
	
	public void addRowValue(LiteYukonPAObject programLitePAO, String startOffset, String stopOffset, LiteGear initialGear) 
	{
		getRows().addElement( new RowValue(programLitePAO, startOffset, stopOffset, initialGear) );
	}
	
	public LiteYukonPAObject getProgramLitePAOAt(int row ) 
	{
		if( getRows() == null )
			return null;

		if( row <= getRows().size() )
		{
			return ((RowValue)getRows().elementAt(row)).getProgramLitePAO();		
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
	public String getStartOffsetAt(int row ) 
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
	
	public String getStopOffsetAt(int row ) 
	{
		if( getRows() == null )
			return null;

		if( row <= getRows().size() )
		{
			return ((RowValue)getRows().elementAt(row)).getStopOffset();
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
				case PROGRAMLITEPAO_COLUMN:
					return rowVal.getProgramLitePAO();

				case STARTOFFSET_COLUMN:
					return rowVal.getStartOffset();
					
				case STOPOFFSET_COLUMN:
					return rowVal.getStopOffset();
	
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
		if (column == PROGRAMLITEPAO_COLUMN)
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
	
	public void setRowValue(int rowNumber, LiteYukonPAObject name, String start, String stop, LiteGear gear) 
	{
		if( rowNumber >=0 && rowNumber < getRowCount() )
		{
			getRows().setElementAt( new RowValue(name, start, stop, gear), rowNumber );
		}
	}
	
	public void setValueAt(Object value, int row, int col) 
	{
		if( row <= getRows().size() && col < getColumnCount() )
		{

			switch( col )
			{
				case PROGRAMLITEPAO_COLUMN:
					((RowValue)getRows().elementAt(row)).setProgramLitePAO((LiteYukonPAObject)value);
					break;
				
				case STARTOFFSET_COLUMN:
					if(value.toString().compareTo("") != 0)
						((RowValue)getRows().elementAt(row)).setStartOffset(value.toString());
					break;
					
				case STOPOFFSET_COLUMN:
					if(value.toString().compareTo("") != 0)
						((RowValue)getRows().elementAt(row)).setStopOffset(value.toString());
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
