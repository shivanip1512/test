package com.cannontech.dbeditor.wizard.port;

import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.event.CaretListener;

import com.cannontech.common.gui.unchanging.LongRangeDocument;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.common.version.DBEditorDefines;
import com.cannontech.database.data.port.TerminalServerPortBase;

public class SimpleTerminalServerSettingsPanel extends DataInputPanel implements ActionListener, CaretListener {
    private javax.swing.JComboBox<String> ivjBaudRateComboBox = null;
    private javax.swing.JLabel ivjBaudRateLabel = null;
    private javax.swing.JLabel ivjipAddressLabel = null;
    private javax.swing.JTextField ivjipAddressTextField = null;
    private javax.swing.JTextField ivjPortTextField = null;
    private javax.swing.JLabel ivjSPortNumberLabel = null;
    private javax.swing.JLabel ivjDescriptionLabel = null;
    private javax.swing.JTextField ivjDescriptionTextField = null;

    public SimpleTerminalServerSettingsPanel() {
        super();
        initialize();
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        if (e.getSource() == getBaudRateComboBox()) {
            try {
                fireInputUpdate();
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
    }

    @Override
    public void caretUpdate(javax.swing.event.CaretEvent e) {
        if (e.getSource() == getipAddressTextField() ||
                e.getSource() == getPortTextField() ||
                e.getSource() == getDescriptionTextField()) {
            try {
                fireInputUpdate();
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
    }

    private javax.swing.JComboBox<String> getBaudRateComboBox() {
        if (ivjBaudRateComboBox == null) {
            try {
                ivjBaudRateComboBox = new javax.swing.JComboBox<String>();
                ivjBaudRateComboBox.setName("BaudRateComboBox");
                ivjBaudRateComboBox.setFont(new java.awt.Font("dialog", 0, 14));
                ivjBaudRateComboBox.setMaximumRowCount(5);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjBaudRateComboBox;
    }

    private javax.swing.JLabel getBaudRateLabel() {
        if (ivjBaudRateLabel == null) {
            try {
                ivjBaudRateLabel = new javax.swing.JLabel();
                ivjBaudRateLabel.setName("BaudRateLabel");
                ivjBaudRateLabel.setFont(new java.awt.Font("dialog", 0, 14));
                ivjBaudRateLabel.setText("Baud Rate:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjBaudRateLabel;
    }

    private javax.swing.JLabel getDescriptionLabel() {
        if (ivjDescriptionLabel == null) {
            try {
                ivjDescriptionLabel = new javax.swing.JLabel();
                ivjDescriptionLabel.setName("DescriptionLabel");
                ivjDescriptionLabel.setFont(new java.awt.Font("dialog", 0, 14));
                ivjDescriptionLabel.setText("Description:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjDescriptionLabel;
    }

    private javax.swing.JTextField getDescriptionTextField() {
        if (ivjDescriptionTextField == null) {
            try {
                ivjDescriptionTextField = new javax.swing.JTextField();
                ivjDescriptionTextField.setName("DescriptionTextField");
                ivjDescriptionTextField.setFont(new java.awt.Font("sansserif", 0, 14));
                ivjDescriptionTextField.setColumns(12);
                ivjDescriptionTextField.setDocument(new TextFieldDocument(TextFieldDocument.MAX_PORT_DESCRIPTION_LENGTH));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjDescriptionTextField;
    }

    private javax.swing.JLabel getipAddressLabel() {
        if (ivjipAddressLabel == null) {
            try {
                ivjipAddressLabel = new javax.swing.JLabel();
                ivjipAddressLabel.setName("ipAddressLabel");
                ivjipAddressLabel.setFont(new java.awt.Font("dialog", 0, 14));
                ivjipAddressLabel.setText("IP Address:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjipAddressLabel;
    }

    private javax.swing.JTextField getipAddressTextField() {
        if (ivjipAddressTextField == null) {
            try {
                ivjipAddressTextField = new javax.swing.JTextField();
                ivjipAddressTextField.setName("ipAddressTextField");
                ivjipAddressTextField.setFont(new java.awt.Font("sansserif", 0, 14));
                ivjipAddressTextField.setText("");
                ivjipAddressTextField.setColumns(12);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjipAddressTextField;
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    private javax.swing.JTextField getPortTextField() {
        if (ivjPortTextField == null) {
            try {
                ivjPortTextField = new javax.swing.JTextField();
                ivjPortTextField.setName("PortTextField");
                ivjPortTextField.setFont(new java.awt.Font("sansserif", 0, 14));
                ivjPortTextField.setColumns(4);
                ivjPortTextField.setDocument(new LongRangeDocument(0, 999999));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjPortTextField;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(350, 200);
    }

    private javax.swing.JLabel getSPortNumberLabel() {
        if (ivjSPortNumberLabel == null) {
            try {
                ivjSPortNumberLabel = new javax.swing.JLabel();
                ivjSPortNumberLabel.setName("SPortNumberLabel");
                ivjSPortNumberLabel.setFont(new java.awt.Font("dialog", 0, 14));
                ivjSPortNumberLabel.setText("Port Number:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjSPortNumberLabel;
    }

    @Override
    public Object getValue(Object val) {
        // Should be of a terminal server variety
        String name = getDescriptionTextField().getText().trim();
        String ipAddress = getipAddressTextField().getText();
        Integer portNumber = new Integer(getPortTextField().getText());

        Integer baudRate = new Integer((String) getBaudRateComboBox().getSelectedItem());

        ((TerminalServerPortBase) val).setPortName(name);
        ((TerminalServerPortBase) val).getPortTerminalServer().setIpAddress(ipAddress);
        ((TerminalServerPortBase) val).getPortTerminalServer().setSocketPortNumber(portNumber);
        ((TerminalServerPortBase) val).getPortSettings().setBaudRate(baudRate);
        ((TerminalServerPortBase) val).getPortSettings().setLineSettings("8N1");

        return val;
    }

    private void handleException(Throwable exception) {
    }

    private void initConnections() throws java.lang.Exception {
        getipAddressTextField().addCaretListener(this);
        getPortTextField().addCaretListener(this);
        getBaudRateComboBox().addActionListener(this);
        getDescriptionTextField().addCaretListener(this);
    }

    private void initialize() {
        try {
            setName("SimpleTerminalServerSettingsPanel");
            setFont(new java.awt.Font("dialog", 0, 14));
            setLayout(new java.awt.GridBagLayout());
            setSize(350, 300);

            java.awt.GridBagConstraints constraintsDescriptionLabel = new java.awt.GridBagConstraints();
            constraintsDescriptionLabel.gridx = 0;
            constraintsDescriptionLabel.gridy = 0;
            constraintsDescriptionLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsDescriptionLabel.anchor = java.awt.GridBagConstraints.WEST;
            add(getDescriptionLabel(), constraintsDescriptionLabel);

            java.awt.GridBagConstraints constraintsipAddressLabel = new java.awt.GridBagConstraints();
            constraintsipAddressLabel.gridx = 0;
            constraintsipAddressLabel.gridy = 1;
            constraintsipAddressLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsipAddressLabel.anchor = java.awt.GridBagConstraints.WEST;
            add(getipAddressLabel(), constraintsipAddressLabel);

            java.awt.GridBagConstraints constraintsSPortNumberLabel = new java.awt.GridBagConstraints();
            constraintsSPortNumberLabel.gridx = 0;
            constraintsSPortNumberLabel.gridy = 2;
            constraintsSPortNumberLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsSPortNumberLabel.anchor = java.awt.GridBagConstraints.WEST;
            add(getSPortNumberLabel(), constraintsSPortNumberLabel);

            java.awt.GridBagConstraints constraintsBaudRateLabel = new java.awt.GridBagConstraints();
            constraintsBaudRateLabel.gridx = 0;
            constraintsBaudRateLabel.gridy = 3;
            constraintsBaudRateLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsBaudRateLabel.anchor = java.awt.GridBagConstraints.WEST;
            add(getBaudRateLabel(), constraintsBaudRateLabel);

            java.awt.GridBagConstraints constraintsDescriptionTextField = new java.awt.GridBagConstraints();
            constraintsDescriptionTextField.gridx = 1;
            constraintsDescriptionTextField.gridy = 0;
            constraintsDescriptionTextField.anchor = java.awt.GridBagConstraints.WEST;
            constraintsDescriptionTextField.insets = new java.awt.Insets(5, 10, 5, 0);
            add(getDescriptionTextField(), constraintsDescriptionTextField);

            java.awt.GridBagConstraints constraintsipAddressTextField = new java.awt.GridBagConstraints();
            constraintsipAddressTextField.gridx = 1;
            constraintsipAddressTextField.gridy = 1;
            constraintsipAddressTextField.anchor = java.awt.GridBagConstraints.WEST;
            constraintsipAddressTextField.insets = new java.awt.Insets(5, 10, 5, 0);
            add(getipAddressTextField(), constraintsipAddressTextField);

            java.awt.GridBagConstraints constraintsPortTextField = new java.awt.GridBagConstraints();
            constraintsPortTextField.gridx = 1;
            constraintsPortTextField.gridy = 2;
            constraintsPortTextField.anchor = java.awt.GridBagConstraints.WEST;
            constraintsPortTextField.insets = new java.awt.Insets(5, 10, 5, 0);
            add(getPortTextField(), constraintsPortTextField);

            java.awt.GridBagConstraints constraintsBaudRateComboBox = new java.awt.GridBagConstraints();
            constraintsBaudRateComboBox.gridx = 1;
            constraintsBaudRateComboBox.gridy = 3;
            constraintsBaudRateComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsBaudRateComboBox.anchor = java.awt.GridBagConstraints.WEST;
            constraintsBaudRateComboBox.insets = new java.awt.Insets(5, 10, 5, 0);
            add(getBaudRateComboBox(), constraintsBaudRateComboBox);
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        ivjBaudRateComboBox.addItem(DBEditorDefines.BAUD_300);
        ivjBaudRateComboBox.addItem(DBEditorDefines.BAUD_1200);
        ivjBaudRateComboBox.addItem(DBEditorDefines.BAUD_2400);
        ivjBaudRateComboBox.addItem(DBEditorDefines.BAUD_4800);
        ivjBaudRateComboBox.addItem(DBEditorDefines.BAUD_9600);
        ivjBaudRateComboBox.addItem(DBEditorDefines.BAUD_14400);
        ivjBaudRateComboBox.addItem(DBEditorDefines.BAUD_28800);
        ivjBaudRateComboBox.addItem(DBEditorDefines.BAUD_38400);
        ivjBaudRateComboBox.addItem(DBEditorDefines.BAUD_57600);
        ivjBaudRateComboBox.addItem(DBEditorDefines.BAUD_115200);
        getBaudRateComboBox().setSelectedItem(DBEditorDefines.BAUD_1200);
    }

    @Override
    public boolean isInputValid() {
        if (getDescriptionTextField().getText().length() < 1) {
            setErrorString("The Description text field must be filled in");
            return false;
        }

        String ipAddr = getipAddressTextField().getText();

        if (ipAddr == null) {
            setErrorString("The IP Address text field must be filled in");
            return false;
        }

        java.util.StringTokenizer t = new java.util.StringTokenizer(ipAddr, ".", false);
        // handle the exception that could be thrown by Integer
        try {
            // see if the port number is ok
            Integer i = new Integer(getPortTextField().getText());
        } catch (NumberFormatException n) {
            setErrorString("The IP Address text field should only contain numbers and periods");
            return false;
        }

        return true;
    }

    @Override
    public void setValue(Object val) {
    }

    @Override
    public void setFirstFocus() {
        // Make sure that when its time to display this panel, the focus starts in the top component
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                getDescriptionTextField().requestFocus();
            }
        });
    }

    public void setAsUDP() {
        getipAddressTextField().setText("UDP");
        getipAddressTextField().setEnabled(false);
    }

    public void setAsTCP() {
        getipAddressTextField().setText("");
        getipAddressTextField().setEnabled(true);
    }
}