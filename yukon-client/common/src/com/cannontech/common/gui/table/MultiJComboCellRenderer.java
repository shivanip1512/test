package com.cannontech.common.gui.table;

import javax.swing.DefaultComboBoxModel;

import com.cannontech.common.gui.util.ComboBoxTableRenderer;

/**
 * @author rneuharth
 *
 * Allows JComboBoxes to have diferent selectable data
 *  
 */
public class MultiJComboCellRenderer extends ComboBoxTableRenderer
{
    private DefaultComboBoxModel[] comboModels;

    
    public MultiJComboCellRenderer( DefaultComboBoxModel[] models )
    {
        super();
        comboModels = models;
    }
    

    public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) 
    {

        super.getTableCellRendererComponent(table, value, isSelected,
                hasFocus, row, column);
        
        setModel( comboModels[row] );
        setSelectedItem( comboModels[row].getSelectedItem() );
        return this;
    }

}