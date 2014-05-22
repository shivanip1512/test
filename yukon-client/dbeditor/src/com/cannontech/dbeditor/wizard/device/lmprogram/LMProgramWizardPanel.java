package com.cannontech.dbeditor.wizard.device.lmprogram;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.wizard.WizardPanel;

public class LMProgramWizardPanel extends WizardPanel {
    private LMProgramTypePanel lmProgramTypePanel;
    private LMProgramBasePanel lmProgramBasePanel;
    private LMProgramControlWindowPanel lmProgramControlWindowPanel;
    private LMProgramListPanel lmProgramListPanel;
    private LMProgramCurtailListPanel lmProgramCurtailListPanel;
    private LMProgramDirectPanel lmProgramDirectPanel;
    private LMProgramEnergyExchangePanel lmProgramEnergyExchangePanel;
    private LMProgramEnergyExchangeCustomerListPanel lmProgramEnergyExchangeCustomerListPanel;

    private LMProgramCurtailmentPanel lmProgramCurtailmentPanel;
    private LMProgramDirectNotifGroupListPanel lmProgramDirectNotifGroupListPanel;

    public LMProgramWizardPanel() {
        super();
    }

    @Override
    public java.awt.Dimension getActualSize() {
        setPreferredSize(new java.awt.Dimension(430, 500));
        return getPreferredSize();
    }

    @Override
    protected String getHeaderText() {
        return "LM Program Setup";
    }

    public LMProgramBasePanel getLmProgramBasePanel() {
        if (lmProgramBasePanel == null) {
            PaoType programType = getLmProgramTypePanel().getLMSelectedType();
            lmProgramBasePanel = new LMProgramBasePanel(programType);
        }

        return lmProgramBasePanel;
    }

    public LMProgramControlWindowPanel getLMProgramControlWindowPanel() {
        if (lmProgramControlWindowPanel == null)
            lmProgramControlWindowPanel = new LMProgramControlWindowPanel();

        return lmProgramControlWindowPanel;
    }

    public LMProgramCurtailListPanel getLmProgramCurtailListPanel() {
        if (lmProgramCurtailListPanel == null)
            lmProgramCurtailListPanel = new LMProgramCurtailListPanel();

        return lmProgramCurtailListPanel;
    }

    public LMProgramCurtailmentPanel getLmProgramCurtailmentPanel() {
        if (lmProgramCurtailmentPanel == null)
            lmProgramCurtailmentPanel = new LMProgramCurtailmentPanel();

        return lmProgramCurtailmentPanel;
    }

    public LMProgramDirectPanel getLmProgramDirectPanel() {
        if (lmProgramDirectPanel == null) {
            PaoType programType = getLmProgramTypePanel().getLMSelectedType();
            lmProgramDirectPanel = new LMProgramDirectPanel(programType);
        }

        return lmProgramDirectPanel;
    }

    public LMProgramDirectNotifGroupListPanel getLmProgramDirectCustomerListPanel() {
        if (lmProgramDirectNotifGroupListPanel == null)
            lmProgramDirectNotifGroupListPanel = new LMProgramDirectNotifGroupListPanel();

        return lmProgramDirectNotifGroupListPanel;
    }

    public LMProgramEnergyExchangeCustomerListPanel getLmProgramEnergyExchangeCustomerListPanel() {
        if (lmProgramEnergyExchangeCustomerListPanel == null)
            lmProgramEnergyExchangeCustomerListPanel = new LMProgramEnergyExchangeCustomerListPanel();

        return lmProgramEnergyExchangeCustomerListPanel;
    }

    public LMProgramEnergyExchangePanel getLmProgramEnergyExchangePanel() {
        if (lmProgramEnergyExchangePanel == null)
            lmProgramEnergyExchangePanel = new LMProgramEnergyExchangePanel();

        return lmProgramEnergyExchangePanel;
    }

    public LMProgramListPanel getLmProgramListPanel() {
        if (lmProgramListPanel == null)
            lmProgramListPanel = new LMProgramListPanel();

        return lmProgramListPanel;
    }

    public LMProgramTypePanel getLmProgramTypePanel() {
        if (lmProgramTypePanel == null)
            lmProgramTypePanel = new LMProgramTypePanel();

        return lmProgramTypePanel;
    }

    @Override
    public java.awt.Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    protected DataInputPanel getNextInputPanel(DataInputPanel currentInputPanel) {
        if (currentInputPanel == null) {
            getLmProgramTypePanel().setFirstFocus();
            return getLmProgramTypePanel();
        } else if (currentInputPanel == getLmProgramTypePanel()) {
            getLmProgramBasePanel().setIsAWizardOp(true);
            getLmProgramBasePanel().getJLabelActualProgType().setText(getLmProgramTypePanel().getLMSelectedType().getPaoTypeName());
            getLmProgramBasePanel().setTriggerThresholdVisible(true);
            getLmProgramBasePanel().setFirstFocus();
            return getLmProgramBasePanel();
        } else if (currentInputPanel == getLmProgramBasePanel()) {
            getLmProgramDirectPanel().setFirstFocus();
            return getLmProgramDirectPanel();
        }
        // Direct program begin
        else if (currentInputPanel == getLmProgramDirectPanel()) {
            getLMProgramControlWindowPanel().setTimedOperationalStateCondition(getLmProgramBasePanel().isTimedOperationalState());
            getLMProgramControlWindowPanel().getWindowChangePasser().setSelected(getLmProgramBasePanel().isTimedOperationalState());
            getLMProgramControlWindowPanel().setFirstFocus();
            return getLMProgramControlWindowPanel();
        } else if (currentInputPanel == getLMProgramControlWindowPanel()) {
            getLmProgramListPanel().initLeftList(!getLmProgramDirectPanel().hasLatchingGear(),
                                                 getLmProgramTypePanel().getLMSelectedType());
            getLmProgramListPanel().setFirstFocus();
            return getLmProgramListPanel();
        } else if (currentInputPanel == getLmProgramListPanel()) {
            getLmProgramDirectCustomerListPanel().setFirstFocus();
            return getLmProgramDirectCustomerListPanel();
        }
        return null;
    }

    @Override
    protected boolean isLastInputPanel(DataInputPanel currentPanel) {
        return (currentPanel == lmProgramDirectNotifGroupListPanel);
    }

    @Override
    protected boolean isBackButtonSupported(DataInputPanel inputPanel) {
        if (inputPanel instanceof LMProgramBasePanel) {
            return false;
        }
        return super.isBackButtonSupported(inputPanel);
    }

}
