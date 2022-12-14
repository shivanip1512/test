package com.cannontech.dbeditor.editor.point;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.gui.unchanging.DoubleRangeDocument;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.TitleBorder;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.AnalogControlType;
import com.cannontech.database.data.point.AnalogPoint;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointType;
import com.cannontech.spring.YukonSpringHook;
import com.klg.jclass.field.DataProperties;
import com.klg.jclass.field.JCInvalidInfo;
import com.klg.jclass.field.JCSpinField;
import com.klg.jclass.field.validate.JCIntegerValidator;
import com.klg.jclass.util.value.JCValueEvent;
import com.klg.jclass.util.value.JCValueListener;
import com.klg.jclass.util.value.MutableValueModel;


/**
 * This panel is also being used for "System" point types.
 */
public class PointAnalogPhysicalSettingsEditorPanel extends DataInputPanel implements ActionListener, ItemListener,
        CaretListener, JCValueListener {
    private JLabel ivjPointOffsetLabel = null;
    private JCheckBox ivjPhysicalPointOffsetCheckBox = null;
    private JCSpinField ivjDeadbandSpinner = null;
    private JCheckBox ivjDeadbandCheckBox = null;
    private JCSpinField ivjPointOffsetSpinner = null;
    private Vector<LitePoint> usedPointOffsetsVector = null;
    private JPanel ivjDeadbandPanel = null;
    private JLabel ivjUsedPointOffsetLabel = null;
    private JLabel ivjDataOffsetLabel = null;
    private JTextField ivjDataOffsetTextField = null;
    private JLabel ivjMultiplierLabel = null;
    private JTextField ivjMultiplierTextField = null;
    private JPanel ivjRawValuePanel = null;
    private JPanel ivjControlSettingsPanel = null;
    private JComboBox<String> ivjControlTypeComboBox = null;
    private JLabel ivjControlTypeLabel = null;
    private JLabel ivjControlPointOffsetLabel = null;
    private JCSpinField ivjControlPointOffsetSpinner = null;
    private JCheckBox ivjControlInhibitCheckBox = null;

    public PointAnalogPhysicalSettingsEditorPanel() {
        super();
        initialize();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == getControlTypeComboBox()) {
                this.controlTypeComboBox_ActionPerformed(e);
            }
            if (e.getSource() == getControlInhibitCheckBox()) {
                this.fireInputUpdate();
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }

    }

    @Override
    public void caretUpdate(CaretEvent e) {
        try {
            if (e.getSource() == getMultiplierTextField()) {
                this.fireInputUpdate();
            }
            if (e.getSource() == getDataOffsetTextField()) {
                this.fireInputUpdate();
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC3: (PhysicalPointOffsetCheckBox.item.itemStateChanged(ItemEvent)
     * --> PointAnalogPhysicalSettingsEditorPanel.fireInputUpdate()V)
     * @param arg1 ItemEvent
     */
    private void connEtoC3(ItemEvent arg1) {
        try {
            this.fireInputUpdate();
            if (getPhysicalPointOffsetCheckBox().isSelected()) {
                getPointOffsetSpinner().setEnabled(true);
                getPointOffsetSpinner().setValidator(new JCIntegerValidator(null,
                                                                            new Integer(1),
                                                                            new Integer(Integer.MAX_VALUE),
                                                                            null,
                                                                            true,
                                                                            null,
                                                                            new Integer(1),
                                                                            "#,##0.###;-#,##0.###",
                                                                            false,
                                                                            false,
                                                                            false,
                                                                            null,
                                                                            new Integer(0)));
                getPointOffsetSpinner().setValue(new Integer(1));
                int temp = 2;
                while (getUsedPointOffsetLabel().getText() != "") {
                    getPointOffsetSpinner().setValue(new Integer(temp));
                    temp++;
                }
                getPointOffsetLabel().setEnabled(true);
            } else {
                getPointOffsetSpinner().setValidator(new JCIntegerValidator(null,
                                                                            new Integer(0),
                                                                            new Integer(0),
                                                                            null,
                                                                            true,
                                                                            null,
                                                                            new Integer(1),
                                                                            "#,##0.###;-#,##0.###",
                                                                            false,
                                                                            false,
                                                                            false,
                                                                            null,
                                                                            new Integer(0)));
                getPointOffsetSpinner().setValue(new Integer(0));
                getPointOffsetLabel().setEnabled(false);
                getDeadbandCheckBox().setSelected(false);
                getDeadbandSpinner().setValidator(new JCIntegerValidator(null,
                                                                         new Integer(-1),
                                                                         new Integer(-1),
                                                                         null,
                                                                         true,
                                                                         null,
                                                                         new Integer(1),
                                                                         "#,##0.###;-#,##0.###",
                                                                         false,
                                                                         false,
                                                                         false,
                                                                         null,
                                                                         new Integer(-1)));
                getDeadbandSpinner().setValue(new Integer(-1));
                getPointOffsetSpinner().setEnabled(false);
            }

            revalidate();
            repaint();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    /**
     * connEtoC5: (DeadbandCheckBox.item.itemStateChanged(ItemEvent) -->
     * PointAnalogPhysicalSettingsEditorPanel.fireInputUpdate()V)
     * @param arg1 ItemEvent
     */
    private void connEtoC5(ItemEvent arg1) {
        try {
            this.fireInputUpdate();
            if (getDeadbandCheckBox().isSelected()) {
                getDeadbandSpinner().setEnabled(true);
            } else {
                getDeadbandSpinner().setEnabled(false);
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    public void controlTypeComboBox_ActionPerformed(ActionEvent actionEvent) {
        Object controlType = getControlTypeComboBox().getSelectedItem();

        boolean enabled = !controlType.toString().equals(AnalogControlType.NONE.getControlName());

        getControlPointOffsetLabel().setEnabled(enabled);
        getControlPointOffsetSpinner().setEnabled(enabled);

        getControlInhibitCheckBox().setEnabled(enabled);

        revalidate();
        repaint();
        fireInputUpdate();
        return;
    }

    private JCheckBox getControlInhibitCheckBox() {
        if (ivjControlInhibitCheckBox == null) {
            try {
                ivjControlInhibitCheckBox = new JCheckBox();
                ivjControlInhibitCheckBox.setName("ControlInhibitCheckBox");
                ivjControlInhibitCheckBox.setSelected(false);
                ivjControlInhibitCheckBox.setEnabled(false);
                ivjControlInhibitCheckBox.setFont(new java.awt.Font("dialog", 0, 14));
                ivjControlInhibitCheckBox.setText("Control Inhibit");
                ivjControlInhibitCheckBox.setActionCommand("PhysicalPointOffsetCheckBox");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjControlInhibitCheckBox;
    }

    private JLabel getControlPointOffsetLabel() {
        if (ivjControlPointOffsetLabel == null) {
            try {
                ivjControlPointOffsetLabel = new JLabel();
                ivjControlPointOffsetLabel.setName("ControlPointOffsetLabel");
                ivjControlPointOffsetLabel.setText("Control Pt Offset:");
                ivjControlPointOffsetLabel.setFont(new java.awt.Font("dialog", 0, 14));
                ivjControlPointOffsetLabel.setEnabled(false);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjControlPointOffsetLabel;
    }

    private JCSpinField getControlPointOffsetSpinner() {
        if (ivjControlPointOffsetSpinner == null) {
            try {
                ivjControlPointOffsetSpinner = new JCSpinField();
                ivjControlPointOffsetSpinner.setName("ControlPointOffsetSpinner");
                ivjControlPointOffsetSpinner.setBackground(java.awt.Color.white);
                ivjControlPointOffsetSpinner.setEnabled(false);
                ivjControlPointOffsetSpinner.setPreferredSize(new java.awt.Dimension(55, 22));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjControlPointOffsetSpinner;
    }

    private JPanel getControlSettingsPanel() {
        if (ivjControlSettingsPanel == null) {
            try {
                TitleBorder ivjLocalBorder;
                ivjLocalBorder = new TitleBorder();
                ivjLocalBorder.setTitleFont(new java.awt.Font("dialog.bold", 1, 14));
                ivjLocalBorder.setTitle("Control Settings");
                ivjControlSettingsPanel = new JPanel();
                ivjControlSettingsPanel.setName("ControlPanel");
                ivjControlSettingsPanel.setBorder(ivjLocalBorder);
                ivjControlSettingsPanel.setLayout(new java.awt.GridBagLayout());

                GridBagConstraints constraintsControlTypeLabel = new GridBagConstraints();
                constraintsControlTypeLabel.gridx = 0;
                constraintsControlTypeLabel.anchor = GridBagConstraints.LINE_START;
                constraintsControlTypeLabel.insets = new Insets(0, 8, 0, 4);
                getControlSettingsPanel().add(getControlTypeLabel(), constraintsControlTypeLabel);

                GridBagConstraints constraintsControlTypeComboBox = new GridBagConstraints();
                constraintsControlTypeComboBox.gridx = 1;
                constraintsControlTypeComboBox.weightx = 0.5;
                constraintsControlTypeComboBox.anchor = GridBagConstraints.LINE_START;
                constraintsControlTypeComboBox.fill = GridBagConstraints.HORIZONTAL;
                getControlSettingsPanel().add(getControlTypeComboBox(), constraintsControlTypeComboBox);

                GridBagConstraints constraintsControlPointOffsetLabel = new GridBagConstraints();
                constraintsControlPointOffsetLabel.gridx = 2;
                constraintsControlPointOffsetLabel.insets = new Insets(0, 4, 0, 4);
                getControlSettingsPanel().add(getControlPointOffsetLabel(), constraintsControlPointOffsetLabel);

                GridBagConstraints constraintsControlPointOffsetSpinner = new GridBagConstraints();
                constraintsControlPointOffsetSpinner.gridx = 3;
                getControlSettingsPanel().add(getControlPointOffsetSpinner(), constraintsControlPointOffsetSpinner);

                GridBagConstraints constraintsControlInhibitCheckBox = new GridBagConstraints();
                constraintsControlInhibitCheckBox.gridy = 1;
                constraintsControlInhibitCheckBox.gridwidth = GridBagConstraints.REMAINDER;
                constraintsControlInhibitCheckBox.anchor = GridBagConstraints.LINE_START;
                constraintsControlInhibitCheckBox.insets = new java.awt.Insets(4, 4, 0, 0);
                getControlSettingsPanel().add(getControlInhibitCheckBox(), constraintsControlInhibitCheckBox);

            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjControlSettingsPanel;
    }

    private JComboBox<String> getControlTypeComboBox() {
        if (ivjControlTypeComboBox == null) {
            try {
                ivjControlTypeComboBox = new JComboBox<String>();
                ivjControlTypeComboBox.setName("ControlTypeComboBox");
                ivjControlTypeComboBox.setFont(new java.awt.Font("dialog", 0, 14));

                ivjControlTypeComboBox.addItem(AnalogControlType.NONE.getControlName());
                ivjControlTypeComboBox.addItem(AnalogControlType.NORMAL.getControlName());
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjControlTypeComboBox;
    }

    private JLabel getControlTypeLabel() {
        if (ivjControlTypeLabel == null) {
            try {
                ivjControlTypeLabel = new JLabel();
                ivjControlTypeLabel.setName("ControlTypeLabel");
                ivjControlTypeLabel.setText("Control Type:");
                ivjControlTypeLabel.setMaximumSize(new java.awt.Dimension(86, 23));
                ivjControlTypeLabel.setPreferredSize(new java.awt.Dimension(86, 23));
                ivjControlTypeLabel.setFont(new java.awt.Font("dialog", 0, 14));
                ivjControlTypeLabel.setMinimumSize(new java.awt.Dimension(86, 23));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjControlTypeLabel;
    }

    /**
     * Return the DataOffsetLabel property value.
     * @return JLabel
     */
    private JLabel getDataOffsetLabel() {
        if (ivjDataOffsetLabel == null) {
            try {
                ivjDataOffsetLabel = new JLabel();
                ivjDataOffsetLabel.setName("DataOffsetLabel");
                ivjDataOffsetLabel.setText("Data Offset:");
                ivjDataOffsetLabel.setFont(new java.awt.Font("dialog", 0, 14));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjDataOffsetLabel;
    }

    /**
     * Return the DataOffsetTextField property value.
     * @return JTextField
     */
    private JTextField getDataOffsetTextField() {
        if (ivjDataOffsetTextField == null) {
            try {
                ivjDataOffsetTextField = new JTextField();
                ivjDataOffsetTextField.setName("DataOffsetTextField");
                ivjDataOffsetTextField.setColumns(17); // -999999.999999999
                ivjDataOffsetTextField.setDocument(new DoubleRangeDocument(-999999.9999999, 999999.999999999, 9));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjDataOffsetTextField;
    }

    /**
     * Return the DeadbandCheckBox property value.
     * @return JCheckBox
     */
    private JCheckBox getDeadbandCheckBox() {
        if (ivjDeadbandCheckBox == null) {
            try {
                ivjDeadbandCheckBox = new JCheckBox();
                ivjDeadbandCheckBox.setName("DeadbandCheckBox");
                ivjDeadbandCheckBox.setSelected(true);
                ivjDeadbandCheckBox.setText("Enable");
                ivjDeadbandCheckBox.setActionCommand("PhysicalPointOffsetCheckBox");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjDeadbandCheckBox;
    }

    /**
     * Return the DeadbandPanel property value.
     * @return JPanel
     */
    private JPanel getDeadbandPanel() {
        if (ivjDeadbandPanel == null) {
            try {
                TitleBorder ivjLocalBorder;
                ivjLocalBorder = new TitleBorder();
                ivjLocalBorder.setTitleFont(new java.awt.Font("dialog.bold", 1, 14));
                ivjLocalBorder.setTitle("Deadband");
                ivjDeadbandPanel = new JPanel();
                ivjDeadbandPanel.setName("DeadbandPanel");
                ivjDeadbandPanel.setBorder(ivjLocalBorder);
                ivjDeadbandPanel.setLayout(new java.awt.GridBagLayout());

                GridBagConstraints constraintsDeadbandCheckBox = new GridBagConstraints();
                constraintsDeadbandCheckBox.gridx = 1;
                constraintsDeadbandCheckBox.gridy = 1;
                getDeadbandPanel().add(getDeadbandCheckBox(), constraintsDeadbandCheckBox);

                GridBagConstraints constraintsDeadbandSpinner = new GridBagConstraints();
                constraintsDeadbandSpinner.gridx = 2;
                constraintsDeadbandSpinner.gridy = 1;
                getDeadbandPanel().add(getDeadbandSpinner(), constraintsDeadbandSpinner);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjDeadbandPanel;
    }

    /**
     * Return the DeadbandSpinner property value.
     * @return JCSpinField
     */
    private JCSpinField getDeadbandSpinner() {
        if (ivjDeadbandSpinner == null) {
            try {
                ivjDeadbandSpinner = new JCSpinField();
                ivjDeadbandSpinner.setName("DeadbandSpinner");
                ivjDeadbandSpinner.setPreferredSize(new java.awt.Dimension(60, 22));
                ivjDeadbandSpinner.setFont(new java.awt.Font("dialog", 0, 14));
                ivjDeadbandSpinner.setBackground(java.awt.Color.white);
                ivjDeadbandSpinner.setMinimumSize(new java.awt.Dimension(60, 22));
                ivjDeadbandSpinner.setDataProperties(new DataProperties(new JCIntegerValidator(null,
                                                                                               new Integer(-1),
                                                                                               new Integer(1000),
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
                                                                        new MutableValueModel(Integer.class,
                                                                                              new Integer(0)),
                                                                        new JCInvalidInfo(true,
                                                                                          2,
                                                                                          new Color(0, 0, 0, 255),
                                                                                          new Color(255, 255, 255, 255))));
                ivjDeadbandSpinner.setValidator(new JCIntegerValidator(null,
                                                                       new Integer(-1),
                                                                       new Integer(100000),
                                                                       null,
                                                                       true,
                                                                       null,
                                                                       new Integer(1),
                                                                       "#,##0.###;-#,##0.###",
                                                                       false,
                                                                       false,
                                                                       false,
                                                                       null,
                                                                       new Integer(-1)));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjDeadbandSpinner;
    }

    /**
     * Return the MultiplierLabel property value.
     * @return JLabel
     */
    private JLabel getMultiplierLabel() {
        if (ivjMultiplierLabel == null) {
            try {
                ivjMultiplierLabel = new JLabel();
                ivjMultiplierLabel.setName("MultiplierLabel");
                ivjMultiplierLabel.setText("Multiplier:");
                ivjMultiplierLabel.setMinimumSize(ivjMultiplierLabel.getPreferredSize());
                ivjMultiplierLabel.setFont(new java.awt.Font("dialog", 0, 14));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjMultiplierLabel;
    }

    /**
     * Return the MultiplierTextField property value.
     * @return JTextField
     */
    private JTextField getMultiplierTextField() {
        if (ivjMultiplierTextField == null) {
            try {
                ivjMultiplierTextField = new JTextField();
                ivjMultiplierTextField.setName("MultiplierTextField");
                ivjMultiplierTextField.setColumns(17); // -999999.999999999
                ivjMultiplierTextField.setDocument(new DoubleRangeDocument(-999999.999999999, 999999.999999999, 9));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjMultiplierTextField;
    }

    /**
     * Return the PhysicalPointOffsetCheckBox property value.
     * @return JCheckBox
     */
    private JCheckBox getPhysicalPointOffsetCheckBox() {
        if (ivjPhysicalPointOffsetCheckBox == null) {
            try {
                ivjPhysicalPointOffsetCheckBox = new JCheckBox();
                ivjPhysicalPointOffsetCheckBox.setName("PhysicalPointOffsetCheckBox");
                ivjPhysicalPointOffsetCheckBox.setSelected(true);
                ivjPhysicalPointOffsetCheckBox.setText("Physical Point Offset");
                ivjPhysicalPointOffsetCheckBox.setFont(new java.awt.Font("dialog", 0, 14));
                ivjPhysicalPointOffsetCheckBox.setActionCommand("PhysicalPointOffsetCheckBox");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjPhysicalPointOffsetCheckBox;
    }

    /**
     * Return the PointOffsetLabel property value.
     * @return JLabel
     */
    private JLabel getPointOffsetLabel() {
        if (ivjPointOffsetLabel == null) {
            try {
                ivjPointOffsetLabel = new JLabel();
                ivjPointOffsetLabel.setName("PointOffsetLabel");
                ivjPointOffsetLabel.setText("Point Offset:");
                ivjPointOffsetLabel.setFont(new java.awt.Font("dialog", 0, 14));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjPointOffsetLabel;
    }

    /**
     * Return the PointOffsetField property value.
     * @return JCSpinField
     */
    private JCSpinField getPointOffsetSpinner() {
        if (ivjPointOffsetSpinner == null) {
            try {
                ivjPointOffsetSpinner = new JCSpinField();
                ivjPointOffsetSpinner.setName("PointOffsetSpinner");
                ivjPointOffsetSpinner.setPreferredSize(new java.awt.Dimension(60, 22));
                ivjPointOffsetSpinner.setFont(new java.awt.Font("dialog", 0, 14));
                ivjPointOffsetSpinner.setBackground(java.awt.Color.white);
                ivjPointOffsetSpinner.setMinimumSize(new java.awt.Dimension(60, 22));
                ivjPointOffsetSpinner.setDataProperties(new DataProperties(new JCIntegerValidator(null,
                                                                                                  new Integer(1),
                                                                                                  new Integer(Integer.MAX_VALUE),
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
                                                                           new MutableValueModel(Integer.class,
                                                                                                 new Integer(1)),
                                                                           new JCInvalidInfo(true,
                                                                                             2,
                                                                                             new Color(0, 0, 0, 255),
                                                                                             new Color(255, 255, 255, 255))));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjPointOffsetSpinner;
    }

    /**
     * Return the RawValuePanel property value.
     * @return JPanel
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private JPanel getRawValuePanel() {
        if (ivjRawValuePanel == null) {
            try {
                TitleBorder ivjLocalBorder1;
                ivjLocalBorder1 = new TitleBorder();
                ivjLocalBorder1.setTitleFont(new java.awt.Font("dialog.bold", 1, 14));
                ivjLocalBorder1.setTitle("Raw Value");
                ivjRawValuePanel = new JPanel();
                ivjRawValuePanel.setName("RawValuePanel");
                ivjRawValuePanel.setBorder(ivjLocalBorder1);
                ivjRawValuePanel.setLayout(new java.awt.GridBagLayout());

                GridBagConstraints constraintsMultiplierLabel = new GridBagConstraints();
                constraintsMultiplierLabel.gridx = 0;
                constraintsMultiplierLabel.gridy = 0;
                constraintsMultiplierLabel.anchor = GridBagConstraints.LINE_START;
                constraintsMultiplierLabel.insets = new Insets(0, 4, 4, 4);
                getRawValuePanel().add(getMultiplierLabel(), constraintsMultiplierLabel);

                GridBagConstraints constraintsMultiplierTextField = new GridBagConstraints();
                constraintsMultiplierTextField.gridx = 1;
                constraintsMultiplierTextField.gridy = 0;
                constraintsMultiplierTextField.anchor = GridBagConstraints.LINE_START;
                constraintsMultiplierTextField.insets = new Insets(0, 0, 4, 8);
                getRawValuePanel().add(getMultiplierTextField(), constraintsMultiplierTextField);

                GridBagConstraints constraintsDataOffsetLabel = new GridBagConstraints();
                constraintsDataOffsetLabel.gridx = 0;
                constraintsDataOffsetLabel.gridy = 1;
                constraintsDataOffsetLabel.anchor = GridBagConstraints.LINE_START;
                constraintsDataOffsetLabel.insets = new Insets(4, 4, 0, 4);
                getRawValuePanel().add(getDataOffsetLabel(), constraintsDataOffsetLabel);

                GridBagConstraints constraintsDataOffsetTextField = new GridBagConstraints();
                constraintsDataOffsetTextField.gridx = 1;
                constraintsDataOffsetTextField.gridy = 1;
                constraintsDataOffsetTextField.anchor = GridBagConstraints.LINE_START;
                constraintsDataOffsetTextField.insets = new Insets(4, 0, 0, 8);
                getRawValuePanel().add(getDataOffsetTextField(), constraintsDataOffsetTextField);

                GridBagConstraints constraintsPanel = new GridBagConstraints();
                constraintsPanel.fill = GridBagConstraints.HORIZONTAL;
                constraintsPanel.weightx = 1.0;
                constraintsPanel.gridy = 0;
                constraintsPanel.gridx = GridBagConstraints.RELATIVE;
                getRawValuePanel().add(new JPanel(), constraintsPanel);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjRawValuePanel;
    }

    /**
     * Return the InvalidPointOffsetLabel property value.
     * @return JLabel
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private JLabel getUsedPointOffsetLabel() {
        if (ivjUsedPointOffsetLabel == null) {
            try {
                ivjUsedPointOffsetLabel = new JLabel();
                ivjUsedPointOffsetLabel.setName("UsedPointOffsetLabel");
                ivjUsedPointOffsetLabel.setText("Offset Used");
                ivjUsedPointOffsetLabel.setFont(new java.awt.Font("dialog.bold", 1, 14));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjUsedPointOffsetLabel;
    }

    /**
     * This method was created in VisualAge.
     * @return java.lang.Object
     * @param val java.lang.Object
     */
    @Override
    public Object getValue(Object val) {
        
        PointBase pointBase = (PointBase)val;
        
        Integer pointOffset = null;
        Object pointOffsetSpinVal = getPointOffsetSpinner().getValue();
        if (pointOffsetSpinVal instanceof Long) {
            pointOffset = new Integer(((Long) pointOffsetSpinVal).intValue());
        } else if (pointOffsetSpinVal instanceof Integer) {
            pointOffset = new Integer(((Integer) pointOffsetSpinVal).intValue());
        }

        if ((getUsedPointOffsetLabel().getText()) == "") {
            pointBase.getPoint().setPointOffset(pointOffset);
        } else {
            pointBase.getPoint().setPointOffset(null);
        }

        if (pointBase.getPoint().getPointTypeEnum() == PointType.System) {
            return pointBase;
        }
        
        // set all the values below
        AnalogPoint point = (AnalogPoint) val;

        try {
            Double multiplier = new Double(getMultiplierTextField().getText());
            point.getPointAnalog().setMultiplier(multiplier);
        } catch (NumberFormatException n) {
            CTILogger.error(n.getMessage(), n);
        }

        try {
            Double dataOffset = new Double(getDataOffsetTextField().getText());
            point.getPointAnalog().setDataOffset(dataOffset);
        } catch (NumberFormatException n) {
            CTILogger.error(n.getMessage(), n);
        }

        if (getDeadbandCheckBox().isSelected()) {
            Double deadband = null;
            Object deadbandSpinVal = getDeadbandSpinner().getValue();
            if (deadbandSpinVal instanceof Long) {
                deadband = new Double(((Long) deadbandSpinVal).intValue());
            } else if (deadbandSpinVal instanceof Integer) {
                deadband = new Double(((Integer) deadbandSpinVal).intValue());
            }
            point.getPointAnalog().setDeadband(deadband);
        } else {
            point.getPointAnalog().setDeadband(-1.0);
        }
        
        Integer controlPointOffset = null;
        Object controlPointOffsetSpinVal = getControlPointOffsetSpinner().getValue();
        if (controlPointOffsetSpinVal instanceof Long) {
            controlPointOffset = new Integer(((Long) controlPointOffsetSpinVal).intValue());
        }  else if (pointOffsetSpinVal instanceof Integer) {
            controlPointOffset = new Integer(((Integer) controlPointOffsetSpinVal).intValue());
        }

        point.getPointAnalogControl().setControlOffset(controlPointOffset);
        point.getPointAnalogControl().setControlType(getControlTypeComboBox().getSelectedItem().toString());
        point.getPointAnalogControl().setControlInhibited(getControlInhibitCheckBox().isSelected());

        return point;
    }

    /**
     * Called whenever the part throws an exception.
     * @param exception java.lang.Throwable
     */
    private void handleException(Throwable exception) {

        /* Uncomment the following lines to print uncaught exceptions to stdout */
        // CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
        // CTILogger.error( exception.getMessage(), exception );;
    }

    /**
     * Initializes connections
     */
    private void initConnections() throws java.lang.Exception {

        getControlPointOffsetSpinner().addValueListener(this);
        getDeadbandSpinner().addValueListener(this);
        getPointOffsetSpinner().addValueListener(this);

        getPhysicalPointOffsetCheckBox().addItemListener(this);
        getDeadbandCheckBox().addItemListener(this);

        getMultiplierTextField().addCaretListener(this);
        getDataOffsetTextField().addCaretListener(this);

        getControlTypeComboBox().addActionListener(this);
        getControlInhibitCheckBox().addActionListener(this);
    }

    /**
     * Initialize the class.
     */
    private void initialize() {
        try {
            setName("PointAnalogPhysicalSettingsEditorPanel");
            setLayout(new java.awt.GridBagLayout());
            setSize(399, 305);

            GridBagConstraints constraintsPhysicalPointOffsetCheckBox = new GridBagConstraints();
            constraintsPhysicalPointOffsetCheckBox.gridx = 0;
            constraintsPhysicalPointOffsetCheckBox.gridy = 0;
            constraintsPhysicalPointOffsetCheckBox.gridwidth = GridBagConstraints.REMAINDER;
            constraintsPhysicalPointOffsetCheckBox.anchor = GridBagConstraints.FIRST_LINE_START;
            constraintsPhysicalPointOffsetCheckBox.insets = new Insets(16, 16, 8, 0);
            add(getPhysicalPointOffsetCheckBox(), constraintsPhysicalPointOffsetCheckBox);

            GridBagConstraints constraintsPointOffsetLabel = new GridBagConstraints();
            constraintsPointOffsetLabel.gridx = 0;
            constraintsPointOffsetLabel.gridy = 1;
            constraintsPointOffsetLabel.insets = new Insets(0, 20, 16, 8);
            add(getPointOffsetLabel(), constraintsPointOffsetLabel);

            GridBagConstraints constraintsPointOffsetSpinner = new GridBagConstraints();
            constraintsPointOffsetSpinner.gridx = 1;
            constraintsPointOffsetSpinner.gridy = 1;
            constraintsPointOffsetSpinner.insets = new Insets(0, 0, 16, 0);
            add(getPointOffsetSpinner(), constraintsPointOffsetSpinner);

            GridBagConstraints constraintsUsedPointOffsetLabel = new GridBagConstraints();
            constraintsUsedPointOffsetLabel.gridx = 2;
            constraintsUsedPointOffsetLabel.gridy = 1;
            constraintsUsedPointOffsetLabel.anchor = GridBagConstraints.FIRST_LINE_START;
            constraintsUsedPointOffsetLabel.gridwidth = GridBagConstraints.REMAINDER;
            constraintsUsedPointOffsetLabel.weightx = 1.0;
            constraintsUsedPointOffsetLabel.fill = GridBagConstraints.HORIZONTAL;
            add(getUsedPointOffsetLabel(), constraintsUsedPointOffsetLabel);

            GridBagConstraints constraintsDeadbandPanel = new GridBagConstraints();
            constraintsDeadbandPanel.gridx = 0;
            constraintsDeadbandPanel.gridy = 2;
            constraintsDeadbandPanel.gridwidth = GridBagConstraints.REMAINDER;
            constraintsDeadbandPanel.fill = GridBagConstraints.HORIZONTAL;
            constraintsDeadbandPanel.weightx = 1.0;
            constraintsDeadbandPanel.insets = new Insets(0, 16, 16, 16);
            constraintsDeadbandPanel.ipadx = 16;
            constraintsDeadbandPanel.ipady = 16;
            add(getDeadbandPanel(), constraintsDeadbandPanel);

            GridBagConstraints constraintsRawValuePanel = new GridBagConstraints();
            constraintsRawValuePanel.gridx = 0;
            constraintsRawValuePanel.gridy = 3;
            constraintsRawValuePanel.gridwidth = GridBagConstraints.REMAINDER;
            constraintsRawValuePanel.fill = GridBagConstraints.HORIZONTAL;
            constraintsRawValuePanel.weightx = 1.0;
            constraintsRawValuePanel.insets = new Insets(0, 16, 16, 16);
            constraintsRawValuePanel.ipadx = 16;
            constraintsRawValuePanel.ipady = 16;
            add(getRawValuePanel(), constraintsRawValuePanel);

            GridBagConstraints constraintsControlPanel = new GridBagConstraints();
            constraintsControlPanel.gridx = 0;
            constraintsControlPanel.gridy = 4;
            constraintsControlPanel.gridwidth = GridBagConstraints.REMAINDER;
            constraintsControlPanel.fill = GridBagConstraints.HORIZONTAL;
            constraintsControlPanel.weightx = 1.0;
            constraintsControlPanel.insets = new Insets(0, 16, 16, 16);
            constraintsControlPanel.ipadx = 16;
            constraintsControlPanel.ipady = 16;
            add(getControlSettingsPanel(), constraintsControlPanel);

            GridBagConstraints constraintsPanel = new GridBagConstraints();
            constraintsPanel.fill = GridBagConstraints.VERTICAL;
            constraintsPanel.weighty = 1.0;
            constraintsPanel.gridy = GridBagConstraints.RELATIVE;
            add(new JPanel(), constraintsPanel);

            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    /**
     * Helper method to determine if the pointOffset is already in use
     * @param pointOffset - Offset to check
     * @return - True if offset is used by another point
     */
    private boolean isPointOffsetInUse(int pointOffset) {

        if (this.usedPointOffsetsVector != null && this.usedPointOffsetsVector.size() > 0) {
            for (LitePoint point : this.usedPointOffsetsVector) {
                if (point.getPointOffset() == pointOffset) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isInputValid() {
        if (getRawValuePanel().isVisible()) {
            if (getMultiplierTextField().getText() != null && getMultiplierTextField().getText().length() >= 1) {
                try {
                    Double.parseDouble(getMultiplierTextField().getText());
                } catch (NumberFormatException e) {
                    setErrorString("The Multiplier text field must contain a valid number");
                    return false;
                }
    
            } else {
                setErrorString("The Multiplier text field must be filled in");
                return false;
            }

            if (getDataOffsetTextField().getText() != null && getDataOffsetTextField().getText().length() >= 1) {
                try {
                    Double.parseDouble(getDataOffsetTextField().getText());
                } catch (NumberFormatException e) {
                    setErrorString("The Data Offset text field must contain a valid number");
                    return false;
                }
    
            } else {
                setErrorString("The Data Offset text field must be filled in");
                return false;
            }
        }

        if (getPhysicalPointOffsetCheckBox().isSelected()) {
            Object value = this.getPointOffsetSpinner().getValue();

            if (value instanceof Number) {
                Number numValue = (Number) value;
                if (this.isPointOffsetInUse(numValue.intValue())) {
                    setErrorString("Analog Point Offset " + numValue.intValue() + " is in use for this device");
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Method to handle events for the ItemListener interface.
     * @param e ItemEvent
     */
    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == getPhysicalPointOffsetCheckBox()) {
            connEtoC3(e);
        }
        if (e.getSource() == getDeadbandCheckBox()) {
            connEtoC5(e);
        }
    }

    @Override
    public void setValue(Object val) {

        PointBase pointBase = (PointBase)val;

        Integer pointOffset = pointBase.getPoint().getPointOffset();
        if (pointOffset != null && pointOffset.intValue() > 0) {
            getPhysicalPointOffsetCheckBox().setSelected(true);
            getPointOffsetSpinner().setValue( pointOffset );
        } 
        else {
            getPhysicalPointOffsetCheckBox().setSelected(false);
            getPointOffsetSpinner().setValue(new Integer(0));
        }

        getUsedPointOffsetLabel().setText("");

        List<LitePoint> points = YukonSpringHook.getBean(PointDao.class).getLitePointsByPaObjectId(pointBase.getPoint() .getPaoID());
        usedPointOffsetsVector = new Vector<LitePoint>(points.size());
        for (LitePoint currPoint : points) {
            if (pointBase.getPoint().getPointID() != currPoint.getPointID() && 
                    pointBase.getPoint().getPointTypeEnum() == currPoint.getPointTypeEnum()) {
                usedPointOffsetsVector.add(currPoint);
            }
        }

        if (pointBase.getPoint().getPointTypeEnum() == PointType.System) {
            getDeadbandPanel().setVisible(false);
            getRawValuePanel().setVisible(false);
            getControlSettingsPanel().setVisible(false);
            return;
        }
        
        AnalogPoint point = (AnalogPoint) pointBase;
        if (point.getPointAnalog().getMultiplier() != null) {
            getMultiplierTextField().setText(point.getPointAnalog().getMultiplier().toString());
        }
        if (point.getPointAnalog().getDataOffset() != null) {
            getDataOffsetTextField().setText(point.getPointAnalog().getDataOffset().toString());
        }

        getControlTypeComboBox().setSelectedItem(point.getPointAnalogControl().getControlType());
        getControlInhibitCheckBox().setSelected(point.getPointAnalogControl().isControlInhibited());

        if (point.getPointAnalogControl().hasControl()) {
            getControlPointOffsetSpinner().setValue(point.getPointAnalogControl().getControlOffset());
        } else {
            getControlPointOffsetSpinner().setValue(0);
        }

        Double deadband = point.getPointAnalog().getDeadband();
        if (deadband != null) {
            getDeadbandCheckBox().setSelected(deadband.intValue() > -1);
            getDeadbandSpinner().setValue(new Integer(deadband.intValue()));

            // Only display the Deadband panel for RTU Welco device types.
            LiteYukonPAObject pao = YukonSpringHook.getBean(PaoDao.class).getLiteYukonPAO(point.getPoint().getPaoID());
            if (pao.getPaoType() != PaoType.RTUWELCO) {
                getDeadbandPanel().setVisible(false);
            }
        }
    }

    @Override
    public void valueChanged(JCValueEvent arg1) {
        if (arg1.getSource() == getDeadbandSpinner()) {
            this.fireInputUpdate();
        }

        if (arg1.getSource() == getPointOffsetSpinner()) {
            this.fireInputUpdate();
        }

        if (arg1.getSource() == getControlPointOffsetSpinner()) {
            this.fireInputUpdate();
        }
    }

    @Override
    public void valueChanging(JCValueEvent arg1) {
    }
}