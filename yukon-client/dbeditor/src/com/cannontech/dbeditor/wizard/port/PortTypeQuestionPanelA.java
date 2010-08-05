package com.cannontech.dbeditor.wizard.port;

/**
 * This type was created in VisualAge.
 */
import java.awt.Dimension;

import com.cannontech.database.data.pao.PortTypes;
import com.cannontech.database.data.port.PortFactory;

public class PortTypeQuestionPanelA extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener {
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
    /**
     * Method to handle events for the ActionListener interface.
     * @param e java.awt.event.ActionEvent
     */
    public void actionPerformed(java.awt.event.ActionEvent e) { 
    	if (e.getSource() == getLocalSerialPortRadioButton()) 
    		connEtoC1(e);
    	if (e.getSource() == getTCPTerminalServerRadioButton()) 
    		connEtoC2(e);
    }
    /**
     * connEtoC1:  (LocalSerialPortRadioButton.action.actionPerformed(java.awt.event.ActionEvent) --> PortTypeQuestionPanelA.radioButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
     * @param arg1 java.awt.event.ActionEvent
     */
    private void connEtoC1(java.awt.event.ActionEvent arg1) {
    	try {
    		this.radioButton_ActionPerformed(arg1);
    	} catch (java.lang.Throwable ivjExc) {
    		handleException(ivjExc);
    	}
    }
    /**
     * connEtoC2:  (TerminalServerRadioButton.action.actionPerformed(java.awt.event.ActionEvent) --> PortTypeQuestionPanelA.radioButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
     * @param arg1 java.awt.event.ActionEvent
     */
    private void connEtoC2(java.awt.event.ActionEvent arg1) {
    	try {
    		this.radioButton_ActionPerformed(arg1);
    	} catch (java.lang.Throwable ivjExc) {
    		handleException(ivjExc);
    	}
    }
    /**
     * connEtoM1:  (PortTypeQuestionPanelA.initialize() --> PortTypeButtonGroup.add(Ljavax.swing.AbstractButton;)V)
     */
    private void connEtoM1() {
    	try {
    		getPortTypeButtonGroup().add(getLocalSerialPortRadioButton());
    	} catch (java.lang.Throwable ivjExc) {
    		handleException(ivjExc);
    	}
    }
    /**
     * connEtoM2:  (PortTypeQuestionPanelA.initialize() --> PortTypeButtonGroup.add(Ljavax.swing.AbstractButton;)V)
     */
    private void connEtoM2() {
    	try {
    		getPortTypeButtonGroup().add(getTCPTerminalServerRadioButton());
    	} catch (java.lang.Throwable ivjExc) {
    		handleException(ivjExc);
    	}
    }
    
