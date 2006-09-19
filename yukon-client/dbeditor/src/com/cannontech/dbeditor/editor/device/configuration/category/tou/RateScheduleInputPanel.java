package com.cannontech.dbeditor.editor.device.configuration.category.tou;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.dbeditor.editor.device.configuration.category.CategoryInputPanel;

/**
 * Input panel for rate schedule category.
 */
public class RateScheduleInputPanel extends CategoryInputPanel {

    protected DataInputPanel createItemPanel() {
        return new RateScheduleItemPanel(true);
    }

}
