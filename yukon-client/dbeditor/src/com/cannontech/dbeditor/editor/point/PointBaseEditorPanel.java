package com.cannontech.dbeditor.editor.point;

/**
 * This type was created in VisualAge.
 */

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.AccumulatorPoint;
import com.cannontech.database.data.point.AnalogPoint;
import com.cannontech.database.data.point.CalculatedPoint;
import com.cannontech.database.data.point.CalcStatusPoint;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointLogicalGroups;
import com.cannontech.database.data.point.StatusPoint;

public class PointBaseEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.cannontech.common.gui.util.DataInputPanelListener, java.awt.event.ActionListener, javax.swing.event.CaretListener {
	private javax.swing.JPanel ivjBasePanel = null;
	private javax.swing.JLabel ivjDeviceNameLabel = null;
	private javax.swing.JLabel ivjLogicalGrpLabel = null;
	private javax.swing.JLabel ivjNameLabel = null;
	private javax.swing.JTextField ivjPointNameTextField = null;
	private javax.swing.JLabel ivjPointNumberLabel = null;
	private javax.swing.JPanel ivjSpecificPanel = null;
	private javax.swing.JCheckBox ivjOutOfServiceCheckBox = null;
	private javax.swing.JComboBox ivjLogGroupComboBox = null;
	private DataInputPanel currentSpecificPanel = null;
	private javax.swing.JLabel ivjPointNumberActualLabel = null;
	private javax.swing.JLabel ivjDeviceNameActualLabel = null;
	private javax.swing.JLabel ivjPointTypeActualLabel = null;
	private javax.swing.JLabel ivjPointTypeLabel = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public PointBaseEditorPanel() {
	super();
	initialize();
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
public void actionPerformed(java.awt.event.ActionEvent e) 
{
	if (e.getSource() == getLogGroupComboBox()) 
		connEtoC3(e);
	if (e.getSource() == getOutOfServiceCheckBox()) 
		connEtoC4(e);
}

/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void caretUpdate(javax.swing.event.CaretEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getPointNameTextField()) 
		connEtoC1(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (PointNameTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> PointBaseEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC3:  (GroupComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> PointBaseEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
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
 * connEtoC4:  (OutOfServiceCheckBox.action.actionPerformed(java.awt.event.ActionEvent) --> PointBaseEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
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
 * Return the BasePanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getBasePanel() {
	if (ivjBasePanel == null) {
		try {
			ivjBasePanel = new javax.swing.JPanel();
			ivjBasePanel.setName("BasePanel");
			ivjBasePanel.setLayout(new java.awt.GridBagLayout());
			ivjBasePanel.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
			ivjBasePanel.setPreferredSize(new java.awt.Dimension(350, 140));
			ivjBasePanel.setMinimumSize(new java.awt.Dimension(300, 140));

			java.awt.GridBagConstraints constraintsNameLabel = new java.awt.GridBagConstraints();
			constraintsNameLabel.gridx = 1; constraintsNameLabel.gridy = 1;
			constraintsNameLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsNameLabel.ipadx = 71;
			constraintsNameLabel.insets = new java.awt.Insets(5, 8, 2, 3);
			getBasePanel().add(getNameLabel(), constraintsNameLabel);

			java.awt.GridBagConstraints constraintsDeviceNameLabel = new java.awt.GridBagConstraints();
			constraintsDeviceNameLabel.gridx = 1; constraintsDeviceNameLabel.gridy = 5;
			constraintsDeviceNameLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsDeviceNameLabel.ipadx = -9;
			constraintsDeviceNameLabel.ipady = 3;
			constraintsDeviceNameLabel.insets = new java.awt.Insets(1, 8, 8, 3);
			getBasePanel().add(getDeviceNameLabel(), constraintsDeviceNameLabel);

			java.awt.GridBagConstraints constraintsPointNameTextField = new java.awt.GridBagConstraints();
			constraintsPointNameTextField.gridx = 2; constraintsPointNameTextField.gridy = 1;
			constraintsPointNameTextField.gridwidth = 2;
			constraintsPointNameTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPointNameTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPointNameTextField.weightx = 1.0;
			constraintsPointNameTextField.ipadx = 192;
			constraintsPointNameTextField.ipady = 5;
			constraintsPointNameTextField.insets = new java.awt.Insets(2, 4, 0, 6);
			getBasePanel().add(getPointNameTextField(), constraintsPointNameTextField);
			
			java.awt.GridBagConstraints constraintsDeviceNameActualLabel = new java.awt.GridBagConstraints();
			constraintsDeviceNameActualLabel.gridx = 2; constraintsDeviceNameActualLabel.gridy = 5;
			constraintsDeviceNameActualLabel.gridwidth = 2;
			constraintsDeviceNameActualLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsDeviceNameActualLabel.ipadx = 168;
			constraintsDeviceNameActualLabel.ipady = 1;
			constraintsDeviceNameActualLabel.insets = new java.awt.Insets(2, 4, 9, 34);
			getBasePanel().add(getDeviceNameActualLabel(), constraintsDeviceNameActualLabel);

			java.awt.GridBagConstraints constraintsGroupLabel = new java.awt.GridBagConstraints();
			constraintsGroupLabel.gridx = 1; constraintsGroupLabel.gridy = 2;
			constraintsGroupLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsGroupLabel.ipadx = 71;
			constraintsGroupLabel.ipady = 4;
			constraintsGroupLabel.insets = new java.awt.Insets(2, 8, 1, 3);
			getBasePanel().add(getLogicalGrpLabel(), constraintsGroupLabel);

			java.awt.GridBagConstraints constraintsGroupComboBox = new java.awt.GridBagConstraints();
			constraintsGroupComboBox.gridx = 2; constraintsGroupComboBox.gridy = 2;
			constraintsGroupComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsGroupComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsGroupComboBox.weightx = 1.0;
			constraintsGroupComboBox.ipadx = 35;
			constraintsGroupComboBox.insets = new java.awt.Insets(2, 4, 0, 4);
			getBasePanel().add(getLogGroupComboBox(), constraintsGroupComboBox);

			java.awt.GridBagConstraints constraintsPointNumberLabel = new java.awt.GridBagConstraints();
			constraintsPointNumberLabel.gridx = 1; constraintsPointNumberLabel.gridy = 3;
			constraintsPointNumberLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPointNumberLabel.ipadx = 22;
			constraintsPointNumberLabel.insets = new java.awt.Insets(3, 8, 2, 3);
			getBasePanel().add(getPointNumberLabel(), constraintsPointNumberLabel);

			java.awt.GridBagConstraints constraintsPointNumberActualLabel = new java.awt.GridBagConstraints();
			constraintsPointNumberActualLabel.gridx = 2; constraintsPointNumberActualLabel.gridy = 3;
			constraintsPointNumberActualLabel.gridwidth = 2;
			constraintsPointNumberActualLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPointNumberActualLabel.ipadx = 221;
			constraintsPointNumberActualLabel.ipady = 1;
			constraintsPointNumberActualLabel.insets = new java.awt.Insets(3, 4, 1, 34);
			getBasePanel().add(getPointNumberActualLabel(), constraintsPointNumberActualLabel);

			java.awt.GridBagConstraints constraintsPointTypeLabel = new java.awt.GridBagConstraints();
			constraintsPointTypeLabel.gridx = 1; constraintsPointTypeLabel.gridy = 4;
			constraintsPointTypeLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPointTypeLabel.ipadx = -42;
			constraintsPointTypeLabel.ipady = 3;
			constraintsPointTypeLabel.insets = new java.awt.Insets(1, 8, 0, 36);
			getBasePanel().add(getPointTypeLabel(), constraintsPointTypeLabel);

			java.awt.GridBagConstraints constraintsPointTypeActualLabel = new java.awt.GridBagConstraints();
			constraintsPointTypeActualLabel.gridx = 2; constraintsPointTypeActualLabel.gridy = 4;
			constraintsPointTypeActualLabel.gridwidth = 2;
			constraintsPointTypeActualLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPointTypeActualLabel.ipadx = 221;
			constraintsPointTypeActualLabel.ipady = 1;
			constraintsPointTypeActualLabel.insets = new java.awt.Insets(1, 4, 2, 34);
			getBasePanel().add(getPointTypeActualLabel(), constraintsPointTypeActualLabel);

			java.awt.GridBagConstraints constraintsOutOfServiceCheckBox = new java.awt.GridBagConstraints();
			constraintsOutOfServiceCheckBox.gridx = 3; constraintsOutOfServiceCheckBox.gridy = 2;
			constraintsOutOfServiceCheckBox.anchor = java.awt.GridBagConstraints.EAST;
			constraintsOutOfServiceCheckBox.ipadx = 38;
			constraintsOutOfServiceCheckBox.ipady = -7;
			constraintsOutOfServiceCheckBox.insets = new java.awt.Insets(3, 4, 3, 6);
			getBasePanel().add(getOutOfServiceCheckBox(), constraintsOutOfServiceCheckBox);
			// user code begin {1}

			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Identification");
			ivjBasePanel.setBorder(ivjLocalBorder);
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBasePanel;
}

/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getDeviceNameActualLabel() {
	if (ivjDeviceNameActualLabel == null) {
		try {
			ivjDeviceNameActualLabel = new javax.swing.JLabel();
			ivjDeviceNameActualLabel.setName("DeviceNameActualLabel");
			ivjDeviceNameActualLabel.setFont(new java.awt.Font("dialog", 1, 14));
			ivjDeviceNameActualLabel.setText("(Unknown Device)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDeviceNameActualLabel;
}
/**
 * Return the DeviceNameLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getDeviceNameLabel() {
	if (ivjDeviceNameLabel == null) {
		try {
			ivjDeviceNameLabel = new javax.swing.JLabel();
			ivjDeviceNameLabel.setName("DeviceNameLabel");
			ivjDeviceNameLabel.setText("Assigned Device:");
			ivjDeviceNameLabel.setMaximumSize(new java.awt.Dimension(150, 16));
			ivjDeviceNameLabel.setPreferredSize(new java.awt.Dimension(120, 16));
			ivjDeviceNameLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjDeviceNameLabel.setMinimumSize(new java.awt.Dimension(120, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDeviceNameLabel;
}
/**
 * Return the JComboBox2 property value.
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox getLogGroupComboBox() 
{
	if (ivjLogGroupComboBox == null) 	
	{
		try 
		{
			ivjLogGroupComboBox = new javax.swing.JComboBox();
			ivjLogGroupComboBox.setName("LogGroupComboBox");
			ivjLogGroupComboBox.setPreferredSize(new java.awt.Dimension(130, 22));
			ivjLogGroupComboBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjLogGroupComboBox.setMinimumSize(new java.awt.Dimension(126, 22));
			ivjLogGroupComboBox.setEditable(false);


			for( int i = 0; i < PointLogicalGroups.LGRP_STRS.length; i++ )
				ivjLogGroupComboBox.addItem( PointLogicalGroups.getLogicalGrp(i) );

		}
		catch (java.lang.Throwable ivjExc) 
		{
			handleException(ivjExc);
		}
	}

	return ivjLogGroupComboBox;
}

/**
 * Return the GroupLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getLogicalGrpLabel() 
{
	if (ivjLogicalGrpLabel == null) 
	{
		try 
		{
			ivjLogicalGrpLabel = new javax.swing.JLabel();
			ivjLogicalGrpLabel.setName("LogGroupLabel");
			ivjLogicalGrpLabel.setText("Timing Group:");
			ivjLogicalGrpLabel.setMaximumSize(new java.awt.Dimension(40, 16));
			ivjLogicalGrpLabel.setPreferredSize(new java.awt.Dimension(40, 16));
			ivjLogicalGrpLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjLogicalGrpLabel.setMinimumSize(new java.awt.Dimension(40, 16));

		}
		catch (java.lang.Throwable ivjExc) 
		{
			handleException(ivjExc);
		}
	}

	return ivjLogicalGrpLabel;
}

/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public java.awt.Dimension getMinimumSize() {
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
			ivjNameLabel.setText("Point Name:");
			ivjNameLabel.setMaximumSize(new java.awt.Dimension(40, 16));
			ivjNameLabel.setPreferredSize(new java.awt.Dimension(40, 16));
			ivjNameLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjNameLabel.setMinimumSize(new java.awt.Dimension(40, 16));
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
 * Return the OutOfServiceCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getOutOfServiceCheckBox() {
	if (ivjOutOfServiceCheckBox == null) {
		try {
			ivjOutOfServiceCheckBox = new javax.swing.JCheckBox();
			ivjOutOfServiceCheckBox.setName("OutOfServiceCheckBox");
			ivjOutOfServiceCheckBox.setActionCommand("Out of Service");
			ivjOutOfServiceCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			ivjOutOfServiceCheckBox.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
			ivjOutOfServiceCheckBox.setMargin(new java.awt.Insets(2, 0, 2, 2));
			ivjOutOfServiceCheckBox.setMinimumSize(new java.awt.Dimension(117, 26));
			ivjOutOfServiceCheckBox.setText("Disable Point");
			ivjOutOfServiceCheckBox.setMaximumSize(new java.awt.Dimension(117, 26));
			ivjOutOfServiceCheckBox.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjOutOfServiceCheckBox.setBorderPainted(false);
			ivjOutOfServiceCheckBox.setPreferredSize(new java.awt.Dimension(117, 26));
			ivjOutOfServiceCheckBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjOutOfServiceCheckBox.setContentAreaFilled(true);
			ivjOutOfServiceCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOutOfServiceCheckBox;
}

/**
 * Return the PointNameTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getPointNameTextField() {
	if (ivjPointNameTextField == null) {
		try {
			ivjPointNameTextField = new javax.swing.JTextField();
			ivjPointNameTextField.setName("PointNameTextField");
			ivjPointNameTextField.setPreferredSize(new java.awt.Dimension(132, 19));
			ivjPointNameTextField.setMinimumSize(new java.awt.Dimension(132, 19));
			ivjPointNameTextField.setColumns(12);
			// user code begin {1}
			ivjPointNameTextField.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPointNameTextField.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_POINT_NAME_LENGTH));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPointNameTextField;
}
/**
 * Return the PointNumberActualLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPointNumberActualLabel() {
	if (ivjPointNumberActualLabel == null) {
		try {
			ivjPointNumberActualLabel = new javax.swing.JLabel();
			ivjPointNumberActualLabel.setName("PointNumberActualLabel");
			ivjPointNumberActualLabel.setFont(new java.awt.Font("dialog", 1, 14));
			ivjPointNumberActualLabel.setText("(Unknown)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPointNumberActualLabel;
}

/**
 * Return the PointNumberLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPointNumberLabel() {
	if (ivjPointNumberLabel == null) {
		try {
			ivjPointNumberLabel = new javax.swing.JLabel();
			ivjPointNumberLabel.setName("PointNumberLabel");
			ivjPointNumberLabel.setText("Point ID:");
			ivjPointNumberLabel.setMaximumSize(new java.awt.Dimension(89, 16));
			ivjPointNumberLabel.setPreferredSize(new java.awt.Dimension(89, 16));
			ivjPointNumberLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPointNumberLabel.setMinimumSize(new java.awt.Dimension(89, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPointNumberLabel;
}
/**
 * Return the PointTypeActualLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPointTypeActualLabel() {
	if (ivjPointTypeActualLabel == null) {
		try {
			ivjPointTypeActualLabel = new javax.swing.JLabel();
			ivjPointTypeActualLabel.setName("PointTypeActualLabel");
			ivjPointTypeActualLabel.setFont(new java.awt.Font("dialog", 1, 14));
			ivjPointTypeActualLabel.setText("(Unknown)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPointTypeActualLabel;
}

/**
 * Return the PointTypeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPointTypeLabel() {
	if (ivjPointTypeLabel == null) {
		try {
			ivjPointTypeLabel = new javax.swing.JLabel();
			ivjPointTypeLabel.setName("PointTypeLabel");
			ivjPointTypeLabel.setText("Point Type:");
			ivjPointTypeLabel.setMaximumSize(new java.awt.Dimension(120, 16));
			ivjPointTypeLabel.setPreferredSize(new java.awt.Dimension(120, 16));
			ivjPointTypeLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPointTypeLabel.setMinimumSize(new java.awt.Dimension(120, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPointTypeLabel;
}

/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public java.awt.Dimension getPreferredSize() {
	return new java.awt.Dimension( 400, 350 );
}
/**
 * Return the SpecificPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getSpecificPanel() {
	if (ivjSpecificPanel == null) {
		try {
			ivjSpecificPanel = new javax.swing.JPanel();
			ivjSpecificPanel.setName("SpecificPanel");
			ivjSpecificPanel.setToolTipText("");
			ivjSpecificPanel.setLayout(new java.awt.BorderLayout());
			ivjSpecificPanel.setAlignmentY(0.5F);
			ivjSpecificPanel.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
			ivjSpecificPanel.setPreferredSize(new java.awt.Dimension(225, 80));
			ivjSpecificPanel.setAlignmentX(0.5F);
			ivjSpecificPanel.setMinimumSize(new java.awt.Dimension(0, 0));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSpecificPanel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) 
{
	//Consider commonObject as a com.cannontech.database.data.point.PointBase
	PointBase point = (PointBase) val;

	point.getPoint().setPointName( getPointNameTextField().getText() );

	point.getPoint().setLogicalGroup( 
			getLogGroupComboBox().getSelectedItem().toString() );

	point.getPoint().setServiceFlag( new Character(
		(getOutOfServiceCheckBox().isSelected() ? 'Y' : 'N') ) );


	//Get the value out of whatever panel happens to be
	//the 'specific panel' - was set in setDefaultValue
	this.currentSpecificPanel.getValue(val);

	return point;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initializes connections
 */
private void initConnections() throws java.lang.Exception 
{
	getPointNameTextField().addCaretListener(this);
	getLogGroupComboBox().addActionListener(this);
	getOutOfServiceCheckBox().addActionListener(this);
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("PointBaseEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(458, 331);

		java.awt.GridBagConstraints constraintsBasePanel = new java.awt.GridBagConstraints();
		constraintsBasePanel.gridx = 1; constraintsBasePanel.gridy = 1;
		constraintsBasePanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsBasePanel.weightx = 1.0;
		constraintsBasePanel.weighty = 1.0;
		constraintsBasePanel.ipadx = 156;
		constraintsBasePanel.ipady = -23;
		constraintsBasePanel.insets = new java.awt.Insets(0, 0, 0, 2);
		add(getBasePanel(), constraintsBasePanel);

		java.awt.GridBagConstraints constraintsSpecificPanel = new java.awt.GridBagConstraints();
		constraintsSpecificPanel.gridx = 1; constraintsSpecificPanel.gridy = 2;
		constraintsSpecificPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsSpecificPanel.weightx = 1.0;
		constraintsSpecificPanel.weighty = 1.0;
		constraintsSpecificPanel.ipadx = 458;
		constraintsSpecificPanel.ipady = 214;
		add(getSpecificPanel(), constraintsSpecificPanel);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}

/**
 * This method was created in VisualAge.
 * @param event PropertyPanelEvent
 */
public void inputUpdate(PropertyPanelEvent event) {
	//Just pass it on to our listeners
	fireInputUpdate();
}
/**
 * Insert the method's description here.
 * Creation date: (5/1/2001 9:11:36 AM)
 * @return boolean
 */
public boolean isInputValid() 
{

	if( getPointNameTextField().getText() == null
		 || getPointNameTextField().getText().length() <= 0
		 || getPointNameTextField().getText().charAt(0) == ' ' )
	{
		setErrorString("The Point Name text field must be filled in");
		return false;
	}


	//be sure we check the Specific panel for the right input values!!!
	if( !((DataInputPanel)this.currentSpecificPanel).isInputValid() )
	{
		setErrorString( ((DataInputPanel)this.currentSpecificPanel).getErrorString() );
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
		java.awt.Frame frame;
		try {
			Class aFrameClass = Class.forName("com.ibm.uvm.abt.edit.TestFrame");
			frame = (java.awt.Frame)aFrameClass.newInstance();
		} catch (java.lang.Throwable ivjExc) {
			frame = new java.awt.Frame();
		}
		PointBaseEditorPanel aPointBaseEditorPanel;
		aPointBaseEditorPanel = new PointBaseEditorPanel();
		frame.add("Center", aPointBaseEditorPanel);
		frame.setSize(aPointBaseEditorPanel.getSize());
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
public void setValue(Object val) 
{
	//Consider defaultObject a com.cannontech.database.data.point.PointBase
	PointBase point = (PointBase) val;

	Integer pointID = point.getPoint().getPointID();
	String name = point.getPoint().getPointName();
	String group = point.getPoint().getLogicalGroup();
	
	int pointDeviceID = point.getPoint().getPaoID().intValue();

	getPointNumberActualLabel().setText( "# " + pointID );
	getPointNameTextField().setText(name);
	getOutOfServiceCheckBox().setSelected( 
				(point.getPoint().getServiceFlag().toString().equalsIgnoreCase("Y") ? true : false) );
	
	if( PointLogicalGroups.isValidLogicalGroup(group) )
		getLogGroupComboBox().setSelectedItem( group );
		//CtiUtilities.setSelectedInComboBox( getLogGroupComboBox(), group );
	else
		getLogGroupComboBox().setSelectedItem(
				PointLogicalGroups.getLogicalGrp(PointLogicalGroups.LGRP_DEFAULT) );



	//Load the device
	LiteYukonPAObject litePAO = PAOFuncs.getLiteYukonPAO( pointDeviceID );
	getDeviceNameActualLabel().setText( litePAO.getPaoName() );


	getPointTypeActualLabel().setText( point.getPoint().getPointType() );
	
	//Depending on type change the specific panel to suite
	
	DataInputPanel sp = null;
	
	if( val instanceof CalcStatusPoint )
	{
		sp = new CalcStatusBasePanel();
	}
	else
	if( val instanceof StatusPoint )
	{
		sp = new PointStatusBasePanel();
	}
	else
	if( val instanceof AccumulatorPoint )
	{
		sp = new AccumulatorBasePanel();
	}
	else
	if( val instanceof AnalogPoint )
	{
		sp = new AnalogBasePanel();
	}
	else
	if( val instanceof CalculatedPoint )
	{
		sp = new CalcBasePanel();
	}
	
	if( sp != null )
	{
		sp.addDataInputPanelListener(this);
		sp.setValue(val);
		
		java.awt.GridBagConstraints g = null;
		
		if( getLayout() instanceof java.awt.GridBagLayout )
			g = ((java.awt.GridBagLayout)getLayout()).getConstraints(getSpecificPanel());
		else
			throw new IllegalArgumentException( "***" + this.getClass().getName() + " does not have a GridBagLayout!!!");
			
		//just use the SpecificPanel object as a place and GridBagConstraint holder
		remove( getSpecificPanel() );
		this.currentSpecificPanel = sp;
		add( sp, g );
		
	}

}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GF8F6A2AEGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155DD8FF8D455150F9290959195EB560D7D70CFAD56580D15FE0B4A2E580F36D8F1CBB1AA5DD2B7D656E6F7E3E5373452161617C9C202A4A1A6C123008CA4E002B1A4A1A1989189790F918204A4E0A419E4DE1211494CB873A699645F1EF36FBB773D193C197C59524F757B8E6F5D5F7DF34E3D775CF34F396FE614F2364ECACEB065C8D2C232247ED719A0C92BFAA469D6E951DB04659ED812A2697E5B86
	743AF4C3DD92BC8BA0EF5F2CA5A919521F5FCD0574FEC87BEED91272A63CE7CB26021213709204CFAA647D60F14FF321797C66ED621342521F3B53601981F490B0ABD07481646FF5672A78D4C8E7286FC889A6C932FF0CE5E26AF3F578CA355CC148EB457C03D8E606FB331ABF87B0AC33881E319F73322B60390EC9B739EA1FDA2E732659524A1F94195211172EFD220E033C6E8575E916663001C8149636FC0CE356F2367FC02FF330385762F0B89D0A5CBBD0EC6D112DB64569AED69FAE274D21D4D535606310
	45A357B1384562106DD2BC137B0F75292CCD109B523EEA445C8DB8D6A5705E8854DDCD327FACFD3FAA5B4C333325583B378615FDDEB517B936DA13FD293DABF946A1EC7FE1B697D84F8C698EA0AECB0207E409B9C4322C2B4132E9B5CBD272017657903E18612D1036830557100C9F3DCBB2F61C7FC7E9550DAE5306A0B2C656F0599654E8B2B6F7F5A53F4574E572E61CF3299678826D836A843AG94893409696941CD3B60D95D2354B887871D0EEA176B105D6A7E54E9156D70DED585B9AAF6DC6EF53A2D1244
	F5F1CDFD1101BCC4F3EA7D759009772DDDA25D09BA7EDDA96E401726E4033E4DF9265834D0EDE3D92D56371CBA9C3717585C2E869C6B21BC9C7F9F06F754E9B81FAB9F43B16D023C18BA9A670E8C1225B969AEA9337418A945C016089F541B7655F399DA6AB5D97898F73D03E3FC81708820E82005C0F1C0298DB44657ED5AB35A98B7016E3B9D967B89EF0F4B62D68EF9EDF62B6CD6477AB1F72831B6A2DE6F29F1576363B2FB0FFE0E16BD54E2B8AE4BE6D718729B78F86CEF50460573DE6B89463B33E124EEE4
	08B93C4856042B014FE122402FE1F8188E67F33B0161AC8DF9AB01F8795ADA268B0D01BA5242F0D7E3200EBCDBC3BAB295726201787C7EF9AB5A4416A6D823C061C011C07300168245B7519CBF33E9779857D10BEEFEF95D691B5F00276CD35C16A607C731B8FA65B60B5BE669314B9EC9ADF35BE6142D472FDEA4DB5F3D4023C1F648EE0BE2F3BA20F54B208C4A6461E53E3579F5AC6316D9EE2BEDD0868603AE095AFDE0F3B6BC6D960FF252E535A832D60267FFFFB36A4410258DDA0486F02E8F63F83D5100F6
	AE8D5E8B8E53D86D5A06D89824A38E533814EC490267A7205C323D2A2A56560BE2D85C67C8C763595A0C0272C51FACC9197D892D4D5FF4108E5626BFA095BDF354D087110A1A39BE55B6876A60AA56369D70E9C0D14D340FDEEA6898673A903CD49975984ABEF26D5DB6E19E49EA9BFAACB1A04D47BBE2BB76EF9174AB8EA875D39A7333DB4CDC97F13BA46DBB6A8645112D5A8C7032836F605EA28F498E25D6F6498EAB3CF85439F73E33CE534BBE275099AF4351CE550062F884FE7E1D57B98EED68351C72A731
	FC0B2238EDBDDEC5BEA1AB0A4D512F553F1E496271B3019E352F3767E1DF4D5047FDC00D222FB9DB33F57A556CF41E753ADAF16528FA3A056D471DD0DE791466FFD28D7AD591A2FD83CBEB799BEBC24D9D561B483C7A6BC7F1C89E0173BDB19E07EB49C7A11D62F89B4367C2BFE201D20FD03F17B119E80D9969617FDD265FB19E09D70887570994F3F01D4EB09B6BF40E59D8275FB60756596DE6A31D65F905E66C1FF35821DB8345E634073DCE07C3E68BD1D41138DF7C1BD66C074DE1D35AED56FED9B9E17BCA
	06FD6C886A727B4C661640FB6D91321343F9E4A7733F3ACF1A5F354B501F0A78D613A97288575B68A31A1D64B26D5E067AA05B65C198289A275721F03C0259F39577A472BE35E0DF4C854C9DBF82DBCDAB0CE53DCD36DB0BFAE161AB456CDF3E1794FCE19932780ABD43B0E245ECBA0AEBB0378EDF8B4D6F3FFF40EC64559775595CF213436525E68A5B8ACB384EE4CB0732DD0CFF035B69F541DE5B63BC647495D958D89673C70B6C6EF33A87E5AB63DFEC99D6D46655ACFB825C4BB86F35C5430C77D12FF2346F
	046C9E32754AB583F26F593F0188FBD4BB78D4BE4E55E2184365C02B0F109DFCF09B66139DD40DFD0D53EE67EA86F95DC7D1075EEB4C5169C54ED19EC7E6087C76D172A799664FBECAF2FCF3FBA62E8779CB2F8CEE8C9A62CF64F3640BEC4F8A48EB33583D1A0F7340767570346BECB847E765996B673DF9E8374F0A763F1337D6C2DF05374957D334855BF439ED0330656126244643659EFDBCBC8F644758F9B57631E5C98A2FBB7FA27655E8066635E09B5756D23CDCG6903C05DAD3C1D4896BEE63CDCF39D15
	CBBD36A4458CF47018D1394CFAAA97867991C0F3834A2DBA46FC5B860AB1F78B6CB3061502B1FDB9117C78CFB375F1FA54EDEB8CED83527E63EAFCF3BCB03EB16D44F6DD00079D27B94E4F576F39559E0F3357465CCBF53E1E4E575B8D0EAD4DFF8D1E20589E08F52C320F667C79FC14957DCF95D6676D7E77BE12C67A53FC8A48252999E68B2955D983AEF0A328CD0DB02BEDB69957AA9F070B757A18FF5E06CA537AA063B0D36D7F9C5DB8ACBF53154CE37BC79AE95CD79EA79D793BAA0C6D0C4EB1FA1960457D
	DEDB31D7312173EBD1ACECD56B4FAE3E497C93EC2B68841D8B4C9DC2FD37C27A8250CCA09E23FC0F6D5BD8B68A3065C0FCCDAE4C502F494D2D30BFG8DGF13B75D69D6A49B84D33BA8F8B8A33783C8598A49A5B3B328F2AFC97FD77AEE97F65DC43330A299B361BAE3472B13D27D59B5BC5BF7B3329B335DBBDD37156D17FD6C019021354BFF9033EFF5DA7F97F261E247EAD2BAB0367788DB045FE9F54935FC8E8B3C670F5DE5473CDEB63FC4B5B086F2C7A0972D54FC24A746BF3FE6C5E206B33310D0FD9E7DB
	607AFC091DBD7AG1F56C66B73540E57F56B73B82C880B239F2C29700FF6A6F23B2926576FCCA63BA9D1EC11D708FD1F85ED4E830A810A87CAEBA79EB76F9C75CC234E976ACC23F1BCE793C56D64235EBD62DC61E03B660F37360F744DF95CDFB40CF3BC504E637EE401D7B27C028E67BA714FC32493B3A0EF2560BF37B610FFC707BFCF2543A89E7C5108FA4581690800FAFC7D6DA858CC76C537DC2B2A8E8B3F0B6FE74EE17DFE965D41631D98F846F55059641A8C549526FB27A8E6BF378B5B5F8769D6A0BB50
	D420A82005C07127E83EDF58E9E40F7D67BB18B15662C0BE96676BA6B606570E2897B806E9270D46305F3E49F88C49FFD4EDF164869C9F5C9B72CF53FCFFBC621CF3EC32FEBA22DE202C1D06329EE88BA62B6AE82A325EFB91E55D027CCF53DCAEF80F45871F01FF71994D5B01F771FC1BEC8173E5A432837F75AE7ACBCE372D5F66A0EC8543FA1DEE9634F06C3D426071D4D921F1BC352F50B81EBAD0987A0C203EB0D83C456379E7599E5C827D3383F16C91360706C1FA2640A6FB911B8B69451F51DC168DA336
	9252E91D04FD44B0346B8582DBB0B41E339BECAFD4BE729A299F3F9C970F514E83128352DBD81DA75E5840465A6311BDECF1228722667DDA59A89944219851C994EB3DD23BCEADD327B8EBCA050E7E36B057D8C719E34375735BECDCD1771ABB492F591808B6B59A528B01E245F8A7ED2035322AF364D826FD8EBE73679ADE7EB9697624DD4175723ADD46FAB9E3974A64F1FA5D3DF2206E5D36ABD764E9562BC91187F1D854D80455D7ACEE084D836BFF1B5567F9416AC76F8A265FFC4C3FD7279F73BDC2DF6E94
	E354887D6F7C5CFF1CDC108E870A50613C3D21FAFDFB65223D7BAFC60FC16F22203D791F9B6BF70C0A4741F3B5D06619606BA0DF4D5B97D02699525DC08A50D4B5AFB220CC34AE4D47700977F02F760974D3ACFDCE6827DC1DA166214EE1D50D9E0F13DEE676631790B83EAC4668E79E34F9F64BB927974A15F4E139B8919FAEG7E4B0012G4F6D6232F0BE8F8E63DE5167368C42941EF0493DD2DAB75BC73CD45787652781C581AD820A6FA25F116F4398D672E800E461EB869B337559FAA90F6F71CB520D7678
	D660B950CDEBA9EAD8EFD742G1F592D0D5F6CEE63B91AE700AF86ECF9G4EFD265D49380ED7F5F31F69CD0157B03C4422615C175A4456BD2687A0AF3F07EC74A6365F46C2FA35407EED8F596D4CDE427EA38D31EEC87B84D6D8095834DE3E77F1EC2EE8EF16A877233D084D017482018D16920F25827BE3B971A8309236B61DFD5707F40B40DEE47B2E9552E182BBFF893119101EAB301FB2EC9124638516F799B1C9DE1212A393D65EC6FC4D822B4BA6794A8556D605D8B824E78A6CD1064D0274DC018DE7B13E
	100E9758138C137A783C28E31AC5E335390F4AC9D5B4D6A582B333B1280534D5E0F60679A09D51C731E926C2E74DD100AD6F237838345B4FFF4508282A2A41ED339E3274B7339547770B8EA5D52D3F391F760BA92C4DD6C85B01427B694C533713627928FB6F121A43DA8C3F614736D61AA27B397E4E6F5762266EAE0A67FF60A51E2BA13FFCG3195DEFD4C07EB72B693775DF87E76A1FDBE5AF20C2D357CD63FFC147756C4FD7E943FFC9C23C235BE6F5B13BB28EF2B1F0C166C5FBF9074FE4205815E27D96AB3
	62E41E645B532D766DB5666B23FCCB87E8BCEBCF63F86E37014DG325BE8BE7F056D5561100E9058DA93E273A03DD8E0050924EFAB85365BC4FA147A85E1675AC8FFB78BACF59B69397D8B32D59549382705C1BA82E88ED08C50AA204C33CBD20A009A016CC0D301E61F25FACD2C5EC2C8471D6567EE29F6D837F6FE3656884F812029C0336D7C9C0D575B16C8FB9E350BED065A7FD0B6A33CC8D78FE5C679A2D459834B4E8952C666A83C09386CADC2F61CD35CA75E9C25DEB01FF1C300FEEED475F0FD905EC6ED
	240FC1EE1281FCFA2643AE375CA73B5D3215FF521024C126134CE6AF04F90A8172B16C77FBEAC334192C36CB4FDA2853811427E2F6F83FAA012F994EFF6BB66C9BEC5236C16FA0674E78E4B1BBB69F5E978D523EFC30927B5C6B34FB879D9E355C8BCC46D5701E6420F277303367BEA7F8962C4CABEC1FA9017C7D8E6AC7F869E87D20BEA0599D7E7D8803F403447B41F964D530BD8F7088A1479656873CAA458E4CD93972F4BA27DCA04A7D71A24E81BF2442905E62300AF63359DE9583E5E3C5791BAF611D0481
	888E3F02BEDA6CF85C50627458540F892C5E8317686CF235287752A5544FA1592D58FAAD76EA3B2D5F0127400217F419ECEE01136AD4DDC67FEB907CB50CC3BB1CF8976E8C3A3EEA1D4F19BD0984FC36134636B3BB545826840CED072AA31BD45EB1504E27707C6D1AB5EB388C03D56C9BB07412731F062E322ABF8793197ED8FC817A331C6D1731D0BEDE74E5FAD60E2E7FFE23466A4C4E227E673A284E2F32F0EEFA1C78512645824E2A2C4DCD9673859A21ECAB90478A995FB0C8CF93EDD42633FB9EF8766CD0
	2A5D32255EE6376B5AB9A6F4E21E28B3A9BD5BEF0EF533456A5C416A201DD964A29B7A06B2313BC879932C7779386BF1DF34434B6E0839384F13A0F08B43D33F54F06E3FCC937ECB8E64793E24B395E7857A5C4BA03D8AA8538D7B8CD0A31055CDF168712292E9143331D6272B9982D53B6E4803776F475E095D51FABC057C04B90FFCC71ABB65AF89413EE9785C2AFF20BECBDA73982EFE5F29447EAD043C65C0299E58C3014A011ABD543F5B0B0AF87F7073CB3522D8FA8758179D6A9CFEEC51E00CDDF85FB6E4
	E0AC526D5176B9855E272A69C80F717E978D7802003C25100E779845A2BFB90473A6A901737CB443531440F9BEFC1A66B99772AEA834967EFDF82DD80394832E39CC71D6AC14DB85D462DD12D26B655F81D47BEB9A99BB161B74F19A460E1DD04765657273355541FC2929004D74523A6EE23E54E2C8AF73121D1B51964A4EA586D06A9078DCC09B55F4893CFFG1DFD9A6C9C67D1497C307D00B78E794722F8C651EC69710BC551D7281571BB3716CBF978DDE864ADG2ADB7259C63F3A553D0A57E28FEC9F3F89
	32AF79EA06BE86767B422F2B13A978FA87FBE4B72B49731EBF7F06FF1E9FD3DE66E9A5034A341EF349FEF9130F676873F43509B7669BFD7D675F0B4213753E3DB24475A4E2083EA7CDBFE6149FA5725BAB0D72670B7CEFF798654708FCCB3AD1FE1C48DF3647A8FF35487FFD05D1FE66B065BF1CE5141F8F79FC4FFC99BFE729E39933E7ABBC9D7230D9FC882B3501F940CB048525FB4A0A4C259F942A36EC56307EBB583E5776877D8E36EF182F21E67519F4CD1E34D51D9B9663327FD6FCBC92A33F223DCA1248
	063E10C6B6F4737DF7CB354F7DA5A1AF885FF015DF4C3096C7665E591D4C5BFC7CC32D5F6FD40E255F3C5E727DDA3DC777062CD7D2E4AEAB23FAAFD7EB75AC255B836B5543E6DE305DEA33581D7D5B0B4CD035349C02CC23F1626DED2A5146E7D22D56761F52C249D436174B444BFE124E7CA737D7666D5CDB273523DC42B53B9D960E5959E7D6174EF61E3760927250FF09BE017F56B859C5D845D35CDCD75F2A36736665ED214B56549DE92DBBAE6677271F6975AA73761DA65A8B8267B74D4767B557274DAF6F
	C7C603560F2A6CD063D1DA8963311766E86FE1EDCCCFFD2235E14F9ACB9B3C6CE3D9E36DAF67F321D963733B8F46AEEB6ED12DDECE7AD87509179F90734E53F35273C24B5CFA3C3A293969C8036A5F1D71A2EF7E5E2FFB3FD919687D59317B1677DFD19E5CF772D5847FC6F13942789B45648A63EFE757D7043E0BF8CBC5703B0837D7686FA2F25FAF6EA85A73E650798120E9C033006678486FAB580B3A00B757C77AFCF556FE19A615782F2F8C5E572C4A516F38EF2AC4FF204729A84EC1EC1F70EDACA6D71CAE
	86F2ECE7A50E115B56BF207022AADE444E056DF29F037959D3A9BB8BDB8C7D0A7309B3AFAB1DEF2D94D8E39FE23967404FBBC7584DFDF466D5AB306B565119D70740BEBB4378C2BADCE05FBAC367E0918233A552B9D85CB90A29969F52747E673A771653D7FE8D582EA88F6EF37FD2223F51D88DFDAE7F0A4E06FF3D854FBE0D6ECEE91726DAED0ADDBE243A656A376F9D8994C35B3F22334D938948639EC84F830A790A6EBC1CB6D159F8D1F62A894B661F07B981EABDCF7E4B24E312C4F64DECAA16025A4F3252
	724A72FDFB76162399956F05E4079FBC2E3563E8B387EFE7CF85B326A505E373E1E83F0EB901A97E7EEA2B46EB15A9942F9215177A8D37AF48B7DC666F7231FBB4193E615A4F53F87E7098E10B85B61D2DB95427728B04BD5FA16A8AACA2DD5495D8F63928FB113077770A3A82DB11C56596DF24587C1E130825DE829F7492592C328F5E03E71B650B11B78681ECF1BBDD32DBB947D1F67BD4350531AC4E2A05F63A014201A20122019682AD83DA8114F49962AB20F220C620CEA097D09850CC205917C90E482A42
	90F2289FC01881F5DB781D8CAEC7BDAEC755167DE75D0EE0ED0085BE8C53F634678B39D7E935740BBAAB0E8637412F9E9D5D86CBAD7CBC31DF469008E349AD6C1EC8DF1FC796367E3516CC01B5F9680C0B6BE1E3861EC5A8CE273D55664248D05555C7CFE15FF80C7C2A15E264F9B0E6B1C02B01B24378BD8C5ECED79F6E8796BA15425AAC069DE6B639G4A6E8F235879D106B5C33AC3E08FB1BBBD9352334228CD77993FB6BB284D6B182DDE8465E243A8761EF7E63458DBFF4638826AA5DD25455E39700E5FFE
	9EC8A09EB7B35B1F8FF849D5E47BDF14C6BB7F69384A687E48FF0FDA6F1D00B42F37ABE1E267E9BFE676D539AA701CE5B143432E8ABCE7B915C067AC53A0EF0568EF17E9E27CD7B31B5DFAF5A07FE476ED397B6AC07E53CD445F8EF90B2FA67E73130D763B72C9FC0C9BA7991FC9F5826E8A488B07F444A423397949B1A39ED1EA5979C1F844G9E9710379A521961C6BCEE29540FE33EDAE61F7AEC8E9F49C355378EA3593A55720AC1BD242900C78664C5C3FA0121ECF7249B71D82A160D8F42C31A3CA4A5E732
	FFDE812477CFB66211DB2E6FFF0BDA462ABEFD1303753FEC2F11EC535472338C6AA14D81FCE1C05EB2C82FB0146DF7D9FA5912262831257AAC1AB212873729771D44F7615F736DC94D0BBE093EBA7EDD9044E1362F58F57B93D050514F73D73685377BFFEA9B5D6EA7359B7B5E2F350F743DB7349B7B5E9B5B753E377A77D36DE8A37BE13F6B9065368A6C144072D91B08355B2C4A0040F7B13990EF1411A161A5CC2EE106F528D8058EBB25E2D56D38F7C117871C62BE63476D6C5B1D4DEE67699AD64666002DCC
	0CC7A31351663268647ED4E0A8B737B3DBE532B387E1DEFDD34837FD29586F9EA73F77407DD8FE6DC175E57BE572CF232710FFFAABEBB38E5252B5CBD2F201C67A02319F2B5F1B7A30AFA5459575FC0F9DBCAFC9648F1EDFD7ACCD54071DF1C1EB2765CC08330432320AA2733E12C20373A512714EB3A463787C5C07AEE20A3FBFFBC9132760746660720017DBD2486FB84E4FCEE773EA37B9649A7ED752E20DBE45BE2B7277E7F56FDD89D7BE66727D8DF8189235BE2D573D4FB4DDF95E8FDCD19EBC0E7E950BCD
	CAE0ED9C3C06E26B0635942BB78BCCE923D83DDBE0B76451BDA7C5E02E8B942BCF955824E40A55178AAC7E98710D97582747096FAA01FDE39771CD1BCA580C8A62EB96583E92F157CBE0376CA53E61827B8E0B31A2A0BDDBE029AC8E1E8769C5829BCC90F75B8416FC0AE4AE3816303ED3A4F33940960B7BF30D82FBC45C1F6B945885F1FF4EA5305515A473FC01BD16CEFCE384E64EA03E3182BBDBCEFCD78AECD29A71CD3D0E3007CF0AFBC5826BCE97770A84363B1C46AACCE07FD446E281C85FA3306D622EE1
	3440DAD9FF256B617DFA4117E11BA1DDAE307D0CEFA3242D82CBE3FCFD101EAA30593D44A3D2E0FFE25F6F63A11DB4CD54E55F79F3A1DDA0301EAE762D8E524D827BF1973557B10DFC5A9D72443E2D6F19E0BD7B386BF1DFFDDD9F6EEB3327857A70D98C1FB3AD50077FFB31374E073C7C9BA8067BC3327FDD0DBFDC46A963B1160B590029D0F6668D94875E41EC40FCC8AF9258A34C862C02F452F40A772E39B01EEF2DF427043E9915C3BBB7CA7A7BBAD14CFE9C843CF9BA1DB115A4E8328EB13B61033C7069A4
	57E34CEEC4C3FA1E409EE6F6A38E52AB052CC7CBC613F5FD88D973EFDC12724A1AB5EBD204AC73787DA540775FC8637CF8257F9D2767589A9F00FCC514591A60BF97A7CC5824FA7FA9E1347BCF6BA5AD068E14719E60F1ACC14D6367DE095A585D486CD7B41419F7A30D539F187D0A0374CA013D487AE51E31A425E8860D5DADA16FE585467501F2F54E60731CACE459310F0D8B604A8C1A67D713B4D91B384D033C2899A457CCE6AB16C2FA394022184D4B3C896C5BCDA46B621323491A92C256E6E867FDBF1D3C
	16592D8E402DB7915FDEA64B8CC8C75EC4739AD366BF2FEF2F5566B5BB6439887D26C330F9DD84BC9ECD50E44E164E6E75677591D3CB1E37F82F715D2BB8B62FCB212D65C2669369416F9972BB4B19B743585ECC65734A7D757B83B68E0D105FFAB30D7FE33D230D7F3A90639F8E6DBC202EB34EB36BC3633B083BBEE4F3G65A3B579328364E3FB44C2480F91E54EF5790FDDF72AB6CF4FF70DB6CF6BC24CD352AD5ABC2977F97AA4711E2BFB3FD33E72BE65A3D71407FA7E3FCE53BBDB9B1E1F8509B14A71A8FD
	2F0815180A2A752E4E9BFB3DBB5235FA568BE37D6E4A6B4EC731557797B7E86DFCF9EC6C7C573E2E553BFED7286F4595EAC5DE2FBCCB2BB739A2649D813FFAB3F234FAC5A5636B6FC39B357E7E62ED2D1D7BB3464E7F67DB34FA2B255178177A71DF7D0E46FF2BD9EB67079B46BE5E0736E9757EE16F586B5D172755F30D793E872F3BA8DF137B159DDABB0D6310BB6BDD2DDEF7F908FA7BBE702BD7D1205573250D3DDE64AE2D5E83A747575F475E537A3B76FD2D1D47760C1D7F60EE2D5E03E563531399A59A7F
	07CA35F6CA53472E27D3F47314D0BE76FAAF6B66694962710D1BF917A6F74DFBDABB11A16F2B78739FAC566A35F50DF35C5818735FCE4D291E830EFF544BD23247ADED90751B5881DD6C501E2A44976FCEB4C9C9CD52DAFEE059A4A56129E313144C8EA51B24C4456972618F85B1EFD4FD1A94A93D566641E3CF7E33BB50FA5648DED5CF7F79F3D1DD35B9A649848C86E45EE272B09ECB32B67130AA5C7325F4C9227FF08BC67FA8092109FEE5B005472FD78337F43F3178611D95AFADFA3E4D13A82DED125E42ED
	7E046CE6273B32957FD2D77D493DFAE99D7B89D4F49620239E76C876A8676CF2331453F79CFFCE47234856EABAB776A85246E664349BBF216FEE34F886589F87FC58ACA5B970478125AC1EED37B87AF773C3648FD94F922A3BD9FF585E8272E6B3F93115E251CA76239BF6ECBB3CFCEE22E42A17DE33BBAD56FACB2F62F4B7C96B1481376C99F05A2DBE574787D9C3296CAF0BD8A4824DED925D474E966317C1A174D95F9D535263526BCDF8E1D95A4DCA22D7D3ACCAD8EB86ACEEE0A53B1B947C31C327BBDF64DD
	750E32732906B7BBCDD2025AC99C7A57FCAE058F1850A36CF2F1BDC82D7C6B4BCF5C750B7DAB6221CA3D144CFE7C8298E7EA25789552A9D137EED9BFB376FA144B054343A6B5C3115676D986ED76F37042B51A70F50A5B6270600F00GD3F3C00FA9CB88DEFC4B93A5375E774753CCF02D2AA2ED65D384D5F7774B4E415D55FDFDB607AC9A0079026637086631CCF1C019AC5FCD9B8FBF332687742BD6CA9CACA9E17F1632FF4B705FD20614B224348CA6ED0594723F40FC1F8F7B7C26CF2C85133A9610927C347F
	12A2E5785F7F93593819FBC593093A6514E8D046BF15BAC2E991CD9B29FD88E7324CACAC0EAF9B4554E1AA17E53E4D7E5DB23101A8648311F8999CFEA4A16FB490720C1F412041127C4A9441143E0BB7E1ECBD03455E2141E2CB0F42FE8519AB0E5D4296A0DF30EAA389A6A97934145D07D72F0F48C3321BCA020DF81D2147E57E876D6C363374269FF6486B305A5975EC36DC1A25CC3F6CCF64F3AA1E6D07F38954954644F041C21302743E856805913D177CFA4F7ED324754CAC1BD120AAC54A2C76F8EC7D8E59
	EA66574891AAB869B86BF08EBBD428906A1CF03A95B33B3582855671961AEA711D8A970A16F1907CF20484F81B2140A075355230DA7F25C6FF51139C22975F70CB793EFEFDBA2275A9D5572704B1481D90C46E2410F28B834EBF9472E25844DF29C3EDDA07B01EC4E3062E10697F172A75E83DC852CDCBE208DE1CB2C8FD2D7AE4B7DEBE09E310FBE22A0629F67C6754DF29870A560384C953AD7F9E783E6ECB7E22568BC91D07FC69CAAE793F7ABCCC9E697360BCA4703FBBA83C2C7F3B03D51337847DFB168B33
	78DD10993382FF57AD1C7DBF8D96819EB70B6E944654E1C09B6CBE6009C10B5D2ED86CE72B9DD63C9BF8681CA2EBF7432F2D4776BDD4C64DD4FF93077DC64C973ABA7F8BD0CB878829BC96A9A2A0GGA0E3GGD0CB818294G94G88G88GF8F6A2AE29BC96A9A2A0GGA0E3GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGDCA0GGGG
**end of data**/
}

}
