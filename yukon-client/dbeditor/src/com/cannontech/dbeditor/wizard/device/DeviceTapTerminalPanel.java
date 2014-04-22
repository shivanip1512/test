package com.cannontech.dbeditor.wizard.device;

import java.awt.Dimension;

import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.device.IEDBase;
import com.cannontech.database.data.device.PagingTapTerminal;
import com.cannontech.database.data.device.RDSTerminal;
import com.cannontech.database.data.device.TNPPTerminal;
import com.cannontech.database.data.pao.PAOGroups;

/**
 * This type was created in VisualAge.
 */
 
public class DeviceTapTerminalPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ItemListener, javax.swing.event.CaretListener {
    int deviceType;
	private javax.swing.JLabel ivjNameLabel = null;
	private javax.swing.JTextField ivjNameTextField = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JLabel ivjPagerNumberLabel = null;
	private javax.swing.JTextField ivjPagerNumberTextField = null;
	private javax.swing.JCheckBox ivjPasswordCheckBox = null;
	private javax.swing.JLabel ivjPasswordLabel = null;
	private javax.swing.JTextField ivjPasswordTextField = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public DeviceTapTerminalPanel() {
	super();
	initialize();
}
/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void caretUpdate(javax.swing.event.CaretEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getNameTextField()) 
		connEtoC1(e);
	if (e.getSource() == getPagerNumberTextField()) 
		connEtoC2(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (NameTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceNameAddressPanel.eitherTextField_CaretUpdate(Ljavax.swing.event.CaretEvent;)V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.eitherTextField_CaretUpdate(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (AddressTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceNameAddressPanel.eitherTextField_CaretUpdate(Ljavax.swing.event.CaretEvent;)V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.eitherTextField_CaretUpdate(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (PasswordCheckBox.item.itemStateChanged(java.awt.event.ItemEvent) --> DeviceTapTerminalPanel.passwordCheckBox_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.passwordCheckBox_ItemStateChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Comment
 */
public void deviceNameAddressPanel_ComponentShown(java.awt.event.ComponentEvent componentEvent) {
	return;
}
/**
 * Comment
 */
public void deviceNameAddressPanel_ComponentShown1(java.awt.event.ComponentEvent componentEvent) {
	return;
}
/**
 * Comment
 */
public void eitherTextField_CaretUpdate(javax.swing.event.CaretEvent caretEvent) {
	fireInputUpdate();
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel1() {
	if (ivjJPanel1 == null) {
		try {
			ivjJPanel1 = new javax.swing.JPanel();
			ivjJPanel1.setName("JPanel1");
			ivjJPanel1.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsNameLabel = new java.awt.GridBagConstraints();
			constraintsNameLabel.gridx = 0; constraintsNameLabel.gridy = 0;
			constraintsNameLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsNameLabel.anchor = java.awt.GridBagConstraints.WEST;
			getJPanel1().add(getNameLabel(), constraintsNameLabel);

			java.awt.GridBagConstraints constraintsNameTextField = new java.awt.GridBagConstraints();
			constraintsNameTextField.gridx = 1; constraintsNameTextField.gridy = 0;
			constraintsNameTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsNameTextField.insets = new java.awt.Insets(5, 8, 5, 0);
			getJPanel1().add(getNameTextField(), constraintsNameTextField);

			java.awt.GridBagConstraints constraintsPagerNumberLabel = new java.awt.GridBagConstraints();
			constraintsPagerNumberLabel.gridx = 0; constraintsPagerNumberLabel.gridy = 1;
			constraintsPagerNumberLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPagerNumberLabel.anchor = java.awt.GridBagConstraints.WEST;
			getJPanel1().add(getPagerNumberLabel(), constraintsPagerNumberLabel);
			
			java.awt.GridBagConstraints constraintsPagerNumberTextField = new java.awt.GridBagConstraints();
			constraintsPagerNumberTextField.gridx = 1; constraintsPagerNumberTextField.gridy = 1;
			constraintsPagerNumberTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPagerNumberTextField.insets = new java.awt.Insets(5, 8, 5, 0);
			getJPanel1().add(getPagerNumberTextField(), constraintsPagerNumberTextField);

			java.awt.GridBagConstraints constraintsPasswordLabel = new java.awt.GridBagConstraints();
			constraintsPasswordLabel.gridx = 0; constraintsPasswordLabel.gridy = 3;
			constraintsPasswordLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPasswordLabel.insets = new java.awt.Insets(5, 0, 5, 0);
			getJPanel1().add(getPasswordLabel(), constraintsPasswordLabel);

			java.awt.GridBagConstraints constraintsPasswordTextField = new java.awt.GridBagConstraints();
			constraintsPasswordTextField.gridx = 1; constraintsPasswordTextField.gridy = 3;
			constraintsPasswordTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPasswordTextField.insets = new java.awt.Insets(5, 10, 5, 0);
			getJPanel1().add(getPasswordTextField(), constraintsPasswordTextField);

			java.awt.GridBagConstraints constraintsPasswordCheckBox = new java.awt.GridBagConstraints();
			constraintsPasswordCheckBox.gridx = 0; constraintsPasswordCheckBox.gridy = 2;
			constraintsPasswordCheckBox.gridwidth = 2;
			constraintsPasswordCheckBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPasswordCheckBox.insets = new java.awt.Insets(5, 0, 5, 0);
			getJPanel1().add(getPasswordCheckBox(), constraintsPasswordCheckBox);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel1;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getMinimumSize() {
	return getPreferredSize();
}
/**
 * Return the NameLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getNameLabel() {
	if (ivjNameLabel == null) {
		try {
			ivjNameLabel = new javax.swing.JLabel();
			ivjNameLabel.setName("NameLabel");
			ivjNameLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjNameLabel.setText("Device Name:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNameLabel;
}
/**
 * Return the NameTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getNameTextField() {
	if (ivjNameTextField == null) {
		try {
			ivjNameTextField = new javax.swing.JTextField();
			ivjNameTextField.setName("NameTextField");
			ivjNameTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjNameTextField.setColumns(20);
			// user code begin {1}
			ivjNameTextField.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_DEVICE_NAME_LENGTH));
		    ivjNameTextField.setDocument(new TextFieldDocument(TextFieldDocument.MAX_DEVICE_NAME_LENGTH, PaoUtils.ILLEGAL_NAME_CHARS));		             
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNameTextField;
}
/**
 * Return the PhysicalAddressLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPagerNumberLabel() {
	if (ivjPagerNumberLabel == null) {
		try {
			ivjPagerNumberLabel = new javax.swing.JLabel();
			ivjPagerNumberLabel.setName("PagerNumberLabel");
			ivjPagerNumberLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPagerNumberLabel.setText("Pager Number:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPagerNumberLabel;
}
/**
 * Return the AddressTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getPagerNumberTextField() {
	if (ivjPagerNumberTextField == null) {
		try {
			ivjPagerNumberTextField = new javax.swing.JTextField();
			ivjPagerNumberTextField.setName("PagerNumberTextField");
			ivjPagerNumberTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjPagerNumberTextField.setColumns(20);
			// user code begin {1}
			ivjPagerNumberTextField.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_PAGER_NUMBER_LENGTH));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPagerNumberTextField;
}
/**
 * Return the PasswordCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getPasswordCheckBox() {
	if (ivjPasswordCheckBox == null) {
		try {
			ivjPasswordCheckBox = new javax.swing.JCheckBox();
			ivjPasswordCheckBox.setName("PasswordCheckBox");
			ivjPasswordCheckBox.setText("Password Required");
			ivjPasswordCheckBox.setActionCommand("PasswordCheckBox");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPasswordCheckBox;
}
/**
 * Return the PasswordLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPasswordLabel() {
	if (ivjPasswordLabel == null) {
		try {
			ivjPasswordLabel = new javax.swing.JLabel();
			ivjPasswordLabel.setName("PasswordLabel");
			ivjPasswordLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPasswordLabel.setText("Password:");
			ivjPasswordLabel.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPasswordLabel;
}
/**
 * Return the PagerNumberTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getPasswordTextField() {
	if (ivjPasswordTextField == null) {
		try {
			ivjPasswordTextField = new javax.swing.JTextField();
			ivjPasswordTextField.setName("PasswordTextField");
			ivjPasswordTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjPasswordTextField.setText("None");
			ivjPasswordTextField.setEnabled(false);
			ivjPasswordTextField.setColumns(14);
			// user code begin {1}
			ivjPasswordTextField.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_IED_PASSWORD_LENGTH));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPasswordTextField;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getPreferredSize() {
	return new Dimension( 350, 200);
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) 
{
    IEDBase iedBase = (IEDBase)val;
    
    String nameString = getNameTextField().getText();
    iedBase.setPAOName(nameString);

    if( getPasswordCheckBox().isSelected() ) {
        iedBase.getDeviceIED().setPassword( getPasswordTextField().getText() );
    } else {
        iedBase.getDeviceIED().setPassword(CtiUtilities.STRING_NONE );
    }

    //Tap Terminals cannot be slaves like some IED meters
    iedBase.getDeviceIED().setSlaveAddress("Master");

    
    if (iedBase instanceof PagingTapTerminal) {
        PagingTapTerminal tapTerm = (PagingTapTerminal)val;
    	String pagerNumber = getPagerNumberTextField().getText();
    	tapTerm.getDeviceTapPagingSettings().setPagerNumber(pagerNumber);
    }
    
	return val;
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
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getNameTextField().addCaretListener(this);
	getPagerNumberTextField().addCaretListener(this);
	getPasswordCheckBox().addItemListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("DeviceNameAddressPanel");
		setLayout(new java.awt.GridLayout());
		setSize(350, 200);
		add(getJPanel1(), getJPanel1().getName());
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	
	// user code end
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() {
	if( getNameTextField().getText() == null   ||
		getNameTextField().getText().length() < 1 )
	{
		setErrorString("The Name text field must be filled in");
		return false;
	}

	if (deviceType == PAOGroups.TAPTERMINAL) {
    	if( getPagerNumberTextField().getText() == null 	||
    			getPagerNumberTextField().getText().length() < 1 )
    	{
    		setErrorString("The Pager Number text field must be filled in");
    		return false;
    	}
	}
	
	if( getPasswordCheckBox().isSelected() &&
         ( getPasswordTextField().getText() == null ||
            getPasswordTextField().getText().length() < 1) )
	{
	    setErrorString("The Password text field must be filled in");
	    return false;
	}

	return true;
}
/**
 * Method to handle events for the ItemListener interface.
 * @param e java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void itemStateChanged(java.awt.event.ItemEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getPasswordCheckBox()) 
		connEtoC3(e);
	// user code begin {2}
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		DeviceNameAddressPanel aDeviceNameAddressPanel;
		aDeviceNameAddressPanel = new DeviceNameAddressPanel();
		frame.getContentPane().add("Center", aDeviceNameAddressPanel);
		frame.setSize(aDeviceNameAddressPanel.getSize());
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * Comment
 */
public void passwordCheckBox_ItemStateChanged(java.awt.event.ItemEvent itemEvent) {

	if( getPasswordCheckBox().isSelected() )
	{
		getPasswordLabel().setEnabled(true);
		getPasswordTextField().setEnabled(true);
//		getPasswordTextField().setText("");
	}
	else
	{
//		getPasswordTextField().setText("None");
		getPasswordLabel().setEnabled(false);
		getPasswordTextField().setEnabled(false);
	}
	
	return;
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val ) {
    IEDBase iedBase = (IEDBase)val;

    if (iedBase instanceof PagingTapTerminal) {
        deviceType = PAOGroups.TAPTERMINAL;
    }
    if (iedBase instanceof TNPPTerminal){
        deviceType = PAOGroups.TNPP_TERMINAL;
    }
    if (iedBase instanceof RDSTerminal){
        deviceType = PAOGroups.RDS_TERMINAL;
    }
    return;
}

public void setFirstFocus() 
{
    // Make sure that when its time to display this panel, the focus starts in the top component
    javax.swing.SwingUtilities.invokeLater( new Runnable() 
        { 
        public void run() 
            { 
            getNameTextField().requestFocus(); 
        } 
    });    
}

public void setDeviceType(int deviceType){
    this.deviceType = deviceType;
    if (deviceType == PAOGroups.TAPTERMINAL) {
        getPagerNumberLabel().setVisible(true);
        getPagerNumberTextField().setVisible(true);
    } else {
        getPagerNumberLabel().setVisible(false);
        getPagerNumberTextField().setVisible(false);
    }
}
}

