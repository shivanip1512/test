package com.cannontech.dbeditor.wizard.device.lmcontrolarea;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.CaretListener;

import com.cannontech.common.gui.unchanging.DoubleRangeDocument;
import com.cannontech.common.gui.unchanging.LongRangeDocument;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.JPanelDevicePoint;
import com.cannontech.common.gui.util.OkCancelDialog;
import com.cannontech.common.util.SwingUtil;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.db.device.lm.IlmDefines;
import com.cannontech.database.db.device.lm.LMControlAreaTrigger;
import com.cannontech.spring.YukonSpringHook;
import com.google.common.collect.Lists;

public class LMControlAreaTriggerModifyPanel extends DataInputPanel implements ActionListener, PropertyChangeListener, CaretListener {
    private JComboBox<LiteState> ivjJComboBoxNormalState = null;
    private JComboBox<String> ivjJComboBoxType = null;
    private JLabel ivjJLabelNormalStateAndThreshold = null;
    private JLabel ivjJLabelType = null;
    private JTextField ivjJTextFieldThreshold = null;
    private JPanelDevicePoint ivjJPanelThresholdID = null;
    private JLabel ivjJLabelMinRestOffset = null;
    private JTextField ivjJTextFieldMinRestOffset = null;
    // a mutable lite point used for comparisons
    private JPanelDevicePoint ivjJPanelDevicePointPeak = null;
    private JCheckBox ivjJCheckBoxPeakTracking = null;
    private JPanel ivjJPanelPeakTracking = null;
    private JPanelDevicePoint ivjJPanelTriggerID = null;
    private JButton ivjJButtonProjection = null;
    private LMTriggerProjectionPanel triggerProjPanel = null;
    private JLabel ivjJLabelATKU = null;
    private JTextField ivjJTextFieldATKU = null;

