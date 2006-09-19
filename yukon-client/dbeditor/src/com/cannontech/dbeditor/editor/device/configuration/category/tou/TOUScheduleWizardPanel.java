package com.cannontech.dbeditor.editor.device.configuration.category.tou;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.dbeditor.editor.device.configuration.category.CategoryWizardPanel;

/**
 * Wizard panel for tou schedule category creation
 */
public class TOUScheduleWizardPanel extends CategoryWizardPanel {

    @Override
    protected DataInputPanel getNextInputPanel(DataInputPanel currentInputPanel) {
        return new TOUScheduleInputPanel();
    }
}
