package com.cannontech.dbeditor.wizard.device.lmscenario;

import java.awt.Dimension;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.wizard.WizardPanel;

public class LMScenarioWizardPanel extends WizardPanel {
    private LMScenarioProgramSettingsPanel lmScenarioProgramSettingsPanel;

    @Override
    public Dimension getActualSize() {
        setPreferredSize(new Dimension(410, 500));

        return getPreferredSize();
    }

    @Override
    protected String getHeaderText() {
        return "LM Control Scenario Setup";
    }

    public LMScenarioProgramSettingsPanel getLmScenarioProgramSettingsPanel() {
        if (lmScenarioProgramSettingsPanel == null) {
            lmScenarioProgramSettingsPanel = new LMScenarioProgramSettingsPanel();
        }

        return lmScenarioProgramSettingsPanel;
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    protected DataInputPanel getNextInputPanel(DataInputPanel currentInputPanel) {
        if (currentInputPanel == null) {
            return getLmScenarioProgramSettingsPanel();
        }

        return null;
    }

    @Override
    protected boolean isLastInputPanel(DataInputPanel currentPanel) {
        return (currentPanel == getLmScenarioProgramSettingsPanel());
    }
}
