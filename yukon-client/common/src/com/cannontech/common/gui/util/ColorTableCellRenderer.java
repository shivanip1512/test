package com.cannontech.common.gui.util;

/**
 * This type was created in VisualAge.
 */
import java.awt.Component;

public class ColorTableCellRenderer extends javax.swing.table.DefaultTableCellRenderer {

	private class ColorComponent extends Component
	{
		java.awt.Color c;
		
		public ColorComponent(java.awt.Color color)
		{
			super();
			this.c = color;
		}

		public void paint( java.awt.Graphics g )
		{
			g.setColor( this.c );
			g.fillRect( 0, 0, getSize().width, getSize().height );
		}
	}
/**
 * ColorTableCellRenderer constructor comment.
 */
public ColorTableCellRenderer() {
	super();
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Component
 * @param table javax.swing.JTable
 * @param value java.lang.Object
 * @param isSelected boolean
 * @param hasFocus boolean
 * @param row int
 * @param column int
 */
public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

	if( !( value instanceof java.awt.Color ) )
	{
		return super.getTableCellRendererComponent( table, value, isSelected, hasFocus, row, column);
	}
	else
	{
		return new ColorComponent( (java.awt.Color) value );
	}
	
}
}
