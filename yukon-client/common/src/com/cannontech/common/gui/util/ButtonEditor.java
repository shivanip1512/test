package com.cannontech.common.gui.util;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;

/**
 * @version 1.0 11/09/98
 */
public class ButtonEditor extends DefaultCellEditor {
	protected JButton button;
	protected String    label;
	protected boolean   isPushed;

	public ButtonEditor(JCheckBox checkBox) {
		super(checkBox);
		button = new JButton();
		button.setOpaque(true);
		button.addActionListener(new ActionListener() {
									 public void actionPerformed(ActionEvent e) {
										 fireEditingStopped();
									 }
								 });
	}
	protected void fireEditingStopped() {
		super.fireEditingStopped();
	}
	public Object getCellEditorValue() {
		if (isPushed) {
			// 
			// 
			JOptionPane.showMessageDialog(button ,label + ": Ouch!");
			// com.cannontech.clientutils.CTILogger.info(label + ": Ouch!");
		}
		isPushed = false;
		return new String( label ) ;
	}
	public Component getTableCellEditorComponent(JTable table, Object value,
												 boolean isSelected, int row, int column) {
		if (isSelected) {
			button.setForeground(table.getSelectionForeground());
			button.setBackground(table.getSelectionBackground());
		} else {
			button.setForeground(table.getForeground());
			button.setBackground(table.getBackground());
		}
		label = (value ==null) ? "" : value.toString();
		button.setText( label );
		isPushed = true;
		return button;
	}
	public boolean stopCellEditing() {
		isPushed = false;
		return super.stopCellEditing();
	}
}
