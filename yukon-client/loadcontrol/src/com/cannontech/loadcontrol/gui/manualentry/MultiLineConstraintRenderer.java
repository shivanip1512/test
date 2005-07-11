package com.cannontech.loadcontrol.gui.manualentry;

import java.awt.Color;
import java.util.List;

import javax.swing.JTextArea;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.loadcontrol.datamodels.ISelectableLMTableModel;

/**
 * Renders multi rows inside a JTable
 * 
 * @author ryan
 *
 */
public class MultiLineConstraintRenderer extends javax.swing.JPanel implements javax.swing.table.TableCellRenderer 
{
	private java.awt.Font boldFont = null;
	private java.awt.Font plainFont = null;
	private javax.swing.JLabel ivjJLabelText = null;
	
/**
 * MultiLineConstraintRenderer constructor comment.
 */
public MultiLineConstraintRenderer() {
	super();
	initialize();
}
/**
 * Return the JLabelText property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelText() {
	if (ivjJLabelText == null) {
		try {
			ivjJLabelText = new javax.swing.JLabel();
			ivjJLabelText.setName("JLabelText");
			ivjJLabelText.setText("TEXT");
			ivjJLabelText.setBounds(0, 1, 67, 14);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelText;
}
/**
 */
public java.awt.Component getTableCellRendererComponent(final javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) 
{
	javax.swing.table.TableModel model = table.getModel();
	removeAll();
	//invalidate();

	// do anything that only needs to be assigned once per repainting here
	if( row == 0 )
	{
		boldFont = new java.awt.Font( table.getFont().getName(), java.awt.Font.BOLD | java.awt.Font.ITALIC, table.getFont().getSize() );
		plainFont = new java.awt.Font( table.getFont().getName(), java.awt.Font.PLAIN, table.getFont().getSize() );
	}
	
	// javax.swing.BorderFactory.createEmptyBorder() returns a single instance
	//   of an empty border, so performance is not degrated by the new operator
	setBorder( javax.swing.BorderFactory.createEmptyBorder() );


	Color foreColor = Color.BLACK; //default
	if( model instanceof ISelectableLMTableModel )
	{
		foreColor = handleColor( (ISelectableLMTableModel)model, row, column, table, isSelected );
	}

	getJLabelText().setForeground( foreColor );
	
	if( value instanceof List )
	{
		processList( (List)value, table, foreColor, row, column );
	}
	else
	{
		add(getJLabelText());
		getJLabelText().setToolTipText( value == null ? "" : value.toString() );
	}


	if( value != null )
	{
		getJLabelText().setText( value.toString() );
		((javax.swing.JComponent)this).setToolTipText( value.toString() );
	}
	else
	{
		getJLabelText().setText( "" );
		((javax.swing.JComponent)this).setToolTipText( "" );
	}

	return this;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	CTILogger.error("--------- UNCAUGHT EXCEPTION ---------", exception);
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("MultiLineGearRenderer");
		setLayout(null);
		setSize(163, 32);
		add(getJLabelText(), getJLabelText().getName());
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	setLayout( new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS) );
	
	// user code end
}


/**
 * Insert the method's description here.
 * Creation date: (7/18/2001 5:03:03 PM)
 */
private void processList( List values, javax.swing.JTable table, Color fgColor, int row, int col ) 
{
	int h = table.getRowHeight(row);
    for( int i = 0; i < values.size(); i++ )
	{
		//reset our height, we may need a resizing
		if( i == 0 ) h = 0;

		String txt = "(" + (i+1) + "):  " + values.get(i).toString();
		JTextArea newLabel = new JTextArea( txt );
		newLabel.setForeground( fgColor );
		newLabel.setBackground( Color.BLACK );
		newLabel.setLineWrap( true );
		newLabel.setWrapStyleWord( true );
		newLabel.setToolTipText( txt );

		//determine the sizes needed to display all text
		newLabel.setSize( 
			table.getColumnModel().getColumn(col).getWidth(),
			table.getRowHeight(row) );		

		h += (int)newLabel.getPreferredSize().getHeight();

		add( newLabel );
	}

	if( h != table.getRowHeight(row) )
		table.setRowHeight(row, h);

}

/**
 * Insert the method's description here.
 * Creation date: (8/23/00 1:51:05 PM)
 * @param model CapBankTableModel
 * @param row int
 * @param column int
 * @param table javax.swing.JTable
 */
private Color handleColor(ISelectableLMTableModel model, int row, int column, javax.swing.JTable table, boolean isSelected ) 
{
	setBackground( model.getCellBackgroundColor( row, column ));

	setForeground( model.getCellForegroundColor( row, column ));		
	setFont( plainFont );
	return model.getCellForegroundColor( row, column );
}
}
