package com.cannontech.dbeditor.editor.device.configuration;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.functions.DeviceConfigurationFuncs;
import com.cannontech.database.cache.functions.DeviceConfigurationFuncsImpl;
import com.cannontech.database.data.device.configuration.DeviceConfiguration;
import com.cannontech.database.data.lite.LiteYukonPAObject;

/**
 * Panel class which allows a user to assign device configurations to devices
 */
public class DeviceSelectionPanel extends JPanel {

    private DeviceConfiguration config = null;

    public DeviceSelectionPanel(DeviceConfiguration config) {
        this.config = config;
        this.initialize();
    }

    /**
     * Helper method to initialize this panel
     * @param config - Config to build selection panel for
     * @return Device Selection Panel
     */
    private void initialize() {

        DeviceConfigurationFuncs funcs = new DeviceConfigurationFuncsImpl();

        List<LiteYukonPAObject> possibleDeviceList = funcs.getPossibleDevicesForConfigType(this.config.getTypeId());
        List<LiteYukonPAObject> deviceConfigList = funcs.getDevicesForConfig(this.config.getId());

        possibleDeviceList.removeAll(deviceConfigList);

        this.setLayout(new GridBagLayout());

        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        final DeviceListModel possibleModel = new DeviceListModel();
        final DeviceListModel deviceConfigModel = new DeviceListModel();

        boolean enabled = true;
        if (possibleDeviceList.size() == 0 && deviceConfigList.size() == 0) {
            possibleModel.addElement("No compatible devices available");
            enabled = false;
        } else {
            possibleModel.addAll(possibleDeviceList.toArray());
            deviceConfigModel.addAll(deviceConfigList.toArray());
        }

        JLabel configNameLabel = new JLabel("Device Configuration: " + this.config.getName());
        configNameLabel.setFont(DeviceConfigurationPropertyPanel.FIELD_FONT_BOLD);
        this.add(configNameLabel, new GridBagConstraints(1,
                                                         1,
                                                         3,
                                                         1,
                                                         1.0,
                                                         0.0,
                                                         GridBagConstraints.WEST,
                                                         GridBagConstraints.NONE,
                                                         new Insets(10, 5, 10, 5),
                                                         5,
                                                         5));

        this.add(new JLabel("Available Devices:"), new GridBagConstraints(1,
                                                                          2,
                                                                          1,
                                                                          1,
                                                                          1.0,
                                                                          0.0,
                                                                          GridBagConstraints.WEST,
                                                                          GridBagConstraints.NONE,
                                                                          new Insets(0, 0, 0, 0),
                                                                          5,
                                                                          5));
        final JList possibleDeviceJList = new JList(possibleModel);
        possibleDeviceJList.setEnabled(enabled);
        possibleDeviceJList.setToolTipText("Devices available to be assigned this configuration");
        JScrollPane possibleScroll = new JScrollPane(possibleDeviceJList);
        this.add(possibleScroll, new GridBagConstraints(1,
                                                        3,
                                                        1,
                                                        1,
                                                        1.0,
                                                        1.0,
                                                        GridBagConstraints.EAST,
                                                        GridBagConstraints.BOTH,
                                                        new Insets(0, 0, 0, 0),
                                                        5,
                                                        5));

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1));
        JButton addDeviceButton = new JButton(">");
        addDeviceButton.setEnabled(enabled);
        addDeviceButton.setToolTipText("Add Device to Configuration");
        addDeviceButton.setFont(DeviceConfigurationPropertyPanel.FIELD_FONT);
        buttonPanel.add(addDeviceButton);

        JButton removeDeviceButton = new JButton("<");
        removeDeviceButton.setEnabled(enabled);
        removeDeviceButton.setToolTipText("Remove Device from Configuration");
        removeDeviceButton.setFont(DeviceConfigurationPropertyPanel.FIELD_FONT);
        buttonPanel.add(removeDeviceButton);

        this.add(buttonPanel, new GridBagConstraints(2,
                                                     3,
                                                     1,
                                                     1,
                                                     0.0,
                                                     1.0,
                                                     GridBagConstraints.CENTER,
                                                     GridBagConstraints.NONE,
                                                     new Insets(0, 5, 0, 5),
                                                     5,
                                                     5));

        this.add(new JLabel("Devices assigned this configuration:"),
                 new GridBagConstraints(3,
                                        2,
                                        1,
                                        1,
                                        1.0,
                                        0.0,
                                        GridBagConstraints.WEST,
                                        GridBagConstraints.NONE,
                                        new Insets(0, 0, 0, 0),
                                        5,
                                        5));
        final JList deviceConfigJList = new JList(deviceConfigModel);
        deviceConfigJList.setEnabled(enabled);
        deviceConfigJList.setToolTipText("Devices assigned to this configuration");
        JScrollPane deviceConfigScroll = new JScrollPane(deviceConfigJList);
        this.add(deviceConfigScroll, new GridBagConstraints(3,
                                                            3,
                                                            1,
                                                            1,
                                                            1.0,
                                                            1.0,
                                                            GridBagConstraints.WEST,
                                                            GridBagConstraints.BOTH,
                                                            new Insets(0, 0, 0, 0),
                                                            5,
                                                            5));

        JButton closeButton = new JButton("Close Window");
        closeButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                CtiUtilities.getParentInternalFrame((JButton) e.getSource()).dispose();
            }
        });
        this.add(closeButton, new GridBagConstraints(1,
                                                     4,
                                                     3,
                                                     1,
                                                     1.0,
                                                     1.0,
                                                     GridBagConstraints.CENTER,
                                                     GridBagConstraints.NONE,
                                                     new Insets(10, 0, 10, 0),
                                                     5,
                                                     5));

        addDeviceButton.addActionListener(new DeviceButtonListener(this.config.getId(),
                                                                   deviceConfigModel,
                                                                   possibleModel,
                                                                   possibleDeviceJList,
                                                                   true));

        removeDeviceButton.addActionListener(new DeviceButtonListener(this.config.getId(),
                                                                      possibleModel,
                                                                      deviceConfigModel,
                                                                      deviceConfigJList,
                                                                      false));

    }

    /**
     * Helper class to listen for device add and remove button clicks
     */
    private class DeviceButtonListener implements ActionListener {

        private Integer configId = null;
        private boolean add = false;
        private DeviceListModel addToModel = null;
        private DeviceListModel removeFromModel = null;
        private JList list = null;

        public DeviceButtonListener(Integer configId, DeviceListModel addToModel,
                DeviceListModel removeFromModel, JList list, boolean addDevice) {

            this.configId = configId;
            this.addToModel = addToModel;
            this.removeFromModel = removeFromModel;
            this.list = list;
            this.add = addDevice;
        }

        public void actionPerformed(ActionEvent e) {

            // Get the selected devices from the JList
            Object[] selectedDevices = this.list.getSelectedValues();

            if (selectedDevices.length > 0) {

                List<LiteYukonPAObject> deviceList = new ArrayList<LiteYukonPAObject>();
                for (int i = 0; i < selectedDevices.length; i++) {
                    LiteYukonPAObject device = (LiteYukonPAObject) selectedDevices[i];
                    deviceList.add(device);
                }

                DeviceConfigurationFuncs funcs = new DeviceConfigurationFuncsImpl();
                boolean success = false;
                if (this.add) {
                    // Add the devices
                    success = funcs.assignConfigToDevices(this.configId, deviceList);
                } else {
                    // Remove the devices
                    success = funcs.removeConfigAssignmentForDevices(deviceList);
                }

                // Update the JLists
                if (success) {
                    this.addToModel.addAll(selectedDevices);
                    this.removeFromModel.removeAll(selectedDevices);
                }
            }
        }
    }

    /**
     * Helper class to allow add and remove of an array of objects from a
     * ListModel
     */
    private class DeviceListModel extends DefaultListModel {

        public void addAll(Object[] objects) {

            for (int i = 0; i < objects.length; i++) {
                Object obj = objects[i];
                this.addElement(obj);
            }
        }

        public void removeAll(Object[] objects) {

            for (int i = 0; i < objects.length; i++) {
                Object obj = objects[i];
                this.removeElement(obj);
            }
        }

        public List<Object> getAllValues() {

            List<Object> elementList = new ArrayList<Object>();

            for (int i = 0; i < this.getSize(); i++) {
                elementList.add(this.getElementAt(i));
            }

            return elementList;
        }
    }
}
