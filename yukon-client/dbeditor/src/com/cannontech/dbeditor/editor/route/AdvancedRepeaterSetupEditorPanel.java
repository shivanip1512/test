package com.cannontech.dbeditor.editor.route;

import java.awt.Color;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.route.CCURoute;
import com.cannontech.database.db.route.RepeaterRoute;
import com.cannontech.yukon.IDatabaseCache;
import com.klg.jclass.field.DataProperties;
import com.klg.jclass.field.JCInvalidInfo;
import com.klg.jclass.field.JCSpinField;
import com.klg.jclass.field.validate.JCIntegerValidator;
import com.klg.jclass.util.value.MutableValueModel;

public class AdvancedRepeaterSetupEditorPanel extends DataInputPanel {
    private JLabel ivjCCUFixedBitsLabel = null;
    private JLabel ivjCCUVariableBitsLabel = null;
    private JLabel ivjRepeaterLabel = null;
    private JLabel ivjVariableBitsLabel = null;
    private JCSpinField ivjCCUFixedBitsField = null;
    private JSeparator ivjJSeparator1 = null;
    private JCSpinField ivjCCUVariableBitsField = null;
    private JCSpinField ivjRepeaterVariableBits1 = null;
    private JCSpinField ivjRepeaterVariableBits2 = null;
    private JCSpinField ivjRepeaterVariableBits3 = null;
    private JCSpinField ivjRepeaterVariableBits4 = null;
    private JCSpinField ivjRepeaterVariableBits5 = null;
    private JCSpinField ivjRepeaterVariableBits6 = null;
    private JCSpinField ivjRepeaterVariableBits7 = null;
    private JPanel ivjJPanel1 = null;
    private JLabel ivjAdvancedRepeaterLabel1 = null;
    private JLabel ivjAdvancedRepeaterLabel2 = null;
    private JLabel ivjAdvancedRepeaterLabel3 = null;
    private JLabel ivjAdvancedRepeaterLabel4 = null;
    private JLabel ivjAdvancedRepeaterLabel5 = null;
    private JLabel ivjAdvancedRepeaterLabel6 = null;
    private JLabel ivjAdvancedRepeaterLabel7 = null;
    private JCheckBox ivjJCheckBoxResetRptSettings = null;
    private JCheckBox ivjJCheckBoxUserLocked = null;
    private JPanel ivjJPanelAdvanced = null;

    public AdvancedRepeaterSetupEditorPanel() {
        super();
        initialize();
    }

