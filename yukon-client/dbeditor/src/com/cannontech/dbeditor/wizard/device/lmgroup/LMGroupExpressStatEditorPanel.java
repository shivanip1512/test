package com.cannontech.dbeditor.wizard.device.lmgroup;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.common.gui.util.LineLabel;

public class LMGroupExpressStatEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, java.awt.event.MouseListener {
	private javax.swing.JButton ivjJButtonDeltasAbsolute = null;
	private javax.swing.JButton ivjJButtonFahrenheitCelsius = null;
	private javax.swing.JCheckBox ivjJCheckBoxCoolMode = null;
	private javax.swing.JCheckBox ivjJCheckBoxDeltaB = null;
	private javax.swing.JCheckBox ivjJCheckBoxDeltaD = null;
	private javax.swing.JCheckBox ivjJCheckBoxDeltaF = null;
	private javax.swing.JCheckBox ivjJCheckBoxHeatMode = null;
	private javax.swing.JCheckBox ivjJCheckBoxMax = null;
	private javax.swing.JPanel ivjJPanelData = null;
	private com.cannontech.common.gui.util.TitleBorder ivjJPanelDataTitleBorder = null;
	private javax.swing.JTable ivjJTableCurve = null;
	private javax.swing.JTextField ivjJTextFieldDeltaD = null;
	private javax.swing.JTextField ivjJTextFieldDeltaF = null;
	private javax.swing.JTextField ivjJTextFieldMax = null;
	private javax.swing.JTextField ivjJTextFieldRand = null;
	private javax.swing.JPanel ivjJPanelDataChanges = null;
	private javax.swing.JScrollPane ivjJScrollPaneJTable = null;
	private javax.swing.JCheckBox ivjJCheckBoxTa = null;
	private javax.swing.JCheckBox ivjJCheckBoxTb = null;
	private javax.swing.JCheckBox ivjJCheckBoxTc = null;
	private javax.swing.JCheckBox ivjJCheckBoxTd = null;
	private javax.swing.JCheckBox ivjJCheckBoxTe = null;
	private javax.swing.JCheckBox ivjJCheckBoxTf = null;
	private javax.swing.JPanel ivjJPanelTable = null;
	private javax.swing.JTextField ivjJTextFieldTa = null;
	private javax.swing.JTextField ivjJTextFieldTb = null;
	private javax.swing.JTextField ivjJTextFieldTc = null;
	private javax.swing.JTextField ivjJTextFieldTd = null;
	private javax.swing.JTextField ivjJTextFieldTe = null;
	private javax.swing.JTextField ivjJTextFieldTf = null;
	private javax.swing.JCheckBox ivjJCheckBoxMin = null;
	private javax.swing.JCheckBox ivjJCheckBoxRand = null;
	private javax.swing.JTextField ivjJTextFieldMin = null;
	private javax.swing.JTextField ivjJTextFieldDeltaB = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public LMGroupExpressStatEditorPanel() {
	super();
	initialize();
}
/**
 * Method to handle events for the ActionListener interface.
 */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJButtonFahrenheitCelsius()) 
		connEtoC2(e);
	if (e.getSource() == getJButtonDeltasAbsolute()) 
		connEtoC3(e);
	// user code begin {2}

	// user code end
}
/**
 * connEtoC2:  (JButtonFahrenheitCelsius.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupExpressComEditorPanel.jButtonFahrenheitCelsius_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonFahrenheitCelsius_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (JButtonDeltasAbsolute.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupExpressComEditorPanel.jButtonDeltasAbsolute_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonDeltasAbsolute_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (JTableCurve.mouse.mouseClicked(java.awt.event.MouseEvent) --> LMGroupExpressComEditorPanel.jTableCurve_MouseClicked(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jTableCurve_MouseClicked(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the JButtonDeltasAbsolute property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonDeltasAbsolute() {
	if (ivjJButtonDeltasAbsolute == null) {
		try {
			ivjJButtonDeltasAbsolute = new javax.swing.JButton();
			ivjJButtonDeltasAbsolute.setName("JButtonDeltasAbsolute");
			ivjJButtonDeltasAbsolute.setMnemonic('s');
			ivjJButtonDeltasAbsolute.setText("Deltas");
			ivjJButtonDeltasAbsolute.setMaximumSize(new java.awt.Dimension(95, 25));
			ivjJButtonDeltasAbsolute.setPreferredSize(new java.awt.Dimension(95, 25));
			ivjJButtonDeltasAbsolute.setMinimumSize(new java.awt.Dimension(95, 25));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonDeltasAbsolute;
}
/**
 * Return the JButtonFahrenheitCelsius property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonFahrenheitCelsius() {
	if (ivjJButtonFahrenheitCelsius == null) {
		try {
			ivjJButtonFahrenheitCelsius = new javax.swing.JButton();
			ivjJButtonFahrenheitCelsius.setName("JButtonFahrenheitCelsius");
			ivjJButtonFahrenheitCelsius.setMnemonic('e');
			ivjJButtonFahrenheitCelsius.setText("Fahrenheit");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonFahrenheitCelsius;
}
/**
 * Return the JCheckBoxCoolMode property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxCoolMode() {
	if (ivjJCheckBoxCoolMode == null) {
		try {
			ivjJCheckBoxCoolMode = new javax.swing.JCheckBox();
			ivjJCheckBoxCoolMode.setName("JCheckBoxCoolMode");
			ivjJCheckBoxCoolMode.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJCheckBoxCoolMode.setText("Cool Mode");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxCoolMode;
}
/**
 * Return the JCheckBoxDeltaB property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxDeltaB() {
	if (ivjJCheckBoxDeltaB == null) {
		try {
			ivjJCheckBoxDeltaB = new javax.swing.JCheckBox();
			ivjJCheckBoxDeltaB.setName("JCheckBoxDeltaB");
			ivjJCheckBoxDeltaB.setToolTipText("Range: -128 to 127");
			ivjJCheckBoxDeltaB.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJCheckBoxDeltaB.setText("Delta B:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxDeltaB;
}
/**
 * Return the JCheckBoxDeltaD property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxDeltaD() {
	if (ivjJCheckBoxDeltaD == null) {
		try {
			ivjJCheckBoxDeltaD = new javax.swing.JCheckBox();
			ivjJCheckBoxDeltaD.setName("JCheckBoxDeltaD");
			ivjJCheckBoxDeltaD.setToolTipText("Range: -128 to 127");
			ivjJCheckBoxDeltaD.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJCheckBoxDeltaD.setText("Delta D:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxDeltaD;
}
/**
 * Return the JCheckBoxDeltaF property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxDeltaF() {
	if (ivjJCheckBoxDeltaF == null) {
		try {
			ivjJCheckBoxDeltaF = new javax.swing.JCheckBox();
			ivjJCheckBoxDeltaF.setName("JCheckBoxDeltaF");
			ivjJCheckBoxDeltaF.setToolTipText("Range: -128 to 127");
			ivjJCheckBoxDeltaF.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJCheckBoxDeltaF.setText("Delta F:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxDeltaF;
}
/**
 * Return the JCheckBoxHeatMode property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxHeatMode() {
	if (ivjJCheckBoxHeatMode == null) {
		try {
			ivjJCheckBoxHeatMode = new javax.swing.JCheckBox();
			ivjJCheckBoxHeatMode.setName("JCheckBoxHeatMode");
			ivjJCheckBoxHeatMode.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJCheckBoxHeatMode.setText("Heat Mode");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxHeatMode;
}
/**
 * Return the JCheckBoxMax property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxMax() {
	if (ivjJCheckBoxMax == null) {
		try {
			ivjJCheckBoxMax = new javax.swing.JCheckBox();
			ivjJCheckBoxMax.setName("JCheckBoxMax");
			ivjJCheckBoxMax.setToolTipText("Range: 1 to 255");
			ivjJCheckBoxMax.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJCheckBoxMax.setText("Max:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxMax;
}
/**
 * Return the JCheckBoxMin5 property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxMin() {
	if (ivjJCheckBoxMin == null) {
		try {
			ivjJCheckBoxMin = new javax.swing.JCheckBox();
			ivjJCheckBoxMin.setName("JCheckBoxMin");
			ivjJCheckBoxMin.setToolTipText("Range: 1 to 255");
			ivjJCheckBoxMin.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJCheckBoxMin.setText("Min:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxMin;
}
/**
 * Return the JCheckBoxMin3 property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxRand() {
	if (ivjJCheckBoxRand == null) {
		try {
			ivjJCheckBoxRand = new javax.swing.JCheckBox();
			ivjJCheckBoxRand.setName("JCheckBoxRand");
			ivjJCheckBoxRand.setToolTipText("Range: 1 to 65535");
			ivjJCheckBoxRand.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJCheckBoxRand.setText("Rand:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxRand;
}
/**
 * Return the JCheckBoxTa property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxTa() {
	if (ivjJCheckBoxTa == null) {
		try {
			ivjJCheckBoxTa = new javax.swing.JCheckBox();
			ivjJCheckBoxTa.setName("JCheckBoxTa");
			ivjJCheckBoxTa.setToolTipText("Range: 1 to 65535");
			ivjJCheckBoxTa.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJCheckBoxTa.setText("Ta:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxTa;
}
/**
 * Return the JCheckBoxTb property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxTb() {
	if (ivjJCheckBoxTb == null) {
		try {
			ivjJCheckBoxTb = new javax.swing.JCheckBox();
			ivjJCheckBoxTb.setName("JCheckBoxTb");
			ivjJCheckBoxTb.setToolTipText("Range: 1 to 65535");
			ivjJCheckBoxTb.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJCheckBoxTb.setText("Tb:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxTb;
}
/**
 * Return the JCheckBoxTc property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxTc() {
	if (ivjJCheckBoxTc == null) {
		try {
			ivjJCheckBoxTc = new javax.swing.JCheckBox();
			ivjJCheckBoxTc.setName("JCheckBoxTc");
			ivjJCheckBoxTc.setToolTipText("Range: 1 to 65535");
			ivjJCheckBoxTc.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJCheckBoxTc.setText("Tc:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxTc;
}
/**
 * Return the JCheckBoxTd property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxTd() {
	if (ivjJCheckBoxTd == null) {
		try {
			ivjJCheckBoxTd = new javax.swing.JCheckBox();
			ivjJCheckBoxTd.setName("JCheckBoxTd");
			ivjJCheckBoxTd.setToolTipText("Range: 1 to 65535");
			ivjJCheckBoxTd.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJCheckBoxTd.setText("Td:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxTd;
}
/**
 * Return the JCheckBoxTe property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxTe() {
	if (ivjJCheckBoxTe == null) {
		try {
			ivjJCheckBoxTe = new javax.swing.JCheckBox();
			ivjJCheckBoxTe.setName("JCheckBoxTe");
			ivjJCheckBoxTe.setToolTipText("Range: 1 to 65535");
			ivjJCheckBoxTe.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJCheckBoxTe.setText("Te:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxTe;
}
/**
 * Return the JCheckBoxTf property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxTf() {
	if (ivjJCheckBoxTf == null) {
		try {
			ivjJCheckBoxTf = new javax.swing.JCheckBox();
			ivjJCheckBoxTf.setName("JCheckBoxTf");
			ivjJCheckBoxTf.setToolTipText("Range: 1 to 65535");
			ivjJCheckBoxTf.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJCheckBoxTf.setText("Tf:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxTf;
}
/**
 * Return the JPanelData property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelData() {
	if (ivjJPanelData == null) {
		try {
			ivjJPanelData = new javax.swing.JPanel();
			ivjJPanelData.setName("JPanelData");
			ivjJPanelData.setBorder(getJPanelDataTitleBorder());
			ivjJPanelData.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJCheckBoxHeatMode = new java.awt.GridBagConstraints();
			constraintsJCheckBoxHeatMode.gridx = 2; constraintsJCheckBoxHeatMode.gridy = 1;
			constraintsJCheckBoxHeatMode.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxHeatMode.ipady = -4;
			constraintsJCheckBoxHeatMode.insets = new java.awt.Insets(0, 24, 2, 3);
			getJPanelData().add(getJCheckBoxHeatMode(), constraintsJCheckBoxHeatMode);

			java.awt.GridBagConstraints constraintsJCheckBoxCoolMode = new java.awt.GridBagConstraints();
			constraintsJCheckBoxCoolMode.gridx = 3; constraintsJCheckBoxCoolMode.gridy = 1;
			constraintsJCheckBoxCoolMode.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxCoolMode.ipady = -4;
			constraintsJCheckBoxCoolMode.insets = new java.awt.Insets(0, 3, 2, 7);
			getJPanelData().add(getJCheckBoxCoolMode(), constraintsJCheckBoxCoolMode);

			java.awt.GridBagConstraints constraintsJButtonDeltasAbsolute = new java.awt.GridBagConstraints();
			constraintsJButtonDeltasAbsolute.gridx = 1; constraintsJButtonDeltasAbsolute.gridy = 1;
constraintsJButtonDeltasAbsolute.gridheight = 2;
			constraintsJButtonDeltasAbsolute.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJButtonDeltasAbsolute.insets = new java.awt.Insets(0, 9, 3, 47);
			getJPanelData().add(getJButtonDeltasAbsolute(), constraintsJButtonDeltasAbsolute);

			java.awt.GridBagConstraints constraintsJButtonFahrenheitCelsius = new java.awt.GridBagConstraints();
			constraintsJButtonFahrenheitCelsius.gridx = 1; constraintsJButtonFahrenheitCelsius.gridy = 3;
			constraintsJButtonFahrenheitCelsius.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJButtonFahrenheitCelsius.insets = new java.awt.Insets(3, 9, 17, 47);
			getJPanelData().add(getJButtonFahrenheitCelsius(), constraintsJButtonFahrenheitCelsius);

			java.awt.GridBagConstraints constraintsJPanelDataChanges = new java.awt.GridBagConstraints();
			constraintsJPanelDataChanges.gridx = 1; constraintsJPanelDataChanges.gridy = 4;
			constraintsJPanelDataChanges.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJPanelDataChanges.weightx = 1.0;
			constraintsJPanelDataChanges.weighty = 1.0;
			constraintsJPanelDataChanges.insets = new java.awt.Insets(0, 8, 10, 24);
			getJPanelData().add(getJPanelDataChanges(), constraintsJPanelDataChanges);

			java.awt.GridBagConstraints constraintsJPanelTable = new java.awt.GridBagConstraints();
			constraintsJPanelTable.gridx = 2; constraintsJPanelTable.gridy = 2;
			constraintsJPanelTable.gridwidth = 2;
constraintsJPanelTable.gridheight = 3;
			constraintsJPanelTable.fill = java.awt.GridBagConstraints.VERTICAL;
			constraintsJPanelTable.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJPanelTable.weightx = 1.0;
			constraintsJPanelTable.weighty = 1.0;
			constraintsJPanelTable.ipadx = 29;
			constraintsJPanelTable.insets = new java.awt.Insets(3, 24, 2, 8);
			getJPanelData().add(getJPanelTable(), constraintsJPanelTable);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelData;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelDataChanges() {
	if (ivjJPanelDataChanges == null) {
		try {
			ivjJPanelDataChanges = new javax.swing.JPanel();
			ivjJPanelDataChanges.setName("JPanelDataChanges");
			ivjJPanelDataChanges.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJCheckBoxDeltaB = new java.awt.GridBagConstraints();
			constraintsJCheckBoxDeltaB.gridx = 1; constraintsJCheckBoxDeltaB.gridy = 3;
			constraintsJCheckBoxDeltaB.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxDeltaB.ipadx = -3;
			constraintsJCheckBoxDeltaB.ipady = -4;
			constraintsJCheckBoxDeltaB.insets = new java.awt.Insets(0, 0, 1, 1);
			getJPanelDataChanges().add(getJCheckBoxDeltaB(), constraintsJCheckBoxDeltaB);

			java.awt.GridBagConstraints constraintsJCheckBoxDeltaD = new java.awt.GridBagConstraints();
			constraintsJCheckBoxDeltaD.gridx = 1; constraintsJCheckBoxDeltaD.gridy = 4;
			constraintsJCheckBoxDeltaD.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxDeltaD.ipadx = -3;
			constraintsJCheckBoxDeltaD.ipady = -4;
			constraintsJCheckBoxDeltaD.insets = new java.awt.Insets(1, 0, 1, 1);
			getJPanelDataChanges().add(getJCheckBoxDeltaD(), constraintsJCheckBoxDeltaD);

			java.awt.GridBagConstraints constraintsJCheckBoxDeltaF = new java.awt.GridBagConstraints();
			constraintsJCheckBoxDeltaF.gridx = 1; constraintsJCheckBoxDeltaF.gridy = 5;
			constraintsJCheckBoxDeltaF.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxDeltaF.ipadx = -2;
			constraintsJCheckBoxDeltaF.ipady = -4;
			constraintsJCheckBoxDeltaF.insets = new java.awt.Insets(1, 0, 1, 1);
			getJPanelDataChanges().add(getJCheckBoxDeltaF(), constraintsJCheckBoxDeltaF);

			java.awt.GridBagConstraints constraintsJTextFieldDeltaF = new java.awt.GridBagConstraints();
			constraintsJTextFieldDeltaF.gridx = 2; constraintsJTextFieldDeltaF.gridy = 5;
			constraintsJTextFieldDeltaF.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldDeltaF.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldDeltaF.weightx = 1.0;
			constraintsJTextFieldDeltaF.ipadx = 55;
			constraintsJTextFieldDeltaF.ipady = -1;
			constraintsJTextFieldDeltaF.insets = new java.awt.Insets(1, 1, 1, 2);
			getJPanelDataChanges().add(getJTextFieldDeltaF(), constraintsJTextFieldDeltaF);

			java.awt.GridBagConstraints constraintsJTextFieldDeltaD = new java.awt.GridBagConstraints();
			constraintsJTextFieldDeltaD.gridx = 2; constraintsJTextFieldDeltaD.gridy = 4;
			constraintsJTextFieldDeltaD.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldDeltaD.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldDeltaD.weightx = 1.0;
			constraintsJTextFieldDeltaD.ipadx = 55;
			constraintsJTextFieldDeltaD.ipady = -1;
			constraintsJTextFieldDeltaD.insets = new java.awt.Insets(1, 1, 1, 2);
			getJPanelDataChanges().add(getJTextFieldDeltaD(), constraintsJTextFieldDeltaD);

			java.awt.GridBagConstraints constraintsJTextFieldDeltaB = new java.awt.GridBagConstraints();
			constraintsJTextFieldDeltaB.gridx = 2; constraintsJTextFieldDeltaB.gridy = 3;
			constraintsJTextFieldDeltaB.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldDeltaB.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldDeltaB.weightx = 1.0;
			constraintsJTextFieldDeltaB.ipadx = 55;
			constraintsJTextFieldDeltaB.ipady = -1;
			constraintsJTextFieldDeltaB.insets = new java.awt.Insets(0, 1, 1, 2);
			getJPanelDataChanges().add(getJTextFieldDeltaB(), constraintsJTextFieldDeltaB);

			java.awt.GridBagConstraints constraintsJCheckBoxMin = new java.awt.GridBagConstraints();
			constraintsJCheckBoxMin.gridx = 1; constraintsJCheckBoxMin.gridy = 1;
			constraintsJCheckBoxMin.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxMin.ipadx = 14;
			constraintsJCheckBoxMin.ipady = -4;
			constraintsJCheckBoxMin.insets = new java.awt.Insets(1, 0, 1, 1);
			getJPanelDataChanges().add(getJCheckBoxMin(), constraintsJCheckBoxMin);

			java.awt.GridBagConstraints constraintsJCheckBoxMax = new java.awt.GridBagConstraints();
			constraintsJCheckBoxMax.gridx = 1; constraintsJCheckBoxMax.gridy = 2;
			constraintsJCheckBoxMax.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxMax.ipadx = 11;
			constraintsJCheckBoxMax.ipady = -4;
			constraintsJCheckBoxMax.insets = new java.awt.Insets(1, 0, 0, 1);
			getJPanelDataChanges().add(getJCheckBoxMax(), constraintsJCheckBoxMax);

			java.awt.GridBagConstraints constraintsJCheckBoxRand = new java.awt.GridBagConstraints();
			constraintsJCheckBoxRand.gridx = 1; constraintsJCheckBoxRand.gridy = 6;
			constraintsJCheckBoxRand.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxRand.ipadx = 7;
			constraintsJCheckBoxRand.ipady = -4;
			constraintsJCheckBoxRand.insets = new java.awt.Insets(1, 0, 4, 1);
			getJPanelDataChanges().add(getJCheckBoxRand(), constraintsJCheckBoxRand);

			java.awt.GridBagConstraints constraintsJTextFieldRand = new java.awt.GridBagConstraints();
			constraintsJTextFieldRand.gridx = 2; constraintsJTextFieldRand.gridy = 6;
			constraintsJTextFieldRand.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldRand.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldRand.weightx = 1.0;
			constraintsJTextFieldRand.ipadx = 55;
			constraintsJTextFieldRand.ipady = -1;
			constraintsJTextFieldRand.insets = new java.awt.Insets(1, 1, 4, 2);
			getJPanelDataChanges().add(getJTextFieldRand(), constraintsJTextFieldRand);

			java.awt.GridBagConstraints constraintsJTextFieldMax = new java.awt.GridBagConstraints();
			constraintsJTextFieldMax.gridx = 2; constraintsJTextFieldMax.gridy = 2;
			constraintsJTextFieldMax.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldMax.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldMax.weightx = 1.0;
			constraintsJTextFieldMax.ipadx = 55;
			constraintsJTextFieldMax.ipady = -1;
			constraintsJTextFieldMax.insets = new java.awt.Insets(1, 1, 0, 2);
			getJPanelDataChanges().add(getJTextFieldMax(), constraintsJTextFieldMax);

			java.awt.GridBagConstraints constraintsJTextFieldMin = new java.awt.GridBagConstraints();
			constraintsJTextFieldMin.gridx = 2; constraintsJTextFieldMin.gridy = 1;
			constraintsJTextFieldMin.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldMin.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldMin.weightx = 1.0;
			constraintsJTextFieldMin.ipadx = 55;
			constraintsJTextFieldMin.ipady = -1;
			constraintsJTextFieldMin.insets = new java.awt.Insets(1, 1, 1, 2);
			getJPanelDataChanges().add(getJTextFieldMin(), constraintsJTextFieldMin);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelDataChanges;
}
/**
 * Return the JPanelDataTitleBorder property value.
 * @return com.cannontech.common.gui.util.TitleBorder
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.TitleBorder getJPanelDataTitleBorder() {
	com.cannontech.common.gui.util.TitleBorder ivjJPanelDataTitleBorder = null;
	try {
		/* Create part */
		ivjJPanelDataTitleBorder = new com.cannontech.common.gui.util.TitleBorder();
		ivjJPanelDataTitleBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
		ivjJPanelDataTitleBorder.setTitle("Stat Data");
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjJPanelDataTitleBorder;
}
/**
 * Return the JPanelTable property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelTable() {
	if (ivjJPanelTable == null) {
		try {
			ivjJPanelTable = new javax.swing.JPanel();
			ivjJPanelTable.setName("JPanelTable");
			ivjJPanelTable.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJScrollPaneJTable = new java.awt.GridBagConstraints();
			constraintsJScrollPaneJTable.gridx = 1; constraintsJScrollPaneJTable.gridy = 1;
			constraintsJScrollPaneJTable.gridwidth = 2;
			constraintsJScrollPaneJTable.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPaneJTable.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJScrollPaneJTable.weightx = 1.0;
			constraintsJScrollPaneJTable.weighty = 1.0;
			constraintsJScrollPaneJTable.ipadx = -3;
			constraintsJScrollPaneJTable.ipady = -5;
			constraintsJScrollPaneJTable.insets = new java.awt.Insets(1, 1, 3, 2);
			getJPanelTable().add(getJScrollPaneJTable(), constraintsJScrollPaneJTable);

			java.awt.GridBagConstraints constraintsJCheckBoxTc = new java.awt.GridBagConstraints();
			constraintsJCheckBoxTc.gridx = 1; constraintsJCheckBoxTc.gridy = 4;
			constraintsJCheckBoxTc.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxTc.ipady = -4;
			constraintsJCheckBoxTc.insets = new java.awt.Insets(0, 0, 1, 0);
			getJPanelTable().add(getJCheckBoxTc(), constraintsJCheckBoxTc);

			java.awt.GridBagConstraints constraintsJCheckBoxTd = new java.awt.GridBagConstraints();
			constraintsJCheckBoxTd.gridx = 1; constraintsJCheckBoxTd.gridy = 5;
			constraintsJCheckBoxTd.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxTd.ipady = -4;
			constraintsJCheckBoxTd.insets = new java.awt.Insets(1, 0, 1, 0);
			getJPanelTable().add(getJCheckBoxTd(), constraintsJCheckBoxTd);

			java.awt.GridBagConstraints constraintsJCheckBoxTe = new java.awt.GridBagConstraints();
			constraintsJCheckBoxTe.gridx = 1; constraintsJCheckBoxTe.gridy = 6;
			constraintsJCheckBoxTe.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxTe.ipady = -4;
			constraintsJCheckBoxTe.insets = new java.awt.Insets(1, 0, 1, 0);
			getJPanelTable().add(getJCheckBoxTe(), constraintsJCheckBoxTe);

			java.awt.GridBagConstraints constraintsJTextFieldTe = new java.awt.GridBagConstraints();
			constraintsJTextFieldTe.gridx = 2; constraintsJTextFieldTe.gridy = 6;
			constraintsJTextFieldTe.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldTe.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldTe.weightx = 1.0;
			constraintsJTextFieldTe.ipadx = 55;
			constraintsJTextFieldTe.ipady = -1;
			constraintsJTextFieldTe.insets = new java.awt.Insets(1, 1, 1, 31);
			getJPanelTable().add(getJTextFieldTe(), constraintsJTextFieldTe);

			java.awt.GridBagConstraints constraintsJTextFieldTd = new java.awt.GridBagConstraints();
			constraintsJTextFieldTd.gridx = 2; constraintsJTextFieldTd.gridy = 5;
			constraintsJTextFieldTd.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldTd.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldTd.weightx = 1.0;
			constraintsJTextFieldTd.ipadx = 55;
			constraintsJTextFieldTd.ipady = -1;
			constraintsJTextFieldTd.insets = new java.awt.Insets(1, 1, 1, 31);
			getJPanelTable().add(getJTextFieldTd(), constraintsJTextFieldTd);

			java.awt.GridBagConstraints constraintsJTextFieldTc = new java.awt.GridBagConstraints();
			constraintsJTextFieldTc.gridx = 2; constraintsJTextFieldTc.gridy = 4;
			constraintsJTextFieldTc.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldTc.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldTc.weightx = 1.0;
			constraintsJTextFieldTc.ipadx = 55;
			constraintsJTextFieldTc.ipady = -1;
			constraintsJTextFieldTc.insets = new java.awt.Insets(0, 1, 1, 31);
			getJPanelTable().add(getJTextFieldTc(), constraintsJTextFieldTc);

			java.awt.GridBagConstraints constraintsJCheckBoxTa = new java.awt.GridBagConstraints();
			constraintsJCheckBoxTa.gridx = 1; constraintsJCheckBoxTa.gridy = 2;
			constraintsJCheckBoxTa.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxTa.ipady = -4;
			constraintsJCheckBoxTa.insets = new java.awt.Insets(4, 0, 1, 0);
			getJPanelTable().add(getJCheckBoxTa(), constraintsJCheckBoxTa);

			java.awt.GridBagConstraints constraintsJCheckBoxTb = new java.awt.GridBagConstraints();
			constraintsJCheckBoxTb.gridx = 1; constraintsJCheckBoxTb.gridy = 3;
			constraintsJCheckBoxTb.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxTb.ipady = -4;
			constraintsJCheckBoxTb.insets = new java.awt.Insets(1, 0, 0, 0);
			getJPanelTable().add(getJCheckBoxTb(), constraintsJCheckBoxTb);

			java.awt.GridBagConstraints constraintsJCheckBoxTf = new java.awt.GridBagConstraints();
			constraintsJCheckBoxTf.gridx = 1; constraintsJCheckBoxTf.gridy = 7;
			constraintsJCheckBoxTf.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxTf.ipadx = 3;
			constraintsJCheckBoxTf.ipady = -4;
			constraintsJCheckBoxTf.insets = new java.awt.Insets(1, 0, 1, 0);
			getJPanelTable().add(getJCheckBoxTf(), constraintsJCheckBoxTf);

			java.awt.GridBagConstraints constraintsJTextFieldTf = new java.awt.GridBagConstraints();
			constraintsJTextFieldTf.gridx = 2; constraintsJTextFieldTf.gridy = 7;
			constraintsJTextFieldTf.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldTf.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldTf.weightx = 1.0;
			constraintsJTextFieldTf.ipadx = 55;
			constraintsJTextFieldTf.ipady = -1;
			constraintsJTextFieldTf.insets = new java.awt.Insets(1, 1, 1, 31);
			getJPanelTable().add(getJTextFieldTf(), constraintsJTextFieldTf);

			java.awt.GridBagConstraints constraintsJTextFieldTb = new java.awt.GridBagConstraints();
			constraintsJTextFieldTb.gridx = 2; constraintsJTextFieldTb.gridy = 3;
			constraintsJTextFieldTb.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldTb.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldTb.weightx = 1.0;
			constraintsJTextFieldTb.ipadx = 55;
			constraintsJTextFieldTb.ipady = -1;
			constraintsJTextFieldTb.insets = new java.awt.Insets(1, 1, 0, 31);
			getJPanelTable().add(getJTextFieldTb(), constraintsJTextFieldTb);

			java.awt.GridBagConstraints constraintsJTextFieldTa = new java.awt.GridBagConstraints();
			constraintsJTextFieldTa.gridx = 2; constraintsJTextFieldTa.gridy = 2;
			constraintsJTextFieldTa.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldTa.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldTa.weightx = 1.0;
			constraintsJTextFieldTa.ipadx = 55;
			constraintsJTextFieldTa.ipady = -1;
			constraintsJTextFieldTa.insets = new java.awt.Insets(4, 1, 1, 31);
			getJPanelTable().add(getJTextFieldTa(), constraintsJTextFieldTa);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelTable;
}
/**
 * Return the JScrollPaneJTable property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneJTable() {
	if (ivjJScrollPaneJTable == null) {
		try {
			ivjJScrollPaneJTable = new javax.swing.JScrollPane();
			ivjJScrollPaneJTable.setName("JScrollPaneJTable");
			ivjJScrollPaneJTable.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			ivjJScrollPaneJTable.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			ivjJScrollPaneJTable.setPreferredSize(new java.awt.Dimension(130, 70));
			ivjJScrollPaneJTable.setMinimumSize(new java.awt.Dimension(130, 70));
			getJScrollPaneJTable().setViewportView(getJTableCurve());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneJTable;
}
/**
 * Return the JTableCurve property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getJTableCurve() {
	if (ivjJTableCurve == null) {
		try {
			ivjJTableCurve = new javax.swing.JTable();
			ivjJTableCurve.setName("JTableCurve");
			getJScrollPaneJTable().setColumnHeaderView(ivjJTableCurve.getTableHeader());
			getJScrollPaneJTable().getViewport().setBackingStoreEnabled(true);
			ivjJTableCurve.setColumnSelectionAllowed(false);
			ivjJTableCurve.setGridColor(java.awt.Color.yellow);
			ivjJTableCurve.setShowHorizontalLines(false);
			ivjJTableCurve.setIntercellSpacing(new java.awt.Dimension(1, 0));
			ivjJTableCurve.setBounds(0, 0, 200, 200);
			ivjJTableCurve.setRowSelectionAllowed(false);
			// user code begin {1}


			ivjJTableCurve.setIntercellSpacing(new java.awt.Dimension(0, 0));
			ivjJTableCurve.setShowVerticalLines(false);

			String[] cols = { "Ta", "Tb", "Tc", "Td","X", "Te", "Tf" };
			javax.swing.JLabel c1 = new javax.swing.JLabel("Ta");
			javax.swing.JLabel c2 = new javax.swing.JLabel("Tb");
			javax.swing.JLabel c3 = new javax.swing.JLabel("Tc");
			javax.swing.JLabel c4 = new javax.swing.JLabel("T");
			javax.swing.JLabel c5 = new javax.swing.JLabel("d");
			javax.swing.JLabel c6 = new javax.swing.JLabel("Te");
			javax.swing.JLabel c7 = new javax.swing.JLabel("Tf");


			c1.setHorizontalAlignment( javax.swing.SwingConstants.CENTER );
			c2.setHorizontalAlignment( javax.swing.SwingConstants.CENTER );
			c3.setHorizontalAlignment( javax.swing.SwingConstants.CENTER );
			c4.setHorizontalAlignment( javax.swing.SwingConstants.RIGHT );
			c5.setHorizontalAlignment( javax.swing.SwingConstants.LEFT );
			c6.setHorizontalAlignment( javax.swing.SwingConstants.CENTER );
			c7.setHorizontalAlignment( javax.swing.SwingConstants.CENTER );
			
			
			final LineLabel blank = new LineLabel( LineLabel.NO_LINE );
			final LineLabel fSlash = new LineLabel( LineLabel.BOT_LEFT_UP_RIGHT );
			final LineLabel bkSlash = new LineLabel( LineLabel.UP_LEFT_BOT_RIGHT );
			final LineLabel bottom = new LineLabel( LineLabel.BOTTOM );
			Object[][] vals = 
			{
				//the columns and the third row must be a unique instances of their object for rendering reasons!!
				{c1, c2, c3, c4, c5, c6, c7 },
				{blank,  blank,   blank,  new LineLabel(LineLabel.NO_LINE), blank, bottom, blank },
				{bottom, blank,   blank,  new LineLabel(LineLabel.NO_LINE), fSlash, blank, bkSlash},
				{blank,  bkSlash, bottom, new LineLabel(LineLabel.BOT_LEFT_UP_RIGHT), blank, blank, blank },
				{blank,  blank,   blank,  new LineLabel(LineLabel.NO_LINE), blank, blank, blank },
				{blank,  blank,   blank,  new LineLabel(LineLabel.NO_LINE), blank, blank, blank },
			};


			ivjJTableCurve.setModel( new javax.swing.table.DefaultTableModel()
			{
				public boolean isCellEditable( int r, int c )
				{ return false; };
				
			});
			((javax.swing.table.DefaultTableModel)ivjJTableCurve.getModel()).setDataVector(  vals, cols  );


			javax.swing.table.JTableHeader th = new javax.swing.table.JTableHeader();
			ivjJTableCurve.setTableHeader( th );

			ivjJTableCurve.setDefaultRenderer( Object.class, new ExpressComCellRenderer() );
			ivjJTableCurve.getColumnModel().getColumn(0).setPreferredWidth(10);
			ivjJTableCurve.getColumnModel().getColumn(1).setPreferredWidth(10);
			ivjJTableCurve.getColumnModel().getColumn(2).setPreferredWidth(10);
			ivjJTableCurve.getColumnModel().getColumn(3).setPreferredWidth(23);
			ivjJTableCurve.getColumnModel().getColumn(4).setPreferredWidth(23);
			ivjJTableCurve.getColumnModel().getColumn(5).setPreferredWidth(10);
			ivjJTableCurve.getColumnModel().getColumn(6).setPreferredWidth(14);

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTableCurve;
}
/**
 * Return the JTextFieldDelatB property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldDeltaB() {
	if (ivjJTextFieldDeltaB == null) {
		try {
			ivjJTextFieldDeltaB = new javax.swing.JTextField();
			ivjJTextFieldDeltaB.setName("JTextFieldDeltaB");
			ivjJTextFieldDeltaB.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJTextFieldDeltaB.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldDeltaB;
}
/**
 * Return the JTextFieldDeltaD property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldDeltaD() {
	if (ivjJTextFieldDeltaD == null) {
		try {
			ivjJTextFieldDeltaD = new javax.swing.JTextField();
			ivjJTextFieldDeltaD.setName("JTextFieldDeltaD");
			ivjJTextFieldDeltaD.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJTextFieldDeltaD.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldDeltaD;
}
/**
 * Return the JTextFieldDeltaF property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldDeltaF() {
	if (ivjJTextFieldDeltaF == null) {
		try {
			ivjJTextFieldDeltaF = new javax.swing.JTextField();
			ivjJTextFieldDeltaF.setName("JTextFieldDeltaF");
			ivjJTextFieldDeltaF.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJTextFieldDeltaF.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldDeltaF;
}
/**
 * Return the JTextFieldMax property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldMax() {
	if (ivjJTextFieldMax == null) {
		try {
			ivjJTextFieldMax = new javax.swing.JTextField();
			ivjJTextFieldMax.setName("JTextFieldMax");
			ivjJTextFieldMax.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJTextFieldMax.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldMax;
}
/**
 * Return the JTextFieldMin5 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldMin() {
	if (ivjJTextFieldMin == null) {
		try {
			ivjJTextFieldMin = new javax.swing.JTextField();
			ivjJTextFieldMin.setName("JTextFieldMin");
			ivjJTextFieldMin.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJTextFieldMin.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldMin;
}
/**
 * Return the JTextFieldRand property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldRand() {
	if (ivjJTextFieldRand == null) {
		try {
			ivjJTextFieldRand = new javax.swing.JTextField();
			ivjJTextFieldRand.setName("JTextFieldRand");
			ivjJTextFieldRand.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJTextFieldRand.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldRand;
}
/**
 * Return the JTextFieldTa property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldTa() {
	if (ivjJTextFieldTa == null) {
		try {
			ivjJTextFieldTa = new javax.swing.JTextField();
			ivjJTextFieldTa.setName("JTextFieldTa");
			ivjJTextFieldTa.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJTextFieldTa.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldTa;
}
/**
 * Return the JTextFieldTb property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldTb() {
	if (ivjJTextFieldTb == null) {
		try {
			ivjJTextFieldTb = new javax.swing.JTextField();
			ivjJTextFieldTb.setName("JTextFieldTb");
			ivjJTextFieldTb.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJTextFieldTb.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldTb;
}
/**
 * Return the JTextFieldTc property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldTc() {
	if (ivjJTextFieldTc == null) {
		try {
			ivjJTextFieldTc = new javax.swing.JTextField();
			ivjJTextFieldTc.setName("JTextFieldTc");
			ivjJTextFieldTc.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJTextFieldTc.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldTc;
}
/**
 * Return the JTextFieldTd property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldTd() {
	if (ivjJTextFieldTd == null) {
		try {
			ivjJTextFieldTd = new javax.swing.JTextField();
			ivjJTextFieldTd.setName("JTextFieldTd");
			ivjJTextFieldTd.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJTextFieldTd.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldTd;
}
/**
 * Return the JTextFieldTe property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldTe() {
	if (ivjJTextFieldTe == null) {
		try {
			ivjJTextFieldTe = new javax.swing.JTextField();
			ivjJTextFieldTe.setName("JTextFieldTe");
			ivjJTextFieldTe.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJTextFieldTe.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldTe;
}
/**
 * Return the JTextFieldTf property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldTf() {
	if (ivjJTextFieldTf == null) {
		try {
			ivjJTextFieldTf = new javax.swing.JTextField();
			ivjJTextFieldTf.setName("JTextFieldTf");
			ivjJTextFieldTf.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJTextFieldTf.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldTf;
}
/**
 * getValue method comment.
 */
