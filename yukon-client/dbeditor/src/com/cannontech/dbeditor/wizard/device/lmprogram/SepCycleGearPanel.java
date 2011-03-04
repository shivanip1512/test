package com.cannontech.dbeditor.wizard.device.lmprogram;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import com.cannontech.common.gui.unchanging.DoubleRangeDocument;
import com.cannontech.common.util.StringUtils;
import com.cannontech.database.data.device.lm.SepCycleGear;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.klg.jclass.field.DataProperties;
import com.klg.jclass.field.JCInvalidInfo;
import com.klg.jclass.field.JCSpinField;
import com.klg.jclass.field.validate.JCIntegerValidator;
import com.klg.jclass.util.value.MutableValueModel;

public class SepCycleGearPanel extends GenericGearPanel {

    private static final long serialVersionUID = 337578549868176151L;
    private JComboBox ivjJComboBoxHowToStop = null;
    private JComboBox ivjJComboBoxWhenChange = null;
    private JCSpinField ivjJCSpinFieldChangeDuration = null;
    private JCSpinField ivjJCSpinFieldChangePriority = null;
    private JCSpinField ivjJCSpinFieldChangeTriggerNumber = null;
    private JCSpinField ivjJCSpinFieldControlPercent = null;
    private JCSpinField ivjJCSpinFieldPercentReduction = null;
    private JLabel ivjJLabelChangeDuration = null;
    private JLabel ivjJLabelChangePriority = null;
    private JLabel ivjJLabelChangeTriggerNumber = null;
    private JLabel ivjJLabelChangeTriggerOffset = null;
    private JLabel ivjJLabelControlPercent = null;
    private JLabel ivjJLabelHowToStop = null;
    private JLabel ivjJLabelMinutesChDur = null;
    private JLabel ivjJLabelPercentReduction = null;
    private JLabel ivjJLabelWhenChange = null;
    private JPanel ivjJPanelChangeMethod = null;
    private JTextField ivjJTextFieldChangeTriggerOffset = null;
    private JCheckBox checkBoxRampIn = null;
    private JCheckBox checkBoxRampOut = null;
    private JCheckBox checkBoxTrueCycle = null;
    private JTextField jTextFieldKWReduction = null;

    public SepCycleGearPanel() {
        super();
        initialize();
    }

    public void actionPerformed(java.awt.event.ActionEvent e) {
        if (e.getSource() == getJComboBoxWhenChange()) {
            connEtoC1(e);
        } else {
            this.fireInputUpdate();
        }
    }

    public void caretUpdate(javax.swing.event.CaretEvent e) {
        fireInputUpdate();
    }

