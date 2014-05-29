package com.cannontech.dbeditor.wizard.port;

import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.JRadioButton;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.port.PortFactory;

public class PortTypeQuestionPanelA extends DataInputPanel implements ActionListener {
    private javax.swing.JLabel ivjJLabel1 = null;
    private javax.swing.JRadioButton ivjLocalSerialPortRadioButton = null;
    private javax.swing.JRadioButton ivjTCPTerminalServerRadioButton = null;
    private javax.swing.JRadioButton ivjUDPTerminalServerRadioButton = null;
    private javax.swing.JRadioButton tcpPortRadioButton = null;
    private javax.swing.ButtonGroup ivjPortTypeButtonGroup = null;

    public PortTypeQuestionPanelA() {
        super();
        initialize();
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        if (e.getSource() == getLocalSerialPortRadioButton() ||
                e.getSource() == getTCPTerminalServerRadioButton()) {
            // do nothing I guess...
        }
    }

    private void addToPortTypeButtonGroup(JRadioButton jRadioButton) {
        try {
            getPortTypeButtonGroup().add(jRadioButton);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    private javax.swing.JLabel getJLabel1() {
        if (ivjJLabel1 == null) {
            try {
                ivjJLabel1 = new javax.swing.JLabel();
                ivjJLabel1.setName("JLabel1");
                ivjJLabel1.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJLabel1.setText("Select the type of port:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabel1;
    }

    private javax.swing.JRadioButton getLocalSerialPortRadioButton() {
        if (ivjLocalSerialPortRadioButton == null) {
            try {
                ivjLocalSerialPortRadioButton = new javax.swing.JRadioButton();
                ivjLocalSerialPortRadioButton.setName("LocalSerialPortRadioButton");
                ivjLocalSerialPortRadioButton.setFont(new java.awt.Font("dialog", 0, 14));
                ivjLocalSerialPortRadioButton.setText("Local Serial Port");
                ivjLocalSerialPortRadioButton.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjLocalSerialPortRadioButton;
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    private javax.swing.ButtonGroup getPortTypeButtonGroup() {
        if (ivjPortTypeButtonGroup == null) {
            try {
                ivjPortTypeButtonGroup = new javax.swing.ButtonGroup();
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjPortTypeButtonGroup;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(350, 200);
    }

    private javax.swing.JRadioButton getTCPTerminalServerRadioButton() {
        if (ivjTCPTerminalServerRadioButton == null) {
            try {
                ivjTCPTerminalServerRadioButton = new javax.swing.JRadioButton();
                ivjTCPTerminalServerRadioButton.setName("TerminalServerRadioButton");
                ivjTCPTerminalServerRadioButton.setFont(new java.awt.Font("dialog", 0, 14));
                ivjTCPTerminalServerRadioButton.setText("TCP Terminal Server");
                ivjTCPTerminalServerRadioButton.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
                ivjTCPTerminalServerRadioButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjTCPTerminalServerRadioButton;
    }

    private javax.swing.JRadioButton getTcpPortRadioButton() {
        if (tcpPortRadioButton == null) {
            try {
                tcpPortRadioButton = new javax.swing.JRadioButton();
                tcpPortRadioButton.setName("TcpPortRadioButton");
                tcpPortRadioButton.setFont(new java.awt.Font("dialog", 0, 14));
                tcpPortRadioButton.setText("TCP Port");
                tcpPortRadioButton.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
                tcpPortRadioButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return tcpPortRadioButton;
    }

    private javax.swing.JRadioButton getUDPTerminalServerRadioButton() {
        if (ivjUDPTerminalServerRadioButton == null) {
            try {
                ivjUDPTerminalServerRadioButton = new javax.swing.JRadioButton();
                ivjUDPTerminalServerRadioButton.setName("UDPTerminalServerRadioButton");
                ivjUDPTerminalServerRadioButton.setFont(new java.awt.Font("dialog", 0, 14));
                ivjUDPTerminalServerRadioButton.setText("UDP Terminal Server");
                ivjUDPTerminalServerRadioButton.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
                ivjUDPTerminalServerRadioButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjUDPTerminalServerRadioButton;
    }

    @Override
    public Object getValue(Object val) {

        if (getUDPTerminalServerRadioButton().isSelected()) {
            return PortFactory.createPort(PaoType.UDPPORT);
        } else if (getTCPTerminalServerRadioButton().isSelected()) {
            return PortFactory.createPort(PaoType.TSERVER_SHARED);
        }
        // If not UDP, we will decide what to create in the next panel.
        return null;
    }

    private void handleException(Throwable exception) {
    }

    private void initConnections() throws java.lang.Exception {
        getLocalSerialPortRadioButton().addActionListener(this);
        getTcpPortRadioButton().addActionListener(this);
        getTCPTerminalServerRadioButton().addActionListener(this);
        getUDPTerminalServerRadioButton().addActionListener(this);
    }

    private void initialize() {
        try {
            setName("PortTypeQuestionPanelA");
            setLayout(new java.awt.GridBagLayout());
            setSize(300, 200);

            java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
            constraintsJLabel1.gridx = 0;
            constraintsJLabel1.gridy = 0;
            constraintsJLabel1.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsJLabel1.anchor = java.awt.GridBagConstraints.WEST;
            add(getJLabel1(), constraintsJLabel1);

            java.awt.GridBagConstraints constraintsLocalSerialPortRadioButton = new java.awt.GridBagConstraints();
            constraintsLocalSerialPortRadioButton.gridx = 0;
            constraintsLocalSerialPortRadioButton.gridy = 1;
            constraintsLocalSerialPortRadioButton.anchor = java.awt.GridBagConstraints.WEST;
            add(getLocalSerialPortRadioButton(), constraintsLocalSerialPortRadioButton);

            java.awt.GridBagConstraints constraintsTCPTerminalServerRadioButton = new java.awt.GridBagConstraints();
            constraintsTCPTerminalServerRadioButton.gridx = 0;
            constraintsTCPTerminalServerRadioButton.gridy = 2;
            constraintsTCPTerminalServerRadioButton.anchor = java.awt.GridBagConstraints.WEST;
            add(getTCPTerminalServerRadioButton(), constraintsTCPTerminalServerRadioButton);

            java.awt.GridBagConstraints constraintsUDPTerminalServerRadioButton = new java.awt.GridBagConstraints();
            constraintsUDPTerminalServerRadioButton.gridx = 0;
            constraintsUDPTerminalServerRadioButton.gridy = 3;
            constraintsUDPTerminalServerRadioButton.anchor = java.awt.GridBagConstraints.WEST;
            add(getUDPTerminalServerRadioButton(), constraintsUDPTerminalServerRadioButton);

            java.awt.GridBagConstraints constraintsTcpPortRadioButton = new java.awt.GridBagConstraints();
            constraintsTcpPortRadioButton.gridx = 0;
            constraintsTcpPortRadioButton.gridy = 4;
            constraintsTcpPortRadioButton.anchor = java.awt.GridBagConstraints.WEST;
            add(getTcpPortRadioButton(), constraintsTcpPortRadioButton);

            initConnections();
            
            try {
                getPortTypeButtonGroup().add(getLocalSerialPortRadioButton());
                getPortTypeButtonGroup().add(getTCPTerminalServerRadioButton());
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }

            
            addToPortTypeButtonGroup(getLocalSerialPortRadioButton());
            addToPortTypeButtonGroup(getTCPTerminalServerRadioButton());
            addToPortTypeButtonGroup(getUDPTerminalServerRadioButton());
            addToPortTypeButtonGroup(getTcpPortRadioButton());
            
            try {
                getPortTypeButtonGroup().setSelected(getLocalSerialPortRadioButton().getModel(), true);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }


        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    public boolean isLocalSerialPort() {
        return (getLocalSerialPortRadioButton().isSelected());
    }

    public boolean isTCPTerminalServerPort() {
        return (getTCPTerminalServerRadioButton().isSelected());
    }

    public boolean isUDPTerminalServerPort() {
        return (getUDPTerminalServerRadioButton().isSelected());
    }

    public boolean isTcpPort() {
        return (getTcpPortRadioButton().isSelected());
    }

    @Override
    public void setFirstFocus() {
        // Make sure that when its time to display this panel, the focus starts in the top textfield
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                getLocalSerialPortRadioButton().requestFocus();
            }
        });
    }

    @Override
    public void setValue(Object val) {
    }
}