package com.cannontech.cbc.gui;

/**
 * This type was created in VisualAge.
 */

public class CapControlTableCellRenderer extends javax.swing.JLabel implements javax.swing.table.TableCellRenderer 
{
	private java.awt.Color borderColor = new java.awt.Color(125,50,180);

	private java.awt.Font boldFont = null;
	private java.awt.Font plainFont = null;

/**
 * ScheduleCellRenderer constructor comment.
 */
public CapControlTableCellRenderer() {
	super();
	setOpaque(true);

}
/**
 * getTableCellRendererComponent - requres that table be a
 */
public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) 
{
	com.cannontech.tdc.alarms.gui.AlarmTableModel model = null;

	if( table.getModel() instanceof com.cannontech.common.gui.util.SortTableModelWrapper )
		model = (com.cannontech.tdc.alarms.gui.AlarmTableModel)
			((com.cannontech.common.gui.util.SortTableModelWrapper)table.getModel()).getRealDataModel();
	else
		model = (com.cannontech.tdc.alarms.gui.AlarmTableModel)table.getModel();

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

		setFont( boldFont );
	}
	else
	{
		// javax.swing.BorderFactory.createEmptyBorder() returns a single instance
		//   of an empty border, so performance is not degrated by the new operator
		setBorder( javax.swing.BorderFactory.createEmptyBorder() );
		setFont( plainFont );
	}

    setForeground( model.getCellForegroundColor(row, column) );

	//do the BG color here
	setBackground( model.getCellBackgroundColor(row, column) );

	if( model instanceof SubBusTableModel )
	{
		handleSubBusTableModel( (SubBusTableModel)model, row, column, table, isSelected );
	}
	else if( model instanceof FeederTableModel )
	{
		handleFeederTableModel( (FeederTableModel)model, row, column, table, isSelected );
	}
	else if( model instanceof CapBankTableModel )
	{
		handleCapBankTableModel( (CapBankTableModel)model, row, column, table, isSelected );
	}


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
	
	return this;
}
/**
 * Insert the method's description here.
 * Creation date: (8/23/00 1:51:05 PM)
 * @param model CapBankTableModel
 * @param row int
 * @param column int
 * @param table javax.swing.JTable
 */
private void handleCapBankTableModel(CapBankTableModel model, int row, int column, javax.swing.JTable table, boolean isSelected ) 
{
	// we have to check the actual ColumnIndexToModel mapping in order to find out which
	// columns are the BankSize and OpCount, because the user could have moved it.
	if( table.convertColumnIndexToModel(column) == CapBankTableModel.OP_COUNT_COLUMN  ||
		 table.convertColumnIndexToModel(column) == CapBankTableModel.BANK_SIZE_COLUMN )
	{
		this.setHorizontalAlignment( javax.swing.SwingConstants.CENTER );
	}
	else
		this.setHorizontalAlignment( javax.swing.SwingConstants.LEFT );
}

/**
 * Insert the method's description here.
 * Creation date: (8/23/00 1:51:05 PM)
 * @param model com.cannontech.cbc.gui.SubBusTableModel
 * @param row int
 * @param column int
 * @param table javax.swing.JTable
 */
private void handleFeederTableModel(FeederTableModel model, int row, int column, javax.swing.JTable table, boolean isSelected ) 
{
	// we have to check the actual ColumnIndexToModel mapping in order to find out which
	// columns are the BankSize and OpCount, because the user could have moved it.
	if( table.convertColumnIndexToModel(column) == FeederTableModel.VAR_LOAD_COLUMN||
		 table.convertColumnIndexToModel(column) == FeederTableModel.DAILY_OPERATIONS_COLUMN ||
       table.convertColumnIndexToModel(column) == FeederTableModel.POWER_FACTOR_COLUMN )
   {
		this.setHorizontalAlignment( javax.swing.SwingConstants.CENTER );
   }
	else
		this.setHorizontalAlignment( javax.swing.SwingConstants.LEFT );

}
/**
 * Insert the method's description here.
 * Creation date: (8/23/00 1:51:05 PM)
 * @param model com.cannontech.cbc.gui.SubBusTableModel
 * @param row int
 * @param column int
 * @param table javax.swing.JTable
 */
private void handleSubBusTableModel(SubBusTableModel model, int row, int column, javax.swing.JTable table, boolean isSelected ) 
{
	// we have to check the actual ColumnIndexToModel mapping in order to find out which
	// columns are the BankSize and OpCount, because the user could have moved it.
	if( table.convertColumnIndexToModel(column) == SubBusTableModel.VAR_LOAD_COLUMN||
       table.convertColumnIndexToModel(column) == SubBusTableModel.DAILY_OPERATIONS_COLUMN ||
		 table.convertColumnIndexToModel(column) == SubBusTableModel.POWER_FACTOR_COLUMN ||
       table.convertColumnIndexToModel(column) == SubBusTableModel.WATTS_COLUMN )
   {
		this.setHorizontalAlignment( javax.swing.SwingConstants.CENTER );
   }
	else
		this.setHorizontalAlignment( javax.swing.SwingConstants.LEFT );

}
}
