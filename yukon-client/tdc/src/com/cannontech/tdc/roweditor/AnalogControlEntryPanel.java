package com.cannontech.tdc.roweditor;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.gui.unchanging.DoubleRangeDocument;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.tdc.TDCMainFrame;
import com.cannontech.tdc.alarms.gui.AlarmingRow;
import com.cannontech.tdc.commandevents.AnalogOutputCommand;
import com.cannontech.tdc.logbox.MessageBoxFrame;

public class AnalogControlEntryPanel extends ManualEntryJPanel implements RowEditorDialogListener {
    public static final double MIN_INPUT_VALUE = -99999.99999;
    public static final double MAX_INPUT_VALUE = 99999.99999;
    private javax.swing.JLabel ivjJLabelPtName = null;
    private javax.swing.JLabel ivjJLabelValue = null;
    private javax.swing.JTextField ivjJTextFieldValue = null;
    private AlarmPanel ivjAlarmPanel = null;
    private javax.swing.JLabel ivjJLabelPointDeviceName = null;
    private javax.swing.JOptionPane optionBox = null;

    private AnalogControlEntryPanel() {
        super();
        initialize();
    }

    public AnalogControlEntryPanel(EditorDialogData data, Object currentValue, AlarmingRow alarmRow_) {
        super(data, currentValue, alarmRow_);

        initialize();
    }

    public AnalogControlEntryPanel(EditorDialogData data, Object currentValue) {
        super(data, currentValue);
        initialize();
    }

    private void destroyObservers() {
        getAlarmPanel().deleteObserver();
    }

