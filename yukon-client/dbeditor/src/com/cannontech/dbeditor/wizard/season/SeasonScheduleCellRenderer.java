package com.cannontech.dbeditor.wizard.season;

/**
 * This type was created in VisualAge.
 */
public class SeasonScheduleCellRenderer extends javax.swing.JLabel implements javax.swing.table.TableCellRenderer 
{
	private int rowHeight = 32; //used to remember the last RowHeight

	private java.awt.Color borderColor = new java.awt.Color(125,50,180);

	private java.awt.Font boldFont = null;
	private java.awt.Font plainFont = null;
/**
 * ScheduleCellRenderer constructor comment.
 */
public SeasonScheduleCellRenderer() {
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

		setBackground( java.awt.Color.blue );
		setForeground( java.awt.Color.white );
	}
	else
	{
		// javax.swing.BorderFactory.createEmptyBorder() returns a single instance
		//   of an empty border, so performance is not degrated by the new operator
		setBorder( javax.swing.BorderFactory.createEmptyBorder() );
		setForeground( java.awt.Color.black );
		setBackground( java.awt.Color.white );
	}


	//make sure our Jtables row height is set
	if( table.getRowHeight() != rowHeight )
		table.setRowHeight( rowHeight );

	setText( value.toString() );

	return this;
}
}
