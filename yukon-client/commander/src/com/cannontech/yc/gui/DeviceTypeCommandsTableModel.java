/*
 * Created on Sep 14, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.yc.gui;

import java.util.Vector;

import com.cannontech.database.cache.functions.CommandFuncs;
import com.cannontech.database.data.command.DeviceTypeCommand;
import com.cannontech.database.data.lite.LiteCommand;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DeviceTypeCommandsTableModel extends javax.swing.table.AbstractTableModel implements com.cannontech.common.gui.dnd.IDroppableTableModel
{
//	public final static int COMMANDID_COLUMN = 0;
	public final static int LABEL_COLUMN = 0;
	public final static int COMMAND_COLUMN = 1;
	public final static int VISIBILTY_COLUMN = 2;
	public final static int CATEGORY_COLUMN = 3;	//this is the command category, not deviceType
//	public final static int DISPLAYORDER_COLUMN = 4;

	public static String[] columnNames =
	{
//		"Command ID",
		"Label",
		"Command",
		"Visible",
		"Category"
//		"Order"
	};

	public static Class[] columnTypes =
	{
//		Number.class,
		String.class,
		String.class,
		Boolean.class,
		String.class
//		Number.class
	};	

	//LiteDeviceTypeCommands rows
	public Vector rows = new Vector(10);	//LiteDeviceTypeCommands
	
	/**
	 * DeviceTypeCommandsTableModel constructor comment.
	 */
	public DeviceTypeCommandsTableModel()
	{
		super();
	}

	/**
	 * Adds the keyAndValue to model.rows array
	  * Creation date: (10/25/00 11:21:21 AM)
	 */
	public void addRow(DeviceTypeCommand devTypeCommand) 
	{
		// always insert at the front
		getRows().add(0, devTypeCommand);
		fireTableDataChanged();
	}
	
	public void addRowToEnd(DeviceTypeCommand devTypeCommand) 
	{
		// always insert at the front
		int rowCount = getRowCount();
		getRows().add(rowCount, devTypeCommand);
		fireTableDataChanged();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (2/6/2001 11:03:39 AM)
	 * @return 
	 */
//	public KeysAndValues getKeysAndValues()
//	{
//		KeysAndValues returnKAV = new KeysAndValues();
//
//		java.util.Iterator iter = getRows().iterator();
//
//		int i = 0;
//		while( iter.hasNext() )
//		{
//			returnKAV.getKeysAndValues().add( (KeyAndValue)iter.next());
//		}
//
//		return returnKAV;
//	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/26/00 1:19:28 PM)
	 * @return java.lang.Class
	 * @param column int
	 */
	public Class getColumnClass(int column)
	{
		return getColumnTypes()[column];
	}
	/**
	 * getColumnCount method comment.
	 */
	public int getColumnCount() 
	{
		return getColumnNames().length;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/25/00 2:54:48 PM)
	 * @return java.lang.String
	 * @param col int
	 */
	public String getColumnName(int col) {
		return getColumnNames()[col];
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/23/2001 4:10:08 PM)
	 * @return java.lang.Class[]
	 */
	public String[] getColumnNames()
	{
		return columnNames;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/23/2001 4:10:08 PM)
	 * @return java.lang.Class[]
	 */
	public Class[] getColumnTypes()
	{
		return columnTypes;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/25/00 11:26:55 AM)
	 * @return java.lang.Object
	 * @param index int
	 */
	public Object getAttribute(int index, DeviceTypeCommand devTypeCmd_)
	{
		LiteCommand lc = (LiteCommand)CommandFuncs.getCommand(devTypeCmd_.getCommandID().intValue());		
		switch( index )
		{
//			case COMMANDID_COLUMN:
//				return devTypeCmd_.getCommandID();
			case LABEL_COLUMN:
				return lc.getLabel();
			
			case COMMAND_COLUMN:
				return lc.getCommand();
			
			case VISIBILTY_COLUMN:
				return new Boolean(devTypeCmd_.isVisible());

			case CATEGORY_COLUMN:
				return lc.getCategory();
			
//			case DISPLAYORDER_COLUMN:
//				return devTypeCmd_.getDisplayOrder();
		}
		return null;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/25/00 11:21:02 AM)
	 * @return KeyAndValue
	 * @param row int
	 */
	public DeviceTypeCommand getRow(int row)
	{
		return ( row < getRows().size() ? (DeviceTypeCommand) getRows().get(row) : null );
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
	 * Creation date: (10/23/2001 4:05:03 PM)
	 * @return java.util.ArrayList
	 */
	public Vector getRows()
	{
		return rows;
	}


	/**
	 * getValueAt method comment.
	 */
	public Object getValueAt(int row, int col)
	{
		if( row < getRows().size() && col < getColumnCount() )
		{
			return getAttribute(col, (DeviceTypeCommand) getRows().get(row) );
		}
		else
			return null;
			
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/23/2001 9:26:48 AM)
	 * @return boolean
	 * @param row int
	 * @param column int
	 */
	public boolean isCellEditable(int row, int column)
	{
		return ( column == VISIBILTY_COLUMN );
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/25/00 4:42:04 PM)
	 * @param row int
	 */
	public void removeRow(int[] row) 
	{
		Object[] toRemove = new Object[row.length]; 
	
		for( int i = 0; i < row.length; i++ )
			toRemove[i] = getRows().get( row[i] );

		for( int i = 0; i < toRemove.length; i++ )
			getRows().remove(toRemove[i]);

		fireTableDataChanged();
	}
	
	public void removeAllRows() 
	{
		getRows().removeAllElements();
		fireTableDataChanged();
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (10/25/00 4:42:04 PM)
	 * @param row int
	 */
	public void removeRow(int row) 
	{
		if( row < getRows().size() )
			getRows().remove(row);

		fireTableDataChanged();
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (11/6/00 10:07:31 AM)
	 * @param value java.lang.Object
	 * @param row int
	 * @param col int
	 */
	public void setValueAt(Object value, int row, int col) 
	{	
		DeviceTypeCommand ldtc = getRow(row);

//		String valueString = value.toString();	

		switch( col )
		{
//			case COMMANDID_COLUMN:
//				ldtc.getDeviceTypeCommand().setCommandID((Integer)value);
//				ldtc.getCommand().setCommandID((Integer)value);
//				break;
			case LABEL_COLUMN:
				ldtc.getCommand().setLabel(value.toString());
				break;
			case COMMAND_COLUMN:
				ldtc.getCommand().setCommand(value.toString());
				break;
			case VISIBILTY_COLUMN:
			{
				Boolean flag = (Boolean)value;
				if( flag.booleanValue())
					ldtc.getDeviceTypeCommand().setVisibleFlag(new Character('Y'));
				else
					ldtc.getDeviceTypeCommand().setVisibleFlag(new Character('N'));
				break;
			}
			case CATEGORY_COLUMN:
				ldtc.getCommand().setCategory(value.toString());
				break;
			
//			case DISPLAYORDER_COLUMN:
//				ldtc.getDeviceTypeCommand().setDisplayOrder(new Integer(row+1));
//				break;
		}
		fireTableRowsUpdated(row, row);	
	}

	/* (non-Javadoc)
	 * @see com.cannontech.common.gui.dnd.IDroppableTableModel#insertNewRow(java.lang.Object)
	 */
	public void insertNewRow(Object devTypeCmd)
	{
		getRows().addElement( devTypeCmd );
		fireTableDataChanged(); // Tell the listeners a new table has arrived.
	}

	/* (non-Javadoc)
	 * @see com.cannontech.common.gui.dnd.IDroppableTableModel#objectExists(java.lang.Object)
	 */
	public boolean objectExists(Object devTypeCmd)
	{
		if( devTypeCmd instanceof DeviceTypeCommand)
		{
			for( int i = 0; i < getRowCount(); i++ )
			{
				if(((DeviceTypeCommand)getRowAt(i)).getCommandID().intValue() == ((DeviceTypeCommand)devTypeCmd).getCommandID().intValue())
					return true;
			}
		}		
		return false;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.common.gui.dnd.IDroppableTableModel#getRowAt(int)
	 */
	public Object getRowAt(int row)
	{
		return getRows().get(row);
	}

	/* (non-Javadoc)
	 * @see com.cannontech.common.gui.dnd.IDroppableTableModel#insertRowAt(java.lang.Object, int)
	 */
	public void insertRowAt(Object value, int row)
	{
		if( value instanceof DeviceTypeCommand)
		{
			getRows().insertElementAt( value, row );
			fireTableDataChanged();
		}

	}
}
