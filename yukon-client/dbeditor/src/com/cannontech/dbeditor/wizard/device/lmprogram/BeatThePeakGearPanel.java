package com.cannontech.dbeditor.wizard.device.lmprogram;

import javax.swing.border.EtchedBorder;

import com.cannontech.database.data.device.lm.BeatThePeakGear;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.cannontech.loadcontrol.gear.model.BeatThePeakGearContainer;

public class BeatThePeakGearPanel extends GenericGearPanel
{
    private javax.swing.JLabel ivjJLabelIndicator = null;
    private javax.swing.JLabel ivjJLabelTimeout = null;
    private javax.swing.JLabel ivjJLabelTimeoutMinutes = null;
    private javax.swing.JLabel ivjJLabelResend = null;
    private javax.swing.JLabel ivjJLabelResendMinutes = null;
    private javax.swing.JLabel ivjJLabelWhenChange = null;
    private javax.swing.JLabel ivjJLabelChangeDuration = null;
    private javax.swing.JLabel ivjJLabelChangeDurationMinutes = null;
    private javax.swing.JLabel ivjJLabelChangePriority = null;
    private javax.swing.JLabel ivjJLabelChangeTriggerNumber = null;
    private javax.swing.JLabel ivjJLabelChangeTriggerOffset = null;
    private javax.swing.JComboBox ivjJComboBoxIndicator = null;
    private com.klg.jclass.field.JCSpinField ivjJCSpinFieldTimeout = null;
    private com.klg.jclass.field.JCSpinField ivjJCSpinFieldResend = null;
    private javax.swing.JComboBox ivjJComboBoxWhenChange = null;
    private com.klg.jclass.field.JCSpinField ivjJCSpinFieldChangeDuration = null;
    private com.klg.jclass.field.JCSpinField ivjJCSpinFieldChangePriority = null;
    private com.klg.jclass.field.JCSpinField ivjJCSpinFieldChangeTriggerNumber = null;
    private javax.swing.JTextField ivjJTextFieldChangeTriggerOffset = null;
    private javax.swing.JPanel ivjJPanelChangeMethod = null;

    private static enum AlertLevel {
        YELLOW("Yellow"), RED("Red");
        private final String alertLevel;

        AlertLevel(String alertLevel) {
            this.alertLevel = alertLevel;
        }

        public String toString() {
            return alertLevel;
        }
    }

    public BeatThePeakGearPanel() {
        super();
        initialize();
    }

