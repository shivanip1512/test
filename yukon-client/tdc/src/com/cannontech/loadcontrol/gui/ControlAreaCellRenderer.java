package com.cannontech.loadcontrol.gui;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.loadcontrol.datamodels.ControlAreaTriggerTableModel;

public class ControlAreaCellRenderer extends javax.swing.JLabel implements javax.swing.table.TableCellRenderer 
{
	private java.awt.Font boldFont = null;
	private java.awt.Font plainFont = null;
/**
 * ScheduleCellRenderer constructor comment.
 */
public ControlAreaCellRenderer() {
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
		boldFont = new java.awt.Font( "Dialog", java.awt.Font.BOLD | java.awt.Font.ITALIC, 10 );
		plainFont = new java.awt.Font( "Dialog", java.awt.Font.PLAIN, 10 );
	}

	if( model instanceof ControlAreaTriggerTableModel )
	{
		if( isSelected )
		{
			setForeground( ((ControlAreaTriggerTableModel)model).getCellForegroundColor( row, column ).brighter());
			setFont( boldFont );
		}
		else
		{
			setForeground( ((ControlAreaTriggerTableModel)model).getCellForegroundColor( row, column ));
			setFont( plainFont );
		}	

	
		if( value != null )
		{
			com.cannontech.loadcontrol.datamodels.ControlAreaRowData d =
			 		((ControlAreaTriggerTableModel) model).getRowAt(row);
			 		
			setText( value.toString() );

			((javax.swing.JComponent)this).setToolTipText( 
						d.getLitePoint().getPointName() + " : " +
						new com.cannontech.clientutils.commonutils.ModifiedDate(d.getTrigger().getLastPointValueTimeStamp().getTime()) );					
		}
		else
		{
			setText( "" );
			((javax.swing.JComponent)this).setToolTipText( "" );
		}
	}
	else
		throw new RuntimeException("** Only table models of type ControlAreaTableModel can have the following class as its renderrer :" +
			 		this.getClass().getName() );

	
	return this;
}
}
