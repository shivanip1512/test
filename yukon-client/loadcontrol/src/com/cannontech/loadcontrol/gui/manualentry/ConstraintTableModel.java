package com.cannontech.loadcontrol.gui.manualentry;

/**
 * This type was created in VisualAge.
 */
import java.awt.Color;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.loadcontrol.datamodels.ISelectableLMTableModel;
import com.cannontech.loadcontrol.events.LCGenericTableModelEvent;
import com.cannontech.message.server.ServerResponseMsg;

public class ConstraintTableModel extends javax.swing.table.AbstractTableModel implements javax.swing.event.TableModelListener, ISelectableLMTableModel
{
	//contains
	private java.util.Vector rows = null;

	//The columns and their column index	
	public static final int COL_OVERRIDE = 0;
	public static final int COL_PROGRAM_NAME = 1;
	public static final int COL_VIOLATION = 2;
	
	//not implemented for now
    public static final int COL_STATUS = 3;
	public static final int COL_ACTION = 4;

  	
	//The column names based on their column index
	public static final String[] COL_NAMES =
	{
		"Override",
		"Program",
		"Violation"
		
        //"Status",
		//"Action"
	};

	private Class[] COLUMN_CLASSES = 
	{ 
		Boolean.class, String.class, String.class, String.class 
	};
	
	private static final Color[] CELL_COLORS =
	{
		//Ok
		Color.WHITE,
		//Conn error
		Color.RED
	};

	/**
	 * ProgramTableModel constructor comment.
	 */
	public ConstraintTableModel() {
		super();
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (3/20/00 11:40:31 AM)
	 */
	public void clear() 
	{
		//do NOT remove the elements, but just set the reference to our list to a
		//  new reference...setting it to NULL would work (getRows().removeAllElements();)
		rows = new java.util.Vector(10);

		//we can drop the current row selection here
		fireTableDataChanged();
	}

	/**
	 * This method was created in VisualAge.
	 * @return java.awt.Color
	 * @param row int
	 * @param col int
	 */
	public java.awt.Color getCellBackgroundColor(int row, int col) 
	{
		return Color.BLACK;
	}
	/**
	 * This method was created in VisualAge.
	 * @return java.awt.Color
	 * @param row int
	 * @param col int
	 */
	public java.awt.Color getCellForegroundColor(int row, int col) 
	{
		ResponseProg prog = getRowAt( row );
		if( prog.getStatus() == ServerResponseMsg.STATUS_UNINIT )
		{
			return CELL_COLORS[1];
		}
		else
			return CELL_COLORS[0];
	}

	/**
	 * getColumnCount method comment.
	 */
	public int getColumnCount() {
		return COL_NAMES.length;
	}
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.String
	 * @param index int
	 */
	public String getColumnName(int index) {
		return COL_NAMES[index];
	}

	/**
	 * This method returns the value of a row in the form of 
	 * an Object, but its really a LMProgramBase object.
	 */
	public synchronized ResponseProg getRowAt(int rowIndex) 
	{
		if( rowIndex < 0 || rowIndex >= getRowCount() )
			return null;
			
		return (ResponseProg)getRows().get(rowIndex);
	}

	/**
	 * Adds a row 
	 */
	public void addRow( ResponseProg progRow ) 
	{
		getRows().add( progRow );
	}


	/**
	 * getRowCount method comment.
	 */
	public int getRowCount() 
	{
		return getRows().size();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (8/23/00 10:01:03 AM)
	 * @return Vector
	 */
	private java.util.Vector getRows()
	{
		if( rows == null )
			rows = new java.util.Vector(10);
			
		return rows;
	}
	/**
	 * This method was created in VisualAge.
	 * @return java.awt.Color
	 * @param row int
	 * @param col int
	 */
	public java.awt.Color getSelectedRowColor(int row, int col) 
	{
		return getCellForegroundColor(row, col);
	}
	/**
	 * getValueAt method comment.
	 */
	public Object getValueAt(int row, int col) 
	{
	
		if( row <= getRowCount() )
		{
			ResponseProg prg = getRowAt(row);
			
			switch( col )
			{
				case COL_OVERRIDE:
				return prg.getOverride();
				
				case COL_PROGRAM_NAME:
				return prg.getLmProgramBase().getYukonName();

				case COL_VIOLATION:
				return prg.getViolations();

                
                //not implemented
                case COL_STATUS:
                return ServerResponseMsg.getStatusStr(prg.getStatus());
                
				case COL_ACTION:
				return prg.getAction();

				
				default:
				return "";
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
		return ( column == COL_OVERRIDE );
	}

	/**
	 * This method was created in VisualAge.
	 * @param event javax.swing.event.TableModelEvent
	 */
	public void tableChanged(javax.swing.event.TableModelEvent event ) 
	{
		if( event instanceof LCGenericTableModelEvent )
		{
			if( ((LCGenericTableModelEvent)event).getType()
				 == LCGenericTableModelEvent.TYPE_CLEAR )
			{
				clear();
			}
	
		}
	
		//fireTableDataChanged();
		fireTableChanged( event );
	}

	/**
	 * setValueAt method comment.
	 */
	public void setValueAt(Object value, int row, int col) 
	{
		ResponseProg prg = getRowAt(row);

		switch( col )
		{
			
			case COL_OVERRIDE:
				prg.setOverride( (Boolean)value );
				break;

			default:
				CTILogger.info(this.getClass() + " tried to set value for an invalid column, column number " + col );
		}

	}
}