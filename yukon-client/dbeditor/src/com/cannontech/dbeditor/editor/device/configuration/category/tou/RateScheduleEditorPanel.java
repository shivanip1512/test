package com.cannontech.dbeditor.editor.device.configuration.category.tou;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.wizard.WizardPanel;
import com.cannontech.dbeditor.editor.device.configuration.category.CategoryEditorPanel;

/**
 * Default panel used to edit a rate schedule category
 */
public class RateScheduleEditorPanel extends CategoryEditorPanel {

    @Override
    protected DataInputPanel createItemPanel() {
        return new RateScheduleItemPanel(false);
    }

    @Override
    protected WizardPanel createWizardPanel() {
        return new RateScheduleWizardPanel();
    }
}
