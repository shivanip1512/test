package com.cannontech.common.gui.util;

/**
 * This type was created in VisualAge.
 */
import java.awt.Color;

public class ColorChooserComboBox extends javax.swing.JComboBox {

	private final static Color[] colors =
	{
		Color.blue,
		Color.red,
		Color.green,
		Color.gray,
		Color.darkGray,
		Color.black,
		Color.pink,
		Color.orange,
		Color.yellow,
		Color.magenta,
		Color.cyan,
		
	};
/**
 * ColorChooserComboBox constructor comment.
 */
public ColorChooserComboBox() {
	super();
	initialize();
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Color
 */
public java.awt.Color getSelectedColor() {
	return (java.awt.Color) getSelectedItem();
}
/**
 * This method was created in VisualAge.
 */
public void initialize() {

	class ListRenderer implements javax.swing.ListCellRenderer
	{
		public java.awt.Component getListCellRendererComponent( javax.swing.JList list, Object value, int index, boolean isSelected, boolean hasFocus )
		{
		 	java.awt.Color c = (java.awt.Color) value;

		 	class ColorComponent extends java.awt.Component 
			{
				public java.awt.Color fillColor;
				
				public void paint(java.awt.Graphics g )
				{
					g.setColor( fillColor );
					g.fillRect( 0, 0, getSize().width, getSize().height );
				}

				public java.awt.Dimension getPreferredSize()
				{
					return new java.awt.Dimension( 126, 27 );
				}
			};

			ColorComponent comp = new ColorComponent();
			comp.fillColor = c;

			return comp;
			
		}
	}

	setRenderer( new ListRenderer() );
	setMaximumRowCount( 4 );
	for( int i = 0; i < colors.length; i++ )
		addItem( colors[i] );	
}
/**
 * This method was created in VisualAge.
 * @param c java.awt.Color
 */
public void setSelectedColor( java.awt.Color c) {

	setSelectedItem( c );
}
}
