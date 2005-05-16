package com.cannontech.dbeditor.editor.point;

/**
 * This type was created in VisualAge.
 */

import com.cannontech.common.util.CtiUtilities;

public class CalcBasePanel extends com.cannontech.common.gui.util.DataInputPanel implements com.klg.jclass.util.value.JCValueListener, java.awt.event.ActionListener {
	private javax.swing.JComboBox ivjArchiveIntervalComboBox = null;
	private javax.swing.JLabel ivjArchiveIntervalLabel = null;
	private javax.swing.JComboBox ivjArchiveTypeComboBox = null;
	private javax.swing.JLabel ivjArchiveTypeLabel = null;
	private javax.swing.JComboBox ivjPeriodicRateComboBox = null;
	private javax.swing.JLabel ivjPeriodicRateLabel = null;
	private javax.swing.JComboBox ivjUpdateTypeComboBox = null;
	private javax.swing.JLabel ivjUpdateTypeLabel = null;
	private javax.swing.JComboBox ivjUnitOfMeasureComboBox = null;
	private javax.swing.JLabel ivjUnitOfMeasureLabel = null;
	private com.klg.jclass.field.JCSpinField ivjDecimalPlacesSpinner = null;
	private javax.swing.JLabel ivjJLabelDecimalPositons = null;
	private javax.swing.JPanel ivjJPanelArchive = null;
	private javax.swing.JPanel ivjJPanelHolder = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public CalcBasePanel() {
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
	if (e.getSource() == getPeriodicRateComboBox()) 
		connEtoC2(e);
	if (e.getSource() == getArchiveTypeComboBox()) 
		connEtoC3(e);
	if (e.getSource() == getArchiveIntervalComboBox()) 
		connEtoC5(e);
	if (e.getSource() == getUpdateTypeComboBox()) 
		connEtoC1(e);
	if (e.getSource() == getUnitOfMeasureComboBox()) 
		connEtoC4(e);
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
public void archiveTypeComboBox_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	String val = getArchiveTypeComboBox().getSelectedItem().toString();

	getArchiveIntervalLabel().setEnabled(
		"On Timer".equalsIgnoreCase(val) );
		
	getArchiveIntervalComboBox().setEnabled(
		"On Timer".equalsIgnoreCase(val) );
	getArchiveIntervalComboBox().setSelectedItem("5 minute");

	fireInputUpdate();

	return;
}
/**
 * connEtoC1:  (UpdateTypeComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> CalcBasePanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateTypeComboBox_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (PeriodicRateComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> CalcBasePanel.fireInputUpdate()V)
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
 * connEtoC3:  (ArchiveTypeComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> CalcBasePanel.archiveTypeComboBox_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.archiveTypeComboBox_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (UnitOfMeasureComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> CalcBasePanel.fireInputUpdate()V)
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
 * connEtoC5:  (ArchiveIntervalComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> CalcBasePanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
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
 * Return the ArchiveIntervalComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getArchiveIntervalComboBox() {
	if (ivjArchiveIntervalComboBox == null) {
		try {
			ivjArchiveIntervalComboBox = new javax.swing.JComboBox();
			ivjArchiveIntervalComboBox.setName("ArchiveIntervalComboBox");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjArchiveIntervalComboBox;
}
/**
 * Return the ArchiveIntervalLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getArchiveIntervalLabel() {
	if (ivjArchiveIntervalLabel == null) {
		try {
			ivjArchiveIntervalLabel = new javax.swing.JLabel();
			ivjArchiveIntervalLabel.setName("ArchiveIntervalLabel");
			ivjArchiveIntervalLabel.setText("Interval:");
			ivjArchiveIntervalLabel.setMaximumSize(new java.awt.Dimension(78, 16));
			ivjArchiveIntervalLabel.setPreferredSize(new java.awt.Dimension(78, 16));
			ivjArchiveIntervalLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjArchiveIntervalLabel.setMinimumSize(new java.awt.Dimension(78, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjArchiveIntervalLabel;
}
/**
 * Return the ArchiveTypeComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getArchiveTypeComboBox() {
	if (ivjArchiveTypeComboBox == null) {
		try {
			ivjArchiveTypeComboBox = new javax.swing.JComboBox();
			ivjArchiveTypeComboBox.setName("ArchiveTypeComboBox");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjArchiveTypeComboBox;
}
/**
 * Return the ArchiveTypeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getArchiveTypeLabel() {
	if (ivjArchiveTypeLabel == null) {
		try {
			ivjArchiveTypeLabel = new javax.swing.JLabel();
			ivjArchiveTypeLabel.setName("ArchiveTypeLabel");
			ivjArchiveTypeLabel.setText("Data Type:");
			ivjArchiveTypeLabel.setMaximumSize(new java.awt.Dimension(78, 16));
			ivjArchiveTypeLabel.setPreferredSize(new java.awt.Dimension(78, 16));
			ivjArchiveTypeLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjArchiveTypeLabel.setMinimumSize(new java.awt.Dimension(78, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjArchiveTypeLabel;
}
/**
 * Return the DecimalPlacesSpinner property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getDecimalPlacesSpinner() {
	if (ivjDecimalPlacesSpinner == null) {
		try {
			ivjDecimalPlacesSpinner = new com.klg.jclass.field.JCSpinField();
			ivjDecimalPlacesSpinner.setName("DecimalPlacesSpinner");
			ivjDecimalPlacesSpinner.setPreferredSize(new java.awt.Dimension(50, 22));
			ivjDecimalPlacesSpinner.setBackground(java.awt.Color.white);
			ivjDecimalPlacesSpinner.setMinimumSize(new java.awt.Dimension(50, 22));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDecimalPlacesSpinner;
}
/**
 * Return the JLabelDecimalPositons property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelDecimalPositons() {
	if (ivjJLabelDecimalPositons == null) {
		try {
			ivjJLabelDecimalPositons = new javax.swing.JLabel();
			ivjJLabelDecimalPositons.setName("JLabelDecimalPositons");
			ivjJLabelDecimalPositons.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelDecimalPositons.setText("Decimal Digits:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelDecimalPositons;
}
/**
 * Return the JPanelArchive property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelArchive() {
	if (ivjJPanelArchive == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Archive");
			ivjJPanelArchive = new javax.swing.JPanel();
			ivjJPanelArchive.setName("JPanelArchive");
			ivjJPanelArchive.setBorder(ivjLocalBorder);
			ivjJPanelArchive.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsArchiveTypeLabel = new java.awt.GridBagConstraints();
			constraintsArchiveTypeLabel.gridx = 1; constraintsArchiveTypeLabel.gridy = 1;
			constraintsArchiveTypeLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsArchiveTypeLabel.ipadx = -5;
			constraintsArchiveTypeLabel.insets = new java.awt.Insets(3, 14, 6, 1);
			getJPanelArchive().add(getArchiveTypeLabel(), constraintsArchiveTypeLabel);

			java.awt.GridBagConstraints constraintsArchiveIntervalLabel = new java.awt.GridBagConstraints();
			constraintsArchiveIntervalLabel.gridx = 1; constraintsArchiveIntervalLabel.gridy = 2;
			constraintsArchiveIntervalLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsArchiveIntervalLabel.ipadx = -5;
			constraintsArchiveIntervalLabel.insets = new java.awt.Insets(5, 14, 10, 1);
			getJPanelArchive().add(getArchiveIntervalLabel(), constraintsArchiveIntervalLabel);

			java.awt.GridBagConstraints constraintsArchiveIntervalComboBox = new java.awt.GridBagConstraints();
			constraintsArchiveIntervalComboBox.gridx = 2; constraintsArchiveIntervalComboBox.gridy = 2;
			constraintsArchiveIntervalComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsArchiveIntervalComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsArchiveIntervalComboBox.weightx = 1.0;
			constraintsArchiveIntervalComboBox.ipadx = 10;
			constraintsArchiveIntervalComboBox.insets = new java.awt.Insets(2, 2, 6, 8);
			getJPanelArchive().add(getArchiveIntervalComboBox(), constraintsArchiveIntervalComboBox);

			java.awt.GridBagConstraints constraintsArchiveTypeComboBox = new java.awt.GridBagConstraints();
			constraintsArchiveTypeComboBox.gridx = 2; constraintsArchiveTypeComboBox.gridy = 1;
			constraintsArchiveTypeComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsArchiveTypeComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsArchiveTypeComboBox.weightx = 1.0;
			constraintsArchiveTypeComboBox.ipadx = 10;
			constraintsArchiveTypeComboBox.insets = new java.awt.Insets(0, 2, 2, 8);
			getJPanelArchive().add(getArchiveTypeComboBox(), constraintsArchiveTypeComboBox);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelArchive;
}
/**
 * Return the JPanelHolder property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelHolder() {
	if (ivjJPanelHolder == null) {
		try {
			ivjJPanelHolder = new javax.swing.JPanel();
			ivjJPanelHolder.setName("JPanelHolder");
			ivjJPanelHolder.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsUnitOfMeasureLabel = new java.awt.GridBagConstraints();
			constraintsUnitOfMeasureLabel.gridx = 1; constraintsUnitOfMeasureLabel.gridy = 1;
			constraintsUnitOfMeasureLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsUnitOfMeasureLabel.insets = new java.awt.Insets(11, 0, 5, 2);
			getJPanelHolder().add(getUnitOfMeasureLabel(), constraintsUnitOfMeasureLabel);

			java.awt.GridBagConstraints constraintsUnitOfMeasureComboBox = new java.awt.GridBagConstraints();
			constraintsUnitOfMeasureComboBox.gridx = 2; constraintsUnitOfMeasureComboBox.gridy = 1;
			constraintsUnitOfMeasureComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsUnitOfMeasureComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsUnitOfMeasureComboBox.weightx = 1.0;
			constraintsUnitOfMeasureComboBox.ipadx = 140;
			constraintsUnitOfMeasureComboBox.insets = new java.awt.Insets(7, 2, 1, 45);
			getJPanelHolder().add(getUnitOfMeasureComboBox(), constraintsUnitOfMeasureComboBox);

			java.awt.GridBagConstraints constraintsJLabelDecimalPositons = new java.awt.GridBagConstraints();
			constraintsJLabelDecimalPositons.gridx = 1; constraintsJLabelDecimalPositons.gridy = 2;
			constraintsJLabelDecimalPositons.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelDecimalPositons.insets = new java.awt.Insets(2, 0, 7, 12);
			getJPanelHolder().add(getJLabelDecimalPositons(), constraintsJLabelDecimalPositons);

			java.awt.GridBagConstraints constraintsDecimalPlacesSpinner = new java.awt.GridBagConstraints();
			constraintsDecimalPlacesSpinner.gridx = 2; constraintsDecimalPlacesSpinner.gridy = 2;
			constraintsDecimalPlacesSpinner.anchor = java.awt.GridBagConstraints.WEST;
			constraintsDecimalPlacesSpinner.ipadx = 20;
			constraintsDecimalPlacesSpinner.insets = new java.awt.Insets(1, 3, 5, 100);
			getJPanelHolder().add(getDecimalPlacesSpinner(), constraintsDecimalPlacesSpinner);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelHolder;
}
/**
 * Return the PeriodicRateComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getPeriodicRateComboBox() {
	if (ivjPeriodicRateComboBox == null) {
		try {
			ivjPeriodicRateComboBox = new javax.swing.JComboBox();
			ivjPeriodicRateComboBox.setName("PeriodicRateComboBox");
			ivjPeriodicRateComboBox.setFont(new java.awt.Font("dialog", 0, 12));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPeriodicRateComboBox;
}
/**
 * Return the PeriodicRateLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPeriodicRateLabel() {
	if (ivjPeriodicRateLabel == null) {
		try {
			ivjPeriodicRateLabel = new javax.swing.JLabel();
			ivjPeriodicRateLabel.setName("PeriodicRateLabel");
			ivjPeriodicRateLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPeriodicRateLabel.setText("Rate:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPeriodicRateLabel;
}
/**
 * Return the JComboBox1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getUnitOfMeasureComboBox() {
	if (ivjUnitOfMeasureComboBox == null) {
		try {
			ivjUnitOfMeasureComboBox = new javax.swing.JComboBox();
			ivjUnitOfMeasureComboBox.setName("UnitOfMeasureComboBox");
			ivjUnitOfMeasureComboBox.setPreferredSize(new java.awt.Dimension(126, 24));
			ivjUnitOfMeasureComboBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjUnitOfMeasureComboBox.setMinimumSize(new java.awt.Dimension(90, 24));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjUnitOfMeasureComboBox;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getUnitOfMeasureLabel() {
	if (ivjUnitOfMeasureLabel == null) {
		try {
			ivjUnitOfMeasureLabel = new javax.swing.JLabel();
			ivjUnitOfMeasureLabel.setName("UnitOfMeasureLabel");
			ivjUnitOfMeasureLabel.setText("Unit of Measure:");
			ivjUnitOfMeasureLabel.setMaximumSize(new java.awt.Dimension(103, 16));
			ivjUnitOfMeasureLabel.setPreferredSize(new java.awt.Dimension(103, 16));
			ivjUnitOfMeasureLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjUnitOfMeasureLabel.setMinimumSize(new java.awt.Dimension(103, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjUnitOfMeasureLabel;
}
/**
 * Return the UpdateTypeComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getUpdateTypeComboBox() {
	if (ivjUpdateTypeComboBox == null) {
		try {
			ivjUpdateTypeComboBox = new javax.swing.JComboBox();
			ivjUpdateTypeComboBox.setName("UpdateTypeComboBox");
			ivjUpdateTypeComboBox.setFont(new java.awt.Font("dialog", 0, 12));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjUpdateTypeComboBox;
}
/**
 * Return the UpdateTypeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getUpdateTypeLabel() {
	if (ivjUpdateTypeLabel == null) {
		try {
			ivjUpdateTypeLabel = new javax.swing.JLabel();
			ivjUpdateTypeLabel.setName("UpdateTypeLabel");
			ivjUpdateTypeLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjUpdateTypeLabel.setText("Update Type:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjUpdateTypeLabel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val)
{
	//Assume that commonObject is an instance of com.cannontech.database.data.point.CalculatedPoint
	int uOfMeasureID =
		((com.cannontech.database.data.lite.LiteUnitMeasure) getUnitOfMeasureComboBox().getSelectedItem()).getUomID();

	com.cannontech.database.data.point.CalculatedPoint calcPoint = (com.cannontech.database.data.point.CalculatedPoint) val;

	calcPoint.getPoint().setArchiveType((String) getArchiveTypeComboBox().getSelectedItem());
	calcPoint.getPoint().setArchiveInterval(CtiUtilities.getIntervalComboBoxSecondsValue(getArchiveIntervalComboBox()));

	calcPoint.getCalcBase().setUpdateType((String) getUpdateTypeComboBox().getSelectedItem());
	calcPoint.getCalcBase().setPeriodicRate(CtiUtilities.getIntervalComboBoxSecondsValue(getPeriodicRateComboBox()));

	calcPoint.getPointUnit().setDecimalPlaces(new Integer(((Number) getDecimalPlacesSpinner().getValue()).intValue()));
	calcPoint.getPointUnit().setUomID( new Integer(uOfMeasureID) );

	return calcPoint;
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

	getDecimalPlacesSpinner().addValueListener( this );

	// user code end
	getPeriodicRateComboBox().addActionListener(this);
	getArchiveTypeComboBox().addActionListener(this);
	getArchiveIntervalComboBox().addActionListener(this);
	getUpdateTypeComboBox().addActionListener(this);
	getUnitOfMeasureComboBox().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("CalcBasePanel");
		setPreferredSize(new java.awt.Dimension(300, 102));
		setLayout(new java.awt.GridBagLayout());
		setSize(392, 203);
		setMinimumSize(new java.awt.Dimension(0, 0));

		java.awt.GridBagConstraints constraintsJPanelArchive = new java.awt.GridBagConstraints();
		constraintsJPanelArchive.gridx = 1; constraintsJPanelArchive.gridy = 2;
		constraintsJPanelArchive.gridwidth = 4;
		constraintsJPanelArchive.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelArchive.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelArchive.weightx = 1.0;
		constraintsJPanelArchive.weighty = 1.0;
		constraintsJPanelArchive.ipadx = 138;
		constraintsJPanelArchive.ipady = -6;
		constraintsJPanelArchive.insets = new java.awt.Insets(3, 4, 1, 6);
		add(getJPanelArchive(), constraintsJPanelArchive);

		java.awt.GridBagConstraints constraintsUpdateTypeLabel = new java.awt.GridBagConstraints();
		constraintsUpdateTypeLabel.gridx = 1; constraintsUpdateTypeLabel.gridy = 3;
		constraintsUpdateTypeLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsUpdateTypeLabel.ipadx = 1;
		constraintsUpdateTypeLabel.insets = new java.awt.Insets(5, 6, 34, 3);
		add(getUpdateTypeLabel(), constraintsUpdateTypeLabel);

		java.awt.GridBagConstraints constraintsUpdateTypeComboBox = new java.awt.GridBagConstraints();
		constraintsUpdateTypeComboBox.gridx = 2; constraintsUpdateTypeComboBox.gridy = 3;
		constraintsUpdateTypeComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsUpdateTypeComboBox.anchor = java.awt.GridBagConstraints.WEST;
		constraintsUpdateTypeComboBox.weightx = 1.0;
		constraintsUpdateTypeComboBox.ipadx = -2;
		constraintsUpdateTypeComboBox.insets = new java.awt.Insets(2, 4, 31, 5);
		add(getUpdateTypeComboBox(), constraintsUpdateTypeComboBox);

		java.awt.GridBagConstraints constraintsPeriodicRateLabel = new java.awt.GridBagConstraints();
		constraintsPeriodicRateLabel.gridx = 3; constraintsPeriodicRateLabel.gridy = 3;
		constraintsPeriodicRateLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsPeriodicRateLabel.ipadx = 6;
		constraintsPeriodicRateLabel.insets = new java.awt.Insets(5, 5, 34, 1);
		add(getPeriodicRateLabel(), constraintsPeriodicRateLabel);

		java.awt.GridBagConstraints constraintsPeriodicRateComboBox = new java.awt.GridBagConstraints();
		constraintsPeriodicRateComboBox.gridx = 4; constraintsPeriodicRateComboBox.gridy = 3;
		constraintsPeriodicRateComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsPeriodicRateComboBox.anchor = java.awt.GridBagConstraints.WEST;
		constraintsPeriodicRateComboBox.weightx = 1.0;
		constraintsPeriodicRateComboBox.ipadx = -13;
		constraintsPeriodicRateComboBox.insets = new java.awt.Insets(2, 1, 31, 4);
		add(getPeriodicRateComboBox(), constraintsPeriodicRateComboBox);

		java.awt.GridBagConstraints constraintsJPanelHolder = new java.awt.GridBagConstraints();
		constraintsJPanelHolder.gridx = 1; constraintsJPanelHolder.gridy = 1;
		constraintsJPanelHolder.gridwidth = 4;
		constraintsJPanelHolder.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelHolder.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelHolder.weightx = 1.0;
		constraintsJPanelHolder.weighty = 1.0;
		constraintsJPanelHolder.insets = new java.awt.Insets(3, 4, 2, 6);
		add(getJPanelHolder(), constraintsJPanelHolder);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	//Put a border around the Calculation section of panel
	javax.swing.border.TitledBorder border = new javax.swing.border.TitledBorder("Calculation Summary");
	border.setTitleFont(new java.awt.Font("dialog", java.awt.Font.BOLD, 14));
	setBorder(border);

	//load unit of measure combo box with all possible values
	
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized(cache)
	{
		java.util.List allUnitMeasures = cache.getAllUnitMeasures();
		for(int i=0; i<allUnitMeasures.size(); i++)
			getUnitOfMeasureComboBox().addItem( allUnitMeasures.get(i) );
	}


	
	//Load the Archive Type combo box with default possible values
	getArchiveTypeComboBox().addItem("None");
	getArchiveTypeComboBox().addItem("On Change");
	getArchiveTypeComboBox().addItem("On Timer");
	getArchiveTypeComboBox().addItem("On Update");

	//Load the Archive Interval combo box with default possible values
	getArchiveIntervalComboBox().addItem("1 second");
	getArchiveIntervalComboBox().addItem("2 second");
	getArchiveIntervalComboBox().addItem("5 second");
	getArchiveIntervalComboBox().addItem("10 second");
	getArchiveIntervalComboBox().addItem("15 second");
	getArchiveIntervalComboBox().addItem("30 second");
	getArchiveIntervalComboBox().addItem("1 minute");
	getArchiveIntervalComboBox().addItem("2 minute");
	getArchiveIntervalComboBox().addItem("3 minute");
	getArchiveIntervalComboBox().addItem("5 minute");
	getArchiveIntervalComboBox().addItem("10 minute");
	getArchiveIntervalComboBox().addItem("15 minute");
	getArchiveIntervalComboBox().addItem("30 minute");
	getArchiveIntervalComboBox().addItem("1 hour");
	getArchiveIntervalComboBox().addItem("2 hour");
	getArchiveIntervalComboBox().addItem("6 hour");
	getArchiveIntervalComboBox().addItem("12 hour");
	getArchiveIntervalComboBox().addItem("24 hour");
	getArchiveIntervalComboBox().setSelectedItem("5 minute");

	//Load the Update Type combo box with default possible values
	getUpdateTypeComboBox().addItem("On First Change");
	getUpdateTypeComboBox().addItem("On All Change");
	getUpdateTypeComboBox().addItem("On Timer");
	getUpdateTypeComboBox().addItem("On Timer+Change");
	getUpdateTypeComboBox().addItem("Constant");
	getUpdateTypeComboBox().addItem("Historical");
	
	//Load the Periodic Rate combo box with default possible values
	getPeriodicRateComboBox().addItem("1 second");
	getPeriodicRateComboBox().addItem("2 second");
	getPeriodicRateComboBox().addItem("5 second");
	getPeriodicRateComboBox().addItem("10 second");
	getPeriodicRateComboBox().addItem("15 second");
	getPeriodicRateComboBox().addItem("30 second");
	getPeriodicRateComboBox().addItem("1 minute");
	getPeriodicRateComboBox().addItem("2 minute");
	getPeriodicRateComboBox().addItem("3 minute");
	getPeriodicRateComboBox().addItem("5 minute");
	getPeriodicRateComboBox().addItem("10 minute");
	getPeriodicRateComboBox().addItem("15 minute");
	getPeriodicRateComboBox().addItem("30 minute");
	getPeriodicRateComboBox().addItem("1 hour");
	getPeriodicRateComboBox().addItem("2 hour");
	getPeriodicRateComboBox().addItem("6 hour");
	getPeriodicRateComboBox().addItem("12 hour");
	getPeriodicRateComboBox().addItem("1 day");
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (5/1/2001 9:11:36 AM)
 * @return boolean
 */
public boolean isInputValid() 
{
	return true;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		CalcBasePanel aCalcBasePanel;
		aCalcBasePanel = new CalcBasePanel();
		frame.setContentPane(aCalcBasePanel);
		frame.setSize(aCalcBasePanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
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
	//Assume that defaultObject is an CalculatedPoint
	com.cannontech.database.data.point.CalculatedPoint calcPoint = (com.cannontech.database.data.point.CalculatedPoint) val;

	String archiveType = calcPoint.getPoint().getArchiveType();
	Integer archiveInteger = calcPoint.getPoint().getArchiveInterval();
	int uOfMeasureID = calcPoint.getPointUnit().getUomID().intValue();
	getArchiveIntervalLabel().setEnabled(false);
	getArchiveIntervalComboBox().setEnabled(false);

	for(int i=0;i<getArchiveTypeComboBox().getModel().getSize();i++)
	{
		if( ((String)getArchiveTypeComboBox().getItemAt(i)).equalsIgnoreCase(archiveType) )
		{
			getArchiveTypeComboBox().setSelectedIndex(i);
			if( getArchiveIntervalComboBox().isEnabled() )
				CtiUtilities.setIntervalComboBoxSelectedItem(getArchiveIntervalComboBox(),archiveInteger.intValue());
			break;
		}
	}

	for(int i=0;i<getUnitOfMeasureComboBox().getModel().getSize();i++)
	{
		if( ((com.cannontech.database.data.lite.LiteUnitMeasure)getUnitOfMeasureComboBox().getItemAt(i)).getUomID()
			 == uOfMeasureID )
		{
			getUnitOfMeasureComboBox().setSelectedIndex(i);
			break;
		}
	}
	
	String updateType = calcPoint.getCalcBase().getUpdateType();
	Integer periodicRate = calcPoint.getCalcBase().getPeriodicRate();

	getPeriodicRateLabel().setEnabled(false);
	getPeriodicRateComboBox().setEnabled(false);

	for(int i=0;i<getUpdateTypeComboBox().getModel().getSize();i++)
	{
		if( ((String)getUpdateTypeComboBox().getItemAt(i)).equalsIgnoreCase(updateType) )
		{
			getUpdateTypeComboBox().setSelectedIndex(i);
			if( getPeriodicRateComboBox().isEnabled() )
				CtiUtilities.setIntervalComboBoxSelectedItem(getPeriodicRateComboBox(),periodicRate.intValue());
			break;
		}
	}

	getDecimalPlacesSpinner().setValue( calcPoint.getPointUnit().getDecimalPlaces() );

}
/**
 * Comment
 */
public void updateTypeComboBox_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	String val = getUpdateTypeComboBox().getSelectedItem().toString();

	getPeriodicRateLabel().setEnabled(
		"On Timer".equalsIgnoreCase(val) || "On Timer+Change".equalsIgnoreCase(val) );
		
	getPeriodicRateComboBox().setEnabled(
		"On Timer".equalsIgnoreCase(val) || "On Timer+Change".equalsIgnoreCase(val) );

	fireInputUpdate();

	return;
}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) 
{
	if (arg1.getSource() == getDecimalPlacesSpinner()) 
		this.fireInputUpdate();
}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
public void valueChanging(com.klg.jclass.util.value.JCValueEvent arg1) 
{
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G63F854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8DD8D44735A4C1C32229A441C892CC085104A61878927B55577875511676B30DED4CD77345B43E16571844362635AF742BE9ED9D7E960C9809222022221094DFA9FF82A1080D7C8481C9A4CA14C403DE588B2CAC3B6B6E05DD90741DB3F3E76E5D65AEBF66793E70F93CF74E19B3F3661CB3E74E4CDCA0697B42136262538909CBA26A4F7BF1705FE9C2266DFE311863A264E893517D6C87F8072418
	92613988E84B3E93EDCAA3734E24C0F9A914E39F0BB6E5407BB6925B1B5588AFFE7A09EDA0E4F8DBFC62587DD8F670FE12E9F9FDFDAABC5F86D886B8FCE6F5E17F6947B2D5FC2C0A679097CF48BADA2770D8160A3BGE58F40C200A5DD2CFE8CBCB7521E27D4D628754E5D9ECEE2FF7C77784D580E2ECD8EC73B992FD9FDE6134763D2FB302F36E329AACF98501683606093C8D0C2A1BC2BDAFA3B7F55E96FAB6814ECB63BCD11BBFB8AE433C531BB8B9CF60BCD292C2C152C1DB512CBEE11EC32158451B6430F25
	50B688E901F23A85F1778DE2BFCB213C8AE03D404BC308EF03F787C010050F6D485F2BD4595BAEBFCE563FE0CB5862E7EC6196B626A596EDECA17DFFCE5EC96D704D8C54F76245E8D39EC095408500G00F0005D547E8BF67C8FBC2BBB14DAFBDF1F5DD66DF054D84D4E0F6DE6598A6F1515C0D1F1A765CE3B53CC8833F17B31FC83F9468246E71DB49F53C944A1EE6F48138F1115CF9E0FCFB650494A082278C59719AE16DD54F4427A1E67376F35973D7D513B6FB3C45F771A297604A4033E97BEFFB0BE311735
	13592B755D500B365ECAEDDD816FF12AFFB0FC9645CF576119EF4E25BE0365B02091AB770D363739AC4D0977103462E6C3BD04FE3FBA5EECE5B28CDBFDE5F9117699E1E5FDEE92323CC671CBF4F8A6CBAF45E3F9B5501A7B22CD4C3F1E5809F3F5811417822C82D88F10E50BB6D5GB45B380F3D3923F002BE56A2B9151AFE0B55ACBB8963FDA2E387BCE50F62149AEDAEC532F54A27A527C56A304AAE2256F9AA83F550614597C5FD3F937AB8A65BE42724D86CB6E8DD6A13955969E2F51E4EF8876BB8E5CAED35
	744950C11F0370F61F4B5886CF2B64D2BEF318A5C556EAB07A0B99E8138169B434888D40BB534BAFF7213E2CB07EC00090210756CCEC6F386C04F0D1D959EC377776BBDAF1A4E4158D753C18460E88F80F32F19D3F349BF1F976E853D1BBEFE7DA66D6DDBBF516CE94DFF28E72B916B9047138554EFD66F7ED5CE76AB64FA3794FB7997A8C02434EFCA5486EBBF78E50B6D1AED6FEE430ED12735897DA6D46F3CE0B95BF784AFDA40CC1E37AED5C0336DAG32AC85D8A3F47C234CEC6633986039171EF0020349E6
	CD63CC4EDF8E252BBE03F181F50E6BE21998476B141E6808B6658334828C834CF5F0DDDE9A520FB309E83127EC1C716D563D6F9FD317379A760134CD13506146B16458C6966F44F5CA9E10EDCA1D6C10EDE6F8F129349F6CC4DEF59EF759854F53946F00F9DF8B934DA5704F6E4439A9A90A5352512F4827E4C531583AB57A8B34AFD7FF07C38B993C2FDF6C4471595DB06FC57D17F7621C69345BECB2B51EE0D1692F50B9ABDB65BE903A565EEFD3987E373B506FFB65C1B5E67CFE97A6C08312359F638F4B9796
	C828C307535287AE01A10C94DD42797CB3116FA0AC85FBE35E1388344CCBFCEE3AF3785C4C9DFA00C41DFFB75EE8ED891DD7976F3944664B74CB5A5CE4B2456CB6167DD59ABBD43CAB016296FC06F912F7664267420DFB311DC8E8BF8AE045A5BE97BA32104E630DBA89EA6DD6ABD325507109BDE9E89379D23F8CE19A835BF1213753FB367A50CE2BBAE47476BD28C32BAEA686B9D10787AE73FCF0AD48138238BCA753E30FD30DD5345E2B225EF2A82F81C8F531FA4DAEFD3DC706F93DB040AFF4E9753C5BEB9F
	916D81FD0D2EDE2A222F37758A2FF78E708AC008625D9E7311647318FB9865145D7D1602FE450211DCD2E0F2B87A95EA9616CF9EE9C8D1675DF205670D2F9FC6BB2576C71BF2812A7A39AFBD3719670DA13D61A46641DDFEF36256FEA61B27DF73256C1BF85E38F49F763988E8CB7A398F3C38C73FF6D53BDC76CE8BDDB6D57B856D557B9A435D3677ADF8F6D91CAE25910201075B7A4E3DA8AF2E2BAADA4DB30EDFCED57535927ACD9F001CF800664F1EF41D6C4D83ECFDD46049FC7A094BA598AB28B39630C70B
	6C6C32BB7BE4F35EC5E9C0AA105CCA810DCB8555147C913E9F643609BE381549A9D36D3345BE6F60E91E7F4D59F6144F61476EA1D5D737459B6563C1DB7644AF9BE07ADCB72069F57889D3421A763FA433B1EE9966E32CF343BA6066E3144AFD473890E8AB5CFC0C8343DFED0CA7BF2BC1FEE7E70FE5C0EE9DF448100BF558EB6C1E7355BE0DB57BEF6DB06A040D63E7E29CEBC046DC8F9F47305BE89C67004E78EE9E61FC8E40C5883EA4C39B47F8781EAC31249A1E7DAC8F7BAA622B729F3E42650805BE728739
	9CE440D78EB35286399D66DC39D6BB30F53826016FC14286190FAC98547220E8F8DFB1681D97315CDC2EC2F9578C325CFC3340AB941FB6246159785E20B1824B25C08B9F627964597D38A625DE06B88AF094208D408390FA194F755D7B8E11F17275C608DDCE1BE4BD6535G733CF99F6A8A56CC357C7CBE54B3DDB38957432717FD77E28BAFFB0FFB19CF19711D9F45D760D30E7561E37E53EDC5D9E87CC5ABF98ADCEE48A7AAAB1B38A3297B7767BDA9AACF6AB00F2BA3757A383AF8CBC97CA6BFF135EED8BDEB
	985666FF3E580FA76560DAB197E8D1GABGE281B2C722CD25GEDA3DC7F67330F0C27FF6AD42D76DA40B5401AD96B1B5788FDED9F3EB6BD670C625B6BD3760CF873311CE7C115BE67899EE173F8A1BC17GB09D071673B31218911E833FF80DF23EB60A4F579FB22FF873B1991EF4F3990A2EF099EE9B39B6996E9D45672B2B9EC3992A042F8588992A2FDC1B8CA7C671796AE15195A3FB7D355F48DE4B206E2AAB5CDE6F8F709CE4BD6269DE75F31A1714DE0DB635DC65387D0D08B3C359A3F0FFEBC2DC981497
	8A5C3EE6442D00725A2BFC5C3D54EF928322CD59GD5G67G06814282F81DA1DA8757130581ACA7DB9E40F2C20C512C4E1B07AF286D784E4DAA95972623E56B7A712DBF96303E522BAE9063BDE6327A5C2C43713D61F87BADDFE0B1413C5F7F5E2AF73F715E6A52FE633D15FB7FBB8C8FED68CFDCB87D6A7E3177DEB76FB75AFB6D213AF8FA47965A364BA53BE8104224D6253D26ECA786FB30CF24BED955A3641117B72AF5BED26C35A5C257BF77687DBB5647DEA9B70C369F0B3953F3F1BD4F82FA85C08BC08F
	C0508DBC566EB9B06EDA7711E70C35EECB0EFF3B6C48B136CBD68E7A054B5E6F6C14FDF51F1313A9E8DAD4EFD464BED4932B13CF79954959AD0F325DA14A4FE87E782BF27C59175920F9D8EF03C3042F21EF9C44368D56D05DFEE5F5D50AEA33089B78FE650D6D180B44C0F99DC05A0DFC2E4EAE9F6BACB86F4651B6E572250D6865534ED9963947EA2F4AEFFB51DE6D950B762A3DFC2E45075F6C37BD4F95FDFB2504477B32819E6FBDB7F25DBCD0CA4F1F203C84A0C66826EA00773D8EF029B7F977158D65D29F
	9C6B5B51003E61F04ADD3293F66927ACC3B83782EE423EEE21B13B815EDBEF6279ECC5A31EE7D825C1FB3F82EBC2A0561B5F485704E8283792A091701901ECEDE03E70EFEE0CE3DDCE298FDC6014C36EA429D310F77BF14E6B017A5381A28116822C8964FD16B7A3AFB0DA7A7A7B2804CC3E440FD13ED6E8674294DE779F75B89E66E155EC77A246FA269D675EE7E2CF50ACF548D615B6183B550BA652BC15FE249D67G246E963B5952F992EAB1EECE4FE9CF77217396987D440729C22E86BBCEC136FFB861357F
	891A42F496B9056FAFEABDC67428A9FCED7C834D8916E3D960925A789A3AEE8A0FDBA1397E634E3D3946F16701DC637560615C31570347F37D458B75FC33CABFCF9A26421ABAD573CD47545173046DDD3AF7208E8327323D4BC601D7A8BEDC07E7FB1AB7E99C4172E220E55F4C7533301C6BA7D0601EF5F35CF201EB233A4D8D02399344F16CCE946BB5847139D7B9609BC7C20334B1C486799B4F76B214AFAA48F7BC87A8FEE5106FF89EAE6563D98734F337F019CE51B8918565158277C1A96256C2195C4AF1C3
	1DDC76F4011BD1C9F3B0A88F83845F0A7735617FA0422FF7B4E64353A63B3345E6354E52A75B7082A36FD0D1D1FEF6F1D149C1366F89F76B6FD8AB5EFA5F70EC92E1792DCCA72B5567621BF354B1BE5F486FD83327419EE79A0F9F4597F0AFA45692942432721853E22E113A1BE9B4E2B1F38F4D9F11BFF89A0F19471B68BD9514E3G56CD637BB44FFBFC1F967950BD24B960386179D7CCEBC9FC5AF4A6E769F4ED1F96817BB4566761633CCF8750A326A34EF2DCBFEFD1173362D80E4D6877FA51F13EE04E2351
	FF62C5C7F95FC95053B7F8511354B68A88BF03FA66DDBE36754FC4906B37BE70FB3637D69DD37AED6C59F2BA07645E7A17E4668BD17F54FC613943D97EFD81BC2168203A6EF779D977E948E4BA1B6A666BFE73EDFC5FB0646138405BF8BC7FF7B71E27F759ED8A0B793B86314EE220AF81487F86453555990CCB2F3BE4F58C886B00AF9578F2D43DE5437BEB9BB6ECC05D30B911F8847BD0G9F748D91B35A91978965A8013BDB6273E955B7F8AC685B01E75A25B3A0F784F0G044C907B91CADB886565B358D9B2
	1EBBB35A066DF85F02F5E98C1CB1717DC569B8F5DBC47F19BB5A08F6A713BC4176397F0EC5C7BDB2DBBCD3BE16AD12F4F6C0088559D202B5DBE4417BF655CFD9FB2F3A47EBCF5FE6DE30F7FBD53A7618BF59BD5E7EB4057ADC8354EB8D667E34784CF8FD6A7DA9B0585B1FC2217CD7AFFF7A1446673900DF944CFDE719D8EFD686F37B6CAEC37F483FBD5A548C60G88391D5372A8ED616D6C4EE0B9BCE385EDFE29513DA7729B490FED662B34E6BFF574407A7811E19F7EFCE8234EE71246E91F7B57B67242007F
	DCF4186AB07D8ED8476F603121324C5B169DF44DEB86FA5B9D5C1637F7CE469F036E7036E59814F746697D47DCE61C0BAE226BE88454DFF887DFDF1EBD2BDF8F536E599F9FC70CE3F04C9D4C8EEB6E505697751B0982ECB77B4EE8D3551DBC17FB659F38C01F16AE2A3B4F0F68D7C6F47B894896275DA1BB15C1062537E62A8DDFEA3C36EFF0FEFD0DFC871AA64B47721F7F206B7539BBD97E93A770CBA83EC707E7B94327CDBCE7D0003662CEFEF67DF2A1DD07C2409FG9CGC1G91G51A1DC178579C5E41C7D
	FC2B5D518C0933D5176A32719DBDFEED5FA6FDAF192F4D8B3FB307AC1A7AE61C3F736195A16A7D107ADC5D1AC371ECFC8345B83E3C195126BA009E00G0090004819FCFC837979ECFC688E550AA2F576509BB7BE385396592D21F1E34646B64DEBEF9CB513753FDCFD2E1EB9BAEEB03E2BDEFB6075EA3D343B58B3772E51FCAC4FA8FA1F08B97627B38708CF1ED18F13BC6F0059A2D96D5D87A05300343364A0A17BE8FDC2D2887FD97B61E89C6F6394CEC755867F154AED1075683D246E05B763F27C5820C215B9
	68AEEDEE2E3E38BE19493A7B886E07BB6CCE3A81E27B6112A3FCCF9B85BCABG52E6C51BF2E7313BAD4677CC7B68FD742F5AB9DF55AC360FEE05E70F4A47625982E92CF806ED73F3C3FC8FG5E10D9AC1EE1B9825E3743A07F886B9313A3BB915715EA6D1E0F66E80C3653162E51688D4E00E45551038FE50C260B0DB32B73D373B66F76357B933A3FFDEC076F7E161E574E627BDFD32591FD8950A7E77B27BF40F7A39EC6DFD329551D7D21660BF7FFA5DFD4F36823DAFBBD83236DFEF5005BFD25EA77D8F82604
	32F3EAE67757464C03787A456D1E8D3C25211A5D9BC23518291EC75453738E1BC5B9517531AC397A1D7CC00351AD755BFD695A1108DAA71056E1D9FD1D5CE969132CADF6972CBAB66DEC707EFA349F27DA25CE59F54AE1315978794739B2DF7B6218DBC3397D9F9AB22277887A05DDFA7A302A338521FCAF7BF235267B0252FD3E365476AF45A5657959A52587D59F3835CE63EB4A18088F140E698373BF527CEBD93D5676ADC3FB7D4B74C19193C90D77A347100F45754402F7091F9CG22FA9334F52263225C29
	34CA5D02675B8528A3F1AE43D28315765D8254A324B58A0C49B2C4BF55B88595ED5DAC2E7F30897BE66FBF553D1FE97A2A5F5C0D1FC7B82EEB9FEC7CEB8A7D1F0F3DD1E8FCBE76E6217179580642316FC3120B7CFF0B76F6117EDBB4E677198DE81BE870699800443B23CD19G79F773B5BE2C887D933F0A9D3D3EFFE46E46FB817C92D69177B5457E47BA35F87C6FE7E79463F7BA9DF6C53177E17B9CBF33986D25589D9449F07794230E1C166E9E05D5D5716145B8C72CF297C533BD519CB7BF4BEC381B6F13DC
	743C6D9C142D8237C21C41F9846EAE3A0FDB8265958277C7DAEF8D14578B5C9762ECB4659E1EA73E5108133D8D4A8AC0A8408200E80015GEB81926F851D839481B4G1C83F0G848204GC4GAC3E175BA33462203F3989C875500A9A45A9D950ACEA9E7B45917E8D77B2E8ABA50C1FFD3D36875B9B7BDB30D60BE215EB54B4C43DBF18F3045F9F9C8D63E7E1F16D5807874A41G91E17CBB1B374E739CBA8AF06B4278194C49737CEC2BE8B6EFA300566D01F2GC0686C092E45BC96BEFB1C8899DF5901F45C77
	AD184D7BBCD249F16977F1799E9E6072F9G3760BEDE777783DC3E5577F179763831EE767D51268A00167BA7AB5FFFB7EB725DD946E569391F77791FC37C1CEA697D5CC6D74A5F535B28575ADDF0319323C2C117C5361A714391DC776A31405B1E350B5F13A706F35F195D60FFEE4EED98FFEE4E27F7B6B24CA9CC3654EF629BB06730F7F539E491833E5D10A6F00DAE3AECC344E7FBD9F696DE85B2B50473FC7556B33857D620F326236D1F200ED8EE727BB14FCB920877F9896E2F799E1B5AD36DC34BCD667B1C
	517C004E6DC02897AC64C9ED7316276204A64F25B14F83134711A78A7A60E725EA3CEE72EF13371B4637C9C613F13C5C53B4BADE9EE8B20E17F9CD7AF849F07FECC25F6B06F852A66A15885C678277BEED93F1E7ACE625C7600FD039905FA0E3079CDFCB65F2D3DC1B0AEB5061BED7F19FB721DF4110FB6CCE4EFB32095A4EE2352A5F7152BA969B38195047D9AA2345A169643EA0F01F933E6EDFB8B25E7EFAE9385137AD36F179FC3F37E1FC8FD33E556193BBFBDD0F73031ECB7453181A76006FF945950A4FFA
	40773CA2391D4732BC20CDFF001F63251D3F36F3163D5748F7E652FCECDCDFC8A87F5AD1636D2478752346BBD52CADA9F3E09D1F43477B1B9DC6F95D32B9CC7F2B66985BE19D60D39F74395F06F26903C63ED0D3E954C703DA775C0346FDB8G9F68C38B05F224E19F73C75D7FCDCC7FCFDDA3DF6C247918FDEE276BE254033EF63B076217BE68EB379CB7375B8A20D54D65F68BADB352E978DC261B05F30DF53A8470ABFDE8EB211CB84FC8273F9A520FB1D32D132FBE0F4E73F77E74024479DAFD6A74C0F998A0
	58874F560F10CF61D5DDFF3AA7346747FD9963FD66A4916D84FF2635736BC93673A4FEEE2CFE7B94358D57A00B4DEA3149356C3752883FCF78AE4D6B6642B8964F63F96DADDFA2EE991667F1BBDDEEA7C2AF013A774D6D57FF4F765E7FC39F97A4EDCC965D7B7473573F6FF957358FE653548CFA078F76A48FF1BBDF2238D4A8678ADCA03D1B2C00F22B40D526A22E874A9E011B71A55F7BCC97381FA6A2EEA514E3854ECC736F1407E14F73B047BDCDF139D02E923850F7905782E52B405552EFAB82209CACF07F
	5C497B1DABF00D34DEAC14D3A3383F4EF2EBB68C573D9FF4DFFFFB56DC57BE58183FC3779259B05E22882E07962A07BAA81F9338A871BD07C360F24A78779C0182771B8C2E57D0017B380CFF4F31DA605E247B1575D0CE7DA647AD2F2132C039C860BE28656DD5895C6F689D77F4A8C7885C1395748E9E4A4B85EE758867DDADF06F51FA950F409E6C911E37BFFF84F3D185F72B988B893F5FFB1D46B4B354F3083A61DFA6713A04474833E8A67E4DD506779EE05B850D9614C16F69A40F4FBDDDB855D9B87491A9
	7A39375D7F3DA33B77DF81F5D7097A1F26FA6FCF3A69DCCBFBB45A1475A8BF2FFD684B091C57F2C8515DDB63FA5B8C6D7CC93DC3E6FD3E12E8FC6F78D7BAFF5B203E79D1AE5F65CAEF3D14CB1ADEE2AA0D75320E4E6FE0E8A3CC347378BB7E7532044E7DA5D0F739287FC53937DE9C5437C9A478DDA457CB5D4E095CDF7853CBD5242FDEAE149B6B65DF9F50FBFD285F9A4965FB2E0C6B0555F90D4E15G20CF97F53A4B0D75F3150EA5926AAD82E03835F41E2E0272DA01EB21EDE64D0FB6654D67E30E4B1C0CAF
	786EC75B66EBF919FA974E66B8609D73395CE9173DF57F5EE5BA9F019EA16A7C36465BAFBE6FC073B099DBEAA7E397DF99D74C773DDFC86A706FBBFBBB285E00E72D106D4795F40FACF576F6BB6D7DB6B32F7BAB9AC3329E83FDBE467539E1E4B25F04784A5AG6D9C741275C805318FFDC67BEE057A979E6377895FB3EBF3E964CB7C9DEA037C2E5A89899E24F745EAFA27462ED53246BB58BE46397F7B9540D8AE724264EE0D2FB0F562FCBDFD9A5FCDDFCEAE971DE6A3C26E476CDABB3F7EE0627DBF675078EE
	FBE762FCD71D9ADFC5794479DED2B43E7DBBA7B75E3F75EB634DF7EB6D343AA75E7F890F4657D00E77EB877082AD5B5E152DDE21D59E60F5712FAF703A5F9A986BDE2A68906B04714D9C5278B24BA64E7707E10D6F97933E4FE23C69D7B45D54DC555AF939FF627DFFBFCE63DBD2B3397EDF09577ACFC9545A19DDB106ED4A706E2CC434B1182471450C60FD0A602935DB6D4E82F70FC511D95DB013565FFFA4EBFCB78CCE24BF76F712FE9677AC84154857C9124BC9360E2EDF7D0D97DF08BCDF179ECF629B0929
	071E2BB512A4B79E50F9F05EF4C1044AF7DDA2D4776C87DA5A6C25319FBED0766A52DF1CF6A590D3A35909417F146C2477F73299FF0FC67DC3A375E4A37D8BC4985A9AC9220BBE12DC4A20D5EEA669DDA771176CDD0AEC2E66B7D6AE05EC273F97DB08178B058D122BC741934D434DA45106FF9205ECE5E42BE46BAEE45704072954F155C72928292A3C5B283C58CA01E8E55BD3DB5E5D5F34EAD182092FA7EFD96D1239DE6AD46C4EC632D169F14A2E9E3B556CF1FC489ACA21E751981F9320395DE278B85802D6
	D84A04503D1E5953B72F5EDC8F4E62F112C2DA93573A82D143DC5BA3B921AB595928601FF831BBF3856D063D4AFBBFB916F1AE1E442903C455A7F99C8AB1F5C9FD966BA03C54D17F24C6592840BF27E4F3612F7202E459BE12F1126820604E270BE6FE734FDF508EB4D6056CE32A845642EE595ED7D85D5585FBFE51G68951A5FA31A47BA85BEF536FAEE5F5E747C068E7003BA1250D7D4C47FAF267F17607F4594D3CCB145A5205C35E44C9FDF3A070F19BA8F4833054A0366AF28878BAABFFF7D69B9AFD52DDD
	83232BA7C974D7F74188E9DA2DCF7A7BBAE467544819FBB60544CCC39BB950D5BC586EE60FE891C758C88F0BE3868ED5A63C7CE0C2BCC90489428E4F41F971849C1C574966CB02E2F7D03F43C004918623C3BC95F6973E29C053D001B569302CEE42A84FC5EC72E897C474A66188D8FA1BD6CD237FEF1B34DA11EBBB654D905A487688C97729A3CA9212628FFEACF2C5A777850AFDFBA2F21B2F035C9754F21EA6F71C2AE17CF6BC59CB6EAF3C6A6058509A89DE9693D8052432DF4849463B2CCA05A4616F55D4DE
	5B502F07497850CB35219F604283A4FD7DA4BD6CADA97F7D0CA405A42B5FE8A964ADDCBA384AB7726F3A60FD0B7A99CFF61D255B22380C8D9167E7F8095E43638145442E14181848F3FD46DD6F91EE08771E1F98143ED61369D34DCEA62ED842A74AD823789A0F692CB62637F4E3CA7216DBA1E93EC933D705EE035257EA18DD5AB0795F46442836697FC26E6B999AEC1A5C49EABDDC8B35F587AF3ACBE025FE5BBBF30E1B17F1D35A0CC37D71A4259EF3A52B23C7EAD4F300E6322903AE5532936A02E935DC0772
	C2C2995F895966EE8D5F09F8C6BD6510BAB1675CE8161C3D348594257FAC5958656C6E209DA8A47EEC7DD9681616106CEEA6EFA9BD784D93A6FF8A49501AEE412FEDD482CBD7498678F1509FAF2321E1F0239F67BDF8755B6103D7755F752F1B126977775C521FE2F7CDF9CF79FE573F41C44FF0G9F70947F7663048997CCFF5F741D0248EBD5A4EBEF354D0C5F77558CAA3276CDD9E2B2366F62F5D4A223EDCAC639AF6AF87E97D0CB8788008F789B1E9CGGA8D6GGD0CB818294G94G88G88G63F854AC
	008F789B1E9CGGA8D6GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG589CGGGG
**end of data**/
}
}
