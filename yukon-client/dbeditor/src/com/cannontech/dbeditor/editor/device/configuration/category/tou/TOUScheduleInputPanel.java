package com.cannontech.dbeditor.editor.device.configuration.category.tou;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.dbeditor.editor.device.configuration.category.CategoryInputPanel;

/**
 * Input panel for tou schedule category.
 */
public class TOUScheduleInputPanel extends CategoryInputPanel {

    protected DataInputPanel createItemPanel() {
        return new TOUScheduleItemPanel(true);
    }

}
