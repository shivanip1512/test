package com.cannontech.tdc.roweditor;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.point.PointQuality;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.tdc.TDCMainFrame;
import com.cannontech.tdc.logbox.MessageBoxFrame;

public class StatusManualEntryPanel extends ManualEntryJPanel implements RowEditorDialogListener {
    private javax.swing.JComboBox ivjJComboBoxValues = null;
    private javax.swing.JLabel ivjJLabelPtName = null;
    private AlarmPanel ivjAlarmPanel = null;
    private javax.swing.JLabel ivjJLabelPointDeviceName = null;
    private javax.swing.JOptionPane optionBox = null;
    private javax.swing.JLabel ivjJLabelValue = null;

    private StatusManualEntryPanel() {
        super();
        initialize();
    }

    public StatusManualEntryPanel(com.cannontech.tdc.roweditor.EditorDialogData data,
                                  java.lang.Object currentValue) {
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
                ivjLocalBorder.setTitle("Alarm Panel");
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

    private javax.swing.JComboBox getJComboBoxValues() {
        if (ivjJComboBoxValues == null) {
            try {
                ivjJComboBoxValues = new javax.swing.JComboBox();
                ivjJComboBoxValues.setName("JComboBoxValues");
                ivjJComboBoxValues.setBackground(java.awt.Color.white);
                ivjJComboBoxValues.setVisible(true);
                ivjJComboBoxValues.setMaximumSize(new java.awt.Dimension(32767, 30));
                ivjJComboBoxValues.setPreferredSize(new java.awt.Dimension(130, 30));
                ivjJComboBoxValues.setEnabled(true);
                ivjJComboBoxValues.setMinimumSize(new java.awt.Dimension(126, 30));
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJComboBoxValues;
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
                ivjJLabelValue.setText("Current State:");
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

    public String getPanelTitle() {
        if (getEditorData() != null) {
            return com.cannontech.database.data.point.PointTypes.getType(getEditorData()
                .getPointType()) + " Point Change";
        } else
            return "Status Point Manual Entry";
    }

    /**
     * Called whenever the part throws an exception.
     * @param exception java.lang.Throwable
     */
    private void handleException(java.lang.Throwable exception) {

        /* Uncomment the following lines to print uncaught exceptions to stdout */
        CTILogger.info("--------- UNCAUGHT EXCEPTION StatusPanel() ---------");
        CTILogger.error(exception.getMessage(), exception);
        ;

        TDCMainFrame.messageLog.addMessage(exception.toString() + " in : " + this.getClass(),
                                           MessageBoxFrame.ERROR_MSG);
    }

    private void initData() {
        getJLabelPointDeviceName().setText(getEditorData().getDeviceName().toString() + " / "
                                           + getEditorData().getPointName());

        synchronized (getAlarmPanel()) {
            getAlarmPanel().setVisible(isRowAlarmed());
        }

        // this inits the JComboBox for Manual Entry
        String[] states = getEditorData().getAllStates();
        for (int i = 0; i < states.length; i++)
            getJComboBoxValues().addItem(states[i]);
    }

    private void initialize() {
        try {
            // user code begin {1}
            // user code end
            setName("EditDataPanel");
            setPreferredSize(new java.awt.Dimension(455, 163));
            setLayout(new java.awt.GridBagLayout());
            setSize(455, 183);
            setMinimumSize(new java.awt.Dimension(455, 163));

            java.awt.GridBagConstraints constraintsJLabelPtName = new java.awt.GridBagConstraints();
            constraintsJLabelPtName.gridx = 0;
            constraintsJLabelPtName.gridy = 0;
            constraintsJLabelPtName.anchor = java.awt.GridBagConstraints.NORTHWEST;
            constraintsJLabelPtName.ipadx = 3;
            constraintsJLabelPtName.insets = new java.awt.Insets(16, 11, 4, 5);
            add(getJLabelPtName(), constraintsJLabelPtName);

            java.awt.GridBagConstraints constraintsJComboBoxValues =
                new java.awt.GridBagConstraints();
            constraintsJComboBoxValues.gridx = 1;
            constraintsJComboBoxValues.gridy = 1;
            constraintsJComboBoxValues.anchor = java.awt.GridBagConstraints.NORTHWEST;
            constraintsJComboBoxValues.weightx = 1.0;
            constraintsJComboBoxValues.ipadx = 55;
            constraintsJComboBoxValues.ipady = -6;
            constraintsJComboBoxValues.insets = new java.awt.Insets(5, 4, 2, 32);
            add(getJComboBoxValues(), constraintsJComboBoxValues);

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
            constraintsJLabelPointDeviceName.ipadx = 96;
            constraintsJLabelPointDeviceName.insets = new java.awt.Insets(16, 4, 4, 2);
            add(getJLabelPointDeviceName(), constraintsJLabelPointDeviceName);

            java.awt.GridBagConstraints constraintsJLabelValue = new java.awt.GridBagConstraints();
            constraintsJLabelValue.gridx = 0;
            constraintsJLabelValue.gridy = 1;
            constraintsJLabelValue.anchor = java.awt.GridBagConstraints.NORTHWEST;
            constraintsJLabelValue.ipadx = 2;
            constraintsJLabelValue.insets = new java.awt.Insets(7, 10, 7, 4);
            add(getJLabelValue(), constraintsJLabelValue);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        // user code begin {2}

        initData();

        getJComboBoxValues().setSelectedItem(getStartingValue());

        // user code end
    }

    public void JButtonCancelAction_actionPerformed(java.util.EventObject newEvent) {
        destroyObservers();
    }

    public void JButtonSendAction_actionPerformed(java.util.EventObject newEvent) {
        try {
            // Create new point Here
            PointData pt = new PointData();
            pt.setId(getEditorData().getPointID());
            pt.setTags(getEditorData().getTags());
            pt.setTimeStamp(new java.util.Date());
            pt.setTime(new java.util.Date());
            pt.setType(getEditorData().getPointType());
            pt.setValue((double) getJComboBoxValues().getSelectedIndex());
            pt.setPointQuality(PointQuality.Manual);
            pt.setStr("Manual change occurred from "
                      + com.cannontech.common.util.CtiUtilities.getUserName() + " using TDC");
            pt.setUserName(com.cannontech.common.util.CtiUtilities.getUserName());

            // Now send the new pointData
            SendData.getInstance().sendPointData(pt);
        } finally {
            destroyObservers();
        }
    }
}
