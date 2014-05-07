package com.cannontech.common.gui.table;

import java.awt.Component;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;

/**
 * Allows all JComboBoxes to have something different selected.
 */
public class MultiJComboCellEditor<T> extends DefaultCellEditor {
    private Vector<DefaultComboBoxModel<T>> comboModels;

    public MultiJComboCellEditor(DefaultComboBoxModel<T>[] defModels) {
        super(new JComboBox<T>());

        // DefaultComboBoxModel
        comboModels = new Vector<>(defModels.length);
        for (int r = 0; r < defModels.length; r++) {
            comboModels.addElement(defModels[r]);
        }
    }

    public MultiJComboCellEditor(Vector<DefaultComboBoxModel<T>> defModels) {
        super(new JComboBox<T>());

        // DefaultComboBoxModel
        comboModels = defModels;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        @SuppressWarnings("unchecked")
        JComboBox<T> editorComponentComboBox = (JComboBox<T>) editorComponent;
        // just checking which row here and set the model ??? you like
        editorComponentComboBox.setModel(comboModels.elementAt(row));

        delegate.setValue(value);
        return editorComponent;
    }

    public void addModel(DefaultComboBoxModel<T> model) {
        comboModels.addElement(model);
    }

    public void removeModel(int row) {
        comboModels.removeElementAt(row);
    }
}
