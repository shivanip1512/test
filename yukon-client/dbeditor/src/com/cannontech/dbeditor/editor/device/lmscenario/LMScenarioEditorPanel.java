package com.cannontech.dbeditor.editor.device.lmscenario;

import java.awt.Dimension;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.editor.IMultiPanelEditor;
import com.cannontech.common.editor.PropertyPanel;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.dbeditor.wizard.device.lmscenario.LMScenarioProgramSettingsPanel;

public class LMScenarioEditorPanel extends PropertyPanel implements IMultiPanelEditor {
    private DataInputPanel[] inputPanels;
    private String[] inputPanelTabNames;

    public LMScenarioEditorPanel() {
        initialize();
    }

    /**
     * This method should return an object array with 2 elements,
     * Object[0] is a DataInputPanel
     * Object[1] is a String (Tab Name)
     */
    @Override
    public Object[] createNewPanel(int panelIndex) {
        Object[] objs = new Object[2];

        switch (panelIndex) {
        case 0:
            objs[0] = new LMScenarioProgramSettingsPanel();
            objs[1] = "General";
            break;
        }
        return objs;
    }

    @Override
    public DataInputPanel[] getInputPanels() {
        // At least guarantee a non-null array if not a meaningful one
        if (inputPanels == null) {
            inputPanels = new DataInputPanel[0];
        }

        return inputPanels;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(400, 450);
    }

    @Override
    public String[] getTabNames() {
        if (inputPanelTabNames == null) {
            inputPanelTabNames = new String[0];
        }

        return inputPanelTabNames;
    }

    /**
     * Called whenever the part throws an exception.
     */
    private void handleException(java.lang.Throwable exception) {
        CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
        CTILogger.error(exception.getMessage(), exception);
    }

    private void initialize() {
        try {
            setName("ProgramScenarioEditorPanel");
            setPreferredSize(new Dimension(400, 350));
            setLayout(null);
            setSize(400, 350);
            setMaximumSize(new Dimension(400, 350));
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    @Override
    public void setValue(Object val) {
        // Vector to hold the panels temporarily
        Vector<DataInputPanel> panels = new Vector<>();
        Vector<String> tabs = new Vector<>();
        DataInputPanel tempPanel;
        final int PANEL_COUNT = 1;

        for (int i = 0; i < PANEL_COUNT; i++) {
            Object[] panelTabs = createNewPanel(i);

            tempPanel = (DataInputPanel) panelTabs[0];
            panels.addElement(tempPanel);
            tabs.addElement((String) panelTabs[1]);
        }

        inputPanels = new DataInputPanel[panels.size()];
        panels.copyInto(inputPanels);

        inputPanelTabNames = new String[tabs.size()];
        tabs.copyInto(inputPanelTabNames);

        // Allow super to do whatever it needs to
        super.setValue(val);
    }

    @Override
    public String toString() {
        return "Control Scenario Editor";
    }
}
