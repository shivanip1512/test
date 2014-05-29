package com.cannontech.dbeditor.wizard.port;

import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretListener;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.version.DBEditorDefines;
import com.cannontech.database.data.port.PortFactory;
import com.cannontech.database.data.port.TcpPort;

public class TcpTypeQuestionPanel extends DataInputPanel implements ActionListener, CaretListener {
    private JComboBox<String> baudRateCombo = null;
    private JLabel baudRateLabel = null;

    private JLabel descriptionLabel = null;
    private JTextField descriptionTextField = null;

    public TcpTypeQuestionPanel() {
        super();
        initialize();
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        if (e.getSource() == getBaudRateComboBox()) {
            try {
                fireInputUpdate();
            } catch (Throwable t) {
                handleException(t);
            }
        }
    }

    @Override
    public void caretUpdate(javax.swing.event.CaretEvent e) {
        if (e.getSource() == getDescriptionTextField()) {
            try {
                fireInputUpdate();
            } catch (Throwable t) {
                handleException(t);
            }
        }
    }

    private JComboBox<String> getBaudRateComboBox() {
        if (baudRateCombo == null) {
            try {
                baudRateCombo = new JComboBox<String>();
                baudRateCombo.setName("BaudRateComboBox");
                baudRateCombo.setFont(new java.awt.Font("dialog", 0, 14));
                baudRateCombo.setMaximumRowCount(5);
            } catch (java.lang.Throwable e) {
                handleException(e);
            }
        }
        return baudRateCombo;
    }

    private JLabel getBaudRateLabel() {
        if (baudRateLabel == null) {
            try {
                baudRateLabel = new javax.swing.JLabel();
                baudRateLabel.setName("BaudRateLabel");
                baudRateLabel.setFont(new java.awt.Font("dialog", 0, 14));
                baudRateLabel.setText("Baud Rate:");
            } catch (java.lang.Throwable e) {
                handleException(e);
            }
        }
        return baudRateLabel;
    }

    private JLabel getDescriptionLabel() {
        if (descriptionLabel == null) {
            try {
                descriptionLabel = new javax.swing.JLabel();
                descriptionLabel.setName("DescriptionLabel");
                descriptionLabel.setFont(new java.awt.Font("dialog", 0, 14));
                descriptionLabel.setText("Description:");
            } catch (Throwable e) {
                handleException(e);
            }
        }
        return descriptionLabel;
    }

    private JTextField getDescriptionTextField() {
        if (descriptionTextField == null) {
            try {
                descriptionTextField = new javax.swing.JTextField();
                descriptionTextField.setName("DescriptionTextField");
                descriptionTextField.setFont(new java.awt.Font("sansserif", 0, 14));
                descriptionTextField.setColumns(12);
                descriptionTextField.setDocument(new TextFieldDocument(TextFieldDocument.MAX_PORT_DESCRIPTION_LENGTH));
            } catch (Throwable e) {
                handleException(e);
            }
        }
        return descriptionTextField;
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(350, 200);
    }

    @Override
    public Object getValue(Object val) {
        val = PortFactory.createPort(PaoType.TCPPORT);
        String name = getDescriptionTextField().getText().trim();

        Integer baudRate = new Integer((String) getBaudRateComboBox().getSelectedItem());

        ((TcpPort) val).setPortName(name);
        ((TcpPort) val).getPortSettings().setBaudRate(baudRate);

        return val;
    }

    private void handleException(Throwable exception) {

    }

    private void initConnections() throws Exception {
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

            java.awt.GridBagConstraints constraintsBaudRateLabel = new java.awt.GridBagConstraints();
            constraintsBaudRateLabel.gridx = 0;
            constraintsBaudRateLabel.gridy = 1;
            constraintsBaudRateLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsBaudRateLabel.anchor = java.awt.GridBagConstraints.WEST;
            add(getBaudRateLabel(), constraintsBaudRateLabel);

            java.awt.GridBagConstraints constraintsDescriptionTextField = new java.awt.GridBagConstraints();
            constraintsDescriptionTextField.gridx = 1;
            constraintsDescriptionTextField.gridy = 0;
            constraintsDescriptionTextField.anchor = java.awt.GridBagConstraints.WEST;
            constraintsDescriptionTextField.insets = new java.awt.Insets(5, 10, 5, 0);
            add(getDescriptionTextField(), constraintsDescriptionTextField);

            java.awt.GridBagConstraints constraintsBaudRateComboBox = new java.awt.GridBagConstraints();
            constraintsBaudRateComboBox.gridx = 1;
            constraintsBaudRateComboBox.gridy = 1;
            constraintsBaudRateComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsBaudRateComboBox.anchor = java.awt.GridBagConstraints.WEST;
            constraintsBaudRateComboBox.insets = new java.awt.Insets(5, 10, 5, 0);
            add(getBaudRateComboBox(), constraintsBaudRateComboBox);
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }

        baudRateCombo.addItem(DBEditorDefines.BAUD_300);
        baudRateCombo.addItem(DBEditorDefines.BAUD_1200);
        baudRateCombo.addItem(DBEditorDefines.BAUD_2400);
        baudRateCombo.addItem(DBEditorDefines.BAUD_4800);
        baudRateCombo.addItem(DBEditorDefines.BAUD_9600);
        baudRateCombo.addItem(DBEditorDefines.BAUD_14400);
        baudRateCombo.addItem(DBEditorDefines.BAUD_28800);
        baudRateCombo.addItem(DBEditorDefines.BAUD_38400);
        baudRateCombo.addItem(DBEditorDefines.BAUD_57600);
        baudRateCombo.addItem(DBEditorDefines.BAUD_115200);
        getBaudRateComboBox().setSelectedItem(DBEditorDefines.BAUD_1200);

    }

    @Override
    public boolean isInputValid() {

        if (getDescriptionTextField().getText().length() < 1) {
            setErrorString("The Description text field must be filled in");
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
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                getDescriptionTextField().requestFocus();
            }
        });
    }
}