package com.cannontech.dbeditor.editor.device.configuration.category.tou;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.dbeditor.editor.device.configuration.category.CategoryWizardPanel;

/**
 * Wizard panel for rate schedule category creation
 */
public class RateScheduleWizardPanel extends CategoryWizardPanel {

    @Override
    protected DataInputPanel getNextInputPanel(DataInputPanel currentInputPanel) {
        return new RateScheduleInputPanel();
    }
}
