package com.cannontech.dbeditor.editor.device.configuration.category;

import com.cannontech.common.gui.util.DataInputPanel;

/**
 * Default input panel for category.
 */
public class CategoryInputPanelBase extends CategoryInputPanel {

    protected DataInputPanel createItemPanel() {
        return new ItemPanel(true);
    }

}
