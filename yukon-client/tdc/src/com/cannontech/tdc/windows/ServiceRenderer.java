package com.cannontech.tdc.windows;

import com.cannontech.tdc.alarms.gui.AlarmTableModel;
import com.cannontech.common.gui.util.Colors;

/**
 * Insert the type's description here.
 * Creation date: (10/9/2002 10:24:18 PM)
 * @author: 
 */
public class ServiceRenderer extends javax.swing.JLabel implements javax.swing.table.TableCellRenderer 
{
	private java.awt.Color borderColor = new java.awt.Color(125,50,180);
	private java.awt.Font boldFont = null;
	private java.awt.Font plainFont = null;

/**
 * ServiceRenderer constructor comment.
 */
public ServiceRenderer() 
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
	 *  This method is sent to the renderer by the drawing table to
	 *  configure the renderer appropriately before drawing.  Return
	 *  the Component used for drawing.
	 *
	 * @param	table		the JTable that is asking the renderer to draw.
	 *				This parameter can be null.
	 * @param	value		the value of the cell to be rendered.  It is
	 *				up to the specific renderer to interpret
	 *				and draw the value.  eg. if value is the
	 *				String "true", it could be rendered as a
	 *				string or it could be rendered as a check
	 *				box that is checked.  null is a valid value.
	 * @param	isSelected	true is the cell is to be renderer with
	 *				selection highlighting
	 * @param	row	        the row index of the cell being drawn.  When
	 *				drawing the header the rowIndex is -1.
	 * @param	column	        the column index of the cell being drawn
	 */
public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) 
{
	AlarmTableModel model = (AlarmTableModel)
		((com.cannontech.common.gui.util.SortTableModelWrapper)table.getModel()).getRealDataModel();

	setBackground( model.getCellBackgroundColor( row, column ) );

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

		setForeground( model.getCellForegroundColor( row, column ).brighter() );
		setFont( boldFont );
	}
	else
	{
		// javax.swing.BorderFactory.createEmptyBorder() returns a single instance
		//   of an empty border, so performance is not degrated by the new operator
		setBorder( javax.swing.BorderFactory.createEmptyBorder() );
		setForeground( 
				model.getCellForegroundColor( row, column ) );

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


	return this;
}
}