    /**
     * Initialize the class.
     */
    private void initialize() {
        try {
            setName("BeatThePeakGearPanel");
            setPreferredSize(new java.awt.Dimension(410, 250));
            setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
            setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
            setLayout(new java.awt.GridBagLayout());
            setSize(402, 250);

            // BTP LED Indicator
            java.awt.GridBagConstraints constraintsJLabelBtpLed = new java.awt.GridBagConstraints();
            constraintsJLabelBtpLed.insets = new java.awt.Insets(2, 0, 5, 2);
            constraintsJLabelBtpLed.anchor = java.awt.GridBagConstraints.NORTHWEST;
            constraintsJLabelBtpLed.gridy = 1;
            constraintsJLabelBtpLed.gridx = 1;
            this.add(getJLabelIndicator(), constraintsJLabelBtpLed);

            java.awt.GridBagConstraints constraintsJComboBoxBtpLed =
                new java.awt.GridBagConstraints();
            constraintsJComboBoxBtpLed.insets = new java.awt.Insets(0, 0, 5, 2);
            constraintsJComboBoxBtpLed.anchor = java.awt.GridBagConstraints.NORTHWEST;
            constraintsJComboBoxBtpLed.gridy = 1;
            constraintsJComboBoxBtpLed.gridx = 2;
            constraintsJComboBoxBtpLed.gridwidth = 2;
            constraintsJComboBoxBtpLed.ipadx = -100;
            this.add(getJComboBoxIndicator(), constraintsJComboBoxBtpLed);

            // Timeout
            java.awt.GridBagConstraints constraintsJLabelTimeout =
                new java.awt.GridBagConstraints();
            constraintsJLabelTimeout.insets = new java.awt.Insets(4, 0, 5, 2);
            constraintsJLabelTimeout.anchor = java.awt.GridBagConstraints.NORTHWEST;
            constraintsJLabelTimeout.gridy = 2;
            constraintsJLabelTimeout.gridx = 1;
            constraintsJLabelTimeout.ipadx = 20;
            this.add(getJLabelTimeout(), constraintsJLabelTimeout);

            java.awt.GridBagConstraints constraintsJCSpinFieldTimeout =
                new java.awt.GridBagConstraints();
            constraintsJCSpinFieldTimeout.insets = new java.awt.Insets(2, 0, 5, 2);
            constraintsJCSpinFieldTimeout.anchor = java.awt.GridBagConstraints.NORTHWEST;
            constraintsJCSpinFieldTimeout.gridy = 2;
            constraintsJCSpinFieldTimeout.gridx = 2;
            this.add(getJCSpinFieldTimeout(), constraintsJCSpinFieldTimeout);

            java.awt.GridBagConstraints constraintsJLabelMinutesTimeout =
                new java.awt.GridBagConstraints();
            constraintsJLabelMinutesTimeout.insets = new java.awt.Insets(4, 6, 5, 2);
            constraintsJLabelMinutesTimeout.anchor = java.awt.GridBagConstraints.NORTHWEST;
            constraintsJLabelMinutesTimeout.gridy = 2;
            constraintsJLabelMinutesTimeout.gridx = 3;
            constraintsJLabelMinutesTimeout.ipadx = 20;
            this.add(getJLabelTimeoutMinutes(), constraintsJLabelMinutesTimeout);

            // Resend
            java.awt.GridBagConstraints constraintsJLabelResend = new java.awt.GridBagConstraints();
            constraintsJLabelResend.insets = new java.awt.Insets(4, 0, 5, 2);
            constraintsJLabelResend.anchor = java.awt.GridBagConstraints.NORTHWEST;
            constraintsJLabelResend.gridy = 3;
            constraintsJLabelResend.gridx = 1;
            constraintsJLabelResend.ipadx = 20;
            this.add(getJLabelResend(), constraintsJLabelResend);

            java.awt.GridBagConstraints constraintsJCSpinFieldResend =
                new java.awt.GridBagConstraints();
            constraintsJCSpinFieldResend.insets = new java.awt.Insets(2, 0, 5, 2);
            constraintsJCSpinFieldResend.anchor = java.awt.GridBagConstraints.NORTHWEST;
            constraintsJCSpinFieldResend.gridy = 3;
            constraintsJCSpinFieldResend.gridx = 2;
            this.add(getJCSpinFieldResend(), constraintsJCSpinFieldResend);

            java.awt.GridBagConstraints constraintsJLabelMinutesResend =
                new java.awt.GridBagConstraints();
            constraintsJLabelMinutesResend.insets = new java.awt.Insets(4, 6, 5, 2);
            constraintsJLabelMinutesResend.anchor = java.awt.GridBagConstraints.NORTHWEST;
            constraintsJLabelMinutesResend.gridy = 3;
            constraintsJLabelMinutesResend.gridx = 3;
            constraintsJLabelMinutesResend.ipadx = 20;
            this.add(getJLabelResendMinutes(), constraintsJLabelMinutesResend);

            // When to Change
            java.awt.GridBagConstraints constraintsJPanelChangeMethod =
                new java.awt.GridBagConstraints();
            constraintsJPanelChangeMethod.insets = new java.awt.Insets(80, 0, 25, 5);
            constraintsJPanelChangeMethod.anchor = java.awt.GridBagConstraints.NORTHWEST;
            constraintsJPanelChangeMethod.gridwidth = 4;
            constraintsJPanelChangeMethod.gridy = 4;
            constraintsJPanelChangeMethod.gridx = 1;
            this.add(getJPanelChangeMethod(), constraintsJPanelChangeMethod);

        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        getJComboBoxWhenChange().setSelectedItem(LMProgramDirectGear.CHANGE_NONE);

        try
        {
            initConnections();
        } catch (Exception e) {}
    }

    /**
     * Initializes connections
     * @exception java.lang.Exception The exception description.
     */
    private void initConnections() throws java.lang.Exception {
        getJCSpinFieldChangeDuration().addValueListener(this);
        getJCSpinFieldChangePriority().addValueListener(this);
        getJCSpinFieldChangeTriggerNumber().addValueListener(this);
        getJCSpinFieldTimeout().addValueListener(this);
        getJCSpinFieldResend().addValueListener(this);
        getJComboBoxWhenChange().addActionListener(this);
        getJComboBoxIndicator().addActionListener(this);
        getJTextFieldChangeTriggerOffset().addCaretListener(this);
    }

    public Object getValue(Object o)
    {
        BeatThePeakGear gear = (BeatThePeakGear) o;

        gear.setChangeCondition(getChangeCondition(getJComboBoxWhenChange().getSelectedItem()
            .toString()));

        gear.setChangeDuration(new Integer(((Number) getJCSpinFieldChangeDuration().getValue())
            .intValue() * 60));
        gear.setChangePriority(new Integer(((Number) getJCSpinFieldChangePriority().getValue())
            .intValue()));
        gear.setChangeTriggerNumber(new Integer(((Number) getJCSpinFieldChangeTriggerNumber()
            .getValue()).intValue()));

        if (getJTextFieldChangeTriggerOffset().getText() == null
            || getJTextFieldChangeTriggerOffset().getText().length() <= 0)
            gear.setChangeTriggerOffset(new Double(0.0));
        else
            gear.setChangeTriggerOffset(Double
                .valueOf(getJTextFieldChangeTriggerOffset().getText()));

        gear.setTimeout(new Integer(((Number) getJCSpinFieldTimeout().getValue()).intValue()));
        gear.setResend(new Integer(60 * ((Number) getJCSpinFieldResend().getValue()).intValue()));

        BeatThePeakGearContainer btpContainer = new BeatThePeakGearContainer();
        String alertLevel = getJComboBoxIndicator().getSelectedItem().toString();
        btpContainer.setAlertLevel(alertLevel);
        gear.setTierGearContainer(btpContainer);

        return gear;
    }

    public void setValue(Object o)
    {
        BeatThePeakGear gear = (BeatThePeakGear) o;
        if (gear == null)
            return;

        setChangeCondition(gear.getChangeCondition());

        getJCSpinFieldChangeDuration()
            .setValue(new Integer(gear.getChangeDuration().intValue() / 60));
        getJCSpinFieldChangePriority().setValue(gear.getChangePriority());
        getJCSpinFieldChangeTriggerNumber().setValue(gear.getChangeTriggerNumber());
        getJTextFieldChangeTriggerOffset().setText(gear.getChangeTriggerOffset().toString());

        getJCSpinFieldTimeout().setValue(new Integer(gear.getTimeout().intValue()));
        getJCSpinFieldResend().setValue(new Integer(gear.getResend().intValue()) / 60);

        BeatThePeakGearContainer tgc = gear.getTierGearContainer();
        String alertLevel = tgc.getAlertLevel();
        getJComboBoxIndicator().setSelectedItem(alertLevel);
    }

    public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1)
    {
        // fire this event for all JCSpinFields!!
        this.fireInputUpdate();
    }

