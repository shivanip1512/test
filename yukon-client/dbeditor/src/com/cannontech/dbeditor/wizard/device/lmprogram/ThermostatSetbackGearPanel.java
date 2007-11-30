package com.cannontech.dbeditor.wizard.device.lmprogram;

/**
 * Insert the type's description here.
 * Creation date: (8/1/2002 4:37:34 PM)
 * @author: 
 */
import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.common.util.StringUtils;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;

public class ThermostatSetbackGearPanel extends GenericGearPanel implements com.cannontech.common.gui.util.DataInputPanelListener {
    private javax.swing.JComboBox ivjJComboBoxHowToStop = null;
    private javax.swing.JComboBox ivjJComboBoxWhenChange = null;
    private com.klg.jclass.field.JCSpinField ivjJCSpinFieldChangeDuration = null;
    private com.klg.jclass.field.JCSpinField ivjJCSpinFieldChangePriority = null;
    private com.klg.jclass.field.JCSpinField ivjJCSpinFieldChangeTriggerNumber = null;
    private com.klg.jclass.field.JCSpinField ivjJCSpinFieldPercentReduction = null;
    private javax.swing.JLabel ivjJLabelChangeDuration = null;
    private javax.swing.JLabel ivjJLabelChangePriority = null;
    private javax.swing.JLabel ivjJLabelChangeTriggerNumber = null;
    private javax.swing.JLabel ivjJLabelChangeTriggerOffset = null;
    private javax.swing.JLabel ivjJLabelHowToStop = null;
    private javax.swing.JLabel ivjJLabelMinutesChDur = null;
    private javax.swing.JLabel ivjJLabelPercentReduction = null;
    private javax.swing.JLabel ivjJLabelWhenChange = null;
    private javax.swing.JPanel ivjJPanelChangeMethod = null;
    private javax.swing.JTextField ivjJTextFieldChangeTriggerOffset = null;
    private com.cannontech.dbeditor.wizard.device.lmprogram.LMExpressStatEditorPanel statEditorPanel = null;
    /**
     * ThermostatSetbackGearPanel constructor comment.
     */
    public ThermostatSetbackGearPanel() {
        super();
        initialize();
    }
    /**
     * Method to handle events for the ActionListener interface.
     * @param e java.awt.event.ActionEvent
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    public void actionPerformed(java.awt.event.ActionEvent e) {
        // user code begin {1}
        if (e.getSource() == getJComboBoxWhenChange()) 
            connEtoC1(e);
        if (e.getSource() == getJComboBoxHowToStop()) 
            connEtoC10(e);
        // user code end

        // user code begin {2}
        // user code end
    }
    /**
     * connEtoC1:  (JComboBoxWhenChange.action.actionPerformed(java.awt.event.ActionEvent) --> DirectModifyGearPanel.jComboBoxWhenChange_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
     * @param arg1 java.awt.event.ActionEvent
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC1(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.jComboBoxWhenChange_ActionPerformed(arg1);
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }
    /**
     * connEtoC10:  (JComboBoxHowToStop.action.actionPerformed(java.awt.event.ActionEvent) --> DirectModifyGearPanel.fireInputUpdate()V)
     * @param arg1 java.awt.event.ActionEvent
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void connEtoC10(java.awt.event.ActionEvent arg1) {
        try {
            // user code begin {1}
            // user code end
            this.fireInputUpdate();
            // user code begin {2}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {3}
            // user code end
            handleException(ivjExc);
        }
    }

    /**
     * Return the JComboBoxHowToStop property value.
     * @return javax.swing.JComboBox
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JComboBox getJComboBoxHowToStop() {
        if (ivjJComboBoxHowToStop == null) {
            try {
                ivjJComboBoxHowToStop = new javax.swing.JComboBox();
                ivjJComboBoxHowToStop.setName("JComboBoxHowToStop");
                ivjJComboBoxHowToStop.setPreferredSize(new java.awt.Dimension(150, 23));
                ivjJComboBoxHowToStop.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
                // user code begin {1}
                ivjJComboBoxHowToStop.addItem( StringUtils.addCharBetweenWords( ' ', LMProgramDirectGear.STOP_TIME_IN ) );
                ivjJComboBoxHowToStop.addItem( StringUtils.addCharBetweenWords( ' ', LMProgramDirectGear.STOP_RESTORE ) );
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJComboBoxHowToStop;
    }
    /**
     * Return the JComboBoxWhenChange property value.
     * @return javax.swing.JComboBox
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JComboBox getJComboBoxWhenChange() {
        if (ivjJComboBoxWhenChange == null) {
            try {
                ivjJComboBoxWhenChange = new javax.swing.JComboBox();
                ivjJComboBoxWhenChange.setName("JComboBoxWhenChange");
                ivjJComboBoxWhenChange.setPreferredSize(new java.awt.Dimension(205, 23));
                ivjJComboBoxWhenChange.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
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
    /**
     * Return the JCSpinFieldChangeDuration property value.
     * @return com.klg.jclass.field.JCSpinField
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private com.klg.jclass.field.JCSpinField getJCSpinFieldChangeDuration() {
        if (ivjJCSpinFieldChangeDuration == null) {
            try {
                ivjJCSpinFieldChangeDuration = new com.klg.jclass.field.JCSpinField();
                ivjJCSpinFieldChangeDuration.setName("JCSpinFieldChangeDuration");
                ivjJCSpinFieldChangeDuration.setPreferredSize(new java.awt.Dimension(35, 20));
                ivjJCSpinFieldChangeDuration.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
                ivjJCSpinFieldChangeDuration.setMaximumSize(new java.awt.Dimension(40, 20));
                // user code begin {1}
                ivjJCSpinFieldChangeDuration.setDataProperties(
                                                               new com.klg.jclass.field.DataProperties(
                                                                                                       new com.klg.jclass.field.validate.JCIntegerValidator(
                                                                                                                                                            null, new Integer(0), new Integer(99999), null, true, 
                                                                                                                                                            null, new Integer(1), "#,##0.###;-#,##0.###", false, 
                                                                                                                                                            false, false, null, new Integer(3)), 
                                                                                                                                                            new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, 
                                                                                                                                                                                                            new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 2, 
                                                                                                                                                                                                                                                                    new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJCSpinFieldChangeDuration;
    }
    /**
     * Return the JCSpinFieldChangePriority property value.
     * @return com.klg.jclass.field.JCSpinField
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private com.klg.jclass.field.JCSpinField getJCSpinFieldChangePriority() {
        if (ivjJCSpinFieldChangePriority == null) {
            try {
                ivjJCSpinFieldChangePriority = new com.klg.jclass.field.JCSpinField();
                ivjJCSpinFieldChangePriority.setName("JCSpinFieldChangePriority");
                ivjJCSpinFieldChangePriority.setPreferredSize(new java.awt.Dimension(30, 20));
                ivjJCSpinFieldChangePriority.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
                ivjJCSpinFieldChangePriority.setMaximumSize(new java.awt.Dimension(40, 30));
                // user code begin {1}
                ivjJCSpinFieldChangePriority.setDataProperties(
                                                               new com.klg.jclass.field.DataProperties(
                                                                                                       new com.klg.jclass.field.validate.JCIntegerValidator(
                                                                                                                                                            null, new Integer(0), new Integer(9999), null, true, 
                                                                                                                                                            null, new Integer(1), "#,##0.###;-#,##0.###", false, 
                                                                                                                                                            false, false, null, new Integer(0)), 
                                                                                                                                                            new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, 
                                                                                                                                                                                                            new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 2, 
                                                                                                                                                                                                                                                                    new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJCSpinFieldChangePriority;
    }
    /**
     * Return the JCSpinFieldChangeTriggerNumber property value.
     * @return com.klg.jclass.field.JCSpinField
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private com.klg.jclass.field.JCSpinField getJCSpinFieldChangeTriggerNumber() {
        if (ivjJCSpinFieldChangeTriggerNumber == null) {
            try {
                ivjJCSpinFieldChangeTriggerNumber = new com.klg.jclass.field.JCSpinField();
                ivjJCSpinFieldChangeTriggerNumber.setName("JCSpinFieldChangeTriggerNumber");
                ivjJCSpinFieldChangeTriggerNumber.setPreferredSize(new java.awt.Dimension(35, 20));
                ivjJCSpinFieldChangeTriggerNumber.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
                ivjJCSpinFieldChangeTriggerNumber.setMaximumSize(new java.awt.Dimension(40, 20));
                // user code begin {1}
                ivjJCSpinFieldChangeTriggerNumber.setDataProperties(
                                                                    new com.klg.jclass.field.DataProperties(
                                                                                                            new com.klg.jclass.field.validate.JCIntegerValidator(
                                                                                                                                                                 null, new Integer(1), new Integer(99999), null, true, 
                                                                                                                                                                 null, new Integer(1), "#,##0.###;-#,##0.###", false, 
                                                                                                                                                                 false, false, null, new Integer(1)), 
                                                                                                                                                                 new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, 
                                                                                                                                                                                                                 new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 2, 
                                                                                                                                                                                                                                                                         new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJCSpinFieldChangeTriggerNumber;
    }
    /**
     * Return the JCSpinFieldPercentReduction property value.
     * @return com.klg.jclass.field.JCSpinField
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private com.klg.jclass.field.JCSpinField getJCSpinFieldPercentReduction() {
        if (ivjJCSpinFieldPercentReduction == null) {
            try {
                ivjJCSpinFieldPercentReduction = new com.klg.jclass.field.JCSpinField();
                ivjJCSpinFieldPercentReduction.setName("JCSpinFieldPercentReduction");
                ivjJCSpinFieldPercentReduction.setPreferredSize(new java.awt.Dimension(88, 20));
                ivjJCSpinFieldPercentReduction.setMaximumSize(new java.awt.Dimension(90, 20));
                ivjJCSpinFieldPercentReduction.setMinimumSize(new java.awt.Dimension(40, 20));
                // user code begin {1}
                ivjJCSpinFieldPercentReduction.setDataProperties(
                                                                 new com.klg.jclass.field.DataProperties(
                                                                                                         new com.klg.jclass.field.validate.JCIntegerValidator(
                                                                                                                                                              null, new Integer(0), new Integer(100), null, true, 
                                                                                                                                                              null, new Integer(1), "#,##0.###;-#,##0.###", false, 
                                                                                                                                                              false, false, null, new Integer(100)), 
                                                                                                                                                              new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, 
                                                                                                                                                                                                              new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 2, 
                                                                                                                                                                                                                                                                      new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));

                ivjJCSpinFieldPercentReduction.setValue( new Integer(100) );

                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJCSpinFieldPercentReduction;
    }
    /**
     * Return the JLabelChangeDuration property value.
     * @return javax.swing.JLabel
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
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
    /**
     * Return the JLabelChangePriority property value.
     * @return javax.swing.JLabel
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
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
    /**
     * Return the JLabelChangeTriggerNumber property value.
     * @return javax.swing.JLabel
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getJLabelChangeTriggerNumber() {
        if (ivjJLabelChangeTriggerNumber == null) {
            try {
                ivjJLabelChangeTriggerNumber = new javax.swing.JLabel();
                ivjJLabelChangeTriggerNumber.setName("JLabelChangeTriggerNumber");
                ivjJLabelChangeTriggerNumber.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
                ivjJLabelChangeTriggerNumber.setText("Trigger Number:");
                ivjJLabelChangeTriggerNumber.setMaximumSize(new java.awt.Dimension(143, 14));
                ivjJLabelChangeTriggerNumber.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
                ivjJLabelChangeTriggerNumber.setPreferredSize(new java.awt.Dimension(143, 14));
                ivjJLabelChangeTriggerNumber.setFont(new java.awt.Font("dialog", 0, 12));
                ivjJLabelChangeTriggerNumber.setMinimumSize(new java.awt.Dimension(143, 14));
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
    /**
     * Return the JLabelChangeTriggerOffset property value.
     * @return javax.swing.JLabel
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getJLabelChangeTriggerOffset() {
        if (ivjJLabelChangeTriggerOffset == null) {
            try {
                ivjJLabelChangeTriggerOffset = new javax.swing.JLabel();
                ivjJLabelChangeTriggerOffset.setName("JLabelChangeTriggerOffset");
                ivjJLabelChangeTriggerOffset.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
                ivjJLabelChangeTriggerOffset.setText("Trigger Offset:");
                ivjJLabelChangeTriggerOffset.setMaximumSize(new java.awt.Dimension(143, 14));
                ivjJLabelChangeTriggerOffset.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
                ivjJLabelChangeTriggerOffset.setPreferredSize(new java.awt.Dimension(143, 14));
                ivjJLabelChangeTriggerOffset.setFont(new java.awt.Font("dialog", 0, 12));
                ivjJLabelChangeTriggerOffset.setMinimumSize(new java.awt.Dimension(143, 14));
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
    /**
     * Return the JLabelHowToStop property value.
     * @return javax.swing.JLabel
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getJLabelHowToStop() {
        if (ivjJLabelHowToStop == null) {
            try {
                ivjJLabelHowToStop = new javax.swing.JLabel();
                ivjJLabelHowToStop.setName("JLabelHowToStop");
                ivjJLabelHowToStop.setFont(new java.awt.Font("dialog", 0, 12));
                ivjJLabelHowToStop.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
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
    /**
     * Return the JLabelMinutesChDur property value.
     * @return javax.swing.JLabel
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getJLabelMinutesChDur() {
        if (ivjJLabelMinutesChDur == null) {
            try {
                ivjJLabelMinutesChDur = new javax.swing.JLabel();
                ivjJLabelMinutesChDur.setName("JLabelMinutesChDur");
                ivjJLabelMinutesChDur.setFont(new java.awt.Font("dialog", 0, 12));
                ivjJLabelMinutesChDur.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
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
    /**
     * Return the JLabelPercentReduction property value.
     * @return javax.swing.JLabel
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getJLabelPercentReduction() {
        if (ivjJLabelPercentReduction == null) {
            try {
                ivjJLabelPercentReduction = new javax.swing.JLabel();
                ivjJLabelPercentReduction.setName("JLabelPercentReduction");
                ivjJLabelPercentReduction.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
                ivjJLabelPercentReduction.setText("Group Capacity Reduction %:");
                ivjJLabelPercentReduction.setMaximumSize(new java.awt.Dimension(112, 14));
                ivjJLabelPercentReduction.setPreferredSize(new java.awt.Dimension(112, 14));
                ivjJLabelPercentReduction.setFont(new java.awt.Font("dialog", 0, 12));
                ivjJLabelPercentReduction.setMinimumSize(new java.awt.Dimension(112, 14));
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
    /**
     * Return the JLabelWhenChange property value.
     * @return javax.swing.JLabel
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JLabel getJLabelWhenChange() {
        if (ivjJLabelWhenChange == null) {
            try {
                ivjJLabelWhenChange = new javax.swing.JLabel();
                ivjJLabelWhenChange.setName("JLabelWhenChange");
                ivjJLabelWhenChange.setFont(new java.awt.Font("dialog", 0, 12));
                ivjJLabelWhenChange.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
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
    /**
     * Return the JPanelChangeMethod property value.
     * @return javax.swing.JPanel
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JPanel getJPanelChangeMethod() {
        if (ivjJPanelChangeMethod == null) {
            try {
                ivjJPanelChangeMethod = new javax.swing.JPanel();
                ivjJPanelChangeMethod.setName("JPanelChangeMethod");
                //ivjJPanelChangeMethod.setBorder(new com.ibm.uvm.abt.edit.DeletedClassView());
                ivjJPanelChangeMethod.setLayout(new java.awt.GridBagLayout());
                ivjJPanelChangeMethod.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
                ivjJPanelChangeMethod.setFont(new java.awt.Font("dialog", 0, 12));
                ivjJPanelChangeMethod.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

                java.awt.GridBagConstraints constraintsJLabelChangeDuration = new java.awt.GridBagConstraints();
                constraintsJLabelChangeDuration.gridx = 1; constraintsJLabelChangeDuration.gridy = 2;
                constraintsJLabelChangeDuration.anchor = java.awt.GridBagConstraints.NORTHWEST;
                constraintsJLabelChangeDuration.ipadx = -5;
                constraintsJLabelChangeDuration.ipady = 6;
                constraintsJLabelChangeDuration.insets = new java.awt.Insets(1, 5, 3, 5);
                getJPanelChangeMethod().add(getJLabelChangeDuration(), constraintsJLabelChangeDuration);

                java.awt.GridBagConstraints constraintsJCSpinFieldChangeDuration = new java.awt.GridBagConstraints();
                constraintsJCSpinFieldChangeDuration.gridx = 2; constraintsJCSpinFieldChangeDuration.gridy = 2;
                constraintsJCSpinFieldChangeDuration.anchor = java.awt.GridBagConstraints.NORTHWEST;
                constraintsJCSpinFieldChangeDuration.ipadx = 34;
                constraintsJCSpinFieldChangeDuration.ipady = 19;
                constraintsJCSpinFieldChangeDuration.insets = new java.awt.Insets(1, 5, 3, 3);
                getJPanelChangeMethod().add(getJCSpinFieldChangeDuration(), constraintsJCSpinFieldChangeDuration);

                java.awt.GridBagConstraints constraintsJLabelMinutesChDur = new java.awt.GridBagConstraints();
                constraintsJLabelMinutesChDur.gridx = 3; constraintsJLabelMinutesChDur.gridy = 2;
                constraintsJLabelMinutesChDur.anchor = java.awt.GridBagConstraints.NORTHWEST;
                constraintsJLabelMinutesChDur.ipadx = 5;
                constraintsJLabelMinutesChDur.ipady = -2;
                constraintsJLabelMinutesChDur.insets = new java.awt.Insets(5, 4, 5, 4);
                getJPanelChangeMethod().add(getJLabelMinutesChDur(), constraintsJLabelMinutesChDur);

                java.awt.GridBagConstraints constraintsJLabelChangePriority = new java.awt.GridBagConstraints();
                constraintsJLabelChangePriority.gridx = 4; constraintsJLabelChangePriority.gridy = 2;
                constraintsJLabelChangePriority.anchor = java.awt.GridBagConstraints.NORTHWEST;
                constraintsJLabelChangePriority.ipadx = -13;
                constraintsJLabelChangePriority.ipady = 6;
                constraintsJLabelChangePriority.insets = new java.awt.Insets(1, 4, 3, 2);
                getJPanelChangeMethod().add(getJLabelChangePriority(), constraintsJLabelChangePriority);

                java.awt.GridBagConstraints constraintsJCSpinFieldChangePriority = new java.awt.GridBagConstraints();
                constraintsJCSpinFieldChangePriority.gridx = 5; constraintsJCSpinFieldChangePriority.gridy = 2;
                constraintsJCSpinFieldChangePriority.anchor = java.awt.GridBagConstraints.NORTHWEST;
                constraintsJCSpinFieldChangePriority.ipadx = 29;
                constraintsJCSpinFieldChangePriority.ipady = 19;
                constraintsJCSpinFieldChangePriority.insets = new java.awt.Insets(1, 3, 3, 10);
                getJPanelChangeMethod().add(getJCSpinFieldChangePriority(), constraintsJCSpinFieldChangePriority);

                java.awt.GridBagConstraints constraintsJLabelChangeTriggerNumber = new java.awt.GridBagConstraints();
                constraintsJLabelChangeTriggerNumber.gridx = 1; constraintsJLabelChangeTriggerNumber.gridy = 3;
                constraintsJLabelChangeTriggerNumber.anchor = java.awt.GridBagConstraints.NORTHWEST;
                constraintsJLabelChangeTriggerNumber.ipadx = -45;
                constraintsJLabelChangeTriggerNumber.ipady = 6;
                constraintsJLabelChangeTriggerNumber.insets = new java.awt.Insets(4, 5, 21, 5);
                getJPanelChangeMethod().add(getJLabelChangeTriggerNumber(), constraintsJLabelChangeTriggerNumber);

                java.awt.GridBagConstraints constraintsJLabelChangeTriggerOffset = new java.awt.GridBagConstraints();
                constraintsJLabelChangeTriggerOffset.gridx = 4; constraintsJLabelChangeTriggerOffset.gridy = 3;
                constraintsJLabelChangeTriggerOffset.anchor = java.awt.GridBagConstraints.NORTHWEST;
                constraintsJLabelChangeTriggerOffset.ipadx = -63;
                constraintsJLabelChangeTriggerOffset.insets = new java.awt.Insets(8, 4, 23, 12);
                getJPanelChangeMethod().add(getJLabelChangeTriggerOffset(), constraintsJLabelChangeTriggerOffset);

                java.awt.GridBagConstraints constraintsJTextFieldChangeTriggerOffset = new java.awt.GridBagConstraints();
                constraintsJTextFieldChangeTriggerOffset.gridx = 5; constraintsJTextFieldChangeTriggerOffset.gridy = 3;
                constraintsJTextFieldChangeTriggerOffset.anchor = java.awt.GridBagConstraints.NORTHWEST;
                constraintsJTextFieldChangeTriggerOffset.weightx = 1.0;
                constraintsJTextFieldChangeTriggerOffset.ipadx = 26;
                constraintsJTextFieldChangeTriggerOffset.insets = new java.awt.Insets(4, 3, 21, 10);
                getJPanelChangeMethod().add(getJTextFieldChangeTriggerOffset(), constraintsJTextFieldChangeTriggerOffset);

                java.awt.GridBagConstraints constraintsJCSpinFieldChangeTriggerNumber = new java.awt.GridBagConstraints();
                constraintsJCSpinFieldChangeTriggerNumber.gridx = 2; constraintsJCSpinFieldChangeTriggerNumber.gridy = 3;
                constraintsJCSpinFieldChangeTriggerNumber.anchor = java.awt.GridBagConstraints.NORTHWEST;
                constraintsJCSpinFieldChangeTriggerNumber.ipadx = 34;
                constraintsJCSpinFieldChangeTriggerNumber.ipady = 19;
                constraintsJCSpinFieldChangeTriggerNumber.insets = new java.awt.Insets(4, 5, 21, 3);
                getJPanelChangeMethod().add(getJCSpinFieldChangeTriggerNumber(), constraintsJCSpinFieldChangeTriggerNumber);

                java.awt.GridBagConstraints constraintsJLabelWhenChange = new java.awt.GridBagConstraints();
                constraintsJLabelWhenChange.gridx = 1; constraintsJLabelWhenChange.gridy = 1;
                constraintsJLabelWhenChange.anchor = java.awt.GridBagConstraints.NORTHWEST;
                constraintsJLabelWhenChange.ipadx = 3;
                constraintsJLabelWhenChange.ipady = 4;
                constraintsJLabelWhenChange.insets = new java.awt.Insets(4, 5, 4, 5);
                getJPanelChangeMethod().add(getJLabelWhenChange(), constraintsJLabelWhenChange);

                java.awt.GridBagConstraints constraintsJComboBoxWhenChange = new java.awt.GridBagConstraints();
                constraintsJComboBoxWhenChange.gridx = 2; constraintsJComboBoxWhenChange.gridy = 1;
                constraintsJComboBoxWhenChange.gridwidth = 4;
                constraintsJComboBoxWhenChange.anchor = java.awt.GridBagConstraints.NORTHWEST;
                constraintsJComboBoxWhenChange.weightx = 1.0;
                constraintsJComboBoxWhenChange.ipadx = 79;
                constraintsJComboBoxWhenChange.insets = new java.awt.Insets(4, 5, 1, 17);
                getJPanelChangeMethod().add(getJComboBoxWhenChange(), constraintsJComboBoxWhenChange);
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
    /**
     * Return the JTextFieldChangeTriggerOffset property value.
     * @return javax.swing.JTextField
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private javax.swing.JTextField getJTextFieldChangeTriggerOffset() {
        if (ivjJTextFieldChangeTriggerOffset == null) {
            try {
                ivjJTextFieldChangeTriggerOffset = new javax.swing.JTextField();
                ivjJTextFieldChangeTriggerOffset.setName("JTextFieldChangeTriggerOffset");
                ivjJTextFieldChangeTriggerOffset.setPreferredSize(new java.awt.Dimension(30, 20));
                ivjJTextFieldChangeTriggerOffset.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
                ivjJTextFieldChangeTriggerOffset.setMaximumSize(new java.awt.Dimension(40, 20));
                // user code begin {1}
                ivjJTextFieldChangeTriggerOffset.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(-99999.9999, 99999.9999, 4) );

                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjJTextFieldChangeTriggerOffset;
    }
    /**
     * Insert the method's description here.
     * Creation date: (7/15/2002 1:48:13 PM)
     * @return com.cannontech.dbeditor.wizard.device.lmgroup.LMGroupExpressStatEditorPanel
     */
    public com.cannontech.dbeditor.wizard.device.lmprogram.LMExpressStatEditorPanel getStatEditorPanel() {
        if(statEditorPanel == null)
            statEditorPanel = new com.cannontech.dbeditor.wizard.device.lmprogram.LMExpressStatEditorPanel();

        return statEditorPanel;
    }
    /**
     * getValue method comment.
     */
    @Override
    public Object getValue(Object o) 
    {
        LMProgramDirectGear gear = null;

        gear = (LMProgramDirectGear)o;

        if( getJComboBoxHowToStop().getSelectedItem() != null )
        {
            gear.setMethodStopType( 
                                   com.cannontech.common.util.StringUtils.removeChars( ' ', getJComboBoxHowToStop().getSelectedItem().toString() ) );
        }

        gear.setPercentReduction( new Integer( ((Number)getJCSpinFieldPercentReduction().getValue()).intValue() ) );

        gear.setChangeCondition( getChangeCondition(getJComboBoxWhenChange().getSelectedItem().toString()) );

        gear.setChangeDuration( new Integer( ((Number)getJCSpinFieldChangeDuration().getValue()).intValue() * 60 ) );
        gear.setChangePriority( new Integer( ((Number)getJCSpinFieldChangePriority().getValue()).intValue() ) );
        gear.setChangeTriggerNumber( new Integer( ((Number)getJCSpinFieldChangeTriggerNumber().getValue()).intValue() ) );

        if( getJTextFieldChangeTriggerOffset().getText() == null || getJTextFieldChangeTriggerOffset().getText().length() <= 0 )
            gear.setChangeTriggerOffset( new Double(0.0) );
        else
            gear.setChangeTriggerOffset( Double.valueOf(getJTextFieldChangeTriggerOffset().getText()) );

        com.cannontech.database.data.device.lm.ThermostatSetbackGear ts = (com.cannontech.database.data.device.lm.ThermostatSetbackGear)gear;

        if(getStatEditorPanel().getJCheckBoxDeltaB().isSelected())
            ts.setValueB(Integer.valueOf(getStatEditorPanel().getJTextFieldDeltaB().getText()));
        if(getStatEditorPanel().getJCheckBoxDeltaD().isSelected())
            ts.setValueD(Integer.valueOf(getStatEditorPanel().getJTextFieldDeltaD().getText()));
        if(getStatEditorPanel().getJCheckBoxDeltaF().isSelected())
            ts.setValueF(Integer.valueOf(getStatEditorPanel().getJTextFieldDeltaF().getText()));
        if(getStatEditorPanel().getJCheckBoxRand().isSelected())
            ts.setRandom(Integer.valueOf(getStatEditorPanel().getJTextFieldRand().getText()));
        if(getStatEditorPanel().getJCheckBoxMax().isSelected())
            ts.setMaxValue(Integer.valueOf(getStatEditorPanel().getJTextFieldMax().getText()));
        if(getStatEditorPanel().getJCheckBoxMin().isSelected())
            ts.setMinValue(Integer.valueOf(getStatEditorPanel().getJTextFieldMin().getText()));
        if(getStatEditorPanel().getJCheckBoxTa().isSelected())
            ts.setValueTa(Integer.valueOf(getStatEditorPanel().getJTextFieldTa().getText()));
        if(getStatEditorPanel().getJCheckBoxTb().isSelected())
            ts.setValueTb(Integer.valueOf(getStatEditorPanel().getJTextFieldTb().getText()));
        if(getStatEditorPanel().getJCheckBoxTc().isSelected())
            ts.setValueTc(Integer.valueOf(getStatEditorPanel().getJTextFieldTc().getText()));
        if(getStatEditorPanel().getJCheckBoxTd().isSelected())
            ts.setValueTd(Integer.valueOf(getStatEditorPanel().getJTextFieldTd().getText()));
        if(getStatEditorPanel().getJCheckBoxTe().isSelected())
            ts.setValueTe(Integer.valueOf(getStatEditorPanel().getJTextFieldTe().getText()));
        if(getStatEditorPanel().getJCheckBoxTf().isSelected())
            ts.setValueTf(Integer.valueOf(getStatEditorPanel().getJTextFieldTf().getText()));

        if(getStatEditorPanel().isAbsolute)
            ts.getSettings().setCharAt(0, 'A');
        else
            ts.getSettings().setCharAt(0, 'D');

        if(getStatEditorPanel().isCelsius)
            ts.getSettings().setCharAt(1, 'C');
        else
            ts.getSettings().setCharAt(1, 'F');

        if(getStatEditorPanel().getJCheckBoxHeatMode().isSelected())
            ts.getSettings().setCharAt(2, 'H');
        else
            ts.getSettings().setCharAt(2, '-');

        if(getStatEditorPanel().getJCheckBoxCoolMode().isSelected())
            //I for "icy goodness"
            ts.getSettings().setCharAt(3, 'I');
        else
            ts.getSettings().setCharAt(3, '-');


        return ts;
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
    /**
     * Insert the method's description here.
     * Creation date: (7/15/2002 2:01:07 PM)
     * @return java.awt.GridBagConstraints
     */
    private java.awt.GridBagConstraints holderConstraints() {
        /* This is annoying, but a call to this method must always be in the 
	initialize of ThermostatSetbackGear to bring in the proper constraints 
	for the ExpressStat panel.
         */
        java.awt.GridBagConstraints constraintsLMGroupExpressStatEditor = new java.awt.GridBagConstraints();
        constraintsLMGroupExpressStatEditor.gridx = 1; constraintsLMGroupExpressStatEditor.gridy = 1;
        constraintsLMGroupExpressStatEditor.gridwidth = 2;
        constraintsLMGroupExpressStatEditor.anchor = java.awt.GridBagConstraints.NORTHWEST;
        constraintsLMGroupExpressStatEditor.weightx = 1.0;
        constraintsLMGroupExpressStatEditor.weighty = 1.0;
        constraintsLMGroupExpressStatEditor.ipadx = -17;
        constraintsLMGroupExpressStatEditor.insets = new java.awt.Insets(4, 3, 5, 34);
        return constraintsLMGroupExpressStatEditor;
    }
    /**
     * Initializes connections
     * @exception java.lang.Exception The exception description.
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void initConnections() throws java.lang.Exception {
        // user code begin {1}

        getJCSpinFieldChangeDuration().addValueListener(this);
        getJCSpinFieldChangePriority().addValueListener(this);
        getJCSpinFieldChangeTriggerNumber().addValueListener(this);
        getJCSpinFieldPercentReduction().addValueListener(this);
        getJComboBoxWhenChange().addActionListener(this);
        getJTextFieldChangeTriggerOffset().addCaretListener(this);
        getJComboBoxWhenChange().addActionListener(this);
        getJComboBoxHowToStop().addActionListener(this);
        getJTextFieldChangeTriggerOffset().addCaretListener(this);
        getStatEditorPanel().addDataInputPanelListener(this);

        // user code end
    }
    /**
     * Initialize the class.
     */
    /* WARNING: THIS METHOD WILL BE REGENERATED. */
    private void initialize() {
        try {
            // user code begin {1}
            // user code end
            setName("ThermostatSetbackGearPanel");
            setPreferredSize(new java.awt.Dimension(402, 430));
            setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
            setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
            setLayout(new java.awt.GridBagLayout());
            setSize(402, 430);

            java.awt.GridBagConstraints constraintsJLabelHowToStop = new java.awt.GridBagConstraints();
            constraintsJLabelHowToStop.gridx = 1; constraintsJLabelHowToStop.gridy = 1;
            constraintsJLabelHowToStop.anchor = java.awt.GridBagConstraints.NORTHWEST;
            constraintsJLabelHowToStop.ipadx = 4;
            constraintsJLabelHowToStop.insets = new java.awt.Insets(280, 10, 10, 61);
            add(getJLabelHowToStop(), constraintsJLabelHowToStop);

            java.awt.GridBagConstraints constraintsJComboBoxHowToStop = new java.awt.GridBagConstraints();
            constraintsJComboBoxHowToStop.gridx = 2; constraintsJComboBoxHowToStop.gridy = 1;
            constraintsJComboBoxHowToStop.anchor = java.awt.GridBagConstraints.NORTHWEST;
            constraintsJComboBoxHowToStop.weightx = 1.0;
            constraintsJComboBoxHowToStop.ipadx = 24;
            constraintsJComboBoxHowToStop.insets = new java.awt.Insets(280, 13, 3, 52);
            add(getJComboBoxHowToStop(), constraintsJComboBoxHowToStop);

            java.awt.GridBagConstraints constraintsJLabelPercentReduction = new java.awt.GridBagConstraints();
            constraintsJLabelPercentReduction.gridx = 1; constraintsJLabelPercentReduction.gridy = 2;
            constraintsJLabelPercentReduction.anchor = java.awt.GridBagConstraints.NORTHWEST;
            constraintsJLabelPercentReduction.ipadx = 53;
            constraintsJLabelPercentReduction.ipady = 3;
            constraintsJLabelPercentReduction.insets = new java.awt.Insets(4, 10, 5, 12);
            add(getJLabelPercentReduction(), constraintsJLabelPercentReduction);

            java.awt.GridBagConstraints constraintsJCSpinFieldPercentReduction = new java.awt.GridBagConstraints();
            constraintsJCSpinFieldPercentReduction.gridx = 2; constraintsJCSpinFieldPercentReduction.gridy = 2;
            constraintsJCSpinFieldPercentReduction.anchor = java.awt.GridBagConstraints.NORTHWEST;
            constraintsJCSpinFieldPercentReduction.ipadx = 48;
            constraintsJCSpinFieldPercentReduction.insets = new java.awt.Insets(4, 13, 2, 114);
            add(getJCSpinFieldPercentReduction(), constraintsJCSpinFieldPercentReduction);

            java.awt.GridBagConstraints constraintsJPanelChangeMethod = new java.awt.GridBagConstraints();
            constraintsJPanelChangeMethod.gridx = 1; constraintsJPanelChangeMethod.gridy = 3;
            constraintsJPanelChangeMethod.gridwidth = 2;
            constraintsJPanelChangeMethod.anchor = java.awt.GridBagConstraints.NORTHWEST;
            constraintsJPanelChangeMethod.weightx = 1.0;
            constraintsJPanelChangeMethod.weighty = 1.0;
            constraintsJPanelChangeMethod.insets = new java.awt.Insets(3, 5, 18, 62);
            add(getJPanelChangeMethod(), constraintsJPanelChangeMethod);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        // user code begin {2}
        add(getStatEditorPanel().getJPanelData(), holderConstraints());
        getJComboBoxWhenChange().setSelectedItem( LMProgramDirectGear.CHANGE_NONE );
        try
        {
            initConnections();
        }
        catch(Exception e)	{ }
        // user code end
    }
    /**
     * Comment
     */
    public void jComboBoxWhenChange_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
    {
        /*
         *This is a sloppy fix for the problem that occurred when there were multiple
         *thermostat gears for the same program.  Field values were carrying over to
         *other gears and getting accidentally saved into the db. 
         */
        getStatEditorPanel().getJTextFieldDeltaB().setText("");
        getStatEditorPanel().getJTextFieldDeltaD().setText("");
        getStatEditorPanel().getJTextFieldDeltaF().setText("");
        getStatEditorPanel().getJTextFieldRand().setText("");
        getStatEditorPanel().getJTextFieldMax().setText("");
        getStatEditorPanel().getJTextFieldMin().setText("");
        getStatEditorPanel().getJTextFieldTa().setText("");
        getStatEditorPanel().getJTextFieldTb().setText("");
        getStatEditorPanel().getJTextFieldTc().setText("");
        getStatEditorPanel().getJTextFieldTd().setText("");
        getStatEditorPanel().getJTextFieldTe().setText("");
        getStatEditorPanel().getJTextFieldTf().setText("");
        getStatEditorPanel().getJCheckBoxDeltaB().setSelected(false);
        getStatEditorPanel().getJCheckBoxDeltaD().setSelected(false);
        getStatEditorPanel().getJCheckBoxDeltaF().setSelected(false);
        getStatEditorPanel().getJCheckBoxRand().setSelected(false);
        getStatEditorPanel().getJCheckBoxMax().setSelected(false);
        getStatEditorPanel().getJCheckBoxMin().setSelected(false);
        getStatEditorPanel().getJCheckBoxTa().setSelected(false);
        getStatEditorPanel().getJCheckBoxTb().setSelected(false);
        getStatEditorPanel().getJCheckBoxTc().setSelected(false);
        getStatEditorPanel().getJCheckBoxTd().setSelected(false);
        getStatEditorPanel().getJCheckBoxTe().setSelected(false);
        getStatEditorPanel().getJCheckBoxTf().setSelected(false);

        getJLabelChangeDuration().setVisible(false);
        getJCSpinFieldChangeDuration().setVisible(false);
        getJLabelMinutesChDur().setVisible(false);

        getJLabelChangePriority().setVisible(false);
        getJCSpinFieldChangePriority().setVisible(false);

        getJLabelChangeTriggerNumber().setVisible(false);
        getJCSpinFieldChangeTriggerNumber().setVisible(false);

        getJLabelChangeTriggerOffset().setVisible(false);
        getJTextFieldChangeTriggerOffset().setVisible(false);


        if( getJComboBoxWhenChange().getSelectedItem().toString().equalsIgnoreCase( LMProgramDirectGear.CHANGE_NONE )
                || getJComboBoxWhenChange().getSelectedItem().toString().equalsIgnoreCase( "Manually Only" ) )
        {
            //None
            return;
        }
        else if( getJComboBoxWhenChange().getSelectedItem().toString().equalsIgnoreCase( LMProgramDirectGear.CHANGE_DURATION )
                || getJComboBoxWhenChange().getSelectedItem().toString().equalsIgnoreCase( "After a Duration" ) )
        {
            //Duration
            getJLabelChangeDuration().setVisible(true);
            getJCSpinFieldChangeDuration().setVisible(true);
            getJLabelMinutesChDur().setVisible(true);
        }
        else if( getJComboBoxWhenChange().getSelectedItem().toString().equalsIgnoreCase( LMProgramDirectGear.CHANGE_PRIORITY )
                || getJComboBoxWhenChange().getSelectedItem().toString().equalsIgnoreCase( "Priority Change" ) )
        {
            //Priority
            getJLabelChangePriority().setVisible(true);
            getJCSpinFieldChangePriority().setVisible(true);
        }
        else if( getJComboBoxWhenChange().getSelectedItem().toString().equalsIgnoreCase( LMProgramDirectGear.CHANGE_TRIGGER_OFFSET )
                || getJComboBoxWhenChange().getSelectedItem().toString().equalsIgnoreCase( "Above Trigger" ) )
        {
            //TriggerOffset
            getJLabelChangeTriggerNumber().setVisible(true);
            getJCSpinFieldChangeTriggerNumber().setVisible(true);

            getJLabelChangeTriggerOffset().setVisible(true);
            getJTextFieldChangeTriggerOffset().setVisible(true);
        }
        else
            throw new Error("Unknown LMProgramDirectGear control condition found, the value = " + getJComboBoxWhenChange().getSelectedItem().toString() );


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
            ThermostatSetbackGearPanel aThermostatSetbackGearPanel;
            aThermostatSetbackGearPanel = new ThermostatSetbackGearPanel();
            frame.setContentPane(aThermostatSetbackGearPanel);
            frame.setSize(aThermostatSetbackGearPanel.getSize());
            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                };
            });
            frame.show();
            java.awt.Insets insets = frame.getInsets();
            frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
            frame.setVisible(true);
        } catch (Throwable exception) {
            System.err.println("Exception occurred in main() of com.cannontech.dbeditor.wizard.device.lmprogram.GenericGearPanel");
            exception.printStackTrace(System.out);
        }
    }

    /**
     * Insert the method's description here.
     * Creation date: (7/15/2002 2:54:59 PM)
     * @param newStatEditorPanel javax.swing.JPanel
     */
    public void setStatEditorPanel(com.cannontech.dbeditor.wizard.device.lmprogram.LMExpressStatEditorPanel newStatEditorPanel) {
        statEditorPanel = newStatEditorPanel;
    }
    /**
     * setValue method comment.
     */
    @Override
    public void setValue(Object o) 
    {
        LMProgramDirectGear gear = null;

        if (o == null) {
            return;
        }

        gear = (LMProgramDirectGear)o;

        getJComboBoxHowToStop().setSelectedItem( StringUtils.addCharBetweenWords( ' ', gear.getMethodStopType() ) );

        getJCSpinFieldPercentReduction().setValue( gear.getPercentReduction() );

        setChangeCondition(getJComboBoxWhenChange(), gear.getChangeCondition() );

        getJCSpinFieldChangeDuration().setValue( new Integer( gear.getChangeDuration().intValue() / 60 ) );
        getJCSpinFieldChangePriority().setValue( gear.getChangePriority() );
        getJCSpinFieldChangeTriggerNumber().setValue( gear.getChangeTriggerNumber() );	
        getJTextFieldChangeTriggerOffset().setText( gear.getChangeTriggerOffset().toString() );

        com.cannontech.database.data.device.lm.ThermostatSetbackGear ts = (com.cannontech.database.data.device.lm.ThermostatSetbackGear)gear;


        if(ts.getValueB().intValue() != 0)
        {
            getStatEditorPanel().getJCheckBoxDeltaB().setSelected(true);
            getStatEditorPanel().getJTextFieldDeltaB().setText(ts.getValueB().toString());
        }
        if(ts.getValueD().intValue() != 0)
        {
            getStatEditorPanel().getJCheckBoxDeltaD().setSelected(true);
            getStatEditorPanel().getJTextFieldDeltaD().setText(ts.getValueD().toString());
        }

        if(ts.getValueF().intValue() != 0)
        {
            getStatEditorPanel().getJCheckBoxDeltaF().setSelected(true);
            getStatEditorPanel().getJTextFieldDeltaF().setText(ts.getValueF().toString());
        }
        if(ts.getRandom().intValue() != 0)
        {
            getStatEditorPanel().getJCheckBoxRand().setSelected(true);
            getStatEditorPanel().getJTextFieldRand().setText(ts.getRandom().toString());
        }
        if(ts.getMaxValue().intValue() != 0)
        {
            getStatEditorPanel().getJCheckBoxMax().setSelected(true);
            getStatEditorPanel().getJTextFieldMax().setText(ts.getMaxValue().toString());
        }

        if(ts.getMinValue().intValue() != 0)
        {
            getStatEditorPanel().getJCheckBoxMin().setSelected(true);
            getStatEditorPanel().getJTextFieldMin().setText(ts.getMinValue().toString());
        }

        if(ts.getValueTa().intValue() != 0)
        {
            getStatEditorPanel().getJCheckBoxTa().setSelected(true);
            getStatEditorPanel().getJTextFieldTa().setText(ts.getValueTa().toString());
        }

        if(ts.getValueTb().intValue() != 0)
        {
            getStatEditorPanel().getJCheckBoxTb().setSelected(true);
            getStatEditorPanel().getJTextFieldTb().setText(ts.getValueTb().toString());
        }

        if(ts.getValueTc().intValue() != 0)
        {
            getStatEditorPanel().getJCheckBoxTc().setSelected(true);
            getStatEditorPanel().getJTextFieldTc().setText(ts.getValueTc().toString());
        }

        if(ts.getValueTd().intValue() != 0)
        {
            getStatEditorPanel().getJCheckBoxTd().setSelected(true);
            getStatEditorPanel().getJTextFieldTd().setText(ts.getValueTd().toString());
        }

        if(ts.getValueTe().intValue() != 0)
        {
            getStatEditorPanel().getJCheckBoxTe().setSelected(true);
            getStatEditorPanel().getJTextFieldTe().setText(ts.getValueTe().toString());
        }

        if(ts.getValueTf().intValue() != 0)
        {
            getStatEditorPanel().getJCheckBoxTf().setSelected(true);
            getStatEditorPanel().getJTextFieldTf().setText(ts.getValueTf().toString());
        }

        if(ts.getSettings().charAt(0) == 'A')
        {
            getStatEditorPanel().getJButtonDeltasAbsolute().setText("Absolutes");
            getStatEditorPanel().getJCheckBoxDeltaB().setText("Abs B:");
            getStatEditorPanel().getJCheckBoxDeltaD().setText("Abs D:");
            getStatEditorPanel().getJCheckBoxDeltaF().setText("Abs F:");
            getStatEditorPanel().isAbsolute = true;
        }
        else
        {
            getStatEditorPanel().getJButtonDeltasAbsolute().setText("Deltas");
            getStatEditorPanel().getJCheckBoxDeltaB().setText("Delta B:");
            getStatEditorPanel().getJCheckBoxDeltaD().setText("Delta D:");
            getStatEditorPanel().getJCheckBoxDeltaF().setText("Delta F:");
            getStatEditorPanel().isAbsolute = false;
        }

        if(ts.getSettings().charAt(1) == 'C')
        {
            getStatEditorPanel().getJButtonFahrenheitCelsius().setText("Celsius");
            getStatEditorPanel().isCelsius = true;
        }

        else
        {
            getStatEditorPanel().getJButtonFahrenheitCelsius().setText("Fahrenheit");
            getStatEditorPanel().isCelsius = false;
        }

        getStatEditorPanel().getJCheckBoxHeatMode().setSelected(ts.getSettings().charAt(2) == 'H');

        //I for "icy goodness"
        getStatEditorPanel().getJCheckBoxCoolMode().setSelected(ts.getSettings().charAt(3) == 'I');

    }

    @Override
    public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) 
    {
        //fire this event for all JCSpinFields!!
        this.fireInputUpdate();
    }
    /* (non-Javadoc)
     * @see com.cannontech.common.gui.util.DataInputPanelListener#inputUpdate(com.cannontech.common.editor.PropertyPanelEvent)
     */
    public void inputUpdate(PropertyPanelEvent event) {
        fireInputUpdate();	
    }
}
