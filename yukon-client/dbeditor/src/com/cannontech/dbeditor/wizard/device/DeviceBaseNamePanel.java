package com.cannontech.dbeditor.wizard.device;

/**
 * Insert the type's description here.
 * Creation date: (10/4/00 4:18:25 PM)
 * @author: 
 */
import java.awt.Dimension;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.DeviceFactory;

public class DeviceBaseNamePanel extends com.cannontech.common.gui.util.DataInputPanel implements javax.swing.event.CaretListener {
	private DeviceBase deviceBase = null;
	private javax.swing.JLabel ivjJLabelName = null;
	private javax.swing.JTextField ivjJTextFieldName = null;
	
	private javax.swing.JLabel ivjJLabelErrorMessage = null;
/**
 * DeviceBaseNamePanel constructor comment.
 */
public DeviceBaseNamePanel() {
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
	if (e.getSource() == getJTextFieldName()) 
		connEtoC1(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JTextField1.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceBaseNamePanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jTextFieldName_CaretUpdate(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the JLabelName property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelName() {
	if (ivjJLabelName == null) {
		try {
			ivjJLabelName = new javax.swing.JLabel();
			ivjJLabelName.setName("JLabelName");
			ivjJLabelName.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelName.setText("Device Name:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelName;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldName() {
	if (ivjJTextFieldName == null) {
		try {
			ivjJTextFieldName = new javax.swing.JTextField();
			ivjJTextFieldName.setName("JTextFieldName");
			// user code begin {1}

			ivjJTextFieldName.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_DEVICE_NAME_LENGTH));
			ivjJTextFieldName.setDocument(new TextFieldDocument(TextFieldDocument.MAX_DEVICE_NAME_LENGTH, PaoUtils.ILLEGAL_NAME_CHARS));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldName;
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
 * @param o java.lang.Object
 */
public Object getValue(Object val) 
{
	com.cannontech.database.data.device.DeviceBase device = (com.cannontech.database.data.device.DeviceBase)val;
	
	String nameString = getJTextFieldName().getText();
	device.setPAOName( nameString );

	return val;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	// com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJTextFieldName().addCaretListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("DeviceBaseNamePanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(351, 264);

		java.awt.GridBagConstraints constraintsJTextFieldName = new java.awt.GridBagConstraints();
		constraintsJTextFieldName.gridx = 2; constraintsJTextFieldName.gridy = 1;
		constraintsJTextFieldName.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldName.weightx = 1.0;
		constraintsJTextFieldName.ipadx = 149;
		constraintsJTextFieldName.insets = new java.awt.Insets(114, 3, 130, 84);
		add(getJTextFieldName(), constraintsJTextFieldName);

		java.awt.GridBagConstraints constraintsJLabelName = new java.awt.GridBagConstraints();
		constraintsJLabelName.gridx = 1; constraintsJLabelName.gridy = 1;
		constraintsJLabelName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelName.ipady = -5;
		constraintsJLabelName.insets = new java.awt.Insets(115, 22, 135, 2);
		add(getJLabelName(), constraintsJLabelName);
		
		java.awt.GridBagConstraints constraintsJLabelErrorMsg = new java.awt.GridBagConstraints();
		constraintsJLabelErrorMsg.gridx = 1; constraintsJLabelErrorMsg.gridy = 2;
		constraintsJLabelErrorMsg.gridwidth = 2;
		constraintsJLabelErrorMsg.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelErrorMsg.ipady = -5;
		constraintsJLabelErrorMsg.insets = new java.awt.Insets(0, 22, 0, 2);
		add(getJLabelErrorMessage(), constraintsJLabelErrorMsg);
		
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}

private javax.swing.JLabel getJLabelErrorMessage() {
	if (ivjJLabelErrorMessage == null) {
		try {
			ivjJLabelErrorMessage = new javax.swing.JLabel();
			ivjJLabelErrorMessage.setName("JLabelErrorMsg");
			ivjJLabelErrorMessage.setOpaque(false);
			ivjJLabelErrorMessage.setVisible(true);
			ivjJLabelErrorMessage.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJLabelErrorMessage.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJLabelErrorMessage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}

			ivjJLabelErrorMessage.setVisible( false );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelErrorMessage;
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() 
{
	String deviceName = getJTextFieldName().getText();
	if( StringUtils.isBlank(deviceName)) {
		setErrorString("The Name text field must be filled in");
		return false;
	}
	
	if( !isUniquePao(deviceName, deviceBase.getPAOCategory(), deviceBase.getPAOClass())) {
		setErrorString("Name '" + deviceName + "' is already in use.");
     	getJLabelErrorMessage().setText( "(" + getErrorString() + ")" );
     	getJLabelErrorMessage().setToolTipText( "(" + getErrorString() + ")" );
     	getJLabelErrorMessage().setVisible( true );
		return false;
	}

	getJLabelErrorMessage().setText( "" );
   	getJLabelErrorMessage().setToolTipText( "" );
    getJLabelErrorMessage().setVisible( false );
	return true;
}
public void setDeviceType(int deviceType) 
{
	deviceBase = DeviceFactory.createDevice(deviceType);
}

/**
 * Comment
 */
public void jTextFieldName_CaretUpdate(javax.swing.event.CaretEvent caretEvent) 
{
	fireInputUpdate();
	
	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		DeviceBaseNamePanel aDeviceBaseNamePanel;
		aDeviceBaseNamePanel = new DeviceBaseNamePanel();
		frame.setContentPane(aDeviceBaseNamePanel);
		frame.setSize(aDeviceBaseNamePanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * This method was created in VisualAge.
 * @param o java.lang.Object
 */
public void setValue(Object o) {}

public void setFirstFocus() 
{
    // Make sure that when its time to display this panel, the focus starts in the top component
    javax.swing.SwingUtilities.invokeLater( new Runnable() 
        { 
        public void run() 
            { 
            getJTextFieldName().requestFocus(); 
        } 
    });    
}

}