public Object getValue(Object o) 
{
/*	com.cannontech.database.data.device.lm.LMGroupVersacom group = null;
	
	if( o instanceof com.cannontech.database.data.multi.MultiDBPersistent )
	{
		group = (com.cannontech.database.data.device.lm.LMGroupVersacom)
					com.cannontech.database.data.multi.MultiDBPersistent.getFirstObjectOfType(
								com.cannontech.database.data.device.lm.LMGroupVersacom.class,
								(com.cannontech.database.data.multi.MultiDBPersistent)o );
	}	
	else if( o instanceof com.cannontech.database.data.multi.SmartMultiDBPersistent )
		group = (com.cannontech.database.data.device.lm.LMGroupRipple)
					((com.cannontech.database.data.multi.SmartMultiDBPersistent)o).getOwnerDBPersistent();
	
	if( o instanceof com.cannontech.database.data.device.lm.LMGroupVersacom || group != null )
	{
		if( group == null )
			group = (com.cannontech.database.data.device.lm.LMGroupVersacom) o;
		
		Integer utilityAddr = null;
		Object utilityAddressSpinVal = getUtilityAddressSpinner().getValue();
		if( utilityAddressSpinVal instanceof Long )
			utilityAddr = new Integer( ((Long)utilityAddressSpinVal).intValue() );
		else if( utilityAddressSpinVal instanceof Integer )
			utilityAddr = new Integer( ((Integer)utilityAddressSpinVal).intValue() );

		Integer sectionAddr = null;
		Object sectionAddressSpinVal = getSectionAddressSpinner().getValue();
		if( sectionAddressSpinVal instanceof Long )
			sectionAddr = new Integer( ((Long)sectionAddressSpinVal).intValue() );
		else if( sectionAddressSpinVal instanceof Integer )
			sectionAddr = new Integer( ((Integer)sectionAddressSpinVal).intValue() );
		
		Integer classAddr =  new Integer( getClassAddressSingleLineBitTogglePanel().getValue() );
		Integer divisionAddr = new Integer( getDivisionAddressSingleLineBitTogglePanel().getValue() );

		StringBuffer addressUsage = new StringBuffer();

		if( getUtilityAddressCheckBox().isSelected() )
			addressUsage.append('U');
		else
			addressUsage.append(' ');
		
		if( getSectionAddressCheckBox().isSelected() )
			addressUsage.append('S');
		else
			addressUsage.append(' ');
		
		if( getClassAddressCheckBox().isSelected() )
			addressUsage.append('C');
		else
			addressUsage.append(' ');
		
		if( getDivisionAddressCheckBox().isSelected() )
			addressUsage.append('D');			
		else
			addressUsage.append(' ');

		StringBuffer relayUsage = new StringBuffer();

		if( getRelay1CheckBox().isSelected() )
			relayUsage.append('1');
		else
			relayUsage.append(' ');
		
		if( getRelay2CheckBox().isSelected() )
			relayUsage.append('2');
		else
			relayUsage.append(' ');
		
		if( getRelay3CheckBox().isSelected() )
			relayUsage.append('3');
		else
			relayUsage.append(' ');

		if( getRelay4CheckBox().isSelected() )
			relayUsage.append('4');
		else
			relayUsage.append(' ');

		group.getLmGroupVersacom().setUtilityAddress(utilityAddr);
		group.getLmGroupVersacom().setSectionAddress(sectionAddr);
		group.getLmGroupVersacom().setClassAddress(classAddr);
		group.getLmGroupVersacom().setDivisionAddress(divisionAddr);
		
		group.getLmGroupVersacom().setAddressUsage(addressUsage.toString());
		group.getLmGroupVersacom().setRelayUsage(relayUsage.toString());

		if( getJCheckBoxSerialAddress().isSelected() )
			group.getLmGroupVersacom().setSerialAddress( getJTextFieldSerialAddress().getText() );
		else
			group.getLmGroupVersacom().setSerialAddress("0");
		
	}
*/
	return o;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}
