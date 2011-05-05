package com.cannontech.dbeditor.wizard.device;

import java.awt.Dimension;
import java.util.List;

import com.cannontech.common.wizard.CancelInsertException;
import com.cannontech.database.data.device.IDeviceMeterGroup;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.db.device.DeviceMeterGroup;
import com.cannontech.dbeditor.DatabaseEditorOptionPane;
 
/**
 * This type was created in VisualAge.
 */

public class DeviceMeterNumberPanel extends com.cannontech.common.gui.util.DataInputPanel implements javax.swing.event.CaretListener {
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JLabel ivjMeterNumberLabel = null;
	private javax.swing.JTextField ivjMeterNumberTextField = null;
	
	private int mctType = 0;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public DeviceMeterNumberPanel() {
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
	if (e.getSource() == getMeterNumberTextField()) 
		connEtoC1(e);
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

			java.awt.GridBagConstraints constraintsMeterNumberLabel = new java.awt.GridBagConstraints();
			constraintsMeterNumberLabel.gridx = 0; constraintsMeterNumberLabel.gridy = 0;
			constraintsMeterNumberLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsMeterNumberLabel.anchor = java.awt.GridBagConstraints.WEST;
			getJPanel1().add(getMeterNumberLabel(), constraintsMeterNumberLabel);

			java.awt.GridBagConstraints constraintsMeterNumberTextField = new java.awt.GridBagConstraints();
			constraintsMeterNumberTextField.gridx = 1; constraintsMeterNumberTextField.gridy = 0;
			constraintsMeterNumberTextField.anchor = java.awt.GridBagConstraints.EAST;
			constraintsMeterNumberTextField.insets = new java.awt.Insets(5, 10, 5, 0);
			getJPanel1().add(getMeterNumberTextField(), constraintsMeterNumberTextField);
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
 * Return the NameLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getMeterNumberLabel() {
	if (ivjMeterNumberLabel == null) {
		try {
			ivjMeterNumberLabel = new javax.swing.JLabel();
			ivjMeterNumberLabel.setName("MeterNumberLabel");
			ivjMeterNumberLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjMeterNumberLabel.setText("Meter Number:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMeterNumberLabel;
}
/**
 * Return the NameTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getMeterNumberTextField() {
	if (ivjMeterNumberTextField == null) {
		try {
			ivjMeterNumberTextField = new javax.swing.JTextField();
			ivjMeterNumberTextField.setName("MeterNumberTextField");
			ivjMeterNumberTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjMeterNumberTextField.setColumns(12);
			// user code begin {1}
			ivjMeterNumberTextField.setDocument(
				new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_METER_NUMBER_LENGTH));
				
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMeterNumberTextField;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getMinimumSize() {
	return getPreferredSize();
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
	if( val == null )
		return val;

	DeviceMeterGroup dmg = null;
    if (val instanceof SmartMultiDBPersistent) {
        dmg = ((IDeviceMeterGroup) ((SmartMultiDBPersistent) val).getOwnerDBPersistent()).getDeviceMeterGroup();
    } else {
        dmg = ((IDeviceMeterGroup) val).getDeviceMeterGroup();
    }

   String meterNumber = getMeterNumberTextField().getText();  
   checkMeterNumber(meterNumber);
   dmg.setMeterNumber(meterNumber);

   return val;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getMeterNumberTextField().addCaretListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize()
{
	try
	{
		// user code begin {1}
		// user code end
		setName("DeviceNameAddressPanel");
		setLayout(new java.awt.GridLayout());
		setSize(350, 200);
		add(getJPanel1(), getJPanel1().getName());
		initConnections();
	}
	catch (java.lang.Throwable ivjExc)
	{
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
	if( getMeterNumberTextField().getText() == null   ||
		getMeterNumberTextField().getText().length() < 1 )
	{
		setErrorString("The Meter Number text field must be filled in");
		return false;
	}
	
	return true;
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
 * Insert the method's description here.
 * Creation date: (7/27/2001 9:23:34 AM)
 * @param address java.lang.String
 */
public void setDefaultMeterNumber(String defaultValue) {
		getMeterNumberTextField().setText(defaultValue);
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val)
{
	getMeterNumberTextField().setText( 
			new String(com.cannontech.common.util.CtiUtilities.STRING_DEFAULT) );
}

public void setFirstFocus() 
{
    // Make sure that when its time to display this panel, the focus starts in the top component
    javax.swing.SwingUtilities.invokeLater( new Runnable() 
        { 
        public void run() 
            { 
            getMeterNumberTextField().requestFocus(); 
        } 
    });    
}

public void setMCT400Type(int truth)
{
	mctType = truth;
}

/**
 * Helper method to check meternumber uniqueness
 * @param meterNumber - Meternumber to check
 */
private void checkMeterNumber(String meterNumber) {
     List<String> devices = DeviceMeterGroup.checkMeterNumber(meterNumber, null);

     if (devices.size() > 0) {

         String message = "The meter number '"
             + meterNumber
             + "' is already used by the following devices,\n"
             + "are you sure you want to use it again?\n";
         
         int response = DatabaseEditorOptionPane.showAlreadyUsedConfirmDialog(this,
                                                                              message,
                                                                              "Meter Number Already Used",
                                                                              devices);
         if (response == javax.swing.JOptionPane.NO_OPTION) {
             throw new CancelInsertException("Device was not inserted");
         }
     }
 }
}