    private AlarmPanel getAlarmPanel() {
        if (ivjAlarmPanel == null) {
            try {
                com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
                ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
                ivjLocalBorder.setTitleFont(new java.awt.Font("dialog.bold", 1, 14));
                ivjLocalBorder.setTitle("Alarms");
                ivjAlarmPanel = new com.cannontech.tdc.roweditor.AlarmPanel();
                ivjAlarmPanel.setName("AlarmPanel");
                ivjAlarmPanel.setBorder(ivjLocalBorder);
                // user code begin {1}

                ivjAlarmPanel.setParentPanel(this);

                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjAlarmPanel;
    }

    private javax.swing.JLabel getJLabelPointDeviceName() {
        if (ivjJLabelPointDeviceName == null) {
            try {
                ivjJLabelPointDeviceName = new javax.swing.JLabel();
                ivjJLabelPointDeviceName.setName("JLabelPointDeviceName");
                ivjJLabelPointDeviceName.setFont(new java.awt.Font("Arial", 1, 12));
                ivjJLabelPointDeviceName.setText("POINT/DEVICE NAME");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJLabelPointDeviceName;
    }

    private javax.swing.JLabel getJLabelPtName() {
        if (ivjJLabelPtName == null) {
            try {
                ivjJLabelPtName = new javax.swing.JLabel();
                ivjJLabelPtName.setName("JLabelPtName");
                ivjJLabelPtName.setFont(new java.awt.Font("dialog", 0, 12));
                ivjJLabelPtName.setText("Point:");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJLabelPtName;
    }

    private javax.swing.JLabel getJLabelValue() {
        if (ivjJLabelValue == null) {
            try {
                ivjJLabelValue = new javax.swing.JLabel();
                ivjJLabelValue.setName("JLabelValue");
                ivjJLabelValue.setFont(new java.awt.Font("dialog", 0, 12));
                ivjJLabelValue.setText("Value:");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJLabelValue;
    }

    private javax.swing.JTextField getJTextFieldValue() {
        if (ivjJTextFieldValue == null) {
            try {
                ivjJTextFieldValue = new javax.swing.JTextField();
                ivjJTextFieldValue.setName("JTextFieldValue");
                // user code begin {1}

                ivjJTextFieldValue.setDocument(new DoubleRangeDocument(MIN_INPUT_VALUE,
                                                                       MAX_INPUT_VALUE));

                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJTextFieldValue;
    }

    @Override
    public String getPanelTitle() {
        if (getEditorData() != null) {
            return PointTypes.getType(getEditorData().getPointType()) + " Point Control";
        } else
            return "Analog Output Value Entry";
    }

    /**
     * Called whenever the part throws an exception.
     * @param exception java.lang.Throwable
     */
    private void handleException(java.lang.Throwable exception) {

        /* Uncomment the following lines to print uncaught exceptions to stdout */
        CTILogger.info("--------- UNCAUGHT EXCEPTION AnalogPanelControlEntry() ---------");
        CTILogger.error(exception.getMessage(), exception);
        ;

        TDCMainFrame.messageLog.addMessage(exception.toString() + " in : " + this.getClass(),
                                           MessageBoxFrame.ERROR_MSG);
    }

    /**
     * Initialize the class.
     */
    private void initialize() {
        try {
            // user code begin {1}
            // user code end
            setName("EditDataPanel");
            setPreferredSize(new java.awt.Dimension(417, 169));
            setLayout(new java.awt.GridBagLayout());
            setSize(455, 183);

            java.awt.GridBagConstraints constraintsJLabelPtName = new java.awt.GridBagConstraints();
            constraintsJLabelPtName.gridx = 0;
            constraintsJLabelPtName.gridy = 0;
            constraintsJLabelPtName.anchor = java.awt.GridBagConstraints.NORTHWEST;
            constraintsJLabelPtName.ipadx = 6;
            constraintsJLabelPtName.insets = new java.awt.Insets(16, 11, 4, 2);
            add(getJLabelPtName(), constraintsJLabelPtName);

            java.awt.GridBagConstraints constraintsJLabelValue = new java.awt.GridBagConstraints();
            constraintsJLabelValue.gridx = 0;
            constraintsJLabelValue.gridy = 1;
            constraintsJLabelValue.anchor = java.awt.GridBagConstraints.NORTHWEST;
            constraintsJLabelValue.ipadx = 3;
            constraintsJLabelValue.insets = new java.awt.Insets(9, 11, 5, 2);
            add(getJLabelValue(), constraintsJLabelValue);

            java.awt.GridBagConstraints constraintsJTextFieldValue =
                new java.awt.GridBagConstraints();
            constraintsJTextFieldValue.gridx = 1;
            constraintsJTextFieldValue.gridy = 1;
            constraintsJTextFieldValue.anchor = java.awt.GridBagConstraints.NORTHWEST;
            constraintsJTextFieldValue.weightx = 1.0;
            constraintsJTextFieldValue.ipadx = 186;
            constraintsJTextFieldValue.insets = new java.awt.Insets(5, 2, 5, 21);
            add(getJTextFieldValue(), constraintsJTextFieldValue);

            java.awt.GridBagConstraints constraintsAlarmPanel = new java.awt.GridBagConstraints();
            constraintsAlarmPanel.gridx = 0;
            constraintsAlarmPanel.gridy = 2;
            constraintsAlarmPanel.gridwidth = 3;
            constraintsAlarmPanel.fill = java.awt.GridBagConstraints.BOTH;
            constraintsAlarmPanel.weightx = 1.0;
            constraintsAlarmPanel.weighty = 1.0;
            constraintsAlarmPanel.ipadx = -162;
            constraintsAlarmPanel.ipady = -15;
            constraintsAlarmPanel.insets = new java.awt.Insets(5, 7, 0, 0);
            add(getAlarmPanel(), constraintsAlarmPanel);

            java.awt.GridBagConstraints constraintsJLabelPointDeviceName =
                new java.awt.GridBagConstraints();
            constraintsJLabelPointDeviceName.gridx = 1;
            constraintsJLabelPointDeviceName.gridy = 0;
            constraintsJLabelPointDeviceName.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsJLabelPointDeviceName.anchor = java.awt.GridBagConstraints.NORTHWEST;
            constraintsJLabelPointDeviceName.ipadx = 92;
            constraintsJLabelPointDeviceName.insets = new java.awt.Insets(16, 2, 4, 4);
            add(getJLabelPointDeviceName(), constraintsJLabelPointDeviceName);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        // user code begin {2}

        initReadOnlyData();

        try {
            Double.parseDouble(getStartingValue().toString()); // if not a float, goto catch

            getJTextFieldValue().setText(getStartingValue().toString());
        } catch (NumberFormatException ex) {
            getJTextFieldValue().setText("0.0");
        }

        // user code end
    }

    private void initReadOnlyData() {
        getJLabelPointDeviceName().setText(getEditorData().getDeviceName().toString() + " / "
                                           + getEditorData().getPointName());

        synchronized (getAlarmPanel()) {
            getAlarmPanel().setVisible(isRowAlarmed());
        }
    }

    @Override
    public void JButtonCancelAction_actionPerformed(java.util.EventObject newEvent) {
        destroyObservers();
    }

    @Override
    public void JButtonSendAction_actionPerformed(java.util.EventObject newEvent) {
        // ask for verification from the user first
        Object[] o = { "Execute", "Cancel" };
        int retValue = optionBox.showOptionDialog(
                                                  this,
                                                  "Execute the Analog Output?",
                                                  "Verification",
                                                  javax.swing.JOptionPane.YES_NO_OPTION,
                                                  javax.swing.JOptionPane.QUESTION_MESSAGE,
                                                  null,
                                                  o,
                                                  o[1].toString());

        if (retValue == javax.swing.JOptionPane.YES_OPTION) {
            Double outputValue = Double.parseDouble(getJTextFieldValue().getText());

            long ptID = getEditorData().getPointID();

            AnalogOutputCommand.send(ptID, outputValue);
            TDCMainFrame.messageLog.addMessage("Analog output value (" + outputValue
                                                       + ") was sent successfully for the point '" +
                                                       getEditorData().getPointName() + "'",
                                               MessageBoxFrame.INFORMATION_MSG);
        }

        return;
    }

    public void jTextFieldValue_CaretUpdate(javax.swing.event.CaretEvent caretEvent) {
        return;
    }
}
