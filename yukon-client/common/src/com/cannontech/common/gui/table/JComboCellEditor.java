package com.cannontech.common.gui.table;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.Serializable;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.table.TableCellEditor;

/**
 * @author rneuharth
 *
 */
public class JComboCellEditor extends AbstractCellEditor implements ActionListener, ItemListener, TableCellEditor, Serializable
{
    private JComboBox comboBox;
    private Object lastVal = null;
    
    public JComboCellEditor(JComboBox comboBox)
    {
        this.comboBox = comboBox;
        this.comboBox.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
        // hitting enter in the combo box should stop cellediting (see below)
        this.comboBox.addActionListener(this);
        this.comboBox.addItemListener(this);
    }
    
    private void setValue(Object value) {
        comboBox.setSelectedItem(value);
    }
    
    // Implementing ActionListener
    public void actionPerformed(java.awt.event.ActionEvent e) {
        // Selecting an item results in an actioncommand "comboBoxChanged".
        // We should ignore these ones.

        // Hitting enter results in an actioncommand "comboBoxEdited"
        if(e.getActionCommand().equals("comboBoxEdited")) {
            stopCellEditing();
        }
    }
    
    public void itemStateChanged( ItemEvent e )
    {
        if( e.getStateChange() == ItemEvent.SELECTED )
        {
            if( !e.getItem().equals(lastVal) )
                stopCellEditing();

            lastVal = e.getItem();
        }

    }
    
    // Implementing CellEditor
    public Object getCellEditorValue() {
        return comboBox.getSelectedItem();
    }
    
    public boolean stopCellEditing() {
        if (comboBox.isEditable()) {
            // Notify the combo box that editing has stopped (e.g. User pressed F2)
            comboBox.actionPerformed(new ActionEvent(this, 0, ""));
        }
        fireEditingStopped();
        return true;
    }
    
    // Implementing TableCellEditor
    public java.awt.Component getTableCellEditorComponent(javax.swing.JTable table, Object value, boolean isSelected, int row, int column) {
        setValue(value);
        return comboBox;
    }

}
