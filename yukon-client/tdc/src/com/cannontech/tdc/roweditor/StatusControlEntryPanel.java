package com.cannontech.tdc.roweditor;

import com.cannontech.clientutils.tags.TagUtils;
import com.cannontech.common.point.PointQuality;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.tdc.TDCMainFrame;
import com.cannontech.tdc.commandevents.ControlCommand;
import com.cannontech.tdc.logbox.MessageBoxFrame;

public class StatusControlEntryPanel extends ManualEntryJPanel implements RowEditorDialogListener,
        java.awt.event.ActionListener {
    private String[] states = null;

    private javax.swing.JLabel ivjJLabelPtName = null;
    private javax.swing.JLabel ivjJLabelPointDeviceName = null;
    private javax.swing.JOptionPane optionBox = null;
    private javax.swing.JButton ivjJButtonRawState1 = null;
    private javax.swing.JButton ivjJButtonRawState2 = null;
    private javax.swing.JLabel ivjJLabelValue = null;
    private javax.swing.JPanel ivjJPanelControl = null;
    private javax.swing.JLabel ivjJLabelStateText = null;

    private StatusControlEntryPanel() {
        super();
        initialize();
    }

    public StatusControlEntryPanel(com.cannontech.tdc.roweditor.EditorDialogData data,
                                   java.lang.Object currentValue) {
        super(data, currentValue);
        initialize();
    }

    /**
     * Method to handle events for the ActionListener interface.
     * @param e java.awt.event.ActionEvent
     */
    public void actionPerformed(java.awt.event.ActionEvent e) {
        // user code begin {1}
        // user code end
        if (e.getSource() == getJButtonRawState1())
            connEtoC2(e);
        if (e.getSource() == getJButtonRawState2())
            connEtoC3(e);
        // user code begin {2}
        // user code end
    }

    /**
     * connEtoC2: (JButtonOpen.action.actionPerformed(java.awt.event.ActionEvent) -->
     * StatusPanel.jButtonOpen_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
     * @param arg1 java.awt.event.ActionEvent
     */
    private void connEtoC2(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.jButtonControl_ActionPerformed(arg1);
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC3: (JButtonClose.action.actionPerformed(java.awt.event.ActionEvent) -->
     * StatusPanel.jButtonClose_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
     * @param arg1 java.awt.event.ActionEvent
     */
    private void connEtoC3(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.jButtonControl_ActionPerformed(arg1);
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * This method will only work when this panel is inside
     * a RowEditorDialog, else it will do nothing
     * 
     */
    private void dispose() {
        java.awt.Container c = this.getParent();
        while (!(c instanceof RowEditorDialog) && c != null)
            c = c.getParent();

        if (c != null)
            ((RowEditorDialog) c).dispose();
    }

    public EditorDialogData getEditorData() {
        return super.getEditorData();
    }

    private javax.swing.JButton getJButtonRawState1() {
        if (ivjJButtonRawState1 == null) {
            try {
                ivjJButtonRawState1 = new javax.swing.JButton();
                ivjJButtonRawState1.setName("JButtonRawState1");
                ivjJButtonRawState1.setText("Open");
                ivjJButtonRawState1.setVisible(true);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJButtonRawState1;
    }

    private javax.swing.JButton getJButtonRawState2() {
        if (ivjJButtonRawState2 == null) {
            try {
                ivjJButtonRawState2 = new javax.swing.JButton();
                ivjJButtonRawState2.setName("JButtonRawState2");
                ivjJButtonRawState2.setText("Close");
                ivjJButtonRawState2.setVisible(true);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJButtonRawState2;
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

    private javax.swing.JLabel getJLabelStateText() {
        if (ivjJLabelStateText == null) {
            try {
                ivjJLabelStateText = new javax.swing.JLabel();
                ivjJLabelStateText.setName("JLabelStateText");
                ivjJLabelStateText.setFont(new java.awt.Font("Arial", 1, 12));
                ivjJLabelStateText.setText("STATE_TEXT");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJLabelStateText;
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

    private javax.swing.JPanel getJPanelControl() {
        if (ivjJPanelControl == null) {
            try {
                com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
                ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
                ivjLocalBorder.setTitleFont(new java.awt.Font("dialog.bold", 1, 12));
                ivjLocalBorder.setTitle("Control");
                ivjJPanelControl = new javax.swing.JPanel();
                ivjJPanelControl.setName("JPanelControl");
                ivjJPanelControl.setBorder(ivjLocalBorder);
                ivjJPanelControl.setLayout(new java.awt.GridBagLayout());
                ivjJPanelControl.setVisible(true);

                java.awt.GridBagConstraints constraintsJButtonRawState1 =
                    new java.awt.GridBagConstraints();
                constraintsJButtonRawState1.gridx = 1;
                constraintsJButtonRawState1.gridy = 1;
                constraintsJButtonRawState1.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJButtonRawState1.ipadx = 28;
                constraintsJButtonRawState1.insets = new java.awt.Insets(5, 23, 13, 19);
                getJPanelControl().add(getJButtonRawState1(), constraintsJButtonRawState1);

                java.awt.GridBagConstraints constraintsJButtonRawState2 =
                    new java.awt.GridBagConstraints();
                constraintsJButtonRawState2.gridx = 2;
                constraintsJButtonRawState2.gridy = 1;
                constraintsJButtonRawState2.anchor = java.awt.GridBagConstraints.EAST;
                constraintsJButtonRawState2.ipadx = 26;
                constraintsJButtonRawState2.insets = new java.awt.Insets(5, 20, 13, 27);
                getJPanelControl().add(getJButtonRawState2(), constraintsJButtonRawState2);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJPanelControl;
    }

    public String getPanelTitle() {
        return "Status Control Panel";
    }

    private int getSelectedStateIntValue() throws StateNotFoundException {
        int i = 0;
        for (i = 0; i < getStates().length; i++)
            if (getJLabelStateText().getText().equalsIgnoreCase(getStates()[i]))
                return i;

        // we did not find the state, lets throw some junk!
        StringBuffer s = new StringBuffer("{");
        for (int j = 0; j < getStates().length; j++)
            s.append(getStates()[j] + ",");

        s.deleteCharAt(s.length() - 1);
        s.append("}");
        throw new StateNotFoundException("Unable to find the selected state in the stategroup: "
                                         + s);
    }

    private java.lang.String[] getStates() {
        return states;
    }

    /**
     * Called whenever the part throws an exception.
     * @param exception java.lang.Throwable
     */
    private void handleException(java.lang.Throwable exception) {

        /* Uncomment the following lines to print uncaught exceptions to stdout */
        com.cannontech.clientutils.CTILogger
            .info("--------- UNCAUGHT EXCEPTION StatusPanel() ---------");
        com.cannontech.clientutils.CTILogger.error(exception.getMessage(), exception);
        ;

        TDCMainFrame.messageLog.addMessage(exception.toString() + " in : " + this.getClass(),
                                           MessageBoxFrame.ERROR_MSG);
    }

    /**
     * Initializes connections
     * @exception java.lang.Exception The exception description.
     */
    private void initConnections() throws java.lang.Exception {
        // user code begin {1}

        // user code end
        getJButtonRawState1().addActionListener(this);
        getJButtonRawState2().addActionListener(this);
    }

    private void initData() {
        setControlJButtons();

        getJLabelPointDeviceName().setText(getEditorData().getDeviceName().toString() + " / "
                                           + getEditorData().getPointName());

        // check to see if the point can be controlled AND its control is NOT disabled
        if (TagUtils.isControllablePoint(getEditorData().getTags())
            && TagUtils.isControlEnabled(getEditorData().getTags())) {
            getJPanelControl().setVisible(true);
        }
    }

    /**
     * Initialize the class.
     */
    private void initialize() {
        try {
            // user code begin {1}
            // user code end
            setName("StatusPanelControlEntry");
            setPreferredSize(new java.awt.Dimension(455, 163));
            setLayout(new java.awt.GridBagLayout());
            setSize(296, 156);
            setMinimumSize(new java.awt.Dimension(100, 100));

            java.awt.GridBagConstraints constraintsJLabelPtName = new java.awt.GridBagConstraints();
            constraintsJLabelPtName.gridx = 1;
            constraintsJLabelPtName.gridy = 1;
            constraintsJLabelPtName.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJLabelPtName.ipadx = 3;
            constraintsJLabelPtName.insets = new java.awt.Insets(15, 11, 5, 46);
            add(getJLabelPtName(), constraintsJLabelPtName);

            java.awt.GridBagConstraints constraintsJLabelPointDeviceName =
                new java.awt.GridBagConstraints();
            constraintsJLabelPointDeviceName.gridx = 2;
            constraintsJLabelPointDeviceName.gridy = 1;
            constraintsJLabelPointDeviceName.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJLabelPointDeviceName.ipadx = 72;
            constraintsJLabelPointDeviceName.ipady = 2;
            constraintsJLabelPointDeviceName.insets = new java.awt.Insets(15, 4, 5, 14);
            add(getJLabelPointDeviceName(), constraintsJLabelPointDeviceName);

            java.awt.GridBagConstraints constraintsJLabelValue = new java.awt.GridBagConstraints();
            constraintsJLabelValue.gridx = 1;
            constraintsJLabelValue.gridy = 2;
            constraintsJLabelValue.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJLabelValue.ipadx = 2;
            constraintsJLabelValue.insets = new java.awt.Insets(6, 11, 8, 3);
            add(getJLabelValue(), constraintsJLabelValue);

            java.awt.GridBagConstraints constraintsJPanelControl =
                new java.awt.GridBagConstraints();
            constraintsJPanelControl.gridx = 1;
            constraintsJPanelControl.gridy = 3;
            constraintsJPanelControl.gridwidth = 2;
            constraintsJPanelControl.fill = java.awt.GridBagConstraints.BOTH;
            constraintsJPanelControl.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJPanelControl.weightx = 1.0;
            constraintsJPanelControl.weighty = 1.0;
            constraintsJPanelControl.ipadx = -10;
            constraintsJPanelControl.ipady = -9;
            constraintsJPanelControl.insets = new java.awt.Insets(8, 11, 23, 10);
            add(getJPanelControl(), constraintsJPanelControl);

            java.awt.GridBagConstraints constraintsJLabelStateText =
                new java.awt.GridBagConstraints();
            constraintsJLabelStateText.gridx = 2;
            constraintsJLabelStateText.gridy = 2;
            constraintsJLabelStateText.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJLabelStateText.ipadx = 114;
            constraintsJLabelStateText.insets = new java.awt.Insets(6, 4, 8, 14);
            add(getJLabelStateText(), constraintsJLabelStateText);
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        // user code begin {2}

        initData();

        setStateText(getStartingValue().toString());

        // user code end
    }

    public void JButtonCancelAction_actionPerformed(java.util.EventObject newEvent) {
    }

    public void jButtonControl_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
        // ask for verification from the user first
        Object[] o = { "Execute", "Cancel" };
        int retValue = optionBox.showOptionDialog(
                                                  this,
                                                  "Execute the Control?",
                                                  "Verification",
                                                  javax.swing.JOptionPane.YES_NO_OPTION,
                                                  javax.swing.JOptionPane.QUESTION_MESSAGE,
                                                  null,
                                                  o,
                                                  o[1].toString());

        if (retValue == javax.swing.JOptionPane.YES_OPTION) {
            javax.swing.JButton buttonPressed = (javax.swing.JButton) actionEvent.getSource();

            long devID = getEditorData().getDeviceID();
            long ptID = getEditorData().getPointID();

            if (buttonPressed.getName().equalsIgnoreCase("JButtonRawState1")) {
                ControlCommand.send(devID, ptID, ControlCommand.CONTROL_OPENED);
                TDCMainFrame.messageLog
                    .addMessage("Control OPEN was sent successfully for the point '" +
                                        getEditorData().getPointName() + "'",
                                MessageBoxFrame.INFORMATION_MSG);
            } else // must be the button named JButtonRawState2
            {
                ControlCommand.send(devID, ptID, ControlCommand.CONTROL_CLOSED);
                TDCMainFrame.messageLog
                    .addMessage("Control CLOSE was sent successfully for the point '" +
                                        getEditorData().getPointName() + "'",
                                MessageBoxFrame.INFORMATION_MSG);
            }

            dispose();
        }

        return;
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

            pt.setValue((double) getSelectedStateIntValue());

            pt.setPointQuality(PointQuality.Manual);
            pt.setStr("Manual change occurred from "
                      + com.cannontech.common.util.CtiUtilities.getUserName() + " using TDC");
            pt.setUserName(com.cannontech.common.util.CtiUtilities.getUserName());

            // Now send the new pointData
            SendData.getInstance().sendPointData(pt);
        } catch (StateNotFoundException snf) {
            handleException(snf);
        } finally {
            JButtonCancelAction_actionPerformed(null);
        }

    }

    private void setControlJButtons() {
        states = getEditorData().getAllStates();

        if (states.length >= 2) {
            // only insert the first 2 states for control purposes
            getJButtonRawState1().setText(states[0]);
            getJButtonRawState2().setText(states[1]);
        }

        // this inits the JComboBox for Manual Entry
        // for( int i = 0; i < states.length; i++ )
        // getJComboBoxValues().addItem( states[i] );
    }

    private void setStates(java.lang.String[] newStates) {
        states = newStates;
    }

    private void setStateText(final String state) {
        javax.swing.SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
        {
            getJLabelStateText().setText(state);
        }
        });
    }
}