    public void actionPerformed(java.awt.event.ActionEvent e) {
        Object source = e.getSource();
        if (source == getJComboBoxWhenChange()
            || source == getJComboBoxIndicator()) {
            connEtoC1(e);
        }
    }

    public void jComboBoxWhenChange_ActionPerformed(java.awt.event.ActionEvent actionEvent)
    {
        getJLabelChangeDuration().setVisible(false);
        getJCSpinFieldChangeDuration().setVisible(false);
        getJLabelChangeDurationMinutes().setVisible(false);

        getJLabelChangePriority().setVisible(false);
        getJCSpinFieldChangePriority().setVisible(false);

        getJLabelChangeTriggerNumber().setVisible(false);
        getJCSpinFieldChangeTriggerNumber().setVisible(false);

        getJLabelChangeTriggerOffset().setVisible(false);
        getJTextFieldChangeTriggerOffset().setVisible(false);

        if (getJComboBoxWhenChange().getSelectedItem().toString()
            .equalsIgnoreCase(LMProgramDirectGear.CHANGE_NONE)
            || getJComboBoxWhenChange().getSelectedItem().toString()
                .equalsIgnoreCase("Manually Only"))
        {
            // None
            return;
        }
        else if (getJComboBoxWhenChange().getSelectedItem().toString()
            .equalsIgnoreCase(LMProgramDirectGear.CHANGE_DURATION)
                 || getJComboBoxWhenChange().getSelectedItem().toString()
                     .equalsIgnoreCase("After a Duration"))
        {
            // Duration
            getJLabelChangeDuration().setVisible(true);
            getJCSpinFieldChangeDuration().setVisible(true);
            getJLabelChangeDurationMinutes().setVisible(true);
        }
        else if (getJComboBoxWhenChange().getSelectedItem().toString()
            .equalsIgnoreCase(LMProgramDirectGear.CHANGE_PRIORITY)
                 || getJComboBoxWhenChange().getSelectedItem().toString()
                     .equalsIgnoreCase("Priority Change"))
        {
            // Priority
            getJLabelChangePriority().setVisible(true);
            getJCSpinFieldChangePriority().setVisible(true);
        }
        else if (getJComboBoxWhenChange().getSelectedItem().toString()
            .equalsIgnoreCase(LMProgramDirectGear.CHANGE_TRIGGER_OFFSET)
                 || getJComboBoxWhenChange().getSelectedItem().toString()
                     .equalsIgnoreCase("Above Trigger"))
        {
            // TriggerOffset
            getJLabelChangeTriggerNumber().setVisible(true);
            getJCSpinFieldChangeTriggerNumber().setVisible(true);
            getJLabelChangeTriggerOffset().setVisible(true);
            getJTextFieldChangeTriggerOffset().setVisible(true);
        }
        else
            throw new Error("Unknown LMProgramDirectGear control condition found, the value = "
                            + getJComboBoxWhenChange().getSelectedItem().toString());

        fireInputUpdate();
        return;
    }

