package com.cannontech.dbeditor.wizard.device.lmprogram;

import static com.cannontech.common.util.StringUtils.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.util.InputMismatchException;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.event.CaretEvent;

import com.cannontech.common.gui.unchanging.DoubleRangeDocument;
import com.cannontech.common.util.StringUtils;
import com.cannontech.database.data.device.lm.EcobeeCycleGear;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.klg.jclass.field.DataProperties;
import com.klg.jclass.field.JCInvalidInfo;
import com.klg.jclass.field.JCSpinField;
import com.klg.jclass.field.validate.JCIntegerValidator;
import com.klg.jclass.util.value.JCValueEvent;
import com.klg.jclass.util.value.MutableValueModel;

public class EcobeeCycleGearPanel extends GenericGearPanel {

    private static final long serialVersionUID = 1L;
    
    private JComboBox<String> ivjJComboBoxHowToStop;
    private JComboBox<String> ivjJComboBoxWhenChange;
    private JCSpinField changeDurationSpinField;
    private JCSpinField changePrioritySpinField;
    private JCSpinField changeTriggerNumberSpinField;
    private JCSpinField controlCyclePercentSpinField;
    private JCSpinField percentReductionSpinField;
    private JLabel changeDurationLabel;
    private JLabel changePriorityLabel;
    private JLabel changeTriggerNumberLabel;
    private JLabel changeTriggerOffsetLabel;
    private JLabel controlCyclePercentLabel;
    private JLabel howToStopLabel;
    private JLabel minutesChangeDurationLabel;
    private JLabel percentReductionLabel;
    private JLabel whenToChangeLabel;
    private JPanel changeMethodPanel;
    private JTextField changeTriggerOffsetTextField;
    private JTextField kwReductionTextField;
    private JCheckBox checkBoxRampIn;
    private JCheckBox checkBoxRampOut;
    private JCheckBox checkBoxMandatory;

