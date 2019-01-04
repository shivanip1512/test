package com.cannontech.dbeditor.editor.device.lmgroup;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.CaretListener;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.gui.unchanging.DoubleRangeDocument;
import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.device.lm.IGroupRoute;
import com.cannontech.database.data.device.lm.LMGroup;
import com.cannontech.database.data.device.lm.LMGroupExpressCom;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.yukon.IDatabaseCache;

public class LMGroupBasePanel extends com.cannontech.common.gui.util.DataInputPanel implements
        ActionListener, CaretListener {
    private javax.swing.JPanel ivjIdentificationPanel;
    private javax.swing.JComboBox<LiteYukonPAObject> ivjRouteComboBox;
    private javax.swing.JLabel ivjRouteLabel;
    private javax.swing.JPanel ivjCommunicationPanel;
    private javax.swing.JLabel ivjJLabelGroupName;
    private javax.swing.JLabel ivjJLabelGroupType;
    private javax.swing.JLabel ivjJLabelKWCapacity;
    private javax.swing.JTextField ivjJTextFieldKWCapacity;
    private javax.swing.JTextField ivjJTextFieldName;
    private javax.swing.JLabel ivjJTextFieldType;
    private javax.swing.JCheckBox ivjJCheckBoxDisable;
    private javax.swing.JCheckBox ivjJCheckBoxDisableControl;
    private javax.swing.JCheckBox ivjJCheckBoxHistory;
    private javax.swing.JCheckBox ivjJCheckBoxAnnual;
    private javax.swing.JCheckBox ivjJCheckBoxDaily;
    private javax.swing.JCheckBox ivjJCheckBoxMonthly;
    private javax.swing.JCheckBox ivjJCheckBoxSeasonal;
    private javax.swing.JPanel ivjJPanelHistory;
    private javax.swing.JPanel ivjJPanelAllHistory;
    private JLabel priorityLabel;
    private JComboBox<?> priorityCombo;
    private LMGroup lmGroup;

    public LMGroupBasePanel() {
        initialize();
    }

    public LMGroupBasePanel(boolean showHistory) {
        this();

        // show or not show the history items
        getJCheckBoxHistory().setVisible(showHistory);
        getJPanelHistory().setVisible(showHistory);
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {

        if (e.getSource() == getRouteComboBox()) {
            connEtoC4(e);
        }
        if (e.getSource() == getJCheckBoxDisableControl()) {
            connEtoC5(e);
        }
        if (e.getSource() == getJCheckBoxDisable()) {
            connEtoC6(e);
        }
        if (e.getSource() == getJCheckBoxHistory()) {
            connEtoC3(e);
        }
        if (e.getSource() == getJCheckBoxDaily()) {
            connEtoC7(e);
        }
        if (e.getSource() == getJCheckBoxSeasonal()) {
            connEtoC8(e);
        }
        if (e.getSource() == getJCheckBoxAnnual()) {
            connEtoC9(e);
        }
        if (e.getSource() == getJCheckBoxMonthly()) {
            connEtoC10(e);
        }
        if (e.getSource() == getPriorityCombo()) {
            this.fireInputUpdate();
        }

    }

    /**
     * Method to handle events for the CaretListener interface.
     */
    @Override
    public void caretUpdate(javax.swing.event.CaretEvent e) {

        if (e.getSource() == getJTextFieldName()) {
            connEtoC1(e);
        }
        if (e.getSource() == getJTextFieldKWCapacity()) {
            connEtoC2(e);
        }

    }

    /**
     * connEtoC1: (NameTextField.caret.caretUpdate(javax.swing.event.CaretEvent) -->
     * DeviceBaseEditorPanel.fireInputUpdate()V)
     */
    private void connEtoC1(javax.swing.event.CaretEvent arg1) {
        try {
            this.fireInputUpdate();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC10: (JCheckBoxMonthly.action.actionPerformed(java.awt.event.ActionEvent) -->
     * LMGroupBasePanel.fireInputUpdate()V)
     */
    private void connEtoC10(java.awt.event.ActionEvent arg1) {
        try {
            this.fireInputUpdate();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC2: (JTextFieldKWCapacity.caret.caretUpdate(javax.swing.event.CaretEvent) -->
     * LMGroupBaseEditorPanel.fireInputUpdate()V)
     */
    private void connEtoC2(javax.swing.event.CaretEvent arg1) {
        try {
            this.fireInputUpdate();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC3: (JCheckBoxHistory.action.actionPerformed(java.awt.event.ActionEvent) -->
     * LMGroupBasePanel.jCheckBoxHistory_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC3(java.awt.event.ActionEvent arg1) {
        try {
            this.jCheckBoxHistory_ActionPerformed(arg1);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC4: (RouteComboBox.action.actionPerformed(java.awt.event.ActionEvent) -->
     * DeviceBaseEditorPanel.fireInputUpdate()V)
     */
    private void connEtoC4(java.awt.event.ActionEvent arg1) {
        try {
            this.fireInputUpdate();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC5: (JCheckBoxDisableControl.action.actionPerformed(java.awt.event.ActionEvent) -->
     * LMGroupBaseEditorPanel.fireInputUpdate()V)
     */
    private void connEtoC5(java.awt.event.ActionEvent arg1) {
        try {
            this.fireInputUpdate();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC6: (JCheckBoxDisable.action.actionPerformed(java.awt.event.ActionEvent) -->
     * LMGroupBaseEditorPanel.fireInputUpdate()V)
     */
    private void connEtoC6(java.awt.event.ActionEvent arg1) {
        try {
            this.fireInputUpdate();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC7: (JCheckBoxDaily.action.actionPerformed(java.awt.event.ActionEvent) -->
     * LMGroupBasePanel.fireInputUpdate()V)
     */
    private void connEtoC7(java.awt.event.ActionEvent arg1) {
        try {
            this.fireInputUpdate();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC8: (JCheckBoxSeasonal.action.actionPerformed(java.awt.event.ActionEvent) -->
     * LMGroupBasePanel.fireInputUpdate()V)
     */
    private void connEtoC8(java.awt.event.ActionEvent arg1) {
        try {
            this.fireInputUpdate();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC9: (JCheckBoxAnnual.action.actionPerformed(java.awt.event.ActionEvent) -->
     * LMGroupBasePanel.fireInputUpdate()V)
     */
    private void connEtoC9(java.awt.event.ActionEvent arg1) {
        try {
            this.fireInputUpdate();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    /**
     * Return the ConfigurationPanel property value.
     */
    private javax.swing.JPanel getCommunicationPanel() {
        if (ivjCommunicationPanel == null) {
            try {
                ivjCommunicationPanel = new javax.swing.JPanel();
                ivjCommunicationPanel.setName("CommunicationPanel");
                // ivjCommunicationPanel.setBorder(new com.ibm.uvm.abt.edit.DeletedClassView());
                ivjCommunicationPanel.setLayout(new java.awt.GridBagLayout());

                java.awt.GridBagConstraints constraintsRouteComboBox =
                    new java.awt.GridBagConstraints();
                constraintsRouteComboBox.gridx = 2;
                constraintsRouteComboBox.gridy = 1;
                constraintsRouteComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsRouteComboBox.anchor = java.awt.GridBagConstraints.WEST;
                constraintsRouteComboBox.weightx = 1.0;
                constraintsRouteComboBox.ipadx = -5;
                constraintsRouteComboBox.ipady = -5;
                constraintsRouteComboBox.insets = new java.awt.Insets(5, 6, 22, 51);
                getCommunicationPanel().add(getRouteComboBox(), constraintsRouteComboBox);

                java.awt.GridBagConstraints constraintsRouteLabel =
                    new java.awt.GridBagConstraints();
                constraintsRouteLabel.gridx = 1;
                constraintsRouteLabel.gridy = 1;
                constraintsRouteLabel.anchor = java.awt.GridBagConstraints.WEST;
                constraintsRouteLabel.insets = new java.awt.Insets(5, 11, 23, 6);
                getCommunicationPanel().add(getRouteLabel(), constraintsRouteLabel);
                // user code begin {1}

            } catch (java.lang.Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjCommunicationPanel;
    }

    /**
     * Return the IdentificationPanel property value.
     * @return javax.swing.JPanel
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JPanel getIdentificationPanel() {
        if (ivjIdentificationPanel == null) {
            try {
                ivjIdentificationPanel = new JPanel();
                ivjIdentificationPanel.setName("IdentificationPanel");
                ivjIdentificationPanel.setLayout(new GridBagLayout());

                java.awt.GridBagConstraints constraintsJTextFieldType =
                    new java.awt.GridBagConstraints();
                constraintsJTextFieldType.gridx = 1;
                constraintsJTextFieldType.gridy = 0;
                constraintsJTextFieldType.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJTextFieldType.ipadx = 138;
                constraintsJTextFieldType.ipady = 4;
                constraintsJTextFieldType.insets = new java.awt.Insets(5, 1, 3, 62);
                getIdentificationPanel().add(getJTextFieldType(), constraintsJTextFieldType);

                java.awt.GridBagConstraints constraintsJLabelGroupType =
                    new java.awt.GridBagConstraints();
                constraintsJLabelGroupType.gridx = 0;
                constraintsJLabelGroupType.gridy = 0;
                constraintsJLabelGroupType.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJLabelGroupType.ipadx = 19;
                constraintsJLabelGroupType.insets = new java.awt.Insets(5, 15, 3, 20);
                getIdentificationPanel().add(getJLabelGroupType(), constraintsJLabelGroupType);

                java.awt.GridBagConstraints constraintsJLabelGroupName =
                    new java.awt.GridBagConstraints();
                constraintsJLabelGroupName.gridx = 0;
                constraintsJLabelGroupName.gridy = 1;
                constraintsJLabelGroupName.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJLabelGroupName.ipadx = 15;
                constraintsJLabelGroupName.ipady = 4;
                constraintsJLabelGroupName.insets = new java.awt.Insets(4, 15, 4, 20);
                getIdentificationPanel().add(getJLabelGroupName(), constraintsJLabelGroupName);

                java.awt.GridBagConstraints constraintsJTextFieldName =
                    new java.awt.GridBagConstraints();
                constraintsJTextFieldName.gridx = 1;
                constraintsJTextFieldName.gridy = 1;
                constraintsJTextFieldName.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsJTextFieldName.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJTextFieldName.weightx = 1.0;
                constraintsJTextFieldName.ipadx = 90;
                constraintsJTextFieldName.insets = new java.awt.Insets(4, 1, 4, 62);
                getIdentificationPanel().add(getJTextFieldName(), constraintsJTextFieldName);

                java.awt.GridBagConstraints constraintsJTextFieldKWCapacity =
                    new java.awt.GridBagConstraints();
                constraintsJTextFieldKWCapacity.gridx = 1;
                constraintsJTextFieldKWCapacity.gridy = 2;
                constraintsJTextFieldKWCapacity.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsJTextFieldKWCapacity.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJTextFieldKWCapacity.weightx = 1.0;
                constraintsJTextFieldKWCapacity.ipadx = 72;
                constraintsJTextFieldKWCapacity.insets = new java.awt.Insets(5, 1, 2, 208);
                getIdentificationPanel().add(getJTextFieldKWCapacity(),
                                             constraintsJTextFieldKWCapacity);

                java.awt.GridBagConstraints constraintsJLabelKWCapacity =
                    new java.awt.GridBagConstraints();
                constraintsJLabelKWCapacity.gridx = 0;
                constraintsJLabelKWCapacity.gridy = 2;
                constraintsJLabelKWCapacity.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJLabelKWCapacity.ipadx = 15;
                constraintsJLabelKWCapacity.ipady = 4;
                constraintsJLabelKWCapacity.insets = new java.awt.Insets(5, 15, 2, 20);
                getIdentificationPanel().add(getJLabelKWCapacity(), constraintsJLabelKWCapacity);

                // Add priority components
                GridBagConstraints priorityComboContraints = new GridBagConstraints();
                priorityComboContraints.gridx = 1;
                priorityComboContraints.gridy = 3;
                priorityComboContraints.fill = GridBagConstraints.HORIZONTAL;
                priorityComboContraints.anchor = GridBagConstraints.WEST;
                priorityComboContraints.weightx = 1.0;
                priorityComboContraints.ipadx = 72;
                priorityComboContraints.insets = new Insets(5, 1, 2, 51);
                ivjIdentificationPanel.add(getPriorityCombo(), priorityComboContraints);

                GridBagConstraints priorityLabelContraints = new GridBagConstraints();
                priorityLabelContraints.gridx = 0;
                priorityLabelContraints.gridy = 3;
                priorityLabelContraints.anchor = GridBagConstraints.WEST;
                priorityLabelContraints.ipadx = 15;
                priorityLabelContraints.ipady = 4;
                priorityLabelContraints.insets = new Insets(5, 15, 2, 20);
                ivjIdentificationPanel.add(getPriorityLabel(), priorityLabelContraints);
                // End priority components

                java.awt.GridBagConstraints constraintsJCheckBoxDisable =
                    new java.awt.GridBagConstraints();
                constraintsJCheckBoxDisable.gridx = 0;
                constraintsJCheckBoxDisable.gridy = 4;
                constraintsJCheckBoxDisable.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJCheckBoxDisable.ipadx = 5;
                constraintsJCheckBoxDisable.insets = new java.awt.Insets(2, 15, 40, 1);
                getIdentificationPanel().add(getJCheckBoxDisable(), constraintsJCheckBoxDisable);

                java.awt.GridBagConstraints constraintsJCheckBoxDisableControl =
                    new java.awt.GridBagConstraints();
                constraintsJCheckBoxDisableControl.gridx = 0;
                constraintsJCheckBoxDisableControl.gridy = 4;
                constraintsJCheckBoxDisableControl.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJCheckBoxDisableControl.ipadx = -1;
                constraintsJCheckBoxDisableControl.insets = new java.awt.Insets(28, 15, 14, 1);
                getIdentificationPanel().add(getJCheckBoxDisableControl(),
                                             constraintsJCheckBoxDisableControl);

            } catch (java.lang.Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjIdentificationPanel;
    }

    private JComboBox<?> getPriorityCombo() {
        if (priorityCombo == null) {
            priorityCombo = new JComboBox<>(new Object[] { "Default", "Medium", "High", "Highest" });
            Dimension priorityDimension = new Dimension(210, 25);
            priorityCombo.setPreferredSize(priorityDimension);
            priorityCombo.setMinimumSize(priorityDimension);
        }

        return priorityCombo;
    }

    private JLabel getPriorityLabel() {
        if (priorityLabel == null) {
            priorityLabel = new JLabel("Control Priority");
            priorityLabel.setFont(new Font("dialog", 0, 14));
        }

        return priorityLabel;
    }

    /**
     * Return the JCheckBoxAnnual property value.
     */
    private javax.swing.JCheckBox getJCheckBoxAnnual() {
        if (ivjJCheckBoxAnnual == null) {
            try {
                ivjJCheckBoxAnnual = new javax.swing.JCheckBox();
                ivjJCheckBoxAnnual.setName("JCheckBoxAnnual");
                ivjJCheckBoxAnnual.setSelected(false);
                ivjJCheckBoxAnnual.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJCheckBoxAnnual.setMnemonic('a');
                ivjJCheckBoxAnnual.setText("Save Annual");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJCheckBoxAnnual;
    }

    /**
     * Return the JCheckBoxDaily property value.
     */
    private javax.swing.JCheckBox getJCheckBoxDaily() {
        if (ivjJCheckBoxDaily == null) {
            try {
                ivjJCheckBoxDaily = new javax.swing.JCheckBox();
                ivjJCheckBoxDaily.setName("JCheckBoxDaily");
                ivjJCheckBoxDaily.setSelected(false);
                ivjJCheckBoxDaily.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJCheckBoxDaily.setMnemonic('d');
                ivjJCheckBoxDaily.setText("Save Daily");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJCheckBoxDaily;
    }

    /**
     * Return the JCheckBoxDisable property value.
     */
    private javax.swing.JCheckBox getJCheckBoxDisable() {
        if (ivjJCheckBoxDisable == null) {
            try {
                ivjJCheckBoxDisable = new javax.swing.JCheckBox();
                ivjJCheckBoxDisable.setName("JCheckBoxDisable");
                ivjJCheckBoxDisable.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJCheckBoxDisable.setMnemonic('d');
                ivjJCheckBoxDisable.setText("Disable Group");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJCheckBoxDisable;
    }

    /**
     * Return the JCheckBoxDisableControl property value.
     */
    private javax.swing.JCheckBox getJCheckBoxDisableControl() {
        if (ivjJCheckBoxDisableControl == null) {
            try {
                ivjJCheckBoxDisableControl = new javax.swing.JCheckBox();
                ivjJCheckBoxDisableControl.setName("JCheckBoxDisableControl");
                ivjJCheckBoxDisableControl.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJCheckBoxDisableControl.setMnemonic('c');
                ivjJCheckBoxDisableControl.setText("Disable Control");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJCheckBoxDisableControl;
    }

    /**
     * Return the JCheckBoxHistory property value.
     */
    private javax.swing.JCheckBox getJCheckBoxHistory() {
        if (ivjJCheckBoxHistory == null) {
            try {
                ivjJCheckBoxHistory = new javax.swing.JCheckBox();
                ivjJCheckBoxHistory.setName("JCheckBoxHistory");
                ivjJCheckBoxHistory.setSelected(true);
                ivjJCheckBoxHistory.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJCheckBoxHistory.setMnemonic('s');
                ivjJCheckBoxHistory.setText("Save History");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJCheckBoxHistory;
    }

    /**
     * Return the JCheckBoxMonthly property value.
     */
    private javax.swing.JCheckBox getJCheckBoxMonthly() {
        if (ivjJCheckBoxMonthly == null) {
            try {
                ivjJCheckBoxMonthly = new javax.swing.JCheckBox();
                ivjJCheckBoxMonthly.setName("JCheckBoxMonthly");
                ivjJCheckBoxMonthly.setSelected(false);
                ivjJCheckBoxMonthly.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJCheckBoxMonthly.setMnemonic('m');
                ivjJCheckBoxMonthly.setText("Save Monthly");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJCheckBoxMonthly;
    }

    /**
     * Return the JCheckBoxSeasonal property value.
     */
    private javax.swing.JCheckBox getJCheckBoxSeasonal() {
        if (ivjJCheckBoxSeasonal == null) {
            try {
                ivjJCheckBoxSeasonal = new javax.swing.JCheckBox();
                ivjJCheckBoxSeasonal.setName("JCheckBoxSeasonal");
                ivjJCheckBoxSeasonal.setSelected(false);
                ivjJCheckBoxSeasonal.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJCheckBoxSeasonal.setMnemonic('e');
                ivjJCheckBoxSeasonal.setText("Save Seasonal");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJCheckBoxSeasonal;
    }

    /**
     * Return the NameLabel property value.
     */
    private javax.swing.JLabel getJLabelGroupName() {
        if (ivjJLabelGroupName == null) {
            try {
                ivjJLabelGroupName = new javax.swing.JLabel();
                ivjJLabelGroupName.setName("JLabelGroupName");
                ivjJLabelGroupName.setText("Group Name:");
                ivjJLabelGroupName.setMaximumSize(new java.awt.Dimension(87, 16));
                ivjJLabelGroupName.setPreferredSize(new java.awt.Dimension(87, 16));
                ivjJLabelGroupName.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJLabelGroupName.setMinimumSize(new java.awt.Dimension(87, 16));
                // user code begin {1}

            } catch (java.lang.Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjJLabelGroupName;
    }

    /**
     * Return the TypeLabel property value.
     */
    private javax.swing.JLabel getJLabelGroupType() {
        if (ivjJLabelGroupType == null) {
            try {
                ivjJLabelGroupType = new javax.swing.JLabel();
                ivjJLabelGroupType.setName("JLabelGroupType");
                ivjJLabelGroupType.setText("Group Type:");
                ivjJLabelGroupType.setMaximumSize(new java.awt.Dimension(83, 20));
                ivjJLabelGroupType.setPreferredSize(new java.awt.Dimension(83, 20));
                ivjJLabelGroupType.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJLabelGroupType.setMinimumSize(new java.awt.Dimension(83, 20));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelGroupType;
    }

    /**
     * Return the JLabelKWCapacity property value.
     */
    private javax.swing.JLabel getJLabelKWCapacity() {
        if (ivjJLabelKWCapacity == null) {
            try {
                ivjJLabelKWCapacity = new javax.swing.JLabel();
                ivjJLabelKWCapacity.setName("JLabelKWCapacity");
                ivjJLabelKWCapacity.setText("kW Capacity:");
                ivjJLabelKWCapacity.setMaximumSize(new java.awt.Dimension(87, 16));
                ivjJLabelKWCapacity.setPreferredSize(new java.awt.Dimension(87, 16));
                ivjJLabelKWCapacity.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJLabelKWCapacity.setMinimumSize(new java.awt.Dimension(87, 16));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelKWCapacity;
    }

    /**
     * Return the JPanelAllHistory property value.
     */
    private javax.swing.JPanel getJPanelAllHistory() {
        if (ivjJPanelAllHistory == null) {
            try {
                ivjJPanelAllHistory = new javax.swing.JPanel();
                ivjJPanelAllHistory.setName("JPanelAllHistory");
                ivjJPanelAllHistory.setLayout(new java.awt.GridBagLayout());

                java.awt.GridBagConstraints constraintsJPanelHistory =
                    new java.awt.GridBagConstraints();
                constraintsJPanelHistory.gridx = 1;
                constraintsJPanelHistory.gridy = 2;
                constraintsJPanelHistory.fill = java.awt.GridBagConstraints.BOTH;
                constraintsJPanelHistory.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJPanelHistory.weightx = 1.0;
                constraintsJPanelHistory.weighty = 1.0;
                constraintsJPanelHistory.ipadx = -4;
                constraintsJPanelHistory.ipady = -4;
                constraintsJPanelHistory.insets = new java.awt.Insets(1, 23, 23, 50);
                getJPanelAllHistory().add(getJPanelHistory(), constraintsJPanelHistory);

                java.awt.GridBagConstraints constraintsJCheckBoxHistory =
                    new java.awt.GridBagConstraints();
                constraintsJCheckBoxHistory.gridx = 1;
                constraintsJCheckBoxHistory.gridy = 1;
                constraintsJCheckBoxHistory.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJCheckBoxHistory.ipadx = 18;
                constraintsJCheckBoxHistory.insets = new java.awt.Insets(3, 23, 1, 50);
                getJPanelAllHistory().add(getJCheckBoxHistory(), constraintsJCheckBoxHistory);
                ivjJPanelAllHistory.setEnabled(false);
                ivjJPanelAllHistory.setVisible(false);

            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJPanelAllHistory;
    }

    /**
     * Return the JPanelHistory property value.
     */
    private JPanel getJPanelHistory() {
        if (ivjJPanelHistory == null) {
            try {
                ivjJPanelHistory = new javax.swing.JPanel();
                ivjJPanelHistory.setName("JPanelHistory");
                ivjJPanelHistory.setBorder(new javax.swing.border.EtchedBorder());
                ivjJPanelHistory.setLayout(new java.awt.GridBagLayout());

                GridBagConstraints constraintsJCheckBoxDaily = new GridBagConstraints();
                constraintsJCheckBoxDaily.gridx = 1;
                constraintsJCheckBoxDaily.gridy = 1;
                constraintsJCheckBoxDaily.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJCheckBoxDaily.ipadx = 30;
                constraintsJCheckBoxDaily.ipady = -7;
                constraintsJCheckBoxDaily.insets = new java.awt.Insets(9, 11, 1, 20);
                getJPanelHistory().add(getJCheckBoxDaily(), constraintsJCheckBoxDaily);

                GridBagConstraints constraintsJCheckBoxMonthly = new GridBagConstraints();
                constraintsJCheckBoxMonthly.gridx = 1;
                constraintsJCheckBoxMonthly.gridy = 2;
                constraintsJCheckBoxMonthly.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJCheckBoxMonthly.ipadx = 12;
                constraintsJCheckBoxMonthly.ipady = -7;
                constraintsJCheckBoxMonthly.insets = new java.awt.Insets(2, 11, 13, 20);
                getJPanelHistory().add(getJCheckBoxMonthly(), constraintsJCheckBoxMonthly);

                GridBagConstraints constraintsJCheckBoxSeasonal = new GridBagConstraints();
                constraintsJCheckBoxSeasonal.gridx = 2;
                constraintsJCheckBoxSeasonal.gridy = 1;
                constraintsJCheckBoxSeasonal.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJCheckBoxSeasonal.ipadx = 2;
                constraintsJCheckBoxSeasonal.ipady = -7;
                constraintsJCheckBoxSeasonal.insets = new java.awt.Insets(9, 12, 1, 20);
                getJPanelHistory().add(getJCheckBoxSeasonal(), constraintsJCheckBoxSeasonal);

                GridBagConstraints constraintsJCheckBoxAnnual = new GridBagConstraints();
                constraintsJCheckBoxAnnual.gridx = 2;
                constraintsJCheckBoxAnnual.gridy = 2;
                constraintsJCheckBoxAnnual.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJCheckBoxAnnual.ipadx = 17;
                constraintsJCheckBoxAnnual.ipady = -7;
                constraintsJCheckBoxAnnual.insets = new java.awt.Insets(2, 12, 13, 20);
                getJPanelHistory().add(getJCheckBoxAnnual(), constraintsJCheckBoxAnnual);

            } catch (java.lang.Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjJPanelHistory;
    }

    /**
     * Return the JTextFieldKWCapacity property value.
     */
    private JTextField getJTextFieldKWCapacity() {
        if (ivjJTextFieldKWCapacity == null) {
            try {
                ivjJTextFieldKWCapacity = new javax.swing.JTextField();
                ivjJTextFieldKWCapacity.setName("JTextFieldKWCapacity");

                ivjJTextFieldKWCapacity.setDocument(new DoubleRangeDocument(0.0, 99999.999, 3));
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJTextFieldKWCapacity;
    }

    /**
     * Return the NameTextField property value.
     */
    private JTextField getJTextFieldName() {
        if (ivjJTextFieldName == null) {
            try {
                ivjJTextFieldName = new JTextField();
                ivjJTextFieldName.setName("JTextFieldName");
                ivjJTextFieldName.setMaximumSize(new Dimension(2147483647, 20));
                ivjJTextFieldName.setColumns(12);
                ivjJTextFieldName.setPreferredSize(new Dimension(132, 20));
                ivjJTextFieldName.setFont(new Font("sansserif", 0, 14));
                ivjJTextFieldName.setMinimumSize(new Dimension(132, 20));

                ivjJTextFieldName.setDocument(
                    new TextFieldDocument(TextFieldDocument.MAX_DEVICE_NAME_LENGTH, PaoUtils.ILLEGAL_NAME_CHARS));

            } catch (java.lang.Throwable ivjExc) {

                handleException(ivjExc);
            }
        }
        return ivjJTextFieldName;
    }

    /**
     * Return the DeviceTypeTextField property value.
     */
    private JLabel getJTextFieldType() {
        if (ivjJTextFieldType == null) {
            try {
                ivjJTextFieldType = new JLabel();
                ivjJTextFieldType.setName("JTextFieldType");
                ivjJTextFieldType.setOpaque(true);
                ivjJTextFieldType.setFont(new Font("Arial", 1, 14));
                ivjJTextFieldType.setText("(UNKNOWN)");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJTextFieldType;
    }

    /**
     * Return the RouteComboBox property value.
     */
    private JComboBox<LiteYukonPAObject> getRouteComboBox() {
        if (ivjRouteComboBox == null) {
            try {
                ivjRouteComboBox = new JComboBox<>();
                ivjRouteComboBox.setName("RouteComboBox");
                ivjRouteComboBox.setPreferredSize(new Dimension(210, 25));
                ivjRouteComboBox.setMinimumSize(new Dimension(210, 25));

                IDatabaseCache cache = DefaultDatabaseCache.getInstance();
                synchronized (cache) {
                    List<LiteYukonPAObject> routes = cache.getAllRoutes();
                    for (int i = 0; i < routes.size(); i++) {
                        getRouteComboBox().addItem(routes.get(i));
                    }
                }

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
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjRouteLabel;
    }

    @Override
    public Object getValue(Object val) {
        LMGroup lmGroup = (LMGroup) val;

        lmGroup.setPAOName(getJTextFieldName().getText());

        if (getJCheckBoxDisable().isSelected()) {
            lmGroup.setDisableFlag(CtiUtilities.trueChar);
        } else {
            lmGroup.setDisableFlag(CtiUtilities.falseChar);
        }

        if (getJCheckBoxDisableControl().isSelected()) {
            lmGroup.getDevice().setControlInhibit(CtiUtilities.trueChar);
        } else {
            lmGroup.getDevice().setControlInhibit(CtiUtilities.falseChar);
        }

        if (getJTextFieldKWCapacity().getText() != null
            && getJTextFieldKWCapacity().getText().length() > 0)
        {
            lmGroup.getLmGroup().setKwCapacity(new Double(getJTextFieldKWCapacity().getText()));
        }

        // only set the route ID for certain LmGroups
        if (lmGroup instanceof IGroupRoute)
        {
            ((IGroupRoute) val)
                .setRouteID(
                new Integer(((com.cannontech.database.data.lite.LiteYukonPAObject) getRouteComboBox()
                    .getSelectedItem()).getYukonID()));
        }

        // only set the priority for certain LmGroups
        if (lmGroup instanceof LMGroupExpressCom) {
            ((LMGroupExpressCom) val).getLMGroupExpressComm()
                .setProtocolPriority(getSelectedPriority());
        }

        return lmGroup;

    }

    private int getSelectedPriority() {
        String priorityString = (String) priorityCombo.getSelectedItem();

        if ("medium".equalsIgnoreCase(priorityString)) {
            return 2;
        } else if ("high".equalsIgnoreCase(priorityString)) {
            return 1;
        } else if ("highest".equalsIgnoreCase(priorityString)) {
            return 0;
        } else {
            return 3;
        }

    }

    private void setSelectedPriority(int priority) {

        switch (priority) {
        case 0:
            getPriorityCombo().setSelectedIndex(3);
            break;
        case 1:
            getPriorityCombo().setSelectedIndex(2);
            break;
        case 2:
            getPriorityCombo().setSelectedIndex(1);
            break;
        default:
            getPriorityCombo().setSelectedIndex(0);
            break;
        }

    }

    /**
     * Called whenever the part throws an exception.
     * @param exception java.lang.Throwable
     */
    private void handleException(Throwable exception) {
        com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
        com.cannontech.clientutils.CTILogger.error(exception.getMessage(), exception);
    }

    /**
     * Initializes connections
     */
    private void initConnections() throws java.lang.Exception {

        getJTextFieldName().addCaretListener(this);
        getRouteComboBox().addActionListener(this);
        getJTextFieldKWCapacity().addCaretListener(this);
        getJCheckBoxDisableControl().addActionListener(this);
        getJCheckBoxDisable().addActionListener(this);
        getJCheckBoxHistory().addActionListener(this);
        getJCheckBoxDaily().addActionListener(this);
        getJCheckBoxSeasonal().addActionListener(this);
        getJCheckBoxAnnual().addActionListener(this);
        getJCheckBoxMonthly().addActionListener(this);
        getPriorityCombo().addActionListener(this);

    }

    /**
     * Initialize the class.
     */
    private void initialize() {
        try {
            setName("LMGroupBaseEditorPanel");
            setLayout(new java.awt.GridBagLayout());
            setSize(432, 416);
            setMinimumSize(new java.awt.Dimension(509, 472));

            java.awt.GridBagConstraints constraintsIdentificationPanel =
                new java.awt.GridBagConstraints();
            constraintsIdentificationPanel.gridx = 1;
            constraintsIdentificationPanel.gridy = 1;
            constraintsIdentificationPanel.fill = java.awt.GridBagConstraints.BOTH;
            constraintsIdentificationPanel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsIdentificationPanel.weightx = 1.0;
            constraintsIdentificationPanel.weighty = 1.0;
            constraintsIdentificationPanel.ipadx = -10;
            constraintsIdentificationPanel.ipady = -7;
            constraintsIdentificationPanel.insets = new java.awt.Insets(14, 6, 7, 4);
            add(getIdentificationPanel(), constraintsIdentificationPanel);

            GridBagConstraints constraintsCommunicationPanel = new GridBagConstraints();
            constraintsCommunicationPanel.gridx = 1;
            constraintsCommunicationPanel.gridy = 2;
            constraintsCommunicationPanel.fill = java.awt.GridBagConstraints.BOTH;
            constraintsCommunicationPanel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsCommunicationPanel.weightx = 1.0;
            constraintsCommunicationPanel.weighty = 1.0;
            constraintsCommunicationPanel.ipadx = -10;
            constraintsCommunicationPanel.ipady = -9;
            constraintsCommunicationPanel.insets = new java.awt.Insets(8, 6, 3, 4);
            add(getCommunicationPanel(), constraintsCommunicationPanel);

            GridBagConstraints constraintsJPanelAllHistory = new GridBagConstraints();
            constraintsJPanelAllHistory.gridx = 1;
            constraintsJPanelAllHistory.gridy = 3;
            constraintsJPanelAllHistory.fill = java.awt.GridBagConstraints.BOTH;
            constraintsJPanelAllHistory.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJPanelAllHistory.weightx = 1.0;
            constraintsJPanelAllHistory.weighty = 1.0;
            constraintsJPanelAllHistory.insets = new java.awt.Insets(4, 6, 25, 4);
            add(getJPanelAllHistory(), constraintsJPanelAllHistory);
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }

    }

    /**
     * This method must be implemented if a notion of data validity needs to be supported.
     * @return boolean
     */
    @Override
    public boolean isInputValid() {
        boolean isValid;
        String newName = getJTextFieldName().getText();

        if (lmGroup == null
            || isUniquePao(newName, lmGroup.getPaoType(), lmGroup.getPAObjectID())) {
            setErrorString("");
            isValid = true;
        } else {
            setErrorString("The name \'" + newName + "\' is already in use.");
            isValid = false;
        }

        if (getJTextFieldKWCapacity().isVisible()
            && (getJTextFieldKWCapacity().getText() == null
            || getJTextFieldKWCapacity().getText().length() <= 0))
        {
            setErrorString("The kW Capacity text field must be filled in.");
            isValid = false;
        }

        if (newName == null || newName.length() <= 0) {
            setErrorString("The Group Name text field must be filled in.");
            isValid = false;
        }

        return isValid;
    }

    public void jCheckBoxHistory_ActionPerformed(ActionEvent actionEvent) {
        for (int i = 0; i < getJPanelHistory().getComponentCount(); i++) {
            getJPanelHistory().getComponent(i).setEnabled(getJCheckBoxHistory().isSelected());
        }

        fireInputUpdate();

        return;
    }

    public static void main(String[] args) {
        try {
            JFrame frame = new JFrame();
            LMGroupBasePanel aLMGroupBasePanel;
            aLMGroupBasePanel = new LMGroupBasePanel();
            frame.setContentPane(aLMGroupBasePanel);
            frame.setSize(aLMGroupBasePanel.getSize());
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });
            frame.setVisible(true);
            java.awt.Insets insets = frame.getInsets();
            frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
            frame.setVisible(true);
        } catch (Throwable exception) {
            System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
            CTILogger.error(exception.getMessage(), exception);
        }
    }

    public void setSwitchType(PaoType paoType) {
        getJTextFieldType().setText(paoType.getPaoTypeName());

        // do not show the route panel if the type is one of the following
        getCommunicationPanel() .setVisible(!(paoType == PaoType.MACRO_GROUP
                          || paoType == PaoType.LM_GROUP_POINT
                          || paoType == PaoType.LM_GROUP_DIGI_SEP
                          || paoType == PaoType.LM_GROUP_ECOBEE
                          || paoType == PaoType.LM_GROUP_HONEYWELL
                          || paoType == PaoType.LM_GROUP_ITRON
                          || paoType == PaoType.LM_GROUP_NEST
                          || paoType == PaoType.LM_GROUP_RFN_EXPRESSCOMM));

        // dont show the following options if this group is a MACRO
        getJLabelKWCapacity() .setVisible(!(paoType == PaoType.MACRO_GROUP));
        getJTextFieldKWCapacity().setVisible(!(paoType == PaoType.MACRO_GROUP));

        getJCheckBoxDisable().setVisible(!(paoType == PaoType.MACRO_GROUP));
        getJCheckBoxDisableControl().setVisible(!(paoType == PaoType.MACRO_GROUP));

        getJCheckBoxHistory().setVisible(getJCheckBoxHistory().isVisible() && !(paoType == PaoType.MACRO_GROUP));

        getJPanelHistory().setVisible(getJCheckBoxHistory().isVisible() && !(paoType == PaoType.MACRO_GROUP));

        if (paoType != PaoType.LM_GROUP_EXPRESSCOMM &&
                paoType != PaoType.LM_GROUP_RFN_EXPRESSCOMM) {
            getPriorityCombo().setVisible(false);
            getPriorityLabel().setVisible(false);
        }

    }

    @Override
    public void setValue(Object val) {
        lmGroup = (LMGroup) val;

        String name = lmGroup.getPAOName();
        String type = lmGroup.getPaoType().getPaoTypeName();

        getJTextFieldName().setText(name);
        getJTextFieldType().setText(type);

        getJCheckBoxDisable().setSelected(CtiUtilities.isTrue(lmGroup.getPAODisableFlag()));

        getJCheckBoxDisableControl().setSelected(CtiUtilities.isTrue(lmGroup.getDevice().getControlInhibit()));

        getJTextFieldKWCapacity().setText(lmGroup.getLmGroup().getKwCapacity().toString());

        if (lmGroup instanceof IGroupRoute) {
            IDatabaseCache cache = DefaultDatabaseCache.getInstance();
            synchronized (cache) {
                List<LiteYukonPAObject> routes = cache.getAllRoutes();
                int assignedRouteID = ((IGroupRoute) lmGroup).getRouteID().intValue();

                for (int i = 0; i < routes.size(); i++) {
                    if (routes.get(i).getYukonID() == assignedRouteID) {
                        getRouteComboBox().setSelectedItem(routes.get(i));
                    }
                }
            }
        }

        // only set the priority for certain LmGroups
        if (lmGroup instanceof LMGroupExpressCom) {
            int priority = ((LMGroupExpressCom) lmGroup).getLMGroupExpressComm().getProtocolPriority();
            setSelectedPriority(priority);
        }

        // show the needed entry fields only
        setSwitchType(lmGroup.getPaoType());
    }
}
