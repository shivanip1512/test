package com.cannontech.dbeditor.editor.device.configuration;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.database.cache.functions.DeviceConfigurationFuncs;
import com.cannontech.database.cache.functions.DeviceConfigurationFuncsImpl;
import com.cannontech.database.data.device.configuration.DeviceConfiguration;
import com.cannontech.database.data.device.configuration.Type;

/**
 * Panel class used to select which type of configuration to create
 */
public class DeviceConfigurationWizardTypePanel extends DataInputPanel {

    private ButtonGroup group;
    private Map<JRadioButton, Integer> typeMap = new HashMap<JRadioButton, Integer>();

    public DeviceConfigurationWizardTypePanel() {
        this.initialize();
    }

    @Override
    public Object getValue(Object o) {

        Iterator<JRadioButton> buttonIter = this.typeMap.keySet().iterator();
        while (buttonIter.hasNext()) {
            JRadioButton button = buttonIter.next();
            if (button.isSelected()) {
                return this.typeMap.get(button);
            }
        }

        return null;
    }

    @Override
    public void setValue(Object o) {

        if (o != null) {
            DeviceConfiguration config = (DeviceConfiguration) o;
            Type type = config.getType();
            this.disablePanel();

            Iterator<Entry<JRadioButton, Integer>> typeIter = this.typeMap.entrySet().iterator();
            while (typeIter.hasNext()) {

                Entry<JRadioButton, Integer> entry = typeIter.next();

                if (type.getId().equals(entry.getValue())) {
                    entry.getKey().setSelected(true);
                    break;
                }
            }
        }
    }

    /**
     * Helper method to initialize the UI for this panel
     */
    private void initialize() {

        this.setLayout(new GridBagLayout());

        DeviceConfigurationFuncs funcs = new DeviceConfigurationFuncsImpl();
        List<Type> configTypes = funcs.getConfigTypes();

        this.group = new ButtonGroup();

        boolean selected = false;
        int row = 1;
        Iterator<Type> typeIter = configTypes.iterator();
        while (typeIter.hasNext()) {
            Type type = typeIter.next();

            JRadioButton typeRadio = new JRadioButton(type.getName());
            if (type.getDescription() != null) {
                typeRadio.setToolTipText(type.getDescription());
            }
            if (!selected) {
                selected = true;
                typeRadio.setSelected(true);
            }
            this.typeMap.put(typeRadio, type.getId());
            this.group.add(typeRadio);
            typeRadio.setFont(DeviceConfigurationPropertyPanel.FIELD_FONT_BOLD);

            this.add(typeRadio, new GridBagConstraints(1,
                                                       row++,
                                                       1,
                                                       1,
                                                       1.0,
                                                       0.0,
                                                       GridBagConstraints.WEST,
                                                       GridBagConstraints.NONE,
                                                       new Insets(5, 15, 0, 0),
                                                       0,
                                                       0));

        }

    }

    /**
     * Helper method to disable all of the radio buttons on this panel
     */
    public void disablePanel() {
        Enumeration<AbstractButton> buttons = this.group.getElements();
        while (buttons.hasMoreElements()) {
            AbstractButton button = buttons.nextElement();
            button.setEnabled(false);

        }
    }

}
