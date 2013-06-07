package com.cannontech.loadcontrol.gui.manualentry;

/**
 * Insert the type's description here.
 * Creation date: (4/17/2001 2:09:06 PM)
 * @author: 
 */
import com.cannontech.messaging.message.loadcontrol.data.CurtailCustomer;
import com.cannontech.messaging.message.loadcontrol.data.CiCustomerBase;

public class LMCurtailCustomerInfoPanel extends javax.swing.JPanel implements java.awt.event.ActionListener 
{
	private CiCustomerBase lmGroup = null;
	private javax.swing.JLabel ivjJLabelAckDateTime = null;
	private javax.swing.JLabel ivjJLabelAckLate = null;
	private javax.swing.JLabel ivjJLabelAckPerson = null;
	private javax.swing.JLabel ivjJLabelAckStatus = null;
	private javax.swing.JLabel ivjJLabelIPAddress = null;
	private javax.swing.JLabel ivjJLabelName = null;
	private javax.swing.JLabel ivjJLabelNotes = null;
	private javax.swing.JLabel ivjJLabelPDL = null;
	private javax.swing.JLabel ivjJLabelTimeZone = null;
	private javax.swing.JLabel ivjJLabelUserIDName = null;
	private javax.swing.JPanel ivjJPanelAckPanel = null;
	private javax.swing.JButton ivjJButtonOk = null;
	private javax.swing.JLabel ivjJLabelActualAckPerson = null;
	private javax.swing.JLabel ivjJLabelActualAckStatus = null;
	private javax.swing.JLabel ivjJLabelActualDateTime = null;
	private javax.swing.JLabel ivjJLabelActualIPAddress = null;
	private javax.swing.JLabel ivjJLabelActualLateResponse = null;
	private javax.swing.JLabel ivjJLabelActualName = null;
	private javax.swing.JLabel ivjJLabelActualNotes = null;
	private javax.swing.JLabel ivjJLabelActualPDL = null;
	private javax.swing.JLabel ivjJLabelActualTimeZone = null;
	private javax.swing.JLabel ivjJLabelActualUserName = null;
	private javax.swing.JLabel ivjJLabelKw = null;
/**
 * LMGroupInfoPanel constructor comment.
 */
public LMCurtailCustomerInfoPanel() {
	super();
	initialize();
}
/**
 * LMGroupInfoPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public LMCurtailCustomerInfoPanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * LMGroupInfoPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public LMCurtailCustomerInfoPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * LMGroupInfoPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public LMCurtailCustomerInfoPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJButtonOk()) 
		connEtoC1(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JButtonOk.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupInfoPanel.jButtonOk_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonOk_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the JButtonOk property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonOk() {
	if (ivjJButtonOk == null) {
		try {
			ivjJButtonOk = new javax.swing.JButton();
			ivjJButtonOk.setName("JButtonOk");
			ivjJButtonOk.setMnemonic('o');
			ivjJButtonOk.setText("Ok");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonOk;
}
/**
 * Return the JLabelAckDateTime property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelAckDateTime() {
	if (ivjJLabelAckDateTime == null) {
		try {
			ivjJLabelAckDateTime = new javax.swing.JLabel();
			ivjJLabelAckDateTime.setName("JLabelAckDateTime");
			ivjJLabelAckDateTime.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJLabelAckDateTime.setText("Date/Time:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelAckDateTime;
}
/**
 * Return the JLabelAckLate property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelAckLate() {
	if (ivjJLabelAckLate == null) {
		try {
			ivjJLabelAckLate = new javax.swing.JLabel();
			ivjJLabelAckLate.setName("JLabelAckLate");
			ivjJLabelAckLate.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJLabelAckLate.setText("Late Response:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelAckLate;
}
/**
 * Return the JLabelAckPerson property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelAckPerson() {
	if (ivjJLabelAckPerson == null) {
		try {
			ivjJLabelAckPerson = new javax.swing.JLabel();
			ivjJLabelAckPerson.setName("JLabelAckPerson");
			ivjJLabelAckPerson.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJLabelAckPerson.setText("Person:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelAckPerson;
}
/**
 * Return the JLabelAckStatus property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelAckStatus() {
	if (ivjJLabelAckStatus == null) {
		try {
			ivjJLabelAckStatus = new javax.swing.JLabel();
			ivjJLabelAckStatus.setName("JLabelAckStatus");
			ivjJLabelAckStatus.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJLabelAckStatus.setText("Status:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelAckStatus;
}
/**
 * Return the JLabelActualAckPerson property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelActualAckPerson() {
	if (ivjJLabelActualAckPerson == null) {
		try {
			ivjJLabelActualAckPerson = new javax.swing.JLabel();
			ivjJLabelActualAckPerson.setName("JLabelActualAckPerson");
			ivjJLabelActualAckPerson.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelActualAckPerson.setText("(null)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelActualAckPerson;
}
/**
 * Return the JLabelActualAckStatus property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelActualAckStatus() {
	if (ivjJLabelActualAckStatus == null) {
		try {
			ivjJLabelActualAckStatus = new javax.swing.JLabel();
			ivjJLabelActualAckStatus.setName("JLabelActualAckStatus");
			ivjJLabelActualAckStatus.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelActualAckStatus.setText("(null)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelActualAckStatus;
}
/**
 * Return the JLabelActualDateTime property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelActualDateTime() {
	if (ivjJLabelActualDateTime == null) {
		try {
			ivjJLabelActualDateTime = new javax.swing.JLabel();
			ivjJLabelActualDateTime.setName("JLabelActualDateTime");
			ivjJLabelActualDateTime.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelActualDateTime.setText("(null)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelActualDateTime;
}
/**
 * Return the JLabelActualIPAddress property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelActualIPAddress() {
	if (ivjJLabelActualIPAddress == null) {
		try {
			ivjJLabelActualIPAddress = new javax.swing.JLabel();
			ivjJLabelActualIPAddress.setName("JLabelActualIPAddress");
			ivjJLabelActualIPAddress.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelActualIPAddress.setText("(null)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelActualIPAddress;
}
/**
 * Return the JLabelActualLateResponse property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelActualLateResponse() {
	if (ivjJLabelActualLateResponse == null) {
		try {
			ivjJLabelActualLateResponse = new javax.swing.JLabel();
			ivjJLabelActualLateResponse.setName("JLabelActualLateResponse");
			ivjJLabelActualLateResponse.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelActualLateResponse.setText("(null)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelActualLateResponse;
}
/**
 * Return the JLabelActualName property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelActualName() {
	if (ivjJLabelActualName == null) {
		try {
			ivjJLabelActualName = new javax.swing.JLabel();
			ivjJLabelActualName.setName("JLabelActualName");
			ivjJLabelActualName.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelActualName.setText("(null)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelActualName;
}
/**
 * Return the JLabelActualNotes property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelActualNotes() {
	if (ivjJLabelActualNotes == null) {
		try {
			ivjJLabelActualNotes = new javax.swing.JLabel();
			ivjJLabelActualNotes.setName("JLabelActualNotes");
			ivjJLabelActualNotes.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelActualNotes.setText("(null)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelActualNotes;
}
/**
 * Return the JLabelActualPDL property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelActualPDL() {
	if (ivjJLabelActualPDL == null) {
		try {
			ivjJLabelActualPDL = new javax.swing.JLabel();
			ivjJLabelActualPDL.setName("JLabelActualPDL");
			ivjJLabelActualPDL.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelActualPDL.setText("(null)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelActualPDL;
}
/**
 * Return the JLabelActualTimeZone property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelActualTimeZone() {
	if (ivjJLabelActualTimeZone == null) {
		try {
			ivjJLabelActualTimeZone = new javax.swing.JLabel();
			ivjJLabelActualTimeZone.setName("JLabelActualTimeZone");
			ivjJLabelActualTimeZone.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelActualTimeZone.setText("(null)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelActualTimeZone;
}
/**
 * Return the JLabelActualUserName property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelActualUserName() {
	if (ivjJLabelActualUserName == null) {
		try {
			ivjJLabelActualUserName = new javax.swing.JLabel();
			ivjJLabelActualUserName.setName("JLabelActualUserName");
			ivjJLabelActualUserName.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelActualUserName.setText("(null)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelActualUserName;
}
/**
 * Return the JLabelIPAddress property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelIPAddress() {
	if (ivjJLabelIPAddress == null) {
		try {
			ivjJLabelIPAddress = new javax.swing.JLabel();
			ivjJLabelIPAddress.setName("JLabelIPAddress");
			ivjJLabelIPAddress.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJLabelIPAddress.setText("IP Address:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelIPAddress;
}
/**
 * Return the JLabelKw property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelKw() {
	if (ivjJLabelKw == null) {
		try {
			ivjJLabelKw = new javax.swing.JLabel();
			ivjJLabelKw.setName("JLabelKw");
			ivjJLabelKw.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJLabelKw.setText("(kw)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelKw;
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
			ivjJLabelName.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJLabelName.setText("Name:");
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
 * Return the JLabelNotes property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelNotes() {
	if (ivjJLabelNotes == null) {
		try {
			ivjJLabelNotes = new javax.swing.JLabel();
			ivjJLabelNotes.setName("JLabelNotes");
			ivjJLabelNotes.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJLabelNotes.setText("Notes:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelNotes;
}
/**
 * Return the JLabelPDL property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelPDL() {
	if (ivjJLabelPDL == null) {
		try {
			ivjJLabelPDL = new javax.swing.JLabel();
			ivjJLabelPDL.setName("JLabelPDL");
			ivjJLabelPDL.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJLabelPDL.setText("Pre-Determined Demand Limit:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelPDL;
}
/**
 * Return the JLabelTimeZone property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelTimeZone() {
	if (ivjJLabelTimeZone == null) {
		try {
			ivjJLabelTimeZone = new javax.swing.JLabel();
			ivjJLabelTimeZone.setName("JLabelTimeZone");
			ivjJLabelTimeZone.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJLabelTimeZone.setText("Time Zone:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelTimeZone;
}
/**
 * Return the JLabelUserIDName property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelUserIDName() {
	if (ivjJLabelUserIDName == null) {
		try {
			ivjJLabelUserIDName = new javax.swing.JLabel();
			ivjJLabelUserIDName.setName("JLabelUserIDName");
			ivjJLabelUserIDName.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJLabelUserIDName.setText("Username:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelUserIDName;
}
/**
 * Return the JPanelAckPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelAckPanel() {
	if (ivjJPanelAckPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Acknowledgement");
			ivjJPanelAckPanel = new javax.swing.JPanel();
			ivjJPanelAckPanel.setName("JPanelAckPanel");
			ivjJPanelAckPanel.setBorder(ivjLocalBorder);
			ivjJPanelAckPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabelAckStatus = new java.awt.GridBagConstraints();
			constraintsJLabelAckStatus.gridx = 1; constraintsJLabelAckStatus.gridy = 2;
			constraintsJLabelAckStatus.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelAckStatus.ipadx = 10;
			constraintsJLabelAckStatus.insets = new java.awt.Insets(3, 11, 2, 49);
			getJPanelAckPanel().add(getJLabelAckStatus(), constraintsJLabelAckStatus);

			java.awt.GridBagConstraints constraintsJLabelAckDateTime = new java.awt.GridBagConstraints();
			constraintsJLabelAckDateTime.gridx = 1; constraintsJLabelAckDateTime.gridy = 1;
			constraintsJLabelAckDateTime.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelAckDateTime.ipadx = 6;
			constraintsJLabelAckDateTime.insets = new java.awt.Insets(0, 11, 3, 33);
			getJPanelAckPanel().add(getJLabelAckDateTime(), constraintsJLabelAckDateTime);

			java.awt.GridBagConstraints constraintsJLabelAckPerson = new java.awt.GridBagConstraints();
			constraintsJLabelAckPerson.gridx = 1; constraintsJLabelAckPerson.gridy = 3;
			constraintsJLabelAckPerson.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelAckPerson.ipadx = 7;
			constraintsJLabelAckPerson.insets = new java.awt.Insets(3, 11, 4, 48);
			getJPanelAckPanel().add(getJLabelAckPerson(), constraintsJLabelAckPerson);

			java.awt.GridBagConstraints constraintsJLabelAckLate = new java.awt.GridBagConstraints();
			constraintsJLabelAckLate.gridx = 1; constraintsJLabelAckLate.gridy = 5;
			constraintsJLabelAckLate.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelAckLate.ipadx = 10;
			constraintsJLabelAckLate.insets = new java.awt.Insets(3, 11, 11, 1);
			getJPanelAckPanel().add(getJLabelAckLate(), constraintsJLabelAckLate);

			java.awt.GridBagConstraints constraintsJLabelIPAddress = new java.awt.GridBagConstraints();
			constraintsJLabelIPAddress.gridx = 1; constraintsJLabelIPAddress.gridy = 4;
			constraintsJLabelIPAddress.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelIPAddress.ipadx = 10;
			constraintsJLabelIPAddress.insets = new java.awt.Insets(4, 11, 2, 24);
			getJPanelAckPanel().add(getJLabelIPAddress(), constraintsJLabelIPAddress);

			java.awt.GridBagConstraints constraintsJLabelActualAckPerson = new java.awt.GridBagConstraints();
			constraintsJLabelActualAckPerson.gridx = 2; constraintsJLabelActualAckPerson.gridy = 3;
			constraintsJLabelActualAckPerson.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelActualAckPerson.ipadx = 142;
			constraintsJLabelActualAckPerson.ipady = -2;
			constraintsJLabelActualAckPerson.insets = new java.awt.Insets(3, 2, 4, 114);
			getJPanelAckPanel().add(getJLabelActualAckPerson(), constraintsJLabelActualAckPerson);

			java.awt.GridBagConstraints constraintsJLabelActualIPAddress = new java.awt.GridBagConstraints();
			constraintsJLabelActualIPAddress.gridx = 2; constraintsJLabelActualIPAddress.gridy = 4;
			constraintsJLabelActualIPAddress.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelActualIPAddress.ipadx = 142;
			constraintsJLabelActualIPAddress.ipady = -2;
			constraintsJLabelActualIPAddress.insets = new java.awt.Insets(4, 2, 2, 114);
			getJPanelAckPanel().add(getJLabelActualIPAddress(), constraintsJLabelActualIPAddress);

			java.awt.GridBagConstraints constraintsJLabelActualLateResponse = new java.awt.GridBagConstraints();
			constraintsJLabelActualLateResponse.gridx = 2; constraintsJLabelActualLateResponse.gridy = 5;
			constraintsJLabelActualLateResponse.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelActualLateResponse.ipadx = 142;
			constraintsJLabelActualLateResponse.ipady = -2;
			constraintsJLabelActualLateResponse.insets = new java.awt.Insets(3, 2, 11, 114);
			getJPanelAckPanel().add(getJLabelActualLateResponse(), constraintsJLabelActualLateResponse);

			java.awt.GridBagConstraints constraintsJLabelActualAckStatus = new java.awt.GridBagConstraints();
			constraintsJLabelActualAckStatus.gridx = 2; constraintsJLabelActualAckStatus.gridy = 2;
			constraintsJLabelActualAckStatus.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelActualAckStatus.ipadx = 142;
			constraintsJLabelActualAckStatus.ipady = -2;
			constraintsJLabelActualAckStatus.insets = new java.awt.Insets(3, 2, 2, 114);
			getJPanelAckPanel().add(getJLabelActualAckStatus(), constraintsJLabelActualAckStatus);

			java.awt.GridBagConstraints constraintsJLabelActualDateTime = new java.awt.GridBagConstraints();
			constraintsJLabelActualDateTime.gridx = 2; constraintsJLabelActualDateTime.gridy = 1;
			constraintsJLabelActualDateTime.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelActualDateTime.ipadx = 142;
			constraintsJLabelActualDateTime.ipady = -2;
			constraintsJLabelActualDateTime.insets = new java.awt.Insets(0, 2, 3, 114);
			getJPanelAckPanel().add(getJLabelActualDateTime(), constraintsJLabelActualDateTime);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelAckPanel;
}
/**
 * Insert the method's description here.
 * Creation date: (4/17/2001 2:12:39 PM)
 * @return com.cannontech.loadcontrol.data.LMCICustomerBase
 */
public CiCustomerBase getLmGroup() {
	return lmGroup;
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
	getJButtonOk().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("LMGroupInfoPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(427, 346);

		java.awt.GridBagConstraints constraintsJLabelName = new java.awt.GridBagConstraints();
		constraintsJLabelName.gridx = 1; constraintsJLabelName.gridy = 1;
		constraintsJLabelName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelName.ipadx = 8;
		constraintsJLabelName.insets = new java.awt.Insets(21, 16, 3, 27);
		add(getJLabelName(), constraintsJLabelName);

		java.awt.GridBagConstraints constraintsJLabelPDL = new java.awt.GridBagConstraints();
		constraintsJLabelPDL.gridx = 1; constraintsJLabelPDL.gridy = 3;
		constraintsJLabelPDL.gridwidth = 2;
		constraintsJLabelPDL.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelPDL.ipadx = 7;
		constraintsJLabelPDL.insets = new java.awt.Insets(4, 16, 6, 3);
		add(getJLabelPDL(), constraintsJLabelPDL);

		java.awt.GridBagConstraints constraintsJLabelTimeZone = new java.awt.GridBagConstraints();
		constraintsJLabelTimeZone.gridx = 1; constraintsJLabelTimeZone.gridy = 2;
		constraintsJLabelTimeZone.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelTimeZone.ipadx = 7;
		constraintsJLabelTimeZone.insets = new java.awt.Insets(3, 16, 5, 2);
		add(getJLabelTimeZone(), constraintsJLabelTimeZone);

		java.awt.GridBagConstraints constraintsJLabelUserIDName = new java.awt.GridBagConstraints();
		constraintsJLabelUserIDName.gridx = 1; constraintsJLabelUserIDName.gridy = 5;
		constraintsJLabelUserIDName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelUserIDName.ipadx = 3;
		constraintsJLabelUserIDName.insets = new java.awt.Insets(10, 16, 9, 2);
		add(getJLabelUserIDName(), constraintsJLabelUserIDName);

		java.awt.GridBagConstraints constraintsJLabelNotes = new java.awt.GridBagConstraints();
		constraintsJLabelNotes.gridx = 1; constraintsJLabelNotes.gridy = 6;
		constraintsJLabelNotes.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelNotes.ipadx = 9;
		constraintsJLabelNotes.insets = new java.awt.Insets(10, 16, 11, 26);
		add(getJLabelNotes(), constraintsJLabelNotes);

		java.awt.GridBagConstraints constraintsJPanelAckPanel = new java.awt.GridBagConstraints();
		constraintsJPanelAckPanel.gridx = 1; constraintsJPanelAckPanel.gridy = 4;
		constraintsJPanelAckPanel.gridwidth = 5;
		constraintsJPanelAckPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelAckPanel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelAckPanel.weightx = 1.0;
		constraintsJPanelAckPanel.weighty = 1.0;
		constraintsJPanelAckPanel.ipadx = 396;
		constraintsJPanelAckPanel.ipady = 127;
		constraintsJPanelAckPanel.insets = new java.awt.Insets(6, 16, 9, 15);
		add(getJPanelAckPanel(), constraintsJPanelAckPanel);

		java.awt.GridBagConstraints constraintsJButtonOk = new java.awt.GridBagConstraints();
		constraintsJButtonOk.gridx = 5; constraintsJButtonOk.gridy = 7;
		constraintsJButtonOk.anchor = java.awt.GridBagConstraints.SOUTHEAST;
		constraintsJButtonOk.ipadx = 34;
		constraintsJButtonOk.insets = new java.awt.Insets(12, 3, 15, 12);
		add(getJButtonOk(), constraintsJButtonOk);

		java.awt.GridBagConstraints constraintsJLabelActualUserName = new java.awt.GridBagConstraints();
		constraintsJLabelActualUserName.gridx = 2; constraintsJLabelActualUserName.gridy = 5;
		constraintsJLabelActualUserName.gridwidth = 2;
		constraintsJLabelActualUserName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelActualUserName.ipadx = 96;
		constraintsJLabelActualUserName.ipady = -2;
		constraintsJLabelActualUserName.insets = new java.awt.Insets(9, 3, 10, 73);
		add(getJLabelActualUserName(), constraintsJLabelActualUserName);

		java.awt.GridBagConstraints constraintsJLabelActualName = new java.awt.GridBagConstraints();
		constraintsJLabelActualName.gridx = 2; constraintsJLabelActualName.gridy = 1;
		constraintsJLabelActualName.gridwidth = 2;
		constraintsJLabelActualName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelActualName.ipadx = 142;
		constraintsJLabelActualName.ipady = -2;
		constraintsJLabelActualName.insets = new java.awt.Insets(22, 8, 2, 22);
		add(getJLabelActualName(), constraintsJLabelActualName);

		java.awt.GridBagConstraints constraintsJLabelActualTimeZone = new java.awt.GridBagConstraints();
		constraintsJLabelActualTimeZone.gridx = 2; constraintsJLabelActualTimeZone.gridy = 2;
		constraintsJLabelActualTimeZone.gridwidth = 2;
		constraintsJLabelActualTimeZone.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelActualTimeZone.ipadx = 142;
		constraintsJLabelActualTimeZone.ipady = -2;
		constraintsJLabelActualTimeZone.insets = new java.awt.Insets(4, 8, 4, 22);
		add(getJLabelActualTimeZone(), constraintsJLabelActualTimeZone);

		java.awt.GridBagConstraints constraintsJLabelActualPDL = new java.awt.GridBagConstraints();
		constraintsJLabelActualPDL.gridx = 3; constraintsJLabelActualPDL.gridy = 3;
		constraintsJLabelActualPDL.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelActualPDL.ipadx = 52;
		constraintsJLabelActualPDL.ipady = -2;
		constraintsJLabelActualPDL.insets = new java.awt.Insets(4, 4, 6, 3);
		add(getJLabelActualPDL(), constraintsJLabelActualPDL);

		java.awt.GridBagConstraints constraintsJLabelActualNotes = new java.awt.GridBagConstraints();
		constraintsJLabelActualNotes.gridx = 2; constraintsJLabelActualNotes.gridy = 6;
		constraintsJLabelActualNotes.gridwidth = 4;
		constraintsJLabelActualNotes.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelActualNotes.ipadx = 292;
		constraintsJLabelActualNotes.ipady = -2;
		constraintsJLabelActualNotes.insets = new java.awt.Insets(10, 3, 11, 17);
		add(getJLabelActualNotes(), constraintsJLabelActualNotes);

		java.awt.GridBagConstraints constraintsJLabelKw = new java.awt.GridBagConstraints();
		constraintsJLabelKw.gridx = 4; constraintsJLabelKw.gridy = 3;
		constraintsJLabelKw.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelKw.ipadx = 9;
		constraintsJLabelKw.insets = new java.awt.Insets(4, 3, 6, 3);
		add(getJLabelKw(), constraintsJLabelKw);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
public void jButtonOk_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	com.cannontech.clientutils.CTILogger.info("Pressing OK does nothing, so override me if you want some action.");
	
	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		LMCurtailCustomerInfoPanel aLMGroupInfoPanel;
		aLMGroupInfoPanel = new LMCurtailCustomerInfoPanel();
		frame.setContentPane(aLMGroupInfoPanel);
		frame.setSize(aLMGroupInfoPanel.getSize());
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
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (4/17/2001 2:12:39 PM)
 * @param newLmGroup com.cannontech.loadcontrol.data.LMCICustomerBase
 */
public void setLmGroup(CiCustomerBase newLmGroup) 
{
	lmGroup = newLmGroup;

	if( getLmGroup() instanceof CurtailCustomer )
	{
		final CurtailCustomer customer = (CurtailCustomer)getLmGroup();

		javax.swing.SwingUtilities.invokeLater( new Runnable()
		{

			public void run()
			{
				getJLabelActualName().setText( customer.getCompanyName() );
				getJLabelActualTimeZone().setText( customer.getTimeZone() );
				getJLabelActualPDL().setText( String.valueOf(customer.getCustomerDemandLevel()) );

				com.cannontech.clientutils.commonutils.ModifiedDate date = new com.cannontech.clientutils.commonutils.ModifiedDate(customer.getAckDateTime().getTime());
				getJLabelActualDateTime().setText( date.toString() );

				getJLabelActualAckStatus().setText( customer.getAckStatus() );
				getJLabelActualAckPerson().setText( customer.getNameOfAckPerson() );
				getJLabelActualIPAddress().setText( customer.getIpAddress() );
				getJLabelActualLateResponse().setText( String.valueOf(customer.getAckLateFlag()));
				getJLabelActualUserName().setText( customer.getUserIDname() );
				getJLabelActualNotes().setText( customer.getCurtailmentNotes() );
			}
		});

	}

}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G55F854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DDC8BD4D45739560498B426EAC3DAEA30A13ECA9A5258562CC592F20B2CA4B50DD92537A6B5375EDB5B5A1626264BF5CB6FB28DB759B38383A88AAA6A082048C33121BC549003244AC385B309F3A382C9C843489CE0E418B34E9C848C6A7D77731C994E0058C7F079F34E7E7E7DBF76737CFB6FF386656C0BCCB598F390B224A2766FED83C2119D885DBB3DF1994726C871E62479379FE8AB3A3BAF852E
	33015734AC5E1C0DE61CCA0774C2C80FFCAB5E3C8B6E3723D927D6B441CD90BB8B0117BD727CB3E35B39EF973713C6524BD0A6DC37GC581062FC992367F9232B0FC8543B9990C882D6D46F912D09E430EC15A86948934341B66DF81578CE2790E1AE3ACDF471CC814740303464DD80FC6A727329EAAEBE357827408E1C38F3615EF48E4B221100E834205B72101CF6B60DAEB6B6B79EB175CDF5EE5F73BE537A2F575163BE43B238B6E3D322B3CE740D95EEFF78F58DD92G43B5B556774FF86581CF333BDB3659
	5D128BF4A83D580EE4C8A7FAC1A7241193E361C6ECBB865289C009827F4C0471C6386FGD21C3C3C6F9E2CE565E97F7C91147C9337A9ABC8F9E7BAE9B917BA5572A618DECB5BCD5A6627965C866B0117F3A95EDC8AE485F2814D845AC37A44AFF67D992EF5971586393FDFF657F9BC75AE0777FD59A139603E2686B88C7BD06A123D8E04E83B97994AF47C990B28DC550465E81DC49D61FDA07A4345E875F75FB3266954496A28CA6342CB34AEE2AF29F5C2EDDF8AEAFB75A57FBE6AEF3B8DE9ED2F1BA61BD2F5EC
	AFFD6110F1BD531352275ABE5A075BBA1334F5B15C9BD87F2078CE028FE9F05ADF3FCE7A8CCE0782EFDD9F6F9B6DDB38AF561439A83B4A2ADB8F6171F5C61B0B7A506B8A74654744E618AB50177FA478C2D720AF12097B3294F8167EF8B36DDFEBF371789D01F4B850922065C009C0A9EE486766FD6CCDCB45CD76B11B5D2B548FB8DD8E490B286CDA4BCE38CAC30A575E6C76A9F6F71754EA77BA6D97DD128F31BC2FD8F09DDC74137BEF52773B40469949ADF96D0AD3F603F6FB3F24C8DE9F4D731AE5AB4E6315
	88374559AF0101FE8F62FACD3B374355E577A997BC8E3BA229B9A8FF53EE5CA6D76C2D20919440BD2D172DF9383E0EC179EDC01D229E2691FD6FC9DE18B0E08A11653E81CF8BAE89DA624675BC154C9D8A5C4FF473BA5E1C0F31A4C8E74ADC4F133B37E974B4BA3B307BF66FB09FE35BCCF80EAE10F91F79FDBB6FB30D1B972132974E6A7699CC35B26DABADF2605849A3BA31DFB43D5058FE136337C0569FDB634F95CF5E348D538475EF91ED7579DE5CD6A360E3B8D00C28638D3B8BE81F2D377BA45ECB4FF921
	03C98E3546291FAF113AB1883FFFC95289B22F2BBED3FB107A7EC7903591C2EC5CCABD5C8C51BA79FDAEFE8EC9D7609958A8F9A43783EEFC0C7747DC5C5758B86D16054CEB84774038EE0001649338E18F9EFBF6C571BAAF8EA852F9C9D11C6E9E151F31875B728DDC7428D38237153D874F3F72A00CEB11FF479EBCA660F96D16C8638891465FC5462464127A416986F9402DD0BCBF8F37D51FB44C66047DF9B86839E2F78D6079054688CB6C1BF0993C4EFEE8F2BCC521A68FF92E9BAC1ABA5AG6D0DE31DBC60
	D5FA78589BAC62E32F786A83A86E3392235E33A3FCD123F136070E13059EF56CD11F36656B7BEEA1F38343FD5457DF35E743B5B0BC21FDFD69BE2C67A9503F8AE83D07777530FC4C6773896B648D324BC52BD25471543DD837CF3ABCA041B40CA72E77C43D056E5D9640EBE5F5C879337762BAF4E9663C27AE63BA4CC961B1E0BA248B00CEDE2675B865B22D87DA2EBF9C42F5CD6234CB76AB7621F25FA074171A1A3324FCB476B29A53D97EECAF0F3172D2F1191BA05D89B464656D72234DBC468A6B0BC4EB9664
	860DA9E7FBD90C65D55B25E3AB0F31DEAFA073B370D2FC3CBEB76D534E73F5BE1F5C65A40F98D6970F6F53369B453E37EFA3DC3B1DDE1F528C83EB08575B537B303F789944E076CCFEB7A513155582F6EDC0CF79B0DEDC1F2371BD54C71FA5CB60EAA27A96269C46630EB4ECB93D58A4EF376C6D179C2538E64B6D03CAB9996365F504FD8A5F9F626D1054087B73257A81C5115D677ABE2B8BD0E18D2E637821D6DA7773371FE47E2D7856DCD4FBE33BD1AF3E8D4D4AB7E6AA344E0FAAEA5D0FFC47ECAAFD6535B4
	BA6F259983E33AA90A7A3C89D3C6BFFBE82C13C87AC514C2E31DBD82FF1D60099A1C56F7A141F13AF4G5A7A8AFFBE5FDD087B659AC8A783658C461B4B002CC02EC15E9F5EAFB8024609FF1AE14D61F55BDD677DA6BC9E076CB751B90A2553762772B98A71793FA1A5309EC2877D4B3EB0A0CD657EEF145C5E007472GB95A8F324E6F569F0F34E7A03E9E7A24BE1D59DAB348475E35535A31971BF55838A94858338CD15B470654766F956B1B370B715CB1843CB020E82065C06B005207219D06F97D4F597F6EF8
	75CFBAE80B5CGD8934CD18D014F911027B15ADEB247D8C1F7A79045FEEB42D88824A30679B37F7729C7619A8769D5C06B01B22F429A8C68A4D087108294863484A881E89DD06667DCBE450C658BA0FD922893E88AD0A4D05C67F41EDC8B571411F8F3B1D0A31083A8E4044B1726A630BAD4185DE04F76F54CFEA00EFD8EE37892665B49B17468D1478473F342F5056B0E27D731FBDA364FC459F08C31E98CBD381F6C524153C83F2077BB58385EBCC19F33EE21DC18B210FF1C160A34B156D78A0347D8738A75E3
	2C85057AB1560305DBA98EBA34ABAB4E7FF66158B158E305FAB1D8BE717F591DD9C4374FA7794860E934ABF6467B0D026BF4D4AC7681AC3ED85937232BA999AC4FA9C5EE2892ED5BD99FB89FAD04BE9DB3427604C6C677A7BA977CDB89FE6E2D81FEB2D04EB5987FC056EBFC8EB8D7B46E9CFCEAE80CB9382EB8F83BB4956B374B874538CF796481EF1794D877970AAD02274EB64D0A540F2B0966799B11D76C5E9EE9D45BC9C41E7202493B0B0335AFED03370CB856F559076581854658B5BC17D90DBC26F5C1BA
	84A89AA86E1AB6967B76A09EF35D301886F567BDD297DAFA9D4B2294AEDBG6923C0ADC08AD058F59EFFE4B563B8672C55FED1F2518EC1F1F9A0C36036C62B70713642AD82476B6F0FE5371049BF11ADF897FC12373991EB24BC656DCD2A9DD901C01462BD4709BE92B556F575314DBB4A7B6CBA710147B64207939DB9C29744B383F6E2CD357512BBBB002F72EE20C0D9DE8A26BBABAB000FCBCFF935E4FE7353CBCA427DDA541FAE786F8D52782F4E2C0D7F122F53782F60860F8FCFD97478C785FFFB339E3FC9
	705F5927476F907CE32DFAFC8F70691A49529638E6427C9060078B9D36DCBD9DCB843FF8C40F7F1460076B72D7897EC15DBADAA7789F276B715194D1C73A7C1CA9BC9E98A4B1CFA924EB8596C7B09B24BB85363F01478D2EA9FC1EFA26A478BC73FC097EBC7363927D797F251231677F1F17841B9FE84C2B98791EF9AC7817B4056F19D7F56238CB44FEB85A2F29B963F5BA6A6DBDD6B21B5058AF1644CAD83EECAADF0F3DCCF4BAA0BD8294B6154704C387F8CC983DF8AE32CEF9CFF7BD36266530B1FAAA1D0B92
	262AB161DA0889294D83A95CE666ED90975D06311694EDFB613173289121F238D27EB4BFBE2E93D86EE878B1FEFC6CEF1BC94BFF590F1F2A1127E5FB2E0417AD7939A864FA741D20EB4D193731F80BDDED2DC52853121C4656405DC15640E48620659FB273F8BB91F4D0EC444231025BB9F63619E36D82736C6358EC01492D9CDBAEB0DB9B47D088EF573E9A326789E99BD0EF884FFBE7AE4FBBC5E01F0CF0ACC6E0BF9458FA017DCD1425748E0E4DC967D827404E8AAC7C8E7E1C799299EF7DEE899E764E2EB5F7
	60FC0B8876B35CF7C43E388E3C7F234020AA1E064F3B2A7F020B95BA0D4CE58D0DBAED4489BFDF32D8FF42548BB21872D95B0E407DA9245D973CBAEA2DBA9ED13950DB14DBB6E1B93A860EA57B7D8953689A58A07027893EDA0353F9E23D18A79201E73D132F0DF717637E3A8452AB005682A5836505461B0F057279EE70C0A59AA7AEEB11BDD61890DD1AA90C166F4DD1EB571B093D91DA164647635247672318E92F9A022DBF2D21F49C3A58B53125C893F3C6D76072C5GAF86E8B5D092D07AF4F85ECE67657B
	5E01B2DA3ED38E27D227A8762EDE326B438B576A1486D59887D334EC574C5A329D1DCE6DB731EB477451F1B0151BEA514AF9D83E90F68D8FAAF72ED9AB974D7245316B4A20F2837B34F209ACDFB23BE64F88A62734EA650AD93EE36CEA8DAAF721CDAB57897994CD5ED09DB95AF7379F47FDB4E2866D3BBB84FE0060899A1C76694BE4BE4369143B60F9FD97DFD74F4C555A6F18C96DF958B5E4E6B03FBBC734F261ACDFB43B468515DB69A7371265CBE4576420F2DDFE7DA77BEE1A2F18DD0F5D9DCC6EAB69DAB9
	AB4B57492ECAD039F3FEF221ACDF843BAE51112373EFECBD3E9E148F323199B928BD6BBF3671ED5D7DF2CCCFB11D2B58B5764EA22663071FE8F5E44F5DEFB48451117DA5AA1B77A5B5165807685EC0BAD27F51E7C3955EBAE5B6FE1E4967103C076EC30DBFF955D09444060D690E5458A8FEB5196DDF46636233BAFCCEF37FBFB7FD76F5ABC4EDE4197E19B6687CD7D89EBC1EBDD42E9F4FD6146B4733EF150F3DDF71EEF970B32346F26D19911D3F1B0E6038ED9534CB9210E5D63C398C6864ACBEF77FFBF9A1A2
	F3374E3CFD4A5103576D78C4DA61769F2D88DE56580A714F311F2E40EB630BB2ACBF7B31FE0EBFD7015BCB11BD8424D8C2852EA32F3327D721D9997EE2856E37AE291B40F41D91C662288EA817EB96DFFBBC584157A3D3843665D33E9E19A930F08B17DDAD30F29F17DDAF30D9BEAE1BAC306EE6AEEB1D4D31E986318E9258EF8CE29DA430197B38EC2C40222EF3598401655C60322B85F6D7AB17AD1B433147EEF05913824BC5DC56AAB09709EDA3F0DAE0CF9A781E6FCA011508BD5F8DF37833E17771D238B6FD
	B95E6CG8A810A840A85DA85148CD4F08F2CC900FA6F61B2E5C4A69452D1C04B0192015243624D25C0CDC03DC021C0D1E1DCE630A6729646A376C58FF7E870D86637161F4C52CD4CFFADEF15A6EFAFAB1B7F9CB37F16FC4D880227EA6EB7B19BDABE5F13B5A13A0E5372347B3BBB103A7F0B0936E9715FF83FD99E465BB92F0D771B55823B56465BBED1E003A3DC76583D9CBBF21A4BDA8596F61A4B368B6CF7C2B6CAE0060F39EC2C40AEFE4CE5178B6C2A985B162FF06C101D4B1689EC061D4B9E93584369DCB6
	D4E0A5C2B6C2E05304EC14403A056C8601BD66603219DF95E3CC6232162F72F501F494E33D108E79AA1FEBFBAA5F04EB2B7D92DB8160F0D976125996C01BD776C8DEE5182264E4194D611DEFA1B177BDDCD992CC874C58E7E149FC6E62A529CBE93177A07E8C382B9A2F7196029FF1C0EB0112013243E17C81558259009CC0A3C0E1C0D1C031C089C0EB001200523F464BF1657021B14A41B6C9480343EBF762C787735DF6A47833236D48784F0E4EA364CC99667C16E18F5B7F150E607DDD393B5BA709E75425A3
	5982EB7659E41F936F9B53D86CA1B3DE8BF74BEE052E1717113E1587E5AB8522B1109707D6ECFDDC7269D86BE3D3G7175B10F135AC167D6F64F6C11E70D83F00F30B77F9D555EE25FF87652905F239EEDAF92F4E679591BC51ECF51004788FBD947D5FB2F9B463347ED6959CB9CE56FD172CCCB829C4D65768E572A765E3CB11E3DCDE3582B1CCB1F8F2A3D2564B9D88BF81330F761DD55DE959A4F5E66B16C8D014E6D6C1E761F5B5BF0FF3BA8FB49A6A3BDCF18AB76FBC3A0FF8450CA204439749C13667935F8
	0F865DC74F9CACF0AD3B0FBECB282F352671FC4D5054CFA348365FC7FD45E9975C6F061BB7042F16F35AFD773E730ADD9960E784E197347B6EFD0D6C55BFA1BB374ECF5686D1266CE63C02BD1B35BCAB488A39389B2ADC332D4E6170CABEEEB3F9F887725F6BD7B5F3792ACF837328DAF81E4B551600BC58058FA51F87669CF666A0556A58E2B540756CBF9CF8EE412B016E01F79F8F5C83476DB9F49F5FA36FEE52634F947C16D1678C189FA978553A7C25027F2C2E7E65027F27B3FA7C55027F36AEFF3D6067B5
	6871D3A2B87FD03D9E5FA278C53A7CB260533179BE09B5CEC25A9A4147665DFFD547E6D85BD87DBD157575E0F35D8868449F8C7C6F9BEF3CC16DC51298A39470B0E1EF4989555EC3274733E79A435ED320B35F203517F38D5BCBGFC3530775B132A3D610F473317BA063D4CF901769610E715857062F95CDEE63DEA6F817BD8765298851B6BDAC027532FBEDFA471CFA760AEE12F22C135B7EFCCFB38DCF81E4888E2AF82F4B69B3576E691FBD100AF9576BEECD46DD5C9E3594B9827FCEB6729F3B90B930EE2B9
	315626A19763DDBE0A7B36538D21035D653CCADEEDBB8F995DBD14FFFFF570D8E6F17578314CA3557A6B60C72BC72F039F2F56DF8747D5EB5741ECFDDD0D6B3D87E230F6116F87827BC8E0BFA4BAB15666F4A83D82FF01780571A6899B6478CF09DF0384EBE7589A8D769143FED50D1F0FD064DE594BE5DF2EA6B1175365E26F5812BCCE182B95D19FFFA0BEBABDF60D5F9B84067D267D6159E604787CBD2F83FF2F6316868B448BF927A130ABAD2C034AF6951695D441FF922BD259F5E711389FB8276AA974CDCC
	CF38CD551374212A672AE1E2FAF63528FAFE552A6A0938BEB1BD696D2A1E4597D5BD8F5F18181EE73B30AC5D9B0EEF635FC32C07F1D37AF5FEAE7A1BFC1CDF6FDBA27C7D500062F4555434B89517D44FC2A4760E5C46F67E5EEA675779F95AC6723DD2B82417GAD87223637083316B500D96E67E7E02D622C55F6BF5791C27206C2BA92A886E8745E667A93FA986B938EA4FC1CF71C1F31A5881B731AB8169E49313B443962C901758BECC8E0C8E0CB84D6A574A58A4CFDC61CDDBE4031738D626CD2E01D62BD43
	59827BD8E04B9F60F3E6D30F5A87325B8A757B00833A005C831D2012F582AA5B572B76256FDFD2759CBCBDB1BDAF7729FA8E78D4BDFF7CE8E2FA1A95D54F22AB2A1EFC7B4474AC9FD47594DFD5756C1E201EB31F2BFA96DC530CB54744465AB35791627BC74B5B46BB63C373755EEBFDF1DC394077DC591EF8BB164B1B2F3FD796C815100F1EA3DDA76335F1FE6019691D84374D8FBCB34D965FA3F6GAFEEBE5F5B4B3945335E42DB147B64964F08B7103925F8C1E0F98D640C38F2C1E0F9E708F92B96F8118BF8
	F9679F575ACFD9C86B5D422EE58B031DB74DEF524A1DE479DA5835B7285CEC3FF34E9116EFB63BAE8CAA6775130BE179D6306B1A20F229FEF2C9ACDF7AA2F66E34A818DC35DF79AAD93EC6F6ED8FAA67BB2315F331FCD358B5AC28DCFB03D6AE0A650BE5570420F2125F7B1AEBD93E8D6C1A39B8185CE7FEF285AC5FD1F6ED5211236F1646ED47F133536DF23A25867AF59F627B378748F33483E41545FC7F714B6439968A6908453C4F0560EE487C38DBF33F53704FBFF32B789758F85D2816A9CDF326B87BDFF0
	267860BF55865BE7BEAE764BC5BB1BADE23FDCE0BD9631DFAE30471A44FE79B7B8362DC86C178B4CD7A4764B85768DF11E95AAB033100D90D80F100D9258F4A13BC1E03FAB947B65D19C338AD90B407204EC2F406E9732A382C3C2B6D4E06F0B722E9258C779DCF61D409E4F9767D9821BF1064BB6BD483116DCAE5BA130C7F339EC2F406486AE9BA3308FF6F1599582FBE49717DDA53011FAAEDB7ACD0EFDFFA7172D95D82910ED925895A19BAE306F8959E80195885918EF72F7415EED476BBE85C768B8BEC1FC
	9F758219D7529F0AB767BC4473BEC862F512977138854FCCDC6697240FB6C2FE5BC3BC762CA4F38DDD2FDF377CBDFB4BD3C0678E245D6B7D85E9671900078BFB670D2A3D99C5FF4FDE7DF25039594FDE826913AB81DFA36CDDB1E94AB72E3D3176A3322383774E3FC36C6581DE9A4D6DFDADC53557B52E3D31764E5B23836B73943167G5CA36C45262A76F60DEBAFE38CFB11236CDDA16D978DF80C3077B333EA6FC779636DA70DD51F0951017BE55FA5E3BB89F074B037C716104C5E0A5C716C0DD53E4A07836D
	051379209670A6E10F3C364C6CBD336BE676BB031EC50C32C75F758C81FC36305734D135F7E2DCFBE30D3F38D1766EA776D6G3ECA586B5B245AFBFBDCFBE30D3F74EF2976286E95D80C2D2F26211BD8DF9DD557D77F33D5157D532E092D4D48CF9870FD229D2A9EF351847709F62AFA12AC9AFF8AA738CF142B6AF93EC8557333896AF92DD85553F6D055F34F8475DCADD5753C78E7D54F7B7993DBCB3F7A262A67C2392A27A9F7E2FAEE2FD075045428FA6AF7CDCC4FB70E2BFA367CD55573444E096929BD216A
	892FD375FCF702FA1EA0FB7B74F70DA224A737415C798ACAD51071DC1F4F0B320412E632133A664A119A53AF97180CA8259919694EF2B3CA415B434DA8156C9EB7A313A2FB06702FFF70373A710AB5D3C12978EB1E9A8565593C52DF9A716F0774436243D150A8755B5D0E822B335F29GFBA3BE9CAB40DF7660847EC22800891A49E7BB782654BD60F29D3A391B14523E41C3D0164DFE95727483EF7DFA45FF357ACC485C0CF663337A731217EC43CB8E7C899E7BAD1253A843A3BB0189B89454C7AE29BEE558A5
	D9D1CE770778BBFD1FA2B96A78863FCFC1BB48375B95F81F3222496E6BD570267CF1ABCAF163DFCDC15BA85BE5F777D4505D7E6324368CF5A7F1D3105AB2003F5B093FD8CB3950327D092C127DE75746181071B45A08FFD969343DCB113D4DA8C36975CA3EDE596598721C200A52875359F41F826A76086263421637C8C30AF03AEFE85E4C4D091BCFC31CB364C595A4A7FE153FDC64F0B4745A3DE0CA72B6AB78D7E0E4EF3160CD5D2B9CF87E4C2E8EA3B230C2DEC657D17610879A215B5E6FF48D43CDA369FEBE
	4C4901A45D5097BD8CD7C679E9A80F729F2FBCBB7F27356B57013253A815FC1B8DAEE52B39BE98683FA8F927C55F1B3FA9EC4DDD58E38FAEAE571C21F86DEE9F7E089E149784141933046B6577BCDBF96F03FF7A18382E0AAAE89FEDA4902D6811647E0A3A6EEE682BC2813498284F976AF11E7200BC5B06666CB87B429B972107B5A2D3FFE5A579DBC57E9E46FF2B88D2C5102A4350EC6B51187F8279C33C4C24DB12449E5C0706701D05F279CB8578E595FC60310320DBC6F5B70CEE9B5DEFB03CD3349ECE6D46
	FF72711F3DC428007CAD1A306FD82AA44067B7D51F4D4CE793693D135867432A4F294CE73CDAB0CDE61F5FD1FD4E50783CF1B27BFCD275F99373B9D7782CAD89FE6818F5CB02021444B43AA47FF87F1BD47FF1740C7BB5BEE54C18FC1E1ED3BD453D825B46B1664E4967E92B6AE90A46D37D79628B7574D37F198DFB9BFCE67BC2BDF5780FB36CA5DE65CC6A39214F3FF67962AFC5C1D9F5DDFDEEF950055F67441F9DA8A80D3E1F05C3CB7CA2D6858EB6A111C6DF91033BAD78BD2F827E2297G1B1AED856C6D30
	09051E1AAA6BA44EEF79622B2C13252F8DCD66863D5DFF8A429E60DF3618548F55997E819776G2FF851E476F90E7F840ABD281C6C759C6EBFC171AFCEA7354F770F6E4FF82BE692CE7F0B7DFB8476569B34F63FD0CF9F762FD36E69A42C53257EBDF6925769937AF5BA89BD0D771FED3927789F7D2E74508D6D372B9B6E30847D7EF566B276DD69B2753BD27ACDCB088B17E9B5609B16716FD2D676619FB68876ED4C79FE3B4B2558DDFDF5EE877ECE26FE187E4E8C151D6E427AFDBC8FE3325FF5F4E13FAFE9E4
	7E9FD0CB8788E6D2DBE4B09BGG48DCGGD0CB818294G94G88G88G55F854ACE6D2DBE4B09BGG48DCGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGEA9BGGGG
**end of data**/
}
}
