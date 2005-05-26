package com.cannontech.common.gui.table;

import com.cannontech.common.gui.table.ICTITableRenderer;

/**
 * @author rneuharth
 * 
 * Generic table renderer that changes row colors
 *
 */
public class CTITableRenderer extends javax.swing.JLabel implements javax.swing.table.TableCellRenderer
{
	private java.awt.Color borderColor = new java.awt.Color(125,50,180);

	private java.awt.Font boldFont = null;
	private java.awt.Font plainFont = null;

	/**
	 * CTITableRenderer constructor comment.
	 */
	public CTITableRenderer() 
	{
		super();
	}


	/**
	 * getTableCellRendererComponent - requres that table be a
	 */
	public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) 
	{
		ICTITableRenderer model = null;

		if( table.getModel() instanceof ICTITableRenderer )
			model = (ICTITableRenderer)table.getModel();
		else
			throw new IllegalStateException(
						"Unknown table model in class: " + this.getClass().getName() );

		// do anything that only needs to be assigned once per repainting here
		if( row == 0 && column == 0 )
		{
			boldFont = new java.awt.Font( table.getFont().getName(), java.awt.Font.BOLD | java.awt.Font.ITALIC, table.getFont().getSize() );
			plainFont = new java.awt.Font( table.getFont().getName(), java.awt.Font.PLAIN, table.getFont().getSize() );
		}
		
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

			setForeground( model.getCellForegroundColor( row, column ).brighter());
			setFont( boldFont );
		}
		else
		{
			// javax.swing.BorderFactory.createEmptyBorder() returns a single instance
			//   of an empty border, so performance is not degrated by the new operator
			setBorder( javax.swing.BorderFactory.createEmptyBorder() );
			setForeground( model.getCellForegroundColor( row, column ));		
			setFont( plainFont );
		}
	
	
		//do the BG color here
		//setBackground( model.getCellBackgroundColor(row, column) );
		if( value != null )
		{
			setText( value.toString() );
		}
		else
		{
			setText( "" );
		}


		return this;
	}


}
