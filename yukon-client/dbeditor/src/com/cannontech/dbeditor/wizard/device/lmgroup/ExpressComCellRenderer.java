package com.cannontech.dbeditor.wizard.device.lmgroup;

/**
 * This type was created in VisualAge.
 */

public class ExpressComCellRenderer implements javax.swing.table.TableCellRenderer 
{
	public static final javax.swing.border.Border yellowBorder = javax.swing.BorderFactory.createMatteBorder(
		0, 0, 0, 1, java.awt.Color.yellow );
/**
 * ScheduleCellRenderer constructor comment.
 */
public ExpressComCellRenderer() {
	super();
}
/**
 * getTableCellRendererComponent - requres that table be a ScheduleTableModel
 */
public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) 
{

	if( value instanceof javax.swing.JComponent )
	{
		if( column != 3 )
			((javax.swing.JComponent)value).setBorder( yellowBorder );

		return (javax.swing.JComponent)value;

	}
	else
		return null;


}
}
