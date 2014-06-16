package com.cannontech.dbeditor.wizard.device.lmcontrolarea;

import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.util.SwingUtil;
import com.cannontech.database.db.device.lm.IlmDefines;
import com.cannontech.database.db.device.lm.LMControlAreaTrigger;

public class LMTriggerProjectionPanel extends DataInputPanel implements ActionListener {
    private JComboBox<String> ivjJComboBoxType = null;
    private JLabel ivjJLabelType = null;
    private JLabel ivjJLabelAhead = null;
    private JLabel ivjJLabelSamples = null;
    private JComboBox<String> ivjJComboBoxAhead = null;
    private JComboBox<Integer> ivjJComboBoxSamples = null;

    public LMTriggerProjectionPanel() {
        super();
        initialize();
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        try {
            if (e.getSource() == getJComboBoxType()) {
                this.jComboBoxType_ActionPerformed(e);
            }
            
            if (e.getSource() == getJComboBoxAhead() ||
                    e.getSource() == getJComboBoxSamples()) {
                this.fireInputUpdate();
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    private JComboBox<String> getJComboBoxAhead() {
        if (ivjJComboBoxAhead == null) {
            try {
                ivjJComboBoxAhead = new JComboBox<String>();
                ivjJComboBoxAhead.setName("JComboBoxAhead");

                ivjJComboBoxAhead.addItem("5 minute");
                ivjJComboBoxAhead.addItem("10 minute");
                ivjJComboBoxAhead.addItem("15 minute");
                ivjJComboBoxAhead.addItem("20 minute");
                ivjJComboBoxAhead.addItem("25 minute");
                ivjJComboBoxAhead.addItem("30 minute");
                ivjJComboBoxAhead.addItem("35 minute");
                ivjJComboBoxAhead.addItem("40 minute");
                ivjJComboBoxAhead.addItem("45 minute");
                ivjJComboBoxAhead.addItem("50 minute");
                ivjJComboBoxAhead.addItem("55 minute");
                ivjJComboBoxAhead.addItem("1 hour");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJComboBoxAhead;
    }

    private JComboBox<Integer> getJComboBoxSamples() {
        if (ivjJComboBoxSamples == null) {
            try {
                ivjJComboBoxSamples = new JComboBox<Integer>();
                ivjJComboBoxSamples.setName("JComboBoxSamples");

                for (int i = 2; i < 13; i++) {
                    ivjJComboBoxSamples.addItem(i);
                }
                ivjJComboBoxSamples.setSelectedItem(new Integer(5));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJComboBoxSamples;
    }

    private JComboBox<String> getJComboBoxType() {
        if (ivjJComboBoxType == null) {
            try {
                ivjJComboBoxType = new JComboBox<String>();
                ivjJComboBoxType.setName("JComboBoxType");

                ivjJComboBoxType.addItem(IlmDefines.PROJ_TYPE_NONE);
                ivjJComboBoxType.addItem(IlmDefines.PROJ_TYPE_LSF);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJComboBoxType;
    }

    private JLabel getJLabelAhead() {
        if (ivjJLabelAhead == null) {
            try {
                ivjJLabelAhead = new JLabel();
                ivjJLabelAhead.setName("JLabelAhead");
                ivjJLabelAhead.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJLabelAhead.setText("Ahead:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelAhead;
    }

    private JLabel getJLabelSamples() {
        if (ivjJLabelSamples == null) {
            try {
                ivjJLabelSamples = new JLabel();
                ivjJLabelSamples.setName("JLabelSamples");
                ivjJLabelSamples.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJLabelSamples.setText("Samples:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelSamples;
    }

    private JLabel getJLabelType() {
        if (ivjJLabelType == null) {
            try {
                ivjJLabelType = new JLabel();
                ivjJLabelType.setName("JLabelType");
                ivjJLabelType.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJLabelType.setText("Type:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelType;
    }

    public String getSelectedType() {
        return getJComboBoxType().getSelectedItem().toString();
    }

    @Override
    public Object getValue(Object o) {
        LMControlAreaTrigger trigger = null;
        if (o == null) {
            return o;
        } else {
            trigger = (LMControlAreaTrigger) o;
        }

        trigger.setProjectionType(getJComboBoxType().getSelectedItem().toString());
        trigger.setProjectionPoints((Integer) getJComboBoxSamples().getSelectedItem());

        trigger.setProjectAheadDuration(SwingUtil.getIntervalComboBoxSecondsValue(getJComboBoxAhead()));

        return trigger;
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
        getJComboBoxType().addActionListener(this);
        getJComboBoxAhead().addActionListener(this);
        getJComboBoxSamples().addActionListener(this);
    }

    /**
     * Initialize the class.
     */
    private void initialize() {
        try {
            com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
            ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
            ivjLocalBorder.setTitleFont(new java.awt.Font("dialog", 0, 14));
            ivjLocalBorder.setTitle("Projection");
            setName("LMTriggerProjectionPanel");
            setToolTipText("");
            setBorder(ivjLocalBorder);
            setLayout(new java.awt.GridBagLayout());
            setPreferredSize(new java.awt.Dimension(303, 194));
            setSize(271, 112);
            setMinimumSize(new java.awt.Dimension(10, 10));

            java.awt.GridBagConstraints constraintsJLabelType = new java.awt.GridBagConstraints();
            constraintsJLabelType.gridx = 1;
            constraintsJLabelType.gridy = 1;
            constraintsJLabelType.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJLabelType.ipadx = 28;
            constraintsJLabelType.insets = new java.awt.Insets(3, 8, 5, 1);
            add(getJLabelType(), constraintsJLabelType);

            java.awt.GridBagConstraints constraintsJComboBoxType = new java.awt.GridBagConstraints();
            constraintsJComboBoxType.gridx = 2;
            constraintsJComboBoxType.gridy = 1;
            constraintsJComboBoxType.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsJComboBoxType.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJComboBoxType.weightx = 1.0;
            constraintsJComboBoxType.ipadx = 55;
            constraintsJComboBoxType.insets = new java.awt.Insets(3, 3, 7, 11);
            add(getJComboBoxType(), constraintsJComboBoxType);

            java.awt.GridBagConstraints constraintsJLabelSamples = new java.awt.GridBagConstraints();
            constraintsJLabelSamples.gridx = 1;
            constraintsJLabelSamples.gridy = 2;
            constraintsJLabelSamples.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJLabelSamples.ipadx = 6;
            constraintsJLabelSamples.insets = new java.awt.Insets(3, 8, 5, 1);
            add(getJLabelSamples(), constraintsJLabelSamples);

            java.awt.GridBagConstraints constraintsJComboBoxAhead = new java.awt.GridBagConstraints();
            constraintsJComboBoxAhead.gridx = 2;
            constraintsJComboBoxAhead.gridy = 3;
            constraintsJComboBoxAhead.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsJComboBoxAhead.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJComboBoxAhead.weightx = 1.0;
            constraintsJComboBoxAhead.ipadx = 55;
            constraintsJComboBoxAhead.insets = new java.awt.Insets(2, 3, 7, 10);
            add(getJComboBoxAhead(), constraintsJComboBoxAhead);

            java.awt.GridBagConstraints constraintsJLabelAhead = new java.awt.GridBagConstraints();
            constraintsJLabelAhead.gridx = 1;
            constraintsJLabelAhead.gridy = 3;
            constraintsJLabelAhead.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJLabelAhead.ipadx = 19;
            constraintsJLabelAhead.insets = new java.awt.Insets(3, 8, 5, 1);
            add(getJLabelAhead(), constraintsJLabelAhead);

            java.awt.GridBagConstraints constraintsJComboBoxSamples = new java.awt.GridBagConstraints();
            constraintsJComboBoxSamples.gridx = 2;
            constraintsJComboBoxSamples.gridy = 2;
            constraintsJComboBoxSamples.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsJComboBoxSamples.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJComboBoxSamples.weightx = 1.0;
            constraintsJComboBoxSamples.ipadx = 55;
            constraintsJComboBoxSamples.insets = new java.awt.Insets(2, 3, 2, 10);
            add(getJComboBoxSamples(), constraintsJComboBoxSamples);
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        // act like the ComboBox was changed
        jComboBoxType_ActionPerformed(null);
    }

    @Override
    public boolean isInputValid() {
        return true;
    }

    public void jComboBoxType_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
        boolean enabled = !IlmDefines.PROJ_TYPE_NONE.equalsIgnoreCase(getJComboBoxType().getSelectedItem().toString());

        getJLabelSamples().setEnabled(enabled);
        getJComboBoxSamples().setEnabled(enabled);

        getJLabelAhead().setEnabled(enabled);
        getJComboBoxAhead().setEnabled(enabled);

        fireInputUpdate();
        return;
    }

    @Override
    public void setValue(Object o) {
        LMControlAreaTrigger trigger = null;

        if (o == null) {
            return;
        } else {
            trigger = (LMControlAreaTrigger) o;
        }

        getJComboBoxType().setSelectedItem(trigger.getProjectionType());
        getJComboBoxSamples().setSelectedItem(trigger.getProjectionPoints());

        SwingUtil.setIntervalComboBoxSelectedItem(getJComboBoxAhead(), trigger.getProjectAheadDuration().doubleValue());
    }
}