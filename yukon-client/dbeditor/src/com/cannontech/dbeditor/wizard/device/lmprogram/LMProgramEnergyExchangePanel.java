package com.cannontech.dbeditor.wizard.device.lmprogram;

/**
 * This type was created in VisualAge.
 */

import com.cannontech.common.gui.util.LimitedStyledDocument;
import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.database.data.device.lm.LMProgramEnergyExchange;

public class LMProgramEnergyExchangePanel extends com.cannontech.common.gui.util.DataInputPanel implements com.klg.jclass.util.value.JCValueListener, javax.swing.event.CaretListener {
	private javax.swing.JLabel ivjJLabelHeading = null;
	private javax.swing.JLabel ivjJLabelMsgFooter = null;
	private javax.swing.JLabel ivjJLabelMsgHeader = null;
	private javax.swing.JPanel ivjJPanelHeading = null;
	private javax.swing.JScrollPane ivjJScrollPaneMsgFooter = null;
	private javax.swing.JScrollPane ivjJScrollPaneMsgHeader = null;
	private javax.swing.JTextField ivjJTextFieldHeading = null;
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
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public LMProgramEnergyExchangePanel() {
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
	if (e.getSource() == getJTextFieldHeading()) 
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
 * Return the JLabelHeading property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelHeading() {
	if (ivjJLabelHeading == null) {
		try {
			ivjJLabelHeading = new javax.swing.JLabel();
			ivjJLabelHeading.setName("JLabelHeading");
			ivjJLabelHeading.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelHeading.setText("Heading:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelHeading;
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
			ivjJLabelMinutesMNT.setText("(minutes)");
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

			java.awt.GridBagConstraints constraintsJLabelHeading = new java.awt.GridBagConstraints();
			constraintsJLabelHeading.gridx = 1; constraintsJLabelHeading.gridy = 1;
			constraintsJLabelHeading.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelHeading.ipadx = 5;
			constraintsJLabelHeading.ipady = 1;
			constraintsJLabelHeading.insets = new java.awt.Insets(0, 9, 3, 1);
			getJPanelHeading().add(getJLabelHeading(), constraintsJLabelHeading);

			java.awt.GridBagConstraints constraintsJTextFieldHeading = new java.awt.GridBagConstraints();
			constraintsJTextFieldHeading.gridx = 2; constraintsJTextFieldHeading.gridy = 1;
			constraintsJTextFieldHeading.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldHeading.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldHeading.weightx = 1.0;
			constraintsJTextFieldHeading.ipadx = 310;
			constraintsJTextFieldHeading.insets = new java.awt.Insets(0, 2, 3, 10);
			getJPanelHeading().add(getJTextFieldHeading(), constraintsJTextFieldHeading);

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
private javax.swing.JTextField getJTextFieldHeading() {
	if (ivjJTextFieldHeading == null) {
		try {
			ivjJTextFieldHeading = new javax.swing.JTextField();
			ivjJTextFieldHeading.setName("JTextFieldHeading");
			ivjJTextFieldHeading.setDocument(new TextFieldDocument(TextFieldDocument.STRING_LENGTH_40));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldHeading;
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
	LMProgramEnergyExchange program = (LMProgramEnergyExchange)o;

	program.getEnergyExchangeProgram().setHeading( getJTextFieldHeading().getText() );

	if( getJTextPaneMsgHeader().getText() == null 
		 || getJTextPaneMsgHeader().getText().length() <= 0 )
		program.getEnergyExchangeProgram().setMessageHeader(" ");
	else
		program.getEnergyExchangeProgram().setMessageHeader( getJTextPaneMsgHeader().getText() );

	if( getJTextPaneMsgFooter().getText() == null 
		 || getJTextPaneMsgFooter().getText().length() <= 0 )
		program.getEnergyExchangeProgram().setMessageFooter(" ");
	else
		program.getEnergyExchangeProgram().setMessageFooter( getJTextPaneMsgFooter().getText() );

	if( getJTextPaneMsgCanceled().getText() == null 
		 || getJTextPaneMsgCanceled().getText().length() <= 0 )
		program.getEnergyExchangeProgram().setCanceledMsg(" ");
	else
		program.getEnergyExchangeProgram().setCanceledMsg( getJTextPaneMsgCanceled().getText() );

	if( getJTextPaneMsgStoppedEarly().getText() == null 
		 || getJTextPaneMsgStoppedEarly().getText().length() <= 0 )
		program.getEnergyExchangeProgram().setStoppedEarlyMsg(" ");
	else
		program.getEnergyExchangeProgram().setStoppedEarlyMsg( getJTextPaneMsgStoppedEarly().getText() );

	program.getEnergyExchangeProgram().setMinNotifyTime( new Integer( ((Number)getJCSpinFieldMinNotifyTime().getValue()).intValue() * 60 ) );

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

	getJCSpinFieldMinNotifyTime().addValueListener( this );
	
	// user code end
	getJTextFieldHeading().addCaretListener(this);
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
		constraintsJPanelHeading.gridx = 0; constraintsJPanelHeading.gridy = 0;
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
		constraintsJLabelMinNotifyTime.gridx = 0; constraintsJLabelMinNotifyTime.gridy = 1;
		constraintsJLabelMinNotifyTime.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelMinNotifyTime.ipadx = 13;
		constraintsJLabelMinNotifyTime.insets = new java.awt.Insets(12, 8, 6, 3);
		add(getJLabelMinNotifyTime(), constraintsJLabelMinNotifyTime);

		java.awt.GridBagConstraints constraintsJCSpinFieldMinNotifyTime = new java.awt.GridBagConstraints();
		constraintsJCSpinFieldMinNotifyTime.gridx = 1; constraintsJCSpinFieldMinNotifyTime.gridy = 1;
		constraintsJCSpinFieldMinNotifyTime.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJCSpinFieldMinNotifyTime.ipadx = 50;
		constraintsJCSpinFieldMinNotifyTime.ipady = 19;
		constraintsJCSpinFieldMinNotifyTime.insets = new java.awt.Insets(11, 3, 6, 5);
		add(getJCSpinFieldMinNotifyTime(), constraintsJCSpinFieldMinNotifyTime);

		java.awt.GridBagConstraints constraintsJLabelMinutesMNT = new java.awt.GridBagConstraints();
		constraintsJLabelMinutesMNT.gridx = 2; constraintsJLabelMinutesMNT.gridy = 1;
		constraintsJLabelMinutesMNT.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelMinutesMNT.ipadx = 8;
		constraintsJLabelMinutesMNT.ipady = -2;
		constraintsJLabelMinutesMNT.insets = new java.awt.Insets(14, 5, 9, 167);
		add(getJLabelMinutesMNT(), constraintsJLabelMinutesMNT);
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
	if( getJTextFieldHeading().getText() == null || getJTextFieldHeading().getText().length() <= 0 )
	{
		setErrorString("The Field Heading text field must be filled in");
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
	LMProgramEnergyExchange program = (LMProgramEnergyExchange)o;

	getJTextFieldHeading().setText( program.getEnergyExchangeProgram().getHeading() );

	getJTextPaneMsgHeader().setText( program.getEnergyExchangeProgram().getMessageHeader() );
	getJTextPaneMsgFooter().setText( program.getEnergyExchangeProgram().getMessageFooter() );
	getJTextPaneMsgCanceled().setText( program.getEnergyExchangeProgram().getCanceledMsg() );
	getJTextPaneMsgStoppedEarly().setText( program.getEnergyExchangeProgram().getStoppedEarlyMsg() );


	getJCSpinFieldMinNotifyTime().setValue( new Integer(program.getEnergyExchangeProgram().getMinNotifyTime().intValue() / 60) );
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
	D0CB838494G88G88G61F854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8BD4D45735A4C10B06C41290CB8CC9502021ADC9CCCBD6DD8DEF5536B44F3C5A955FFAE6E9935A78DA5A50D7135A163EC792DFBD8C9F71039240840DC9C85488A98F7147CF93C17003E2CAA24F5046D6AF4CE5E6E01899E7AEB008605B67334F3DB34C8CE2133E3856765E337759E76F334FB97B6CFD4E9DC849DBF139D92692C232F20978D71BC5C879D9C2FE7D75E34908CBD0D37208615F5BG2F
	107AF5B970DCG344CEF266495123AE379D0DE8C655407D272DE0377E2727E311CD6F889A2A775A4A12DAD89BF8AADE774EFA8E7ADABFFFBECA3BCB781AC819CFDEE6F2172170D158AFC0640A3E419C0858B2D735B316D82F791F83CGCBG52FAF87D5570DC47A4CFE92893754E5F96C7B27EE537290036E3E893613C05730609E799F9B82B5CCAE53DFEF52360D9G34D5G347359E4438754A4075B7BAC9FF4BB7B2B3A950743695054EEEB55106D3262B6D71955C1DB37DAE56FF7391D9637525F5050F6261D
	3F9EF328EE4B70B1EF37D5F1D854F645215A212DF8A62F7BEABEEB1F90BB142BED94773D03D407F4A893A8175A90BF3D0662B5F80F840837E13F5F5FFDD8742BF364E132FA19A3FBF310FEA75BF8FF53ECFA3F2387DEDA3B150D51BFDD20E3D1561B1257825089F085A096A09960F5B6B7D27E769EBC1B3A34E6E7FF3F535164F29D351B5DE71CE6558E6F8D8DC091380F55EE275BCC889F7F372FD68650A794F03EBD136663B6C9580FF3A17163F9A4759B27CDEB8358A4B5215A3434175BE2E52FEE93AE5B9AD4
	7646BE5F396AAB7B93E2143DF22AB3BBB7006C2427AACCE5221D83FD3AECF39F9D6BDAB6569D701EA5668747B7B1FC02814F676D93EC4E50728220ED37635C685C043A34654CA205FB5B825AA1663BCD26B0BB57A1466E2F4B9F184C94BB1739C16A32311662538CF82ECB942BCF4BB97DA9F9D6GBE3F9ED768BADE8C65F4G6200398DD0875081E0F5609CAB3830679A67D83B62560E8E586CE655CDB86F6397DE05276A555CCA2B4323A90EEE751C6236A9DDF655C3C41DA7AFD09BF47970ADE5F33F9BE41CD0
	E139AA1A4D69005615FED5D35D9EDE674797DE21F55CAA23F6587AD590506FA2586E4FAF9453F52CF8347FF51995CD55EBF07A739768188CAA6720C5E8G5E39DDFE57C56D95817D0F8318AF6DE03DC85BBB2D3A41E100BBF1BA7B86DC9D34A704B8291D2FB25F118275D39DE863DFF7D3DC8B504FBB311D7BAE96995AE931F5D3759577B02E3177EB282FF6B9F14E3C5009F32625E0AE29FC6AD440B943660A134F1579CE7F35F30235C9756265A78FF68A9913D94B13739569CCC67667507EA6B9D6FFD569D8AD
	07FEE4G14385046CFDFAC63F3762862D1F1167E498D93CCB56B9667FA66541688BD69FE1BAB74DFA77BD144681550F687409500F80045AE34654C1ACE21D3369FFFB05DDF1B34AD9F7611016DEF884117BB89995CE6CB943ACF29032AC3EBD1DD2A438CAF9EC1DB26D0F9E29D77B8A54F0F995E856B3E99961AC762FF26503529E81A5B56B52029E7D5CD33B9ACBA7DD7CC16E7204B253B8C1475DB056A6E9C02F5AF6B7F17C257CC375361D05960C996C1FF112DD9552E7603524D4E81074671EB3A683C6FD307
	054F486E26415120E29F207E0747927395EAC7175B568FD3023AB052F9092E6719D7CB8DB64A007126B1D1A5509AAF615A9C5A01EBF3676559E46105DD26C0FBCB4C5C96D35CA53E5612AE69EB136B14519DD87717186F90F8CFB6434D3BC4673D8CE51A875C1AE233538A984770F5B13307363994E4258390B72E0BC1B325236F918B2259E937F333CAFBF72B05F4FC54CB83AA38EC6A64CECB9B762AC5FE34F3421E1C6ED629BD6D867F18682676BCD10BF1E3A9142B815A5D5C2611EEEE93DEEFFD9D564B24E5
	8F5475842A77C3D94F8E74702075569E41FA71C0CF8EDA6FB1D9AF155283546373E56D859A07840ABDAD83362A814DC63D3A2241C2F18D70E16131A5A179220D928DE3484AECBACEEDD0B683DC51F0DE7DEB81461051FDF1E4457D5B02464E519A57ED01264F2B321BB006DCE121B2B30116BF00F360F955380FB5F9BC4EEE9B5BC24578457698679D474D6ED98F4F9E1B5B233502E37062D8BF50C37525FB2CC00B184345E2915A576DA03783E065G45BF53DEE250BD92F0C9G4B81787CDED6CBE30DEE452DEA
	D56CFF3EF51777AA030A374AB384CE240AF9282AE6CABDC6DFAB443824D494F19DD5E6F941D9F18E6340B9450DC26E6207E61143D70BCD016272084DEF188E8CF2DB1E9F54EDFA656BF959AB7F7672DA5E3F7DF5583F842833E2907BD7D8E36CDF6990788AG739076EFD65D75740F4B7CCFA9538B6D4D9FC21961DEFF1969G9B3DA9F91C2F7A887215812E530BFC83F57EFCD1C0CB12FCE912EF916048B0723DD56C4FD787347343389F66927FB85DB52C4799614363E38E9E3F46313590B34C6357F5927FA043
	A79970DC3FFD8CCF4BCB00F660B246DCCE9B755BB1D0CE82D88C108610B3121237F38457409E4BFEB2C1CC5B8AEB5A6DD06CE7FDB6A90CADADF4BCE1DF9165DFDA281D583EC2F04F9E2C7537C36308EF5F4F7B15B9DF78B83EAA3FF2189F9F1F7F3B4A3723DFB2CC270606D39D30EF9F3729F6B35F571E4BB66EEBB1A3BCDECD066792G6E2B0ABE42BCB8FD84FD5268F123CFCA5E3C4F34A108CFAA3B4275EB3C222F1FCA1957EE6D23FA0681AD96A089E089C086C061A80C63A80E11477A7EC4E34456C7073399
	F0A7E13FE9760F8F24CD6B6B2EEFACCE0C63739F0B0E51C0E351BB72FA703120FB0208B19EB7994742BE4A7DE18CBC93C7F1ACCE5460D8240C629ADBFD1D7DD9DFBFD1FFD67B75076B99D79BC84F12B17095E32867E3DE5433F18C752CB9F2FDFA360C63735753BB96C84F0452C0FAC6D19D251E09F528E7325473BF2ED34F4C89754C8828672F9B82693971EACADE59D55473DCB16AD987B81EE3EEE7B1048B4A619277E29645A5C0F961D56C4F3CEC9AEF2451BAE150A6C0CB98523E41E8D45EC540058144C9DA
	DDF6271F7EF2CD07054E95C990FA0B4027C95DBE8AAAE3ED0876F1ED8542E32EC5ED45FD075796BC1739EA8B1C4BCC31854EE5EE363D4271AC4A56F782245FE18B1D6B5CED8B146B3C4174FD62554D2CED0FC775B0E7C683C7C1FBCE5BC28264BC9FAA7D2A68EBB1F930F61D28F3CCF3B66FA368FFFED7C167D7G7FE308C75F4A4997B6DF980671E843A85DD3B321DCF8832C6D9BF04CDE7BA85439DF638D63471E7B7AB8BB0D95BA006E8208G0883D8F8837A783D3D936E43473CA176619DFD4147793D3E4063
	DC5DC7E3E90FF3405D2D7A0FE5EDDF2924693BC92B2676D33373BA0D0CDFD35C96F55CDCB84178B9AD98FFFBDF3079424754D5E79C533DF2CCAD475E88BC26E73B5D104131DD854F317FE0C25F3162869C5BCF5919E4690DD086B8F9A30EEDDBCD28313DF863783165FA6E2FB76A39DF4ADEDE0B32359BD17613C3D4F6A214D3GD2256C055ED032B3024A6EBFE214FDC04ADED10A3273EFC2598BD9AE528865CEG57CDA87B11F1FB2891A2EE8AA67B6DC6236C03D2766EC3A8BBD64A6EADE2F9B814D782640423
	6CCF0BC3495E9E9E5466041EDF5815E1670086F242290CD3847712EAA81F84F081040773380E4F1DC706E82C5663D67AE14A1CF5295D44BB0565ABB27EC8037A19G25002F83681C02B9CBC4891D6F5CF31CD495B334A075095EC9F349D3EDCA17EABFE3F3FC6854ECBD437420D550EB0F5276CFB51FF5599CAC847529446B7CC8A9B43681CB42F366438E1EEBBCDCE24CB596CE61FDCA1B02394823DB0D74CCC12F1C0A746DB501789B2762B86C4CB10EC387602DD3F55BDF199AACD779A97313D1D3F9CE32C562
	FF4F70A986BC4FD59A991E16B301967FA55C3B2BBE423D3BBD82FDE202BD38CFFB589E5827BDEA8F3CF7BDE68F3DF7FD4F9E4C97F17BE4E7071A27F191416C73A473B5C9917E77A5BFE27814887F7B92C55E17AC85DA5DB434CFF78D5AA7F69A0E590F4765C7C6D8B8AD18CE4DDE369627796BF406615326796B7454906A1481344E692853B2AF6A14BC9DF57AF9E9A81DD6CC8F2653811653E7CC77576983064F3959DF2747EBD1279220F9EFC61D965521CE29B723CE1507C269C4A2036974936633CAA27DF5CA
	E7781D117EBA390AD02783C00B09C41D5C4528536AC8BCD3F0933C4FAD3B85ECFA8B7A273D97FDF257AAE5C8EBE8B86136190FAA16B6666D380F7D960BFFA9FF54AD68E353D91B0B203C82A073964CD73D6FE23E1AB8EF96E98BBB9D708CEDC547BED3612D625E6FD6BDDFCD00FC154B2CB121CC975093EE25B8334968D7E85FFFE56231BF274F742153B5C677EE1D3E4807CE75ED4AB652DF7221D39B6D967C3CEFBF5805FDDB7D0384E2FF34BE687960AA512712997C59FEEE8759B97D6535A2CE6C8992A7B2EF
	4C7B7F0B920C935BE660F80D553CCB8CE720FDF6CBD5EFB775BCD5BD544BC3833A4B477DF165D6BC7F8C1F01B94E599AECBBF9864E1BC3ACAE4D00F2FE9462B6D5D0DCA5149BA56E1677F0FE1923F04E270F1643B3894ACBGD681945E867384208D408A908910883064B664F91971E45E464F249B4579D9696D6007EE67E7E21D70745E4E4F39B84F71513890EB4B477ED1935F31D1FD705DAA5E5B8C38425B823731C434DDAD7A03F85A47A4E913CB572C6BC4799705E2437BE671AC701B4B0178B0F723F14896
	3F35C1574F625B69189E7B28A5046CDCD197A186F89281F6885AC2F86FB771F540674836EC1CA3A5F720BD1EBAC86F044C327CACAB53FB86DE369C8C161FCABFF947755F1BF299E1934A78FB6ECDF9ECF73986E33BF050B786E08140F2004CBBF08DE55422FDCA23317F2DDEEA8F2BAC1FE565F0E91FE706A652FD13603FFE7BE4DC130C6B3591374F8BDE34CF84689A8B108C108A30BA9A6D333A946D337DCE6CFFC39D3507DD16DBD8B9D95AE7F16D3568FEADC0E584364F4FBFB39901EC486D73EC9D5AA79274
	0883D8883082004CC47BB49FC27B144D447E2795316F97E47985D64E107699AC3A965DA97F754F1F9957A4636F1BBF530A51BE5120EB8240A200F4007CAFE3DF3D636EC22ECD7F295749776DC97371586EDBD9B406AA7FB20F6D32A47EFB8CFF400067F15C8BB20EEB84DA7C17716EE52A035AA4BFA6A5EFA740E1008EG8DA0AA86731BBF77D713894EFCBA1C2EB6C8006C061405776F10697A3EBF7B4EDA0C17123EB507AC187AE2D6303B0B7898BE6FD244B33DE387430B3B8B976DDF86508A6FCA49BBG5086
	F09140FB9776EFF6FFA56F5FB133CDEB52B4255B4AEED231F367EC6A100E2649B86F5B2FCA0CFD0B3C0B4B0F934F243B0279025FEC25FC0B8250A92486656B74B977D9A56A654C624F52D9414EA0BE4E21E790DDCEB7CBA4799904AD8757C2A57035GDC81081A454F35B95F2E0A71E79707AB10AFE196BF3BD8884F25020F4784716F050A89F0DE2E957A2D84DEF2B70F89D81E846F4F2FD93366B7GDC0FE75FE46799AC4275BF4FE8AF207108FEE366CFFF61D59A179BCE5C4EF8AC340E6A66743B8E0D279FF7
	BAB524AFAB5CACE5CBD651768E278F0D33895A1531D747355BCC3F26332BE6544DFA7595A33FCE9674541CADC67AD94D69F22966E30A5BBEAC6B28955B4649712D484FD58E0E791F2B5071AA3B9B4FDDCAAE87229F1074D78352CFCAFAEEC3A07AF9C9BF5D99086E827ADB04472879847F2DBAB29E87714C5D74BB690F0668BB77917F3E91FD44762F5DCDDA163D1835038476910B6E96EBA4D64F9753FBD72F6573F5EFF628790A5FEFE1AC5B8EED18E375796A05776F1B78B7E2FCCCF66EAF42B969B363B02F0A
	2B11F9D5AC66274BEB7C7353CF08B13FDCDF6B1F1FE679508F577A67271EACA35DD16B1F1FA2BF3741063A09EC3041E003E450FB094186E963ECF07A83FF9B0865A1ECD0B20CB6D8A5ED50B69C5A86E34321EDB0FFB8348DFEBA9C5A8645DBA73241E6038D4A6F017D729E5D866D77785B603BEC4D99ED00CB1C5BA0379EEDF07E9E34417B7521ED602C8FED03597521ED70EF7521ED7017C393592058E003E82A3741864963ECF03528544F86C66F446D70F0B15AE1113443F345216D3035A4349DCE17043643E8
	C9B0BB08FB8E16127277A78C6F9964330A63838117D11075F94A60B14F8CC770B36B190E40E756310E40E756F39C216FD33F6A887E6D68A38E633723BCE633326F23483D104381B482F4GD86F45F86D08739D426235G315AB133055E8352AF57B5147F0EABF8DFABDC93FF6F3E4FC53F256BF2EA1A331F360F78BA979DAF184E8C49F16F3B280D5CB60BD563D5853E59C56738DD6D61E8160B1936D0DCB874AB7ADE4C4FF69DE579991497C85C5423183325C8DC7EB60AAB39AFA52F72BE44B937E13DBA9D4776
	1A88A847C8DCAC4B1193211CACF1D9DB11F751FDF876FBA2974F09CB63D2725A6370AC7139B7AA48C45FD3F658B43BFAD4041C42674F4845F59E91A76FB84B290C05D0DE8E30AA8EE51BABB0DF491F8DF3E0367C9624824FF63D33310D27D95D79D0DE8430E2763546942B0E7018A26FB211BA5A4770FCB3D34ABCFD99F155F390D7BC0AB82B449DEEC0DC1C445D58093865F3701C7B49CF0D3F33AA1C753689E3E27F35DAF83F782D577DFA5C4257C87D8875536D003783C481A4GA483AC85C887D88DD0988F76
	84E88468G3082DC8108840883C80A47B5B5F3080E69B925D7E4F7B495F23A592A8224B858E78B4B2D586852A213337116D1C2503F4D7549E396018E2971214F8EA6A76B4EB1DD5656E11F1C8964644C7DACE5AD382A4B727DD62B9464D4FE26321EA13A2CA41F7B68C61053B101ACBE4EAEEFF0DFB8641D589706313BB495FCD84730CBFC9FB145CB73C4E7CF0FC715BEF73A37D062DABD6DCE0F0DFED741E3027B466879D28F780C7839F40D7C1179C1AB3CEBF3B1777C66902D2301B23C6FFFE0FBB00FD16EB9
	BAD1FEE9BCF3CEB41CB9D3DA7ADCBE6731DEB1F9EB9B0DEDB527535EE1F351C8C5649A3BF2F419D337CD949705121981B269DE30DB4A1CDA63EB03D86627493C143C0D7350865FA875354149DCDD1F5210312A7F393B3FBE07C1865D337F9BF2ECBE1F4E64CEF4CEF3F2DE206F69BE1D106FCD3FB2670BE6FC66F957F6E66805FA621E1A6D9711737C4F3D16B3FC4CBC7FF3AF259C7711F82065BF0067FC3D9557F7DE673DCE3EC7A64D477B7561FBD4FF7B837E7D7D04613D8F7877F7055CB7430116AE7BFBE94C
	A83FAD015B7D22F8EA89414E1C5AAF9B79A2C43DD8711C9F142FF4544817A26AAD974F15C1791A9A0CFC64AB62AED4BC4B3F920C6FCB1DC63E4322DE3BF81A837071FD7477ECCDF11FF56A72AE92E48F04487214B2287C292BD76D56BA940B64B9B3925C1FF60CCC6CCF7FBA92B8366C9E999FDBDAC78247160E91E3ECA93E459E219D33403E5DA96BF9A56E4F92B74A5A2438CFECE64DAA71B7B13DA87E24CA85A2FE9A53EB0861BA856ED6836E4F780D65885D93204BD6279BF9E3C6184F33596DA21FE0F5EC8E
	58A224BDE6B39DEDAE452077BC0923FA73F8B51979CBAF0CAB4D6BB86EBB8C978538CFE43D6A6171758E8E0F2F77F67D78FA6F560F2F770362717516949B6BF1DF7D9CB51F7075DB0F52FC45218E15612792D5474171173FE336A9F627651D4A326A4A323D87AA70DCABEE48989F562D2F8D789BB58A09E2DEAF944F64AF6D90729536FC78FB78E67D3D2CC857AB2A74DA745AD7013C4DB4AC915F3AAEACAEE0E3E737B954E67E2BDFE9232F31B8BF93F4AA7CAA6697BF5BCDF1BB21DC77D5F92FCB2F71457A7D09
	615DD27E796758E17FG999D6F69FD7A2B617D97951F3F6CB51F2B8CBE2691E3183F3648F10E9B427C35D362F26577D7E60933DCC65EA409CB6BC45EC5927752BB483BD462760CA26F4E2FA1EECB937256C95CE0937236C8DCA74B5122219CAFF1BFE4317682A8AF1238FD729B7D2592F7EFA7726ECC143A181037CE6222CCD2EEA2664C4A3BF4DF36B9A08CD66C4C55F27CF6EF010EC7D7AB7626D54C2FC4C39B7393B1CF2B5D70C792BCCFEBF7BBDD2ADB9B66D876ABCFBC0B4C253EC523D9B9DD63704E634E27
	59DADD826D27A6E25C79526EDC2CCBF06D5B69D50460F9140D7B4687D372CA81B8FD865D6AC4EC3A5D7B77444AA7214D8D024665C531395281780BD25E57DF53653D32E3A2F939C40F09D15E96A1AF8A5A24B6FD99E2E1AE2F134D2FD840A7C8F9CF156A72EEE91AC85E3A907216C31B7B05BCEEEFE703EFAEB0054D51B428376AC19C13B4621B8BE4EE5575E94D1AC81F02907A14BD1412D761537FFFE6733C9270F58FE17F676C5065BDE01AC85EAB81646178DA9F527B4F67746F0BA91FBCBF6453D66C7D2B59
	B930D89F364B6CE72FE72122434275FA13FE56A66678325D74EF6784584F1AA0683243F636D7EC33E2CEAFAF57F923072EF50F663CAF53E644FBEA255E4E724E49353366FF74F6BE2D565BD957B439F646766A6DBCDB2B37739DB239F6CC75FABB3FBC24373343B439F6B6521F82927E771012F2B74302C8FC1E64FA5CE43364EFE5015F0A417D8D59BF3BBF5BC4F2DAC99E8F04DBC98E0DE6DBC9AE8BF6DBC9B68442DE17D046844022AB4FA542CEFA5855D43EF896C82CC9ACC41690CD004D8E085DE844EA2470
	0C4DD146BF06AF23DF43B7E824CEFC7FDE26D8D545DC666CA96B56FF61DF26C049837DD15DE57D70D2562516B9A83BCDB5EBE4DDF9BF672DG2B9478D8757B338F7EE271CF4EF932C9DEAB59CA9D43D9554D663FEA263FB792FFG65B8D967F25A0088F8B01907BDF2BD5A30DDEDA3A5BD9F539F7CFBB4555C04AB4B2311ADEDD4529EFAC83B67246231EAB49BB954C6F29C74CF3510A2CE36AB8E4B9E3E4C8EB13BE7B5DDE496E11BBF68DB4C74252DD449D60A9F5B3C6B6DD3E98B320969B8D9EFF7AA6663CAB7
	ECC32DE41DE6F52B9E2B53EE763A0EB432067207F04032213957E577E9E72B68990CD43A4FFBCFE4C1FA41F198B82F1B6CE1B5299F2C12B54C4DD6458D22D4F72BC67F740C533DD352EEF8D3FB7707A7DEBBEFA2D92213F4F0B7FADD9A496BD17AED76E1F8E9E1F3188DFBA18C8C64EB8ECF0F535D8F1A1579E906A4A9206A0EA72A6F7C4ACBFFE182F4D60D3C45CD892CFBAC2A33FFCFD3CF8F444A328130ABB47F06EC1E5629722BD3643DED4B292756F441BCE8A1597D55556C7F3D6C7FFD747F3D8C3317E176
	6E83632EA2A17F79533D58E7B6F9646A2443DFF59CC6D0FB7A79A766BCF3F855CA685DF1124BFEAE8D03D028577AF0203FCBF5CFCD3C730D8D51ABEE26E36422D345CB5BAD714A1514A7D6A8DDE79BFD3038BDEFE05875EC2EB54A5547577826711306220B6450515296D675B5767F36C99B02F2EDE73CEFD2712C3CCB579FB77487689A50485AB32A4723D8E041640B8FCE40972C65377C740D5FF5435BBA3C710577CD6266330CDDFDB6987A2B2F2D2CA07D4D716DAF8EDB9ECF3A395E146733B5848E64BEEFC0
	E57FDF8687F5BA649D673C3324CEDFA8CD0F6A1A922EE9965D9C333FF81A366A1AE699B4B5FD71B4ED9FEF536AAF26CDBF99EFD39EA573331E0A2B46331E4CA925C14FFA8EAF90E71A8B7C6F822B57539EC582BEE1816609E16BE8E0996CAC73EC3FE237EB0A3D2F49E126671AC707B5D53F2FBF381E366F41BA0248E90757D33DFB8DBC7F87D0CB87888DA831F46C9AGGD0D3GGD0CB818294G94G88G88G61F854AC8DA831F46C9AGGD0D3GG8CGGGGGGGGGGGGGGGGGE2F5E9EC
	E4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGA69BGGGG
**end of data**/
}
}
