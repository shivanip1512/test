package com.cannontech.dbeditor.wizard.device;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.event.CaretListener;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.gui.unchanging.LongRangeDocument;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.definition.service.PaoDefinitionService;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.wizard.CancelInsertException;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.device.CCU721;
import com.cannontech.database.data.device.CarrierBase;
import com.cannontech.database.data.device.DNPBase;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.DeviceFactory;
import com.cannontech.database.data.device.IDLCBase;
import com.cannontech.database.data.device.IonBase;
import com.cannontech.database.data.device.RTCBase;
import com.cannontech.database.data.device.RTM;
import com.cannontech.database.data.device.Repeater900;
import com.cannontech.database.data.device.Repeater921;
import com.cannontech.database.data.device.Series5Base;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.db.device.DeviceCarrierSettings;
import com.cannontech.dbeditor.DatabaseEditorOptionPane;
import com.cannontech.device.range.DlcAddressRangeService;
import com.cannontech.spring.YukonSpringHook;

public class DeviceNameAddressPanel extends DataInputPanel implements CaretListener {
    private DeviceBase deviceBase = null;
    private javax.swing.JTextField ivjAddressTextField = null;
    private javax.swing.JLabel ivjNameLabel = null;
    private javax.swing.JTextField ivjNameTextField = null;
    private javax.swing.JLabel ivjPhysicalAddressLabel = null;
    private javax.swing.JLabel ivjJLabelErrorMessage = null;
    private javax.swing.JPanel ivjJPanelNameAddy = null;
    private javax.swing.JPanel ivjJPanel1 = null;

    private JCheckBox createPointsCheck = null;

    private DlcAddressRangeService dlcAddressRangeService = YukonSpringHook.getBean("dlcAddressRangeService", DlcAddressRangeService.class);

    public DeviceNameAddressPanel() {
        super();
        initialize();
    }

