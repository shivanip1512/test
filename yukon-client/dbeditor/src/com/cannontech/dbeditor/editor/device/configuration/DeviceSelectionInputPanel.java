package com.cannontech.dbeditor.editor.device.configuration;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.cannontech.common.device.configuration.model.DeviceConfiguration;
import com.cannontech.common.device.configuration.service.DeviceConfigurationFuncs;
import com.cannontech.common.device.configuration.service.DeviceConfigurationFuncsImpl;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.message.dispatch.message.DBChangeMsg;

/**
 * Panel class which allows a user to assign device configurations to devices
 */
public class DeviceSelectionInputPanel extends DataInputPanel {

    private final DeviceAssigner deviceAssigner = new DeviceAssigner();

    public DeviceSelectionInputPanel() {
        this.initialize();
    }

    @Override
    public Object getValue(Object o) {
        return this.deviceAssigner;
    }

    @Override
    public void setValue(Object o) {

        DeviceConfiguration config = (DeviceConfiguration) o;

        DeviceConfigurationFuncs funcs = new DeviceConfigurationFuncsImpl();

        // Get the list of devices that are compatible with this type of config
        // and also the list of devices that have been assigned this config
        List<LiteYukonPAObject> possibleDeviceList = funcs.getPossibleDevicesForConfigType(config.getTypeId());
        List<LiteYukonPAObject> assignedDeviceList = funcs.getDevicesForConfig(config.getId());

        // Assigned devices should not be in the possible list
        possibleDeviceList.removeAll(assignedDeviceList);

        final DeviceListModel possibleModel = new DeviceListModel<YukonPAObject>();
        final DeviceListModel deviceConfigModel = new DeviceListModel<YukonPAObject>();

        boolean enabled = true;
        if (possibleDeviceList.size() == 0 && assignedDeviceList.size() == 0) {
            possibleModel.addElement("No compatible devices available");
            enabled = false;
        } else {
            possibleModel.addAll(possibleDeviceList.toArray());
            deviceConfigModel.addAll(assignedDeviceList.toArray());
        }

        JLabel configNameLabel = new JLabel("Device Configuration: " + config.getName());
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

        // Add available devices label and JList
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
        JList possibleDeviceJList = new JList(possibleModel);
        possibleDeviceJList.setEnabled(enabled);
        possibleDeviceJList.setToolTipText("Devices available to be assigned to this configuration");
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

        // Add the assign and unassign buttons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1));
        JButton addDeviceButton = new JButton(">");
        addDeviceButton.setEnabled(enabled);
        addDeviceButton.setToolTipText("Assign Configuration to Device(s)");
        addDeviceButton.setFont(DeviceConfigurationPropertyPanel.FIELD_FONT);
        buttonPanel.add(addDeviceButton);

        JButton removeDeviceButton = new JButton("<");
        removeDeviceButton.setEnabled(enabled);
        removeDeviceButton.setToolTipText("Unassign Configuration from Device(s)");
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

        // Add assigned devices label and JList
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
        JList assignedDeviceJList = new JList(deviceConfigModel);
        assignedDeviceJList.setEnabled(enabled);
        assignedDeviceJList.setToolTipText("Devices assigned to this configuration");
        JScrollPane deviceConfigScroll = new JScrollPane(assignedDeviceJList);
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

        // Initialize the device assigner
        deviceAssigner.initialize(config.getId(), assignedDeviceJList, possibleDeviceJList);

        addDeviceButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deviceAssigner.assignDevices();
                fireInputUpdate();
            }
        });

        removeDeviceButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deviceAssigner.unassignDevices();
                fireInputUpdate();
            }
        });

    }

    /**
     * Helper method to initialize this panel
     * @param config - Config to build selection panel for
     * @return Device Selection Panel
     */
    private void initialize() {
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    /**
     * Helper class which manages the assignment and unassignment of the config
     * to devices
     */
    private class DeviceAssigner extends DBPersistent implements CTIDbChange {

        private JList unassignedDeviceJList = null;
        private JList assignedDeviceJList = null;

        private List<LiteYukonPAObject> originalAssignedDeviceList = null;
        private List<LiteYukonPAObject> originalUnassignedDeviceList = null;
        private Integer configId = null;

        /**
         * Convenience method for initilization
         * @param configId - Id of config to be used
         * @param assignedDeviceList - List of devices assigned to the config
         * @param unassignedDeviceList - List of devices compatible with the
         *            config type
         */
        public void initialize(Integer configId, JList assignedDeviceList,
                JList unassignedDeviceList) {

            this.configId = configId;

            this.assignedDeviceJList = assignedDeviceList;
            this.unassignedDeviceJList = unassignedDeviceList;

            this.updateOriginalDeviceLists();
        }

        /**
         * Method to assign a device configuration to a device
         */
        public void assignDevices() {
            this.moveDevices(this.unassignedDeviceJList, this.assignedDeviceJList);
        }

        /**
         * Method to unassign a device configuration from a device
         */
        public void unassignDevices() {
            this.moveDevices(this.assignedDeviceJList, this.unassignedDeviceJList);
        }

        @Override
        public void add() throws SQLException {
        }

        @Override
        public void delete() throws SQLException {
        }

        @Override
        public void retrieve() throws SQLException {
        }

        @Override
        public void update() throws SQLException {

            if (this.assignedDeviceJList != null) {
                DeviceConfigurationFuncs funcs = new DeviceConfigurationFuncsImpl();

                List<LiteYukonPAObject> assignedDevices = ((DeviceListModel<LiteYukonPAObject>) this.assignedDeviceJList.getModel()).getAllValues();
                List<LiteYukonPAObject> unassignedDevices = ((DeviceListModel<LiteYukonPAObject>) this.unassignedDeviceJList.getModel()).getAllValues();

                assignedDevices.removeAll(this.originalAssignedDeviceList);
                unassignedDevices.removeAll(this.originalUnassignedDeviceList);

                funcs.assignConfigToDevices(this.configId, assignedDevices);
                funcs.removeConfigAssignmentForDevices(unassignedDevices);

            }
        }

        public DBChangeMsg[] getDBChangeMsgs(int typeOfChange) {

            List<LiteYukonPAObject> deviceChangedList = this.getChangedDevices();

            int index = 0;
            DBChangeMsg[] msgs = new DBChangeMsg[deviceChangedList.size()];

            // Create a dbchange msg for each device that was assigned or
            // unassigned a config
            Iterator<LiteYukonPAObject> deviceIter = deviceChangedList.iterator();
            while (deviceIter.hasNext()) {

                LiteYukonPAObject device = deviceIter.next();

                msgs[index++] = new DBChangeMsg(device.getLiteID(),
                                                DBChangeMsg.CHANGE_CONFIG_DB,
                                                DeviceConfiguration.DB_CHANGE_CATEGORY,
                                                DeviceConfigurationComboPanel.DB_CHANGE_OBJECT_TYPE,
                                                typeOfChange);

            }

            this.updateOriginalDeviceLists();

            return msgs;
        }

        @Override
        public String toString() {
            return "Device / Config assignment";
        }

        /**
         * Helper method to move selected devices from one JList to another
         * JList
         * @param fromJList - List to move selected devices from
         * @param toJList - List to move selected devices to
         */
        @SuppressWarnings("unchecked")
        private void moveDevices(JList fromJList, JList toJList) {

            Object[] selectedDevices = fromJList.getSelectedValues();
            if (selectedDevices.length > 0) {
                ((DeviceListModel) fromJList.getModel()).removeAll(selectedDevices);
                ((DeviceListModel) toJList.getModel()).addAll(selectedDevices);
            }
        }

        /**
         * Helper method to get a list of all devices that have been assigned or
         * unassigned a config
         * @return List of devices
         */
        private List<LiteYukonPAObject> getChangedDevices() {

            List<LiteYukonPAObject> assignedDevices = ((DeviceListModel<LiteYukonPAObject>) this.assignedDeviceJList.getModel()).getAllValues();
            List<LiteYukonPAObject> unassignedDevices = ((DeviceListModel<LiteYukonPAObject>) this.unassignedDeviceJList.getModel()).getAllValues();

            assignedDevices.removeAll(this.originalAssignedDeviceList);
            unassignedDevices.removeAll(this.originalUnassignedDeviceList);

            List<LiteYukonPAObject> deviceChangedList = new ArrayList<LiteYukonPAObject>();
            deviceChangedList.addAll(assignedDevices);
            deviceChangedList.addAll(unassignedDevices);

            return deviceChangedList;
        }

        private void updateOriginalDeviceLists() {
            this.originalAssignedDeviceList = ((DeviceListModel<LiteYukonPAObject>) this.assignedDeviceJList.getModel()).getAllValues();
            this.originalUnassignedDeviceList = ((DeviceListModel<LiteYukonPAObject>) this.unassignedDeviceJList.getModel()).getAllValues();
        }
    }

    /**
     * Helper class to allow add and remove of an array of objects from a
     * ListModel
     */
    private class DeviceListModel<T> extends DefaultListModel {

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

        @SuppressWarnings("unchecked")
        public List<T> getAllValues() {

            List<T> elementList = new ArrayList<T>();

            for (int i = 0; i < this.getSize(); i++) {
                elementList.add((T) this.getElementAt(i));
            }

            return elementList;
        }
    }
}