    private void addTcpPortSelection() {
        try {
            getPortTypeButtonGroup().add(getTcpPortRadioButton());
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    
    /**
     * connEtoM3:  (PortTypeQuestionPanelA.initialize() --> PortTypeButtonGroup.setSelected(Ljavax.swing.ButtonModel;Z)V)
     */
    private void connEtoM3() {
    	try {
    		getPortTypeButtonGroup().setSelected(getLocalSerialPortRadioButton().getModel(), true);
    	} catch (java.lang.Throwable ivjExc) {
    		handleException(ivjExc);
    	}
    }
    private void connEtoM4() {
        try {
            getPortTypeButtonGroup().add(getUDPTerminalServerRadioButton());
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    /**
     * Return the JLabel1 property value.
     * @return javax.swing.JLabel
     */
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
    /**
     * Return the LocalSerialPortRadioButton property value.
     * @return javax.swing.JRadioButton
     */
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
    /**
     * This method was created in VisualAge.
     */
    public Dimension getMinimumSize() {
    	return getPreferredSize();
    }
    /**
     * Return the PortTypeButtonGroup property value.
     * @return javax.swing.ButtonGroup
     */
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
    /**
     * This method was created in VisualAge.
     */
    public Dimension getPreferredSize() {
    	return new Dimension(350, 200);
    }
    /**
     * Return the TerminalServerRadioButton property value.
     * @return javax.swing.JRadioButton
     */
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
    
    /**
     * Return the TerminalServerRadioButton property value.
     * @return javax.swing.JRadioButton
     */
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
    /**
     * This method was created in VisualAge.
     * @return java.lang.Object
     * @param val java.lang.Object
     */
    public Object getValue(Object val) {
        
        if (getUDPTerminalServerRadioButton().isSelected()) {
            return PortFactory.createPort(PortTypes.UDPPORT);
        }
        else if (getTCPTerminalServerRadioButton().isSelected()) {
            return PortFactory.createPort(PortTypes.TSERVER_SHARED);
        }
        //If not UDP, we will decide what to create in the next panel.
        return null;
    }
    /**
     * Called whenever the part throws an exception.
     * @param exception java.lang.Throwable
     */
    private void handleException(Throwable exception) {
    
    	/* Uncomment the following lines to print uncaught exceptions to stdout */
    	// com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
    	// com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
    }
    /**
     * Initializes connections
     */
    private void initConnections() throws java.lang.Exception {
    	getLocalSerialPortRadioButton().addActionListener(this);
    	getTcpPortRadioButton().addActionListener(this);
    	getTCPTerminalServerRadioButton().addActionListener(this);
        getUDPTerminalServerRadioButton().addActionListener(this);
        
    }
    /**
     * Initialize the class.
     */
    private void initialize() {
    	try {
    		setName("PortTypeQuestionPanelA");
    		setLayout(new java.awt.GridBagLayout());
    		setSize(300, 200);
    
    		java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
    		constraintsJLabel1.gridx = 0; constraintsJLabel1.gridy = 0;
    		constraintsJLabel1.fill = java.awt.GridBagConstraints.HORIZONTAL;
    		constraintsJLabel1.anchor = java.awt.GridBagConstraints.WEST;
    		add(getJLabel1(), constraintsJLabel1);
    
    		java.awt.GridBagConstraints constraintsLocalSerialPortRadioButton = new java.awt.GridBagConstraints();
    		constraintsLocalSerialPortRadioButton.gridx = 0; constraintsLocalSerialPortRadioButton.gridy = 1;
    		constraintsLocalSerialPortRadioButton.anchor = java.awt.GridBagConstraints.WEST;
    		add(getLocalSerialPortRadioButton(), constraintsLocalSerialPortRadioButton);
    
    		java.awt.GridBagConstraints constraintsTCPTerminalServerRadioButton = new java.awt.GridBagConstraints();
            constraintsTCPTerminalServerRadioButton.gridx = 0; constraintsTCPTerminalServerRadioButton.gridy = 2;
            constraintsTCPTerminalServerRadioButton.anchor = java.awt.GridBagConstraints.WEST;
    		add(getTCPTerminalServerRadioButton(), constraintsTCPTerminalServerRadioButton);
    		
            java.awt.GridBagConstraints constraintsUDPTerminalServerRadioButton = new java.awt.GridBagConstraints();
            constraintsUDPTerminalServerRadioButton.gridx = 0; constraintsUDPTerminalServerRadioButton.gridy = 3;
            constraintsUDPTerminalServerRadioButton.anchor = java.awt.GridBagConstraints.WEST;
            add(getUDPTerminalServerRadioButton(), constraintsUDPTerminalServerRadioButton);
    
            java.awt.GridBagConstraints constraintsTcpPortRadioButton = new java.awt.GridBagConstraints();
            constraintsTcpPortRadioButton.gridx = 0; constraintsTcpPortRadioButton.gridy = 4;
            constraintsTcpPortRadioButton.anchor = java.awt.GridBagConstraints.WEST;
            add(getTcpPortRadioButton(), constraintsTcpPortRadioButton);
    
    		initConnections();
    		connEtoM1();
    		connEtoM2();
    		connEtoM3();
            connEtoM4();
            addTcpPortSelection();
            
    	} catch (java.lang.Throwable ivjExc) {
    		handleException(ivjExc);
    	}
    }
    /**
     * This method was created in VisualAge.
     * @return boolean
     */
    public boolean isLocalSerialPort() {
    	return (getLocalSerialPortRadioButton().isSelected() );
    }
    
    public boolean isTCPTerminalServerPort() {
        return (getTCPTerminalServerRadioButton().isSelected() );
    }
    
    public boolean isUDPTerminalServerPort() {
        return (getUDPTerminalServerRadioButton().isSelected() );
    }
    
    public boolean isTcpPort() {
        return (getTcpPortRadioButton().isSelected() );
    }
    
    /**
     * main entrypoint - starts the part when it is run as an application
     * @param args java.lang.String[]
     */
    public static void main(java.lang.String[] args) {
    	try {
    		java.awt.Frame frame = new java.awt.Frame();
    		PortTypeQuestionPanelA aPortTypeQuestionPanelA;
    		aPortTypeQuestionPanelA = new PortTypeQuestionPanelA();
    		frame.add("Center", aPortTypeQuestionPanelA);
    		frame.setSize(aPortTypeQuestionPanelA.getSize());
    		frame.addWindowListener(new java.awt.event.WindowAdapter() {
    			public void windowClosing(java.awt.event.WindowEvent e) {
    				System.exit(0);
    			};
    		});
    		frame.setVisible(true);
    	} catch (Throwable exception) {
    		System.err.println("Exception occurred in main() of com.ibm.uvm.abt.edit.DeletedClassView");
    		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
    	}
    }
    /**
     * Comment
     */
    public void radioButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
    {
        
    }
    
    public void setFirstFocus() 
    {
        // Make sure that when its time to display this panel, the focus starts in the top textfield
        javax.swing.SwingUtilities.invokeLater( new Runnable() 
            { 
            public void run() 
                { 
                getLocalSerialPortRadioButton().requestFocus(); 
            } 
        });    
    }
    
    /**
     * This method was created in VisualAge.
     * @param val java.lang.Object
     */
    public void setValue(Object val) {
        
    }
    
    private static void getBuilderData() {
    }
}
