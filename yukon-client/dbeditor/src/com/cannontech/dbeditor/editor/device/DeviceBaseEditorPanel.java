package com.cannontech.dbeditor.editor.device;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.DNPConfiguration;
import com.cannontech.common.device.config.model.DeviceConfiguration;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.gui.unchanging.DoubleRangeDocument;
import com.cannontech.common.gui.unchanging.LongRangeDocument;
import com.cannontech.common.gui.util.AdvancedPropertiesDialog;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.JTextFieldComboEditor;
import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.common.gui.util.TitleBorder;
import com.cannontech.common.model.PaoProperty;
import com.cannontech.common.model.PaoPropertyName;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SwingUtil;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoPropertyDao;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.device.AddressBase;
import com.cannontech.database.data.device.CCU721;
import com.cannontech.database.data.device.CarrierBase;
import com.cannontech.database.data.device.DNPBase;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.GridAdvisorBase;
import com.cannontech.database.data.device.IDLCBase;
import com.cannontech.database.data.device.IEDBase;
import com.cannontech.database.data.device.IonBase;
import com.cannontech.database.data.device.KV;
import com.cannontech.database.data.device.MCT400SeriesBase;
import com.cannontech.database.data.device.MCTBase;
import com.cannontech.database.data.device.RTCBase;
import com.cannontech.database.data.device.RTM;
import com.cannontech.database.data.device.RemoteBase;
import com.cannontech.database.data.device.Repeater900;
import com.cannontech.database.data.device.Repeater921;
import com.cannontech.database.data.device.RfnBase;
import com.cannontech.database.data.device.SNPPTerminal;
import com.cannontech.database.data.device.Schlumberger;
import com.cannontech.database.data.device.Series5Base;
import com.cannontech.database.data.device.Sixnet;
import com.cannontech.database.data.device.TapTerminalBase;
import com.cannontech.database.data.device.TwoWayLCR;
import com.cannontech.database.data.device.WCTPTerminal;
import com.cannontech.database.data.device.lm.LMGroupEmetcon;
import com.cannontech.database.data.device.lm.LMGroupRipple;
import com.cannontech.database.data.device.lm.LMGroupVersacom;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.device.DeviceCarrierSettings;
import com.cannontech.database.db.device.DeviceDialupSettings;
import com.cannontech.database.db.device.DeviceDirectCommSettings;
import com.cannontech.database.db.device.DeviceIDLCRemote;
import com.cannontech.dbeditor.DatabaseEditorOptionPane;
import com.cannontech.dbeditor.DatabaseEditorUtil;
import com.cannontech.device.range.DlcAddressRangeService;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.klg.jclass.field.DataProperties;
import com.klg.jclass.field.JCInvalidInfo;
import com.klg.jclass.field.JCSpinField;
import com.klg.jclass.field.validate.JCIntegerValidator;
import com.klg.jclass.util.value.JCValueEvent;
import com.klg.jclass.util.value.JCValueListener;
import com.klg.jclass.util.value.MutableValueModel;

public class DeviceBaseEditorPanel extends DataInputPanel {

    private DeviceBase deviceBase = null;
    private DeviceAdvancedDialupEditorPanel advancedPanel = null;
    private JCheckBox ivjControlInhibitCheckBox = null;
    private JCheckBox ivjDisableFlagCheckBox = null;
    private JLabel ivjNameLabel = null;
    private JTextField ivjNameTextField = null;
    private JLabel ivjTypeLabel = null;
    private JLabel ivjPhysicalAddressLabel = null;
    private JTextField ivjPhysicalAddressTextField = null;
    private JLabel ivjTypeTextField = null;
    private JPanel ivjIdentificationPanel = null;
    private JComboBox<LiteYukonPAObject> ivjRouteComboBox = null;
    private JPanel ivjDialupSettingsPanel = null;
    private JLabel ivjPhoneNumberLabel = null;
    private JTextField ivjPhoneNumberTextField = null;
    private JComboBox<LiteYukonPAObject> ivjPortComboBox = null;
    private JLabel ivjPortLabel = null;
    private JLabel ivjPostCommWaitLabel = null;
    private com.klg.jclass.field.JCSpinField ivjPostCommWaitSpinner = null;
    private JLabel ivjWaitLabel = null;
    private JLabel ivjRouteLabel = null;
    private JLabel ivjPasswordLabel = null;
    private JTextField ivjPasswordTextField = null;
    private JComboBox<Comparable> ivjSlaveAddressComboBox = null;
    private JLabel ivjSlaveAddressLabel = null;
    private JPanel communicationPanel = null;
    private JButton ivjJButtonAdvanced = null;
    private JComboBox<String> ivjJComboBoxAmpUseType = null;
    private JLabel ivjJLabelCCUAmpUseType = null;
    EventHandler eventHandler = new EventHandler();
    private JLabel ivjMctConfigLabel = null;
    private JLabel assignedMctConfigLabel = null;
    private JComboBox<String> ivjTOUComboBox = null;
    private JLabel ivjTOULabel = null;
    private JLabel ivjSecurityCodeLabel = null;
    private JTextField ivjSecurityCodeTextField = null;
    private JLabel ivjSenderLabel = null;
    private JTextField ivjSenderTextField = null;

    private JLabel tcpIpAddressLabel = null;
    private JTextField tcpIpAddressTextField = null;
    private JLabel tcpPortLabel = null;
    private JTextField tcpPortTextField = null;

    private PaoType deviceType;

    private JLabel internalRetriesLabel = null;
    private JLabel internalRetriesValueLabel = null;
    private JLabel timeOffsetLabel = null;
    private JLabel timeOffsetValueLabel = null;
    private JLabel timesyncLabel = null;
    private JLabel timesyncValueLabel = null;
    private JLabel omitTimeRequestLabel = null;
    private JLabel omitTimeRequestValueLabel = null;
    private JLabel unsolicitedLabel = null;
    private JLabel unsolicitedValueLabel = null;
    private JLabel dnpConfigLabel = null;
    private JLabel assignedDnpConfigLabel = null;

    private JPanel jPanelMCTSettings = null;
    private JPanel dnpConfigPanel = null;
    private JTextField serialNumberTextField;
    private JTextField manufacturerTextField;
    private JTextField modelTextField;
    private JLabel serialNumberLabel;
    private JLabel manufacturerLabel;
    private JLabel modelLabel;

    private final DlcAddressRangeService dlcAddressRangeService =
        YukonSpringHook.getBean("dlcAddressRangeService", DlcAddressRangeService.class);

    class EventHandler implements ActionListener, CaretListener, JCValueListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if (source == getDisableFlagCheckBox() ||
                source == getControlInhibitCheckBox() ||
                source == getSlaveAddressComboBox() ||
                source == getJComboBoxAmpUseType() ||
                source == getTOUComboBox() ||
                source == getRouteComboBox()) {
                fireInputUpdate();
            }

            if (source == getPortComboBox()) {
                updatePortSettings(e);
            }

