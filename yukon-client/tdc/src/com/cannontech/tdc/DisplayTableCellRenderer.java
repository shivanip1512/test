package com.cannontech.tdc;

/**
 * Insert the type's description here.
 * Creation date: (2/3/00 1:26:04 PM)
 * @author: 
 */
import java.awt.Component;

import com.cannontech.common.gui.util.Colors;
import com.cannontech.common.gui.util.SortTableModelWrapper;
import com.cannontech.tdc.custom.CustomDisplay;

public class DisplayTableCellRenderer extends javax.swing.JLabel implements javax.swing.table.TableCellRenderer
{
	private java.awt.Color borderColor = new java.awt.Color(125,50,180);
	private java.awt.Font boldFont = null;
	private java.awt.Font plainFont = null;

/**
 * DisplayTableCellRenderer constructor comment.
 */
public DisplayTableCellRenderer() 
{
	super();
	setOpaque( true );
}
/**
 * Insert the method's description here.
 * Creation date: (12/19/2000 5:33:23 PM)
 * @param table javax.swing.JTable
 */
private void doOncePerTableRefresh(javax.swing.JTable table) 
{
	boldFont = new java.awt.Font( table.getFont().getName(), java.awt.Font.BOLD | java.awt.Font.ITALIC, table.getFont().getSize() );
	plainFont = new java.awt.Font( table.getFont().getName(), java.awt.Font.PLAIN, table.getFont().getSize() );
	
}
/**
 * getTableCellRendererComponent method comment.
 */
public Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
{
	Display2WayDataAdapter model = (Display2WayDataAdapter)(((SortTableModelWrapper)table.getModel()).getRealDataModel());

	setBackground( Colors.getColor( model.getRowBackgroundColor( row ) ) );

	// do anything that only needs to be assigned once per repainting here
	if( row == 0 && column == 0 )
		doOncePerTableRefresh( table );


	if( table.getSelectedRow() == row )
	{				
		//Each border is a newly allocated object (uses the new operator)
		//  but, we only create at most the number of columns the table has
		if( column == 0 )
			setBorder( javax.swing.BorderFactory.createMatteBorder( 2, 2, 2, 0, borderColor) );
		else if( column == (table.getModel().getColumnCount()-1) )
			setBorder( javax.swing.BorderFactory.createMatteBorder( 2, 0, 2, 2, borderColor) );
		else
			setBorder( javax.swing.BorderFactory.createMatteBorder( 2, 0, 2, 0, borderColor) );

		setForeground( Colors.getColor( model.getRowForegroundColor( row ) ).brighter() );
		setFont( boldFont );
	}
	else
	{
		// javax.swing.BorderFactory.createEmptyBorder() returns a single instance
		//   of an empty border, so performance is not degrated by the new operator
		setBorder( javax.swing.BorderFactory.createEmptyBorder() );
		setForeground( Colors.getColor( model.getRowForegroundColor( row ) ) );
		setFont( plainFont );
	}

	if( value != null )
		setText( value.toString() );
	else
		setText( "" );


	if( value.toString().equalsIgnoreCase("") )
		((javax.swing.JComponent)this).setToolTipText("FILLER ROW");
	else
		((javax.swing.JComponent)this).setToolTipText( value.toString() );


	// we have to check the the actual ColumnIndexToModel mapping in order to find out what
	// column is the PointValue, because the user could have moved it.
	try
	{
		if( model.getColumnTypeName( table.convertColumnIndexToModel(column) ).equalsIgnoreCase(CustomDisplay.COLUMN_TYPE_POINTVALUE) )
			renderPointValueColumn();
		else
			this.setHorizontalAlignment( javax.swing.SwingConstants.LEFT );
	}
	catch( NullPointerException ne )
	{
		this.setHorizontalAlignment( javax.swing.SwingConstants.LEFT );
	}
	
		
	return this;
}
/**
 * Insert the method's description here.
 * Creation date: (12/19/2000 5:39:02 PM)
 */
private void renderPointValueColumn() 
{
	this.setHorizontalAlignment( javax.swing.SwingConstants.CENTER );
}
}