    /**
     * connEtoC1: (JComboBoxWhenChange.action.actionPerformed(java.awt.event.ActionEvent) -->
     * DirectModifyGearPanel.jComboBoxWhenChange_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
     * @param arg1 java.awt.event.ActionEvent
     */
    private void connEtoC1(java.awt.event.ActionEvent arg1) {
        try {
            this.jComboBoxWhenChange_ActionPerformed(arg1);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    private javax.swing.JLabel getJLabelIndicator() {
        if (ivjJLabelIndicator == null) {
            try {
                ivjJLabelIndicator = new javax.swing.JLabel();
                ivjJLabelIndicator.setName("JLabelIndicator");
                ivjJLabelIndicator.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
                ivjJLabelIndicator.setText("BTP LED Indicator:");
                ivjJLabelIndicator.setMaximumSize(new java.awt.Dimension(150, 14));
                ivjJLabelIndicator.setPreferredSize(new java.awt.Dimension(150, 14));
                ivjJLabelIndicator.setFont(new java.awt.Font("dialog", 0, 12));
                ivjJLabelIndicator.setMinimumSize(new java.awt.Dimension(150, 14));
            } catch (java.lang.Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjJLabelIndicator;
    }

    private javax.swing.JLabel getJLabelTimeout() {
        if (ivjJLabelTimeout == null) {
            try {
                ivjJLabelTimeout = new javax.swing.JLabel();
                ivjJLabelTimeout.setName("JLabelTimeout");
                ivjJLabelTimeout.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
                ivjJLabelTimeout.setText("Max Indicator Timeout:");
                ivjJLabelTimeout.setMaximumSize(new java.awt.Dimension(103, 14));
                ivjJLabelTimeout.setPreferredSize(new java.awt.Dimension(103, 14));
                ivjJLabelTimeout.setFont(new java.awt.Font("dialog", 0, 12));
                ivjJLabelTimeout.setMinimumSize(new java.awt.Dimension(103, 14));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelTimeout;
    }

    private javax.swing.JLabel getJLabelTimeoutMinutes() {
        if (ivjJLabelTimeoutMinutes == null) {
            try {
                ivjJLabelTimeoutMinutes = new javax.swing.JLabel();
                ivjJLabelTimeoutMinutes.setName("JLabelMinutesTimeout");
                ivjJLabelTimeoutMinutes.setFont(new java.awt.Font("dialog", 0, 12));
                ivjJLabelTimeoutMinutes.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
                ivjJLabelTimeoutMinutes.setText("(min.)");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelTimeoutMinutes;
    }

    private javax.swing.JLabel getJLabelResend() {
        if (ivjJLabelResend == null) {
            try {
                ivjJLabelResend = new javax.swing.JLabel();
                ivjJLabelResend.setName("JLabelResend");
                ivjJLabelResend.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
                ivjJLabelResend.setText("Resend Rate:");
                ivjJLabelResend.setMaximumSize(new java.awt.Dimension(103, 14));
                ivjJLabelResend.setPreferredSize(new java.awt.Dimension(103, 14));
                ivjJLabelResend.setFont(new java.awt.Font("dialog", 0, 12));
                ivjJLabelResend.setMinimumSize(new java.awt.Dimension(103, 14));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelResend;
    }

    private javax.swing.JLabel getJLabelResendMinutes() {
        if (ivjJLabelResendMinutes == null) {
            try {
                ivjJLabelResendMinutes = new javax.swing.JLabel();
                ivjJLabelResendMinutes.setName("JLabelMinutesResend");
                ivjJLabelResendMinutes.setFont(new java.awt.Font("dialog", 0, 12));
                ivjJLabelResendMinutes.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
                ivjJLabelResendMinutes.setText("(min.)");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelResendMinutes;
    }

    private javax.swing.JLabel getJLabelWhenChange() {
        if (ivjJLabelWhenChange == null) {
            try {
                ivjJLabelWhenChange = new javax.swing.JLabel();
                ivjJLabelWhenChange.setName("JLabelWhenChange");
                ivjJLabelWhenChange.setFont(new java.awt.Font("dialog", 0, 12));
                ivjJLabelWhenChange.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
                ivjJLabelWhenChange.setText("When to Change:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelWhenChange;
    }

    private javax.swing.JLabel getJLabelChangeDuration() {
        if (ivjJLabelChangeDuration == null) {
            try {
                ivjJLabelChangeDuration = new javax.swing.JLabel();
                ivjJLabelChangeDuration.setName("JLabelChangeDuration");
                ivjJLabelChangeDuration.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
                ivjJLabelChangeDuration.setText("Change Duration:");
                ivjJLabelChangeDuration.setMaximumSize(new java.awt.Dimension(103, 14));
                ivjJLabelChangeDuration.setPreferredSize(new java.awt.Dimension(103, 14));
                ivjJLabelChangeDuration.setFont(new java.awt.Font("dialog", 0, 12));
                ivjJLabelChangeDuration.setMinimumSize(new java.awt.Dimension(103, 14));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelChangeDuration;
    }

    private javax.swing.JLabel getJLabelChangeDurationMinutes() {
        if (ivjJLabelChangeDurationMinutes == null) {
            try {
                ivjJLabelChangeDurationMinutes = new javax.swing.JLabel();
                ivjJLabelChangeDurationMinutes.setName("JLabelMinutesChDur");
                ivjJLabelChangeDurationMinutes.setFont(new java.awt.Font("dialog", 0, 12));
                ivjJLabelChangeDurationMinutes.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
                ivjJLabelChangeDurationMinutes.setText("(min.)");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelChangeDurationMinutes;
    }

    private javax.swing.JLabel getJLabelChangePriority() {
        if (ivjJLabelChangePriority == null) {
            try {
                ivjJLabelChangePriority = new javax.swing.JLabel();
                ivjJLabelChangePriority.setName("JLabelChangePriority");
                ivjJLabelChangePriority.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
                ivjJLabelChangePriority.setText("Change Priority:");
                ivjJLabelChangePriority.setMaximumSize(new java.awt.Dimension(103, 14));
                ivjJLabelChangePriority.setPreferredSize(new java.awt.Dimension(103, 14));
                ivjJLabelChangePriority.setFont(new java.awt.Font("dialog", 0, 12));
                ivjJLabelChangePriority.setMinimumSize(new java.awt.Dimension(103, 14));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelChangePriority;
    }

    private javax.swing.JLabel getJLabelChangeTriggerNumber() {
        if (ivjJLabelChangeTriggerNumber == null) {
            try {
                ivjJLabelChangeTriggerNumber = new javax.swing.JLabel();
                ivjJLabelChangeTriggerNumber.setName("JLabelChangeTriggerNumber");
                ivjJLabelChangeTriggerNumber.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
                ivjJLabelChangeTriggerNumber.setText("Trigger Number:");
                ivjJLabelChangeTriggerNumber.setMaximumSize(new java.awt.Dimension(2, 14));
                ivjJLabelChangeTriggerNumber
                    .setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
                ivjJLabelChangeTriggerNumber.setPreferredSize(new java.awt.Dimension(143, 14));
                ivjJLabelChangeTriggerNumber.setFont(new java.awt.Font("dialog", 0, 12));
                ivjJLabelChangeTriggerNumber.setMinimumSize(new java.awt.Dimension(143, 14));
                ivjJLabelChangeTriggerNumber
                    .setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelChangeTriggerNumber;
    }

    private javax.swing.JLabel getJLabelChangeTriggerOffset() {
        if (ivjJLabelChangeTriggerOffset == null) {
            try {
                ivjJLabelChangeTriggerOffset = new javax.swing.JLabel();
                ivjJLabelChangeTriggerOffset.setName("JLabelChangeTriggerOffset");
                ivjJLabelChangeTriggerOffset.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
                ivjJLabelChangeTriggerOffset.setText("Trigger Offset:");
                ivjJLabelChangeTriggerOffset.setMaximumSize(new java.awt.Dimension(143, 14));
                ivjJLabelChangeTriggerOffset
                    .setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
                ivjJLabelChangeTriggerOffset.setPreferredSize(new java.awt.Dimension(143, 14));
                ivjJLabelChangeTriggerOffset.setFont(new java.awt.Font("dialog", 0, 12));
                ivjJLabelChangeTriggerOffset.setMinimumSize(new java.awt.Dimension(143, 14));
                ivjJLabelChangeTriggerOffset
                    .setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelChangeTriggerOffset;
    }

    private javax.swing.JComboBox getJComboBoxIndicator() {
        if (ivjJComboBoxIndicator == null) {
            try {
                ivjJComboBoxIndicator = new javax.swing.JComboBox();
                ivjJComboBoxIndicator.setName("JComboBoxIndicator");
                ivjJComboBoxIndicator.setPreferredSize(new java.awt.Dimension(195, 23));
                ivjJComboBoxIndicator.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);

                for (AlertLevel value : AlertLevel.values()) {
                    ivjJComboBoxIndicator.addItem(value.toString());
                }

            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJComboBoxIndicator;
    }

    private com.klg.jclass.field.JCSpinField getJCSpinFieldTimeout() {
        if (ivjJCSpinFieldTimeout == null) {
            try {
                ivjJCSpinFieldTimeout = new com.klg.jclass.field.JCSpinField();
                ivjJCSpinFieldTimeout.setName("JCSpinFieldTimeout");
                ivjJCSpinFieldTimeout.setPreferredSize(new java.awt.Dimension(35, 20));
                ivjJCSpinFieldTimeout.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
                ivjJCSpinFieldTimeout.setMaximumSize(new java.awt.Dimension(40, 20));

                ivjJCSpinFieldTimeout
                    .setDataProperties(
                    new com.klg.jclass.field.DataProperties(
                                                            new com.klg.jclass.field.validate.JCIntegerValidator(
                                                                                                                 null,
                                                                                                                 new Integer(0),
                                                                                                                 new Integer(99999),
                                                                                                                 null,
                                                                                                                 true,
                                                                                                                 null,
                                                                                                                 new Integer(1),
                                                                                                                 "#,##0.###;-#,##0.###",
                                                                                                                 false,
                                                                                                                 false,
                                                                                                                 false,
                                                                                                                 null,
                                                                                                                 new Integer(3)),
                                                            new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class,
                                                                                                            new Integer(0)),
                                                            new com.klg.jclass.field.JCInvalidInfo(true,
                                                                                                   2,
                                                                                                   new java.awt.Color(0,
                                                                                                                      0,
                                                                                                                      0,
                                                                                                                      255),
                                                                                                   new java.awt.Color(255,
                                                                                                                      255,
                                                                                                                      255,
                                                                                                                      255))));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJCSpinFieldTimeout;
    }

    private com.klg.jclass.field.JCSpinField getJCSpinFieldResend() {
        if (ivjJCSpinFieldResend == null) {
            try {
                ivjJCSpinFieldResend = new com.klg.jclass.field.JCSpinField();
                ivjJCSpinFieldResend.setName("JCSpinFieldResend");
                ivjJCSpinFieldResend.setPreferredSize(new java.awt.Dimension(35, 20));
                ivjJCSpinFieldResend.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
                ivjJCSpinFieldResend.setMaximumSize(new java.awt.Dimension(40, 20));

                ivjJCSpinFieldResend
                    .setDataProperties(
                    new com.klg.jclass.field.DataProperties(
                                                            new com.klg.jclass.field.validate.JCIntegerValidator(
                                                                                                                 null,
                                                                                                                 new Integer(0),
                                                                                                                 new Integer(99999),
                                                                                                                 null,
                                                                                                                 true,
                                                                                                                 null,
                                                                                                                 new Integer(1),
                                                                                                                 "#,##0.###;-#,##0.###",
                                                                                                                 false,
                                                                                                                 false,
                                                                                                                 false,
                                                                                                                 null,
                                                                                                                 new Integer(3)),
                                                            new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class,
                                                                                                            new Integer(0)),
                                                            new com.klg.jclass.field.JCInvalidInfo(true,
                                                                                                   2,
                                                                                                   new java.awt.Color(0,
                                                                                                                      0,
                                                                                                                      0,
                                                                                                                      255),
                                                                                                   new java.awt.Color(255,
                                                                                                                      255,
                                                                                                                      255,
                                                                                                                      255))));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJCSpinFieldResend;
    }

    private javax.swing.JComboBox getJComboBoxWhenChange() {
        if (ivjJComboBoxWhenChange == null) {
            try {
                ivjJComboBoxWhenChange = new javax.swing.JComboBox();
                ivjJComboBoxWhenChange.setName("JComboBoxWhenChange");
                ivjJComboBoxWhenChange.setPreferredSize(new java.awt.Dimension(195, 23));
                ivjJComboBoxWhenChange.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);

                ivjJComboBoxWhenChange.addItem("Manually Only");
                ivjJComboBoxWhenChange.addItem("After a Duration");
                ivjJComboBoxWhenChange.addItem("Priority Change");
                ivjJComboBoxWhenChange.addItem("Above Trigger");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJComboBoxWhenChange;
    }

    private com.klg.jclass.field.JCSpinField getJCSpinFieldChangeDuration() {
        if (ivjJCSpinFieldChangeDuration == null) {
            try {
                ivjJCSpinFieldChangeDuration = new com.klg.jclass.field.JCSpinField();
                ivjJCSpinFieldChangeDuration.setName("JCSpinFieldChangeDuration");
                ivjJCSpinFieldChangeDuration.setPreferredSize(new java.awt.Dimension(35, 20));
                ivjJCSpinFieldChangeDuration.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
                ivjJCSpinFieldChangeDuration.setMaximumSize(new java.awt.Dimension(40, 20));

                ivjJCSpinFieldChangeDuration
                    .setDataProperties(
                    new com.klg.jclass.field.DataProperties(
                                                            new com.klg.jclass.field.validate.JCIntegerValidator(
                                                                                                                 null,
                                                                                                                 new Integer(0),
                                                                                                                 new Integer(99999),
                                                                                                                 null,
                                                                                                                 true,
                                                                                                                 null,
                                                                                                                 new Integer(1),
                                                                                                                 "#,##0.###;-#,##0.###",
                                                                                                                 false,
                                                                                                                 false,
                                                                                                                 false,
                                                                                                                 null,
                                                                                                                 new Integer(3)),
                                                            new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class,
                                                                                                            new Integer(0)),
                                                            new com.klg.jclass.field.JCInvalidInfo(true,
                                                                                                   2,
                                                                                                   new java.awt.Color(0,
                                                                                                                      0,
                                                                                                                      0,
                                                                                                                      255),
                                                                                                   new java.awt.Color(255,
                                                                                                                      255,
                                                                                                                      255,
                                                                                                                      255))));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJCSpinFieldChangeDuration;
    }

    private com.klg.jclass.field.JCSpinField getJCSpinFieldChangePriority() {
        if (ivjJCSpinFieldChangePriority == null) {
            try {
                ivjJCSpinFieldChangePriority = new com.klg.jclass.field.JCSpinField();
                ivjJCSpinFieldChangePriority.setName("JCSpinFieldChangePriority");
                ivjJCSpinFieldChangePriority.setPreferredSize(new java.awt.Dimension(30, 20));
                ivjJCSpinFieldChangePriority.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
                ivjJCSpinFieldChangePriority.setMaximumSize(new java.awt.Dimension(40, 30));

                ivjJCSpinFieldChangePriority
                    .setDataProperties(
                    new com.klg.jclass.field.DataProperties(
                                                            new com.klg.jclass.field.validate.JCIntegerValidator(
                                                                                                                 null,
                                                                                                                 new Integer(0),
                                                                                                                 new Integer(9999),
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
                                                            new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class,
                                                                                                            new Integer(0)),
                                                            new com.klg.jclass.field.JCInvalidInfo(true,
                                                                                                   2,
                                                                                                   new java.awt.Color(0,
                                                                                                                      0,
                                                                                                                      0,
                                                                                                                      255),
                                                                                                   new java.awt.Color(255,
                                                                                                                      255,
                                                                                                                      255,
                                                                                                                      255))));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJCSpinFieldChangePriority;
    }

    private com.klg.jclass.field.JCSpinField getJCSpinFieldChangeTriggerNumber() {
        if (ivjJCSpinFieldChangeTriggerNumber == null) {
            try {
                ivjJCSpinFieldChangeTriggerNumber = new com.klg.jclass.field.JCSpinField();
                ivjJCSpinFieldChangeTriggerNumber.setName("JCSpinFieldChangeTriggerNumber");
                ivjJCSpinFieldChangeTriggerNumber.setPreferredSize(new java.awt.Dimension(35, 20));
                ivjJCSpinFieldChangeTriggerNumber.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
                ivjJCSpinFieldChangeTriggerNumber.setMaximumSize(new java.awt.Dimension(40, 20));
                ivjJCSpinFieldChangeTriggerNumber
                    .setDataProperties(
                    new com.klg.jclass.field.DataProperties(
                                                            new com.klg.jclass.field.validate.JCIntegerValidator(
                                                                                                                 null,
                                                                                                                 new Integer(1),
                                                                                                                 new Integer(99999),
                                                                                                                 null,
                                                                                                                 true,
                                                                                                                 null,
                                                                                                                 new Integer(1),
                                                                                                                 "#,##0.###;-#,##0.###",
                                                                                                                 false,
                                                                                                                 false,
                                                                                                                 false,
                                                                                                                 null,
                                                                                                                 new Integer(1)),
                                                            new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class,
                                                                                                            new Integer(0)),
                                                            new com.klg.jclass.field.JCInvalidInfo(true,
                                                                                                   2,
                                                                                                   new java.awt.Color(0,
                                                                                                                      0,
                                                                                                                      0,
                                                                                                                      255),
                                                                                                   new java.awt.Color(255,
                                                                                                                      255,
                                                                                                                      255,
                                                                                                                      255))));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJCSpinFieldChangeTriggerNumber;
    }

    private javax.swing.JTextField getJTextFieldChangeTriggerOffset() {
        if (ivjJTextFieldChangeTriggerOffset == null) {
            try {
                ivjJTextFieldChangeTriggerOffset = new javax.swing.JTextField();
                ivjJTextFieldChangeTriggerOffset.setName("JTextFieldChangeTriggerOffset");
                ivjJTextFieldChangeTriggerOffset.setMinimumSize(new java.awt.Dimension(40, 20));
                ivjJTextFieldChangeTriggerOffset.setPreferredSize(new java.awt.Dimension(40, 20));
                ivjJTextFieldChangeTriggerOffset.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
                ivjJTextFieldChangeTriggerOffset.setMaximumSize(new java.awt.Dimension(40, 20));

                ivjJTextFieldChangeTriggerOffset
                    .setDocument(new com.cannontech.common.gui.unchanging.DoubleRangeDocument(-99999.9999,
                                                                                              99999.9999,
                                                                                              4));

            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJTextFieldChangeTriggerOffset;
    }

    private javax.swing.JPanel getJPanelChangeMethod() {
        if (ivjJPanelChangeMethod == null) {
            try {
                ivjJPanelChangeMethod = new javax.swing.JPanel();
                ivjJPanelChangeMethod.setName("JPanelChangeMethod");
                ivjJPanelChangeMethod.setLayout(new java.awt.GridBagLayout());
                ivjJPanelChangeMethod.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
                ivjJPanelChangeMethod.setMaximumSize(new java.awt.Dimension(335, 88));
                ivjJPanelChangeMethod.setPreferredSize(new java.awt.Dimension(335, 88));
                ivjJPanelChangeMethod.setFont(new java.awt.Font("dialog", 0, 12));
                ivjJPanelChangeMethod.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

                // When to Change

                java.awt.GridBagConstraints constraintsJLabelWhenChange =
                    new java.awt.GridBagConstraints();
                constraintsJLabelWhenChange.gridx = 1;
                constraintsJLabelWhenChange.gridy = 1;
                constraintsJLabelWhenChange.anchor = java.awt.GridBagConstraints.NORTHWEST;
                constraintsJLabelWhenChange.ipadx = 3;
                constraintsJLabelWhenChange.ipady = 4;
                constraintsJLabelWhenChange.insets = new java.awt.Insets(4, 5, 4, 5);
                getJPanelChangeMethod().add(getJLabelWhenChange(), constraintsJLabelWhenChange);

                java.awt.GridBagConstraints constraintsJComboBoxWhenChange =
                    new java.awt.GridBagConstraints();
                constraintsJComboBoxWhenChange.gridx = 2;
                constraintsJComboBoxWhenChange.gridy = 1;
                constraintsJComboBoxWhenChange.gridwidth = 4;
                constraintsJComboBoxWhenChange.anchor = java.awt.GridBagConstraints.NORTHWEST;
                constraintsJComboBoxWhenChange.weightx = 1.0;
                constraintsJComboBoxWhenChange.ipadx = 79;
                constraintsJComboBoxWhenChange.insets = new java.awt.Insets(4, 5, 1, 17);
                getJPanelChangeMethod().add(getJComboBoxWhenChange(),
                                            constraintsJComboBoxWhenChange);

                // After a Duration
                java.awt.GridBagConstraints constraintsJLabelChangeDuration =
                    new java.awt.GridBagConstraints();
                constraintsJLabelChangeDuration.gridx = 1;
                constraintsJLabelChangeDuration.gridy = 3;
                constraintsJLabelChangeDuration.anchor = java.awt.GridBagConstraints.NORTHWEST;
                constraintsJLabelChangeDuration.ipadx = -5;
                constraintsJLabelChangeDuration.ipady = 6;
                constraintsJLabelChangeDuration.insets = new java.awt.Insets(1, 5, 3, 5);
                getJPanelChangeMethod().add(getJLabelChangeDuration(),
                                            constraintsJLabelChangeDuration);

                java.awt.GridBagConstraints constraintsJCSpinFieldChangeDuration =
                    new java.awt.GridBagConstraints();
                constraintsJCSpinFieldChangeDuration.gridx = 2;
                constraintsJCSpinFieldChangeDuration.gridy = 3;
                constraintsJCSpinFieldChangeDuration.anchor = java.awt.GridBagConstraints.NORTHWEST;
                constraintsJCSpinFieldChangeDuration.ipadx = 34;
                constraintsJCSpinFieldChangeDuration.ipady = 19;
                constraintsJCSpinFieldChangeDuration.insets = new java.awt.Insets(1, 5, 3, 2);
                getJPanelChangeMethod().add(getJCSpinFieldChangeDuration(),
                                            constraintsJCSpinFieldChangeDuration);

                java.awt.GridBagConstraints constraintsJLabelMinutesChDur =
                    new java.awt.GridBagConstraints();
                constraintsJLabelMinutesChDur.gridx = 3;
                constraintsJLabelMinutesChDur.gridy = 3;
                constraintsJLabelMinutesChDur.anchor = java.awt.GridBagConstraints.NORTHWEST;
                constraintsJLabelMinutesChDur.ipadx = 5;
                constraintsJLabelMinutesChDur.ipady = -2;
                constraintsJLabelMinutesChDur.insets = new java.awt.Insets(5, 3, 5, 5);
                getJPanelChangeMethod().add(getJLabelChangeDurationMinutes(),
                                            constraintsJLabelMinutesChDur);

                // Priority Change
                java.awt.GridBagConstraints constraintsJLabelChangePriority =
                    new java.awt.GridBagConstraints();
                constraintsJLabelChangePriority.gridx = 1;
                constraintsJLabelChangePriority.gridy = 3;
                constraintsJLabelChangePriority.anchor = java.awt.GridBagConstraints.NORTHWEST;
                constraintsJLabelChangePriority.ipadx = -13;
                constraintsJLabelChangePriority.ipady = 6;
                constraintsJLabelChangePriority.insets = new java.awt.Insets(1, 6, 3, 3);
                getJPanelChangeMethod().add(getJLabelChangePriority(),
                                            constraintsJLabelChangePriority);

                java.awt.GridBagConstraints constraintsJCSpinFieldChangePriority =
                    new java.awt.GridBagConstraints();
                constraintsJCSpinFieldChangePriority.gridx = 2;
                constraintsJCSpinFieldChangePriority.gridy = 3;
                constraintsJCSpinFieldChangePriority.anchor = java.awt.GridBagConstraints.NORTHWEST;
                constraintsJCSpinFieldChangePriority.ipadx = 29;
                constraintsJCSpinFieldChangePriority.ipady = 19;
                constraintsJCSpinFieldChangePriority.insets = new java.awt.Insets(1, 3, 3, 8);
                getJPanelChangeMethod().add(getJCSpinFieldChangePriority(),
                                            constraintsJCSpinFieldChangePriority);

                // Above Trigger

                java.awt.GridBagConstraints constraintsJLabelChangeTriggerNumber =
                    new java.awt.GridBagConstraints();
                constraintsJLabelChangeTriggerNumber.gridx = 1;
                constraintsJLabelChangeTriggerNumber.gridy = 3;
                constraintsJLabelChangeTriggerNumber.anchor = java.awt.GridBagConstraints.NORTHWEST;
                constraintsJLabelChangeTriggerNumber.ipadx = -45;
                constraintsJLabelChangeTriggerNumber.ipady = 6;
                constraintsJLabelChangeTriggerNumber.insets = new java.awt.Insets(4, 5, 21, 5);
                getJPanelChangeMethod().add(getJLabelChangeTriggerNumber(),
                                            constraintsJLabelChangeTriggerNumber);

                java.awt.GridBagConstraints constraintsJLabelChangeTriggerOffset =
                    new java.awt.GridBagConstraints();
                constraintsJLabelChangeTriggerOffset.gridx = 4;
                constraintsJLabelChangeTriggerOffset.gridy = 3;
                constraintsJLabelChangeTriggerOffset.anchor = java.awt.GridBagConstraints.NORTHWEST;
                constraintsJLabelChangeTriggerOffset.ipadx = -63;
                constraintsJLabelChangeTriggerOffset.insets = new java.awt.Insets(8, 3, 23, 13);
                getJPanelChangeMethod().add(getJLabelChangeTriggerOffset(),
                                            constraintsJLabelChangeTriggerOffset);

                java.awt.GridBagConstraints constraintsJTextFieldChangeTriggerOffset =
                    new java.awt.GridBagConstraints();
                constraintsJTextFieldChangeTriggerOffset.gridx = 5;
                constraintsJTextFieldChangeTriggerOffset.gridy = 3;
                constraintsJTextFieldChangeTriggerOffset.anchor =
                    java.awt.GridBagConstraints.NORTHWEST;
                constraintsJTextFieldChangeTriggerOffset.weightx = 1.0;
                constraintsJTextFieldChangeTriggerOffset.ipadx = 26;
                constraintsJTextFieldChangeTriggerOffset.insets = new java.awt.Insets(4, 4, 21, 8);
                getJPanelChangeMethod().add(getJTextFieldChangeTriggerOffset(),
                                            constraintsJTextFieldChangeTriggerOffset);

                java.awt.GridBagConstraints constraintsJCSpinFieldChangeTriggerNumber =
                    new java.awt.GridBagConstraints();
                constraintsJCSpinFieldChangeTriggerNumber.gridx = 2;
                constraintsJCSpinFieldChangeTriggerNumber.gridy = 3;
                constraintsJCSpinFieldChangeTriggerNumber.anchor =
                    java.awt.GridBagConstraints.NORTHWEST;
                constraintsJCSpinFieldChangeTriggerNumber.ipadx = 34;
                constraintsJCSpinFieldChangeTriggerNumber.ipady = 19;
                constraintsJCSpinFieldChangeTriggerNumber.insets = new java.awt.Insets(4, 5, 21, 4);
                getJPanelChangeMethod().add(getJCSpinFieldChangeTriggerNumber(),
                                            constraintsJCSpinFieldChangeTriggerNumber);

                ivjJPanelChangeMethod.setBorder(new EtchedBorder());

                jComboBoxWhenChange_ActionPerformed(null);

            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJPanelChangeMethod;
    }

    private void setChangeCondition(String change)
    {
        if (change == null)
            return;

        if (change.equalsIgnoreCase(LMProgramDirectGear.CHANGE_NONE))
        {
            getJComboBoxWhenChange().setSelectedItem("Manually Only");
        }
        else if (change.equalsIgnoreCase(LMProgramDirectGear.CHANGE_DURATION))
        {
            getJComboBoxWhenChange().setSelectedItem("After a Duration");
        }
        else if (change.equalsIgnoreCase(LMProgramDirectGear.CHANGE_PRIORITY))
        {
            getJComboBoxWhenChange().setSelectedItem("Priority Change");
        }
        else if (change.equalsIgnoreCase(LMProgramDirectGear.CHANGE_TRIGGER_OFFSET))
        {
            getJComboBoxWhenChange().setSelectedItem("Above Trigger");
        }

    }

    /**
     * main entrypoint - starts the part when it is run as an application
     * @param args java.lang.String[]
     */
    public static void main(java.lang.String[] args) {
        try {
            javax.swing.JFrame frame = new javax.swing.JFrame();
            BeatThePeakGearPanel aBeatThePeakGearPanel;
            aBeatThePeakGearPanel = new BeatThePeakGearPanel();
            frame.setContentPane(aBeatThePeakGearPanel);
            frame.setSize(aBeatThePeakGearPanel.getSize());
            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                };
            });
            frame.setVisible(true);
            java.awt.Insets insets = frame.getInsets();
            frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight()
                                                                         + insets.top
                                                                         + insets.bottom);
            frame.setVisible(true);
        } catch (Throwable exception) {
            System.err
                .println("Exception occurred in main() of com.cannontech.dbeditor.wizard.device.lmprogram.GenericGearPanel");
            exception.printStackTrace(System.out);
        }
    }

    /**
     * Called whenever the part throws an exception.
     * @param exception java.lang.Throwable
     */
    private void handleException(java.lang.Throwable exception) {
        /* Uncomment the following lines to print uncaught exceptions to stdout */
        // System.out.println("--------- UNCAUGHT EXCEPTION ---------");
        // exception.printStackTrace(System.out);
    }

}
