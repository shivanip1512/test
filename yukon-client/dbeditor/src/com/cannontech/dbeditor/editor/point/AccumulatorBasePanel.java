package com.cannontech.dbeditor.editor.point;

/**
 * This type was created in VisualAge.
 */

import com.cannontech.database.data.point.PointTypes;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.util.CtiUtilities;
import javax.swing.JComboBox;

public class AccumulatorBasePanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, com.klg.jclass.util.value.JCValueListener
{
	private javax.swing.JComboBox ivjUnitOfMeasureComboBox = null;
	private javax.swing.JLabel ivjUnitOfMeasureLabel = null;
	private javax.swing.JComboBox ivjArchiveIntervalComboBox = null;
	private javax.swing.JLabel ivjArchiveIntervalLabel = null;
	private javax.swing.JComboBox ivjArchiveTypeComboBox = null;
	private javax.swing.JLabel ivjArchiveTypeLabel = null;
	private com.klg.jclass.field.JCSpinField ivjDecimalPlacesSpinner = null;
	private javax.swing.JLabel ivjJLabelDecimalPositons = null;
	private javax.swing.ButtonGroup ivjButtonGroup1 = null;
	private javax.swing.JRadioButton ivjDemandReadingRadioButton = null;
	private javax.swing.JRadioButton ivjDialReadingRadioButton = null;
	private javax.swing.JPanel ivjReadingPanel = null;
	private javax.swing.JPanel ivjJPanelArchive = null;
	private javax.swing.JPanel ivjJPanelHolder = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public AccumulatorBasePanel() {
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
	if (e.getSource() == getUnitOfMeasureComboBox()) 
		connEtoC2(e);
	if (e.getSource() == getArchiveTypeComboBox()) 
		connEtoC3(e);
	if (e.getSource() == getArchiveIntervalComboBox()) 
		connEtoC4(e);
	if (e.getSource() == getDialReadingRadioButton()) 
		connEtoC1(e);
	if (e.getSource() == getDemandReadingRadioButton()) 
		connEtoC5(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JRadioButton1.action.actionPerformed(java.awt.event.ActionEvent) --> AccumulatorBasePanel.fireInputUpdate()V)
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
 * connEtoC2:  (UnitOfMeasureComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> AccumulatorBasePanel.fireInputUpdate()V)
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
 * connEtoC3:  (ArchiveTypeComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> AccumulatorBasePanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		if( ((String)getArchiveTypeComboBox().getSelectedItem()).equalsIgnoreCase("None") ||
				((String)getArchiveTypeComboBox().getSelectedItem()).equalsIgnoreCase("On Change") ||
				((String)getArchiveTypeComboBox().getSelectedItem()).equalsIgnoreCase("On Update") )
		{
			getArchiveIntervalLabel().setEnabled(false);
			getArchiveIntervalComboBox().setEnabled(false);
		}
		else
		{
			getArchiveIntervalLabel().setEnabled(true);
			getArchiveIntervalComboBox().setEnabled(true);
		}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (ArchiveIntervalComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> AccumulatorBasePanel.fireInputUpdate()V)
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
 * connEtoC5:  (JRadioButton2.action.actionPerformed(java.awt.event.ActionEvent) --> AccumulatorBasePanel.fireInputUpdate()V)
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
			ivjArchiveIntervalLabel.setText("Archive Interval:");
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
			ivjArchiveTypeLabel.setText("Data Archive Type:");
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
 * Return the ButtonGroup1 property value.
 * @return javax.swing.ButtonGroup
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ButtonGroup getButtonGroup1() {
	if (ivjButtonGroup1 == null) {
		try {
			ivjButtonGroup1 = new javax.swing.ButtonGroup();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjButtonGroup1;
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
			ivjDecimalPlacesSpinner.setDataProperties(new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(0), new Integer(10), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 2, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			ivjDecimalPlacesSpinner.setPreferredSize(new java.awt.Dimension(30,22));
			ivjDecimalPlacesSpinner.setMinimumSize(new java.awt.Dimension(30,22));
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
 * Return the JRadioButton2 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getDemandReadingRadioButton() {
	if (ivjDemandReadingRadioButton == null) {
		try {
			ivjDemandReadingRadioButton = new javax.swing.JRadioButton();
			ivjDemandReadingRadioButton.setName("DemandReadingRadioButton");
			ivjDemandReadingRadioButton.setFont(new java.awt.Font("dialog", 0, 12));
			ivjDemandReadingRadioButton.setText("Demand");
			ivjDemandReadingRadioButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			ivjDemandReadingRadioButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
				getButtonGroup1().add(ivjDemandReadingRadioButton);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDemandReadingRadioButton;
}
/**
 * Return the JRadioButton1 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getDialReadingRadioButton() {
	if (ivjDialReadingRadioButton == null) {
		try {
			ivjDialReadingRadioButton = new javax.swing.JRadioButton();
			ivjDialReadingRadioButton.setName("DialReadingRadioButton");
			ivjDialReadingRadioButton.setFont(new java.awt.Font("dialog", 0, 12));
			ivjDialReadingRadioButton.setText("Dial");
			// user code begin {1}
			getButtonGroup1().add(ivjDialReadingRadioButton);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDialReadingRadioButton;
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
			ivjJLabelDecimalPositons.setText("Decimal Positions:");
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
			constraintsArchiveTypeLabel.ipadx = 45;
			constraintsArchiveTypeLabel.insets = new java.awt.Insets(3, 9, 6, 5);
			getJPanelArchive().add(getArchiveTypeLabel(), constraintsArchiveTypeLabel);

			java.awt.GridBagConstraints constraintsArchiveIntervalLabel = new java.awt.GridBagConstraints();
			constraintsArchiveIntervalLabel.gridx = 1; constraintsArchiveIntervalLabel.gridy = 2;
			constraintsArchiveIntervalLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsArchiveIntervalLabel.ipadx = 45;
			constraintsArchiveIntervalLabel.insets = new java.awt.Insets(5, 9, 16, 5);
			getJPanelArchive().add(getArchiveIntervalLabel(), constraintsArchiveIntervalLabel);

			java.awt.GridBagConstraints constraintsArchiveIntervalComboBox = new java.awt.GridBagConstraints();
			constraintsArchiveIntervalComboBox.gridx = 2; constraintsArchiveIntervalComboBox.gridy = 2;
			constraintsArchiveIntervalComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsArchiveIntervalComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsArchiveIntervalComboBox.weightx = 1.0;
			constraintsArchiveIntervalComboBox.ipadx = 37;
			constraintsArchiveIntervalComboBox.insets = new java.awt.Insets(2, 5, 12, 37);
			getJPanelArchive().add(getArchiveIntervalComboBox(), constraintsArchiveIntervalComboBox);

			java.awt.GridBagConstraints constraintsArchiveTypeComboBox = new java.awt.GridBagConstraints();
			constraintsArchiveTypeComboBox.gridx = 2; constraintsArchiveTypeComboBox.gridy = 1;
			constraintsArchiveTypeComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsArchiveTypeComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsArchiveTypeComboBox.weightx = 1.0;
			constraintsArchiveTypeComboBox.ipadx = 37;
			constraintsArchiveTypeComboBox.insets = new java.awt.Insets(0, 5, 2, 37);
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
			ivjJPanelHolder.setMaximumSize(new java.awt.Dimension(180, 24));

			java.awt.GridBagConstraints constraintsDecimalPlacesSpinner = new java.awt.GridBagConstraints();
			constraintsDecimalPlacesSpinner.gridx = 3; constraintsDecimalPlacesSpinner.gridy = 1;
			constraintsDecimalPlacesSpinner.anchor = java.awt.GridBagConstraints.WEST;
			constraintsDecimalPlacesSpinner.ipadx = 3;
			constraintsDecimalPlacesSpinner.insets = new java.awt.Insets(14, 3, 17, 6);
			getJPanelHolder().add(getDecimalPlacesSpinner(), constraintsDecimalPlacesSpinner);

			java.awt.GridBagConstraints constraintsJLabelDecimalPositons = new java.awt.GridBagConstraints();
			constraintsJLabelDecimalPositons.gridx = 2; constraintsJLabelDecimalPositons.gridy = 1;
			constraintsJLabelDecimalPositons.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelDecimalPositons.insets = new java.awt.Insets(16, 17, 18, 3);
			getJPanelHolder().add(getJLabelDecimalPositons(), constraintsJLabelDecimalPositons);

			java.awt.GridBagConstraints constraintsReadingPanel = new java.awt.GridBagConstraints();
			constraintsReadingPanel.gridx = 1; constraintsReadingPanel.gridy = 1;
			constraintsReadingPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsReadingPanel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsReadingPanel.weightx = 1.0;
			constraintsReadingPanel.weighty = 1.0;
			constraintsReadingPanel.ipadx = -10;
			constraintsReadingPanel.ipady = -9;
			constraintsReadingPanel.insets = new java.awt.Insets(3, 6, 3, 16);
			getJPanelHolder().add(getReadingPanel(), constraintsReadingPanel);
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
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getReadingPanel() {
	if (ivjReadingPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder1.setTitle("Reading Type");
			ivjReadingPanel = new javax.swing.JPanel();
			ivjReadingPanel.setName("ReadingPanel");
			ivjReadingPanel.setBorder(ivjLocalBorder1);
			ivjReadingPanel.setLayout(new java.awt.GridBagLayout());
			ivjReadingPanel.setFont(new java.awt.Font("dialog", 0, 12));

			java.awt.GridBagConstraints constraintsDialReadingRadioButton = new java.awt.GridBagConstraints();
			constraintsDialReadingRadioButton.gridx = 1; constraintsDialReadingRadioButton.gridy = 1;
			constraintsDialReadingRadioButton.ipadx = 1;
			constraintsDialReadingRadioButton.insets = new java.awt.Insets(0, 12, 6, 2);
			getReadingPanel().add(getDialReadingRadioButton(), constraintsDialReadingRadioButton);

			java.awt.GridBagConstraints constraintsDemandReadingRadioButton = new java.awt.GridBagConstraints();
			constraintsDemandReadingRadioButton.gridx = 2; constraintsDemandReadingRadioButton.gridy = 1;
			constraintsDemandReadingRadioButton.ipadx = -4;
			constraintsDemandReadingRadioButton.insets = new java.awt.Insets(0, 3, 6, 6);
			getReadingPanel().add(getDemandReadingRadioButton(), constraintsDemandReadingRadioButton);
			// user code begin {1}
				//ivjReadingPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjReadingPanel;
}
/**
 * Return the UnitOfMeasureComboBox property value.
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
 * Return the UnitOfMeasureLabel property value.
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
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val)
{
	//Assuming that commonObject is an instance of com.cannontech.database.data.point.AccumulatorPoint
	com.cannontech.database.data.point.AccumulatorPoint point = (com.cannontech.database.data.point.AccumulatorPoint) val;


	//START SPECIAL CASE-- change the accumulator point type to the correct type
	if( getDemandReadingRadioButton().isSelected())
		point.getPoint().setPointType(PointTypes.getType(PointTypes.DEMAND_ACCUMULATOR_POINT));
	else
		point.getPoint().setPointType(PointTypes.getType(PointTypes.PULSE_ACCUMULATOR_POINT));
	//END SPECIAL CASE-- change the accumulator point type to the correct type

	int uOfMeasureID =
		((com.cannontech.database.data.lite.LiteUnitMeasure) getUnitOfMeasureComboBox().getSelectedItem()).getUomID();

	
	point.getPointUnit().setUomID( new Integer(uOfMeasureID) );//setUnit(uOfMeasure);
	point.getPointUnit().setDecimalPlaces(new Integer(((Number) getDecimalPlacesSpinner().getValue()).intValue()));
	
	point.getPoint().setArchiveType((String) getArchiveTypeComboBox().getSelectedItem());
	point.getPoint().setArchiveInterval(CtiUtilities.getIntervalComboBoxSecondsValue(getArchiveIntervalComboBox()));

	return point;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}
/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	
	getDecimalPlacesSpinner().addValueListener( this );
	
	// user code end
	getUnitOfMeasureComboBox().addActionListener(this);
	getArchiveTypeComboBox().addActionListener(this);
	getArchiveIntervalComboBox().addActionListener(this);
	getDialReadingRadioButton().addActionListener(this);
	getDemandReadingRadioButton().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("AccumulatorBasePanel");
		setPreferredSize(new java.awt.Dimension(300, 102));
		setLayout(new java.awt.GridBagLayout());
		setSize(371, 177);
		setMinimumSize(new java.awt.Dimension(0, 0));

		java.awt.GridBagConstraints constraintsUnitOfMeasureLabel = new java.awt.GridBagConstraints();
		constraintsUnitOfMeasureLabel.gridx = 1; constraintsUnitOfMeasureLabel.gridy = 2;
		constraintsUnitOfMeasureLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsUnitOfMeasureLabel.ipadx = 11;
		constraintsUnitOfMeasureLabel.insets = new java.awt.Insets(5, 6, 7, 4);
		add(getUnitOfMeasureLabel(), constraintsUnitOfMeasureLabel);

		java.awt.GridBagConstraints constraintsUnitOfMeasureComboBox = new java.awt.GridBagConstraints();
		constraintsUnitOfMeasureComboBox.gridx = 2; constraintsUnitOfMeasureComboBox.gridy = 2;
		constraintsUnitOfMeasureComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsUnitOfMeasureComboBox.anchor = java.awt.GridBagConstraints.WEST;
		constraintsUnitOfMeasureComboBox.weightx = 1.0;
		constraintsUnitOfMeasureComboBox.ipadx = 73;
		constraintsUnitOfMeasureComboBox.insets = new java.awt.Insets(1, 5, 3, 79);
		add(getUnitOfMeasureComboBox(), constraintsUnitOfMeasureComboBox);

		java.awt.GridBagConstraints constraintsJPanelArchive = new java.awt.GridBagConstraints();
		constraintsJPanelArchive.gridx = 1; constraintsJPanelArchive.gridy = 3;
		constraintsJPanelArchive.gridwidth = 2;
		constraintsJPanelArchive.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJPanelArchive.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelArchive.weightx = 1.0;
		constraintsJPanelArchive.weighty = 1.0;
		constraintsJPanelArchive.ipadx = 7;
		constraintsJPanelArchive.ipady = -6;
		constraintsJPanelArchive.insets = new java.awt.Insets(3, 6, 7, 6);
		add(getJPanelArchive(), constraintsJPanelArchive);

		java.awt.GridBagConstraints constraintsJPanelHolder = new java.awt.GridBagConstraints();
		constraintsJPanelHolder.gridx = 1; constraintsJPanelHolder.gridy = 1;
		constraintsJPanelHolder.gridwidth = 2;
		constraintsJPanelHolder.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJPanelHolder.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelHolder.weightx = 1.0;
		constraintsJPanelHolder.weighty = 1.0;
		constraintsJPanelHolder.insets = new java.awt.Insets(4, 6, 0, 6);
		add(getJPanelHolder(), constraintsJPanelHolder);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	
	//Put a border around the Accumulator section of panel
	javax.swing.border.TitledBorder border = new javax.swing.border.TitledBorder("Accumulator Summary");
	border.setTitleFont(new java.awt.Font("dialog", java.awt.Font.BOLD, 14));
	setBorder( border );

	//Load the type combo box with default possible values
	
	
	//Load the unit of measure combo box with default possible values
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
	getArchiveIntervalComboBox().addItem("1 day");
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (5/1/2001 9:11:36 AM)
 * @return boolean
 */
public boolean isInputValid() {
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
		AccumulatorBasePanel aAccumulatorBasePanel;
		aAccumulatorBasePanel = new AccumulatorBasePanel();
		frame.add("Center", aAccumulatorBasePanel);
		frame.setSize(aAccumulatorBasePanel.getSize());
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		exception.printStackTrace(System.out);
	}
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val)
{
	//Assuming defaultObject is an instance of com.cannontech.database.data.point.AccumulatorPoint
	com.cannontech.database.data.point.AccumulatorPoint point = (com.cannontech.database.data.point.AccumulatorPoint) val;

	int uOfMeasureID = point.getPointUnit().getUomID().intValue();
	String pointType = point.getPoint().getPointType();
	String archiveType = point.getPoint().getArchiveType();
	Integer archiveInteger = point.getPoint().getArchiveInterval();

	if (com.cannontech.database.data.point.PointTypes.getType(pointType) == PointTypes.DEMAND_ACCUMULATOR_POINT)
		getDemandReadingRadioButton().setSelected(true);
	else
		getDialReadingRadioButton().setSelected(true);

	getDecimalPlacesSpinner().setValue(point.getPointUnit().getDecimalPlaces());
	getArchiveIntervalLabel().setEnabled(false);
	getArchiveIntervalComboBox().setEnabled(false);

	for (int i = 0; i < getUnitOfMeasureComboBox().getModel().getSize(); i++)
	{
		if( ((com.cannontech.database.data.lite.LiteUnitMeasure) getUnitOfMeasureComboBox().getItemAt(i)).getUomID()
			 == uOfMeasureID )
		{
			getUnitOfMeasureComboBox().setSelectedIndex(i);
			break;
		}
	}

	for (int i = 0; i < getArchiveTypeComboBox().getModel().getSize(); i++)
	{
		if (((String) getArchiveTypeComboBox().getItemAt(i)).equalsIgnoreCase(archiveType))
		{
			getArchiveTypeComboBox().setSelectedIndex(i);
			if (getArchiveIntervalComboBox().isEnabled())
				CtiUtilities.setIntervalComboBoxSelectedItem(getArchiveIntervalComboBox(), archiveInteger.intValue());
			break;
		}
	}

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
	D0CB838494G88G88G7DF854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8BD4DCC51651FCA40A06A81A98F1A4BFC50DA6AAF3CE46496CC6A6B3461119093B7118590DA32B9C459D546C0833D1B3636393C214904C979312F4BEC4921110C000B0287C0281BA509192D1F38CCD7783BAB47DBA5D2F21C9C85CFB2BDE55FB5D3C6693B76748B917776AD65D3A1F3AF56BD63D82E1532E705509C91B84A1F135207C14A48AC248D9C1087F552BFFE338B1E2D42A2079598D702130
	2BBE851EA1D0D77AC4D4EA2660DD138665D9D02E78F9D46AD6F85FA8F46707B441CBGBEDDD0D775FA504A2179AC7F0871D9C34AAFB52543B383A082F078CC90117F3BCDD98ABEDA41B3C8CC9204E59D58E60BA9DB4115C25984908A9051C15BAF0467DA42F9FCD929526E6C64F0A161F79FA72D43FEB4FDB2B0F4D25A9A65E99066A646F7A22FAFCC698A4DG14A381D079E461136CE2F8161B3ABBBF37C8BD79963343A1B9E4515215AFDAED32644AF7CAB607DCD6D6E931F8FABCF6B3202A4CEE51E4F608F630
	D99776FD03A90D74A58885D0CEA038A6A3721B836F0BG16F3FC4DC944175AC0DF00AE9B5371330F4B959DDAAF4D95D6BD67C8DE9FC047B1B62ADB04CD55B154734E1A0F48F8E4EFC73B47C2DD5A792854EC008A00B600B1G5B099F6C5DF6901E156DF2355453A3B9AA1D4EAA3B55F5D2320AF6F887DD5BE5857715E811DCD6C120E3FD5C1427A34FD0C0691AC7CDC7ED92D14846FD4ED7778A4B9FEBC8DA23E313659185C9D34FD3DB4CBB2F5A04728E8E48FB49F9DF3F74657D3520659DBFC1CADE2D43BB7259
	83C931CABFABB53C733AF12CFFCDFCA08B5E118E69A97E8F846F5460290F1EA6F8AC0F033A38EE669B2D99CC96E34AB4A17330D1578ED323AA130CF6AA03556EAF4BFC62F7A1F64A7383AE4BEF89FE1E86CFE529A7F8ACAF013A221E28D46ADFB9BBF04ECE05F2A4C0B4C0ACC00AA3AA55GD064E0BEE65FF6E804BEE6B23B64AA0F4DEE95DD82254D593E851E22D7F6196B9CEE596C3008E74CAE1B395DAE3A8525CD7EF634C13B8FDDA171FD8B7078D2F408AE33EC139C503B39C714C5171B36A95F7EA136F109
	24364556A382039E27407A2D593E911EF633DBBE6D341AE5D1EDC16B0D5BF1CCFA4DE720C768G5E29DD3E5A0976EA817D6DG835C8ECF127E9AC497440B32B223A4F5FB1CAD2809906DC0BB4FA431A3845EA39C4C460D3B9017A9C52566CA2C1FFFEC5F20692746E6C1714D2EFEB6471EB2A2CDB934BFABB13FF93D1579CD4D3A59C25E33753AFE03A0CB54DF02A57F793314740B32C5CAAC66FFE7EC0567D9C9FF7E0C964A7D7A21BCE442E3757FC37F8849FEE5EA6785869C332540FF85C02613597AD836017A
	AEC6FA662DCDAEF0B4512ADA1E4A79AFA7B7A97D6FG5825483F166B91CD6AF321EF930097A09CE031135952F43255CFC6C6BF923D984F21632FED908F6C7F039172D82D7493281E5A6C508EDC2F44DE51A15708CE51E1059737D2D73C8379A97339C362B465846F0479DF8D934E4D71D5BBF00E1AE559E5EB7748E233A84BB6C727DADFCFF839BD6DCEB5F4B0DE27F6203CD29F4CFF5E7E4C8E1CBB964961904960F192253E154CDD51AE760050551247A1D37C77BBB15769967B1558513E939322DE335D03F108
	668F734CE8C7274B5683AE0121CCE83900737AF1D31646C6ABE03CB18F4A053A728BECFE766DE573F35F45694242F3B9C9FAEB4C5459B5C9E19714756D02BABF29CC4D3B74E57F064490856F263246E152A768E5B24904A6B1917B2A724832647852A5F91C0769BCF9EBB76217G6FD800D59758BCF9F69756331824CC10EA49EE27E6063A8A975AA20B4C81264FA69735056ABBD106CC9CCF7102C704D00F4131015B7CA943863F3AB30A7DE97DB386343FDD93D78DEE64195848F24BA060978E30D061AB38959B
	12F63F616D6CD09E87B04B2D57EEE293EB978B65D5815BFD46F35AEC9956C30096D92FDD62A956AE946A67683423E336669C66AFFA79E92747166F11ED389A18E518D8CE0FCC461266247BCE25A973BCDAE63967BFCAF19CD3BC10978294F9189FBE330E651E215D61C24C4CED8173EB23074A667428FEE8984BF24FAF76A24FF9D0375043FC4438DB3B7ED53A5D1245C616DEE57C5E5D2D75D30A93763C8F4F8E1B4BAD57C1A0713231CE5D0372625A2C20959B7DBDFF873351F9F32F591B6F6E03F8D1D62671E4
	0165F517EB353AAED8FFA46903G3AAED5F4CC50681A87F9B64D0F8EB632DC2D289756FCG6A6BAF2D577A7A2CDE3A2EC743134E31490DC7B036118912CF9FA65155A139FAC4EBAECA1FEF6E1373C99C4D2FA45547717DG53F551018D54B6A299F11A24649EB843741B3131C2619D7D70B4217C070DC9FA7B082075BB12E2FA29DEE9FD2AFE830F26A647FF7F2E22636BDC4796E8935C47F4FC67283F0E0B20AE2E0F69F83A69EAF524FC9FEDE2FCD7C11FC5DE4677952F9EDFBB54D33A8E93239B8338791CEEFD
	0316EE8560337B212E1F497BFB530F137760A9EEA768B3381F71FD3DDE0F6F22FE36769FB84576A64B7A699844772B3910F0B1AAF553C55F5C08666C2EE348AF77A24D5957F27C8F84DF2341D379D2889E4BEDD03768A24BB33765609A15FBA9AA358660AC008C908A90F9094D5FDB76948A436471F5900FDC8E333D59E7C1E67974DE34B12C21CA7944DE3493D9C3851607856DC7BBAD811E71GA983E0A7007281A6439F778EAB43F12F2E8C544E850D7EFB4033833EF6758E6865201F8F224B77AB07795151B9
	5AE95F2E9B0F6AC17EF629CA72AA67865BB2D90C0E9CE0E3726A4738AE44C3B96DB26C29G4A815AG86G42AFB31B6C5C775930B6C1A7ED112A81F7825647EA7F7C076B19F01576C9EA9A4EBE8BAF7B52515C68990FB6B7DAF6194648D5704C3C9215CAEDF338181D2964DEE173E57EA0FEA3137337434A59F645170E4A30331F496065B258871DA70CCC060B03687CE518F7C54F1736D46D5477252F4CD61BC4D7B8E6573FDE74376BE268B39EC07801593536A1CD11A96B8726D341292B1C9F03687CF5B27D20
	E7579CA2832E3BDDD0CF774195A4DF890772820E9356A1EE8914E3B96E698C44250D0139B106616257A32E864AE747B01D3C47B027F4C2B984A092E0892F9BCF6A6246503C90571045FCFC5FBF566A278352E578613460D46AC3F45AC50E51335B5EC1BC065B6F31FD1B5EFE58FFEF261C7165845E0F9D4C515F0F9564686F470AF3BE24F868C3FBDA436ABF4F99FA3FD61B93F83F9632BF301CD3766B4BF977FEFDB9E76E9FDA4E87778712F3873171D3DB5693FA37DBF41360090935D2770A3CD95059C3B61AFB
	C4E5FCB68A1D0DEB15B647E5297A889F6FDD19EC1E24DD87EB5BF53EBED1G659ABF9C15774D0F835B661D0F75EDA364E26E6E16BCAE0B682F7F1A5CACDE272E86F5325803EA50B61904DEB63BBA45C1765BCA68E9DDA0FAC3EEA09BD39BFD522435D12140625C1B8730EF1DB82759532C6DE5312B75BA3627B9390D1CEBC2B982E061F5EC9E97978FF5663C6C3A41F3154A77060FFCC56A1C6D9F2A3F78007D4DB4E97BBBAC30D8FDC956DB83056BA1673B1E456A0317D8FE10F7BD5337359E7535C2F9GA074FA
	26EFC303D63EB957AB5FD01467526B834957F9CAAB5FD12E6FC11F7E62947AD54AB3F3ACFD6E03E72972CE7B7B7A947A0F53A5F608AE17E8ED36DD4479B1EEAC4A6EA4B15E886F2DE3D96E3DF59D1E29584D7D12C7063D54B8EC973C0E59E6B1340B81C883FC76B86D3E7811BEDCBFBADC669EF013E627E891B247A36D0B990CF6G5A07G4C83D88230E29C63191C0934C0E86B7174908929FC4F10FC2C957A310EE7ED858177DB279DB639296324E8F6FBDC22515C0E5FFED87DE67FFA1E6531B637DCC61BD0CF
	2EF4D93AEC3DEA4C902A53F95D8989279B5DBB7C6218F66F90BC1E7A473C71EC4DABA632AE02F28C471594A1AE814AA993986E9FDB59FA1945F196522E8B4AE3A6B07F4968550EF7A860A3A628BE33E042E07F21FB0E5367485950847F3DC8BB4147CF705F0BB4EFE3FB11349BE06FF6831369E4B113F3894715757335BE084D0947E496831CC1ECCED8E46CB3824A8B811685B11D8EDD526A348270A993418E9329AEF993836994CD66D845C47F6FB64F91FC4BC47F6FB65F57337DDF9754C5CFE4723713BC2760
	C6588F5E48F09D864435C0594AF1CF9B181ECE0E2B64348BB96E035D0CF6A94759F7B35A980E3B03525E9415EA3A0961DEA5B8AB1403G42EE42EF3BBB71930D6207EF259B6069907B8CFC43DBE36B919D7811A3373020A04FF038604881FA46905E277D9EDB7AFE096E39A5C24CCD4A990A72DCF043DE45E6116B5877583CE030C5B07B9EFB388D77BFFC9DC0C14A4A3EF4592CD566CEA30992548FD6115C90694302199F9C4CA06779D00E87C839191DEFF97733B31FB977CE930CE39AF44F37E2DA0EA4E55FCC
	652C38D9BD9309703C330672CCDB4FF88EC0FD644DE49E7A1C7720ADAD09B48F2675DDBE759FAA3E2F56CF7329473AD712347551BE75E823F58ABD556D699C265B2A27A3847B4F0F85BC3BDB45F43A05BECDE7768A7BEEFCF78D75054F8F2B3E70E2E9F6E0DFGCFA8B8202C599D8156EC92A1294DD69CCBD3E4EE3D05455437B784DEA34E113836G5AAE666DF719709C3EC3F24834CD9B0DF3D0AFCC825F1904384DD9B5BABAEB6D36DA510FFC27871A9A003D8A3E955EEFD36CC6F9EEB8263FC684935EF3207D
	7CC9ECEE65103CA4964AABB9EECAA56232C322D28BC298EECBB51B2BADA17CDB5AB95C0B4C0272C200980014492C2E0B546D03F24DE4FA660C6753346E1BED3807403644F7C3C67EADEE56B0ED97F259DC04875B1B8C758DC8AF37982A9EEDCE474DDABC5C38A5EB408A7AFAA72B63968C6F8D4A38517ECE750F468F267A75B7C7531FF266A07B67FD73A72B760B067718493E76D4568A32A6A5CC26EB459A0EFF096053EFD571B49E5A889E4B7B20AE68D666B3351718EF45F35C1D75647B70EDD1297BEEE338AC
	721D768414DBB96E29DD4C5F6C3731B17DB7C23BG4AB19C77BC4195A95FA1A84EDA6F3FC79DADE07F5A7282CD1972387C23F9786F07137D5EB78A7D8D3E31E392D987D3C261BD1445174B86ECA343E60A46C2B3EE5D94FFFA51B014BFA57BC10D92D7B6ABBED4217040F74A7FBC61EF84FCEBA8DB07A7972A7C466E9E4DFC70679784FD3E7D5EFB6F29F16C21864CD13BA4176DA25840EC2F345BBA9D782DC51167C9A28F769B4A6DA1983778503400E5CC125BC6BF6AD13A6DC636B7D9984A5659BFFE274D8BB2
	276DCECA9474572278D0654ECC283A4E52734F31A43E665D9E15FA62F6F656E9B97489BC4F184FAB271C47494DACF24C89C813CBF20AAE391FE24957C1453F9EDFF7F5771596DFA5DDE246E869E8CC789949DB3AEE27B1A111636FA3F8D903273122A00365CEE320AE76F6F6F69CF994576393F7401A88B0G908A908930648EE64BB9078A04E14E0DDBA427D16C956D1AA3942A5F3A75D7F70F6B09B5ACC709FCFC06B0FF425B09013E39455EC17DA1E58AFD46356CA5F82A5F17A528DF915419GE400E000F000
	79D318FE89077228FE688E1532EC36F411AF0BCC39B3B631CFC56346116A765620B3B9925F95B9E226682F198900CF1B6ADB178D6582BF9C1D67B1B8BD14F919D70477B68264D8070F9467990E949DD0680EFE2E527DF56B9EFF3ADA182439FB2CB633DD6A5C835999246AC0CA6E05605CCF93584F0AAF866318EC16AF84BEDE7F154E46AB7B413B041A675ECE5C9BE03CEA94BDC3272A73B86E7CAAA5277CFD05AAF724E7E7E0FD0F95D0F51536653DF8A650AE39486193BD93E86E6577C901D7ACC0761DE067BB
	6979AD25FB38F270D942EFAA995D09BB69D9C29BBCE5050E465CCF2B070B7168A3AC768683ED781DEA4E90896FF8CE779644DDAA07A18573B6E5275F526F24A786346EA347A6350EFCB96BB55BB575A105DB87577393855A668F560D3E7D73FAFAF690F64E7F6CGF5DDF8A77BAEB937C42FFEA9540F4EE70ED6613B9E0D62D355D82239489F493EBD8EF82C3A1365A7653DFCFFBF0D61AE95A24E896520E9BCBFA139D1981497G44F07C2B843F924AD9F751EF2B94AF49FAF399E9356598259CB6ED6439339A6B
	03F4F97867A3ACE79EAE9F76876A3B52A5663B25F7D11FB741532A68CAFDE1DE7DE01FFF321E1F015DC5FDBE941E91BEF4070C3EF951F1321EAF04B651F731F99133EBB4394FAA204B8C2373824B7B607DB51893EF70F9515F09F9C58D842A2FC43395360ABE5F30E81B76E2143DC66CB1BB2C03DBD13F4ABAA1882C7D3706114443A387184F7E33CE755FA98DEABF6F0D321F470DAA6D1F9BF08EE83640794D783BDAA217D7E5F73E71F8ED0B52760851B0F45B2F6A3EBC51C2573B4E1348C339EBD518A3844801
	E0D52B077E1A5A4F0B9639455C49E9020AF01C78D9BCCD0D143AC9C59883E0FBAB03FE360B64CACEB3B4F4F4D27F1E05240A2FBF24F9AF4E7831B97B70F32B691A72207A7F6AE860EFAB0B0E6AFFDB795DD17D6FCECB0E8E7D7DEB5951405F3FDEB82A3D2FC8477D07C69C1B8218BFC6GA740B80029E1AC4769A9C6DF459B5403731B6356CE11B99363EFAA892C6B57A5435F35EEAB417BD86D924C409E6C1F61BBCAF03CE449C9109457DD02B6F259BA3BE45AD441DFA84178E297BB881A467A2DE5080B843D96
	0531787F9041AD03728A0EFBF183BB6BCE391B6152CC6C2CBB0B63DAD259FA1247F16F1632336E013BD91E7CFC93463590A84F81D8829087308AE0534F22D2F3814A81CCGDDGE3GC281A2G96GC48344GA47C0C0DC7449787824DCDC0AA0717E4D0DCE69B8E0B1247DF6ACDD7E2C85ABD30B65F434EC0DF59017D8DFD672F45A65B45AAA5C5D23E2B7F3A176D653C7730B351588AF23F9F4A0B81E26EE1F725DE2BE4FB081500AB88E7676BC72B5899E7D7B8BFDF2F423661D01E8F30A4FC24F9824B83FEF1
	CA60B27E7BB9F69E9847F93EDC42F04669ECFDFAD99E3C2E7D1DFFB31BB71D2EEB0B6139FC3A72B78F13E81EB812B50B65F289C01BB6C34D6532E728FBADAA472D05649C506C75BB87FC0064A939503EE88613797CF61C37B5224556E3361B6CE60B68EEF65A9CB035E9FD53F65C275353D1568A375560991C5F1B64BE2F3272112FC2B4E74B76E8F3363699744EC2688C1653C54B7EB99D56C74CE063BD659273358500CB1841EC7F42A566B3F9B359F83FDCC73EB1C2F9GA0F4A66B630DFA66B3F3G97BB1375
	D15240FCA6FB966BC3ECA05FCA20EC8798B70B1DF5ECEE91382E3F5AB552B5F7F47ED6FDDA60F167064640316F164661E35F14C6F2BF95E296A65A949756083EA9F5F438C59EE327B7E6F2DC1D1B1D16D0BD734B587992751D9A92634240A69133186FACAA53773365246DE2E837946DC8F09BB6921DE7C725264FE63823A49E16C239E6B64B5F3AD3071B8B5A73249A4D19B80E25BC1B7E5D20FAF6F5101C650F81FC70EC967B7E27A930FDD7B68DEF5FC4137E5A3246B4F8EDC9B7692FAD9BCC5A35C5392BE0C2
	5DBAA1763672F6599C778D476DA6FDA26EEB1BD56E62780F09DC08BFA1A2C3067F144855C7F02D8A6E0886770D02ABB1210F004ADD120B7F3D837D0EE1335BE939023431B940E538BD0E9399EDCE33C66EFA0EC339E90EF4DD6FF0E7B173E76B5DBD1AB4AC5DCE3F32F2A6C668164E9E59DEA98E5A51B32CD6324E2C1A6DFF36E5A67834FB7D4F367E3B024518CDD0673D176D695E2C3C4AB3392A2B23AB9BB51DF26ECF564A65777A6B9BC370F1037435D5B1FD93202E75BE266F32F33AFB587B287D175C27BF8E
	3100DF69D7179E817B6B88BDDF78CB099E0FA2256D0988FD9EE7816F742B9B876529FEB82ADF0E072F99D09F9B41560C9E8FDFB36EE7EB46DD24ED8B146DG636E9FED1EF1718C1668FB445778CEE3EB2DBC54D9DB29F278C4E36115627D02A6776A36F7661F37E0361F5FE1936DD63C440BEBFAAD96D8CE536CE1E761E177B37D12BD6C1E42527B59185EFD696A666C034352694F59D8C2177E4048662C6181B6E73FA56BFC51837EBEEC2177A29E70776178BAB646C6281B77G53772D7A2B1BFB498DD7C7D7BE
	EABA2A571FC82E3269C1FFFDDFA4F843037E7AF6B7703B8BD09772A04BEDBC6D2A4F4DAE43FBC3FB70E210C16AB0A8D7034A76B07F1CEAD15BBE37E1A87FACA804A428181D857F522A527D3DE964F4E11DAA5D0BA9A3BDC326347F5A254E2B57ECEABF3FDDBF927E747E64420D185F5B9CF61BC32C26FF21AB30334197483EA5816C18BE075FEB29C01C814AC5F318BFFDDFA970316E543C2F283876E792EBAB2FBD0F672BD41DDE563CCBD5571EF750B565C147749D32772B0071B471F14E31A06EAC14E50EB311
	1CB7884A33B86EF3F297ECBE1497F35C54CA365FDF4EF18B3348774307A017FD08615611B300D6A8F7F11C0B60E4A887F3DCB361918E65859C37096CCD17C0B90663422EB03E899C7764E9F2877D612854338FB3FF7D44F3F5F16C58D55239C6CDC7635CFAF28E61FC58BF7EE5917C40437E71EFBADF530320AE1E6B5B4A791B667A2EB9D6A8FB8102676A2DEFE9BEF207ABEDA2156762390357AF65EC076CDD17C3FD42DCB68E0EBAB2B67322D23367B1395A6A854E4B26F97FEB5D351FD79F7C68BB8E4373F821
	C155A9CE73FE21615A733E711A72206B4C5FFAB1371271448B57A9016DC7B37A48998C0CF351BC360F9EDB311A3595581A1559AD703EBEEC733D6BD1E8D76B4AAD7AFB709692232C40434979F416853E43B6066E6F21ED84EFFF3A5F77FBCBB709B5CB21BEE69E5B2F4F2D9C4D77962F666E924E454CC722D23F9334E7D74FE5695FE1FB034448ECE81F7B8813EFDF292FDD4E7428F619D82AEF1769A40636C09FED3C1F737D016DB216689C8CED2772761F14785A2512445BC5D03F6491E617472E0C669C435FAE
	42237EF67973E9FD3B7C0D4470F4E81F75A80B9D1BFB557934D373FE1F675A4F2D055714870D1792592754003EED0F3298DACA4EB6E5A887F3DC04172DF9610F7AFE2BDD8165344798EE9769AF9B4AF99C3734052C7BD0B6F15C31532C3F330FB1DF58B020BF4F8F8E10BB1750EEBEEFBB3EEE70BC6FF7887C7DFF1D2A7F36D5607F2E5249F7ABDD1070C23AFBD8C9F7955AEDAE1576F859907796CACAE90A2B50FD66D6698A7BC7CEF76A22CA97DCB93A5CDC3E246ABCE5C06D6756121173FF7C32CAF7E294F2BF
	FFC525DBBB8AFEB509AADD481551695B19246AFBFB325A4F6F3CA361CF5BCE70840ED177907F0D83BFCB603E56D4C84EA84D166ECE1764F1D8D95BCE627F06C87075C8962B7E55BBD42CDAA3685F5FE3314A98C9634E206F8F03E2957D7E500AFCA3191C459E5FD8EAA43A04C0FD98EF7317965FB8BF9F0B4A379353692B39FB48E40F0ED473C45A772EB56A7834570DEE8E124FD08A6D2705C30C6DD1BC3ED13F4BC72FD56942497F8B60B45512DDF26577F559E41136FD63FD5517B6242BF4F36511736B4FD069
	96370C4E17437068DC207F1B6F256371B050F3DE93D63BDD420641FCABEF7953F3F34E55ECCA92126A0454AEF256DCA72C6E43C3EBAF463A8E30FC1E7B02C066A47D1105943CECA18B2974BE852F454FE8D7004FBA9FB9FFB37D684B51FFBE63CE96D26B040F500D1AC597399DA0DA71AFA115FFF9D5AB2CA57F938F1D31CEC8F11347EA375CEF970D42260E2F70DF3538E551DA493E073BE5E13391B99D42CF1707CE185DDDB2FE837827D1C8F160BF6792B650EA3B5951F908DEC278A751A931320D48CBEC8C72
	EEA472E2AF793C170D3FDC1F333BBEF6FE3210D4AB3CEF174C56DA33C516DCF542DA394BA53A3BA43B556B7C2202F41456477E72B8993A5B4E55C7E573711AAB973A5BFBF7703A38F53590EE3CAE6190E90933A61F3730D6F719DD40CAF45549784F46A457BEDEF75DCEF97F6F3F5CFAB6C9C8D414C45327F91DB0969D669E1B3D9FDE28D710C1C917C1DA17596146FFBA8112997CA4E3D51CC17EEDCF955CFE7FBB5F9286AA29AC6C22268452C31D2254F3283223436690F987E0D768FE876F9E5B647B3559601D
	3C397E59775A418FEA04641E0282727BB079FD84FF9FA6184384F3788898F705B0640FFF3D1769CC1C8764D9CF6441614F2F0591147F633527E67CE7790AF8502ED6D8CD7ED9848CC2265A2A515353AE3AA64C39FD478721B1B76198B951D53C58EF261777089A56114FABB13D05E549AF4DCCCE92D2E07A504FCD603C784D881C57C5E7D332ACB9095F3114927FDBE6224F2C79161470C8ABC9E913C3FC13E153B558A402CDA5FECC5DAF1D37FDFF30C3A2BA8367AAF18BACFDC81AEEA43F370C5A48C815C5E837
	A1FBD25E67651226AA72A4F119FED212FE62ABE93272D4AD3DC641105A1F1E7C052A7CCC76CCAE698E0512491F6263E311321069FFEE4C9BE0A71DBAA52AFC9B41E60CC4F96BB5D03ECDA9FF619DB4CC18E62C7615DB9656A8F7D8E18540854620948D78293DCC96BEE4E5F68F96F0694A558852C6D77344G1A27788EBB0BC129743BAD55GE92E0DC99A3CBE4AB76A14FED2C3F846F77EE1D6C59240E13578896B54266A1429514957AD9B519969659C833B4661AE7BB11E06A5A37EEA3CB6EA59D457427B96C7
	E0794B70CDFA93E7EF9D1BB9295013A42455E282E2F7F6196B140555A8FC50CE56BF5185EDA1742889842105ACAD4982A95CF695EFC1BC2DEDF61AAD18482D351ADD5D248794C57ACED85B616AECA78CE4A1693B5A6F00ADA9E1758523703E5C055794B12312052DEA57A63CA027D4509CD0F88FFE1C64070F9EDE37E4D1B543CF792B723AEB31923061712ADE37C1A3FBA67DDB0B83BFE87FE6EA65782C00FFCBBD700472FFE61E707F1B29AD647ED5B460631EE077F2BA6ABF95825F97EE866F364BE6FBF72543
	0AF7072B7AE5D13D2F3A3D817BF733B6CAA52D5B5300F21F57507C9FD0CB8788F1A7AF98EA9EGG38DCGGD0CB818294G94G88G88G7DF854ACF1A7AF98EA9EGG38DCGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG249EGGGG
**end of data**/
}
}
