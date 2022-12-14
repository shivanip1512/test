package com.cannontech.dbeditor.wizard.device.lmprogram;

import static com.cannontech.common.util.StringUtils.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.InputMismatchException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.event.CaretEvent;

import com.cannontech.common.gui.unchanging.DoubleRangeDocument;
import com.cannontech.common.util.StringUtils;
import com.cannontech.database.data.device.lm.SepTemperatureOffsetGear;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.klg.jclass.field.DataProperties;
import com.klg.jclass.field.JCInvalidInfo;
import com.klg.jclass.field.JCSpinField;
import com.klg.jclass.field.validate.JCDoubleValidator;
import com.klg.jclass.field.validate.JCIntegerValidator;
import com.klg.jclass.util.value.JCValueEvent;
import com.klg.jclass.util.value.JCValueListener;
import com.klg.jclass.util.value.MutableValueModel;

public class SepTemperatureOffsetGearPanel extends GenericGearPanel {

    private static final long serialVersionUID = -3413404735483505763L;

    private JLabel offsetAmountLabel = null;
    private JLabel offsetUnitsLabel = null;
    private JLabel criticalityLabel = null;
    private JLabel howToStopLabel = null;
    private JLabel controlPercentLabel = null;
    private JButton temperatureUnitsButton = null;
    private JCheckBox checkBoxRampIn = null;
    private JCheckBox checkBoxRampOut = null;
    private JCheckBox checkBoxHeatingMode = null;
    private JCheckBox checkBoxCoolingMode = null;
    private JCSpinField offsetAmountSpinField = null;
    private JCSpinField criticalitySpinField = null;
    private JComboBox ivjJComboBoxHowToStop = null;
    private JCSpinField percentReductionSpinField = null;

    private JPanel changeMethodPanel = null;
    private JLabel whenToChangeLabel = null;
    private JLabel changeDurationLabel = null;
    private JLabel changePriorityLabel = null;
    private JLabel minutesChangeDurationLabel = null;
    private JLabel changeTriggerNumberLabel = null;
    private JLabel changeTriggerOffsetLabel = null;
    private JComboBox ivjJComboBoxWhenChange = null;
    private JTextField changeTriggerOffsetTextField = null;
    private JCSpinField changeDurationSpinField = null;
    private JCSpinField changePrioritySpinField = null;
    private JCSpinField changeTriggerNumberSpinField = null;

    private Boolean isFahrenheit = true;

