package com.cannontech.dbeditor.editor.device.configuration;

import java.awt.Dimension;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.wizard.WizardPanel;

/**
 * Wizard panel for category creation
 */
public class CategoryWizardPanel extends WizardPanel {

    @Override
    public Dimension getActualSize() {

        setPreferredSize(new java.awt.Dimension(410, 480));

        return getPreferredSize();
    }

    @Override
    protected String getHeaderText() {
        return "Category Setup";
    }

    @Override
    protected DataInputPanel getNextInputPanel(DataInputPanel currentInputPanel) {
        return new CategoryInputPanel();
    }

    @Override
    protected boolean isLastInputPanel(DataInputPanel currentPanel) {
        return true;
    }

}
