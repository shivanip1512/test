package com.cannontech.dbeditor.editor.device.configuration.category;

import java.awt.Dimension;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.wizard.WizardPanel;

/**
 * Wizard panel for category creation
 */
abstract public class CategoryWizardPanel extends WizardPanel {

    public CategoryWizardPanel() {
        super();
    }

    abstract protected DataInputPanel getNextInputPanel(DataInputPanel currentInputPanel);

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
    protected boolean isLastInputPanel(DataInputPanel currentPanel) {
        return true;
    }

}
