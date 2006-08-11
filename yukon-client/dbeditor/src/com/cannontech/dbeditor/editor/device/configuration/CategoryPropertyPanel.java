package com.cannontech.dbeditor.editor.device.configuration;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.editor.PropertyPanel;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.database.data.device.configuration.Category;

/**
 * Main panel for editing device configuration categories
 */
public class CategoryPropertyPanel extends PropertyPanel {

    private List<DataInputPanel> inputPanels = null;
    private List<String> inputPanelTabNames = null;

    public CategoryPropertyPanel() {
        this.initialize();
    }

    @Override
    public void setValue(Object val) {

        Category category = (Category) val;

        setOriginalObjectToEdit(category);

        this.inputPanels.get(0).setValue(category);

        String[] tabNames = getTabNames();
        DataInputPanel[] inputPanels = getInputPanels();

        // only add the tabs if we have 0
        if (getTabbedPane().getTabRunCount() == 0) {
            for (int i = 0; i < tabNames.length; i++) {

                getTabbedPane().addTab(tabNames[i], inputPanels[i]);

                inputPanels[i].addDataInputPanelListener(this);
            }
        }

        getTabbedPane().setSelectedIndex(0);
        this.removeAll();
        this.setLayout(new java.awt.BorderLayout());
        add(getTabbedPane(), BorderLayout.CENTER);
        add(getPropertyButtonPanel(), BorderLayout.SOUTH);

        setChanged(false);

        return;
    }

    @Override
    public String toString() {
        return "Device Configuration Category Editor";
    }

    @Override
    protected DataInputPanel[] getInputPanels() {
        return inputPanels.toArray(new DataInputPanel[] {});
    }

    @Override
    protected String[] getTabNames() {
        return inputPanelTabNames.toArray(new String[] {});
    }

    /**
     * Helper method to initialize this panel.
     */
    private void initialize() {

        this.inputPanels = inputPanels = new ArrayList<DataInputPanel>();
        this.inputPanels.add(new CategoryInputPanel());
        this.inputPanelTabNames = new ArrayList<String>();
        this.inputPanelTabNames.add("General");

    }

}
