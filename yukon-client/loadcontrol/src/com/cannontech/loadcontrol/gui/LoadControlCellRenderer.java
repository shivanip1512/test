package com.cannontech.loadcontrol.gui;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.loadcontrol.datamodels.ISelectableLMTableModel;

public class LoadControlCellRenderer extends javax.swing.JLabel implements javax.swing.table.TableCellRenderer 
{
	private int rowHeight = 32; //used to remember the last RowHeight

	private java.awt.Color borderColor = new java.awt.Color(125,50,180);

	private java.awt.Font boldFont = null;
	private java.awt.Font plainFont = null;


	/**
	 * LoadControlCellRenderer constructor comment.
	 */
	public LoadControlCellRenderer() {
		super();
		setOpaque(true);
	}
	
	/**
	 * getTableCellRendererComponent - requres that table be a ScheduleTableModel
	 */
	public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) 
	{
		javax.swing.table.TableModel model = table.getModel();
	
		// do anything that only needs to be assigned once per repainting here
		if( row == 0 && column == 0 )
		{
			rowHeight = table.getFont().getSize() + 2;
			boldFont = new java.awt.Font( table.getFont().getName(), java.awt.Font.BOLD | java.awt.Font.ITALIC, table.getFont().getSize() );
			plainFont = new java.awt.Font( table.getFont().getName(), java.awt.Font.PLAIN, table.getFont().getSize() );
		}
	
		if( isSelected )
		{				
			//Each border is a newly allocated object (uses the new operator)
			//  but, we only create at most the number of columns the table has
			if( column == 0 )
				setBorder( javax.swing.BorderFactory.createMatteBorder( 2, 2, 2, 0, borderColor) );
			else if( column == (table.getModel().getColumnCount()-1) )
				setBorder( javax.swing.BorderFactory.createMatteBorder( 2, 0, 2, 2, borderColor) );
			else
				setBorder( javax.swing.BorderFactory.createMatteBorder( 2, 0, 2, 0, borderColor) );
	
		}
		else
		{
			// javax.swing.BorderFactory.createEmptyBorder() returns a single instance
			//   of an empty border, so performance is not degrated by the new operator
			setBorder( javax.swing.BorderFactory.createEmptyBorder() );
		}
	
	
		//make sure our Jtables row height is set
		if( table.getRowHeight() != rowHeight )
			table.setRowHeight( rowHeight );
	
		//if( model instanceof GroupTableModel )
			//handleGroupTableModel( (GroupTableModel)model, row, column, table, isSelected );
		//else if( model instanceof ControlAreaTableModel )
			//handleControlAreaTableModel( (ControlAreaTableModel)model, row, column, table, isSelected );
		if( model instanceof ISelectableLMTableModel )
			handleColor( (ISelectableLMTableModel)model, row, column, table, isSelected );
	
		
		if( value != null )
		{
			setText( value.toString() );
			((javax.swing.JComponent)this).setToolTipText( value.toString() );
		}
		else
		{
			setText( "" );
			((javax.swing.JComponent)this).setToolTipText( "" );
		}
	
		setCellAlignment( value );
		return this;
	}


	private void setCellAlignment( Object value )
	{
		
		if( value instanceof Double
			 || value instanceof Integer )
	   {
			setHorizontalAlignment( javax.swing.SwingConstants.CENTER );
	   }
		else
		{
			setHorizontalAlignment( javax.swing.SwingConstants.LEFT );
		}
					
	}
		
	
	/**
	 * Insert the method's description here.
	 * Creation date: (8/23/00 1:51:05 PM)
	 * @param model CapBankTableModel
	 * @param row int
	 * @param column int
	 * @param table javax.swing.JTable
	 */
	private void handleColor(ISelectableLMTableModel model, int row, int column, javax.swing.JTable table, boolean isSelected ) 
	{
		setBackground( model.getCellBackgroundColor( row, column ));
		
		if( isSelected )
		{
			setForeground( model.getSelectedRowColor(row, column) );
			setFont( boldFont );
		}
		else
		{
			setForeground( model.getCellForegroundColor( row, column ));		
			setFont( plainFont );
		}	
	}
}
