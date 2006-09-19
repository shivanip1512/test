package com.cannontech.dbeditor.editor.device.configuration;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

        final DeviceListModel possibleModel = new DeviceListModel();
        final DeviceListModel deviceConfigModel = new DeviceListModel();

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

        private List<LiteYukonPAObject> assignedDeviceList = new ArrayList<LiteYukonPAObject>();
        private List<LiteYukonPAObject> unassignedDeviceList = new ArrayList<LiteYukonPAObject>();
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

            this.assignedDeviceJList = assignedDeviceList;
            this.unassignedDeviceJList = unassignedDeviceList;
            this.configId = configId;
        }

        /**
         * Method to assign a device configuration to a device
         */
        public void assignDevices() {
            this.moveDevices(this.unassignedDeviceJList,
                             this.unassignedDeviceList,
                             this.assignedDeviceJList,
                             this.assignedDeviceList);
        }

        /**
         * Method to unassign a device configuration from a device
         */
        public void unassignDevices() {
            this.moveDevices(this.assignedDeviceJList,
                             this.assignedDeviceList,
                             this.unassignedDeviceJList,
                             this.unassignedDeviceList);
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

                funcs.assignConfigToDevices(this.configId, this.assignedDeviceList);
                funcs.removeConfigAssignmentForDevices(this.unassignedDeviceList);
            }
        }

        public DBChangeMsg[] getDBChangeMsgs(int typeOfChange) {

            int index = 0;
            DBChangeMsg[] msgs = new DBChangeMsg[this.assignedDeviceList.size()
                    + this.unassignedDeviceList.size()];

            // Create a dbchange msg for each device that was assigned a config
            Iterator<LiteYukonPAObject> assignIter = this.assignedDeviceList.iterator();
            while (assignIter.hasNext()) {

                LiteYukonPAObject device = assignIter.next();

                msgs[index++] = new DBChangeMsg(device.getLiteID(),
                                                DBChangeMsg.CHANGE_CONFIG_DB,
                                                DeviceConfiguration.DB_CHANGE_CATEGORY,
                                                DeviceConfigurationComboPanel.DB_CHANGE_OBJECT_TYPE,
                                                typeOfChange);
            }

            // Create a dbchange msg for each device that was unassigned a
            // config
            Iterator<LiteYukonPAObject> unassignIter = this.unassignedDeviceList.iterator();
            while (unassignIter.hasNext()) {

                LiteYukonPAObject device = unassignIter.next();
                msgs[index++] = new DBChangeMsg(device.getLiteID(),
                                                DBChangeMsg.CHANGE_CONFIG_DB,
                                                DeviceConfiguration.DB_CHANGE_CATEGORY,
                                                DeviceConfigurationComboPanel.DB_CHANGE_OBJECT_TYPE,
                                                typeOfChange);
            }

            return msgs;
        }
        
        @Override
        public String toString(){
            return "Device / Config assignment";
        }

        /**
         * Helper method to move selected devices from one JList to another
         * JList
         * @param fromJList - List to move selected devices from
         * @param toJList - List to move selected devices to
         */
        @SuppressWarnings("unchecked")
        private void moveDevices(JList fromJList, List<LiteYukonPAObject> fromList, JList toJList,
                List<LiteYukonPAObject> toList) {

            Object[] selectedDevices = fromJList.getSelectedValues();
            if (selectedDevices.length > 0) {
                ((DeviceListModel) fromJList.getModel()).removeAll(selectedDevices);
                fromList.removeAll(Arrays.asList(selectedDevices));
                ((DeviceListModel) toJList.getModel()).addAll(selectedDevices);
                toList.addAll((Collection<? extends LiteYukonPAObject>) Arrays.asList(selectedDevices));
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
