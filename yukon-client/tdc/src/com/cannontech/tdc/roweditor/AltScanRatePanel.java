package com.cannontech.tdc.roweditor;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.SwingUtil;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.message.util.Command;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.tdc.TDCMainFrame;
import com.cannontech.tdc.logbox.MessageBoxFrame;

public class AltScanRatePanel extends ManualEntryJPanel implements RowEditorDialogListener {
    private JComboBox<String> ivjJComboBoxTime = null;
    private JLabel ivjJLabelDevName = null;
    private JLabel ivjJLabelDuration = null;
    private JLabel ivjJLabelActualDevName = null;

    public AltScanRatePanel(com.cannontech.tdc.roweditor.EditorDialogData data) {
        super(data, null);
        initialize();
    }

    public AltScanRatePanel(com.cannontech.tdc.roweditor.EditorDialogData data, java.lang.Object currentValue) {
        super(data, currentValue);
        initialize();
    }

    private JComboBox<String> getJComboBoxTime() {
        if (ivjJComboBoxTime == null) {
            try {
                ivjJComboBoxTime = new JComboBox<String>();
                ivjJComboBoxTime.setName("JComboBoxTime");

                ivjJComboBoxTime.setName("How long should this device scan using its alternate scan rate");

                ivjJComboBoxTime.addItem("(Only Once)");
                ivjJComboBoxTime.addItem("1 minute");
                ivjJComboBoxTime.addItem("2 minutes");
                ivjJComboBoxTime.addItem("3 minutes");
                ivjJComboBoxTime.addItem("4 minutes");
                ivjJComboBoxTime.addItem("5 minutes");
                ivjJComboBoxTime.addItem("10 minutes");
                ivjJComboBoxTime.addItem("15 minutes");
                ivjJComboBoxTime.addItem("30 minutes");
                ivjJComboBoxTime.addItem("1 hour");
                ivjJComboBoxTime.addItem("2 hours");
                ivjJComboBoxTime.addItem("4 hours");
                ivjJComboBoxTime.addItem("8 hours");
                ivjJComboBoxTime.addItem("16 hours");
                ivjJComboBoxTime.addItem("1 day");
                ivjJComboBoxTime.addItem("2 days");
                ivjJComboBoxTime.addItem("5 days");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJComboBoxTime;
    }

    private JLabel getJLabelActualDevName() {
        if (ivjJLabelActualDevName == null) {
            try {
                ivjJLabelActualDevName = new JLabel();
                ivjJLabelActualDevName.setName("JLabelActualDevName");
                ivjJLabelActualDevName.setFont(new java.awt.Font("Arial", 1, 12));
                ivjJLabelActualDevName.setText("<<DEVICE NAME>>");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelActualDevName;
    }

    private JLabel getJLabelDevName() {
        if (ivjJLabelDevName == null) {
            try {
                ivjJLabelDevName = new JLabel();
                ivjJLabelDevName.setName("JLabelDevName");
                ivjJLabelDevName.setFont(new java.awt.Font("dialog", 0, 12));
                ivjJLabelDevName.setText("Device:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelDevName;
    }

    private JLabel getJLabelDuration() {
        if (ivjJLabelDuration == null) {
            try {
                ivjJLabelDuration = new JLabel();
                ivjJLabelDuration.setName("JLabelDuration");
                ivjJLabelDuration.setFont(new java.awt.Font("dialog", 0, 12));
                ivjJLabelDuration.setText("Duration:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelDuration;
    }

    @Override
    public String getPanelTitle() {
        return "Force Alternate Scan Rate";
    }

    /**
     * Called whenever the part throws an exception.
     */
    private void handleException(java.lang.Throwable exception) {
        /* Uncomment the following lines to print uncaught exceptions to stdout */
        CTILogger.info("--------- UNCAUGHT EXCEPTION AnalogPanel() ---------");
        CTILogger.error(exception.getMessage(), exception);

        TDCMainFrame.messageLog.addMessage(exception.toString() + " in : " + this.getClass(), MessageBoxFrame.ERROR_MSG);
    }

    /**
     * Initialize the class.
     */
    private void initialize() {
        try {
            setName("EditDataPanel");
            setPreferredSize(new java.awt.Dimension(417, 169));
            setLayout(new java.awt.GridBagLayout());
            setSize(376, 82);

            java.awt.GridBagConstraints constraintsJLabelDevName = new java.awt.GridBagConstraints();
            constraintsJLabelDevName.gridx = 1;
            constraintsJLabelDevName.gridy = 1;
            constraintsJLabelDevName.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJLabelDevName.ipadx = 15;
            constraintsJLabelDevName.insets = new java.awt.Insets(13, 9, 5, 0);
            add(getJLabelDevName(), constraintsJLabelDevName);

            java.awt.GridBagConstraints constraintsJLabelDuration = new java.awt.GridBagConstraints();
            constraintsJLabelDuration.gridx = 1;
            constraintsJLabelDuration.gridy = 2;
            constraintsJLabelDuration.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJLabelDuration.ipadx = 5;
            constraintsJLabelDuration.insets = new java.awt.Insets(8, 9, 24, 0);
            add(getJLabelDuration(), constraintsJLabelDuration);

            java.awt.GridBagConstraints constraintsJLabelActualDevName = new java.awt.GridBagConstraints();
            constraintsJLabelActualDevName.gridx = 2;
            constraintsJLabelActualDevName.gridy = 1;
            constraintsJLabelActualDevName.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsJLabelActualDevName.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJLabelActualDevName.ipadx = 202;
            constraintsJLabelActualDevName.insets = new java.awt.Insets(14, 1, 6, 4);
            add(getJLabelActualDevName(), constraintsJLabelActualDevName);

            java.awt.GridBagConstraints constraintsJComboBoxTime = new java.awt.GridBagConstraints();
            constraintsJComboBoxTime.gridx = 2;
            constraintsJComboBoxTime.gridy = 2;
            constraintsJComboBoxTime.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsJComboBoxTime.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJComboBoxTime.weightx = 1.0;
            constraintsJComboBoxTime.ipadx = 75;
            constraintsJComboBoxTime.insets = new java.awt.Insets(5, 1, 20, 110);
            add(getJComboBoxTime(), constraintsJComboBoxTime);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        initReadOnlyData();
    }

    private void initReadOnlyData() {
        getJLabelActualDevName().setText(getEditorData().getDeviceName().toString());
    }

    @Override
    public void JButtonCancelAction_actionPerformed(java.util.EventObject newEvent) {
    }

    @Override
    public void JButtonSendAction_actionPerformed(java.util.EventObject newEvent) {
        LiteYukonPAObject paobject = YukonSpringHook.getBean(PaoDao.class).getLiteYukonPAO(getEditorData().getDeviceID());

        if (paobject != null && DeviceTypesFuncs.hasDeviceScanRate(paobject.getPaoType().getDeviceTypeId())) {
            long duration = 0;

            if (getJComboBoxTime().getSelectedIndex() > 0) {
                duration = SwingUtil.getIntervalComboBoxSecondsValue(getJComboBoxTime()).longValue();
            }

            Command cmdMsg = new Command();
            cmdMsg.setOperation(Command.ALTERNATE_SCAN_RATE);
            cmdMsg.setPriority(14);

            List<Integer> opArgList = new ArrayList<Integer>(4);
            opArgList.add(Command.DEFAULT_CLIENT_REGISTRATION_TOKEN);
            opArgList.add(paobject.getLiteID()); // deviceID
            opArgList.add(-1); // open?
            opArgList.add((int) duration); // duration in secs

            cmdMsg.setOpArgList(opArgList);

            // now send the point data
            SendData.getInstance().sendCommandMsg(cmdMsg);
        }
    }
}