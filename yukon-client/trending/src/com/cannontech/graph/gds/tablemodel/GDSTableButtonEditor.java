package com.cannontech.graph.gds.tablemodel;

import javax.swing.JCheckBox;

import com.cannontech.common.gui.util.ButtonEditor;

/**
 * @author snebben
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class GDSTableButtonEditor extends ButtonEditor
{
//	protected javax.swing.JButton button;
//	private String    label;
//	private boolean   isPushed;


	/**
	 * Constructor for GDSTableButtonEditor.
	 * @param checkBox
	 */
	public GDSTableButtonEditor(JCheckBox checkBox)
	{
		super(checkBox);
	}

//	protected void fireEditingStopped() 
//	{
//		super.fireEditingStopped();
//	}
//
	public Object getCellEditorValue()
	{
		if (isPushed)
		{
			// 
			// 
//	javax.swing.JFrame frame = new javax.swing.JFrame();
//	com.cannontech.graph.AboutTrending aboutDialog = new com.cannontech.graph.AboutTrending(frame, "About Trending", true );
//	aboutDialog.setLocationRelativeTo(frame );
//	aboutDialog.setValue(null);
//	aboutDialog.show();
//			
			javax.swing.JOptionPane.showMessageDialog(button ,label + ": Ouch!");
			// com.cannontech.clientutils.CTILogger.info(label + ": Ouch!");
		}
		isPushed = false;
		return new String(label) ;
	}
	
	public java.awt.Component getTableCellEditorComponent(javax.swing.JTable table, Object value, boolean isSelected, int row, int column)
	{
		if (isSelected)
		{
			button.setForeground(table.getSelectionForeground());
			button.setBackground(table.getSelectionBackground());
		}
		else
		{
			button.setForeground(table.getForeground());
			button.setBackground(table.getBackground());
		}
		label = (value ==null) ? "" : value.toString();
		button.setText( label);
		isPushed = true;
		return button;
	}
	
//	public boolean stopCellEditing()
//	{
//		isPushed = false;
//		return super.stopCellEditing();
//	}
}
