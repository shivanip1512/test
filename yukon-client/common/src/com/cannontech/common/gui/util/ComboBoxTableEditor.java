/*
 * Created on Apr 27, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.common.gui.util;

import javax.swing.AbstractCellEditor;
import javax.swing.table.TableCellEditor;
import javax.swing.JComboBox;
import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JTable;
import java.util.Vector;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ComboBoxTableEditor extends AbstractCellEditor implements TableCellEditor {
	 // This is the component that will handle the editing of the cell value
	 JComponent component = new JComboBox();
    
	 public ComboBoxTableEditor(JComboBox newBox)
	 {
	 	super();
	 	component = newBox;
	 }
	 
	 // This method is called when a cell value is edited by the user.
	 public Component getTableCellEditorComponent(JTable table, Object value,
			 boolean isSelected, int rowIndex, int vColIndex) {
		 // 'value' is value contained in the cell located at (rowIndex, vColIndex)
    
		 if (isSelected) {
			 // cell (and perhaps other cells) are selected
		 }
    
		 // Configure the component with the specified value
		 if(value instanceof Vector)
		 {
			((JComboBox)component).removeAllItems();
		 	for(int j = 0; j < ((Vector)value).size(); j++)
		 	{
		 		((JComboBox)component).addItem(((Vector)value).elementAt(j));
		 	}
		 }
		 else
		 {
		 	((JComboBox)component).setSelectedItem(value.toString());
		 }
    
		 // Return the configured component
		 return component;
	 }
    
	 // This method is called when editing is completed.
	 // It must return the new value to be stored in the cell.
	 public Object getCellEditorValue() {
		 return ((JComboBox)component).getSelectedItem();
	 }
 }