    public EcobeeCycleGearPanel() {
        initialize();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == getJComboBoxWhenChange()) {
            jComboBoxWhenChange_ActionPerformed(e);
        } else {
            fireInputUpdate();
        }
    }

    @Override
    public void caretUpdate(CaretEvent e) {
        fireInputUpdate();
    }
    
    private JCheckBox getCheckBoxRampIn() {
        if (checkBoxRampIn == null) {
            checkBoxRampIn = new JCheckBox();
            checkBoxRampIn.setText("Ramp In");
            checkBoxRampIn.setPreferredSize(new Dimension(165, 23));
            checkBoxRampIn.setSelected(true);
        }
        return checkBoxRampIn;
    }
    
    private JCheckBox getCheckBoxMandatory() {
        if (checkBoxMandatory == null) {
            checkBoxMandatory = new JCheckBox();
            checkBoxMandatory.setText(LMProgramDirectGear.OPTION_MANDATORY);
            checkBoxMandatory.setPreferredSize(new Dimension(165, 23));
            checkBoxMandatory.setSelected(false);
        }
        return checkBoxMandatory;
    }
    
    private JCheckBox getCheckBoxRampOut() {
        if (checkBoxRampOut == null) {
            checkBoxRampOut = new JCheckBox();
            checkBoxRampOut.setText("Ramp Out");
            checkBoxRampOut.setPreferredSize(new Dimension(165, 23));
            checkBoxRampOut.setSelected(true);
        }
        return checkBoxRampOut;
    }

    private JComboBox<String> getJComboBoxHowToStop() {
        if (ivjJComboBoxHowToStop == null) {
            try {
                ivjJComboBoxHowToStop = new JComboBox<String>();
                ivjJComboBoxHowToStop.setName("JComboBoxHowToStop");
                ivjJComboBoxHowToStop.setPreferredSize(new Dimension(95, 23));
                ivjJComboBoxHowToStop.setAlignmentY(TOP_ALIGNMENT);
                ivjJComboBoxHowToStop.addItem(StringUtils.addCharBetweenWords(' ', LMProgramDirectGear.STOP_RESTORE));
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJComboBoxHowToStop;
    }

    private JTextField getJTextFieldKWReduction() {
        if (kwReductionTextField == null) {
            try {
                kwReductionTextField = new JTextField();
                kwReductionTextField.setName("JTextFieldKWReduction");
                kwReductionTextField.setPreferredSize(new Dimension(70, 22));
                kwReductionTextField.setAlignmentX(LEFT_ALIGNMENT);
                kwReductionTextField.setAlignmentY(TOP_ALIGNMENT);
                kwReductionTextField.setDocument(new DoubleRangeDocument(0.0, 99999.999, 3));
                kwReductionTextField.setVisible(false);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return kwReductionTextField;
    }

    private JComboBox<String> getJComboBoxWhenChange() {
        if (ivjJComboBoxWhenChange == null) {
            try {
                ivjJComboBoxWhenChange = new JComboBox<String>();
                ivjJComboBoxWhenChange.setName("JComboBoxWhenChange");
                ivjJComboBoxWhenChange.setPreferredSize(new Dimension(205, 23));
                ivjJComboBoxWhenChange.setAlignmentY(TOP_ALIGNMENT);
                ivjJComboBoxWhenChange.addItem("Manually Only");
                ivjJComboBoxWhenChange.addItem("After a Duration");
                ivjJComboBoxWhenChange.addItem("Priority Change");
                ivjJComboBoxWhenChange.addItem("Above Trigger");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJComboBoxWhenChange;
    }
    
    private JCSpinField getJCSpinFieldChangeDuration() {
        if (changeDurationSpinField == null) {
            try {
                changeDurationSpinField = new JCSpinField();
                changeDurationSpinField.setName("JCSpinFieldChangeDuration");
                changeDurationSpinField.setPreferredSize(new Dimension(35, 20));
                changeDurationSpinField.setAlignmentY(TOP_ALIGNMENT);
                changeDurationSpinField.setMaximumSize(new Dimension(40, 20));
                changeDurationSpinField.setDataProperties(new DataProperties(
                        new JCIntegerValidator(null, 0, 99999, null, true, null, 1, "#,##0.###;-#,##0.###", false,
                                false, false, null, 3),
                        new MutableValueModel(Integer.class, 0),
                        new JCInvalidInfo(true, 2, new Color(0, 0, 0, 255), new Color(255, 255, 255, 255))));
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return changeDurationSpinField;
    }

    private JCSpinField getJCSpinFieldChangePriority() {
        if (changePrioritySpinField == null) {
            try {
                changePrioritySpinField = new JCSpinField();
                changePrioritySpinField.setName("JCSpinFieldChangePriority");
                changePrioritySpinField.setPreferredSize(new Dimension(30, 20));
                changePrioritySpinField.setAlignmentY(TOP_ALIGNMENT);
                changePrioritySpinField.setMaximumSize(new Dimension(40, 30));
                changePrioritySpinField.setDataProperties(new DataProperties(
                        new JCIntegerValidator(null, 0, 9999, null, true, null, 1, "#,##0.###;-#,##0.###", false,
                                false, false, null, 0),
                        new MutableValueModel(Integer.class, 0),
                        new JCInvalidInfo(true, 2, new Color(0, 0, 0, 255), new Color(255, 255, 255, 255))));
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return changePrioritySpinField;
    }

    private JCSpinField getJCSpinFieldChangeTriggerNumber() {
        if (changeTriggerNumberSpinField == null) {
            try {
                changeTriggerNumberSpinField = new JCSpinField();
                changeTriggerNumberSpinField.setName("JCSpinFieldChangeTriggerNumber");
                changeTriggerNumberSpinField.setPreferredSize(new Dimension(35, 20));
                changeTriggerNumberSpinField.setAlignmentY(TOP_ALIGNMENT);
                changeTriggerNumberSpinField.setMaximumSize(new Dimension(40, 20));
                changeTriggerNumberSpinField.setDataProperties(new DataProperties(
                        new JCIntegerValidator(null, 1, 99999, null, true, null, 1, "#,##0.###;-#,##0.###",
                                false, false, false, null, 1),
                        new MutableValueModel(Integer.class, 0),
                        new JCInvalidInfo(true, 2, new Color(0, 0, 0, 255), new Color(255, 255, 255, 255))));
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return changeTriggerNumberSpinField;
    }

    private JCSpinField getJCSpinFieldControlCyclePercent() {
        if (controlCyclePercentSpinField == null) {
            try {
                controlCyclePercentSpinField = new JCSpinField();
                controlCyclePercentSpinField.setName("JCSpinFieldControlCyclePercent");
                controlCyclePercentSpinField.setPreferredSize(new Dimension(48, 20));
                controlCyclePercentSpinField.setAlignmentX(LEFT_ALIGNMENT);
                controlCyclePercentSpinField.setAlignmentY(TOP_ALIGNMENT);
                controlCyclePercentSpinField.setMaximumSize(new Dimension(50, 20));
                controlCyclePercentSpinField.setMinimumSize(new Dimension(48, 20));
                controlCyclePercentSpinField.setDataProperties(new DataProperties(
                        new JCIntegerValidator(null, 5, 100, null, true, null, 1, "#,##0.###;-#,##0.###", false,
                                false, false, null, 50),
                        new MutableValueModel(Integer.class, 0),
                        new JCInvalidInfo(true, 2, new Color(0, 0, 0,255), new Color(255, 255, 255, 255))));

            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return controlCyclePercentSpinField;
    }

    private JCSpinField getJCSpinFieldPercentReduction() {
        if (percentReductionSpinField == null) {
            try {
                percentReductionSpinField = new JCSpinField();
                percentReductionSpinField.setName("JCSpinFieldPercentReduction");
                percentReductionSpinField.setPreferredSize(new Dimension(49, 20));
                percentReductionSpinField.setMaximumSize(new Dimension(50, 60));
                percentReductionSpinField.setMinimumSize(new Dimension(40, 50));
                percentReductionSpinField.setDataProperties(new DataProperties(
                        new JCIntegerValidator(null, 0, 100, null, true, null, 1, "#,##0.###;-#,##0.###", false,
                                false, false, null,100),
                        new MutableValueModel(Integer.class, 0),
                        new JCInvalidInfo(true,2, new Color(0, 0, 0, 255), new Color(255, 255, 255, 255))));

                percentReductionSpinField.setValue(100);

            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return percentReductionSpinField;
    }

    private JLabel getJLabelChangeDuration() {
        if (changeDurationLabel == null) {
            try {
                changeDurationLabel = new JLabel();
                changeDurationLabel.setName("JLabelChangeDuration");
                changeDurationLabel.setAlignmentY(TOP_ALIGNMENT);
                changeDurationLabel.setText("Change Duration:");
                changeDurationLabel.setMaximumSize(new Dimension(103, 14));
                changeDurationLabel.setPreferredSize(new Dimension(103, 14));
                changeDurationLabel.setFont(new Font("dialog", 0, 12));
                changeDurationLabel.setMinimumSize(new Dimension(103, 14));
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return changeDurationLabel;
    }

    private JLabel getJLabelChangePriority() {
        if (changePriorityLabel == null) {
            try {
                changePriorityLabel = new JLabel();
                changePriorityLabel.setName("JLabelChangePriority");
                changePriorityLabel.setAlignmentY(TOP_ALIGNMENT);
                changePriorityLabel.setText("Change Priority:");
                changePriorityLabel.setMaximumSize(new Dimension(103, 14));
                changePriorityLabel.setPreferredSize(new Dimension(103, 14));
                changePriorityLabel.setFont(new Font("dialog", 0, 12));
                changePriorityLabel.setMinimumSize(new Dimension(103, 14));
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return changePriorityLabel;
    }

    private JLabel getJLabelChangeTriggerNumber() {
        if (changeTriggerNumberLabel == null) {
            try {
                changeTriggerNumberLabel = new JLabel();
                changeTriggerNumberLabel.setName("JLabelChangeTriggerNumber");
                changeTriggerNumberLabel.setAlignmentY(TOP_ALIGNMENT);
                changeTriggerNumberLabel.setText("Trigger Number:");
                changeTriggerNumberLabel.setMaximumSize(new Dimension(143, 14));
                changeTriggerNumberLabel.setHorizontalTextPosition(SwingConstants.LEFT);
                changeTriggerNumberLabel.setPreferredSize(new Dimension(143, 14));
                changeTriggerNumberLabel.setFont(new Font("dialog", 0, 12));
                changeTriggerNumberLabel.setMinimumSize(new Dimension(143, 14));
                changeTriggerNumberLabel.setHorizontalAlignment(SwingConstants.LEFT);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return changeTriggerNumberLabel;
    }

    private JLabel getJLabelChangeTriggerOffset() {
        if (changeTriggerOffsetLabel == null) {
            try {
                changeTriggerOffsetLabel = new JLabel();
                changeTriggerOffsetLabel.setName("JLabelChangeTriggerOffset");
                changeTriggerOffsetLabel.setAlignmentY(TOP_ALIGNMENT);
                changeTriggerOffsetLabel.setText("Trigger Offset:");
                changeTriggerOffsetLabel.setMaximumSize(new Dimension(143, 14));
                changeTriggerOffsetLabel.setHorizontalTextPosition(SwingConstants.LEFT);
                changeTriggerOffsetLabel.setPreferredSize(new Dimension(143, 14));
                changeTriggerOffsetLabel.setFont(new Font("dialog", 0, 12));
                changeTriggerOffsetLabel.setMinimumSize(new Dimension(143, 14));
                changeTriggerOffsetLabel.setHorizontalAlignment(SwingConstants.LEFT);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return changeTriggerOffsetLabel;
    }

    private JLabel getJLabelControlCyclePercent() {
        if (controlCyclePercentLabel == null) {
            try {
                controlCyclePercentLabel = new JLabel();
                controlCyclePercentLabel.setName("JLabelControlCyclePercent");
                controlCyclePercentLabel.setAlignmentY(TOP_ALIGNMENT);
                controlCyclePercentLabel.setText("Control Percent:");
                controlCyclePercentLabel.setMaximumSize(new Dimension(112, 14));
                controlCyclePercentLabel.setPreferredSize(new Dimension(112, 14));
                controlCyclePercentLabel.setFont(new Font("dialog", 0, 12));
                controlCyclePercentLabel.setAlignmentX(LEFT_ALIGNMENT);
                controlCyclePercentLabel.setMinimumSize(new Dimension(112, 14));
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return controlCyclePercentLabel;
    }
    
    private JLabel getJLabelHowToStop() {
        if (howToStopLabel == null) {
            try {
                howToStopLabel = new JLabel();
                howToStopLabel.setName("JLabelHowToStop");
                howToStopLabel.setFont(new Font("dialog", 0, 12));
                howToStopLabel.setAlignmentY(TOP_ALIGNMENT);
                howToStopLabel.setText("How to Stop Control:");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return howToStopLabel;
    }

    private JLabel getJLabelMinutesChDur() {
        if (minutesChangeDurationLabel == null) {
            try {
                minutesChangeDurationLabel = new JLabel();
                minutesChangeDurationLabel.setName("JLabelMinutesChDur");
                minutesChangeDurationLabel.setFont(new Font("dialog", 0, 12));
                minutesChangeDurationLabel.setAlignmentY(TOP_ALIGNMENT);
                minutesChangeDurationLabel.setText("(min.)");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return minutesChangeDurationLabel;
    }

    private JLabel getJLabelPercentReduction() {
        if (percentReductionLabel == null) {
            try {
                percentReductionLabel = new JLabel();
                percentReductionLabel.setName("JLabelPercentReduction");
                percentReductionLabel.setAlignmentY(TOP_ALIGNMENT);
                percentReductionLabel.setText("Group Capacity Reduction %:");
                percentReductionLabel.setMaximumSize(new Dimension(160, 14));
                percentReductionLabel.setPreferredSize(new Dimension(160, 14));
                percentReductionLabel.setFont(new Font("dialog", 0, 12));
                percentReductionLabel.setMinimumSize(new Dimension(160, 14));
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return percentReductionLabel;
    }

    private JLabel getJLabelWhenChange() {
        if (whenToChangeLabel == null) {
            try {
                whenToChangeLabel = new JLabel();
                whenToChangeLabel.setName("JLabelWhenChange");
                whenToChangeLabel.setFont(new Font("dialog", 0, 12));
                whenToChangeLabel.setAlignmentY(TOP_ALIGNMENT);
                whenToChangeLabel.setText("When to Change:");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return whenToChangeLabel;
    }

    private JPanel getJPanelChangeMethod() {
        if (changeMethodPanel == null) {
            try {
                changeMethodPanel = new JPanel();
                changeMethodPanel.setName("JPanelChangeMethod");
                changeMethodPanel.setLayout(new GridBagLayout());
                changeMethodPanel.setAlignmentY(TOP_ALIGNMENT);
                changeMethodPanel.setMaximumSize(new Dimension(300, 75));
                changeMethodPanel.setPreferredSize(new Dimension(300, 75));
                changeMethodPanel.setFont(new Font("dialog", 0, 12));
                changeMethodPanel.setAlignmentX(LEFT_ALIGNMENT);

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
                changeMethodPanel.setBorder(new EtchedBorder());
                jComboBoxWhenChange_ActionPerformed(null);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return changeMethodPanel;
    }

    private JTextField getJTextFieldChangeTriggerOffset() {
        if (changeTriggerOffsetTextField == null) {
            try {
                changeTriggerOffsetTextField = new JTextField();
                changeTriggerOffsetTextField.setName("JTextFieldChangeTriggerOffset");
                changeTriggerOffsetTextField.setPreferredSize(new Dimension(30, 20));
                changeTriggerOffsetTextField.setAlignmentY(TOP_ALIGNMENT);
                changeTriggerOffsetTextField.setMaximumSize(new Dimension(40, 20));
                changeTriggerOffsetTextField.setDocument(new DoubleRangeDocument(-99999.9999, 99999.9999, 4));
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return changeTriggerOffsetTextField;
    }

    @Override
    public Object getValue(Object gearObj) {
        EcobeeCycleGear gear = (EcobeeCycleGear) gearObj;

        if (getJComboBoxHowToStop().getSelectedItem() != null) {
            gear.setMethodStopType(removeChars(' ', getJComboBoxHowToStop().getSelectedItem().toString()));
        }

        gear.setPercentReduction(toInteger(getJCSpinFieldPercentReduction().getValue()));

        gear.setChangeCondition(getChangeCondition(getJComboBoxWhenChange().getSelectedItem().toString()));

        gear.setChangeDuration(toInteger(getJCSpinFieldChangeDuration().getValue()) * 60);
        gear.setChangePriority(toInteger(getJCSpinFieldChangePriority().getValue()));
        gear.setChangeTriggerNumber(toInteger(getJCSpinFieldChangeTriggerNumber().getValue()));

        if (getJTextFieldChangeTriggerOffset().getText() == null || 
                getJTextFieldChangeTriggerOffset().getText().length() <= 0) {
            gear.setChangeTriggerOffset(0.0);
        } else {
            gear.setChangeTriggerOffset(Double.valueOf(getJTextFieldChangeTriggerOffset().getText()));
        }
        gear.setControlCyclePercent(toInteger(getJCSpinFieldControlCyclePercent().getValue()));
        gear.setFrontRampEnabled(getCheckBoxRampIn().isSelected());
        gear.setMethodOptionType(getCheckBoxMandatory().isSelected());
        gear.setBackRampEnabled(getCheckBoxRampOut().isSelected());
        
        return gear;
    }

    
    private Integer toInteger(Object o) throws ClassCastException {
        return ((Number)o).intValue();
    }
    
    private void handleException(Throwable exception) {
        System.out.print(exception.getMessage());
        System.out.println("--------- UNCAUGHT EXCEPTION ---------");
        exception.printStackTrace(System.out);
    }

    private void initConnections() {
        getJCSpinFieldChangeDuration().addValueListener(this);
        getJCSpinFieldChangePriority().addValueListener(this);
        getJCSpinFieldChangeTriggerNumber().addValueListener(this);
        getJCSpinFieldControlCyclePercent().addValueListener(this);
        getJCSpinFieldPercentReduction().addValueListener(this);
        getJComboBoxWhenChange().addActionListener(this);
        getJComboBoxHowToStop().addActionListener(this);
        getJTextFieldChangeTriggerOffset().addCaretListener(this);
        getJTextFieldKWReduction().addCaretListener(this);
        getCheckBoxRampIn().addActionListener(this);
        getCheckBoxMandatory().addActionListener(this);
        getCheckBoxRampOut().addActionListener(this);
    }

    private void initialize() {
        try {
            setName("EcobeeCycleGearPanel");
            
            GridBagConstraints constraintJLabelControlCyclePercent = new GridBagConstraints();
            GridBagConstraints constraintJCSpinFieldControlCyclePercent = new GridBagConstraints();
            GridBagConstraints constraintJPanelChangeMethod = new GridBagConstraints();
            GridBagConstraints constraintJLabelPercentReduction = new GridBagConstraints();
            GridBagConstraints constraintJLabelHowToStop = new GridBagConstraints();
            GridBagConstraints constraintJCSpinFieldPercentReduction = new GridBagConstraints();
            GridBagConstraints constraintJComboBoxHowToStop = new GridBagConstraints();
            GridBagConstraints constraintJCheckBoxRampIn = new GridBagConstraints();
            GridBagConstraints constraintJCheckBoxMandatory = new GridBagConstraints();
            GridBagConstraints constraintJCheckBoxRampOut = new GridBagConstraints();
            
            constraintJCheckBoxMandatory.insets = new Insets(0, 0, 5, 0);
            constraintJCheckBoxMandatory.ipady = -3;
            constraintJCheckBoxMandatory.ipadx = 200;
            constraintJCheckBoxMandatory.gridwidth = 3;
            constraintJCheckBoxMandatory.anchor = GridBagConstraints.WEST;
            constraintJCheckBoxMandatory.gridy = 0;
            constraintJCheckBoxMandatory.gridx = 1;

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
            
            
            constraintJLabelControlCyclePercent.insets = new Insets(0, 0, 5, 5);
            constraintJLabelControlCyclePercent.anchor = GridBagConstraints.WEST;
            constraintJLabelControlCyclePercent.gridy = 3;
            constraintJLabelControlCyclePercent.gridx = 1;

            constraintJCSpinFieldControlCyclePercent.insets = new Insets(0, 0, 5, 5);
            constraintJCSpinFieldControlCyclePercent.anchor = GridBagConstraints.WEST;
            constraintJCSpinFieldControlCyclePercent.gridy = 3;
            constraintJCSpinFieldControlCyclePercent.gridx = 2;

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
            this.add(getCheckBoxRampIn(), constraintJCheckBoxRampIn);
            this.add(getCheckBoxMandatory(), constraintJCheckBoxMandatory);
            this.add(getCheckBoxRampOut(), constraintJCheckBoxRampOut);
            this.add(getJLabelControlCyclePercent(), constraintJLabelControlCyclePercent);
            this.add(getJCSpinFieldControlCyclePercent(), constraintJCSpinFieldControlCyclePercent);
            this.add(getJPanelChangeMethod(), constraintJPanelChangeMethod);
            this.add(getJLabelPercentReduction(), constraintJLabelPercentReduction);
            this.add(getJLabelHowToStop(), constraintJLabelHowToStop);
            this.add(getJComboBoxHowToStop(), constraintJComboBoxHowToStop);
            this.add(getJCSpinFieldPercentReduction(), constraintJCSpinFieldPercentReduction);
        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }
        getJComboBoxWhenChange().setSelectedItem(LMProgramDirectGear.CHANGE_NONE);
        getJComboBoxHowToStop().setSelectedItem(StringUtils.addCharBetweenWords(' ', LMProgramDirectGear.STOP_TIME_IN));
        
        initConnections();
    }

    @Override
    public void jComboBoxWhenChange_ActionPerformed(ActionEvent actionEvent) {
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
            throw new InputMismatchException("Unknown LMProgramDirectGear control condition found, the value = "
                            + getJComboBoxWhenChange().getSelectedItem().toString());

        fireInputUpdate();
        return;
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

    @Override
    public void setValue(Object gearObj) {
        if (gearObj == null) {
            return;
        }

        EcobeeCycleGear gear = (EcobeeCycleGear) gearObj;

        getJComboBoxHowToStop().setSelectedItem(StringUtils.addCharBetweenWords(' ', gear.getMethodStopType()));

        getJCSpinFieldPercentReduction().setValue(gear.getPercentReduction());

        setChangeCondition(gear.getChangeCondition());

        getJCSpinFieldChangeDuration().setValue(gear.getChangeDuration() / 60);
        getJCSpinFieldChangePriority().setValue(gear.getChangePriority());
        getJCSpinFieldChangeTriggerNumber().setValue(gear.getChangeTriggerNumber());
        final DecimalFormat format = new DecimalFormat("#####.####");
        getJTextFieldChangeTriggerOffset().setText(format.format(gear.getChangeTriggerOffset()));
        getJCSpinFieldControlCyclePercent().setValue(gear.getControlCyclePercent());
        getCheckBoxRampIn().setSelected(gear.isFrontRampEnabled());
        getCheckBoxMandatory().setText(LMProgramDirectGear.OPTION_MANDATORY);
        getCheckBoxMandatory().setSelected(gear.isMandatorySelected(gear.getMethodOptionType()));
        getCheckBoxRampOut().setSelected(gear.isBackRampEnabled());
    }

    @Override
    public void valueChanged(JCValueEvent valueEvent) {
        fireInputUpdate();
    }

}
