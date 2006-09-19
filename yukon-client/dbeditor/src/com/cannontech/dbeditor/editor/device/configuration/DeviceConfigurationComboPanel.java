package com.cannontech.dbeditor.editor.device.configuration;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import com.cannontech.common.device.configuration.model.DeviceConfiguration;
import com.cannontech.common.device.configuration.model.Type;
import com.cannontech.common.device.configuration.service.DeviceConfigurationFuncs;
import com.cannontech.common.device.configuration.service.DeviceConfigurationFuncsImpl;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.TitleBorder;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.message.dispatch.message.DBChangeMsg;

/**
 * Panel to display a list of device configurations valid for a given device
 * type
 */
public class DeviceConfigurationComboPanel extends DataInputPanel {

    public static final String DB_CHANGE_OBJECT_TYPE = "device";
    private static final Integer NO_CONFIG_SELECTED_ID = -1;

    private YukonPAObject device = null;
    private JComboBox deviceConfigurationCombo = new JComboBox();

    public DeviceConfigurationComboPanel() {
        this.initialize();
    }

    @Override
    public Object getValue(Object o) {
        return new DeviceConfigurationDBPersistant(this.device,
                                                   ((DeviceConfiguration) deviceConfigurationCombo.getSelectedItem()).getId());
    }

    @Override
    public void setValue(Object o) {

        DeviceBase device = (DeviceBase) o;

        this.deviceConfigurationCombo.removeAllItems();

        DeviceConfigurationFuncs funcs = new DeviceConfigurationFuncsImpl();

        List<DeviceConfiguration> configList = new ArrayList<DeviceConfiguration>();
        Iterator<Type> configTypeIter = funcs.getConfigTypesForDevice(device.getPAOType())
                                             .iterator();
        while (configTypeIter.hasNext()) {
            Type configType = configTypeIter.next();

            configList.addAll(funcs.getConfigsForType(configType.getId()));
        }

        this.device = device;
        DeviceConfiguration selectedConfig = funcs.getConfigForDevice(this.device.getPAObjectID());

        // Add a dummy DeviceConfiguration for no selection
        DeviceConfiguration noSelection = new DeviceConfiguration();
        noSelection.setId(NO_CONFIG_SELECTED_ID);
        noSelection.setName("No Selection");
        this.deviceConfigurationCombo.addItem(noSelection);

        if (configList.size() > 0) {
            // Add all of the configurations available for the device's type

            Iterator<DeviceConfiguration> configListIter = configList.iterator();
            while (configListIter.hasNext()) {
                DeviceConfiguration config = configListIter.next();
                this.deviceConfigurationCombo.addItem(config);

                // Select the config that is assigned to the device if there is
                // one
                if (selectedConfig != null && config.getId().equals(selectedConfig.getId())) {
                    this.deviceConfigurationCombo.setSelectedItem(config);
                }

            }

        } else {
            // If there are no configurations for the devices type, disable the
            // combo box and display a message

            this.deviceConfigurationCombo.setEnabled(false);

            JLabel noConfigLabel = new JLabel("There are no configurations for this device type");
            noConfigLabel.setFont(new Font("dialog", 0, 14));
            noConfigLabel.setForeground(Color.BLUE);
            this.add(noConfigLabel, new GridBagConstraints(1,
                                                           2,
                                                           2,
                                                           1,
                                                           0.0,
                                                           1.0,
                                                           GridBagConstraints.NORTH,
                                                           GridBagConstraints.NONE,
                                                           new Insets(5, 5, 5, 5),
                                                           0,
                                                           0));

            this.deviceConfigurationCombo.setFont(new Font("sansserif", 0, 14));
            this.deviceConfigurationCombo.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    fireInputUpdate();
                }
            });

        }

    }

    /**
     * Helper method to initialize this panel
     */
    private void initialize() {

        this.setLayout(new GridBagLayout());

        TitleBorder border = new TitleBorder("Device Configuration");
        border.setTitleFont(DeviceConfigurationPropertyPanel.TITLE_FONT);
        this.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10,
                                                                                          10,
                                                                                          10,
                                                                                          10),
                                                          border));

        JLabel deviceConfigurationLabel = new JLabel("Device Configuration:");
        deviceConfigurationLabel.setToolTipText("Select a device configuration for this device");
        deviceConfigurationLabel.setFont(new Font("dialog", 0, 14));
        this.add(deviceConfigurationLabel, new GridBagConstraints(1,
                                                                  1,
                                                                  1,
                                                                  1,
                                                                  0.0,
                                                                  0.0,
                                                                  GridBagConstraints.NORTHWEST,
                                                                  GridBagConstraints.NONE,
                                                                  new Insets(5, 5, 5, 0),
                                                                  0,
                                                                  0));

        this.deviceConfigurationCombo.setFont(new Font("sansserif", 0, 14));
        this.deviceConfigurationCombo.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                fireInputUpdate();
            }
        });
        this.add(this.deviceConfigurationCombo,
                 new GridBagConstraints(2,
                                        1,
                                        1,
                                        1,
                                        1.0,
                                        0.0,
                                        GridBagConstraints.NORTHWEST,
                                        GridBagConstraints.NONE,
                                        new Insets(5, 5, 5, 5),
                                        0,
                                        0));

    }

    /**
     * Inner class which implements the DBPersistant update method to persist
     * the device / configuration mapping
     */
    private class DeviceConfigurationDBPersistant extends DBPersistent implements CTIDbChange {

        private YukonPAObject device = null;
        private Integer configId = null;

        public DeviceConfigurationDBPersistant(YukonPAObject device, Integer configId) {
            this.device = device;
            this.configId = configId;
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
            DeviceConfigurationFuncs funcs = new DeviceConfigurationFuncsImpl();

            if (this.configId.equals(DeviceConfigurationComboPanel.NO_CONFIG_SELECTED_ID)) {
                // If no config is selected, remove any config mapping for the
                // device

                List<LiteYukonPAObject> deviceList = new ArrayList<LiteYukonPAObject>();
                deviceList.add((LiteYukonPAObject) LiteFactory.createLite(this.device));
                funcs.removeConfigAssignmentForDevices(deviceList);

            } else if (this.device != null && this.configId != null) {
                // Save the device / config mapping
                funcs.save(this.device.getPAObjectID(), this.configId);

            }
        }

        @Override
        public String toString() {
            return this.device.toString();
        }

        public DBChangeMsg[] getDBChangeMsgs(int typeOfChange) {

            DBChangeMsg[] msgs = new DBChangeMsg[1];
            msgs[0] = new DBChangeMsg(this.device.getPAObjectID(),
                                      DBChangeMsg.CHANGE_CONFIG_DB,
                                      DeviceConfiguration.DB_CHANGE_CATEGORY,
                                      DB_CHANGE_OBJECT_TYPE,
                                      typeOfChange);

            return msgs;
        }
    }

}
