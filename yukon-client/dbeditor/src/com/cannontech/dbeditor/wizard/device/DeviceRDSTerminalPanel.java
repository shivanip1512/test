package com.cannontech.dbeditor.wizard.device;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.apache.commons.lang.StringUtils;

import com.cannontech.common.gui.unchanging.DoubleRangeDocument;
import com.cannontech.common.gui.unchanging.LongRangeDocument;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.common.gui.util.TitleBorder;
import com.cannontech.database.data.device.RDSTerminal;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;

public class DeviceRDSTerminalPanel extends DataInputPanel implements
        CaretListener, ActionListener {

    private JPanel staticPaoInfoPanel = null;
    private JLabel siteAddressLabel = null;
    private JTextField siteAddress = null;
    private JLabel encoderAddressLabel = null;
    private JTextField encoderAddress = null;
    private JLabel transmitSpeedLabel = null;
    private JTextField transmitSpeed = null;
    private JLabel groupTypeLabel = null;
    private JTextField groupType = null;

    private JLabel paoInfoIpPortLabel = null;
    private JTextField paoInfoIpPort = null;
    private JLabel paoInfoIpAddressLabel = null;
    private JTextField paoInfoIpAddress = null;
    
    private JLabel paoInfoSpidLabel = null;
    private JTextField paoInfoSpid = null;
    private JLabel paoInfoAidRepeatPeriodLabel = null;
    private JTextField paoInfoAidRepeatPeriod = null;

    private JLabel errorMessageLabel = null;

    public DeviceRDSTerminalPanel() {
        super();
        initialize();
    }

    /**
     * Method to handle events for the CaretListener interface.
     * @param e event.CaretEvent
     */
    public void caretUpdate(CaretEvent caretEvent) {
        Object source = caretEvent.getSource();
        if (source == getSiteAddress() || 
            source == getEncoderAddress() || 
            source == getTransmitSpeed() || 
            source == getGroupType() ||
            source == getPaoInfoIpAddress() ||
            source == getPaoInfoIpPort() ||
            source == getPaoInfoSpid() ||
            source == getPaoInfoAidRepeatPeriod()) {
            fireInputUpdate();
        }
    }

    private JPanel getStaticPaoInfoPanel() {
        if (staticPaoInfoPanel == null) {
            try {
                staticPaoInfoPanel = new JPanel();
                staticPaoInfoPanel.setName("Pao Info Panel");
                staticPaoInfoPanel.setLayout(new GridBagLayout());

                TitleBorder titleBorder = new TitleBorder();
                titleBorder.setTitleFont(new Font("Arial", 1, 12));
                titleBorder.setTitle("Pao Info");
                staticPaoInfoPanel.setBorder(titleBorder);

                GridBagConstraints constraints = new GridBagConstraints();
                constraints.gridx = 0;
                constraints.gridy = 0;
                constraints.fill = GridBagConstraints.HORIZONTAL;
                constraints.anchor = GridBagConstraints.WEST;
                staticPaoInfoPanel.add(getSiteAddressLabel(), constraints);

                constraints = new GridBagConstraints();
                constraints.gridx = 1;
                constraints.gridy = 0;
                constraints.fill = GridBagConstraints.HORIZONTAL;
                constraints.anchor = GridBagConstraints.WEST;
                constraints.insets = new Insets(5, 8, 5, 0);
                constraints.weightx = 1.0;
                staticPaoInfoPanel.add(getSiteAddress(), constraints);

                constraints = new GridBagConstraints();
                constraints.gridx = 0;
                constraints.gridy = 1;
                constraints.fill = GridBagConstraints.HORIZONTAL;
                constraints.anchor = GridBagConstraints.WEST;
                staticPaoInfoPanel.add(getEncoderAddressLabel(), constraints);

                constraints = new GridBagConstraints();
                constraints.gridx = 1;
                constraints.gridy = 1;
                constraints.fill = GridBagConstraints.HORIZONTAL;
                constraints.anchor = GridBagConstraints.WEST;
                constraints.insets = new Insets(5, 8, 5, 0);
                constraints.weightx = 1.0;
                staticPaoInfoPanel.add(getEncoderAddress(), constraints);

                constraints = new GridBagConstraints();
                constraints.gridx = 0;
                constraints.gridy = 2;
                constraints.fill = GridBagConstraints.HORIZONTAL;
                constraints.anchor = GridBagConstraints.WEST;
                staticPaoInfoPanel.add(getTransmitSpeedLabel(), constraints);

                constraints = new GridBagConstraints();
                constraints.gridx = 1;
                constraints.gridy = 2;
                constraints.fill = GridBagConstraints.HORIZONTAL;
                constraints.anchor = GridBagConstraints.WEST;
                constraints.insets = new Insets(5, 8, 5, 0);
                constraints.weightx = 1.0;
                staticPaoInfoPanel.add(getTransmitSpeed(), constraints);

                constraints = new GridBagConstraints();
                constraints.gridx = 0;
                constraints.gridy = 3;
                constraints.fill = GridBagConstraints.HORIZONTAL;
                constraints.anchor = GridBagConstraints.WEST;
                staticPaoInfoPanel.add(getGroupTypeLabel(), constraints);

                constraints = new GridBagConstraints();
                constraints.gridx = 1;
                constraints.gridy = 3;
                constraints.fill = GridBagConstraints.HORIZONTAL;
                constraints.anchor = GridBagConstraints.WEST;
                constraints.insets = new Insets(5, 8, 5, 0);
                constraints.weightx = 1.0;
                staticPaoInfoPanel.add(getGroupType(), constraints);

                
                constraints = new GridBagConstraints();
                constraints.gridx = 0;
                constraints.gridy = 4;
                constraints.fill = GridBagConstraints.HORIZONTAL;
                constraints.anchor = GridBagConstraints.WEST;
                staticPaoInfoPanel.add(getPaoInfoIpAddressLabel(), constraints);

                constraints = new GridBagConstraints();
                constraints.gridx = 1;
                constraints.gridy = 4;
                constraints.fill = GridBagConstraints.HORIZONTAL;
                constraints.anchor = GridBagConstraints.WEST;
                constraints.insets = new Insets(5, 8, 5, 0);
                constraints.weightx = 1.0;
                staticPaoInfoPanel.add(getPaoInfoIpAddress(), constraints);

                constraints = new GridBagConstraints();
                constraints.gridx = 0;
                constraints.gridy = 5;
                constraints.fill = GridBagConstraints.HORIZONTAL;
                constraints.anchor = GridBagConstraints.WEST;
                staticPaoInfoPanel.add(getPaoInfoIpPortLabel(), constraints);

                constraints = new GridBagConstraints();
                constraints.gridx = 1;
                constraints.gridy = 5;
                constraints.fill = GridBagConstraints.HORIZONTAL;
                constraints.anchor = GridBagConstraints.WEST;
                constraints.insets = new Insets(5, 8, 5, 0);
                constraints.weightx = 1.0;
                staticPaoInfoPanel.add(getPaoInfoIpPort(), constraints);

                
                constraints = new GridBagConstraints();
                constraints.gridx = 0;
                constraints.gridy = 6;
                constraints.fill = GridBagConstraints.HORIZONTAL;
                constraints.anchor = GridBagConstraints.WEST;
                staticPaoInfoPanel.add(getPaoInfoAidRepeatPeriodLabel(), constraints);

                constraints = new GridBagConstraints();
                constraints.gridx = 1;
                constraints.gridy = 6;
                constraints.fill = GridBagConstraints.HORIZONTAL;
                constraints.anchor = GridBagConstraints.WEST;
                constraints.insets = new Insets(5, 8, 5, 0);
                constraints.weightx = 1.0;
                staticPaoInfoPanel.add(getPaoInfoAidRepeatPeriod(), constraints);
                
                constraints = new GridBagConstraints();
                constraints.gridx = 0;
                constraints.gridy = 7;
                constraints.fill = GridBagConstraints.HORIZONTAL;
                constraints.anchor = GridBagConstraints.WEST;
                staticPaoInfoPanel.add(getPaoInfoSpidLabel(), constraints);

                constraints = new GridBagConstraints();
                constraints.gridx = 1;
                constraints.gridy = 7;
                constraints.fill = GridBagConstraints.HORIZONTAL;
                constraints.anchor = GridBagConstraints.WEST;
                constraints.insets = new Insets(5, 8, 5, 0);
                constraints.weightx = 1.0;
                staticPaoInfoPanel.add(getPaoInfoSpid(), constraints);

                constraints = new GridBagConstraints();
                constraints.gridx = 0;
                constraints.gridy = 6;
                constraints.gridwidth = 2;
                constraints.anchor = GridBagConstraints.WEST;
                constraints.insets = new java.awt.Insets(0, 2, 0, 2);
                staticPaoInfoPanel.add(getErrorMessageLabel(), constraints);    
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return staticPaoInfoPanel;
    }

    private JLabel getSiteAddressLabel() {
        if (siteAddressLabel == null) {
            try {
                siteAddressLabel = new JLabel();
                siteAddressLabel.setName("SiteAddressLabel");
                siteAddressLabel.setFont(new Font("dialog", 0, 14));
                siteAddressLabel.setText("Site Address:");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return siteAddressLabel;
    }

    private JTextField getSiteAddress() {
        if (siteAddress == null) {
            try {
                siteAddress = new JTextField();
                siteAddress.setName("SiteAddress");
                siteAddress.setFont(new Font("sansserif", 0, 14));
                siteAddress.setColumns(10);
                siteAddress.setDocument(new LongRangeDocument(0, 1023));
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return siteAddress;
    }

    private JLabel getEncoderAddressLabel() {
        if (encoderAddressLabel == null) {
            try {
                encoderAddressLabel = new JLabel();
                encoderAddressLabel.setName("EncoderAddressLabel");
                encoderAddressLabel.setFont(new Font("dialog", 0, 14));
                encoderAddressLabel.setText("Encoder Address:");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return encoderAddressLabel;
    }

    private JTextField getEncoderAddress() {
        if (encoderAddress == null) {
            try {
                encoderAddress = new JTextField();
                encoderAddress.setName("EncoderAddress");
                encoderAddress.setFont(new Font("sansserif", 0, 14));
                encoderAddress.setColumns(10);
                encoderAddress.setDocument(new LongRangeDocument(0, 63));
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return encoderAddress;
    }

    private JLabel getTransmitSpeedLabel() {
        if (transmitSpeedLabel == null) {
            try {
                transmitSpeedLabel = new JLabel();
                transmitSpeedLabel.setName("TranmsitterSpeedLabel");
                transmitSpeedLabel.setFont(new Font("dialog", 0, 14));
                transmitSpeedLabel.setText("Transmit Speed:");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return transmitSpeedLabel;
    }

    private JTextField getTransmitSpeed() {
        if (transmitSpeed == null) {
            try {
                transmitSpeed = new JTextField();
                transmitSpeed.setName("TransmitSpeed");
                transmitSpeed.setFont(new Font("sansserif", 0, 14));
                transmitSpeed.setColumns(10);
                transmitSpeed.setDocument(new DoubleRangeDocument(0, 11.99999));
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return transmitSpeed;
    }

    private JLabel getGroupTypeLabel() {
        if (groupTypeLabel == null) {
            try {
                groupTypeLabel = new JLabel();
                groupTypeLabel.setName("GroupTypeLabel");
                groupTypeLabel.setFont(new Font("dialog", 0, 14));
                groupTypeLabel.setText("Group Type:");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return groupTypeLabel;
    }

    private JTextField getGroupType() {
        if (groupType == null) {
            try {
                groupType = new JTextField();
                groupType.setName("GroupType");
                groupType.setFont(new Font("sansserif", 0, 14));
                groupType.setColumns(10);
                groupType.setDocument(new TextFieldDocument(3));
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return groupType;
    }

    private JLabel getPaoInfoIpPortLabel() {
        if (paoInfoIpPortLabel == null) {
            try {
                paoInfoIpPortLabel = new JLabel();
                paoInfoIpPortLabel.setName("PaoInfoIpPortLabel");
                paoInfoIpPortLabel.setFont(new Font("dialog", 0, 14));
                paoInfoIpPortLabel.setText("IP Port:");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return paoInfoIpPortLabel;
    }

    private JTextField getPaoInfoIpPort() {
        if (paoInfoIpPort == null) {
            try {
                paoInfoIpPort = new JTextField();
                paoInfoIpPort.setName("PaoInfoIpPort");
                paoInfoIpPort.setFont(new Font("sansserif", 0, 14));
                paoInfoIpPort.setColumns(20);
                paoInfoIpPort.setDocument(new TextFieldDocument(128));
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return paoInfoIpPort;
    }
    
    private JLabel getPaoInfoSpidLabel() {
        if (paoInfoSpidLabel == null) {
            try {
                paoInfoSpidLabel = new JLabel();
                paoInfoSpidLabel.setName("PaoInfoSpidLabel");
                paoInfoSpidLabel.setFont(new Font("dialog", 0, 14));
                paoInfoSpidLabel.setText("SPID:");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return paoInfoSpidLabel;
    }
    
    private JTextField getPaoInfoSpid() {
        if (paoInfoSpid == null) {
            try {
                paoInfoSpid = new JTextField();
                paoInfoSpid.setName("PaoInfoSpid");
                paoInfoSpid.setFont(new Font("sansserif", 0, 14));
                paoInfoSpid.setColumns(20);
                paoInfoSpid.setDocument(new LongRangeDocument(0, 65535));
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return paoInfoSpid;
    }
    
    private JLabel getPaoInfoAidRepeatPeriodLabel() {
        if (paoInfoAidRepeatPeriodLabel == null) {
            try {
                paoInfoAidRepeatPeriodLabel = new JLabel();
                paoInfoAidRepeatPeriodLabel.setName("PaoInfoAidRepeatPeriodLabel");
                paoInfoAidRepeatPeriodLabel.setFont(new Font("dialog", 0, 14));
                paoInfoAidRepeatPeriodLabel.setText("AID Repeat Period:");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return paoInfoAidRepeatPeriodLabel;
    }
    
    private JTextField getPaoInfoAidRepeatPeriod() {
        if (paoInfoAidRepeatPeriod == null) {
            try {
                paoInfoAidRepeatPeriod = new JTextField();
                paoInfoAidRepeatPeriod.setName("PaoInfoAidRepeatPeriod");
                paoInfoAidRepeatPeriod.setFont(new Font("sansserif", 0, 14));
                paoInfoAidRepeatPeriod.setColumns(20);
                paoInfoAidRepeatPeriod.setDocument(new LongRangeDocument(0, 3600));
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return paoInfoAidRepeatPeriod;
    }

    private JLabel getPaoInfoIpAddressLabel() {
        if (paoInfoIpAddressLabel == null) {
            try {
                paoInfoIpAddressLabel = new JLabel();
                paoInfoIpAddressLabel.setName("PaoInfoIpAddressLabel");
                paoInfoIpAddressLabel.setFont(new Font("dialog", 0, 14));
                paoInfoIpAddressLabel.setText("IP Address:");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return paoInfoIpAddressLabel;
    }

    private JTextField getPaoInfoIpAddress() {
        if (paoInfoIpAddress == null) {
            try {
                paoInfoIpAddress = new JTextField();
                paoInfoIpAddress.setName("PaoInfoIpAddress");
                paoInfoIpAddress.setFont(new Font("sansserif", 0, 14));
                paoInfoIpAddress.setColumns(20);
                paoInfoIpAddress.setDocument(new TextFieldDocument(128));
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return paoInfoIpAddress;
    }

    private JLabel getErrorMessageLabel() {
        if (errorMessageLabel == null) {
            try {
                errorMessageLabel = new javax.swing.JLabel();
                errorMessageLabel.setName("ErrorMessageLabel");
                errorMessageLabel.setFont(new Font("Arial", 1, 10));
                errorMessageLabel.setHorizontalTextPosition(SwingConstants.CENTER);
                errorMessageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return errorMessageLabel;
    }

    public Dimension getPreferredSize() {
        return new Dimension(350, 200);
    }

    @Override
    public Object getValue(Object val) {
        Object value = null;
        if (val instanceof SmartMultiDBPersistent) {
            value = ((SmartMultiDBPersistent) val).getOwnerDBPersistent();
        } else {
            value = val;
        }
        RDSTerminal rdsTerminal = (RDSTerminal) value;

        String siteAddress = getSiteAddress().getText();
        rdsTerminal.getSiteAddress().setValue(siteAddress);

        String encoderAddress = getEncoderAddress().getText();
        rdsTerminal.getEncoderAddress().setValue(encoderAddress);

        String transmitSpeed = getTransmitSpeed().getText();
        rdsTerminal.getTransmitSpeed().setValue(transmitSpeed);

        String groupType = getGroupType().getText();
        rdsTerminal.getGroupType().setValue(groupType);

        String paoInfoIpAddress = getPaoInfoIpAddress().getText();
        rdsTerminal.getRdsIpAddress().setValue(paoInfoIpAddress);
        
        String paoInfoIpPort = getPaoInfoIpPort().getText();
        rdsTerminal.getRdsIpPort().setValue(paoInfoIpPort);
        
        String paoInfoSpid = getPaoInfoSpid().getText();
        rdsTerminal.getSpid().setValue(paoInfoSpid);
        
        String paoInfoAidRepeatPeriod = getPaoInfoAidRepeatPeriod().getText();
        rdsTerminal.getAidRepeatPeriod().setValue(paoInfoAidRepeatPeriod);
        
        return rdsTerminal;
    }

    private void handleException(Throwable exception) {
        // do nothing?
    }

    private void initConnections() throws Exception {

        getSiteAddress().addActionListener(this);
        getEncoderAddress().addActionListener(this);
        getTransmitSpeed().addActionListener(this);
        getGroupType().addActionListener(this);
        getPaoInfoIpAddress().addActionListener(this);
        getPaoInfoIpPort().addActionListener(this);
        getPaoInfoSpid().addActionListener(this);
        getPaoInfoAidRepeatPeriod().addActionListener(this);

        getSiteAddress().addCaretListener(this);
        getEncoderAddress().addCaretListener(this);
        getTransmitSpeed().addCaretListener(this);
        getGroupType().addCaretListener(this);
        getPaoInfoIpAddress().addCaretListener(this);
        getPaoInfoIpPort().addCaretListener(this);
        getPaoInfoSpid().addCaretListener(this);
        getPaoInfoAidRepeatPeriod().addCaretListener(this);
    }

    private void initialize() {
        try {
            setName("Device RDS Terminal Panel");
            setSize(350, 200);
            setLayout(new GridBagLayout());

            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.fill = GridBagConstraints.BOTH;
            constraints.anchor = GridBagConstraints.WEST;
            constraints.weightx = 1.0;
            constraints.weighty = 1.0;
            add(getStaticPaoInfoPanel(), constraints);

            constraints = new GridBagConstraints();
            constraints.gridx = 0;
            constraints.gridy = 1;
            constraints.anchor = GridBagConstraints.WEST;
            constraints.ipady = -5;
            constraints.insets = new java.awt.Insets(10, 22, 10, 2);
            add(getErrorMessageLabel(), constraints);

            initConnections();
        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    /**
     * Validates the current values of the panel.
     */
    public boolean isInputValid() {

        String errorMessage = validateTextFields();
        if (StringUtils.isNotBlank(errorMessage)) {
            setErrorString(errorMessage);
            getErrorMessageLabel().setText("(" + errorMessage + ")");
            return false;
        } else {
            setErrorString("");
            getErrorMessageLabel().setText("");
        }
        return true;
    }

    private String validateTextFields() {

        // validate site address
        // must exist and be an int 0-1023
        String siteAddress = getSiteAddress().getText();
        if (StringUtils.isBlank(siteAddress) ||
            !StringUtils.isNumeric(siteAddress) ||
            (Integer.parseInt(siteAddress) > 1023 || Integer.parseInt(siteAddress) < 0)) {
            return "The Site Address must be an integer 0 - 1023.";
        }

        // validate encoder address
        // must exist and be an int 0-63
        String encoderAddress = getEncoderAddress().getText();
        if (StringUtils.isBlank(encoderAddress) ||
                !StringUtils.isNumeric(encoderAddress) ||
                (Integer.parseInt(encoderAddress) > 63 || Integer.parseInt(encoderAddress) < 0)) {
                return "The Encoder Address must be an integer 0 - 63.";
        }
        
        // validate transmission speed
        // must be a floating point number 0 - 11.4
        try {
            if (StringUtils.isBlank(getTransmitSpeed().getText()) ||
                (Float.parseFloat(getTransmitSpeed().getText()) > 12 || Float.parseFloat(getTransmitSpeed().getText()) < 0)) {
                return "The Transmission Speed (groups per second), valid values are 0 - 11.99999.";
            }
        } catch (NumberFormatException e) {
            return "The Transmission Speed (groups per second), valid values are 0 - 11.99999.";
        }

        if (StringUtils.isBlank(getGroupType().getText())) {
            return "The Group Type must be filled in, valid values are 0A - 15B.";
        }

        if (StringUtils.isBlank(getPaoInfoIpAddress().getText())) {
            return "The Pao Info IP Address must be filled in.";
        }

        if (StringUtils.isBlank(getPaoInfoIpPort().getText())) {
            return "The Pao Info IP Port must be filled in.";
        }
        
        // Validate AID Repeat Period
        String aidRepeatPeriod = getPaoInfoAidRepeatPeriod().getText();
        if(StringUtils.isBlank(aidRepeatPeriod) || !StringUtils.isNumeric(aidRepeatPeriod) ||
           Integer.parseInt(aidRepeatPeriod) < 0) {
            return "The Pao Info AID Repeat Period must be a non-negative integer.";
        } else if (!StringUtils.isBlank(aidRepeatPeriod) && StringUtils.isNumeric(aidRepeatPeriod) ||
                   Integer.parseInt(aidRepeatPeriod) > 0){
            // Validate SPID. Must be an integer 0-65535. Only required if AID Repeat Period is nonzero
            String spid = getPaoInfoSpid().getText();
            if (StringUtils.isBlank(spid) || !StringUtils.isNumeric(spid) ||
                (Integer.parseInt(spid) < 0 || Integer.parseInt(spid) > 65535)) {
                return "The Pao Info SPID must be an integer 0 - 65535.";
            }
        }

        return null;
    }

    public static void main(String[] args) {
        try {
            JFrame frame = new JFrame();
            DeviceNameAddressPanel aDeviceNameAddressPanel;
            aDeviceNameAddressPanel = new DeviceNameAddressPanel();
            frame.getContentPane().add("Center", aDeviceNameAddressPanel);
            frame.setSize(aDeviceNameAddressPanel.getSize());
            frame.setVisible(true);
        } catch (Throwable exception) {
            System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
            com.cannontech.clientutils.CTILogger.error(exception.getMessage(),
                                                       exception);
            ;
        }
    }

    @Override
    public void setValue(Object o) {

        RDSTerminal rdsTerminal = (RDSTerminal) o;

        if (rdsTerminal != null) {
            getSiteAddress().setText(rdsTerminal.getSiteAddress().getValue());
            getEncoderAddress().setText(rdsTerminal.getEncoderAddress().getValue());
            getTransmitSpeed().setText(rdsTerminal.getTransmitSpeed().getValue());
            getGroupType().setText(rdsTerminal.getGroupType().getValue());            
            getPaoInfoIpAddress().setText(rdsTerminal.getRdsIpAddress().getValue());
            getPaoInfoIpPort().setText(rdsTerminal.getRdsIpPort().getValue());
            getPaoInfoSpid().setText(rdsTerminal.getSpid().getValue());
            getPaoInfoAidRepeatPeriod().setText(rdsTerminal.getAidRepeatPeriod().getValue());
        }
    }

    public void setFirstFocus() {
        // Make sure that when its time to display this panel, the focus starts
        // in the top component
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                getSiteAddress().requestFocus();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.fireInputUpdate();
    }
}