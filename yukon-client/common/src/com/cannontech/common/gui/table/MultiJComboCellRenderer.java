package com.cannontech.common.gui.table;

import javax.swing.DefaultComboBoxModel;
import java.util.Vector;
import com.cannontech.common.gui.util.ComboBoxTableRenderer;

/**
 * @author rneuharth
 *
 * Allows JComboBoxes to have diferent selectable data
 *  
 */
public class MultiJComboCellRenderer extends ComboBoxTableRenderer
{
    private Vector comboModels;
    
    public MultiJComboCellRenderer( DefaultComboBoxModel[] models )
    {
        super();
		comboModels = new Vector(models.length);
	  	for(int r = 0; r < models.length; r++)
	   	{
			comboModels.addElement(models[r]);
	   	}
    }
    
	public MultiJComboCellRenderer( Vector defModels )
	{
		//DefaultComboBoxModel
		comboModels = defModels;
	}

    public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) 
    {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        Vector crap = this.comboModels;
        setModel( (DefaultComboBoxModel)comboModels.elementAt(row) );
        setSelectedItem( ((DefaultComboBoxModel)comboModels.elementAt(row)).getSelectedItem() );
        return this;
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