            if (source == getJButtonAdvanced()) {
                advancedButtonActionPerformed(e);
            }
        }

        @Override
        public void caretUpdate(CaretEvent e) {
            Object source = e.getSource();
            if (source == getNameTextField() ||
                source == getPhysicalAddressTextField() ||
                source == getPhoneNumberTextField() ||
                source == getPasswordTextField() ||
                source == getSenderTextField() ||
                source == getSecurityCodeTextField() ||
                source == getTcpIpAddressTextField() ||
                source == getTcpPortTextField() ||
                source == getSerialNumberTextField() ||
                source == getManufacturerTextField() ||
                source == getModelTextField() ||
                source instanceof JTextFieldComboEditor) {
                fireInputUpdate();
            }
        }

        @Override
        public void valueChanged(JCValueEvent arg1) {
            if (arg1.getSource() == getPostCommWaitSpinner()) {
                fireInputUpdate();
            }
        }

        @Override
        public void valueChanging(JCValueEvent arg1) {
            // ignore
        }
    }

    public DeviceBaseEditorPanel() {
        super();
        initialize();
    }

    /**
     * Helper method to check for duplicate address
     * @param address - Address in question
     * @param portID - id of port to check addresses for
     * @return True if address is unique
     */
    private boolean checkForDuplicateAddresses(int address, Integer paobjectId, Integer portId) {
        String[] devices = DeviceIDLCRemote.isAddressUnique(address, paobjectId, portId);

        if (devices.length > 0) {
            String devStr = new String();
            for (int i = 0; i < devices.length; i++) {
                devStr += "          " + devices[i] + "\n";
            }

            JOptionPane
                .showMessageDialog(this,
                                   "The address '"
                                           + address
                                           + "' is already in use by the following CCUs or RTUs: \n"
                                           + devStr
                                           + "\nCCUs and/or RTUs cannot have duplicate addresses on a dedicated comm channel.",
                                   "Address Already Used",
                                   JOptionPane.WARNING_MESSAGE);

            setErrorString(null);
            return false;
        }

        return true;
    }

    /**
     * Helper method to check that an address for an mct is unique
     * @param address - Address to check
     * @return True if unique
     */
    private boolean checkMCTAddresses(int address, Integer paobjectId) {
        String[] devices = DeviceCarrierSettings.isAddressUnique(address, paobjectId);

        if (devices.length > 0) {

            String message = "The address '"
                             + address
                             + "' is already used by the following devices,\n"
                             + "are you sure you want to use it again?\n";

            int res = DatabaseEditorOptionPane.showAlreadyUsedConfirmDialog(this,
                                                                            message,
                                                                            "Address Already Used",
                                                                            devices);
            if (res == JOptionPane.NO_OPTION) {
                setErrorString(null);
                return false;
            }
        }

        return true;
    }

    public void configComboBox_ActionPerformed(ActionEvent actionEvent) {
        this.fireInputUpdate();
        return;
    }

    private void updatePortSettings(ActionEvent arg1) {
        fireInputUpdate();
        PaoType paoType = ((LiteYukonPAObject) getPortComboBox().getSelectedItem()).getPaoType();
        getDialupSettingsPanel().setVisible(paoType.isDialupPort());

        boolean tcpport = (paoType == PaoType.TCPPORT) && (deviceType.isTcpPortEligible());

        getTcpIpAddressTextField().setVisible(tcpport);
        getTcpIpAddressLabel().setVisible(tcpport);
        getTcpPortTextField().setVisible(tcpport);
        getTcpPortLabel().setVisible(tcpport);

        revalidate();
        repaint();
    }

    private DeviceAdvancedDialupEditorPanel getAdvancedPanel() {
        if (advancedPanel == null) {
            advancedPanel = new DeviceAdvancedDialupEditorPanel();
        }

        return advancedPanel;
    }

    private JPanel getCommunicationPanel() {
        if (communicationPanel == null) {
            TitleBorder ivjLocalBorder1 = new TitleBorder();
            ivjLocalBorder1.setTitleFont(new Font("Dialog", Font.BOLD, 12));
            ivjLocalBorder1.setTitle("Communication");
            communicationPanel = new JPanel();

            communicationPanel.setName("CommunicationPanel");
            communicationPanel.setBorder(ivjLocalBorder1);
            communicationPanel.setLayout(new GridBagLayout());

            GridBagConstraints routeComboBoxConstraint = new GridBagConstraints();
            GridBagConstraints portLabelConstraint = new GridBagConstraints();
            GridBagConstraints routeLabelConstraint = new GridBagConstraints();
            GridBagConstraints portComboBoxConstraint = new GridBagConstraints();
            GridBagConstraints postCommWaitSpinnerConstraint = new GridBagConstraints();
            GridBagConstraints postCommWaitLabelConstraint = new GridBagConstraints();
            GridBagConstraints waitLabelConstraint = new GridBagConstraints();
            GridBagConstraints passwordLabelConstraint = new GridBagConstraints();
            GridBagConstraints passwordTextFieldConstraint = new GridBagConstraints();
            GridBagConstraints dialupSettingsPanelConstraint = new GridBagConstraints();
            GridBagConstraints slaveAddressComboBoxConstraint = new GridBagConstraints();
            GridBagConstraints slaveAddressLabelConstraint = new GridBagConstraints();
            GridBagConstraints ampUseTypeComboBoxConstraint = new GridBagConstraints();
            GridBagConstraints senderLabelConstraint = new GridBagConstraints();
            GridBagConstraints ccuAmpUseTypeLabelConstraint = new GridBagConstraints();
            GridBagConstraints senderTextFieldConstraint = new GridBagConstraints();
            GridBagConstraints securityCodeTextFieldConstraint = new GridBagConstraints();
            GridBagConstraints securityCodeLabelConstraint = new GridBagConstraints();
            GridBagConstraints tcpIpAddressLabelContraint = new GridBagConstraints();
            GridBagConstraints tcpIpAdressTextFieldConstraint = new GridBagConstraints();
            GridBagConstraints tcpPortLabelConstraint = new GridBagConstraints();
            GridBagConstraints tcpPortTextFieldConstraint = new GridBagConstraints();

            waitLabelConstraint.insets = new Insets(3, 3, 3, 3);
            waitLabelConstraint.gridy = 2;
            waitLabelConstraint.gridx = 2;
            communicationPanel.add(getWaitLabel(), waitLabelConstraint);

            passwordLabelConstraint.insets = new Insets(3, 3, 3, 3);
            passwordLabelConstraint.gridy = 3;
            passwordLabelConstraint.gridx = 0;
            communicationPanel.add(getPasswordLabel(), passwordLabelConstraint);

            portLabelConstraint.insets = new Insets(3, 3, 3, 3);
            portLabelConstraint.gridy = 1;
            portLabelConstraint.gridx = 0;
            portLabelConstraint.anchor = GridBagConstraints.NORTHWEST;
            communicationPanel.add(getPortLabel(), portLabelConstraint);

            portComboBoxConstraint.insets = new Insets(3, 3, 3, 3);
            portComboBoxConstraint.fill = GridBagConstraints.HORIZONTAL;
            portComboBoxConstraint.weightx = 1.0;
            portComboBoxConstraint.gridwidth = 2;
            portComboBoxConstraint.gridy = 1;
            portComboBoxConstraint.gridx = 1;
            portComboBoxConstraint.anchor = GridBagConstraints.NORTHWEST;
            communicationPanel.add(getPortComboBox(), portComboBoxConstraint);

            postCommWaitLabelConstraint.insets = new Insets(3, 3, 3, 3);
            postCommWaitLabelConstraint.gridy = 2;
            postCommWaitLabelConstraint.gridx = 0;
            communicationPanel.add(getPostCommWaitLabel(), postCommWaitLabelConstraint);

            postCommWaitSpinnerConstraint.insets = new Insets(3, 3, 3, 3);
            postCommWaitSpinnerConstraint.gridy = 2;
            postCommWaitSpinnerConstraint.gridx = 1;
            communicationPanel.add(getPostCommWaitSpinner(), postCommWaitSpinnerConstraint);

            routeLabelConstraint.insets = new Insets(3, 3, 3, 3);
            routeLabelConstraint.anchor = GridBagConstraints.NORTHWEST;
            routeLabelConstraint.gridy = 0;
            routeLabelConstraint.gridx = 0;
            communicationPanel.add(getRouteLabel(), routeLabelConstraint);

            routeComboBoxConstraint.insets = new Insets(3, 3, 3, 3);
            routeComboBoxConstraint.fill = GridBagConstraints.HORIZONTAL;
            routeComboBoxConstraint.weightx = 1.0;
            routeComboBoxConstraint.gridwidth = 2;
            routeComboBoxConstraint.gridy = 1;
            routeComboBoxConstraint.gridx = 0;
            routeComboBoxConstraint.anchor = GridBagConstraints.NORTHWEST;
            communicationPanel.add(getRouteComboBox(), routeComboBoxConstraint);

            passwordTextFieldConstraint.insets = new Insets(3, 3, 3, 3);
            passwordTextFieldConstraint.fill = GridBagConstraints.HORIZONTAL;
            passwordTextFieldConstraint.weightx = 1.0;
            passwordTextFieldConstraint.gridwidth = 2;
            passwordTextFieldConstraint.gridy = 3;
            passwordTextFieldConstraint.gridx = 1;
            communicationPanel.add(getPasswordTextField(), passwordTextFieldConstraint);

            slaveAddressComboBoxConstraint.insets = new Insets(3, 3, 3, 3);
            slaveAddressComboBoxConstraint.fill = GridBagConstraints.HORIZONTAL;
            slaveAddressComboBoxConstraint.weightx = 1.0;
            slaveAddressComboBoxConstraint.gridwidth = 2;
            slaveAddressComboBoxConstraint.gridy = 5;
            slaveAddressComboBoxConstraint.gridx = 1;
            communicationPanel.add(getSlaveAddressComboBox(), slaveAddressComboBoxConstraint);

            senderLabelConstraint.insets = new Insets(3, 3, 3, 3);
            senderLabelConstraint.gridy = 6;
            senderLabelConstraint.gridx = 0;
            senderLabelConstraint.anchor = GridBagConstraints.NORTHWEST;
            communicationPanel.add(getSenderLabel(), senderLabelConstraint);

            dialupSettingsPanelConstraint.insets = new Insets(3, 3, 3, 3);
            dialupSettingsPanelConstraint.gridwidth = 3;
            dialupSettingsPanelConstraint.gridy = 8;
            dialupSettingsPanelConstraint.gridx = 0;
            communicationPanel.add(getDialupSettingsPanel(), dialupSettingsPanelConstraint);

            slaveAddressLabelConstraint.insets = new Insets(3, 3, 3, 3);
            slaveAddressLabelConstraint.gridy = 5;
            slaveAddressLabelConstraint.gridx = 0;
            communicationPanel.add(getSlaveAddressLabel(), slaveAddressLabelConstraint);

            ampUseTypeComboBoxConstraint.insets = new Insets(3, 3, 3, 3);
            ampUseTypeComboBoxConstraint.fill = GridBagConstraints.HORIZONTAL;
            ampUseTypeComboBoxConstraint.weightx = 1.0;
            ampUseTypeComboBoxConstraint.gridwidth = 2;
            ampUseTypeComboBoxConstraint.gridy = 4;
            ampUseTypeComboBoxConstraint.gridx = 1;
            communicationPanel.add(getJComboBoxAmpUseType(), ampUseTypeComboBoxConstraint);

            senderTextFieldConstraint.insets = new Insets(3, 3, 3, 3);
            senderTextFieldConstraint.fill = GridBagConstraints.HORIZONTAL;
            senderTextFieldConstraint.weightx = 1.0;
            senderTextFieldConstraint.gridwidth = 2;
            senderTextFieldConstraint.gridy = 6;
            senderTextFieldConstraint.gridx = 1;
            senderTextFieldConstraint.anchor = GridBagConstraints.NORTHWEST;
            communicationPanel.add(getSenderTextField(), senderTextFieldConstraint);

            securityCodeTextFieldConstraint.insets = new Insets(3, 3, 3, 3);
            securityCodeTextFieldConstraint.fill = GridBagConstraints.HORIZONTAL;
            securityCodeTextFieldConstraint.weightx = 1.0;
            securityCodeTextFieldConstraint.gridwidth = 2;
            securityCodeTextFieldConstraint.gridy = 7;
            securityCodeTextFieldConstraint.gridx = 1;
            securityCodeTextFieldConstraint.anchor = GridBagConstraints.NORTHWEST;
            communicationPanel.add(getSecurityCodeTextField(), securityCodeTextFieldConstraint);

            securityCodeLabelConstraint.insets = new Insets(3, 3, 3, 3);
            securityCodeLabelConstraint.gridy = 7;
            securityCodeLabelConstraint.gridx = 0;
            securityCodeLabelConstraint.anchor = GridBagConstraints.NORTHWEST;
            communicationPanel.add(getSecurityCodeLabel(), securityCodeLabelConstraint);

            ccuAmpUseTypeLabelConstraint.insets = new Insets(3, 3, 3, 3);
            ccuAmpUseTypeLabelConstraint.gridy = 4;
            ccuAmpUseTypeLabelConstraint.gridx = 0;
            communicationPanel.add(getJLabelCCUAmpUseType(), ccuAmpUseTypeLabelConstraint);

            tcpIpAddressLabelContraint.insets = new Insets(3, 3, 3, 3);
            tcpIpAddressLabelContraint.gridy = 6;
            tcpIpAddressLabelContraint.gridx = 0;
            communicationPanel.add(getTcpIpAddressLabel(), tcpIpAddressLabelContraint);

            tcpIpAdressTextFieldConstraint.insets = new Insets(3, 3, 3, 3);
            tcpIpAdressTextFieldConstraint.gridy = 6;
            tcpIpAdressTextFieldConstraint.gridx = 1;
            communicationPanel.add(getTcpIpAddressTextField(), tcpIpAdressTextFieldConstraint);

            tcpPortLabelConstraint.insets = new Insets(3, 3, 3, 3);
            tcpPortLabelConstraint.gridy = 7;
            tcpPortLabelConstraint.gridx = 0;
            communicationPanel.add(getTcpPortLabel(), tcpPortLabelConstraint);

            tcpPortTextFieldConstraint.insets = new Insets(3, 3, 3, 3);
            tcpPortTextFieldConstraint.gridy = 7;
            tcpPortTextFieldConstraint.gridx = 1;
            communicationPanel.add(getTcpPortTextField(), tcpPortTextFieldConstraint);
        }
        return communicationPanel;
    }

    private JPanel getDnpConfigPanel() {
        if (dnpConfigPanel == null) {
            dnpConfigPanel = new JPanel();

            dnpConfigPanel.setBorder(BorderFactory
                .createTitledBorder(null,
                                    "DNP Additional Settings",
                                    TitledBorder.DEFAULT_JUSTIFICATION,
                                    TitledBorder.DEFAULT_POSITION,
                                    new Font("Dialog", Font.BOLD, 12),
                                    Color.black));
            dnpConfigPanel.setName("DNPConfigPanel");
            dnpConfigPanel.setLayout(new GridBagLayout());
            dnpConfigPanel.setVisible(false);

            GridBagConstraints configLabelConstraints = new GridBagConstraints();
            GridBagConstraints assignedConfigLabelConstraints = new GridBagConstraints();
            GridBagConstraints retriesLabelConstraints = new GridBagConstraints();
            GridBagConstraints retriesValueLabelConstraints = new GridBagConstraints();
            GridBagConstraints timeOffsetLabelConstraints = new GridBagConstraints();
            GridBagConstraints timeOffsetValueLabelConstraints = new GridBagConstraints();
            GridBagConstraints timesyncLabelConstraints = new GridBagConstraints();
            GridBagConstraints timesyncValueLabelConstraints = new GridBagConstraints();
            GridBagConstraints omitTimeRequestLabelConstraints = new GridBagConstraints();
            GridBagConstraints omitTimeRequestValueLabelConstraints = new GridBagConstraints();
            GridBagConstraints unsolicitedLabelConstraints = new GridBagConstraints();
            GridBagConstraints unsolicitedValueLabelConstraints = new GridBagConstraints();

            configLabelConstraints.insets = new Insets(3, 3, 3, 3);
            configLabelConstraints.gridy = 0;
            configLabelConstraints.gridx = 0;
            configLabelConstraints.anchor = GridBagConstraints.NORTHWEST;

            assignedConfigLabelConstraints.insets = new Insets(3, 3, 3, 3);
            assignedConfigLabelConstraints.fill = GridBagConstraints.HORIZONTAL;
            assignedConfigLabelConstraints.gridy = 0;
            assignedConfigLabelConstraints.gridx = 1;
            assignedConfigLabelConstraints.anchor = GridBagConstraints.NORTHWEST;

            retriesLabelConstraints.insets = new Insets(3, 3, 3, 3);
            retriesLabelConstraints.gridy = 1;
            retriesLabelConstraints.gridx = 0;
            retriesLabelConstraints.anchor = GridBagConstraints.NORTHWEST;

            retriesValueLabelConstraints.insets = new Insets(3, 3, 3, 3);
            retriesValueLabelConstraints.fill = GridBagConstraints.HORIZONTAL;
            retriesValueLabelConstraints.gridy = 1;
            retriesValueLabelConstraints.gridx = 1;
            retriesValueLabelConstraints.anchor = GridBagConstraints.NORTHWEST;

            timeOffsetLabelConstraints.insets = new Insets(3, 3, 3, 3);
            timeOffsetLabelConstraints.gridy = 2;
            timeOffsetLabelConstraints.gridx = 0;
            timeOffsetLabelConstraints.anchor = GridBagConstraints.NORTHWEST;

            timeOffsetValueLabelConstraints.insets = new Insets(3, 3, 3, 3);
            timeOffsetValueLabelConstraints.fill = GridBagConstraints.HORIZONTAL;
            timeOffsetValueLabelConstraints.gridy = 2;
            timeOffsetValueLabelConstraints.gridx = 1;
            timeOffsetValueLabelConstraints.anchor = GridBagConstraints.NORTHWEST;

            timesyncLabelConstraints.insets = new Insets(3, 3, 3, 3);
            timesyncLabelConstraints.gridy = 3;
            timesyncLabelConstraints.gridx = 0;
            timesyncLabelConstraints.anchor = GridBagConstraints.NORTHWEST;

            timesyncValueLabelConstraints.insets = new Insets(3, 3, 3, 3);
            timesyncValueLabelConstraints.fill = GridBagConstraints.HORIZONTAL;
            timesyncValueLabelConstraints.gridy = 3;
            timesyncValueLabelConstraints.gridx = 1;
            timesyncValueLabelConstraints.anchor = GridBagConstraints.NORTHWEST;

            omitTimeRequestLabelConstraints.insets = new Insets(3, 3, 3, 3);
            omitTimeRequestLabelConstraints.gridy = 4;
            omitTimeRequestLabelConstraints.gridx = 0;
            omitTimeRequestLabelConstraints.anchor = GridBagConstraints.NORTHWEST;

            omitTimeRequestValueLabelConstraints.insets = new Insets(3, 3, 3, 3);
            omitTimeRequestValueLabelConstraints.fill = GridBagConstraints.HORIZONTAL;
            omitTimeRequestValueLabelConstraints.gridy = 4;
            omitTimeRequestValueLabelConstraints.gridx = 1;
            omitTimeRequestValueLabelConstraints.anchor = GridBagConstraints.NORTHWEST;

            unsolicitedLabelConstraints.insets = new Insets(3, 3, 3, 3);
            unsolicitedLabelConstraints.gridy = 5;
            unsolicitedLabelConstraints.gridx = 0;
            unsolicitedLabelConstraints.weightx = 1.0;
            unsolicitedLabelConstraints.anchor = GridBagConstraints.NORTHWEST;

            unsolicitedValueLabelConstraints.insets = new Insets(3, 3, 3, 3);
            unsolicitedValueLabelConstraints.fill = GridBagConstraints.HORIZONTAL;
            unsolicitedValueLabelConstraints.gridy = 5;
            unsolicitedValueLabelConstraints.gridx = 1;
            unsolicitedValueLabelConstraints.anchor = GridBagConstraints.NORTHWEST;

            dnpConfigPanel.add(getDnpConfigLabel(), configLabelConstraints);
            dnpConfigPanel.add(getAssignedDnpConfigLabel(), assignedConfigLabelConstraints);
            dnpConfigPanel.add(getInternalRetriesLabel(), retriesLabelConstraints);
            dnpConfigPanel.add(getInternalRetriesValueLabel(), retriesValueLabelConstraints);
            dnpConfigPanel.add(getTimeOffsetLabel(), timeOffsetLabelConstraints);
            dnpConfigPanel.add(getTimeOffsetValueLabel(), timeOffsetValueLabelConstraints);
            dnpConfigPanel.add(getTimesyncLabel(), timesyncLabelConstraints);
            dnpConfigPanel.add(getTimesyncValueLabel(), timesyncValueLabelConstraints);
            dnpConfigPanel.add(getOmitTimeRequestLabel(), omitTimeRequestLabelConstraints);
            dnpConfigPanel
                .add(getOmitTimeRequestValueLabel(), omitTimeRequestValueLabelConstraints);
            dnpConfigPanel.add(getUnsolicitedLabel(), unsolicitedLabelConstraints);
            dnpConfigPanel.add(getUnsolicitedValueLabel(), unsolicitedValueLabelConstraints);
        }

        return dnpConfigPanel;
    }

    /**
     * Return the ConfigLabel property value.
     */
    private JLabel getMctConfigLabel() {
        if (ivjMctConfigLabel == null) {
            try {
                ivjMctConfigLabel = new JLabel();
                ivjMctConfigLabel.setName("MctConfigLabel");
                ivjMctConfigLabel.setFont(new Font("dialog", 0, 14));
                ivjMctConfigLabel.setText("MCT Config: ");
                ivjMctConfigLabel.setVisible(true);
                ivjMctConfigLabel.setPreferredSize(new Dimension(172, 19));
                ivjMctConfigLabel.setMaximumSize(new Dimension(172, 19));
                ivjMctConfigLabel.setMinimumSize(new Dimension(172, 19));
                ivjMctConfigLabel.setFont(new Font("Arial", 0, 14));
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjMctConfigLabel;
    }

    private JLabel getAssignedMctConfigLabel() {
        if (assignedMctConfigLabel == null) {
            assignedMctConfigLabel = new JLabel();
            assignedMctConfigLabel.setFont(new Font("dialog", 0, 14));
            assignedMctConfigLabel.setText(CtiUtilities.STRING_NONE);
            assignedMctConfigLabel.setPreferredSize(new Dimension(172, 19));
            assignedMctConfigLabel.setMaximumSize(new Dimension(172, 19));
            assignedMctConfigLabel.setMinimumSize(new Dimension(172, 19));
            assignedMctConfigLabel.setFont(new Font("Arial", 0, 14));
        }
        return assignedMctConfigLabel;
    }

    private JLabel getAssignedDnpConfigLabel() {
        if (assignedDnpConfigLabel == null) {
            assignedDnpConfigLabel = new JLabel();
            assignedDnpConfigLabel.setFont(new Font("dialog", 0, 14));
            assignedDnpConfigLabel.setText(CtiUtilities.STRING_NONE);
            assignedDnpConfigLabel.setPreferredSize(new Dimension(172, 19));
            assignedDnpConfigLabel.setMaximumSize(new Dimension(172, 19));
            assignedDnpConfigLabel.setMinimumSize(new Dimension(172, 19));
            assignedDnpConfigLabel.setFont(new Font("Arial", 0, 14));
        }
        return assignedDnpConfigLabel;
    }

    private JLabel getDnpConfigLabel() {
        if (dnpConfigLabel == null) {
            try {
                dnpConfigLabel = new JLabel();
                dnpConfigLabel.setName("DnpConfigLabel");
                dnpConfigLabel.setFont(new Font("dialog", 0, 14));
                dnpConfigLabel.setText("DNP Config: ");
                dnpConfigLabel.setVisible(true);
                dnpConfigLabel.setPreferredSize(new Dimension(172, 19));
                dnpConfigLabel.setMaximumSize(new Dimension(172, 19));
                dnpConfigLabel.setMinimumSize(new Dimension(172, 19));
                dnpConfigLabel.setFont(new Font("Arial", 0, 14));
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return dnpConfigLabel;
    }

    private JLabel getInternalRetriesLabel() {
        if (internalRetriesLabel == null) {
            internalRetriesLabel = new JLabel();
            internalRetriesLabel.setName("InternalRetriesLabel");
            internalRetriesLabel.setFont(new Font("dialog", 0, 14));
            internalRetriesLabel.setText("Internal Retries: ");
            internalRetriesLabel.setVisible(true);
            internalRetriesLabel.setPreferredSize(new Dimension(172, 19));
            internalRetriesLabel.setMaximumSize(new Dimension(172, 19));
            internalRetriesLabel.setMinimumSize(new Dimension(172, 19));
            internalRetriesLabel.setFont(new Font("Arial", 0, 14));
        }

        return internalRetriesLabel;
    }

    private JLabel getInternalRetriesValueLabel() {
        if (internalRetriesValueLabel == null) {
            internalRetriesValueLabel = new JLabel();
            internalRetriesValueLabel.setFont(new Font("dialog", 0, 14));
            internalRetriesValueLabel.setText(CtiUtilities.STRING_NONE);
            internalRetriesValueLabel.setPreferredSize(new Dimension(172, 19));
            internalRetriesValueLabel.setMaximumSize(new Dimension(172, 19));
            internalRetriesValueLabel.setMinimumSize(new Dimension(172, 19));
            internalRetriesValueLabel.setFont(new Font("Arial", 0, 14));
        }

        return internalRetriesValueLabel;
    }

    private JLabel getTimeOffsetLabel() {
        if (timeOffsetLabel == null) {
            timeOffsetLabel = new JLabel();
            timeOffsetLabel.setName("TimeOffsetLabel");
            timeOffsetLabel.setFont(new Font("dialog", 0, 14));
            timeOffsetLabel.setText("Time Offset: ");
            timeOffsetLabel.setVisible(true);
            timeOffsetLabel.setPreferredSize(new Dimension(172, 19));
            timeOffsetLabel.setMaximumSize(new Dimension(172, 19));
            timeOffsetLabel.setMinimumSize(new Dimension(172, 19));
            timeOffsetLabel.setFont(new Font("Arial", 0, 14));
        }

        return timeOffsetLabel;
    }

    private JLabel getTimeOffsetValueLabel() {
        if (timeOffsetValueLabel == null) {
            timeOffsetValueLabel = new JLabel();
            timeOffsetValueLabel.setFont(new Font("dialog", 0, 14));
            timeOffsetValueLabel.setText(CtiUtilities.STRING_NONE);
            timeOffsetValueLabel.setPreferredSize(new Dimension(172, 19));
            timeOffsetValueLabel.setMaximumSize(new Dimension(172, 19));
            timeOffsetValueLabel.setMinimumSize(new Dimension(172, 19));
            timeOffsetValueLabel.setFont(new Font("Arial", 0, 14));
        }

        return timeOffsetValueLabel;
    }

    private JLabel getTimesyncLabel() {
        if (timesyncLabel == null) {
            timesyncLabel = new JLabel();
            timesyncLabel.setName("TimesyncLabel");
            timesyncLabel.setFont(new Font("dialog", 0, 14));
            timesyncLabel.setText("Timesyncs Enabled: ");
            timesyncLabel.setVisible(true);
            timesyncLabel.setPreferredSize(new Dimension(172, 19));
            timesyncLabel.setMaximumSize(new Dimension(172, 19));
            timesyncLabel.setMinimumSize(new Dimension(172, 19));
            timesyncLabel.setFont(new Font("Arial", 0, 14));
        }

        return timesyncLabel;
    }

    private JLabel getTimesyncValueLabel() {
        if (timesyncValueLabel == null) {
            timesyncValueLabel = new JLabel();
            timesyncValueLabel.setFont(new Font("dialog", 0, 14));
            timesyncValueLabel.setText(CtiUtilities.STRING_NONE);
            timesyncValueLabel.setPreferredSize(new Dimension(172, 19));
            timesyncValueLabel.setMaximumSize(new Dimension(172, 19));
            timesyncValueLabel.setMinimumSize(new Dimension(172, 19));
            timesyncValueLabel.setFont(new Font("Arial", 0, 14));
        }

        return timesyncValueLabel;
    }

    private JLabel getOmitTimeRequestLabel() {
        if (omitTimeRequestLabel == null) {
            omitTimeRequestLabel = new JLabel();
            omitTimeRequestLabel.setName("OmitTimeRequestLabel");
            omitTimeRequestLabel.setFont(new Font("dialog", 0, 14));
            omitTimeRequestLabel.setText("Omit Time Request: ");
            omitTimeRequestLabel.setVisible(true);
            omitTimeRequestLabel.setPreferredSize(new Dimension(172, 19));
            omitTimeRequestLabel.setMaximumSize(new Dimension(172, 19));
            omitTimeRequestLabel.setMinimumSize(new Dimension(172, 19));
            omitTimeRequestLabel.setFont(new Font("Arial", 0, 14));
        }

        return omitTimeRequestLabel;
    }

    private JLabel getOmitTimeRequestValueLabel() {
        if (omitTimeRequestValueLabel == null) {
            omitTimeRequestValueLabel = new JLabel();
            omitTimeRequestValueLabel.setFont(new Font("dialog", 0, 14));
            omitTimeRequestValueLabel.setText(CtiUtilities.STRING_NONE);
            omitTimeRequestValueLabel.setPreferredSize(new Dimension(172, 19));
            omitTimeRequestValueLabel.setMaximumSize(new Dimension(172, 19));
            omitTimeRequestValueLabel.setMinimumSize(new Dimension(172, 19));
            omitTimeRequestValueLabel.setFont(new Font("Arial", 0, 14));
        }

        return omitTimeRequestValueLabel;
    }

    private JLabel getUnsolicitedLabel() {
        if (unsolicitedLabel == null) {
            unsolicitedLabel = new JLabel();
            unsolicitedLabel.setName("UnsolicitedLabel");
            unsolicitedLabel.setFont(new Font("dialog", 0, 14));
            unsolicitedLabel.setText("Unsolicited Enabled Class(1,2,3):");
            unsolicitedLabel.setVisible(true);
            unsolicitedLabel.setPreferredSize(new Dimension(210, 19));
            unsolicitedLabel.setMaximumSize(new Dimension(210, 19));
            unsolicitedLabel.setMinimumSize(new Dimension(210, 19));
            unsolicitedLabel.setFont(new Font("Arial", 0, 14));
        }

        return unsolicitedLabel;
    }

    private JLabel getUnsolicitedValueLabel() {
        if (unsolicitedValueLabel == null) {
            unsolicitedValueLabel = new JLabel();
            unsolicitedValueLabel.setFont(new Font("dialog", 0, 14));
            unsolicitedValueLabel.setText(CtiUtilities.STRING_NONE);
            unsolicitedValueLabel.setPreferredSize(new Dimension(172, 19));
            unsolicitedValueLabel.setMaximumSize(new Dimension(172, 19));
            unsolicitedValueLabel.setMinimumSize(new Dimension(172, 19));
            unsolicitedValueLabel.setFont(new Font("Arial", 0, 14));
        }

        return unsolicitedValueLabel;
    }

    /**
     * Return the ControlInhibitCheckBox property value.
     */
    private JCheckBox getControlInhibitCheckBox() {
        if (ivjControlInhibitCheckBox == null) {
            try {
                ivjControlInhibitCheckBox = new JCheckBox();
                ivjControlInhibitCheckBox.setName("ControlInhibitCheckBox");
                ivjControlInhibitCheckBox.setText("Disable Code Verification");
                ivjControlInhibitCheckBox.setMaximumSize(new Dimension(200, 23));
                ivjControlInhibitCheckBox.setActionCommand("Control Inhibit");
                ivjControlInhibitCheckBox.setBorderPainted(false);
                ivjControlInhibitCheckBox.setPreferredSize(new Dimension(200, 23));
                ivjControlInhibitCheckBox.setFont(new Font("dialog", 0, 14));
                ivjControlInhibitCheckBox.setMargin(new Insets(2, 2, 2, 2));
                ivjControlInhibitCheckBox.setMinimumSize(new Dimension(200, 23));
                ivjControlInhibitCheckBox.setHorizontalAlignment(2);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjControlInhibitCheckBox;
    }

    /**
     * Return the DialupSettingsPanel property value.
     */
    private JPanel getDialupSettingsPanel() {
        if (ivjDialupSettingsPanel == null) {
            try {
                com.cannontech.common.gui.util.TitleBorder ivjLocalBorder2;
                ivjLocalBorder2 = new com.cannontech.common.gui.util.TitleBorder();
                ivjLocalBorder2.setTitleFont(new Font("Arial", 1, 12));
                ivjLocalBorder2.setTitle("Dialing");
                ivjDialupSettingsPanel = new JPanel();
                ivjDialupSettingsPanel.setName("DialupSettingsPanel");
                ivjDialupSettingsPanel.setBorder(ivjLocalBorder2);
                ivjDialupSettingsPanel.setLayout(new GridBagLayout());
                ivjDialupSettingsPanel
                    .setMaximumSize(new Dimension(2147483647, 2147483647));
                ivjDialupSettingsPanel.setVisible(true);
                ivjDialupSettingsPanel.setPreferredSize(new Dimension(332, 70));
                ivjDialupSettingsPanel.setMinimumSize(new Dimension(332, 70));

                GridBagConstraints constraintsPhoneNumberTextField =
                    new GridBagConstraints();
                constraintsPhoneNumberTextField.gridx = 2;
                constraintsPhoneNumberTextField.gridy = 1;
                constraintsPhoneNumberTextField.fill = GridBagConstraints.HORIZONTAL;
                constraintsPhoneNumberTextField.weightx = 1.0;
                constraintsPhoneNumberTextField.insets = new Insets(11, 21, 1, 11);
                getDialupSettingsPanel().add(getPhoneNumberTextField(),
                                             constraintsPhoneNumberTextField);

                GridBagConstraints constraintsPhoneNumberLabel =
                    new GridBagConstraints();
                constraintsPhoneNumberLabel.gridx = 1;
                constraintsPhoneNumberLabel.gridy = 1;
                constraintsPhoneNumberLabel.ipadx = 24;
                constraintsPhoneNumberLabel.ipady = 7;
                constraintsPhoneNumberLabel.insets = new Insets(11, 15, 1, 21);
                getDialupSettingsPanel().add(getPhoneNumberLabel(), constraintsPhoneNumberLabel);

                GridBagConstraints constraintsJButtonAdvanced =
                    new GridBagConstraints();
                constraintsJButtonAdvanced.gridx = 1;
                constraintsJButtonAdvanced.gridy = 2;
                constraintsJButtonAdvanced.insets = new Insets(2, 15, 10, 21);
                getDialupSettingsPanel().add(getJButtonAdvanced(), constraintsJButtonAdvanced);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjDialupSettingsPanel;
    }

    /**
     * Return the DisableFlagCheckBox property value.
     */
    private JCheckBox getDisableFlagCheckBox() {
        if (ivjDisableFlagCheckBox == null) {
            try {
                ivjDisableFlagCheckBox = new JCheckBox();
                ivjDisableFlagCheckBox.setName("DisableFlagCheckBox");
                ivjDisableFlagCheckBox.setText("Disable Device");
                ivjDisableFlagCheckBox.setMaximumSize(new Dimension(121, 26));
                ivjDisableFlagCheckBox.setActionCommand("Disable Device");
                ivjDisableFlagCheckBox.setBorderPainted(false);
                ivjDisableFlagCheckBox.setPreferredSize(new Dimension(121, 26));
                ivjDisableFlagCheckBox.setFont(new Font("dialog", 0, 14));
                ivjDisableFlagCheckBox.setMargin(new Insets(2, 2, 2, 2));
                ivjDisableFlagCheckBox.setMinimumSize(new Dimension(121, 26));
                ivjDisableFlagCheckBox.setHorizontalAlignment(2);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjDisableFlagCheckBox;
    }

    private JPanel getIdentificationPanel() {
        if (ivjIdentificationPanel == null) {
            TitleBorder ivjLocalBorder = new TitleBorder();
            ivjLocalBorder.setTitleFont(new Font("Arial", 1, 12));
            ivjLocalBorder.setTitle("Identification");

            ivjIdentificationPanel = new JPanel();
            ivjIdentificationPanel.setName("IdentificationPanel");
            ivjIdentificationPanel.setBorder(ivjLocalBorder);
            ivjIdentificationPanel.setLayout(new GridBagLayout());

            GridBagConstraints constraintsTypeTextField = new GridBagConstraints();
            constraintsTypeTextField.anchor = GridBagConstraints.NORTHWEST;
            constraintsTypeTextField.gridx = 2;
            constraintsTypeTextField.gridy = 1;
            constraintsTypeTextField.insets = new Insets(3, 3, 3, 3);

            GridBagConstraints constraintsTypeLabel = new GridBagConstraints();
            constraintsTypeLabel.anchor = GridBagConstraints.NORTHWEST;
            constraintsTypeLabel.gridx = 1;
            constraintsTypeLabel.gridy = 1;
            constraintsTypeLabel.insets = new Insets(3, 3, 3, 3);

            GridBagConstraints constraintsNameLabel = new GridBagConstraints();
            constraintsNameLabel.anchor = GridBagConstraints.NORTHWEST;
            constraintsNameLabel.gridx = 1;
            constraintsNameLabel.gridy = 2;
            constraintsNameLabel.insets = new Insets(3, 3, 3, 3);

            GridBagConstraints constraintsNameTextField = new GridBagConstraints();
            constraintsNameTextField.anchor = GridBagConstraints.NORTHWEST;
            constraintsNameTextField.gridx = 2;
            constraintsNameTextField.gridy = 2;
            constraintsNameTextField.fill = GridBagConstraints.HORIZONTAL;
            constraintsNameTextField.weightx = 1.0;
            constraintsNameTextField.insets = new Insets(3, 3, 3, 3);

            GridBagConstraints constraintsPhysicalAddressLabel = new GridBagConstraints();
            constraintsPhysicalAddressLabel.anchor = GridBagConstraints.NORTHWEST;
            constraintsPhysicalAddressLabel.gridx = 1;
            constraintsPhysicalAddressLabel.gridy = 3;
            constraintsPhysicalAddressLabel.insets = new Insets(3, 3, 3, 3);

            GridBagConstraints constraintsPhysicalAddressTextField = new GridBagConstraints();
            constraintsPhysicalAddressTextField.anchor = GridBagConstraints.NORTHWEST;
            constraintsPhysicalAddressTextField.gridx = 2;
            constraintsPhysicalAddressTextField.gridy = 3;
            constraintsPhysicalAddressTextField.fill = GridBagConstraints.HORIZONTAL;
            constraintsPhysicalAddressTextField.weightx = 1.0;
            constraintsPhysicalAddressTextField.insets = new Insets(3, 3, 3, 3);

            GridBagConstraints constraintsDisableFlagCheckBox = new GridBagConstraints();
            constraintsDisableFlagCheckBox.gridx = 1;
            constraintsDisableFlagCheckBox.gridy = 7;
            constraintsDisableFlagCheckBox.insets = new Insets(3, 3, 3, 3);

            GridBagConstraints constraintsControlInhibitCheckBox = new GridBagConstraints();
            constraintsControlInhibitCheckBox.gridx = 2;
            constraintsControlInhibitCheckBox.gridy = 7;
            constraintsControlInhibitCheckBox.insets = new Insets(3, 3, 3, 3);

            GridBagConstraints constraintsSerialNumberLabel = new GridBagConstraints();
            constraintsSerialNumberLabel.gridx = 1;
            constraintsSerialNumberLabel.gridy = 4;
            constraintsSerialNumberLabel.anchor = GridBagConstraints.WEST;
            constraintsSerialNumberLabel.insets = new Insets(3, 3, 3, 3);

            GridBagConstraints constraintsSerialNumberTextField = new GridBagConstraints();
            constraintsSerialNumberTextField.gridx = 2;
            constraintsSerialNumberTextField.gridy = 4;
            constraintsSerialNumberTextField.fill = GridBagConstraints.HORIZONTAL;
            constraintsSerialNumberTextField.weightx = 1.0;
            constraintsSerialNumberTextField.insets = new Insets(3, 3, 3, 3);

            GridBagConstraints constraintsManufacturerLabel = new GridBagConstraints();
            constraintsManufacturerLabel.gridx = 1;
            constraintsManufacturerLabel.gridy = 5;
            constraintsManufacturerLabel.anchor = GridBagConstraints.WEST;
            constraintsManufacturerLabel.insets = new Insets(3, 3, 3, 3);

            GridBagConstraints constraintsManufacturerTextField = new GridBagConstraints();
            constraintsManufacturerTextField.gridx = 2;
            constraintsManufacturerTextField.gridy = 5;
            constraintsManufacturerTextField.fill = GridBagConstraints.HORIZONTAL;
            constraintsManufacturerTextField.weightx = 1.0;
            constraintsManufacturerTextField.insets = new Insets(3, 3, 3, 3);

            GridBagConstraints constraintsModelLabel = new GridBagConstraints();
            constraintsModelLabel.gridx = 1;
            constraintsModelLabel.gridy = 6;
            constraintsModelLabel.anchor = GridBagConstraints.WEST;
            constraintsModelLabel.insets = new Insets(3, 3, 3, 3);

            GridBagConstraints constraintsModelTextField = new GridBagConstraints();
            constraintsModelTextField.gridx = 2;
            constraintsModelTextField.gridy = 6;
            constraintsModelTextField.fill = GridBagConstraints.HORIZONTAL;
            constraintsModelTextField.weightx = 1.0;
            constraintsModelTextField.insets = new Insets(3, 3, 3, 3);

            ivjIdentificationPanel.add(getTypeTextField(), constraintsTypeTextField);
            ivjIdentificationPanel.add(getTypeLabel(), constraintsTypeLabel);
            ivjIdentificationPanel.add(getNameLabel(), constraintsNameLabel);
            ivjIdentificationPanel.add(getNameTextField(), constraintsNameTextField);
            ivjIdentificationPanel.add(getPhysicalAddressLabel(), constraintsPhysicalAddressLabel);
            ivjIdentificationPanel.add(getPhysicalAddressTextField(),
                                       constraintsPhysicalAddressTextField);
            ivjIdentificationPanel.add(getControlInhibitCheckBox(),
                                       constraintsControlInhibitCheckBox);
            ivjIdentificationPanel.add(getSerialNumberLabel(), constraintsSerialNumberLabel);
            ivjIdentificationPanel.add(getManufacturerLabel(), constraintsManufacturerLabel);
            ivjIdentificationPanel.add(getModelLabel(), constraintsModelLabel);
            ivjIdentificationPanel
                .add(getSerialNumberTextField(), constraintsSerialNumberTextField);
            ivjIdentificationPanel
                .add(getManufacturerTextField(), constraintsManufacturerTextField);
            ivjIdentificationPanel.add(getModelTextField(), constraintsModelTextField);
            ivjIdentificationPanel.add(getDisableFlagCheckBox(), constraintsDisableFlagCheckBox);
            getControlInhibitCheckBox().setVisible(false);
        }
        return ivjIdentificationPanel;
    }

    /**
     * Return the JButtonAdvanced property value.
     */
    private JButton getJButtonAdvanced() {
        if (ivjJButtonAdvanced == null) {
            try {
                ivjJButtonAdvanced = new JButton();
                ivjJButtonAdvanced.setName("JButtonAdvanced");
                ivjJButtonAdvanced.setPreferredSize(new Dimension(122, 23));
                ivjJButtonAdvanced.setText("Advanced...");
                ivjJButtonAdvanced.setMaximumSize(new Dimension(122, 23));
                ivjJButtonAdvanced.setMinimumSize(new Dimension(122, 23));
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJButtonAdvanced;
    }

    /**
     * Return the JComboBoxAmpUseType property value.
     */
    private JComboBox<String> getJComboBoxAmpUseType() {
        if (ivjJComboBoxAmpUseType == null) {
            try {
                ivjJComboBoxAmpUseType = new JComboBox<String>();
                ivjJComboBoxAmpUseType.setName("JComboBoxAmpUseType");
                ivjJComboBoxAmpUseType.setMaximumSize(new Dimension(162, 20));
                ivjJComboBoxAmpUseType.setPreferredSize(new Dimension(162, 20));
                ivjJComboBoxAmpUseType.setFont(new Font("dialog", 0, 14));
                ivjJComboBoxAmpUseType.setMinimumSize(new Dimension(162, 20));
                ivjJComboBoxAmpUseType
                    .addItem(com.cannontech.database.db.device.DeviceIDLCRemote.AMPUSE_AMP1);
                ivjJComboBoxAmpUseType
                    .addItem(com.cannontech.database.db.device.DeviceIDLCRemote.AMPUSE_AMP2);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJComboBoxAmpUseType;
    }

    /**
     * Return the JLabelCCUAmpUseType property value.
     */
    private JLabel getJLabelCCUAmpUseType() {
        if (ivjJLabelCCUAmpUseType == null) {
            try {
                ivjJLabelCCUAmpUseType = new JLabel();
                ivjJLabelCCUAmpUseType.setName("JLabelCCUAmpUseType");
                ivjJLabelCCUAmpUseType.setText("CCU Amp Use Type:");
                ivjJLabelCCUAmpUseType.setMaximumSize(new Dimension(172, 19));
                ivjJLabelCCUAmpUseType.setVisible(true);
                ivjJLabelCCUAmpUseType.setPreferredSize(new Dimension(172, 19));
                ivjJLabelCCUAmpUseType.setFont(new Font("dialog", 0, 14));
                ivjJLabelCCUAmpUseType.setMinimumSize(new Dimension(172, 19));
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelCCUAmpUseType;
    }

    /**
     * Return the NameLabel property value.
     */
    private JLabel getNameLabel() {
        if (ivjNameLabel == null) {
            try {
                ivjNameLabel = new JLabel();
                ivjNameLabel.setName("NameLabel");
                ivjNameLabel.setText("Device Name:");
                ivjNameLabel.setMaximumSize(new Dimension(87, 16));
                ivjNameLabel.setPreferredSize(new Dimension(87, 16));
                ivjNameLabel.setFont(new Font("dialog", 0, 14));
                ivjNameLabel.setMinimumSize(new Dimension(87, 16));
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjNameLabel;
    }

    /**
     * Return the NameTextField property value.
     */
    private JTextField getNameTextField() {
        if (ivjNameTextField == null) {
            try {
                ivjNameTextField = new JTextField();
                ivjNameTextField.setName("NameTextField");
                ivjNameTextField.setMaximumSize(new Dimension(200, 23));
                ivjNameTextField.setColumns(12);
                ivjNameTextField.setPreferredSize(new Dimension(200, 23));
                ivjNameTextField.setFont(new Font("sansserif", 0, 14));
                ivjNameTextField.setMinimumSize(new Dimension(200, 23));

                ivjNameTextField.setDocument(
                    new TextFieldDocument(
                                          TextFieldDocument.MAX_DEVICE_NAME_LENGTH,
                                          PaoUtils.ILLEGAL_NAME_CHARS));

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjNameTextField;
    }

    /**
     * Return the PasswordLabel property value.
     */
    private JLabel getPasswordLabel() {
        if (ivjPasswordLabel == null) {
            try {
                ivjPasswordLabel = new JLabel();
                ivjPasswordLabel.setName("PasswordLabel");
                ivjPasswordLabel.setText("Password:");
                ivjPasswordLabel.setMaximumSize(new Dimension(172, 19));
                ivjPasswordLabel.setPreferredSize(new Dimension(172, 19));
                ivjPasswordLabel.setFont(new Font("dialog", 0, 14));
                ivjPasswordLabel.setMinimumSize(new Dimension(172, 19));

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjPasswordLabel;
    }

    /**
     * Return the PhysicalAddressTextField1 property value.
     * @return JTextField
     */
    private JTextField getPasswordTextField() {
        if (ivjPasswordTextField == null) {
            try {
                ivjPasswordTextField = new JTextField();
                ivjPasswordTextField.setName("PasswordTextField");
                ivjPasswordTextField.setMaximumSize(new Dimension(162, 24));
                ivjPasswordTextField.setColumns(0);
                ivjPasswordTextField.setPreferredSize(new Dimension(162, 24));
                ivjPasswordTextField.setFont(new Font("sansserif", 0, 14));
                ivjPasswordTextField.setMinimumSize(new Dimension(162, 24));

                ivjPasswordTextField.setDocument(
                    new TextFieldDocument(TextFieldDocument.MAX_IED_PASSWORD_LENGTH));

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjPasswordTextField;
    }

    /**
     * Return the PhoneNumberLabel property value.
     */
    private JLabel getPhoneNumberLabel() {
        if (ivjPhoneNumberLabel == null) {
            try {
                ivjPhoneNumberLabel = new JLabel();
                ivjPhoneNumberLabel.setName("PhoneNumberLabel");
                ivjPhoneNumberLabel.setText("Phone Number:");
                ivjPhoneNumberLabel.setMaximumSize(new Dimension(98, 16));
                ivjPhoneNumberLabel.setVisible(true);
                ivjPhoneNumberLabel.setPreferredSize(new Dimension(98, 16));
                ivjPhoneNumberLabel.setFont(new Font("dialog", 0, 14));
                ivjPhoneNumberLabel.setEnabled(true);
                ivjPhoneNumberLabel.setMinimumSize(new Dimension(98, 16));

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjPhoneNumberLabel;
    }

    /**
     * Return the PhoneNumberTextField property value.
     * @return JTextField
     */

    private JTextField getPhoneNumberTextField() {
        if (ivjPhoneNumberTextField == null) {
            try {
                ivjPhoneNumberTextField = new JTextField();
                ivjPhoneNumberTextField.setName("PhoneNumberTextField");
                ivjPhoneNumberTextField.setMaximumSize(new Dimension(2147483647, 20));
                ivjPhoneNumberTextField.setVisible(true);
                ivjPhoneNumberTextField.setColumns(13);
                ivjPhoneNumberTextField.setPreferredSize(new Dimension(142, 23));
                ivjPhoneNumberTextField.setFont(new Font("sansserif", 0, 14));
                ivjPhoneNumberTextField.setEnabled(true);
                ivjPhoneNumberTextField.setMinimumSize(new Dimension(142, 23));

                ivjPhoneNumberTextField.setDocument(
                    new TextFieldDocument(TextFieldDocument.MAX_PHONE_NUMBER_LENGTH));

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjPhoneNumberTextField;
    }

    /**
     * Return the PhysicalAddressLabel property value.
     */

    private JLabel getPhysicalAddressLabel() {
        if (ivjPhysicalAddressLabel == null) {
            try {
                ivjPhysicalAddressLabel = new JLabel();
                ivjPhysicalAddressLabel.setName("PhysicalAddressLabel");
                ivjPhysicalAddressLabel.setText("Physical Address:");
                ivjPhysicalAddressLabel.setMaximumSize(new Dimension(112, 16));
                ivjPhysicalAddressLabel.setPreferredSize(new Dimension(112, 16));
                ivjPhysicalAddressLabel.setFont(new Font("dialog", 0, 14));
                ivjPhysicalAddressLabel.setMinimumSize(new Dimension(112, 16));

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjPhysicalAddressLabel;
    }

    private JLabel getSerialNumberLabel() {
        if (serialNumberLabel == null) {
            serialNumberLabel = new JLabel();
            serialNumberLabel.setText("Serial Number:");
            serialNumberLabel.setFont(new Font("dialog", 0, 14));
            serialNumberLabel.setMinimumSize(new Dimension(112, 16));
            serialNumberLabel.setVisible(false);
        }
        return serialNumberLabel;
    }

    private JLabel getManufacturerLabel() {
        if (manufacturerLabel == null) {
            manufacturerLabel = new JLabel();
            manufacturerLabel.setText("Manufacturer:");
            manufacturerLabel.setFont(new Font("dialog", 0, 14));
            manufacturerLabel.setMinimumSize(new Dimension(112, 16));
            manufacturerLabel.setVisible(false);
        }
        return manufacturerLabel;
    }

    private JLabel getModelLabel() {
        if (modelLabel == null) {
            modelLabel = new JLabel();
            modelLabel.setText("Model:");
            modelLabel.setFont(new Font("dialog", 0, 14));
            modelLabel.setMinimumSize(new Dimension(112, 16));
            modelLabel.setVisible(false);
        }
        return modelLabel;
    }

    /**
     * Return the PhysicalAddressTextField property value.
     * @return JTextField
     */

    private JTextField getPhysicalAddressTextField() {
        if (ivjPhysicalAddressTextField == null) {
            try {
                ivjPhysicalAddressTextField = new JTextField();
                ivjPhysicalAddressTextField.setName("PhysicalAddressTextField");
                ivjPhysicalAddressTextField.setMaximumSize(new Dimension(200, 23));
                // ivjPhysicalAddressTextField.setColumns(15);
                ivjPhysicalAddressTextField.setPreferredSize(new Dimension(200, 23));
                ivjPhysicalAddressTextField.setFont(new Font("sansserif", 0, 14));
                ivjPhysicalAddressTextField.setMinimumSize(new Dimension(200, 23));

                ivjPhysicalAddressTextField
                    .setDocument(new com.cannontech.common.gui.unchanging.LongRangeDocument());

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjPhysicalAddressTextField;
    }

    private JTextField getSerialNumberTextField() {
        if (serialNumberTextField == null) {
            serialNumberTextField = new JTextField();
            serialNumberTextField.setName("SerialNumberTextField");
            serialNumberTextField.setFont(new Font("sansserif", 0, 14));
            serialNumberTextField.setMinimumSize(new Dimension(200, 23));
            serialNumberTextField.setVisible(false);
            
            serialNumberTextField.setDocument(new TextFieldDocument(TextFieldDocument.STRING_LENGTH_30));
        }
        return serialNumberTextField;
    }

    private JTextField getManufacturerTextField() {
        if (manufacturerTextField == null) {
            manufacturerTextField = new JTextField();
            manufacturerTextField.setName("ManufacturerTextField");
            manufacturerTextField.setFont(new Font("sansserif", 0, 14));
            manufacturerTextField.setMinimumSize(new Dimension(200, 23));
            manufacturerTextField.setVisible(false);
        }
        return manufacturerTextField;
    }

    private JTextField getModelTextField() {
        if (modelTextField == null) {
            modelTextField = new JTextField();
            modelTextField.setName("ModelTextField");
            modelTextField.setFont(new Font("sansserif", 0, 14));
            modelTextField.setMinimumSize(new Dimension(200, 23));
            modelTextField.setVisible(false);
        }
        return modelTextField;
    }

    /**
     * Return the PortComboBox property value.
     * @return JComboBox
     */

    private JComboBox<LiteYukonPAObject> getPortComboBox() {
        if (ivjPortComboBox == null) {
            try {
                ivjPortComboBox = new JComboBox<LiteYukonPAObject>();
                ivjPortComboBox.setName("PortComboBox");
                ivjPortComboBox.setMaximumSize(new Dimension(162, 20));
                ivjPortComboBox.setPreferredSize(new Dimension(162, 20));
                ivjPortComboBox.setFont(new Font("dialog", 0, 14));
                ivjPortComboBox.setMinimumSize(new Dimension(162, 20));

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjPortComboBox;
    }

    /**
     * Return the PortLabel property value.
     */

    private JLabel getPortLabel() {
        if (ivjPortLabel == null) {
            try {
                ivjPortLabel = new JLabel();
                ivjPortLabel.setName("PortLabel");
                ivjPortLabel.setText("Communication Channel:");
                ivjPortLabel.setMaximumSize(new Dimension(172, 19));
                ivjPortLabel.setPreferredSize(new Dimension(172, 19));
                ivjPortLabel.setFont(new Font("dialog", 0, 14));
                ivjPortLabel.setMinimumSize(new Dimension(172, 19));

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjPortLabel;
    }

    /**
     * Return the PostCommWaitLabel property value.
     */

    private JLabel getPostCommWaitLabel() {
        if (ivjPostCommWaitLabel == null) {
            try {
                ivjPostCommWaitLabel = new JLabel();
                ivjPostCommWaitLabel.setName("PostCommWaitLabel");
                ivjPostCommWaitLabel.setText("Post Communication Wait:");
                ivjPostCommWaitLabel.setMaximumSize(new Dimension(172, 16));
                ivjPostCommWaitLabel.setPreferredSize(new Dimension(172, 19));
                ivjPostCommWaitLabel.setFont(new Font("dialog", 0, 14));
                ivjPostCommWaitLabel.setMinimumSize(new Dimension(172, 16));

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjPostCommWaitLabel;
    }

    /**
     * Return the PostCommWaitSpinner property value.
     * @return JCSpinField
     */
    private JCSpinField getPostCommWaitSpinner() {
        if (ivjPostCommWaitSpinner == null) {
            try {
                ivjPostCommWaitSpinner = new com.klg.jclass.field.JCSpinField();
                ivjPostCommWaitSpinner.setName("PostCommWaitSpinner");
                ivjPostCommWaitSpinner.setPreferredSize(new Dimension(50, 22));
                ivjPostCommWaitSpinner.setFont(new Font("sansserif", 0, 14));
                ivjPostCommWaitSpinner.setMaximumSize(new Dimension(2147483647, 20));
                ivjPostCommWaitSpinner.setMinimumSize(new Dimension(50, 22));
                ivjPostCommWaitSpinner
                    .setDataProperties(new DataProperties(new JCIntegerValidator(null,
                                                                                 new Integer(0),
                                                                                 new Integer(1000000),
                                                                                 null,
                                                                                 true,
                                                                                 null,
                                                                                 new Integer(1),
                                                                                 "#,##0.###;-#,##0.###",
                                                                                 false,
                                                                                 false,
                                                                                 false,
                                                                                 null,
                                                                                 new Integer(0)),
                                                          new MutableValueModel(Integer.class,
                                                                                new Integer(0)),
                                                          new JCInvalidInfo(true,
                                                                            2,
                                                                            new Color(0,
                                                                                      0,
                                                                                      0,
                                                                                      255),
                                                                            new Color(255,
                                                                                      255,
                                                                                      255,
                                                                                      255))));
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjPostCommWaitSpinner;
    }

    /**
     * Return the RouteComboBox property value.
     * @return JComboBox
     */
    private JComboBox<LiteYukonPAObject> getRouteComboBox() {
        if (ivjRouteComboBox == null) {
            try {
                ivjRouteComboBox = new JComboBox<LiteYukonPAObject>();
                ivjRouteComboBox.setName("RouteComboBox");
                ivjRouteComboBox.setPreferredSize(new Dimension(200, 20));
                ivjRouteComboBox.setMinimumSize(new Dimension(200, 20));

                ivjRouteComboBox.setMaximumSize(new Dimension(200, 20));

            } catch (Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjRouteComboBox;
    }

    /**
     * Return the CommPathLabel property value.
     */

    private JLabel getRouteLabel() {
        if (ivjRouteLabel == null) {
            try {
                ivjRouteLabel = new JLabel();
                ivjRouteLabel.setName("RouteLabel");
                ivjRouteLabel.setFont(new Font("dialog", 0, 14));
                ivjRouteLabel.setText("Communication Route:");
                ivjRouteLabel.setPreferredSize(new Dimension(150, 19));
                ivjRouteLabel.setMinimumSize(new Dimension(150, 19));
                ivjRouteLabel.setMaximumSize(new Dimension(150, 19));
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjRouteLabel;
    }

    /**
     * Return the SecurityCodeLabel property value.
     */

    private JLabel getSecurityCodeLabel() {
        if (ivjSecurityCodeLabel == null) {
            try {
                ivjSecurityCodeLabel = new JLabel();
                ivjSecurityCodeLabel.setName("SecurityCodeLabel");
                ivjSecurityCodeLabel.setText("Security Code:");
                ivjSecurityCodeLabel.setMaximumSize(new Dimension(172, 19));
                ivjSecurityCodeLabel.setPreferredSize(new Dimension(172, 19));
                ivjSecurityCodeLabel.setFont(new Font("dialog", 0, 14));
                ivjSecurityCodeLabel.setMinimumSize(new Dimension(172, 19));
                ivjSecurityCodeLabel.setVisible(false);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjSecurityCodeLabel;
    }

    /**
     * Return the SecurityCodeTextField property value.
     * @return JTextField
     */
    private JTextField getSecurityCodeTextField() {
        if (ivjSecurityCodeTextField == null) {
            try {
                ivjSecurityCodeTextField = new JTextField();
                ivjSecurityCodeTextField.setName("SecurityCodeTextField");
                ivjSecurityCodeTextField.setMaximumSize(new Dimension(2147483647, 20));
                ivjSecurityCodeTextField.setColumns(0);
                ivjSecurityCodeTextField.setPreferredSize(new Dimension(157, 24));
                ivjSecurityCodeTextField.setFont(new Font("sansserif", 0, 14));
                ivjSecurityCodeTextField.setMinimumSize(new Dimension(120, 20));
                ivjSecurityCodeTextField.setVisible(false);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjSecurityCodeTextField;
    }

    /**
     * Return the SenderLabel property value.
     */
    private JLabel getSenderLabel() {
        if (ivjSenderLabel == null) {
            try {
                ivjSenderLabel = new JLabel();
                ivjSenderLabel.setName("SenderLabel");
                ivjSenderLabel.setText("Sender:");
                ivjSenderLabel.setMaximumSize(new Dimension(172, 19));
                ivjSenderLabel.setPreferredSize(new Dimension(172, 19));
                ivjSenderLabel.setFont(new Font("dialog", 0, 14));
                ivjSenderLabel.setMinimumSize(new Dimension(172, 19));
                ivjSenderLabel.setVisible(false);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjSenderLabel;
    }

    private JLabel getTcpIpAddressLabel() {
        if (tcpIpAddressLabel == null) {
            try {
                tcpIpAddressLabel = new JLabel();
                tcpIpAddressLabel.setName("IpAddressLabel");
                tcpIpAddressLabel.setText("Ip Address:");
                tcpIpAddressLabel.setMaximumSize(new Dimension(172, 19));
                tcpIpAddressLabel.setPreferredSize(new Dimension(172, 19));
                tcpIpAddressLabel.setFont(new Font("dialog", 0, 14));
                tcpIpAddressLabel.setMinimumSize(new Dimension(172, 19));
                tcpIpAddressLabel.setVisible(false);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return tcpIpAddressLabel;
    }

    private JLabel getTcpPortLabel() {
        if (tcpPortLabel == null) {
            try {
                tcpPortLabel = new JLabel();
                tcpPortLabel.setName("PortLabel");
                tcpPortLabel.setText("Port:");
                tcpPortLabel.setMaximumSize(new Dimension(172, 19));
                tcpPortLabel.setPreferredSize(new Dimension(172, 19));
                tcpPortLabel.setFont(new Font("dialog", 0, 14));
                tcpPortLabel.setMinimumSize(new Dimension(172, 19));
                tcpPortLabel.setVisible(false);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return tcpPortLabel;
    }

    private JTextField getTcpIpAddressTextField() {
        if (tcpIpAddressTextField == null) {
            try {
                tcpIpAddressTextField = new JTextField();
                tcpIpAddressTextField.setName("TcpIpAddressTextField");
                tcpIpAddressTextField.setMaximumSize(new Dimension(200, 20));
                tcpIpAddressTextField.setColumns(0);
                tcpIpAddressTextField.setPreferredSize(new Dimension(157, 24));
                tcpIpAddressTextField.setFont(new Font("sansserif", 0, 14));
                tcpIpAddressTextField.setMinimumSize(new Dimension(120, 20));
                tcpIpAddressTextField.setVisible(false);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return tcpIpAddressTextField;
    }

    private JTextField getTcpPortTextField() {
        if (tcpPortTextField == null) {
            try {
                tcpPortTextField = new JTextField();
                tcpPortTextField.setName("TcpPortTextField");
                tcpPortTextField.setMaximumSize(new Dimension(200, 20));
                tcpPortTextField.setColumns(0);
                tcpPortTextField.setPreferredSize(new Dimension(157, 24));
                tcpPortTextField.setFont(new Font("sansserif", 0, 14));
                tcpPortTextField.setMinimumSize(new Dimension(120, 20));
                tcpPortTextField.setVisible(false);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return tcpPortTextField;
    }

    /**
     * Return the SenderTextField property value.
     */
    private JTextField getSenderTextField() {
        if (ivjSenderTextField == null) {
            try {
                ivjSenderTextField = new JTextField();
                ivjSenderTextField.setName("SenderTextField");
                ivjSenderTextField.setMaximumSize(new Dimension(2147483647, 20));
                ivjSenderTextField.setColumns(0);
                ivjSenderTextField.setPreferredSize(new Dimension(120, 20));
                ivjSenderTextField.setFont(new Font("sansserif", 0, 14));
                ivjSenderTextField.setMinimumSize(new Dimension(120, 20));
                ivjSenderTextField.setVisible(false);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjSenderTextField;
    }

    /**
     * Return the SlaveAddressComboBox property value.
     */
    private JComboBox<Comparable> getSlaveAddressComboBox() {
        if (ivjSlaveAddressComboBox == null) {
            try {
                ivjSlaveAddressComboBox = new JComboBox<>();
                ivjSlaveAddressComboBox.setName("SlaveAddressComboBox");
                ivjSlaveAddressComboBox.setMaximumSize(new Dimension(162, 20));
                ivjSlaveAddressComboBox.setPreferredSize(new Dimension(162, 20));
                ivjSlaveAddressComboBox.setFont(new Font("dialog", 0, 14));
                ivjSlaveAddressComboBox.setMinimumSize(new Dimension(162, 20));
                ivjSlaveAddressComboBox.addItem(IEDBase.SLAVE_STAND_ALONE);
                ivjSlaveAddressComboBox.addItem(IEDBase.SLAVE_MASTER);
                ivjSlaveAddressComboBox.addItem(IEDBase.SLAVE_SLAVE1);
                ivjSlaveAddressComboBox.addItem(IEDBase.SLAVE_SLAVE2);
                ivjSlaveAddressComboBox.addItem(IEDBase.SLAVE_SLAVE3);
                ivjSlaveAddressComboBox.addItem(IEDBase.SLAVE_SLAVE4);

            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjSlaveAddressComboBox;
    }

    /**
     * Return the SlaveAddressLabel property value.
     */
    private JLabel getSlaveAddressLabel() {
        if (ivjSlaveAddressLabel == null) {
            try {
                ivjSlaveAddressLabel = new JLabel();
                ivjSlaveAddressLabel.setName("SlaveAddressLabel");
                ivjSlaveAddressLabel.setText("Slave Address:");
                ivjSlaveAddressLabel.setMaximumSize(new Dimension(172, 19));
                ivjSlaveAddressLabel.setPreferredSize(new Dimension(172, 19));
                ivjSlaveAddressLabel.setFont(new Font("dialog", 0, 14));
                ivjSlaveAddressLabel.setMinimumSize(new Dimension(172, 19));
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjSlaveAddressLabel;
    }

    /**
     * Return the TOUComboBox property value.
     */
    private JComboBox<String> getTOUComboBox() {
        if (ivjTOUComboBox == null) {
            try {
                ivjTOUComboBox = new JComboBox<String>();
                ivjTOUComboBox.setName("TOUComboBox");
                ivjTOUComboBox.setMaximumSize(new Dimension(162, 20));
                ivjTOUComboBox.setVisible(false);
                ivjTOUComboBox.setPreferredSize(new Dimension(162, 20));
                ivjTOUComboBox.setFont(new Font("dialog", 0, 14));
                ivjTOUComboBox.setMinimumSize(new Dimension(162, 20));
                ivjTOUComboBox.setEnabled(false);
                ivjTOUComboBox.setVisible(false);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjTOUComboBox;
    }

    /**
     * Return the TOULabel property value.
     */
    private JLabel getTOULabel() {
        if (ivjTOULabel == null) {
            try {
                ivjTOULabel = new JLabel();
                ivjTOULabel.setName("TOULabel");
                ivjTOULabel.setFont(new Font("dialog", 0, 14));
                ivjTOULabel.setText("TOU Schedule: ");
                ivjTOULabel.setVisible(false);
                ivjTOULabel.setEnabled(false);
                ivjTOULabel.setPreferredSize(new Dimension(172, 19));
                ivjTOULabel.setMaximumSize(new Dimension(172, 19));
                ivjTOULabel.setMinimumSize(new Dimension(172, 19));
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjTOULabel;
    }

    /**
     * Return the TypeLabel property value.
     */
    private JLabel getTypeLabel() {
        if (ivjTypeLabel == null) {
            try {
                ivjTypeLabel = new JLabel();
                ivjTypeLabel.setName("TypeLabel");
                ivjTypeLabel.setText("Device Type:");
                ivjTypeLabel.setMaximumSize(new Dimension(83, 20));
                ivjTypeLabel.setPreferredSize(new Dimension(83, 20));
                ivjTypeLabel.setFont(new Font("dialog", 0, 14));
                ivjTypeLabel.setMinimumSize(new Dimension(83, 20));
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjTypeLabel;
    }

    /**
     * Return the DeviceTypeTextField property value.
     */
    private JLabel getTypeTextField() {
        if (ivjTypeTextField == null) {
            try {
                ivjTypeTextField = new JLabel();
                ivjTypeTextField.setName("TypeTextField");
                ivjTypeTextField.setOpaque(true);
                ivjTypeTextField.setText("");
                ivjTypeTextField.setMaximumSize(new Dimension(200, 23));
                ivjTypeTextField.setPreferredSize(new Dimension(200, 23));
                ivjTypeTextField.setFont(new Font("dialog.bold", 1, 14));
                ivjTypeTextField.setMinimumSize(new Dimension(200, 23));
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjTypeTextField;
    }

    @Override
    public Object getValue(Object val) {
        deviceBase = (DeviceBase) val;

        deviceBase.setPAOName(getNameTextField().getText());
        PaoType paoType = deviceBase.getPaoType();

        if (getDisableFlagCheckBox().isSelected()) {
            deviceBase.setDisableFlag(new Character('Y'));
        } else {
            deviceBase.setDisableFlag(new Character('N'));
        }

        PaoPropertyDao propertyDao = YukonSpringHook.getBean(PaoPropertyDao.class);
        propertyDao.removeAll(deviceBase.getPAObjectID());

        LiteYukonPAObject port = (LiteYukonPAObject) getPortComboBox().getSelectedItem();
        if (port != null && PaoType.TCPPORT == port.getPaoType()
            && deviceType.isTcpPortEligible()) {
            String portNum = getTcpPortTextField().getText();
            String ipAddress = getTcpIpAddressTextField().getText();

            if (val instanceof RemoteBase) {
                // Set the values on the DBPersistent for it to persist when it saves.
                RemoteBase remoteBase = (RemoteBase) val;
                remoteBase.setPort(portNum);
                remoteBase.setIpAddress(ipAddress);
                remoteBase.addTcpProperties(); // commit these to the database, since they have already been removed above. Necessary if no changes to object are made in DBeditor (in which no "update" db is called)
            } else {
                // The DBPersistent doesn't track this info, write to PaoProperty for it.
                PaoIdentifier identifier = new PaoIdentifier(deviceBase.getPAObjectID(), deviceType);

                propertyDao.add(new PaoProperty(identifier, PaoPropertyName.TcpIpAddress, ipAddress));
                propertyDao.add(new PaoProperty(identifier, PaoPropertyName.TcpPort, portNum));
            }
        }

        // This is a little bit ugly
        // The address could be coming from three distinct types of devices - yet all devices have an address eeck.
        if (getPhysicalAddressTextField().isVisible()) {
            try {
                Integer address = new Integer(
                                              getPhysicalAddressTextField().getText());

                if (val instanceof CarrierBase) {
                    CarrierBase carrierBase = (CarrierBase) val;
                    if (paoType == PaoType.REPEATER) { // val instanceof Repeater900
                        carrierBase.getDeviceCarrierSettings().setAddress(new Integer(address.intValue() + Repeater900.ADDRESS_OFFSET));
                    } else if (paoType == PaoType.REPEATER_921) {// val instanceof Repeater921
                        carrierBase.getDeviceCarrierSettings().setAddress(new Integer(address.intValue() + Repeater921.ADDRESS_OFFSET));
                    } else {
                        carrierBase.getDeviceCarrierSettings().setAddress(address);
                    }
                } else if (val instanceof IDLCBase) {
                    IDLCBase idlcBase = (IDLCBase) val;
                    idlcBase.getDeviceIDLCRemote().setAddress(address);
                }
            } catch (NumberFormatException n) {
                CTILogger.error(n.getMessage(), n);
            }
        }

        if (val instanceof GridAdvisorBase) {
            GridAdvisorBase gridAdvisorBase = (GridAdvisorBase) val;
            LiteYukonPAObject litePort = null;

            litePort = (LiteYukonPAObject) getPortComboBox().getSelectedItem();
            // get new port from combo box and set it.
            gridAdvisorBase.getDeviceDirectCommSettings().setPortID(litePort.getLiteID());
            try {
                gridAdvisorBase.getDeviceAddress().setMasterAddress(new Integer(getPhysicalAddressTextField().getText()));
            } catch (NumberFormatException e) {
                gridAdvisorBase.getDeviceAddress().setMasterAddress(new Integer(0));
            }
        }

        if (val instanceof RemoteBase) {
            RemoteBase remoteBase = (RemoteBase) val;
            DeviceDirectCommSettings dDirect = remoteBase.getDeviceDirectCommSettings();

            Integer portID = null;
            Integer postCommWait = null;

            portID = new Integer(port.getYukonID());
            dDirect.setPortID(portID);

            Object postCommWaitSpinVal = getPostCommWaitSpinner().getValue();
            if (postCommWaitSpinVal instanceof Long) {
                postCommWait = new Integer(((Long) postCommWaitSpinVal).intValue());
            } else if (postCommWaitSpinVal instanceof Integer) {
                postCommWait = new Integer(((Integer) postCommWaitSpinVal).intValue());
            }

            if (val instanceof IDLCBase) {
                IDLCBase idlcBase = (IDLCBase) val;
                idlcBase.getDeviceIDLCRemote().setPostCommWait(postCommWait);
                idlcBase.getDeviceIDLCRemote().setCcuAmpUseType(getJComboBoxAmpUseType().getSelectedItem().toString());
            }

            if (port.getPaoType().isDialupPort()) {
                DeviceDialupSettings dDialup = remoteBase.getDeviceDialupSettings();

                getAdvancedPanel().getValue(dDialup);

                dDialup.setPhoneNumber(getPhoneNumberTextField().getText().trim());
                if (val instanceof TapTerminalBase) {
                    dDialup.setLineSettings("7E1");
                } else {
                    dDialup.setLineSettings("8N1");
                }
            } else {
                remoteBase.getDeviceDialupSettings().setPhoneNumber(null);
            }

            if (val instanceof DNPBase || val instanceof IonBase) { // DeviceTypesFuncs.hasMasterAddress(devType)

                AddressBase addressBase = (AddressBase) val;    //both DNPBase and IonBase extend AddressBase
                try {
                    addressBase.getDeviceAddress().setMasterAddress(new Integer(getPhysicalAddressTextField().getText()));
                } catch (NumberFormatException e) {
                    addressBase.getDeviceAddress().setMasterAddress(new Integer(0));
                }

                try {
                    addressBase.getDeviceAddress().setSlaveAddress(new Integer(getSlaveAddressComboBox().getSelectedItem().toString()));
                } catch (NumberFormatException e) {
                    addressBase.getDeviceAddress().setSlaveAddress(new Integer(0));
                }

                try {
                    addressBase.getDeviceAddress().setPostCommWait(new Integer(getPostCommWaitSpinner().getValue().toString()));
                } catch (NumberFormatException e) {
                    addressBase.getDeviceAddress().setPostCommWait(new Integer(0));
                }

            } else if (val instanceof Series5Base) {
                Series5Base series5Base = (Series5Base) val;

                try {
                    series5Base.getSeries5().setSlaveAddress(new Integer(getPhysicalAddressTextField().getText()));
                } catch (NumberFormatException e) {
                    series5Base.getSeries5().setSlaveAddress(new Integer(0));
                }

                try {
                    series5Base.getSeries5().setPostCommWait(new Integer(getPostCommWaitSpinner().getValue().toString()));
                } catch (NumberFormatException e) {
                    series5Base.getSeries5().setPostCommWait(new Integer(0));
                }

                if (getControlInhibitCheckBox().isSelected()) {
                    series5Base.getVerification().setDisable("Y");
                } else {
                    series5Base.getVerification().setDisable("N");
                }
            } else if (val instanceof RTCBase) {
                RTCBase rtcBase = (RTCBase) val;
                try {
                    rtcBase.getDeviceRTC().setRTCAddress(new Integer(getPhysicalAddressTextField().getText()));
                } catch (NumberFormatException e) {
                    rtcBase.getDeviceRTC().setRTCAddress(new Integer(0));
                }

                try {
                    rtcBase.setLBTMode(getSlaveAddressComboBox().getSelectedItem().toString());
                } catch (NumberFormatException e) {
                    rtcBase.getDeviceRTC().setLBTMode(new Integer(0));
                }

                if (getControlInhibitCheckBox().isSelected()) {
                    rtcBase.getDeviceRTC().setDisableVerifies("Y");
                } else {
                    rtcBase.getDeviceRTC().setDisableVerifies("N");
                }

            } else if (val instanceof RTM) {
                RTM rtm = (RTM) val;
                rtm.getDeviceIED().setSlaveAddress(getPhysicalAddressTextField().getText());
            } else if (val instanceof IEDBase) {
                IEDBase iedBase = (IEDBase) val;
                String password = getPasswordTextField().getText();
                if (password.length() > 0) {
                    if (val instanceof WCTPTerminal) {
                        ((WCTPTerminal) val).getDeviceTapPagingSettings().setPOSTPath(password);
                    } else {
                        iedBase.getDeviceIED().setPassword(password);
                    }
                } else {
                    if (val instanceof TapTerminalBase) {
                        ((TapTerminalBase) val).getDeviceTapPagingSettings().setPOSTPath(CtiUtilities.STRING_NONE);
                    } else {
                        iedBase.getDeviceIED().setPassword(CtiUtilities.STRING_NONE);
                    }
                }

                if (val instanceof TapTerminalBase) {
                    TapTerminalBase tapTerminalBase = (TapTerminalBase) val;
                    if (getSenderTextField().isVisible()
                        && getSenderTextField().getText().length() > 0) {
                        tapTerminalBase.getDeviceTapPagingSettings().setSender(getSenderTextField().getText());
                    }
                    if (getSecurityCodeTextField().isVisible()
                        && getSecurityCodeTextField().getText().length() > 0) {
                        tapTerminalBase.getDeviceTapPagingSettings().setSecurityCode(getSecurityCodeTextField().getText());
                    }
                }

                if (getSlaveAddressComboBox().isVisible()) {
                    String slaveAddress = null;

                    /**** START SUPER HACK ****/
                    if (getSlaveAddressComboBox().isEditable()) {
                        slaveAddress = getSlaveAddressComboBox().getEditor()
                            .getItem()
                            .toString();
                    } else {
                        /**** END SUPER HACK ****/
                        slaveAddress =
                            new String(getSlaveAddressComboBox().getSelectedItem() != null ? 
                                    getSlaveAddressComboBox().getSelectedItem().toString() : "");
                    }

                    iedBase.getDeviceIED().setSlaveAddress(slaveAddress);
                }
            } else if (val instanceof CCU721) {
                CCU721 ccu721 = (CCU721) val;
                try {
                    ccu721.getDeviceAddress().setMasterAddress(new Integer(getPhysicalAddressTextField().getText()));
                } catch (NumberFormatException e) {
                    ccu721.getDeviceAddress().setMasterAddress(new Integer(0));
                }

                try {
                    ccu721.getDeviceAddress().setSlaveAddress(new Integer(getSlaveAddressComboBox().getSelectedItem().toString()));
                } catch (NumberFormatException e) {
                    ccu721.getDeviceAddress().setSlaveAddress(new Integer(0));
                }
            }

        } else {
            if (val instanceof CarrierBase) {
                CarrierBase carrierBase = (CarrierBase) val;
                int routeId = (((LiteYukonPAObject) getRouteComboBox().getSelectedItem()).getYukonID());
                carrierBase.getDeviceRoutes().setRouteID(routeId);
            } else if (val instanceof RfnBase) {
                RfnBase rfnBase = (RfnBase) val;
                rfnBase.getRfnAddress().setSerialNumber(StringUtils.trimToNull(getSerialNumberTextField().getText())); // Don't respect leading or trailing spaces
                String manufacturer = StringUtils.trimToNull(getManufacturerTextField().getText());
                rfnBase.getRfnAddress().setManufacturer(manufacturer == null ? null : getManufacturerTextField().getText());
                String model = StringUtils.trimToNull(getModelTextField().getText());
                rfnBase.getRfnAddress().setModel(model == null ? null : getModelTextField().getText());
            }
        }

        return val;
    }

    private JLabel getWaitLabel() {
        if (ivjWaitLabel == null) {
            try {
                ivjWaitLabel = new JLabel();
                ivjWaitLabel.setName("WaitLabel");
                ivjWaitLabel.setText("(msec.)");
                ivjWaitLabel.setMaximumSize(new Dimension(65, 16));
                ivjWaitLabel.setPreferredSize(new Dimension(65, 16));
                ivjWaitLabel.setFont(new Font("dialog", 0, 12));
                ivjWaitLabel.setMinimumSize(new Dimension(65, 16));
                ivjWaitLabel.setText("(msec.)");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjWaitLabel;
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
    private void initConnections() throws Exception {
        getPostCommWaitSpinner().addValueListener(eventHandler);

        getNameTextField().addCaretListener(eventHandler);
        getPhysicalAddressTextField().addCaretListener(eventHandler);
        getDisableFlagCheckBox().addActionListener(eventHandler);
        getControlInhibitCheckBox().addActionListener(eventHandler);
        getRouteComboBox().addActionListener(eventHandler);
        getPortComboBox().addActionListener(eventHandler);
        getPhoneNumberTextField().addCaretListener(eventHandler);
        getSlaveAddressComboBox().addActionListener(eventHandler);
        getJButtonAdvanced().addActionListener(eventHandler);
        getJComboBoxAmpUseType().addActionListener(eventHandler);
        getTOUComboBox().addActionListener(eventHandler);
        getPasswordTextField().addCaretListener(eventHandler);
        getSenderTextField().addCaretListener(eventHandler);
        getSecurityCodeTextField().addCaretListener(eventHandler);
        getTcpIpAddressTextField().addCaretListener(eventHandler);
        getTcpPortTextField().addCaretListener(eventHandler);
        getSerialNumberTextField().addCaretListener(eventHandler);
        getManufacturerTextField().addCaretListener(eventHandler);
        getModelTextField().addCaretListener(eventHandler);
    }

    private void initialize() {
        this.setDoubleBuffered(true);
        setName("DeviceBaseEditorPanel");
        setLayout(new GridBagLayout());

        GridBagConstraints identificationPanelConstraints = new GridBagConstraints();
        GridBagConstraints mctSettingsConstraints = new GridBagConstraints();
        GridBagConstraints commPanelConstraints = new GridBagConstraints();
        GridBagConstraints dnpPanelConstraints = new GridBagConstraints();

        identificationPanelConstraints.insets = new Insets(3, 3, 3, 3);
        identificationPanelConstraints.gridy = 0;
        identificationPanelConstraints.gridx = 0;
        identificationPanelConstraints.weightx = 1.0;
        identificationPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
        identificationPanelConstraints.anchor = GridBagConstraints.NORTHWEST;

        commPanelConstraints.insets = new Insets(3, 3, 3, 3);
        commPanelConstraints.gridy = 1;
        commPanelConstraints.gridx = 0;
        commPanelConstraints.anchor = GridBagConstraints.NORTHWEST;
        commPanelConstraints.fill = GridBagConstraints.BOTH;
        commPanelConstraints.weighty = 1.0;

        mctSettingsConstraints.insets = new Insets(3, 3, 3, 3);
        mctSettingsConstraints.gridy = 2;
        mctSettingsConstraints.gridx = 0;
        mctSettingsConstraints.fill = GridBagConstraints.HORIZONTAL;
        mctSettingsConstraints.anchor = GridBagConstraints.NORTHWEST;

        dnpPanelConstraints.insets = new Insets(3, 3, 3, 3);
        dnpPanelConstraints.gridy = 3;
        dnpPanelConstraints.gridx = 0;
        dnpPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
        dnpPanelConstraints.anchor = GridBagConstraints.NORTHWEST;

        add(getIdentificationPanel(), identificationPanelConstraints);
        add(getJPanelMCTSettings(), mctSettingsConstraints);
        add(getDnpConfigPanel(), dnpPanelConstraints);
        add(getCommunicationPanel(), commPanelConstraints);

        try {
            initConnections();
        } catch (Throwable ivjExc) {
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

        if (getPhysicalAddressTextField().isVisible()
            && (getPhysicalAddressTextField().getText() == null
            || getPhysicalAddressTextField().getText().length() < 1)) {
            setErrorString("The Address text field must be filled in");
            return false;
        }

        if (getPhysicalAddressTextField().isVisible()) {
            PaoType paoType = deviceBase.getPaoType();
            try {
                int address = Integer.parseInt(getPhysicalAddressTextField().getText());
                // Verify Address is within range
                if (!dlcAddressRangeService.isValidEnforcedAddress(paoType, address)) {
                    
                    String rangeString = dlcAddressRangeService.rangeStringEnforced(paoType);
                    setErrorString("Invalid address. Device address range: " + rangeString);
                    
                    return false;
                }

                // Check address is not already used, user can override and continue
                if (deviceType.isMct() || deviceType.isRepeater()) {
                    if (!checkMCTAddresses(address, deviceBase.getPAObjectID())) {
                        return false;
                    }
                }

                // verify that there are no duplicate physical address for CCUs or RTUs on a dedicated channel
                LiteYukonPAObject port = ((LiteYukonPAObject) getPortComboBox().getSelectedItem());
                if (port != null && (!port.getPaoType().isDialupPort()) &&
                    (deviceType.isCcu() || deviceType.isRtu() || deviceType.isIon())) {
                    if (!checkForDuplicateAddresses(address, deviceBase.getPAObjectID(), port.getLiteID())) {
                        return false;
                    }
                }

            } catch (NumberFormatException nfe) {
                setErrorString(nfe.getMessage());
                return false;
            }
        }

        /* Check RFN Address settings */
        if (deviceBase instanceof RfnBase) {
            String serialNumber = getSerialNumberTextField().getText();
            String manufacturer = getManufacturerTextField().getText();
            String model = getModelTextField().getText();
            PaoType paoType = deviceBase.getPaoType();

            /*
             * Valid:
             * 1. All fields are blank.
             * 2. All fields are filled in.
             * Invalid:
             * 1. One or two of the three fields are blank.
             */
            boolean allBlank = StringUtils.isBlank(serialNumber) && StringUtils.isBlank(manufacturer) && StringUtils.isBlank(model);
            boolean allFilledIn = StringUtils.isNotBlank(serialNumber) && StringUtils.isNotBlank(manufacturer) && StringUtils.isNotBlank(model);
            if (!allBlank && !allFilledIn) {
                setErrorString("Serial Number, Manufacturer, and Model fields must all be empty or all be filled in.");
                return false;
            }
            /*Check if the device is of RFN type*/
            else if (paoType.isRfn()) {
                if (!(YukonValidationUtils.isRfnSerialNumberValid(serialNumber))) {
                    setErrorString("Device serial number must be alphanumeric and serial number length must be less than 30.");
                    return false;
                }
            }
            /* Check for duplicates */
            RfnDeviceDao rfnDeviceDao = YukonSpringHook.getBean("rfnDeviceDao", RfnDeviceDao.class);
            try {
                RfnDevice possibleDuplicate =
                    rfnDeviceDao.getDeviceForExactIdentifier(new RfnIdentifier(serialNumber,
                                                                               manufacturer,
                                                                               model));
                if (possibleDuplicate.getPaoIdentifier().getPaoId() != deviceBase.getPAObjectID()) {
                    setErrorString("Serial Number, Manufacturer, and Model fields must be unique among RFN Devices.");
                    return false;
                }
            } catch (NotFoundException e) {
                // IGNORE
            }
        }

        if (!isUniquePao(deviceName, deviceBase.getPaoType(), deviceBase.getPAObjectID())) {
            setErrorString("Name '" + deviceName
                           + "' is already in use.  Device Name must be unique for Category("
                           + deviceBase.getPaoType().getPaoCategory() + ") and PAOClass("
                           + deviceBase.getPaoType().getPaoClass() + ")");
            return false;
        }

        return true;
    }

    public void advancedButtonActionPerformed(ActionEvent actionEvent) {
        AdvancedPropertiesDialog dialog =
            new AdvancedPropertiesDialog(getAdvancedPanel(), "Advanced Dialup Properties");

        int result = dialog.showPanel(SwingUtil.getParentFrame(this));

        if (result == AdvancedPropertiesDialog.RESPONSE_ACCEPT) {
            fireInputUpdate(); // there has been a change!!!!
        }

        return;
    }

    private void setCarrierBaseValue(CarrierBase cBase) {
        Integer address = cBase.getDeviceCarrierSettings().getAddress();

        if (cBase instanceof Repeater900) {
            address = new Integer(address.intValue() - Repeater900.ADDRESS_OFFSET);
        } else if (cBase instanceof Repeater921) {
            address = new Integer(address.intValue() - Repeater921.ADDRESS_OFFSET);
        }

        if (cBase instanceof com.cannontech.database.data.device.MCT_Broadcast) {
            getPhysicalAddressLabel().setText("Lead Address:");
        } else if (cBase instanceof TwoWayLCR) {
            getPhysicalAddressLabel().setText("Serial Number:");
        }

        getPhysicalAddressLabel().setVisible(true);
        getPhysicalAddressTextField().setVisible(true);

        getPhysicalAddressTextField().setText(address.toString());
    }

    private void setIDLCBaseValue(IDLCBase idlcBase) {
        Integer address = idlcBase.getDeviceIDLCRemote().getAddress();
        getPhysicalAddressTextField().setText(address.toString());

        getPhysicalAddressLabel().setVisible(true);
        getPhysicalAddressTextField().setVisible(true);
    }

    private void setNonRemBaseValue(Object base) {
        getJLabelCCUAmpUseType().setVisible(false);
        getJComboBoxAmpUseType().setVisible(false);
        getPortLabel().setVisible(false);
        getPortComboBox().setVisible(false);
        getPasswordLabel().setVisible(false);
        getPasswordTextField().setVisible(false);

        if (base instanceof MCTBase) {
            MCTBase mctBase = (MCTBase) base;

            getJPanelMCTSettings().setVisible(true);
            if (DatabaseEditorUtil.isTagSupported(mctBase, ImmutableSet.of(PaoTag.DEVICE_CONFIGURATION))) {
                int id = mctBase.getPAObjectID();
                PaoType type = mctBase.getPaoType();
                SimpleDevice device = new SimpleDevice(id, type);
                DeviceConfigurationDao deviceConfigurationDao = YukonSpringHook.getBean(DeviceConfigurationDao.class);
                LightDeviceConfiguration config = deviceConfigurationDao.findConfigurationForDevice(device);
                if (config != null) {
                    getAssignedMctConfigLabel().setText(config.getName());
                } else {
                    getAssignedMctConfigLabel().setText(CtiUtilities.STRING_NONE);
                }
            } else {
                getAssignedMctConfigLabel().setText("Not Supported");
            }

            if (base instanceof MCT400SeriesBase) {
                getTOUComboBox().setVisible(false);
                getTOULabel().setVisible(false);
            }
        }

        getRouteLabel().setVisible(true);
        getRouteComboBox().setVisible(true);

        getPostCommWaitLabel().setVisible(false);
        getPostCommWaitSpinner().setVisible(false);
        getWaitLabel().setVisible(false);
        getSlaveAddressLabel().setVisible(false);
        getSlaveAddressComboBox().setVisible(false);

        if (getRouteComboBox().getModel().getSize() > 0) {
            getRouteComboBox().removeAllItems();
        }

        getTOUComboBox().addItem(CtiUtilities.STRING_NONE);

        IDatabaseCache cache = DefaultDatabaseCache.getInstance();

        synchronized (cache) {
            List<LiteYukonPAObject> routes = cache.getAllRoutes();

            int assignedRouteID = 0;
            if (base instanceof CarrierBase) {
                assignedRouteID = ((CarrierBase) base).getDeviceRoutes().getRouteID().intValue();

                for (LiteYukonPAObject liteRoute : routes) {
                    PaoType paoType = liteRoute.getPaoType();
                    if (paoType == PaoType.ROUTE_CCU || paoType == PaoType.ROUTE_MACRO) {
                        getRouteComboBox().addItem(liteRoute);
                        if (liteRoute.getYukonID() == assignedRouteID) {
                            getRouteComboBox().setSelectedItem(liteRoute);
                        }
                    }
                }
            } else {
                if (base instanceof LMGroupEmetcon) {
                    assignedRouteID =
                        ((LMGroupEmetcon) base).getLmGroupEmetcon().getRouteID().intValue();
                } else if (base instanceof LMGroupVersacom) {
                    assignedRouteID =
                        ((LMGroupVersacom) base).getLmGroupVersacom().getRouteID().intValue();
                } else if (base instanceof LMGroupRipple) {
                    assignedRouteID =
                        ((LMGroupRipple) base).getLmGroupRipple().getRouteID().intValue();
                }

                for (LiteYukonPAObject route : routes) {
                    getRouteComboBox().addItem(route);
                    if (route.getYukonID() == assignedRouteID) {
                        getRouteComboBox().setSelectedItem(route);
                    }
                }
            }
        }
    }

    private void setGridBaseValue(GridAdvisorBase gBase) {
        getRouteLabel().setVisible(false);
        getRouteComboBox().setVisible(false);
        getJLabelCCUAmpUseType().setVisible(false);
        getJComboBoxAmpUseType().setVisible(false);

        getPortLabel().setVisible(true);
        getPortComboBox().setVisible(true);
        getPostCommWaitLabel().setVisible(false);
        getPostCommWaitSpinner().setVisible(false);
        getDialupSettingsPanel().setVisible(false);
        getWaitLabel().setVisible(false);

        if (getRouteComboBox().getModel().getSize() > 0) {
            getRouteComboBox().removeAllItems();
        }

        int portID = 0;
        if (gBase.getDeviceDirectCommSettings().getPortID() != null) {
            portID = gBase.getDeviceDirectCommSettings().getPortID().intValue();
        }

        // Load the combo box
        IDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
        synchronized (cache) {
            List<LiteYukonPAObject> ports = cache.getAllPorts();
            if (getPortComboBox().getModel().getSize() > 0) {
                getPortComboBox().removeAllItems();
            }

            com.cannontech.database.data.lite.LiteYukonPAObject litePort = null;
            for (int i = 0; i < ports.size(); i++) {
                litePort = ports.get(i);
                getPortComboBox().addItem(litePort);

                if (ports.get(i).getYukonID() == portID) {
                    getPortComboBox().setSelectedItem(litePort);
                }
            }
        }

        getPhysicalAddressLabel().setVisible(true);
        getPhysicalAddressLabel().setText("Serial Number:");
        getPhysicalAddressTextField().setVisible(true);
        getPhysicalAddressTextField().setText(gBase.getDeviceAddress().getMasterAddress().toString());
        getPasswordLabel().setVisible(false);
        getPasswordTextField().setVisible(false);
        getSlaveAddressLabel().setVisible(false);
        getSlaveAddressComboBox().setVisible(false);

    }

    private void setRemoteBaseValue(RemoteBase rBase, PaoType paoType) {
        getRouteLabel().setVisible(false);
        getRouteComboBox().setVisible(false);
        getJLabelCCUAmpUseType().setVisible(false);
        getJComboBoxAmpUseType().setVisible(false);

        getPortLabel().setVisible(true);
        getPortComboBox().setVisible(true);
        getPostCommWaitLabel().setVisible(true);
        getPostCommWaitSpinner().setVisible(true);
        getWaitLabel().setVisible(true);

        if (getRouteComboBox().getModel().getSize() > 0) {
            getRouteComboBox().removeAllItems();
        }

        int portID = rBase.getDeviceDirectCommSettings().getPortID().intValue();
        // Load the combo box
        IDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
        synchronized (cache) {
            List<LiteYukonPAObject> ports = cache.getAllPorts();
            if (getPortComboBox().getModel().getSize() > 0) {
                getPortComboBox().removeAllItems();
            }

            com.cannontech.database.data.lite.LiteYukonPAObject litePort = null;
            for (int i = 0; i < ports.size(); i++) {
                litePort = ports.get(i);
                getPortComboBox().addItem(litePort);

                if (ports.get(i).getYukonID() == portID) {
                    getPortComboBox().setSelectedItem(litePort);

                    if (litePort.getPaoType().isDialupPort()) {
                        getDialupSettingsPanel().setVisible(true);
                    }
                }
            }
        }

        Integer postCommWait = null;
        String ampUse = null;
        if (rBase instanceof IDLCBase) {
            postCommWait = ((IDLCBase) rBase).getDeviceIDLCRemote().getPostCommWait();

            // only show CCUAmpUse when its a CCU-711 or CCU-710A
            if (rBase.getPaoType() == PaoType.CCU711 ||
                rBase.getPaoType() == PaoType.CCU710A) {
                ampUse = ((IDLCBase) rBase).getDeviceIDLCRemote().getCcuAmpUseType();
                getJLabelCCUAmpUseType().setVisible(true);
                getJComboBoxAmpUseType().setVisible(true);

                // add the extra options for CCU-711's only!
                if (rBase.getPaoType() == PaoType.CCU711) {
                    getJComboBoxAmpUseType().addItem(DeviceIDLCRemote.AMPUSE_ALTERNATING);
                    getJComboBoxAmpUseType().addItem(DeviceIDLCRemote.AMPUSE_DEF_1_FAIL_2);
                    getJComboBoxAmpUseType().addItem(DeviceIDLCRemote.AMPUSE_DEF_2_FAIL_1);
                    getJComboBoxAmpUseType().addItem(DeviceIDLCRemote.AMPUSE_ALT_FAILOVER);
                }

            }

        }

        // regardless of our type, we should set the advanced settings of the port
        getAdvancedPanel().setValue(rBase);

        if (getDialupSettingsPanel().isVisible()) {
            DeviceDialupSettings dDialup = rBase.getDeviceDialupSettings();

            if (dDialup != null) {
                String phoneNumber = dDialup.getPhoneNumber();

                if (phoneNumber != null) {
                    getPhoneNumberTextField().setText(phoneNumber);
                }
            }
        }

        if (rBase instanceof IEDBase) {
            // do not show the PostCommWait Items
            getPostCommWaitLabel().setVisible(false);
            getPostCommWaitSpinner().setVisible(false);
            getWaitLabel().setVisible(false);

            getPasswordLabel().setVisible(true);
            getPasswordTextField().setVisible(true);

            if (rBase instanceof WCTPTerminal) {
                getSenderLabel().setVisible(true);
                getSenderTextField().setVisible(true);
                getSecurityCodeLabel().setVisible(true);
                getSecurityCodeTextField().setVisible(true);

                getSenderTextField().setText(((TapTerminalBase) rBase).getDeviceTapPagingSettings().getSender());
                getSecurityCodeTextField().setText(((TapTerminalBase) rBase).getDeviceTapPagingSettings().getSecurityCode());
            } else if (rBase instanceof SNPPTerminal) {
                getSenderLabel().setText("Login: ");
                getSenderLabel().setVisible(true);
                getSenderTextField().setVisible(true);
                getSecurityCodeLabel().setText("Password: ");
                getSecurityCodeLabel().setVisible(true);
                getSecurityCodeTextField().setVisible(true);
                getPasswordLabel().setVisible(false);
                getPasswordTextField().setVisible(false);

                getSenderTextField().setText(((TapTerminalBase) rBase).getDeviceTapPagingSettings().getSender());
                getSecurityCodeTextField().setText(((TapTerminalBase) rBase).getDeviceTapPagingSettings().getSecurityCode());
            }

            String password;
            if (rBase instanceof WCTPTerminal) {
                password = ((TapTerminalBase) rBase).getDeviceTapPagingSettings().getPOSTPath();
            } else {
                password = ((IEDBase) rBase).getDeviceIED().getPassword();
            }

            if (CtiUtilities.STRING_NONE.equalsIgnoreCase(password)
                || "None".equalsIgnoreCase(password) // keep the old (none) value valid
                || "0".equalsIgnoreCase(password)) // keep the old '0' value valid
            {
                getPasswordTextField().setText("");
            } else {
                getPasswordTextField().setText(password);
            }

            if (rBase instanceof Schlumberger || paoType == PaoType.ALPHA_PPLUS
                || paoType == PaoType.TRANSDATA_MARKV || rBase instanceof KV) {
                getSlaveAddressLabel().setVisible(true);
                getSlaveAddressComboBox().setVisible(true);

                String slaveAddress = ((IEDBase) rBase).getDeviceIED().getSlaveAddress();
                getSlaveAddressComboBox().setSelectedItem(slaveAddress);
            } else if (rBase instanceof RTM) {
                getPhysicalAddressLabel().setVisible(true);
                // getPhysicalAddressLabel().setText("RTM Address:");
                getPhysicalAddressLabel().setText("Physical Address:");
                getPhysicalAddressTextField().setVisible(true);
                ivjPhysicalAddressTextField.setDocument(new LongRangeDocument(0, 15));
                getPhysicalAddressTextField().setText(((IEDBase) rBase).getDeviceIED().getSlaveAddress());

                getSlaveAddressLabel().setVisible(false);
                getSlaveAddressComboBox().setVisible(false);

                getPasswordLabel().setVisible(false);
                getPasswordTextField().setVisible(false);

            } else if (rBase instanceof Sixnet) {
                /**** BEGIN SUPER HACK --- Special case for Sixnet Devices!! ****/
                getSlaveAddressLabel().setText("Station Address:");
                getSlaveAddressLabel().setVisible(true);
                getSlaveAddressComboBox().setVisible(true);
                getSlaveAddressComboBox().setEditable(true);
                getSlaveAddressComboBox().removeAllItems();

                JTextFieldComboEditor e = new JTextFieldComboEditor();
                e.setDocument(new LongRangeDocument(0, 16000));
                e.addCaretListener(eventHandler);
                getSlaveAddressComboBox().setEditor(e);

                String slaveAddress = ((IEDBase) rBase).getDeviceIED().getSlaveAddress();
                getSlaveAddressComboBox().addItem(slaveAddress);
                getSlaveAddressComboBox().setSelectedItem(slaveAddress);

                getPasswordLabel().setText("Log File:");
                /**** END SUPER HACK --- Special case for Sixnet Devices!! ****/
            }
            else
            {
                getSlaveAddressLabel().setVisible(false);
                getSlaveAddressComboBox().setVisible(false);
            }
        }
        else if (rBase instanceof DNPBase || rBase instanceof IonBase)
        {
            AddressBase addressBase = (AddressBase) rBase;    //both DNPBase and IonBase extend AddressBase
                    
            getPhysicalAddressLabel().setVisible(true);
            getPhysicalAddressLabel().setText("Master Address:");
            getPhysicalAddressTextField().setDocument(new LongRangeDocument(0, 65535));
            getPhysicalAddressTextField().setVisible(true);
            getPhysicalAddressTextField().setText(addressBase.getDeviceAddress().getMasterAddress().toString());

            getSlaveAddressLabel().setVisible(true);
            getSlaveAddressComboBox().setVisible(true);

            // create a new editor for our combobox so we can set the document
            getSlaveAddressComboBox().setEditable(true);
            getSlaveAddressComboBox().removeAllItems();
            JTextFieldComboEditor editor = new JTextFieldComboEditor();
            editor.setDocument(new LongRangeDocument(0, 65535));
            editor.addCaretListener(eventHandler); // be sure to fireInputUpdate() messages!

            getSlaveAddressComboBox().setEditor(editor);
            getSlaveAddressComboBox().addItem(addressBase.getDeviceAddress().getSlaveAddress());

            getPostCommWaitSpinner().setValue(addressBase.getDeviceAddress().getPostCommWait());

            getPasswordLabel().setVisible(false);
            getPasswordTextField().setVisible(false);
        }
        else if (rBase instanceof Series5Base)
        {
            getPhysicalAddressLabel().setVisible(true);
            getPhysicalAddressLabel().setText("Address:");
            getPhysicalAddressTextField().setVisible(true);
            getPhysicalAddressTextField().setDocument(new DoubleRangeDocument(1, 127));
            getPhysicalAddressTextField().setText(((Series5Base) rBase).getSeries5().getSlaveAddress().toString());

            getSlaveAddressLabel().setVisible(false);
            getSlaveAddressComboBox().setVisible(false);

            getControlInhibitCheckBox().setVisible(true);
            ivjControlInhibitCheckBox.setText("Disable Verification");
            if (((Series5Base) rBase).getVerification().getDisable().compareTo("Y") == 0) {
                getControlInhibitCheckBox().setSelected(true);
            } else {
                getControlInhibitCheckBox().setSelected(false);
            }

            getPostCommWaitSpinner().setValue(((Series5Base) rBase).getSeries5().getPostCommWait());

            getPasswordLabel().setVisible(false);
            getPasswordTextField().setVisible(false);
        }
        else if (rBase instanceof RTCBase)
        {
            getPhysicalAddressLabel().setVisible(true);
            getPhysicalAddressLabel().setText("Physical Address:");
            getPhysicalAddressTextField().setVisible(true);
            getPhysicalAddressTextField().setDocument(new DoubleRangeDocument(0, 15));
            getPhysicalAddressTextField().setText(((RTCBase) rBase).getDeviceRTC().getRTCAddress().toString());

            getSlaveAddressLabel().setText("Listen Before Talk: ");
            getSlaveAddressLabel().setVisible(true);
            getSlaveAddressComboBox().setVisible(true);

            // create a new editor for our combobox so we can set the document
            getSlaveAddressComboBox().setEditable(false);
            getSlaveAddressComboBox().removeAllItems();
            getSlaveAddressComboBox().addItem(RTCBase.LBT3);
            getSlaveAddressComboBox().addItem(RTCBase.LBT2);
            getSlaveAddressComboBox().addItem(RTCBase.LBT1);
            getSlaveAddressComboBox().addItem(RTCBase.LBT0);
            getSlaveAddressComboBox().setSelectedItem(RTCBase.getLBTModeString(((RTCBase) rBase).getDeviceRTC().getLBTMode()));

            getPostCommWaitSpinner().setVisible(false);
            getPostCommWaitLabel().setVisible(false);
            getWaitLabel().setVisible(false);
            getPasswordLabel().setVisible(false);
            getPasswordTextField().setVisible(false);

            getControlInhibitCheckBox().setVisible(true);
            ivjControlInhibitCheckBox.setText("Disable Code Verification");
            if (((RTCBase) rBase).getDeviceRTC().getDisableVerifies().compareTo("Y") == 0) {
                getControlInhibitCheckBox().setSelected(true);
            } else {
                getControlInhibitCheckBox().setSelected(false);
            }

        } else if (rBase instanceof CCU721) {
            getPhysicalAddressLabel().setVisible(false);
            getPhysicalAddressLabel().setText("Master Address:");
            getPhysicalAddressTextField().setVisible(false);
            getPhysicalAddressTextField().setText(((CCU721) rBase).getDeviceAddress().getMasterAddress().toString());

            getSlaveAddressLabel().setVisible(true);
            getSlaveAddressComboBox().setVisible(true);

            // create a new editor for our combobox so we can set the document
            getSlaveAddressComboBox().setEditable(true);
            getSlaveAddressComboBox().removeAllItems();
            JTextFieldComboEditor editor = new JTextFieldComboEditor();
            editor.addCaretListener(eventHandler); // be sure to fireInputUpdate() messages!
            getSlaveAddressComboBox().setEditor(editor);
            getSlaveAddressComboBox().addItem(((CCU721) rBase).getDeviceAddress().getSlaveAddress());

            getPostCommWaitSpinner().setVisible(false);
            getPostCommWaitLabel().setVisible(false);
            getWaitLabel().setVisible(false);
            getPasswordLabel().setVisible(false);
            getPasswordTextField().setVisible(false);

        } else {
            getPasswordLabel().setVisible(false);
            getPasswordTextField().setVisible(false);
            getSlaveAddressLabel().setVisible(false);
            getSlaveAddressComboBox().setVisible(false);
        }

        if (rBase.getPaoType() == PaoType.RTU_DNP) {
            getDnpConfigPanel().setVisible(true);
            int id = rBase.getPAObjectID();
            PaoType type = rBase.getPaoType();
            SimpleDevice device = new SimpleDevice(id, type);
            DeviceConfigurationDao deviceConfigurationDao = YukonSpringHook.getBean(DeviceConfigurationDao.class);
            LightDeviceConfiguration config = deviceConfigurationDao.findConfigurationForDevice(device);
            DNPConfiguration dnpConfig = null;
            if (config != null) {
                DeviceConfiguration configuration = deviceConfigurationDao.getDeviceConfiguration(config.getConfigurationId());

                dnpConfig = deviceConfigurationDao.getDnpConfiguration(configuration);
            }
            if (dnpConfig != null) {
                getAssignedDnpConfigLabel().setText(dnpConfig.getName());

                int internalRetries = dnpConfig.getInternalRetries();
                String timeOffset = dnpConfig.getTimeOffset();
                boolean enableTimesyncs = dnpConfig.isEnableDnpTimesyncs();
                boolean omitTimeRequest = dnpConfig.isOmitTimeRequest();
                boolean unsolicitedEnabled1 = dnpConfig.isEnableUnsolicitedMessageClass1();
                boolean unsolicitedEnabled2 = dnpConfig.isEnableUnsolicitedMessageClass2();
                boolean unsolicitedEnabled3 = dnpConfig.isEnableUnsolicitedMessageClass3();
                String unsolicitedEnabled = Joiner.on(", ").join(unsolicitedEnabled1, unsolicitedEnabled2, unsolicitedEnabled3);
                
                getInternalRetriesValueLabel().setText(Integer.toString(internalRetries));
                getTimeOffsetValueLabel().setText(timeOffset);
                getTimesyncValueLabel().setText(Boolean.toString(enableTimesyncs));
                getOmitTimeRequestValueLabel().setText(Boolean.toString(omitTimeRequest));
                getUnsolicitedValueLabel().setText(unsolicitedEnabled);
            } else {
                getAssignedDnpConfigLabel().setText(CtiUtilities.STRING_NONE);
                getAssignedDnpConfigLabel().setForeground(Color.RED);
                getInternalRetriesValueLabel().setText("MISSING!");
                getInternalRetriesValueLabel().setForeground(Color.RED);
                getTimeOffsetValueLabel().setText("MISSING!");
                getTimeOffsetValueLabel().setForeground(Color.RED);
                getTimesyncValueLabel().setText("MISSING!");
                getTimesyncValueLabel().setForeground(Color.RED);
                getOmitTimeRequestValueLabel().setText("MISSING!");
                getOmitTimeRequestValueLabel().setForeground(Color.RED);
                getUnsolicitedValueLabel().setText("MISSING!");
                getUnsolicitedValueLabel().setForeground(Color.RED);
            }
        }

        if (postCommWait != null) {
            getPostCommWaitSpinner().setValue(postCommWait);
        }

        if (ampUse != null) {
            getJComboBoxAmpUseType().setSelectedItem(ampUse);
        }

    }

    @Override
    public void setValue(Object val)
    {
        deviceBase = (DeviceBase) val;

        deviceType = deviceBase.getPaoType();
        PaoType paoType = deviceBase.getPaoType();

        String typeDisplayString = paoType.getPaoTypeName();
        // Override default type string for TapTerminal
        if (paoType == PaoType.TAPTERMINAL) {
            typeDisplayString = "PAGING TAP TERMINAL";
        }
        getTypeTextField().setText(typeDisplayString);
        getNameTextField().setText(deviceBase.getPAOName());

        SwingUtil.setCheckBoxState(getDisableFlagCheckBox(), deviceBase.getPAODisableFlag());
        SwingUtil.setCheckBoxState(getControlInhibitCheckBox(), deviceBase.getDevice().getControlInhibit());

        // This is a bit ugly
        // The address could come from one of three different types of devices even though they all have one
        // Note also getValue(DBPersistent)
        if (val instanceof CarrierBase) {
            setCarrierBaseValue((CarrierBase) val);
        } else if (val instanceof IDLCBase) {
            setIDLCBaseValue((IDLCBase) val);
        } else {
            if (paoType.getPaoClass() == PaoClass.VIRTUAL ||
                    paoType.getPaoClass() == PaoClass.RFMESH ||
                    paoType.isEcobee() ||
                    paoType.isHoneywell() || 
                    paoType.isNest()) {
                getCommunicationPanel().setVisible(false);
            }

            getPhysicalAddressLabel().setVisible(false);
            getPhysicalAddressTextField().setVisible(false);

            if (paoType.getPaoClass() == PaoClass.RFMESH) {
                RfnBase rfnBase = (RfnBase) deviceBase;
                getSerialNumberLabel().setVisible(true);
                getSerialNumberTextField().setVisible(true);
                getSerialNumberTextField().setText(rfnBase.getRfnAddress().getSerialNumber());
                getManufacturerLabel().setVisible(true);
                getManufacturerTextField().setVisible(true);
                getManufacturerTextField().setText(rfnBase.getRfnAddress().getManufacturer());
                getModelLabel().setVisible(true);
                getModelTextField().setVisible(true);
                getModelTextField().setText(rfnBase.getRfnAddress().getModel());
            }
        }

        if (paoType.getPaoClass() == PaoClass.GROUP) {
            getDisableFlagCheckBox().setVisible(false);
        }
        else {
            getDisableFlagCheckBox().setVisible(true);
        }

        getDialupSettingsPanel().setVisible(false);

        if (val instanceof RemoteBase) {
            setRemoteBaseValue((RemoteBase) val, deviceType);
        } else if (val instanceof GridAdvisorBase) {
            setGridBaseValue((GridAdvisorBase) val);
        } else {
            setNonRemBaseValue(val);
        }
        LiteYukonPAObject port = ((LiteYukonPAObject) getPortComboBox().getSelectedItem());

        PaoPropertyDao propertyDao =
            YukonSpringHook.getBean("paoPropertyDao", PaoPropertyDao.class);
        if (port != null) {
            if (PaoType.TCPPORT == port.getPaoType() && deviceType.isTcpPortEligible())
            {
                int id = deviceBase.getPAObjectID();
                String value = null;
                try {
                    PaoProperty prop = propertyDao.getByIdAndName(id, PaoPropertyName.TcpIpAddress);
                    value = prop.getPropertyValue();
                } catch (EmptyResultDataAccessException e) {
                    value = CtiUtilities.STRING_NONE;
                }
                getTcpIpAddressTextField().setText(value);

                try {
                    PaoProperty prop = propertyDao.getByIdAndName(id, PaoPropertyName.TcpPort);
                    value = prop.getPropertyValue();
                } catch (EmptyResultDataAccessException e) {
                    value = CtiUtilities.STRING_NONE;
                }
                getTcpPortTextField().setText(value);

                getTcpIpAddressLabel().setVisible(true);
                getTcpIpAddressTextField().setVisible(true);
                getTcpPortLabel().setVisible(true);
                getTcpPortTextField().setVisible(true);
            } else {
                getTcpIpAddressLabel().setVisible(false);
                getTcpIpAddressTextField().setVisible(false);
                getTcpPortLabel().setVisible(false);
                getTcpPortTextField().setVisible(false);
            }
        }
    }

    /**
     * This method initializes jPanelMCTSettings
     */
    private JPanel getJPanelMCTSettings() {
        if (jPanelMCTSettings == null) {
            jPanelMCTSettings = new JPanel();

            jPanelMCTSettings.setBorder(BorderFactory
                .createTitledBorder(null,
                                    "MCT Additional Settings",
                                    TitledBorder.DEFAULT_JUSTIFICATION,
                                    TitledBorder.DEFAULT_POSITION,
                                    new Font("Dialog", Font.BOLD, 12),
                                    Color.black));
            jPanelMCTSettings.setName("JPanelMCTSettings");
            jPanelMCTSettings.setLayout(new GridBagLayout());
            jPanelMCTSettings.setVisible(false);

            GridBagConstraints touLabelContstraints = new GridBagConstraints();
            GridBagConstraints configLabelConstraints = new GridBagConstraints();
            GridBagConstraints touComboBoxConstraints = new GridBagConstraints();
            GridBagConstraints assignedConfigLabelConstraints = new GridBagConstraints();

            touComboBoxConstraints.insets = new Insets(3, 3, 3, 3);
            touComboBoxConstraints.fill = GridBagConstraints.HORIZONTAL;
            touComboBoxConstraints.weightx = 1.0;
            touComboBoxConstraints.gridy = 1;
            touComboBoxConstraints.gridx = 1;
            touComboBoxConstraints.anchor = GridBagConstraints.NORTHWEST;

            touLabelContstraints.insets = new Insets(3, 3, 3, 3);
            touLabelContstraints.gridy = 1;
            touLabelContstraints.gridx = 0;
            touLabelContstraints.anchor = GridBagConstraints.NORTHWEST;

            assignedConfigLabelConstraints.insets = new Insets(3, 3, 3, 3);
            assignedConfigLabelConstraints.fill = GridBagConstraints.HORIZONTAL;
            assignedConfigLabelConstraints.gridy = 0;
            assignedConfigLabelConstraints.gridx = 1;
            assignedConfigLabelConstraints.anchor = GridBagConstraints.NORTHWEST;

            configLabelConstraints.insets = new Insets(3, 3, 3, 3);
            configLabelConstraints.gridy = 0;
            configLabelConstraints.gridx = 0;
            configLabelConstraints.anchor = GridBagConstraints.NORTHWEST;

            jPanelMCTSettings.add(getMctConfigLabel(), configLabelConstraints);
            jPanelMCTSettings.add(getTOULabel(), touLabelContstraints);
            jPanelMCTSettings.add(getTOUComboBox(), touComboBoxConstraints);
            jPanelMCTSettings.add(getAssignedMctConfigLabel(), assignedConfigLabelConstraints);
        }
        return jPanelMCTSettings;
    }

    
}