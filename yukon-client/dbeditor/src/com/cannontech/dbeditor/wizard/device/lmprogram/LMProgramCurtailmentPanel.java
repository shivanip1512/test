package com.cannontech.dbeditor.wizard.device.lmprogram;

/**
 * This type was created in VisualAge.
 */

import javax.swing.text.StyledDocument;

import com.cannontech.common.gui.util.LimitedStyledDocument;
import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.database.data.device.lm.LMProgramCurtailment;

public class LMProgramCurtailmentPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.klg.jclass.util.value.JCValueListener, javax.swing.event.CaretListener {
	private javax.swing.JLabel ivjJLabelMsgFooter = null;
	private javax.swing.JLabel ivjJLabelMsgHeader = null;
	private javax.swing.JPanel ivjJPanelHeading = null;
	private javax.swing.JScrollPane ivjJScrollPaneMsgFooter = null;
	private javax.swing.JScrollPane ivjJScrollPaneMsgHeader = null;
	private javax.swing.JTextPane ivjJTextPaneMsgFooter = null;
	private javax.swing.JTextPane ivjJTextPaneMsgHeader = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldMinNotifyTime = null;
	private javax.swing.JLabel ivjJLabelMinNotifyTime = null;
	private javax.swing.JLabel ivjJLabelMinutesMNT = null;
	private javax.swing.JLabel ivjJLabelMsgCanceled = null;
	private javax.swing.JLabel ivjJLabelMsgStoppedEarly = null;
	private javax.swing.JScrollPane ivjJScrollPaneMsgCanceled = null;
	private javax.swing.JScrollPane ivjJScrollPaneMsgStoppedEarly = null;
	private javax.swing.JTextPane ivjJTextPaneMsgCanceled = null;
	private javax.swing.JTextPane ivjJTextPaneMsgStoppedEarly = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldAckTimeLimit = null;
	private javax.swing.JLabel ivjJLabelAckTimeLimit = null;
	private javax.swing.JLabel ivjJLabelMinutesAckTLimit = null;
	private javax.swing.JLabel ivjJLabelSubject = null;
	private javax.swing.JTextField ivjJTextFieldSubject = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public LMProgramCurtailmentPanel() {
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
	if (e.getSource() == getJTextFieldSubject()) 
		connEtoC2(e);
	if (e.getSource() == getJTextPaneMsgHeader()) 
		connEtoC1(e);
	if (e.getSource() == getJTextPaneMsgFooter()) 
		connEtoC3(e);
	if (e.getSource() == getJTextPaneMsgCanceled()) 
		connEtoC4(e);
	if (e.getSource() == getJTextPaneMsgStoppedEarly()) 
		connEtoC5(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JTextPaneMsgHeader.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMProgramCurtailmentPanel.fireInputUpdate()V)
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
 * connEtoC2:  (JTextFieldHeading.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMProgramCurtailmentPanel.fireInputUpdate()V)
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
 * connEtoC3:  (JTextPaneMsgFooter.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMProgramCurtailmentPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC4:  (JTextPaneMsgCanceled.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMProgramCurtailmentPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC5:  (JTextPaneMsgStoppedEarly.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMProgramCurtailmentPanel.fireInputUpdate()V)
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
 * Return the JCSpinFieldAckTimeLimit property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getJCSpinFieldAckTimeLimit() {
	if (ivjJCSpinFieldAckTimeLimit == null) {
		try {
			ivjJCSpinFieldAckTimeLimit = new com.klg.jclass.field.JCSpinField();
			ivjJCSpinFieldAckTimeLimit.setName("JCSpinFieldAckTimeLimit");
			ivjJCSpinFieldAckTimeLimit.setToolTipText("Minutes before the curtailment starts that a customer is late");
			// user code begin {1}

			ivjJCSpinFieldAckTimeLimit.setDataProperties(
					new com.klg.jclass.field.DataProperties(
						new com.klg.jclass.field.validate.JCIntegerValidator(
						null, new Integer(1), new Integer(99999), null, true, 
						null, new Integer(1), "#,##0.###;-#,##0.###", false, 
						false, false, null, new Integer(20)), 
						new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, 
						new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 2, 
						new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCSpinFieldAckTimeLimit;
}
/**
 * Return the JCSpinFieldMinNotifyTime property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getJCSpinFieldMinNotifyTime() {
	if (ivjJCSpinFieldMinNotifyTime == null) {
		try {
			ivjJCSpinFieldMinNotifyTime = new com.klg.jclass.field.JCSpinField();
			ivjJCSpinFieldMinNotifyTime.setName("JCSpinFieldMinNotifyTime");
			ivjJCSpinFieldMinNotifyTime.setToolTipText("Minutes ahead of curtailment a customer must be notified");
			// user code begin {1}

			ivjJCSpinFieldMinNotifyTime.setDataProperties(
					new com.klg.jclass.field.DataProperties(
						new com.klg.jclass.field.validate.JCIntegerValidator(
						null, new Integer(1), new Integer(99999), null, true, 
						null, new Integer(1), "#,##0.###;-#,##0.###", false, 
						false, false, null, new Integer(120)), 
						new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, 
						new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 2, 
						new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCSpinFieldMinNotifyTime;
}
/**
 * Return the JLabelAckTimeLimit property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelAckTimeLimit() {
	if (ivjJLabelAckTimeLimit == null) {
		try {
			ivjJLabelAckTimeLimit = new javax.swing.JLabel();
			ivjJLabelAckTimeLimit.setName("JLabelAckTimeLimit");
			ivjJLabelAckTimeLimit.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelAckTimeLimit.setText("Ack Time Limit:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelAckTimeLimit;
}
/**
 * Return the JLabelMinNotifyTime property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMinNotifyTime() {
	if (ivjJLabelMinNotifyTime == null) {
		try {
			ivjJLabelMinNotifyTime = new javax.swing.JLabel();
			ivjJLabelMinNotifyTime.setName("JLabelMinNotifyTime");
			ivjJLabelMinNotifyTime.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelMinNotifyTime.setText("Min Notify Time:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMinNotifyTime;
}
/**
 * Return the JLabelMinutesAckTLimit property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMinutesAckTLimit() {
	if (ivjJLabelMinutesAckTLimit == null) {
		try {
			ivjJLabelMinutesAckTLimit = new javax.swing.JLabel();
			ivjJLabelMinutesAckTLimit.setName("JLabelMinutesAckTLimit");
			ivjJLabelMinutesAckTLimit.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelMinutesAckTLimit.setText("(min.)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMinutesAckTLimit;
}
/**
 * Return the JLabelMinutesMNT property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMinutesMNT() {
	if (ivjJLabelMinutesMNT == null) {
		try {
			ivjJLabelMinutesMNT = new javax.swing.JLabel();
			ivjJLabelMinutesMNT.setName("JLabelMinutesMNT");
			ivjJLabelMinutesMNT.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelMinutesMNT.setText("(min.)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMinutesMNT;
}
/**
 * Return the JLabelMsgCanceled property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMsgCanceled() {
	if (ivjJLabelMsgCanceled == null) {
		try {
			ivjJLabelMsgCanceled = new javax.swing.JLabel();
			ivjJLabelMsgCanceled.setName("JLabelMsgCanceled");
			ivjJLabelMsgCanceled.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelMsgCanceled.setText("Canceled:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMsgCanceled;
}
/**
 * Return the JLabelMsgFooter property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMsgFooter() {
	if (ivjJLabelMsgFooter == null) {
		try {
			ivjJLabelMsgFooter = new javax.swing.JLabel();
			ivjJLabelMsgFooter.setName("JLabelMsgFooter");
			ivjJLabelMsgFooter.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelMsgFooter.setText("Footer:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMsgFooter;
}
/**
 * Return the JLabelMsgHeader property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMsgHeader() {
	if (ivjJLabelMsgHeader == null) {
		try {
			ivjJLabelMsgHeader = new javax.swing.JLabel();
			ivjJLabelMsgHeader.setName("JLabelMsgHeader");
			ivjJLabelMsgHeader.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelMsgHeader.setText("Header:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMsgHeader;
}
/**
 * Return the JLabelMsgStoppedEarly property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMsgStoppedEarly() {
	if (ivjJLabelMsgStoppedEarly == null) {
		try {
			ivjJLabelMsgStoppedEarly = new javax.swing.JLabel();
			ivjJLabelMsgStoppedEarly.setName("JLabelMsgStoppedEarly");
			ivjJLabelMsgStoppedEarly.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelMsgStoppedEarly.setText("Stopped Early:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMsgStoppedEarly;
}
/**
 * Return the JLabelHeading property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelSubject() {
	if (ivjJLabelSubject == null) {
		try {
			ivjJLabelSubject = new javax.swing.JLabel();
			ivjJLabelSubject.setName("JLabelSubject");
			ivjJLabelSubject.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelSubject.setText("Subject:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelSubject;
}
/**
 * Return the JPanelHeading property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelHeading() {
	if (ivjJPanelHeading == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Message");
			ivjJPanelHeading = new javax.swing.JPanel();
			ivjJPanelHeading.setName("JPanelHeading");
			ivjJPanelHeading.setBorder(ivjLocalBorder);
			ivjJPanelHeading.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabelSubject = new java.awt.GridBagConstraints();
			constraintsJLabelSubject.gridx = 1; constraintsJLabelSubject.gridy = 1;
			constraintsJLabelSubject.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelSubject.ipadx = 5;
			constraintsJLabelSubject.ipady = 1;
			constraintsJLabelSubject.insets = new java.awt.Insets(0, 9, 3, 1);
			getJPanelHeading().add(getJLabelSubject(), constraintsJLabelSubject);

			java.awt.GridBagConstraints constraintsJTextFieldSubject = new java.awt.GridBagConstraints();
			constraintsJTextFieldSubject.gridx = 2; constraintsJTextFieldSubject.gridy = 1;
			constraintsJTextFieldSubject.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldSubject.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldSubject.weightx = 1.0;
			constraintsJTextFieldSubject.ipadx = 310;
			constraintsJTextFieldSubject.insets = new java.awt.Insets(0, 2, 3, 10);
			getJPanelHeading().add(getJTextFieldSubject(), constraintsJTextFieldSubject);

			java.awt.GridBagConstraints constraintsJScrollPaneMsgHeader = new java.awt.GridBagConstraints();
			constraintsJScrollPaneMsgHeader.gridx = 1; constraintsJScrollPaneMsgHeader.gridy = 3;
			constraintsJScrollPaneMsgHeader.gridwidth = 2;
			constraintsJScrollPaneMsgHeader.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPaneMsgHeader.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJScrollPaneMsgHeader.weightx = 1.0;
			constraintsJScrollPaneMsgHeader.weighty = 1.0;
			constraintsJScrollPaneMsgHeader.ipadx = 354;
			constraintsJScrollPaneMsgHeader.ipady = 11;
			constraintsJScrollPaneMsgHeader.insets = new java.awt.Insets(1, 9, 2, 12);
			getJPanelHeading().add(getJScrollPaneMsgHeader(), constraintsJScrollPaneMsgHeader);

			java.awt.GridBagConstraints constraintsJScrollPaneMsgFooter = new java.awt.GridBagConstraints();
			constraintsJScrollPaneMsgFooter.gridx = 1; constraintsJScrollPaneMsgFooter.gridy = 5;
			constraintsJScrollPaneMsgFooter.gridwidth = 2;
			constraintsJScrollPaneMsgFooter.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPaneMsgFooter.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJScrollPaneMsgFooter.weightx = 1.0;
			constraintsJScrollPaneMsgFooter.weighty = 1.0;
			constraintsJScrollPaneMsgFooter.ipadx = 354;
			constraintsJScrollPaneMsgFooter.ipady = 11;
			constraintsJScrollPaneMsgFooter.insets = new java.awt.Insets(1, 9, 2, 12);
			getJPanelHeading().add(getJScrollPaneMsgFooter(), constraintsJScrollPaneMsgFooter);

			java.awt.GridBagConstraints constraintsJLabelMsgHeader = new java.awt.GridBagConstraints();
			constraintsJLabelMsgHeader.gridx = 1; constraintsJLabelMsgHeader.gridy = 2;
			constraintsJLabelMsgHeader.gridwidth = 2;
			constraintsJLabelMsgHeader.ipadx = 21;
			constraintsJLabelMsgHeader.ipady = -3;
			constraintsJLabelMsgHeader.insets = new java.awt.Insets(3, 9, 0, 317);
			getJPanelHeading().add(getJLabelMsgHeader(), constraintsJLabelMsgHeader);

			java.awt.GridBagConstraints constraintsJLabelMsgFooter = new java.awt.GridBagConstraints();
			constraintsJLabelMsgFooter.gridx = 1; constraintsJLabelMsgFooter.gridy = 4;
			constraintsJLabelMsgFooter.gridwidth = 2;
			constraintsJLabelMsgFooter.ipadx = 25;
			constraintsJLabelMsgFooter.ipady = -3;
			constraintsJLabelMsgFooter.insets = new java.awt.Insets(2, 9, 1, 317);
			getJPanelHeading().add(getJLabelMsgFooter(), constraintsJLabelMsgFooter);

			java.awt.GridBagConstraints constraintsJScrollPaneMsgCanceled = new java.awt.GridBagConstraints();
			constraintsJScrollPaneMsgCanceled.gridx = 1; constraintsJScrollPaneMsgCanceled.gridy = 7;
			constraintsJScrollPaneMsgCanceled.gridwidth = 2;
			constraintsJScrollPaneMsgCanceled.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPaneMsgCanceled.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJScrollPaneMsgCanceled.weightx = 1.0;
			constraintsJScrollPaneMsgCanceled.weighty = 1.0;
			constraintsJScrollPaneMsgCanceled.ipadx = 354;
			constraintsJScrollPaneMsgCanceled.ipady = 11;
			constraintsJScrollPaneMsgCanceled.insets = new java.awt.Insets(1, 9, 3, 12);
			getJPanelHeading().add(getJScrollPaneMsgCanceled(), constraintsJScrollPaneMsgCanceled);

			java.awt.GridBagConstraints constraintsJLabelMsgCanceled = new java.awt.GridBagConstraints();
			constraintsJLabelMsgCanceled.gridx = 1; constraintsJLabelMsgCanceled.gridy = 6;
			constraintsJLabelMsgCanceled.gridwidth = 2;
			constraintsJLabelMsgCanceled.ipadx = 20;
			constraintsJLabelMsgCanceled.ipady = -3;
			constraintsJLabelMsgCanceled.insets = new java.awt.Insets(3, 9, 0, 304);
			getJPanelHeading().add(getJLabelMsgCanceled(), constraintsJLabelMsgCanceled);

			java.awt.GridBagConstraints constraintsJLabelMsgStoppedEarly = new java.awt.GridBagConstraints();
			constraintsJLabelMsgStoppedEarly.gridx = 1; constraintsJLabelMsgStoppedEarly.gridy = 8;
			constraintsJLabelMsgStoppedEarly.gridwidth = 2;
			constraintsJLabelMsgStoppedEarly.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelMsgStoppedEarly.ipadx = 25;
			constraintsJLabelMsgStoppedEarly.ipady = -3;
			constraintsJLabelMsgStoppedEarly.insets = new java.awt.Insets(3, 9, 1, 270);
			getJPanelHeading().add(getJLabelMsgStoppedEarly(), constraintsJLabelMsgStoppedEarly);

			java.awt.GridBagConstraints constraintsJScrollPaneMsgStoppedEarly = new java.awt.GridBagConstraints();
			constraintsJScrollPaneMsgStoppedEarly.gridx = 1; constraintsJScrollPaneMsgStoppedEarly.gridy = 9;
			constraintsJScrollPaneMsgStoppedEarly.gridwidth = 2;
			constraintsJScrollPaneMsgStoppedEarly.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPaneMsgStoppedEarly.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJScrollPaneMsgStoppedEarly.weightx = 1.0;
			constraintsJScrollPaneMsgStoppedEarly.weighty = 1.0;
			constraintsJScrollPaneMsgStoppedEarly.ipadx = 354;
			constraintsJScrollPaneMsgStoppedEarly.ipady = 11;
			constraintsJScrollPaneMsgStoppedEarly.insets = new java.awt.Insets(1, 9, 12, 12);
			getJPanelHeading().add(getJScrollPaneMsgStoppedEarly(), constraintsJScrollPaneMsgStoppedEarly);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelHeading;
}
/**
 * Return the JScrollPaneMsgCanceled property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneMsgCanceled() {
	if (ivjJScrollPaneMsgCanceled == null) {
		try {
			ivjJScrollPaneMsgCanceled = new javax.swing.JScrollPane();
			ivjJScrollPaneMsgCanceled.setName("JScrollPaneMsgCanceled");
			getJScrollPaneMsgCanceled().setViewportView(getJTextPaneMsgCanceled());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneMsgCanceled;
}
/**
 * Return the JScrollPaneMsgFooter property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneMsgFooter() {
	if (ivjJScrollPaneMsgFooter == null) {
		try {
			ivjJScrollPaneMsgFooter = new javax.swing.JScrollPane();
			ivjJScrollPaneMsgFooter.setName("JScrollPaneMsgFooter");
			getJScrollPaneMsgFooter().setViewportView(getJTextPaneMsgFooter());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneMsgFooter;
}
/**
 * Return the JScrollPaneMsgHeader property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneMsgHeader() {
	if (ivjJScrollPaneMsgHeader == null) {
		try {
			ivjJScrollPaneMsgHeader = new javax.swing.JScrollPane();
			ivjJScrollPaneMsgHeader.setName("JScrollPaneMsgHeader");
			getJScrollPaneMsgHeader().setViewportView(getJTextPaneMsgHeader());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneMsgHeader;
}
/**
 * Return the JScrollPaneMsgStoppedEarly property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneMsgStoppedEarly() {
	if (ivjJScrollPaneMsgStoppedEarly == null) {
		try {
			ivjJScrollPaneMsgStoppedEarly = new javax.swing.JScrollPane();
			ivjJScrollPaneMsgStoppedEarly.setName("JScrollPaneMsgStoppedEarly");
			getJScrollPaneMsgStoppedEarly().setViewportView(getJTextPaneMsgStoppedEarly());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneMsgStoppedEarly;
}
/**
 * Return the JTextFieldHeading property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldSubject() {
	if (ivjJTextFieldSubject == null) {
		try {
			ivjJTextFieldSubject = new javax.swing.JTextField();
			ivjJTextFieldSubject.setName("JTextFieldSubject");
			ivjJTextFieldSubject.setDocument(new TextFieldDocument(TextFieldDocument.STRING_LENGTH_40));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldSubject;
}
/**
 * Return the JTextPaneMsgCanceled property value.
 * @return javax.swing.JTextPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextPane getJTextPaneMsgCanceled() {
	if (ivjJTextPaneMsgCanceled == null) {
		try {
			ivjJTextPaneMsgCanceled = new javax.swing.JTextPane();
			ivjJTextPaneMsgCanceled.setName("JTextPaneMsgCanceled");
			ivjJTextPaneMsgCanceled.setBounds(0, 0, 185, 43);
			LimitedStyledDocument lpd = new LimitedStyledDocument(80);
			ivjJTextPaneMsgCanceled.setDocument(lpd);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextPaneMsgCanceled;
}
/**
 * Return the JTextPaneMsgFooter property value.
 * @return javax.swing.JTextPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextPane getJTextPaneMsgFooter() {
	if (ivjJTextPaneMsgFooter == null) {
		try {
			ivjJTextPaneMsgFooter = new javax.swing.JTextPane();
			ivjJTextPaneMsgFooter.setName("JTextPaneMsgFooter");
			ivjJTextPaneMsgFooter.setBounds(0, 0, 185, 43);
			LimitedStyledDocument lpd = new LimitedStyledDocument(160);
			ivjJTextPaneMsgFooter.setDocument(lpd);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextPaneMsgFooter;
}
/**
 * Return the JTextPaneMsgHeader property value.
 * @return javax.swing.JTextPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextPane getJTextPaneMsgHeader() {
	if (ivjJTextPaneMsgHeader == null) {
		try {
			ivjJTextPaneMsgHeader = new javax.swing.JTextPane();
			ivjJTextPaneMsgHeader.setName("JTextPaneMsgHeader");
			ivjJTextPaneMsgHeader.setBounds(0, 0, 185, 43);
			LimitedStyledDocument lpd = new LimitedStyledDocument(160);
			ivjJTextPaneMsgHeader.setDocument(lpd);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextPaneMsgHeader;
}
/**
 * Return the JTextPaneMsgStoppedEarly property value.
 * @return javax.swing.JTextPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextPane getJTextPaneMsgStoppedEarly() {
	if (ivjJTextPaneMsgStoppedEarly == null) {
		try {
			ivjJTextPaneMsgStoppedEarly = new javax.swing.JTextPane();
			ivjJTextPaneMsgStoppedEarly.setName("JTextPaneMsgStoppedEarly");
			ivjJTextPaneMsgStoppedEarly.setBounds(0, 0, 185, 43);
			LimitedStyledDocument lpd = new LimitedStyledDocument(80);
			ivjJTextPaneMsgStoppedEarly.setDocument(lpd);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextPaneMsgStoppedEarly;
}
/**
 * getValue method comment.
 */
public Object getValue(Object o) 
{
	LMProgramCurtailment program = (LMProgramCurtailment)o;

	program.getCurtailmentProgram().setHeading( getJTextFieldSubject().getText() );

	if( getJTextPaneMsgHeader().getText() == null 
		 || getJTextPaneMsgHeader().getText().length() <= 0 )
		program.getCurtailmentProgram().setMessageHeader(" ");
	else
		program.getCurtailmentProgram().setMessageHeader( getJTextPaneMsgHeader().getText() );

	if( getJTextPaneMsgFooter().getText() == null 
		 || getJTextPaneMsgFooter().getText().length() <= 0 )
		program.getCurtailmentProgram().setMessageFooter(" ");
	else
		program.getCurtailmentProgram().setMessageFooter( getJTextPaneMsgFooter().getText() );

	if( getJTextPaneMsgCanceled().getText() == null 
		 || getJTextPaneMsgCanceled().getText().length() <= 0 )
		program.getCurtailmentProgram().setCanceledMsg(" ");
	else
		program.getCurtailmentProgram().setCanceledMsg( getJTextPaneMsgCanceled().getText() );

	if( getJTextPaneMsgStoppedEarly().getText() == null 
		 || getJTextPaneMsgStoppedEarly().getText().length() <= 0 )
		program.getCurtailmentProgram().setStoppedEarlyMsg(" ");
	else
		program.getCurtailmentProgram().setStoppedEarlyMsg( getJTextPaneMsgStoppedEarly().getText() );


	program.getCurtailmentProgram().setAckTimeLimit( new Integer( ((Number)getJCSpinFieldAckTimeLimit().getValue()).intValue() * 60 ) );
	program.getCurtailmentProgram().setMinNotifyTime( new Integer( ((Number)getJCSpinFieldMinNotifyTime().getValue()).intValue() * 60 ) );

	return o;
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
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}

	getJCSpinFieldAckTimeLimit().addValueListener( this );
	getJCSpinFieldMinNotifyTime().addValueListener( this );
	
	// user code end
	getJTextFieldSubject().addCaretListener(this);
	getJTextPaneMsgHeader().addCaretListener(this);
	getJTextPaneMsgFooter().addCaretListener(this);
	getJTextPaneMsgCanceled().addCaretListener(this);
	getJTextPaneMsgStoppedEarly().addCaretListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("LMProgramCurtailmentPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(416, 373);

		java.awt.GridBagConstraints constraintsJPanelHeading = new java.awt.GridBagConstraints();
		constraintsJPanelHeading.gridx = 1; constraintsJPanelHeading.gridy = 1;
		constraintsJPanelHeading.gridwidth = 3;
		constraintsJPanelHeading.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelHeading.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelHeading.weightx = 1.0;
		constraintsJPanelHeading.weighty = 1.0;
		constraintsJPanelHeading.ipadx = -10;
		constraintsJPanelHeading.ipady = -8;
		constraintsJPanelHeading.insets = new java.awt.Insets(13, 8, 11, 11);
		add(getJPanelHeading(), constraintsJPanelHeading);

		java.awt.GridBagConstraints constraintsJLabelMinNotifyTime = new java.awt.GridBagConstraints();
		constraintsJLabelMinNotifyTime.gridx = 1; constraintsJLabelMinNotifyTime.gridy = 2;
		constraintsJLabelMinNotifyTime.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelMinNotifyTime.ipadx = 13;
		constraintsJLabelMinNotifyTime.insets = new java.awt.Insets(12, 8, 6, 3);
		add(getJLabelMinNotifyTime(), constraintsJLabelMinNotifyTime);

		java.awt.GridBagConstraints constraintsJCSpinFieldMinNotifyTime = new java.awt.GridBagConstraints();
		constraintsJCSpinFieldMinNotifyTime.gridx = 2; constraintsJCSpinFieldMinNotifyTime.gridy = 2;
		constraintsJCSpinFieldMinNotifyTime.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJCSpinFieldMinNotifyTime.ipadx = 50;
		constraintsJCSpinFieldMinNotifyTime.ipady = 19;
		constraintsJCSpinFieldMinNotifyTime.insets = new java.awt.Insets(11, 3, 6, 5);
		add(getJCSpinFieldMinNotifyTime(), constraintsJCSpinFieldMinNotifyTime);

		java.awt.GridBagConstraints constraintsJLabelMinutesMNT = new java.awt.GridBagConstraints();
		constraintsJLabelMinutesMNT.gridx = 3; constraintsJLabelMinutesMNT.gridy = 2;
		constraintsJLabelMinutesMNT.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelMinutesMNT.ipadx = 8;
		constraintsJLabelMinutesMNT.ipady = -2;
		constraintsJLabelMinutesMNT.insets = new java.awt.Insets(14, 5, 9, 167);
		add(getJLabelMinutesMNT(), constraintsJLabelMinutesMNT);

		java.awt.GridBagConstraints constraintsJLabelAckTimeLimit = new java.awt.GridBagConstraints();
		constraintsJLabelAckTimeLimit.gridx = 1; constraintsJLabelAckTimeLimit.gridy = 3;
		constraintsJLabelAckTimeLimit.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelAckTimeLimit.ipadx = 18;
		constraintsJLabelAckTimeLimit.insets = new java.awt.Insets(8, 8, 12, 3);
		add(getJLabelAckTimeLimit(), constraintsJLabelAckTimeLimit);

		java.awt.GridBagConstraints constraintsJCSpinFieldAckTimeLimit = new java.awt.GridBagConstraints();
		constraintsJCSpinFieldAckTimeLimit.gridx = 2; constraintsJCSpinFieldAckTimeLimit.gridy = 3;
		constraintsJCSpinFieldAckTimeLimit.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJCSpinFieldAckTimeLimit.ipadx = 50;
		constraintsJCSpinFieldAckTimeLimit.ipady = 19;
		constraintsJCSpinFieldAckTimeLimit.insets = new java.awt.Insets(7, 3, 12, 5);
		add(getJCSpinFieldAckTimeLimit(), constraintsJCSpinFieldAckTimeLimit);

		java.awt.GridBagConstraints constraintsJLabelMinutesAckTLimit = new java.awt.GridBagConstraints();
		constraintsJLabelMinutesAckTLimit.gridx = 3; constraintsJLabelMinutesAckTLimit.gridy = 3;
		constraintsJLabelMinutesAckTLimit.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelMinutesAckTLimit.ipadx = 8;
		constraintsJLabelMinutesAckTLimit.ipady = -2;
		constraintsJLabelMinutesAckTLimit.insets = new java.awt.Insets(10, 5, 15, 167);
		add(getJLabelMinutesAckTLimit(), constraintsJLabelMinutesAckTLimit);
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
	if( getJTextFieldSubject().getText() == null || getJTextFieldSubject().getText().length() <= 0 )
	{
		setErrorString("The Field Subject text field must be filled in");
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
		LMProgramBasePanel aLMProgramBasePanel;
		aLMProgramBasePanel = new LMProgramBasePanel();
		frame.setContentPane(aLMProgramBasePanel);
		frame.setSize(aLMProgramBasePanel.getSize());
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
 * setValue method comment.
 */
public void setValue(Object o) 
{
	LMProgramCurtailment program = (LMProgramCurtailment)o;

	getJTextFieldSubject().setText( program.getCurtailmentProgram().getHeading() );

	getJTextPaneMsgHeader().setText( program.getCurtailmentProgram().getMessageHeader() );
	getJTextPaneMsgFooter().setText( program.getCurtailmentProgram().getMessageFooter() );
	getJTextPaneMsgCanceled().setText( program.getCurtailmentProgram().getCanceledMsg() );
	getJTextPaneMsgStoppedEarly().setText( program.getCurtailmentProgram().getStoppedEarlyMsg() );


	getJCSpinFieldMinNotifyTime().setValue( new Integer(program.getCurtailmentProgram().getMinNotifyTime().intValue() / 60) );
	getJCSpinFieldAckTimeLimit().setValue( new Integer(program.getCurtailmentProgram().getAckTimeLimit().intValue() / 60) );
}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) 
{
	//fire this event for all JCSpinFields!!
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
	D0CB838494G88G88GDBF954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8BF4D45535A8919123C6419A91B4CA248123C60B2B346579F8A9ED312262FB2054D20BCB5A663D220F36344B5A74F913498F79A41000118698A4C082A19F21A5CE026447A79AA01088530A101B4CCDB249E4E61839A1138F7076796CF36FCC66C3283EA7EBED6EB9FB1FFD76BE7B6CF34E5E675E091433B5AAB5A9B9C71212D2A57E6FC012A40D7A3BA419F37E500438D1CAFC1A247B378DE01DF447
	1194F8C6G2D60697834AC49101A8175E8289F7BD6FC5AFBD04E16B2D2A30FC3A1009CA75022CA1ECD8CAE6764E61413CE6BDD3BD743F3ADC08C6048F305C2645F38BB176367F1BCC2D232A4A53413B613F767F1DCB9549B812281E636337673613912CA3E49D44E5B1D3BB3CADA7163DD49EBC8BF3ABE91CABB98EFB3FF9A25471392BB082C9FD2BD89CFB85467G10419B2430770E403332313B6363B6C7CFF11BEC37BB6C2A5256D95CE79D10DD16E20BF2495A26945BFA1CAEC707CB6EB1199A0EB732E2ED2F
	CB152D369E452EB64AF645A6C91ECEA2EB6D1E8C5A37A4D5C1BD45CAF0BFB9C8644F027A8200E5829F75B1419F03F28B00470AE3BE382B120F49BC78389476325D1099E04C91D6B65619D6ED4C93FA5FCEFF1F794596B28FAB00165B951FD6845088608408G788B750BEFEC5E8D4F6AD6355651536330D7BB1DB5B60B6B3843225820ECB2810563CEABED8E17C512585C4F5BDD64C71FE04078DE99B19F33C94CBE74035853D325455F7AB4B95D0FCD964714A647F4B1DB4C6E52EC42E4E76F89A4FBC917371FFA
	4BBEAB69E52BE39C06D4BF32E7ACACCCDE417B496856E417F71339BECE673AG4AC95CBF987EAC458F69704CE7FFCEFD06544301363C9BFD433C96F5E9C819A8E515B57835C3E4FCF5F2330D69E033796A12F308749DE9E3B2D789DDBE207819BABC53E5AA55115497812D32A7BE0D795794A3D943D1D01F8530GE09940EABB6CC3G15F674311FEFAE39C69FEB14DDEACD2F55E6D1DC92630D5C32911E0AC7F54975F637AA5B5B14B3324BAA375A9437445BBC38055820550BEFAA757DB610F1D431ABAED935BA
	6C503B5C23280A4B4D5ABC3EE59DE963D2A835595A2300009E27047DFEFBCBB6311F6CD6BFF3DAE4D551DAB07A3FEEA1F3F2C9BE83BDC287D0E6F6193F0D586B9C0CDF858893F6B83C1574772962027D82369207233B5759CCC6A2AD3093BB6F26FBC7A414636CE863F99F90DC2EA3BE2D54017D8CEDDD2F6B274E5AC65417DD7D38461ABEA67BF41D83FD66CDB37ACC5D1A072522051F7875996AAB8E66ABE18E5F3573856D1368456A3FB8E866B2C63216C736D77C8F15E1788A7ADFAB664A14CF66AA9646B197
	A0C158785BDB0D4CE7EBE43702DEFA52858E26D8B40BB3BDB79D4A61FA1233B6156B3FD20C230052A5A72C8B00C6G8FC01493ED997B31196BE470618F247BFBA336658F3CE4E07F2B0270250EC086335923C6F2CEA91760CC2CD31C0A5D8285B7274DB092F9FC9D37BB844FF7A95E896B3E96961ADB60E79B495A14D555E5ED6DD515A6C5D52D768E0DBE174AF27736BA35AD83E53DE0A43ABB7AE05D0B768B0CE44D34B96CF605CE1EE0617417681AD5EC8AB951EB9D3DF6156197EFA3FE5F2D7473BDE349B692
	98DD12ED3DE47FE1F1C41CCC6C68F4D9FB40A54896A695DDA46B39FACF2E4EC6F3E13EC9BCA481AD67A22E4D3E6D38B673879E14E6DF58116C6FEC09FC38AED93D48565A580B5A5AE4BA3D7801FF5DDF26FB87473B8D94B775A2717BA0E18CDF976E9D244F9810B58BE041C5DC97CD5B899D779E3EA0EA9DB69BB32B30F7D5FE96199F65E22F82DBB65964BE95B6BC1A3F5E07F6065B1351CF6693FB5AF47B63A89731679707B0E6DC867A24GAE5F45EC5A62E2B6E16D0CD558EE9E54178224387D35FBDD342B82
	3A599D285DBD55584E837408006DBE2D41F6F1C01F6B279D7317748BA48E7197F7F674DA0BFBD5AB5955E595960A3317CD8B0BAD9F21678369A3410DB1644B45E41E0CEAFCDAB9C0230AFE75629A0CA1A7F4C7C9CB26EC8A98B7F72ACC37F0D573AB63E80CA1676EA4B26781ED310ABE7063FC7DB9D66DF6BB5A2C74886573573FC36FF78CF7E5473B70EC373A5CEABDEC8C9E1C6B309DC4DFF246F2B40FB94650D8040CF5B948AD680DCF1B5DCB705F5F1023533D85F0E381E28118FF3FF108449AED32CBD10B69
	7F6C68AE6812AF491EE2F79FECA245F407AA2EA5544324D84867E5CE61FA2623C2AD4FB98B4FE09C78D0F6957A55E393254A2B59497E627231191B13B3AEB1DB16DE52ECBA74E41AE15979BF25337155D7617806204D0CCBB83EEA03FEFC4BG1F5387F35B07630B2B3A1E71B1192B2BD1E68374975607B227D4784A1C8534A5G0CEF3660DB8138A28F72958E7872D900B65603FCEA8D72CD825C8241F73E5117EFF5BF44B77DF89E468D0B536B7A35B8435CBFBC66E0716B67A5C41E2D1F452F19825FCD7193F4
	F8265F4F685AA175D9C0AB9F40186BEC815937E7C0FDBEC08200B4981FE684289A44B5705E4EFDD20818369E56344BAE5B1A3C8EA96E774F6DA47389678A2F3F381358091EAB921E596107FC6DF0EE50FB6C9E1FBA631BB80C2F5827BE49070F797F0E02BC5C17F46EE4B2FD528C6776912BE23330F3AD26C4FF2E4D98E4716AA2F8AE83E0FB555B9BB08FCE9942BD6972917D1EB4AB734364D58176244AA1265F39A1ED7D94093CB6095E9DCC825ACC0079G4BG32AE03EF82B4DE46B97AFC5741D0F3C457C733
	2396F047603C29750D8F04CDCFD6DD5FDCFCB10C4FF7AE1C177D4DC55760DF824F85B993F80C61E9524F45584BECBF1C814F3917F1AEBC861C0BA517F10D2D2F3E3E71980771790EA7770AB79F53734594FFFA16C25B3AAB2867AB95286739AB28678F2FD34F85A1751C60D74FB74E7853B3965A4E96FA368C201E0B041E83B557276758E1FC3EFAE6DD752767C10FBFBD8B20ED65D554736EC6543399F0AC477C910DA142219EA5F051FB89EE8E5497DF457148A5E40DC923625372GAA81DAG06GA6814C9E05
	6DAE52F6C476D240255CGB199C09D0085A06C86EC77FA31136B0A7C3EEB7EDA215207B70F6BA6F17D065BB83984E51398A873BA65E2FF3EEBF5A53731EFAE457620160240B9D1C7017F1C282740FFCE64AED84770B4DA57CE94248F96844F19C697784B19B6D37D1F5918C97BF63B95B75D94C9G4AE93FD2B7C8FEF22793F20F42471EADFD7F50CA5E66306A287DD042FD6CF70544CF7D6CE33A38F6C9C98637ED748D98575EC3633F45D0DF81307AC67423F7B6843BBF4C3FF138DF31B3E36C9E92F314833D99
	4086908690FDA31E956F9706BC4F8FFB021C672BF6871E67753B7D4FF36EEE620FEEC72F2BCD711D4B2D748E1D5134D329DED5FA0859D91B824A2F4A2E8EE518AF14D0FEC68B44DF3EBB103F30B99DD3251F53B2B1279D07B77B1F5326B697E40274F4427B7038A65C03665C08F39B1ECC66B6916AB923614C9E0DF3FB5990ECEE6BC68F1FDB26E7FD35DE4FFDC2764AD414ED9E0D325FB4915991D00F8118ADE47F3CA2186CC581E57F59CB76FEA1FB47D9143DCC48D67B096C7CB058CF811A43D036B290CC36AD
	AC106CA7EA74327FAAE477FAD07628B014BD6DA43D5B057AFC0084A1FBE2E3B05952CD01E43F3A075C035864FEC72F8AF2EEA2B2FEAF6EBBF220DE84508C50F9930B8F196FBC51C77649F6175C83AE5364D45A24F3E3886F7776A26FDCE83F98A091A08FE815E3B077991BC37C1D6D9C47945982BD88FDA6501C74138639D5319D375ACFB8D4EBFBBF3930657483B5247FCFEA1B1CD6BB8DE53D9A31B6BF113374FD4012F09FBF514CE8EFF52D97346A36EE4254E06D312AA8FF3062BD6F7E3D9A31B6DB13B77A76
	CFDA71FED8EE7466A9FDEE94BD0659EE6E984C1DFEF5D6CFDF4C69D9B7A37D5EBEFF7C79025E6517DEAE6806BEFF7DB7887AA413BF7E161B453DF0095E1FD4400F3DD97321C9B7874A5D8E51FCB86EE61623ED907853943FD407E7395BE4FABE10FA6E587834B9E3B116593891E39967AD3837C76C893CB75F33477F5EBCF90F7FB3F84A1E60E770A3FB826D294CBECFB585DBEF33EF89E49F49C0463B60965F77C76FD27C52DBFC5F9F5D150C6F0FD6G4DBC8E6DB341007619B58E67ECEDEAB01D160C8B24D3EB
	851131E21C2FCEB60ACF3955D72795A654A987E81EDBD127BFD4087873D654E97759207BD2F8A01D92689E1B936E2B53EF595E9B6E2B53C0BF6A349FE81161285350G6A1498AEE2FCCFB01D0C378552E9F3A311317FB6DF1DF6D1FC5DED3EBABDFE92F5EA865A4C5BD027A79AD1273C5B710EE5F3913E5B2E821C67F65CE74BDA3CF279E239CFB5190E3A2C169A3923016E5A6C2CD00AB2B8FF5C6DF8D60C27FDA6C0DD3AA3BEAD778E4C5FBDBBB1FF0F1DBAD1EA9875295FBB45A54D9FA6175E415F455F21656F
	B1103FB3192F6DC51991C01FFB8741E5966B7795B276DFA733DC08512B3D68E40D1198C4233B3D68C45FEA031E7E30971D58E89767E7E3FBF6870EAD715998497654C7816FCB33A258187673E763196DD27E38BF2573F837BDC03CCBF7FD9E9B1C46F857960173F565EFBBA55D1DF0372D23382B0D6CBC456D64B4018E3423854F79ADE63C8F0E0E401CEFFE9F763DD8609E94381CBB91E76AC35E86017B8F93368B3B93FDAE1146663320BEDF60561E479CF205400D3D003E19FB972E1743D585706C04FA98C0B4
	409C0025G52F870E100AA009600307148E326BC3163597DBE397BA4F71173203EECBC3BDFDCBD81E20B896C4E1071CCB99495E4DD9259623C3AABF45EC97441F29EAFCBBA5C1CGFDF4F2BC99DF182EFD95AFB3DD1F8F2A2B7E1D1BEF2E698F32F5E54CEB57782C83FFFC78CE1544E29BFC5695F90E1AC066F46F463A903A9AF450GBCADG5BB9CD05F2CFB2DBC34CC766B5210F4C1D00F317E6A06F57F26F46FA962D13F7B62C9E19EC8E2243DA4E9B4ACE010049F8A2240CFF669DB40BEF9FB1E0FCDB8E7AB6
	G58G4281E26F46B534A3956DB3CF5823330258A36F9BD8F751BAF9D7456AEF1BC2693E1673DF3FFD32AB2EC546755A0859E7CDA55A2792F4ED84F082C4G44FD836DD3FD966DB3DF58634DFEE28F63BDD8FF0B56A304FDC675DF0B6E5782C406FF7B5C7B254970E7C3E61F7183E81FAA502399C08518GB0639E344F1D9E344F82E10F6D0D449E7911D85FCB6BF305FD1EBAF9AD3A937E6B771F672FC946BF67BFF39B51BE47C057F3GC3G13GE60931EFAF3A3EEF564A2E132FEB44FCACAED476523B2FC896
	97A6893C1D6293F4F896835EABE24095C0B35F0B6F316CB4271E8575C5G4B81B2A6462795G54CD445C283A28D48AF16F556CF0B6C072E45325BBECFC3F5CFBFD5F727DDBBA46DAB33E7B10B4F34C9F13823D87B2CFE4FE67614F04666D944F5F835177D0B1C01B8310GA05D87B183C051FDB83EDB689C105BBB0BD52DD6D5392D133E1546411D31AAFD9A1ADCC8707C6334FEECD57771F816BFBB6F8B3497ECB7933EA1BFF48291817996766965C573F6B379F3DEC03E29DEFCCBF83B957C19B1A9905FA14A17
	6707CE20B4A05FA213DEDE9DEFE766CF1B9FBEF67711DDCA6E0BDA9DAE1AAC337B225D2538E6C781DFA440FC002513583B8C566640F9EC13B8095DA965CC869F1E4C5B50B865018B4162940CCF5279338AF81BA733B80554BB217C46BB6F3C739BG266B42AEFA4FC4A376265E56AE25CD9563D8A41398C9FBA36ACBFFF3A349B3F4B721475D9D642ECAF1B17A3D954369C79C8E9569AFE7E58A590215773D5D61C5E3EC1CB6F4E56330FEEB495712B645023AF5DEDD276757481C3EB8E5031E5E24BA1CCE45F2D8
	F6597AC59B25F053B0B95E8D59BD51DDD53E77C4E43E5449F80F346F20BF7AD8C17F285C9FFD1220FFE7509FBDCE50DFFA5F9FFD8E5037CAACEEA6E7A57B377C50F01CA4AD1ACC3E017F68EF244C762D57D6633E157748FDD25D4BFFCCA2BE64EF5F4A3B1F2F077B357CB721ABB11D796B6BCD217C1578AA46575150474C7BB5FF1D87659FA433EFG591C646FDB0FBE6965F118A73EE840DCEF517D18EF2FCC714D374FCA7AFC39A245B75FCE7222772478665B6EA4BD7D01945FFC9B79198DAAD3C359E0154E86
	D98F442765BF2059207C81DF9BFC7A312F8D787260B658D709B628FBGED50DD995C8613AB035B60054A60B6C8BA985C86663321EC1029334190689D2133C174B09B44DF49713181AEF1E6036F8C208D62048DFEB3905C865B87035B60334160B638F9B0388DE2BC21EC102D33C182681D2833C1D6142F8D2E2E4F753101FEF7E2F6F833916D109705F658F9AA389D4C27025BE15C2960F618F5AA109D58395A3DD792E5D5D7FE20684B4AAD7C8113B173AB154162303E3D016F602F6C75FF879F36577F9D7C383D
	415FF35F35B7703741776E55FF9B4C62484683E4EE1A41971CG93GE2GE6C5E18C392B7883094610FE624743168E72FE167CB2C1C5796F16849E6B3A12503FE758D4C23E15ECF5282A2307740FF8E38919AFF0E70AE4389DA544C6AEEBC7274A1AF27C9EFAEFE7D35AA91A674F745EF1910CABA18AF3464B744CCB047A6A8791772AA46E72844EC46F9ABB21BECA609EB4E33BF001DBC66F9597C3FD19403DF1061E2B8FC179A1441D9639FC1E407D8B6535C1FDC86026D15E8828C7895CDA419BABF0E77AD03F
	C4017BF69F364B1802387F26F71CE628F78A5C3F27905C905443856E00380B091C02F76D99E5F8AF1FGB8E3B45E5D7EEAF321946AFB5EE62BEAD3EAF8784B4F2446B25C074C51F8777E7997747DB454E3GE6C7236C8B67B147DB84382C07714E761B6771AE7D58435847C25AF69454A381629E3E5618E779A196733CCEBEC967BABED305F742F3054C2E03081326A24ED90E38F201DBBE08B827402D590438182978DE61054F753F714B1A38AD9973DB5F3DE449D496CFAD1B2A45D5EC8DEFB411F344784D7834
	CA0096G8FC0B8C0B440CC00F9GCBG5E82480A01F882209C2081609C0093A0AC86577C153F11B9BDA3F7710C18240F8E97DD750064AFD26842F749D6327425115978379512047B6FCBDE5FBDC4028E31B1416FDBC6A62B6320A62B3CD2AFEBB648D970254A9A7731A66BE92F6F9A92C0CE6217AA6B51C31A2C5FF9FD4710B58D66F7DAF0D9EC1E4D87826F55978E045E2BBB8F50EF5DE10FED6EF7726FEAFA8E10DC57515E6ED6441960BA10A5F0756EC6075BCA3E47E1B1CBE2953913EB07BDC31DC657975D27
	4B21DCB78D3FFDE8B711B6AAA84346BE3F0A0C1D45D03F17C245D07A3B7A965DBDBDD9E3B327B11F47F7105952A9B3093DD5074356ECF512C80A67C24BEAB419B34D215E8D8413B987E41233EA1710E970F85B60FDFA17B99F4A0B058DD24EFA5BA033CE53479AB416CE8F214F6A6971E9A42678333883D850672D4FFF5133A4975A65CFC7FDE61F71566758E1CD1F9DA1E37B74A07AB4030C9D3C4C7A6EBE22757DFCDFA85B876B3BE53A2F6D53AA3C471AC75F475B201DC70C75BFD33C47FA7BD1CD1F93C1F339
	74907A4468EC4F3FC7A88BF52FB8E33A3F6FBC73C272ED71235BBA69B34AB7FF7A355DF1AF05F66CBE7685FAD63FB55D771EF6914527BC62FBCFABFF01E7F8965054C74477DF672F6FFE59FD1DFC31A366E3632A3FC074DF74086FF8CFD37C52E1637D2908D916832DB996477B3C57B771B3E3799DA7FFAE098DF47768B82867DB415BE5BC4A1EF90F86622B707A362F14372B63CFF3C03E5F8F6A79EC3C5DA87E1C9010EF7DA6BDDF8CEFB70BBF677B61E3B1CC45B1F87073E257C7E47D7B0DBFA06B78C43EA41F
	24970F4DF2076059ED8AFC161519C21FE5A6137F3C234AB4BC6FB8EC721FF7FCEA52679D8C57E8A2FBD28744CCE6516EB3017B3B407D0376C9F0E72D9635D360DB28DE84FFCCA182915FCE756A23B8B347F56BF0FF67B827091C47B064CE078BF9FB69F95AEE355978EF56E89B2B9D0EE7E10FABD4C72BD35669BDDA60085EBC3F2177EBCBE1DEC94E4FF03F2538C4401D956DCED68EEF57DCB93C5D4201616DDE99985E6E5DD3435BE51E5237E3FB750C93442F58BEFED1A2392CDD69B362E7C145C7E063AF7840
	E215ED0E0E8F0A0C25C546327D05F8679955270F4D4B5FBD6077772984F29FE3FEDD441F33EE5E4E65EFE91404AE552734724F1AB43DDEBBF3ADFAFDD8083C52E7DABF674EEA7DCC3E26FE760BFE2E1A35FE5ED13472F31DDA1FBFCC9919EE4B2D0417FDBBBFBBFB8D75AB1B552E54323FC6A0664FC673BF8B58EB68B14CBBDFBFC773DD28C7BF067B6542AFA431373C2AABF7FC71555F8D0D7ABF10F17202B6A6332E7C5A792FDE765BDF298CB6273F2842FB17B8B14FA7CDF86FB2C760E61844778D82B766A072
	9A9FC71CF993726E9738BDB272D6895C698A640D903813D711B7DA60622EA2EF1C402D9E2279D69C1463906728A138FC286F973885B5C22E4095EEC25E8801BBDA0B3C518297D3AB644661DD0A3C13448CD6BB24C7320D9E838C1F67D92361719860676664AEAA6F0978343CA7B0FF7F6309BDD2607C3D5165F0AAAE351FE1692F4F79DA9FDDC676BD155C5610B5CE4AB4A63E0F2E55CA687F589398933FFDAE955BCA387657121F50F31E75550447836D43G983D2C0750D95C7C2C69DA5EDB860A1BE7C01F2BB8
	0D49FB1C7A4AEC404F93721A6D1A3C9A79DA7244D49FF99B383CC468135874CF9027B3F903543FB21E0CCF4BF992655D6850647D68EAA8F9AB03482B03BE77F1F97C7D6710F71E72917551C6E8F76EC91C13B1B55EF94A6E0B1ABE3F2E89254F1AA07A04030CC22F711F26FE9E8978E8B17E63AECD5E3F560612374E0FBC1C5F853A71B31FFE280B7009FBEF6636BCAE195615232D8F6BG7DB9FE93B434F7B03D1EA22E4CFD7C65F3646FF979B94F2AA1A03441F1D6460FC86E535B86B55E1F150F6CFC2D9B12C4
	BFD1D734FE9AB60D2C1F9312564F74E42D1F5A2BA36B67B90356CFE11A56CFDB4D487AE9C8577A09D92975D3D1BB32FE1EA5212D447EC65B3DB5AFC2AD768DA9556D12B285FFBD8DCA17DC5AE7B23CBE4510AC2554CBE9ACC82F17D2C824DDAF2552C03CDEB2C0106EF1F2E50815E86F6E0B9235BCFD251A411FEB84B619AFC41A2C05409A7CE2D76B312A14F55CEAB7329F579849EFE0CC2AD44EFF6FE214BB9559E2F4349B5B343FBCE21421660671A8AEE38F940C2D0A51CE582D0AC515528AFA2C7662C2D54A
	2CEE6B267D9969AFE620570358EB2B526EF0A9C635D3716A97729C176A86346CA5436AB65AE4D591DD033557F84D568F9E7C6BAF673DFA46ED10526A25774906532438683AD2AC64F7117C8FBE9D11D6BA9DD6A0829E2642CD9F29EE355F26B4C8B96D27499FB8F12B0A259AD72CDB15B6B490C9A56425C049B1595D29128C2C22C1CA3113BFCDA52DE7E41BEC6FA8E14B37024EE7D2F58B3574DABA1FAEA91B6ACBFAA9963DE4FFAFF34736CF164EB4C849C724F7ED8E59F2C4EE03632DDEDA29F63A94F72743E6
	71B88FD5510EB27A50918C505DDF447049E00B491D1FD03A5BB3B9FCCD421AA360909E17D4C2DB127D35D8343054F64AAE9025386AD57227369C2EFCC13BE10B3A7339236F1DCB161278A00953E4FA1CE07DF63947EA6B07C29DDD9B541D32D55056A55B5DB021BD201951C7B3A4898145631FA93DFB5A5B7F2082B4D6D55A4ACC892CA59D0A2327243A3D9DE2F0518158953A5FAC3AA7ED0AFD5A2C775C396113056F34029F54C9061E52D27AFF997D7FC372FF994514D1CC5907E05C65D250FF3EF48F0E19BA0F
	D875E47A0B0F408C2A2F3C714CC3BF2DDC3E8CC6F7C4CA25FF9E82A6A1CBEBF5223727D5F10D093DFB732A89CBEEA5F364A42E62A17D3E6791ABB40D2FFC32FED7FBE17158DFC53174C78F55D5E2D5333DE35DF027A1688DE26AC8AD17B65DC47F5FBCE2C3902E2D14F79B91CF6B851A7E98A8FC132C811D267AD5717F20A9DA3A446335972225B33C30E869742F1F258FE87A63A7A744526F2AD27AF1456D16BBE0EB4A601F3A413E1B4E3EAFA2A576158D14D66237A6D0DE4B3F39B0520FAECCFE67ABA940F8D3
	3C470B8BA40DDD69B03D894FD7B311F51E2F1FF39D57A6870F5FA4215357CA53E6CDD309E91AC4DEBD9A3EFE1AFE2EE91A2453B4796B27E96BF01B16FEBDEDEA9DEED3FD1E877176D57DDD62DBB765863CCB34BD4D6F5A1F76FD4FBF66A87DBD9A6097BD0D397ECBC74887D3016E4A1BFAE41BCD15ED5D55F68B39B72F69D7956DDB21DB0F127E5D58069379F7C4C7095EDDBA1E7F85D0CB878812C21BCD0C9CGG28D9GGD0CB818294G94G88G88GDBF954AC12C21BCD0C9CGG28D9GG8CGGGGG
	GGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG469CGGGG
**end of data**/
}
}
