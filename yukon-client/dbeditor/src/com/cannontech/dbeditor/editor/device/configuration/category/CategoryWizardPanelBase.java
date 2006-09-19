package com.cannontech.dbeditor.editor.device.configuration.category;

import com.cannontech.common.gui.util.DataInputPanel;

/**
 * Default wizard panel for category creation
 */
public class CategoryWizardPanelBase extends CategoryWizardPanel {

    public CategoryWizardPanelBase() {
        super();
    }

    @Override
    protected DataInputPanel getNextInputPanel(DataInputPanel currentInputPanel) {
        return new CategoryInputPanelBase();
    }

}