    public LMControlAreaTriggerModifyPanel() {
        super();
        initialize();
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        try {
            if (e.getSource() == getJComboBoxType()) {
                this.jComboBoxType_ActionPerformed(e);
            }
            if (e.getSource() == getJComboBoxNormalState()) {
                this.fireInputUpdate();
            }
            if (e.getSource() == getJCheckBoxPeakTracking()) {
                this.jCheckBoxPeakTracking_ActionPerformed(e);
            }
            if (e.getSource() == getJButtonProjection()) {
                this.jButtonProjection_ActionPerformed(e);
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    @Override
    public void caretUpdate(javax.swing.event.CaretEvent e) {
        try {
            if (e.getSource() == getJTextFieldThreshold() ||
                    e.getSource() == getJTextFieldMinRestOffset() ||
                    e.getSource() == getJTextFieldATKU()) {
                this.fireInputUpdate();
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    private JButton getJButtonProjection() {
        if (ivjJButtonProjection == null) {
            try {
                ivjJButtonProjection = new JButton();
                ivjJButtonProjection.setName("JButtonProjection");
                ivjJButtonProjection.setToolTipText("Allows access to the Projection values");
                ivjJButtonProjection.setText("Projection...");

                ivjJButtonProjection.setText("Projection..." + IlmDefines.PROJ_TYPE_NONE);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJButtonProjection;
    }

    private JCheckBox getJCheckBoxPeakTracking() {
        if (ivjJCheckBoxPeakTracking == null) {
            try {
                ivjJCheckBoxPeakTracking = new JCheckBox();
                ivjJCheckBoxPeakTracking.setName("JCheckBoxPeakTracking");
                ivjJCheckBoxPeakTracking.setMnemonic('u');
                ivjJCheckBoxPeakTracking.setText("Use Peak Tracking");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJCheckBoxPeakTracking;
    }

    private JComboBox<LiteState> getJComboBoxNormalState() {
        if (ivjJComboBoxNormalState == null) {
            try {
                ivjJComboBoxNormalState = new JComboBox<LiteState>();
                ivjJComboBoxNormalState.setName("JComboBoxNormalState");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJComboBoxNormalState;
    }

    private JComboBox<String> getJComboBoxType() {
        if (ivjJComboBoxType == null) {
            try {
                ivjJComboBoxType = new JComboBox<String>();
                ivjJComboBoxType.setName("JComboBoxType");

                ivjJComboBoxType.addItem(IlmDefines.TYPE_THRESHOLD_POINT);
                ivjJComboBoxType.addItem(IlmDefines.TYPE_THRESHOLD);
                ivjJComboBoxType.addItem(IlmDefines.TYPE_STATUS);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJComboBoxType;
    }

    private JLabel getJLabelATKU() {
        if (ivjJLabelATKU == null) {
            try {
                ivjJLabelATKU = new JLabel();
                ivjJLabelATKU.setName("JLabelATKU");
                ivjJLabelATKU.setToolTipText("Automatic threshold kickup offest to be used");
                ivjJLabelATKU.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJLabelATKU.setText("ATKU:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelATKU;
    }

    private JLabel getJLabelMinRestOffset() {
        if (ivjJLabelMinRestOffset == null) {
            try {
                ivjJLabelMinRestOffset = new JLabel();
                ivjJLabelMinRestOffset.setName("JLabelMinRestOffset");
                ivjJLabelMinRestOffset.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJLabelMinRestOffset.setText("Min Restore Offset:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelMinRestOffset;
    }

    private JLabel getJLabelNormalStateAndThreshold() {
        if (ivjJLabelNormalStateAndThreshold == null) {
            try {
                ivjJLabelNormalStateAndThreshold = new JLabel();
                ivjJLabelNormalStateAndThreshold.setName("JLabelNormalStateAndThreshold");
                ivjJLabelNormalStateAndThreshold.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJLabelNormalStateAndThreshold.setText("Normal State:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelNormalStateAndThreshold;
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

    private JPanelDevicePoint getJPanelDevicePointPeak() {
        if (ivjJPanelDevicePointPeak == null) {
            try {
                ivjJPanelDevicePointPeak = new JPanelDevicePoint();
                ivjJPanelDevicePointPeak.setName("JPanelDevicePointPeak");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJPanelDevicePointPeak;
    }

    private JPanel getJPanelPeakTracking() {
        if (ivjJPanelPeakTracking == null) {
            try {
                com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
                ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
                ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
                ivjLocalBorder.setTitle("Peak Tracking");
                ivjJPanelPeakTracking = new JPanel();
                ivjJPanelPeakTracking.setName("JPanelPeakTracking");
                ivjJPanelPeakTracking.setBorder(ivjLocalBorder);
                ivjJPanelPeakTracking.setLayout(new java.awt.GridBagLayout());
                ivjJPanelPeakTracking.setFont(new java.awt.Font("Arial", 1, 12));

                java.awt.GridBagConstraints constraintsJCheckBoxPeakTracking = new java.awt.GridBagConstraints();
                constraintsJCheckBoxPeakTracking.gridx = 1;
                constraintsJCheckBoxPeakTracking.gridy = 1;
                constraintsJCheckBoxPeakTracking.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJCheckBoxPeakTracking.ipadx = 132;
                constraintsJCheckBoxPeakTracking.ipady = -5;
                constraintsJCheckBoxPeakTracking.insets = new java.awt.Insets(2, 5, 1, 57);
                getJPanelPeakTracking().add(getJCheckBoxPeakTracking(), constraintsJCheckBoxPeakTracking);

                java.awt.GridBagConstraints constraintsJPanelDevicePointPeak = new java.awt.GridBagConstraints();
                constraintsJPanelDevicePointPeak.gridx = 1;
                constraintsJPanelDevicePointPeak.gridy = 2;
                constraintsJPanelDevicePointPeak.fill = java.awt.GridBagConstraints.BOTH;
                constraintsJPanelDevicePointPeak.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJPanelDevicePointPeak.weightx = 1.0;
                constraintsJPanelDevicePointPeak.weighty = 1.0;
                constraintsJPanelDevicePointPeak.ipadx = 33;
                constraintsJPanelDevicePointPeak.ipady = 6;
                constraintsJPanelDevicePointPeak.insets = new java.awt.Insets(1, 8, 5, 5);
                getJPanelPeakTracking().add(getJPanelDevicePointPeak(), constraintsJPanelDevicePointPeak);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJPanelPeakTracking;
    }

    private java.awt.FlowLayout getJPanelProjectionFlowLayout() {
        java.awt.FlowLayout ivjJPanelProjectionFlowLayout = null;
        try {
            /* Create part */
            ivjJPanelProjectionFlowLayout = new java.awt.FlowLayout();
            ivjJPanelProjectionFlowLayout.setVgap(2);
            ivjJPanelProjectionFlowLayout.setHgap(0);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        ;
        return ivjJPanelProjectionFlowLayout;
    }

    private JPanelDevicePoint getJPanelTriggerID() {
        if (ivjJPanelTriggerID == null) {
            try {
                com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
                ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
                ivjLocalBorder1.setTitleFont(new java.awt.Font("Arial", 1, 14));
                ivjLocalBorder1.setTitle("Trigger Identification");
                ivjJPanelTriggerID = new JPanelDevicePoint();
                ivjJPanelTriggerID.setName("JPanelTriggerID");
                ivjJPanelTriggerID.setBorder(ivjLocalBorder1);
                ivjJPanelTriggerID.setFont(new java.awt.Font("Arial", 1, 12));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJPanelTriggerID;
    }

    private LMTriggerProjectionPanel getJPanelTriggerProjPanel() {
        if (triggerProjPanel == null) {
            triggerProjPanel = new LMTriggerProjectionPanel();
            triggerProjPanel.setName("LMTriggerProjectionPanel");
        }

        return triggerProjPanel;
    }

    private JTextField getJTextFieldATKU() {
        if (ivjJTextFieldATKU == null) {
            try {
                ivjJTextFieldATKU = new JTextField();
                ivjJTextFieldATKU.setName("JTextFieldATKU");
                ivjJTextFieldATKU.setToolTipText("Automatic threshold kickup offest to be used");

                ivjJTextFieldATKU.setDocument(new LongRangeDocument());
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJTextFieldATKU;
    }

    private JTextField getJTextFieldMinRestOffset() {
        if (ivjJTextFieldMinRestOffset == null) {
            try {
                ivjJTextFieldMinRestOffset = new JTextField();
                ivjJTextFieldMinRestOffset.setName("JTextFieldMinRestOffset");

                ivjJTextFieldMinRestOffset.setDocument(new DoubleRangeDocument(-99999.9999, 99999.9999, 4));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJTextFieldMinRestOffset;
    }

    private JTextField getJTextFieldThreshold() {
        if (ivjJTextFieldThreshold == null) {
            try {
                ivjJTextFieldThreshold = new JTextField();
                ivjJTextFieldThreshold.setName("JTextFieldThreshold");

                ivjJTextFieldThreshold.setDocument(new DoubleRangeDocument(-999999.99999999, 999999.99999999, 8));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJTextFieldThreshold;
    }

    public Integer getSelectedNormalState() {
        if (getJComboBoxNormalState().getSelectedItem() != null) {
            return new Integer(((LiteState) getJComboBoxNormalState().getSelectedItem()).getStateRawState());
        }
        return null;
    }

    public Integer getSelectedPointID() {
        if (getJPanelTriggerID().getSelectedPoint() != null) {
            return new Integer(getJPanelTriggerID().getSelectedPoint().getPointID());
        }
        return null;
    }

    public Double getSelectedThreshold() {
        try {
            return new Double(getJTextFieldThreshold().getText());
        } catch (NumberFormatException e) {
            return new Double(0.0);
        }
    }

    public String getSelectedType() {
        return getJComboBoxType().getSelectedItem().toString();
    }

    @Override
    public Object getValue(Object o) {
        LMControlAreaTrigger trigger = null;
        if (o == null) {
            trigger = new LMControlAreaTrigger();
        } else {
            trigger = (LMControlAreaTrigger) o;
        }

        trigger.setPointID(getSelectedPointID());
        trigger.setTriggerType(getSelectedType());

        if (trigger.getTriggerType().equalsIgnoreCase(IlmDefines.TYPE_STATUS)) {
            trigger.setNormalState(getSelectedNormalState());
            trigger.setThreshold(new Double(0.0));
        } else {
            trigger.setNormalState(new Integer(IlmDefines.INVALID_INT_VALUE));
            if (trigger.getTriggerType().equalsIgnoreCase(IlmDefines.TYPE_THRESHOLD_POINT)) {
                trigger.setThreshold(getSelectedThreshold());
                trigger.setThresholdPointID(new Integer(getJPanelThresholdID().getSelectedPoint().getPointID()));
            } else {
                trigger.setThreshold(getSelectedThreshold());
            }

            try {
                trigger.setMinRestoreOffset(new Double(getJTextFieldMinRestOffset().getText()));
            } catch (NumberFormatException e) {
                trigger.setMinRestoreOffset(new Double(0.0));
            }

            try {
                trigger.setThresholdKickPercent(new Integer(getJTextFieldATKU().getText()));
            } catch (NumberFormatException e) {
                trigger.setThresholdKickPercent(new Integer(0));
            }

        }

        if (getJCheckBoxPeakTracking().isSelected() && getJPanelDevicePointPeak().getSelectedPoint() != null) {
            trigger.setPeakPointID(new Integer(getJPanelDevicePointPeak().getSelectedPoint().getPointID()));
        } else
            trigger.setPeakPointID(new Integer(0));

        // get the projection panels values
        getJPanelTriggerProjPanel().getValue(trigger);

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
        getJPanelTriggerID().addComboBoxPropertyChangeListener(this);
        getJPanelDevicePointPeak().addComboBoxPropertyChangeListener(this);
        getJPanelThresholdID().addComboBoxPropertyChangeListener(this);

        getJComboBoxType().addActionListener(this);
        getJTextFieldThreshold().addCaretListener(this);
        getJTextFieldMinRestOffset().addCaretListener(this);
        getJComboBoxNormalState().addActionListener(this);
        getJCheckBoxPeakTracking().addActionListener(this);
        getJButtonProjection().addActionListener(this);
        getJTextFieldATKU().addCaretListener(this);
    }

    /**
     * Initialize the class.
     */
    private void initialize() {
        try {
            setName("LMControlAreaTriggerModifyPanel");
            setToolTipText("");
            setPreferredSize(new java.awt.Dimension(390, 404));
            setLayout(new java.awt.GridBagLayout());
            setSize(390, 550);
            setMinimumSize(new java.awt.Dimension(10, 10));

            java.awt.GridBagConstraints constraintsJLabelType = new java.awt.GridBagConstraints();
            constraintsJLabelType.gridx = 1;
            constraintsJLabelType.gridy = 1;
            constraintsJLabelType.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJLabelType.ipadx = 9;
            constraintsJLabelType.ipady = -2;
            constraintsJLabelType.insets = new java.awt.Insets(7, 8, 8, 4);
            add(getJLabelType(), constraintsJLabelType);

            java.awt.GridBagConstraints constraintsJComboBoxType = new java.awt.GridBagConstraints();
            constraintsJComboBoxType.gridx = 2;
            constraintsJComboBoxType.gridy = 1;
            constraintsJComboBoxType.gridwidth = 3;
            constraintsJComboBoxType.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsJComboBoxType.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJComboBoxType.weightx = 1.0;
            constraintsJComboBoxType.ipadx = -11;
            constraintsJComboBoxType.insets = new java.awt.Insets(6, 5, 3, 33);
            add(getJComboBoxType(), constraintsJComboBoxType);

            java.awt.GridBagConstraints constraintsJLabelNormalStateAndThreshold = new java.awt.GridBagConstraints();
            constraintsJLabelNormalStateAndThreshold.gridx = 1;
            constraintsJLabelNormalStateAndThreshold.gridy = 2;
            constraintsJLabelNormalStateAndThreshold.gridwidth = 2;
            constraintsJLabelNormalStateAndThreshold.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJLabelNormalStateAndThreshold.ipadx = 7;
            constraintsJLabelNormalStateAndThreshold.ipady = -2;
            constraintsJLabelNormalStateAndThreshold.insets = new java.awt.Insets(5, 8, 5, 5);
            add(getJLabelNormalStateAndThreshold(), constraintsJLabelNormalStateAndThreshold);

            java.awt.GridBagConstraints constraintsJComboBoxNormalState = new java.awt.GridBagConstraints();
            constraintsJComboBoxNormalState.gridx = 3;
            constraintsJComboBoxNormalState.gridy = 2;
            constraintsJComboBoxNormalState.gridwidth = 4;
            constraintsJComboBoxNormalState.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsJComboBoxNormalState.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJComboBoxNormalState.weightx = 1.0;
            constraintsJComboBoxNormalState.ipadx = 104;
            constraintsJComboBoxNormalState.insets = new java.awt.Insets(2, 5, 2, 7);
            add(getJComboBoxNormalState(), constraintsJComboBoxNormalState);

            java.awt.GridBagConstraints constraintsJTextFieldThreshold = new java.awt.GridBagConstraints();
            constraintsJTextFieldThreshold.gridx = 3;
            constraintsJTextFieldThreshold.gridy = 2;
            constraintsJTextFieldThreshold.gridwidth = 4;
            constraintsJTextFieldThreshold.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsJTextFieldThreshold.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJTextFieldThreshold.weightx = 1.0;
            constraintsJTextFieldThreshold.ipadx = 226;
            constraintsJTextFieldThreshold.ipady = 3;
            constraintsJTextFieldThreshold.insets = new java.awt.Insets(2, 5, 2, 7);
            add(getJTextFieldThreshold(), constraintsJTextFieldThreshold);

            java.awt.GridBagConstraints constraintsJLabelMinRestOffset = new java.awt.GridBagConstraints();
            constraintsJLabelMinRestOffset.gridx = 1;
            constraintsJLabelMinRestOffset.gridy = 3;
            constraintsJLabelMinRestOffset.gridwidth = 3;
            constraintsJLabelMinRestOffset.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJLabelMinRestOffset.ipady = -2;
            constraintsJLabelMinRestOffset.insets = new java.awt.Insets(5, 8, 5, 0);
            add(getJLabelMinRestOffset(), constraintsJLabelMinRestOffset);

            java.awt.GridBagConstraints constraintsJTextFieldMinRestOffset = new java.awt.GridBagConstraints();
            constraintsJTextFieldMinRestOffset.gridx = 4;
            constraintsJTextFieldMinRestOffset.gridy = 3;
            constraintsJTextFieldMinRestOffset.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsJTextFieldMinRestOffset.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJTextFieldMinRestOffset.weightx = 1.0;
            constraintsJTextFieldMinRestOffset.ipadx = 64;
            constraintsJTextFieldMinRestOffset.insets = new java.awt.Insets(3, 1, 4, 11);
            add(getJTextFieldMinRestOffset(), constraintsJTextFieldMinRestOffset);

            java.awt.GridBagConstraints constraintsJPanelPeakTracking = new java.awt.GridBagConstraints();
            constraintsJPanelPeakTracking.gridx = 1;
            constraintsJPanelPeakTracking.gridy = 5;
            constraintsJPanelPeakTracking.gridwidth = 6;
            constraintsJPanelPeakTracking.fill = java.awt.GridBagConstraints.BOTH;
            constraintsJPanelPeakTracking.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJPanelPeakTracking.weightx = 1.0;
            constraintsJPanelPeakTracking.weighty = 1.0;
            constraintsJPanelPeakTracking.ipadx = -5;
            constraintsJPanelPeakTracking.insets = new java.awt.Insets(2, 8, 6, 8);
            add(getJPanelPeakTracking(), constraintsJPanelPeakTracking);

            java.awt.GridBagConstraints constraintsJPanelTriggerID = new java.awt.GridBagConstraints();
            constraintsJPanelTriggerID.gridx = 1;
            constraintsJPanelTriggerID.gridy = 4;
            constraintsJPanelTriggerID.gridwidth = 6;
            constraintsJPanelTriggerID.fill = java.awt.GridBagConstraints.BOTH;
            constraintsJPanelTriggerID.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJPanelTriggerID.weightx = 1.0;
            constraintsJPanelTriggerID.weighty = 1.0;
            constraintsJPanelTriggerID.ipadx = 90;
            constraintsJPanelTriggerID.insets = new java.awt.Insets(5, 8, 1, 8);
            add(getJPanelTriggerID(), constraintsJPanelTriggerID);

            java.awt.GridBagConstraints constraintsJPanelThresholdID = new java.awt.GridBagConstraints();
            constraintsJPanelThresholdID.gridx = 1;
            constraintsJPanelThresholdID.gridy = 6;
            constraintsJPanelThresholdID.gridwidth = 6;
            constraintsJPanelThresholdID.fill = java.awt.GridBagConstraints.BOTH;
            constraintsJPanelThresholdID.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJPanelThresholdID.weightx = 1.0;
            constraintsJPanelThresholdID.weighty = 1.0;
            constraintsJPanelThresholdID.ipadx = 90;
            constraintsJPanelThresholdID.insets = new java.awt.Insets(2, 8, 6, 8);
            add(getJPanelThresholdID(), constraintsJPanelThresholdID);

            java.awt.GridBagConstraints constraintsJButtonProjection = new java.awt.GridBagConstraints();
            constraintsJButtonProjection.gridx = 5;
            constraintsJButtonProjection.gridy = 1;
            constraintsJButtonProjection.gridwidth = 2;
            constraintsJButtonProjection.anchor = java.awt.GridBagConstraints.EAST;
            constraintsJButtonProjection.insets = new java.awt.Insets(5, 30, 2, 6);
            add(getJButtonProjection(), constraintsJButtonProjection);

            java.awt.GridBagConstraints constraintsJTextFieldATKU = new java.awt.GridBagConstraints();
            constraintsJTextFieldATKU.gridx = 6;
            constraintsJTextFieldATKU.gridy = 3;
            constraintsJTextFieldATKU.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsJTextFieldATKU.anchor = java.awt.GridBagConstraints.EAST;
            constraintsJTextFieldATKU.weightx = 1.0;
            constraintsJTextFieldATKU.ipadx = 65;
            constraintsJTextFieldATKU.insets = new java.awt.Insets(3, 2, 4, 10);
            add(getJTextFieldATKU(), constraintsJTextFieldATKU);

            java.awt.GridBagConstraints constraintsJLabelATKU = new java.awt.GridBagConstraints();
            constraintsJLabelATKU.gridx = 5;
            constraintsJLabelATKU.gridy = 3;
            constraintsJLabelATKU.anchor = java.awt.GridBagConstraints.EAST;
            constraintsJLabelATKU.ipadx = 3;
            constraintsJLabelATKU.ipady = -2;
            constraintsJLabelATKU.insets = new java.awt.Insets(5, 12, 5, 1);
            add(getJLabelATKU(), constraintsJLabelATKU);
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        // act like the ComboBox was changed
        jComboBoxType_ActionPerformed(null);
    }

    @Override
    public boolean isInputValid() {
        if (getJPanelTriggerID().getSelectedDevice() == null || 
                getJPanelTriggerID().getSelectedPoint() == null || 
                getJComboBoxType().getSelectedItem() == null) {
            setErrorString("A trigger type, device and point must be specified.");
            return false;
        }

        if (getJComboBoxType().getSelectedItem().toString().equalsIgnoreCase(IlmDefines.TYPE_THRESHOLD)) {
            try {
                if (getJTextFieldThreshold().getText() == null || 
                        getJTextFieldThreshold().getText().length() <= 0 || 
                        Double.parseDouble(getJTextFieldThreshold().getText()) > Double.MAX_VALUE) {
                    setErrorString("The threshold for this trigger must be a valid number.");
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }

        return true;
    }

    public void jButtonProjection_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
        OkCancelDialog d = new OkCancelDialog(SwingUtil.getParentFrame(this),
                                              "Projection Properties",
                                              true,
                                              getJPanelTriggerProjPanel());

        d.setLocationRelativeTo(this);
        d.setCancelButtonVisible(false);
        d.setSize(new Dimension(280, 200));
        d.show();

        d.dispose();

        // set the text of the button to the type of projection used
        getJButtonProjection().setText("Projection..." + getJPanelTriggerProjPanel().getSelectedType());

        fireInputUpdate();
        return;
    }

    public void jCheckBoxPeakTracking_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
        setTrackingEnabled(getJCheckBoxPeakTracking().isSelected());
        fireInputUpdate();
    }

    public void jComboBoxType_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
        boolean isThresh = IlmDefines.TYPE_THRESHOLD.equalsIgnoreCase(getJComboBoxType().getSelectedItem().toString());
        boolean isThreshPoint = IlmDefines.TYPE_THRESHOLD_POINT.equalsIgnoreCase(getJComboBoxType().getSelectedItem().toString());

        getJComboBoxNormalState().setVisible(!(isThresh || isThreshPoint));
        getJTextFieldThreshold().setVisible(isThresh || isThreshPoint);
        getJTextFieldThreshold().setEnabled(isThresh);
        getJLabelMinRestOffset().setEnabled(isThresh || isThreshPoint);
        getJTextFieldMinRestOffset().setEnabled(isThresh || isThreshPoint);
        getJTextFieldATKU().setEnabled(isThresh);
        getJLabelATKU().setEnabled(isThresh);
        getJCheckBoxPeakTracking().setEnabled(isThresh || isThreshPoint);
        getJPanelThresholdID().setEnabled(isThreshPoint);
        getJButtonProjection().setEnabled(isThresh);
        setThresholdPointSettingsEnabled(isThreshPoint);
        getJLabelNormalStateAndThreshold().setEnabled(isThresh);

        if (isThresh || isThreshPoint) {
            getJLabelNormalStateAndThreshold().setText(IlmDefines.TYPE_THRESHOLD + ":");

            // initDeviceComboBox( LMControlAreaTrigger.TYPE_THRESHOLD );
            List<PointType> pointTypes = Lists.newArrayList(PointType.Analog,
                                                            PointType.DemandAccumulator,
                                                            PointType.PulseAccumulator,
                                                            PointType.CalcAnalog);

            getJPanelTriggerID().setPointTypeFilter(pointTypes);
            if (isThreshPoint) {
                getJPanelThresholdID().setPointTypeFilter(pointTypes);
            }
        } else {
            getJLabelNormalStateAndThreshold().setText("Normal State:");
            getJCheckBoxPeakTracking().setSelected(false);
            setTrackingEnabled(false);

            // initDeviceComboBox( LMControlAreaTrigger.TYPE_STATUS );
            List<PointType> pointTypes = Lists.newArrayList(PointType.Status);

            getJButtonProjection().setEnabled(false);
            getJPanelTriggerID().setPointTypeFilter(pointTypes);
        }

        updateStates();
        fireInputUpdate();
        return;
    }

    @Override
    public void propertyChange(java.beans.PropertyChangeEvent evt) {
        if (evt.getPropertyName().equalsIgnoreCase(getJPanelTriggerID().PROPERTY_PAO_UPDATE) || 
                evt.getPropertyName().equalsIgnoreCase(getJPanelTriggerID().PROPERTY_POINT_UPDATE)) {
            if (evt.getSource() == getJPanelTriggerID()) {
                updateStates();
                fireInputUpdate();
            }

            if (evt.getSource() == getJPanelDevicePointPeak()) {
                fireInputUpdate();
            }
        }

        if (evt.getSource() == getJPanelThresholdID()) {
            fireInputUpdate();
        }
    }

    private void setTrackingEnabled(boolean value) {
        for (int i = 0; i < getJPanelDevicePointPeak().getComponentCount(); i++) {
            getJPanelDevicePointPeak().getComponent(i).setEnabled(value);
        }
    }

    private void setThresholdPointSettingsEnabled(boolean value) {
        for (int i = 0; i < getJPanelThresholdID().getComponentCount(); i++) {
            getJPanelThresholdID().getComponent(i).setEnabled(value);
        }
    }

    @Override
    public void setValue(Object o) {
        LMControlAreaTrigger trigger = null;

        if (o == null) {
            return;
        } else {
            trigger = (LMControlAreaTrigger) o;
        }

        LiteYukonPAObject litePAO = null;
        LitePoint litePoint = null;
        LiteState liteState = null;

        // look for the litePoint here
        litePoint = YukonSpringHook.getBean(PointDao.class).getLitePoint(trigger.getPointID().intValue());

        if (litePoint == null) {
            throw new RuntimeException("Unable to find the point (ID= " + trigger.getPointID() + ") associated with the LMTrigger of type '" + trigger.getTriggerType() + "'");
        }

        // look for the litePAO here
        litePAO = YukonSpringHook.getBean(PaoDao.class).getLiteYukonPAO(litePoint.getPaobjectID());

        // set the states for the row
        liteState = YukonSpringHook.getBean(StateGroupDao.class).findLiteState(litePoint.getStateGroupID(),trigger.getNormalState().intValue());

        if (trigger.getTriggerType().equalsIgnoreCase(IlmDefines.TYPE_STATUS)) {
            if (liteState == null) {
                throw new RuntimeException("Unable to find the rawState value of " + trigger.getNormalState() + 
                                           ", associated with the LMTrigger for the point id = '" + trigger.getPointID() + "'");
            }

            getJComboBoxType().setSelectedItem(trigger.getTriggerType());
            getJPanelTriggerID().setSelectedLitePAO(litePAO.getYukonID());
            getJPanelTriggerID().setSelectedLitePoint(litePoint.getPointID());
            getJComboBoxNormalState().setSelectedItem(liteState);

            getJCheckBoxPeakTracking().setEnabled(false);
        } else {
            getJComboBoxType().setSelectedItem(trigger.getTriggerType());
            if (trigger.getTriggerType().equalsIgnoreCase(IlmDefines.TYPE_THRESHOLD_POINT)) {

                LitePoint lp = YukonSpringHook.getBean(PointDao.class).getLitePoint(trigger.getThresholdPointID().intValue());
                getJPanelThresholdID().setSelectedLitePAO(lp.getPaobjectID());
                getJPanelThresholdID().setSelectedLitePoint(lp.getPointID());
            } else {
                getJTextFieldThreshold().setText(trigger.getThreshold().toString());
            }
            getJPanelTriggerID().setSelectedLitePAO(litePAO.getYukonID());
            getJPanelTriggerID().setSelectedLitePoint(litePoint.getPointID());

            getJCheckBoxPeakTracking().setEnabled(true);
        }

        getJCheckBoxPeakTracking().setSelected(trigger.getPeakPointID().intValue() > 0);
        setTrackingEnabled(trigger.getPeakPointID().intValue() > 0);

        if (trigger.getPeakPointID().intValue() > 0) {
            LitePoint lp = YukonSpringHook.getBean(PointDao.class).getLitePoint(trigger.getPeakPointID().intValue());

            getJPanelDevicePointPeak().setSelectedLitePAO(lp.getPaobjectID());
            getJPanelDevicePointPeak().setSelectedLitePoint(lp.getPointID());
        }

        // always do the following settings
        getJTextFieldMinRestOffset().setText(trigger.getMinRestoreOffset().toString());

        getJTextFieldATKU().setText(trigger.getThresholdKickPercent().toString());

        // set the projection panels values
        getJButtonProjection().setEnabled(trigger.getTriggerType().equalsIgnoreCase(IlmDefines.TYPE_THRESHOLD));

        getJPanelTriggerProjPanel().setValue(trigger);

        // set the text of the button to the type of projection used
        getJButtonProjection().setText("Projection..." + trigger.getProjectionType());
    }

    private void updateStates() {
        getJComboBoxNormalState().removeAllItems();

        if (getJComboBoxNormalState().isVisible() && getJPanelTriggerID().getSelectedPoint() != null) {
            // set the states for the JCombobox
            int stateGroupID = getJPanelTriggerID().getSelectedPoint().getStateGroupID();

            LiteStateGroup stateGroup = YukonSpringHook.getBean(StateGroupDao.class).getStateGroup(stateGroupID);

            Iterator<LiteState> stateIterator = stateGroup.getStatesList().iterator();
            while (stateIterator.hasNext()) {
                getJComboBoxNormalState().addItem(stateIterator.next());
            }
        }

        return;
    }

    private JPanelDevicePoint getJPanelThresholdID() {
        if (ivjJPanelThresholdID == null) {
            try {
                com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
                ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
                ivjLocalBorder1.setTitleFont(new java.awt.Font("Arial", 1, 14));
                ivjLocalBorder1.setTitle("Threshold Point Settings");
                ivjJPanelThresholdID = new JPanelDevicePoint();
                ivjJPanelThresholdID.setName("JPanelThresholdID");
                ivjJPanelThresholdID.setBorder(ivjLocalBorder1);
                ivjJPanelThresholdID.setFont(new java.awt.Font("Arial", 1, 12));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJPanelThresholdID;
    }
}