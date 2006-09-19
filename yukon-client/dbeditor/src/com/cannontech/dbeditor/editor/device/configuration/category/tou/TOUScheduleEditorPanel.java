package com.cannontech.dbeditor.editor.device.configuration.category.tou;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.wizard.WizardPanel;
import com.cannontech.dbeditor.editor.device.configuration.category.CategoryEditorPanel;

/**
 * Default panel used to edit a tou schedule category
 */
public class TOUScheduleEditorPanel extends CategoryEditorPanel {

    @Override
    protected DataInputPanel createItemPanel() {
        return new TOUScheduleItemPanel(false);
    }

    @Override
    protected WizardPanel createWizardPanel() {
        return new TOUScheduleWizardPanel();
    }
}
