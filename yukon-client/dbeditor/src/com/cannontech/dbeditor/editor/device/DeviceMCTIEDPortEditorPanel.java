package com.cannontech.dbeditor.editor.device;

import com.cannontech.database.data.device.*;
import com.cannontech.database.db.*;
import com.cannontech.database.db.device.*;

import com.cannontech.common.gui.util.DataInputPanel;

/**
 * This type was created in VisualAge.
 */
public class DeviceMCTIEDPortEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.klg.jclass.util.value.JCValueListener, java.awt.event.ActionListener, javax.swing.event.CaretListener {
	private javax.swing.JComboBox ivjConnectedIEDComboBox = null;
	private javax.swing.JLabel ivjConnectedIEDLabel = null;
	private javax.swing.JLabel ivjDefaultDataClassLabel = null;
	private javax.swing.JLabel ivjDefaultDataOffsetLabel = null;
	private javax.swing.JLabel ivjIEDScanRateLabel = null;
	private javax.swing.JLabel ivjPasswordLabel = null;
	private javax.swing.JCheckBox ivjRealTimeScanCheckBox = null;
	private javax.swing.JComboBox ivjIEDScanRateComboBox = null;
	private com.klg.jclass.field.JCSpinField ivjDefaultDataClassSpinner = null;
	private com.klg.jclass.field.JCSpinField ivjDefaultDataOffsetSpinner = null;
	private javax.swing.JTextField ivjPasswordTextField = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public DeviceMCTIEDPortEditorPanel() {
	super();
	initialize();
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getConnectedIEDComboBox()) 
		connEtoC1(e);
	if (e.getSource() == getIEDScanRateComboBox()) 
		connEtoC2(e);
	if (e.getSource() == getRealTimeScanCheckBox()) 
		connEtoC6(e);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void caretUpdate(javax.swing.event.CaretEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getPasswordTextField()) 
		connEtoC5(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (ConnectedIEDComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceMCTIEDPortEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (IEDScanRateComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceMCTIEDPortEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (DefaultDataClassSpinner.value.valueChanged(com.klg.jclass.util.value.JCValueEvent) --> DeviceMCTIEDPortEditorPanel.fireInputUpdate()V)
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(com.klg.jclass.util.value.JCValueEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (DefaultDataOffsetSpinner.value.valueChanged(com.klg.jclass.util.value.JCValueEvent) --> DeviceMCTIEDPortEditorPanel.fireInputUpdate()V)
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(com.klg.jclass.util.value.JCValueEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (PasswordTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceMCTIEDPortEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC6:  (RealTimeScanCheckBox.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceMCTIEDPortEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the ConnectedIEDComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getConnectedIEDComboBox() {
	if (ivjConnectedIEDComboBox == null) {
		try {
			ivjConnectedIEDComboBox = new javax.swing.JComboBox();
			ivjConnectedIEDComboBox.setName("ConnectedIEDComboBox");
			ivjConnectedIEDComboBox.setPreferredSize(new java.awt.Dimension(140, 27));
			ivjConnectedIEDComboBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjConnectedIEDComboBox.setMinimumSize(new java.awt.Dimension(140, 27));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjConnectedIEDComboBox;
}
/**
 * Return the ConnectedIEDLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getConnectedIEDLabel() {
	if (ivjConnectedIEDLabel == null) {
		try {
			ivjConnectedIEDLabel = new javax.swing.JLabel();
			ivjConnectedIEDLabel.setName("ConnectedIEDLabel");
			ivjConnectedIEDLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjConnectedIEDLabel.setText("Connected IED:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjConnectedIEDLabel;
}
/**
 * Return the DefaultDataClassLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getDefaultDataClassLabel() {
	if (ivjDefaultDataClassLabel == null) {
		try {
			ivjDefaultDataClassLabel = new javax.swing.JLabel();
			ivjDefaultDataClassLabel.setName("DefaultDataClassLabel");
			ivjDefaultDataClassLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjDefaultDataClassLabel.setText("Default Data Class:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDefaultDataClassLabel;
}
/**
 * Return the DefaultDataClassSpinner property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getDefaultDataClassSpinner() {
	if (ivjDefaultDataClassSpinner == null) {
		try {
			ivjDefaultDataClassSpinner = new com.klg.jclass.field.JCSpinField();
			ivjDefaultDataClassSpinner.setName("DefaultDataClassSpinner");
			ivjDefaultDataClassSpinner.setPreferredSize(new java.awt.Dimension(50, 22));
			ivjDefaultDataClassSpinner.setMinimumSize(new java.awt.Dimension(50, 22));
			// user code begin {1}
			ivjDefaultDataClassSpinner.setDataProperties(new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(0), new Integer(255), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 2, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDefaultDataClassSpinner;
}
/**
 * Return the DefaultDataOffsetLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getDefaultDataOffsetLabel() {
	if (ivjDefaultDataOffsetLabel == null) {
		try {
			ivjDefaultDataOffsetLabel = new javax.swing.JLabel();
			ivjDefaultDataOffsetLabel.setName("DefaultDataOffsetLabel");
			ivjDefaultDataOffsetLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjDefaultDataOffsetLabel.setText("Default Data Offset:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDefaultDataOffsetLabel;
}
/**
 * Return the DefaultDataOffsetSpinner property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getDefaultDataOffsetSpinner() {
	if (ivjDefaultDataOffsetSpinner == null) {
		try {
			ivjDefaultDataOffsetSpinner = new com.klg.jclass.field.JCSpinField();
			ivjDefaultDataOffsetSpinner.setName("DefaultDataOffsetSpinner");
			ivjDefaultDataOffsetSpinner.setPreferredSize(new java.awt.Dimension(50, 22));
			ivjDefaultDataOffsetSpinner.setMinimumSize(new java.awt.Dimension(50, 22));
			// user code begin {1}
			ivjDefaultDataOffsetSpinner.setDataProperties(new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(0), new Integer(4096), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 2, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDefaultDataOffsetSpinner;
}
/**
 * Return the IEDScanRateComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getIEDScanRateComboBox() {
	if (ivjIEDScanRateComboBox == null) {
		try {
			ivjIEDScanRateComboBox = new javax.swing.JComboBox();
			ivjIEDScanRateComboBox.setName("IEDScanRateComboBox");
			ivjIEDScanRateComboBox.setPreferredSize(new java.awt.Dimension(140, 27));
			ivjIEDScanRateComboBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjIEDScanRateComboBox.setMinimumSize(new java.awt.Dimension(140, 27));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjIEDScanRateComboBox;
}
/**
 * Return the IEDScanRateLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getIEDScanRateLabel() {
	if (ivjIEDScanRateLabel == null) {
		try {
			ivjIEDScanRateLabel = new javax.swing.JLabel();
			ivjIEDScanRateLabel.setName("IEDScanRateLabel");
			ivjIEDScanRateLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjIEDScanRateLabel.setText("IED Scan Rate:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjIEDScanRateLabel;
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
 * Return the PasswordTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getPasswordTextField() {
	if (ivjPasswordTextField == null) {
		try {
			ivjPasswordTextField = new javax.swing.JTextField();
			ivjPasswordTextField.setName("PasswordTextField");
			ivjPasswordTextField.setColumns(8);
			// user code begin {1}
			ivjPasswordTextField.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_MCT_PASSWORD_LENGTH));
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
 * Return the RealTimeScanCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getRealTimeScanCheckBox() {
	if (ivjRealTimeScanCheckBox == null) {
		try {
			ivjRealTimeScanCheckBox = new javax.swing.JCheckBox();
			ivjRealTimeScanCheckBox.setName("RealTimeScanCheckBox");
			ivjRealTimeScanCheckBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjRealTimeScanCheckBox.setText("Use IED Data for Real-time Scan");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRealTimeScanCheckBox;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) {
	MCTIEDBase mctIED = (MCTIEDBase)val;

	mctIED.getDeviceMCTIEDPort().setConnectedIED((String)getConnectedIEDComboBox().getSelectedItem());
	mctIED.getDeviceMCTIEDPort().setIEDScanRate(com.cannontech.common.util.CtiUtilities.getIntervalComboBoxSecondsValue(getIEDScanRateComboBox()));

	Object defaultDataClassSpinVal = getDefaultDataClassSpinner().getValue();
	Integer defaultDataClass = null;
	if( defaultDataClassSpinVal instanceof Integer )
		defaultDataClass = new Integer( ((Integer)defaultDataClassSpinVal).intValue() );
	else if( defaultDataClassSpinVal instanceof Long )
		defaultDataClass = new Integer( ((Long)defaultDataClassSpinVal).intValue() );
	mctIED.getDeviceMCTIEDPort().setDefaultDataClass(defaultDataClass);

	Object defaultDataOffsetSpinVal = getDefaultDataOffsetSpinner().getValue();
	Integer defaultDataOffset = null;
	if( defaultDataOffsetSpinVal instanceof Integer )
		defaultDataOffset = new Integer( ((Integer)defaultDataOffsetSpinVal).intValue() );
	else if( defaultDataOffsetSpinVal instanceof Long )
		defaultDataOffset = new Integer( ((Long)defaultDataOffsetSpinVal).intValue() );
	mctIED.getDeviceMCTIEDPort().setDefaultDataOffset(defaultDataOffset);

	mctIED.getDeviceMCTIEDPort().setPassword(getPasswordTextField().getText());
	if( getRealTimeScanCheckBox().isSelected() )
		mctIED.getDeviceMCTIEDPort().setRealTimeScan(new Character('Y'));
	else
		mctIED.getDeviceMCTIEDPort().setRealTimeScan(new Character('N'));

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
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getConnectedIEDComboBox().addActionListener(this);
	getIEDScanRateComboBox().addActionListener(this);
	getDefaultDataClassSpinner().addValueListener(this);
	getDefaultDataOffsetSpinner().addValueListener(this);
	getPasswordTextField().addCaretListener(this);
	getRealTimeScanCheckBox().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("DeviceMeterGroupEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(416, 357);

		java.awt.GridBagConstraints constraintsConnectedIEDComboBox = new java.awt.GridBagConstraints();
		constraintsConnectedIEDComboBox.gridx = 1; constraintsConnectedIEDComboBox.gridy = 0;
		constraintsConnectedIEDComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsConnectedIEDComboBox.anchor = java.awt.GridBagConstraints.WEST;
		constraintsConnectedIEDComboBox.insets = new java.awt.Insets(5, 10, 5, 0);
		add(getConnectedIEDComboBox(), constraintsConnectedIEDComboBox);

		java.awt.GridBagConstraints constraintsConnectedIEDLabel = new java.awt.GridBagConstraints();
		constraintsConnectedIEDLabel.gridx = 0; constraintsConnectedIEDLabel.gridy = 0;
		constraintsConnectedIEDLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsConnectedIEDLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsConnectedIEDLabel.insets = new java.awt.Insets(5, 0, 5, 0);
		add(getConnectedIEDLabel(), constraintsConnectedIEDLabel);

		java.awt.GridBagConstraints constraintsIEDScanRateLabel = new java.awt.GridBagConstraints();
		constraintsIEDScanRateLabel.gridx = 0; constraintsIEDScanRateLabel.gridy = 1;
		constraintsIEDScanRateLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsIEDScanRateLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsIEDScanRateLabel.insets = new java.awt.Insets(5, 0, 5, 0);
		add(getIEDScanRateLabel(), constraintsIEDScanRateLabel);

		java.awt.GridBagConstraints constraintsDefaultDataClassLabel = new java.awt.GridBagConstraints();
		constraintsDefaultDataClassLabel.gridx = 0; constraintsDefaultDataClassLabel.gridy = 2;
		constraintsDefaultDataClassLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsDefaultDataClassLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsDefaultDataClassLabel.insets = new java.awt.Insets(5, 0, 5, 0);
		add(getDefaultDataClassLabel(), constraintsDefaultDataClassLabel);

		java.awt.GridBagConstraints constraintsDefaultDataOffsetLabel = new java.awt.GridBagConstraints();
		constraintsDefaultDataOffsetLabel.gridx = 0; constraintsDefaultDataOffsetLabel.gridy = 3;
		constraintsDefaultDataOffsetLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsDefaultDataOffsetLabel.insets = new java.awt.Insets(5, 0, 5, 0);
		add(getDefaultDataOffsetLabel(), constraintsDefaultDataOffsetLabel);

		java.awt.GridBagConstraints constraintsPasswordLabel = new java.awt.GridBagConstraints();
		constraintsPasswordLabel.gridx = 0; constraintsPasswordLabel.gridy = 4;
		constraintsPasswordLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsPasswordLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsPasswordLabel.insets = new java.awt.Insets(5, 0, 5, 0);
		add(getPasswordLabel(), constraintsPasswordLabel);

		java.awt.GridBagConstraints constraintsRealTimeScanCheckBox = new java.awt.GridBagConstraints();
		constraintsRealTimeScanCheckBox.gridx = 0; constraintsRealTimeScanCheckBox.gridy = 5;
		constraintsRealTimeScanCheckBox.gridwidth = 2;
		constraintsRealTimeScanCheckBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsRealTimeScanCheckBox.anchor = java.awt.GridBagConstraints.WEST;
		constraintsRealTimeScanCheckBox.insets = new java.awt.Insets(5, 0, 5, 0);
		add(getRealTimeScanCheckBox(), constraintsRealTimeScanCheckBox);

		java.awt.GridBagConstraints constraintsIEDScanRateComboBox = new java.awt.GridBagConstraints();
		constraintsIEDScanRateComboBox.gridx = 1; constraintsIEDScanRateComboBox.gridy = 1;
		constraintsIEDScanRateComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsIEDScanRateComboBox.anchor = java.awt.GridBagConstraints.WEST;
		constraintsIEDScanRateComboBox.insets = new java.awt.Insets(5, 10, 5, 0);
		add(getIEDScanRateComboBox(), constraintsIEDScanRateComboBox);

		java.awt.GridBagConstraints constraintsDefaultDataClassSpinner = new java.awt.GridBagConstraints();
		constraintsDefaultDataClassSpinner.gridx = 1; constraintsDefaultDataClassSpinner.gridy = 2;
		constraintsDefaultDataClassSpinner.anchor = java.awt.GridBagConstraints.WEST;
		constraintsDefaultDataClassSpinner.insets = new java.awt.Insets(5, 10, 5, 0);
		add(getDefaultDataClassSpinner(), constraintsDefaultDataClassSpinner);

		java.awt.GridBagConstraints constraintsDefaultDataOffsetSpinner = new java.awt.GridBagConstraints();
		constraintsDefaultDataOffsetSpinner.gridx = 1; constraintsDefaultDataOffsetSpinner.gridy = 3;
		constraintsDefaultDataOffsetSpinner.anchor = java.awt.GridBagConstraints.WEST;
		constraintsDefaultDataOffsetSpinner.insets = new java.awt.Insets(5, 10, 5, 0);
		add(getDefaultDataOffsetSpinner(), constraintsDefaultDataOffsetSpinner);

		java.awt.GridBagConstraints constraintsPasswordTextField = new java.awt.GridBagConstraints();
		constraintsPasswordTextField.gridx = 1; constraintsPasswordTextField.gridy = 4;
		constraintsPasswordTextField.anchor = java.awt.GridBagConstraints.WEST;
		constraintsPasswordTextField.insets = new java.awt.Insets(5, 10, 5, 0);
		add(getPasswordTextField(), constraintsPasswordTextField);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	if( getConnectedIEDComboBox().getModel().getSize() > 0 )
		getConnectedIEDComboBox().removeAllItems();
	getConnectedIEDComboBox().addItem("None");
	getConnectedIEDComboBox().addItem("Alpha Power Plus");
	getConnectedIEDComboBox().addItem("Landis and Gyr S4");
	getConnectedIEDComboBox().addItem("General Electric KV");

	if( getIEDScanRateComboBox().getModel().getSize() > 0 )
		getIEDScanRateComboBox().removeAllItems();
	getIEDScanRateComboBox().addItem("1 minute");
	getIEDScanRateComboBox().addItem("2 minute");
	getIEDScanRateComboBox().addItem("3 minute");
	getIEDScanRateComboBox().addItem("5 minute");
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		java.awt.Frame frame;
		try {
			Class aFrameClass = Class.forName("com.ibm.uvm.abt.edit.TestFrame");
			frame = (java.awt.Frame)aFrameClass.newInstance();
		} catch (java.lang.Throwable ivjExc) {
			frame = new java.awt.Frame();
		}
		DeviceMeterGroupEditorPanel aDeviceMeterGroupEditorPanel;
		aDeviceMeterGroupEditorPanel = new DeviceMeterGroupEditorPanel();
		frame.add("Center", aDeviceMeterGroupEditorPanel);
		frame.setSize(aDeviceMeterGroupEditorPanel.getSize());
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val) {
	MCTIEDBase mctIED = (MCTIEDBase)val;

	getConnectedIEDComboBox().setSelectedItem(mctIED.getDeviceMCTIEDPort().getConnectedIED());
	com.cannontech.common.util.CtiUtilities.setIntervalComboBoxSelectedItem( getIEDScanRateComboBox(), mctIED.getDeviceMCTIEDPort().getIEDScanRate().intValue() );
	getDefaultDataClassSpinner().setValue(mctIED.getDeviceMCTIEDPort().getDefaultDataClass());
	getDefaultDataOffsetSpinner().setValue(mctIED.getDeviceMCTIEDPort().getDefaultDataOffset());
	getPasswordTextField().setText(mctIED.getDeviceMCTIEDPort().getPassword());
	getRealTimeScanCheckBox().setSelected(mctIED.getDeviceMCTIEDPort().getRealTimeScan().charValue() == 'Y');
}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) {
	// user code begin {1}
	// user code end
	if (arg1.getSource() == getDefaultDataClassSpinner()) 
		connEtoC3(arg1);
	if (arg1.getSource() == getDefaultDataOffsetSpinner()) 
		connEtoC4(arg1);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void valueChanging(com.klg.jclass.util.value.JCValueEvent arg1) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G74F854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155BB8DD4D447394620A109095810449A131044665923CDE81FBD7535B631547410975B173EE39A7A6AEBED1EED6CABCD4D2B6D2129ED871085FFD094D0D4A298FF8251A0A2A022C9C1D0C157CA049A7C0BDEF6AF30323BF73DFBD796FF627B3E19BBF36F6E5E65A7673DF3DA0F9F774EB75F5FFCB3734DB7F3E7C9C1C9C2D6FAC681A169D9C47F37BF1D10A9C789B99F1BF41E63C649C9B6E27A3795E0AD
	59E6DF864FB82833CFCB326511785A9CA8CF01F25E13C9368D701ECFBEDFFB79903CC451D387F5B7DFAB1FB5381E1579DCCFB6AD5F28D9854F55GD381074FA52813BCD0D32463676AF88E699904AC68C61AE7EA0AF5DC9D145BGA683A4F6B37AE4F8AE271A47545769F497A6A410A57F7AEEC6AE4AB14964D05643F86D7A3314BC1B3E2487F53DCA6DC41E98A84F824046E712677E7AA1BC8F357575FC68D0BC958E496BD53C1A6C682D101DAECDD1AB1C72B517C32E2FEF264F13CD9D2DC71A5B94D5BBC22B5B
	A42F6CA644570BBACA284FB331BDD0DECAF1FF28C23DB360FDAE40C201CF5A077886D7122D93406762ED7D605DC3FADBBAEFBCCB163E6A4DDC93252D31AE5646C41751567840DB599BE93F1CA8C07F27C2DD5E15A45BF6009600DE00D8004DF4BC1C4EFF8F1E0DDDDA136271A85EC61F6F305B291ED41C329B5E6B6B21C6471D169D0A6AA4047579FF566C3630E7B0E0FC3F9CB19F734954FD3C7F271FFE1A4C7F6793995996BE19BF35B2A3618A73454CAB06CF186E6D3551F427DC899D1F213ABFA6E65D29E315
	4CAC8B5DB3DEA94FC8556510BEC3F7E59F7635C27B3A945E537571417083942F1970EC2C26D3BC16E320EED19F9F9B1D2B39AD76E513C85EDE3B259FA6A6B5E6585D4C8627BB5C167AEA149D67E6BAF305AD2D94BF4304E736A4D3DB301C82F54D1EA49B9BDF9D6BF16EA6C2F9AE40C20025G455EA4DB9D005D4B4758A379FB06B9465AA4D5BB9CF0391D32CA98EFC741FAF84AC1CD15DA3DFECD72BA64B3126A123A5C321F68B4978B5087DDA1FCDDF46CBBC047B159AB2B1266D23CA0DD72481A2C7A99CDDF41
	DA24D1E5DA5B617248204063A3DC2E3FA09F1EEE492F7D4D6714B45920E075B78A30CF2EC9E7C0A28800F76617BBB6203F5C507E9800F861075FD1F9A7E49562C5FD3DDDD17A823E8EEC89D968C5BF9F2431E3AA3C4F76F29F5FAAC2DC1912E4EBD0381C778B571964B43B9CE83E248E70B99658077139C361E3660DCEBEE61AF33FC0F63FF25CF24CA05CD458D809D742674EEDAA936DE2656CFD1DA31C472BF51B069FABCABF130E4CE152B17F7EF2A37655AC30AB85A0CD78383730140D594312DF662374148A
	83CCF69A9EE7F6B6D497697EC45DD93A7D687B1522AD27A8CD1E8F668640E500D800C400949F77697776DD9EC44EC87DB038EFFFC1F5D969497DBF5445FC7949FADC3F64EB32D7EB16FD325789AFFE3DCEDE0FFA75795D2D881EBE0A77C1BCE802896897782B6BF14ECA1A263A3A821A5CAEEB1A4B5BE3548FD0DD7EC0174F88A5DC57287558D6259F6201209F339E6712C371FAE55A2902C52F3F0B4EE559ADFB4068A6A56055987E3E8DB89F7A6481BD16CC58000952B5499D403844720AC4897D69D3DD9E98AA
	985A0873AA4E735335C5A69F9140E1FED487CF7BD5BEE77B3771B93B7D7A93E476259D99D6EB4E44AFB4E7CC3F4A66546C2B461CE5B60DAE3236BD1646949D6FE73692956B748487E364B1D589784C898E1BAF5BB7234C8520EB8940AA154F17E59B311E47A4FD22B4A9EEB7F32B70771B9B7330FF642B8199C2B9863F934207E99B5705551D517D496A49A674275B94B7E729684FB155BC075C8E65C3G97D4665349AA738923EB9539E60EBF49D66A0FC677C7C157GB41DGC13F955D37EBB95DEC28CF09CAB7
	FABF27CB037ABCAD49D629D9519D2963F4EE280F811892C6477AAA7B9266B1D679EACF40D59150DC38AAC89ACCA8DFC0231D47F253D9F4FDC1998BB51E03BEFBG7B33B810E42B86308778787BFEAE4FC1637B92480227B6C54D371D81E6DBEC4098FF25F7729C54DB0CBA1321AEA540474A0DCD66F530516FD79CAE3A846B7D7C5EE67378E438BD1BD740335B257A35D688A0C1BEA6EAB6233D38C66BE8BDE7F90666B25856D4503B7DDA12ED62B5444F2CAFB059FE619ADBEBE36019C9659556D4E1FC21033602
	BD5AE435DBD1BD32336C0AF4CD2A107A358A9A4BAA9AE97591FCAF67FDB327FC9D33D3265EE709C3D979991ECBBE195F206B1E7B4CA4F268F6FE06D5EE9F3B666D0C855718BFF37A8D3F5E7C0AADB37513BFE433B62AB53C0D9DC0B32E1F37F1024D2A0DF3201E716D2C65FCA9004B8BF23EBB03E63E16A0633B99643E392F361E47388A7A3729D7727600DFF0947639FBAA2EB8B0D653914888AA0EB751A94CFD4474278A7DF1A0BBD9687754DA69A7835C6EE97BB9DF81606C831C6FC14D4C97B34078268FF03B
	5755E17E6910D4D92B20FF75DE416E8CD6787BE18151FB3489EBF5E319CEEF9D57B99B6425891D76AC334E4A6BCCE767F52E73153A4FBA0E587A7C2D083D0A7B3A11BB0D3A9E19C7319C5ED10F7646DFE7B97C86016F277839A6BCEB5FD7697C4772329B50069BBC6F7C635B38C65D3C896D85C884C886D88410F30B4F63EFEE5EC706486BDBA1AE29DE495D9E32A0737C3A98FD85EB28DE6EAF4E62630BF0BF4C288E774376DB21EDEF88ABB33E24883E0A703895464766EF0F9BED213155B4BC6A6B0FC39B3B14
	43CAD05F677F3A9EE3944E691B37F84C3CF5549CB3E72D294AD8E9B13791E6683A675DB26676EE31EF7F2792DC878ABE05F58220854089908BB085A079D36E7F7437BF984A7FF4D0F5A8CD00EB01F533A9BC4F917E8AC66C4B0767673191FC61FEDE74E9A89F73DF320D7B0F5C66796F6E08B1BFBC9B6AA37842ED283BED55571F566EA426F55494413ADD325B091D5E6EF3F90FE2015B7D75E00E2E37CD587DBF1F51EE32FFA83BE3EFDB79EECAAD37A1C158706588D943336179A1EDC831745D0E32E26BF95281
	39BC759833F52E46F30E4542D62D6E3359BABA02AF5C5632D1D636FEF8A65A1C6E159DFD461C3EB30B7B35816430BDF98A75F5904A6384EECB9D6292211CBC0A3749D50F3625C2F9559DC9365DGADGEE00B80069GF381D28172C6C32EB51A735D22FC1833F6GCE8308871881B0EFB44B81CB759CB4C54F9D995FB78EA40635BD9C5006F05C5CB01C5BE48F1663ACF8D243FC1AEAC15322371117D7D950987D785FC35A1EED01DB9BD64658DAC8585A05E52BFDEB78D9C03E858D4620B5E1E3464AA696F3EDDB
	226FE5F337D86FE57337D86FE5B7EED94B70A043FC72456B37EE99FC2FDB3E45EA2F7BB6ED770BF49F6C83F5321FAE8238A1506BDE5750EF91FB5E3FCA9ED977D9BE79E575F21D660826B46D95FD37241E4F41A5B0E6F26E8C6D67E2A8D70661581AD631957325E6283B8C90840883C83C132FE54FEF99B217B8929CA41778F2C974FE19D9E25DAF4F15601871AB8155A1077B7E0512A2D1E72C1A2D1A6CC1B7B11A6FD1FECDD2FB64083EFB0572333AE87CBFAE09563F2C8F1E2DB177C11568036F5AF89F3C8C7E
	DB98666FB4A82F0A8945B1F90EDA333CFDC25E73C1AE2FB4A6F25ED6832E45D2DE79FE333CEAA16F0BE2BD3CE0A14F873898CBF90BC2645588F95F57867BE6B0B1CC5674981E03CF2BB34B2B9572E2334C72E66B7C737467A28B1B193CDF5762990FDB9AD0829A6C394660DAE05B4F4FF21682DF419846FBE88C5B8F3075674B7D982B3AD549835D5B6E139D24F2AC725654F15E49C01F8870B240E214BB16376150B51CB77A88121D2DC71AF9A24A6A5F25F151DCEF173A70F314EE73422BF85E8268F60764BD8D
	3B9EDA4D6AE67E8563C0335CAD855CB4BE5070944AFF7A5A7AD01AD35D5DFED95309984D1F9CB88F5A0037DFD11D217C09B5E87FE9D9F26369B99A4197DDD67F23F772C36D93F9B62B1FE15FE0E1A326E5F80A47E8DEB8D8E4E523CE44E81E3B3E56E423C8D2F07C507D5C57A573FEAEB816755F6431FC3F770595D67509227E2B99D675F3C47D55F42B7A94D1BF4D32BEF5AC4FB9D69E441C035C15E4AB3E0B631EBF0038CAA8B7885C32F61E1BF4881C3A03FE8706722C3BF83C2D5D9ABDDEFE3055BADEB6ED35
	DE47DA378E3E0EFD34B5DA1CE376AD3471363DACEC3E7881EF5BC2014BA964EDCB93B847915E3616D80E7BC1104B3BA0F0CF1F623CBE01DBB540F9E78B5CEC1A6F4D0372E201A3C14E3B6CEE0EDBCB69BA206C9638C59A279BA5F089D508FB994B8277F896EFDB1A403D758962D65D837DFB8F471D3F4465155D434FC0AA7673EF58C140A55E43E34C5E4BA179B81EAE54579FD3DD4E43D20F1DC6ACF65E57C77997819F39B749D6F4AF5F3B86F7723D6B7427A7917B28931667FD8BBA2AB2AA6FE5B12E63DEE36F
	BA95762EAC077E971A9BA3DF9C54A75F0B38576B4CE39B6763CB99ACF7E7759B8F186BF12C1493F37D0910FA3437B153DCFFAB24BE4B444F5A76528E5E3625AFCDA56E2F9E08FA1619B70E3529DAFF361D59C6365F7307EC66635FF497914BFD848DF92C7DEFCAFC8F6186998CB7FF8547CD9138548C0E1BAFF0D353B92E60BE0EFBCD606C7771B5611D03E83FCF153BE5D5151D6D2E6B18374D3A0FFEC324F1A2865E63847D8F8E60370AEE452BB11AD49AB7487D10A7G445C0F384D6D4D3A8F8E5DEF3D7E716F
	D288EDC0638458267BBBF8BFBBD762B479646E9D38664102677284BC54C2267B899A0B6682FD8AG4365E6A3EE59F8F89F4FF1E3697CBF846516715C4612C2EEE3F03C350DE698B7BE54460471060DCC47B89ABB2683FE26507BE7BA5797C2F931507B4DD3DCEFF11C355EAC135ECA20C158266BEA0E8B57DBC363CE9B60AF4471BEFA35B6FA1FAE2631A696E8636338CD9B025C2659D1EC4A9E4426F9F1617DD5D6EB5DDF8DD4778220CF953A7F2CF15DC5932CF5AFB7695EBDA1D4F74304F05DDF2BC2DB9D0ABB
	60717AF53D5F23F193E301FD826F1BE49AB7AFC039F782775B74CE9C5B9AACD693A760DD251F176150E6F62A17381D73AD6CC47DABCDFD678C9B530BCDF6323C31BA622CE4A8E0FC07C74C474EECFFC2E375D2139D8C3F0862F3BEE760595AF0D52C8D85D0977C9CBF4BFD319C6356FCA8278164BDG6B89C08B0073811E8F3C3E35128C317F6AD0FCF6C8985C26A51E356F3908F31921004565EFE57338BC636BCF1219E3FF1F9E6D3CB478G6B3F097AF3D147B6535E7242AEECDFB254AD844809CF321582D483
	5863F97B763C331B358FBFE9B6EA1A646825DF17F8634E3864FE830DC9BAEB5B6FA4F35BAE47B37D1A7E0C0D0F9CD70C6F27AB4CFC13F53AC47DB9A7AA5F1B99E63E941DAED5FF12872371FDA95D4CD7205315694FC3D179DE8F61EB53691C7AB3E8414766592677896173F7460192F8FA657ED271E1239912FDAFDE0AA95BDBDD393B34EAFF395E67897D66FBFCF5AB76DBFE57C698276BCE501F336E5A266B6E28B2F4271DFEA7DC77D1D8684A5EF13AA43752734E6ED2D03F373A1C1F1D5A5B79F9E48A48ADA1
	6CCC082E077ABFAB9C535B0FDBE63D9DBFDD4547EE7134C7C8732B3FCF6771ADA227F80859FF79A1A3DFDAF4E5E9B61319D6ED3465D1E9B8FEE4FC1BEB8D3E539F8C1FEFEE3D413734E0E43EEBAB643E1B7810356F180EDF9FB45E57FDE0686B8E8E5F4ED19F9AFC0F1C5A921D2F86592A040DEF1C62B63EBC280DCF7D45F87FCE03212B2DF6B83A985FAF9A8D3E5F768F5F4616A037B167E1587FD1D92B1A8CFB76B49BF2035A7065D686385C060787EB7B69A3067C1FD561182D93F54F9CB36A5E78E43863034D
	09FFBBEF1E5BF91336E67075A9A21EBF4C66C24C43461CE0EDF86A92EF43CC611B2D2DA81D452B1F6DB75E5FB03D776F7FACF745C6360E477D3F6AE06B543F17C75F177F305CFADF7E13F26BFD794F4A87BFBF7ED5F9743BD2CB4A4DF72558BA7BE6AB6E3396C33F2C1A88B99DC0B3C067C43E460EFE8F478B5E660CDCDF0FB8FB709C95EFF0EADC3F3CABFADB5D3B063E77995805F7C23A94CDD3BCA81F63EF6C427E52949FC56AF73FF6210FD4D7CF2F46C8F57C58DDB85E5CF2B7C533FDF7EE975F1FEB9379DE
	7C41CE3EBF0F953837BA797EBCDE605268DEEFA11417885C460B084B79BC6C37BF4FF11FDE643C5B85AEA793F19A14E3846E3DF3081B88652982B77E9C671DA1F0F368BEF15924A4DB41A40E9BB8CB6F0EC239CE60FEFC1673B68B5C1FA8EFBC1427983CD447CCA8A79B3CC26F3CC9BC37DCFD82F15B9FC932B5BC4247412577F741730CF4C54FA259ADB2BA92G5926AABED955869816DE8F516757C76DA1399648539E997C3EA83361389D83E6AC50CE81C886D8G3084E055E44885812A81DAGAE8384814681
	A4G4CG1883108210BA195BBFEBDF79A0766B07AAF4B02B128B0733EE7F5F8A4D7613C721AF9E9D5CFE46771BD3E63EB260B9B4AC3E1341105C8DF81C43622B8918790240B3EE88BE66674413C44C1BBF9E5B914DC7B05B0F43FAF12A6B0A6C50BA249E21F76AA5335E8450B9E3883DCC576FDB234789F2FC68B8317CB83DCF86733BE34027FFFBD9FD9C4F7295FAD0AC3E539D4F9338D6FF1B62F761B79B36772C25B1A1996C1D77A8DF1772CE21BF58DEF36DD93E57DC761875DE53FCFE923E57ACFAACFCCF3C
	E6E568194D0E15F41E81DD65E3FC6FDBF9B1F46F3B2F4D3067410B5C1E4BD16C499C449EAD421E124CD0FBEAE82C9A85F46304BD0D67C26D11BEB26CF97A9C37E7D694FB328631676588FBFE149EEA4FD29A4366835DA2E1CF5C59D0FBE2CE9B767CD674D751635676E4C731876BF6BF9EEECFE1C628BD3B6879DE9D50B5BFAE6C89734FB75ACD76887FE8D16CD99E459E74CF2C499EFDAC1FC0BE317633B02B57EDBA016B384B8B43DBF23B2E532B176DC0686DE1F13E176E1162F5198C67213869BACE3F57ED9F
	64DE37FD68F96935DB2F5F1ABDF27D9E30DB2F5F376D66751B61E26C68139E08531D026EEE01BBABF077D319087B586554FA85BE1E5A0578969995F27CA4EAD7BF45F56A3847CD3833BAEE0A9DE38BB439D7D1B96F9769EF733ADDEEB7ABFF0952383C90EA04BF3EC2EDF479A4135DDF93383322AF5C05117DF335505CBF8C3722AD12EECDDBA41DB398C9678AC652598311F49F85A2691E3F94C9B7671219CE5F631DA1E27E5C5DB9323D686133FC1F107CB80F47CFFDCC046C4E0B066C5C0BA3137D1D0BDCB6C9
	6032EFFCE248FE6D32A1FB4B391149FE759C17DDA7E4E7F599322F769A32BDE7C7A6FB27106D933263DD066C9BEE136C915A3D73BC17BDD548FE4CC3880F5B524ED5C6CC215309610B0339861ECFA7BD9FB8F6561C8FACG39CB9206133F3CF4514C1773C4122D780961704DBFE7662B841E66E1716D8A616B849E7770784E1B79C681CF7C90FCEC2EFCF2B6F27EB84EC64E3357AEC6523DFEB1126EB767A269524EC5529D3620BBE6C5F75E026E3C190E7D56782116BD90E0277F0AE4E92464EFFE393475C8F3A9
	FE1FAF6DD654D23C93703E6672482578695F2F12F511A79C0D777F705569171A8BB2C8C6AB3175526153CA327AF1FC85798F754681EFEE48A8FE61091A1F4F7D7199FFA63135120D38A8374BAADD6BE4A76A57FF30F714AC77A9104063670ED6324CCF9FD9FEED40AD5BC9C177E97CA10ADF131D0DFC9575EB24500E1A76609577BDAD123FD743956D201DAC7362CF8B49BAD66D163CBDFB5812FA105A195EC8E3865B98003D7954DEFAD1DEC8497F461A9DDB0FAF1C19C9B20E1295EEC5F29E159C10D4371265DA
	2FAA7BFB9537B3687BCB839514530F7E467372CC9037D9B49F9BDB01579B0451FD41C74765AE4ABD1A81293ECA76D0CA4CC1AA84053329D7D2C1152C36EA78D3C9C55DAE6A6E58226D7C6E318D97B2C83A5E482B64D3129574E94456ADF9DC6E81F8E926B1430FB54BB5782FC2FF62D5F7302CB44CB2DEA594D4BC70E26503DFFC6B9CD5E0304208E12E8456BDBD32625953585D6D724AC2G7895443FAD44A3CDC5984D3A600442632F7C398B46C1B34974D4D6523FFB695FAA7C3B17E276D24C5EAAF06EE2B268
	3F707AA0EFB39DBCE04F9AEA8FF6FF45D168C16DBFFE756213BFBA34B895DAF714E4512B6D5089F98655DF831EAED99DBB7D4137D746AF38977B4807C3A508F2F303C2A2363015A68B8B2E6D2B4F7C2F27B2B348B21892ACF902410B99908CDE154D10CCC81E683843A03CC2B7B60B9A4B5337F196254C08110A586C48A1076815226331341A1266513F79A3F6A3F295D25E8D2275254C391B06598ACCB72B0660B0E8C64EE1CD73F76531BD219E637FA07A2E969744B086578382C3B1465DD23CF38508FC7D7A94
	0B4F749C913085A1D8F62D8A504B79BD2A7A3FBFA79C0C62043FBFCB8F7F43D85A7A8FE3E95BBF0C259FC731D47F16D2FE5B7CADA5EDCCD1546F24F153D8CEB9E51A71AD051D57EDBAC96FEEGFE69B4FE36F9450E9FEE231D5B35FBA437DB135CFD0DDEA71E619D9E50E463FBC149C9146F67B4FAA52B5BF9926D3EE26279DFD0CB878898C683C24697GG4CC6GGD0CB818294G94G88G88G74F854AC98C683C24697GG4CC6GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1
	F4E1D0CB8586GGGG81G81GBAGGGG98GGGG
**end of data**/
}
}
