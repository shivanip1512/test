package com.cannontech.dbeditor.wizard.capsubbus;

import com.cannontech.database.db.device.*;
import com.cannontech.database.data.*;
import com.cannontech.database.data.device.*;

/**
 * This type was created in VisualAge.
 */
 import java.awt.Dimension;
 import com.cannontech.database.db.*;
 import com.cannontech.database.data.device.*;

 import com.cannontech.common.gui.util.DataInputPanel;
 
public class CCSubstationBusPeakSettingsPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
	private javax.swing.JLabel ivjOffPeakSetPointLabel = null;
	private javax.swing.JTextField ivjOffPeakSetPointTextField = null;
	private javax.swing.JLabel ivjPeakSetPointLabel = null;
	private javax.swing.JTextField ivjPeakSetPointTextField = null;
	private javax.swing.JLabel ivjPeakStartTimeLabel = null;
	private javax.swing.JLabel ivjPeakStopTimeLabel = null;
	private javax.swing.JLabel ivjJLabelFormatTime = null;
	private javax.swing.JLabel ivjJLabelFormatTime2 = null;
	private javax.swing.JLabel ivjBandwidthLabel = null;
	private javax.swing.JTextField ivjBandwidthTextField = null;
	private javax.swing.JPanel ivjCapControlStrategySettingsPanel = null;
	private com.cannontech.common.gui.util.JTextFieldTimeEntry ivjJTextFieldStartTime = null;
	private com.cannontech.common.gui.util.JTextFieldTimeEntry ivjJTextFieldStopTime = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public CCSubstationBusPeakSettingsPanel() {
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
	if (e.getSource() == getJTextFieldStartTime()) 
		connEtoC3(e);
	if (e.getSource() == getJTextFieldStopTime()) 
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
	if (e.getSource() == getPeakSetPointTextField()) 
		connEtoC1(e);
	if (e.getSource() == getOffPeakSetPointTextField()) 
		connEtoC2(e);
	if (e.getSource() == getBandwidthTextField()) 
		connEtoC6(e);
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
 * connEtoC2:  (DistrictNameTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> CapControlStrategyNamePanel.eitherTextField_CaretUpdate(Ljavax.swing.event.CaretEvent;)V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC3:  (JTextFieldStartTime.action.actionPerformed(java.awt.event.ActionEvent) --> CapControlStrategyPeakSettingsPanel.fireInputUpdate()V)
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
 * connEtoC4:  (JTextFieldStopTime.action.actionPerformed(java.awt.event.ActionEvent) --> CapControlStrategyPeakSettingsPanel.fireInputUpdate()V)
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
 * connEtoC6:  (BandwidthTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> CapControlStrategyPeakSettingsPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(javax.swing.event.CaretEvent arg1) {
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
 * Return the BandwidthLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getBandwidthLabel() {
	if (ivjBandwidthLabel == null) {
		try {
			ivjBandwidthLabel = new javax.swing.JLabel();
			ivjBandwidthLabel.setName("BandwidthLabel");
			ivjBandwidthLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjBandwidthLabel.setText("Bandwidth:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBandwidthLabel;
}
/**
 * Return the BandwidthTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getBandwidthTextField() {
	if (ivjBandwidthTextField == null) {
		try {
			ivjBandwidthTextField = new javax.swing.JTextField();
			ivjBandwidthTextField.setName("BandwidthTextField");
			ivjBandwidthTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjBandwidthTextField.setColumns(6);
			// user code begin {1}
			
			ivjBandwidthTextField.setDocument( new com.cannontech.common.gui.unchanging.LongRangeDocument(0, 99999999) );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBandwidthTextField;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getCapControlStrategySettingsPanel() {
	if (ivjCapControlStrategySettingsPanel == null) {
		try {
			ivjCapControlStrategySettingsPanel = new javax.swing.JPanel();
			ivjCapControlStrategySettingsPanel.setName("CapControlStrategySettingsPanel");
			ivjCapControlStrategySettingsPanel.setPreferredSize(new java.awt.Dimension(196, 100));
			ivjCapControlStrategySettingsPanel.setLayout(new java.awt.GridBagLayout());
			ivjCapControlStrategySettingsPanel.setMinimumSize(new java.awt.Dimension(196, 100));

			java.awt.GridBagConstraints constraintsPeakSetPointLabel = new java.awt.GridBagConstraints();
			constraintsPeakSetPointLabel.gridx = 1; constraintsPeakSetPointLabel.gridy = 1;
			constraintsPeakSetPointLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPeakSetPointLabel.ipadx = 51;
			constraintsPeakSetPointLabel.insets = new java.awt.Insets(11, 16, 6, 5);
			getCapControlStrategySettingsPanel().add(getPeakSetPointLabel(), constraintsPeakSetPointLabel);

			java.awt.GridBagConstraints constraintsPeakSetPointTextField = new java.awt.GridBagConstraints();
			constraintsPeakSetPointTextField.gridx = 2; constraintsPeakSetPointTextField.gridy = 1;
			constraintsPeakSetPointTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPeakSetPointTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPeakSetPointTextField.weightx = 1.0;
			constraintsPeakSetPointTextField.ipadx = 115;
			constraintsPeakSetPointTextField.insets = new java.awt.Insets(9, 5, 4, 4);
			getCapControlStrategySettingsPanel().add(getPeakSetPointTextField(), constraintsPeakSetPointTextField);

			java.awt.GridBagConstraints constraintsOffPeakSetPointLabel = new java.awt.GridBagConstraints();
			constraintsOffPeakSetPointLabel.gridx = 1; constraintsOffPeakSetPointLabel.gridy = 2;
			constraintsOffPeakSetPointLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsOffPeakSetPointLabel.insets = new java.awt.Insets(7, 16, 6, 33);
			getCapControlStrategySettingsPanel().add(getOffPeakSetPointLabel(), constraintsOffPeakSetPointLabel);

			java.awt.GridBagConstraints constraintsOffPeakSetPointTextField = new java.awt.GridBagConstraints();
			constraintsOffPeakSetPointTextField.gridx = 2; constraintsOffPeakSetPointTextField.gridy = 2;
			constraintsOffPeakSetPointTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsOffPeakSetPointTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsOffPeakSetPointTextField.weightx = 1.0;
			constraintsOffPeakSetPointTextField.ipadx = 115;
			constraintsOffPeakSetPointTextField.insets = new java.awt.Insets(5, 5, 4, 4);
			getCapControlStrategySettingsPanel().add(getOffPeakSetPointTextField(), constraintsOffPeakSetPointTextField);

			java.awt.GridBagConstraints constraintsPeakStartTimeLabel = new java.awt.GridBagConstraints();
			constraintsPeakStartTimeLabel.gridx = 1; constraintsPeakStartTimeLabel.gridy = 3;
			constraintsPeakStartTimeLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPeakStartTimeLabel.insets = new java.awt.Insets(7, 16, 6, 48);
			getCapControlStrategySettingsPanel().add(getPeakStartTimeLabel(), constraintsPeakStartTimeLabel);

			java.awt.GridBagConstraints constraintsPeakStopTimeLabel = new java.awt.GridBagConstraints();
			constraintsPeakStopTimeLabel.gridx = 1; constraintsPeakStopTimeLabel.gridy = 4;
			constraintsPeakStopTimeLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPeakStopTimeLabel.insets = new java.awt.Insets(7, 16, 3, 49);
			getCapControlStrategySettingsPanel().add(getPeakStopTimeLabel(), constraintsPeakStopTimeLabel);

			java.awt.GridBagConstraints constraintsJLabelFormatTime = new java.awt.GridBagConstraints();
			constraintsJLabelFormatTime.gridx = 3; constraintsJLabelFormatTime.gridy = 3;
			constraintsJLabelFormatTime.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelFormatTime.ipadx = 3;
			constraintsJLabelFormatTime.insets = new java.awt.Insets(11, 4, 5, 7);
			getCapControlStrategySettingsPanel().add(getJLabelFormatTime(), constraintsJLabelFormatTime);

			java.awt.GridBagConstraints constraintsJLabelFormatTime2 = new java.awt.GridBagConstraints();
			constraintsJLabelFormatTime2.gridx = 3; constraintsJLabelFormatTime2.gridy = 4;
			constraintsJLabelFormatTime2.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelFormatTime2.ipadx = 3;
			constraintsJLabelFormatTime2.insets = new java.awt.Insets(9, 4, 4, 7);
			getCapControlStrategySettingsPanel().add(getJLabelFormatTime2(), constraintsJLabelFormatTime2);

			java.awt.GridBagConstraints constraintsBandwidthLabel = new java.awt.GridBagConstraints();
			constraintsBandwidthLabel.gridx = 1; constraintsBandwidthLabel.gridy = 5;
			constraintsBandwidthLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsBandwidthLabel.ipadx = 63;
			constraintsBandwidthLabel.insets = new java.awt.Insets(6, 16, 46, 21);
			getCapControlStrategySettingsPanel().add(getBandwidthLabel(), constraintsBandwidthLabel);

			java.awt.GridBagConstraints constraintsBandwidthTextField = new java.awt.GridBagConstraints();
			constraintsBandwidthTextField.gridx = 2; constraintsBandwidthTextField.gridy = 5;
			constraintsBandwidthTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsBandwidthTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsBandwidthTextField.weightx = 1.0;
			constraintsBandwidthTextField.ipadx = 115;
			constraintsBandwidthTextField.insets = new java.awt.Insets(2, 5, 46, 4);
			getCapControlStrategySettingsPanel().add(getBandwidthTextField(), constraintsBandwidthTextField);

			java.awt.GridBagConstraints constraintsJTextFieldStartTime = new java.awt.GridBagConstraints();
			constraintsJTextFieldStartTime.gridx = 2; constraintsJTextFieldStartTime.gridy = 3;
			constraintsJTextFieldStartTime.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldStartTime.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldStartTime.weightx = 1.0;
			constraintsJTextFieldStartTime.ipadx = 115;
			constraintsJTextFieldStartTime.ipady = 3;
			constraintsJTextFieldStartTime.insets = new java.awt.Insets(5, 5, 4, 4);
			getCapControlStrategySettingsPanel().add(getJTextFieldStartTime(), constraintsJTextFieldStartTime);

			java.awt.GridBagConstraints constraintsJTextFieldStopTime = new java.awt.GridBagConstraints();
			constraintsJTextFieldStopTime.gridx = 2; constraintsJTextFieldStopTime.gridy = 4;
			constraintsJTextFieldStopTime.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldStopTime.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldStopTime.weightx = 1.0;
			constraintsJTextFieldStopTime.ipadx = 115;
			constraintsJTextFieldStopTime.ipady = 3;
			constraintsJTextFieldStopTime.insets = new java.awt.Insets(5, 5, 1, 4);
			getCapControlStrategySettingsPanel().add(getJTextFieldStopTime(), constraintsJTextFieldStopTime);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCapControlStrategySettingsPanel;
}
/**
 * Return the JLabelFormatTime property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelFormatTime() {
	if (ivjJLabelFormatTime == null) {
		try {
			ivjJLabelFormatTime = new javax.swing.JLabel();
			ivjJLabelFormatTime.setName("JLabelFormatTime");
			ivjJLabelFormatTime.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelFormatTime.setText("HH:MM");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelFormatTime;
}
/**
 * Return the JLabelFormatTime2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelFormatTime2() {
	if (ivjJLabelFormatTime2 == null) {
		try {
			ivjJLabelFormatTime2 = new javax.swing.JLabel();
			ivjJLabelFormatTime2.setName("JLabelFormatTime2");
			ivjJLabelFormatTime2.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelFormatTime2.setText("HH:MM");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelFormatTime2;
}
/**
 * Return the JTextFieldStartTime property value.
 * @return com.cannontech.common.gui.util.JTextFieldTimeEntry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.JTextFieldTimeEntry getJTextFieldStartTime() {
	if (ivjJTextFieldStartTime == null) {
		try {
			ivjJTextFieldStartTime = new com.cannontech.common.gui.util.JTextFieldTimeEntry();
			ivjJTextFieldStartTime.setName("JTextFieldStartTime");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldStartTime;
}
/**
 * Return the JTextFieldStopTime property value.
 * @return com.cannontech.common.gui.util.JTextFieldTimeEntry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.JTextFieldTimeEntry getJTextFieldStopTime() {
	if (ivjJTextFieldStopTime == null) {
		try {
			ivjJTextFieldStopTime = new com.cannontech.common.gui.util.JTextFieldTimeEntry();
			ivjJTextFieldStopTime.setName("JTextFieldStopTime");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldStopTime;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getMinimumSize() {
	return getPreferredSize();
}
/**
 * Return the OffPeakSetPointLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getOffPeakSetPointLabel() {
	if (ivjOffPeakSetPointLabel == null) {
		try {
			ivjOffPeakSetPointLabel = new javax.swing.JLabel();
			ivjOffPeakSetPointLabel.setName("OffPeakSetPointLabel");
			ivjOffPeakSetPointLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjOffPeakSetPointLabel.setText("Off Peak Set Point:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOffPeakSetPointLabel;
}
/**
 * Return the OffPeakSetPointTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getOffPeakSetPointTextField() {
	if (ivjOffPeakSetPointTextField == null) {
		try {
			ivjOffPeakSetPointTextField = new javax.swing.JTextField();
			ivjOffPeakSetPointTextField.setName("OffPeakSetPointTextField");
			ivjOffPeakSetPointTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjOffPeakSetPointTextField.setColumns(6);
			// user code begin {1}
			
			ivjOffPeakSetPointTextField.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument( -999999.999, 999999.999, 3) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOffPeakSetPointTextField;
}
/**
 * Return the NameLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPeakSetPointLabel() {
	if (ivjPeakSetPointLabel == null) {
		try {
			ivjPeakSetPointLabel = new javax.swing.JLabel();
			ivjPeakSetPointLabel.setName("PeakSetPointLabel");
			ivjPeakSetPointLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPeakSetPointLabel.setText("Peak Set Point:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPeakSetPointLabel;
}
/**
 * Return the NameTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getPeakSetPointTextField() {
	if (ivjPeakSetPointTextField == null) {
		try {
			ivjPeakSetPointTextField = new javax.swing.JTextField();
			ivjPeakSetPointTextField.setName("PeakSetPointTextField");
			ivjPeakSetPointTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjPeakSetPointTextField.setColumns(6);
			// user code begin {1}

			ivjPeakSetPointTextField.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument( -999999.999, 999999.999, 3) );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPeakSetPointTextField;
}
/**
 * Return the PeakStartTimeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPeakStartTimeLabel() {
	if (ivjPeakStartTimeLabel == null) {
		try {
			ivjPeakStartTimeLabel = new javax.swing.JLabel();
			ivjPeakStartTimeLabel.setName("PeakStartTimeLabel");
			ivjPeakStartTimeLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPeakStartTimeLabel.setText("Peak Start Time:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPeakStartTimeLabel;
}
/**
 * Return the PeakStopTimeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPeakStopTimeLabel() {
	if (ivjPeakStopTimeLabel == null) {
		try {
			ivjPeakStopTimeLabel = new javax.swing.JLabel();
			ivjPeakStopTimeLabel.setName("PeakStopTimeLabel");
			ivjPeakStopTimeLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPeakStopTimeLabel.setText("Peak Stop Time:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPeakStopTimeLabel;
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
	com.cannontech.database.data.capcontrol.CapControlSubBus subBus = ((com.cannontech.database.data.capcontrol.CapControlSubBus) val);

	try
	{
		subBus.getCapControlSubstationBus().setPeakSetPoint( new Double(getPeakSetPointTextField().getText()) );
	}
	catch(NumberFormatException nfe)
	{
		nfe.printStackTrace();
		subBus.getCapControlSubstationBus().setPeakSetPoint( new Double(0.0) );
	}

	try
	{
		subBus.getCapControlSubstationBus().setOffPeakSetPoint( new Double(getOffPeakSetPointTextField().getText()) );
	}
	catch(NumberFormatException nfe)
	{
		nfe.printStackTrace();
		subBus.getCapControlSubstationBus().setOffPeakSetPoint( new Double(0.0) );
	}

	try
	{
		subBus.getCapControlSubstationBus().setBandwidth( new Integer(getBandwidthTextField().getText()) );
	}
	catch(NumberFormatException nfe)
	{
		nfe.printStackTrace();
		subBus.getCapControlSubstationBus().setBandwidth( new Integer(0) );
	}
	
	subBus.getCapControlSubstationBus().setPeakStartTime( getJTextFieldStartTime().getTimeTotalSeconds() );
	
	subBus.getCapControlSubstationBus().setPeakStopTime( getJTextFieldStopTime().getTimeTotalSeconds() );

	return val;
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
	// user code end
	getPeakSetPointTextField().addCaretListener(this);
	getOffPeakSetPointTextField().addCaretListener(this);
	getBandwidthTextField().addCaretListener(this);
	getJTextFieldStartTime().addActionListener(this);
	getJTextFieldStopTime().addActionListener(this);
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
		add(getCapControlStrategySettingsPanel(), getCapControlStrategySettingsPanel().getName());
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
public boolean isInputValid() 
{
	try
	{
		if( getPeakSetPointTextField().getText() == null   ||
			getPeakSetPointTextField().getText().length() < 1 )
		{
			setErrorString("The Peak Set Point text field must be filled in");
			return false;
		}
		
		new Double( getPeakSetPointTextField().getText() );
	}
	catch(NumberFormatException nfe)
	{
		setErrorString("The Peak Set Point text field can only be a number");
		return false;
	}

	try
	{
		if( getBandwidthTextField().getText() == null   ||
			getBandwidthTextField().getText().length() < 1 )
			return false;
		new Double( getBandwidthTextField().getText() );
	}
	catch(NumberFormatException nfe)
	{
		return false;
	}
	
	try
	{
		if( getOffPeakSetPointTextField().getText() == null   ||
			getOffPeakSetPointTextField().getText().length() < 1 )
			return false;
		new Double( getOffPeakSetPointTextField().getText() );
	}
	catch(NumberFormatException nfe)
	{
		return false;
	}

/*	try
	{
		if( getPeakStartTimeTextField().getText().length() == 5 &&
			getPeakStopTimeTextField().getText().length() == 5 )
		{

			java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("HH:mm");
			df.setLenient( false );
			df.parse( getPeakStartTimeTextField().getText() );
			df.parse( getPeakStopTimeTextField().getText() );
		}
		else
			return false;
		
	}
	catch(Exception ex)
	{
		return false;
	}

	
	if( getPeakStartTimeTextField().getText() == null   ||
			getPeakStartTimeTextField().getText().length() < 1 )
		return false;

	if( getPeakStopTimeTextField().getText() == null   ||
			getPeakStopTimeTextField().getText().length() < 1 )
		return false;
*/
	return true;
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val ) {
	return;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G60F854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8BD4D5571546108493E2B55104A424C5EB336C04CCEDC3D7DC2D6DB8B32F0DB6B649CCCD53CCED06F6B2D3D6976D5854B5250D2BBD7C9E98B1A21F78D08C28E850B088C660797888BEB4E2A0D2C14454D6AE3C8BBCF9BF5F3B404398193D4F39675C7BFE20F6DCAB2E3539776CFD76671C334FBE7B1CF31F2430B4BEABAD3D101034AC227E2BCFA3246D94A1CFDF7D3BE40EDBAA9B0CC477EFB740B6
	525B11894F65C0ABFC4AE04CA36F5665C0F98D14D3979B0C5B613D00D45615B541CB84BD290D04B85FE8EC18DECFDDA15713CD4B2F1AF3617996400B0043E749906A7F9F33C945EFD0719C525289499B46BA5B0E162838FE6071832C81C89AE275D3613909EA3E4BE2D66BDD1C9FCFB6FC6F5D74ADA8C7A713C35FB0633D26BE4B48D7524AC7D01765E82E4A13883494GECFC8671641F04E7E35768F073205BD9B5A839DCEE17A28F0ED4CD582FCADE9BE0BC3E310101B11F45E235F60F8D7894C9313BDD4747FC
	DD32B45AADAB0A5DB56C6B12DC320310F82AEB76519CAA1B1091A8D75A91774191547F9A14B782945AB97E2DDA44FB60BD9AA05E4E5B5C74EEA3EF53A7DFA19BDFF6E5EC0D506665F65656A43B566685E33F4FAE2663B3DE0463D0F259E0EC81688170834482AC8758C97D6253420370EC9DD02CEE27536DEA75F80EBBEC5ECE37CDF6403B45829495F7D69EF4FBED043031EFB9DA99461E690071FDF453FC2CCF163E4F7DA0616C93E45D539F26E7076913F5CB2B53DF3C4C7AA2793256A7CC77BD91F5670C867A
	E9206E73C42FBB65EEF7C6D6985D09AF9DC8AFD165D40FEA3A7BC7F12C3FC9FD208B5E53D47FE0786FD07C929D1E79ECA745E3B991E8A687770D3E3738AD9D190F123C1A0E307D907BCF2D69579C4C06850EE0DBE6D33FDB69E0BAB78BDB66D1FC128E4FECA924F8AC932741E883E07EB5359D6770AAA83F8630912050E5B09E8168823039380FFD3770508D7AD81764D50E0F599DB659CB986FD4515B70147D0AD7EAF7417CF38D4A3D1257AE8DB8E49FD16BC497E19F8C84705DCFFDFF90F4FCA03BE4AF1D37A0
	DDF24A0A6C7531BA8F95ED43BADE19D2FB6CCE9994B8BD044B7DFCD181BC9D12CFB96731C90A2C55E074A70AF0CC4625DE108882601D75CB02897BAB8A5A9F8710A07AE1775BA86FC3598B7142E269F03BC747BCBD58923251057D7CF99ABBD6427B0FDC3C0F1FAEC6DC0B5BE06CF3F3B9FDEF676B6434598751FC49BB496758BF9E41B86DF0F31F79D59F771936ADDFA215AF1D8E6BB388735C4CD7163A03674EF3D4A65A454A5DC77AE618378EF778B9F5633142B1230E0CDB147F16982B5DBBF12CDE84DBD3G
	F2BD3C0F9FFC3B0C796CF149A7F3AFBD63858713EDDA0FB3BB23EB8B557E4CD0678962B709F6BCC46965A03B83C0810883B0F8F8DF6E286D53713F25635F7CB734AF30AF5F27BAD0FE9651E29B3775EF55417AEC4EF6DC2764F1592534499E59E503971FCA1B379D752973F848ADF89E22F88F4CFBABCCB41F4047ED4739A9A90A57BEB02648FC2D9574A5D4972C4E9EADE4F0DDCFEE47B63AA7E05E0B7A4B36631C99F43BDC72A0DB43D596157EB5BAE7E5076C84232D6EB11742704BCD68772372249AB3D618B0
	B19A179CE398FFD89E31CC42FE74F86DCEF0898CE1246B8A4E672F9FB5697AA885469B7321F220192F7039B931174F4D722B0B484ACB7B52432DAD31DFEACB0F3D4266D34295EDEE2AB69607377D5BB4F628F89F33754CC10C27ED7238FDD07E8822E12B4D6615FDBC07E1F362639214378674A48124DE61F342328369BC6E2813416AF6B8D8170A3EFEF7E79E0E0DFCE5CC06F00D816EC351FFD5BB7303E83DEADFB2FA5DCE6CCB07AEB6AE71E2DFBED74B7345BCA8D7G1C72327E0C72327EE075665671FA6B21
	1C630BD46FF8BD2FD781F5DAG7AFD616AE519F93DB820A7822C8DDBEF3528D728C0FE270456E37D1FFD89F310F0B96770183DEACC31E3C4179418A41EB1050E8A4BABCBADB92A0C8E05670F9FB651F5864AF1G098A7729FF59427347852371A4690BBBA2664CEB94E65BFAC5732932BBF97E38E4976A2C9E037E9963BE70551D7AB52C5567F38F5A6972290EDFFF095E6798CEAEF9931EC3F62FCFE907206067E3BDDA0276627A2A2255FCE3834DB7302DBD20B78620FC9C714F1A8AF5369BG1F821087348CAA
	6F4FF518E78CCADED92922FF5932DDF1D99A177CD53E8988A0D5B4BAD5D911FA82DF8F2863E2B8104FEC14E94F2B1C87FAF98E3838200577595FBFCA9A278A5243656451DBF725BB46553CEBDC6B53EBDFB3E6247C750DEC563EE76AF87B12200EE902374FD4972EFDDDC827FC23751C4F8638B84177435EF0FCEBA7F83F7CEB7D2D748B53D9E566BA13C1DE191F6B6CB46BF57679194ED8BF573950FC981E928D8CD56C51A5FB075CDE27EC230A2B2489C5555ACA492ADAD56F6BC2EF8248FCCD6815464369AD19
	64EBE939A5B8472F1E5472103649501C04653E7FC067D25FE4F0FE3E1A62C7A6037373BFB5717C5C8F34F513BCDFEBAE45185FF255E03C8820G44GA4G2C394A6750EFCA5EA7B364436D90933CAE49519D30402973664B3BD0AF2CC9EA7929DD58CFF4CDA2FC3D7FFDEDF0BFA4DF8DECFB66A701E54617D3974CD795D42E8862E373E7DFC5890FEBBA3732D8CE77401AFF52AEBBECECCDA44D7AB5316593166B5A606987E0316E8F269C551EF91F70187669C9FDCCDB3175F07A6688B1ED15EADF72A75A7C2B94
	FB6264B23433621A415886F091408F9087309CE05DB5BEC6756FB44DB4C654E1FB5CD6401D02754A9A1CDB08BE7DD6C81F5E58D8BCB763D8EC3C9648477A6FCC9D6F3F42CF793C38D2FFEBB6CC0570855B5071E9B89B361C67B658048DC74C37E64371903EE09B62BE8D671387F755101956DA4DBFF1F7F7422560B6CAEDC314191F65A40AB6245EE29B0CA1E78F41EDC8895B0F31635C064C6B3047267B44861A8BB4C239CB60CC0D08D3209CF31D5B7A1DA64CDD1709728B340C7112154FB74D343F3AD550F4FA
	EE1B8E6C93B6D7BFA80D3C6F7828B47C3E2327B47C3E636352ED8C8FB27427911CBED0BA7D3E64F2E938FD49AEEA6F6A3737D259BE1F6C23418393BD1576F3250804591FE00A2E3635005454EDD26B1CD05C569A61673FB9E0A2E1E32FAEFFB4B723FFA07F3A6BBCFFAC32609AD6B885E50036A9BEE69945FADF3DB89568171E20F25494DF7B5CFBF0ED0E05F2A2409A0057GB2E77118CA4AE6DC77CE7827D977DEAF0BBC4E3FAB8BBF4EFFAC435C57679E738E4A41E3E95CED92B4ADFA37AB32933B1D55593A9B
	799549BBAC0778425B141F51A271176E0E64AFECCCB7840CA966CCEC8CFA82E2FF792C40BEB78715BBE6713CEBE93DDEDE1D1017FFDEAF2FBF08DF89AAC78BF923E63D3C77053CB9A1F1B0A09E4F8A4D33183CC3C775720E88F90F0FCFA7EFD9C4F9E7AD389FF7C8136EB185F23DBB502F6D961EE39800EF9DC0A660CDF730DC034D192FCEE07EB064151CB09C5D9EF910644EC65EC30D1C578F75E3G92G56G245C417B64FB6F154253AAF9E050952F5B518DCE274843130197822CDDCD4D3CDDBD33D99BC6E6
	EBED39866F73E687360D651DAF513939E4B64BBB0B857E97941F244333FCF4A745E33964CE03B179CE3EAE6C20E7G09D18623A10A632AC779DA71A301AB2EC5DC45DD86E363DDFC3ECE6C09BC5F2E6F89BF5F2276040F2B776E19BE2EBE30A752BCE1760D521C268BEC6B3F0B5BBCEE626DD0846E8F349D86A87FC8608E50DCA4854B8277ECBF674D3B1B63DE2FC7DCBF1495010BB773FA518217B8AE7A6FEE3EB71BEF61F7CC9577000D77F0BFFB61920E09084D386B31D8BE705AED9D2A4B2A6B7CABF4CDC77E
	58FBF8FC4EEE20E713D0CE86A051BC97766F6739F042930F425C7FB06C7EBE2967F03AA91A79D6E334168BAF05DC1869BC5748F57A013EAC9AF1F7B66A77D7686F7353D82E4268DF8F2063186DA5FA7A4F8368E86F3752757462A6BD9D7BE8174A4F5A765CBE5E360D4FADA50E2F9F0DF8F6112A3629E48EFBF6756EA565773E114D4628072EF7BDC0734C616316C1F1D1D00E95B84FB1BE16AB846E879417846554B9FCCD7C31259A1E39774258GF481F8G96GA4822C85D88F10F79F5081BAG9C77F15E7514
	B766BEF6EE15F09FBB17DA834F94005C9860816808E167D0F8460467C70CF737E5252E4D28EF263CE8FD04BAEBD5FC177AC4BB318DE8EBF05DC4350EC3C74B3D815D9CAA553E60E5F5CD8BE84B0D1CEDEF0B005F2A130B4FA255FF3607293BC5FD968779750D3685FD34A0083700ACEA40DCFA88960004987499AB0D338EF87783145FCF6FFDC75BE6101B210375401789FCFBD51A895EFFCE34FB0182D2DD0B6D9DF4BB461CAE9FE1FABF20718E5B68381F2FCF2F56865A3600464EEBC00F3E1F5BF63FE9A65B74
	762D38BF503697C3ECAB2A8B352D320E5B36CE5856549BE85BD9961B011EAAECFB35FFA65BB66BEC2B1C9BE8DB4B5CE05BB619C3EDABB3F35BBA66F23D4F18E7521B2D53FBB1C82FC7550BF5183C6746E71237C9A76FDA103CF9BAF9EC7EBC9CF236B593B03EAF5DA25F2BB74D47F214D7683A95B71765A8E9827FB30ADF2A433335F3CB0378BE83E8264F717D4439F24C452EC1B996E09940AA00E4004CF9BCC729580B31F55ABDC50F5B5381C90CC317F63076FD5CF8EB5FA27CE3B6DF23923F3118AC3F7BF7E9
	114E22CC73D88CB42B4F641E3DA296EDA3497B30FDA3C00B82D88A3092E0ADC00AE81FE7EFA5EB5F891BDDE9D594E9F0041E2C7346755A65898D0D092C1A37B723EFFB3C72106C754A36EE7BD55CC7B25F0F226BA0198FEB4FFCBEAFF7B4076651B5622C4E8C753AG7CGB173D9DB164F47BA779C63F5164C0F14237F6298768FA45AF6671813DA426CC8EF21F6BCGF6BC406D4824F9207AF9CD175B6ED2BA24813C1AD2E54DF197855145810E28F3F6976698E70606C244B07A41E2D3A8DD08E1F52A6C79DC8F
	EC7094BC9CD28DE174619CE12762766848AA7D91A75E011D264813EE2FD322A2986D375D5B42508E8B5E175E413EBBAE39EC93F61BB2922077D76F646BE93A46B37A6696DC2B34D3ADE13DCABF5E199FC4E75643DC24676B158D7A7C4C7CGBBDF3F78GBFFFD7EA4351BD02FE21AE9CBDCA500DE16931027E7D7AF07484C1FF65DCB87ACAC17FC1D87ADAF8A6BF005F34DD42E39335AF760E6E0327CB1EA8939792A7E1E12A586313DCBE1F6C358F6D29AC2B2EAC2BB9F2C04C7D78897D77E866B76B435E57A2D4
	BE4866C30B7ADCF14FDED2CAD8AEC17D1E7EDBFFAC9487F1025A3A9ADDC2557B53DC9EF3CA1EFC0C343D7C3B343D91747AD5FD2B9E54F27E644B9B33D91B7FDA0BBCF5C247D7EA357E301BEE26BF423505497CC91DA653DA97286F7AD10D760D7E7F9FFDBF35E8B26B4DDCDF647A19581553505F4523FFF58D7C77866DBDD977DE50F03B4EB535753772366AE06B4BDF4BA36F71657270FB7C517270FB7CAB65531F0DCE16C77EE6E3D6397E1B8D36BE7E166E95498203318420852087E0E481DF9B5F5A37075035
	B14C3AF842B60CE7FA78451842757FFADF6436266E1B79BB33347DF80FBD60D694379365F3FC76FE9CAF88249449F0397B310F3C76619105D5D571797B69BEC19E22E866EF779E537CB735290C844D09B6884FAE7C402C2266E89D4C066A6A83ACEF3A504C7326A86813958B7819C3CDFF40DD1CF666F0DC9AE6478EEC7F3F28196F7FB7AC60E78EAFD019158B8D46B6000B8B791D5CD3477877874AC2FE8E316AAF7A3DFA59FB19E1776A29FFB9141E301045229797EAB1E9AD44A4567E57DBF46DBFFAA36DD74F
	3D07A21CFB539513596DEA6077D0198F7136BCD94BF10D82B7CF5C19A98257A4F0898217E966385784AE2F1763CA9E6638B7852E67E15EDFAF7CD93FF664BD3ABB1D674141F3B366E156CF318FEB7D456644FF3752EF3B811F7CB01F87CBEA5E03E72FF4D94DFEB1D5F4FB69CCGE417576D113D4AA443522FED5479751E9577A7B93130F6821C826887D0GE2G62819281D681A481248264BEE2B016819C81E8836883F0BCA266637B872631C3F5C0BAA93D129D27A5BF23EF0ABCAF0D4DB34F4B2D540FE518CF
	BD139E755C3D1066336E21A11FAC66FFF1F31E40357B3A5CBEBB1E5733D8FB29795676C84EDB64FB622679589E4AFA8C67672CC70277D69D949F73C8705E6AA591A3E201367191FE2E7435E38177810F4238BFBA7D798E7319EF37611C0B063ACBGD681A481ECG48FD0C7B417347AA2671839601B4B7E076789A747614BD46ECB922BECFBD96EA9B63DB96701D43C5351EC7FDC6C564DB98F03F922B56CBD01FABA37295845C632ED56BA52B4F0D9179B6855C17644531FA656A539C97092F28D74F5721566BD7
	1FCAC43E2D81FC51EA3DB87539AC8C9F9B5F1F1E0007BADFDADB77C59AC7D8E1CFC348BDB3F0D99ED4FA24E141332195E5099C00059B1576C6AB2E67308DD46C1243FE15FEB6568D95DD430CFE4C9AB98E1C344E9C874ED8432F4F672C216B734756706BF33FD53FBE2BF91095571BE108DFFD225EE5013BA0F0EEAA93F167F1EFA670E354AE441F12D1A147FFC26D1A2038BE1E8769F097D45CDDD60CE550649137D75C27D869B91A5D61E06539340E5D8521CD74470354C63BC752599DABF097042F34537C033D
	1F553D7F38797667389BEE2B8EF61E3F1A66AE8670F93C0BE03867A9EE9D6070FE03754D33ED117D6F7936197D6F6536707E771336D07F7BD9DBF87FCBEE537B9F33755E86FE07179A476FA86EBB07385C47A1B7FE1C63FA4F733B0C8A01FB36165F1B7985AE1E5E1B45C0B9CE60320AB96FD201CB2079CFAA14F3BE4FF1F64ADB8265CA01FBDE5C4319856EA07DADC3B414E3852EE0876292203CC2607CBBB86FAA01E3E73326AFC0BB3E40F1FD5769B7AFD06E9038EFDD673CFD82D7594BF9178A1CACF8D7885C
	F7856FAA015B5F4B6F89CD719C671DC2DCA5149B856E65A94EFBAA1E675C527EDCA2E29B1D7E8CDF625F22617974D74F3F1E21639A8BB2164473732F4567F0DEAB60E8ACB7FE3E9D67A3BBCFF61E1F6EBCB9A388B2F5E776387E2F859D436ABB533F017AC69260D3047EE7CC017AFFFDDA53DFD2BC137E2CE97417AC52F293266B1F6B826F82DE217ED681750EAC62764C8A322730C33367BF273D4348A0016775417658C26CE9BD9AE84FF96A439E28F7CD58F3F0C720BD751D1ABD1F5BB113BD1B2631A7A1441E
	FAF320BD1DF4DEAC07FA86E10F7BFA20BD9FFF245933767AF47670FB05C876ECD844F2C54DDF4E5179C196C32E37181F27C51F51747DE4C6FD53353FF1F1303EEE2A6F9460FB9673764E198AEC6F13DD1A7EDFCD4D547F1B27519F85BA2654F7A6FBE7B791E3B1F58E4F9A421CD559E0DE3B07F587D50C37390788B9293D1A1C85261B13935F2749F97212A667AFB7A96779FECD4E41C1CDCE760E1B1353E95364FCF9C81353F17DC6649C96F2568CEBF21E3E2C4939F013F2FEB82A49B96C52648CCD5DDC3B4E61
	7196E13F151EFFEAABAC64893FA4D9BEAF5992407F1DC5357F356655DEDF86B13613E21C1A5D3217E633328D3F66D3FF8AF812EC72602582DEF63413CC9FFDE47914C9075CC18A074E624FDFFC0AECEB65F932CFA1C59D286990FE16FA6814649BD1B037E868A019AE7C51A249E7E407649ABE44126686BF7ED4B12D150EACF5F1827696D0FBD1CA1510D2704D2D7BF61FFEEDF986C9BFC95EF438A55BC9E990D279F632C999714A3E91374366779CEB2102F2A6F278CC81F1BBC5733131D5F8EFA00C9E75BF9E33
	A5F94BC9C82A7CDEF208564419D9A5EA582CA31297D4495EF685FF046976168B5A9D6FA87B3F7F41760B69A4CDED6495F21D247BBD8AB18EC9CE3BE3D2A11B44C9308F09C640C0826672616F88403832A063B8C96828FAF0F57542AF7F7EE32AC3E3D5C8A96BCDE0BDB4AC3B1D07DA07066CAED988002E85713B04F82CD395D4A75FBF3F6874CBFF9CGD7E8A3994E6AEA7A37067EBD0CFFEBA82606E2EA8EC37F2EA7537E8B267BF91B297F00BDDB29BD6881D5A7E19015FF7B656A45BFEEDC1F822DBBC9326877
	7FB08EF9DA2D0F461C8332776E04053BB6AFC83A8F0749035E62C739EF7A05C4ECE1BB5DB9A40D3FEF49780FAFE624134CF6E2E419AC78AF2623603FB4DBEDA7991049D25743C81C25BEB7D1E3C908C3A2B6B35433901D239FDFC4EC263537503FF9B75DDB4815CFF98BC5A3F732BE5C9EE4EC56E75258BD1A31D5BAE35327E982E2538230DE129FFAF05ABA77151793AE35952613F4985691190DE3963DC124A309C15DC0A59E60A505EC455B4D32EED9A92397249605941C999AAA8B4166B18CDEB41661D522C5
	300223F0CC5629D3164ECE6D79263842345C52E098D99C3FBDA3D12B0D846E2A73549E5E7C593334B154D27C3FBA32BFFB16DAC3AD2D7EECDAFADA33B4C33534A648D2E6D76E0DD85AF69BACEDD14BFF52AC6591GB3684D1FBDCB7B8283B1377456D60D5BBA7A178223B00EBEFEF1E2D46F618FCC696FF0D26FB2C53C7F6FFBCA7D06722960BB1C4ECEEC51D2409B1E62E7660FF77CAF09FC565A6D149C8EC5F20C363AECF86EFAFCD21135FB55331DA85F476B28C4C63B5009F6DF56717C9FD0CB8788E3C67816
	8698GGECC8GGD0CB818294G94G88G88G60F854ACE3C678168698GGECC8GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGC098GGGG
**end of data**/
}
}