    private void connEtoC1(java.awt.event.ActionEvent arg1) {
        try {
            this.jComboBoxWhenChange_ActionPerformed(arg1);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    private javax.swing.JComboBox getJComboBoxHowToStop() {
        if (ivjJComboBoxHowToStop == null) {
            try {
                ivjJComboBoxHowToStop = new javax.swing.JComboBox();
                ivjJComboBoxHowToStop.setName("JComboBoxHowToStop");
                ivjJComboBoxHowToStop.setPreferredSize(new Dimension(75, 23));
                ivjJComboBoxHowToStop.setAlignmentY(TOP_ALIGNMENT);
                // user code begin {1}
                ivjJComboBoxHowToStop.addItem(StringUtils.addCharBetweenWords(' ', LMProgramDirectGear.STOP_TIME_IN));
                ivjJComboBoxHowToStop.addItem(StringUtils.addCharBetweenWords(' ', LMProgramDirectGear.STOP_RESTORE));
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJComboBoxHowToStop;
    }

    private JTextField getJTextFieldKWReduction() {
        if (jTextFieldKWReduction == null) {
            try {
                jTextFieldKWReduction = new javax.swing.JTextField();
                jTextFieldKWReduction.setName("JTextFieldKWReduction");
                jTextFieldKWReduction.setPreferredSize(new Dimension(70, 22));
                jTextFieldKWReduction.setAlignmentX(LEFT_ALIGNMENT);
                jTextFieldKWReduction.setAlignmentY(TOP_ALIGNMENT);
                jTextFieldKWReduction.setDocument(new DoubleRangeDocument(0.0, 99999.999, 3));
                jTextFieldKWReduction.setVisible(false);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return jTextFieldKWReduction;
    }

    private javax.swing.JComboBox getJComboBoxWhenChange() {
        if (ivjJComboBoxWhenChange == null) {
            try {
                ivjJComboBoxWhenChange = new javax.swing.JComboBox();
                ivjJComboBoxWhenChange.setName("JComboBoxWhenChange");
                ivjJComboBoxWhenChange.setPreferredSize(new Dimension(205, 23));
                ivjJComboBoxWhenChange.setAlignmentY(TOP_ALIGNMENT);
                // user code begin {1}
                ivjJComboBoxWhenChange.addItem("Manually Only");
                ivjJComboBoxWhenChange.addItem("After a Duration");
                ivjJComboBoxWhenChange.addItem("Priority Change");
                ivjJComboBoxWhenChange.addItem("Above Trigger");
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
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
                ivjJCSpinFieldChangeDuration.setPreferredSize(new Dimension(35, 20));
                ivjJCSpinFieldChangeDuration.setAlignmentY(TOP_ALIGNMENT);
                ivjJCSpinFieldChangeDuration.setMaximumSize(new Dimension(40, 20));
                // user code begin {1}
                ivjJCSpinFieldChangeDuration.setDataProperties(new DataProperties(new JCIntegerValidator(null, new Integer(0),
                                                                                                         new Integer(99999), null, true,
                                                                                                         null, new Integer(1),
                                                                                                         "#,##0.###;-#,##0.###", false,
                                                                                                         false, false, null, new Integer(3)),
                                                                                  new MutableValueModel(java.lang.Integer.class,
                                                                                                        new Integer(0)),
                                                                                  new JCInvalidInfo(true, 2, new Color(0, 0, 0, 255),
                                                                                                    new Color(255, 255, 255, 255))));
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
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
                ivjJCSpinFieldChangePriority.setPreferredSize(new Dimension(30, 20));
                ivjJCSpinFieldChangePriority.setAlignmentY(TOP_ALIGNMENT);
                ivjJCSpinFieldChangePriority.setMaximumSize(new Dimension(40, 30));
                // user code begin {1}
                ivjJCSpinFieldChangePriority.setDataProperties(new DataProperties(new JCIntegerValidator(null, new Integer(0),
                                                                                                         new Integer(9999), null, true,
                                                                                                         null, new Integer(1),
                                                                                                         "#,##0.###;-#,##0.###", false,
                                                                                                         false, false, null, new Integer(0)),
                                                                                  new MutableValueModel(java.lang.Integer.class,
                                                                                                        new Integer(0)),
                                                                                  new JCInvalidInfo(true, 2, new Color(0, 0, 0, 255),
                                                                                                    new Color(255, 255, 255, 255))));
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
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
                ivjJCSpinFieldChangeTriggerNumber.setPreferredSize(new Dimension(35, 20));
                ivjJCSpinFieldChangeTriggerNumber.setAlignmentY(TOP_ALIGNMENT);
                ivjJCSpinFieldChangeTriggerNumber.setMaximumSize(new Dimension(40, 20));
                // user code begin {1}
                ivjJCSpinFieldChangeTriggerNumber.setDataProperties(new DataProperties(new JCIntegerValidator(null, new Integer(1),
                                                                                                              new Integer(99999), null,
                                                                                                              true, null, new Integer(1),
                                                                                                              "#,##0.###;-#,##0.###",
                                                                                                              false, false, false, null,
                                                                                                              new Integer(1)),
                                                                                       new MutableValueModel(java.lang.Integer.class,
                                                                                                             new Integer(0)),
                                                                                       new JCInvalidInfo(true, 2,
                                                                                                         new Color(0, 0, 0, 255),
                                                                                                         new Color(255, 255, 255,
                                                                                                                            255))));
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJCSpinFieldChangeTriggerNumber;
    }

    private com.klg.jclass.field.JCSpinField getJCSpinFieldControlPercent() {
        if (ivjJCSpinFieldControlPercent == null) {
            try {
                ivjJCSpinFieldControlPercent = new com.klg.jclass.field.JCSpinField();
                ivjJCSpinFieldControlPercent.setName("JCSpinFieldControlPercent");
                ivjJCSpinFieldControlPercent.setPreferredSize(new Dimension(48, 20));
                ivjJCSpinFieldControlPercent.setAlignmentX(LEFT_ALIGNMENT);
                ivjJCSpinFieldControlPercent.setAlignmentY(TOP_ALIGNMENT);
                ivjJCSpinFieldControlPercent.setMaximumSize(new Dimension(50, 20));
                ivjJCSpinFieldControlPercent.setMinimumSize(new Dimension(48, 20));
                // user code begin {1}
                ivjJCSpinFieldControlPercent.setDataProperties(new DataProperties(new JCIntegerValidator(null, new Integer(5),
                                                                                                         new Integer(100), null, true,
                                                                                                         null, new Integer(1),
                                                                                                         "#,##0.###;-#,##0.###", false,
                                                                                                         false, false, null,
                                                                                                         new Integer(50)),
                                                                                  new MutableValueModel(java.lang.Integer.class,
                                                                                                        new Integer(0)),
                                                                                  new JCInvalidInfo(true, 2, new Color(0, 0, 0,
                                                                                                                                255),
                                                                                                    new Color(255, 255, 255, 255))));

                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJCSpinFieldControlPercent;
    }

    private com.klg.jclass.field.JCSpinField getJCSpinFieldPercentReduction() {
        if (ivjJCSpinFieldPercentReduction == null) {
            try {
                ivjJCSpinFieldPercentReduction = new com.klg.jclass.field.JCSpinField();
                ivjJCSpinFieldPercentReduction.setName("JCSpinFieldPercentReduction");
                ivjJCSpinFieldPercentReduction.setPreferredSize(new Dimension(49, 20));
                ivjJCSpinFieldPercentReduction.setMaximumSize(new Dimension(50, 60));
                ivjJCSpinFieldPercentReduction.setMinimumSize(new Dimension(40, 50));
                // user code begin {1}
                ivjJCSpinFieldPercentReduction.setDataProperties(new DataProperties(new JCIntegerValidator(null, new Integer(0),
                                                                                                           new Integer(100), null, true,
                                                                                                           null, new Integer(1),
                                                                                                           "#,##0.###;-#,##0.###", false,
                                                                                                           false, false, null,
                                                                                                           new Integer(100)),
                                                                                    new MutableValueModel(java.lang.Integer.class,
                                                                                                          new Integer(0)),
                                                                                    new JCInvalidInfo(true,
                                                                                                      2,
                                                                                                      new Color(0, 0, 0, 255),
                                                                                                      new Color(255, 255, 255, 255))));

                ivjJCSpinFieldPercentReduction.setValue(new Integer(100));

                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJCSpinFieldPercentReduction;
    }

    private javax.swing.JLabel getJLabelChangeDuration() {
        if (ivjJLabelChangeDuration == null) {
            try {
                ivjJLabelChangeDuration = new javax.swing.JLabel();
                ivjJLabelChangeDuration.setName("JLabelChangeDuration");
                ivjJLabelChangeDuration.setAlignmentY(TOP_ALIGNMENT);
                ivjJLabelChangeDuration.setText("Change Duration:");
                ivjJLabelChangeDuration.setMaximumSize(new Dimension(103, 14));
                ivjJLabelChangeDuration.setPreferredSize(new Dimension(103, 14));
                ivjJLabelChangeDuration.setFont(new Font("dialog", 0, 12));
                ivjJLabelChangeDuration.setMinimumSize(new Dimension(103, 14));
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJLabelChangeDuration;
    }

    private javax.swing.JLabel getJLabelChangePriority() {
        if (ivjJLabelChangePriority == null) {
            try {
                ivjJLabelChangePriority = new javax.swing.JLabel();
                ivjJLabelChangePriority.setName("JLabelChangePriority");
                ivjJLabelChangePriority.setAlignmentY(TOP_ALIGNMENT);
                ivjJLabelChangePriority.setText("Change Priority:");
                ivjJLabelChangePriority.setMaximumSize(new Dimension(103, 14));
                ivjJLabelChangePriority.setPreferredSize(new Dimension(103, 14));
                ivjJLabelChangePriority.setFont(new Font("dialog", 0, 12));
                ivjJLabelChangePriority.setMinimumSize(new Dimension(103, 14));
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
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
                ivjJLabelChangeTriggerNumber.setAlignmentY(TOP_ALIGNMENT);
                ivjJLabelChangeTriggerNumber.setText("Trigger Number:");
                ivjJLabelChangeTriggerNumber.setMaximumSize(new Dimension(143, 14));
                ivjJLabelChangeTriggerNumber.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
                ivjJLabelChangeTriggerNumber.setPreferredSize(new Dimension(143, 14));
                ivjJLabelChangeTriggerNumber.setFont(new Font("dialog", 0, 12));
                ivjJLabelChangeTriggerNumber.setMinimumSize(new Dimension(143, 14));
                ivjJLabelChangeTriggerNumber.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
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
                ivjJLabelChangeTriggerOffset.setAlignmentY(TOP_ALIGNMENT);
                ivjJLabelChangeTriggerOffset.setText("Trigger Offset:");
                ivjJLabelChangeTriggerOffset.setMaximumSize(new Dimension(143, 14));
                ivjJLabelChangeTriggerOffset.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
                ivjJLabelChangeTriggerOffset.setPreferredSize(new Dimension(143, 14));
                ivjJLabelChangeTriggerOffset.setFont(new Font("dialog", 0, 12));
                ivjJLabelChangeTriggerOffset.setMinimumSize(new Dimension(143, 14));
                ivjJLabelChangeTriggerOffset.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJLabelChangeTriggerOffset;
    }

    private javax.swing.JLabel getJLabelControlPercent() {
        if (ivjJLabelControlPercent == null) {
            try {
                ivjJLabelControlPercent = new javax.swing.JLabel();
                ivjJLabelControlPercent.setName("JLabelControlPercent");
                ivjJLabelControlPercent.setAlignmentY(TOP_ALIGNMENT);
                ivjJLabelControlPercent.setText("Control Percent:");
                ivjJLabelControlPercent.setMaximumSize(new Dimension(112, 14));
                ivjJLabelControlPercent.setPreferredSize(new Dimension(112, 14));
                ivjJLabelControlPercent.setFont(new Font("dialog", 0, 12));
                ivjJLabelControlPercent.setAlignmentX(LEFT_ALIGNMENT);
                ivjJLabelControlPercent.setMinimumSize(new Dimension(112, 14));
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJLabelControlPercent;
    }

    private javax.swing.JLabel getJLabelHowToStop() {
        if (ivjJLabelHowToStop == null) {
            try {
                ivjJLabelHowToStop = new javax.swing.JLabel();
                ivjJLabelHowToStop.setName("JLabelHowToStop");
                ivjJLabelHowToStop.setFont(new Font("dialog", 0, 12));
                ivjJLabelHowToStop.setAlignmentY(TOP_ALIGNMENT);
                ivjJLabelHowToStop.setText("How to Stop Control:");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJLabelHowToStop;
    }

    private javax.swing.JLabel getJLabelMinutesChDur() {
        if (ivjJLabelMinutesChDur == null) {
            try {
                ivjJLabelMinutesChDur = new javax.swing.JLabel();
                ivjJLabelMinutesChDur.setName("JLabelMinutesChDur");
                ivjJLabelMinutesChDur.setFont(new Font("dialog", 0, 12));
                ivjJLabelMinutesChDur.setAlignmentY(TOP_ALIGNMENT);
                ivjJLabelMinutesChDur.setText("(min.)");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJLabelMinutesChDur;
    }

    private javax.swing.JLabel getJLabelPercentReduction() {
        if (ivjJLabelPercentReduction == null) {
            try {
                ivjJLabelPercentReduction = new javax.swing.JLabel();
                ivjJLabelPercentReduction.setName("JLabelPercentReduction");
                ivjJLabelPercentReduction.setAlignmentY(TOP_ALIGNMENT);
                ivjJLabelPercentReduction.setText("Group Capacity Reduction %:");
                ivjJLabelPercentReduction.setMaximumSize(new Dimension(160, 14));
                ivjJLabelPercentReduction.setPreferredSize(new Dimension(160, 14));
                ivjJLabelPercentReduction.setFont(new Font("dialog", 0, 12));
                ivjJLabelPercentReduction.setMinimumSize(new Dimension(160, 14));
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJLabelPercentReduction;
    }

    private javax.swing.JLabel getJLabelWhenChange() {
        if (ivjJLabelWhenChange == null) {
            try {
                ivjJLabelWhenChange = new javax.swing.JLabel();
                ivjJLabelWhenChange.setName("JLabelWhenChange");
                ivjJLabelWhenChange.setFont(new Font("dialog", 0, 12));
                ivjJLabelWhenChange.setAlignmentY(TOP_ALIGNMENT);
                ivjJLabelWhenChange.setText("When to Change:");
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJLabelWhenChange;
    }

    private javax.swing.JPanel getJPanelChangeMethod() {
        if (ivjJPanelChangeMethod == null) {
            try {
                ivjJPanelChangeMethod = new javax.swing.JPanel();
                ivjJPanelChangeMethod.setName("JPanelChangeMethod");
                ivjJPanelChangeMethod.setLayout(new GridBagLayout());
                ivjJPanelChangeMethod.setAlignmentY(TOP_ALIGNMENT);
                ivjJPanelChangeMethod.setMaximumSize(new Dimension(300, 75));
                ivjJPanelChangeMethod.setPreferredSize(new Dimension(300, 75));
                ivjJPanelChangeMethod.setFont(new Font("dialog", 0, 12));
                ivjJPanelChangeMethod.setAlignmentX(LEFT_ALIGNMENT);

                GridBagConstraints constraintsJLabelChangeDuration = new GridBagConstraints();
                constraintsJLabelChangeDuration.gridx = 1;
                constraintsJLabelChangeDuration.gridy = 2;
                constraintsJLabelChangeDuration.anchor = GridBagConstraints.NORTHWEST;
                constraintsJLabelChangeDuration.ipadx = -5;
                constraintsJLabelChangeDuration.ipady = 6;
                constraintsJLabelChangeDuration.insets = new Insets(1, 5, 3, 5);
                getJPanelChangeMethod().add(getJLabelChangeDuration(), constraintsJLabelChangeDuration);

                GridBagConstraints constraintsJCSpinFieldChangeDuration = new GridBagConstraints();
                constraintsJCSpinFieldChangeDuration.gridx = 2;
                constraintsJCSpinFieldChangeDuration.gridy = 2;
                constraintsJCSpinFieldChangeDuration.anchor = GridBagConstraints.NORTHWEST;
                constraintsJCSpinFieldChangeDuration.ipadx = 34;
                constraintsJCSpinFieldChangeDuration.ipady = 19;
                constraintsJCSpinFieldChangeDuration.insets = new Insets(1, 5, 3, 4);
                getJPanelChangeMethod().add(getJCSpinFieldChangeDuration(), constraintsJCSpinFieldChangeDuration);

                GridBagConstraints constraintsJLabelMinutesChDur = new GridBagConstraints();
                constraintsJLabelMinutesChDur.gridx = 3;
                constraintsJLabelMinutesChDur.gridy = 2;
                constraintsJLabelMinutesChDur.anchor = GridBagConstraints.NORTHWEST;
                constraintsJLabelMinutesChDur.ipadx = 5;
                constraintsJLabelMinutesChDur.ipady = -2;
                constraintsJLabelMinutesChDur.insets = new Insets(5, 5, 5, 3);
                getJPanelChangeMethod().add(getJLabelMinutesChDur(), constraintsJLabelMinutesChDur);

                GridBagConstraints constraintsJLabelChangePriority = new GridBagConstraints();
                constraintsJLabelChangePriority.gridx = 4;
                constraintsJLabelChangePriority.gridy = 2;
                constraintsJLabelChangePriority.anchor = GridBagConstraints.NORTHWEST;
                constraintsJLabelChangePriority.ipadx = -13;
                constraintsJLabelChangePriority.ipady = 6;
                constraintsJLabelChangePriority.insets = new Insets(1, 3, 3, 3);
                getJPanelChangeMethod().add(getJLabelChangePriority(), constraintsJLabelChangePriority);

                GridBagConstraints constraintsJCSpinFieldChangePriority = new GridBagConstraints();
                constraintsJCSpinFieldChangePriority.gridx = 5;
                constraintsJCSpinFieldChangePriority.gridy = 2;
                constraintsJCSpinFieldChangePriority.anchor = GridBagConstraints.NORTHWEST;
                constraintsJCSpinFieldChangePriority.ipadx = 29;
                constraintsJCSpinFieldChangePriority.ipady = 19;
                constraintsJCSpinFieldChangePriority.insets = new Insets(1, 4, 3, 8);
                getJPanelChangeMethod().add(getJCSpinFieldChangePriority(), constraintsJCSpinFieldChangePriority);

                GridBagConstraints constraintsJLabelChangeTriggerNumber = new GridBagConstraints();
                constraintsJLabelChangeTriggerNumber.gridx = 1;
                constraintsJLabelChangeTriggerNumber.gridy = 3;
                constraintsJLabelChangeTriggerNumber.anchor = GridBagConstraints.NORTHWEST;
                constraintsJLabelChangeTriggerNumber.ipadx = -45;
                constraintsJLabelChangeTriggerNumber.ipady = 6;
                constraintsJLabelChangeTriggerNumber.insets = new Insets(4, 5, 21, 5);
                getJPanelChangeMethod().add(getJLabelChangeTriggerNumber(), constraintsJLabelChangeTriggerNumber);

                GridBagConstraints constraintsJLabelChangeTriggerOffset = new GridBagConstraints();
                constraintsJLabelChangeTriggerOffset.gridx = 4;
                constraintsJLabelChangeTriggerOffset.gridy = 3;
                constraintsJLabelChangeTriggerOffset.anchor = GridBagConstraints.NORTHWEST;
                constraintsJLabelChangeTriggerOffset.ipadx = -63;
                constraintsJLabelChangeTriggerOffset.insets = new Insets(8, 3, 23, 13);
                getJPanelChangeMethod().add(getJLabelChangeTriggerOffset(), constraintsJLabelChangeTriggerOffset);

                GridBagConstraints constraintsJTextFieldChangeTriggerOffset = new GridBagConstraints();
                constraintsJTextFieldChangeTriggerOffset.gridx = 5;
                constraintsJTextFieldChangeTriggerOffset.gridy = 3;
                constraintsJTextFieldChangeTriggerOffset.anchor = GridBagConstraints.NORTHWEST;
                constraintsJTextFieldChangeTriggerOffset.weightx = 1.0;
                constraintsJTextFieldChangeTriggerOffset.ipadx = 26;
                constraintsJTextFieldChangeTriggerOffset.insets = new Insets(4, 4, 21, 8);
                getJPanelChangeMethod().add(getJTextFieldChangeTriggerOffset(), constraintsJTextFieldChangeTriggerOffset);

                GridBagConstraints constraintsJCSpinFieldChangeTriggerNumber = new GridBagConstraints();
                constraintsJCSpinFieldChangeTriggerNumber.gridx = 2;
                constraintsJCSpinFieldChangeTriggerNumber.gridy = 3;
                constraintsJCSpinFieldChangeTriggerNumber.anchor = GridBagConstraints.NORTHWEST;
                constraintsJCSpinFieldChangeTriggerNumber.ipadx = 34;
                constraintsJCSpinFieldChangeTriggerNumber.ipady = 19;
                constraintsJCSpinFieldChangeTriggerNumber.insets = new Insets(4, 5, 21, 4);
                getJPanelChangeMethod().add(getJCSpinFieldChangeTriggerNumber(), constraintsJCSpinFieldChangeTriggerNumber);

                GridBagConstraints constraintsJLabelWhenChange = new GridBagConstraints();
                constraintsJLabelWhenChange.gridx = 1;
                constraintsJLabelWhenChange.gridy = 1;
                constraintsJLabelWhenChange.anchor = GridBagConstraints.NORTHWEST;
                constraintsJLabelWhenChange.ipadx = 3;
                constraintsJLabelWhenChange.ipady = 4;
                constraintsJLabelWhenChange.insets = new Insets(4, 5, 4, 5);
                getJPanelChangeMethod().add(getJLabelWhenChange(), constraintsJLabelWhenChange);

                GridBagConstraints constraintsJComboBoxWhenChange = new GridBagConstraints();
                constraintsJComboBoxWhenChange.gridx = 2;
                constraintsJComboBoxWhenChange.gridy = 1;
                constraintsJComboBoxWhenChange.gridwidth = 4;
                constraintsJComboBoxWhenChange.anchor = GridBagConstraints.NORTHWEST;
                constraintsJComboBoxWhenChange.weightx = 1.0;
                constraintsJComboBoxWhenChange.ipadx = 79;
                constraintsJComboBoxWhenChange.insets = new Insets(4, 5, 1, 17);
                getJPanelChangeMethod().add(getJComboBoxWhenChange(), constraintsJComboBoxWhenChange);
                ivjJPanelChangeMethod.setBorder(new EtchedBorder());
                // user code begin {1}
                jComboBoxWhenChange_ActionPerformed(null);
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJPanelChangeMethod;
    }

    private javax.swing.JTextField getJTextFieldChangeTriggerOffset() {
        if (ivjJTextFieldChangeTriggerOffset == null) {
            try {
                ivjJTextFieldChangeTriggerOffset = new javax.swing.JTextField();
                ivjJTextFieldChangeTriggerOffset.setName("JTextFieldChangeTriggerOffset");
                ivjJTextFieldChangeTriggerOffset.setPreferredSize(new Dimension(30, 20));
                ivjJTextFieldChangeTriggerOffset.setAlignmentY(TOP_ALIGNMENT);
                ivjJTextFieldChangeTriggerOffset.setMaximumSize(new Dimension(40, 20));
                ivjJTextFieldChangeTriggerOffset.setDocument(new com.cannontech.common.gui.unchanging.DoubleRangeDocument(-99999.9999,
                                                                                                                          99999.9999, 4));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJTextFieldChangeTriggerOffset;
    }

    public Object getValue(Object o) {
        SepCycleGear gear = null;

        gear = (SepCycleGear) o;

        if (getJComboBoxHowToStop().getSelectedItem() != null) {
            gear.setMethodStopType(com.cannontech.common.util.StringUtils.removeChars(' ', getJComboBoxHowToStop().getSelectedItem()
                .toString()));
        }

        gear.setPercentReduction(new Integer(((Number) getJCSpinFieldPercentReduction().getValue()).intValue()));

        gear.setChangeCondition(getChangeCondition(getJComboBoxWhenChange().getSelectedItem().toString()));

        gear.setChangeDuration(new Integer(((Number) getJCSpinFieldChangeDuration().getValue()).intValue() * 60));
        gear.setChangePriority(new Integer(((Number) getJCSpinFieldChangePriority().getValue()).intValue()));
        gear.setChangeTriggerNumber(new Integer(((Number) getJCSpinFieldChangeTriggerNumber().getValue()).intValue()));

        if (getJTextFieldChangeTriggerOffset().getText() == null || getJTextFieldChangeTriggerOffset().getText().length() <= 0)
            gear.setChangeTriggerOffset(new Double(0.0));
        else
            gear.setChangeTriggerOffset(Double.valueOf(getJTextFieldChangeTriggerOffset().getText()));

        gear.setControlPercent(new Integer(((Number) getJCSpinFieldControlPercent().getValue()).intValue()));

        if (getCheckBoxRampIn().isSelected()) {
            gear.setFrontRampEnabled(true);
        } else {
            gear.setFrontRampEnabled(false);
        }

        if (getCheckBoxRampOut().isSelected()) {
            gear.setBackRampEnabled(true);
        } else {
            gear.setBackRampEnabled(false);
        }

        if (getCheckBoxTrueCycle().isSelected()) {
            gear.setTrueCycleEnabled(true);
        } else {
            gear.setTrueCycleEnabled(false);
        }

        return gear;
    }

    private void handleException(java.lang.Throwable exception) {
        System.out.print(exception.getMessage());
        /* Uncomment the following lines to print uncaught exceptions to stdout */
        System.out.println("--------- UNCAUGHT EXCEPTION ---------");
        exception.printStackTrace(System.out);
    }

    private void initConnections() throws java.lang.Exception {
        getJCSpinFieldChangeDuration().addValueListener(this);
        getJCSpinFieldChangePriority().addValueListener(this);
        getJCSpinFieldChangeTriggerNumber().addValueListener(this);
        getJCSpinFieldControlPercent().addValueListener(this);
        getJCSpinFieldPercentReduction().addValueListener(this);
        getJComboBoxWhenChange().addActionListener(this);
        getJComboBoxHowToStop().addActionListener(this);
        getJTextFieldChangeTriggerOffset().addCaretListener(this);
        getCheckBoxRampIn().addActionListener(this);
        getCheckBoxRampOut().addActionListener(this);
        getCheckBoxTrueCycle().addActionListener(this);
        getJTextFieldKWReduction().addCaretListener(this);
    }

    private void initialize() {
        try {
            setName("SepCycleGearPanel");

            GridBagConstraints constraintJLabelControlPercent = new GridBagConstraints();
            GridBagConstraints constraintJCSpinFieldControlPercent = new GridBagConstraints();
            GridBagConstraints constraintJPanelChangeMethod = new GridBagConstraints();
            GridBagConstraints constraintJLabelPercentReduction = new GridBagConstraints();
            GridBagConstraints constraintJLabelHowToStop = new GridBagConstraints();
            GridBagConstraints constraintJCSpinFieldPercentReduction = new GridBagConstraints();
            GridBagConstraints constraintJComboBoxHowToStop = new GridBagConstraints();
            GridBagConstraints constraintJCheckBoxRampIn = new GridBagConstraints();
            GridBagConstraints constraintJCheckBoxRampOut = new GridBagConstraints();
            GridBagConstraints constraintJCheckBoxTrueCycle = new GridBagConstraints();

            constraintJCheckBoxRampIn.insets = new Insets(0, 0, 5, 0);
            constraintJCheckBoxRampIn.ipady = -3;
            constraintJCheckBoxRampIn.ipadx = 200;
            constraintJCheckBoxRampIn.gridwidth = 3;
            constraintJCheckBoxRampIn.anchor = GridBagConstraints.WEST;
            constraintJCheckBoxRampIn.gridy = 1;
            constraintJCheckBoxRampIn.gridx = 1;

            constraintJCheckBoxRampOut.insets = new Insets(0, 0, 5, 0);
            constraintJCheckBoxRampOut.ipady = -3;
            constraintJCheckBoxRampOut.ipadx = 200;
            constraintJCheckBoxRampOut.gridwidth = 3;
            constraintJCheckBoxRampOut.anchor = GridBagConstraints.WEST;
            constraintJCheckBoxRampOut.gridy = 2;
            constraintJCheckBoxRampOut.gridx = 1;

            constraintJCheckBoxTrueCycle.insets = new Insets(0, 0, 5, 0);
            constraintJCheckBoxTrueCycle.ipady = -3;
            constraintJCheckBoxTrueCycle.ipadx = 200;
            constraintJCheckBoxTrueCycle.gridwidth = 3;
            constraintJCheckBoxTrueCycle.anchor = GridBagConstraints.WEST;
            constraintJCheckBoxTrueCycle.gridy = 3;
            constraintJCheckBoxTrueCycle.gridx = 1;

            constraintJLabelControlPercent.insets = new Insets(0, 0, 5, 5);
            constraintJLabelControlPercent.anchor = GridBagConstraints.WEST;
            constraintJLabelControlPercent.gridy = 4;
            constraintJLabelControlPercent.gridx = 1;

            constraintJCSpinFieldControlPercent.insets = new Insets(0, 0, 5, 5);
            constraintJCSpinFieldControlPercent.anchor = GridBagConstraints.WEST;
            constraintJCSpinFieldControlPercent.gridy = 4;
            constraintJCSpinFieldControlPercent.gridx = 2;

            constraintJLabelHowToStop.insets = new Insets(0, 0, 5, 5);
            constraintJLabelHowToStop.anchor = GridBagConstraints.WEST;
            constraintJLabelHowToStop.gridy = 8;
            constraintJLabelHowToStop.gridx = 1;

            constraintJComboBoxHowToStop.insets = new Insets(0, 0, 5, 5);
            constraintJComboBoxHowToStop.anchor = GridBagConstraints.WEST;
            constraintJComboBoxHowToStop.gridy = 8;
            constraintJComboBoxHowToStop.gridx = 2;

            constraintJLabelPercentReduction.insets = new Insets(0, 0, 5, 5);
            constraintJLabelPercentReduction.anchor = GridBagConstraints.WEST;
            constraintJLabelPercentReduction.gridy = 9;
            constraintJLabelPercentReduction.gridx = 1;

            constraintJCSpinFieldPercentReduction.insets = new Insets(0, 0, 5, 5);
            constraintJCSpinFieldPercentReduction.anchor = GridBagConstraints.WEST;
            constraintJCSpinFieldPercentReduction.gridy = 9;
            constraintJCSpinFieldPercentReduction.gridx = 2;

            constraintJPanelChangeMethod.insets = new Insets(0, 0, 5, 5);
            constraintJPanelChangeMethod.anchor = GridBagConstraints.WEST;
            constraintJPanelChangeMethod.gridwidth = 3;
            constraintJPanelChangeMethod.gridy = 11;
            constraintJPanelChangeMethod.gridx = 1;

            setLayout(new GridBagLayout());
            this.add(getJLabelControlPercent(), constraintJLabelControlPercent);
            this.add(getJCSpinFieldControlPercent(), constraintJCSpinFieldControlPercent);
            this.add(getJPanelChangeMethod(), constraintJPanelChangeMethod);
            this.add(getJLabelPercentReduction(), constraintJLabelPercentReduction);
            this.add(getJLabelHowToStop(), constraintJLabelHowToStop);
            this.add(getJComboBoxHowToStop(), constraintJComboBoxHowToStop);
            this.add(getJCSpinFieldPercentReduction(), constraintJCSpinFieldPercentReduction);
            this.add(getCheckBoxRampIn(), constraintJCheckBoxRampIn);
            this.add(getCheckBoxRampOut(), constraintJCheckBoxRampOut);
            this.add(getCheckBoxTrueCycle(), constraintJCheckBoxTrueCycle);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        getJComboBoxWhenChange().setSelectedItem(LMProgramDirectGear.CHANGE_NONE);
        getJComboBoxHowToStop().removeItem(StringUtils.addCharBetweenWords(' ', LMProgramDirectGear.STOP_TIME_IN));
        getJComboBoxHowToStop().addItem(StringUtils.addCharBetweenWords(' ', LMProgramDirectGear.STOP_STOP_CYCLE));
        getJComboBoxHowToStop().setSelectedItem(StringUtils.addCharBetweenWords(' ', LMProgramDirectGear.STOP_STOP_CYCLE));
        try {
            initConnections();
        } catch (Exception e) {}
    }

    public void jComboBoxWhenChange_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
        getJLabelChangeDuration().setVisible(false);
        getJCSpinFieldChangeDuration().setVisible(false);
        getJLabelMinutesChDur().setVisible(false);

        getJLabelChangePriority().setVisible(false);
        getJCSpinFieldChangePriority().setVisible(false);

        getJLabelChangeTriggerNumber().setVisible(false);
        getJCSpinFieldChangeTriggerNumber().setVisible(false);

        getJLabelChangeTriggerOffset().setVisible(false);
        getJTextFieldChangeTriggerOffset().setVisible(false);

        if (getJComboBoxWhenChange().getSelectedItem().toString().equalsIgnoreCase(LMProgramDirectGear.CHANGE_NONE)
            || getJComboBoxWhenChange().getSelectedItem().toString().equalsIgnoreCase("Manually Only")) {
            // None
            return;
        } else if (getJComboBoxWhenChange().getSelectedItem().toString().equalsIgnoreCase(LMProgramDirectGear.CHANGE_DURATION)
                   || getJComboBoxWhenChange().getSelectedItem().toString().equalsIgnoreCase("After a Duration")) {
            // Duration
            getJLabelChangeDuration().setVisible(true);
            getJCSpinFieldChangeDuration().setVisible(true);
            getJLabelMinutesChDur().setVisible(true);
        } else if (getJComboBoxWhenChange().getSelectedItem().toString().equalsIgnoreCase(LMProgramDirectGear.CHANGE_PRIORITY)
                   || getJComboBoxWhenChange().getSelectedItem().toString().equalsIgnoreCase("Priority Change")) {
            // Priority
            getJLabelChangePriority().setVisible(true);
            getJCSpinFieldChangePriority().setVisible(true);
        } else if (getJComboBoxWhenChange().getSelectedItem().toString().equalsIgnoreCase(LMProgramDirectGear.CHANGE_TRIGGER_OFFSET)
                   || getJComboBoxWhenChange().getSelectedItem().toString().equalsIgnoreCase("Above Trigger")) {
            // TriggerOffset
            getJLabelChangeTriggerNumber().setVisible(true);
            getJCSpinFieldChangeTriggerNumber().setVisible(true);

            getJLabelChangeTriggerOffset().setVisible(true);
            getJTextFieldChangeTriggerOffset().setVisible(true);
        } else
            throw new Error("Unknown LMProgramDirectGear control condition found, the value = "
                            + getJComboBoxWhenChange().getSelectedItem().toString());

        fireInputUpdate();
        return;
    }

    /**
     * main entrypoint - starts the part when it is run as an application
     * @param args java.lang.String[]
     */
    public static void main(java.lang.String[] args) {
        try {
            javax.swing.JFrame frame = new javax.swing.JFrame();
            SepCycleGearPanel sepCycleGearPanel;
            sepCycleGearPanel = new SepCycleGearPanel();
            frame.setContentPane(sepCycleGearPanel);
            frame.setSize(sepCycleGearPanel.getSize());
            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                };
            });
            frame.setVisible(true);
            Insets insets = frame.getInsets();
            frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
            frame.setVisible(true);
        } catch (Throwable exception) {
            System.err.println("Exception occurred in main() of com.cannontech.dbeditor.wizard.device.lmprogram.GenericGearPanel");
            exception.printStackTrace(System.out);
        }
    }

    private void setChangeCondition(String change) {
        if (change == null)
            return;

        if (change.equalsIgnoreCase(LMProgramDirectGear.CHANGE_NONE)) {
            getJComboBoxWhenChange().setSelectedItem("Manually Only");
        } else if (change.equalsIgnoreCase(LMProgramDirectGear.CHANGE_DURATION)) {
            getJComboBoxWhenChange().setSelectedItem("After a Duration");
        } else if (change.equalsIgnoreCase(LMProgramDirectGear.CHANGE_PRIORITY)) {
            getJComboBoxWhenChange().setSelectedItem("Priority Change");
        } else if (change.equalsIgnoreCase(LMProgramDirectGear.CHANGE_TRIGGER_OFFSET)) {
            getJComboBoxWhenChange().setSelectedItem("Above Trigger");
        }

    }

    public void setValue(Object o) {
        SepCycleGear gear = null;

        if (o == null) {
            return;
        } else
            gear = (SepCycleGear) o;

        getJComboBoxHowToStop().setSelectedItem(StringUtils.addCharBetweenWords(' ', gear.getMethodStopType()));

        getJCSpinFieldPercentReduction().setValue(gear.getPercentReduction());

        setChangeCondition(gear.getChangeCondition());

        getJCSpinFieldChangeDuration().setValue(new Integer(gear.getChangeDuration().intValue() / 60));
        getJCSpinFieldChangePriority().setValue(gear.getChangePriority());
        getJCSpinFieldChangeTriggerNumber().setValue(gear.getChangeTriggerNumber());
        getJTextFieldChangeTriggerOffset().setText(gear.getChangeTriggerOffset().toString());

        getJCSpinFieldControlPercent().setValue(gear.getControlPercent());

        if (gear.isFrontRampEnabled()) {
            getCheckBoxRampIn().setSelected(true);
        } else {
            getCheckBoxRampIn().setSelected(false);
        }

        if (gear.isBackRampEnabled()) {
            getCheckBoxRampOut().setSelected(true);
        } else {
            getCheckBoxRampOut().setSelected(false);
        }

        if (gear.isTrueCycleEnabled()) {
            getCheckBoxTrueCycle().setSelected(true);
        } else {
            getCheckBoxTrueCycle().setSelected(false);
        }
    }

    public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) {
        // fire this event for all JCSpinFields!!
        this.fireInputUpdate();
    }

    /**
     * This method initializes jCheckBoxNoRamp
     * @return javax.swing.JCheckBox
     */
    private javax.swing.JCheckBox getCheckBoxRampIn() {
        if (checkBoxRampIn == null) {
            checkBoxRampIn = new javax.swing.JCheckBox();
            checkBoxRampIn.setText("Ramp In");
            checkBoxRampIn.setPreferredSize(new Dimension(165, 23));
            checkBoxRampIn.setSelected(true);
        }
        return checkBoxRampIn;
    }

    private javax.swing.JCheckBox getCheckBoxRampOut() {
        if (checkBoxRampOut == null) {
            checkBoxRampOut = new javax.swing.JCheckBox();
            checkBoxRampOut.setText("Ramp Out");
            checkBoxRampOut.setPreferredSize(new Dimension(165, 23));
            checkBoxRampOut.setSelected(true);

        }
        return checkBoxRampOut;
    }

    private javax.swing.JCheckBox getCheckBoxTrueCycle() {
        if (checkBoxTrueCycle == null) {
            checkBoxTrueCycle = new javax.swing.JCheckBox();
            checkBoxTrueCycle.setText("TrueCycle or adaptive algorithm");
            checkBoxTrueCycle.setPreferredSize(new Dimension(165, 23));
            checkBoxTrueCycle.setSelected(true);

        }
        return checkBoxTrueCycle;
    }
}
