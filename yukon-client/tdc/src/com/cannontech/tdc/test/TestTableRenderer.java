package com.cannontech.tdc.test;

/**
 * Insert the type's description here.
 * Creation date: (2/3/00 1:34:52 PM)
 * @author: 
 */
import java.awt.Color;
import java.awt.Component;

import javax.swing.table.DefaultTableCellRenderer;

public class TestTableRenderer implements javax.swing.table.TableCellRenderer 
{
	public static final DefaultTableCellRenderer DEFAULT_RENDERER = new DefaultTableCellRenderer();
/**
 * TestTableRenderer constructor comment.
 */
public TestTableRenderer() {
	super();
}
/**
 * getTableCellRendererComponent method comment.
 */
public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
{

	Component renderer = DEFAULT_RENDERER.getTableCellRendererComponent( 
				table, value,
				isSelected, hasFocus, row, column );

	Color foreground, background;

	if( isSelected )
	{
		foreground = Color.yellow;
		background = Color.green;
	}
	else
	{
		foreground = Color.blue;
		background = Color.pink;
	}
		
	renderer.setForeground( foreground );
	renderer.setBackground( background );
			
	return renderer;
}
}