/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}

	// user code end
	getJButtonFahrenheitCelsius().addActionListener(this);
	getJButtonDeltasAbsolute().addActionListener(this);
	getJTableCurve().addMouseListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("LMGroupExpressComEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(357, 389);

		java.awt.GridBagConstraints constraintsJPanelData = new java.awt.GridBagConstraints();
		constraintsJPanelData.gridx = 1; constraintsJPanelData.gridy = 1;
		constraintsJPanelData.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelData.weightx = 1.0;
		constraintsJPanelData.weighty = 1.0;
		constraintsJPanelData.ipadx = -10;
		constraintsJPanelData.ipady = 18;
		constraintsJPanelData.insets = new java.awt.Insets(6, 5, 131, 6);
		add(getJPanelData(), constraintsJPanelData);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * This method must be implemented if a notion of data validity needs to be supported.
 * @return boolean
 */
public boolean isInputValid() 
{
/*	if( getJCheckBoxSerialAddress().isSelected() )
		if( getJTextFieldSerialAddress().getText() == null 
			 || getJTextFieldSerialAddress().getText().length() <= 0 )
		{
			setErrorString("A value for the Serial Address text field must be filled in");
			return false;
		}

	String idRange = com.cannontech.common.util.CtiProperties.getInstance().getProperty(
		com.cannontech.common.util.CtiProperties.KEY_UTILITYID_RANGE, "1-" +
		com.cannontech.common.util.CtiUtilities.MAX_UTILITY_ID );

	int res = java.util.Arrays.binarySearch( 
				com.cannontech.common.util.CtiUtilities.decodeUtilityIDString( idRange ),
				((Number)getUtilityAddressSpinner().getValue()).intValue() );

	if( res < 0 )
	{
		setErrorString("An invalid Utility ID was entered, the valid Utility ID range is: " + idRange );
		return false;
	}
*/
	return true;
}
/**
 * Comment
 */
public void jButtonDeltasAbsolute_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( getJButtonDeltasAbsolute().getText().equalsIgnoreCase("Deltas") )
	{
		getJButtonDeltasAbsolute().setText("Absolutes");
		getJCheckBoxDeltaB().setText("Abs B:");
		getJCheckBoxDeltaD().setText("Abs D:");
		getJCheckBoxDeltaF().setText("Abs F:");
	}
	else
	{
		getJButtonDeltasAbsolute().setText("Deltas");
		getJCheckBoxDeltaB().setText("Delta B:");
		getJCheckBoxDeltaD().setText("Delta D:");
		getJCheckBoxDeltaF().setText("Delta F:");
	}

	return;
}
/**
 * Comment
 */