    private JLabel getAdvancedRepeaterLabel1() {
        if (ivjAdvancedRepeaterLabel1 == null) {
            try {
                ivjAdvancedRepeaterLabel1 = new JLabel();
                ivjAdvancedRepeaterLabel1.setName("AdvancedRepeaterLabel1");
                ivjAdvancedRepeaterLabel1.setFont(new java.awt.Font("dialog", 0, 14));
                ivjAdvancedRepeaterLabel1.setText("------------");
                ivjAdvancedRepeaterLabel1.setVisible(true);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjAdvancedRepeaterLabel1;
    }

    private JLabel getAdvancedRepeaterLabel2() {
        if (ivjAdvancedRepeaterLabel2 == null) {
            try {
                ivjAdvancedRepeaterLabel2 = new JLabel();
                ivjAdvancedRepeaterLabel2.setName("AdvancedRepeaterLabel2");
                ivjAdvancedRepeaterLabel2.setFont(new java.awt.Font("dialog", 0, 14));
                ivjAdvancedRepeaterLabel2.setText("------------");
                ivjAdvancedRepeaterLabel2.setVisible(true);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjAdvancedRepeaterLabel2;
    }

    private JLabel getAdvancedRepeaterLabel3() {
        if (ivjAdvancedRepeaterLabel3 == null) {
            try {
                ivjAdvancedRepeaterLabel3 = new JLabel();
                ivjAdvancedRepeaterLabel3.setName("AdvancedRepeaterLabel3");
                ivjAdvancedRepeaterLabel3.setFont(new java.awt.Font("dialog", 0, 14));
                ivjAdvancedRepeaterLabel3.setText("------------");
                ivjAdvancedRepeaterLabel3.setVisible(true);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjAdvancedRepeaterLabel3;
    }

    private JLabel getAdvancedRepeaterLabel4() {
        if (ivjAdvancedRepeaterLabel4 == null) {
            try {
                ivjAdvancedRepeaterLabel4 = new JLabel();
                ivjAdvancedRepeaterLabel4.setName("AdvancedRepeaterLabel4");
                ivjAdvancedRepeaterLabel4.setFont(new java.awt.Font("dialog", 0, 14));
                ivjAdvancedRepeaterLabel4.setText("------------");
                ivjAdvancedRepeaterLabel4.setVisible(true);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjAdvancedRepeaterLabel4;
    }

    private JLabel getAdvancedRepeaterLabel5() {
        if (ivjAdvancedRepeaterLabel5 == null) {
            try {
                ivjAdvancedRepeaterLabel5 = new JLabel();
                ivjAdvancedRepeaterLabel5.setName("AdvancedRepeaterLabel5");
                ivjAdvancedRepeaterLabel5.setFont(new java.awt.Font("dialog", 0, 14));
                ivjAdvancedRepeaterLabel5.setText("------------");
                ivjAdvancedRepeaterLabel5.setVisible(true);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjAdvancedRepeaterLabel5;
    }

    private JLabel getAdvancedRepeaterLabel6() {
        if (ivjAdvancedRepeaterLabel6 == null) {
            try {
                ivjAdvancedRepeaterLabel6 = new JLabel();
                ivjAdvancedRepeaterLabel6.setName("AdvancedRepeaterLabel6");
                ivjAdvancedRepeaterLabel6.setFont(new java.awt.Font("dialog", 0, 14));
                ivjAdvancedRepeaterLabel6.setText("------------");
                ivjAdvancedRepeaterLabel6.setVisible(true);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjAdvancedRepeaterLabel6;
    }

    private JLabel getAdvancedRepeaterLabel7() {
        if (ivjAdvancedRepeaterLabel7 == null) {
            try {
                ivjAdvancedRepeaterLabel7 = new JLabel();
                ivjAdvancedRepeaterLabel7.setName("AdvancedRepeaterLabel7");
                ivjAdvancedRepeaterLabel7.setFont(new java.awt.Font("dialog", 0, 14));
                ivjAdvancedRepeaterLabel7.setText("------------");
                ivjAdvancedRepeaterLabel7.setVisible(true);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjAdvancedRepeaterLabel7;
    }

    private JCSpinField getCCUFixedBitsField() {
        if (ivjCCUFixedBitsField == null) {
            try {
                ivjCCUFixedBitsField = new JCSpinField();
                ivjCCUFixedBitsField.setName("CCUFixedBitsField");
                ivjCCUFixedBitsField.setPreferredSize(new java.awt.Dimension(50, 22));
                ivjCCUFixedBitsField.setBackground(Color.white);
                // user code begin {1}
                ivjCCUFixedBitsField.setDataProperties(new DataProperties(new JCIntegerValidator(null, new Integer(0), new Integer(31), null, true, null, new Integer(1),
                                                                                                 "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)),
                                                                                                 new MutableValueModel(Integer.class, new Integer(0)),
                                                                                                 new JCInvalidInfo(true, 2, new Color(0, 0, 0, 255), new Color(255, 255, 255, 255))));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjCCUFixedBitsField;
    }

    private JLabel getCCUFixedBitsLabel() {
        if (ivjCCUFixedBitsLabel == null) {
            try {
                ivjCCUFixedBitsLabel = new JLabel();
                ivjCCUFixedBitsLabel.setName("CCUFixedBitsLabel");
                ivjCCUFixedBitsLabel.setFont(new java.awt.Font("dialog", 1, 14));
                ivjCCUFixedBitsLabel.setText("CCU Fixed Bits:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjCCUFixedBitsLabel;
    }

    private JCSpinField getCCUVariableBitsField() {
        if (ivjCCUVariableBitsField == null) {
            try {
                ivjCCUVariableBitsField = new JCSpinField();
                ivjCCUVariableBitsField.setName("CCUVariableBitsField");
                ivjCCUVariableBitsField.setPreferredSize(new java.awt.Dimension(50, 22));
                ivjCCUVariableBitsField.setBackground(Color.white);

                ivjCCUVariableBitsField.setDataProperties(new DataProperties(new JCIntegerValidator(null, new Integer(0), new Integer(7), null, true, null, new Integer(1),
                                                                                                    "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)),
                                                                                                    new MutableValueModel(Integer.class, new Integer(0)),
                                                                                                    new JCInvalidInfo(true, 4, new Color(0, 0, 0, 255), new Color(255, 255, 255, 255))));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjCCUVariableBitsField;
    }

    private JLabel getCCUVariableBitsLabel() {
        if (ivjCCUVariableBitsLabel == null) {
            try {
                ivjCCUVariableBitsLabel = new JLabel();
                ivjCCUVariableBitsLabel.setName("CCUVariableBitsLabel");
                ivjCCUVariableBitsLabel.setFont(new java.awt.Font("dialog", 1, 14));
                ivjCCUVariableBitsLabel.setText("CCU Variable Bits:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjCCUVariableBitsLabel;
    }

    private JCheckBox getJCheckBoxResetRptSettings() {
        if (ivjJCheckBoxResetRptSettings == null) {
            try {
                ivjJCheckBoxResetRptSettings = new JCheckBox();
                ivjJCheckBoxResetRptSettings.setName("JCheckBoxResetRptSettings");
                ivjJCheckBoxResetRptSettings.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJCheckBoxResetRptSettings.setText("Recalculate fix/var bits (Reset)");
                ivjJCheckBoxResetRptSettings.setActionCommand("JCheckBoxUserLocked");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJCheckBoxResetRptSettings;
    }

    private JCheckBox getJCheckBoxUserLocked() {
        if (ivjJCheckBoxUserLocked == null) {
            try {
                ivjJCheckBoxUserLocked = new JCheckBox();
                ivjJCheckBoxUserLocked.setName("JCheckBoxUserLocked");
                ivjJCheckBoxUserLocked.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJCheckBoxUserLocked.setText("Do not allow auto bit change (Lock)");
                ivjJCheckBoxUserLocked.setActionCommand("JCheckBoxUserLocked");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJCheckBoxUserLocked;
    }

    private JPanel getJPanel1() {
        if (ivjJPanel1 == null) {
            try {
                ivjJPanel1 = new JPanel();
                ivjJPanel1.setName("JPanel1");
                ivjJPanel1.setLayout(new java.awt.GridBagLayout());

                java.awt.GridBagConstraints constraintsRepeaterLabel = new java.awt.GridBagConstraints();
                constraintsRepeaterLabel.gridx = 0;
                constraintsRepeaterLabel.gridy = 0;
                constraintsRepeaterLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsRepeaterLabel.anchor = java.awt.GridBagConstraints.WEST;
                constraintsRepeaterLabel.insets = new java.awt.Insets(0, 0, 0, 15);
                getJPanel1().add(getRepeaterLabel(), constraintsRepeaterLabel);

                java.awt.GridBagConstraints constraintsVariableBitsLabel = new java.awt.GridBagConstraints();
                constraintsVariableBitsLabel.gridx = 1;
                constraintsVariableBitsLabel.gridy = 0;
                constraintsVariableBitsLabel.anchor = java.awt.GridBagConstraints.WEST;
                constraintsVariableBitsLabel.insets = new java.awt.Insets(0, 15, 0, 0);
                getJPanel1().add(getVariableBitsLabel(), constraintsVariableBitsLabel);

                java.awt.GridBagConstraints constraintsRepeaterVariableBits1 = new java.awt.GridBagConstraints();
                constraintsRepeaterVariableBits1.gridx = 1;
                constraintsRepeaterVariableBits1.gridy = 1;
                constraintsRepeaterVariableBits1.anchor = java.awt.GridBagConstraints.WEST;
                constraintsRepeaterVariableBits1.insets = new java.awt.Insets(5, 15, 5, 0);
                getJPanel1().add(getRepeaterVariableBits1(), constraintsRepeaterVariableBits1);

                java.awt.GridBagConstraints constraintsRepeaterVariableBits2 = new java.awt.GridBagConstraints();
                constraintsRepeaterVariableBits2.gridx = 1;
                constraintsRepeaterVariableBits2.gridy = 2;
                constraintsRepeaterVariableBits2.anchor = java.awt.GridBagConstraints.WEST;
                constraintsRepeaterVariableBits2.insets = new java.awt.Insets(5, 15, 5, 0);
                getJPanel1().add(getRepeaterVariableBits2(), constraintsRepeaterVariableBits2);

                java.awt.GridBagConstraints constraintsRepeaterVariableBits3 = new java.awt.GridBagConstraints();
                constraintsRepeaterVariableBits3.gridx = 1;
                constraintsRepeaterVariableBits3.gridy = 3;
                constraintsRepeaterVariableBits3.anchor = java.awt.GridBagConstraints.WEST;
                constraintsRepeaterVariableBits3.insets = new java.awt.Insets(5, 15, 5, 0);
                getJPanel1().add(getRepeaterVariableBits3(), constraintsRepeaterVariableBits3);

                java.awt.GridBagConstraints constraintsRepeaterVariableBits4 = new java.awt.GridBagConstraints();
                constraintsRepeaterVariableBits4.gridx = 1;
                constraintsRepeaterVariableBits4.gridy = 4;
                constraintsRepeaterVariableBits4.anchor = java.awt.GridBagConstraints.WEST;
                constraintsRepeaterVariableBits4.insets = new java.awt.Insets(5, 15, 5, 0);
                getJPanel1().add(getRepeaterVariableBits4(), constraintsRepeaterVariableBits4);

                java.awt.GridBagConstraints constraintsRepeaterVariableBits5 = new java.awt.GridBagConstraints();
                constraintsRepeaterVariableBits5.gridx = 1;
                constraintsRepeaterVariableBits5.gridy = 5;
                constraintsRepeaterVariableBits5.anchor = java.awt.GridBagConstraints.WEST;
                constraintsRepeaterVariableBits5.insets = new java.awt.Insets(5, 15, 5, 0);
                getJPanel1().add(getRepeaterVariableBits5(), constraintsRepeaterVariableBits5);

                java.awt.GridBagConstraints constraintsRepeaterVariableBits6 = new java.awt.GridBagConstraints();
                constraintsRepeaterVariableBits6.gridx = 1;
                constraintsRepeaterVariableBits6.gridy = 6;
                constraintsRepeaterVariableBits6.anchor = java.awt.GridBagConstraints.WEST;
                constraintsRepeaterVariableBits6.insets = new java.awt.Insets(5, 15, 5, 0);
                getJPanel1().add(getRepeaterVariableBits6(), constraintsRepeaterVariableBits6);

                java.awt.GridBagConstraints constraintsRepeaterVariableBits7 = new java.awt.GridBagConstraints();
                constraintsRepeaterVariableBits7.gridx = 1;
                constraintsRepeaterVariableBits7.gridy = 7;
                constraintsRepeaterVariableBits7.anchor = java.awt.GridBagConstraints.WEST;
                constraintsRepeaterVariableBits7.insets = new java.awt.Insets(5, 15, 5, 0);
                getJPanel1().add(getRepeaterVariableBits7(), constraintsRepeaterVariableBits7);

                java.awt.GridBagConstraints constraintsAdvancedRepeaterLabel1 = new java.awt.GridBagConstraints();
                constraintsAdvancedRepeaterLabel1.gridx = 0;
                constraintsAdvancedRepeaterLabel1.gridy = 1;
                constraintsAdvancedRepeaterLabel1.anchor = java.awt.GridBagConstraints.WEST;
                constraintsAdvancedRepeaterLabel1.insets = new java.awt.Insets(5, 0, 5, 0);
                getJPanel1().add(getAdvancedRepeaterLabel1(), constraintsAdvancedRepeaterLabel1);

                java.awt.GridBagConstraints constraintsAdvancedRepeaterLabel2 = new java.awt.GridBagConstraints();
                constraintsAdvancedRepeaterLabel2.gridx = 0;
                constraintsAdvancedRepeaterLabel2.gridy = 2;
                constraintsAdvancedRepeaterLabel2.anchor = java.awt.GridBagConstraints.WEST;
                getJPanel1().add(getAdvancedRepeaterLabel2(), constraintsAdvancedRepeaterLabel2);

                java.awt.GridBagConstraints constraintsAdvancedRepeaterLabel3 = new java.awt.GridBagConstraints();
                constraintsAdvancedRepeaterLabel3.gridx = 0;
                constraintsAdvancedRepeaterLabel3.gridy = 3;
                constraintsAdvancedRepeaterLabel3.anchor = java.awt.GridBagConstraints.WEST;
                getJPanel1().add(getAdvancedRepeaterLabel3(), constraintsAdvancedRepeaterLabel3);

                java.awt.GridBagConstraints constraintsAdvancedRepeaterLabel4 = new java.awt.GridBagConstraints();
                constraintsAdvancedRepeaterLabel4.gridx = 0;
                constraintsAdvancedRepeaterLabel4.gridy = 4;
                constraintsAdvancedRepeaterLabel4.anchor = java.awt.GridBagConstraints.WEST;
                getJPanel1().add(getAdvancedRepeaterLabel4(), constraintsAdvancedRepeaterLabel4);

                java.awt.GridBagConstraints constraintsAdvancedRepeaterLabel5 = new java.awt.GridBagConstraints();
                constraintsAdvancedRepeaterLabel5.gridx = 0;
                constraintsAdvancedRepeaterLabel5.gridy = 5;
                constraintsAdvancedRepeaterLabel5.anchor = java.awt.GridBagConstraints.WEST;
                getJPanel1().add(getAdvancedRepeaterLabel5(), constraintsAdvancedRepeaterLabel5);

                java.awt.GridBagConstraints constraintsAdvancedRepeaterLabel6 = new java.awt.GridBagConstraints();
                constraintsAdvancedRepeaterLabel6.gridx = 0;
                constraintsAdvancedRepeaterLabel6.gridy = 6;
                constraintsAdvancedRepeaterLabel6.anchor = java.awt.GridBagConstraints.WEST;
                getJPanel1().add(getAdvancedRepeaterLabel6(), constraintsAdvancedRepeaterLabel6);

                java.awt.GridBagConstraints constraintsAdvancedRepeaterLabel7 = new java.awt.GridBagConstraints();
                constraintsAdvancedRepeaterLabel7.gridx = 0;
                constraintsAdvancedRepeaterLabel7.gridy = 7;
                constraintsAdvancedRepeaterLabel7.anchor = java.awt.GridBagConstraints.WEST;
                getJPanel1().add(getAdvancedRepeaterLabel7(), constraintsAdvancedRepeaterLabel7);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJPanel1;
    }

    private JPanel getJPanelAdvanced() {
        if (ivjJPanelAdvanced == null) {
            try {
                com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
                ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
                ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
                ivjLocalBorder.setTitle("Advanced Repeater Setup");
                ivjJPanelAdvanced = new JPanel();
                ivjJPanelAdvanced.setName("JPanelAdvanced");
                ivjJPanelAdvanced.setBorder(ivjLocalBorder);
                ivjJPanelAdvanced.setLayout(new java.awt.GridBagLayout());

                java.awt.GridBagConstraints constraintsJCheckBoxUserLocked = new java.awt.GridBagConstraints();
                constraintsJCheckBoxUserLocked.gridx = 1;
                constraintsJCheckBoxUserLocked.gridy = 1;
                constraintsJCheckBoxUserLocked.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJCheckBoxUserLocked.ipadx = 11;
                constraintsJCheckBoxUserLocked.ipady = -5;
                constraintsJCheckBoxUserLocked.insets = new java.awt.Insets(4, 10, 5, 104);
                getJPanelAdvanced().add(getJCheckBoxUserLocked(), constraintsJCheckBoxUserLocked);

                java.awt.GridBagConstraints constraintsJCheckBoxResetRptSettings = new java.awt.GridBagConstraints();
                constraintsJCheckBoxResetRptSettings.gridx = 1;
                constraintsJCheckBoxResetRptSettings.gridy = 2;
                constraintsJCheckBoxResetRptSettings.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJCheckBoxResetRptSettings.ipadx = 42;
                constraintsJCheckBoxResetRptSettings.ipady = -5;
                constraintsJCheckBoxResetRptSettings.insets = new java.awt.Insets(6, 10, 19, 104);
                getJPanelAdvanced().add(getJCheckBoxResetRptSettings(), constraintsJCheckBoxResetRptSettings);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJPanelAdvanced;
    }

    private JSeparator getJSeparator1() {
        if (ivjJSeparator1 == null) {
            try {
                ivjJSeparator1 = new JSeparator();
                ivjJSeparator1.setName("JSeparator1");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJSeparator1;
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(491, 318);
    }

    private JLabel getRepeaterLabel() {
        if (ivjRepeaterLabel == null) {
            try {
                ivjRepeaterLabel = new JLabel();
                ivjRepeaterLabel.setName("RepeaterLabel");
                ivjRepeaterLabel.setFont(new java.awt.Font("dialog", 1, 14));
                ivjRepeaterLabel.setText("Repeater Name");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjRepeaterLabel;
    }

    private JCSpinField getRepeaterVariableBits1() {
        if (ivjRepeaterVariableBits1 == null) {
            try {
                ivjRepeaterVariableBits1 = new JCSpinField();
                ivjRepeaterVariableBits1.setName("RepeaterVariableBits1");
                ivjRepeaterVariableBits1.setPreferredSize(new java.awt.Dimension(50, 22));
                ivjRepeaterVariableBits1.setBackground(Color.white);
                ivjRepeaterVariableBits1.setMinimumSize(new java.awt.Dimension(50, 22));

                ivjRepeaterVariableBits1.setDataProperties(new DataProperties(new JCIntegerValidator(null, new Integer(0), new Integer(7), null, true, null, new Integer(1),
                                                                                                     "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)),
                                                                                                     new MutableValueModel(Integer.class, new Integer(0)), 
                                                                                                     new JCInvalidInfo(true, 4, new Color(0, 0, 0, 255), new Color(255, 255, 255, 255))));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjRepeaterVariableBits1;
    }

    private JCSpinField getRepeaterVariableBits2() {
        if (ivjRepeaterVariableBits2 == null) {
            try {
                ivjRepeaterVariableBits2 = new JCSpinField();
                ivjRepeaterVariableBits2.setName("RepeaterVariableBits2");
                ivjRepeaterVariableBits2.setPreferredSize(new java.awt.Dimension(50, 22));
                ivjRepeaterVariableBits2.setBackground(Color.white);
                ivjRepeaterVariableBits2.setMinimumSize(new java.awt.Dimension(50, 22));

                ivjRepeaterVariableBits2.setDataProperties(new DataProperties(new JCIntegerValidator(null, new Integer(0), new Integer(7), null, true, null, new Integer(1),
                                                                                                     "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)),
                                                                                                     new MutableValueModel(Integer.class, new Integer(0)), 
                                                                                                     new JCInvalidInfo(true, 4, new Color(0, 0, 0, 255), new Color(255, 255, 255, 255))));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjRepeaterVariableBits2;
    }

    private JCSpinField getRepeaterVariableBits3() {
        if (ivjRepeaterVariableBits3 == null) {
            try {
                ivjRepeaterVariableBits3 = new JCSpinField();
                ivjRepeaterVariableBits3.setName("RepeaterVariableBits3");
                ivjRepeaterVariableBits3.setPreferredSize(new java.awt.Dimension(50, 22));
                ivjRepeaterVariableBits3.setBackground(Color.white);
                ivjRepeaterVariableBits3.setMinimumSize(new java.awt.Dimension(50, 22));

                ivjRepeaterVariableBits3.setDataProperties(new DataProperties(new JCIntegerValidator(null, new Integer(0), new Integer(7), null, true, null, new Integer(1),
                                                                                                      "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)),
                                                                                                      new MutableValueModel(Integer.class, new Integer(0)),
                                                                                                      new JCInvalidInfo(true, 4, new Color(0, 0, 0, 255), new Color(255, 255, 255, 255))));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjRepeaterVariableBits3;
    }

    private JCSpinField getRepeaterVariableBits4() {
        if (ivjRepeaterVariableBits4 == null) {
            try {
                ivjRepeaterVariableBits4 = new JCSpinField();
                ivjRepeaterVariableBits4.setName("RepeaterVariableBits4");
                ivjRepeaterVariableBits4.setPreferredSize(new java.awt.Dimension(50, 22));
                ivjRepeaterVariableBits4.setBackground(Color.white);
                ivjRepeaterVariableBits4.setMinimumSize(new java.awt.Dimension(50, 22));

                ivjRepeaterVariableBits4.setDataProperties(new DataProperties(new JCIntegerValidator(null, new Integer(0), new Integer(7), null, true, null, new Integer(1),
                                                                                                     "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)),
                                                                                                     new MutableValueModel(Integer.class, new Integer(0)),
                                                                                                     new JCInvalidInfo(true, 4, new Color(0, 0, 0, 255), new Color(255, 255, 255, 255))));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjRepeaterVariableBits4;
    }

    private JCSpinField getRepeaterVariableBits5() {
        if (ivjRepeaterVariableBits5 == null) {
            try {
                ivjRepeaterVariableBits5 = new JCSpinField();
                ivjRepeaterVariableBits5.setName("RepeaterVariableBits5");
                ivjRepeaterVariableBits5.setPreferredSize(new java.awt.Dimension(50, 22));
                ivjRepeaterVariableBits5.setBackground(Color.white);
                ivjRepeaterVariableBits5.setMinimumSize(new java.awt.Dimension(50, 22));

                ivjRepeaterVariableBits5.setDataProperties(new DataProperties(new JCIntegerValidator(null, new Integer(0), new Integer(7), null, true, null, new Integer(1),
                                                                                                     "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)),
                                                                                                     new MutableValueModel(Integer.class, new Integer(0)),
                                                                                                     new JCInvalidInfo(true, 4, new Color(0, 0, 0, 255), new Color(255, 255, 255, 255))));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjRepeaterVariableBits5;
    }

    private JCSpinField getRepeaterVariableBits6() {
        if (ivjRepeaterVariableBits6 == null) {
            try {
                ivjRepeaterVariableBits6 = new JCSpinField();
                ivjRepeaterVariableBits6.setName("RepeaterVariableBits6");
                ivjRepeaterVariableBits6.setPreferredSize(new java.awt.Dimension(50, 22));
                ivjRepeaterVariableBits6.setBackground(Color.white);
                ivjRepeaterVariableBits6.setMinimumSize(new java.awt.Dimension(50, 22));

                ivjRepeaterVariableBits6.setDataProperties(new DataProperties(new JCIntegerValidator(null, new Integer(0), new Integer(7), null, true, null, new Integer(1),
                                                                                                     "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)),
                                                                                                     new MutableValueModel(Integer.class, new Integer(0)),
                                                                                                     new JCInvalidInfo(true, 4, new Color(0, 0, 0, 255), new Color(255, 255, 255, 255))));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjRepeaterVariableBits6;
    }

    private JCSpinField getRepeaterVariableBits7() {
        if (ivjRepeaterVariableBits7 == null) {
            try {
                ivjRepeaterVariableBits7 = new JCSpinField();
                ivjRepeaterVariableBits7.setName("RepeaterVariableBits7");
                ivjRepeaterVariableBits7.setPreferredSize(new java.awt.Dimension(50, 22));
                ivjRepeaterVariableBits7.setBackground(Color.white);
                ivjRepeaterVariableBits7.setMinimumSize(new java.awt.Dimension(50, 22));

                ivjRepeaterVariableBits7.setDataProperties(new DataProperties(new JCIntegerValidator(null, new Integer(0), new Integer(7), null, true, null, new Integer(1),
                                                                                                     "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)),
                                                                                                     new MutableValueModel(Integer.class, new Integer(0)),
                                                                                                     new JCInvalidInfo(true, 4, new Color(0, 0, 0, 255), new Color(255, 255, 255, 255))));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjRepeaterVariableBits7;
    }

    @Override
    public Object getValue(Object val) {

        CCURoute ccuRoute = (CCURoute) val;

        if (getJCheckBoxResetRptSettings().isSelected())
            ccuRoute.getCarrierRoute().setResetRptSettings("Y");
        else
            ccuRoute.getCarrierRoute().setResetRptSettings("N");

        if (getJCheckBoxUserLocked().isSelected())
            ccuRoute.getCarrierRoute().setUserLocked("Y");
        else
            ccuRoute.getCarrierRoute().setUserLocked("N");

        Object ccuFixedSpinVal = getCCUFixedBitsField().getValue();
        if (ccuFixedSpinVal instanceof Long)
            ccuRoute.getCarrierRoute().setCcuFixBits(new Integer(((Long) ccuFixedSpinVal).byteValue()));
        else if (ccuFixedSpinVal instanceof Integer)
            ccuRoute.getCarrierRoute().setCcuFixBits(new Integer(((Integer) ccuFixedSpinVal).byteValue()));

        Object ccuVariableSpinVal = getCCUVariableBitsField().getValue();
        if (ccuVariableSpinVal instanceof Long)
            ccuRoute.getCarrierRoute().setCcuVariableBits(new Integer(((Long) ccuVariableSpinVal).byteValue()));
        else if (ccuVariableSpinVal instanceof Integer)
            ccuRoute.getCarrierRoute().setCcuVariableBits(new Integer(((Integer) ccuVariableSpinVal).byteValue()));

        JLabel repeaterLabels[] = { getAdvancedRepeaterLabel1(), getAdvancedRepeaterLabel2(),
                getAdvancedRepeaterLabel3(), getAdvancedRepeaterLabel4(), getAdvancedRepeaterLabel5(),
                getAdvancedRepeaterLabel6(), getAdvancedRepeaterLabel7() };

        JCSpinField repeaterFields[] = { getRepeaterVariableBits1(), getRepeaterVariableBits2(),
                getRepeaterVariableBits3(), getRepeaterVariableBits4(), getRepeaterVariableBits5(),
                getRepeaterVariableBits6(), getRepeaterVariableBits7() };

        // Grab all the repeater route(s) from the route
        List<RepeaterRoute> repeaterRoutes = ccuRoute.getRepeaters();
        // Get Devices which contain all repeaters
        IDatabaseCache cache = DefaultDatabaseCache.getInstance();
        synchronized (cache) {
            List<LiteYukonPAObject> devices = cache.getAllDevices();

            // Fill in the labels for every repeater in label
            int repeaterRouteID;
            for (int i = 0; i < ccuRoute.getRepeaters().size(); i++) {
                repeaterLabels[i].setVisible(true);
                repeaterRouteID = repeaterRoutes.get(i).getDeviceID().intValue();
                for (int j = 0; j < devices.size(); j++) {
                    if (devices.get(j).getYukonID() == repeaterRouteID) {
                        Object repeaterISpinVal = repeaterFields[i].getValue();
                        if (repeaterISpinVal instanceof Long)
                            ccuRoute.getRepeaters().get(i).setVariableBits(new Integer(((Long) repeaterISpinVal).intValue()));
                        else if (repeaterISpinVal instanceof Integer)
                            ccuRoute.getRepeaters().get(i).setVariableBits(new Integer(((Integer) repeaterISpinVal).intValue()));
                        repeaterLabels[i].setText(devices.get(j).getPaoName());
                        break;
                    }
                }
                repeaterFields[i].setVisible(true);
                repeaterFields[i].setValue(ccuRoute.getRepeaters().get(i).getVariableBits());
            }
        }
        return val;
    }

    private JLabel getVariableBitsLabel() {
        if (ivjVariableBitsLabel == null) {
            try {
                ivjVariableBitsLabel = new JLabel();
                ivjVariableBitsLabel.setName("VariableBitsLabel");
                ivjVariableBitsLabel.setFont(new java.awt.Font("dialog", 1, 14));
                ivjVariableBitsLabel.setText("Variable Bits");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjVariableBitsLabel;
    }

    /**
     * Called whenever the part throws an exception.
     */
    private void handleException(Throwable exception) {
        /* Uncomment the following lines to print uncaught exceptions to stdout */
        // com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
        // com.cannontech.clientutils.CTILogger.error( exception.getMessage(),
        // exception );;
    }

    /**
     * Initialize the class.
     */
    private void initialize() {
        try {
            setName("AdvancedRepeaterSetupEditorPanel");
            setLayout(new java.awt.GridBagLayout());
            setSize(430, 400);

            java.awt.GridBagConstraints constraintsCCUFixedBitsLabel = new java.awt.GridBagConstraints();
            constraintsCCUFixedBitsLabel.gridx = 1;
            constraintsCCUFixedBitsLabel.gridy = 1;
            constraintsCCUFixedBitsLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsCCUFixedBitsLabel.insets = new java.awt.Insets(14, 10, 5, 5);
            add(getCCUFixedBitsLabel(), constraintsCCUFixedBitsLabel);

            java.awt.GridBagConstraints constraintsCCUVariableBitsLabel = new java.awt.GridBagConstraints();
            constraintsCCUVariableBitsLabel.gridx = 3;
            constraintsCCUVariableBitsLabel.gridy = 1;
            constraintsCCUVariableBitsLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsCCUVariableBitsLabel.insets = new java.awt.Insets(14, 20, 5, 14);
            add(getCCUVariableBitsLabel(), constraintsCCUVariableBitsLabel);

            java.awt.GridBagConstraints constraintsCCUFixedBitsField = new java.awt.GridBagConstraints();
            constraintsCCUFixedBitsField.gridx = 2;
            constraintsCCUFixedBitsField.gridy = 1;
            constraintsCCUFixedBitsField.anchor = java.awt.GridBagConstraints.WEST;
            constraintsCCUFixedBitsField.ipadx = 49;
            constraintsCCUFixedBitsField.ipady = 21;
            constraintsCCUFixedBitsField.insets = new java.awt.Insets(11, 5, 2, 19);
            add(getCCUFixedBitsField(), constraintsCCUFixedBitsField);

            java.awt.GridBagConstraints constraintsCCUVariableBitsField = new java.awt.GridBagConstraints();
            constraintsCCUVariableBitsField.gridx = 4;
            constraintsCCUVariableBitsField.gridy = 1;
            constraintsCCUVariableBitsField.anchor = java.awt.GridBagConstraints.WEST;
            constraintsCCUVariableBitsField.ipadx = 49;
            constraintsCCUVariableBitsField.ipady = 21;
            constraintsCCUVariableBitsField.insets = new java.awt.Insets(11, 15, 2, 8);
            add(getCCUVariableBitsField(), constraintsCCUVariableBitsField);

            java.awt.GridBagConstraints constraintsJSeparator1 = new java.awt.GridBagConstraints();
            constraintsJSeparator1.gridx = 1;
            constraintsJSeparator1.gridy = 2;
            constraintsJSeparator1.gridwidth = 4;
            constraintsJSeparator1.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJSeparator1.ipadx = 411;
            constraintsJSeparator1.ipady = 1;
            constraintsJSeparator1.insets = new java.awt.Insets(3, 10, 1, 8);
            add(getJSeparator1(), constraintsJSeparator1);

            java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
            constraintsJPanel1.gridx = 1;
            constraintsJPanel1.gridy = 3;
            constraintsJPanel1.gridwidth = 3;
            constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
            constraintsJPanel1.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJPanel1.weightx = 1.0;
            constraintsJPanel1.weighty = 1.0;
            constraintsJPanel1.ipadx = 8;
            constraintsJPanel1.insets = new java.awt.Insets(2, 99, 2, 24);
            add(getJPanel1(), constraintsJPanel1);

            java.awt.GridBagConstraints constraintsJPanelAdvanced = new java.awt.GridBagConstraints();
            constraintsJPanelAdvanced.gridx = 1;
            constraintsJPanelAdvanced.gridy = 4;
            constraintsJPanelAdvanced.gridwidth = 4;
            constraintsJPanelAdvanced.fill = java.awt.GridBagConstraints.BOTH;
            constraintsJPanelAdvanced.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJPanelAdvanced.weightx = 1.0;
            constraintsJPanelAdvanced.weighty = 1.0;
            constraintsJPanelAdvanced.insets = new java.awt.Insets(3, 25, 8, 22);
            add(getJPanelAdvanced(), constraintsJPanelAdvanced);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    public boolean isDataCorrect(Object aRoute) {

        CCURoute ccuRoute = (CCURoute) aRoute;
        int finalUsedBitField = ccuRoute.getRepeaters().size();

        JCSpinField bitFields[] = { getCCUVariableBitsField(), getRepeaterVariableBits1(),
                getRepeaterVariableBits2(), getRepeaterVariableBits3(), getRepeaterVariableBits4(),
                getRepeaterVariableBits5(), getRepeaterVariableBits6(), getRepeaterVariableBits7() };

        // verify that the last used bit field has a value of 7
        if (((Number) bitFields[finalUsedBitField].getValue()).intValue() != 7) {
            bitFields[finalUsedBitField].setValue(new Integer(7));
            StringBuffer message = new StringBuffer("The last variable bit is required to be a 7. \n" + "Advanced Setup is changing variable bit " + finalUsedBitField + " to contain a value of 7.");
            javax.swing.JOptionPane.showMessageDialog(this,
                                                      message,
                                                      "IMPROPER FINAL BIT VALUE",
                                                      javax.swing.JOptionPane.ERROR_MESSAGE);
        }

        // Verify there are no duplicate bit field values behold the crapsort algorithm, tremble in its presence
        for (int j = 0; j <= finalUsedBitField; j++) {
            for (int x = finalUsedBitField; x > j; x--) {
                if (((Number) bitFields[j].getValue()).intValue() == (((Number) bitFields[x].getValue()).intValue())) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void setValue(Object val) {

        // Must be some flavor of CCU Route to have repeaters
        CCURoute ccuRoute = (CCURoute) val;

        if (ccuRoute.getCarrierRoute().getUserLocked() != null)
            getJCheckBoxUserLocked().setSelected(ccuRoute.getCarrierRoute().getUserLocked().equalsIgnoreCase("Y") || 
                                                 ccuRoute.getCarrierRoute().getUserLocked().equalsIgnoreCase("Y"));

        if (ccuRoute.getCarrierRoute().getResetRptSettings() != null)
            getJCheckBoxResetRptSettings().setSelected(ccuRoute.getCarrierRoute().getResetRptSettings().equalsIgnoreCase("Y") || 
                                                       ccuRoute.getCarrierRoute().getResetRptSettings().equalsIgnoreCase("Y"));

        Integer ccuFixBits = ccuRoute.getCarrierRoute().getCcuFixBits();
        Integer ccuVarBits = ccuRoute.getCarrierRoute().getCcuVariableBits();

        if (ccuFixBits != null)
            getCCUFixedBitsField().setValue(ccuFixBits);

        if (ccuVarBits != null)
            getCCUVariableBitsField().setValue(ccuVarBits);

        // Grab all the repeater route(s) from the route
        List<RepeaterRoute>repeaterRoutes = ccuRoute.getRepeaters();

        // Use this array to loop though the combo boxes in order
        JLabel repeaterLabels[] = { getAdvancedRepeaterLabel1(), getAdvancedRepeaterLabel2(),
                getAdvancedRepeaterLabel3(), getAdvancedRepeaterLabel4(), getAdvancedRepeaterLabel5(),
                getAdvancedRepeaterLabel6(), getAdvancedRepeaterLabel7() };

        JCSpinField repeaterFields[] = { getRepeaterVariableBits1(), getRepeaterVariableBits2(),
                getRepeaterVariableBits3(), getRepeaterVariableBits4(), getRepeaterVariableBits5(),
                getRepeaterVariableBits6(), getRepeaterVariableBits7() };

        IDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
        synchronized (cache) {
            List<LiteYukonPAObject> devices = cache.getAllDevices();
            // Fill in the labels for every repeater in label
            int repeaterRouteDeviceID;
            for (int i = 0; i < ccuRoute.getRepeaters().size(); i++) {
                repeaterLabels[i].setVisible(true);
                repeaterRouteDeviceID = repeaterRoutes.get(i).getDeviceID().intValue();
                for (int j = 0; j < devices.size(); j++) {
                    if (devices.get(j).getYukonID() == repeaterRouteDeviceID) {
                        repeaterLabels[i].setText(devices.get(j).getPaoName());
                        break;
                    }
                }
                repeaterFields[i].setVisible(true);
                repeaterFields[i].setValue(ccuRoute.getRepeaters().get(i).getVariableBits());
            }
        }
    }

}