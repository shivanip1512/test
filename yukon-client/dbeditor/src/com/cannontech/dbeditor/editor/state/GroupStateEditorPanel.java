package com.cannontech.dbeditor.editor.state;

/**
 * This type was created in VisualAge.
 */
import com.klg.jclass.util.value.JCValueListener;
import com.klg.jclass.util.value.JCValueEvent;

public class GroupStateEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.klg.jclass.util.value.JCValueListener, java.awt.event.ItemListener, javax.swing.event.CaretListener {
	// this must be changed whenever the number or states are changed
	public static final int STATE_COUNT = 10;
	private javax.swing.JLabel[] rawStateLabels = null;
	private javax.swing.JTextField[] stateNameTextFields = null;
	private javax.swing.JComboBox[] foregroundColorComboBoxes = null;
	private javax.swing.JLabel ivjStateGroupNameLabel = null;
	private javax.swing.JTextField ivjStateGroupNameTextField = null;
	private javax.swing.JLabel ivjStateNumberLabel = null;
	private com.klg.jclass.field.JCSpinField ivjStateNumberSpinner = null;
	private javax.swing.JLabel ivjRepeaterLabel = null;
	private javax.swing.JLabel ivjVariableBitsLabel = null;
	private javax.swing.JLabel ivjRawStateLabel1 = null;
	private javax.swing.JTextField ivjStateNameTextField1 = null;
	private javax.swing.JComboBox ivjForegroundColorComboBox1 = null;
	private javax.swing.JLabel ivjRawStateLabel2 = null;
	private javax.swing.JLabel ivjRawStateLabel4 = null;
	private javax.swing.JLabel ivjRawStateLabel5 = null;
	private javax.swing.JLabel ivjRawStateLabel6 = null;
	private javax.swing.JTextField ivjStateNameTextField2 = null;
	private javax.swing.JTextField ivjStateNameTextField4 = null;
	private javax.swing.JTextField ivjStateNameTextField5 = null;
	private javax.swing.JTextField ivjStateNameTextField6 = null;
	private javax.swing.JComboBox ivjForegroundColorComboBox2 = null;
	private javax.swing.JComboBox ivjForegroundColorComboBox4 = null;
	private javax.swing.JComboBox ivjForegroundColorComboBox5 = null;
	private javax.swing.JComboBox ivjForegroundColorComboBox6 = null;
	private javax.swing.JLabel ivjRawStateColumnLabel = null;
	private javax.swing.JPanel ivjIdentificationPanel = null;
	private javax.swing.JPanel ivjStatesPanel = null;
	private javax.swing.JComboBox ivjForegroundColorComboBox8 = null;
	private javax.swing.JScrollPane ivjJScrollPane = null;
	private javax.swing.JLabel ivjRawStateLabel8 = null;
	private javax.swing.JComboBox ivjForegroundColorComboBox3 = null;
	private javax.swing.JLabel ivjRawStateLabel3 = null;
	private javax.swing.JTextField ivjStateNameTextField3 = null;
	private javax.swing.JComboBox ivjForegroundColorComboBox7 = null;
	private javax.swing.JLabel ivjRawStateLabel7 = null;
	private javax.swing.JTextField ivjStateNameTextField7 = null;
	private javax.swing.JTextField ivjStateNameTextField8 = null;
	private javax.swing.JComboBox ivjForegroundColorComboBox10 = null;
	private javax.swing.JLabel ivjRawStateLabel10 = null;
	private javax.swing.JTextField ivjStateNameTextField10 = null;
	private javax.swing.JComboBox ivjForegroundColorComboBox9 = null;
	private javax.swing.JLabel ivjRawStateLabel9 = null;
	private javax.swing.JTextField ivjStateNameTextField9 = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public GroupStateEditorPanel() {
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
	if (e.getSource() == getStateGroupNameTextField()) 
		connEtoC1(e);
	if (e.getSource() == getStateNameTextField1()) 
		connEtoC3(e);
	if (e.getSource() == getStateNameTextField2()) 
		connEtoC4(e);
	if (e.getSource() == getStateNameTextField3()) 
		connEtoC5(e);
	if (e.getSource() == getStateNameTextField4()) 
		connEtoC6(e);
	if (e.getSource() == getStateNameTextField5()) 
		connEtoC7(e);
	if (e.getSource() == getStateNameTextField6()) 
		connEtoC8(e);
	if (e.getSource() == getStateNameTextField7()) 
		connEtoC2(e);
	if (e.getSource() == getStateNameTextField8()) 
		connEtoC15(e);
	if (e.getSource() == getStateNameTextField9()) 
		connEtoC16(e);
	if (e.getSource() == getStateNameTextField10()) 
		connEtoC17(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (StateGroupNameTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> GroupStateEditorPanel.fireInputUpdate()V)
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
 * connEtoC10:  (ForegroundColorComboBox2.item.itemStateChanged(java.awt.event.ItemEvent) --> GroupStateEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC10(java.awt.event.ItemEvent arg1) {
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
 * connEtoC11:  (ForegroundColorComboBox3.item.itemStateChanged(java.awt.event.ItemEvent) --> GroupStateEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC11(java.awt.event.ItemEvent arg1) {
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
 * connEtoC12:  (ForegroundColorComboBox4.item.itemStateChanged(java.awt.event.ItemEvent) --> GroupStateEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC12(java.awt.event.ItemEvent arg1) {
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
 * connEtoC13:  (ForegroundColorComboBox5.item.itemStateChanged(java.awt.event.ItemEvent) --> GroupStateEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC13(java.awt.event.ItemEvent arg1) {
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
 * connEtoC14:  (ForegroundColorComboBox6.item.itemStateChanged(java.awt.event.ItemEvent) --> GroupStateEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC14(java.awt.event.ItemEvent arg1) {
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
 * connEtoC15:  (StateNameTextField8.caret.caretUpdate(javax.swing.event.CaretEvent) --> GroupStateEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC15(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC16:  (StateNameTextField9.caret.caretUpdate(javax.swing.event.CaretEvent) --> GroupStateEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC16(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC17:  (StateNameTextField10.caret.caretUpdate(javax.swing.event.CaretEvent) --> GroupStateEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC17(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC18:  (ForegroundColorComboBox7.item.itemStateChanged(java.awt.event.ItemEvent) --> GroupStateEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC18(java.awt.event.ItemEvent arg1) {
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
 * connEtoC19:  (ForegroundColorComboBox8.item.itemStateChanged(java.awt.event.ItemEvent) --> GroupStateEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC19(java.awt.event.ItemEvent arg1) {
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
 * connEtoC2:  (StateNameTextField7.caret.caretUpdate(javax.swing.event.CaretEvent) --> GroupStateEditorPanel.fireInputUpdate()V)
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
 * connEtoC20:  (ForegroundColorComboBox9.item.itemStateChanged(java.awt.event.ItemEvent) --> GroupStateEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC20(java.awt.event.ItemEvent arg1) {
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
 * connEtoC21:  (ForegroundColorComboBox10.item.itemStateChanged(java.awt.event.ItemEvent) --> GroupStateEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC21(java.awt.event.ItemEvent arg1) {
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
 * connEtoC3:  (StateNameTextField1.caret.caretUpdate(javax.swing.event.CaretEvent) --> GroupStateEditorPanel.fireInputUpdate()V)
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
 * connEtoC4:  (StateNameTextField2.caret.caretUpdate(javax.swing.event.CaretEvent) --> GroupStateEditorPanel.fireInputUpdate()V)
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
 * connEtoC5:  (StateNameTextField3.caret.caretUpdate(javax.swing.event.CaretEvent) --> GroupStateEditorPanel.fireInputUpdate()V)
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
 * connEtoC6:  (StateNameTextField4.caret.caretUpdate(javax.swing.event.CaretEvent) --> GroupStateEditorPanel.fireInputUpdate()V)
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
 * connEtoC7:  (StateNameTextField5.caret.caretUpdate(javax.swing.event.CaretEvent) --> GroupStateEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC8:  (StateNameTextField6.caret.caretUpdate(javax.swing.event.CaretEvent) --> GroupStateEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC9:  (ForegroundColorComboBox1.item.itemStateChanged(java.awt.event.ItemEvent) --> GroupStateEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(java.awt.event.ItemEvent arg1) {
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
 * Return the ForegroundColorComboBox1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getForegroundColorComboBox1() {
	if (ivjForegroundColorComboBox1 == null) {
		try {
			ivjForegroundColorComboBox1 = new javax.swing.JComboBox();
			ivjForegroundColorComboBox1.setName("ForegroundColorComboBox1");
			ivjForegroundColorComboBox1.setPreferredSize(new java.awt.Dimension(120, 25));
			ivjForegroundColorComboBox1.setFont(new java.awt.Font("dialog", 0, 12));
			ivjForegroundColorComboBox1.setMinimumSize(new java.awt.Dimension(120, 25));
			// user code begin {1}
			ivjForegroundColorComboBox1.setRenderer(new com.cannontech.common.gui.util.ColorComboBoxCellRenderer());
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjForegroundColorComboBox1;
}
/**
 * Return the ForegroundColorComboBox9 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getForegroundColorComboBox10() {
	if (ivjForegroundColorComboBox10 == null) {
		try {
			ivjForegroundColorComboBox10 = new javax.swing.JComboBox();
			ivjForegroundColorComboBox10.setName("ForegroundColorComboBox10");
			ivjForegroundColorComboBox10.setPreferredSize(new java.awt.Dimension(120, 25));
			ivjForegroundColorComboBox10.setFont(new java.awt.Font("dialog", 0, 12));
			ivjForegroundColorComboBox10.setMinimumSize(new java.awt.Dimension(120, 25));
			ivjForegroundColorComboBox10.setEnabled(false);
			// user code begin {1}
			ivjForegroundColorComboBox10.setRenderer(new com.cannontech.common.gui.util.ColorComboBoxCellRenderer());
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjForegroundColorComboBox10;
}
/**
 * Return the ForegroundColorComboBox2 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getForegroundColorComboBox2() {
	if (ivjForegroundColorComboBox2 == null) {
		try {
			ivjForegroundColorComboBox2 = new javax.swing.JComboBox();
			ivjForegroundColorComboBox2.setName("ForegroundColorComboBox2");
			ivjForegroundColorComboBox2.setPreferredSize(new java.awt.Dimension(120, 25));
			ivjForegroundColorComboBox2.setFont(new java.awt.Font("dialog", 0, 12));
			ivjForegroundColorComboBox2.setMinimumSize(new java.awt.Dimension(120, 25));
			ivjForegroundColorComboBox2.setEnabled(false);
			// user code begin {1}
			ivjForegroundColorComboBox2.setRenderer(new com.cannontech.common.gui.util.ColorComboBoxCellRenderer());
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjForegroundColorComboBox2;
}
/**
 * Return the ForegroundColorComboBox4 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getForegroundColorComboBox3() {
	if (ivjForegroundColorComboBox3 == null) {
		try {
			ivjForegroundColorComboBox3 = new javax.swing.JComboBox();
			ivjForegroundColorComboBox3.setName("ForegroundColorComboBox3");
			ivjForegroundColorComboBox3.setPreferredSize(new java.awt.Dimension(120, 25));
			ivjForegroundColorComboBox3.setFont(new java.awt.Font("dialog", 0, 12));
			ivjForegroundColorComboBox3.setMinimumSize(new java.awt.Dimension(120, 25));
			ivjForegroundColorComboBox3.setEnabled(false);
			// user code begin {1}
			ivjForegroundColorComboBox3.setRenderer(new com.cannontech.common.gui.util.ColorComboBoxCellRenderer());
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjForegroundColorComboBox3;
}
/**
 * Return the ForegroundColorComboBox4 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getForegroundColorComboBox4() {
	if (ivjForegroundColorComboBox4 == null) {
		try {
			ivjForegroundColorComboBox4 = new javax.swing.JComboBox();
			ivjForegroundColorComboBox4.setName("ForegroundColorComboBox4");
			ivjForegroundColorComboBox4.setPreferredSize(new java.awt.Dimension(120, 25));
			ivjForegroundColorComboBox4.setFont(new java.awt.Font("dialog", 0, 12));
			ivjForegroundColorComboBox4.setMinimumSize(new java.awt.Dimension(120, 25));
			ivjForegroundColorComboBox4.setEnabled(false);
			// user code begin {1}
			ivjForegroundColorComboBox4.setRenderer(new com.cannontech.common.gui.util.ColorComboBoxCellRenderer());
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjForegroundColorComboBox4;
}
/**
 * Return the ForegroundColorComboBox5 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getForegroundColorComboBox5() {
	if (ivjForegroundColorComboBox5 == null) {
		try {
			ivjForegroundColorComboBox5 = new javax.swing.JComboBox();
			ivjForegroundColorComboBox5.setName("ForegroundColorComboBox5");
			ivjForegroundColorComboBox5.setPreferredSize(new java.awt.Dimension(120, 25));
			ivjForegroundColorComboBox5.setFont(new java.awt.Font("dialog", 0, 12));
			ivjForegroundColorComboBox5.setMinimumSize(new java.awt.Dimension(120, 25));
			ivjForegroundColorComboBox5.setEnabled(false);
			// user code begin {1}
			ivjForegroundColorComboBox5.setRenderer(new com.cannontech.common.gui.util.ColorComboBoxCellRenderer());
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjForegroundColorComboBox5;
}
/**
 * Return the ForegroundColorComboBox6 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getForegroundColorComboBox6() {
	if (ivjForegroundColorComboBox6 == null) {
		try {
			ivjForegroundColorComboBox6 = new javax.swing.JComboBox();
			ivjForegroundColorComboBox6.setName("ForegroundColorComboBox6");
			ivjForegroundColorComboBox6.setPreferredSize(new java.awt.Dimension(120, 25));
			ivjForegroundColorComboBox6.setFont(new java.awt.Font("dialog", 0, 12));
			ivjForegroundColorComboBox6.setMinimumSize(new java.awt.Dimension(120, 25));
			ivjForegroundColorComboBox6.setEnabled(false);
			// user code begin {1}
			ivjForegroundColorComboBox6.setRenderer(new com.cannontech.common.gui.util.ColorComboBoxCellRenderer());
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjForegroundColorComboBox6;
}
/**
 * Return the ForegroundColorComboBox8 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getForegroundColorComboBox7() {
	if (ivjForegroundColorComboBox7 == null) {
		try {
			ivjForegroundColorComboBox7 = new javax.swing.JComboBox();
			ivjForegroundColorComboBox7.setName("ForegroundColorComboBox7");
			ivjForegroundColorComboBox7.setAutoscrolls(false);
			ivjForegroundColorComboBox7.setDoubleBuffered(false);
			ivjForegroundColorComboBox7.setPreferredSize(new java.awt.Dimension(120, 25));
			ivjForegroundColorComboBox7.setFont(new java.awt.Font("dialog", 0, 12));
			ivjForegroundColorComboBox7.setMinimumSize(new java.awt.Dimension(120, 25));
			ivjForegroundColorComboBox7.setEnabled(false);
			// user code begin {1}
			ivjForegroundColorComboBox7.setRenderer(new com.cannontech.common.gui.util.ColorComboBoxCellRenderer());
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjForegroundColorComboBox7;
}
/**
 * Return the ForegroundColorComboBox8 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getForegroundColorComboBox8() {
	if (ivjForegroundColorComboBox8 == null) {
		try {
			ivjForegroundColorComboBox8 = new javax.swing.JComboBox();
			ivjForegroundColorComboBox8.setName("ForegroundColorComboBox8");
			ivjForegroundColorComboBox8.setPreferredSize(new java.awt.Dimension(120, 25));
			ivjForegroundColorComboBox8.setFont(new java.awt.Font("dialog", 0, 12));
			ivjForegroundColorComboBox8.setMinimumSize(new java.awt.Dimension(120, 25));
			ivjForegroundColorComboBox8.setEnabled(false);
			// user code begin {1}
			ivjForegroundColorComboBox8.setRenderer(new com.cannontech.common.gui.util.ColorComboBoxCellRenderer());
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjForegroundColorComboBox8;
}
/**
 * Return the ForegroundColorComboBox92 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getForegroundColorComboBox9() {
	if (ivjForegroundColorComboBox9 == null) {
		try {
			ivjForegroundColorComboBox9 = new javax.swing.JComboBox();
			ivjForegroundColorComboBox9.setName("ForegroundColorComboBox9");
			ivjForegroundColorComboBox9.setPreferredSize(new java.awt.Dimension(120, 25));
			ivjForegroundColorComboBox9.setFont(new java.awt.Font("dialog", 0, 12));
			ivjForegroundColorComboBox9.setMinimumSize(new java.awt.Dimension(120, 25));
			ivjForegroundColorComboBox9.setEnabled(false);
			// user code begin {1}
			ivjForegroundColorComboBox9.setRenderer(new com.cannontech.common.gui.util.ColorComboBoxCellRenderer());
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjForegroundColorComboBox9;
}
/**
 * Return the IdentificationPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getIdentificationPanel() {
	if (ivjIdentificationPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitlePosition(2);
			ivjLocalBorder.setTitle("Identification");
			ivjIdentificationPanel = new javax.swing.JPanel();
			ivjIdentificationPanel.setName("IdentificationPanel");
			ivjIdentificationPanel.setBorder(ivjLocalBorder);
			ivjIdentificationPanel.setLayout(null);
			getIdentificationPanel().add(getStateGroupNameLabel(), getStateGroupNameLabel().getName());
			getIdentificationPanel().add(getStateGroupNameTextField(), getStateGroupNameTextField().getName());
			getIdentificationPanel().add(getStateNumberLabel(), getStateNumberLabel().getName());
			getIdentificationPanel().add(getStateNumberSpinner(), getStateNumberSpinner().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjIdentificationPanel;
}
/**
 * Return the JScrollPane property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane() {
	if (ivjJScrollPane == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder1.setTitlePosition(2);
			ivjLocalBorder1.setTitle("Configuration");
			ivjJScrollPane = new javax.swing.JScrollPane();
			ivjJScrollPane.setName("JScrollPane");
			ivjJScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPane.setBorder(ivjLocalBorder1);
			ivjJScrollPane.setDoubleBuffered(false);
			ivjJScrollPane.setPreferredSize(new java.awt.Dimension(383, 235));
			ivjJScrollPane.setMinimumSize(new java.awt.Dimension(383, 235));
			getJScrollPane().setViewportView(getStatesPanel());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane;
}
/**
 * Return the RepeaterLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getRawStateColumnLabel() {
	if (ivjRawStateColumnLabel == null) {
		try {
			ivjRawStateColumnLabel = new javax.swing.JLabel();
			ivjRawStateColumnLabel.setName("RawStateColumnLabel");
			ivjRawStateColumnLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjRawStateColumnLabel.setText("Raw State:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRawStateColumnLabel;
}
/**
 * Return the RawStateLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getRawStateLabel1() {
	if (ivjRawStateLabel1 == null) {
		try {
			ivjRawStateLabel1 = new javax.swing.JLabel();
			ivjRawStateLabel1.setName("RawStateLabel1");
			ivjRawStateLabel1.setFont(new java.awt.Font("dialog", 0, 14));
			ivjRawStateLabel1.setText("1 (Open)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRawStateLabel1;
}
/**
 * Return the RawStateLabel9 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getRawStateLabel10() {
	if (ivjRawStateLabel10 == null) {
		try {
			ivjRawStateLabel10 = new javax.swing.JLabel();
			ivjRawStateLabel10.setName("RawStateLabel10");
			ivjRawStateLabel10.setText("10");
			ivjRawStateLabel10.setMaximumSize(new java.awt.Dimension(18, 19));
			ivjRawStateLabel10.setFont(new java.awt.Font("dialog", 0, 14));
			ivjRawStateLabel10.setEnabled(false);
			ivjRawStateLabel10.setMinimumSize(new java.awt.Dimension(18, 19));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRawStateLabel10;
}
/**
 * Return the RawStateLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getRawStateLabel2() {
	if (ivjRawStateLabel2 == null) {
		try {
			ivjRawStateLabel2 = new javax.swing.JLabel();
			ivjRawStateLabel2.setName("RawStateLabel2");
			ivjRawStateLabel2.setFont(new java.awt.Font("dialog", 0, 14));
			ivjRawStateLabel2.setText("2 (Close)");
			ivjRawStateLabel2.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRawStateLabel2;
}
/**
 * Return the RawStateLabel4 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getRawStateLabel3() {
	if (ivjRawStateLabel3 == null) {
		try {
			ivjRawStateLabel3 = new javax.swing.JLabel();
			ivjRawStateLabel3.setName("RawStateLabel3");
			ivjRawStateLabel3.setFont(new java.awt.Font("dialog", 0, 14));
			ivjRawStateLabel3.setText("3");
			ivjRawStateLabel3.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRawStateLabel3;
}
/**
 * Return the RawStateLabel4 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getRawStateLabel4() {
	if (ivjRawStateLabel4 == null) {
		try {
			ivjRawStateLabel4 = new javax.swing.JLabel();
			ivjRawStateLabel4.setName("RawStateLabel4");
			ivjRawStateLabel4.setFont(new java.awt.Font("dialog", 0, 14));
			ivjRawStateLabel4.setText("4");
			ivjRawStateLabel4.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRawStateLabel4;
}
/**
 * Return the RawStateLabel5 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getRawStateLabel5() {
	if (ivjRawStateLabel5 == null) {
		try {
			ivjRawStateLabel5 = new javax.swing.JLabel();
			ivjRawStateLabel5.setName("RawStateLabel5");
			ivjRawStateLabel5.setFont(new java.awt.Font("dialog", 0, 14));
			ivjRawStateLabel5.setText("5");
			ivjRawStateLabel5.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRawStateLabel5;
}
/**
 * Return the RawStateLabel6 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getRawStateLabel6() {
	if (ivjRawStateLabel6 == null) {
		try {
			ivjRawStateLabel6 = new javax.swing.JLabel();
			ivjRawStateLabel6.setName("RawStateLabel6");
			ivjRawStateLabel6.setFont(new java.awt.Font("dialog", 0, 14));
			ivjRawStateLabel6.setText("6");
			ivjRawStateLabel6.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRawStateLabel6;
}
/**
 * Return the RawStateLabel8 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getRawStateLabel7() {
	if (ivjRawStateLabel7 == null) {
		try {
			ivjRawStateLabel7 = new javax.swing.JLabel();
			ivjRawStateLabel7.setName("RawStateLabel7");
			ivjRawStateLabel7.setAutoscrolls(false);
			ivjRawStateLabel7.setText("7");
			ivjRawStateLabel7.setDoubleBuffered(false);
			ivjRawStateLabel7.setFont(new java.awt.Font("dialog", 0, 14));
			ivjRawStateLabel7.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRawStateLabel7;
}
/**
 * Return the RawStateLabel8 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getRawStateLabel8() {
	if (ivjRawStateLabel8 == null) {
		try {
			ivjRawStateLabel8 = new javax.swing.JLabel();
			ivjRawStateLabel8.setName("RawStateLabel8");
			ivjRawStateLabel8.setFont(new java.awt.Font("dialog", 0, 14));
			ivjRawStateLabel8.setText("8");
			ivjRawStateLabel8.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRawStateLabel8;
}
/**
 * Return the RawStateLabel92 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getRawStateLabel9() {
	if (ivjRawStateLabel9 == null) {
		try {
			ivjRawStateLabel9 = new javax.swing.JLabel();
			ivjRawStateLabel9.setName("RawStateLabel9");
			ivjRawStateLabel9.setFont(new java.awt.Font("dialog", 0, 14));
			ivjRawStateLabel9.setText("9");
			ivjRawStateLabel9.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRawStateLabel9;
}
/**
 * Return the RepeaterLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getRepeaterLabel() {
	if (ivjRepeaterLabel == null) {
		try {
			ivjRepeaterLabel = new javax.swing.JLabel();
			ivjRepeaterLabel.setName("RepeaterLabel");
			ivjRepeaterLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjRepeaterLabel.setText("State Text:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRepeaterLabel;
}
/**
 * Return the StateGroupNameLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getStateGroupNameLabel() {
	if (ivjStateGroupNameLabel == null) {
		try {
			ivjStateGroupNameLabel = new javax.swing.JLabel();
			ivjStateGroupNameLabel.setName("StateGroupNameLabel");
			ivjStateGroupNameLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjStateGroupNameLabel.setText("State Group Name:");
			ivjStateGroupNameLabel.setBounds(10, 25, 121, 19);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStateGroupNameLabel;
}
/**
 * Return the StateGroupNameTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getStateGroupNameTextField() {
	if (ivjStateGroupNameTextField == null) {
		try {
			ivjStateGroupNameTextField = new javax.swing.JTextField();
			ivjStateGroupNameTextField.setName("StateGroupNameTextField");
			ivjStateGroupNameTextField.setPreferredSize(new java.awt.Dimension(150, 21));
			ivjStateGroupNameTextField.setBounds(141, 24, 150, 21);
			ivjStateGroupNameTextField.setMinimumSize(new java.awt.Dimension(150, 21));
			// user code begin {1}
			ivjStateGroupNameTextField.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_STATE_GROUP_NAME_LENGTH));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStateGroupNameTextField;
}
/**
 * Return the StateNameTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getStateNameTextField1() {
	if (ivjStateNameTextField1 == null) {
		try {
			ivjStateNameTextField1 = new javax.swing.JTextField();
			ivjStateNameTextField1.setName("StateNameTextField1");
			ivjStateNameTextField1.setPreferredSize(new java.awt.Dimension(1300, 22));
			ivjStateNameTextField1.setMinimumSize(new java.awt.Dimension(130, 22));
			ivjStateNameTextField1.setColumns(0);
			// user code begin {1}
			ivjStateNameTextField1.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_STATE_NAME_LENGTH));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStateNameTextField1;
}
/**
 * Return the StateNameTextField9 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getStateNameTextField10() {
	if (ivjStateNameTextField10 == null) {
		try {
			ivjStateNameTextField10 = new javax.swing.JTextField();
			ivjStateNameTextField10.setName("StateNameTextField10");
			ivjStateNameTextField10.setPreferredSize(new java.awt.Dimension(1300, 22));
			ivjStateNameTextField10.setMinimumSize(new java.awt.Dimension(130, 22));
			ivjStateNameTextField10.setEnabled(false);
			ivjStateNameTextField10.setColumns(0);
			// user code begin {1}
			ivjStateNameTextField10.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_STATE_NAME_LENGTH));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStateNameTextField10;
}
/**
 * Return the StateNameTextField2 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getStateNameTextField2() {
	if (ivjStateNameTextField2 == null) {
		try {
			ivjStateNameTextField2 = new javax.swing.JTextField();
			ivjStateNameTextField2.setName("StateNameTextField2");
			ivjStateNameTextField2.setPreferredSize(new java.awt.Dimension(1300, 22));
			ivjStateNameTextField2.setMinimumSize(new java.awt.Dimension(130, 22));
			ivjStateNameTextField2.setEnabled(false);
			ivjStateNameTextField2.setColumns(0);
			// user code begin {1}
			ivjStateNameTextField2.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_STATE_NAME_LENGTH));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStateNameTextField2;
}
/**
 * Return the StateNameTextField4 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getStateNameTextField3() {
	if (ivjStateNameTextField3 == null) {
		try {
			ivjStateNameTextField3 = new javax.swing.JTextField();
			ivjStateNameTextField3.setName("StateNameTextField3");
			ivjStateNameTextField3.setPreferredSize(new java.awt.Dimension(1300, 22));
			ivjStateNameTextField3.setMinimumSize(new java.awt.Dimension(130, 22));
			ivjStateNameTextField3.setEnabled(false);
			ivjStateNameTextField3.setColumns(0);
			// user code begin {1}
			ivjStateNameTextField3.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_STATE_NAME_LENGTH));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStateNameTextField3;
}
/**
 * Return the StateNameTextField4 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getStateNameTextField4() {
	if (ivjStateNameTextField4 == null) {
		try {
			ivjStateNameTextField4 = new javax.swing.JTextField();
			ivjStateNameTextField4.setName("StateNameTextField4");
			ivjStateNameTextField4.setPreferredSize(new java.awt.Dimension(1300, 22));
			ivjStateNameTextField4.setMinimumSize(new java.awt.Dimension(130, 22));
			ivjStateNameTextField4.setEnabled(false);
			ivjStateNameTextField4.setColumns(0);
			// user code begin {1}
			ivjStateNameTextField4.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_STATE_NAME_LENGTH));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStateNameTextField4;
}
/**
 * Return the StateNameTextField5 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getStateNameTextField5() {
	if (ivjStateNameTextField5 == null) {
		try {
			ivjStateNameTextField5 = new javax.swing.JTextField();
			ivjStateNameTextField5.setName("StateNameTextField5");
			ivjStateNameTextField5.setPreferredSize(new java.awt.Dimension(1300, 22));
			ivjStateNameTextField5.setMinimumSize(new java.awt.Dimension(130, 22));
			ivjStateNameTextField5.setEnabled(false);
			ivjStateNameTextField5.setColumns(0);
			// user code begin {1}
			ivjStateNameTextField5.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_STATE_NAME_LENGTH));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStateNameTextField5;
}
/**
 * Return the StateNameTextField6 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getStateNameTextField6() {
	if (ivjStateNameTextField6 == null) {
		try {
			ivjStateNameTextField6 = new javax.swing.JTextField();
			ivjStateNameTextField6.setName("StateNameTextField6");
			ivjStateNameTextField6.setPreferredSize(new java.awt.Dimension(1300, 22));
			ivjStateNameTextField6.setMinimumSize(new java.awt.Dimension(130, 22));
			ivjStateNameTextField6.setEnabled(false);
			ivjStateNameTextField6.setColumns(0);
			// user code begin {1}
			ivjStateNameTextField6.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_STATE_NAME_LENGTH));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStateNameTextField6;
}
/**
 * Return the StateNameTextField8 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getStateNameTextField7() {
	if (ivjStateNameTextField7 == null) {
		try {
			ivjStateNameTextField7 = new javax.swing.JTextField();
			ivjStateNameTextField7.setName("StateNameTextField7");
			ivjStateNameTextField7.setDoubleBuffered(false);
			ivjStateNameTextField7.setColumns(0);
			ivjStateNameTextField7.setPreferredSize(new java.awt.Dimension(1300, 22));
			ivjStateNameTextField7.setMinimumSize(new java.awt.Dimension(130, 22));
			ivjStateNameTextField7.setEnabled(false);
			// user code begin {1}
			ivjStateNameTextField7.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_STATE_NAME_LENGTH));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStateNameTextField7;
}
/**
 * Return the StateNameTextField8a property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getStateNameTextField8() {
	if (ivjStateNameTextField8 == null) {
		try {
			ivjStateNameTextField8 = new javax.swing.JTextField();
			ivjStateNameTextField8.setName("StateNameTextField8");
			ivjStateNameTextField8.setPreferredSize(new java.awt.Dimension(1300, 22));
			ivjStateNameTextField8.setMinimumSize(new java.awt.Dimension(130, 22));
			ivjStateNameTextField8.setEnabled(false);
			ivjStateNameTextField8.setColumns(0);
			// user code begin {1}
			ivjStateNameTextField8.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_STATE_NAME_LENGTH));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStateNameTextField8;
}
/**
 * Return the StateNameTextField92 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getStateNameTextField9() {
	if (ivjStateNameTextField9 == null) {
		try {
			ivjStateNameTextField9 = new javax.swing.JTextField();
			ivjStateNameTextField9.setName("StateNameTextField9");
			ivjStateNameTextField9.setPreferredSize(new java.awt.Dimension(1300, 22));
			ivjStateNameTextField9.setMinimumSize(new java.awt.Dimension(130, 22));
			ivjStateNameTextField9.setEnabled(false);
			ivjStateNameTextField9.setColumns(0);
			// user code begin {1}
			ivjStateNameTextField9.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_STATE_NAME_LENGTH));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStateNameTextField9;
}
/**
 * Return the StateNumberLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getStateNumberLabel() {
	if (ivjStateNumberLabel == null) {
		try {
			ivjStateNumberLabel = new javax.swing.JLabel();
			ivjStateNumberLabel.setName("StateNumberLabel");
			ivjStateNumberLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjStateNumberLabel.setText("Number of States:");
			ivjStateNumberLabel.setBounds(10, 52, 113, 19);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStateNumberLabel;
}
/**
 * Return the StateNumberSpinner property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getStateNumberSpinner() {
	if (ivjStateNumberSpinner == null) {
		try {
			ivjStateNumberSpinner = new com.klg.jclass.field.JCSpinField();
			ivjStateNumberSpinner.setName("StateNumberSpinner");
			ivjStateNumberSpinner.setPreferredSize(new java.awt.Dimension(50, 22));
			ivjStateNumberSpinner.setBounds(141, 51, 50, 22);
			ivjStateNumberSpinner.setMinimumSize(new java.awt.Dimension(50, 22));
			// user code begin {1}
			ivjStateNumberSpinner.setDataProperties(new com.klg.jclass.field.DataProperties(
				new com.klg.jclass.field.validate.JCIntegerValidator(
					null, new Integer(2), new Integer(STATE_COUNT), null, true, null, 
					new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, 
					new Integer(2)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, 
						new Integer(1)), new com.klg.jclass.field.JCInvalidInfo(true, 2, new java.awt.Color(0, 0, 0, 255), 
						new java.awt.Color(255, 255, 255, 255))));

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStateNumberSpinner;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getStatesPanel() {
	if (ivjStatesPanel == null) {
		try {
			ivjStatesPanel = new javax.swing.JPanel();
			ivjStatesPanel.setName("StatesPanel");
			ivjStatesPanel.setLayout(new java.awt.GridBagLayout());
			ivjStatesPanel.setMaximumSize(new java.awt.Dimension(353, 370));
			ivjStatesPanel.setPreferredSize(new java.awt.Dimension(353, 370));
			ivjStatesPanel.setBounds(0, 0, 352, 370);
			ivjStatesPanel.setMinimumSize(new java.awt.Dimension(353, 370));

			java.awt.GridBagConstraints constraintsRepeaterLabel = new java.awt.GridBagConstraints();
			constraintsRepeaterLabel.gridx = 2; constraintsRepeaterLabel.gridy = 1;
			constraintsRepeaterLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRepeaterLabel.insets = new java.awt.Insets(8, 6, 3, 73);
			getStatesPanel().add(getRepeaterLabel(), constraintsRepeaterLabel);

			java.awt.GridBagConstraints constraintsVariableBitsLabel = new java.awt.GridBagConstraints();
			constraintsVariableBitsLabel.gridx = 3; constraintsVariableBitsLabel.gridy = 1;
			constraintsVariableBitsLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsVariableBitsLabel.insets = new java.awt.Insets(8, 5, 3, 19);
			getStatesPanel().add(getVariableBitsLabel(), constraintsVariableBitsLabel);

			java.awt.GridBagConstraints constraintsRawStateColumnLabel = new java.awt.GridBagConstraints();
			constraintsRawStateColumnLabel.gridx = 1; constraintsRawStateColumnLabel.gridy = 1;
			constraintsRawStateColumnLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRawStateColumnLabel.insets = new java.awt.Insets(8, 10, 3, 6);
			getStatesPanel().add(getRawStateColumnLabel(), constraintsRawStateColumnLabel);

			java.awt.GridBagConstraints constraintsStateNameTextField1 = new java.awt.GridBagConstraints();
			constraintsStateNameTextField1.gridx = 2; constraintsStateNameTextField1.gridy = 2;
			constraintsStateNameTextField1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsStateNameTextField1.anchor = java.awt.GridBagConstraints.WEST;
			constraintsStateNameTextField1.weightx = 1.0;
			constraintsStateNameTextField1.ipadx = 6;
			constraintsStateNameTextField1.insets = new java.awt.Insets(4, 6, 5, 5);
			getStatesPanel().add(getStateNameTextField1(), constraintsStateNameTextField1);

			java.awt.GridBagConstraints constraintsRawStateLabel1 = new java.awt.GridBagConstraints();
			constraintsRawStateLabel1.gridx = 1; constraintsRawStateLabel1.gridy = 2;
			constraintsRawStateLabel1.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRawStateLabel1.ipadx = 6;
			constraintsRawStateLabel1.insets = new java.awt.Insets(6, 10, 6, 11);
			getStatesPanel().add(getRawStateLabel1(), constraintsRawStateLabel1);

			java.awt.GridBagConstraints constraintsForegroundColorComboBox1 = new java.awt.GridBagConstraints();
			constraintsForegroundColorComboBox1.gridx = 3; constraintsForegroundColorComboBox1.gridy = 2;
			constraintsForegroundColorComboBox1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsForegroundColorComboBox1.anchor = java.awt.GridBagConstraints.WEST;
			constraintsForegroundColorComboBox1.weightx = 1.0;
			constraintsForegroundColorComboBox1.ipadx = 6;
			constraintsForegroundColorComboBox1.insets = new java.awt.Insets(3, 5, 3, 10);
			getStatesPanel().add(getForegroundColorComboBox1(), constraintsForegroundColorComboBox1);

			java.awt.GridBagConstraints constraintsRawStateLabel2 = new java.awt.GridBagConstraints();
			constraintsRawStateLabel2.gridx = 1; constraintsRawStateLabel2.gridy = 3;
			constraintsRawStateLabel2.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRawStateLabel2.ipadx = 5;
			constraintsRawStateLabel2.insets = new java.awt.Insets(6, 10, 6, 11);
			getStatesPanel().add(getRawStateLabel2(), constraintsRawStateLabel2);

			java.awt.GridBagConstraints constraintsRawStateLabel3 = new java.awt.GridBagConstraints();
			constraintsRawStateLabel3.gridx = 1; constraintsRawStateLabel3.gridy = 4;
			constraintsRawStateLabel3.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRawStateLabel3.insets = new java.awt.Insets(6, 10, 6, 66);
			getStatesPanel().add(getRawStateLabel3(), constraintsRawStateLabel3);

			java.awt.GridBagConstraints constraintsRawStateLabel4 = new java.awt.GridBagConstraints();
			constraintsRawStateLabel4.gridx = 1; constraintsRawStateLabel4.gridy = 5;
			constraintsRawStateLabel4.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRawStateLabel4.insets = new java.awt.Insets(6, 10, 6, 66);
			getStatesPanel().add(getRawStateLabel4(), constraintsRawStateLabel4);

			java.awt.GridBagConstraints constraintsRawStateLabel5 = new java.awt.GridBagConstraints();
			constraintsRawStateLabel5.gridx = 1; constraintsRawStateLabel5.gridy = 6;
			constraintsRawStateLabel5.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRawStateLabel5.insets = new java.awt.Insets(6, 10, 5, 66);
			getStatesPanel().add(getRawStateLabel5(), constraintsRawStateLabel5);

			java.awt.GridBagConstraints constraintsRawStateLabel6 = new java.awt.GridBagConstraints();
			constraintsRawStateLabel6.gridx = 1; constraintsRawStateLabel6.gridy = 7;
			constraintsRawStateLabel6.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRawStateLabel6.insets = new java.awt.Insets(7, 10, 5, 66);
			getStatesPanel().add(getRawStateLabel6(), constraintsRawStateLabel6);

			java.awt.GridBagConstraints constraintsStateNameTextField2 = new java.awt.GridBagConstraints();
			constraintsStateNameTextField2.gridx = 2; constraintsStateNameTextField2.gridy = 3;
			constraintsStateNameTextField2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsStateNameTextField2.anchor = java.awt.GridBagConstraints.WEST;
			constraintsStateNameTextField2.weightx = 1.0;
			constraintsStateNameTextField2.ipadx = 6;
			constraintsStateNameTextField2.insets = new java.awt.Insets(4, 6, 5, 5);
			getStatesPanel().add(getStateNameTextField2(), constraintsStateNameTextField2);

			java.awt.GridBagConstraints constraintsStateNameTextField3 = new java.awt.GridBagConstraints();
			constraintsStateNameTextField3.gridx = 2; constraintsStateNameTextField3.gridy = 4;
			constraintsStateNameTextField3.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsStateNameTextField3.anchor = java.awt.GridBagConstraints.WEST;
			constraintsStateNameTextField3.weightx = 1.0;
			constraintsStateNameTextField3.ipadx = 6;
			constraintsStateNameTextField3.insets = new java.awt.Insets(4, 6, 5, 5);
			getStatesPanel().add(getStateNameTextField3(), constraintsStateNameTextField3);

			java.awt.GridBagConstraints constraintsStateNameTextField4 = new java.awt.GridBagConstraints();
			constraintsStateNameTextField4.gridx = 2; constraintsStateNameTextField4.gridy = 5;
			constraintsStateNameTextField4.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsStateNameTextField4.anchor = java.awt.GridBagConstraints.WEST;
			constraintsStateNameTextField4.weightx = 1.0;
			constraintsStateNameTextField4.ipadx = 6;
			constraintsStateNameTextField4.insets = new java.awt.Insets(4, 6, 5, 5);
			getStatesPanel().add(getStateNameTextField4(), constraintsStateNameTextField4);

			java.awt.GridBagConstraints constraintsStateNameTextField5 = new java.awt.GridBagConstraints();
			constraintsStateNameTextField5.gridx = 2; constraintsStateNameTextField5.gridy = 6;
			constraintsStateNameTextField5.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsStateNameTextField5.anchor = java.awt.GridBagConstraints.WEST;
			constraintsStateNameTextField5.weightx = 1.0;
			constraintsStateNameTextField5.ipadx = 6;
			constraintsStateNameTextField5.insets = new java.awt.Insets(4, 6, 4, 5);
			getStatesPanel().add(getStateNameTextField5(), constraintsStateNameTextField5);

			java.awt.GridBagConstraints constraintsStateNameTextField6 = new java.awt.GridBagConstraints();
			constraintsStateNameTextField6.gridx = 2; constraintsStateNameTextField6.gridy = 7;
			constraintsStateNameTextField6.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsStateNameTextField6.anchor = java.awt.GridBagConstraints.WEST;
			constraintsStateNameTextField6.weightx = 1.0;
			constraintsStateNameTextField6.ipadx = 6;
			constraintsStateNameTextField6.insets = new java.awt.Insets(5, 6, 4, 5);
			getStatesPanel().add(getStateNameTextField6(), constraintsStateNameTextField6);

			java.awt.GridBagConstraints constraintsForegroundColorComboBox2 = new java.awt.GridBagConstraints();
			constraintsForegroundColorComboBox2.gridx = 3; constraintsForegroundColorComboBox2.gridy = 3;
			constraintsForegroundColorComboBox2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsForegroundColorComboBox2.anchor = java.awt.GridBagConstraints.WEST;
			constraintsForegroundColorComboBox2.weightx = 1.0;
			constraintsForegroundColorComboBox2.ipadx = 6;
			constraintsForegroundColorComboBox2.insets = new java.awt.Insets(3, 5, 3, 10);
			getStatesPanel().add(getForegroundColorComboBox2(), constraintsForegroundColorComboBox2);

			java.awt.GridBagConstraints constraintsForegroundColorComboBox3 = new java.awt.GridBagConstraints();
			constraintsForegroundColorComboBox3.gridx = 3; constraintsForegroundColorComboBox3.gridy = 4;
			constraintsForegroundColorComboBox3.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsForegroundColorComboBox3.anchor = java.awt.GridBagConstraints.WEST;
			constraintsForegroundColorComboBox3.weightx = 1.0;
			constraintsForegroundColorComboBox3.ipadx = 6;
			constraintsForegroundColorComboBox3.insets = new java.awt.Insets(3, 5, 3, 10);
			getStatesPanel().add(getForegroundColorComboBox3(), constraintsForegroundColorComboBox3);

			java.awt.GridBagConstraints constraintsForegroundColorComboBox4 = new java.awt.GridBagConstraints();
			constraintsForegroundColorComboBox4.gridx = 3; constraintsForegroundColorComboBox4.gridy = 5;
			constraintsForegroundColorComboBox4.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsForegroundColorComboBox4.anchor = java.awt.GridBagConstraints.WEST;
			constraintsForegroundColorComboBox4.weightx = 1.0;
			constraintsForegroundColorComboBox4.ipadx = 6;
			constraintsForegroundColorComboBox4.insets = new java.awt.Insets(3, 5, 3, 10);
			getStatesPanel().add(getForegroundColorComboBox4(), constraintsForegroundColorComboBox4);

			java.awt.GridBagConstraints constraintsForegroundColorComboBox5 = new java.awt.GridBagConstraints();
			constraintsForegroundColorComboBox5.gridx = 3; constraintsForegroundColorComboBox5.gridy = 6;
			constraintsForegroundColorComboBox5.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsForegroundColorComboBox5.anchor = java.awt.GridBagConstraints.WEST;
			constraintsForegroundColorComboBox5.weightx = 1.0;
			constraintsForegroundColorComboBox5.ipadx = 6;
			constraintsForegroundColorComboBox5.insets = new java.awt.Insets(3, 5, 2, 10);
			getStatesPanel().add(getForegroundColorComboBox5(), constraintsForegroundColorComboBox5);

			java.awt.GridBagConstraints constraintsForegroundColorComboBox6 = new java.awt.GridBagConstraints();
			constraintsForegroundColorComboBox6.gridx = 3; constraintsForegroundColorComboBox6.gridy = 7;
			constraintsForegroundColorComboBox6.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsForegroundColorComboBox6.anchor = java.awt.GridBagConstraints.WEST;
			constraintsForegroundColorComboBox6.weightx = 1.0;
			constraintsForegroundColorComboBox6.ipadx = 6;
			constraintsForegroundColorComboBox6.insets = new java.awt.Insets(2, 5, 4, 10);
			getStatesPanel().add(getForegroundColorComboBox6(), constraintsForegroundColorComboBox6);

			java.awt.GridBagConstraints constraintsRawStateLabel10 = new java.awt.GridBagConstraints();
			constraintsRawStateLabel10.gridx = 1; constraintsRawStateLabel10.gridy = 11;
			constraintsRawStateLabel10.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRawStateLabel10.ipadx = 4;
			constraintsRawStateLabel10.insets = new java.awt.Insets(7, 10, 33, 52);
			getStatesPanel().add(getRawStateLabel10(), constraintsRawStateLabel10);

			java.awt.GridBagConstraints constraintsStateNameTextField10 = new java.awt.GridBagConstraints();
			constraintsStateNameTextField10.gridx = 2; constraintsStateNameTextField10.gridy = 11;
			constraintsStateNameTextField10.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsStateNameTextField10.anchor = java.awt.GridBagConstraints.WEST;
			constraintsStateNameTextField10.weightx = 1.0;
			constraintsStateNameTextField10.ipadx = 6;
			constraintsStateNameTextField10.insets = new java.awt.Insets(4, 7, 33, 4);
			getStatesPanel().add(getStateNameTextField10(), constraintsStateNameTextField10);

			java.awt.GridBagConstraints constraintsForegroundColorComboBox10 = new java.awt.GridBagConstraints();
			constraintsForegroundColorComboBox10.gridx = 3; constraintsForegroundColorComboBox10.gridy = 11;
			constraintsForegroundColorComboBox10.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsForegroundColorComboBox10.anchor = java.awt.GridBagConstraints.WEST;
			constraintsForegroundColorComboBox10.weightx = 1.0;
			constraintsForegroundColorComboBox10.ipadx = 6;
			constraintsForegroundColorComboBox10.insets = new java.awt.Insets(3, 5, 31, 10);
			getStatesPanel().add(getForegroundColorComboBox10(), constraintsForegroundColorComboBox10);

			java.awt.GridBagConstraints constraintsForegroundColorComboBox9 = new java.awt.GridBagConstraints();
			constraintsForegroundColorComboBox9.gridx = 3; constraintsForegroundColorComboBox9.gridy = 10;
			constraintsForegroundColorComboBox9.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsForegroundColorComboBox9.anchor = java.awt.GridBagConstraints.WEST;
			constraintsForegroundColorComboBox9.weightx = 1.0;
			constraintsForegroundColorComboBox9.ipadx = 6;
			constraintsForegroundColorComboBox9.insets = new java.awt.Insets(1, 5, 3, 10);
			getStatesPanel().add(getForegroundColorComboBox9(), constraintsForegroundColorComboBox9);

			java.awt.GridBagConstraints constraintsStateNameTextField9 = new java.awt.GridBagConstraints();
			constraintsStateNameTextField9.gridx = 2; constraintsStateNameTextField9.gridy = 10;
			constraintsStateNameTextField9.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsStateNameTextField9.anchor = java.awt.GridBagConstraints.WEST;
			constraintsStateNameTextField9.weightx = 1.0;
			constraintsStateNameTextField9.ipadx = 6;
			constraintsStateNameTextField9.insets = new java.awt.Insets(4, 6, 3, 5);
			getStatesPanel().add(getStateNameTextField9(), constraintsStateNameTextField9);

			java.awt.GridBagConstraints constraintsRawStateLabel9 = new java.awt.GridBagConstraints();
			constraintsRawStateLabel9.gridx = 1; constraintsRawStateLabel9.gridy = 10;
			constraintsRawStateLabel9.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRawStateLabel9.insets = new java.awt.Insets(6, 10, 4, 66);
			getStatesPanel().add(getRawStateLabel9(), constraintsRawStateLabel9);

			java.awt.GridBagConstraints constraintsRawStateLabel8 = new java.awt.GridBagConstraints();
			constraintsRawStateLabel8.gridx = 1; constraintsRawStateLabel8.gridy = 9;
			constraintsRawStateLabel8.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRawStateLabel8.insets = new java.awt.Insets(4, 10, 6, 66);
			getStatesPanel().add(getRawStateLabel8(), constraintsRawStateLabel8);

			java.awt.GridBagConstraints constraintsStateNameTextField8 = new java.awt.GridBagConstraints();
			constraintsStateNameTextField8.gridx = 2; constraintsStateNameTextField8.gridy = 9;
			constraintsStateNameTextField8.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsStateNameTextField8.anchor = java.awt.GridBagConstraints.WEST;
			constraintsStateNameTextField8.weightx = 1.0;
			constraintsStateNameTextField8.ipadx = 6;
			constraintsStateNameTextField8.insets = new java.awt.Insets(6, 6, 1, 5);
			getStatesPanel().add(getStateNameTextField8(), constraintsStateNameTextField8);

			java.awt.GridBagConstraints constraintsForegroundColorComboBox8 = new java.awt.GridBagConstraints();
			constraintsForegroundColorComboBox8.gridx = 3; constraintsForegroundColorComboBox8.gridy = 9;
			constraintsForegroundColorComboBox8.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsForegroundColorComboBox8.anchor = java.awt.GridBagConstraints.WEST;
			constraintsForegroundColorComboBox8.weightx = 1.0;
			constraintsForegroundColorComboBox8.ipadx = 6;
			constraintsForegroundColorComboBox8.insets = new java.awt.Insets(3, 5, 1, 10);
			getStatesPanel().add(getForegroundColorComboBox8(), constraintsForegroundColorComboBox8);

			java.awt.GridBagConstraints constraintsForegroundColorComboBox7 = new java.awt.GridBagConstraints();
			constraintsForegroundColorComboBox7.gridx = 3; constraintsForegroundColorComboBox7.gridy = 8;
			constraintsForegroundColorComboBox7.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsForegroundColorComboBox7.anchor = java.awt.GridBagConstraints.WEST;
			constraintsForegroundColorComboBox7.weightx = 1.0;
			constraintsForegroundColorComboBox7.ipadx = 6;
			constraintsForegroundColorComboBox7.insets = new java.awt.Insets(5, 5, 3, 10);
			getStatesPanel().add(getForegroundColorComboBox7(), constraintsForegroundColorComboBox7);

			java.awt.GridBagConstraints constraintsStateNameTextField7 = new java.awt.GridBagConstraints();
			constraintsStateNameTextField7.gridx = 2; constraintsStateNameTextField7.gridy = 8;
			constraintsStateNameTextField7.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsStateNameTextField7.anchor = java.awt.GridBagConstraints.WEST;
			constraintsStateNameTextField7.weightx = 1.0;
			constraintsStateNameTextField7.ipadx = 6;
			constraintsStateNameTextField7.insets = new java.awt.Insets(8, 6, 3, 5);
			getStatesPanel().add(getStateNameTextField7(), constraintsStateNameTextField7);

			java.awt.GridBagConstraints constraintsRawStateLabel7 = new java.awt.GridBagConstraints();
			constraintsRawStateLabel7.gridx = 1; constraintsRawStateLabel7.gridy = 8;
			constraintsRawStateLabel7.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRawStateLabel7.insets = new java.awt.Insets(6, 10, 8, 66);
			getStatesPanel().add(getRawStateLabel7(), constraintsRawStateLabel7);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStatesPanel;
}
/**
 * getValue method comment.
 */
public Object getValue(Object val) 
{
	com.cannontech.database.data.state.GroupState gs = (com.cannontech.database.data.state.GroupState)val;

	Object stateNumberSpinVal = getStateNumberSpinner().getValue();
	Integer numberOfStates = null;
	if( stateNumberSpinVal instanceof Long )
		numberOfStates = new Integer( ((Long)stateNumberSpinVal).intValue() );
	else if( stateNumberSpinVal instanceof Integer )
		numberOfStates = new Integer( ((Integer)stateNumberSpinVal).intValue() );

	String stateGroupName = getStateGroupNameTextField().getText();
	if( stateGroupName != null )
		gs.getStateGroup().setName(stateGroupName);
	
	gs.getStatesVector().removeAllElements();
	com.cannontech.database.db.state.State tempState = null;
	for(int i=0;i<numberOfStates.intValue();i++)
	{
		tempState = new com.cannontech.database.db.state.State(	gs.getStateGroup().getStateGroupID(),
												new Integer(i),
												stateNameTextFields[i].getText(),
												new Integer(foregroundColorComboBoxes[i].getSelectedIndex()),
												new Integer(com.cannontech.common.gui.util.Colors.BLACK_ID) );
		gs.getStatesVector().add(tempState);
	}

	return val;
}
/**
 * Return the VariableBitsLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getVariableBitsLabel() {
	if (ivjVariableBitsLabel == null) {
		try {
			ivjVariableBitsLabel = new javax.swing.JLabel();
			ivjVariableBitsLabel.setName("VariableBitsLabel");
			ivjVariableBitsLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjVariableBitsLabel.setText("Foreground Color:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjVariableBitsLabel;
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
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}

	getStateNumberSpinner().addValueListener(this);
	
	// user code end
	getStateGroupNameTextField().addCaretListener(this);
	getStateNameTextField1().addCaretListener(this);
	getStateNameTextField2().addCaretListener(this);
	getStateNameTextField3().addCaretListener(this);
	getStateNameTextField4().addCaretListener(this);
	getStateNameTextField5().addCaretListener(this);
	getStateNameTextField6().addCaretListener(this);
	getForegroundColorComboBox1().addItemListener(this);
	getForegroundColorComboBox2().addItemListener(this);
	getForegroundColorComboBox3().addItemListener(this);
	getForegroundColorComboBox4().addItemListener(this);
	getForegroundColorComboBox5().addItemListener(this);
	getForegroundColorComboBox6().addItemListener(this);
	getStateNameTextField7().addCaretListener(this);
	getStateNameTextField8().addCaretListener(this);
	getStateNameTextField9().addCaretListener(this);
	getStateNameTextField10().addCaretListener(this);
	getForegroundColorComboBox7().addItemListener(this);
	getForegroundColorComboBox8().addItemListener(this);
	getForegroundColorComboBox9().addItemListener(this);
	getForegroundColorComboBox10().addItemListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("VersacomAddressingEditorPanel");
		setPreferredSize(new java.awt.Dimension(381, 348));
		setLayout(new java.awt.GridBagLayout());
		setSize(407, 348);
		setMinimumSize(new java.awt.Dimension(381, 348));
		setMaximumSize(new java.awt.Dimension(381, 348));

		java.awt.GridBagConstraints constraintsIdentificationPanel = new java.awt.GridBagConstraints();
		constraintsIdentificationPanel.gridx = 1; constraintsIdentificationPanel.gridy = 1;
		constraintsIdentificationPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsIdentificationPanel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsIdentificationPanel.weightx = 1.0;
		constraintsIdentificationPanel.weighty = 1.0;
		constraintsIdentificationPanel.ipadx = 397;
		constraintsIdentificationPanel.ipady = 83;
		constraintsIdentificationPanel.insets = new java.awt.Insets(9, 5, 1, 5);
		add(getIdentificationPanel(), constraintsIdentificationPanel);

		java.awt.GridBagConstraints constraintsJScrollPane = new java.awt.GridBagConstraints();
		constraintsJScrollPane.gridx = 1; constraintsJScrollPane.gridy = 2;
		constraintsJScrollPane.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPane.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJScrollPane.weightx = 1.0;
		constraintsJScrollPane.weighty = 1.0;
		constraintsJScrollPane.ipadx = 14;
		constraintsJScrollPane.insets = new java.awt.Insets(2, 5, 18, 5);
		add(getJScrollPane(), constraintsJScrollPane);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	rawStateLabels = new javax.swing.JLabel[STATE_COUNT];
	stateNameTextFields = new javax.swing.JTextField[STATE_COUNT];
	foregroundColorComboBoxes = new javax.swing.JComboBox[STATE_COUNT];
	
	rawStateLabels[0] = getRawStateLabel1();
	rawStateLabels[1] = getRawStateLabel2();
	rawStateLabels[2] = getRawStateLabel3();
	rawStateLabels[3] = getRawStateLabel4();
	rawStateLabels[4] = getRawStateLabel5();
	rawStateLabels[5] = getRawStateLabel6();
	rawStateLabels[6] = getRawStateLabel7();
	rawStateLabels[7] = getRawStateLabel8();
	rawStateLabels[8] = getRawStateLabel9();
	rawStateLabels[9] = getRawStateLabel10();
	
	stateNameTextFields[0] = getStateNameTextField1();
	stateNameTextFields[1] = getStateNameTextField2();
	stateNameTextFields[2] = getStateNameTextField3();
	stateNameTextFields[3] = getStateNameTextField4();
	stateNameTextFields[4] = getStateNameTextField5();
	stateNameTextFields[5] = getStateNameTextField6();
	stateNameTextFields[6] = getStateNameTextField7();
	stateNameTextFields[7] = getStateNameTextField8();
	stateNameTextFields[8] = getStateNameTextField9();
	stateNameTextFields[9] = getStateNameTextField10();
		
	foregroundColorComboBoxes[0] = getForegroundColorComboBox1();
	foregroundColorComboBoxes[1] = getForegroundColorComboBox2();
	foregroundColorComboBoxes[2] = getForegroundColorComboBox3();
	foregroundColorComboBoxes[3] = getForegroundColorComboBox4();
	foregroundColorComboBoxes[4] = getForegroundColorComboBox5();
	foregroundColorComboBoxes[5] = getForegroundColorComboBox6();
	foregroundColorComboBoxes[6] = getForegroundColorComboBox7();
	foregroundColorComboBoxes[7] = getForegroundColorComboBox8();
	foregroundColorComboBoxes[8] = getForegroundColorComboBox9();
	foregroundColorComboBoxes[9] = getForegroundColorComboBox10();

	for(int i=0;i<foregroundColorComboBoxes.length;i++)
	{
		foregroundColorComboBoxes[i].addItem(com.cannontech.common.gui.util.Colors.GREEN_STR_ID);
		foregroundColorComboBoxes[i].addItem(com.cannontech.common.gui.util.Colors.RED_STR_ID);
		foregroundColorComboBoxes[i].addItem(com.cannontech.common.gui.util.Colors.WHITE_STR_ID);
		foregroundColorComboBoxes[i].addItem(com.cannontech.common.gui.util.Colors.YELLOW_STR_ID);
		foregroundColorComboBoxes[i].addItem(com.cannontech.common.gui.util.Colors.BLUE_STR_ID);
		foregroundColorComboBoxes[i].addItem(com.cannontech.common.gui.util.Colors.CYAN_STR_ID);
		foregroundColorComboBoxes[i].addItem(com.cannontech.common.gui.util.Colors.BLACK_STR_ID);
		foregroundColorComboBoxes[i].addItem(com.cannontech.common.gui.util.Colors.ORANGE_STR_ID);
		foregroundColorComboBoxes[i].addItem(com.cannontech.common.gui.util.Colors.MAGENTA_STR_ID);
		foregroundColorComboBoxes[i].addItem(com.cannontech.common.gui.util.Colors.GRAY_STR_ID);
		foregroundColorComboBoxes[i].addItem(com.cannontech.common.gui.util.Colors.PINK_STR_ID);
	}
	// user code end
}
/**
 * Method to handle events for the ItemListener interface.
 * @param e java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void itemStateChanged(java.awt.event.ItemEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getForegroundColorComboBox1()) 
		connEtoC9(e);
	if (e.getSource() == getForegroundColorComboBox2()) 
		connEtoC10(e);
	if (e.getSource() == getForegroundColorComboBox3()) 
		connEtoC11(e);
	if (e.getSource() == getForegroundColorComboBox4()) 
		connEtoC12(e);
	if (e.getSource() == getForegroundColorComboBox5()) 
		connEtoC13(e);
	if (e.getSource() == getForegroundColorComboBox6()) 
		connEtoC14(e);
	if (e.getSource() == getForegroundColorComboBox7()) 
		connEtoC18(e);
	if (e.getSource() == getForegroundColorComboBox8()) 
		connEtoC19(e);
	if (e.getSource() == getForegroundColorComboBox9()) 
		connEtoC20(e);
	if (e.getSource() == getForegroundColorComboBox10()) 
		connEtoC21(e);
	// user code begin {2}
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		GroupStateEditorPanel aGroupStateEditorPanel;
		aGroupStateEditorPanel = new GroupStateEditorPanel();
		frame.setContentPane(aGroupStateEditorPanel);
		frame.setSize(aGroupStateEditorPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		exception.printStackTrace(System.out);
	}
}
/**
 * setValue method comment.
 */
public void setValue(Object val) {
	com.cannontech.database.data.state.GroupState gs = (com.cannontech.database.data.state.GroupState)val;
	
	String groupName = gs.getStateGroup().getName();
	java.util.Vector statesVector = gs.getStatesVector();
	Integer numberOfStates = new Integer(statesVector.size());

	if( groupName != null )
		getStateGroupNameTextField().setText(groupName);

	if( numberOfStates != null )
	{
		getStateNumberSpinner().setValue(numberOfStates);

		for(int i=0;i<numberOfStates.intValue();i++)
		{
			rawStateLabels[i].setEnabled(true);
			stateNameTextFields[i].setEnabled(true);
			stateNameTextFields[i].setText( ((com.cannontech.database.db.state.State)statesVector.get(i)).getText() );
			foregroundColorComboBoxes[i].setEnabled(true);
			foregroundColorComboBoxes[i].setSelectedIndex( ((com.cannontech.database.db.state.State)statesVector.get(i)).getForegroundColor().intValue() );
		}
	}
}
/**
 * stateNumberSpinnerChanged:  (StateNumberSpinner.value.valueChanged(com.klg.jclass.util.value.JCValueEvent) --> GroupStateEditorPanel.fireInputUpdate()V)
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
private void stateNumberSpinnerChanged(com.klg.jclass.util.value.JCValueEvent arg1) 
{
	try 
	{
		this.fireInputUpdate();

		Object stateNumberSpinVal = getStateNumberSpinner().getValue();
		Integer numberOfStates = null;
		if( stateNumberSpinVal instanceof Long )
			numberOfStates = new Integer( ((Long)stateNumberSpinVal).intValue() );
		else if( stateNumberSpinVal instanceof Integer )
			numberOfStates = new Integer( ((Integer)stateNumberSpinVal).intValue() );

		if( numberOfStates != null )
		{
			for(int i=0;i<numberOfStates.intValue();i++)
			{
				rawStateLabels[i].setEnabled(true);
				stateNameTextFields[i].setEnabled(true);
				foregroundColorComboBoxes[i].setEnabled(true);
				if( stateNameTextFields[i].getText().equals("") )
					stateNameTextFields[i].setText("DefaultStateName" + (Integer.toString(i+1)) );
			}

			for(int i=numberOfStates.intValue();i<stateNameTextFields.length;i++)
			{
				rawStateLabels[i].setEnabled(false);
				stateNameTextFields[i].setEnabled(false);
				foregroundColorComboBoxes[i].setEnabled(false);
				if( stateNameTextFields[i].getText().equals("DefaultStateName" + (Integer.toString(i+1))) )
					stateNameTextFields[i].setText("");
			}
		}
	} 
	catch (java.lang.Throwable ivjExc) 
	{
		handleException(ivjExc);
	}

}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
public void valueChanged(JCValueEvent argValue) 
{
	if (argValue.getSource() == getStateNumberSpinner())
	{
		stateNumberSpinnerChanged(argValue);
	}
}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
public void valueChanging(JCValueEvent argValue) 
{
	
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GC2F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E16CFD8DD8D5D556BFD3D4CCC3C5C6C58D159595CD54D0D151C4C5F5A9AAAAAAACAAAAACAAEA18099A262C282E0AC80ACA0A0ACAC9C509054647C5D051D0D0D13198C525068A9494728A88973CA86AFF6D3D4FDA677BDE40FC1F777FBCEFCF4B4B7A6D3556DEEB1FFD7659DFE79FEB7642511346CF4836DA47CF32CA7F158365D5CE304E6E59F5B5E201ED16F42B623F4F00BE36462FCB035FD0C8733E736E
	74ACEBD0E38670D140FB345D1DBE977E1EE5752908DFB941EAB54AE7BC68C2DA5AD3072EF41DCFE2B566B3197341F3B261F7BAD0B4E06CB7F70F85720F1C13A361A9920EB49EDC306EE5B2AF4E491530E650F1820581C56F917209707B914F7914959512DC5359232DA98FAC1AB00D59D158C42A5DABF46D52EF1E752F63335BD9DE19F3B3A51DGC80B83E241CF34DA2631B22E2C5F37F7756E2E7DCBF737F4F6F6F5BA5AF637AFEDEB6DF0F475AC6DF534B85AD62C58505355573D0D7D3D0E6375AD1DEDF62B8F
	375DB0B7035B32DA9B014F6DE0D8F69F4BAF8678A420348E441F61F8B37C6D8472694098D7AD2A14E2E8BC7CD7EB48131D93E718449858A1E20B6E10E3746DFBF772BCFEBD8A5631F24F7C4D12DE84D483548E6485948834005703EFEB3E065FB53B9C55DD7B77F7F52E696EDEEBEF6D5952555AE6073FD72C0094895B5A363B2B2755EA9557FA665CC283FFDC11504B9B361EA81340D23C6EC1DB2F34465D38F942E403B2098BAC1A907E1BA80B585F64B291F97FE81A77385F5475D21D77CE2BB26F00D33BA6CE
	B248BBE44C6289197B24BC7749F9576FE3575A0BDF6BAA78FB3CD4BF84BE0A633E8ADC5451799CE7FCG24E55831EEB4CEC7DF6A52AE34E695579916035F5DEBA6586D4287CF3B56174987196DE03B48739B7265CB0EE76D17F1614BD86E8B633BA1AD2A5312AE6AD77186F64F96F4C35D82EA82F2G79828583D93A310EB9EB3E9DE29D2BEF69F12C6D6B30373675D805EE713AB9705B66F47434EC6C047B2FF3F75B0E161E0E16DD7636DE2BA433E29DAB03DDAA3D6FF85D5F8DF9ECE86BEC6BE9F1F4F4F50275
	167DED0E361EDEA1D33B6EE3A6535346D39BBA7637C1867B3B2DE837FE5DAC78353774BA36F737425DAECB087446F56C1A74376CG0BE0G7E966572CBADAB2F8408BF95A873G1643635C5E66369EE8AED62C286B6A5A57575D40A231B69DE0655C4D5B0EBC783B62G1671CF9B99B69A78D032B3E57DCC051D1A0E5D4C7D161E813C474E6CE36DF254812CB37FEC44BAD3B36D8AEB6118CD06F5065F8787C4DD496A515EBBFE5CA673CB3A0F7B9ACD6E252883467752705B0A6526F9CCBCCE7B5369DAC5EEE257
	2A8262EDGF274E0992FDB1FA76A6C5A165EB62C25BF74C085EBEB15CBDC78B96DE003D41E13A45FD939CF85E24F2699DC065934DAE7834D1572E679307B62932005D671CC7D8268CB20C5C00B299C3E61763D41379020B82034DECBFAA1D08350A0D0GD094D08AD02E836EC7A0BB10AFD098D082D0E61FA55D86548464899454075772153E7A71F2FD98EE8C2E7C47327E76F738DE7F3F11A85B557D2CEC313E8E37EC4D4A759BC5BE1F7CAF0875FF1A44BD3AF283AB3B367E36CEC7CDDBF7DBE7AB7C51AB252D
	5D404AD8FAEE6C69A21DDA0EF743F3269A9A76DE423F5F402EC10B435153312B4F51362D4D6168685CAB276F60F975766D6A169FD118574F9B58F56DBA884F99125F350135513B3BBABB5BF8E3C1AAD2FAABFFC634595B76035355DDFD1D8E017FD64B5A59FDED8352B32A3316F53C7BDB6CFD6CF9A77A2941AD2C6EF477F46C07A608BDB22D957D6C79F15A5C9CC59945407D4D7A5B191016570F4F0203DF6033A07F50255670DF3F1CE05417713B22E602C73FE847FD7B65E7017069570D463E3771E71504778A
	DF8F58D87DE4CF489668D32FE9ED6DE96B6D0512D5761C31AD9EDF476C06C0FE11C0717D58960F5D485271F9A7B54255DDF63BA8DAAA7307B7E531EB54F6202F8D3A896C413A194AF14C26191A349DD2190A74E7B731B235AB1E495E8779B379A00ECB4631B2862C6820A8D77BC1D1AEC2EE359365E201CFB615FB1364321C16748227195C1583A8D78BB24D26F2BF9DC2B98F1071B3151BFE9865C2C1A65AD46EEE12CB86190C81B3397EC114ABG19AA206681A33965C7D04E9F52C3CC657EC1F2B1101EE4AAF7
	69D1144BBCE4C94FBFE4A6377D984A5500CC13295CC7D6149B8499DFD3395BC9AE84E42200128E9949F90C27F2B98C65F258CC4EB601463BA063B415CBA4B9BF1089B615731F08F251A0138814B1E8A477EB9A158B24B78D1A494D1AC46582693E26F27711DC8824C7994809F6E0722FEC6CE5B4365E5B5731344F5141FA32AD8EE82C3B7B9C3CD9906369B7F81F185D1BC903B8EE7EE08AEBA7720FD852AB019A0EE05B7668B49CB77B6E9BED0D3FFC3E695C007D08704D67085C36651D0C63664DDBD81E511096
	FF845B20464D4A3E7B1A5E5E2E5D9DFC58A0359F136B14ED1F40B26B26406F1E0E1EDE47C6F8B8B9312D19D9477CE5638A891646D997707197AB2F94483770A80C210EB27C6C4F32953E3782668D948CB4115B33BAD97FEAF7CBCF1BE3A97FD78CD78AFEEB69EFF1AE6DBD884D6DD27E14DCDA4DD25731BF97CB574532F82670310D173C2439F8870EFDAF1BD5A5659BF55D05564AE333A6984DC5F84D78E4C266B1D11636E3F2198E5E10BEB1691777A60B7876BAB1BE8F1089BD0671EDF7EA63CB0634EC8FA02E
	37E0G758AGEB76C03D3AB2A3BDBF52FB047402GCBA03DEF7A0D74F27F00FAA79FC23DA2405A7F00FAFBCA0D74C613DE79E1548B81AC0974FEF19869651F04FAFFA73D8A403ACFC2BDDB0911DEA069DDB208FAE1002510DE21CD29D7F83250EBB8996BCB7A91D6DFBA9CED7B1732FF78CCD6F5FBCB675E36D6DEE716369CF4C895E6A324CB75C564396B8866598E76C61F0CF93E58EB14E76C49686B1C23281784D83EA76A4DF19869B5F9225E8324578DD8A0697D47D0AF1E740E9E23FB893042D3D06FB5C33D
	56D3D02F440AFACE4002C96F7BBEA33DC452FB1974D281331D0AFAD7EEB33A0676D371DAEC99BF12FBD764F975F83A1F40DEA865F9E11FD11E4927222FBFCFC03D3453208DBD8D75C28C6B1B63B4547BF8A26AF98196CEFA779A566FF12497C1FA19DEB0B675C23D13CA0D4AF4508B4B26B0ED647554AB8D737481FB111467A143BC2DFFC4DFABA721DEB6E0F5FFC43D3F159869F912DEB269798196CDFA9F9A6AE51C0EE36F37E8CEB277F451EE961DAE4F1F54405F0DC0F685A6666AFA26327CBCCE97F3F51388
	BF42F1DF85AE7CCB1B0A7E85C2DA661FF0FE69696F5918219BF8EF20G20B020B82054BF61336F423AD22B1B793B0D70AC6F69EC31EFD38D10F09EED8B3B1EB026117806AD4C8FBE263162B87912035A39599CEF75BC12CD438B3DEBF5FACBB5FC23C6CFBC773EACE07D965E9FD15CEAABD6ECEA00B1637A0EB6FB2B98D305CFD50E293A3D455C1CAF7C868209BECA64E79912BF615E5897B932DE5997891BD1B20175E18C675CA47FF24E101F1B05B4076FD94F7CEC04B48710B7D0A0D094D0A2D06619F80D1E5B
	324A5DB56277C7C3D7B5E035B04E29560ECD29CC77BBC7F6AD8E6B7434572270CC351EA8633C8AED995700DCBB506019D8465ECED6462CECFD4F44FB6733011179792DCECF6BE71446CF614349E55897CDA09F460C5007975D7A10FB16118F3B7A51875BD968439F8F0D4C0773F5FADA9F6C06BEDCD40ABEF8128F2B8E0F4C074DBABD2D8FE106BE1C6BC09FE24807B7C6684307EEFD4874B172E1DF897A106F03BEDCB1B8B29FEE5469E9FDE872B1EA47765A597DEF500EC1BB302BEBED17D372F5378D6F9F8779
	5AFEE4E43E766B74343E86991657FC3AE69672A1776848FC78DA272775A1555007AD64C3765968C37488FDF84E2D8FF5E79B7960C5BE34128F1E47C66643A81D1E5687FFC39FBE6BC39FC248078A6B48FCD82F53537A10E468038F7910B68AFDC89A218F6F3A7521F214118F4FEEE3BE30F9028672811E3FA37261F61D1E5607D0C39F9E25F208A69FF6CF98198F87F4FADA9FB24EB172616F5416651D03BE4C1DB8B29F3E5469E9FDE8B47461FD3AAF3A490787C76843D3EEFD88B47461D67EECE3F39C61644319
	E9A37341DF272775A15F5748076B6979DA690BBE2C1DB4B29F6AF5FADA9F9C06BE3CC175411BFCF8EB04BE24253B7341E26843B2AA07B8C897EB6453F29816FD2EA53D68DC449E1949309A609B898B4AE658A0703E04BDB41BE12140C793F64FB106A5G1FFAAE46691C4A302C73A00F7390BB19E3EC5E2D067AFED3261A2DCFA3B19B78F751F96A985D1148638B37F9A85742A78D43BEA3B1F6F871FB73F534573EB7DEC7CB7E5EF89D6D2D6FBF96B858D06E6640748F3EF73D4E167E0351BA5BA7BC4E7B674C60
	36FBFB5BFA79E006CD98CBE92FB8585A29EE3D6D7B167DEDD2994D32BEF170A3C9E61D232B3A580AFD28FFADE6638F03BE14E29EFA5CD49C63551E0773503B79FC2D8770FEC041D4D79E7A8C6BFB2441B50F83ACD9030B315E542DEC2EA06DFCCBFA9ED085D0BDD07779B846737F416DB8FC1D5345B87C4CFA736BFCEE3D71F57EF3BDFBB676F675756CEE53DE4B4B6AF3A8CD9ECD322998D66CC2669A2E6FE869595B262B8BB7F0FD11E626FFDB3DD9FD9157B440293C262CFC44187250C165185267FCB1EE8F03
	5F6873F1CC59D8014F7E4473711A3E4677BA5579D952DB01867D701A1ECDE3DBDFBF7DB58D84ACCC01C77B619CD6630052BF9B5D57DEE5E8AF41405EB840B2AF00FAF001B427FE815A4BBC2434D7C6767676233D5A8B74761AG6B867214527C48DE384A5EB232F7D9A95A8BB230978ED88CD0221416CA769C0715764A495E058E3417F5215EDEBEE095C0F5D2DA7305E82FE0D0E92F026CF51720BD0701BDAF407C010225B48B590BD359DBCE765AEDE8AF96E413B4B62DFF3624E77FD90D89FB179C1922ECD30A
	505EC20A37G74AAB53A75403782B9953830376D284ADEB15A5BCA763CC1E73446DE887011C0F1BAFB130F296C15203DB348DEB26824792B6D65GDF88D4652F35F71B5ADEA95A7B2A8F6DB500CE3B465EA070BEC081BAFBDDD615BD9B5ABB176C0502CE1446DEBC70A9C09997E96DFDB1DEF9FDCB687A3E304D6CB91AFB113E7E94C9D80DC1DAA3E0F6A08FDD5EAF0FD745D206F9BFC131789A588B84AC8CA8DACACB38886B6A0593D47616213DE42A7B638C6CE5DE8C6D86106DE211D6FBB15A7BFE224ADEB95A
	1BC07EB5DD2C37578D18A7101F1496C476A624296CD5203DBBCAD9D9079B580AB1401092A1ADD5131EF509A5BD9F286292EDD9C72873DE0EF95FCC6DD69D68B4DF223667G5E8B48DFE7EFE01252DE895D47EFD1D987030EC5E3AF9678A4A06BE82D3DA569AAFBF49F2FA47F32C127E0345ADEA57075C02DBAFB79B96CB9555D5336272D27272DF5DB47A1562F899E4D1ED92BF81F5889FFFB0E46EB7372CC36EF485EB2505567007674D2A6775FD9348F9F64B281338155DEAA1E0D22AFF47DC1565F5E5353329F
	9E335B3A5BF6DBABAF637D64EC548D8479B020B820D4205C4BB04F0459CC9794BB76776D679E8A7FEE18437C7384B99F12FD7A9817EDF1EAE45F647D71B0100BA459177B595A7BC656DD69585331DB5A4B4BF68E09749F76B1DB1B366D6E69325BD902D4566FCFD50EBBD240DE4665F23966DE2E2F0BE28D65C95E2FAB3ADC2C21FCC4F882476B9438D8DB6960B8635BA1AD6AF29CDBA470F50122GF82E85A0966844F1C9B3E1778F7072815EBFG7BFA33361A775566EFB56E2B65EDB56E13FF3555F51FFC69D6
	33BE16B447380E75318D7A470A9247B15B8D8731CF959C00FD2AFD652CFC322F00E78AD04D9558277A39C259A6B7815EFD05FCCDBC2FB0EEAB7C8C70A04042B53838E6D772EB93F30538E65504C7F33C75CA99175671F87F0F71A91016F5357A3A95DE83774BB508D5AD43EB59C4D8B21F1F8F825EC2D886DFB70885BE0930CBB8E68D02B28AC2CC4C6DB3FBC504F971F9DB8F607D887B931FAF8A82BE1C30E80E45G1FC858D1EEAF95781CEB91BBD20AF99492360B6B8E826FCB583DA518C7A0E1D339EE987051
	0409791284602D57A1666740BC3289BB135BEB85FE1030BBFAB18F9F42A6F25DG60C3893B1EE3D140A792F6DBB16611CA58455CDE5DDF217D7CABE23E451807133027B8668D7CE842CE637E05G9FCD58493D18C782E1AFF3DDDBB05C534108D5D39C4D04F9F14C813C97E14778357487BE1430E7C5EC404791D63A9D73CDA6CC4C57D6DDEFC9EF3C9E31E5DC578E3C97E1D15CBFFF600388DBB49B6D05935648634D3A811EC1B7A0F6A745D1C9D8874F239EF8BBE159DC5783F89F42C2A90FG4246F2399460B3
	EFC46CDD4AA30F30G1E078D78FA424E937585F887E14B66E09EDE042D6175AF9678E4424EF1E09EE9A1082D626517833C0DB0BB4F2396784690FD9EF64272C43D87BE063037A90FC44202F89E2940675C04589FF89E0540D792B60F7228A7ECB34FE3B4702104DDC0F9C49116564030F860D3895B48312C1B212FF4B3E223B8D681FC83E1AF74E03E6D045D436BCBA87051046573EB14G7CB8427E41E34B3C856A4BAD0835D0DBD2C9580D5CE7EF608388FB3A846D059236106BC6819FCF58E40E25G1F95CA77
	8235A57904A5F0FB8E603D89FB13FC9ECDD8B9578D81BE1230F9BC0EB8604791F6C1896611F92B3A6C1B00F79076AB4561C5D8G477C018FA6AC1E47E181BE0E30ED7DF85D12897363D85A5F60DE789B5DC793783DGFC8DE1B7B720FF0D04DDB64830C06043897BE19B7A97C358C35C17C46053EEC36CB00FAD877882427EC9F9D49266494B4A9B78G423EA925EBC958B4F1AD01CFA4ECAD4FA395784CB04426D09EF904093530EE603D887B194A591F302DBC0FE06023885B40F56301CFA6ACE99B6611F6BBE2
	67F33986606D046D26BCBCG733D1D3D5F3A70C776C896FDCA4B4CBC786DECBB18C7BB0DEABA7637F532973D8ACAAB0A8B73CAED950B45DE0F5187156F24D6CCA9B75CD34F478B370BBECA12749BF65A97D23F6364D978CEEA7E9D60639D589FAFEED66DD3E10E3097D0BBDA57366C2D633DFB516FFAF0D606246FFB87763B5E61B62300CFGCA3D837726B83F423DA9C1D7DEE82D7358EC38CFB63E21E4C2761D424F0ABB653DA901FD6FCE96F93E1A0DF9BAA0BD68CE064D1D2D5C6F444A725689E23EDF24EFD4
	2533FEA76BDB4A6907D5694C5FEFA7AA532FD52533B2DAAC690B589E7C92E3CBFDB050EA3FE939699E6094A9261CF071DB3F638BEB7E696FCD96F5613B7F4AF5A17458D06A02505B55A46B0D1DB294BD69191A491F3360CBEBB856510FF89F518BF8FF423C284F999E0E7D68E78F32779E326E02FEA3D083D077DD18769ACF73811E3D3B908C3F511456F5D0BBA742740C7A5CD98A3CC871F70309BCA3E91ED31747C7A672E66B9813CD64653171FB1F190F0D33BFE765158CFE268209316C1DCE9C4B965C8D98D0
	9350A0107F5DB8660C9FB09E4B0E63E335B0100B3C9B4B71960AB185300CFB94E34CFB4C4B2748A02D863046FB0C46B14F73B1147D9E6D7B432FF2FC709E6D7B43874A717DE1EFC8CB3E077A2095D84F9AAC085D4531D6601D04D52D40F5B1FF424E6E473537E0425E22BE00053039E5384E16E841B28ABA68496EC960F3A2AC6995C00DC0CEA0DF20D0209820E4204CFB613984D483548A648994GE481CAG4A388F46AEC075C08EA0BF2090209820D4203CC8CBFA95D0931093488FA88CA89E48FABF7A738477
	A79778CA20267B45BBBB03770BF7D97C61B78CA88EA895A87781712ECE33740ECD3474EECC08742ECA3E740E89FBFF247281711EC614745E84FB372389304187447BA06C1D8B76FE86FB07447F81DADB6D4B02F612454CE2E0571D1509329E30B2B12AC789921E9C21CF773BCF0FB9A40C1505113D8C0D0E2F01DD7E2C0BF43D1E69B451AB142ED73C249F23490FDD2B8A835DAAC93E55249C1414674237FACDFE2CFE7939094595D9947E307A16A3791EB984BF9985E86458BD52A85D8F22EE9C69E7F5032D9116
	CAB2B3B4B6661A587EC27A1DE5103644G4377CA2BCD6CFD2961271948ADF79373D4931C3D5F4A4F8A1078E2CD7AF7D66367C139743BE08865DD66A2ED3106E7FD13D5A32C970CBED37C3D52AABF57E68FD1FF2106E76DB9FBFEFEEC45E75F1DCE73E7DFAC1F338A03B6A61235DF8FB2EC67329A9779CD32621C1F2094504BG3DAFA4BC977E467E14343EF45078B939104F83F6037C600378CCDDD8462E5D6EAEFB5F7E4EDEC9EED99976A183C96E66B273189E914F961035BC08B18D743B0BE932A226648755B1
	25C5E9E33A44E69C53ADFC7EA68B64F3235057D3CA196D36CE764AE82BA4975207B155125CABFD7A58BFA039A612BB375FBC766B7873578E324EA80C5D3754DD6C9FA9E28F08D2479E2A0BFD204FB87633392F96100F8692586BA568FFAA797FF929BE4E6525A81775904AC5171A4779AA174F83594207B04E3FBA5C45B9D591E763C36AB86D8FE9633C2244B84E5BF9CCCE1077FC8863FC25977D8FA67F67BB74F196BBD04EC2F2118E73B847F27998100D27B89FABF69767B4C51C198F2B634CFBD89B67CD3D46
	F1BE44E3AA84790A07B14E5B0B517F5607517F64E2FD1C13693ABBC96E629273B8EF6172DEA06B7BB046F9E12F3BB8E7A8628C574499230B737DE263B8E7733C63C1BE09623453754CF9847D3F24D79F674DA4D7C8F267594C637CABEF9FAAC1361664D39C6CFD7EBDDD1D8EA1B3055FC78EC877F8844BE253F6D7E5B1592AEE13039FD117056591EDD9D4185C4375BC6FE8100FA37F3638281FFBF87D4C083624E7C7237C0B456AF85E61F5268A526B22B11E55335D4523EC0F8723557178C4EB6369B239D7451C
	3DBF4887C66335CD24EB96C3BEFB3808D14C73A502EC8A49FFD3220EF18D4FA76FD1E80F9E45989FF19B23325DEDFAD49DE377235A9847194418A546D7A06F75A846788535B3A10F224F373908B1064BC702EC8C49DF2B09719E9E2375B198FFBD06B1EE1963AEC6E51BDB7918BA467A4734B1B6169A47584DE3EC82797647B046B00A5177B17439214FBC467D3CEE8702EC88491F2C09D14C7346C3FA52B0E2D43637390F2BE3ACFADC9BE339C90CBF7098ABC13E76F10CB10F1E7F0E475167FBDC449847653DC0
	5617647DB4B1DE47E38C07742847B146A237B1AA5B5AD4CD0CD9B15A98F71944F80847188B7285B198635DF49D9BE25067AB87D83E2DDDFD50E7DA5B37E78FBB4E83E3DC5200726DA45F5C20EF1B8F125CA049DD5DE0DEF6F7F3F9EF10750B41F2F927475DB368E3C539D8E25465922BAB1727CD1EC1EF72B6B281641349571F8C7A0DDB286D4A9D03F2E9AE7A031F707EE09148D60E41181CEE7B033394B1350FD147B4B8C69B53838E6318DE6057558B64FD4760351EC87D3CF072FF19C1FFF08B4945105C27AE
	7A0365E24D81E413A94ECDEE7B033315776D931A7B7689ED1C6F19343F3344FD8B7235CFE01C8B28CEBB1E203DD5867D41B61273A239A2977777C6FEFF7B01EC409398E7F0093BB867AA620C564419200BB35864FAC6F2DF13C1BE15627422FEDE4113741CA95147799849D5125C5B3C0DE869F3F47572CDA2A8BB430632F5CFE2BE037D08B5137EDFDD547BC8AE6FGD98F12FFE4023A9DFC092F2385C1FA6813D8168FB538EB871577FB62136A32CCFDD2DB16378E9A176523FCEDAEA3967ACD319823F39B46D8
	95CB7B25DC547977F93DEBG59E6129F2569CB860A35D6C8770B4598A7396DCBAA6F7F48D8F50CF1315A987FEC52173C115755C410CF21989B683E4EFB8AFD4EF79163AAAEEF8359AA127FD193E3175863G694E27B0464F5CDEC7653D9F7814BA463027F463FD13FB629651EF82791827B046D3292EDB1FC61FBBDC44F8920FB19BE473C87E97CD0C8E1ECF9D24B7BE0DB1CEF49B23723E77FADA9D237F535A982FB30971DE1EF7A04807BC0DB1FAD30C71CF631A61A37FD52EF1E6DD78590471D613F535272579
	65E764B5C371FEC3434F7CFA83DE7F8C6EF3EB7D114DB96EE879CDFA1BC11C8B46DFE7G303E272B3B2D47B1A0D0FEE217F41F7EF156484E1FBDFF04FA514356936BB37B3359BACC77B3E2FDE6BC617D9C9FD460E25D66C3DA8776023424E7701C06D3FEE1EBD675F116F4BB10A710BFD098D0EC9C166535BF96D95D3C9F525055DD57565FE6D7EC8394717DB3FBE467725EB59957DBC37EF619B57454F7461B1DF31094A74D0BBFABFE939B3E603808AF27193F0383E90DC003C03EC001C0614FE2FC7F7E31D044
	472A439A0723E5F7BBBFED89035B5151F6D0065966D5E9BC174976426E6A6A611B9E45DE58351938869883761300721F3324D7G55BF0777E9DE19FE8FEDF9996AB9C04E9BA89CA866B9F1A61338F7AFF1B947BAD1C3C920EBFDDE5C3B0C4F063F97409FD7513DFB7ED72C4D66471F702327595BB1F5AD3BE87F6B9FE64E552553B19612CC6E5CAC14793EEF7F2E369EEEC024D9D74ED427ED6B6E686407990AB3CCBED51D4F14773C083566F9BC63643AB22374C6CABFB4DF19EE1752C7BF0F7BAE6EE831D22C97
	B9591C37491AF9F1D9D1E1DEC971E2E92F69CBCE9A87BF0F7B8FE68DE8779F3076E52995778F2C9B506EBFA8D6259F9C506EBFD8B2DE19FE7500F67F4182A9DD44B2F6179C4B59E5C309C55A1348674B13A00E2CF8DCC7AD61716583DFC158D983380F280930E7B8568D3CB7E19B4AF1DDF6B4E14B38DCB27099E3F1ED6925F276AC4AA57EF54E33B3EA84FF305C5D7B07A3A5B9CF1F9316873B8ED5E3F9DF290245E59B0B77F6EC057E5EFE1D4EA1719CAB76477B43EF70D8F1E60FF8B6F670F571C840E246A276
	B75E9F353EGBAAF609E03E9B634D581D89D109D489BA86885542B62EB99D140473E00FE5D405F175B5A565D86F72338C929BEC5FE456AAB9EFF3D3643512BCE2FBB445A092DAD8745C9C93C0F2EECA76EBCCC6D082A09A8165297BB26AB7439E6B15A9E554B62D95F555336971A18CEF67CE6D78F1EB1D1ACE4DE29576917207E4EEFF4E92518F6FB3FAE4D06E961CEDDDA9926DD39C417368C531E5FE89CEB09147E4A866374D21CC32DB4CE37C9691D9D4669E5D23AE32FF17AB2A95D6F200B72AC91B27759DD
	4814CA632C8A97B2B6A1F35937AEE44A24F96DF9AEE4168919C3ABE6686AC711944FDF5B320D6BD611C87F2EF41E0B7AD3C4F578B9D76532DC3AF71798176DF249C6E6212EDEAC27B948EEDDDA854DEBCDB336DBA12537B638702DC24824FC61C226DC48ECB429BB65D2BEE11F69FCAC47E771A25D331835CDE9AF623376E343749CCADFE818DEC8693BF54FF216DEC5693925C6698D147E00E1FABB25775B0C5287A95DE9186EC369B9067E85D0FA3243A8BD1452C79926C7D17ADB06697114FE3857A8BD65C56C
	4BECEC35D2DDAEDB16E7B5FF7E1796318EC0496205D6B1A6674F907EDF72F7FA8C7B97FFE0C3CB296FBDB6937B5E39577C59DA7364BB63D99F52286F1D11A07A5C0D897218AC7137D4E92F635F7748BE3FEFF355E7A995AECB32DB4AD8DEC564DBFE3BECE7E77FC8E3973622BA6418DF7FCD366B51B7F47F9E6CE37D33BDAD76DE798C00597DEA1FE75AE55BBD254767F359FE596746CE596EEDA5C377B93DD46FF3FD295A67BB3BE45BE7B80E4F67973AE51FA77548F6EF6C9D3A4F9F746AFD2EF528FDAE6815ED
	87979F1F4F1B9D324F1DFD325DBF968F5D6718E23D4F1F1628FDBE3BDF366D6CBDBE1FC38E4ABEBF69146DD68C231CCFB6A86707FA55BEFFF5C8367D1843156D92E63BEC3124F726D556DB347DF32D5E7A2ECEC741672D9DAD762E3D1F976601EAB138E5B50F777A71F23CB193E45B9F3A3C9754BE3DBDD1563B4B6535D56B6DCD1775964CBE7ED892A74B31CC7DC8369D62725A21CFD25C2AEBA7ECAD1DA25B7AE89865B2E31A2C9774BB44D7B2DD0EEF7B8C59766B4308EF05C39F1FB3CB367527E14497B3D356
	7BFA4E71477776ACB93E7933E55BDEC33ABF046C6386712D1AA35BEA99C6FC97647C3E715DB7CF0E6FF9C57DCC9AC6FC65FD7A78E6AA6AC27FB062FB67E3D9EF5A6F905FA245755B14AD5BBEF39871BD6550371DB58DFA6C4C86F5FB3ADFD1DF1E6C49F3111F7BE7004F9CB90E9B674AF6678D639950EA500EBC2128CFBB0E338F730E22AE65AE106DBEB20CBE4CFC03BECC2B268F73DD2EEC3B60B87BB03BBE11FDBE65D3596EAB43A85792037AB12869435C38D0D177CA0E4F67A772E41F5F79CC365BB00CF23E
	24C46F732B5407917CD7B63D4CB97D7ABA147B056C43359306FE2FDF75252C771706633F579F78CAAE17FFAC12EDAF5FB674FB7D2CED7A7896AC16EDF58F23DFF4709BD96F055FA13E8B8B6578EEDFA25B4EAB9DFAFC7BCA757145AF15ED158FA33EB5A5325E875BFE073E04CD0EEFFB19ECFB6B101E9F529AC009C1DFE219EC2BF2987145D448FA713FC3FC93D64871FD39D2365DB80C78AED745A7563F9E633644BA5715CD6C26B2DC93DD334B31896C7F306B37365D0E06163D2473634F2C93C7E7F80825D2A9
	2D79E776506C686CF0C01C9D077879A95BC030F32F18FF67D39A52BA6720626FC959A7EADEFEBA657169894DC3BA776E9797675E7DE2F26E5DAF46E729DC7A0B6B738B2F7E457CBBE1413F306B0E5F8993577D79CAD660BE891674A020E8208420F1893866397AD7D6E75997B2756B1D6BDA7732F37758D7B19D187FE74D663196B43B7F16E6F1B39B146C6AF2B83A76B37B0817B7336B65686A662040AA1BD99975F46CEDF788D17C76DAB35F3351360743E2DD7B37CCFC9FB17BA5CBFA5DCB78FE73AB1F300659
	75F7019ABA9C7636355272AD1EB3435F17E5BEF93E046FBB6F5F4B728883BE86A889C8BADF3EDDFB3EFC5A4B167482204A17717B8D3F16613AFB83E05EAF23FFFBBF7FD269DF472E7DCB7B7A77AFED5965609F85DE31627BBE3B3DCEDAC612FC3B0A5EE9313C0C3E0D6176D3D9FE2FD8528B0106B66F076DCE4FEA46C967D56EB1B92F12CF958B9F36BB31FCEADE41738EDFAAC3EC70950C6F68322F14716D336FDD7A5BEED663176EE1D3526CA0F1369AAC161225789E79956D043CC26F996B4E7E9F4A5A5CACEB
	72C156916EEF6BF1F46CEE310B830556367454F759BBF68FC8FBD42672D85843B26995DCA75B68542F69B753FB3D0509E2CD3F86FE9B013A93F14D6D4E815AD71C08365E9CB05F3FB375905F9F8332A109383E7FCB393B75FD65FE7DF850CBC9145777B3DE3524FFE2D56E4D59E2722EF19B77B59B6473DEC5DF8F15B3D93EB0502BBC836B8E7E1C133E9F5D265C2BD3F4F87C0409D663F9613AD745FCF0532B5A738866AE477B419B52AC2FE29D7AF5B93D7373AA566DB3382C6DB5382F01DADF433970D8763A1E
	645F292E6603CB4BCA8B73CA4B0B24F7685F2B12757E6EEABCC3FA4237D25ADBB8789A6E87E831E977833C24BA0F609C1BF6BFG531753A3EC5A7DGF729745F36E97783203E08655335F2AC8F758DBD16DF687E8DF98D4B3E1630A442F690167FF744AE247B3C0930EDFC9F9A7FFE86E163691BGD104DDCBD8DA92E2FFA52C12309F4A90F39036086F89E477D290E177BBD0AE6B9F08AD69C32C16309BFAD157639F3867E9EEB53B4F9200377E53121E8FD489D48F548EB488648394G948694831488148A1475
	BA5CB3C0B6205A5771D9FAEF9BFB56186DB5138E55608F541E168E76C8151EE72FD41ABFD37FD9697E197AF6A50B2D8D1E058D835D52393629152C7D6A5A3327370D1E5D63D7E5913631373E2B370375542473CEB35D352FCD2F9B1D3DFD542D5E279ADEBA53006BB94126776B6E5FE39E8DB2E2DF5BD87E8C8EF9DD3B5F6DB50E073F2E5D6F563B975B19A8C8ABCA46E776CA55B31BFD4B54975242002213314ED44E40FA1E180CED51AC7EBD673CB7408ED07D9B78FCEAAB9B593E3F5E916A858DDBCF145346E5
	4C7F76B734653715630EB73465778C75DBBCA0AD01627DC975BC2EFDD3EA67255F6EB77557D4FA0E2B7C7614647C245FA0D33D18DFD1AF5CC0A68630C48DAE5A444F1DEC7F1572EBE70AD385176ED83735E1E3751A3ADF37D5EF7D212EEE6D1A2D3F2E295B396647ED943F17937B1529E05F76AF2C9B5154AE5A7F05F5432B1A4986G9F869473AF2AEBB55A332213A0ADAB855A1C94FC6EBEB1209F137E0BB5CFB8A6AD4F333AB86B855A76125232A12E75E21FEF4EFAAB6535FC031C5777C34ACB3421AFEFE07B
	1EAAD3308D3CE9B59BCF1835016C5BD0E2E40857751365A32B7F5F0CD02FF558FA627E983F02DD5B7A946DFDB30563CDA95A7B669C6A5F34C3DAD48AFD8345B6B23FF72B6E3722FFCB6787CB3F0D7FB63BEF3628746C121C07746BEB2A77DF15DE20A497A67DC61B6ADD223A3F93A439F152EF66DBE6FA0D25CA3DBCC94EA67D5642EF53DBC64F1FF4D5F9F6CBB21E522F9F7C86996A5D20528B17E4E2245FC478CDB5548BD169E53DADE472255F8A782DFB5BC82FC1D5AE4D120CC37A7502DFFFC33D42BE23790B
	E0C95672363EBC11E2B5E9C940DB474178F01C0C0BBC9EF6A8FDAB1052AB255FFA78ED9DE764DBC91FD24FA949F8CB3F2361B744D06F16DE96D324A6AD8E7864F16671B0CAFBC7F30EB870059ACC34C35FD41B7729CA2A5D7729D6D69B4FD32C29564FD32C2FB61E27282BD64ED3886CBF552C1D5F8BFD41C6125BC1580F047D175BE4584E0ED6C7BB61AD5CAF065736318C915F4B7DBA4831C68933AB309FA56CC0B56B97C2486DDDBD285BDF4D5F9769305B85FF084BF4F4C2B7114A63987731233BC56137A7E1
	4CEF711D0C70D953382E3D23330D35769B5ABA01977B039F66F3AAD5F03D9A5F41BD43B5ED3C0C0077A26CAC0E79839F7C8E361B2F3029C8BC1FDC7177405E93BF07B86A0466E15D4B47D96D781D6BB0085939877A8F701BA855ED6B3B586FF8EB87AB9BB6C64FF9977B9DBB2673E793706DC09E6FE2192D19213CBF7DG8FFAD73ED7425F555FEB6219F69A6F63463C2BBD1F5A1763496FEA4F271E37894F274EFC4F125E7D9E667F43B279BAB5AA7E9EDBF6622F593BA7B48FD1CF8F70BEE08C441B709E1D1B39
	13E163004FC8C56C111D38D7BE1B30714BD0370230797378FBDA0CA7EC43BC54EDA76C6FFCFE4C9B78C0426E26F37B4288FB116F510F87FEDCAAFDEB4A6DBC1A71B84F5F2D1E71B8EF8C574BFC9F464B6F3B9F67595E47F1DEAA6FCB573E2F6DEFCD2AE1F8437B5A7E561955585FEA06B44B7B58775ED1239D6795FCGF9G35FE0077D47DG4E8D8CFE007754533C5F9F8F7CB8202C8F31FF7B5A8656B6762A7A3013D748F5BBCB7177854BCFFCBD3F610466A16A51A1DE4F8B208CAABE2473D2F91D2EE3BCE147
	281E379356CA3A21049DA55DA8422EA25DD842FA042EB5A23D82C8F9FF55B11EB0797EEAA76CE6F10E937011041D463FE39087FC92E1A1B928BB0E30AEBE17D5BBBEA23DF1BCE25F70B3156D408F9256A81F97CDD8A757E5E7AA479276891F5FCA82BE15304739EE560408743C8908D967223DC242965A505EA0E177F34C8778510479F15D9060AD04C5103DE842BEA3FBF99391FB10E3B660EB887B9357ED843E1D30D03267A06CAE32E7A1EC1FB80F9E78C4426E613A2940E726A116CC76F2887BB759EBA76CFB
	F1FEBC705E049D966F32839FCC58797C3DD28B70B104ED25EB14C8D8B51DB1DCB18931B3B8D687FCABE1AF7139C5A770DE041D424FF97087BE08309B685C61D042AEF1E09E99690809776FF301AFA4AC1A6BD681DFCF587BE42F09306ABE3497C458D3FCCE131D839DCE5827624CA76093885B6C40F73A12897361D85A6408746C49084D20FC8B883B1D62F082A66687DE4A647332EC550C2DEFDB691DF25E3F7386B9BF127DEF5BB4144589C712F770FE9E6BC3C6127C15ED13D05E0AF393F59BD91BAA5A79E36D
	584E4702CED6C60454065F574EECB19B350009B6BC3303FFBF8DF8BF20E0429F67F8A47071C06300C4DB7A5E8E7A0654C791E2DCA77DB22A003F6B001A9518F8A6BD407BDF8E4047DB155FCA9F43F1AF85AEE2E862B8FF0F9A522CD328BD61730D2D40BB892B6B64758D78E0429E6C426B94B385FDBFE31379993D9772347C29F0FF833582F9CEC53D051BCE7C993D953ABCFE7FB3FA3D1C6AF75EAF664F0EG08B3F8AA562DDBF7B299878C7CC43DBA340955AB71BE5D4BBB311F908F72A9C059199169B62086A0
	E173EB5E47F2G6F1109B60B6729EDDE34D936794BBC34998C729620842034E991697953D0FFC233795A60823E4EDD833275C04266D34B5066A0E0BEC041C051C049E4735DF263753E197C9C5E2C69F09FCC473A14444B299678E6425EDC4B63845E0730DABE168DGBEF4BA561D89FCFED27CBDD57137EF75096F77DCFBC27390E33283B5B8A633C04CECCC96873F4953457D1FB1835B1E70AA9C13654D40B666B75EFFEC843E9B48EB861659768174FBB4E0A1B36436A4D2713716628C521281CB4BD263225D79
	FE00651D139521B94B391163858ADC34C771B447DD81E97ED9D80F4E565CD3D7713AE27D981E619F23CC07661E8A2FE3A2E29D7B3C1D6E4E161928208C4539197C2C170FA3280FAF72BFC354D5409B00C4FEAFEE11733B43E5FE135D64672948CFFA5FC3937FA5BCFF9F10737F582CCDC97FC17667E5177ECCF463CF144E1F074A547E3CA87AD9A017C07EDC1C2376E7F13D6C4F03B94359D72075A7FBE604664C0FD3CB557E780BF3B5C12EF0A67A33F60E5A1F3A7F487EF439BCB7EC22661AE97DE902BC70AC88
	E57EFC4E1D72375954BEEE627D4BC1C877A21957B4BEF6ED15FD1CBB571D0F9F3970B1F426368E57529E828B659F2F7171ADAE938B6989A4735739EA9FC7ED13FD0CF36B63D497BEE64F527A3809FC4C1B05793F2071F11C98B3C0FA9549DC26717126865947C75C7AB84D050F5DBA9FDF1377E2D07EA7E96E8FBFFEFF78C03ABF49BC2D71B1F63B6C63E43777478C97BEC669FC7C1476E945D27EEDFD6A7C5F5BE1257BE9D41F71B76B7E427B7EC9E0E39C5939CEF3AE1F050FFD7233A3520B00C4BC9DEE6F2F39
	AE62E986BB9F2962B9C07717BD9B7D88B0B92BAF0C4BFA001CB74926EA4EA51A49638A05F48B4978B7284B6753C6797A5C6F724CA226BF5BC5BCA9595A6B93C96727D9E7E37E4DFDEA9F7B390F59101EC7B22FE8FCFCC55166E73BBC9FCB681B7BD8BFDB6B23DDBE43F9B676C3A7105F5D64D319C36D53945E4EAA4FFF624FB8123FCAF3AD44B84F8F5283E6E37D794AEDF9FF6CA21698DDACAF139F713371795D33CD2E7F95ED6CC585037D53EBFABADA6C1F976695437F3645A47F27B6E6CB7E866BA96DA3DBDB
	BDE704FAD126FAA9330D7BF2229F6641471F99F3347D3353B91EB3C75BBF1B59017D3382C8738692729F7E466763A579EF484EA77B78FA40DC9997F61E62F6989F8CE9197330EFBF134F1F354F07F161FC441E19466B8370C1047DB403661388BB1C45309460B397A0163B057783002FD8007D5E6869FC6C217A667DB39CE367A155115C2E69A77E1B77075D66E1B68E9D8A093A79D336FE7F689FE6E84F046AE6E524BA93EAF7277E5C18C31DF4EE8C4833B3217C61B798086909FB75052E63B993AA92EC6119D0
	629C0207B7E94FA1E0F945001CC8FFF91FB23D7528C8CF24F44B57C67A2914FE7FA26D19CDACBDAB975B052778F3D91449F9BB0772DE85EB9844F8AC33915B19FC30A776D12EE8546E53E4AF7FAC3562BECBFB23F61FE629AA7D62C66DBE4DFF2952239B357BB4E7DB157BB479F214944F74F9430B47F39EBD83A81E9B66390EA7E11EB61E8ED57A1C795AF8DED425574EF79D4FEE45374CEED97629B69EF93FD01974CA19944B074BB096DF0AE5C939B616A5AADFFEAD574632D715FEF639B616B6D57ABD655AD8
	54E7EA1D7A139C4B4F8707920B101DB8E0BC8F3A002FA7C5C1FC3139783C7D285AFC9EF4C9B54E03E6FC02723FD66B67C12F665F8C93ED7905B5B88F1A83BA751F60DC448BB5B88F6A75894EC5ACDA4777C6829F8B14CC78BFB91E71A974ED00AABE45B6B8228A1FEB751F0A67D86B2772734C89FFFB830D7ED4BB8F7A9C1FC78879D4BB8F7AAA47A3BF554E03F6BBF19EB48E522A96627363A0AF3FG60A3890B66D89C704904DD3D1E774D72204F1B0758BFB9D683FCB3E1738E513A85E14B7E43674E010F4B43
	389B8654E7157D4279E4CAFF59F9624FAA7B700465B1116A519445F9AC6B075C368A5DA5FC6F0578BBCCF1EECA4FB06DBC3FCF363332D336B37436DE687E52A55B09B8A05BB91CB3BCBB09BD329DAF45199F8B0774FE39EC679A45199EB79A3CC7B9F3C0CE3FEF18364B94E7EF7CEA535B769F14533D678E4F761DC7E45D96835B7CD5EDA97D58B0ED2F9EAF6B0EAF535B3EF0221CFE4BB0ED5F1EA66B1699580E1F24A8139297EDFBF18558DDCEF657A84E4ED8BF4C6B54BAD55675B2288323B265742B9DC37769
	1F53E53D01A13DABAC7B7431622C0C670D4E52D01C09E199266DBF4F12F5CFB6B893E91E413B6FFFB27061ED45D98893076943170A738E5ED7591676427075413C83625DBC51561E5A41AACF5481BAA71273F2FACE07D9DB4CF40E37BD96F9941B66B171B86D4BF9CC5A47A66270590B5F8EE1151475C9E6D8C51F0ADDC4F681D8C1323D5D6C0EE75FCAE37B7A589EF2B6B6E76385763EA26BEBAEA67BDFF07B591FC524D7823582F98305G458125FD9E11DE8854GB48894G94851482147BC5C4FA8D109D4897
	A88CA881A8B3BFA25D160F4F5D67BB6A474B6F178DB786D77EE3B9FD7BBB14757FEFA44AF659EF2CEC312E8D37EC4D4A759BC5BE1F7CAF0875FF1AC4BBF44ACA73355D15FC4E25896AF8FBBE76795F1C269F7BFFB49D477E3EA0474E0C8D025FF0A056CE09B56183B42F908BF892137B926EB9203AAF5176282346733481FC7E468EF2CE124D1BE3BC8F3D0E4F6B8400DCB01058DFF74BB16D3BAD4C2FB8C8CF7E9247A43752FBD499DFC5C86FC3DF56096317AA401CDF216CEB1DB8BE8C7E8A47A9B9DDCCB691F8
	6B2208741CC5B8C7D04B4F2591FA13B6E39EC50B5056A7F6445A89FB6FEB44C693E6ADC0ACF69136B99FB70EECCE700F73C62617D6BEDCBD6559D3623A3D3CF6F87327B5556A795351B5A3737D3A916A3DE22A17D560EA7E744FFC5C1ADF201DBF3D026336826D7C6912F5B8FFDA83E90185B8FFBAFF8343B397EB67CFABB8EEDF2C1DBFCDD9077327D110D6708D0EA563787A3FE7A10C098B91FB021F379D82FCA4E15B87F09C1AC4D8BF9F1BE6AD81FBCB9093F3A41540B7AC417A1860544F1F3E4E31F610E16F
	1F8B2C5F6D78717867CF4FF21B47714F1F1EB9607E3D7D2BF9F93239A26FA534B6B3201FF77DE0C07B5EFE944863FB7BC2EF0B0D259BFFB36A317534EEC179CC5AE05E36FF3A014F09032CF5A92E25949DBA1EF77A8B410E661D7E7F183C534F67B5AAC03EE6A93651978DA85BE8F6BE850B3B9B52BD013C8FB21976FD385745FF2958A796823F6C1DD87BC1693D9EAE17F68C6512A1BD63DBB33926D214ABG19AAD339AC9B4AB5030C53D44EC2F2FEA093ECAA67ACC339E810C9B415ABDC06F299C59169F9C5E6
	F271A4D785B20DC04EA2A3394B4BD1AE845223CC65F6D620DC922427951B49CDDD0EF279A0D3E9AAF7A749B5010C43D42EF7854A7902CC10295C2215A89785B289C0E9A5C6F22F11DC9524B7161849DDDC09F2CEC877B1155B3A8A6502A1BD52D4AEFDB54AA5C23A35942EDD29115C7DA45784698ED3391337D339C0FA1029DC798EAA97C8CFB010531F7F98BDAD4F6A62BDDAF89A175AAA960BE7CB71F46C8F255960FA5B30EFB2B09D5B9E078D7BC3D7F159A060ADC0F1A4FB7E8C14CD31615CFA448C6D5C7A2D
	6A73A5E6E86756D9BBA327AF4E524E2DFFBBD1197E5FAC6D5C7AE22BF2EEDDF9066547DB06D29EC22FD9B11EDFBEFDA8FA228CDEBF464A00354D05E5D8DE1B78B7EFFC008F848AAF43E76A9D7C7D28D860134A4C775EA1D91721DEE067487ABBB70FD06F75E16B09FE0CB76FB7E7AF0B503CC3F3AE4773943828031FF6E19DAC04B49F0AF74AE6E57EA94B247D0F6562B737DCDFF6C26FF3BB6A95994854G562841051E759B55BB4B120C07746BEB1A5F2482548BB4108983ACDA030B7E5687B5B8B7445E4B9736
	AEDD271E9B3A0673F2FA693A93BFB7D4E71A476FB7B7143761444E8D2D667693205CB3ABA252EDC0CDC01EC0C1C0B1C029C0794BE12C8A64G728732GA581E52F0036902895489BA884A88EA8EDA5564DF7577D5F5C506FCF22EC7F337E7F6606FEFF1276GD4180F9FA62DE16DD1A154710A15B85E7847C17D5CD09A1DC7563AD24C8D8D422F8F90EB27445C101D7A7E010007814581258265D6226D47CD4E873B040FCDABC12E16E4E38E9B4F8D9570F7CD9DA067D109630E85355AF18773AB88524349DE7840
	C84FE7914F7BE507E86E06EC3E3ADE3F36F026628C636907722C6E4FB7114E19D71CD79CB3DF36F13BF3A8B6040FE3687D522CD558A779D11C37833CB7D040AA1C97BBFB9F2E09078116328AE33A77A8EB07F6377434B91672FF37F73736B85A8A14A547CFBBD8DA4DD25731BF97637C47B134D9389A5A525538876724E35ABDBC7E10963E9AFDB9259475E2G4B7C8E7592579A6955FC07FA4BED285788188F691D5EEF2497C9FA7FA03DB806D521DE194DC82F2E8A75AEADC33DE6407CC86FAC0711DEB469EDDF
	06FA8900652EC13D6A92A33D06B52877D1B96A3583B61A74E6EDD66A951EAC74E25760B56CAEE75730435136FFA97BC7FCC52B3D25F3EFDBEB8155A2F191B7C23A6A9AC6D11EC9E0AFFFAD6679D42FD11ECDEB51D70F8A546B86AC1074CA0E996945131EEDB96A25GD6D80DFADFDA0D74DA2BD1AF11741C0085131E3F21DEA269792FC03DD440ECB528773C43C84FDE03FA75ABD14F8330D052FB5D709AA657603538E565486F27D91518E75ABA681F2C237A56E71427E39D7AFA9F69F98196CEFA77987ABA0E74
	8E2FC23D4C75F01F2DC73D98437ABD389E753EDD0DFABE00C5125E1925C6E5EA5D00E5F35E6A115753B7A94FEC30D73781739CB04C53F3837A9A349D757CG0BA63DE7CA0C74B2EAD12FF9876A658256D00BFA738C73732E45FEE37974110D45560DD06F68307544D8ED418C965F68DA6D98AE1F63C1355AB15C35B40F908AE9399BB1DE7F99A373BB65980EC1BC40166FC6F97C943851FC4C9CE610968DD842C62377556E606373F19B357379119C4F58241D4F2F1F057379B910B63889674D4770798FF63ECCBC
	E1CF6C676BDB2A73E5BE64187AFC19D1FC9F11787B4F0A3F733BCE7C3B0C2BCEE89E52FB7FFCFDAC854AA5EDB3164D7C458C4B81DEBE2B6458E2FA871C641673F534BA605B893B25946586890B656F640C86BE1430395FE03ED10495FF455FC7E7BC65FB5ED7E8AF1964F2793ADCF6DDC4FAD19D1DA3308F65EA88CB607E35836FD1077359A7EF20F9BD400200220058599F6AF93DC983B48F3CA5A2BDEF0B19DCA14DE7D701CC2329DCBC49B9C14647D44E17663303C1A652D42E16663393C1467A3D19DCAA4965
	01CC0529DCB04DE7B702CCB7104F77C6F23DB41F9D85698926F20BE8BEBB6D87E8EFFEB013FB0664AAC12641D46EAC1A4FF6000C3729DCB54DE787010C45D46EED12CBG19D466E33D115C5FE8BE3B91523B6B4D64BAE9BE5B875283CD65BE27796CC8C80FB715FB02642C7F09C84F812A780F115C85B41F6D00F4EFD339AD5FD139C03A45D4EEFC95158B24279A48C96B4B0AEF34BCF8B04FBA54796CC2A7BE07F23742755E0AF3548E7A263277D69C8BDD46E5AD40478125106CA8BACBBBF39B4EE7076B3E3F2C
	1E4FFED3777DE575FCF67EA1577359BB8E391E4F1E7F09DC9EA92E3E3F28194FDE2578EE4C9657E3C755FC7636DA9C5F56EC43727A43FCD6DE03407B32F259064F78150758B35E82FC6CB6777359C9247774007279E3ED003ED5032CDF50E0EC2B52G2F872C35416899BF1B0FC31C8D62D9DECD78F79C4F58AE6352F979FCEC46F8BFC88B59093E1E3FCF5B0F4971D64F375AB43C503BC22737D443C77FE8B497DDFAECE4792D5169E973CB5464A766CC7C0FE967CC583BD1561F601A7C0477452FF43EF561CFD8
	AEFFAA9D191F97687434FE367E2476D37830309A4FBC98A49F3E531DEDBAB49F36687434BE04987AF0D7BF7A90C5BE7CEB04BE0C571D272C75A16D3FC6BE58ED68C36EFF51072BF436066643CDEEFDE8B07461DE877AE0A79F7E3BECE4BEF4687434BE84987AF02884FD88A39FE66876B48D4D07CFF4FADA9FD28CFD5834996F0B4CA6746131917A906F56071AA6A39F3268DAB4118F27D60C4C87DF1D1E5687DFC39FBA0E218FC164438A5D3947C37321D6272775A1415007A6AB7A10CABE7CF304BE243A754176
	33118F0F138FF5BF238F172D98198F41BABD2D8F1E06BE4C69C39F7C49079DABC766C32BCECF6BC30C218FDF15627CF39279B02FF2E4BEAC5269E9FD287F45300D22F2E87F85FDF8E804BEBC6356075106BE3CCBEDD4A8797007D5A37361AC1D1E5607E4C39F26D2B9E47C0ABE142D9E198F55BABD2D8FD53F9A7990416B83DB3BEBA49FBEE898198F195B5D7990E668C3F0A916C38C79F0034E5650FC88F76BC3E633118FB349077CE674E14F0E1179605069E9FDE8B274211EFCF0108F1F716F220A3F3FD67CFD
	235B77560F0724B38DCEE89EE23EE68EEF1703A05E68E6BAF313E389400FA3AC3E0EE139ADB0B6EDC12C106FC12F833E1D305471B497C558E17ECD14A86093DA30DCAD33546FF7BE4CF9B6AFAC354B33CE7C7B1DDDA7AC8FB63694EB319F70F950F190F75AAE1C532F582CBE6F63FB5ED72A00749A12A9DE0CE708083563739663D84481B2DEC001C011C00924D3EFB7DF476E6073F2993BE16C329B4F2FFB3894EDD68156G64G72858A590DB6DF37992FC7CF6667694400DC7CEEBC8B250A4FAFE636C6246734
	22FE6DD76AB82E7D8A732C8519A620C1A0FF20B052A9ADB00F230E4FC34601ECE2AB467174BE34195D9611DEGD48B548E645506B65F339B47B10B4FBF8702DCC89B2DFFEC40F50CF840F27660BA4678DA6D9A8F5B6FDD3F8773F84CB972F52C258318E7AB587327BCCF9E5066495E010E59C36B2634AE1C88D85EDE545BD9E62457381756CDE9FD578ED8G69FD2CDB97E6FAF124F7BB69A583D6500EFA5BCA0D741A5BD16FG2D8BBBG8BA23D7F6A564D18DE8269FDC96B4263GAB6AC03DA25DBA9D53EB6FC0
	3D27C8EF903090527BF619F6AD0A69A5F560B52CBA0EF5E1FFDA97367E866D64EF666B42AC4F6E5FE85D145605BD818BA33D1BF46B2DCCAF057452E8FDB7E3DFC4FA55BE54F3986AB97721DEA869F9B39D527B40D0AF15746C34AE1CE507FB4C0EFAAF9A6AF958D1AF0F5605FD810BA23DB443BA13369F2F45E94731AE7CA2651983766A77E31ED7685605D91EDE7B515773E9DD589F309852BB4B303EE5F6225EE6DA974E832C3153F84D9C75FCC86FC3528BGAC0E744E502D19B23D6CAEAC1BBD4731AEFCB72D
	8B97003D66AE4C334BB0CF3FAE74357FBB548B82AC0174EE552D8BB33D5CEE547B3A8A750AGEB6FC63DDC437CC6F7E39F20E4585F1994FAD5A3549B98361E18B71D435B6E10EE6D3A70C20E07F7EB570583E9BEBE8A520A8E50FC6A40487CEE2CD56A79023D4083F27FB66C00415C307836C4B373A37AC004765B929CCFD260426F5378DCB763B3FAA2526DBDF42EB75F77454E2D0DA62C143F2723BE373609E3F8EE2D54DF646B8A8920B70EF4D7F3FB993DD007FA696C6B43789ED695E1EF17633BDE8D047D59
	097D4FF6424286703DAE6FDEAC6B85951E701BEA312467C4D852AB009A011CC03EC021C0B1C049C01977DA528B00EA00DA01BC018200ACC089C09977D9528B016A019CC0FEC0A1C0B1C029C0F9111674AA20A6A0A7109FD098D0BC1075FE6C0B2E637E0C867F420162FB457A2755A156857360378A28994883E834C32CFB7AC86B15D6E91D112D8D66CAEBFA5E52DADC38C32CF925CAEBD0EC1DAC9EB0EB1FD8DB7316563A58FADCDE9F16CF5B322C89D6AB0B1945406A8CAB93E59DE2E5E2D4A793A4BCB9C21F6E
	F71F9EF3C898AB8BA3FB999A9DDF833B0CD8793A3AD71CA6FA0552750A1774E3B47931EBD5E120DBA549371A14031272DC78D62F490F55AFBFB73138A20B429FD65FF2A45FB30760A723G0D9C3BC79A257BC1540DA4DEB758E64CD2C9E6064646DC935BDFC83F338C521698E038BF395A445E179AFE1A095CF2B7B1CFB54159BEE9761CDDA371451AF476515B4986FA65526F02A114F7190B34459A1EADF82E9AE13DE0741962EF7691FAFC9FF476907597EAF85697E14F83BCFBF2167561FD0C5F5D5559394E51
	D5DDCC78EF4714B82BB7E24EA5B79B67C32B7AF0CDF5AA6F0385G9F8694C3EDD2ABFD63AC29CF7FCC3376C3FF23DF31D65A0FFD253BCB15F95B2C341F289A6DD5769BBCA381EB85F2CAE95EE42F5F2634D7C6766E6DC7FB238D6C05G968994A725A5133D2F4B147616113D8334FE16F6D0EFAF8730C220AAA92D61A05AFBCEE52F1C6CBDC85F16EBB730B788188FD0001496CA76C6ADD35A2BA0FB47CA50DE14013DF840D200B21C523BC9CE34373EDCE9EFB9597B015EEDAA82599A275AE6A370F68DA61D67D6
	B1C5D9670A505EEC0A578374FCB53A01400781C5ABF0E9BF395ADEB15A6B24F564845099273117B9G4FCFA05B00565E0165AAFBA5E82F450A76EAC127E9C0ED2F9BF8CFA0BF1D3DAFD7286C15223DD8329784BA619AFBB140A782256A6CBD2536E7C3FB8B68FE4BBA94111EFFC833C68EFC9DD073A12D3DB3D6AA2F2FFC2641B725E673ED0EC37A7A63A5E17E86E94100D900E2F5F957D72AE2A9233AAF379D86762C07216DGAAB8AC52AA8FE3DDCDDB2532378C6DFDC8F53F7E305EDEABE0CEA0EFA9EDB4598B
	D55BAB27EFD511FFA18676A2810B83CA16525286511EFD354ADE855AFB0017F54E205ED6218106D485E98D1A74F6608701FC8635E5DD36DD1577F2DABB27FD8C8120932A319785FCBCD00A4E5E2BAAFBA5F49F4FA5FB99C76099F1C4ED2F88789A2046A3DAFB976DD059237BF8BB5933030E07461EAF7001C0E1BAFBCB2C6E4FC1283562B98851C7709A1DEA551F03F0119567AC330EC670F3908A603792086DB191FAEF57713AE4F88EC256965C0F55F094652B37184F1BB6F179F610F59C45F390E6CDB81EF390
	C69F551E03B02A52F80E75AFFC7EBF8864C30F62F9B11D6CC8B1C96F7E7D2C1FE5FA3E5852DDDD76D679449421BFA6DF561F3EF8F8E73806AD4632CBB80AFB59DEDB6C7A8C575C2F35FB5954E752FE7F35F6AF1B7ACCDA0F2F35FB5954E7380E7BD20EA728F421B69E45B9F47C60536245D2ACA9F48EFC6EB10C65F31BB616EFD43EB4583431942B52CF35E9E3D9B2DE199EEA534622BE43F541A2B9162B0F8EA59621F7F0312C7765D7433B260FFFC57783154378C52E2FE969A2EDB9284F65EDDB24AD0797D569
	67AFF2FDCDAF7CDA0EA7F45F504F65355AE9DFA64532546E7ADC5EE63BB6163D2A74D1F6EDAC6AF3F9ADF657577476EF64D8BE308DA596E99DF943885711F5FACBB53C6518519A6E4B07F10FE65CB19CB758F473EAC373E12DCECF6BC33607118FBD721EC08F5A83B642BD019D6E77849A7AF0E6996D89A49FE60CF0BF5EA76E77849A7AD0AB6F89A49F6E99218F0F3A75A16D8FC6BE1CCEEB7A39FFC09F8E0EF0CF60A96E77849A7AB0CA5E93C8BEFCB342BD014B5D6F89B474E12F3CA710FCF8FE04BE7C432D8F
	A906BE6CD9C6FB82CF22BDE0A35C93986CFECF60C9EE7684128FBBC638A730557D1EC0C39F124807A072E14A88776365385F93E86843B564C3AA79F04788FD08F23FA770E4A39F4AE87FD55D4968C34F887784FA385F93E86843FCF9CFA07970558877845A5C6F89B474E1253CA710FCF8F504BE3C6D5607FACFA39F6264BD011E6843C5A35C93F80D7BBD0106BE5CAB6F89A49F7EB342BD013F3A5F93E868033CC7B4639474E1528877844EF23FA77094A39FAC721EC072A1F204BEBC615607B0C39F2E14778412
	8F035F0D4C0753F5FADA9FB2CFB572A1196AC37E2968C3D15548FCD82D53537A50E46843B6F9CFA079104B4F0D97FFFF29787B3A93763DGB90E7BCEE89E527E3FDADC138DBA9557D01F646720C4831FC0580EEADCEB4DB80D3EFD5B6C625B37BC2D71B4718E2893FE7DC8EFE1737F4037EFF5F97C1E5F3E95F39D07795AF5A844E5B90D66FFEA1573BFD5E3793E26C3ECDFDC8A49ECBF24BFD346FE8867082CDE62CC199C78AD84E27BA4C4FEC9FC3CD383D83D97E20B7837E58701777242BDF2B75079B4A100C5
	82A582E57CB1A23D600F2877F73ECEDE8BFC439FF10F46F9955AEF87713DBD14EE5FE9146ECB6937EEB7CA8F247442F22374B0CAFF3754A8BD1A520F580C5293A87DA74374F1145EE7181EF9BA6D07ABB3CA4F2374E807D13A0D52C7992657D27A984374A6CA3F5AB03D7BF45A1F1C2D2C531E007B1DAE37E3C1279B4F75069B60B100A5EAF0319F64BE3EC7A975F46D7B02EFF05C7BCF5A7785BF666BF70C4F750EC82FB8D37D1D78869F705F87318AFAF75D13307B45F75301CFA26CD97EAC301E9D111EFDB6E2
	ABF97BD3GFC8DE19367233DC642FE62E361C0604389FB0D7B9283FCA2E15F7271EAAA70D923A8DF0E6583DFC558C69B6651C058F55CDEG70E1041DDB0AF9C4937634785EBD706388736276B24F01FB719CF59930BCAA895B45F53D018FA06C75DE4CA314B09B570D82BE1E3097B81682FC16AFE23F15E09E79043D4B6DB90077A66C72E24CE3B4E1099C8B81BE123073383DB8604791B62A977348BC173EE94DF51B00F790361C6270A26CA8477C018FA6EC8F5FABED81BE1E3071FC4FF98A70E967113D1FB15F
	9C428ECE66F36040FB90D6484B459778E042EE667ED9000FA12CECB65ACBA4EC0047DBF1BE343D6753377A0EE29C2D041D425F81F3826FCB581AD53CCE829FCA588BD3B00FA842B671BDD339FE502678A17670B14C2316B0A7DFF7E8825EC958577CDCFBEF607D893BF1AA6691CC18BFBFA359FAC1C4FA4E85082D33E29E05043D4E31AA4693F6A74F239BF88F42B64E41BCFC89DBA36ABD702904F9B9B00F2C8B91AB6177D1BE70D504BD474B398178D642CE22BC1C04F570BCA2010F3FD01FC78AE1DBCA71DD06
	0CBFD3BD959870C504BD43319A601B899B247B52C158E6AEE781BE0E308AAE178CFC1ABFDDDF9EDB8E70B642DA297D2BA56C81717DCF600389FB2A846DD9887B146B46821FC4589C0ED9AF0AC84F3D8831FC72390830FFF1FB9E407B9116C1BE87913605E3614047903608471188FC5A4508DDC2EDC98EE1E7F25DF660BD889B20B8FC893B03E3014007911644630886BE1130F29BDE37D44286B916F5895453CB909B4D732D823E1E30F18D68DFABE1B7F0399060238873A37F62890B623E24G1FB59A31BFF25D
	FC60ED047D037228A5ECAFAFAB3F512C0FC5F1145235A4EC293816402790D645734838946E4BCB914B23BC8A8973965797F8DF426C94C7200C71BC42000FA5EC8757CD82BE1530F24AA36BB23A4F39DCB370CE42BCA98F6F4B68DBF956111EA1A87A8E91D656F7606B0BE0B376B2FC4FFE9AEF63D91FA76BF25CA734240E4956825F8C643C9CFDD834C5FF66605F5970CFDA3FF8E3FC1EF528E78ECEDFEAA59B455F4AB6164C9A0A8D51C73BEB714876753E231B7B9F1ADE6CD7A3531BE49F2E1E34EE3FC13B67CB
	587B7BE13467F3393E3F99GD828869776768DE8770AC9737634C7A24A40DEBCE0A98ABCA30076AF196C7FBA076673F38374760AG2B81EA14526CE4EF3D497E2FAD347F4B43401EAFE001C0E1D2DAB459FB57E63C7F6BAC3A7E898676468116F9850CC52E90E936AB68FB64A67B3F4E277DD035D7686DB581568D64A92579113DDDA67B3F6C3487A64840DEB8E0B1C009D2DAAA591BE3327FEB9F2DABE4DD99111EFF25E63F9170F5D79A5517274A5D6D7FEA86BD07C6578BF8FF20E085AE6DDFB2597F75865933
	00CE2C46DE9270562BE0FCF215565E9A137DDF41E42FGF4AA2F520C3D00EF85F26A6C7DFB39717E2F95F4FFF8034EE80D3D9060A30162F4762EB6597F35106CA503CEDA205ADE8E7005C0D501DAFBBF1B6C7F2AB25D7F5590282FBF6D92B6E8106683D8GD028AE6FBC137DDFE365364340DEBCE0A9C099D7CBFB952F26B3F6AB0D77FFBDC8F53F68EA3D3D9A409A016CD21A875973B2597FA56F3573B5309788D898D0341416C076AACD76FF5D4F4BFA1C012D4C3F186F7F4A03B41BA63D9678A6206E3FE84B7A
	4355467B3F2E21F64B93F47CB47602008F870A51593B71BB637DDF7F2632CE841DD40D3D2CEB202DG2A38C6EBEFEF15717E2FC6722F8EF41A2FD15BF3G6F85642F4025BDDBF406F270B5E24FD6A47C468125DC0357631FEB7473F8135622DECE1078E6F8917C56G3177C745DE2C7D5547734D7026A05DB7431B3573C6AC2F76A01CD73AE32DD17AA025FBAE50FE931C257B84617EADBF7618174AF9517A61FD6BEAA57FF62A686F799E42F7218240F6DC9076776278BCAAFFE76ADA6C6FCDB94CE46B00EF859A
	3C96FD392DC476A529BCCF6B0B22DFD6867D325272A229EF77DC292C6763729CB054933E455239E93E57627E1CF45DD9E8AF294EC22B501D05467465745F86347BF36ED2697B8FE87767203E08E5FC199CCB254B33505431BCD4017D6A28EB316CAF5F09D85AF508A5EFC72C12305D6508B988AB6663B166FF90E1A104A590F6B6E139FFC5AC08308642EA4B9073A6ECF29F2E17DA889373E5CC2EA09831CC5A0753CCD83C83F57D02F19CD34D5FC1E81D82ED8BE127F3AC73FA68DFDD0F58C7FC4EA1EF815C3B04
	F52E64E35C79505E937653919CBF5989BB4C6705D2C04E67FABAF3E5557F2EF7505E667E8400FF9620046B45B72552EE905FDA4A075F9A20D6A0CF20009B4437241C52B72058771D72246FAD25C95FC9F2C85FB7325CA03EA314A0FD5707FDFBA88130349B45770A58B70158770358B70E72EF447219DB79FF6F20897A3FF750346F20DDDC79FF6F20897A3FF750346F200D6AB35F87FF8DFFA654C09BD3FFA3763DFEA8F555771AE4D56F03CFF9G5A5B87C55F0B1FAF81F6D8FE72BE78F80771BE78F17C596385
	723EB7E2FFB12504DD3B5DDD763E7D1D3D125CD45A539DCEF2DB2B4DE35A47574122C1B60EE21ABF5FDDCC1395B1A5BF280EA9A3C49B5301B263187E481F4F59A01F97023EFAECE0365BBADBF6595BDA711C15FE0C290E6456776BE37F19641AC9AE255FBC76E97C5B1E5DA0BB980231B75B5C457E11A27600A8F56C013A58A37A0CE3FF16572590108F8F41B574BB685AA5117FEF146A634C30211C75A6143B44E69EE728186786595C1BB04EF8073BB827AA62ECFCC89DE76DCD5AB887CA0D63BC1B4754G724D
	B7E11CB98E745F1B7C5F6C5047390B64C6135CBB8E73B8E772B28D8659B00A738FA56E621C2608B373E1F51C093AB8BFB639BF970BB5D210375E0CF1C695237F05B7237F9F966B631CD302F2D5A4F7ED09F91CE22FDBBD48B65D0CF1DE5D6BAE4E990AB843B5F1FA5D2C0D73239263B8BF63F97B027CE80AF3D7AF7A9FC57E1F5B2B0FB3086462C96E48E673B84F636B3549A01BCA72B9635567DFAD65F37B8537C4245BEE4132287F59D5D9CC362A5B6460C754E551FA0B36AC4E1CECDC96D772B561EE109F3C05
	5E23F2D1BF1BF89D8984599012FF28D89D4F0B3C4E44C3FA9245D3B75BDDBC4A76F8B0DA9DCFEE28B69E9F13EBFB9DDFD7AEGF9DBA8DE5B34E3542606224F7E474CE33C094BBBC05603649BD729E36C647984C1FAE8A846784E94F7B1AA5B5D26C7B57729AE46167546B18E70B5719410375E0AB13ECDB1965E0ABE1FE5B50FB1D02CE503EC9D492F59290EF1A79F87BBA15D73D60C7101296EE2D43639150F29E38C39D59B630D9B0CE3FC102F490703FC944558E9459847114F69634DE3DC486F2B4C3FC52467
	7C8D651F50441844E32C01747A3F5173F30E3B98156DED6E636A98BD7E260D3151643952AD7633003C5F5FB0466B69F9E1A11FB73A684F6D624F0BD810CDA079FF1728E3947D292C5B6059F99B46F8015B9815EDED2AA6463A5B34B1FA1844684773EE8479565B74B17A9056CF7D06005BB00E2B8D7A8D61A497CAF2EF161A174FD45EEF088459980A7D9C377D060F9531DBE25431DB4334316FB2097D9077B59364F3425057D5B6FD5F30054A2306645EF1F14DE772BEE2A3483606E1CC075D7661E7A9E2EA9F23
	0E49C79753D813BE42BB5CD7FF108F8C43EBB7012EC98C79DFE6F06D6AC8AE1164163838F66B397CB810CD3B9D63DC69765A4DD65E1BCF2863AC3ADD9B679413EB77B94F3B9264EBEF47B80B69BAB9EEC77FDB8D7AFC07C94E0B64D63B381EBB459E89108D20B86FF25B671B2B08B3DA93E734AE4EABCC2E676D5C57B810CF24B8AF233EDC769D687F9803F1DA924995105CC4FEDEE6CB1F232B17EFFBC05985545F2F388373E9A62C0E744FF1B1869073234DA0EBA779DB9C6A366EA131F78852836E40321C315D
	DDDB273C5F931FD417E54C9D5A323C5F24AC47723C63C1BE09E23C0B4AA867CE7479C597F5BE15573BC2102DA079511A98EF667934C2FA771D98E31A5B981577FFE42CBA4651F7EAE3BCEAB266BB0F5755A0108F3D132EA35D57896473BA97B17EA26EEB10CD8BC7791D1A987773980BA03DB29CE3ACEFF09723725E8FFCCA9D23BDDC9B6365A65771B61E379364BD43B146F369BA86134FA73B0851DF6C098359E8125F2F0971E41ECFAA24E75C05B1FE6FB6C665FD6F7534BA460A3B34B1BEE19263BFF95EB5A0
	DFFF9746F8AD45B8F8974EC1BFE8F537A72669F52377CD1EF22B772961FDB8F3BC5373017C836EB2DF27C58AE57E733D5817723DD0D1F7E94FA07C8B47E36F521EC1D8DA076BEE8910D6FBB72ED3CC9864759B78C842623762FAC69CE163799EB16BBD70EC3F07767B71FB239078DA4226527E46A642C41B9B843C05B0D1763140A7917697BE37E435C03F4D0218F827186FE5A42C15370B9E407B91F6C05C27400793F63358738DFC222CAB76FC831F93C1EBBA64F3A1E1221FB9883CAFE1B716E09E01040D63D8
	9870510409F7299200375E0B589934A7B31B308B38EEAB7003045D560BF97890E6653A81400792F61D58738DFCA2E1414518C7AAE1E2BFF65DFD50F73A8FB11FE24C43C958980EF983BF1A301339FFA140C79366C17B3B9388FB076B5AA2E1EC95295E5F4D72E8A6CC6C6FF6G6FC558C97C9DE1FF60C3892B65F3FCD1404791F65ACE4CB71930D57CDAD65D9F115EF83FFAFF379DF8AF426E677E79839FC458E65A5F9DCE18582B1A75G34CD8FA0760B836328A4AC03570DFA606D0465087D35407B90F6976591
	C058FC313794784C87917B047248A36CBE0E59002FA74CDB5497609D046D23FD51DE047DA87616821FCC58D514C7DA94E27F917B130137C5297328853E1130EFA98FBBE16B445ED260E3E4DD4AA31130E5BC0FD460F39ED2572BC260AB89DBCDF95493164773988DFCA8E18314C794E162BD096C3C0874F8422E92658CFC464308A50BBD5F40D790F69E5D17F50409F7G7D018FA1ECAB170B84BE0E30F1BC36E460B39FC1ECBB35A5F9045D41F53B0177A26C11923467CFD8B6578D865EC2588B221D84BE053092
	72B9A3DA3D3F3B9178EE425E6C45BCBC89AB977DC560038893FB7043010FA34C1376F7A793F62A58E77DA8540DC7E9BFB645E1A7CC7481BD0077A56C6FBC36C0604389DBE6436B96C358C99CCB84BE6DB1447CF93EB9409790769C6DD92EA46CF1AE678DFCGE15712FF21045D4DFD0982BE1130C322EF82FC666308BDC9F9649166494B2A9BF8AF423EA925EBC9D826381640C79136D6741701CFA66C03ED18C7DA8CE2C738DC8370F642AAA88E8F423AF89E3E40874BF2DC5782FC8CE173A80EC442AE65F295E3
	20FD99C377916551BA864FF20E28457D59CE400247507E6C0D99D29FA6F18C6E57F9EEA1134DFB826EAF207AA770DB74E767E93F2B568EE95EC0231F40FC162C457EC9A8E049CFE0BEFDEBE94C75A466F3B917ED865E896473A46E8B4AE7DBE92577F8A73520FF01CF222DFF2FC5AC1E305B96A016930BFD408B8F0DECBFF5608875468EDBCF74750E727DD175315ABE60E99CEF0A557681338EE39937C3DA94457B6C884F4FFECCF59EC651D3227FDAA37DB6BE256F4FCA6534D329E71764BC24DFDFD33DFF2B3E
	B593A84905C93F5126FA6D2A33C392A439F152EF6653E6FA4BD4E75064C9F2B6693796FE1BB43AC26FD615DE37A463A97D7A41EF10215EF9AA3DF0C9A6C67ACD045FD4C33D6BD5FAD94F8819FC693782FE6B1EB1525B22BA5F26D912F1C83FDE706BEF281759E7342FB7D81235BCE3BE0E095524A581EF0D03B6A0CE46C59E8FBB143E95C8691552EFBD7C3646997976F11FD24FA949F8CB3F2361B744D06FA5870BA9D2139687FCF21CF9BC0C521E556C13863ED00309F7408FE43371F05533B8BE3EB4DB3FF7B4
	B49B5B4A3AE745BB602D706B84E25F4A9163568A5ED772E1583308DD4C7725C5829F73AC3E83FE60883DCF7E9C14B1108D288148719C6A7D056FD37385FE74F3389734F635F62FA84BBB0A525B3EB3CA0F277465B5C669A914DEE8181E71BC264FB6341FCB69919B0C520BA87DAF9B0D52EBA8FD03E17E0D149E205BAB4B526D147E3C6E9DED166EC1690106693E147E16E1FAA0251FE5189E76BCBE6736322DBE78DED2E91E557C3C3D52A2F1BA1B506BE32FF2C87BC77D164BB66EABF1F5E6DF29F4401B107D3C
	14BDBF0A484EEDABE4BB63668D5D17D7D74A3EE42E12EDBC5FB7F4DF3E6ED73E77A5B0C73F5A3FEFD74B36D75B066E5F566FE47F7A2AE49B273BBC2BD16D5F18D23DFF8BEDEA7FAEDFA35B3E43B1F47F6EDDAB7B77EA35ECA321F7687EFD6B507BB760D07B1739CE367DEB49507D7BF63D6C5F568D328D3F62217B77E2315E3FA5A5EA7F7AEAE55B27758E5DBF3F0D32FF37ED12EDAC9DC6798D746A7D3B27D76D5FF31BE55BEFD9DD59AEE1364B70EC4C955FAB6E711F07F2B6A6BB03D1687E7C039C1BC73D627E
	DCBD747CAF7F0F2CF70D4BEB26561B52A06BAD1EBDBC3F17EC177D2E5FA15B09F6F926A866AFE4979C533F8BFAF427ECEB592A2147F243DED96F09A9430BE5CC3B9C4B388E594EBD47069E4BF42BBE16857BE4DB8BF78EBD16037BE53D2B268EAF168BBB65D8EE6F126D2C98D2FD9632279844925FAD5B8A99C6AC153D325E2AB9430B65D7079C4BC9FD321DA30E214792695047F2C53FEC6BF1174FD3F5AC198332DE7FB0E3797610220DBD7CFB44A230B38C1EDBCFEA1E7BFD03F2FE8706715C72BB22E8F70FAA
	5AB817BE2B5BDD27C179BFF7CC367563B07AC463474B7E64CF10EDBCBC0CBE51BC03725A2DA92F8D93E55B7306D1DE3F25497E1DB1C936717EB01ED3358657F81466B99F1CAE5B769D46F37E714932FFEFE548B6BA07D1FE3716687D1BC865A778921BDE6620CDDF8F722748BE1CBB247BC0FACEE64AFAC98D433B8FAF1FA617413D53E5BBE78C299D903231A57AD8DE1DA15BBABA0CD86AE74AFAE30799CB6FACB9167332E5BB05AE7B3D6AD88E166AE339F536EC2BEC98317029C3C9AFFB1831D44F13E359B3DF
	3653B20C6B92E4F0DDE6FDAA5B5ABC0CD82E79C2567BE61831BC1CAF477206626C7801E144E2D145A266FC9773BD0351B036CCFC9E6701FF63FBF8D3014F09C76CA60E95460BEF3909B9132B6A189F523B6D0A3F17571D78F391371E50BC24F6FA50FC3FC389DFB72F0272280B477916373762FC0B93B0EF20A020A82024F85A8B5DE03CBF21084F51E70E05B29F0B32D73858C3F507F82F85E46B4762DE0A4F0F6F3D16315A3D94B1A6FBA944F946DEA06FCB3EDA8C76CBBDC37BAB42C96E2EB273181E666B7F51
	A09BC7B17D5AFFDC6F353C200D693049770D4F91EBD1A01F7782FD3759E06F6205FD98D39D49BD54270FBD01641AC96E300B77DAFC7FDFFBDF83D6D51575CF0AC6451496959595B5B663CCCCD151C48D53D0AF92EFD1D1C3C545CCB4D1D1D2D2D1D151F4793A22E2E22222E2DE8D8D8B11AF85939284958D9595158A8B9474D2D7C03860D5C17F6B6CF356BA9F775C0F03637F851F71F936177D5BEB2D3D565E67E31F3D575A1B2DE5F7836D403F5076B18E7B53EEDC0B156DA7EC78B80D6763DA00BE70DFB8AF98
	C9FD97CB7A4FD271F1DCC13ED03A17106E2FF67C041E66FDF301B667A534F3425945353C24345357062FFC883F2F9850373C04F6C6D3FC03BB69BFDF454731C00CEBA13AC9F662867E4968FD01B6006CFC28682C62DA2C6C7CC32F3A1D8FB01B6201DE970DF65EAB4635C4237E31AAF1ADA9E2DC8B51DDEBA72E057709E9G5A66E834736A330BEB09D65A19E923BF7378389620770ED609EBA17D9D4435905DB5EBED5BF9A773D309835AC4227F79385C3F2D0F792D643DBCC9DF7AB236458D7B4FAA2E65E5E5DB
	B4762B370509FFFEG7D404B9A62DA00560F682394F1AD6F71F1ADD09ECB769CBC3B3816D71C0DEB19487AB68F68CBDF413E3D106E4116D7D067A5F65E11656C39E686DA9722DF2D089558447BF4C1397FABE8E304C39B6D4635D859D8E44367FAAB6F2382743AD751464DE4E37E2BF43E1F1DFEFC1F7749G5AFA227F2D4246FB190D96A8F7FD95ED9C65B09E42EEDC4B2BCA9B3BED586846EC8C847AD032B10CECCCA01D0DF67A7182560F99B1B0B60A31EFE3AD14B744200D1B4FAE2EA5C6E9E3338D9B4DFCDC
	8B50FB46200DF70BF1AD2473F6BBB69E6663DA00B6BAC6FC6E4AED74657D53DE03F767EBE8635E330BEBF9CDE9E3198D9BF772F1ADC05F769A794D0BF1AD04DDA246353C06F65C2FB2EEF8CA0CEBA13A29F6FC618DFCDC8B5006136DFFBA3B38165715367FE24376D1FCDC8B50E73F0E3AEEAB319E9B0AF19035C4B74B4E18AF0F0FEB815A365751A653594535D85974140DB17C9BFCDC8B50CFFC9D7BAE015E7D61247F57AAFD37D10CEBA13A8F6C0C91BE637D5F01B6F5B25A39756C62DAA6EB0CEB817A3A49E8
	67A2B12EE5B26A3FC3E54C57AE4635901D6358B4CF201DC0F6DEF7F6F1ADD6F6DEEF23BF7967CDA45047101D3708F1AD3128FF044AF75A94B12E056812ED4435AC9463DAE2311E43E2DC8B71DFE367DE3E130FEB81DA93515F250895607777710472893158166FBA0C95309B57922BEC4B57ED34E5B26B47A8200FA59B1F9663DA5EC01D1F35731D73B69F57823465C4BFDEE1A36F135B86655DEF200DEFBA3451EEDC4B9BCA9B2F33714DF7976FAF8F747EEF200D0DE2DC8B697C059D9BAB787B9AE8D35FC47A6F
	95B6F671F1ADD0DE71A65AB8776C62DA5ED45A68E1239FFD78389620F7FD93EDEC9563DAC86731F6EC3C110FEB815AB0225F293011FFCEA4C2F97A943431706C62DA26A8ED7437E1A3BF162A857A06A9748E9663DA26D0DCCB5D9063DA9C725908EB594A62DA207E89D31C08EB1902F1AD3B8DAC2EE50A522771C706C7CCD17AB4C6AF213896A82B0BC31FD39F194FE9F8DF105E874A42A14544216F67D25A8BCD77967A164EE3E79C14C33E811271ADEC3F120DC373112C9EA25FC94DFCFCBB4D2F6174F7F9CB59
	FE86063B3F25EC3FBF105F2DA714A5123D53DBE53E01EF8B3E01422F5B5B36FCA6BF14696DA550798A3FC1B6798297A1DF048ACDACE03AF7547C76DC593AA77F77EFA4FF4FBBF56E6720D71F53BA78F9775758B51A8536673E03F371F3583EG65DC7E13A05CBBDC07BE5EF5C4476F554F71FA9256FD9AF9C309771A53486BC3F4997B58B38D72997108FD3800438CDCDE075817F47EC6A1511DE17B843AC0FE9CE153995D84484792EF99717A915DF6B6B6C8FDF712BE6BDD42663339G488FA4113F7EFC64ADA7
	3A250C170B8DF1A3AC0CE1DE1077A14C44F883A1EFC826D8AC1297C6F4A9A44F709E4593B02C9472356F4965B5C1BEC8C51E1168BEA7F97E0405B0AC94721104F9B35EB8489BD390FB1464695EC76CBE1257C0D89B1BC3EF033C19303FB1DEB748E724A296CB72BC09CE3786654590D640476FC05E1DF8FF643F4793601ACCC06CD27EEC8C485791361CE2FA1A89AB21D8899F428679B98248C79076B23FCFBD6493893B012D11E4FEG3CE908BDA246E8FCC0F1E6942B53CDD89E535995729E04BD4FF8A7C23EC1
	CF31DDFBD11EBF516D68C3F9699FA2466FC94945336490D64F30C248C7133C938C63E2DC2A080EDF3BE904FC9BE19B29DE8BE119D4EF4807B87638102D49724F6E28ACFCF6C7C2F956C778DE4B1BCB63710F703D36EFAEC76B8AF9AFC8FE9F21AFE996375D9C7AAC2CB7681C5DCB35ACCF645BB9688C9F2F5B5503B4BF7B916E25BAE9507EDE2A6F8F5A5FCBF565E97BFB297EF85A7EDE2ABFC84E21FE5329B660EDB973AD0E9FD293316D3DB6A0D6C1D8F7ADE2E6428E9166C3D8BD63E57B2692163885311C0F91
	FBF1ABE20D041D2EC54C1D3067B7A296C458BC5ACBB5113097E8AF5516FFA3360A76D275A4AC1876D28D7BB7DE0FD5C99C0D3943ECEA67562FF5B8EE7EBC15BDA7BF016B7E93243D006D9B45E89B3AFABB4C5024C4FFADDBDF667A261B68C33FCBC3FA6A2BC149995BCF6DF67E5C7445FB703AF379842F3B6F7658BFB7FDF00F723A131F1BFE7B9E65F5A7BFB77D45BD4A6BCEFE46762D12B3366F36B8FFEE7AE31233ADF49AFC1D5E62363B94E4E4C94E3638D9032F4E5B8A7F5B32C2D1CEE53F733A3497093A38
	C976F3BE2C417F7B0EBEEB1F200F7A647A5DAA597379100636FA2CD4546FAD49DE4B93B53455BBAAFECD158A3F30AC09AF7FA88DFEE1E592DF7E9FA43E7C3E9A7C42BED271DF5E24708B9B1478775FD2643CFE5E927F7E7F2990E50C51607F9D26627F3DD0F17D3DA2099D6853607FBDCD625F1FDFA54A78CF7BFF6F147877D79EF75ED7684FB5A25F297D5AFC051E2D95EDCB14786F7F43A99FAE1EEF0944E77FFE8D7E5F27360AFC9BB47AFF5F30CD54BBD462B73FC703BF6B8DAA3E21D36AC5D9B3B57866FE37
	D3643BDB23ADA79AC4DB3CA43E741FE93065000AADFF5BAD4A3AC003AD0B760AFCBFEB74B33E26CD34A520DD1463254116D7D5ECF97D7050EC59AC71277FC223AD9D3F083638CBFC69AF50E0CB280AAD371BC4D9968D364C13785397EB34E5DD37E8CB73B1D1CE2786DB6ED5718D3ED9451FB9D6715EBA53A356F7090677561FFAC51D434C220CAD9A7C3FAFD7E97FF7A43E7C5BB43C67679D9775D9E791E584E9F84F4FD2E92F6F956D55AC71737FD2C3FB1D1178785FAC7171FFD543FB2AD8250F87946F79C992
	FF7E939A7C3FE3A47E7CB3A4BE7847B434DF004AB8C92F707FAED2717F9ED4717F66C657D8BEDE03EF6E45925F7FA7B57A667EA9C5F2CD270AF26C0F2B6577619BAAFE466FC87C7D3BB45872C33A487708C6DB86270936F8CBFC698D9A7C3FFFD5717F7E1F99222CB28D36FC75294877769EED36EC1FA55A521DA94AD92521DFACAA7DF245EC09AC8D36FCB8C7641B2931DF964F95ED291AA74A592F41161B557C3F59DC02BFFC9307FDC27B5FAC6130E8486BF441B8774630AC487BFE00E7B2B4F10F89E1AE3CD5
	72776BCB4E7D5CFB0A61DC5621BB42597C10A1C74867015D1643C17AAA78ED0444E1A6786567963A12393661AEF0D74078793675E55CFCDBG6443A145C26275DEA15BB3A3B5A9D81F1D94CC6B9A79123FB1716BB79733F529AAA8671E0B53883F1A614D921C1F9B4CE1B81737C0D9E492563F38C66C2795123FEF5FF86E7BAC641C56A12C8130FDDF2A1203758D49F86D56D733B9F14827A7621A4709EA1CEFB5935D6C9A6475A4AC172DD178F0F9625DC56B9681C4778C5BC7A68A7209046D5802F419A908F555
	31F9B14857C39266F056495713672DE37E91D0EEA21A2FF49C0D395DE2665717776B9C791524C8D23A44AF1E3BCE3CD202E99D102F739D36D6B491F0BFC8FC9D3E279D2DE15B2BA3DAD207B0D65FA737F39DDBE30A833AC432F376C2391D5F5ABD57A245018E7929CA9D2EAA136B6043562A4A012EB695F5D82A50E107C39D526C68E0863972357C36D2398EA7797D8C014E1DF4E81BAF57E17CFCC7BACC3523C3G481DA16B6F1D643393C2F53E2A506BE3C6938965B1C4B3DA21172FC33D265951ABBBCDF99D66
	13DE39E9D8E710C22F6798CDB91457268DDD2F99F674B2DB69350574F221BADFD06875AE237100F2EF2239C721D734C33DE65A51AB4CCA2F17C82FC82A73549A39DE63593ADE9C14A7924D538A3DD2329D69B54B0EDE79FA25DEAB57101F189E6B5C5407F572799E5EB729552270E939182DBD5483DF93715E2270057D8BDBEFF40572F1FAFCDED558750565644C37E3C3A04819A63321157C21C2C98F8F9BFE06BE0CB68A68E209B6D161A7441FF512BDB5D81FBB95E96EEE1477433F6C7A8DF1BCF36D5850B8D5
	598F3F97FD66294E8DFDF23D7672FE72D06EC2B4EFAB74DAE2F73FE561BDE6D3AFDFAB3D0ED3FCD4G1489B1F00405119E07681A91FC0E09A61AE81ED258B31951A4C0F9EABADE9B5FBB54FF369D7DCB53157A2FA3BD2A52F1EC75CA5650FCFD62074837EA6E50FC049EE6EB410D921BF87C6F8CEF11607CD832F9AE2EB31B20AC0C6C759A9C1A5E2E5FCA79CA2771E35EBA6137F91A759818673BE903ECDFB5014ED57875344957D7AB6573916882055FF01BFCC699DF0CC017A87CE6CE374537CB26E72EC0D7AE
	7C5643EF0B021767CB12795A19859AB761578BFEFDD579DE59AA658B92E8A2045FD878554DD0639BAD33AFCB204993FEAB6037C115AFCA666355A650D804DFF77875D665DB243AEF1C1FC09BB2433ABDB1C5AA4A62A01F1A815FCE99A24E5791AD5BB7AEDFA82F92FE9B6157182126DB31EC5F388101E61C70BB81FE7DD5799EE17B4605AA4A22A09F1FE15B9EAE254F141767C03ED001896B871298781F3EDB2A53F2766E9D5FF1772DF8A67732BDDA6FD9DECE55D675F57EA6B6FFD08BFAB74C446771AFCBEC2F
	730FDE026B7CAEC4FF779269BABF7F4C09B360B3E79C50054C44EF6D72256902CE3ACF71DBFBF0A9735904FC83A4A361354B18AFA464BDA179FE0A4FADF3B236C14827FCFBC7FEAA36FB9C7C1DBA8B2E2DD9A246BF9FEFE75F7479337867E39A6177B13CCA0273B6D4B31C4BB7C2197FAC7C862CE4BEFDB919505F1908BD4C7CA59AA1EFA4EC2B9E3F9F5DB3D177F1155CC4D66683C17AC2C80D103A9F4079186BD9D9A450A6C24A199D2C2F180DFC4BAA15779E4727565F19923CD072F7238DFA71DED8EFD547B4
	BB57947E1D2610E37FBA2CE9153F6B3FE75F7B4DE027F1B60D992AF14C4047650F2BC6DFE0CF207101948AA996D246E7B8767806FD7B67C33E7CB31433FE21DC0EF1A14AB1824DGA4AFC88110A209674DC536FD492766B0DFBA20CD1F93AC6C99503E85E51682D6874988498D5244B9A8F3EC1D7A187386B6179082F4611004FDF916094F0A29BAE76698CBE90E51139B6E88FF47CB7618E8BB2DCD4E32AFC5B927253ED58B34497179CA14133ECA1473EF0DF23EAE9065DCA871D7291E2FCD4E9FA5E7209FD6D9
	FB50170A65AD9AE52F1478185428489ED3B6F459F7150B3CBF29481E35DEAC7750A83BD8625B71210A6CCBA57BAF5E25D1F640861137EA0D356C972A447251451FABE5CB66414BC16EBA123B3EC664DB60541A2B28D3C72D48EBD1D967F01778DBBCE8F6DE2798093F4565CE2D8D08BA4D1378DC5C2F32B65AA37125F8DC236CABA53E93172A78DBEDD1416E6D3356612D9D221C35FB356910A5594330C1A61BFF6F04313591A1DEC57277413967FECE7B54B92D039F87DC477CCCF5D9417A4CAC9C9B0CE5E303DC
	AECFFE7C13E93CD0CAF47E9B10571530683948AB46GECA2DFEDCF22F3E7BE25B110CFA0EC8C0B8148189BAC0981F8FD8172664CC53AB30C57847981426E496630F1F303A5B1GB9446BCDF4C626F39C64F573905B456832A0AF46G7C19F8F3092E034D39070F1F24B793164D5E13EE1077A46CC2B68E75013C9883903B04667509EE4FD61417B19F310F99E600FCA1E197B35EDA480BB1GD3C8DEA3514555203C8904DD4B5A4A9F72A1044DE4ED9589F9B18620A79B6545925D760DA82FA29B31779956G7996
	421EE57DEB063C988370AD45G38925DF81297CED8A51B6B08498E1644G7073B08900E5AEC03A94B687168B79D2420CF4DD559136024DD946DDB7C96FC9D8B79F4BG79A042BEE2F3D791100FA16CBA66331F88F9B186C0C73E73998B116EC6B35651CC58D67E6C86483B90964A64F9C0DE0C81A8A2F993080E5FCB021397CF989FC39DF643A4FD7AA2447833B4F2A0AF46GDC4DE32D137479C4974930AA48B79036006AEDA36CE33243EF910E710EC976C65D324459E7A9CFFF6D92797760FB06214D6124AE9D
	9ADF25CD3E10C56A5F4B7CB756DB6C9BAFF2917F0D15C278C78C0F15607C37576825F8EEC5A2143564848B71055DDFF0F8D08ECF7FB549391461390BC51C17330DFDC3F2F9D7A80B7A9C7B65923666DBE18856B79990DB4D7608B1C15E05305B4A68DAA26CF1767D978279A84282AC9C968F790C25784DF6B7634D3C8A3E752E42EF3CC006455FBAC9EFA03AADE50E5EC11C8C7A263B4A76F71C6DFEEBF1D8072DEFBAE792FFED4E2C64B621BC585553565E23937643BFDC0E5FC73EE0F36CBF027435E0F7A47C4D
	35814F77E2AA47A75FC7BFA195791A005E7D1F701D8C3F2E10B8BE615CDB3D163545D81021FB011F936672DEA0EB917C71FB6EFAE27B36BFD1295C371D2BEBA25071657BF32565ED277972GAA3FB3DB0DBF0C4AFFE913161B0472682578DC08603A88474B554EBE9778F915BFD763FCF68248636397222B1571915C07DD018E639B66EDD446C714484A6BB6AA63A35E11151F5C280C0F182B1346C704CA7CC33397EA33675205E8CF61B234A7E021FDFB5EDC283427C3D6EED8243467A5D9796EC5766D19A259CF
	3AE60B1DEF03B502FB1CE04BA5DB5096A359F26F9625AD2BE43ACC5E223465082CFCF11D5216F6D9794EBA7B31AB1FC9FCF06FBF650CADBC6D1D5F4A67EFC25858561B33EB994EF7DC2F172FBDA5CFBD9BDF0748E54A351B975978B2867078E538BE546CF09F9DFBF564FE2174B378ED2DE2FFA2B646AB843A0AAF50CE2F85F2BB134E4A5741E42543D58A9DFC188E9620F3A59D9E4B166BD03260ECFC9D7C3ED07ABA1C59A05761CA567601C097CABA98956D7028C39D6C79BAA4FE2174F5381A6E1F74DC2CF3F5
	1DE29FB0760D108365F9C4F34392459A3961ECFC9D1AF215576140D6542B0D6A4C5AA6576B2BED2C4F205C15E8EED26825F72817BDDF87BFAB3D965422DE01D4679D0AEB69A1C6938E65D1C4132B3816AEF268D3E04F57A1F339D22FFEBA8BACE7B95679E90DDC2FDC76FDD5886595C493B7CF2E57DE07BE9876FC9DCCD6FA5DC7FAD9284EC6052F433DB6FC9D1EE05F816E4067C93C8BCB64B69572FB10C0F958F2FCDE1DB9ABDF875D8A252FC3A37911E42CC0BDAE3431D76A0D0C56GF479C47B19E28D1E0F37
	EE0272B62239D361D3B05F611A3CBDDF870F954AFE8891776824BADB94FA7573FB1DC2F9A0513C24502B6E2CFC9D622C74EAA5DF014495385EB00F304CAFD10F2D8ADF07C02249A51AF79476CCE3FB0815C3F96D17F8ED941E152F0379CB257EABC88F17AF71FB25112D43990E71E779715F886FAF65F48BBD065FAAC94B383CD8FE51D2DB5F909C0FADFD1DCDFC9DB75B2CA365AC650BF574FF51A834C71ACE5C8F123BD7386759AC21FD678857AFF79FF29B11AF0244855DAE064479ECF36B38DC1BF38BAD5CB8
	F4A54977E072BD202D83A0C5C34A488B5617C2EA06648A4987D2B824C4C839AB037575104C103CA085C10A05147595BC83A135C1F207647795FEAFB6ACEBC862757FF488B658539F5BE975FF202DC7DA9276C4E7ED0B571A563635552EDFCB6AD9BC8CEC7D7F1D78E75F61533657D67D58FCCCA4DC63B1DF6133F273DA6BF90183EBF1DEA06B6BE0F63EDEBE7CD6C1621ED37C1A6C24B21ABBG5C84499D128F24502FD1F6C2057A3BB1014D6D4482DD82517E7DDB75F77F4BEC4EA7B7BFD8DF88095F37EAF31AF4
	5F2AF1D7734F57E6A86F4EC79F089BE85FAAB74078F9AAC39A7ACB848216C03446B47A8EDE05FE91D7B1DAA364DDA0F92E427903B5ACB61267CB273DADFCD7212CCBF3910BA16C35EC448C85085D5406D8D381BEF37ED2BD347942AB968E0DEF5C962DFCFC9B0C1744F11E54EBDB2772E3E7AC71FFE7CA4EB60AF0AAF6D814D3F2CC14F319642C23939A573D6F141CF57469F1D14E2D595A6494D9C4B93FB9A94A5924D19FDFC97C62EDDB2C5788B3A467811DF2FADE1B67DDAD09BD3CE22B356CEFA46B3627B54A
	BEA0D953AD2C3516FD2FE46DB6C9E35BC656093C3F59E8ADFB07E46D76D10DEB5547A4EB37CF28487658AE16A7E8D8BF7FC892975F2CD1271512D878DFD57C856AA5716D77E8D8BF1FA8B9F76EB20DEB5CCFCBE263DFD0D9BF2F144C2B86EA14FD1BA4AE592232D63ECA85FBC6E57DFC2EA4B6F926467573F2C96CF125CC367BF136B6788546390DAB60FD47A642AF37DF2269B8BFAE67692EC8C53AE80E2E40965D73E6244BD98D6356553668AE6BC33AC620B159242B69C7BAF72019E0136EFD228B821AC81BF4
	379EC73AC42049AA34C577338569CA01269E1229D00DEE5989241B886501B6691EA63AA8A8CF30C9F771C9244BAE0231C111AD3AEFCFA1DD835098ED523DCDF4EEC063ED136EF783C89788B49110920A5468BA8629DD0A21DD0AED51ADBE0DF4A620F135C9778451CDG1AG1BF4E34EA0DDA45044C34AAAD1232B53D13BC039314496DDA25139C13937CDBA5FA4EA97A80FD021636F339FA56F465C35865B4F2E127222FCC3C9E979CAFE6CB6E6AD0E5D92C0EEE1A90E239EDDCB737E25B8F67B0851FAC35E9FD2
	9851CEAEC35A18D2DCA3D8D02EDCA338D736AF5AF7654AB5826E5BC2AC9FACD72E912CCE116DFFD52EDCA3D829132E911C163C6F6F30B8539EBC5FCD123D88BEAFF306CF58830D0DF539EF84439AEC2F0F5733FD6FA06F8549EF8D0EC7FF4B62F0C3A19F35463E1FB6176209EFC55A50462325C364BB2A190FDF4F1E2E676CCEDF2B0C711843702C354A988FAF7AFEB0C0196BDA34F726ACD6A3E62D9083A17CE616590A0138A29779F2D5E84A812BAFD37327FFB3DB366F2CC0E396FE5DEC56773BB66473D22171
	85ACC80173F6A767220FF68414E716533CE1AAB707F12835275DDC407E5F5B5D56EAEE4F632EC2CBC16F710E4EA3856D7D6D1D66029A2EF4937767CAC1E6C0AA4A2C83F9AE6538FEBBBBCD397E6B8FE51165788D175A07FCF100652DC33E177B5578DA56A11FBF71198173A13E1FCA557822094F540FFC890095FE03FC8B7A54780C5F503766F1649BG4C0F78EEAFD6630BA53EF062532D076F5D754877F02F9ADF77FA64F333A01FABE08144F75FF1A9DF7EE81EAFFEBD7661948B57079D667623855CFFFB4C50
	01B546564EA36DEDF974B461BBF1AB144B7A703B93D8E7FAC5303E2A826BACACD62B53D2013AFEC2FC6E9C8F71392872A5925FDDA711AF33B2D8DFD709FC37287239D4A25F11D3486781D8A8716515287125FE0BFC0B8610AF9B3006EF11EF3BAA1F9B71BDCEFCDE0005935F5813EAFD1831817BE26D60506F274B8731CE8348EB5A00F5E66FD62BF35C8654F573E9641BGD8A4717DEAD6634B2AC23E8F09AF8F3016AA642BA9D16373A43E5B4EA01F8FE05144B73AD32DCDF32A31ED666A06FE1D9E53E11D05A0
	4FD80DF51675295569DD0D3AAECFC2BEBF40E22B555B8679F2B7A25F3344D78ED877C6643BCB15EF62C6FC0FDC32F6E86F3FEB06487724E6BE7E7D58456F5B31D1795E34B0BCE423723D19D4066F4DF0A8AB2FC1FB634A06267768B4DC3F7082D93EB562BBAB284676B8A5C225AC96B0DD2D9C67EDB9D64146G35CADF3AD38C4F2DD57A5225D420AFDDA914F956629A50FDEC9E125BBBA61130C69647A45FBBE610E1723DE3B23929D9416685123FEFCEBB77BE5BFFB927F5887B063079534CCD41FA43A6EC07AA
	66EFDB3AC93A8F49D172412DA53ABF32F9D6A364DD891BD7CF7E590415ED6730G4807931636G6B0DA1EC7AA166B33BC93A07C959A172195D0CF4FFE5733605102FA76C73F424EBA1EC43B48EF3053C57E63C5E53F31557FB363BFCCD2F54DD6DFC09F9D6FC850A3C6FE635F1620329C32BAF4C0ACFD9DF0822BEFEDE7EEB593CFCB748667CAF2300B6FEB3FE03FD4B5669396F240CAD58AE537B0626E736951FD24F5AADF2BDF99DF25899045CFAC3B369F07F90F588F52803072A8ED716228EBE24C3CF7F50F4
	F03162D36A902D2AC3C39F6A10C8BA2CB8BEB49DCA2D7814BA1456296970CAB16AD0DF07BABCB7C49DE29C6A602A2AC31299F570A29DAE358CCD87AFABBE258E612ABA3CF59C6F0BD852E1570921697033951FD2075C2DEABA9C213E28580ABA241D9C1A8EF32C7814BA18D5F53807F4F0A79D6E9D228E8FB954A1C8D507C852A112F4B8FAEAE8BA1C3262D36A1035CDCD071FCBD0077CED284352012169305A0ACF29C31B2A8E2E2443G69705490F5F849218EFE2ABA7C75A44ED705128E1ED6FBEAB827436F2C
	7814BA94FE2726434E5D588EF55F218E75270726438FD6FCCA9DDCD4F538191ED11E24036E4C50F478540ACF29C3182A8EA769FA08A19D7C062843038EF5B0542B69F0DFA72783679FD2DE0FBA9C578DCD07B1D6FCCA9D3CD4F5682477269F6970F55250F4D8E7452754A1D6D58771FAC85D0EBA3CB0C49D5EF028C345F6B59DDEA79D9AC907BE96034D7FFDDA7277D40731B1E71378BA8C67348EFEDCFF8B1BBFF787FBFD3763D87F5F8C8B02FC84E1ED9D6CFB827259BB9073E5D8BE646B885BDF045F8E4D04BD
	4C4E1A7201FC508E3A46ABB8BF411C97715B70821667668F787C340AF3E53BD86732F3D687775D4C7B7B3C493E17A3406E689D344704A29EE2977BEE4C5E992C4F5D09B47BE92FB65EBF49F5BEFE0BB4820D91129B240910C2086701DC5B3ED24F316F4B98200D5F097BD02C2AC7190606E0FDA924C6C8E6C89E8DA8F347F6F51F27436CBB548F68829B503F3500FD0746C2BE0178179E125B3163905619378B7400548CE9G1257AE6489EF33ED479B6CFBB5GE8C3F6219DEB52D1E6BCE0693B0375F9106AA099
	F7234CFD53546D68E45F35EEC067C1349BF239F16B212E4E4ECD662E1AA29D2ECBFD10AA453938A77E3971F8853E5383F7639A14B71B3F4C5D83BAC2EA5C0357F5CD89BE1BCCFB2C67EFDCG731060937720CE55FD523A39CDA704B194FDA38428488B83AC9AD202D0165108725E1549DBC3722EADC5F906C6EBF9250055C1EA964A3AC95E9F7A257256123C3D74BD692AA24F93B09FC801C2D9B849BBF0DCAA2F0C64CDAEC6F9B1AA7292814B5C8B7751DEE17F2D3D344728C5AA2F1C6469E96F287A3D5672DAG
	B3C3F2934A3CC85EA4193CF5A46FBD6A8FDF208DD2480C00FC2C826365391D18AE3D668AD11E096C556D8B56E76D137366C13E82D203846765D51C1449ABC2F981A42F8DF8AC8AF96E10770664E7A5AFCEAE2F98653DC872C200A7D2A1AF8E72297B611D31DFA9EF42A9193C921457C677C7BE70D46D174BEB043C915200153C2681193CD214F791499B87BC9394727CA19F8AA94ACADE462034FF45586987CF5AFAE7446F373EFE521BF8AC27493A2C9030DAC8CDCD4A3A9F9F145932866BEE5CCD4F8E95F9AE00
	F9C01AA81485B451BE2927E57256125FB2DD7BE1AA722281CB001471BDDFE6789E65151D1149ABC3F9E7282DCB3F3716D787D8B324EE214C1564CD164BABC7F9FF6B645A5AD3C5160F8A06A9904A429565B110CF04147903322DEF53496ADE07F57752F3AB97F84AFF104B2B07FC8BA433153C03C9D2F9450556ED6D86BCDE8AF93E108F0294E1A5EFDE32CC9E5D47D3C8DEAC7068FED42CF3C3BE8FD2450FCAF981E91C6FF9FFFB0F3963D02BE98FBB67FBE3EBCFC31729635089611D788C9B7FF131258DBF52FA
	155EBACEBBDF0F6F5DF1C04745E9CF045FGC85CBC29605359411EB5DDFD87CD6D9B7B8E439B3D3D8D5F659E9D683F92CE75BC54E17B5D9F4568E300B67EC70CFB69AC52923B989593240F0F9163398D4D417A45BAE96C620B275547899F30B1ECBE5017B7236FF43A447FF4CBAA6737EF53BF2F60E01729CD5C290F67EFCB9279CB1D5EA7099FF37CF1BE4591B6236FCF44FC7B314E5333153EBF72586D0AEC256F0FBCF63BB3DB697BA30FF53ED0F21E460CFA674F695BD30F36F811AD97545BBF276F2EFA25AD
	72F37ADE2ED75AA2BF27EF7EF67B314EFFCC91ED29C9F57E1CBEF61B88FC7FBE282DCF4D0768FE20F63879107DBE8DBF24EC87F97C3A3ECD598EAF494A4B5B6C77694A2922BDF3521D0FDF7FB11DE28B8F20AD97255B0FDF7FCB3A5296F97C7A9B69CADB64716BCB26596FD3F619893604D6BBE38B7F2C8D17FDA75602BDCD8744672A69007A3B48C58577GEC22826757E11B199FC14081FE9D360670318C2F7FC9447975595F316F8BAE9F89E589AD782D7D831B534ABA942CAFBC0458B7E6DAEFA4ECA6237300
	3C8FE14D0CAE907261041533F97C98482736A176E0A94A4BA62C1045A49AA16FC2587DFC9D101FC898FF3EF2G6443883B03E15110573553FEC1FDD8C796E1E1CCDE9B648788BBE146BA4691F6BF43A6C05E1FB09313978A7998421EAD46BA9289ABE03C75070375ED07911BD50CF5D888EBE418BB643D89CBE67A79C1BE0C3056DE2CA31A30D7589CC96911E0FD7D91443E6A45BADA886BE2189972EE04ADE67AF9C15E1F3067E94EA514306B99EF0E913E6B0D080DAB42BA6A88FB0AE14D106FA6ECA60BD1F205
	3CB7E15EECEFF7BF488791A66E6395C158ED2CAF73BAE06C5E0158E856D60D106FA64C0569678AF98F42FE3B8765CDA4ECB933B78172193FA036127AA317303F73E7D1C33E11303BD89DC648DB88AB2033235D896377CB0F04FCBCE1BB280E74DF91CBE0D88E644B894B612F97AECF189F5551CD98BF3798887988421E20BAE289BB452EA11DA9D81FE3C2ECAE63AD04FC95E157D09D0DA6F99D93A09FC058FBD4C798E15E2C0EE8486B0EA21647F833A01FFB543A0E722372BA5CA06FCDD8A45561C7D874C91CAF
	8CA1EC65A9564E100FA32C07E1291DB0E66CC4EC73EE2C3714305F73578B643D88FB1ADD3B3E108FA26CD7469B8179F8428A69D9125EC54FC45686CD106FA66CEAB34AF3A5AC12E11E1077A10CFF3E84C2BE12B0B7FA164491F687232B6A0677C5B76D95D60CF51888BB4EB09748FB90F69553EFA2640388DBC24F7108EEF97F669D03EB6398E2F3480EC6421A98E6047CGE1AF323507F1101FC8D8FE9776DBGE14798968679D842EE7C19DD27BDF0DF76906FC954AF1730C759BC2199726E042DBE097AF99346
	1F7F608779B0422219ED51100FA76C5DDA6A4BDE441631394DA648F793165FC9FD59AB6FB7CF487B9176FBD6C78864A3887B6BDC2CA31630FFB23A8AB334299931ACEAE7A3E1FA7EDA033CA7E177B2DE9F48879076845591C6D88E6BF3C39FBCDF7A2C6B286F237D065338312E540BF29B7FA157505A59DE30EF536E462DB55575BF6F29593D333EFEE3756E1F2B6B77D7FF3F07468E0DFA746DEA81191EFD684B624576126446BCE1FDB80FF90711791477437D594F452B228E37F6D847ABF531F3647872FAFB67
	8617141740G2BE40D607BFFB9BBF30E7FBBD0F2E6DC145DF359D186BFC63BFD7E50FC6F1A6A0746F762505078DA5235728977B91BE366768FEF6847EB23321243DAA0EFA6AC002DA5F0FB4AF9923613FD3BF27B56799096389E2F3FC042DEE4F5F0FB54C57763FC7541B32E5C73A08846FA1360BDG298912851287A4FFC8611062A0E584876973A055C2EA03648AE98224A0C85110529F8A5267C3EA00E4066489498FD2B824C4C80610A0FD9524E6C896C81E1082A0C5C1527DAF2E5938B3FD92C03F6C63F0BD
	9E67E3521A0E7331DA96787504648FA982D242F1BE964DA44410C58A31DF01C2ACD62190C34545C7E5DB78B82488A1AE080BDDAA872C494247BBF1B1C5DC7C9197A3E531E07B788CE642D7AFE7B3E78357DFDC1BC87B0FEB9335FE0D967038C956651E8FD9E3E681635AC2CDDE3A0247C3C5AE3B1EC26C6F3BE1314117AF74D714409F2E280F6B2BF2955EAA013E4DC6BBC813410EEE8D0A7A386B4B5301AD76D210C49F6EFA4B96F44FF0C2CFAECDD050F177C813F0BF7057C649E96E5A6076F9A891E8E6A9E44C
	37A17B8B61F70ECA59AA958C770A2831A1EF39A2BF53865DBA87B64F3001F3F3921CBFFB3510AFD214F31BFACFD561AB93FE97B9515EEB6C142DD46439B93D4AA1DE97DCDAAA791B5BA89D77361C6BA47F92C51EFB1FF1AFC75C03E70E6ED8096DF94E0B59B840931EB193ACB84F394B6CE8BFBC69BCE77C43627EBE9C9EE6D16E5116EB63CC2B8AD6F7A45047D870DD5FEB667A6ED017296FE8E7AF6AC87BAEE51DC03A1D47ED5BE4E4738839C0DBF882EDFA365411CDD3A5B645BDA2372979045226AD7D6AB67D
	4C4FCDG3D19F4FD2C0F135D5E59FA50545EA6503DCAE7FFF9935D42D2EB5B0BE85FACBF228BA935ED7B0B0CBE88E84348764ABEC736CB77151B90AA37BD514A76D7ED7467A74C267413705EBB096B74B7D35FD51D247334D47AB816689A09EEF41FEDBB3D5858378DE83BCF221DF1450E6C146ED35774285CCE2F13CABBB719556DBC40F41D8874FEE4E7F9B16A9FC57A7FDAECED67A8220BA73A85F66E4FE256262927605B67945AE969F5E427F45F3B0C4764F6D61CD25A79F1113A1DF3192EF5C05FF88A6D5C
	530B7A3B1C2273C3FA2D6D3C1E68BC082E54EC5B4E6D7CFC82D78F5979365D75964E9669BEFA018ABBA32D6C746BD537B314699A837471E4673DC5283FE1G750FAD3236B3057A331468F2AD366D2CE473DD35C05BC07477A84ED3FC144DFF8DC0395BG36057DFDB3276A644FE45F4765ED61BF20EC0BC9B6765E7B877F7CG7AB0526F5AE25B765C456CCF845A74C1243F3AD7EE4F1FD87B16C2F955A05AF35F9EC776C81F4783E1F2FB3A871576D45838D79B79F98C20F79B443E5DCDFD66C7BA3FEE47C6FE5F
	7990208DA77A0795B6C6B19BF527037599275146CDF64FDA44FD3B504666A7947769E9250D775B3071E9E6E39D50B71EC69B0F130DAE27D167D9F61EB3F97C7CB950FA93FD0962FC492D2C1ED0A80FA09B9FF1E8237419DB71245C4674B3CA9B137A54EDDC401E77D9C0EFB803B6FEC8B6561FC11DAF37E3632D4C469620B591FD3E42460D7C3C8D14CFB8C36794BB34D17A3C4D793B5C46F00DB6C681FDAC593804468959AE743C30E3A33FEE118F3465C43FC1E1E38B33318D4A3BDD1C37D17A2CCDD458686D22
	4DC69F2077F7C19B534846E842E61EC2AC016C28BAE57D7C5DCFF49997A05DC527EC374F4D0CBE87E873AEC05B8F6CF6741E116EE79994AE373D71822B7B5446FB66E9760CE881FA93691A25B2B67C045EBD1EC4B7EA37ED1B2EE37350BEC06BCFB6EDF1B866136EB1EAFCCAEED33415CD3BEC0C79BAD85F4581FD628558F701B4164B9BC57B0A2A0C7926533E1F9523443121EDBB5F6457BF0036E9945AF92343B11FF40F571C2765F63A0ED25A99E263999B456A9E8774DE23504E9F697993C27ADF2AB2663B05
	68A2096ECFF66E61E0562631C01BC0F62EF578CDA65D0FB7CCE1E76EE8251DB3ED7467F2D6F7A150D70CC6BBAB28BF0D23D17FAE157E9CCBF483C417504955535AE76E6AE55346C81B5905346ED44FDE423C095F2D4BF6BB5D44687D00B610687F7C337CD9974A568EE221BCE1B436E54DC9C74FBA697D9E730C3CAD73DC15ED99ED43EFB9112DC71482FD15AB5A58F492EDB43952FE5BF63EF36EE377088B503A937DC70A332A9B589E5E7ED09E640AB6E6ECF2E423747E8F09105B98EFE5633ADCF59BF7317590
	5D9898770C21FE6CC49B4B47E8382F013609686FD758984E1F478D656346200D61F39D59A83D77A7BEAB37B1F00C5246F70B55ED647D97C201BE02EC2C24EBBDF5AC6A3C58CEBF2E654F66865ADC228FD3584847F3B6C0F973D834B146210D527B5E6DB9390D6EE315B61658386F3F63670100FE42D8EB9B4346629C748FFAC7EB8A4DD35462E40C8E79BED73D8FEFE2FC51D0FF42D8477BA8E5DC086770AC6F6056128C97AA77D9A8E0F87E054AFD966EB162BE8B65D06685296AC58E4F2D647AC867C67B2ED7DA
	6F797AEBA57AA6E63871FB3E6642EFB9A4AE4604EF4B49EC2D248130E6B7444E303592B748FB38E1FC41B36BD1D6A0E0611062A1E5DD942CAF3D8879B259DACCA364DBAE427DB17EF1C6F9368BD737BB159FDB28D66ECD65F74FD7AB7723725596357290AA3F27CF2DBC124A67742B1547D179CC5572540B317CEB5572ECAAFFCE35BC1F4ACBCA544A2B287C49E23572C6AADF2B4AEF2472D52A7C8397237F66CB5C65A55C032B4D861DED9F3F12C25EA3CC7071634421FF5CACD146AE33BDBF41924129CCF83F18
	597E3CA427BED314F3CF29733AF44D96F5397CB3D146DDA54E6B72EF591916BCD6D7AA576F6EB9226CC5FD4E6B9711A56A271FA74A685450D6771A2D75CB69136B37F23EA87B31E26775E3A1D038FE3DC01411DA6C3CFEBB0B2D753B26D82E5F658BC5597BFA1D576F64D4D13F6B53C599CBFA1D576F17DEEB7D7CFA657A85CA566E436C2D5DAB74FBC932F62F175CCFE30A1C57AF2C48DA3F050A76DBB9D314FD659AFB320BB959EB509FF70C647E4CF4EA6F62E2F1FF59CC5136C7E60BF22E6DF33E7EB7A577E4
	1F5DBE1373ED1BAB72DD3DC71B5EC76709FA0F135C971F5B3D2631FE1E36C3F6CD8BE78ACB4E241F284116D90BC43EE5CE6D25AB5AD21CA35A32F731A827C603AD172A58E279DC14B55B6E33C8EECB38C152869AEDF9FF29E84B72AFC4B9419AEC192DF27EF0DD2EA8EB3A86DBA62C9079FE235116073E94EDF9BDCF14B3D903ADEBD5EC197915A8EB3186DBBA7207EE0B7BAA51165B8BC4B9CFE830A5C7659D1AFB4A9A6BBCA5FF2EBD39DA2CEF7DEE671F3B6F960ABAE7970BB29E51705C1D27F2DFEC131CE73E
	DC43FB7E689A49B3E72DA843DD43FB7E9915777CD245FB7EAE491968039A5E73CF170B7AFD30CE1471250677FC0F4AFBBEC0711E7AD2F22EFA1606B165368AD13F2315220CF48D6D37C7257D2E27F126E0EF17B50D294B7ABA98A7B9435D63E7676F43772BC4BE43C9ED7761420DE29BD45608F2F2CFBAFF9FF61D343625C5F28E4033CE5D5342D9741BC53E57EA35597242965116E449D96DCBBB1D374554E9ED4B72ED22ACAF8D3638CB4E8538432933A9C4DBEE5FAE5A72648ED14EB4271E4F42B73A4A9E7B6F
	CA4E9438CD03AD3BA46785CC52E80BF90FE84B550D7F89DBF85FC9FF66BBBB8E3EADA7DECCB1ACC68E8B00FC84E14798968BF97FCF710C2C2F59391A423EE3123F7FEABC77FB0CBCFDCE6B605B6B9B76BC6367F42E2844B99D5DA5417A72CB50777879CA1C576A3E84FD0F27FD4B51CE04FCA02408CB703BFD12646C065A53865B7DD63226A45FD0D2D6A8BCE3E3A4E7E85FED57379879F85DBEDE0F3A45DD0231ECDFD7A8E359DE166D33FE20C2994B46710B6517D5A8E3599E1071BFD0210CE5C3FE5E16991273
	A9C68FBAEFCB168EFD3C735C316D8F34A056C2D86CFC44BC89BBECA17FF0425EE4F3281C7E693FC1ECDBBFE2D5045592E6A16CA0E13E044D26FD63E2887B054D0332BD5EAFC5EC9A6D3B68CA58D1428288EBABC65E78CBF13E70F1F68E223943ECEA671E95BA1C8FFE155D43CD17856B5BAEC35ACA63CC24ED686A6DB0C313927DBEF6AF33FD18095E4D180674F40FDDD3B9B4DF715F8F11AFCAB39FBF8FBB486E378917A967E747B25C77B2657C6C2CEF71DE8E00B243B81C0FCE2E981A5E8BE47BD7390E636716
	BD05DF1FF1367601BF58A2658B946842055F981BFCEF49E28692853A4C717CEF6EF8DBFC3F4A76C4AC97686A055F961BFC6F487656B48BF4EE422F977C7AAAF8F93E1D327DB003841A8861B796FEF5172B71ED1571E589B4F942EF857CB628729D11713589B49661579DFE3DD5796649762B7493E8C2045FC8780DD3656BAAD1FB8725DE4153E6DFE1FB0DA1DFD1D68579C6C8C689AE6C91D7A255EDC0A89FA77CCE00DF7FAB54F46B1171058AB4D142EFBC7C26FB2871F594F3B665A84A8AA1DF6BE15B9EAEB5A9
	4ACD10F7D1E07CBB2067877893674B0C5C64024AB8283A8786C2624E83C8FF54485D37623EA62359F8DF0831107CBD775BF3BF86A9B827F588B1006C3D67816DB8418347E2CB5939417EDC7E935CA3F9C78D464504905D0996644D3E92315DD5489BCA3C272A10B71F685233F9ACD86FCE58AF7BB94C1B4B6BA6E16C61FE6475A53AD16CFD2B3BAAD81FF995E2C5CD4C0F9A7231447BD3937216925DF476EEF5053C87E18F6CE5BE32109FC8C25EB777A22FBF515D435E69AC7675EA448EB3F9B9104FA76CE946DB
	85F9C3B24ACB5907729A086E4FA44F1B3096A64F8F72C1047D0B490B60B012B70B644590DDB849AB77C46CB413D74F6589FB01496B063CB18565E510BC97229B5D0F724288DB4546A151100FA76CE3A6AF7D1AE0FDC6AA4A7B0A64E5DF03F41712BCA3E1635958E4G726E444B47473B83B601683EE06B3B7E108FA1AC177AA812300FA9AEB07FDA443630F62902FC93E13C6F08897283044D1F473ED5A06F13063A544E43BA3C096E850AD50DA36CA25E77793AE0FD56F5E4AF731F4803FC039E657D05E2C14B09
	6E01DDA84F0D30CBD89DDC8C2A97E1E1FC3D100FA6F99F73BE2D5B61DB0068B6B2DD42B9BA42BE20384A8442A2490EBAAF9C27ED514957798F3067C3B3149B09263CC6FEC6F4FB0DA3FF1F94C9D21E316C6925BCE379E676DC19G382F977A96DCDE65483740DE9DD112BA04F522EC391DEB5873A8966892484E8B764B6D3CED3F23BDF56C6910F73DD207EBACF29DEEE74F35D2202B3A9EF528EB126B505724E5DF9F258E5D57ABFDAC16764BF5A8E37768G5039118E91FB65BA246DF32443D4BBBA78035C9932
	7EFE19FC6B02284EC105DED7B03DA220BC1AE85ED068355021DE536C6815F50372BAFC0174B25C00F59ED768759B26D7A914D7914D878A3DD6B854EB069D3D3A2D747A18749A20BA2FD468F5AB53EB9C14FB914DFB8A3D3EF02857CCBBFA05DA6965530FFAC5D01D354765FA7D405E9F31D01EC0B4D97B64FAD5BB54EB169D3D72EED46AF58F69D5FAA356B931846B6473CF15281FCB7C86FB165681DFA371FE2370D1EEE06F9C97A8F73F911FD76B9D1EBBBF5F0E8D81A0E71A4C062F294FC3C80F1FEC78AD77B3
	5AC8200BA15A0A92394E3B797DF03CE10C620DB48573647D7063BCC7B64C35E3C30337329F5249F73A196A74D66875E7C6538D6583C4F34BC939DECF5A750964F9EC6B65E325576B3BD0AFFFEF74DB72A3BF58D0526355E2540327D9C176C4914D778AFB3A98CDBC146BEE42EBA353E13B4E3623FF61CDCA7DC51F740A1BF0AE7031E69D35455C9AEDE7243EF50063657F5E5EA24A59DC25CDCEF72BA8A77410A867277D5A64CCE993656CBBA24AD95E24CD4E8923A86729DFC4B9F7EBBCC3B661D711B7D165FC5A
	1FBB457207B54A9E55A5723E27A27B35E3E279539AE5E7C84E4F4E6A3716ED141C4F7BF70D32AF6997F97FC7C5762B12B3FB5F52A8FB3A64AC5147D4E42F121C2D192D611CE43794891FC61DEECD95F9B72A745333E9E2790A9267F57AC6626FB6C363F95287A43EB8CBD4FC8802A43EEA231CDA0394E53F1AA572A66D36DEABFDC4E58D38D2C5079D923F1BB7B41E85FFCC62E7A377ABA80307DF748FBADD79F713191E5CBF17768775BA493F79FC3B94FC7CBD3C835E5025757668BE336E0F6ACB1FFD4667675A
	2CE4EEF5D2EFEC6FB8E2B4EF55259D6FE8B39BAD5D70140567EA52E06FB19DBB1F5DDE0E3D9C2F32111B876374AC50E096E9303533375D5C9B55DF72CD4ACB3FCDC95625C275873B4C662E23DBF5292676431C96BD3CB6A9662EEECE97B654578B3F29D2593AD10A1A385C54E14B11FAD69CE9B6B8B82FC58B3471CC5955B56926B52F047EF3DFEF0ACE3FD53700FBD16FE9672682E17854D65BEAEE5D575E43DD871BF5533A3BBA2090F06800DE761356EBBEE1EA2F57E59D5E5DFE2C2F3D575C5ED6EDB677F49C
	6CB3C3C6B7371E2BE9F51F3943343A2E3557E866822556576BD2BBDB0F361BF51F714526564EA32B77GD76711752C97132A2B183E4CCB90741D43746524941014B9FF1935FC6936E87F94DD72E65DF4D3D7EB5B6656C3662E1E2D3AE9E6E3CFFB2F314B54E6695E408BCA27F32FD2C05CA2B21FB336209106AF24742F166B5DE7464C5C1C8CAD5523DB4DA83971CD81D1345598DBFB202A761E2D10DA214ADCAA3B60F3730AC7374CEFCE56A589C6F2CD1FE369B66B7407DB0FF618CE409F356CDEE61DE2B00336
	BD2D1D3D073BFA0E02E68605E6D8C49594DC713F05D77E61439FD885A22BD93704EFCAE0DDFD243D6B686A6A4307BBBA5BC9G34AB08DFCC62B91A82854DE71671F337BD7549C1388EEAF5A9C78B8B597FC56C7FE26E7FA20694B12428989AB7CEE7771F325C02B6330B076EF52E7B8BB6C38F1A1FFB63FFEFFE3EA2AE962C5B2CCB6BEFB57535C3A7E40AD4BB7A0E9EEC6F996BF36562999ED117F0FD544DDDAA96CEEE2E056E857C681F4EBADCFE75DB6796DADDF19C7A39752543414B2883395CF2C67AA57B7F
	AB4D4D41F165B35E8232E26DF9E1450673420A3A73420ADD229549A3570A9FC4ABD2A5D6A45B30C27EEE64FA8F5F0D69CE3DCD13A49CFA27B824F5B87746661661509EDB9C4334B73A546F0C3411F5CDD9AC327E9E21F746856AFD11B4322C38D834E2094EFEDFC87B0BCB5368EB2387D22BB3B95DE4481DA1E91FC924A54BE94C3ACF373635F71ABB8EF79CEA65FCFA4C3A596C88EE43161E2E3EEE438E9863FD8398BFB2B1F49DB63052DE40E670F45C701223585C5553FE84F8BA5B8CB5DD262E9E40266FEEBD
	4E53C3A63D481037333B3DF3A50C890B8DF9B5262E5EF678BB2984D2A924B510561AF549C5057047BAC865104AA4BD5942347E147A5E3AA7D365BD0943B17DF176D147778847731F6D6296A17F870B2A327FA7179D6AF467B05449DF769A53954BF3E65D4C1A2E4E439DC77AFA5875B87C74FFC07EC062DE0A1CDBF37A7053B4447A51B9E7F8EA7A38DC5369C23B8EC3CD1F96B5558B1AF2F56B071F267F98B11AC6DBEB3AF1F8EABAF944E87A3628A98EB60629269F28EBBA155E8B5CB36B33E127F7D2122877D2
	C16F52112077B4792766703DB212B2556F3654612769FCD153F42BB6D59F508FF1D04B657E487D67F3EEACD9C223B38D8B9FCEFFEEE814BDCC5B28E0780D2A13CA45D1352DE528E125EF055D2F00A1DED94374FA59A25ED34EFD425B1C8298910BFB43EBB9D03CAE782B607FFAAAA8296DFC18F6CFBAFADED851FFDED8F1663C30426DFCD848C99A7F5F051CE1549B3FBB9F96F212EEBF9F96F2127C4F07051CA4496C2F23059C1D26A59B474EE3D86F709C437DF754077FD42F1B974F0BF7746B6705956F1C97D6
	A41E8FD6A4271D8F233E64CF7FBB6A9BC63D714579B06ACB4EBF9FC6FD49EB4E07D1DF72374E0F7A06679CE4726661B7E71DDCBFD2BCA912770C94CF0A649F641A8EDFCF0A645611621F10EC9CB11AF6D9EBBABC57F6137BC70C2603A34513A259F5E4FAD2A43B0FCCCF0AE40F1162C911FC5DC87124C83E591EA7453052F4987A7BA65FBE8CF53A5B71CA3FF39EB27F27D65CBFE2462A13C64CD8B5F4440CD57FBEE2C6004F0D98CDDF9CB1A340189123691491B3D6FDEF040ED57FBDB2472AA97A11B2D6CD19B9
	D2462AA9D976462A3A916E651A7279709BCB26640EACAF57147CF36165AA708F7B757D7FFA8460BF35B50514ED67437AED0A4A4C78883422793C30227D3C302273FC7088C8B97EDF0F00E1549B6E6AF746487288C83972FC7088C83961FC7088C8797D3970839D26DF8243F097099405B771701D0FCD795BC8190FCD899EA97331A90F0E14D94E147091236973A3E516B365DFA3C65357C64AFCECCA5C481C0FCDC99819733129BA797741703DB2D2270E147958D4099736ECBED6471F8322532DBCD3AE6859BC5E
	DB17B0A6BB193B5613F456FB72077D6DC1F64ED83470EB505D1E440D68E66BBEBB4649B684BE282F00449D68BEC737F1B3F7F0763E56DF2A3B3BB71A5AFAB635F5183BFAA0734DB755874DFB0E361ACC66D6532F551DED5BDA7BDBB71EB037F39B33733C730FF172FB11C6A8644BBEBF46697D0B04677F81D0CB8788398A57690E1CGGC01782GD0CB818294G94G88G88GC2F954AC398A57690E1CGGC01782G8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GG
	GG81G81GBAGGG481CGGGG
**end of data**/
}
}
