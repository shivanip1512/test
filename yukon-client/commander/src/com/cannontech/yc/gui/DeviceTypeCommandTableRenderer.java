package com.cannontech.yc.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;

import com.cannontech.common.gui.dnd.DAndDJTableRendererWrapper;
import com.cannontech.database.data.command.DeviceTypeCommand;

/**
 * Insert the type's description here.
 * Creation date: (2/12/2002 6:08:05 PM)
 * @author: 
 */

public class DeviceTypeCommandTableRenderer extends DAndDJTableRendererWrapper
{
/**
 * DeviceTypeCommandTableRenderer constructor comment.
 */
public DeviceTypeCommandTableRenderer() {
	super();
}
/**
 * Return a component that has been configured to display the specified
 * value. That component's <code>paint</code> method is then called to
 * "render" the cell.  If it is necessary to compute the dimensions
 * of a list because the list cells do not have a fixed size, this method
 * is called to generate a component on which <code>getPreferredSize</code>
 * can be invoked.
 */
public Component getTableCellRendererComponent(
			JTable table, Object value, boolean isSelected, 
			boolean hasFocus, int row, int column)
{
	
	java.awt.Component c = super.getTableCellRendererComponent(
					table, value, isSelected, 
					hasFocus, row, column);

	if( table.getModel() instanceof DeviceTypeCommandsTableModel)
	{
		DeviceTypeCommand dtc = ((DeviceTypeCommandsTableModel)table.getModel()).getRow(row);
		if( dtc.getCommandID().intValue() <= 0)
		{
			/** If the commandID is CTI (<= 0) then make it appear disabled */
			if( table.getSelectedRow() == row)
				c.setForeground(Color.LIGHT_GRAY);
			else
				c.setForeground(Color.GRAY);				
		}
		else
			c.setForeground(Color.BLACK);
		
	}
		
	return c;	
}
}
