package com.cannontech.dbeditor.wizard.device.lmcontrolarea;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.CaretListener;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.JTextFieldTimeEntry;
import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.common.gui.util.TitleBorder;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SwingUtil;
import com.cannontech.database.data.device.lm.LMControlArea;
import com.cannontech.database.data.device.lm.LMFactory;

public class LMControlAreaBasePanel extends DataInputPanel implements ActionListener, CaretListener {
    private JLabel ivjJLabelControlInterval = null;
    private JLabel ivjJLabelMinRespTime = null;
    private JLabel ivjJLabelName = null;
    private JTextField ivjJTextFieldName = null;
    private JComboBox<String> ivjJComboBoxOperationalState = null;
    private JLabel ivjJLabelOperationalState = null;
    private JPanel ivjJPanelOptional = null;
    private JLabel ivjJLabelTime1 = null;
    private JLabel ivjJLabelTime2 = null;
    private JLabel ivjJLabelStartTime = null;
    private JLabel ivjJLabelStopTime = null;
    private JLabel ivjJLabelWarning = null;
    private JTextFieldTimeEntry ivjJTextFieldTimeEntryStart = null;
    private JTextFieldTimeEntry ivjJTextFieldTimeEntryStop = null;
    private JComboBox<String> ivjJComboBoxControlInterval = null;
    private JComboBox<String> ivjJComboBoxMinRespTime = null;
    private JCheckBox ivjJCheckBoxReqAllTrigActive = null;

    private static final Map<String, String> STRING_MAP = new Hashtable<String, String>(6);

    static {
        STRING_MAP.put(com.cannontech.database.db.device.lm.LMControlArea.OPSTATE_NONE, "No Change");
        STRING_MAP.put(com.cannontech.database.db.device.lm.LMControlArea.OPSTATE_ENABLED, "Enable / ReEnable");
        STRING_MAP.put(com.cannontech.database.db.device.lm.LMControlArea.OPSTATE_DISABLED, "Disable / Waived");

        STRING_MAP.put("No Change", com.cannontech.database.db.device.lm.LMControlArea.OPSTATE_NONE);
        STRING_MAP.put("Enable / ReEnable", com.cannontech.database.db.device.lm.LMControlArea.OPSTATE_ENABLED);
        STRING_MAP.put("Disable / Waived", com.cannontech.database.db.device.lm.LMControlArea.OPSTATE_DISABLED);
    };

