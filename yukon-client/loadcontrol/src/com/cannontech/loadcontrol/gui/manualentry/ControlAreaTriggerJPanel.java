package com.cannontech.loadcontrol.gui.manualentry;

/**
 * Insert the type's description here.
 * Creation date: (4/19/2001 3:54:21 PM)
 * @author: 
 */
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.db.device.lm.IlmDefines;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.loadcontrol.data.LMControlAreaTrigger;
import com.cannontech.loadcontrol.datamodels.IProgramTableModel;
import com.cannontech.loadcontrol.messages.LMCommand;

public class ControlAreaTriggerJPanel extends com.cannontech.common.gui.util.ConfirmationJPanel implements java.awt.event.ActionListener, java.util.Observer {
	private LMControlArea lmControlArea = null;
	private javax.swing.JButton ivjJButtonCancel = null;
	private javax.swing.JButton ivjJButtonUpdate = null;
	private javax.swing.JComboBox ivjJComboBoxTrigger1NewThreshold = null;
	private javax.swing.JComboBox ivjJComboBoxTrigger2NewThreshold = null;
	private javax.swing.JLabel ivjJLabelTrigger1NewThreshold = null;
	private javax.swing.JLabel ivjJLabelTrigger1NotPresent = null;
	private javax.swing.JLabel ivjJLabelTrigger2NewThreshold = null;
	private javax.swing.JLabel ivjJLabelTrigger2NotPresent = null;
	private javax.swing.JPanel ivjJPanelTrigger1 = null;
	private javax.swing.JPanel ivjJPanelTrigger2 = null;
	private javax.swing.JLabel ivjJLabelTrigger1NewRestoreOffset = null;
	private javax.swing.JLabel ivjJLabelTrigger2NewRestoreOffset = null;
	private javax.swing.JTextField ivjJTextFieldTrigger1RestoreOffset = null;
	private javax.swing.JTextField ivjJTextFieldTrigger2RestoreOffset = null;
	private javax.swing.JPanel ivjJPanelButtons = null;
/**
 * AckUserPanel constructor comment.
 */
public ControlAreaTriggerJPanel() {
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
	if (e.getSource() == getJButtonCancel()) 
		connEtoC2(e);
	if (e.getSource() == getJButtonUpdate()) 
		connEtoC1(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JButtonUpdate.action.actionPerformed(java.awt.event.ActionEvent) --> ControlAreaTriggerJPanel.jButtonUpdate_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonUpdate_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JButtonCancel.action.actionPerformed(java.awt.event.ActionEvent) --> AckUserPanel.jButtonCancel_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonCancel_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the JButtonCancel property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonCancel() {
	if (ivjJButtonCancel == null) {
		try {
			ivjJButtonCancel = new javax.swing.JButton();
			ivjJButtonCancel.setName("JButtonCancel");
			ivjJButtonCancel.setMnemonic('c');
			ivjJButtonCancel.setText("Cancel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonCancel;
}
/**
 * Return the JButtonUpdate property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonUpdate() {
	if (ivjJButtonUpdate == null) {
		try {
			ivjJButtonUpdate = new javax.swing.JButton();
			ivjJButtonUpdate.setName("JButtonUpdate");
			ivjJButtonUpdate.setMnemonic('u');
			ivjJButtonUpdate.setText("Update");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonUpdate;
}
/**
 * Return the JComboBoxTrigger1NewThreshold property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxTrigger1NewThreshold() {
	if (ivjJComboBoxTrigger1NewThreshold == null) {
		try {
			ivjJComboBoxTrigger1NewThreshold = new javax.swing.JComboBox();
			ivjJComboBoxTrigger1NewThreshold.setName("JComboBoxTrigger1NewThreshold");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxTrigger1NewThreshold;
}
/**
 * Return the JComboBoxTrigger2NewThreshold property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxTrigger2NewThreshold() {
	if (ivjJComboBoxTrigger2NewThreshold == null) {
		try {
			ivjJComboBoxTrigger2NewThreshold = new javax.swing.JComboBox();
			ivjJComboBoxTrigger2NewThreshold.setName("JComboBoxTrigger2NewThreshold");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxTrigger2NewThreshold;
}
/**
 * Return the JLabelTrigger1NewRestoreOffset property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelTrigger1NewRestoreOffset() {
	if (ivjJLabelTrigger1NewRestoreOffset == null) {
		try {
			ivjJLabelTrigger1NewRestoreOffset = new javax.swing.JLabel();
			ivjJLabelTrigger1NewRestoreOffset.setName("JLabelTrigger1NewRestoreOffset");
			ivjJLabelTrigger1NewRestoreOffset.setText("New Restore Offset:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelTrigger1NewRestoreOffset;
}
/**
 * Return the JLabelTrigger1NewThreshold property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelTrigger1NewThreshold() {
	if (ivjJLabelTrigger1NewThreshold == null) {
		try {
			ivjJLabelTrigger1NewThreshold = new javax.swing.JLabel();
			ivjJLabelTrigger1NewThreshold.setName("JLabelTrigger1NewThreshold");
			ivjJLabelTrigger1NewThreshold.setText("New Threshold:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelTrigger1NewThreshold;
}
/**
 * Return the JLabelTrigger1NotPresent property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelTrigger1NotPresent() {
	if (ivjJLabelTrigger1NotPresent == null) {
		try {
			ivjJLabelTrigger1NotPresent = new javax.swing.JLabel();
			ivjJLabelTrigger1NotPresent.setName("JLabelTrigger1NotPresent");
			ivjJLabelTrigger1NotPresent.setText("(No Threshold Trigger Present)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelTrigger1NotPresent;
}
/**
 * Return the JLabelTrigger2NewRestoreOffset property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelTrigger2NewRestoreOffset() {
	if (ivjJLabelTrigger2NewRestoreOffset == null) {
		try {
			ivjJLabelTrigger2NewRestoreOffset = new javax.swing.JLabel();
			ivjJLabelTrigger2NewRestoreOffset.setName("JLabelTrigger2NewRestoreOffset");
			ivjJLabelTrigger2NewRestoreOffset.setText("New Restore Offset:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelTrigger2NewRestoreOffset;
}
/**
 * Return the JLabelTrigger2NewThreshold property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelTrigger2NewThreshold() {
	if (ivjJLabelTrigger2NewThreshold == null) {
		try {
			ivjJLabelTrigger2NewThreshold = new javax.swing.JLabel();
			ivjJLabelTrigger2NewThreshold.setName("JLabelTrigger2NewThreshold");
			ivjJLabelTrigger2NewThreshold.setText("New Threshold:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelTrigger2NewThreshold;
}
/**
 * Return the JLabelTrigger2NotPresent property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelTrigger2NotPresent() {
	if (ivjJLabelTrigger2NotPresent == null) {
		try {
			ivjJLabelTrigger2NotPresent = new javax.swing.JLabel();
			ivjJLabelTrigger2NotPresent.setName("JLabelTrigger2NotPresent");
			ivjJLabelTrigger2NotPresent.setText("(No Threshold Trigger Present)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelTrigger2NotPresent;
}
/**
 * Return the JPanelButtons property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelButtons() {
	if (ivjJPanelButtons == null) {
		try {
			ivjJPanelButtons = new javax.swing.JPanel();
			ivjJPanelButtons.setName("JPanelButtons");
			ivjJPanelButtons.setLayout(new java.awt.FlowLayout());
			getJPanelButtons().add(getJButtonUpdate(), getJButtonUpdate().getName());
			getJPanelButtons().add(getJButtonCancel(), getJButtonCancel().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelButtons;
}
/**
 * Return the JPanelTrigger1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelTrigger1() {
	if (ivjJPanelTrigger1 == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Trigger #1");
			ivjJPanelTrigger1 = new javax.swing.JPanel();
			ivjJPanelTrigger1.setName("JPanelTrigger1");
			ivjJPanelTrigger1.setBorder(ivjLocalBorder);
			ivjJPanelTrigger1.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJComboBoxTrigger1NewThreshold = new java.awt.GridBagConstraints();
			constraintsJComboBoxTrigger1NewThreshold.gridx = 2; constraintsJComboBoxTrigger1NewThreshold.gridy = 1;
			constraintsJComboBoxTrigger1NewThreshold.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxTrigger1NewThreshold.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxTrigger1NewThreshold.weightx = 1.0;
			constraintsJComboBoxTrigger1NewThreshold.ipadx = 28;
			constraintsJComboBoxTrigger1NewThreshold.insets = new java.awt.Insets(5, 1, 2, 12);
			getJPanelTrigger1().add(getJComboBoxTrigger1NewThreshold(), constraintsJComboBoxTrigger1NewThreshold);

			java.awt.GridBagConstraints constraintsJLabelTrigger1NotPresent = new java.awt.GridBagConstraints();
			constraintsJLabelTrigger1NotPresent.gridx = 1; constraintsJLabelTrigger1NotPresent.gridy = 2;
			constraintsJLabelTrigger1NotPresent.gridwidth = 2;
			constraintsJLabelTrigger1NotPresent.ipadx = 5;
			constraintsJLabelTrigger1NotPresent.ipady = 6;
			constraintsJLabelTrigger1NotPresent.insets = new java.awt.Insets(22, 70, 7, 129);
			getJPanelTrigger1().add(getJLabelTrigger1NotPresent(), constraintsJLabelTrigger1NotPresent);

			java.awt.GridBagConstraints constraintsJLabelTrigger1NewThreshold = new java.awt.GridBagConstraints();
			constraintsJLabelTrigger1NewThreshold.gridx = 1; constraintsJLabelTrigger1NewThreshold.gridy = 1;
			constraintsJLabelTrigger1NewThreshold.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelTrigger1NewThreshold.ipadx = 24;
			constraintsJLabelTrigger1NewThreshold.insets = new java.awt.Insets(5, 18, 7, 11);
			getJPanelTrigger1().add(getJLabelTrigger1NewThreshold(), constraintsJLabelTrigger1NewThreshold);

			java.awt.GridBagConstraints constraintsJLabelTrigger1NewRestoreOffset = new java.awt.GridBagConstraints();
			constraintsJLabelTrigger1NewRestoreOffset.gridx = 1; constraintsJLabelTrigger1NewRestoreOffset.gridy = 2;
			constraintsJLabelTrigger1NewRestoreOffset.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelTrigger1NewRestoreOffset.ipadx = 8;
			constraintsJLabelTrigger1NewRestoreOffset.insets = new java.awt.Insets(9, 18, 26, 1);
			getJPanelTrigger1().add(getJLabelTrigger1NewRestoreOffset(), constraintsJLabelTrigger1NewRestoreOffset);

			java.awt.GridBagConstraints constraintsJTextFieldTrigger1RestoreOffset = new java.awt.GridBagConstraints();
			constraintsJTextFieldTrigger1RestoreOffset.gridx = 2; constraintsJTextFieldTrigger1RestoreOffset.gridy = 2;
			constraintsJTextFieldTrigger1RestoreOffset.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldTrigger1RestoreOffset.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldTrigger1RestoreOffset.weightx = 1.0;
			constraintsJTextFieldTrigger1RestoreOffset.ipadx = 114;
			constraintsJTextFieldTrigger1RestoreOffset.insets = new java.awt.Insets(3, 1, 26, 48);
			getJPanelTrigger1().add(getJTextFieldTrigger1RestoreOffset(), constraintsJTextFieldTrigger1RestoreOffset);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelTrigger1;
}
/**
 * Return the JPanelTrigger2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelTrigger2() {
	if (ivjJPanelTrigger2 == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder1.setTitle("Trigger #2");
			ivjJPanelTrigger2 = new javax.swing.JPanel();
			ivjJPanelTrigger2.setName("JPanelTrigger2");
			ivjJPanelTrigger2.setBorder(ivjLocalBorder1);
			ivjJPanelTrigger2.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJComboBoxTrigger2NewThreshold = new java.awt.GridBagConstraints();
			constraintsJComboBoxTrigger2NewThreshold.gridx = 2; constraintsJComboBoxTrigger2NewThreshold.gridy = 1;
			constraintsJComboBoxTrigger2NewThreshold.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxTrigger2NewThreshold.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxTrigger2NewThreshold.weightx = 1.0;
			constraintsJComboBoxTrigger2NewThreshold.ipadx = 23;
			constraintsJComboBoxTrigger2NewThreshold.insets = new java.awt.Insets(5, 4, 3, 14);
			getJPanelTrigger2().add(getJComboBoxTrigger2NewThreshold(), constraintsJComboBoxTrigger2NewThreshold);

			java.awt.GridBagConstraints constraintsJLabelTrigger2NotPresent = new java.awt.GridBagConstraints();
			constraintsJLabelTrigger2NotPresent.gridx = 1; constraintsJLabelTrigger2NotPresent.gridy = 2;
			constraintsJLabelTrigger2NotPresent.gridwidth = 2;
			constraintsJLabelTrigger2NotPresent.ipadx = 5;
			constraintsJLabelTrigger2NotPresent.ipady = 6;
			constraintsJLabelTrigger2NotPresent.insets = new java.awt.Insets(23, 70, 5, 127);
			getJPanelTrigger2().add(getJLabelTrigger2NotPresent(), constraintsJLabelTrigger2NotPresent);

			java.awt.GridBagConstraints constraintsJLabelTrigger2NewThreshold = new java.awt.GridBagConstraints();
			constraintsJLabelTrigger2NewThreshold.gridx = 1; constraintsJLabelTrigger2NewThreshold.gridy = 1;
			constraintsJLabelTrigger2NewThreshold.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelTrigger2NewThreshold.ipadx = 28;
			constraintsJLabelTrigger2NewThreshold.insets = new java.awt.Insets(5, 18, 8, 7);
			getJPanelTrigger2().add(getJLabelTrigger2NewThreshold(), constraintsJLabelTrigger2NewThreshold);

			java.awt.GridBagConstraints constraintsJLabelTrigger2NewRestoreOffset = new java.awt.GridBagConstraints();
			constraintsJLabelTrigger2NewRestoreOffset.gridx = 1; constraintsJLabelTrigger2NewRestoreOffset.gridy = 2;
			constraintsJLabelTrigger2NewRestoreOffset.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelTrigger2NewRestoreOffset.ipadx = 8;
			constraintsJLabelTrigger2NewRestoreOffset.insets = new java.awt.Insets(10, 16, 24, 3);
			getJPanelTrigger2().add(getJLabelTrigger2NewRestoreOffset(), constraintsJLabelTrigger2NewRestoreOffset);

			java.awt.GridBagConstraints constraintsJTextFieldTrigger2RestoreOffset = new java.awt.GridBagConstraints();
			constraintsJTextFieldTrigger2RestoreOffset.gridx = 2; constraintsJTextFieldTrigger2RestoreOffset.gridy = 2;
			constraintsJTextFieldTrigger2RestoreOffset.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldTrigger2RestoreOffset.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldTrigger2RestoreOffset.weightx = 1.0;
			constraintsJTextFieldTrigger2RestoreOffset.ipadx = 111;
			constraintsJTextFieldTrigger2RestoreOffset.insets = new java.awt.Insets(4, 4, 24, 48);
			getJPanelTrigger2().add(getJTextFieldTrigger2RestoreOffset(), constraintsJTextFieldTrigger2RestoreOffset);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelTrigger2;
}
/**
 * Return the JTextFieldTrigger1RestoreOffset property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldTrigger1RestoreOffset() {
	if (ivjJTextFieldTrigger1RestoreOffset == null) {
		try {
			ivjJTextFieldTrigger1RestoreOffset = new javax.swing.JTextField();
			ivjJTextFieldTrigger1RestoreOffset.setName("JTextFieldTrigger1RestoreOffset");
			// user code begin {1}

			ivjJTextFieldTrigger1RestoreOffset.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument( 0.0, 999999.0 ) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldTrigger1RestoreOffset;
}
/**
 * Return the JTextFieldTrigger2RestoreOffset property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldTrigger2RestoreOffset() {
	if (ivjJTextFieldTrigger2RestoreOffset == null) {
		try {
			ivjJTextFieldTrigger2RestoreOffset = new javax.swing.JTextField();
			ivjJTextFieldTrigger2RestoreOffset.setName("JTextFieldTrigger2RestoreOffset");
			// user code begin {1}

			ivjJTextFieldTrigger2RestoreOffset.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument( 0.0, 999999.0 ) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldTrigger2RestoreOffset;
}
/**
 * Insert the method's description here.
 * Creation date: (1/9/2002 9:34:03 AM)
 * @return com.cannontech.loadcontrol.data.LMControlArea
 */
public com.cannontech.loadcontrol.data.LMControlArea getLmControlArea() {
	return lmControlArea;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJButtonCancel().addActionListener(this);
	getJButtonUpdate().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("AckUserPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(325, 255);

		java.awt.GridBagConstraints constraintsJPanelTrigger1 = new java.awt.GridBagConstraints();
		constraintsJPanelTrigger1.gridx = 1; constraintsJPanelTrigger1.gridy = 1;
		constraintsJPanelTrigger1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelTrigger1.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelTrigger1.weightx = 1.0;
		constraintsJPanelTrigger1.weighty = 1.0;
		constraintsJPanelTrigger1.ipadx = -10;
		constraintsJPanelTrigger1.ipady = -8;
		constraintsJPanelTrigger1.insets = new java.awt.Insets(6, 7, 6, 10);
		add(getJPanelTrigger1(), constraintsJPanelTrigger1);

		java.awt.GridBagConstraints constraintsJPanelTrigger2 = new java.awt.GridBagConstraints();
		constraintsJPanelTrigger2.gridx = 1; constraintsJPanelTrigger2.gridy = 2;
		constraintsJPanelTrigger2.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelTrigger2.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelTrigger2.weightx = 1.0;
		constraintsJPanelTrigger2.weighty = 1.0;
		constraintsJPanelTrigger2.ipadx = -10;
		constraintsJPanelTrigger2.ipady = -8;
		constraintsJPanelTrigger2.insets = new java.awt.Insets(6, 7, 1, 10);
		add(getJPanelTrigger2(), constraintsJPanelTrigger2);

		java.awt.GridBagConstraints constraintsJPanelButtons = new java.awt.GridBagConstraints();
		constraintsJPanelButtons.gridx = 1; constraintsJPanelButtons.gridy = 3;
		constraintsJPanelButtons.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelButtons.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelButtons.weightx = 1.0;
		constraintsJPanelButtons.weighty = 1.0;
		constraintsJPanelButtons.ipadx = 153;
		constraintsJPanelButtons.ipady = -2;
		constraintsJPanelButtons.insets = new java.awt.Insets(2, 4, 7, 5);
		add(getJPanelButtons(), constraintsJPanelButtons);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (4/19/2001 4:18:59 PM)
 * @return boolean
 */
private boolean isInputValid() 
{
	//if( getJTextFieldYourName().getText() == null || getJTextFieldYourName().getText().length() <= 0 )
		//return false;
		
	return true;
}
/**
 * Comment
 */
public void jButtonCancel_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	setChoice( ControlAreaTriggerJPanel.CANCELED_PANEL );
	
	disposePanel();
	
	return;
}
/**
 * Comment
 */
public void jButtonUpdate_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{

	com.cannontech.message.dispatch.message.Multi multi = new com.cannontech.message.dispatch.message.Multi();
	
	for( int i = 0; i < getLmControlArea().getTriggerVector().size(); i++ )
	{
		LMControlAreaTrigger trigger = (LMControlAreaTrigger)getLmControlArea().getTriggerVector().get(i);

		double threshValue = 0.0;
		double restoreVal = 0.0;
		
		if( trigger.getTriggerType().equalsIgnoreCase(IlmDefines.TYPE_THRESHOLD) )
		{
			try
			{
				threshValue = (trigger.getTriggerNumber().intValue() == 1 
							? Double.parseDouble(getJComboBoxTrigger1NewThreshold().getSelectedItem().toString())
							: Double.parseDouble(getJComboBoxTrigger2NewThreshold().getSelectedItem().toString()) );
			}
			catch( NumberFormatException e )
			{
				javax.swing.JOptionPane.showMessageDialog( this, 
					"A number value must be entered for the new threshold for trigger #" + trigger.getTriggerNumber(), 
					"Incorrect Number Entered",
					javax.swing.JOptionPane.WARNING_MESSAGE,
					null );
	
				return;  //entry error, get out now!!!
			}

			try
			{
				restoreVal = (trigger.getTriggerNumber().intValue() == 1
						? Double.parseDouble(getJTextFieldTrigger1RestoreOffset().getText())
						: Double.parseDouble(getJTextFieldTrigger2RestoreOffset().getText()) );
			}
			catch( NumberFormatException e )
			{
				javax.swing.JOptionPane.showMessageDialog( this, 
					"A number value must be entered for the new restore time for trigger #" + trigger.getTriggerNumber(), 
					"Incorrect Number Entered",
					javax.swing.JOptionPane.WARNING_MESSAGE,
					null );
	
				return;  //entry error, get out now!!!
			}

				
			//create a new restore offset command message
			LMCommand offsetCmd = new LMCommand(
					LMCommand.CHANGE_RESTORE_OFFSET,
					getLmControlArea().getYukonID().intValue(),
					trigger.getTriggerNumber().intValue(),  //the trigger number
					restoreVal );

			//only add changes when the value is different
			if( offsetCmd.getValue() != trigger.getMinRestoreOffset().doubleValue() )
				multi.getVector().add( offsetCmd );
		
		//}
		
			//create a new threshold command message
			LMCommand threshCmd = new LMCommand(
					LMCommand.CHANGE_THRESHOLD,
					getLmControlArea().getYukonID().intValue(),
					trigger.getTriggerNumber().intValue(),  //the trigger number
					threshValue );
	
			//only add changes when the value is different
			if( threshCmd.getValue() != trigger.getThreshold().doubleValue() )
				multi.getVector().add( threshCmd );
		}
		
	}

	//send the messages here if we have any
	if( multi.getVector().size() > 0  )
		com.cannontech.loadcontrol.LoadControlClientConnection.getInstance().write( multi );


	//dismiss this panel
	setChoice( ControlAreaTriggerJPanel.CONFIRMED_PANEL );
	disposePanel();

	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ControlAreaTriggerJPanel aControlAreaTriggerJPanel;
		aControlAreaTriggerJPanel = new ControlAreaTriggerJPanel();
		frame.setContentPane(aControlAreaTriggerJPanel);
		frame.setSize(aControlAreaTriggerJPanel.getSize());
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
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.ConfirmationJPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (1/9/2002 2:50:30 PM)
 * @param groupID int, JComboBox jCombo
 */
private void setComboBoxText(int groupID, javax.swing.JComboBox jCombo ) 
{
	DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
	LiteStateGroup stateGroup = null;
	
	synchronized( cache )
	{
		stateGroup = (LiteStateGroup)
			cache.getAllStateGroupMap().get( new Integer(groupID) );

		for( int i = 0; i < stateGroup.getStatesList().size(); i++ )
			jCombo.addItem( stateGroup.getStatesList().get(i) );
		
	}
}

/**
 * Insert the method's description here.
 * Creation date: (1/9/2002 9:34:03 AM)
 * @param newLmControlArea com.cannontech.loadcontrol.data.LMControlArea
 */
public void setLmControlArea(com.cannontech.loadcontrol.data.LMControlArea newLmControlArea) 
{
	lmControlArea = newLmControlArea;

	for( int i = 0; i < getLmControlArea().getTriggerVector().size(); i++ )
	{
		LMControlAreaTrigger trigger = (LMControlAreaTrigger)getLmControlArea().getTriggerVector().get(i);

		//get the point used for this trigger
		com.cannontech.database.data.lite.LitePoint point = com.cannontech.database.cache.functions.PointFuncs.getLitePoint(
				trigger.getPointId().intValue() );

		
		if( trigger.getTriggerNumber().intValue() == 1 )
		{
			setTrigger1Values( trigger, point );
			
		}
		else if( trigger.getTriggerNumber().intValue() == 2 )
		{
			setTrigger2Values( trigger, point );
		}
		
	}

	//set the #1 trigger items visible or not
	getJLabelTrigger1NewThreshold().setVisible( !getJLabelTrigger1NotPresent().isVisible() );
	getJComboBoxTrigger1NewThreshold().setVisible( !getJLabelTrigger1NotPresent().isVisible() );

	if( getJLabelTrigger1NewRestoreOffset().isVisible() )
		getJLabelTrigger1NewRestoreOffset().setVisible( !getJLabelTrigger1NotPresent().isVisible() );
		
	if( getJTextFieldTrigger1RestoreOffset().isVisible() )
		getJTextFieldTrigger1RestoreOffset().setVisible( !getJLabelTrigger1NotPresent().isVisible() );


	
	//set the #2 trigger items visible or not
	getJLabelTrigger2NewThreshold().setVisible( !getJLabelTrigger2NotPresent().isVisible() );
	getJComboBoxTrigger2NewThreshold().setVisible( !getJLabelTrigger2NotPresent().isVisible() );
	
	if( getJLabelTrigger2NewRestoreOffset().isVisible() )
		getJLabelTrigger2NewRestoreOffset().setVisible( !getJLabelTrigger2NotPresent().isVisible() );
	
	if( getJTextFieldTrigger2RestoreOffset().isVisible() )
		getJTextFieldTrigger2RestoreOffset().setVisible( !getJLabelTrigger2NotPresent().isVisible() );
	
}
/**
 * Insert the method's description here.
 * Creation date: (1/9/2002 5:09:43 PM)
 * @param trigger com.cannontech.loadcontrol.data.LMControlAreaTrigger
 * @param point com.cannontech.database.data.lite.LitePoint
 */
private void setTrigger1Values(LMControlAreaTrigger trigger, com.cannontech.database.data.lite.LitePoint point) 
{
/*
	if( trigger.getTriggerType().equalsIgnoreCase(ILMControlAreaTrigger.TYPE_STATUS) )
	{
		//set all our text choices in the text box
		setComboBoxText( point.getStateGroupID(), getJComboBoxTrigger1NewThreshold() );

		//do not show the min restore offset since this is a status type
		getJLabelTrigger1NewRestoreOffset().setVisible( false );
		getJTextFieldTrigger1RestoreOffset().setVisible( false );
		
		getJComboBoxTrigger1NewThreshold().setEditable(false);

		getJComboBoxTrigger1NewThreshold().setSelectedIndex( trigger.getPointValue().intValue() );
	}
	else
*/

	if( trigger.getTriggerType().equalsIgnoreCase(IlmDefines.TYPE_THRESHOLD) )
	{
		getJLabelTrigger1NotPresent().setVisible(false);

		getJComboBoxTrigger1NewThreshold().setEditable(true);

		getJTextFieldTrigger1RestoreOffset().setText( trigger.getMinRestoreOffset().toString() );
		getJComboBoxTrigger1NewThreshold().addItem( trigger.getThreshold() );
		getJComboBoxTrigger1NewThreshold().setSelectedItem( trigger.getThreshold() );
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (1/9/2002 5:09:43 PM)
 * @param trigger com.cannontech.loadcontrol.data.LMControlAreaTrigger
 * @param point com.cannontech.database.data.lite.LitePoint
 */
private void setTrigger2Values(LMControlAreaTrigger trigger, com.cannontech.database.data.lite.LitePoint point) 
{
/*
	if( trigger.getTriggerType().equalsIgnoreCase(ILMControlAreaTrigger.TYPE_STATUS) )
	{
		//set all our text choices in the text box
		setComboBoxText( point.getStateGroupID(), getJComboBoxTrigger2NewThreshold() );

		//do not show the min restore offset since this is a status type
		getJLabelTrigger2NewRestoreOffset().setVisible( false );
		getJTextFieldTrigger2RestoreOffset().setVisible( false );
		
		getJComboBoxTrigger2NewThreshold().setEditable(false);

		getJComboBoxTrigger2NewThreshold().setSelectedIndex( trigger.getPointValue().intValue() );
	}
	else
*/
	if( trigger.getTriggerType().equalsIgnoreCase(IlmDefines.TYPE_THRESHOLD) )
	{
		getJLabelTrigger2NotPresent().setVisible(false);

		getJComboBoxTrigger2NewThreshold().setEditable(true);

		getJTextFieldTrigger2RestoreOffset().setText( trigger.getMinRestoreOffset().toString() );
		getJComboBoxTrigger2NewThreshold().addItem( trigger.getThreshold() );
		getJComboBoxTrigger2NewThreshold().setSelectedItem( trigger.getThreshold() );
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (3/10/00 5:06:20 PM)
 */
public void update( java.util.Observable originator, Object newValue ) 
{
	// should be a value of com.cannontech.loadcontrol.data.LMControlArea;
	if( newValue instanceof LMControlArea )
	{
		LMControlArea newArea = (LMControlArea)newValue;

		//be sure this is our area
		if( newArea.equals(getLmControlArea()) )
		{
			//we really dont do anything here
			
		}
		
	}

}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G5DF854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DFD8DDC14D5563815952D34E9EB3E24182818D4D4E818D41434CEC6C5092D16EF6B2F35F75955ED31175D753F2F3D2FDB8FB00C1878D1E2D2D12228009262C702084637FC25282CD17941488CB0BA4CCCB38FB0A869FB6E3D4F39734CB34F00504B7E7E7DBA4EF34FB9771EFB4FBD775CF3BF1E87A1F5CBC0D2FCC22AA044A78952FF85710230C29404916316CCC5DC10D12393E47FED8578D0187D1D96
	FE430096796F9A5D86A163D0B22423A0EDF8D5235B8C4F9B059D07FC4D7060C50E81E83FFFFA429F7B16F3728B14330A265F28C8015FF5G5100A33F692DC47EDFAB52A47CF2890F901FGD5E8A3BCEBAB53A5DCB364F182C4834434B27E15703B1ACA3E3528D062BBBBAAC0D87EE2D642DAD20E2CCC0433EDAC6FB069B7C3F8B4BE3B1D48BAD819A2658983DA9CGE9FC22E06B2A015F62064BEDC7DB2C9DF9ADFA0B45EA910DAD6DF9E62B5E50824FF62BB92F2D5314572137F46A4DC6C074949515B3C2295D28
	EF341B5A5A0C765A863D45C8549BC865DD2BCC26658B02995279A6025B67A4F5D88AE98152E9A6443FCB71A2BC7B82841A305DC7320A25F6B5DDFDD4D8793AA5F13D17F6071BD8FBE3CC2EF60F6E7C6FD51F50BE9A350574C546A50D2E8220892097409FA09C60B3EA9BFEDF6C025F520BE239352343EAA935594A4C867BB7D603518C4FC5C5C011F0270CADD63BC190D87F97D7662A5427AFE079EA871C0F69A4E8BF5AC270297B05C50F55A52CD2514922207C04791718AEE2AF39F442E45F62D5F64AE5F7DBF5
	177D2DA0179DFB1BB5B1C9C5F668021C048C291C8317DD328D17C9DF5FCF7B3A911E63A57BE078900A8F1261195D96D2BCC90781AD5D0C3651348E6BD22F9DABEC58DB2F2A873FDF17A68CB333BA781915F56968A1E5EB4CCCE6922F0B1362974A702CAE6F533A10F4AC509ABAB4BAE6DFE33712F19C8A69E80025G02C5234BGA886E830200D3D75451E9B3431863DDDAC6BB4198DC63B40720E5D72B17C9A1D22DDDFE3F108FACB0B710C5EEE52DFB49B9D0244B3F98B5141C537FCC15476DBC046B12345E857
	0BA62B85CA57F798C5235D41F826ED79107040D8A654C6D3079184F45884AC7709AD9B49B856BB44FF5A8CFA516862E074E73710BE69521F019221GF8E6FAF9FE9B51178D5A6F83604775D01CC14A2BB35A41E1949555DB2D17BBED0D24A542D28B5173D86ABB0261D9E3C19D4F4EA438DCAB0CE1AB16634878C8D6CE052905D4DFEF6F41B1966CA43E3A490AB673CE935ACC455AA9C26E02DAD51BA1E033B2DB7131AA474E6334CCD2AF16BE6AEC9A60B8EE322A0FB16F3E625B814BC89CGAF536F5E9D242FC2
	20EED1G31DC470FE4E4B01BAD53BB0CE825A76CE0E0C603CB632C1E0E9E74791BF95D2FD11C562651655AD0E7C9BDCD832C67668134074978E240B21286A00369A6F0AB190F0CDDB0A7D698EDC60B819E9C926D212D446624715AEA65F926D13C8D46F7B98CA887474F5CCA4620DE946D260B1D227134D194CD16B697FD961565683CE8F3398614B5E7AB293B359B46B767FFE9AB999BB0FFDB0C3413F896097EB29D1BC6333183AADDEE6D34088C7F1BED443EAF9BFBA45F707AB69284F5694D1D444F3018A1C4
	3F063441EE6A002EA72ECA883711F1BB2DB2CD2623C6401158E7A47C865AF08CF6E762985CFEE52290F1E1C7025A9C62B725A2C17BA39BA7E9BF3A46A02BD3E426FA5DDF21BEC242BBD8DDFB8A88EFE94B65FFBA0CF69A22B0FB3F3413608B216CFAG430FE86F355B899DFD0AE46865D6331929116B37E04786529F469FBB0D600A09732A63BABB3A63A3856D0C24BFC62F5AC174E716793DE2BB1D83FBB09E6C057A0C86887D1169F039248346A7DEC1BE9FC887GC458991F60E0FC2C6DCFEF4A9754E3BB924F
	F50AA6B38D64DACD768E6A79D9884762374F2A1225F22A9D9827FDD5C3F4B68C52FEGA18E6C53F9EBB1CE9BFDB9C0D8BC79D32F71E924D43FD8072BCFB3EE41B8ADB40B7AF89166CD917B63599D7239225461303618E8E5A5DD5AF64A7B1C61BAF7FEG3F50AC07D883035209FA3F3A1354174CE392DA1A577F3D87677586106B831051C970855D29323AC7GFEA9C08A5092E9F9A5D77611B1CB8DA30F7DB4986D2DD6FB075110FDC95F254F53F70BF954BF6415D2F2A5F94E417E39E4A2FA3BD456A90AD6CBB9
	19524D97CA954554FBAF67F04E99267FC99BCB24BAC6BDB2D6A83E3EB1C1ADCE76D97FF9C2FBA7537B48CE177EFB276B92E34F7F4FAA260B71D7D0970B00A7358BF5D15C25260B7AAE5405766A4F5345FB5F4AF441420C816A02797D0CAAE5EC5B5C651AEF45AE4F391745FC8FD05B7669E2B15FAA0E1FC17181B2BC5353E10AA769B02025F7E31CD21EC3FC60B0C87B830482C481448224B851269333768B7D4401B5B0CE6D963D79341B4347F8ACAB1179E8A97DF4D6927AE881673F5F75A875106DF4EFFB05A2
	4D72AD73487705A25D2E48477AEF7D69CF505710DEF266B93AE17EAAAA2AE5BD8AB4B6C7046C174F91431CAC468B045FB0G66FBAC5D49D2FD221D68E3FE2A127B1870757B9256F871B149BD2CFE59BDAE9B4F65EB41E53B086FEC875AF0GFF00B000C5GABG522FE09F9D4BBE52DF9FD143EB3416832E9AFCF939F22E65BA1DFCE5F0FDB14DA31F32AF0A2F3867E37AF3F4217E9A014EE26A826A4FFDA19D40F189558427017422AB981BBDCA6DDA388A3AGA83C0A7827A83E91524E2BEC6CB3FCE94DC0E3D3
	D2A6BE8BD755E361F4894FE4FCBFE0992BE44F5E62BF0C77D8FF5767F80F65CE672847F25F652847F297F2BEE4F8A8C33E12C3FADBCE5F311EB5C7AD567B1C3665050F5753329D8E23039AE005DE54CB34A562A6C1A566BB8E0BBDC98F9B050F2F2C16F8AAC5EB79BEBEFE7FC67D164A78154584CB77E3CC60FF95E302DBBB09DD4C07F4AC0036976D42512D345DF420657732FE2D68756CF7B6BE45DCB24FB481DD847085888450746238CC5A552F6F2CF47661BB7FEB1777FEFEEF17FABFEBF713F846E16D3437
	9815FD39E6F79A2739BCC00DE86CA0EAE7BC29B43F28373799BDEC61B31A1F513C655F3E5B1B3D30BEFD432DCF7772F1636CD276C18C68F105247B141FBC7B0015F73C0A447CE6FD0F35D3841F719369E7E3957A6D8CC89782B48308BF49634DE95D44A7375A759DD03D53B6E30B50FE0D647D249A734E877E25G49004F86283E0671431255044E2CCD5AD15C0B75C93966C15B0734CEE1AD2731290668054DB56F54284DB5A1A0D38340F89E6ED263D9847438EBE84B4B76AB7518FC5D253B746B5EE20AF76958
	483F2E5CDF52F691FC45F5657E5208CE0C3D1A0096FE9D7D74AF68FC18B1CC23BBB08CF13F2138EAC8B7F11C60C4FFEE98066325B7573B3D5F122BEE6FF7642A7B353BF27B76EB7F166B4DCED97D6E2273128F544D1F57F9A14505C0BA0263123A319DD100E33A39338A772F63886EA65C3F5E5B1CAE77DBA4BAACAABAE6B7994A74ED755402196FDAC067BF123F79A674DD0947C819FE108E81083C89E38D67CE0CB5026F9FAB548F2BD3DD4FACEE5C173078A656775A1BDD31C690449ACC665755A83384684E1B
	691EE335BC0EA6B67CBEB064912914BE410DCE7489A1320CBE4F0DCE6ABBA2C1CECF2C1153090EB2257C2CEDAF6D40362DFCA9C8B04FB864F52D96A135A9C67AEDB813A9EC7F457FC8ED5BD90BB1C67AAD284FE9F4CF219152E6006137E0BC77879E0F78F35E0BD7693C8169D8G6DF06CFB6D81421B8E69FC0E3B76955AC363F09C136FF792DCA024A3B86E83AA072C07495A1961C6F58EB4F67086C496574DF0F9FC72607F198CF92C12A443ED94A652B151E1B14224E7EA099DCE7492C6F14F83AC9A0EFE746D
	EE32FFA342204E3F151CDF7D15CC1702F8AB615F6CCC6BC776C6617EAECFB9CFD35C1F8142EFC5B97F5965AEE753D7AEB9695DFD49E1BA7B1547BA2DBFE079A68EB25F9B834E47FC75826AA322EEE53EBA1E635F247805B2BC73D32B0F211F0A81DA75ED38AEDC719551C9B024A38196832CGC83B9DFC7A6D682B3F7C126C5774995BB4DAED75604C4DB2774B5AD7D1BD387324E7D721BF88FDF21290F65B3B715E56D55537B31BEF17FE17B6E6D2BCEB5F088252BEBF200582AC840883507A40BA42875B5779E5
	AEEBDF2541A4160A223E251D6E5AE0634E180C5DAEB489A0D85BF6574A5B168F65D5783846E2130F3779F7875DA7B47B30BEDA43717BA95ED706E7FDB7137AAF128E865A06BB50073C6D96C70900773943A5537F8EEF72DF23BEAE648E257C980A0F3EC3A93F7DAA4A0F83DA63AFF02CED3A60990FFDF9G47D89870C5832CGC8994156E6CDA3884FBDDFD574517F095258C814E0BB64A98448145275703C9FAA780F775FFF1F55A3341C46DA72BD9A817D46CF67E4B4361745E9F33B49EFD6CB16E4F3815D7273
	6C428F8AD4770089840CE03A8D15FE43EF4F144A187B033C0C8DE337A644FBA9A3D64A3BE204EB0EE6E5C41E134F05993B352AF3610AF3FB92F2FD25B95057D5467CCBABD731764D6F226D6B4C12FC4DEF0F89827A1D9859737AE37FD77304F7BF17BD24B258B80EBF68BD26CCBE289ED32EBB289ED3269E6CFBAD1CFE507B39C766C179398773C3138E1178E7B874D3G0086E0A1C02CAF7A20E9873789548729781FCAC39BD9431153D591650FAE705E56F1857D1F498696103D680BD698969D24FC44BFD8C07A
	CB345AA812619EADA0BA0285CE3B48D8A57C0C82B27F1A0D2D944DE222B29AFFA55F897E75CE0C135EBB0F31D38947DDEE66EB02BB511F6D4EA4BA8A0574E2G6DAFB53ADC0026DFA27D90258F07F4A00086A086C0FB97521F5EC6EC0C64A1F9EF44EE089CB5FC28826F2A43EBD446EA41FDAF27AF70F6FE275C7FA17E1569A61E46CF5B21DD05F7213E269EA638331036F1DC6A915461E82E03C5D7090EE2A03DE1A44C61GE781FCC672B90352C3A1BD9FE0B9C06AA8609B05F44B55410CD3A20F48A04F87C63A
	F4F7D6763C01576176C149C814F404BE85712B953AE6F356BF8FE03C5388ED739D056B327D7B5C7603593A2C4AEC6D2E1726B1E92FF27C81DCA3E9C6615A60A5DA66CAC8275E8D368930050E8330F7817F0B7BDA8D47E2517B8E8B8246B7EF25200FCFFFE81CD0717A3B7119027ABCD1F1B75361683BDDBEFEA978F866D76EAE267749003674EE74A5D5053B61770C7E129429113046EA275E84108DF62B4DE897FB98161EGCABE6ACE3235AC0DED5F429D5E4AG0FD48B2ABBF1711231C5EC5437896863820F12
	39BF79D79A5DF600EA00E6G914077D7D837F90559FD540DF58275F5F63D894FCF9FBC645D57CDBB543F2FFB125E39B3020FEA6C31C9FBD7338E1158455A5A6AB0F21F2AB9340163EA9C8DD607096CF93139743BDA42FF51EA279B8BAC563155E22C93G6D8B830883500E069869AA34197264DEC01E7451ECFF6AG7CD60FE6FD4962211B1B7B0B0764508CF945512EF848871E17C1AC74979E8F2DBEC976DDEBA1DA3FE8AD33BAF15B6A38313B315DEEF434DB4D8681E3203B32C87DEA6B7597DD7BDB472DE283
	700116994F6E64CFBDF95C8AE3FC674EFD2146F74A6800AEB61E10F44DF8471612F1D25B88BDD7E5B21A8D486F4E4C4E78AC19723D035112FE43C763992045A827C7CB74651C7EC8359ABD799E241B935468691CBE6E101C1EFF8F23B75D03F1F0DA3727ED6C61EBCD73BD4CB606432F9FC0884F774CE14F78F941E13EA7F98F3317D8789546486DE577117E6CA5C9E6AFE910B7F70C4BDECA46A86D650FA9EA7632CF55DE7EDE60E1AF7B1476B275E38FFB5927E6AF05159E76324F1B3D7C21C655DE7629594B16
	DE253D1033217AB1581FE30B147DCD684D1C3E27D70DAEF23A0FEAFE9FCE0F68D25A8B217B0F41796F0783035BABB08DB25F248167E36B412F3F226B44B14ABD06EA0A8F9F235CE31847675CC8E2F37F06FB8C49A544DEC7C2BA98A09AE0A940CA00D4BF0DAE9B20982081209DE09840E800A000F000A80045G4B7DF08E797DE1327768ED8E11B6D9DD13084077010BC95BF8CC4F26BE09F6DFB169B31385DC3E5EEC3AC22FF21C86C6CB9B23BFFC547B9C94FA347FB96869236A71766C231E71F664D175F87B15
	2372F81B697CF89D5EC3CD3E97626BFBB14ED972B951D15FF7EC9ACD2259D8A639ADA96E99D307E7E94EFBB16E29BBCF6DG5211G0B6F453D6F468B585FAB886FD85CD7753F0031D37BD8AC63C54A9BG69B0006831830D1D7ED2AA8868575A4C1F892AE70268412436CC4944FBFFCB4722DE7E566A65BC11FAA116EFCD9B6AA0E39C5FAB2EC6DC9347CDCEC05C48F1D87E0E6CF475723967E2F9DFBD04F50B9C077A7C369B7BD270875F6F0FF2A6F4239C1BBF6AF3E3993D178C698800057EF8EEB427CCF9EE94
	87345471A0E3BC4A7947E16C379240B947231C4AE26C371071A847D1CC6F46C3DA38CF23CB3BEF207D16DAAEF05BBA530BED4E3F8FE54EAB423635F3DCEBAF6282B8EE9667DB48F17F550565ED18003E35126E773267633267578F8C7D9EC75C104A10769DCA3C7B214C127E7DD0EE093A9F5AD76269078ACA547D501192399FE238329252B7ED60FB1BB85FB10E7B0E636AE91984772D49A036F37CA9DAAF022FB6920108EF22756A2638A689F7CE067BCE42DDACA13194B4395DEA677799CA48D9C02B49ECE669
	4B1447E401C8016B43C66BE83269E5756E64386FF85FB65675B707D6CFD03BABF3315FFC4A3BB3AC5FB81A4FB0614676B11C40476666D954D77BCED04E5911946FB7C1B9E71FBC0F3EA090E84981B80E3E3FB038D823FD1079A68EB89FEB5751E63A179E20EC6FB10AF786A85B9B4D672C61C0DB4A5B7BE0E6DF726BA7FA76834B3721CDADDF3344AFFA4D77366A39080F446F6FB5DFD002DA3E9009DF63B55F6B077AEA5FC22F794EF95C1739B13B3F5CEFBEF53B8F2479E2D56A238642A4347B5B69FC18BAC9E9
	87A3A9BEE312528EBEA9C3BB48855A48C9B8FF3E65B6FF523B62C0CB1F8C7361E41CF766446339F175E41CA7B5C9748EB2247DGC2A623DDA5968FEEBCFCB848FC35834E477434044E731113157A0B23787913157A6BA9C67DAD86DAC5A0DF5B7476A5BFA8501B1D3DD62416AFDC620F761A6F322A3C9809FF0557FC9AD5F9A9D324BD36A95E722D74383BA40783AA79589D38080D648C47E4B11BAC46F2765613007B3D53E94CDD8DF91B2770FBB46788CE04344F94546DF36785DE0FD7E44F5F1E9F7A584676AF
	10D1516CEA53B7326757AE8C3D6C7F9CD2992CCF1F4D647B753C1F3368DAA386524BB94E378D79047B9197DFC3F08D10EE6638569A64B3F35CAC3AA68907F49447EDCFC43E059C774EA144E5CFC55CD3F41D610474C80E9BD7CA6F33C0BAECAA5A611DE52E3E99A3FB5ED2B6747DD4B824B2587B4FAB4B70BD964DD4767E73A2780D1B4A46F6F2904E83AFFF435709C1B88F0CEBA0FE318952B6G1FA05459CB71F23F91G7850A0172F088C72749B4C7F966AC8F90B0214E775D5943FB2C8F9D67FE2921E5527BF
	20518DFB005F95ACF675535FE54F570B073E4F7C06D48633D3FBAF5AF34C83E83B4B681A714003B0BFBD0838CF8FA3DF83475DCC7386C0BA046316707242B96E37343C1407A09EF888F1A53C3CEC0E5B486F89BA9F423D5E976A483AC7A4BBAED5D63AA7456EB03DC5FDFFA4704D673CEF1FCBC2DE78E56B68823205A66D7FAE4C6CEB7FD73E77CBC09B8C6B76E0367FCBE83970FCC8E0F3B713F7344D7D4E5434CA172C49EDB732570CB253A4D91B24728B61195862DAAEAB2D46DD561F2ADC32FE2A9948B9C81A
	2CDDC4D685BC6FF5EB57EB896E32D22ADD328EA78EE88FDDA12B811EBF141ED9F9C9C57D75093C3C330ABE3141739F5C6A7EEA373A3D3CCDED2B9778FD0251DE2617FA5ACBA579FC026493F61473EFACC01E38E074DF9D6538FF127DB07A2F6914379952CE00119C5FC33FA79184698800058F23DFF9759BE5BC9477304B17E99F716657561FA46525BD427C57EA0ECF277883B2BCEB43C46AD7C93A81E8210F60D87B8F9A0B27BD2A5165BE0A380F68FC57GE983470D52621874F9143FA32DA3FB3E41101E8F30
	92A0A384FCC588526BA95DG6961G01G9A0098CE0FCED273EBA4BF79A5E512327B72DDA8533B6F7C132A8CBC87C71B6A4F872F76C2E336FBC76F007C49B4F75BCD03749EB75B559EF69FF76D3582173577704F71A75B27A97D493D3D6E32C65439E43D57E73B7A73A7795314FE722FC56E329E29F7497A76065A65CDD671B425BF5955BD90FFD22D681333107E9D360356755AA901376379F3641BB196E3F786FFF11178186CED25F6135E3CADB7E3AF7CFF001C9F5C58DEAF4A18F85A752CE9F4BDAF79276B79
	03EF85AF65B27A17CDAE5E1D6D2EBA5FD5FA23F5166E92158FEE7DFCE810796CDE7335CFD39F5F4C5F6D24FE35F71A723BA379946FBBCD795D1130F2BCA370871AB09D77BF16D1FE43F465BB9DABA85EB9DD79CE47758AFC274397E8F15351CF7E0946340D0FE9F46D0FA12E7D847ACE5F475077EDBAC96ECA107B66647EB93E1747E85BA8AD84F82381629EE3E75E0CB6356167440272777214775EDD72A3FE168C795DA2795DAD2273555C9E5F0D77BC735F1020BC73CF8DD51E7967A4FA1E7917A4E23E6CD0F6
	66DF823F0D21AC9FFBD7A9C84B4B867CB000G0088004521D836799CBDF7D73977C10FBA79FBCBEB4F13F3D9AFF7BE8CFBF99B9FB96FF9B7243C534E6E8C6D155E493A305933ACAE10713CF3A1550307B13073721BF44A73FA52EEE1861E278B254A73F6C2CF6574319E77B788BD1B536FABD12397F3FA1DEA7E06996AE7DD4CB73DF9192458F3FC076BF93B4575FC0CDCB36D4357DDA21F321156AFF7146337E548F99D33635C8A7CCE8CCB9F24E9977D1972217F464B6B8316F1235FB5F14978DB056777D95E23
	B88374016F8CEC6F2D95C37FFD167B86ACE3A05FE7E163C1B86E6983B69F47719D8A6D0D82909E879BFF1C5983738157CFA0CFAE60CBG1A81068184BC0E36AC3A5DF5A25FC3A079A3B87D7BBAB57A7C47F17F7B4AC935736325C0DF79B82E9B2A7939F76ACCBCD3EECF44B5C6BD60064FC45E10C4BCD38E1F09EB09B7A96FF2C82704E9F45B43107795AD72960761FBE9AB354A7752E68A72774A7212146F257D4E0D5E6CC62777F65D5ECB3B5B0DCE5A34DE4A4F6A9426C35B94F9BD8BCA317D419CB70F6BA406
	639614A0AE7D89445D4C799A81473E1B124F3F37678BB8B2EF86BE414E9D5C3F2F32289679E21EE0F36BCA098F3F2F427A626B63589745CFEAF46213F8B721ED9B3953496F0418AEF664F5F6F56469AF0AF9C603C9ACAABA5EE9B6AB6E415EFE9C7BA478C96C3F99347C98C82F84C8FD8A5BB665847266BE057D771B934A7E7B075BFB05C98DFD3FD7D850206C3FCC377EB3B5A87B8F5FABE4F5FA289A4FEA8C3C1E236A78FBFF9CB77DA46266BF056D9C3F0B34B3A59CEC9420B89C7D50B7748C2EB17C46FCE9BB
	4FE7C99CDCEC59BB48FCB3861C0F450C2734B4A68DD71E857DC071BE614A332098BE76C783ED85EF6FEF34032B77533A3E721DFD5A5B59CCF129DABE1B44BF5CEB3E57D44FD47DA47EE02F795EA8D14B97A1714F771A6FF6D5F9CBA57E15AA79181E3F2B4418FD43B39ADD75B3987785558E769BB72C5C24BAACF7981499464B7D65B703ADD73A0BF3FCF07DFFFA1079FC871C0F59F54E89527E38E714763E0F62D7BE2334772738FFCD1E255119E771BB84A786D76F900F3BG51D0E64CAC698CF316B7BB9A63F1
	37A4A58266798869BD0C88EF79E61E14672B863E33B2DE1BCABE260F8FF713F68F0FD01EC5EC2178408865D94464DDF8962181DA7633381EB4A4F81EB35D190067CC3E4F22BE1BC838AB55D9AFFBFEBBF1684FAC9235C3FFE6B1DF076BEABF2E1B53B436D87A2C7C4CA22C9479D6F03EE5D4DF553F56681AFE0D38D37C1C4F40F1FF21B1C8A824B59CF70617974DF1775172B2E6435CB79BF1093C3C625958A797BC3E51FAE35F8C6A7048273C7F61BBDBAD5F9A8F9B3FB1F91FF864D34A8BD4153767044B4E8AE5
	4F4F9E9FFA1BFBE3C8E530BEFD1E46C3615076E85E4F9B68B9F24CEC79B7BBBA795972F24E77B00D1B52B49ADD2E86F1FF2738920D7CBB99BF5473B3B64E778A0D2FC4C87BF05C31EC3A66A5F9B96ED28EBF4753204DED5ECDE23C646740B6819A811CG7E4FA1FD872507C3FA9140CA0074B9E033F3107E40AE25DE7325B2B01D7C1C77D84E3F8FDAA32F432FBDE41079D43E6F5E57FAF2CD9FB4D23E343F7AA331C736BF7CE75D004E1BC09736B92E7D6161701CA5487736AF15625EB6ABFFEE690D3C230DB2EC
	3272C919A367A8776935096EFB679A3B2BAD26FE4F62642014658F4F78FEB41315DC6AAE2BD2F4496A6837DDC9FD488A1A23BCE31C21B8E3EC6FF4497A344FF6A94FD51432C26179AEB7D9E3AA155F48E27832BA65F716987E4DEA4FEFE67C3FEA7CB6C7449C0C73327819AC5BE770655FA70A861E98G6D7330868228F89E73E86ABC4B7EED9DFE0F43407956563B17ED2F4732C560718188820884D84A737C6124675E6ACA3EAF119C4976D6B36077C0A45BDFE6794EE7FB6E2BF4E64B77B3485EEABD64E90E14
	3E99C16BF4D38E72B8816F8B908C9085909B09E53F3C066E19FA5BDB65BAFF6BC09F6F5399980F2550F34F146F2BCA653476FC6CD98E9746F8E6FF3D510307B1C8FBBD3B157B2AC4D7A9AF60DE508927F25F1350B3B87D21A3EA74831C7E6AB5B5FAB527071429514F3EF0236F90205F9B5165FAFE225B753C4869FAFE3F4775BC752A407BA325067C12EFBCC86B7C9A5C23303D00FB940BEB14FB946463B4F9826EB1FCD42B5C2318663607D1D12B5C23F848ADFFD72DF20F22CD4AAF1DAB5651BAF6E29D7D6A31
	0EC13C0EF36A7B2E630A7A3E6B386B1B3E6BF8769BE59D27CAF4964BEFBF0567CE0B21CEE9AF623E0979946EE7B53C087BA6233368BE812483G42B86F38ECECD7B467FDA11B70A63F84B683D0789272CE4DC15E0617D0878BF27A5E8BDC35CB290335EE74C33B7A5E8B3C3C2B6F3D40D13BF1AF50075773B6A736BF02637E638862D6F05C6A6B084B0FC25C12B27E6ECB944691B3EB8637069CDFBF38FC3BCE8D6EFEF7E13F79546FF77FC87385C45D585ED9E8941EEFA6D3BB090CD22E51BF247879D14AB57A58
	ACFEBF99E895AF23FE83328737C7F0A4075EFFF9D9A93F0A62C3DFD64A1F1B0D72A300367D95149F1CB338FE9A333B2FFCBE2FF8DB6B0FF02A65731778C33C66DBF2C4AD1FC662DF68B55F3A6BEA79E2A5FEE12E37FC31E5EA79D2A57EEC15FCEC3D60C37DCFB1509B67629A2211FE2F3BFD2E7C6E74BA725981C956E659F3C056502F6B1E789748A84FF13529DE763CA0FB68E53FB324B2243567EEDC977672FE1EC4FDEF54DC795D69397CDB130BB85FFB549F67C6431AB09AF1ADC7112F1A63F6D09F6D8769A0
	0E7B45F5648B6538C4FAD62B1D2751254E63F7A44A11AFE39E460C93CE295F611BC9ED5686FC3D1C77F2167C8E9FDBBFE8F68FE40D97BC4F7D8ED044BC659A6F89277B9A6F0F4E012C7114F7162266A957F81B0E3A2F05DE6C95F8DB2E9C7DB9F731964ED32E710A2F3B4BDA7613CB56446BBF67AED64CBC659A6F53F2F7D99B2E3BE4DDAF9F489ACFA9ABEE1EEB0D474A6EA60F125C6A2C1FF3CF0B15B7A5C160E5C7A63A1E7F2CF5BD2F53391E7712AE97585FA51BF906FC3CBEF81910A48ABA766DB4F8E09FD8
	F35805753CD6B574154A45DD7B0B92FFBFB9B1C1505688BA760AE90D20A56F095688C974B5529AA1D1345A1CB6493434120A929CBF8A5784D2D31DC4A1AF036AB8B6DE0E95058F2445CEC666DED1D81B8D4B2B8C697B93B922308ED6C9997CD393C5223011A0244FC4E4306FC494494B58B748B2C083EB5D7A653909DF7DB16A4DB30EC4C1D7A3FC528A1853C6BB7D1A0051C03E06AD7D9922AAE1354DEA82A260C1BD8E7A136490FB4C46FAA135759479739C8E51E8A8456F94B8C4E1937D9383FB48EB7BFB2A75
	0EF6113C53FB38DE50DA489FCC92BEE2E4335E523607FD3460B055F1FCE9B3B586E20A71D05F0D343E2414BCDE4A4627566F58DA9B9316A8A4D4899F10BF2ED625EF01F656882BC554005376F589ADA8195EBEA623DE8B45FD461BCF9A1BC75EF067153E6C9C6F3BF6695AAA68BE27DD58C3B949884A639C0672F63D9DC4996DB5A27983D0D67BF6CE3B698BF1674B47B61FCD906225C692D3586034017D356ABBCC669EF828G5DDA5B281124C2123D8F4C6D12A8A32F8A5AA33E316C05C93FAD0E0B05422A04A4
	7A678820CA9BDCDC47BBBBAE9A6D37855F73791A510B49E7EB1DB652DCACF935E857DB9C64588B8A4FD03499C93C6AF93FFAA17F1E877E7BFBDAF5D7D6D15842BA89326EE9B3DABB761436361AACC6DEG7498947FB9AF1E7064A9F8BEF20E5AD43B607D0BE0E195C2E2C7FEBE7DF7AF7DF79F79F7AF456C25183D7B205B6204BE7FD3521D58E6EA16B4719931A1A7F9CAF372810903121E9BF2AC992264BA7FCDG9F3BE1BF122C46457B290425373C5D305497929FC8316BBC4C18E0BF7234C7027E18DB85C9FD
	C27E791C7C73854D14C17F4D9C30CEC82E9D8ADD6CF169025E0916EA67EADF12248BB2D5EC6658E429552B5CF8490E518629248F5C30DAC997DA0553DBC7360B40D7E5506D9E624E0803420476CB780F17A3C8659019BA37DA137A7D40F50A35CE11785D5B325623FEC9929F79B0C7520DD7697F538E2DF27EEB95065F014B39AE7FDE680ADB533CFEF3B4658D695DD47C959E0DE75F57F972FB22343300F73E015F4879607BAF856F5FEFBB5D21B71BC53D79F22945C03E65D656A39AE5FF536BFBD23E83F9A422
	74B73D3EA7753EA44B733FD0CB87881A63A35C51A3GGA0F5GGD0CB818294G94G88G88G5DF854AC1A63A35C51A3GGA0F5GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG8BA4GGGG
**end of data**/
}
}
