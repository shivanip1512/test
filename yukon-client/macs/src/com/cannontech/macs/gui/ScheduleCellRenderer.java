package com.cannontech.macs.gui;

import com.cannontech.common.gui.util.SortTableModelWrapper;

/**
 * This class will renderer the MACs JTable
 */
public class ScheduleCellRenderer extends javax.swing.JLabel implements javax.swing.table.TableCellRenderer 
{
	private java.awt.Color borderColor = new java.awt.Color(125,50,180);

	private java.awt.Font boldFont = null;
	private java.awt.Font plainFont = null;
	
/**
 * ScheduleCellRenderer constructor comment.
 */
public ScheduleCellRenderer() {
	super();
	setOpaque(true);

}
/**
 * getTableCellRendererComponent - requres that table be a ScheduleTableModel
 */
public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) 
{
	javax.swing.table.TableModel model = table.getModel();

	if( model instanceof SortTableModelWrapper )
		model = ((SortTableModelWrapper)model).getRealDataModel();


	
	if( model instanceof ScheduleTableModel )
	{
		ScheduleTableModel stm = (ScheduleTableModel) model;

		setFont( stm.getModelFont() );
		

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

			setForeground( stm.getCellForegroundColor( row, column ).brighter() );
			setBackground( stm.getCellBackgroundColor( row, column ));
			setFont( boldFont );
		}
		else
		{
			// javax.swing.BorderFactory.createEmptyBorder() returns a single instance
			//   of an empty border, so performance is not degrated by the new operator
			setBorder( javax.swing.BorderFactory.createEmptyBorder() );
			setForeground( stm.getCellForegroundColor( row, column ).brighter() );
			setBackground( stm.getCellBackgroundColor( row, column ));
			setFont( plainFont );
		}
			
		
	}

	if( value != null )
	{
		setText( value.toString() );
		((javax.swing.JComponent)this).setToolTipText( value.toString() );
	}
	else
	{
		setText("");
		((javax.swing.JComponent)this).setToolTipText("");
	}
	
	return this;
}
}