    public LMControlAreaBasePanel() {
        super();
        initialize();
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        try {
            if (e.getSource() == getJComboBoxOperationalState() ||
                    e.getSource() == getJComboBoxControlInterval() ||
                    e.getSource() == getJComboBoxMinRespTime() ||
                    e.getSource() == getJCheckBoxReqAllTrigActive()) {
                this.fireInputUpdate();
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    @Override
    public void caretUpdate(javax.swing.event.CaretEvent e) {
        try {
            if (e.getSource() == getJTextFieldName() ||
                    e.getSource() == getJTextFieldTimeEntryStart() ||
                    e.getSource() == getJTextFieldTimeEntryStop()) {
                this.fireInputUpdate();
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    private javax.swing.JCheckBox getJCheckBoxReqAllTrigActive() {
        if (ivjJCheckBoxReqAllTrigActive == null) {
            try {
                ivjJCheckBoxReqAllTrigActive = new javax.swing.JCheckBox();
                ivjJCheckBoxReqAllTrigActive.setName("JCheckBoxReqAllTrigActive");
                ivjJCheckBoxReqAllTrigActive.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJCheckBoxReqAllTrigActive.setText("Require All Triggers Active");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJCheckBoxReqAllTrigActive;
    }

    private JComboBox<String> getJComboBoxControlInterval() {
        if (ivjJComboBoxControlInterval == null) {
            try {
                ivjJComboBoxControlInterval = new JComboBox<String>();
                ivjJComboBoxControlInterval.setName("JComboBoxControlInterval");
                ivjJComboBoxControlInterval.setToolTipText("Default operational state");

                ivjJComboBoxControlInterval.addItem("(On New Data Only)");
                ivjJComboBoxControlInterval.addItem("1 minute");
                ivjJComboBoxControlInterval.addItem("2 minute");
                ivjJComboBoxControlInterval.addItem("3 minute");
                ivjJComboBoxControlInterval.addItem("4 minute");
                ivjJComboBoxControlInterval.addItem("5 minute");
                ivjJComboBoxControlInterval.addItem("10 minute");
                ivjJComboBoxControlInterval.addItem("15 minute");
                ivjJComboBoxControlInterval.addItem("30 minute");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJComboBoxControlInterval;
    }

    private JComboBox<String> getJComboBoxMinRespTime() {
        if (ivjJComboBoxMinRespTime == null) {
            try {
                ivjJComboBoxMinRespTime = new JComboBox<String>();
                ivjJComboBoxMinRespTime.setName("JComboBoxMinRespTime");
                ivjJComboBoxMinRespTime.setToolTipText("Default operational state");

                ivjJComboBoxMinRespTime.addItem(CtiUtilities.STRING_NONE);
                ivjJComboBoxMinRespTime.addItem("1 minute");
                ivjJComboBoxMinRespTime.addItem("2 minute");
                ivjJComboBoxMinRespTime.addItem("3 minute");
                ivjJComboBoxMinRespTime.addItem("4 minute");
                ivjJComboBoxMinRespTime.addItem("5 minute");
                ivjJComboBoxMinRespTime.addItem("10 minute");
                ivjJComboBoxMinRespTime.addItem("15 minute");
                ivjJComboBoxMinRespTime.addItem("30 minute");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJComboBoxMinRespTime;
    }

    private JComboBox<String> getJComboBoxOperationalState() {
        if (ivjJComboBoxOperationalState == null) {
            try {
                ivjJComboBoxOperationalState = new JComboBox<String>();
                ivjJComboBoxOperationalState.setName("JComboBoxOperationalState");
                ivjJComboBoxOperationalState.setToolTipText("Default operational state");

                ivjJComboBoxOperationalState.addItem(STRING_MAP.get(com.cannontech.database.db.device.lm.LMControlArea.OPSTATE_NONE));
                ivjJComboBoxOperationalState.addItem(STRING_MAP.get(com.cannontech.database.db.device.lm.LMControlArea.OPSTATE_ENABLED));
                ivjJComboBoxOperationalState.addItem(STRING_MAP.get(com.cannontech.database.db.device.lm.LMControlArea.OPSTATE_DISABLED));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJComboBoxOperationalState;
    }

    private JLabel getJLabelControlInterval() {
        if (ivjJLabelControlInterval == null) {
            try {
                ivjJLabelControlInterval = new JLabel();
                ivjJLabelControlInterval.setName("JLabelControlInterval");
                ivjJLabelControlInterval.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJLabelControlInterval.setText("Control Interval:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelControlInterval;
    }

    private JLabel getJLabelMinRespTime() {
        if (ivjJLabelMinRespTime == null) {
            try {
                ivjJLabelMinRespTime = new JLabel();
                ivjJLabelMinRespTime.setName("JLabelMinRespTime");
                ivjJLabelMinRespTime.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJLabelMinRespTime.setText("Min Response Time:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelMinRespTime;
    }

    private JLabel getJLabelName() {
        if (ivjJLabelName == null) {
            try {
                ivjJLabelName = new JLabel();
                ivjJLabelName.setName("JLabelName");
                ivjJLabelName.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJLabelName.setText("Name:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelName;
    }

    private JLabel getJLabelOperationalState() {
        if (ivjJLabelOperationalState == null) {
            try {
                ivjJLabelOperationalState = new JLabel();
                ivjJLabelOperationalState.setName("JLabelOperationalState");
                ivjJLabelOperationalState.setToolTipText("Default Operational State");
                ivjJLabelOperationalState.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJLabelOperationalState.setText("Daily Default State:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelOperationalState;
    }

    private JLabel getJLabelStartTime() {
        if (ivjJLabelStartTime == null) {
            try {
                ivjJLabelStartTime = new JLabel();
                ivjJLabelStartTime.setName("JLabelStartTime");
                ivjJLabelStartTime.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJLabelStartTime.setText("Start Time:");
                ivjJLabelStartTime.setMinimumSize(new Dimension(70, 20));
                ivjJLabelStartTime.setPreferredSize(new Dimension(70, 20));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelStartTime;
    }

    private JLabel getJLabelStopTime() {
        if (ivjJLabelStopTime == null) {
            try {
                ivjJLabelStopTime = new JLabel();
                ivjJLabelStopTime.setName("JLabelStopTime");
                ivjJLabelStopTime.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJLabelStopTime.setText("Stop Time:");
                ivjJLabelStopTime.setMinimumSize(new Dimension(70, 20));
                ivjJLabelStopTime.setPreferredSize(new Dimension(70, 20));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelStopTime;
    }

    private JLabel getJLabelTime1() {
        if (ivjJLabelTime1 == null) {
            try {
                ivjJLabelTime1 = new JLabel();
                ivjJLabelTime1.setName("JLabelTime1");
                ivjJLabelTime1.setFont(new java.awt.Font("dialog", 0, 12));
                ivjJLabelTime1.setText("(HH:mm)");
                ivjJLabelTime1.setMinimumSize(new Dimension(60, 20));
                ivjJLabelTime1.setPreferredSize(new Dimension(60, 20));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelTime1;
    }

    private JLabel getJLabelTime2() {
        if (ivjJLabelTime2 == null) {
            try {
                ivjJLabelTime2 = new JLabel();
                ivjJLabelTime2.setName("JLabelTime2");
                ivjJLabelTime2.setFont(new java.awt.Font("dialog", 0, 12));
                ivjJLabelTime2.setText("(HH:mm)");
                ivjJLabelTime2.setMinimumSize(new Dimension(60, 20));
                ivjJLabelTime2.setPreferredSize(new Dimension(60, 20));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelTime2;
    }

    private JLabel getJLabelWarning() {
        if (ivjJLabelWarning == null) {
            try {
                ivjJLabelWarning = new JLabel();
                ivjJLabelWarning.setName("JLabelWarning");
                ivjJLabelWarning.setFont(new java.awt.Font("dialog", 0, 12));
                ivjJLabelWarning.setText("Control Window changes take effect at midnight");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelWarning;
    }

    private javax.swing.JPanel getJPanelOptional() {
        if (ivjJPanelOptional == null) {
            try {
                TitleBorder ivjLocalBorder;
                ivjLocalBorder = new TitleBorder();
                ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
                ivjLocalBorder.setTitle("Optional Control Window");
                ivjJPanelOptional = new javax.swing.JPanel();
                ivjJPanelOptional.setName("JPanelOptional");
                ivjJPanelOptional.setBorder(ivjLocalBorder);
                ivjJPanelOptional.setLayout(new java.awt.GridBagLayout());

                java.awt.GridBagConstraints constraintsJLabelStartTime = new java.awt.GridBagConstraints();
                constraintsJLabelStartTime.gridx = 0;
                constraintsJLabelStartTime.gridy = 0;
                constraintsJLabelStartTime.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJLabelStartTime.insets = new java.awt.Insets(5, 5, 5, 5);
                getJPanelOptional().add(getJLabelStartTime(), constraintsJLabelStartTime);

                java.awt.GridBagConstraints constraintsJLabelStopTime = new java.awt.GridBagConstraints();
                constraintsJLabelStopTime.gridx = 0;
                constraintsJLabelStopTime.gridy = 1;
                constraintsJLabelStopTime.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJLabelStopTime.insets = new java.awt.Insets(5, 5, 5, 5);
                getJPanelOptional().add(getJLabelStopTime(), constraintsJLabelStopTime);

                java.awt.GridBagConstraints constraintsJLabelWarning = new java.awt.GridBagConstraints();
                constraintsJLabelWarning.gridx = 0;
                constraintsJLabelWarning.gridy = 2;
                constraintsJLabelWarning.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJLabelWarning.gridwidth = 3;
                constraintsJLabelWarning.insets = new java.awt.Insets(5, 5, 5, 5);
                getJPanelOptional().add(getJLabelWarning(), constraintsJLabelWarning);

                java.awt.GridBagConstraints constraintsJTextFieldTimeEntryStart = new java.awt.GridBagConstraints();
                constraintsJTextFieldTimeEntryStart.gridx = 1;
                constraintsJTextFieldTimeEntryStart.gridy = 0;
                constraintsJTextFieldTimeEntryStart.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJTextFieldTimeEntryStart.insets = new java.awt.Insets(5, 5, 5, 5);
                getJPanelOptional().add(getJTextFieldTimeEntryStart(), constraintsJTextFieldTimeEntryStart);

                java.awt.GridBagConstraints constraintsJTextFieldTimeEntryStop = new java.awt.GridBagConstraints();
                constraintsJTextFieldTimeEntryStop.gridx = 1;
                constraintsJTextFieldTimeEntryStop.gridy = 1;
                constraintsJTextFieldTimeEntryStop.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJTextFieldTimeEntryStop.insets = new java.awt.Insets(5, 5, 5, 5);
                getJPanelOptional().add(getJTextFieldTimeEntryStop(), constraintsJTextFieldTimeEntryStop);

                java.awt.GridBagConstraints constraintsJLabelTime1 = new java.awt.GridBagConstraints();
                constraintsJLabelTime1.gridx = 2;
                constraintsJLabelTime1.gridy = 0;
                constraintsJLabelTime1.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJLabelTime1.insets = new java.awt.Insets(5, 5, 5, 5);
                getJPanelOptional().add(getJLabelTime1(), constraintsJLabelTime1);

                java.awt.GridBagConstraints constraintsJLabelTime2 = new java.awt.GridBagConstraints();
                constraintsJLabelTime2.gridx = 2;
                constraintsJLabelTime2.gridy = 1;
                constraintsJLabelTime2.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJLabelTime2.insets = new java.awt.Insets(5, 5, 5, 5);
                getJPanelOptional().add(getJLabelTime2(), constraintsJLabelTime2);

            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJPanelOptional;
    }

    private JTextField getJTextFieldName() {
        if (ivjJTextFieldName == null) {
            try {
                ivjJTextFieldName = new JTextField();
                ivjJTextFieldName.setName("JTextFieldName");
                ivjJTextFieldName.setToolTipText("Name of control area");
                ivjJTextFieldName.setText("");

                ivjJTextFieldName.setDocument(new TextFieldDocument(TextFieldDocument.MAX_DEVICE_NAME_LENGTH,
                                                                    PaoUtils.ILLEGAL_NAME_CHARS));

            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJTextFieldName;
    }

    private JTextFieldTimeEntry getJTextFieldTimeEntryStart() {
        if (ivjJTextFieldTimeEntryStart == null) {
            try {
                ivjJTextFieldTimeEntryStart = new JTextFieldTimeEntry();
                ivjJTextFieldTimeEntryStart.setName("JTextFieldTimeEntryStart");
                ivjJTextFieldTimeEntryStart.setMinimumSize(new Dimension(40, 20));
                ivjJTextFieldTimeEntryStart.setPreferredSize(new Dimension(40, 20));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJTextFieldTimeEntryStart;
    }

    private JTextFieldTimeEntry getJTextFieldTimeEntryStop() {
        if (ivjJTextFieldTimeEntryStop == null) {
            try {
                ivjJTextFieldTimeEntryStop = new JTextFieldTimeEntry();
                ivjJTextFieldTimeEntryStop.setName("JTextFieldTimeEntryStop");
                ivjJTextFieldTimeEntryStop.setMinimumSize(new Dimension(40, 20));
                ivjJTextFieldTimeEntryStop.setPreferredSize(new Dimension(40, 20));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJTextFieldTimeEntryStop;
    }

    @Override
    public Dimension getPreferredSize() {
        return null;
    }

    @Override
    public Object getValue(Object o) {
        // first panel for any LMControlArea, so create a new instance
        LMControlArea controlArea = null;

        if (o == null) {
            controlArea = (LMControlArea) LMFactory.createLoadManagement(PaoType.LM_CONTROL_AREA);
        } else {
            controlArea = (LMControlArea) o;
        }

        controlArea.setName(getJTextFieldName().getText());
        if (((String) getJComboBoxControlInterval().getSelectedItem()).compareTo("(On New Data Only)") == 0) {
            controlArea.getControlArea().setControlInterval(new Integer(0));
        } else {
            controlArea.getControlArea().setControlInterval(SwingUtil.getIntervalComboBoxSecondsValue(getJComboBoxControlInterval()));
        }

        if (((String) getJComboBoxMinRespTime().getSelectedItem()).compareTo(CtiUtilities.STRING_NONE) == 0) {
            controlArea.getControlArea().setMinResponseTime(new Integer(0));
        } else {
            controlArea.getControlArea().setMinResponseTime(SwingUtil.getIntervalComboBoxSecondsValue(getJComboBoxMinRespTime()));
        }

        controlArea.getControlArea().setDefOperationalState(STRING_MAP.get(getJComboBoxOperationalState().getSelectedItem().toString()).toString());

        // do the optional defaults here
        if (getJTextFieldTimeEntryStart().getText() != null && getJTextFieldTimeEntryStart().getText().length() > 0) {
            controlArea.getControlArea().setDefDailyStartTime(new Integer(CtiUtilities.decodeStringToSeconds(getJTextFieldTimeEntryStart().getTimeText())));
        } else {
            controlArea.getControlArea().setDefDailyStartTime(new Integer(com.cannontech.database.db.device.lm.LMControlArea.OPTIONAL_VALUE_UNUSED));
        }

        if (getJTextFieldTimeEntryStop().getText() != null && getJTextFieldTimeEntryStop().getText().length() > 0) {
            controlArea.getControlArea().setDefDailyStopTime(new Integer(CtiUtilities.decodeStringToSeconds(getJTextFieldTimeEntryStop().getTimeText())));
        } else {
            controlArea.getControlArea().setDefDailyStopTime(new Integer(com.cannontech.database.db.device.lm.LMControlArea.OPTIONAL_VALUE_UNUSED));
        }

        controlArea.getControlArea().setRequireAllTriggersActiveFlag((getJCheckBoxReqAllTrigActive().isSelected() ? new Character('T') : new Character('F')));

        return controlArea;
    }

    /**
     * Called whenever the part throws an exception.
     */
    private void handleException(Throwable exception) {
        /* Uncomment the following lines to print uncaught exceptions to stdout */
        com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
        com.cannontech.clientutils.CTILogger.error(exception.getMessage(), exception);
    }

    /**
     * Initializes connections
     */
    private void initConnections() throws java.lang.Exception {
        getJTextFieldName().addCaretListener(this);
        getJComboBoxOperationalState().addActionListener(this);
        getJTextFieldTimeEntryStart().addCaretListener(this);
        getJTextFieldTimeEntryStop().addCaretListener(this);
        getJComboBoxControlInterval().addActionListener(this);
        getJComboBoxMinRespTime().addActionListener(this);
        getJCheckBoxReqAllTrigActive().addActionListener(this);
    }

    /**
     * Initialize the class.
     */
    private void initialize() {
        try {
            setName("LMControlAreaBasePanel");
            setLayout(new java.awt.GridBagLayout());
            setSize(500, 500);

            java.awt.GridBagConstraints constraintsJLabelName = new java.awt.GridBagConstraints();
            constraintsJLabelName.gridx = 1;
            constraintsJLabelName.gridy = 1;
            constraintsJLabelName.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJLabelName.ipadx = 5;
            constraintsJLabelName.ipady = -2;
            constraintsJLabelName.insets = new java.awt.Insets(15, 12, 7, 5);
            add(getJLabelName(), constraintsJLabelName);

            java.awt.GridBagConstraints constraintsJTextFieldName = new java.awt.GridBagConstraints();
            constraintsJTextFieldName.gridx = 2;
            constraintsJTextFieldName.gridy = 1;
            constraintsJTextFieldName.gridwidth = 2;
            constraintsJTextFieldName.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsJTextFieldName.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJTextFieldName.weightx = 1.0;
            constraintsJTextFieldName.ipadx = 220;
            constraintsJTextFieldName.insets = new java.awt.Insets(13, 5, 6, 84);
            add(getJTextFieldName(), constraintsJTextFieldName);

            java.awt.GridBagConstraints constraintsJLabelControlInterval = new java.awt.GridBagConstraints();
            constraintsJLabelControlInterval.gridx = 1;
            constraintsJLabelControlInterval.gridy = 2;
            constraintsJLabelControlInterval.gridwidth = 2;
            constraintsJLabelControlInterval.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJLabelControlInterval.ipadx = 29;
            constraintsJLabelControlInterval.ipady = -2;
            constraintsJLabelControlInterval.insets = new java.awt.Insets(9, 12, 5, 4);
            add(getJLabelControlInterval(), constraintsJLabelControlInterval);

            java.awt.GridBagConstraints constraintsJLabelMinRespTime = new java.awt.GridBagConstraints();
            constraintsJLabelMinRespTime.gridx = 1;
            constraintsJLabelMinRespTime.gridy = 3;
            constraintsJLabelMinRespTime.gridwidth = 2;
            constraintsJLabelMinRespTime.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJLabelMinRespTime.ipadx = 2;
            constraintsJLabelMinRespTime.ipady = -2;
            constraintsJLabelMinRespTime.insets = new java.awt.Insets(5, 12, 9, 2);
            add(getJLabelMinRespTime(), constraintsJLabelMinRespTime);

            java.awt.GridBagConstraints constraintsJLabelOperationalState = new java.awt.GridBagConstraints();
            constraintsJLabelOperationalState.gridx = 1;
            constraintsJLabelOperationalState.gridy = 4;
            constraintsJLabelOperationalState.gridwidth = 2;
            constraintsJLabelOperationalState.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJLabelOperationalState.ipadx = 10;
            constraintsJLabelOperationalState.ipady = -2;
            constraintsJLabelOperationalState.insets = new java.awt.Insets(9, 12, 7, 8);
            add(getJLabelOperationalState(), constraintsJLabelOperationalState);

            java.awt.GridBagConstraints constraintsJComboBoxOperationalState = new java.awt.GridBagConstraints();
            constraintsJComboBoxOperationalState.gridx = 3;
            constraintsJComboBoxOperationalState.gridy = 4;
            constraintsJComboBoxOperationalState.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsJComboBoxOperationalState.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJComboBoxOperationalState.weightx = 1.0;
            constraintsJComboBoxOperationalState.ipadx = 57;
            constraintsJComboBoxOperationalState.insets = new java.awt.Insets(7, 4, 3, 43);
            add(getJComboBoxOperationalState(), constraintsJComboBoxOperationalState);

            java.awt.GridBagConstraints constraintsJPanelOptional = new java.awt.GridBagConstraints();
            constraintsJPanelOptional.gridx = 1;
            constraintsJPanelOptional.gridy = 6;
            constraintsJPanelOptional.gridwidth = 3;
            constraintsJPanelOptional.fill = java.awt.GridBagConstraints.BOTH;
            constraintsJPanelOptional.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJPanelOptional.weightx = 1.0;
            constraintsJPanelOptional.weighty = 1.0;
            constraintsJPanelOptional.insets = new java.awt.Insets(5, 5, 5, 5);
            add(getJPanelOptional(), constraintsJPanelOptional);

            java.awt.GridBagConstraints constraintsJComboBoxControlInterval = new java.awt.GridBagConstraints();
            constraintsJComboBoxControlInterval.gridx = 3;
            constraintsJComboBoxControlInterval.gridy = 2;
            constraintsJComboBoxControlInterval.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsJComboBoxControlInterval.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJComboBoxControlInterval.weightx = 1.0;
            constraintsJComboBoxControlInterval.ipadx = 57;
            constraintsJComboBoxControlInterval.insets = new java.awt.Insets(6, 3, 2, 44);
            add(getJComboBoxControlInterval(), constraintsJComboBoxControlInterval);

            java.awt.GridBagConstraints constraintsJComboBoxMinRespTime = new java.awt.GridBagConstraints();
            constraintsJComboBoxMinRespTime.gridx = 3;
            constraintsJComboBoxMinRespTime.gridy = 3;
            constraintsJComboBoxMinRespTime.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsJComboBoxMinRespTime.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJComboBoxMinRespTime.weightx = 1.0;
            constraintsJComboBoxMinRespTime.ipadx = 57;
            constraintsJComboBoxMinRespTime.insets = new java.awt.Insets(2, 3, 6, 44);
            add(getJComboBoxMinRespTime(), constraintsJComboBoxMinRespTime);

            java.awt.GridBagConstraints constraintsJCheckBoxReqAllTrigActive = new java.awt.GridBagConstraints();
            constraintsJCheckBoxReqAllTrigActive.gridx = 1;
            constraintsJCheckBoxReqAllTrigActive.gridy = 5;
            constraintsJCheckBoxReqAllTrigActive.gridwidth = 3;
            constraintsJCheckBoxReqAllTrigActive.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJCheckBoxReqAllTrigActive.ipadx = 123;
            constraintsJCheckBoxReqAllTrigActive.ipady = -5;
            constraintsJCheckBoxReqAllTrigActive.insets = new java.awt.Insets(3, 13, 11, 46);
            add(getJCheckBoxReqAllTrigActive(), constraintsJCheckBoxReqAllTrigActive);
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    @Override
    public boolean isInputValid() {
        if (getJTextFieldName().getText() == null || getJTextFieldName().getText().length() <= 0) {
            setErrorString("The Name text field must be filled in");
            return false;
        }

        // used to make sure the starting time is less than the ending time
        int totalStartTime = CtiUtilities.decodeStringToSeconds(getJTextFieldTimeEntryStart().getTimeText());

        if (totalStartTime > CtiUtilities.decodeStringToSeconds(getJTextFieldTimeEntryStop().getTimeText())) {
            setErrorString("The Start Time must be before the Stop Time");
            return false;
        }

        return true;
    }

    @Override
    public void setValue(Object o) {
        if (o != null) {
            LMControlArea controlArea = (LMControlArea) o;

            getJTextFieldName().setText(controlArea.getPAOName());

            if (controlArea.getControlArea().getControlInterval().intValue() == 0) {
                getJComboBoxControlInterval().setSelectedIndex(0);
            } else {
                SwingUtil.setIntervalComboBoxSelectedItem(getJComboBoxControlInterval(), controlArea.getControlArea().getControlInterval().intValue());
            }

            if (controlArea.getControlArea().getMinResponseTime().intValue() == 0) {
                getJComboBoxMinRespTime().setSelectedIndex(0);
            } else {
                SwingUtil.setIntervalComboBoxSelectedItem(getJComboBoxMinRespTime(), controlArea.getControlArea().getMinResponseTime().intValue());
            }

            getJComboBoxOperationalState().setSelectedItem(STRING_MAP.get(controlArea.getControlArea().getDefOperationalState()));

            // do the optional defaults here
            if (controlArea.getControlArea().getDefDailyStartTime().intValue() != com.cannontech.database.db.device.lm.LMControlArea.OPTIONAL_VALUE_UNUSED) {
                getJTextFieldTimeEntryStart().setText(CtiUtilities.decodeSecondsToTime(controlArea.getControlArea().getDefDailyStartTime().intValue()));
            }

            if (controlArea.getControlArea().getDefDailyStopTime().intValue() != com.cannontech.database.db.device.lm.LMControlArea.OPTIONAL_VALUE_UNUSED) {
                getJTextFieldTimeEntryStop().setText(CtiUtilities.decodeSecondsToTime(controlArea.getControlArea().getDefDailyStopTime().intValue()));
            }

            getJCheckBoxReqAllTrigActive().setSelected(Character.toUpperCase(controlArea.getControlArea().getRequireAllTriggersActiveFlag().charValue()) == 'T');
        }

    }

    @Override
    public void setFirstFocus() {
        // Make sure that when its time to display this panel, the focus starts in the top component
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                getJTextFieldName().requestFocus();
            }
        });
    }
}