    @Override
    public void caretUpdate(javax.swing.event.CaretEvent e) {
        try {
            if (e.getSource() == getNameTextField() ||
                    e.getSource() == getAddressTextField()) {
                this.eitherTextField_CaretUpdate(e);
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    private void checkPaoAddresses(int address) {

        String[] devices = DeviceCarrierSettings.isAddressUnique(address, null);

        if (devices.length > 0) {

            String message = "The address '" + address + "' is already used by the following devices,\n" + "are you sure you want to use it again?\n";

            int res = DatabaseEditorOptionPane.showAlreadyUsedConfirmDialog(this, message, "Address Already Used", devices);
            if (res == JOptionPane.NO_OPTION) {
                throw new CancelInsertException("Device was not inserted");
            }
        }
    }

    public void eitherTextField_CaretUpdate(javax.swing.event.CaretEvent caretEvent) {
        fireInputUpdate();
    }

    public String getAddress() {
        return getAddressTextField().getText();
    }

    private javax.swing.JTextField getAddressTextField() {
        if (ivjAddressTextField == null) {
            try {
                ivjAddressTextField = new javax.swing.JTextField();
                ivjAddressTextField.setName("AddressTextField");
                ivjAddressTextField.setFont(new java.awt.Font("sansserif", 0, 14));
                ivjAddressTextField.setColumns(6);

                ivjAddressTextField.setDocument(new LongRangeDocument(-999999999L, 999999999L));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjAddressTextField;
    }

    private javax.swing.JLabel getJLabelErrorMessage() {
        if (ivjJLabelErrorMessage == null) {
            try {
                ivjJLabelErrorMessage = new javax.swing.JLabel();
                ivjJLabelErrorMessage.setName("JLabelRange");
                ivjJLabelErrorMessage.setOpaque(false);
                ivjJLabelErrorMessage.setText("..RANGE TEXT..");
                ivjJLabelErrorMessage.setVisible(true);
                ivjJLabelErrorMessage.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
                ivjJLabelErrorMessage.setFont(new java.awt.Font("Arial", 1, 10));
                ivjJLabelErrorMessage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

                ivjJLabelErrorMessage.setVisible(false);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelErrorMessage;
    }

    private javax.swing.JPanel getJPanel1() {
        if (ivjJPanel1 == null) {
            try {
                ivjJPanel1 = new javax.swing.JPanel();
                ivjJPanel1.setName("JPanel1");
                ivjJPanel1.setPreferredSize(new java.awt.Dimension(342, 27));
                ivjJPanel1.setLayout(new java.awt.FlowLayout());
                ivjJPanel1.setMaximumSize(new java.awt.Dimension(342, 27));
                ivjJPanel1.setMinimumSize(new java.awt.Dimension(342, 27));
                getJPanel1().add(getJLabelErrorMessage(), getJLabelErrorMessage().getName());
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJPanel1;
    }

    private javax.swing.JPanel getJPanelNameAddy() {
        if (ivjJPanelNameAddy == null) {
            try {
                ivjJPanelNameAddy = new javax.swing.JPanel();
                ivjJPanelNameAddy.setName("JPanelNameAddy");
                ivjJPanelNameAddy.setLayout(new GridBagLayout());

                GridBagConstraints constraintsNameLabel = new GridBagConstraints();
                constraintsNameLabel.gridx = 1;
                constraintsNameLabel.gridy = 1;
                constraintsNameLabel.anchor = GridBagConstraints.WEST;
                constraintsNameLabel.insets = new Insets(15, 10, 0, 0);
                getJPanelNameAddy().add(getNameLabel(), constraintsNameLabel);

                GridBagConstraints constraintsPhysicalAddressLabel = new GridBagConstraints();
                constraintsPhysicalAddressLabel.gridx = 1;
                constraintsPhysicalAddressLabel.gridy = 2;
                constraintsPhysicalAddressLabel.anchor = GridBagConstraints.WEST;
                constraintsPhysicalAddressLabel.insets = new Insets(10, 10, 0, 0);
                getJPanelNameAddy().add(getPhysicalAddressLabel(), constraintsPhysicalAddressLabel);

                // Add create points check box
                GridBagConstraints constraintsCreatePoints = new GridBagConstraints();
                constraintsCreatePoints.gridx = 1;
                constraintsCreatePoints.gridy = 3;
                constraintsCreatePoints.anchor = GridBagConstraints.WEST;
                constraintsCreatePoints.insets = new Insets(10, 10, 0, 0);
                constraintsCreatePoints.weighty = 1.0;
                this.createPointsCheck = new JCheckBox("Create default points");
                this.createPointsCheck.setVisible(false);
                getJPanelNameAddy().add(this.createPointsCheck, constraintsCreatePoints);

                GridBagConstraints constraintsNameTextField = new GridBagConstraints();
                constraintsNameTextField.gridx = 2;
                constraintsNameTextField.gridy = 1;
                constraintsNameTextField.fill = GridBagConstraints.HORIZONTAL;
                constraintsNameTextField.anchor = java.awt.GridBagConstraints.WEST;
                constraintsNameTextField.weightx = 1.0;
                constraintsNameTextField.insets = new Insets(15, 0, 0, 20);
                getJPanelNameAddy().add(getNameTextField(), constraintsNameTextField);

                GridBagConstraints constraintsAddressTextField = new GridBagConstraints();
                constraintsAddressTextField.gridx = 2;
                constraintsAddressTextField.gridy = 2;
                constraintsAddressTextField.fill = GridBagConstraints.HORIZONTAL;
                constraintsAddressTextField.anchor = GridBagConstraints.WEST;
                constraintsAddressTextField.weightx = 1.0;
                constraintsAddressTextField.insets = new Insets(10, 0, 0, 20);
                getJPanelNameAddy().add(getAddressTextField(), constraintsAddressTextField);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJPanelNameAddy;
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    private javax.swing.JLabel getNameLabel() {
        if (ivjNameLabel == null) {
            try {
                ivjNameLabel = new javax.swing.JLabel();
                ivjNameLabel.setName("NameLabel");
                ivjNameLabel.setFont(new java.awt.Font("dialog", 0, 14));
                ivjNameLabel.setText("Device Name:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjNameLabel;
    }

    private javax.swing.JTextField getNameTextField() {
        if (ivjNameTextField == null) {
            try {
                ivjNameTextField = new javax.swing.JTextField();
                ivjNameTextField.setName("NameTextField");
                ivjNameTextField.setFont(new java.awt.Font("sansserif", 0, 14));
                ivjNameTextField.setColumns(12);

                ivjNameTextField.setDocument(new TextFieldDocument(TextFieldDocument.MAX_DEVICE_NAME_LENGTH, PaoUtils.ILLEGAL_NAME_CHARS));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjNameTextField;
    }

    private javax.swing.JLabel getPhysicalAddressLabel() {
        if (ivjPhysicalAddressLabel == null) {
            try {
                ivjPhysicalAddressLabel = new javax.swing.JLabel();
                ivjPhysicalAddressLabel.setName("PhysicalAddressLabel");
                ivjPhysicalAddressLabel.setFont(new java.awt.Font("dialog", 0, 14));
                ivjPhysicalAddressLabel.setText("Physical Address:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjPhysicalAddressLabel;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(350, 200);
    }

    @Override
    public Object getValue(Object val) {
        // The name is easy, the address is more difficult since at this point all devices have a physical address 
        // but it is tedious to tell what type of device it is - CommLine, Carrier, Repeater, MCT Broadcast group, etc

        deviceBase = (DeviceBase) val;

        String nameString = getNameTextField().getText();
        deviceBase.setPAOName(nameString);

        // Search for the correct sub-type and set the address

        Integer address = new Integer(getAddressTextField().getText());

        if (val instanceof IDLCBase) {
            ((IDLCBase) deviceBase).getDeviceIDLCRemote().setAddress(address);
        } else if (val instanceof CCU721) {
            ((CCU721) deviceBase).getDeviceAddress().setSlaveAddress(address);
        } else if (val instanceof IonBase) {
            ((IonBase) val).getDeviceAddress().setSlaveAddress(address);
        } else if (val instanceof DNPBase) {
            ((DNPBase) val).getDeviceAddress().setSlaveAddress(address);
        } else if (val instanceof RTCBase) {
            ((RTCBase) val).getDeviceRTC().setRTCAddress(address);
        } else if (val instanceof RTM) {
            ((RTM) val).getDeviceIED().setSlaveAddress(address.toString());
            ((RTM) val).getDeviceIED().setPassword(CtiUtilities.STRING_NONE);
        } else if (val instanceof Series5Base) {
            ((Series5Base) val).getSeries5().setSlaveAddress(address);
        } else if (val instanceof CarrierBase) {
            CarrierBase carrierBase = (CarrierBase) val;
            if (carrierBase instanceof Repeater900) {
                // special case, we must add ADDRESS_OFFSET to every address for Repeater900
                carrierBase.getDeviceCarrierSettings().setAddress(new Integer(address.intValue() + Repeater900.ADDRESS_OFFSET));
            } else if (val instanceof Repeater921) {
                // special case, we must add ADDRESS_OFFSET to every address for Repeater921
                carrierBase.getDeviceCarrierSettings().setAddress(new Integer(address.intValue() + Repeater921.ADDRESS_OFFSET));
            } else {
                carrierBase.getDeviceCarrierSettings().setAddress(address);
            }
        } else { // didn't find it
            throw new Error("Unable to determine device type when attempting to set the address");
        }

        PaoType deviceType = deviceBase.getPaoType();
        if (deviceType.isMct() || deviceType.isRepeater() || deviceType.isTwoWayRfnLcr() || deviceType.isTwoWayPlcLcr()) {

            // Check for unique address
            checkPaoAddresses(address.intValue());

            if (this.createPointsCheck.isSelected()) {
                PaoDao paoDao = YukonSpringHook.getBean(PaoDao.class);
                deviceBase.setDeviceID(paoDao.getNextPaoId());

                PaoDefinitionService paoDefinitionService = YukonSpringHook.getBean(PaoDefinitionService.class);
                DeviceDao deviceDao = YukonSpringHook.getBean(DeviceDao.class);
                SimpleDevice yukonDevice = deviceDao.getYukonDeviceForDevice(deviceBase);
                List<PointBase> defaultPoints = paoDefinitionService.createDefaultPointsForPao(yukonDevice);

                SmartMultiDBPersistent persistant = new SmartMultiDBPersistent();
                persistant.addOwnerDBPersistent(deviceBase);
                for (PointBase point : defaultPoints) {
                    persistant.addDBPersistent(point);
                }
                return persistant;
            }
        }

        return val;
    }

    /**
     * Called whenever the part throws an exception.
     */
    private void handleException(Throwable exception) {
        com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
        com.cannontech.clientutils.CTILogger.error(exception.getMessage(), exception);
    }

    /**
     * Initializes connections
     */
    private void initConnections() throws java.lang.Exception {
        getNameTextField().addCaretListener(this);
        getAddressTextField().addCaretListener(this);
    }

    /**
     * Initialize the class.
     */
    private void initialize() {
        try {
            setName("DeviceNameAddressPanel");
            setLayout(new java.awt.GridBagLayout());
            setSize(350, 357);

            GridBagConstraints constraintsJPanelNameAddy = new GridBagConstraints();
            constraintsJPanelNameAddy.gridx = 1;
            constraintsJPanelNameAddy.gridy = 1;
            constraintsJPanelNameAddy.fill = GridBagConstraints.HORIZONTAL;
            constraintsJPanelNameAddy.anchor = GridBagConstraints.WEST;
            constraintsJPanelNameAddy.weightx = 1.0;
            constraintsJPanelNameAddy.insets = new Insets(0, 0, 0, 0);
            add(getJPanelNameAddy(), constraintsJPanelNameAddy);

            GridBagConstraints constraintsJPanel1 = new GridBagConstraints();
            constraintsJPanel1.gridx = 1;
            constraintsJPanel1.gridy = 2;
            constraintsJPanel1.fill = GridBagConstraints.HORIZONTAL;
            constraintsJPanel1.anchor = GridBagConstraints.WEST;
            constraintsJPanel1.weightx = 1.0;
            constraintsJPanel1.weighty = 1.0;
            constraintsJPanel1.insets = new Insets(2, 4, 202, 4);
            add(getJPanel1(), constraintsJPanel1);
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    @Override
    public boolean isInputValid() {
        String deviceName = getNameTextField().getText();
        if (StringUtils.isBlank(deviceName)) {
            setErrorString("The Name text field must be filled in");
            return false;
        }
        
        if (StringUtils.isBlank(getAddress())) {
            setErrorString("The Address text field must be filled in");
            return false;
        }
        
        int address = Integer.parseInt(getAddress());
        PaoType paoType = deviceBase.getPaoType();
        if (!dlcAddressRangeService.isValidEnforcedAddress(paoType, address)) {
            
            String rangeString = dlcAddressRangeService.rangeString(paoType);
            setErrorString("Invalid address. Device address range: " + rangeString);
            getJLabelErrorMessage().setText("(" + getErrorString() + ")");
            getJLabelErrorMessage().setToolTipText("(" + getErrorString() + ")");
            getJLabelErrorMessage().setVisible(true);
            
            return false;
        }
        
        if (!isUniquePao(deviceName, deviceBase.getPaoType())) {
            setErrorString("Name '" + deviceName + "' is already in use.");
            getJLabelErrorMessage().setText("(" + getErrorString() + ")");
            getJLabelErrorMessage().setToolTipText("(" + getErrorString() + ")");
            getJLabelErrorMessage().setVisible(true);
            return false;
        }
        
        getJLabelErrorMessage().setText("");
        getJLabelErrorMessage().setToolTipText("");
        getJLabelErrorMessage().setVisible(false);
        return true;
    }

    public void setDeviceType(PaoType deviceType) {
        deviceBase = DeviceFactory.createDevice(deviceType);
        if (deviceType == PaoType.RTU_DART)
            getPhysicalAddressLabel().setText("Master Address:");
        else if (deviceType.isIon() ||
                deviceType == PaoType.RTU_MODBUS ||
                deviceType == PaoType.RTU_DNP  ||
                deviceType == PaoType.CCU721)
            getPhysicalAddressLabel().setText("Slave Address:");
        else if (deviceType == PaoType.MCTBROADCAST)
            getPhysicalAddressLabel().setText("Lead Meter Address:");
        else if (deviceType == PaoType.SERIES_5_LMI)
            getPhysicalAddressLabel().setText("Address:");
        else if (deviceType.isTwoWayLcr())
            getPhysicalAddressLabel().setText("Serial Number:");
        else
            getPhysicalAddressLabel().setText("Physical Address:");

        if (deviceType.isMct() || 
                deviceType.isRepeater()  || 
                deviceType.isCcu() || 
                deviceType.isTwoWayLcr()) {
            this.createPointsCheck.setVisible(true);
            this.createPointsCheck.setSelected(true);
        } else {
            this.createPointsCheck.setVisible(false);
            this.createPointsCheck.setSelected(false);
        }

    }

    @Override
    public void setValue(Object val) {
        return;
    }

    @Override
    public void setFirstFocus() {
        // Make sure that when its time to display this panel, the focus starts in the top component
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                getNameTextField().requestFocus();
            }
        });
    }
}