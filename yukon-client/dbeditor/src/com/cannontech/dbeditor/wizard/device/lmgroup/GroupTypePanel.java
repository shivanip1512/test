package com.cannontech.dbeditor.wizard.device.lmgroup;

import java.awt.Dimension;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.device.lm.LMFactory;

public class GroupTypePanel extends DataInputPanel {
    private javax.swing.JLabel ivjSelectLabel = null;
    private javax.swing.ButtonGroup ivjGroupTypeButtonGroup = null;
    private javax.swing.JRadioButton ivjLoadGroupRadioButton = null;
    private javax.swing.JRadioButton ivjMacroLoadGroupRadioButton = null;

    public GroupTypePanel() {
        super();
        initialize();
    }

    private void connEtoM1() {
        try {
            getGroupTypeButtonGroup().add(getLoadGroupRadioButton());
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    private void connEtoM2() {
        try {
            getGroupTypeButtonGroup().add(getMacroLoadGroupRadioButton());
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    private void connEtoM3() {
        try {
            getGroupTypeButtonGroup().setSelected(getLoadGroupRadioButton().getModel(), true);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    private javax.swing.ButtonGroup getGroupTypeButtonGroup() {
        if (ivjGroupTypeButtonGroup == null) {
            try {
                ivjGroupTypeButtonGroup = new javax.swing.ButtonGroup();
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjGroupTypeButtonGroup;
    }

    private javax.swing.JRadioButton getLoadGroupRadioButton() {
        if (ivjLoadGroupRadioButton == null) {
            try {
                ivjLoadGroupRadioButton = new javax.swing.JRadioButton();
                ivjLoadGroupRadioButton.setName("LoadGroupRadioButton");
                ivjLoadGroupRadioButton.setFont(new java.awt.Font("dialog", 0, 14));
                ivjLoadGroupRadioButton.setText("Load Group");
                ivjLoadGroupRadioButton.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
                // user code begin {1}
                // user code end
            } catch (java.lang.Throwable ivjExc) {
                // user code begin {2}
                // user code end
                handleException(ivjExc);
            }
        }
        return ivjLoadGroupRadioButton;
    }

    private javax.swing.JRadioButton getMacroLoadGroupRadioButton() {
        if (ivjMacroLoadGroupRadioButton == null) {
            try {
                ivjMacroLoadGroupRadioButton = new javax.swing.JRadioButton();
                ivjMacroLoadGroupRadioButton.setName("MacroLoadGroupRadioButton");
                ivjMacroLoadGroupRadioButton.setFont(new java.awt.Font("dialog",
                                                                       0,
                                                                       14));
                ivjMacroLoadGroupRadioButton.setText("Macro Load Group (multiple load groups)");
                ivjMacroLoadGroupRadioButton.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjMacroLoadGroupRadioButton;
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(350, 200);
    }

    private javax.swing.JLabel getSelectLabel() {
        if (ivjSelectLabel == null) {
            try {
                ivjSelectLabel = new javax.swing.JLabel();
                ivjSelectLabel.setName("SelectLabel");
                ivjSelectLabel.setFont(new java.awt.Font("dialog", 0, 14));
                ivjSelectLabel.setText("Select the type of Group:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjSelectLabel;
    }

    @Override
    public Object getValue(Object val) {
        if (getMacroLoadGroupRadioButton().isSelected()) {
            val = LMFactory.createLoadManagement(PaoType.MACRO_GROUP);
        }

        return val;
    }

    private void handleException(Throwable exception) {
    }

    private void initialize() {
        try {
            // user code begin {1}
            // user code end
            setName("GroupTypePanel");
            setLayout(new java.awt.GridBagLayout());
            setSize(350, 200);

            java.awt.GridBagConstraints constraintsSelectLabel = new java.awt.GridBagConstraints();
            constraintsSelectLabel.gridx = 1;
            constraintsSelectLabel.gridy = 1;
            constraintsSelectLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsSelectLabel.insets = new java.awt.Insets(4, 4, 4, 4);
            add(getSelectLabel(), constraintsSelectLabel);

            java.awt.GridBagConstraints constraintsLoadGroupRadioButton = new java.awt.GridBagConstraints();
            constraintsLoadGroupRadioButton.gridx = 1;
            constraintsLoadGroupRadioButton.gridy = 2;
            constraintsLoadGroupRadioButton.anchor = java.awt.GridBagConstraints.WEST;
            constraintsLoadGroupRadioButton.insets = new java.awt.Insets(4, 4, 4, 4);
            add(getLoadGroupRadioButton(), constraintsLoadGroupRadioButton);

            java.awt.GridBagConstraints constraintsMacroLoadGroupRadioButton = new java.awt.GridBagConstraints();
            constraintsMacroLoadGroupRadioButton.gridx = 1;
            constraintsMacroLoadGroupRadioButton.gridy = 3;
            constraintsMacroLoadGroupRadioButton.anchor = java.awt.GridBagConstraints.WEST;
            constraintsMacroLoadGroupRadioButton.insets = new java.awt.Insets(4, 4, 4, 4);
            add(getMacroLoadGroupRadioButton(), constraintsMacroLoadGroupRadioButton);
            
            connEtoM1();
            connEtoM2();
            connEtoM3();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    public boolean isGroupMacro() {
        return getMacroLoadGroupRadioButton().isSelected();
    }

    public static void main(java.lang.String[] args) {
        try {
            java.awt.Frame frame = new java.awt.Frame();
            GroupTypePanel aGroupTypePanel;
            aGroupTypePanel = new GroupTypePanel();
            frame.add("Center", aGroupTypePanel);
            frame.setSize(aGroupTypePanel.getSize());
            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                };
            });
            frame.setVisible(true);
        } catch (Throwable exception) {
            System.err.println("Exception occurred in main() of com.ibm.uvm.abt.edit.DeletedClassView");
            CTILogger.error(exception.getMessage(), exception);
            ;
        }
    }

    @Override
    public void setValue(Object val) {
    }

    @Override
    public void setFirstFocus() {
        // Make sure that when its time to display this panel, the focus starts
        // in the top component
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                getLoadGroupRadioButton().requestFocus();
            }
        });
    }

}
