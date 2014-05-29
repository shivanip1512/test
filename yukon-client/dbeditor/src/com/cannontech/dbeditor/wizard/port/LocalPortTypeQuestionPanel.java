package com.cannontech.dbeditor.wizard.port;

import java.awt.Dimension;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.port.PortFactory;

public class LocalPortTypeQuestionPanel extends DataInputPanel {
    private javax.swing.JRadioButton ivjDedicatedRadioButton = null;
    private javax.swing.JRadioButton ivjDialupRadioButton = null;
    private javax.swing.JLabel ivjSelectLabel = null;
    private javax.swing.JRadioButton ivjJRadioButtonDialBack = null;
    private javax.swing.ButtonGroup commLineTypeButtonGroup = null;
    private javax.swing.JRadioButton ivjJRadioButtonDialoutPool = null;

    public LocalPortTypeQuestionPanel() {
        super();
        initialize();
    }

    private javax.swing.ButtonGroup getCommLineTypeButtonGroup() {
        if (commLineTypeButtonGroup == null) {
            try {
                commLineTypeButtonGroup = new javax.swing.ButtonGroup();

            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }

        return commLineTypeButtonGroup;
    }

    private javax.swing.JRadioButton getDedicatedRadioButton() {
        if (ivjDedicatedRadioButton == null) {
            try {
                ivjDedicatedRadioButton = new javax.swing.JRadioButton();
                ivjDedicatedRadioButton.setName("DedicatedRadioButton");
                ivjDedicatedRadioButton.setFont(new java.awt.Font("dialog", 0, 14));
                ivjDedicatedRadioButton.setText("Dedicated");
                ivjDedicatedRadioButton.setSelected(true);
                ivjDedicatedRadioButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjDedicatedRadioButton;
    }

    private javax.swing.JRadioButton getDialupRadioButton() {
        if (ivjDialupRadioButton == null) {
            try {
                ivjDialupRadioButton = new javax.swing.JRadioButton();
                ivjDialupRadioButton.setName("DialupRadioButton");
                ivjDialupRadioButton.setFont(new java.awt.Font("dialog", 0, 14));
                ivjDialupRadioButton.setText("Dialup");
                ivjDialupRadioButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjDialupRadioButton;
    }

    private javax.swing.JRadioButton getJRadioButtonDialBack() {
        if (ivjJRadioButtonDialBack == null) {
            try {
                ivjJRadioButtonDialBack = new javax.swing.JRadioButton();
                ivjJRadioButtonDialBack.setName("JRadioButtonDialBack");
                ivjJRadioButtonDialBack.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJRadioButtonDialBack.setText("Dialback");
                ivjJRadioButtonDialBack.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJRadioButtonDialBack;
    }

    private javax.swing.JRadioButton getJRadioButtonDialoutPool() {
        if (ivjJRadioButtonDialoutPool == null) {
            try {
                ivjJRadioButtonDialoutPool = new javax.swing.JRadioButton();
                ivjJRadioButtonDialoutPool.setName("JRadioButtonDialoutPool");
                ivjJRadioButtonDialoutPool.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJRadioButtonDialoutPool.setText("Dialout Pool");
                ivjJRadioButtonDialoutPool.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJRadioButtonDialoutPool;
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
                ivjSelectLabel.setText("Select the type of communications line:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjSelectLabel;
    }

    @Override
    public Object getValue(Object commonObject) {
        // We can assume that the DBPersistent commonObject is simply an indicator as to whether the port
        // should be created a local type or a terminal server type

        try {

            if (getDedicatedRadioButton().isSelected()) {
                return PortFactory.createPort(PaoType.LOCAL_SHARED);
            } else if (getJRadioButtonDialBack().isSelected()) {
                return PortFactory.createPort(PaoType.LOCAL_DIALBACK);
            } else if (getJRadioButtonDialoutPool().isSelected()) {
                return PortFactory.createPort(PaoType.DIALOUT_POOL);
            } else {
                // if( getDialupRadioButton().isSelected() )
                return PortFactory.createPort(PaoType.LOCAL_DIALUP);
            }
        } catch (Throwable t) {
            com.cannontech.clientutils.CTILogger.error(t.getMessage(), t);
            throw new Error(t.getMessage());
        }
    }

    private void handleException(Throwable exception) {

        /* Uncomment the following lines to print uncaught exceptions to stdout */
        CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
        CTILogger.error(exception.getMessage(),exception);
    }

    private void initialize() {
        try {
            setName("PortTypeQuestionPanelB");
            setFont(new java.awt.Font("dialog", 0, 12));
            setLayout(new java.awt.GridBagLayout());
            setSize(350, 200);

            java.awt.GridBagConstraints constraintsSelectLabel = new java.awt.GridBagConstraints();
            constraintsSelectLabel.gridx = 1;
            constraintsSelectLabel.gridy = 1;
            constraintsSelectLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsSelectLabel.insets = new java.awt.Insets(29, 50, 0, 55);
            add(getSelectLabel(), constraintsSelectLabel);

            java.awt.GridBagConstraints constraintsDedicatedRadioButton = new java.awt.GridBagConstraints();
            constraintsDedicatedRadioButton.gridx = 1;
            constraintsDedicatedRadioButton.gridy = 2;
            constraintsDedicatedRadioButton.anchor = java.awt.GridBagConstraints.WEST;
            constraintsDedicatedRadioButton.ipadx = 22;
            constraintsDedicatedRadioButton.insets = new java.awt.Insets(0, 75, 0, 164);
            add(getDedicatedRadioButton(), constraintsDedicatedRadioButton);

            java.awt.GridBagConstraints constraintsDialupRadioButton = new java.awt.GridBagConstraints();
            constraintsDialupRadioButton.gridx = 1;
            constraintsDialupRadioButton.gridy = 3;
            constraintsDialupRadioButton.anchor = java.awt.GridBagConstraints.WEST;
            constraintsDialupRadioButton.ipadx = 46;
            constraintsDialupRadioButton.insets = new java.awt.Insets(0, 75, 1, 164);
            add(getDialupRadioButton(), constraintsDialupRadioButton);

            java.awt.GridBagConstraints constraintsJRadioButtonDialBack = new java.awt.GridBagConstraints();
            constraintsJRadioButtonDialBack.gridx = 1;
            constraintsJRadioButtonDialBack.gridy = 4;
            constraintsJRadioButtonDialBack.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJRadioButtonDialBack.ipadx = 32;
            constraintsJRadioButtonDialBack.insets = new java.awt.Insets(1, 75, 1, 164);
            add(getJRadioButtonDialBack(), constraintsJRadioButtonDialBack);

            java.awt.GridBagConstraints constraintsJRadioButtonDialoutPool = new java.awt.GridBagConstraints();
            constraintsJRadioButtonDialoutPool.gridx = 1;
            constraintsJRadioButtonDialoutPool.gridy = 5;
            constraintsJRadioButtonDialoutPool.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJRadioButtonDialoutPool.ipadx = 10;
            constraintsJRadioButtonDialoutPool.insets = new java.awt.Insets(2, 75, 39, 164);
            add(getJRadioButtonDialoutPool(),
                constraintsJRadioButtonDialoutPool);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }

        getCommLineTypeButtonGroup().add(getDedicatedRadioButton());
        getCommLineTypeButtonGroup().add(getDialupRadioButton());
        getCommLineTypeButtonGroup().add(getJRadioButtonDialBack());
        getCommLineTypeButtonGroup().add(getJRadioButtonDialoutPool());

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                getDedicatedRadioButton().requestFocus();
            }
        });
    }

    public boolean isDialBack() {
        return getJRadioButtonDialBack().isSelected();
    }

    public boolean isDialup() {
        return (getDialupRadioButton().isSelected());
    }

    public boolean isDialoutPool() {
        return getJRadioButtonDialoutPool().isSelected();
    }

    @Override
    public void setValue(Object val) {
        return;
    }

    @Override
    public void setFirstFocus() {
        // Make sure that when its time to display this panel, the focus starts in the top component
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                getDedicatedRadioButton().requestFocus();
            }
        });
    }
}