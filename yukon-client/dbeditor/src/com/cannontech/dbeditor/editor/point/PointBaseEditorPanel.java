package com.cannontech.dbeditor.editor.point;

/**
 * This type was created in VisualAge.
 */

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.DataInputPanelEvent;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.point.AccumulatorPoint;
import com.cannontech.database.data.point.AnalogPoint;
import com.cannontech.database.data.point.CalculatedPoint;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.StatusPoint;

public class PointBaseEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.cannontech.common.gui.util.DataInputPanelListener, java.awt.event.ActionListener, javax.swing.event.CaretListener {
	private javax.swing.JPanel ivjBasePanel = null;
	private javax.swing.JLabel ivjDeviceNameLabel = null;
	private javax.swing.JLabel ivjGroupLabel = null;
	private javax.swing.JLabel ivjNameLabel = null;
	private javax.swing.JTextField ivjPointNameTextField = null;
	private javax.swing.JLabel ivjPointNumberLabel = null;
	private javax.swing.JPanel ivjSpecificPanel = null;
	private javax.swing.JCheckBox ivjOutOfServiceCheckBox = null;
	private javax.swing.JComboBox ivjGroupComboBox = null;
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
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getGroupComboBox()) 
		connEtoC3(e);
	if (e.getSource() == getOutOfServiceCheckBox()) 
		connEtoC4(e);
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
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Identification");
			ivjBasePanel = new javax.swing.JPanel();
			ivjBasePanel.setName("BasePanel");
			ivjBasePanel.setBorder(ivjLocalBorder);
			ivjBasePanel.setLayout(new java.awt.GridBagLayout());
			ivjBasePanel.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
			ivjBasePanel.setPreferredSize(new java.awt.Dimension(350, 140));
			ivjBasePanel.setMinimumSize(new java.awt.Dimension(300, 140));

			java.awt.GridBagConstraints constraintsPointNumberLabel = new java.awt.GridBagConstraints();
			constraintsPointNumberLabel.gridx = 1; constraintsPointNumberLabel.gridy = 1;
			constraintsPointNumberLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPointNumberLabel.ipadx = 22;
			constraintsPointNumberLabel.insets = new java.awt.Insets(3, 10, 6, 1);
			getBasePanel().add(getPointNumberLabel(), constraintsPointNumberLabel);

			java.awt.GridBagConstraints constraintsNameLabel = new java.awt.GridBagConstraints();
			constraintsNameLabel.gridx = 1; constraintsNameLabel.gridy = 2;
			constraintsNameLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsNameLabel.ipadx = 71;
			constraintsNameLabel.insets = new java.awt.Insets(2, 10, 3, 1);
			getBasePanel().add(getNameLabel(), constraintsNameLabel);

			java.awt.GridBagConstraints constraintsDeviceNameLabel = new java.awt.GridBagConstraints();
			constraintsDeviceNameLabel.gridx = 1; constraintsDeviceNameLabel.gridy = 4;
			constraintsDeviceNameLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsDeviceNameLabel.ipadx = -9;
			constraintsDeviceNameLabel.ipady = 3;
			constraintsDeviceNameLabel.insets = new java.awt.Insets(4, 10, 2, 1);
			getBasePanel().add(getDeviceNameLabel(), constraintsDeviceNameLabel);

			java.awt.GridBagConstraints constraintsGroupLabel = new java.awt.GridBagConstraints();
			constraintsGroupLabel.gridx = 1; constraintsGroupLabel.gridy = 5;
			constraintsGroupLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsGroupLabel.ipadx = 71;
			constraintsGroupLabel.ipady = 2;
			constraintsGroupLabel.insets = new java.awt.Insets(5, 10, 9, 1);
			getBasePanel().add(getGroupLabel(), constraintsGroupLabel);

			java.awt.GridBagConstraints constraintsPointNameTextField = new java.awt.GridBagConstraints();
			constraintsPointNameTextField.gridx = 2; constraintsPointNameTextField.gridy = 2;
			constraintsPointNameTextField.gridwidth = 2;
			constraintsPointNameTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPointNameTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPointNameTextField.weightx = 1.0;
			constraintsPointNameTextField.ipadx = 192;
			constraintsPointNameTextField.insets = new java.awt.Insets(1, 1, 1, 9);
			getBasePanel().add(getPointNameTextField(), constraintsPointNameTextField);

			java.awt.GridBagConstraints constraintsGroupComboBox = new java.awt.GridBagConstraints();
			constraintsGroupComboBox.gridx = 2; constraintsGroupComboBox.gridy = 5;
			constraintsGroupComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsGroupComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsGroupComboBox.weightx = 1.0;
			constraintsGroupComboBox.ipadx = 65;
			constraintsGroupComboBox.insets = new java.awt.Insets(3, 1, 7, 13);
			getBasePanel().add(getGroupComboBox(), constraintsGroupComboBox);

			java.awt.GridBagConstraints constraintsPointNumberActualLabel = new java.awt.GridBagConstraints();
			constraintsPointNumberActualLabel.gridx = 2; constraintsPointNumberActualLabel.gridy = 1;
			constraintsPointNumberActualLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPointNumberActualLabel.ipadx = 46;
			constraintsPointNumberActualLabel.ipady = 1;
			constraintsPointNumberActualLabel.insets = new java.awt.Insets(3, 1, 5, 24);
			getBasePanel().add(getPointNumberActualLabel(), constraintsPointNumberActualLabel);

			java.awt.GridBagConstraints constraintsDeviceNameActualLabel = new java.awt.GridBagConstraints();
			constraintsDeviceNameActualLabel.gridx = 2; constraintsDeviceNameActualLabel.gridy = 4;
			constraintsDeviceNameActualLabel.gridwidth = 2;
			constraintsDeviceNameActualLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsDeviceNameActualLabel.ipadx = 168;
			constraintsDeviceNameActualLabel.ipady = 1;
			constraintsDeviceNameActualLabel.insets = new java.awt.Insets(6, 1, 2, 37);
			getBasePanel().add(getDeviceNameActualLabel(), constraintsDeviceNameActualLabel);

			java.awt.GridBagConstraints constraintsOutOfServiceCheckBox = new java.awt.GridBagConstraints();
			constraintsOutOfServiceCheckBox.gridx = 3; constraintsOutOfServiceCheckBox.gridy = 1;
			constraintsOutOfServiceCheckBox.anchor = java.awt.GridBagConstraints.EAST;
			constraintsOutOfServiceCheckBox.ipadx = -10;
			constraintsOutOfServiceCheckBox.insets = new java.awt.Insets(3, 13, 1, 9);
			getBasePanel().add(getOutOfServiceCheckBox(), constraintsOutOfServiceCheckBox);

			java.awt.GridBagConstraints constraintsPointTypeLabel = new java.awt.GridBagConstraints();
			constraintsPointTypeLabel.gridx = 1; constraintsPointTypeLabel.gridy = 3;
			constraintsPointTypeLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPointTypeLabel.ipadx = -9;
			constraintsPointTypeLabel.ipady = 3;
			constraintsPointTypeLabel.insets = new java.awt.Insets(2, 10, 3, 1);
			getBasePanel().add(getPointTypeLabel(), constraintsPointTypeLabel);

			java.awt.GridBagConstraints constraintsPointTypeActualLabel = new java.awt.GridBagConstraints();
			constraintsPointTypeActualLabel.gridx = 2; constraintsPointTypeActualLabel.gridy = 3;
			constraintsPointTypeActualLabel.gridwidth = 2;
			constraintsPointTypeActualLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPointTypeActualLabel.ipadx = 148;
			constraintsPointTypeActualLabel.ipady = 1;
			constraintsPointTypeActualLabel.insets = new java.awt.Insets(4, 1, 3, 37);
			getBasePanel().add(getPointTypeActualLabel(), constraintsPointTypeActualLabel);
			// user code begin {1}
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
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getGroupComboBox() {
	if (ivjGroupComboBox == null) {
		try {
			ivjGroupComboBox = new javax.swing.JComboBox();
			ivjGroupComboBox.setName("GroupComboBox");
			ivjGroupComboBox.setPreferredSize(new java.awt.Dimension(130, 22));
			ivjGroupComboBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjGroupComboBox.setMinimumSize(new java.awt.Dimension(126, 22));
			ivjGroupComboBox.setEditable(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGroupComboBox;
}
/**
 * Return the GroupLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getGroupLabel() {
	if (ivjGroupLabel == null) {
		try {
			ivjGroupLabel = new javax.swing.JLabel();
			ivjGroupLabel.setName("GroupLabel");
			ivjGroupLabel.setText("Sort Label:");
			ivjGroupLabel.setMaximumSize(new java.awt.Dimension(40, 16));
			ivjGroupLabel.setPreferredSize(new java.awt.Dimension(40, 16));
			ivjGroupLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjGroupLabel.setMinimumSize(new java.awt.Dimension(40, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGroupLabel;
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
			ivjOutOfServiceCheckBox.setMargin(new java.awt.Insets(2, 2, 2, 2));
			ivjOutOfServiceCheckBox.setMinimumSize(new java.awt.Dimension(117, 26));
			ivjOutOfServiceCheckBox.setText("Disable Point");
			ivjOutOfServiceCheckBox.setMaximumSize(new java.awt.Dimension(117, 26));
			ivjOutOfServiceCheckBox.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjOutOfServiceCheckBox.setBorderPainted(false);
			ivjOutOfServiceCheckBox.setPreferredSize(new java.awt.Dimension(117, 26));
			ivjOutOfServiceCheckBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjOutOfServiceCheckBox.setContentAreaFilled(true);
			ivjOutOfServiceCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
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
			ivjPointNumberActualLabel.setText("(Unknown Number)");
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
			ivjPointTypeActualLabel.setText("(Unknown PointType)");
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
			ivjPointTypeLabel.setMaximumSize(new java.awt.Dimension(150, 16));
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
public Object getValue(Object val) {
	//Consider commonObject as a com.cannontech.database.data.point.PointBase
	PointBase point = (PointBase) val;

	String name = getPointNameTextField().getText();
	String group = (String) getGroupComboBox().getSelectedItem();

	point.getPoint().setPointName( name );
	point.getPoint().setLogicalGroup( group );
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
	getPointNameTextField().addCaretListener(this);
	getGroupComboBox().addActionListener(this);
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
		constraintsBasePanel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsBasePanel.anchor = java.awt.GridBagConstraints.NORTH;
		constraintsBasePanel.weightx = 1.0;
		constraintsBasePanel.weighty = 1.0;
		constraintsBasePanel.ipadx = 156;
		constraintsBasePanel.ipady = 15;
		constraintsBasePanel.insets = new java.awt.Insets(0, 0, 0, 2);
		add(getBasePanel(), constraintsBasePanel);

		java.awt.GridBagConstraints constraintsSpecificPanel = new java.awt.GridBagConstraints();
		constraintsSpecificPanel.gridx = 1; constraintsSpecificPanel.gridy = 2;
		constraintsSpecificPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsSpecificPanel.weightx = 2.0;
		constraintsSpecificPanel.weighty = 2.0;
		constraintsSpecificPanel.ipadx = 456;
		constraintsSpecificPanel.ipady = 170;
		constraintsSpecificPanel.insets = new java.awt.Insets(1, 2, 4, 0);
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
 * @param event DataInputPanelEvent
 */
public void inputUpdate(DataInputPanelEvent event) {
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
public void setValue(Object val) {
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
	
	if( group != null )
		CtiUtilities.setSelectedInComboBox( getGroupComboBox(), group );
	
	//Load the device
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	com.cannontech.database.data.lite.LiteYukonPAObject liteDevice = null;
	synchronized(cache)
	{
		java.util.List devices = cache.getAllYukonPAObjects();
		for(int i=0;i<devices.size();i++)
		{
			if( ((com.cannontech.database.data.lite.LiteYukonPAObject)devices.get(i)).getYukonID() == pointDeviceID )
			{
				liteDevice = (com.cannontech.database.data.lite.LiteYukonPAObject)devices.get(i);
				break;
			}
		}
	}

	getDeviceNameActualLabel().setText( liteDevice.getPaoName() );
	getPointTypeActualLabel().setText( point.getPoint().getPointType() );
	
	//Depending on type change the specific panel to suite
	
	DataInputPanel sp = null;
	
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
	D0CB838494G88G88G9DF954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155DC8FF8D445358F020A0A02A58AC5AC91820DEDC450784AEB69AB8D3646E75A5256BFD8795A3426166F95DFE31B7A506263E6739785C492B4450011BFCA5A98928890B0C2C292C8B0C08A91037C4BA6FB13DD586CAE3BF77F84023EF3E66E19FBF7F3F713500F7759FC5F495E794D1CB9674C1C19B9B3F7F62502F7E232B3CC85121411AD29FF3BB2A4A9762CA4655C795CF9422264449CC97777AE509B
	52DB4B3360F38C64B54FCD4CD9A5E59D4803F4AC24139FCA4CF98B1E7325BEE7D18BBC041113FED4122A5EDC74CEE4B9CBCF121CDC165E36F7B9FC2E840A838CBF53E514FFE8EF210AA72BB8D106C91266F7E199F9EF110AD5C13A85A89AA82E0B171F831F4B18646B77D42965CE5F9EA3257F677B2695D80F2ECE2262EE4EDB2FFE96CB77E7AC6AC6D937B1BD11278F5289C0E8FC26F47C531D70D95DF221FBEF2733272C536CF0B89D0A5CE9AD13ADB64569AEF3B9ED8EE54F1E967C58EF76488D8CEEB1BBE43B
	A4D92DD87713FB73D8DD12D48E69F4063D68C5F971707CB0508201FF5707F8158D6C85325A48460F5E2FD6EDE83BFC3F147014A37375B0B6C65938EDF1B64D46E86F4B39FFE57D316F9436FBAA64651DCF4CA9822A81EA870A82FA1B7941014F7E861F359DCA1D3327476928F539765BAD6E43CE0BEC0767BDFBA0C7450E491DCE37C512F8DFC76FADB550A792F13E49C36663ED92371D7ABD7E58B4E941039F1BF28D5AE4C1DC39E97CF95E96B34EEBED42E5BF93D6763C7341FE99AC7BD3C9AFBB7686E7E63601
	6C04A7CACC29EABD0BF53237DC403EBE4F7A3A901EB3D47F603842F03B8E67BE3A04798C267BA0AF658279C65BCA5225B9EB02342A2259309D46A7561A6A6DDC075376D0DDAA7CD877C8BB1719ABF4790861B1BA1C6BB217690869D910D7541318437D2B718C0E59A8C8CF84CAGCA86DA84146700F28E7231511FED9D240F35185D4AFE2F4DEE115D9267EDBC75A6FC4A8145EDEEF2F8943323D3BEE1F65B4C9DF659A329E57EF18AF56B8862BB41FC3F93E49C149D325B2C581C8E285D5CA3AB325B434B1CBB75
	8616F14BAC375556A303009E17C475F61F4A473EB1FB14CFDC9633A2EBA5F83E739476094FFC82EA048A6019370B77AC36D7B5585F8AE4916D70DF2C3E0FE5B74C97FB76B4BB1D973C2ED634C4CAF0E0BB7F114D9D81F89E6320B63EF88E3145186F24FA5EBD3DDAD7CF3D2D9355B73BFBE90C39FCB8AFEFF2124F7CF79B79CC7D0A29D26993078CFD0665BBD51FF1060E1DCB2CCE540B27CD3E3688E3F61353F8BC8DED2EF8BB220C4CFF227E15222FFE550EFDB582740D811AA35A787469E26E33B84F13179EF5
	030349962D45391E3B7C341EE42B3AF33C0E617321CE4915183345C5ED77A8330B5A1FF87E99DB7A375DEF180C4CEBA403375131B338AE49BE5921544BAE59E101870F1AF7728C7A1EBAEE3B1C02679C43DDB04E6BE0E0F984AE1F4131E8D694372D432B4847E5C531B93A357C9EA64B636DF0E9D38449D24E20FDCEBF0CF3D13E778C0E11CE2743A1334E92ACEA7EE5B6C6E53B5C83CA57B93D8E0563434E221FDF10FB55B9627A339878784CF6AF4EB7BCCE18E1FE8DEDF05BFA4085F04A1292DCB8FE176DAB54
	35D18BE0986F0C044F09AE9A0B7E0DB496B7DD3AC71AF3EE3349E8AD99BF355E34444547502A0B5AD864BA794E996B5E47668A9577F0DD67DCA9C0DD8CA2966E7F059DD8D7B9482887BAFD117CFF71B94C27B9C6F57CBA275D4E1BD33473EF5BD7E13F48973DB2CC4DB819FDAC5AEED17B6A103C93EABB727C74F6ECC73BEE9E4CF3E3BB7A7C948BAA204FA820780B3CAD972AED414B6D8ED039D6C85B01C6390D4A3DA24AA5C3BA95A84B93DC0E37D56EB90C8F0C623FEE2F2D4C2B58F036B5AB6050AE2F425A10
	47FC1EFDF9EA9D759E0A6DA66D47F66D03F4B4D03C077A7F27ABA8360B3E90A325CCD99BB6FEFD5843F5DB68517A3FF8B845F69D1DA8F30B1218D325D01FB94D7A75255663F1F65A58522636777A8E3DFFF0ECE3472B7059E5F3FB14A69840816A1B128E549757BE95D6E301C9ECCEC3DB1BC16E8820A2AF62F9BB8AF43A4F86BC95A88F72B2D9FDB9810C81BA4DEED9A9E37F79123A653C59E78E14F97CB0584B58CCD2D607398D78D822764B43A52B390EB2EBF91533648445E71373EB28FF27CF102A3F48B799
	454BA3DFDFE732F8F9DB0E72EAED5A77C0CE662233FF496576F584483E79E8170F6C4BD17476B57A38FDAE9F5977E8E09B4EDFECF014710F96595D65F4774896E6E419592F28965632EC55C435DD5F6BA539A3204E59C26EDC2FDEEE3AAA3748CFF2EF6D3DDA39FC1D886C8B0D094B7D5A5ADD6F6F3F0E73D8B13F8E75ED7373D8B1DB606B99EE57615C3E297B493EA848CB75D3FC730285674C46G44C9C0FDC051C089C00F87482FE7F6EC1786089F1BE01C3A9DE67B712085026238CEECAB1853557405CE5403
	4D69923543D4FFE8BB248502EDDF5E9B1C66FC8F7663AB8BED4F90BE6E531B379451DC239B82FB769CEA05B57300CD36DB781AF222CE3F26B4767258508E1FD1C0FC7E59378347B50E05713DB44FDCB9201FE7E63F3E4D74DA18F9E61E2ADFDA2FB6A6CA459E722EAE54337CD2E2CEB31085A88AA896E88ED06AA56A23BFF7FEB4D09FB147EBF556815688F3FEDD685AAC5A549A383A3E7075638B6D0B2C4BC6FD51EDFF5B38AF40168E67FEE7C05D5BFF24E0BBE3BB96DD26D82E34776AF45D530FAFD457D6C3DD
	771E88272BD56E3C20697A3297F5DD07E3916A617B0B51EC1D5A521718D35DC7183D9631F6C8877A48263F56A1B6914B88EC9343F06E19A730EF6E0F941F07925697A9BF214F281D9E9E120C01768459A1E96E3BAFDB4247319932F19C1BAB9B4731AB64B7B88EF568F733143FC60E9C672E130D625CF54C36C75FFC1D556D71489EB618B098D173FE272C118C625DA33061D55BA5DFDA60DF2616E9D01CF59542277ED4D2E86CD33A9867B6567F982B2477D10C3379B0DB832E40BA88B4728A79C57D8E726D18AB
	7D7BBC8130A49D3E608A2D859B2C385625C1FA796709B91B002A01CEFFCEF34C0372006BC0C3A042BAB035ABFCBF47F799777383DDE8334769F5F74A21FD796FDD05A2CF1B4D1A9439871B1D17C9E47C0A595DAD777305C799BF4F8B477F332EF07E427BF4D2C05F275B4538592FD09F74C17B0D79BC389FE2A1BD8BA8D907737ABAFB7575D50A7A16FAE78CE27C25C0FD691F9B0F733CAFB8DE841F65C075DF041F8F5A54BCFBC819A8C8C78345814DD673660514C97DA25426E97BF08FE7B777BA3D0AA4D92258
	592B582BAD06722BG2B82EA0E626BAB9F97B37DB857F7394DBD506447DDF227D4B38CF9075792EFBC141F831482A4C1DE71B00A4B78FA0CDBAE75F8D8A29C7D81AB33F559BAA90F2F756D15C6EB7DC828F362B09ADBE36B7473E18260C943F4E3E918F11B269960D957A56694DE970C73986EE9B62ECB2F63B15CDB82FF1E61969D4EE33B3FB39C5393A12FEDB82DAD27D8DCD2B0827AFB84E1CFFAE83D299758CD5B900B06F43C40FE444A4D99416786BE967EE68DBFFEAB2C4663F71755F81E7E489AF91EEE30
	069BF7DC3F138A5976385079EB9508AD0474E201352FA1FB17DFCF5888E16FE901A5B2FBDD109EA130DDBE4446E3DAE0BFE1D892249789EC239FB16986703F9B88FB258F31CAC8B78A6C64EE125BAA30B917C8BF2B402633F233A01DA4307CBD08BD8E69C5823B1F71CAB7025C9B894B3C4C645E48FD04E36FDE2636EA9165EE2A223632886C0C091DFFC2FA3C406EE5D8BC241301383F7DF69F1D3725GD6B81246EBC5FBD0CC0DBB1FBDFB8E3AED167D666EE6B67A79DA76C1CD1E4A5FB2125632D3B56CCC9C52
	B1C033C6D29C9DF80F626878E9932466280F8D776BA92D5BCC73C62A6F3CC6EAF1F49C4451DC663D35A4337CA6E86B1B90FB2AD63F374379E11C094701BCFF59FEFDBE7AA36E21357C8F0372D15FBBB27579BDC17958C6A5AABF376D314DE45B124762A47BC33B431EC54C38095B74387A59F2E2233469663F28360D2F4CD375DBFEB335E79DFBFF610274C820181B29BF9FE2F144ACC8A78B4CEF2638B4CDE0AFF4103F955CC2589AB16636882C679C79EF3540562FA3BF9FF58B4D1B3FBA0C6BE18C24E783BD8E
	148614B78A6A822A8732820D820A87CA9AC5FCFFE2FC8BA11DF7AB1485EA3C151FC961F913951EC75E4A4F146260B369D6622BB8CC6BE53D2E5EC8EBA76AE5044768F02B2A9B695FEF0D8DA3E34BG3209386E6DC2F71CEFF14DDAB3GDFE8DC2D77E93D7F3E254AD8E6D07635B0F52C98045E5ADADF2AE0F917DB6E125DEE59F25CF689635F625B583C4B56230550C769C06399763C27FEG3B0830FE8B708400B62A58A8F89E1641478C173FE39D5A8181032D475B4324F3598F30B9BF851E975DC6B1C22A875B
	2253E97776B8BCEA396B180F970E0678F7B4157BF19B1EC9F7B99D8AAF7388DBE7DAA07F74E832433FE6A0BBDED3F5C49AB5BA580E98C88F17389DDC46FDCC5FF840E789BD1EE6EB943E8B4743DADEAE4DC36F3BD3C539A3BE6C83FE9805213A59E19175F63375AB9D4ACAE3287CF3BEFC47E7F53AED1740C6339D0FC0DA1C9E9BBFE866FC99FEBAE3AD92FCFB7C68D3BE592D58BA4D76DA3B2D5B01A71BC256AF7AE84EED94BC85D7B0966C01185B86F58746603D065FE138C06D4F563E5900A70D2136FD6AD224
	364D5535ABD2C0751137D55969D04F73CB17AE55FC64873B31BF9530124BFF7522A47272BA197F1883A17ED34D56DF69F608A3EEA7DBFEF829C0E73F3E55B8CF4AA5323FCA702C3D0CFD536144837D96334D2148DA5F9CE26B34954AAAC02A5F32F5BA865271228E45445E1B625938C329F54B6683B63BDDD74FEDA672098502E70EA9BF280F355ED27B4AC4E7D80BEE27B974777D4EA687B7FF3DF415FC5BEA064A4763628FEA593B7D2F7018A7C360BB993EC907737845DCC371CBB964C5FF05763F4FDF4478FF
	B1248B46A66614825583D9002246529E7899EB39B4409E385569EA06A0592E8BEB39FD53EB2F6E6E4577F3A9CEC87876E4088BDF4A88F71698BD162FB3896A6742560D8C6776C5F958FD94481322E1FE832A86EA8132C613FD9BED255CBEFCD3D52BA866CEABFBDBC1461D3049FE8D46FD94376D6ECA5C9775C5EB6B5C98F80ED55333220D573FE440D3C27252A11DF707513E68DBAC26A93AA3341F67B0FC4B9D217D5C6425FE2E043C71F750D8C82E4B91E31076234F0AF35EA5D02E60CE580B82355F497560E5
	E24CD4A6F0A75F330E014F58BB79BB81BE0E0A59FB215900A55DC9D883DBD332464173B81A53C61C0BB427E506D0B570350C63F747F08E364073EEB08CAF08F19907D94C65823CEF1C368746F7036C5D6391EFCF076CEEB6F748F67D7E984FB998A8E97BE30CB96AE51F2DD356F2A98F5FD59DF4BB3DAE15097055C28E3008937460BDB76393A73A14774B4BEF866BD85B29F84D76207A4BCF2D8952A928081001F371D12FF2346B386C4612F4A04B73FF2E2CA4B92D3DAEB9287E6B0F956873F45573F7C0E5C774
	F1FC5CB85E5F4963689D51F12FD1FE0A485F315DA8BFDD647F074FA8BFEFBC652FD30C720BC47EA3067965A2FFE526D1FE3D480FE9B24AEF03FC3EBEDE70CA221D9F73204FB9E4FF31F839D5EF0329GAF60EC29583E2D3438E2F709BAEF457875F7852BDE5DE5782E9D49BE1E0F33A875F3760D9B19EFE33F32F5177D256D6B0FD18C7104CF12E83E7C75F21AAF0B3EF917D47F54CB999B43481D254A4B7B2A36E7DCF8FEC9AE2FF3DFC033BBF1D0F6F33E0EDE0DEFECC5C43E725262ED5B086F134FB53E56B59B
	C2798E40423DE50345E636BB3BB7149683EBC5E509E458CE3C3E4097DA7B24C8DA5D5F0AE84B361DDCA7DEB6D7E131125BAB73FAB2CDDABDC9BE1CDFB64020ABF6F6952B43EE834FAB74238C7D5B6563783F4E49AE11A91E326606832DEABDAF743D9B39ECDD43115606E322FFFFF2CA6FD72BA63CEB22F9BF34FFEB54FEED56752F6A53191A9D8FDD0A549E953B20BDF6D29F0D4D515A745FF235BAE287D587AF7BBF1786EBAF1773F41EA667732A416B6AFDCD637B07E9307E444BCFB5D13F2BFDE4AA0E2C7331
	26030D2DBC9658DE0BF279F3356E393BE6A86F1206CADCC654B51541E32497AF06BF87FD652271B9E8265B789CF419BB727B28B75C616FDD953A75772EF80C57309C6772A8707709C0F3009E87DA78D50A6F2639518F7046E77F582E4152ADD307127C1BBC61ED9D639978EE68D7BD98B3F4B8954559037593BE096D3D95270B019C0B75E09B39ED5DD60597D571EF30BD2EDD6EE2B0BFE37A9EBB734A1AGEB5884BAF75A9620F32CAD820B6FC52C995296013D58CBE7DB8101DD2C24332DD1827BB1FB079B8369
	D982AB7451F9D732406E2B24732E223BE86F74DD2F6677BF54BD9F75DE7BB1E03D26B2F8EC7D7DFA3A9FDD8EB6DB6F22B3605F2D43B34E48F738DAED0ADD5E2F065F6A7B72E58DF43E90F3971DE1BE496E064E07F49AD05EC4BAFFCEB2D3DCDF8CD85BC43A07312A03765BE3A6D29DFB597D3AA4C827GAD1EC8B1CD8A06246ADC172314C9E16754ED95153BAA2B366F2C4429D5BC17505C4C3E9A205653F12EB8FCBD5BF73089363CE4F0F18D2D619538442A32DA5F50E4659D0EA42BDC15253EDF6E8A73FE1945
	383C6D64A374FEF9555D541EB7FB89D384E65FCE58FC017D59C7D8755788EBE5FB9E744798017D2F40668B2CA1D3481DC458D3CDD4CE19C4FB730C556CBC8C52C913E8AE1B617DBBFC1EB01FD7F7617CF2A51B4DGECF1BBDD32DB6965A83B79284E119F65635EEBA154139583F385D08DD0AB109DA88AA89AA88EE88E50FC20C5C052BD30AF845A84D48554FC8F693156DF92C18F7585881BD85DF862A351DCBD8527EAF50EBBE85A9C2E8E18198FC1379D6DB8AFF7AA2D66EE41F3F8F9783939F57940F3731965
	7CBC31DB46ED8F47BA16E300636C6A7248E28D68DE3ECAE0CD9EBA6362FE78E4A586990A53E9EF3539F067277A6AC6942F6E1BCF85E85F5C8EED96G0A81CA38075FE561754C6D45F542CC27D2A7E9DF7B8C1B2B1321EC4ABD341FFE071D2B25C1FA0940A630B938EAB26CEBA6D31D3F75865559C6F53E427C5A8565C6CC267D78D65FC07BF1BA03C60A863EB820B5EAFAB6BC5F8E8F1FE4100CA719BEC900BFBE195604C76A87BA7FD9B259688E4E9383722D8FC9F33E158D48D7B8E5F06F9ECA219CBFCFB94466
	571AA92167AC47995EBCA5741CE5C1BB4D47ED10B7FB8A597BE373551D83AE38CA3EB59DD7F7FE78GDBA30AE3C36D7D8E43CBE3C36D2D6FA0FB2BA0EFE2AC59FB7E08513AABCD656DDFB0B54C3BA1402BC372DAA0ED19EA648B238D577600DAF6D49899B100A70464A5C1FA01210CDDBB0DE42429E53326994BA8843CB4A42F86522D530CE43CEC785D922BDA36AF0C0CB1004706644D02F432210C130A5E9FD254B2696AE75E577B4B60FC19DEBDDF11DA2EDC7D2C8F4BF7A8534826B6353C5D008FA98A706810
	3CB8C84F7E3A11CD8B1A743A4DD34B242A1F0B8DE470B9BCE7B5BE0BFD86DF8E694E5CEADC08EC8E1856E1323C442E2C9F07020EEE757BDD79615719367C015799F33E719E20BB3F7F9E401EEF3C87F0676B77G9C7367631C5C8D6BEB1BA857A73013829B466AC46CD31BC5318A7CC62697620DB28AA47CD626171FE1EDAA76959DF6D245466563DA89A6DB1DEE621D184F5E955A6CF61E0EE1E5EC8ED8BAC5FBCCE5BA5ADCE61D5E5F90986A4D671961811AE71647413A96C7B176F3E5C1F7D079BD8B9ECF73EB
	96EACC7DD80062613EB80A07DFE2F5A6C0BA99A89528FF6C19320F2F1FFF6CC5DB4A4BF69C60EB7A91BCFE26D83732CC3A5A18193DE6D16B196B0DF01E31ED5B0E52622A729203B3AE523140C7BA9A1C8D5698E1DC07C99802297A306AD5FD5E7796055787226A7292FE5FF3CE7E8A562FF61BC32E635FF214683E4DE63617D982EDDBF0AF6D914F36333DA9242B6F25752245AC09F1FDD2777C1B76EB3FC77C4B7F030CD99D1ACD5F57BD9F6A387632BB2F298C5E2723581E29917A33CD74738BC768EC41A23017
	B368ECA1A030EF35517D2BD1820BAB2433059801DD5EC1E78BE9821B69A539D95FA0EC350F642E92583737135CCD821B66A539D582DB748949ED9658B09F491DA83013BBC96E8CCD46CE12BBDBE07779C96EBCCD3F9DA4B7D553C5583BD8E0A3053D0D5FA42C48CFF2CF8B6C0A1F64DA85763A386F58A730E9BE12BBC6E04F1DA0393182FBDE581BAEB0975B2F6645A766944693B60A75DBA9242B8576C29B5557A83035DE629DAF306F30F271101EAD309B5693EF12406CECCFDCF89F4463779176D306D5C23AD9
	E0B5550C9752FD82ABE06B6498C847886C3EBA1211A030C75C6C7CECFAE2CE51F4427E44765D65102E915858350835C25AA23012B3D41FEBBA4DE5B7757B6E47604669382B641BB7E4BE9E5377306FF94D199E9A6B7B981EBCBDB456DFAA56607910D7FDBF6DAD2B9BB026D070A48B4F51A55A77353239BF864A450B325E76ECAAAB511AD3D68C0F6A1AB365C870FD9499DF8F28752DBD0A75AD003A965ECFFB5D0D6C2EC6418CC84FA0AC1579D6B3245BE6501E76FB05C3F947CC77E668DD59C8286783C9FFA7A9
	160FCD40E3E650B95A488D1A2E9F32B1148CF97305DE4F32B9A8EBA6BC4FA46CA7ECECD4C33AF1A6691AF9E2A87B6FD0DD83D04F68A0DD9FE23A0CGFC4CCC52F559A6CD57E7593CB59B721204DECF32B9A59D52528304FD48ECAE05F45583246BF787EC57E591F435C2BDFFD83AF4E92E50E5A61321G9E75G693AEB33266BB426D7BC644D92FAD5B11ED4C827890C5FCDAEFA90660D87C957D786EC5757A2685A76A0EF57EC214B1BCC978B602E87493FEFD4026F475D4D664D09109FA74ACCE98B76714F5EC3
	7B380E716B863ABB9749CF67030CB3EA9E17DF477CAB9570B4A17FE7A1722F3C2F493FFB7DC0721769748815DF1C1018730F8CCD3FFCE9F8E6302C18AD1248DB18E9FC3FCC4AE2FE86F5D5A5104E1FD6073FCB69E2F33F854A3AC4793F678777C3ADDB8BA6C2FEDC82794343F58379037EBEDD3F36CE50FCD73D3F61B63EEF79A2DBDFD221FC1A506F55C3417A959FE27E7A90786BC3D466195341EDB737C46B271AB38375D3EE04FEEA8399A5991A0DEFC89E761E1EBF77691E33FB2FFDFC3A7E1A4A60EDB723D4
	733BE36D3828983C7F2D054D3AFDC3E9F1057A7AD7657D79879A6F9FB2560772EAFB222A5D4038D36C6DDEAC5378625B864FF726C2631BD8B858F7551CB78A3F022DBE476D526A59699D3C7C472AB43E7B37C7602BAC8F62FBE93746F7D7A4F9A1FC3F2F5678D2BE991A3DAB77EA76D66C536AA99C025E2DB59A5FE4FF24BB85BBD4C64E672F5578F2F7C43CC792447773BA0DEF42907BB723C133F753812D1EFBFC0317FF7020461735E3706DF43ED1573F3E41737D741046770B93C333B77DE34D5E830735FAFE
	DFB9F879130FEAFCD1ED83492F88127F0396CD7E3B47F563E208759CED556AF1FF22553323FA707EB661D30DEFD97E607B7B4F324ECF6A06567E055D1A5E9FDA35FA06B78DDE7E38739A5F0DEB0756EE5F3E003C7CF782FBF3718B95714FCB599E37743A60EFE2870AA93E6DFBB21F1D12E912321A249CFE405AA4E561A9E913144D8ED11B24CC45698A600FEA197424C8AB6AED9E76BD82F67191EADF5D5F2A5A5B1EFEAA7EDCFD01C9B201G2B4CEB4C7663B1AA2B938F57C6F8AECA6C661AC4DBA78C17B2981A
	99D486D3F8DCFC9DC8DB915496BF38E747F3493FBC614914F21A243FE208F0DCF63353E859025F32D6FF626B00340C7D86A0869AE02807FDE4FB14DE3B5CAC95F49D431F3A71A83225164E39BD0A342699A5ED452B87DB9B4D9EAB7B7244074DD21683FF0CCCDA4D336DE6C777D6FE687DA133AC23361D59044B29847A66B3FD3116B2D1CB7EF7DE5F7C6E2154D919126900742A5DE9369CB0F7AACEF71334CC313AE50F55E93784DC7B58D4AB65314819C55BD05D5B42FCB4368C5F1C8A25AF846E9E35E2610A83
	50D18137341515440828CC143054D94DEE90A53B1B947CF1B527FB13489B36DEF96FC7875FBAED12B2D4A33169DF8D3894A9274B5CE33377428377BC56A9AB955056EDF6F870C7CBC03362904DA8CB88A89B7BE8799D773E7C99932031AA52BB3CA901F5EB376C6C59DA5B55E5F3482282E8D728FE1D289E4B140514D99D38FD4D21A716F6009F54CB19BD65656CFF857B3F8D7FD7B02402A1955B20F1532408FF2179813219B90F98D3587DE587208715DFBC7F6864E72A539601F58724EC76A3A550892B34D27C
	1A728D71F72CFBADBA65966CA3973ACAG6BDD9D90E301FE62ED856BF03D77FF2EC8AB036FCC0A6C35C05E206134C29DCE1986E502D3F97D1C9651657D3D8F61D54287B0154F0A2EE17F8B075C224835167196090658429BA2738B58ACA089FDC70112E7050574E7927AD219D2681A777186112D23274C6B6BA9B3F7A86CE7C039C35A4DECG72812BD612E11232BF13723B706A799159A73B29A44C91EFB074184CFFAC005DG175E8A4276FB9D96BB3BDE4E06CB3334AAA87BE339D7457303F02E01BA42189AAE
	98E8D2986BF1E17D3530BE0BAC177453A57B8397E94257EC78A5F576BE8E86A91B270B1B6A77A852EB7C993505542ADA0F47566D10AD457C0EBC96B86EF4AB456CFE8F9658720963024369F794F367AD51C31C27C4D40A0D83BC7922003898DFE268E799E17CAC0B7C2CBEE82E49616F50383FA04F35F1407D815120F8F96524C16ACBB5E08EEBB3873E114297136C8BEB912C58E2107AD2597429E6D3264EA6D384AB6E8CCA853EFCB619B51BCCBA1B328C747E1769278B1ACD4BF4B6E58FC26F81873F26F79D7E
	EB38B696F8B48B68ABA6780EB9734B2C7325E0CFC21D714CB767DFEBBC8C9F4C98B7567BCB528F3784DBC07D10453FEBD272057E3BA60B2FAF8C7B9D26B1F3795D1B58395AF7CD78BD4F15AB50349440174C25BB23E7176381DB387B1E47FB4CF63BE236DF28F5D8706E677EDEC5563E93103F826B77D099B5D33D0B3A8275BE2F6379BFD0CB8788884B173F529EGG4CDEGGD0CB818294G94G88G88G9DF954AC884B173F529EGG4CDEGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5
	F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG8C9FGGGG
**end of data**/
}
}