public void jButtonFahrenheitCelsius_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( getJButtonFahrenheitCelsius().getText().equalsIgnoreCase("Fahrenheit") )
	{
		getJButtonFahrenheitCelsius().setText("Celsius");
	}
	else
	{
		getJButtonFahrenheitCelsius().setText("Fahrenheit");
	}
		
	
	return;
}
/**
 * Comment
 */
public void jTableCurve_MouseClicked(java.awt.event.MouseEvent mouseEvent) 
{

	int col = getJTableCurve().columnAtPoint( mouseEvent.getPoint() );

	if( col == 0 )
		getJCheckBoxTa().setSelected( true );
	else if( col == 1 )
		getJCheckBoxTb().setSelected( true );
	else if( col == 2 )
		getJCheckBoxTc().setSelected( true );
	else if( col == 3 || col == 4 )
		getJCheckBoxTd().setSelected( true );		
	else if( col == 5 )
		getJCheckBoxTe().setSelected( true );
	else if( col == 6 )
		getJCheckBoxTf().setSelected( true );
	
	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		java.awt.Frame frame = new java.awt.Frame();
		LMGroupExpressComEditorPanel aGroupTypePanel;
		aGroupTypePanel = new LMGroupExpressComEditorPanel();
		frame.add("Center", aGroupTypePanel);
		frame.setSize(aGroupTypePanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main()");
		exception.printStackTrace(System.out);
	}
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mouseClicked(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJTableCurve()) 
		connEtoC4(e);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mouseEntered(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mouseExited(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mousePressed(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mouseReleased(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * setValue method comment.
 */
public void setValue(Object o) 
{
/*	if( o instanceof com.cannontech.database.data.device.lm.LMGroupVersacom )
	{
		com.cannontech.database.data.device.lm.LMGroupVersacom group = (com.cannontech.database.data.device.lm.LMGroupVersacom) o;

		Integer utilityAddr = group.getLmGroupVersacom().getUtilityAddress();
		Integer sectionAddr = group.getLmGroupVersacom().getSectionAddress();
		Integer classAddr = group.getLmGroupVersacom().getClassAddress();
		Integer divisionAddr = group.getLmGroupVersacom().getDivisionAddress();

		String addressUsage = group.getLmGroupVersacom().getAddressUsage();
		String relayUsage = group.getLmGroupVersacom().getRelayUsage();

		getUtilityAddressSpinner().setValue(utilityAddr);
		getSectionAddressSpinner().setValue(sectionAddr);
		getClassAddressSingleLineBitTogglePanel().setValue(classAddr.intValue());
		getDivisionAddressSingleLineBitTogglePanel().setValue(divisionAddr.intValue());

		getUtilityAddressCheckBox().setSelected(addressUsage.charAt(0) == 'U');
		getSectionAddressCheckBox().setSelected(addressUsage.charAt(1) == 'S');
		getClassAddressCheckBox().setSelected(addressUsage.charAt(2) == 'C');
		getDivisionAddressCheckBox().setSelected(addressUsage.charAt(3) == 'D');
		getRelay1CheckBox().setSelected(relayUsage.charAt(0) == '1');
		getRelay2CheckBox().setSelected(relayUsage.charAt(1) == '2');
		getRelay3CheckBox().setSelected(relayUsage.charAt(2) == '3');
		getRelay4CheckBox().setSelected(relayUsage.charAt(3) == '4');

		if( !group.getLmGroupVersacom().getSerialAddress().equalsIgnoreCase("0") )
		{
			getJCheckBoxSerialAddress().doClick();
			getJTextFieldSerialAddress().setText( group.getLmGroupVersacom().getSerialAddress() );
		}

	}
*/

}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GF4F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E15DFD8DDCD4D55EBFDBD6D6EC511315CF31ADA595DBD4D4D6D454324896959595EE5652C6AD7B48EE56DAD2D9DADD90919515528495F3D4D46AA144F7D4ACDE959015141012121199E4F4E046198B8C087A7F1DF36E6F5C1739830E64BE7A7763614E797E4E6F653C5E5FF939778A731684CCCDCE19A78849D385695F26E4C1B00C930407966F74C74C479C11A6A87EAD057019E04DCA052B9F50226AC625
	E58A976FCB07F8A04403D70CCA4B025FF3054B77CD7EA37C7020A796E8AB1B73727A57732A8975CC2371A756E440F5B604A04048B5892E1942EBEB32A5BCDA42B1A42788C2E2ABC973511A9C89AB02F89D04C088212DACFD8CDC27D34D97EDA91252B5DD95A0A4BD35AAE596112310092170B0632D172E86616E644907092E65EBB3A49EDF08C7C2A0191FA2781F2802EBF15D51435F36585A8BDA0C9D9D368E515C52D650ED69B5BACC85A6F3172545DCE0EDBF6C30F55A37EC295D35037C28F4599DE627F31FE8
	94ABCD96516628B3F6182D2EB6226B685AF4AADB90CAA01EEAA1582D82519F8671D88863B85E1ECC70AA785D8C41E541BCEFDBD5AC652971785DC253CB9DD366F848331F0565B554A267F9D867C75396507AD95CCA6AA189E859C7A25272A154C130C370033008360BDC7357F0ADBDA4165B5A5BED9D25F6FB19556458E5B3192D70FB4B9620C858DEF30B4DE19284D677DF2D4D5731273F4078B6F94D474AA4E89D3603603D378AF1776DCC1926D3A6F1C105A9C1C7D8D9049F114B046936FB549DFFC45DCE553A
	778BCA5D35975B26CC5551BDF274D7A9C9121C7423326E2223242E97523A4E035F49D27BE0F8AE457B94B8EB3341344D1038AF50469F453651B89BED29CD3DDE48DCD32BDB8E43A3CAD36A2D4C862BD5EB4B6BD467F0AB53B90D5B729645C394B83345C9F1920F82DAFEFBC49AEBDFAB5BC89F7603F8B004C888F190A6C3486E00F49D58465A4D2BCF330D55999DE2D92745EAB2BB84463B32F5BEDC4DAE51E12C6EF00A460E96F30351E1B19E321A1D0214E6F5AB316D100AEFBDED7BAD20E30739436CB00A96DB
	87C8B7361BC533434952ECEB7D0C24F118A9355E52EE86856DF68165D6364EA565E7F40ABF58CDC651AC27E074DD2D24CE3A0C8DA0918440EFD6AE7BAC243CEAA17FA688A2AF07A8AAEF27598123858CA6B65B51CEFBBD49099059C14A7992BAF68C055F819DD846FB0E90AC5D9611E630211CCFDBBFD74829303490730D0E9E6CE306E4B2CE2F37E11BF93B915BCC452CDB047C51B53AED060E31B656D6C41B366FFCCDE5923BD87CDE21311FBE3B5E265F1F3C9BAB1E6ED74714C1481F4D6B6A5123242E7C415E
	D088E3F899EFB9ECE0ED364C68B4E3AB5D630086E6B649A54E6C9C1B0CFDF52AE4BB43A7D0FCBC484C36C7241558316CFA129B95F9C01E4164453D6C7E6B2C6AE0E5D458C66ECB66AEF307D8E1361BBBCC7043A951B6351136A7755BD69B6759C6F1BB7473F268D8CE0E1737113EE894C50765D027E85EE796C5CB47E1193E136AF2F69E324BC3846A5A5BC672E76B06FE4E536FEFA3FD24455651E1261545D9A47A0F340F1A2D66F6B03A5C5659A1B27C2005347323669EE90CE83190C7284BE86DA4638D739BC2
	0CB3C99E9C16F6E882E44892226D247F5E38B6DBD1C6ED00917FA7G2EA36D58973B17E3DF4C6D3DC988BF38A2C56FDEB27C160A14F947D89F4ABBA677C5E6D37D91FD5BFF22E30504BB27D0AC261C0C51EA478506E1055F427A417BD66A0B002EA68862B16C87FFB9C268B856C89D205CE63532E26565BD6AE8A6299F7331CEB38C51E4D05B494B7029231FEBE88DD2F9B27A8BC7C9F9DA9563E1230314674A947491FD411EC0881147D81926BAD81930F47F626982A19E6610534529525DB8855365BBA352AAA0
	34B9D93AC0A7CB476AF75AC162A768790507BBAD851D22050C3AC6919A363DD3D478FE696B52A59909CE74713E59C84A55A0C62495C12893319DBCBF8BFD3CE1C7830478918BBD7A35EDA2334DD7145B0161C27471F6F6901D51C00B93314EF6D9157719D2275356E22137B8293CA7DB156D04E1496DB3605AEAF1B845EA6848AE2C1BE96D44DEF28F14E049A7581312A165F5BC684D6B0CC88B6FA478612EF98A5B1B819B8AA19882EB0F8F242CA57D1CB61E82F629B3BBDAED0EF633A96F08314BD8E06C968B68
	18D2D0CA49156477D7D8B747766C006B1132CED134F5ECB736B94C9DEDE60BD8EE36BAAD1D4E03259A09351EC5EE7D2AC1726FEE1EDBA2599BFD5775C2712939A9FA7E7650B90BD3523BD89D94F649F551F7EF5A14F13FCC1A464AE56E94AC17BEC8B3328B4BA5B3D52FDC46F561DCA42F21C24ED70559AA9A1D25071CB6AB0C05DEE60A59718437C368063687010DD1EFF5AB47A8BB6043A00492FAB756D1C16AB7F0ED37F5BA4D8574EF395552F2543DFEF6919AD60FD4BF7F7CECA349876D46651D0EAE7341DD
	CA89DA53E59124BA24FB572CF5DABFBD21DB76A7A6F63B7B964CFF3D0976358C1756473E0362B9AE2D0F3DEEA35E3773016667C21F6B959B1DB7C1BC33875AB60492884D90FCFA300FDD5F3ECE9840272D06B14351E1346ED35D3424FED557C17442FDC60A57F71072237799814B610794EDB9787728739E2A09B33E96B73EAFB571B88D9F2B3BB97B966038C72A49D5606C06FB7016ADB52CD5127EC15B509B9B15EDA82907792B593D6073C3E0E3610DDD6912BD953DB8661D582E9C7342662CCD1969E14C6B6B
	E57679774A7DAC1F4FEB079CA363F89C50A6C248BC8EFAA154C130C270BB0EF574370EED835591EDFA7536F2402A607ED32E7597F819AE18F2E6F5314A0DCFDB97A14755FC2C7CF6241272A37704286368B7BEF306B67CF5C09B727B745A431163597A6D0176EE5696EED735058A10E307B08CC2D09F360595AE4CCBF89F5E978F3AC85F4BBD9111D68C219E821B836552FB289562BE9C7BE8BDDD4F02F85889AC0B5BB692BFAD1E476F21F1B2263278660D0D927D5770F735016958CBF58CF69E62A930B67CF71B
	E79F7BDFB6FD9FFB3CCD5F47FE5F769943C106F2260D74CFEC7D7B60E9F6BD9FFCB1AD03A7674F21321DCE3313F6AA62A0C934316297020EAF3E9BA663D2794D95FCD326CBE9AAC5DB79DA5E86A7FC65218DAA7C2E051BF02C19F8825B57871D24FD951E0CC82B0250FC925B4508AEEDDFF0814D77A4EBA781A75D5B8B9BF3E6BA497DFBA450E3A0A4C290CE411C7C940EB57E76816F87152EFE6E87D79C73DC4F579C532F679B0E11BE6534F5BADA4C5A3A3C79D8B6274923DA35E8EEA7454E525CC179C5236330
	592DAD5CCB79994D937FA347BC3597D627F5D314F53A0E771B5AD46D3C3E844A317E94AB7B36D37AFD36CF877783ACD003B35D2929CA5D6BF9FBBADC39D83FBD6DEBF1402C0634AA816FF37B3A3577B9B23E04023E68D3B8363D311134BDF29FCBB805ED2F281B7A9BBE91E96BA1547AE05B7B5A254D374947BDCFAE40FCF5F05ABEA5FCA4DC23B4E9182F73A29DD763FC182F134C71B80AA7AAF0668319DD38B61E8434465F208F38F59D1977DA0DBD36CE916620971034FB57E1F904C13AD88813A1E4DE201CBB
	5D53CD4A2255E1EC07E25DE7B737882997925E4FB6A02F9D528F019088A192C24285287372F5E48E42FAB69DBE247632E413324F56023C668B653C0B97FA7287EDDDA4EFC3AFE4F91E457113948FD6602CACBE26E3860947G2DF8885EFF9E263ECC904443B8768945E2A09E4F317F6D467B5438A1B8AE4CF5FA6E578B1D7A7D5A60549F3FD7BA7B9F3F8B1C1E7AA3332FA39573D1FC915A7CA0456AA05E4C31840AD9A1BE04E32D1B89B69C62A3B976F5A666B70AE3019B8816FDF1C4DA614558C7AE6F46F9F145
	4558C7AE5AC0573DA1BE8CC2704558C746EE443A0E84EC8C04F1974B75ABDC9211B66F923DF69FCC7BDC5EA5DA5F7F810A97DD22757D3F6DC65F3F8AE84117207D3554D7499C8AFDF7A8E257D32C8A624D9C9BC14BC30478B00E0D27D89009F36C35AEAC23B19C93283C49904F3994737DB219DACB6D7D8D456F23EEE3462F6F478CB92BBAD8F9772E43F12690725CF6A94EFB57B493DF1E0F47E4EA38E54B8E0745D4E6BCDCCBC79F5606A228DFC87883AE45B6B4094A9C8371719052AF43F906EBA54EB302EF3D
	DE287559293B3692DF3FB645F099EBCB951749730CA018E7B01D1BB620CE9F2007DEC6474B8D4AF9BCE9BFB361CF81DC997DBA951D74C7B2B71669CF2A68445E27D31474C9AA7AD485BF4B5B53ABB0EF131F8E922C77EF7638EEA4DC4E7214ABDD6B9A168B3917CD1A465A60C66AB3B5814DFEB9364B76B2028D03F890474615E37B0D3E9C5BEAE797994BF3FCE19C0150844187C2A004C8DFCCB30426090378E4DF36A6C7566FF2FE4B6687AC4D142EE60D4DC4867E4E1164AA69453E27674FB7C93648ED7CCBB7
	DDB3B575B8DDC31FFA1A6D1B481F2510B3D738080E1F6D9DE6F084ADADF53FA56578F7BA8EBC8D216437F83F7B221B4CD9C438818C7BAD59EF3F3217D45BC41A3E2C3B229F1DD3A43DD324B086F8C8D8AE3505F13F156F73CC3FBF35E9AC51FF856AFFB6D52D7F5E63824F4324D4D2BE6DC617253D33FD1F2557AC4919BB155EAFC0C69317F3E2B3493F5DE1EEB5BB9CE693CD0DFA2F25E362D0C8BB8C824BDBE966C0F943B0D5A7EF61D748F9E37A9F59C0ED35F4E8ECFD11765FE8C8BF668AECF3FE5DDA7FBF75
	CA39CDE5DF694917781E2E9565DF49F4AF6278E10ADB95B89B6FAE2777B7928F865AD0BF6EB7D0FF51F495E0D7A156C07B22BF09F3ACE4935EEF47F0EC026CDFF06C0B0DF83FAD7EAF3EB7C75BD59D0F0751B8D96FE6710A6E410E77C4F67F77141F872D233FF9B52B779697599BEE31D9BB5BBB7619259D09D22B5556EDB6E17BBB6942B13C6D3F303D5E63A2FD7DB05CE44AEDD61B033755A7E95ABEC8B71427FDF8B3D9CBF736593A2BEC8ECB2F2DC3B4DAEBAD9DE62744933B996587F31EE9DB48DE0805AC7F
	35182D56FDF6E38B78E1AC3DE18BC99F8EE923A1B05F787DAE740D13GCB3FBAA2AD8FC29504362BD1E6160B586130F53BE51449DD4373B974EA2CE79F37F55453994BBE932EB8C33EA73C66E3FD681975F48F76EA6D1C65AF948F3ADABBE7C9DE0F3E44C820E58F43F55BEB7AC8B976C1FCB8049088D19092A124DE037EF82433D098E01EDEEF33570243EED53858ACFF459B4E6C6C4A1F2661BDFB6443B78B2197FF186CE95DB37B9A562F0A24EBE27DF20A337C2DBBC972578634A19002A004C30805B00E67AF
	C34CE779A3BBFC2522E8ECE9233BBC1839860B39DB0649840D352FDD1B48FC6F104DC1A794EC3EE75A046DD238964A99C29D8493043EEB31DD8EA9F31FA75ED006FC815732F9E2A8DC232FE573E7F68F18D2F13A77G929200F7A204AF24F846F591E9EFFD7A6927AB78D870E2AF7135EA4A5B4CADC74BEC2EAA33D1A4154A69BF7D2422175BECD695FD74A2A23BC6EF7F0527C9DF1AAD27F15BFBE2E926CC23FA789C363C4D58F158AC2F09BC7B75CCCEE7FB1DECFF68E1553ED976F52C2CCA2E437D233FA46B51
	6B2F43FD1D259B7456AA5A005EF79D5655C7DD643A4A3CCAEA17815D4A33F5C5B3B6696EC513B06CBA569E0324EB58A54BA5994F9DD04A483CFEE9CA32879971926F386BE4DFFB8935CB905285799F73C5C6505FCC476B9958FFF26E38C128F8694364659EF49806B3597661320E44A31327317C5F17CA736FDCA569D818A270FAB930192C09F7183B8DFC072A4282DD049C6C495B3CA15F30F643D75C36A9B25F124C5367FBBCD54E5FA7D3E599B5DD27A3035D6FEF23BEC09064AFECB87AGABFB50AF089F0EE3
	7F7E0DE44D2B703F214FC2E8036083E1B8049088D19062A1CC0410F9BD5CEFA014C0E83C9E795BA9BF599B34836687A184C28C047190B2EF0079BD04BA88F688FE90C2A0445C007CD7ED0851299F22C765C77C37E7BF22D032B7CA3233CDA7ED080479F81003F97224A3DE4A3FF77739A85D3CE5E9627D4D9F683C91429C297D6B1D4D41B905F6DE02015C93967523030CC3DF1CE63E50A61813ECA27968B2BBC4CB0B514A96C54B0C0EBA1B555252A3799A0554B77DDF8863EF403125FD9331270D7BC8DACE7431
	AE253C5FC148770776C33117A9E605384B9F6F8FB3DC27F3FF18AADD8301AF541F4D91C8BC9AFECF027B438790180EEF291FBC867084FF347906D5F4AC16772A25B23846C564369A2DCEB276337E7A8B9D22987D7A4D24DCF9DF25CEE4C10F1978E3BC4D1B1B5DE57CE63AACE3F497D97770506757649B8A3FE2EB90B95D6857E57E8E57A0EA3A35EB90D7A5AB57907A3A35EB9004DF265F552DDD03D07347F7EB57A0101F59DE25AA9F56A7DEA4430574BBCE71FB5A7A333FFEE5B82BBA18DFF5E31F67F55CDB7A
	7457F16F6853DF473D272F7FFD36077ABC1FF58B6FD31EF5E3FE6177B3C8FDD5C17BB0C1988A419FC2486F50A75CFDE219C0FDC29DFF3052F4186C8F111335A26AAFBE69B92F65A787BE0FDBFB122C639F32011B54CE64A33E77A429AF51E62720F4CE6FA4A9A30765F01B4812CA784FA76918E0EE25B0737B4B0A506F0F063C255E08EB08E39713B5327E4FEB55DBC42B39CCF2EF25BD764B0AF0DD3A78C6DCD36C5EC9F40890770590F8A37AD7DF57EA7D2BB02045C3C802406C9BD50676E57CBEA22D6277387E
	BD339C7BF05F6FD14F66F212F6A44423A1A4C0F0770546FF27073131A40F30CB635F378D0B845DBDAD74F6257C3E434FE4CC66364DCAC1ECFDGE2BFEE40F23186203DEF53BD13C00807C1088D40B957688C12F69C4453EF42767676C9F2EFE8B09E11E6D76C30A1EDF1G56B9ECF6383D74B0141E84145AF18C396561796DD3ABBC4900D6DB83595CF36888CC046B0D07F9BF18B41B5CB372400E8A88ED90FCA08C03908CA19CC2AC04C488424DB01F01108F2184C2BD84938497845F1BB19F09F46D55D3BE24C5
	EA5AF99CC68B69BE527878D8377B1CE8B45FCB8C38D91A9341B57AE6369E4A6EF95BFA7A3B67CDD584D2DF8940BB71E6F64FA3710C9191E9DB85653A5895F6BA9711F7B6592D0E531F1ACD6FFF58CA6ADBA4FC45F1B5EE12703DA5EA9C6F1706E924ED54544328305DE2361A6A4D8CDFDE2241CD52FA5DEC8DAE4DC10F372B659BA579E5AEB5FEC8423F2BD4632DB8475A2C115F4A70FF64EB70C38CFFE12D86B732B95767BD5AB997292B6C91B8A7DB31C00F1E4F6947F569A51CFEC31612DEBF02517BB87DC437
	9E5DCF162FCB8F64740F33757407F27A2A1DFA74E8CE5FF4CA0F9E4F696FA66B69CF62F40320C7CF8FC47AF4DDFACEA07AE7BBB611359A4510D620E84B858D15FB6B2B4BCBEB8F6EAB5F3B2736362CF46F415D15GB25E00CDEB7B612D5A33377A27BD3B6B5D3951D7FB09AC61C96DA9311B0C431EFC35F5057906CD05DF895E0D5F9F112EA969DABCD756356834F4B13FE7295BDA766979B2BF4E70FCFFEF1EB1707D5DB2031E9F067BF2FD0FDDBA8B52B1030CF93656D627197B914E99199C2BF656591C9632EE
	49EC2FAABA33B54606B3643B127295861E5EDC2F825231B548B729BF509828DD1BCC2238A9D03BB6F9E2A77AGF6204586227FD029729F483381C537C424B5C390EF417BEE5DF13C977B5E02775D9FCF91F911900F0310F48B4E2F1FACBB33F2F8698C79B24B4FECED77CE6A67E45C2AAD3F87A81EFD2B367C3EAB47724B855A50DBB13F6F2B4EED0D3B155523F09B3B4E3B4D3DDE995F672A3330F9D23AE269DA67116F973735DE13A22DCB070F656BAEFA2E4077B6162F859C9FC571A8854E721BC47DAA92CF8D
	0AC80B89C25F63158CA2134F91181BA4752B57B34838E969006EE434DAFA69517EFD103063B0234F196339FF4F1FB3F07FDEB2C75FFFDFB1475DFF7FFA0E3E7FDEB8C7693FB3EC439C623B10FD17C61EEE8B47FE6458F7F3F0FFE63F45A436F13C125AC570AAB3D1481F8D22F6F5D32C914FD7AB309FA56C07B9E40C02AC3711CD9F691919B9E4D12E55E2353278AFB40D258306ACDE9EAD54C60B5D28303B0DE3BF723A7C389B5B2E7F9F206F7F817BF5A55F1B49389D7B35131E3B2A00F8938471F6EF7D7F9252
	84A43F3E1A2E7F11B2733D9DF59EDE00D8A4476E6558645BF16E34A2CFF57EC2F146973DA1467BC2961FA75C0172C6F6A3E665D868AC4482B9361E6B0D6358AFBB911B970CD847A944EAB976951F1F8C6158FE8131B00E15F0ACA99857C5EF25733B7CBBE1BEF0A7E21F56902C916256BBF1ECF9E727407B7DC70A5F976EBC7BEB977EE7D587FB567EE8ADBE77643A13BDEB6F875740BB25357CBB318CB3DCB86F0B3D935B6949DEBAFE5F9511168B2178AEAC339F0EAB4747FA405B6E1247443E3BBC0D0F4F537B
	195FDD5AB3F971949FF91776CC5E41D3F8A6EF8C50AA6EC67D77154B75942678DDD9FE766B6C17332A033553D934EF05C0FE436F66E7709A88968B71840EDD5D00EB7F63B9467ACCC98854F588E223FF26678EA0AEF2AC6597647561D8A71D7B47C0BC01E3C99BB1DD7ABDD87EA33B09EF5E88F191029F04E088D19092A0887742BC89C2A104DA88ED90065C4BFD974A9BG71F0887190047BA2528C90CAA0B4C3700190GA19CC2BC84E1A472FE54952EA8ABD152C564A3D6A87DA636102B5FBD1EFD3EA1776A53
	086D41D2DE4E248E9BB5FC359ABDACAF5FF4AB73C27A9D39EFAB779166CA57198ABECFBA97F84049C4030C29337A616DAFE84FE1E877942433DC2EB373ABBFBBC33E9A2F7958B8B3165EF78DA3357E68F80A670F547A23BD7CFCF491507CF9BB5C5DC3748B77CB7E27F44D132E4577FB72AB2D8B88DF1DC4B7C9D717F475754877A0658B1068A325EB14F40D734877D216321C46EB5225BEG7D74813D367910EAEE192FC9D38271FADD3EC7E7A9795AB4E97AA06E274B371966AFD02205CA57E8699A7F00277C35
	6CA4FCC992BD7DC1F64D112E058FFA62F31EA2FC95923DD13ADA252B0FC73E8255FCE218A6DD9044439E544BDF1320640B5124C90078C4DD3ECA95DFC628BA0D81626BB598DBC7F8340874F9112CFAEF375135C9368FB61A7AC4D540D39F0A6BAC73F7CE4534822EC9F410EEAE7501E135588706GCFD8A87A8BF135782E1B24D07497166E227D68A138D7C3E8FA8871C40A0B90770590789076A3777E9F76101C1F18075C6B017557F729DF127010763C77E40AA7BD24BD6FBD24974FFB27BE8C770B07715EE625
	F36EE80847F36C36BE0225BE82FD63913E47FE926F01450F206DAF1D9A789C6C7F50B44D406383A1GC2B804F88829E11846FC2AD9134733F78E5647CD5759BB871BCB4F9D36C273E3EDEF8B75E972A05F6B43785951867599542B4874D4BA83FBFB43C06B614AB3C25AF32226B05686BE7C7453CF192E129435BD8D54CF32C33ABEEE4F381F55760C4C126DD970CBFF76CC9B401EE0B7FBFCB7386F930C6067F8C24358BEC18CDC93425859E9E6474CCD836D0DCFD758B199F8B39F156D48FDD4FBFEF6CF9671BB
	78BABABD7DD4A651A2AD6E348A0936E13BBBED3BC43BF389D97B13E7110C48E83F2C5521D5B05A962B8E2D0C517E7DED06D25FAECB073417BEC70D9BDD8C3F2AE716925FEB6CB071BC9F304DD669E1C4C6FB636BD9AA9A8864FC81499A1A258362ECBDFB2ACB3BDECE6A2C70D1DC6FEEF2DB0FA7748ACE4F5725B7F27ABBEE7B8504EE6574CF5D7683885D07531F52258F6374DD5DFA7A03B8FDE4169EBD0C53476852E3B87DA7DD7B93B87D75AEBD7AC4CEFFC1171E71980E23EFA91E1BF9DF717B54FF60991A61
	E7D5873B871969BA0F8172DB74985E172E20D885446BB976F8A9415AA06E625804D23CFF8D6158058BD0DEAC4702A9968F71719C1BCE1F8BC97DE3C45A3CBFA236318C6599B876AC17E7675887946B0338AF47561490ACG62A19C7B2504BF634D31373310B7A39C31E778FC574031D5743EE20778900E45F4E3BE06F36CFE4E9B4C31951CF7B247E2E7E119E67E8931DDA6644D6558ED5948ABF22C0E1741D00E75D0EC984483B936052E3504C2BC0AE307DD28A316E33DBBD1DEEE84FF760CE20590AF6118E5A5
	416AA1EE625835ABD11E1DE3171DC2F9619C3B0FE251900F6318FFAB4146C3DC980558132DA8AF03E315A9485B4431CB9B39DE0EBDF58A4BAA1CE3CE8173914B315A7D481BB8CAED9F61AD0AC42CC940B22D65D8A167ED66D81D003CA10031FBFA4A86B2B63071FCC28DF93725EA3F0D784BF94BCA9D9623F5D93EE18D7CDF7F956F6757571E59FCB5688C79DE77489799296F83B2BF789A6AEF0F09547A473F27F8E22456BFDE358B7D6309C0EB7AB36EFFB56DA6F8781F356F8AEC237898854E643C3D8B5F95B8
	9EE8750FE3BD6431768E71E80E6D6467D647BF0EE37844DE75FB9DD20E13B8B9EF4E6217751E7D77BA0C68BD1B6FF5E06D6E42636E676D2FBF2EBDEFBF6F896DF97B477AF44E1674A1DF5E934CE7AC01EB7D930C0FBDE3326DA42631826E83A1GC2B80478A7707E1AA3E86F5F04DF08C27A72DEBD7ABCCE3FE03E769DC7041E4769CB7668510B23F00FF474946D99ABC22F833A0927595A2317268F68FED1583F67EE91F8B97F54F09A7D3B1074EF46DBC30E50C83FD33F1565BC7933F7F2D611A32ED21FF8FA83
	4E1F03404678A81CBF4F5D087BC259CF62BC3914BECFD68771B688BECFFA3BAF74DC8909B17945AE5CCF997EA46A3CBA9B31980E35F0AC7DA9447232F1BF2504E39BDD0839B8164C31900E8D69C6F9099CFBB38BB14353084D61FA9BB9F6955FE77263D8B65FE70A6258F40E414DC76A9BB6FACEC834592C7596BB7150253AF837D42E47DBFA54F340C76898D484B2AA381CD7CB55F340AF687E02F4AE394C1B3917F68EA8C64BF3C0A63B28C236ED548235EDAFD33F4187F8FC39ED06B235ED87AAE55BF68DE85B
	4CFEEC8BF3336D526D32ED33972AED4BDDC8EC0B821ED8EEDBD5095A36609D32ED1DA58359B62B9F5BD21F11EDE37A33E85B4984BC67991C97A78C38DE2054D17B0C2C03501AA1CE4E6EFE4A67670FF41376E56EA01B63781CDDDDB72EF9D91F41736B9FD22C8F62C339AD51835A222C0BD00DAD518ADB180E5DDC6F982E77E36A27A6C2BC096BB51986522BEC1F394F2A7596BD2B559B130DFAAB1E45717539AA39DD78E95A6C2D344D56C35A66E731DDFC6FD2370BF72B65F6F1F240F37E4AF59EED3B70FBD65B
	E63F58A95B7670CE35EDB154EF76871EA0EE1B4FCA35EDC53532EDF72E9C4CB358E35CECBB30CB366D29D3EA5B62290F1BGBC6339ED77352AEDBB39DB36AD3EF5A05B6674E31B61B966CF497D29037A71790097BD07EDA920F12036245451761C3AAD75BD27EDCB93CEE1DB9A7A9C36E1FFFA8F9B8E71C02E37E57FC0FA15659E2B511B6826B70E6BCD62FAAF22FAD3E360F78C6A5DB120DEE5DBAC09D16B2D0F516A6D91D0EFF38C7A3C93691AB9F947B3B9BF4E7C4D9BF6617B1AD97C969A17695F6CBA7B6FDA
	5E5E2F0E417A42CC47215D7A6FC96E20389D4AE8B80470984CF79237C949B398BB5CED1A357BEC6A104EA93B5CFD768617520FCD04BC67BC9F11D678BC2BCB66339F623EEE9560CD907AA078C388FB9E7D20970E63190D386771B9040F7B505F1C77824C0FDF606710CE203FB96485FE8E6984C99B8671D88863F85A161318B675C5FC6E682A135A670EBED4BDB7B4CAC5A77B4E33D47477D4F4F29F3ADF756E13D52A7346F80FA4F4E653BEFE462778C5347327DE44C40EBDB41FE064DE9F4C3106BD18AEB316BF
	7B100AE5D7950B6561D3CC4A43876243A10470344E9ECC9B4553267DC09F42988DF3B5880523315DBE6F75F3620C6F35B3644B5E68AD9F1B435FCF679AD52335FB610FD13CFE34F6AFFC47C6ECD74DC00B6479AD25FBF8F9FF11763E25EB1DF4B57D4553DE6A70EC426712683E52B5C03A0E744857C67922A4FA1CF49DAFDDD3DF724477CD36321C32B56972A1DE72125E9E6CD6559EE53DA6CD9B447BF47952D4FCFE1AB401908F55653B146E3DC7CB34F8691AA4DD5347F84A5FF83A571FA3518B25EB05F4ED74
	48B70F160BD5227BC857E152B548A35F352A3385E11AF4B190CF9823173F452A338593B5E9B2DE0EC8B33C2C47B7DB4537DE13268A62CD9A0C79CD7F28031F689316FAB70FCF5FAB705F9F554BF2BE76D24E6286D9CECF23ACE7C509F7F27CFF1465CCBCA04B79D343B2FDB9A690E3BB8C028AA5C11E578E967DAC4BBB4C5ECD6468B4B3DA77C1197607DFC157AD4D32BC33C657B5EDB22D54B4F8DD2196D95E58EC352EA5D619D660722EAE4A5A653A98E117657C3C52BBB90F9F13650C97E5B9FE2D5E49195BA5
	4B69F549F2CEB48E3E8CFF57A34B1BA1284BB035CF26156C9F3C2ED5A7E4F993CE29F5DDF1CA26657E8A3A6E9194654D73453E97F053EEBCB7EBFF191D5B987AB2FBEEC37D7D01547918AE6AE5F6468353293FBFF0098B534583FDA2041CD7D8BA7577878EAFA34FBE7267D9AD075A8BBA3B5A8B0C0744823349A2EE59323B53EA2D155CC949B76A63E74C9BDFC17F64F6FAF64D8F62C19042DFC1DFE6E88F269D738A7AF68F74E8FDBB75FB6D92FB343E1D7A3DF68BFB353E1D7A3DF6353DDA5F8E792533B474BC
	F166DFCFEFAE137BD77E8E3993E7F86E708C79EA3C66134E9DD2BF3B682FEE678EA9DE71D737F307A750572A835AC81E5F3613E7E6F7236A9CD9F69CAB67FC69DA9267699EFF30D7657BC8695A24EB1FC73E4766EB6713FE10B6D0CA9F2A43C7C2348E9E8FD8D21C1EBFF0E00F36AC52DF05BE752A647F3C6A711920D4ADDF05A2ED238E9F2B2B12ED74DDEF2FEA1F6D790162C12FEA1F6D3938981F6DC9GDA485F308EDDBDDA7DFD2F49BA7DDE73247F85BA5F88FCCD7B8E3AC40AA7A8F026BF0362A41E9B8FE3
	5D6B287F9172098629ACA2953FF7EDBC7B67A4DA4F2A8E3646526842BD6E3857F15D65920AA5C2FCA2475E23E78EB25E00B67386E24B78B909BC0E5D1D0D72DC9C9BC331A1909F463107293CA00807F2ECACBF57904931D3DC5E3C3FA39644B00897F26CDA4ADB8171FA0E0D62721AB9D641F9C3B87689BF6B904931C0FE56E1DE82E2FBDD180FBC0E5D4177510BB9A6F2DEDF0EAD66E59A403191DC6FC80EFD50053C297FC0AC209B4BA01BE3CFD2AC97626BB956C873DB8571C60EDD444B404431E93CCCC7F22C
	10E261900F61D88B65CD00F892477C38BC61FF783B1C393CBA0E55D32C8962D60E4D203CBE90776358A6AE4F1FE3F7E6A16FB80EDD404BCA980B58653C2C6AB8363E9B73E16258B51C574531BE4E9B4531953CCC63B8F63D7C4C8847BE663CA509380E7494DD1723EB1B0079A6623AF44C97E77A8D9FA6771B79A8B71268627AC2DC082F7FA71471BFF14FF72AEA4F17FC33A690E8D190627E495FE55AFDA65F2CE1E352EF4E70D902AFFB68D963FFEA6F692BA8AE7CCBFBCF8F614F92E4G4D7EAF9C0FFF6BF6BF
	B83DF16EED3A7627FE3EAD0B1EDDA64F3745F279EFD1FFDABA972778FD6A44FF60DC5CD9554172EC23FEFDBC64F77C3F309C62AB697A7E1B4AE725AA37E33B1F77A6E2DDF47D5084F111E3EF7CC857685FD4BEAB3563A7649D4E534553F546C408CF66D8AE6D671963146FD33F14E286400A46A176F73A5FD28B71B60EBDB69FF574F1ECFE95BDDF8571840EB976205E099CDB7ABD3D173D25FC9F7D455F233C1C377833E16C5D6E907761D850D4CC97789636196D5B4E7C792E8314B789E44DFB9B46F2887590C4
	8843A00C04908BA189423CFF839DC27D3F115737B8DCD13F677773DDFFD46565FCFD3E0B3511AB691CD104FA9A7AEFECB7330FE33B896675B76607413DF770ED4ACF467749A0B3E7BC78A0901AA074C170079086A18E42E488B96FG9DC253BB487F798F7F7F3EF730DAB7EF677BFB8759BACCAC39ADCA6BB0B73A54FB63A359FB19210E073E03777B8C4D991D8F683BBC59BE6E6652416C5B073E435E9DA86F5B5323AF12ED7F4ED65BB6151EF708841E98EE5B731AF3B05B264A36FDB260B9187E4E8F896FEAED
	EBCE13ED7B2346365154DF4DG1E6CF75136BBB536DDB8CD366D1581ED6B6F7CD0956878D8320D69EF277A6B81EFFE9777583F98F00FDD29436FDD759EFB603B5ABD769778391810F7F1EF7FB8EDAF61900F66FAA386542BFAE6C623B7F302D66F6F385E1C8928F7BB551B8F712289287761814F81A95BE25B844D998AB73D862EF76884BC0FF3EB3A5C8662B54FDC3DCF538F0734019378B9B9CD9BF8F23A5C867C87EC834A20ED83312003FCDFC9EE1F63E648366D50344F1FE97B08871EF15C36DE0DED74519E
	49365B86342D3F33C2B9C991E9D9AA5B564E12EDBB2A316DB75436BC60D91F0436AD55587643EC59365D03BA5FE6829DF38465F91C3FD01F518EF8DF9236257F766AFCDBF0123AAD05A7E95B5248AEECCB51C95806FF266DA58E6209DCEF7000FA15651E7D1EDAEF7EFBDA3D37F03DC56F21DEF63E2D8A62756F215EFB3CFA7EEF08C66FF0B73DABF95F89FC8F7D05185D5A395769FDD371EFEEFC5AEFAA0EFCCF0FEF5D7CB353D76A462755972D2B6F8137396D69690BF463536ACB5068E3F3F503EA2ECE3E87C9
	1EF31D8CE9B35F477D072DFDF8BEA36FFD5C2BD8445E1B8471C088E16FE37D6452FDA7E97FDE717B017F40B3DD4F1DD59DEC8CF111EEAF0DC1978B6AB1081D0D0B053208FF9F47208FAAF18CE2FD24E97BE01E114D7CC07BCC6AEFFB55B60C2073639CC81777815A70610FEA9BFE79E9B04F45B63A59F0A91D5F2A3F1FB31D0E07E3A118389DDB33508E968F48527FE6CDA86595014F0773FE1462791BB5F32710744110B674839C0BC64F9F4CB7EB92BE50BEF37BCD157EB7EBAA69FCFFBC241F7C810E0DE9F49E
	CF3E9D14B7916DAF7ADEDD873F7BFEB0BEEF51C42DFDA5296EE7567627E25F2E18481EB3E9022BFDA2FB1E1D5971472903F9B6F928428E764C48F55B344F1C90DD43A662B3A5FED5FA74A0CE1F24CB8F6374C55B354F1492FA8C272F285323A7F07A8E37E7D2E9D9F0FA5C8CBD7D999FA23DFA039E5D406997556A5157F37AB365FA7A2BB87D17B2BDFA93274768526D9FE2BB3B35473D9DBC4A4778A19F32F6608F57108F59D84F5A410A6303F917ED5407723BEC191D5B9A3465C0F44572FC64EC5523A7F27A04
	8ABD7AE4CEDFD525AD87C24F7C88692B2B7568391C7ED0059E3D0853434B747457F27A033A76B5F3FA10AEDD64740A9ABD7DC3B93DD7176E4F69BD3A74100FF00E719AFDDFA9EB937F74724CD28AFD3F29749E7DAFE4B9F7F9A967B773E5B90959321C4B3C14B3F321AC671645321CF7FF05F3CDD1DFAA4AC9F32EA9F729CC7B7D2FF0F6E547B2D95EC80D2E9139B26D3ADFA1DF0F2F1065ED5268DA16A753EA3D2C0B3A2F643A38FF23ACE73F17F246EC1265FC27F87E6F136F3C13F3C8717C5F135BE4B9DD3FC2
	990ED3BCCF78F4173A8C2B944F28DD7AAB68BADAA64B8B53680AD6BC57F5452F206B5FBBE4F95BF85BE071E9FB345F75E67811795AEFC3B37CAD17763B4DEC4E50CC5F5BC8FC26480FF04EE0B9CE668C13A1BEEFD2C4DA7EA41CB31474E97D760AC96EEB2D0D00D9F5F092FCA4FC98DC03B4E9589E5F15F43E92B6C93B77B71C3D67F512F66FEFA9FFB7E8AC500AA663385AD0A317DDEC25F7ED35E7275CD6C7AB1EAD7A4B0F5E491938DB66B5E4112B7C0D24C50A3333E3D2CE7FDBC7555F4BFC774DBFFDBE2B62
	3C6D157BE4995BDD27A30325DD7CBD4D039973F0AFBD374B6A61D4AAFFC7AB54C16CE41C13BE110AE762B2BE4676759ECDDB8171A6886247D8E723FF90F83BFCF69B61236FDE7D184B2AC2AC12E3EB2B911B4C313D5B711904751FA056D4071815E3A779F79B82B9B6F7A64A0B63184F864466FD0AD8D8ADE2359CCB2DC0F9C3B8F6CD99E2E19C1BD60ED8522738C77F0F9EAC3FFCE1D41A8982CB7BCDAF161FBFE02C7C8E50F7EE45C2FC9C4112C7C965B7E53FDCFEE2835A17130C3276EDC52C1EE3DFD772F266
	58AFD5E8DFA447DA2B911B4C3171BC3F6BD3907B208C65D9B976F685E2019CFB0DE3F19CBBDE037266CDC16C9E0E55F2AC04E3C3G135E75D5036BAAE10045C0C80000E34BC4781D11BACAE73CF9150EF58620295F67B01662058A1C55136DB8BEEBD58234C088EC4F6E90DDFBC91FBAAAADFFAAE2A194AB01F8A347FE226F0933C25C17E337D1ACG62E19C3B219377GE3B896C65BEB92446725E13E2FA11FC61256D5EED47CDEB5686F047697247B41FFC047C5EEE78ACF0F6F6AB3640B761A0F35974BD452AE
	7252C6E96EDB8E0A2FD7602C9DCD6263E5895082F9FD3E380D68CF1F465AEA0EF4AD142E9553C6395DD7A53B2B88DF23C437CAD79F69BA4CA35F26EA4297A4514324EB0CF4CD704837FF3B6AD993CD3A0C74D1E90674D1BA6B1F46BA5533A61AB4D590EF52653BD0751EFA3BA64D90088F57654B1EC97802A5DA38F40D152E09691E72F791FDE6EC32C44F1C4E2E395235E83AA73EBF51774A54CA74E669AACA57219E7926D7A873672FC99782714869FA793BDE755D01B11AB463A0AE4C50634BD0FDF7E01EA6CD
	9E440BF5795E747ACC95637B708C7956761ED9FFFC0E5EA36BE6E87B63980AB74D5076C7E3AF7647B620C54F407E5853C074974E147A1FF4ED142E56191E6A33F1AB617311684324EB10F48D7348373A127045C8748469BAD13AE6E4F862EB29D21613C113EEBD442BB2746A73E835122FC913468E71A13AFC6F295A6BF0CD1AE008076B72CD266DB5D622A5CA574952B5F316277CCD207AF2A5FA11F42D152E4D9E7992A81FA85107CAD7FF699A62114F27C6193FC8CD3AB1909FB7CBAFFF8F287804596AB473A0
	1EB7DB0F6FFE95DF31A6CD9D44CD9ACCBA1BCAFDA197507CE7231F90CB3190080F61D841CEBC97B70EE35582BFA7BB87B1F1BF1E0B2B1B03FD606F47555FBDFF0B4649F3B6AC5EF97C6CFF777C0AC16B18B2GED2E5053B770F74F077521BFEE0272E1FCCF7469FF777CB51A568569067034470A7A7F6E7965C5A8BF0873BCD664793B67AF5174E110B68A82FB3E7D5ECEDCC79ECF304CD1E93990AAA018B2D1660BDD7DFF777C2DAE34E3C8A636839BBDEFA3BDEB21781DBC756C7B018B4F2A8E56767FC5671C43
	A13F72738DBB3F25FD893070CC6CA3750B687EA14465679B9EDA04FDA901277BFB954A131FEF28207328751FC1FDFC06690EAE23E70BA1AEBF5F90338C653571F43B2BD11E7CFC435D948B05B4D1BC5D21A5742C814465679B1ED802724671F49D5B113778F3441EDD08696AB8769C5DB78C0078C80E396AB09FD19C8B636FC50C6358BB1D481BBF97316B3B30CCCBB8F6208EF96BB9F6718C648D64586A19D88672738DFE948B05B4D1BC5D21A3348CA0AEBF5F704491DE86BC1D7F8614A7BF5F70B1450A670DCA
	2B1D4747544F88568CF17979064D1F23BCBBCFB7269665494FB7EC5EC54FCDC11A981EAEE0B14192A0AEBF5FB075CB1417445399AA1037628B444672734C0D9CCBAF43328A6258BDE5180FB00E19B9EF8C47FAB92FE1BEE24B4B31CC57F36CB64EDB45318C4EBBFCBE0E9323FB653161EF0A5F07FB4F7EB861F3D6F5C86D2D816BD8FEDFEC8A3DEF86C39904713239022EFD45C0DC713E58D4AC43F81E4E369565496F0B0D21D8E1967411ACCC57C9775B6BA1AE3FAF7615AC14E7626936D722BC79FD3137D1ECA4
	C94353D59888B68662727BE27FE0C0F909BCDDEF957296E5A336133755DA0E45D6E1DB7067588555180F900EAD64E3D1A447DAAA10F75E825E36AA31CC73B8F6D9B67296F36C88EF033E9C1BDB0EE5A03FAFD624D8G248961691E2263F6A4446577452E4EC19D31BC5D8CDE26727BE20FD2ACF7A17411053C5F50FEDD85F179FD31D7AF667D15271BD401726477453EC17D36A0C8934A53FD15CB7DB1084B6F0B3DA2170F33BC5DF51CB7FF91E29BF9DD16F0AC37864B4A0FE34F54E0BE82B9360E7306F2ECB967
	CD4FC1AC3A864BB407E33BB8EFA147BE2DC6DE1F9C74C12E2E99781B68F7D179643B3743B85F1BB52777CD74E95467A5AF0C0C81DE066D27D866E23097825BD3EF968673CD74AA10237EA67A6F68D8D08FF873E234790EBE776F16DB946F2B783EE860EF1F5FDC64AE630D94D946D51D27776D73B11D685B7902FD6CB972B455F98BA2CF7D6D72A2955D7D5B67C7D4F477EF1F7B2B686E5FBECF6DD6660DE137B5E85FBB4970C35F2A4FD239280F9689F9096165FDF4117A9CCF4C22411C63913E9C454F07B37987
	2A55B69C236DBB8352E5FF09B6DC30CCED4358E503B923DE65E643639A9B62288D75102E195B5033C4ED435F168C66AC3A9F48D51FC53F0E7A107E0087FD097D699F8B87F396FD8C48D13FAF724DAAD2B76A77C5CE24BEA921A7FC09FD7BA66AC3A6C1DCD802363C586F3B8687BA1FDE38E414662C6BC2FE4636F8896A9DC531DA08B7F23D0E3A411CCF9F6A26776E9928F758922C5B5BE62A6B7FD16A878782BD042769B922297FA303B9079E8FF2719CBA13BFEF235A062F693BF74711F260B63C79195A063A4F
	87F35EBC4FB00A1FB7E7726BF629EDE8257E76FAC8D7E2C09B9EDC2C36A1674B411CAB3703DC753972EAFABF6F83FC28815B40A77D1EED9C68DCF928C15B865614E19B08B4E05BFB05E231900F67FA7B86545B5F39724C25DA3DA3385E1C252837167AD27990AFDA0AFA9F9BD02F32EDE94F153739695D40750ACB319E976CD75775EAFA2F9D82F4BF1E6616D4F5DD4F706A6C24362E43170E521C239D3BD5ED434754D70F06F4E3388D3ED9EA9B5E499A4C381B3ECCEB43E315EA9BE229AFBA8F529916218D878C
	EA9B428D03E973F58A9B24F6C17D7DA6405B16E19BB854EF3F9A68DD414317E95BC0CAB5360140E558766E2058C80807F33DEB7A3D2F8D74DE6071EEFAFDABD06FE42EF783AD630C65F00FDF0EFAEF4A9E4CFB012B16EB75DE4D5BFC7DF22C474B4B55F5FD936DFBA6205BF91A57B57E4E761C410C31814B356D6D82CDFB9B4E66E610AE145B30C5B3465E3EF8B0BED7221B8D9F5628ED18C3FD79A4C8A764228DA539EA9BEE499D4C7BFC8BF3C7E95E679BCE67A34500D765E29B5855EF3F9A68FD3E2EDCED9B58
	D503EDE0C8AE363D77A9B68C6281DCEF4D00FA15652BFD1FEF0C1B5EDA2EB70E6BFD0FE26308CE2E57B4205E7E5E671B37C22BB70F6BDD3F826B714FB564D9F74527138BEA58B42F4E58E1AEE8285CDBDFDDDEDAFBF0DF795EBD3535E525FB8F6E2E841071FE39F3EDBF3CD5FB76D67F34E7F73DBBB74E2926AA1ECB7D161E4F73B427DAD718EF58D478154417C75744596F1D0A67C7EB0F1F0E8C36FE75EA27377B1D52D92CF4D9DF55A26F4E290D1EAE5B3DA6C316F3F019F7F26AE749F2AED21CEBEED8621D1C
	204FE5B9F394671AA3961E6E19CA69B94479B26F1CBA4D7B6E94671CC3D6FAA7771E85B26F9A4D9950D9B9B2EDCF1DF7F28B95E7269F1F21166B3BC4D11EC73CAB4F078DF2F94EDFAE4B097A4CBBB91BF3E5B981ABE5B94396FBA7E754AAD94E57DF4BF27EBA5F3B725A753F0AB6DF26AE2FBFFCA353F6FBA9B73AC066FDD8A3B7EF0DCC3B41CB39F4D842B1C1A3773675B26D02D46F4A73298D0A7EDBA44B31E4F959FFB74BF2C2364AF20A8C5E4979DB31ACE74BF7321C6F3E742E3C5ACAE45E85556A720AD41C
	431E3D44BB39E34BE55E9195EA39650AB35CA7333D135B2AB82FFD3B26FEA32AE55AE3DE0E4F7F28114B33D6F19EB94B4BFEE75BAD4BF9DAF17EF8F12EF7F25E7EDE167303625D3DDF9B3CABAF578FB22F29C6DDDE2F6D17E92B3C141B72234C5B22117B06625D3E25DE4A3D4C21E84BDCEE960D0F1DBD87C6236037042922B0E33B314DE16EE8B3DBC4D1C823DF5AF4BA9D429C2E253A43E9961D71DD6B36CC797B08A9A9C2EA3510F648A60A3676EAA155EAEE952B05298E4B61B638CE91EDF697F987AE3E0EE6
	B5B18124FD6E6ECB14DE7172CB4187AB6625402CC9C88375D44254EE0BC9EC23B248FD6B207318F012BE90AB47D284F99ACC648B1AB48EE136FAE5BA79162C8BB32FDC5719A224EF9752DA0DD6FB1B91B4F5992D1D66DAE166A1EA3C5981E9C5A13316702E865E5514F7EB2D1052629405C5B25EC2F0C6A5079B4C10E43A4968B8CAA590D3E69C902637BA8E9F228AC4A16540768320164688B923D618A136194B217240719705ACD9F41D5152A1CA042D34900863A95869BF1ECD72AF199745058273D4098B1746
	5310016182D5F11199C28A67F4CFC382F1B328F7AB8A537709C651D0E1043F4274AAB37C5EE5B3C1EE26175BECD67A9B8AFA162AA97E79268D7F937D37866794A12DDAD8C0E6BE7B4C24CFDAFA4DA6A228416CA0953FDD18EE37C1BE49732E50021C74B255A976D821B666356EB59F6BB4BBC53329D4949D16C31DA2C404AFD899F50A966B6AAA2333CDA42B2BD070299D46F6B06AF3C6369ABB8E2F5E87DC9D07D969A517B653521ACD736D9066D2FB091482AEE56EA3F3D6AC2DC9881DA224EC97E6D8EDC653F6
	E30BE8F3D48B53C568984EB61B556432FFC7C7F7A11D3EBC00CC4ED3C15CA21EFD12598232684F0DBE6A7A1D6F2C44D95B21D8DD8EE1B5CDC92A350027B01537999D204A6C2806E08415391C761BAF45154F6C48EACA911225CC12EADA68320B248137DB2CBD242F520ECFAB70CB08F653AE443BAEA90C026D109F7115371E3C792F45634701306DD2338713B265D43BBB5B8F199D9787DF33F86630784B094576926CD7A0F93A68B0F6B8491B7DC038C113E7A4F1538B2EFE32701ABFFC7493B5DDE69505A5F2BB
	BFEC36352FAEEDED35F41839G28B1903F180BA7E98AB4E9BEF7DD75C54D68CF8FC18B2B9026349796523FEB685F35646F9A0A2C21481A35D0ED6305FE7FE969AE4CB3ED16B4320234A19779350CD133E943B25074B3F8074AF1EFA184B670FAA23165644FAA72A70FB2FDCD7FFE63351504EB35E41DC10A2FF739F5E5526D05F359662DEA1BF111C560A831B5DD4A0DA08A1F6D0566EA5EE2D8E390ED060D9BB62C5BA08AD36ADB36F82218BCD24C9EA9C60F14C39EA92DDB4E65D25EA317F232B27139ECF3033E
	4D4967324DBFFBE333604166A96EB67F7A161A54161230645C347468F9E329C35F52F33A177568375854F359E69F59E6653FF3FAE438643C99992EB8EF7A5BB56AF6F08EDB6A2FDF2629B0EF113C09EF562CADA28E451A351BC4A11D4E418DE5DB7A27D78CC05F8E74B9CAEFE56D8670D5D2F7D9BABC900CAE8F7E4DD44008A715CAA6F167F2B78BF16FE6493C724FD11B9F5437791C9E4E9E7346667F532E77E7772EA72F011CD316C61FB7163E28EF69B95D4BDEB98FFB596B6E0E4EB9EFF322ECB3DF2974E873
	7FE9AB7E375A52F3383F3D6F6E681C2316FE6C4D98F6EE3458645473EF59ACF9E6FF4BE66A4D926F36CEB00DFA7B86D155E21CDB292573347400C63F7B965E6FD01C23B571657967F1A62FB87F6E054979670B47193C6EFC716312371CB716161EFF9EE7F255F9584B761CFF9EE7F2437962F1A67FFC3EF81C4926734563CC56599EB867BDCE077EE270B9BD1AF958A8B8274B5967FCD9F4CF3E64FCD9F4CFBEEF3687122F5137741C6EE57E7AAD76DC5E0ECB9EF17EED47A55FFE5E0C8C771CB77DAD74FC590ECB
	7E23076DB836EEA1FCF50A9CCAA7A7861B2E8A90A6DE141D024F7FEB1FE549781E3D634C00D7616EE4320E721950423E918C388B8279CE5EDC613EACF2483F41F82454EEAF331A9C15A60BE8F3C0E44B1652C3623EF62355AA9A2DC7CBBBCCB546AEE3D90FE8F68A6C9BFBF30536ECA25F09E9A4A2235932095DC794BC7F8FD0CB87882B6184F2FDBCGGC86CGGD0CB818294G94G88G88GF4F954AC2B6184F2FDBCGGC86CGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4
	E1D0CB8586GGGG81G81GBAGGG37BCGGGG
**end of data**/
}
}
