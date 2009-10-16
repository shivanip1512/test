package com.cannontech.dbeditor.wizard.port;

import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.event.CaretListener;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.database.data.pao.PortTypes;
import com.cannontech.database.data.port.PortFactory;
import com.cannontech.database.data.port.TcpPort;

public class TcpTypeQuestionPanel extends DataInputPanel implements ActionListener, CaretListener{
    private javax.swing.JComboBox ivjBaudRateComboBox = null;
    private javax.swing.JLabel ivjBaudRateLabel = null;
        
    private javax.swing.JLabel ivjDescriptionLabel = null;
    private javax.swing.JTextField ivjDescriptionTextField = null;

    public TcpTypeQuestionPanel() {
        super();
        initialize();
    }

    public void actionPerformed(java.awt.event.ActionEvent e) {
        if (e.getSource() == getBaudRateComboBox()) { 
            connEtoC3(e);
        }
    }

    public void caretUpdate(javax.swing.event.CaretEvent e) {
        if (e.getSource() == getDescriptionTextField()) { 
            connEtoC4(e);
        }

    }

    private void connEtoC3(java.awt.event.ActionEvent arg1) {
        try {

            this.data_Update(null);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    private void connEtoC4(javax.swing.event.CaretEvent arg1) {
        try {
            this.data_Update(arg1);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    public void data_Update(javax.swing.event.CaretEvent caretEvent) {
        fireInputUpdate();
    }

    private javax.swing.JComboBox getBaudRateComboBox() {
        if (ivjBaudRateComboBox == null) {
            try {
                ivjBaudRateComboBox = new javax.swing.JComboBox();
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
                ivjDescriptionTextField.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_PORT_DESCRIPTION_LENGTH));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjDescriptionTextField;
    }

    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    public Dimension getPreferredSize() {
        return new Dimension( 350, 200 );
    }

    public Object getValue(Object val) 
    {
        val = PortFactory.createPort( PortTypes.TCPPORT );
        String name = getDescriptionTextField().getText().trim();
    
        Integer baudRate = new Integer((String) getBaudRateComboBox().getSelectedItem());
    
        ((TcpPort) val).setPortName( name );
        ((TcpPort) val).getPortSettings().setBaudRate( baudRate );
    
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
            constraintsDescriptionLabel.gridx = 0; constraintsDescriptionLabel.gridy = 0;
            constraintsDescriptionLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsDescriptionLabel.anchor = java.awt.GridBagConstraints.WEST;
            add(getDescriptionLabel(), constraintsDescriptionLabel);
    
            java.awt.GridBagConstraints constraintsBaudRateLabel = new java.awt.GridBagConstraints();
            constraintsBaudRateLabel.gridx = 0; constraintsBaudRateLabel.gridy = 1;
            constraintsBaudRateLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsBaudRateLabel.anchor = java.awt.GridBagConstraints.WEST;
            add(getBaudRateLabel(), constraintsBaudRateLabel);
    
            java.awt.GridBagConstraints constraintsDescriptionTextField = new java.awt.GridBagConstraints();
            constraintsDescriptionTextField.gridx = 1; constraintsDescriptionTextField.gridy = 0;
            constraintsDescriptionTextField.anchor = java.awt.GridBagConstraints.WEST;
            constraintsDescriptionTextField.insets = new java.awt.Insets(5, 10, 5, 0);
            add(getDescriptionTextField(), constraintsDescriptionTextField);
    
            java.awt.GridBagConstraints constraintsBaudRateComboBox = new java.awt.GridBagConstraints();
            constraintsBaudRateComboBox.gridx = 1; constraintsBaudRateComboBox.gridy = 1;
            constraintsBaudRateComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsBaudRateComboBox.anchor = java.awt.GridBagConstraints.WEST;
            constraintsBaudRateComboBox.insets = new java.awt.Insets(5, 10, 5, 0);
            add(getBaudRateComboBox(), constraintsBaudRateComboBox);
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }

        ivjBaudRateComboBox.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_300 );
        ivjBaudRateComboBox.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_1200 );
        ivjBaudRateComboBox.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_2400 );
        ivjBaudRateComboBox.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_4800 );
        ivjBaudRateComboBox.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_9600 );
        ivjBaudRateComboBox.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_14400 );
        ivjBaudRateComboBox.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_28800 );
        ivjBaudRateComboBox.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_38400 );
        ivjBaudRateComboBox.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_57600 );
        ivjBaudRateComboBox.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_115200 );
        getBaudRateComboBox().setSelectedItem(com.cannontech.common.version.DBEditorDefines.BAUD_1200);

    }

    public boolean isInputValid() {
        
        if( getDescriptionTextField().getText().length() < 1 )
        {
            setErrorString("The Description text field must be filled in");
            return false;
        }
        
        return true;
    }

    public void setValue(Object val) {
        
    }
    
    public void setFirstFocus() 
    {
        // Make sure that when its time to display this panel, the focus starts in the top component
        javax.swing.SwingUtilities.invokeLater( new Runnable() 
            { 
            public void run() 
                { 
                getDescriptionTextField().requestFocus(); 
            } 
        });    
    }
}
