package com.cannontech.common.gui.table;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;
import java.util.Vector;

/**
 * @author rneuharth
 * 
 * Allows all JComboBoxes to have something diferent selected
 *
 */
public class MultiJComboCellEditor extends DefaultCellEditor 
{
    private Vector comboModels;
    
    public MultiJComboCellEditor( DefaultComboBoxModel[] defModels )
    {
        super( new JComboBox() );
        
        //DefaultComboBoxModel
        comboModels = new Vector(defModels.length);
        for(int r = 0; r < defModels.length; r++)
        {
        	comboModels.addElement(defModels[r]);
        }
    }

	public MultiJComboCellEditor( Vector defModels )
	{
		super( new JComboBox() );
		
		//DefaultComboBoxModel
		comboModels = defModels;
	}

    public Component getTableCellEditorComponent(JTable table,
            Object value, boolean isSelected, int row, int column)
    {
        //just checking which row here and set the model ??? you like
        ((JComboBox)editorComponent).setModel( (DefaultComboBoxModel)comboModels.elementAt(row) );

        delegate.setValue(value);
        return editorComponent;
    }
    
    public void addModel(DefaultComboBoxModel model)
    {
    	comboModels.addElement(model);
    }
    
    public void removeModel(int row)
	{
		comboModels.removeElementAt(row);
	}

}
