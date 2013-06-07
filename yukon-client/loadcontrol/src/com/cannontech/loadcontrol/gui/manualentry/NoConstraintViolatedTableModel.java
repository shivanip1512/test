package com.cannontech.loadcontrol.gui.manualentry;

import java.awt.Color;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.loadcontrol.datamodels.ISelectableLMTableModel;
import com.cannontech.loadcontrol.events.LCGenericTableModelEvent;
import com.cannontech.messaging.message.server.ServerResponseMessage;

public class NoConstraintViolatedTableModel extends javax.swing.table.AbstractTableModel implements javax.swing.event.TableModelListener, ISelectableLMTableModel {
	//contains
	private java.util.Vector rows = null;

	//The columns and their column index	
	public static final int COL_PROGRAM_NAME = 0;
	public static final int COL_NOVIOLATION = 1;
	
	//The column names based on their column index
	public static final String[] COL_NAMES = {
		"Program",
		"Server Reports"
	};

	private static final Color[] CELL_COLORS = {
		//Ok
		Color.WHITE,
		Color.GREEN		
	};

	public NoConstraintViolatedTableModel() {
		super();
	}
	
	public void clear() {
		//do NOT remove the elements, but just set the reference to our list to a
		//  new reference...setting it to NULL would work (getRows().removeAllElements();)
		rows = new java.util.Vector(10);

		//we can drop the current row selection here
		fireTableDataChanged();
	}

	public java.awt.Color getCellBackgroundColor(int row, int col) {
		return Color.BLACK;
	}

	public java.awt.Color getCellForegroundColor(int row, int col) {
		ResponseProg prog = getRowAt( row );

		if( prog.getStatus() == ServerResponseMessage.STATUS_UNINIT ) {
			return CELL_COLORS[0];
		}
		else
			return CELL_COLORS[1];
	}

	public int getColumnCount() {
		return COL_NAMES.length;
	}
    
	public String getColumnName(int index) {
		return COL_NAMES[index];
	}

	public synchronized ResponseProg getRowAt(int rowIndex) {
		if( rowIndex < 0 || rowIndex >= getRowCount() )
			return null;
			
		return (ResponseProg)getRows().get(rowIndex);
	}

	public void addRow( ResponseProg progRow ) {
		getRows().add( progRow );
	}

	public int getRowCount() {
		return getRows().size();
	}
    
	private java.util.Vector getRows() {
		if( rows == null )
			rows = new java.util.Vector(10);
			
		return rows;
	}
    
	public java.awt.Color getSelectedRowColor(int row, int col) {
		return getCellForegroundColor(row, col);
	}
    
	public Object getValueAt(int row, int col) {
		if( row <= getRowCount() ) 	{
			ResponseProg prg = getRowAt(row);
			
			switch( col ) {
				case COL_PROGRAM_NAME:
				return prg.getLmProgramBase().getYukonName();

				case COL_NOVIOLATION:
				return "No constraints violated";

				default:
				return "";
			}

		}
		else
			return null;
	}
    
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	public void tableChanged(javax.swing.event.TableModelEvent event ) {
		if( event instanceof LCGenericTableModelEvent ) {
			if( ((LCGenericTableModelEvent)event).getType()
				 == LCGenericTableModelEvent.TYPE_CLEAR ) {
				clear();
			}
		}
	
		//fireTableDataChanged();
		fireTableChanged( event );
	}

	public void setValueAt(Object value, int row, int col) {
		ResponseProg prg = getRowAt(row);

		switch( col ) {
			default:
				CTILogger.info(this.getClass() + " tried to set value for an invalid column, column number " + col );
		}
	}
}