    public SepTemperatureOffsetGearPanel() {
        super();
        initialize();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == getJComboBoxWhenChange()) {
            jComboBoxWhenChange_ActionPerformed(e);
        } else if (e.getSource() == getJButtonTemperatureUnits()) {
            jButtonTemperatureUnits_ActionPerformed(e);
        } else {
            fireInputUpdate();
        }
    }

    @Override
    public void caretUpdate(CaretEvent e) {
        fireInputUpdate();
    }

    // Panel Labels
    private JLabel getJLabelOffsetAmount() {
        if (offsetAmountLabel == null) {
            offsetAmountLabel = new JLabel();
            offsetAmountLabel.setName("JLabelOffsetAmount");
            offsetAmountLabel.setAlignmentY(TOP_ALIGNMENT);
            offsetAmountLabel.setText("Heating Offset:");
            offsetAmountLabel.setFont(new Font("dialog", 0, 12));
            offsetAmountLabel.setMaximumSize(new Dimension(143, 14));
            offsetAmountLabel.setPreferredSize(new Dimension(143, 14));
            offsetAmountLabel.setMinimumSize(new Dimension(143, 14));
            offsetAmountLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
            offsetAmountLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        }
        return offsetAmountLabel;
    }

    private JLabel getJLabelOffsetUnits() {
        if (offsetUnitsLabel == null) {
            offsetUnitsLabel = new JLabel();
            offsetUnitsLabel.setName("JLabelOffsetUnits");
            offsetUnitsLabel.setAlignmentY(TOP_ALIGNMENT);
            offsetUnitsLabel.setFont(new Font("dialog", 0, 12));
            offsetUnitsLabel.setMaximumSize(new Dimension(25, 14));
            offsetUnitsLabel.setPreferredSize(new Dimension(25, 14));
            offsetUnitsLabel.setMinimumSize(new Dimension(25, 14));
            offsetUnitsLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
            offsetUnitsLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        }
        return offsetUnitsLabel;
    }

    private JLabel getJLabelCriticality() {
        if (criticalityLabel == null) {
            criticalityLabel = new JLabel();
            criticalityLabel.setName("JLabelCoolingUnits");
            criticalityLabel.setAlignmentY(TOP_ALIGNMENT);
            criticalityLabel.setText("Criticality:");
            criticalityLabel.setFont(new Font("dialog", 0, 12));
            criticalityLabel.setMaximumSize(new Dimension(143, 14));
            criticalityLabel.setPreferredSize(new Dimension(143, 14));
            criticalityLabel.setMinimumSize(new Dimension(143, 14));
            criticalityLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
            criticalityLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        }
        return criticalityLabel;
    }

    private JLabel getJLabelHowToStop() {
        if (howToStopLabel == null) {
            try {
                howToStopLabel = new JLabel();
                howToStopLabel.setName("JLabelHowToStop");
                howToStopLabel.setAlignmentY(TOP_ALIGNMENT);
                howToStopLabel.setText("How to Stop Control:");
                howToStopLabel.setFont(new Font("dialog", 0, 12));
                howToStopLabel.setMaximumSize(new Dimension(143, 14));
                howToStopLabel.setPreferredSize(new Dimension(143, 14));
                howToStopLabel.setMinimumSize(new Dimension(143, 14));
                howToStopLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
                howToStopLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return howToStopLabel;
    }

    private JLabel getJLabelGroupCapacityReduction() {
        if (controlPercentLabel == null) {
            try {
                controlPercentLabel = new JLabel();
                controlPercentLabel.setName("JLabelGroupCapacityReduction");
                controlPercentLabel.setAlignmentY(TOP_ALIGNMENT);
                controlPercentLabel.setAlignmentX(LEFT_ALIGNMENT);
                controlPercentLabel.setText("Group Capacity Reduction %:");
                controlPercentLabel.setFont(new Font("dialog", 0, 12));
                controlPercentLabel.setMaximumSize(new Dimension(160, 14));
                controlPercentLabel.setPreferredSize(new Dimension(160, 14));
                controlPercentLabel.setMinimumSize(new Dimension(160, 14));
                controlPercentLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
                controlPercentLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return controlPercentLabel;
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
                changeTriggerNumberLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
                changeTriggerNumberLabel.setPreferredSize(new Dimension(143, 14));
                changeTriggerNumberLabel.setFont(new Font("dialog", 0, 12));
                changeTriggerNumberLabel.setMinimumSize(new Dimension(143, 14));
                changeTriggerNumberLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
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
                changeTriggerOffsetLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
                changeTriggerOffsetLabel.setPreferredSize(new Dimension(143, 14));
                changeTriggerOffsetLabel.setFont(new Font("dialog", 0, 12));
                changeTriggerOffsetLabel.setMinimumSize(new Dimension(143, 14));
                changeTriggerOffsetLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return changeTriggerOffsetLabel;
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

    private JButton getJButtonTemperatureUnits() {
        if (temperatureUnitsButton == null) {
            temperatureUnitsButton = new JButton();
            temperatureUnitsButton.setName("JButtonTemperatureUnits");
            temperatureUnitsButton.setText("\u00B0F / \u00B0C");
            temperatureUnitsButton.setFont(new Font("dialog", 0, 12));
            temperatureUnitsButton.setSize(42, 16);
            temperatureUnitsButton.setAlignmentY(TOP_ALIGNMENT);
        }
        return temperatureUnitsButton;

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

    private JCheckBox getCheckBoxRampOut() {
        if (checkBoxRampOut == null) {
            checkBoxRampOut = new JCheckBox();
            checkBoxRampOut.setText("Ramp Out");
            checkBoxRampOut.setPreferredSize(new Dimension(165, 23));
            checkBoxRampOut.setSelected(true);
        }
        return checkBoxRampOut;
    }

    private JCheckBox getCheckBoxHeatingMode() {
        if (checkBoxHeatingMode == null) {
            checkBoxHeatingMode = new JCheckBox();
            checkBoxHeatingMode.setText("Heating Mode");
            checkBoxHeatingMode.setPreferredSize(new Dimension(100, 23));
            checkBoxHeatingMode.setSelected(true);
        }
        return checkBoxHeatingMode;
    }

    private JCheckBox getCheckBoxCoolingMode() {
        if (checkBoxCoolingMode == null) {
            checkBoxCoolingMode = new JCheckBox();
            checkBoxCoolingMode.setText("Cooling Mode");
            checkBoxCoolingMode.setPreferredSize(new Dimension(165, 23));
            checkBoxCoolingMode.setSelected(false);
        }
        return checkBoxCoolingMode;
    }

    private JCSpinField getJCSpinFieldOffsetAmount() {
        if (offsetAmountSpinField == null) {
            try {
                offsetAmountSpinField = new JCSpinField();
                offsetAmountSpinField.setName("JCSpinFieldOffsetAmount");
                offsetAmountSpinField.setPreferredSize(new Dimension(48, 20));
                offsetAmountSpinField.setAlignmentX(LEFT_ALIGNMENT);
                offsetAmountSpinField.setAlignmentY(TOP_ALIGNMENT);
                offsetAmountSpinField.setMaximumSize(new Dimension(50, 20));
                offsetAmountSpinField.setMinimumSize(new Dimension(48, 20));
                JCDoubleValidator validator =
                    new JCDoubleValidator(null, SepTemperatureOffsetGear.MIN_FAHRENHEIT - 0.001,
                        SepTemperatureOffsetGear.MAX_FAHRENHEIT + 0.001, 0.1, "##.#", false, false, false, null, 0);
                validator.setEditPattern("#0.0");

                offsetAmountSpinField.setDataProperties(new DataProperties(validator, new MutableValueModel(
                    java.lang.Double.class, new Double(1.0)), new JCInvalidInfo(true, 2, new Color(0, 0, 0, 255),
                    new Color(255, 255, 255, 255))));

            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return offsetAmountSpinField;
    }

    private JCSpinField getJCSpinFieldCriticality() {
        if (criticalitySpinField == null) {
            try {
                criticalitySpinField = new JCSpinField();
                criticalitySpinField.setPreferredSize(new Dimension(48, 20));
                criticalitySpinField.setAlignmentX(LEFT_ALIGNMENT);
                criticalitySpinField.setAlignmentY(TOP_ALIGNMENT);
                criticalitySpinField.setMaximumSize(new Dimension(50, 20));
                criticalitySpinField.setMinimumSize(new Dimension(48, 20));
                criticalitySpinField.setDataProperties(new DataProperties(new JCIntegerValidator(null, new Integer(1),
                    new Integer(15), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false,
                    null, new Integer(6)), new MutableValueModel(java.lang.Integer.class, new Integer(0)),
                    new JCInvalidInfo(true, 2, new Color(0, 0, 0, 255), new Color(255, 255, 255, 255))));

            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return criticalitySpinField;
    }

    private JComboBox getJComboBoxHowToStop() {
        if (ivjJComboBoxHowToStop == null) {
            try {
                ivjJComboBoxHowToStop = new JComboBox();
                ivjJComboBoxHowToStop.setName("JComboBoxHowToStop");
                ivjJComboBoxHowToStop.setPreferredSize(new Dimension(100, 23));
                ivjJComboBoxHowToStop.setAlignmentY(TOP_ALIGNMENT);
                ivjJComboBoxHowToStop.addItem(StringUtils.addCharBetweenWords(' ', LMProgramDirectGear.STOP_TIME_IN));
                ivjJComboBoxHowToStop.addItem(StringUtils.addCharBetweenWords(' ', LMProgramDirectGear.STOP_RESTORE));
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJComboBoxHowToStop;
    }

    private JCSpinField getJCSpinFieldPercentReduction() {
        if (percentReductionSpinField == null) {
            try {
                percentReductionSpinField = new JCSpinField();
                percentReductionSpinField.setName("JCSpinFieldPercentReduction");
                percentReductionSpinField.setPreferredSize(new Dimension(49, 20));
                percentReductionSpinField.setMaximumSize(new Dimension(50, 60));
                percentReductionSpinField.setMinimumSize(new Dimension(40, 50));
                percentReductionSpinField.setDataProperties(new DataProperties(new JCIntegerValidator(null,
                    new Integer(0), new Integer(100), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false,
                    false, false, null, new Integer(100)), new MutableValueModel(java.lang.Integer.class,
                    new Integer(0)), new JCInvalidInfo(true, 2, new Color(0, 0, 0, 255), new Color(255, 255, 255, 255))));

                percentReductionSpinField.setValue(new Integer(100));

            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return percentReductionSpinField;
    }

    private JComboBox getJComboBoxWhenChange() {
        if (ivjJComboBoxWhenChange == null) {
            try {
                ivjJComboBoxWhenChange = new javax.swing.JComboBox();
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
                changeDurationSpinField.setDataProperties(new DataProperties(new JCIntegerValidator(null,
                    new Integer(0), new Integer(99999), null, true, null, new Integer(1), "#,##0.###;-#,##0.###",
                    false, false, false, null, new Integer(3)), new MutableValueModel(java.lang.Integer.class,
                    new Integer(0)), new JCInvalidInfo(true, 2, new Color(0, 0, 0, 255), new Color(255, 255, 255, 255))));
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
                changePrioritySpinField.setDataProperties(new DataProperties(new JCIntegerValidator(null,
                    new Integer(0), new Integer(9999), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false,
                    false, false, null, new Integer(0)),
                    new MutableValueModel(java.lang.Integer.class, new Integer(0)), new JCInvalidInfo(true, 2,
                        new Color(0, 0, 0, 255), new Color(255, 255, 255, 255))));
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
                changeTriggerNumberSpinField.setDataProperties(new DataProperties(new JCIntegerValidator(null,
                    new Integer(1), new Integer(99999), null, true, null, new Integer(1), "#,##0.###;-#,##0.###",
                    false, false, false, null, new Integer(1)), new MutableValueModel(java.lang.Integer.class,
                    new Integer(0)), new JCInvalidInfo(true, 2, new Color(0, 0, 0, 255), new Color(255, 255, 255, 255))));
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return changeTriggerNumberSpinField;
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
                getJPanelChangeMethod().add(getJTextFieldChangeTriggerOffset(),
                    constraintsJTextFieldChangeTriggerOffset);

                GridBagConstraints constraintsJCSpinFieldChangeTriggerNumber = new GridBagConstraints();
                constraintsJCSpinFieldChangeTriggerNumber.gridx = 2;
                constraintsJCSpinFieldChangeTriggerNumber.gridy = 3;
                constraintsJCSpinFieldChangeTriggerNumber.anchor = GridBagConstraints.NORTHWEST;
                constraintsJCSpinFieldChangeTriggerNumber.ipadx = 34;
                constraintsJCSpinFieldChangeTriggerNumber.ipady = 19;
                constraintsJCSpinFieldChangeTriggerNumber.insets = new Insets(4, 5, 21, 4);
                getJPanelChangeMethod().add(getJCSpinFieldChangeTriggerNumber(),
                    constraintsJCSpinFieldChangeTriggerNumber);

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
    public Object getValue(Object o) {
        SepTemperatureOffsetGear gear = null;

        gear = (SepTemperatureOffsetGear) o;

        gear.getSettings().setCharAt(1, getIsFahrenheit() ? 'F' : 'C');

        gear.setFrontRampEnabled(getCheckBoxRampIn().isSelected());
        gear.setBackRampEnabled(getCheckBoxRampOut().isSelected());

        if (getCheckBoxHeatingMode().isSelected()) {
            gear.setHeatingOffset(toDouble(getJCSpinFieldOffsetAmount().getValue()));
            gear.setCoolingOffset(0.0);
        } else if (getCheckBoxCoolingMode().isSelected()) {
            gear.setHeatingOffset(0.0);
            gear.setCoolingOffset(toDouble(getJCSpinFieldOffsetAmount().getValue()));
        }

        gear.setCriticality(toInteger(getJCSpinFieldCriticality().getValue()));
        if (getJComboBoxHowToStop().getSelectedItem() != null) {
            gear.setMethodStopType(removeChars(' ', getJComboBoxHowToStop().getSelectedItem().toString()));
        }
        gear.setPercentReduction(toInteger(getJCSpinFieldPercentReduction().getValue()));

        gear.setChangeCondition(getChangeCondition(getJComboBoxWhenChange().getSelectedItem().toString()));
        gear.setChangeDuration(toInteger(getJCSpinFieldChangeDuration().getValue()) * 60);
        gear.setChangePriority(toInteger(getJCSpinFieldChangePriority().getValue()));
        gear.setChangeTriggerNumber(toInteger(getJCSpinFieldChangeTriggerNumber().getValue()));

        if (getJTextFieldChangeTriggerOffset().getText() == null
            || getJTextFieldChangeTriggerOffset().getText().length() <= 0) {
            gear.setChangeTriggerOffset(0.0);
        } else {
            gear.setChangeTriggerOffset(Double.valueOf(getJTextFieldChangeTriggerOffset().getText()));
        }

        return gear;
    }

    private Integer toInteger(Object o) throws ClassCastException {
        return ((Number) o).intValue();
    }

    private Double toDouble(Object o) throws ClassCastException {
        return ((Number) o).doubleValue();
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
        getJCSpinFieldOffsetAmount().addValueListener(this);
        getJCSpinFieldPercentReduction().addValueListener(this);
        getJComboBoxWhenChange().addActionListener(this);
        getJComboBoxHowToStop().addActionListener(this);
        getJTextFieldChangeTriggerOffset().addCaretListener(this);
        getCheckBoxRampIn().addActionListener(this);
        getCheckBoxRampOut().addActionListener(this);
        getJButtonTemperatureUnits().addActionListener(this);

        offsetAmountSpinField.addValueListener(new JCValueListener() {
            @Override
            public void valueChanging(JCValueEvent arg0) {
                if (getIsFahrenheit() && (toDouble(arg0.getNewValue()) > SepTemperatureOffsetGear.MAX_FAHRENHEIT)) {
                    arg0.setNewValue(SepTemperatureOffsetGear.MAX_FAHRENHEIT);
                } else if (getIsFahrenheit()
                    && (toDouble(arg0.getNewValue()) < SepTemperatureOffsetGear.MIN_FAHRENHEIT)) {
                    arg0.setNewValue(SepTemperatureOffsetGear.MIN_FAHRENHEIT);
                } else if (!getIsFahrenheit() && (toDouble(arg0.getNewValue()) > SepTemperatureOffsetGear.MAX_CELSIUS)) {
                    arg0.setNewValue(SepTemperatureOffsetGear.MAX_CELSIUS);
                } else if (!getIsFahrenheit() && (toDouble(arg0.getNewValue()) < SepTemperatureOffsetGear.MIN_CELSIUS)) {
                    arg0.setNewValue(SepTemperatureOffsetGear.MIN_CELSIUS);
                }
            }

            @Override
            public void valueChanged(JCValueEvent arg0) {
            }
        });

        checkBoxHeatingMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                setHeatingMode();
            }
        });

        checkBoxCoolingMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                setCoolingMode();
            }
        });
    }

    private void initialize() {
        try {
            setName("SepTemperatureOffsetGearPanel");

            GridBagConstraints constraintJCheckBoxRampIn = new GridBagConstraints();
            GridBagConstraints constraintJCheckBoxRampOut = new GridBagConstraints();
            GridBagConstraints constraintJCheckBoxHeatingMode = new GridBagConstraints();
            GridBagConstraints constraintJCheckBoxCoolingMode = new GridBagConstraints();
            GridBagConstraints constraintJButtonTemperatureUnits = new GridBagConstraints();
            GridBagConstraints constraintJLabelOffsetAmount = new GridBagConstraints();
            GridBagConstraints constraintJCSpinFieldOffsetAmount = new GridBagConstraints();
            GridBagConstraints constraintJLabelOffsetUnits = new GridBagConstraints();
            GridBagConstraints constraintJLabelCriticality = new GridBagConstraints();
            GridBagConstraints constraintJCSpinFieldCriticality = new GridBagConstraints();
            GridBagConstraints constraintJLabelHowToStop = new GridBagConstraints();
            GridBagConstraints constraintJComboBoxHowToStop = new GridBagConstraints();
            GridBagConstraints constraintJLabelGroupCapacityReduction = new GridBagConstraints();
            GridBagConstraints constraintJCSpinFieldPercentReduction = new GridBagConstraints();
            GridBagConstraints constraintJPanelChangeMethod = new GridBagConstraints();

            constraintJCheckBoxRampIn.insets = new Insets(0, 0, 0, 0);
            constraintJCheckBoxRampIn.gridwidth = 3;
            constraintJCheckBoxRampIn.anchor = GridBagConstraints.WEST;
            constraintJCheckBoxRampIn.gridy = 1;
            constraintJCheckBoxRampIn.gridx = 1;

            constraintJCheckBoxRampOut.insets = new Insets(0, 0, 15, 0);
            constraintJCheckBoxRampOut.gridwidth = 3;
            constraintJCheckBoxRampOut.anchor = GridBagConstraints.WEST;
            constraintJCheckBoxRampOut.gridy = 2;
            constraintJCheckBoxRampOut.gridx = 1;

            constraintJCheckBoxHeatingMode.insets = new Insets(0, 30, 15, 0);
            constraintJCheckBoxHeatingMode.gridwidth = 1;
            constraintJCheckBoxHeatingMode.anchor = GridBagConstraints.WEST;
            constraintJCheckBoxHeatingMode.gridy = 3;
            constraintJCheckBoxHeatingMode.gridx = 1;

            constraintJCheckBoxCoolingMode.insets = new Insets(0, 130, 15, 0);
            constraintJCheckBoxCoolingMode.gridwidth = 3;
            constraintJCheckBoxCoolingMode.anchor = GridBagConstraints.WEST;
            constraintJCheckBoxCoolingMode.gridy = 3;
            constraintJCheckBoxCoolingMode.gridx = 1;

            constraintJButtonTemperatureUnits.insets = new Insets(10, 0, 0, 10);
            constraintJButtonTemperatureUnits.anchor = GridBagConstraints.NORTHEAST;
            constraintJButtonTemperatureUnits.gridy = 1;
            constraintJButtonTemperatureUnits.gridheight = 2;
            constraintJButtonTemperatureUnits.gridx = 3;

            constraintJLabelOffsetAmount.insets = new Insets(0, 0, 3, 0);
            constraintJLabelOffsetAmount.anchor = GridBagConstraints.WEST;
            constraintJLabelOffsetAmount.gridy = 4;
            constraintJLabelOffsetAmount.gridx = 1;

            constraintJCSpinFieldOffsetAmount.insets = new Insets(0, 5, 3, 0);
            constraintJCSpinFieldOffsetAmount.anchor = GridBagConstraints.WEST;
            constraintJCSpinFieldOffsetAmount.gridy = 4;
            constraintJCSpinFieldOffsetAmount.gridx = 2;

            constraintJLabelOffsetUnits.insets = new Insets(0, 4, 3, 0);
            constraintJLabelOffsetUnits.anchor = GridBagConstraints.WEST;
            constraintJLabelOffsetUnits.gridy = 4;
            constraintJLabelOffsetUnits.gridx = 3;

            constraintJLabelCriticality.insets = new Insets(0, 0, 3, 0);
            constraintJLabelCriticality.anchor = GridBagConstraints.WEST;
            constraintJLabelCriticality.gridy = 6;
            constraintJLabelCriticality.gridx = 1;

            constraintJCSpinFieldCriticality.insets = new Insets(0, 5, 3, 0);
            constraintJCSpinFieldCriticality.anchor = GridBagConstraints.WEST;
            constraintJCSpinFieldCriticality.gridy = 6;
            constraintJCSpinFieldCriticality.gridx = 2;

            constraintJLabelHowToStop.insets = new Insets(0, 0, 3, 0);
            constraintJLabelHowToStop.anchor = GridBagConstraints.WEST;
            constraintJLabelHowToStop.gridy = 7;
            constraintJLabelHowToStop.gridx = 1;

            constraintJComboBoxHowToStop.insets = new Insets(0, 5, 3, 0);
            constraintJComboBoxHowToStop.anchor = GridBagConstraints.WEST;
            constraintJComboBoxHowToStop.gridwidth = 3;
            constraintJComboBoxHowToStop.gridy = 7;
            constraintJComboBoxHowToStop.gridx = 2;

            constraintJLabelGroupCapacityReduction.insets = new Insets(0, 0, 15, 0);
            constraintJLabelGroupCapacityReduction.anchor = GridBagConstraints.WEST;
            constraintJLabelGroupCapacityReduction.gridy = 9;
            constraintJLabelGroupCapacityReduction.gridx = 1;

            constraintJCSpinFieldPercentReduction.insets = new Insets(0, 5, 15, 0);
            constraintJCSpinFieldPercentReduction.anchor = GridBagConstraints.WEST;
            constraintJCSpinFieldPercentReduction.gridy = 9;
            constraintJCSpinFieldPercentReduction.gridx = 2;

            constraintJPanelChangeMethod.insets = new Insets(0, 0, 5, 5);
            constraintJPanelChangeMethod.anchor = GridBagConstraints.WEST;
            constraintJPanelChangeMethod.gridwidth = 3;
            constraintJPanelChangeMethod.gridy = 20;
            constraintJPanelChangeMethod.gridx = 1;

            setLayout(new GridBagLayout());
            this.add(getCheckBoxRampIn(), constraintJCheckBoxRampIn);
            this.add(getCheckBoxRampOut(), constraintJCheckBoxRampOut);
            this.add(getCheckBoxHeatingMode(), constraintJCheckBoxHeatingMode);
            this.add(getCheckBoxCoolingMode(), constraintJCheckBoxCoolingMode);
            this.add(getJButtonTemperatureUnits(), constraintJButtonTemperatureUnits);
            this.add(getJLabelOffsetAmount(), constraintJLabelOffsetAmount);
            this.add(getJCSpinFieldOffsetAmount(), constraintJCSpinFieldOffsetAmount);
            this.add(getJLabelOffsetUnits(), constraintJLabelOffsetUnits);
            this.add(getJLabelCriticality(), constraintJLabelCriticality);
            this.add(getJCSpinFieldCriticality(), constraintJCSpinFieldCriticality);
            this.add(getJLabelHowToStop(), constraintJLabelHowToStop);
            this.add(getJComboBoxHowToStop(), constraintJComboBoxHowToStop);
            this.add(getJLabelGroupCapacityReduction(), constraintJLabelGroupCapacityReduction);
            this.add(getJCSpinFieldPercentReduction(), constraintJCSpinFieldPercentReduction);
            this.add(getJPanelChangeMethod(), constraintJPanelChangeMethod);
        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }
        setTemperatureUnits(getIsFahrenheit() ? 'F' : 'C');
        getJComboBoxWhenChange().setSelectedItem(LMProgramDirectGear.CHANGE_NONE);
        getJComboBoxHowToStop().setSelectedItem(StringUtils.addCharBetweenWords(' ', LMProgramDirectGear.STOP_TIME_IN));

        initConnections();
    }

    @Override
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
        } else if (getJComboBoxWhenChange().getSelectedItem().toString().equalsIgnoreCase(
            LMProgramDirectGear.CHANGE_DURATION)
            || getJComboBoxWhenChange().getSelectedItem().toString().equalsIgnoreCase("After a Duration")) {
            // Duration
            getJLabelChangeDuration().setVisible(true);
            getJCSpinFieldChangeDuration().setVisible(true);
            getJLabelMinutesChDur().setVisible(true);
        } else if (getJComboBoxWhenChange().getSelectedItem().toString().equalsIgnoreCase(
            LMProgramDirectGear.CHANGE_PRIORITY)
            || getJComboBoxWhenChange().getSelectedItem().toString().equalsIgnoreCase("Priority Change")) {
            // Priority
            getJLabelChangePriority().setVisible(true);
            getJCSpinFieldChangePriority().setVisible(true);
        } else if (getJComboBoxWhenChange().getSelectedItem().toString().equalsIgnoreCase(
            LMProgramDirectGear.CHANGE_TRIGGER_OFFSET)
            || getJComboBoxWhenChange().getSelectedItem().toString().equalsIgnoreCase("Above Trigger")) {
            // TriggerOffset
            getJLabelChangeTriggerNumber().setVisible(true);
            getJCSpinFieldChangeTriggerNumber().setVisible(true);

            getJLabelChangeTriggerOffset().setVisible(true);
            getJTextFieldChangeTriggerOffset().setVisible(true);
        } else {
            throw new InputMismatchException("Unknown LMProgramDirectGear control condition found, the value = "
                + getJComboBoxWhenChange().getSelectedItem().toString());
        }

        fireInputUpdate();
        return;
    }

    public void jButtonTemperatureUnits_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
        setIsFahrenheit(!getIsFahrenheit());
        if (getIsFahrenheit()) {
            setTemperatureUnits('F');
        } else {
            setTemperatureUnits('C');
        }
    }

    public void setTemperatureUnits(char temperatureUnit) {
        if (temperatureUnit == 'C') {
            setIsFahrenheit(false);
            getJLabelOffsetUnits().setText("\u00B0C");
            ((JCDoubleValidator) getJCSpinFieldOffsetAmount().getValidator()).setMax(SepTemperatureOffsetGear.MAX_CELSIUS);

            if (toDouble(getJCSpinFieldOffsetAmount().getValue()) > SepTemperatureOffsetGear.MAX_CELSIUS) {
                getJCSpinFieldOffsetAmount().setValue(SepTemperatureOffsetGear.MAX_CELSIUS);
            }

            getJCSpinFieldOffsetAmount().revalidate();
        } else {
            setIsFahrenheit(true);
            getJLabelOffsetUnits().setText("\u00B0F");

            ((JCDoubleValidator) getJCSpinFieldOffsetAmount().getValidator()).setMax(SepTemperatureOffsetGear.MAX_FAHRENHEIT);

            if (toDouble(getJCSpinFieldOffsetAmount().getValue()) > SepTemperatureOffsetGear.MAX_FAHRENHEIT) {
                getJCSpinFieldOffsetAmount().setValue(SepTemperatureOffsetGear.MAX_FAHRENHEIT);
            }

            getJCSpinFieldOffsetAmount().revalidate();
        }
    }

    private void setChangeCondition(String change) {
        if (change == null) {
            return;
        }

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
    public void setValue(Object o) {
        SepTemperatureOffsetGear gear = null;

        if (o == null) {
            return;
        } else {
            gear = (SepTemperatureOffsetGear) o;
        }

        getCheckBoxRampIn().setSelected(gear.isFrontRampEnabled());
        getCheckBoxRampOut().setSelected(gear.isBackRampEnabled());

        Double heatingOffset = gear.getHeatingOffset();
        Double coolingOffset = gear.getCoolingOffset();

        if (heatingOffset != 0.0 && coolingOffset == 0.0) {
            getJCSpinFieldOffsetAmount().setValue(heatingOffset);
            setHeatingMode();
        } else if (heatingOffset == 0.0 && coolingOffset != 0.0) {
            getJCSpinFieldOffsetAmount().setValue(coolingOffset);
            setCoolingMode();
        } else if (heatingOffset == 0.0 && coolingOffset == 0.0) {
            getJCSpinFieldOffsetAmount().setValue(0.0);
            setHeatingMode();
        } else {
            throw new RuntimeException(this.getClass().toString()
                + ": Illegal database values: Heating and cooling offsets cannot both be nonzero");
        }

        if (gear.getSettings().charAt(1) == 'C') {
            setTemperatureUnits('C');
        } else {
            setTemperatureUnits('F');
        }

        getJCSpinFieldCriticality().setValue(gear.getCriticality());
        getJComboBoxHowToStop().setSelectedItem(StringUtils.addCharBetweenWords(' ', gear.getMethodStopType()));
        getJCSpinFieldPercentReduction().setValue(gear.getPercentReduction());

        setChangeCondition(gear.getChangeCondition());
        getJCSpinFieldChangeDuration().setValue(new Integer(gear.getChangeDuration().intValue() / 60));
        getJCSpinFieldChangePriority().setValue(gear.getChangePriority());
        getJCSpinFieldChangeTriggerNumber().setValue(gear.getChangeTriggerNumber());
        final DecimalFormat format = new DecimalFormat("#####.####");
        getJTextFieldChangeTriggerOffset().setText(format.format(gear.getChangeTriggerOffset()));
    }

    public void setCoolingMode() {
        getCheckBoxHeatingMode().setSelected(false);
        getCheckBoxCoolingMode().setSelected(true);
        getJLabelOffsetAmount().setText("Cooling Offset:");
    }

    public void setHeatingMode() {
        getCheckBoxHeatingMode().setSelected(true);
        getCheckBoxCoolingMode().setSelected(false);
        getJLabelOffsetAmount().setText("Heating Offset:");
    }

    public void setIsFahrenheit(Boolean isFahrenheit) {
        this.isFahrenheit = isFahrenheit;
    }

    public Boolean getIsFahrenheit() {
        return isFahrenheit;
    }

    @Override
    public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) {
        fireInputUpdate();
    }
}
