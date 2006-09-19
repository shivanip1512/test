package com.cannontech.dbeditor.editor.device.configuration;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.editor.PropertyPanel;
import com.cannontech.common.gui.util.DataInputPanel;

/**
 * Panel class which allows a user to assign device configurations to devices
 */
public class DeviceSelectionPropertyPanel extends PropertyPanel {

    private List<DataInputPanel> inputPanels = null;
    private List<String> inputPanelTabNames = null;

    public DeviceSelectionPropertyPanel() {
        initialize();
    }

    @Override
    public String toString() {
        return "Device Assignment";
    }

    @Override
    protected DataInputPanel[] getInputPanels() {
        return this.inputPanels.toArray(new DataInputPanel[] {});
    }

    @Override
    protected String[] getTabNames() {
        return this.inputPanelTabNames.toArray(new String[] {});
    }

    /**
     * Helper method to initialize this panel.
     */
    private void initialize() {

        this.inputPanels = new ArrayList<DataInputPanel>();
        this.inputPanelTabNames = new ArrayList<String>();
        this.inputPanelTabNames.add("Assignment");

        this.inputPanels.add(new DeviceSelectionInputPanel());

    }

}
