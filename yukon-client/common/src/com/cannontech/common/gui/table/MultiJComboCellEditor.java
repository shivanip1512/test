package com.cannontech.common.gui.table;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;

/**
 * @author rneuharth
 * 
 * Allows all JComboBoxes to have something diferent selected
 *
 */
public class MultiJComboCellEditor extends DefaultCellEditor 
{
    DefaultComboBoxModel[] comboModels;
    
    public MultiJComboCellEditor( DefaultComboBoxModel[] defModels )
    {
        super( new JComboBox() );
        
        //DefaultComboBoxModel
        comboModels = defModels;
    }


    public Component getTableCellEditorComponent(JTable table,
            Object value, boolean isSelected, int row, int column)
    {
        //just checking which row here and set the model ??? you like
        ((JComboBox)editorComponent).setModel( comboModels[row] );

        delegate.setValue(value);
        return editorComponent;
    }

}
