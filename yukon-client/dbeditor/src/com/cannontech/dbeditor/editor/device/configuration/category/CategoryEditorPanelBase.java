package com.cannontech.dbeditor.editor.device.configuration.category;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.wizard.WizardPanel;

/**
 * Default panel used to edit a category
 */
public class CategoryEditorPanelBase extends CategoryEditorPanel {

    /**
     * Method to create a DataInputPanel for the object's items.
     * @param category - Object to create an item panel for
     * @return - A DataInputPanel for the object's items
     */
    protected DataInputPanel createItemPanel() {
        return new ItemPanel(false);
    }

    protected WizardPanel createWizardPanel() {
        return new CategoryWizardPanelBase();
    }

}
