package com.cannontech.dbeditor.editor.state;
/**
 * This type was created in VisualAge.
 */

import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.common.gui.util.DataInputPanelListener;
import com.cannontech.database.data.lite.LiteYukonImage;
import com.cannontech.database.db.state.StateGroupUtils;
import com.cannontech.yukon.IDatabaseCache;
import com.klg.jclass.util.value.JCValueEvent;

public class GroupStateEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.klg.jclass.util.value.JCValueListener, java.awt.event.ActionListener, java.awt.event.ItemListener, javax.swing.event.CaretListener, DataInputPanelListener 
{
	// this must be changed whenever the number or states are changed
	public static final int STATE_COUNT = 11;
	private javax.swing.JLabel[] rawStateLabels = null;
	private javax.swing.JTextField[] stateNameTextFields = null;
	private javax.swing.JComboBox[] foregroundColorComboBoxes = null;
   private javax.swing.JButton[] imageButton = null;   
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
    private javax.swing.JComboBox ivjForegroundColorComboBox11 = null;
    private javax.swing.JLabel ivjRawStateLabel11 = null;
    private javax.swing.JTextField ivjStateNameTextField11 = null;
	private javax.swing.JComboBox ivjForegroundColorComboBox9 = null;
	private javax.swing.JLabel ivjRawStateLabel9 = null;
	private javax.swing.JTextField ivjStateNameTextField9 = null;
	private javax.swing.JLabel ivjJLabelImage = null;
	private javax.swing.JButton ivjJButtonImage1 = null;
	private javax.swing.JButton ivjJButtonImage10 = null;
    private javax.swing.JButton ivjJButtonImage11 = null;
	private javax.swing.JButton ivjJButtonImage2 = null;
	private javax.swing.JButton ivjJButtonImage3 = null;
	private javax.swing.JButton ivjJButtonImage4 = null;
	private javax.swing.JButton ivjJButtonImage5 = null;
	private javax.swing.JButton ivjJButtonImage6 = null;
	private javax.swing.JButton ivjJButtonImage7 = null;
	private javax.swing.JButton ivjJButtonImage8 = null;
	private javax.swing.JButton ivjJButtonImage9 = null;

/**
 * Constructor
 */
public GroupStateEditorPanel() {
	super();
	initialize();
}


/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
public void actionPerformed(java.awt.event.ActionEvent e) {
	if (e.getSource() == getJButtonImage1()) 
		connEtoC22(e);
	if (e.getSource() == getJButtonImage2()) 
		connEtoC23(e);
	if (e.getSource() == getJButtonImage3()) 
		connEtoC24(e);
	if (e.getSource() == getJButtonImage4()) 
		connEtoC25(e);
	if (e.getSource() == getJButtonImage5()) 
		connEtoC26(e);
	if (e.getSource() == getJButtonImage6()) 
		connEtoC27(e);
	if (e.getSource() == getJButtonImage7()) 
		connEtoC28(e);
	if (e.getSource() == getJButtonImage8()) 
		connEtoC29(e);
	if (e.getSource() == getJButtonImage9()) 
		connEtoC30(e);
	if (e.getSource() == getJButtonImage10()) 
		connEtoC31(e);
    if (e.getSource() == getJButtonImage11()) 
        connEtoC31(e);
}



/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
public void caretUpdate(javax.swing.event.CaretEvent e) {
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
}


/**
 * connEtoC1:  (StateGroupNameTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> GroupStateEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
private void connEtoC1(javax.swing.event.CaretEvent arg1) {
	try {
		this.fireInputUpdate();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connEtoC10:  (ForegroundColorComboBox2.item.itemStateChanged(java.awt.event.ItemEvent) --> GroupStateEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ItemEvent
 */
private void connEtoC10(java.awt.event.ItemEvent arg1) {
	try {
		this.fireInputUpdate();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connEtoC11:  (ForegroundColorComboBox3.item.itemStateChanged(java.awt.event.ItemEvent) --> GroupStateEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ItemEvent
 */
private void connEtoC11(java.awt.event.ItemEvent arg1) {
	try {
		this.fireInputUpdate();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connEtoC12:  (ForegroundColorComboBox4.item.itemStateChanged(java.awt.event.ItemEvent) --> GroupStateEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ItemEvent
 */
private void connEtoC12(java.awt.event.ItemEvent arg1) {
	try {
		this.fireInputUpdate();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connEtoC13:  (ForegroundColorComboBox5.item.itemStateChanged(java.awt.event.ItemEvent) --> GroupStateEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ItemEvent
 */
private void connEtoC13(java.awt.event.ItemEvent arg1) {
	try {
		this.fireInputUpdate();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connEtoC14:  (ForegroundColorComboBox6.item.itemStateChanged(java.awt.event.ItemEvent) --> GroupStateEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ItemEvent
 */
private void connEtoC14(java.awt.event.ItemEvent arg1) {
	try {
		this.fireInputUpdate();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connEtoC15:  (StateNameTextField8.caret.caretUpdate(javax.swing.event.CaretEvent) --> GroupStateEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
private void connEtoC15(javax.swing.event.CaretEvent arg1) {
	try {
		this.fireInputUpdate();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connEtoC16:  (StateNameTextField9.caret.caretUpdate(javax.swing.event.CaretEvent) --> GroupStateEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
private void connEtoC16(javax.swing.event.CaretEvent arg1) {
	try {
		this.fireInputUpdate();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connEtoC17:  (StateNameTextField10.caret.caretUpdate(javax.swing.event.CaretEvent) --> GroupStateEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
private void connEtoC17(javax.swing.event.CaretEvent arg1) {
	try {
		this.fireInputUpdate();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connEtoC18:  (ForegroundColorComboBox7.item.itemStateChanged(java.awt.event.ItemEvent) --> GroupStateEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ItemEvent
 */
private void connEtoC18(java.awt.event.ItemEvent arg1) {
	try {
		this.fireInputUpdate();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connEtoC19:  (ForegroundColorComboBox8.item.itemStateChanged(java.awt.event.ItemEvent) --> GroupStateEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ItemEvent
 */
private void connEtoC19(java.awt.event.ItemEvent arg1) {
	try {
		this.fireInputUpdate();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (StateNameTextField7.caret.caretUpdate(javax.swing.event.CaretEvent) --> GroupStateEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
private void connEtoC2(javax.swing.event.CaretEvent arg1) {
	try {
		this.fireInputUpdate();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connEtoC20:  (ForegroundColorComboBox9.item.itemStateChanged(java.awt.event.ItemEvent) --> GroupStateEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ItemEvent
 */
private void connEtoC20(java.awt.event.ItemEvent arg1) {
	try {
		this.fireInputUpdate();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connEtoC21:  (ForegroundColorComboBox10.item.itemStateChanged(java.awt.event.ItemEvent) --> GroupStateEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ItemEvent
 */
private void connEtoC21(java.awt.event.ItemEvent arg1) {
	try {
		this.fireInputUpdate();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connEtoC22:  (JButtonImage1.action.actionPerformed(java.awt.event.ActionEvent) --> GroupStateEditorPanel.jButtonImage1_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC22(java.awt.event.ActionEvent arg1) {
	try {
		this.jButtonImage_ActionPerformed(arg1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}



/**
 * connEtoC3:  (StateNameTextField1.caret.caretUpdate(javax.swing.event.CaretEvent) --> GroupStateEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
private void connEtoC3(javax.swing.event.CaretEvent arg1) {
	try {
		this.fireInputUpdate();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connEtoC4:  (StateNameTextField2.caret.caretUpdate(javax.swing.event.CaretEvent) --> GroupStateEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
private void connEtoC4(javax.swing.event.CaretEvent arg1) {
	try {
		this.fireInputUpdate();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connEtoC5:  (StateNameTextField3.caret.caretUpdate(javax.swing.event.CaretEvent) --> GroupStateEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
private void connEtoC5(javax.swing.event.CaretEvent arg1) {
	try {
		this.fireInputUpdate();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connEtoC6:  (StateNameTextField4.caret.caretUpdate(javax.swing.event.CaretEvent) --> GroupStateEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
private void connEtoC6(javax.swing.event.CaretEvent arg1) {
	try {
		this.fireInputUpdate();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connEtoC7:  (StateNameTextField5.caret.caretUpdate(javax.swing.event.CaretEvent) --> GroupStateEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
private void connEtoC7(javax.swing.event.CaretEvent arg1) {
	try {
		this.fireInputUpdate();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connEtoC8:  (StateNameTextField6.caret.caretUpdate(javax.swing.event.CaretEvent) --> GroupStateEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
private void connEtoC8(javax.swing.event.CaretEvent arg1) {
	try {
		this.fireInputUpdate();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connEtoC9:  (ForegroundColorComboBox1.item.itemStateChanged(java.awt.event.ItemEvent) --> GroupStateEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ItemEvent
 */
private void connEtoC9(java.awt.event.ItemEvent arg1) {
	try {
		this.fireInputUpdate();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * Return the ForegroundColorComboBox1 property value.
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox getForegroundColorComboBox1() {
	if (ivjForegroundColorComboBox1 == null) {
		try {
			ivjForegroundColorComboBox1 = new javax.swing.JComboBox();
			ivjForegroundColorComboBox1.setName("ForegroundColorComboBox1");
			ivjForegroundColorComboBox1.setPreferredSize(new java.awt.Dimension(120, 25));
			ivjForegroundColorComboBox1.setFont(new java.awt.Font("dialog", 0, 12));
			ivjForegroundColorComboBox1.setMinimumSize(new java.awt.Dimension(120, 25));
			ivjForegroundColorComboBox1.setRenderer(new com.cannontech.common.gui.util.ColorComboBoxCellRenderer());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjForegroundColorComboBox1;
}

/**
 * Return the ForegroundColorComboBox9 property value.
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox getForegroundColorComboBox10() {
	if (ivjForegroundColorComboBox10 == null) {
		try {
			ivjForegroundColorComboBox10 = new javax.swing.JComboBox();
			ivjForegroundColorComboBox10.setName("ForegroundColorComboBox10");
			ivjForegroundColorComboBox10.setPreferredSize(new java.awt.Dimension(120, 25));
			ivjForegroundColorComboBox10.setFont(new java.awt.Font("dialog", 0, 12));
			ivjForegroundColorComboBox10.setMinimumSize(new java.awt.Dimension(120, 25));
			ivjForegroundColorComboBox10.setEnabled(false);
			ivjForegroundColorComboBox10.setRenderer(new com.cannontech.common.gui.util.ColorComboBoxCellRenderer());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjForegroundColorComboBox10;
}

/**
 * Return the ForegroundColorComboBox11 property value.
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox getForegroundColorComboBox11() {
    if (ivjForegroundColorComboBox11 == null) {
        try {
            ivjForegroundColorComboBox11 = new javax.swing.JComboBox();
            ivjForegroundColorComboBox11.setName("ForegroundColorComboBox11");
            ivjForegroundColorComboBox11.setPreferredSize(new java.awt.Dimension(120, 25));
            ivjForegroundColorComboBox11.setFont(new java.awt.Font("dialog", 0, 12));
            ivjForegroundColorComboBox11.setMinimumSize(new java.awt.Dimension(120, 25));
            ivjForegroundColorComboBox11.setEnabled(false);
            ivjForegroundColorComboBox11.setRenderer(new com.cannontech.common.gui.util.ColorComboBoxCellRenderer());
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return ivjForegroundColorComboBox11;
}

/**
 * Return the ForegroundColorComboBox2 property value.
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox getForegroundColorComboBox2() {
	if (ivjForegroundColorComboBox2 == null) {
		try {
			ivjForegroundColorComboBox2 = new javax.swing.JComboBox();
			ivjForegroundColorComboBox2.setName("ForegroundColorComboBox2");
			ivjForegroundColorComboBox2.setPreferredSize(new java.awt.Dimension(120, 25));
			ivjForegroundColorComboBox2.setFont(new java.awt.Font("dialog", 0, 12));
			ivjForegroundColorComboBox2.setMinimumSize(new java.awt.Dimension(120, 25));
			ivjForegroundColorComboBox2.setEnabled(false);
			ivjForegroundColorComboBox2.setRenderer(new com.cannontech.common.gui.util.ColorComboBoxCellRenderer());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjForegroundColorComboBox2;
}


/**
 * Return the ForegroundColorComboBox4 property value.
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox getForegroundColorComboBox3() {
	if (ivjForegroundColorComboBox3 == null) {
		try {
			ivjForegroundColorComboBox3 = new javax.swing.JComboBox();
			ivjForegroundColorComboBox3.setName("ForegroundColorComboBox3");
			ivjForegroundColorComboBox3.setPreferredSize(new java.awt.Dimension(120, 25));
			ivjForegroundColorComboBox3.setFont(new java.awt.Font("dialog", 0, 12));
			ivjForegroundColorComboBox3.setMinimumSize(new java.awt.Dimension(120, 25));
			ivjForegroundColorComboBox3.setEnabled(false);
			ivjForegroundColorComboBox3.setRenderer(new com.cannontech.common.gui.util.ColorComboBoxCellRenderer());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjForegroundColorComboBox3;
}


/**
 * Return the ForegroundColorComboBox4 property value.
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox getForegroundColorComboBox4() {
	if (ivjForegroundColorComboBox4 == null) {
		try {
			ivjForegroundColorComboBox4 = new javax.swing.JComboBox();
			ivjForegroundColorComboBox4.setName("ForegroundColorComboBox4");
			ivjForegroundColorComboBox4.setPreferredSize(new java.awt.Dimension(120, 25));
			ivjForegroundColorComboBox4.setFont(new java.awt.Font("dialog", 0, 12));
			ivjForegroundColorComboBox4.setMinimumSize(new java.awt.Dimension(120, 25));
			ivjForegroundColorComboBox4.setEnabled(false);
			ivjForegroundColorComboBox4.setRenderer(new com.cannontech.common.gui.util.ColorComboBoxCellRenderer());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjForegroundColorComboBox4;
}


/**
 * Return the ForegroundColorComboBox5 property value.
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox getForegroundColorComboBox5() {
	if (ivjForegroundColorComboBox5 == null) {
		try {
			ivjForegroundColorComboBox5 = new javax.swing.JComboBox();
			ivjForegroundColorComboBox5.setName("ForegroundColorComboBox5");
			ivjForegroundColorComboBox5.setPreferredSize(new java.awt.Dimension(120, 25));
			ivjForegroundColorComboBox5.setFont(new java.awt.Font("dialog", 0, 12));
			ivjForegroundColorComboBox5.setMinimumSize(new java.awt.Dimension(120, 25));
			ivjForegroundColorComboBox5.setEnabled(false);
			ivjForegroundColorComboBox5.setRenderer(new com.cannontech.common.gui.util.ColorComboBoxCellRenderer());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjForegroundColorComboBox5;
}


/**
 * Return the ForegroundColorComboBox6 property value.
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox getForegroundColorComboBox6() {
	if (ivjForegroundColorComboBox6 == null) {
		try {
			ivjForegroundColorComboBox6 = new javax.swing.JComboBox();
			ivjForegroundColorComboBox6.setName("ForegroundColorComboBox6");
			ivjForegroundColorComboBox6.setPreferredSize(new java.awt.Dimension(120, 25));
			ivjForegroundColorComboBox6.setFont(new java.awt.Font("dialog", 0, 12));
			ivjForegroundColorComboBox6.setMinimumSize(new java.awt.Dimension(120, 25));
			ivjForegroundColorComboBox6.setEnabled(false);
			ivjForegroundColorComboBox6.setRenderer(new com.cannontech.common.gui.util.ColorComboBoxCellRenderer());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjForegroundColorComboBox6;
}


/**
 * Return the ForegroundColorComboBox8 property value.
 * @return javax.swing.JComboBox
 */
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
			ivjForegroundColorComboBox7.setRenderer(new com.cannontech.common.gui.util.ColorComboBoxCellRenderer());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjForegroundColorComboBox7;
}


/**
 * Return the ForegroundColorComboBox8 property value.
 * @return javax.swing.JComboBox
 */
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
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjForegroundColorComboBox8;
}


/**
 * Return the ForegroundColorComboBox92 property value.
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox getForegroundColorComboBox9() {
	if (ivjForegroundColorComboBox9 == null) {
		try {
			ivjForegroundColorComboBox9 = new javax.swing.JComboBox();
			ivjForegroundColorComboBox9.setName("ForegroundColorComboBox9");
			ivjForegroundColorComboBox9.setPreferredSize(new java.awt.Dimension(120, 25));
			ivjForegroundColorComboBox9.setFont(new java.awt.Font("dialog", 0, 12));
			ivjForegroundColorComboBox9.setMinimumSize(new java.awt.Dimension(120, 25));
			ivjForegroundColorComboBox9.setEnabled(false);
			ivjForegroundColorComboBox9.setRenderer(new com.cannontech.common.gui.util.ColorComboBoxCellRenderer());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjForegroundColorComboBox9;
}


/**
 * Return the IdentificationPanel property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getIdentificationPanel() {
	if (ivjIdentificationPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("dialog", 0, 12));
			ivjLocalBorder.setTitle("Identification");
			ivjIdentificationPanel = new javax.swing.JPanel();
			ivjIdentificationPanel.setName("IdentificationPanel");
			ivjIdentificationPanel.setBorder(ivjLocalBorder);
			ivjIdentificationPanel.setLayout(null);
			getIdentificationPanel().add(getStateGroupNameLabel(), getStateGroupNameLabel().getName());
			getIdentificationPanel().add(getStateGroupNameTextField(), getStateGroupNameTextField().getName());
			getIdentificationPanel().add(getStateNumberLabel(), getStateNumberLabel().getName());
			getIdentificationPanel().add(getStateNumberSpinner(), getStateNumberSpinner().getName());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjIdentificationPanel;
}

/**
 * Return the JButtonImage1 property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getJButtonImage1() {
	if (ivjJButtonImage1 == null) {
		try {
			ivjJButtonImage1 = new javax.swing.JButton();
			ivjJButtonImage1.setName("JButtonImage1");
			ivjJButtonImage1.setText("Image...");
			ivjJButtonImage1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJButtonImage1.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJButtonImage1.setMargin(new java.awt.Insets(2, 3, 2, 3));
			ivjJButtonImage1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			ivjJButtonImage1.setEnabled(false);
         
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJButtonImage1;
}



/**
 * Return the JButtonImage19 property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getJButtonImage10() {
	if (ivjJButtonImage10 == null) {
		try {
			ivjJButtonImage10 = new javax.swing.JButton();
			ivjJButtonImage10.setName("JButtonImage10");
			ivjJButtonImage10.setText("Image...");
			ivjJButtonImage10.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJButtonImage10.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJButtonImage10.setMargin(new java.awt.Insets(2, 3, 2, 3));
			ivjJButtonImage10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			ivjJButtonImage10.setEnabled(false);
         
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJButtonImage10;
}
/**
 * Return the JButtonImage11 property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getJButtonImage11() {
    if (ivjJButtonImage11 == null) {
        try {
            ivjJButtonImage11 = new javax.swing.JButton();
            ivjJButtonImage11.setName("JButtonImage11");
            ivjJButtonImage11.setText("Image...");
            ivjJButtonImage11.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            ivjJButtonImage11.setFont(new java.awt.Font("Arial", 1, 10));
            ivjJButtonImage11.setMargin(new java.awt.Insets(2, 3, 2, 3));
            ivjJButtonImage11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            ivjJButtonImage11.setEnabled(false);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return ivjJButtonImage11;
}


/**
 * Return the JButtonImage11 property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getJButtonImage2() {
	if (ivjJButtonImage2 == null) {
		try {
			ivjJButtonImage2 = new javax.swing.JButton();
			ivjJButtonImage2.setName("JButtonImage2");
			ivjJButtonImage2.setText("Image...");
			ivjJButtonImage2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJButtonImage2.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJButtonImage2.setMargin(new java.awt.Insets(2, 3, 2, 3));
			ivjJButtonImage2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			ivjJButtonImage2.setEnabled(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJButtonImage2;
}


/**
 * Return the JButtonImage12 property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getJButtonImage3() {
	if (ivjJButtonImage3 == null) {
		try {
			ivjJButtonImage3 = new javax.swing.JButton();
			ivjJButtonImage3.setName("JButtonImage3");
			ivjJButtonImage3.setText("Image...");
			ivjJButtonImage3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJButtonImage3.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJButtonImage3.setMargin(new java.awt.Insets(2, 3, 2, 3));
			ivjJButtonImage3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			ivjJButtonImage3.setEnabled(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJButtonImage3;
}


/**
 * Return the JButtonImage13 property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getJButtonImage4() {
	if (ivjJButtonImage4 == null) {
		try {
			ivjJButtonImage4 = new javax.swing.JButton();
			ivjJButtonImage4.setName("JButtonImage4");
			ivjJButtonImage4.setText("Image...");
			ivjJButtonImage4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJButtonImage4.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJButtonImage4.setMargin(new java.awt.Insets(2, 3, 2, 3));
			ivjJButtonImage4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			ivjJButtonImage4.setEnabled(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJButtonImage4;
}


/**
 * Return the JButtonImage14 property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getJButtonImage5() {
	if (ivjJButtonImage5 == null) {
		try {
			ivjJButtonImage5 = new javax.swing.JButton();
			ivjJButtonImage5.setName("JButtonImage5");
			ivjJButtonImage5.setText("Image...");
			ivjJButtonImage5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJButtonImage5.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJButtonImage5.setMargin(new java.awt.Insets(2, 3, 2, 3));
			ivjJButtonImage5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			ivjJButtonImage5.setEnabled(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJButtonImage5;
}


/**
 * Return the JButtonImage15 property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getJButtonImage6() {
	if (ivjJButtonImage6 == null) {
		try {
			ivjJButtonImage6 = new javax.swing.JButton();
			ivjJButtonImage6.setName("JButtonImage6");
			ivjJButtonImage6.setText("Image...");
			ivjJButtonImage6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJButtonImage6.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJButtonImage6.setMargin(new java.awt.Insets(2, 3, 2, 3));
			ivjJButtonImage6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			ivjJButtonImage6.setEnabled(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJButtonImage6;
}


/**
 * Return the JButtonImage16 property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getJButtonImage7() {
	if (ivjJButtonImage7 == null) {
		try {
			ivjJButtonImage7 = new javax.swing.JButton();
			ivjJButtonImage7.setName("JButtonImage7");
			ivjJButtonImage7.setText("Image...");
			ivjJButtonImage7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJButtonImage7.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJButtonImage7.setMargin(new java.awt.Insets(2, 3, 2, 3));
			ivjJButtonImage7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			ivjJButtonImage7.setEnabled(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJButtonImage7;
}


/**
 * Return the JButtonImage17 property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getJButtonImage8() {
	if (ivjJButtonImage8 == null) {
		try {
			ivjJButtonImage8 = new javax.swing.JButton();
			ivjJButtonImage8.setName("JButtonImage8");
			ivjJButtonImage8.setText("Image...");
			ivjJButtonImage8.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJButtonImage8.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJButtonImage8.setMargin(new java.awt.Insets(2, 3, 2, 3));
			ivjJButtonImage8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			ivjJButtonImage8.setEnabled(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJButtonImage8;
}


/**
 * Return the JButtonImage18 property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getJButtonImage9() {
	if (ivjJButtonImage9 == null) {
		try {
			ivjJButtonImage9 = new javax.swing.JButton();
			ivjJButtonImage9.setName("JButtonImage9");
			ivjJButtonImage9.setText("Image...");
			ivjJButtonImage9.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJButtonImage9.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJButtonImage9.setMargin(new java.awt.Insets(2, 3, 2, 3));
			ivjJButtonImage9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			ivjJButtonImage9.setEnabled(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJButtonImage9;
}


/**
 * Return the JLabelImage property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabelImage() {
	if (ivjJLabelImage == null) {
		try {
			ivjJLabelImage = new javax.swing.JLabel();
			ivjJLabelImage.setName("JLabelImage");
			ivjJLabelImage.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelImage.setText("Image");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabelImage;
}


/**
 * Return the JScrollPane property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getJScrollPane() {
	if (ivjJScrollPane == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("dialog", 0, 12));
			ivjLocalBorder1.setTitle("Configuration");
			ivjJScrollPane = new javax.swing.JScrollPane();
			ivjJScrollPane.setName("JScrollPane");
			ivjJScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPane.setBorder(ivjLocalBorder1);
			ivjJScrollPane.setDoubleBuffered(false);
			ivjJScrollPane.setPreferredSize(new java.awt.Dimension(383, 235));
			ivjJScrollPane.setMinimumSize(new java.awt.Dimension(383, 235));
			getJScrollPane().setViewportView(getStatesPanel());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane;
}

/**
 * Return the RepeaterLabel1 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getRawStateColumnLabel() {
	if (ivjRawStateColumnLabel == null) {
		try {
			ivjRawStateColumnLabel = new javax.swing.JLabel();
			ivjRawStateColumnLabel.setName("RawStateColumnLabel");
			ivjRawStateColumnLabel.setFont(new java.awt.Font("dialog", 0, 12));
			ivjRawStateColumnLabel.setText("Raw State");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRawStateColumnLabel;
}

/**
 * Return the RawStateLabel2 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getRawStateLabel1() {
	if (ivjRawStateLabel1 == null) {
		try {
			ivjRawStateLabel1 = new javax.swing.JLabel();
			ivjRawStateLabel1.setName("RawStateLabel1");
			ivjRawStateLabel1.setFont(new java.awt.Font("dialog", 0, 12));
			ivjRawStateLabel1.setText("0 (Open)");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRawStateLabel1;
}

/**
 * Return the RawStateLabel9 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getRawStateLabel10() {
	if (ivjRawStateLabel10 == null) {
		try {
			ivjRawStateLabel10 = new javax.swing.JLabel();
			ivjRawStateLabel10.setName("RawStateLabel10");
			ivjRawStateLabel10.setText("9");
			ivjRawStateLabel10.setMaximumSize(new java.awt.Dimension(18, 19));
			ivjRawStateLabel10.setFont(new java.awt.Font("dialog", 0, 12));
			ivjRawStateLabel10.setEnabled(false);
			ivjRawStateLabel10.setMinimumSize(new java.awt.Dimension(18, 19));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRawStateLabel10;
}

/**
 * Return the RawStateLabel11 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getRawStateLabel11() {
    if (ivjRawStateLabel11 == null) {
        try {
            ivjRawStateLabel11 = new javax.swing.JLabel();
            ivjRawStateLabel11.setName("RawStateLabel11");
            ivjRawStateLabel11.setText("10");
            ivjRawStateLabel11.setMaximumSize(new java.awt.Dimension(18, 19));
            ivjRawStateLabel11.setFont(new java.awt.Font("dialog", 0, 12));
            ivjRawStateLabel11.setEnabled(false);
            ivjRawStateLabel11.setMinimumSize(new java.awt.Dimension(18, 19));
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return ivjRawStateLabel11;
}

/**
 * Return the RawStateLabel2 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getRawStateLabel2() {
	if (ivjRawStateLabel2 == null) {
		try {
			ivjRawStateLabel2 = new javax.swing.JLabel();
			ivjRawStateLabel2.setName("RawStateLabel2");
			ivjRawStateLabel2.setFont(new java.awt.Font("dialog", 0, 12));
			ivjRawStateLabel2.setText("1 (Close)");
			ivjRawStateLabel2.setEnabled(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRawStateLabel2;
}

/**
 * Return the RawStateLabel4 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getRawStateLabel3() {
	if (ivjRawStateLabel3 == null) {
		try {
			ivjRawStateLabel3 = new javax.swing.JLabel();
			ivjRawStateLabel3.setName("RawStateLabel3");
			ivjRawStateLabel3.setFont(new java.awt.Font("dialog", 0, 12));
			ivjRawStateLabel3.setText("2");
			ivjRawStateLabel3.setEnabled(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRawStateLabel3;
}

/**
 * Return the RawStateLabel4 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getRawStateLabel4() {
	if (ivjRawStateLabel4 == null) {
		try {
			ivjRawStateLabel4 = new javax.swing.JLabel();
			ivjRawStateLabel4.setName("RawStateLabel4");
			ivjRawStateLabel4.setFont(new java.awt.Font("dialog", 0, 12));
			ivjRawStateLabel4.setText("3");
			ivjRawStateLabel4.setEnabled(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRawStateLabel4;
}

/**
 * Return the RawStateLabel5 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getRawStateLabel5() {
	if (ivjRawStateLabel5 == null) {
		try {
			ivjRawStateLabel5 = new javax.swing.JLabel();
			ivjRawStateLabel5.setName("RawStateLabel5");
			ivjRawStateLabel5.setFont(new java.awt.Font("dialog", 0, 12));
			ivjRawStateLabel5.setText("4");
			ivjRawStateLabel5.setEnabled(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRawStateLabel5;
}

/**
 * Return the RawStateLabel6 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getRawStateLabel6() {
	if (ivjRawStateLabel6 == null) {
		try {
			ivjRawStateLabel6 = new javax.swing.JLabel();
			ivjRawStateLabel6.setName("RawStateLabel6");
			ivjRawStateLabel6.setFont(new java.awt.Font("dialog", 0, 12));
			ivjRawStateLabel6.setText("5");
			ivjRawStateLabel6.setEnabled(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRawStateLabel6;
}

/**
 * Return the RawStateLabel8 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getRawStateLabel7() {
	if (ivjRawStateLabel7 == null) {
		try {
			ivjRawStateLabel7 = new javax.swing.JLabel();
			ivjRawStateLabel7.setName("RawStateLabel7");
			ivjRawStateLabel7.setAutoscrolls(false);
			ivjRawStateLabel7.setText("6");
			ivjRawStateLabel7.setDoubleBuffered(false);
			ivjRawStateLabel7.setFont(new java.awt.Font("dialog", 0, 12));
			ivjRawStateLabel7.setEnabled(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRawStateLabel7;
}

/**
 * Return the RawStateLabel8 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getRawStateLabel8() {
	if (ivjRawStateLabel8 == null) {
		try {
			ivjRawStateLabel8 = new javax.swing.JLabel();
			ivjRawStateLabel8.setName("RawStateLabel8");
			ivjRawStateLabel8.setFont(new java.awt.Font("dialog", 0, 12));
			ivjRawStateLabel8.setText("7");
			ivjRawStateLabel8.setEnabled(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRawStateLabel8;
}

/**
 * Return the RawStateLabel92 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getRawStateLabel9() {
	if (ivjRawStateLabel9 == null) {
		try {
			ivjRawStateLabel9 = new javax.swing.JLabel();
			ivjRawStateLabel9.setName("RawStateLabel9");
			ivjRawStateLabel9.setFont(new java.awt.Font("dialog", 0, 12));
			ivjRawStateLabel9.setText("8");
			ivjRawStateLabel9.setEnabled(false);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRawStateLabel9;
}

/**
 * Return the RepeaterLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getRepeaterLabel() {
	if (ivjRepeaterLabel == null) {
		try {
			ivjRepeaterLabel = new javax.swing.JLabel();
			ivjRepeaterLabel.setName("RepeaterLabel");
			ivjRepeaterLabel.setFont(new java.awt.Font("dialog", 0, 12));
			ivjRepeaterLabel.setText("State Text");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRepeaterLabel;
}

/**
 * Return the StateGroupNameLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getStateGroupNameLabel() {
	if (ivjStateGroupNameLabel == null) {
		try {
			ivjStateGroupNameLabel = new javax.swing.JLabel();
			ivjStateGroupNameLabel.setName("StateGroupNameLabel");
			ivjStateGroupNameLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjStateGroupNameLabel.setText("State Group Name:");
			ivjStateGroupNameLabel.setBounds(10, 25, 121, 19);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjStateGroupNameLabel;
}

/**
 * Return the StateGroupNameTextField property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getStateGroupNameTextField() {
	if (ivjStateGroupNameTextField == null) {
		try {
			ivjStateGroupNameTextField = new javax.swing.JTextField();
			ivjStateGroupNameTextField.setName("StateGroupNameTextField");
			ivjStateGroupNameTextField.setPreferredSize(new java.awt.Dimension(150, 21));
			ivjStateGroupNameTextField.setBounds(141, 24, 150, 21);
			ivjStateGroupNameTextField.setMinimumSize(new java.awt.Dimension(150, 21));
			ivjStateGroupNameTextField.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_STATE_GROUP_NAME_LENGTH));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjStateGroupNameTextField;
}

/**
 * Return the StateNameTextField1 property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getStateNameTextField1() {
	if (ivjStateNameTextField1 == null) {
		try {
			ivjStateNameTextField1 = new javax.swing.JTextField();
			ivjStateNameTextField1.setName("StateNameTextField1");
			ivjStateNameTextField1.setPreferredSize(new java.awt.Dimension(1300, 22));
			ivjStateNameTextField1.setMinimumSize(new java.awt.Dimension(130, 22));
			ivjStateNameTextField1.setColumns(0);
			ivjStateNameTextField1.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_STATE_NAME_LENGTH));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjStateNameTextField1;
}

/**
 * Return the StateNameTextField9 property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getStateNameTextField10() {
	if (ivjStateNameTextField10 == null) {
		try {
			ivjStateNameTextField10 = new javax.swing.JTextField();
			ivjStateNameTextField10.setName("StateNameTextField10");
			ivjStateNameTextField10.setPreferredSize(new java.awt.Dimension(1300, 22));
			ivjStateNameTextField10.setMinimumSize(new java.awt.Dimension(130, 22));
			ivjStateNameTextField10.setEnabled(false);
			ivjStateNameTextField10.setColumns(0);
			ivjStateNameTextField10.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_STATE_NAME_LENGTH));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjStateNameTextField10;
}

/**
 * Return the StateNameTextField11 property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getStateNameTextField11() {
    if (ivjStateNameTextField11 == null) {
        try {
            ivjStateNameTextField11 = new javax.swing.JTextField();
            ivjStateNameTextField11.setName("StateNameTextField11");
            ivjStateNameTextField11.setPreferredSize(new java.awt.Dimension(1300, 22));
            ivjStateNameTextField11.setMinimumSize(new java.awt.Dimension(130, 22));
            ivjStateNameTextField11.setEnabled(false);
            ivjStateNameTextField11.setColumns(0);
            ivjStateNameTextField11.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_STATE_NAME_LENGTH));
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return ivjStateNameTextField11;
}

/**
 * Return the StateNameTextField2 property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getStateNameTextField2() {
	if (ivjStateNameTextField2 == null) {
		try {
			ivjStateNameTextField2 = new javax.swing.JTextField();
			ivjStateNameTextField2.setName("StateNameTextField2");
			ivjStateNameTextField2.setPreferredSize(new java.awt.Dimension(1300, 22));
			ivjStateNameTextField2.setMinimumSize(new java.awt.Dimension(130, 22));
			ivjStateNameTextField2.setEnabled(false);
			ivjStateNameTextField2.setColumns(0);
			ivjStateNameTextField2.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_STATE_NAME_LENGTH));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjStateNameTextField2;
}


/**
 * Return the StateNameTextField4 property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getStateNameTextField3() {
	if (ivjStateNameTextField3 == null) {
		try {
			ivjStateNameTextField3 = new javax.swing.JTextField();
			ivjStateNameTextField3.setName("StateNameTextField3");
			ivjStateNameTextField3.setPreferredSize(new java.awt.Dimension(1300, 22));
			ivjStateNameTextField3.setMinimumSize(new java.awt.Dimension(130, 22));
			ivjStateNameTextField3.setEnabled(false);
			ivjStateNameTextField3.setColumns(0);
			ivjStateNameTextField3.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_STATE_NAME_LENGTH));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjStateNameTextField3;
}


/**
 * Return the StateNameTextField4 property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getStateNameTextField4() {
	if (ivjStateNameTextField4 == null) {
		try {
			ivjStateNameTextField4 = new javax.swing.JTextField();
			ivjStateNameTextField4.setName("StateNameTextField4");
			ivjStateNameTextField4.setPreferredSize(new java.awt.Dimension(1300, 22));
			ivjStateNameTextField4.setMinimumSize(new java.awt.Dimension(130, 22));
			ivjStateNameTextField4.setEnabled(false);
			ivjStateNameTextField4.setColumns(0);
			ivjStateNameTextField4.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_STATE_NAME_LENGTH));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjStateNameTextField4;
}


/**
 * Return the StateNameTextField5 property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getStateNameTextField5() {
	if (ivjStateNameTextField5 == null) {
		try {
			ivjStateNameTextField5 = new javax.swing.JTextField();
			ivjStateNameTextField5.setName("StateNameTextField5");
			ivjStateNameTextField5.setPreferredSize(new java.awt.Dimension(1300, 22));
			ivjStateNameTextField5.setMinimumSize(new java.awt.Dimension(130, 22));
			ivjStateNameTextField5.setEnabled(false);
			ivjStateNameTextField5.setColumns(0);
			ivjStateNameTextField5.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_STATE_NAME_LENGTH));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjStateNameTextField5;
}


/**
 * Return the StateNameTextField6 property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getStateNameTextField6() {
	if (ivjStateNameTextField6 == null) {
		try {
			ivjStateNameTextField6 = new javax.swing.JTextField();
			ivjStateNameTextField6.setName("StateNameTextField6");
			ivjStateNameTextField6.setPreferredSize(new java.awt.Dimension(1300, 22));
			ivjStateNameTextField6.setMinimumSize(new java.awt.Dimension(130, 22));
			ivjStateNameTextField6.setEnabled(false);
			ivjStateNameTextField6.setColumns(0);
			ivjStateNameTextField6.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_STATE_NAME_LENGTH));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjStateNameTextField6;
}


/**
 * Return the StateNameTextField8 property value.
 * @return javax.swing.JTextField
 */
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
			ivjStateNameTextField7.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_STATE_NAME_LENGTH));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjStateNameTextField7;
}


/**
 * Return the StateNameTextField8a property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getStateNameTextField8() {
	if (ivjStateNameTextField8 == null) {
		try {
			ivjStateNameTextField8 = new javax.swing.JTextField();
			ivjStateNameTextField8.setName("StateNameTextField8");
			ivjStateNameTextField8.setPreferredSize(new java.awt.Dimension(1300, 22));
			ivjStateNameTextField8.setMinimumSize(new java.awt.Dimension(130, 22));
			ivjStateNameTextField8.setEnabled(false);
			ivjStateNameTextField8.setColumns(0);
			ivjStateNameTextField8.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_STATE_NAME_LENGTH));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjStateNameTextField8;
}


/**
 * Return the StateNameTextField92 property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getStateNameTextField9() {
	if (ivjStateNameTextField9 == null) {
		try {
			ivjStateNameTextField9 = new javax.swing.JTextField();
			ivjStateNameTextField9.setName("StateNameTextField9");
			ivjStateNameTextField9.setPreferredSize(new java.awt.Dimension(1300, 22));
			ivjStateNameTextField9.setMinimumSize(new java.awt.Dimension(130, 22));
			ivjStateNameTextField9.setEnabled(false);
			ivjStateNameTextField9.setColumns(0);
			ivjStateNameTextField9.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_STATE_NAME_LENGTH));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjStateNameTextField9;
}


/**
 * Return the StateNumberLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getStateNumberLabel() {
	if (ivjStateNumberLabel == null) {
		try {
			ivjStateNumberLabel = new javax.swing.JLabel();
			ivjStateNumberLabel.setName("StateNumberLabel");
			ivjStateNumberLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjStateNumberLabel.setText("Number of States:");
			ivjStateNumberLabel.setBounds(10, 52, 113, 19);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjStateNumberLabel;
}


/**
 * Return the StateNumberSpinner property value.
 * @return com.klg.jclass.field.JCSpinField
 */
private com.klg.jclass.field.JCSpinField getStateNumberSpinner() {
	if (ivjStateNumberSpinner == null) {
		try {
			ivjStateNumberSpinner = new com.klg.jclass.field.JCSpinField();
			ivjStateNumberSpinner.setName("StateNumberSpinner");
			ivjStateNumberSpinner.setPreferredSize(new java.awt.Dimension(50, 22));
			ivjStateNumberSpinner.setBounds(141, 51, 50, 22);
			ivjStateNumberSpinner.setMinimumSize(new java.awt.Dimension(50, 22));
			ivjStateNumberSpinner.setDataProperties(new com.klg.jclass.field.DataProperties(
				new com.klg.jclass.field.validate.JCIntegerValidator(
					null, new Integer(2), new Integer(STATE_COUNT), null, true, null, 
					new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, 
					new Integer(2)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, 
						new Integer(1)), new com.klg.jclass.field.JCInvalidInfo(true, 2, new java.awt.Color(0, 0, 0, 255), 
						new java.awt.Color(255, 255, 255, 255))));

		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjStateNumberSpinner;
}


/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
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
			constraintsRepeaterLabel.gridx = 3; constraintsRepeaterLabel.gridy = 1;
			constraintsRepeaterLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRepeaterLabel.insets = new java.awt.Insets(0, 5, 5, 0);
			getStatesPanel().add(getRepeaterLabel(), constraintsRepeaterLabel);

			java.awt.GridBagConstraints constraintsVariableBitsLabel = new java.awt.GridBagConstraints();
			constraintsVariableBitsLabel.gridx = 4; constraintsVariableBitsLabel.gridy = 1;
			constraintsVariableBitsLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsVariableBitsLabel.insets = new java.awt.Insets(0, 5, 5, 0);
			getStatesPanel().add(getVariableBitsLabel(), constraintsVariableBitsLabel);

			java.awt.GridBagConstraints constraintsRawStateColumnLabel = new java.awt.GridBagConstraints();
			constraintsRawStateColumnLabel.gridx = 1; constraintsRawStateColumnLabel.gridy = 1;
			constraintsRawStateColumnLabel.gridwidth = 2;
			constraintsRawStateColumnLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRawStateColumnLabel.insets = new java.awt.Insets(0, 5, 5, 0);
			getStatesPanel().add(getRawStateColumnLabel(), constraintsRawStateColumnLabel);

			java.awt.GridBagConstraints constraintsStateNameTextField1 = new java.awt.GridBagConstraints();
			constraintsStateNameTextField1.gridx = 2; constraintsStateNameTextField1.gridy = 2;
			constraintsStateNameTextField1.gridwidth = 2;
			constraintsStateNameTextField1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsStateNameTextField1.anchor = java.awt.GridBagConstraints.WEST;
			constraintsStateNameTextField1.weightx = 1.0;
			constraintsStateNameTextField1.insets = new java.awt.Insets(0, 5, 5, 0);
			getStatesPanel().add(getStateNameTextField1(), constraintsStateNameTextField1);

			java.awt.GridBagConstraints constraintsRawStateLabel1 = new java.awt.GridBagConstraints();
			constraintsRawStateLabel1.gridx = 1; constraintsRawStateLabel1.gridy = 2;
			constraintsRawStateLabel1.gridwidth = 2;
			constraintsRawStateLabel1.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRawStateLabel1.insets = new java.awt.Insets(0, 5, 5, 30);
			getStatesPanel().add(getRawStateLabel1(), constraintsRawStateLabel1);

			java.awt.GridBagConstraints constraintsForegroundColorComboBox1 = new java.awt.GridBagConstraints();
			constraintsForegroundColorComboBox1.gridx = 4; constraintsForegroundColorComboBox1.gridy = 2;
			constraintsForegroundColorComboBox1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsForegroundColorComboBox1.anchor = java.awt.GridBagConstraints.WEST;
			constraintsForegroundColorComboBox1.weightx = 1.0;
			constraintsForegroundColorComboBox1.insets = new java.awt.Insets(0, 5, 5, 0);
			getStatesPanel().add(getForegroundColorComboBox1(), constraintsForegroundColorComboBox1);

			java.awt.GridBagConstraints constraintsRawStateLabel2 = new java.awt.GridBagConstraints();
			constraintsRawStateLabel2.gridx = 1; constraintsRawStateLabel2.gridy = 3;
			constraintsRawStateLabel2.gridwidth = 2;
			constraintsRawStateLabel2.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRawStateLabel2.insets = new java.awt.Insets(0, 5, 5, 30);
			getStatesPanel().add(getRawStateLabel2(), constraintsRawStateLabel2);

			java.awt.GridBagConstraints constraintsRawStateLabel3 = new java.awt.GridBagConstraints();
			constraintsRawStateLabel3.gridx = 1; constraintsRawStateLabel3.gridy = 4;
			constraintsRawStateLabel3.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRawStateLabel3.insets = new java.awt.Insets(0, 5, 5,25);
			getStatesPanel().add(getRawStateLabel3(), constraintsRawStateLabel3);

			java.awt.GridBagConstraints constraintsRawStateLabel4 = new java.awt.GridBagConstraints();
			constraintsRawStateLabel4.gridx = 1; constraintsRawStateLabel4.gridy = 5;
			constraintsRawStateLabel4.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRawStateLabel4.insets = new java.awt.Insets(0, 5, 5, 30);
			getStatesPanel().add(getRawStateLabel4(), constraintsRawStateLabel4);

			java.awt.GridBagConstraints constraintsRawStateLabel5 = new java.awt.GridBagConstraints();
			constraintsRawStateLabel5.gridx = 1; constraintsRawStateLabel5.gridy = 6;
			constraintsRawStateLabel5.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRawStateLabel5.insets = new java.awt.Insets(0, 5, 5, 30);
			getStatesPanel().add(getRawStateLabel5(), constraintsRawStateLabel5);

			java.awt.GridBagConstraints constraintsRawStateLabel6 = new java.awt.GridBagConstraints();
			constraintsRawStateLabel6.gridx = 1; constraintsRawStateLabel6.gridy = 7;
			constraintsRawStateLabel6.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRawStateLabel6.insets = new java.awt.Insets(0, 5, 5, 30);
			getStatesPanel().add(getRawStateLabel6(), constraintsRawStateLabel6);

			java.awt.GridBagConstraints constraintsStateNameTextField2 = new java.awt.GridBagConstraints();
			constraintsStateNameTextField2.gridx = 2; constraintsStateNameTextField2.gridy = 3;
			constraintsStateNameTextField2.gridwidth = 2;
			constraintsStateNameTextField2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsStateNameTextField2.anchor = java.awt.GridBagConstraints.WEST;
			constraintsStateNameTextField2.weightx = 1.0;
			constraintsStateNameTextField2.insets = new java.awt.Insets(0, 5, 5, 0);
			getStatesPanel().add(getStateNameTextField2(), constraintsStateNameTextField2);

			java.awt.GridBagConstraints constraintsStateNameTextField3 = new java.awt.GridBagConstraints();
			constraintsStateNameTextField3.gridx = 2; constraintsStateNameTextField3.gridy = 4;
			constraintsStateNameTextField3.gridwidth = 2;
			constraintsStateNameTextField3.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsStateNameTextField3.anchor = java.awt.GridBagConstraints.WEST;
			constraintsStateNameTextField3.weightx = 1.0;
			constraintsStateNameTextField3.insets = new java.awt.Insets(0, 5, 5, 0);
			getStatesPanel().add(getStateNameTextField3(), constraintsStateNameTextField3);

			java.awt.GridBagConstraints constraintsStateNameTextField4 = new java.awt.GridBagConstraints();
			constraintsStateNameTextField4.gridx = 2; constraintsStateNameTextField4.gridy = 5;
			constraintsStateNameTextField4.gridwidth = 2;
			constraintsStateNameTextField4.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsStateNameTextField4.anchor = java.awt.GridBagConstraints.WEST;
			constraintsStateNameTextField4.weightx = 1.0;
			constraintsStateNameTextField4.insets = new java.awt.Insets(0, 5, 5, 0);
			getStatesPanel().add(getStateNameTextField4(), constraintsStateNameTextField4);

			java.awt.GridBagConstraints constraintsStateNameTextField5 = new java.awt.GridBagConstraints();
			constraintsStateNameTextField5.gridx = 2; constraintsStateNameTextField5.gridy = 6;
			constraintsStateNameTextField5.gridwidth = 2;
			constraintsStateNameTextField5.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsStateNameTextField5.anchor = java.awt.GridBagConstraints.WEST;
			constraintsStateNameTextField5.weightx = 1.0;
			constraintsStateNameTextField5.insets = new java.awt.Insets(0, 5, 5, 0);
			getStatesPanel().add(getStateNameTextField5(), constraintsStateNameTextField5);

			java.awt.GridBagConstraints constraintsStateNameTextField6 = new java.awt.GridBagConstraints();
			constraintsStateNameTextField6.gridx = 2; constraintsStateNameTextField6.gridy = 7;
			constraintsStateNameTextField6.gridwidth = 2;
			constraintsStateNameTextField6.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsStateNameTextField6.anchor = java.awt.GridBagConstraints.WEST;
			constraintsStateNameTextField6.weightx = 1.0;
			constraintsStateNameTextField6.insets = new java.awt.Insets(0, 5, 5, 0);
			getStatesPanel().add(getStateNameTextField6(), constraintsStateNameTextField6);

			java.awt.GridBagConstraints constraintsForegroundColorComboBox2 = new java.awt.GridBagConstraints();
			constraintsForegroundColorComboBox2.gridx = 4; constraintsForegroundColorComboBox2.gridy = 3;
			constraintsForegroundColorComboBox2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsForegroundColorComboBox2.anchor = java.awt.GridBagConstraints.WEST;
			constraintsForegroundColorComboBox2.weightx = 1.0;
			constraintsForegroundColorComboBox2.insets = new java.awt.Insets(0, 5, 5, 0);
			getStatesPanel().add(getForegroundColorComboBox2(), constraintsForegroundColorComboBox2);

			java.awt.GridBagConstraints constraintsForegroundColorComboBox3 = new java.awt.GridBagConstraints();
			constraintsForegroundColorComboBox3.gridx = 4; constraintsForegroundColorComboBox3.gridy = 4;
			constraintsForegroundColorComboBox3.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsForegroundColorComboBox3.anchor = java.awt.GridBagConstraints.WEST;
			constraintsForegroundColorComboBox3.weightx = 1.0;
			constraintsForegroundColorComboBox3.insets = new java.awt.Insets(0, 5, 5, 0);
			getStatesPanel().add(getForegroundColorComboBox3(), constraintsForegroundColorComboBox3);

			java.awt.GridBagConstraints constraintsForegroundColorComboBox4 = new java.awt.GridBagConstraints();
			constraintsForegroundColorComboBox4.gridx = 4; constraintsForegroundColorComboBox4.gridy = 5;
			constraintsForegroundColorComboBox4.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsForegroundColorComboBox4.anchor = java.awt.GridBagConstraints.WEST;
			constraintsForegroundColorComboBox4.weightx = 1.0;
			constraintsForegroundColorComboBox4.insets = new java.awt.Insets(0, 5, 5, 0);
			getStatesPanel().add(getForegroundColorComboBox4(), constraintsForegroundColorComboBox4);

			java.awt.GridBagConstraints constraintsForegroundColorComboBox5 = new java.awt.GridBagConstraints();
			constraintsForegroundColorComboBox5.gridx = 4; constraintsForegroundColorComboBox5.gridy = 6;
			constraintsForegroundColorComboBox5.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsForegroundColorComboBox5.anchor = java.awt.GridBagConstraints.WEST;
			constraintsForegroundColorComboBox5.weightx = 1.0;
			constraintsForegroundColorComboBox5.insets = new java.awt.Insets(0, 5, 5, 0);
			getStatesPanel().add(getForegroundColorComboBox5(), constraintsForegroundColorComboBox5);

			java.awt.GridBagConstraints constraintsForegroundColorComboBox6 = new java.awt.GridBagConstraints();
			constraintsForegroundColorComboBox6.gridx = 4; constraintsForegroundColorComboBox6.gridy = 7;
			constraintsForegroundColorComboBox6.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsForegroundColorComboBox6.anchor = java.awt.GridBagConstraints.WEST;
			constraintsForegroundColorComboBox6.weightx = 1.0;
			constraintsForegroundColorComboBox6.insets = new java.awt.Insets(0, 5, 5, 0);
			getStatesPanel().add(getForegroundColorComboBox6(), constraintsForegroundColorComboBox6);

			java.awt.GridBagConstraints constraintsRawStateLabel10 = new java.awt.GridBagConstraints();
			constraintsRawStateLabel10.gridx = 1; constraintsRawStateLabel10.gridy = 11;
			constraintsRawStateLabel10.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRawStateLabel10.insets = new java.awt.Insets(0, 5, 5, 30);
			getStatesPanel().add(getRawStateLabel10(), constraintsRawStateLabel10);

			java.awt.GridBagConstraints constraintsStateNameTextField10 = new java.awt.GridBagConstraints();
			constraintsStateNameTextField10.gridx = 2; constraintsStateNameTextField10.gridy = 11;
			constraintsStateNameTextField10.gridwidth = 2;
			constraintsStateNameTextField10.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsStateNameTextField10.anchor = java.awt.GridBagConstraints.WEST;
			constraintsStateNameTextField10.weightx = 1.0;
			constraintsStateNameTextField10.insets = new java.awt.Insets(0, 5, 5, 0);
			getStatesPanel().add(getStateNameTextField10(), constraintsStateNameTextField10);

			java.awt.GridBagConstraints constraintsForegroundColorComboBox10 = new java.awt.GridBagConstraints();
			constraintsForegroundColorComboBox10.gridx = 4; constraintsForegroundColorComboBox10.gridy = 11;
			constraintsForegroundColorComboBox10.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsForegroundColorComboBox10.anchor = java.awt.GridBagConstraints.WEST;
			constraintsForegroundColorComboBox10.weightx = 1.0;
			constraintsForegroundColorComboBox10.insets = new java.awt.Insets(0, 5, 5, 0);
			getStatesPanel().add(getForegroundColorComboBox10(), constraintsForegroundColorComboBox10);
            
            java.awt.GridBagConstraints constraintsRawStateLabel11 = new java.awt.GridBagConstraints();
            constraintsRawStateLabel11.gridx = 1; constraintsRawStateLabel11.gridy = 12;
            constraintsRawStateLabel11.anchor = java.awt.GridBagConstraints.WEST;
            constraintsRawStateLabel11.insets = new java.awt.Insets(0, 5, 5, 30);
            getStatesPanel().add(getRawStateLabel11  (), constraintsRawStateLabel11);

            java.awt.GridBagConstraints constraintsStateNameTextField11 = new java.awt.GridBagConstraints();
            constraintsStateNameTextField11.gridx = 2; constraintsStateNameTextField11.gridy = 12;
            constraintsStateNameTextField11.gridwidth = 2;
            constraintsStateNameTextField11.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsStateNameTextField11.anchor = java.awt.GridBagConstraints.WEST;
            constraintsStateNameTextField11.weightx = 1.0;
            constraintsStateNameTextField11.insets = new java.awt.Insets(0, 5, 5, 0);
            getStatesPanel().add(getStateNameTextField11(), constraintsStateNameTextField11);

            java.awt.GridBagConstraints constraintsForegroundColorComboBox11 = new java.awt.GridBagConstraints();
            constraintsForegroundColorComboBox11.gridx = 4; constraintsForegroundColorComboBox11.gridy = 12;
            constraintsForegroundColorComboBox11.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsForegroundColorComboBox11.anchor = java.awt.GridBagConstraints.WEST;
            constraintsForegroundColorComboBox11.weightx = 1.0;
            constraintsForegroundColorComboBox11.insets = new java.awt.Insets(0, 5, 5, 0);
            getStatesPanel().add(getForegroundColorComboBox11(), constraintsForegroundColorComboBox11);

			java.awt.GridBagConstraints constraintsForegroundColorComboBox9 = new java.awt.GridBagConstraints();
			constraintsForegroundColorComboBox9.gridx = 4; constraintsForegroundColorComboBox9.gridy = 10;
			constraintsForegroundColorComboBox9.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsForegroundColorComboBox9.anchor = java.awt.GridBagConstraints.WEST;
			constraintsForegroundColorComboBox9.weightx = 1.0;
			constraintsForegroundColorComboBox9.insets = new java.awt.Insets(0, 5, 5, 0);
			getStatesPanel().add(getForegroundColorComboBox9(), constraintsForegroundColorComboBox9);

			java.awt.GridBagConstraints constraintsStateNameTextField9 = new java.awt.GridBagConstraints();
			constraintsStateNameTextField9.gridx = 2; constraintsStateNameTextField9.gridy = 10;
			constraintsStateNameTextField9.gridwidth = 2;
			constraintsStateNameTextField9.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsStateNameTextField9.anchor = java.awt.GridBagConstraints.WEST;
			constraintsStateNameTextField9.weightx = 1.0;
			constraintsStateNameTextField9.insets = new java.awt.Insets(0, 5, 5, 0);
			getStatesPanel().add(getStateNameTextField9(), constraintsStateNameTextField9);

			java.awt.GridBagConstraints constraintsRawStateLabel9 = new java.awt.GridBagConstraints();
			constraintsRawStateLabel9.gridx = 1; constraintsRawStateLabel9.gridy = 10;
			constraintsRawStateLabel9.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRawStateLabel9.insets = new java.awt.Insets(0, 5, 5, 30);
			getStatesPanel().add(getRawStateLabel9(), constraintsRawStateLabel9);

			java.awt.GridBagConstraints constraintsRawStateLabel8 = new java.awt.GridBagConstraints();
			constraintsRawStateLabel8.gridx = 1; constraintsRawStateLabel8.gridy = 9;
			constraintsRawStateLabel8.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRawStateLabel8.ipadx = 1;
			constraintsRawStateLabel8.insets = new java.awt.Insets(0, 5, 5, 30);
			getStatesPanel().add(getRawStateLabel8(), constraintsRawStateLabel8);

			java.awt.GridBagConstraints constraintsStateNameTextField8 = new java.awt.GridBagConstraints();
			constraintsStateNameTextField8.gridx = 2; constraintsStateNameTextField8.gridy = 9;
			constraintsStateNameTextField8.gridwidth = 2;
			constraintsStateNameTextField8.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsStateNameTextField8.anchor = java.awt.GridBagConstraints.WEST;
			constraintsStateNameTextField8.weightx = 1.0;
			constraintsStateNameTextField8.insets = new java.awt.Insets(0, 5, 5, 0);
			getStatesPanel().add(getStateNameTextField8(), constraintsStateNameTextField8);

			java.awt.GridBagConstraints constraintsForegroundColorComboBox8 = new java.awt.GridBagConstraints();
			constraintsForegroundColorComboBox8.gridx = 4; constraintsForegroundColorComboBox8.gridy = 9;
			constraintsForegroundColorComboBox8.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsForegroundColorComboBox8.anchor = java.awt.GridBagConstraints.WEST;
			constraintsForegroundColorComboBox8.weightx = 1.0;
			constraintsForegroundColorComboBox8.insets = new java.awt.Insets(0, 5, 5, 0);
			getStatesPanel().add(getForegroundColorComboBox8(), constraintsForegroundColorComboBox8);

			java.awt.GridBagConstraints constraintsForegroundColorComboBox7 = new java.awt.GridBagConstraints();
			constraintsForegroundColorComboBox7.gridx = 4; constraintsForegroundColorComboBox7.gridy = 8;
			constraintsForegroundColorComboBox7.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsForegroundColorComboBox7.anchor = java.awt.GridBagConstraints.WEST;
			constraintsForegroundColorComboBox7.weightx = 1.0;
			constraintsForegroundColorComboBox7.insets = new java.awt.Insets(0, 5, 5, 0);
			getStatesPanel().add(getForegroundColorComboBox7(), constraintsForegroundColorComboBox7);

			java.awt.GridBagConstraints constraintsStateNameTextField7 = new java.awt.GridBagConstraints();
			constraintsStateNameTextField7.gridx = 2; constraintsStateNameTextField7.gridy = 8;
			constraintsStateNameTextField7.gridwidth = 2;
			constraintsStateNameTextField7.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsStateNameTextField7.anchor = java.awt.GridBagConstraints.WEST;
			constraintsStateNameTextField7.weightx = 1.0;
			constraintsStateNameTextField7.insets = new java.awt.Insets(0, 5, 5, 0);
			getStatesPanel().add(getStateNameTextField7(), constraintsStateNameTextField7);

			java.awt.GridBagConstraints constraintsRawStateLabel7 = new java.awt.GridBagConstraints();
			constraintsRawStateLabel7.gridx = 1; constraintsRawStateLabel7.gridy = 8;
			constraintsRawStateLabel7.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRawStateLabel7.insets = new java.awt.Insets(0, 5, 5, 30);
			getStatesPanel().add(getRawStateLabel7(), constraintsRawStateLabel7);

			java.awt.GridBagConstraints constraintsJLabelImage = new java.awt.GridBagConstraints();
			constraintsJLabelImage.gridx = 5; constraintsJLabelImage.gridy = 1;
			constraintsJLabelImage.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelImage.insets = new java.awt.Insets(0, 5, 5, 0);
			getStatesPanel().add(getJLabelImage(), constraintsJLabelImage);

			java.awt.GridBagConstraints constraintsJButtonImage1 = new java.awt.GridBagConstraints();
			constraintsJButtonImage1.gridx = 5; constraintsJButtonImage1.gridy = 2;
			constraintsJButtonImage1.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJButtonImage1.insets = new java.awt.Insets(0, 5, 5, 0);
			getStatesPanel().add(getJButtonImage1(), constraintsJButtonImage1);

			java.awt.GridBagConstraints constraintsJButtonImage2 = new java.awt.GridBagConstraints();
			constraintsJButtonImage2.gridx = 5; constraintsJButtonImage2.gridy = 3;
			constraintsJButtonImage2.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJButtonImage2.insets = new java.awt.Insets(0, 5, 5, 0);
			getStatesPanel().add(getJButtonImage2(), constraintsJButtonImage2);

			java.awt.GridBagConstraints constraintsJButtonImage3 = new java.awt.GridBagConstraints();
			constraintsJButtonImage3.gridx = 5; constraintsJButtonImage3.gridy = 4;
			constraintsJButtonImage3.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJButtonImage3.insets = new java.awt.Insets(0, 5, 5, 0);
			getStatesPanel().add(getJButtonImage3(), constraintsJButtonImage3);

			java.awt.GridBagConstraints constraintsJButtonImage4 = new java.awt.GridBagConstraints();
			constraintsJButtonImage4.gridx = 5; constraintsJButtonImage4.gridy = 5;
			constraintsJButtonImage4.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJButtonImage4.insets = new java.awt.Insets(0, 5, 5, 0);
			getStatesPanel().add(getJButtonImage4(), constraintsJButtonImage4);

			java.awt.GridBagConstraints constraintsJButtonImage5 = new java.awt.GridBagConstraints();
			constraintsJButtonImage5.gridx = 5; constraintsJButtonImage5.gridy = 6;
			constraintsJButtonImage5.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJButtonImage5.insets = new java.awt.Insets(0, 5, 5, 0);
			getStatesPanel().add(getJButtonImage5(), constraintsJButtonImage5);

			java.awt.GridBagConstraints constraintsJButtonImage6 = new java.awt.GridBagConstraints();
			constraintsJButtonImage6.gridx = 5; constraintsJButtonImage6.gridy = 7;
			constraintsJButtonImage6.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJButtonImage6.insets = new java.awt.Insets(0, 5, 5, 0);
			getStatesPanel().add(getJButtonImage6(), constraintsJButtonImage6);

			java.awt.GridBagConstraints constraintsJButtonImage7 = new java.awt.GridBagConstraints();
			constraintsJButtonImage7.gridx = 5; constraintsJButtonImage7.gridy = 8;
			constraintsJButtonImage7.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJButtonImage7.insets = new java.awt.Insets(0, 5, 5, 0);
			getStatesPanel().add(getJButtonImage7(), constraintsJButtonImage7);

			java.awt.GridBagConstraints constraintsJButtonImage8 = new java.awt.GridBagConstraints();
			constraintsJButtonImage8.gridx = 5; constraintsJButtonImage8.gridy = 9;
			constraintsJButtonImage8.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJButtonImage8.insets = new java.awt.Insets(0, 5, 5, 0);
			getStatesPanel().add(getJButtonImage8(), constraintsJButtonImage8);

			java.awt.GridBagConstraints constraintsJButtonImage9 = new java.awt.GridBagConstraints();
			constraintsJButtonImage9.gridx = 5; constraintsJButtonImage9.gridy = 10;
			constraintsJButtonImage9.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJButtonImage9.insets = new java.awt.Insets(0, 5, 5, 0);
			getStatesPanel().add(getJButtonImage9(), constraintsJButtonImage9);

			java.awt.GridBagConstraints constraintsJButtonImage10 = new java.awt.GridBagConstraints();
			constraintsJButtonImage10.gridx = 5; constraintsJButtonImage10.gridy = 11;
			constraintsJButtonImage10.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJButtonImage10.insets = new java.awt.Insets(0, 5, 5, 0);
			getStatesPanel().add(getJButtonImage10(), constraintsJButtonImage10);
            
            java.awt.GridBagConstraints constraintsJButtonImage11 = new java.awt.GridBagConstraints();
            constraintsJButtonImage11.gridx = 5; constraintsJButtonImage11.gridy = 12;
            constraintsJButtonImage11.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJButtonImage11.insets = new java.awt.Insets(0, 5, 5, 0);
            getStatesPanel().add(getJButtonImage11(), constraintsJButtonImage11);
		} catch (java.lang.Throwable ivjExc) {
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
   
   com.cannontech.database.data.state.State tempStateData = null;

   for(int i=0;i<numberOfStates.intValue();i++)
   {
      Integer yukImgId = 
                (imageButton[i].getClientProperty("LiteYukonImage") == null)
                ? new Integer(com.cannontech.database.db.state.YukonImage.NONE_IMAGE_ID)
                : new Integer( ((LiteYukonImage)imageButton[i].getClientProperty("LiteYukonImage")).getImageID() );
      
      tempStateData = new com.cannontech.database.data.state.State();
      tempStateData.setState(
             new com.cannontech.database.db.state.State( 
                     gs.getStateGroup().getStateGroupID(),
                     new Integer(i),
                     stateNameTextFields[i].getText(),
                     new Integer(foregroundColorComboBoxes[i].getSelectedIndex()),
                     new Integer(com.cannontech.common.gui.util.Colors.BLACK_ID),
                     yukImgId ) );
                                    
      gs.getStatesVector().add(tempStateData);
   }

   return val;
}

/**
 * Return the VariableBitsLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getVariableBitsLabel() {
	if (ivjVariableBitsLabel == null) {
		try {
			ivjVariableBitsLabel = new javax.swing.JLabel();
			ivjVariableBitsLabel.setName("VariableBitsLabel");
			ivjVariableBitsLabel.setFont(new java.awt.Font("dialog", 0, 12));
			ivjVariableBitsLabel.setText("Color");
		} catch (java.lang.Throwable ivjExc) {
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
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}


/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
private void initConnections() throws java.lang.Exception {

	getStateNumberSpinner().addValueListener(this);
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
    getStateNameTextField11().addCaretListener(this);
	getForegroundColorComboBox7().addItemListener(this);
	getForegroundColorComboBox8().addItemListener(this);
	getForegroundColorComboBox9().addItemListener(this);
	getForegroundColorComboBox10().addItemListener(this);
    getForegroundColorComboBox11().addItemListener(this);
	getJButtonImage1().addActionListener(this);
	getJButtonImage2().addActionListener(this);
	getJButtonImage3().addActionListener(this);
	getJButtonImage4().addActionListener(this);
	getJButtonImage5().addActionListener(this);
	getJButtonImage6().addActionListener(this);
	getJButtonImage7().addActionListener(this);
	getJButtonImage8().addActionListener(this);
	getJButtonImage9().addActionListener(this);
	getJButtonImage10().addActionListener(this);
    getJButtonImage11().addActionListener(this);
}


/**
 * Initialize the class.
 */
private void initialize() {
	try {
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
	rawStateLabels = new javax.swing.JLabel[STATE_COUNT];
	stateNameTextFields = new javax.swing.JTextField[STATE_COUNT];
	foregroundColorComboBoxes = new javax.swing.JComboBox[STATE_COUNT];
	imageButton = new javax.swing.JButton[STATE_COUNT];
   
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
    rawStateLabels[10] = getRawStateLabel11();
	
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
    stateNameTextFields[10] = getStateNameTextField11();
		
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
    foregroundColorComboBoxes[10] = getForegroundColorComboBox11();

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
        foregroundColorComboBoxes[i].addItem(com.cannontech.common.gui.util.Colors.PINK_STR_ID);
        
	}
   
   imageButton[0] = getJButtonImage1();
   imageButton[1] = getJButtonImage2();
   imageButton[2] = getJButtonImage3();
   imageButton[3] = getJButtonImage4();
   imageButton[4] = getJButtonImage5();
   imageButton[5] = getJButtonImage6();
   imageButton[6] = getJButtonImage7();
   imageButton[7] = getJButtonImage8();
   imageButton[8] = getJButtonImage9();
   imageButton[9] = getJButtonImage10();
   imageButton[10] = getJButtonImage11();
}


/**
 * Method to handle events for the ItemListener interface.
 * @param e java.awt.event.ItemEvent
 */
public void itemStateChanged(java.awt.event.ItemEvent e) {
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
    if (e.getSource() == getForegroundColorComboBox11()) 
        connEtoC21(e);
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
public void setValue(Object val) 
{

   com.cannontech.database.data.state.GroupState gs = (com.cannontech.database.data.state.GroupState)val;
   
   String groupName = gs.getStateGroup().getName();
   java.util.Vector statesVector = gs.getStatesVector();

   if( groupName != null )
      getStateGroupNameTextField().setText(groupName);

	//if we are a system reserved group or an analog group, do not allow the number of states
	// to be modified
	if( gs.getStateGroup().getStateGroupID().intValue() <= StateGroupUtils.SYSTEM_STATEGROUPID ||
            gs.getStateGroup().getGroupType().equalsIgnoreCase(StateGroupUtils.GROUP_TYPE_ANALOG))
	{
		getStateNumberSpinner().setEnabled( false );
		getStateNumberSpinner().setToolTipText( "The number of states can NOT be changed for SYSTEM RESERVED or Analog state groups");
		getStateNumberLabel().setToolTipText( "The number of states can NOT be changed for SYSTEM RESERVED or Analog state groups");
	}
	
   getStateNumberSpinner().setValue( new Integer(statesVector.size()) );

   for( int i = 0; i < statesVector.size() && i < STATE_COUNT; i++ )
   {
      rawStateLabels[i].setEnabled(true);
      stateNameTextFields[i].setEnabled(true);
      stateNameTextFields[i].setText( 
            ((com.cannontech.database.data.state.State)statesVector.get(i)).getState().getText() );

      foregroundColorComboBoxes[i].setEnabled(true);
      foregroundColorComboBoxes[i].setSelectedIndex( 
            ((com.cannontech.database.data.state.State)statesVector.get(i)).getState().getForegroundColor().intValue() );
       
      imageButton[i].setEnabled(true);
      
      //set up all the Images for each state that has one
      int yukImgID = ((com.cannontech.database.data.state.State)statesVector.get(i)).getState().getImageID().intValue();
      if( yukImgID >  com.cannontech.database.db.state.YukonImage.NONE_IMAGE_ID )
      {
         IDatabaseCache cache = 
            com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
          
         LiteYukonImage liteYukImg = null;
         synchronized( cache )
         {
            java.util.List imgList = cache.getAllYukonImages();
       
            for( int j = 0; j < imgList.size(); j++ )
               if( ((LiteYukonImage)imgList.get(j)).getImageID() == yukImgID )
               {
                  liteYukImg = (LiteYukonImage)imgList.get(j);
                  break;
               }
         }
         
         //be sure we have found a matching LiteYukonImage
         if( liteYukImg != null )
         {
				setImageButton( 
						imageButton[i],
						new javax.swing.ImageIcon(liteYukImg.getImageValue()),
						liteYukImg );
         }
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
				imageButton[i].setEnabled(true);

				if( stateNameTextFields[i].getText().equals("") )
					stateNameTextFields[i].setText("DefaultStateName" + (Integer.toString(i+1)) );
			}

			for(int i=numberOfStates.intValue();i<stateNameTextFields.length;i++)
			{
				rawStateLabels[i].setEnabled(false);
				stateNameTextFields[i].setEnabled(false);
				foregroundColorComboBoxes[i].setEnabled(false);
				imageButton[i].setEnabled(false);

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
 * connEtoC23:  (JButtonImage2.action.actionPerformed(java.awt.event.ActionEvent) --> GroupStateEditorPanel.jButtonImage_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC23(java.awt.event.ActionEvent arg1) {
	try {
		this.jButtonImage_ActionPerformed(arg1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * connEtoC24:  (JButtonImage3.action.actionPerformed(java.awt.event.ActionEvent) --> GroupStateEditorPanel.jButtonImage_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC24(java.awt.event.ActionEvent arg1) {
	try {
		this.jButtonImage_ActionPerformed(arg1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * connEtoC25:  (JButtonImage4.action.actionPerformed(java.awt.event.ActionEvent) --> GroupStateEditorPanel.jButtonImage_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC25(java.awt.event.ActionEvent arg1) {
	try {
		this.jButtonImage_ActionPerformed(arg1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * connEtoC26:  (JButtonImage5.action.actionPerformed(java.awt.event.ActionEvent) --> GroupStateEditorPanel.jButtonImage_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC26(java.awt.event.ActionEvent arg1) {
	try {
		this.jButtonImage_ActionPerformed(arg1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * connEtoC27:  (JButtonImage6.action.actionPerformed(java.awt.event.ActionEvent) --> GroupStateEditorPanel.jButtonImage_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC27(java.awt.event.ActionEvent arg1) {
	try {
		this.jButtonImage_ActionPerformed(arg1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * connEtoC28:  (JButtonImage7.action.actionPerformed(java.awt.event.ActionEvent) --> GroupStateEditorPanel.jButtonImage_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC28(java.awt.event.ActionEvent arg1) {
	try {
		this.jButtonImage_ActionPerformed(arg1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * connEtoC29:  (JButtonImage8.action.actionPerformed(java.awt.event.ActionEvent) --> GroupStateEditorPanel.jButtonImage_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC29(java.awt.event.ActionEvent arg1) {
	try {
		this.jButtonImage_ActionPerformed(arg1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * connEtoC30:  (JButtonImage9.action.actionPerformed(java.awt.event.ActionEvent) --> GroupStateEditorPanel.jButtonImage_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC30(java.awt.event.ActionEvent arg1) {
	try {
		this.jButtonImage_ActionPerformed(arg1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * connEtoC31:  (JButtonImage10.action.actionPerformed(java.awt.event.ActionEvent) --> GroupStateEditorPanel.jButtonImage_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC31(java.awt.event.ActionEvent arg1) {
	try {
		this.jButtonImage_ActionPerformed(arg1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * Comment
 */
public void jButtonImage_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
    javax.swing.JButton button = (javax.swing.JButton)actionEvent.getSource();
	
	java.awt.Image[] images = null;  
	LiteYukonImage[] yukonImages = null;
	IDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
    
   synchronized( cache )
   {
      java.util.List imgList = cache.getAllYukonImages();
      yukonImages = new  LiteYukonImage[ imgList.size() ];

      for( int i = 0; i < imgList.size(); i++ )
         if( ((LiteYukonImage)imgList.get(i)).getImageValue() != null )
            yukonImages[i] = (LiteYukonImage)imgList.get(i);
   }

   final javax.swing.JDialog d = new javax.swing.JDialog();

   com.cannontech.dbeditor.wizard.state.YukonImagePanel yPanel =
         new com.cannontech.dbeditor.wizard.state.YukonImagePanel( yukonImages )
   {
      public void disposePanel()
      {
         d.setVisible(false);
      }
   };
  
	yPanel.addDataInputPanelListener( this );

    //get our selected image id with the JButton
   LiteYukonImage liteImg = (LiteYukonImage)button.getClientProperty("LiteYukonImage");
   if( liteImg != null )
      yPanel.setSelectedLiteYukonImage( liteImg );
         
   d.setModal( true );      
   d.setTitle("Image Selection");
   d.getContentPane().add( yPanel );
   d.setSize(800, 600);

   //set the location of the dialog to the center of the screen
   d.setLocation( (getToolkit().getScreenSize().width - d.getSize().width) / 2,
                  (getToolkit().getScreenSize().height - d.getSize().height) / 2);
   d.show();   

   if( yPanel.getReturnResult() == yPanel.OK_OPTION )
   {
	   setImageButton( button, yPanel.getSelectedImageIcon(), yPanel.getSelectedLiteImage() );
   }


	fireInputUpdate();
	return;
}

public void inputUpdate( PropertyPanelEvent event )
{
	//if( event.getSource() instanceof com.cannontech.dbeditor.wizard.state.YukonImagePanel )
	if( event.getID() == PropertyPanelEvent.EVENT_DB_INSERT 
		 || event.getID() == PropertyPanelEvent.EVENT_DB_UPDATE
		 || event.getID() == PropertyPanelEvent.EVENT_DB_DELETE )
	{
		fireInputDataPanelEvent( event );	
	}
	else
		fireInputUpdate();
}

/**
 * Insert the method's description here.
 * Creation date: (8/20/2002 4:21:07 PM)
 * @param button javax.swing.JButton
 * @param img java.awt.Image
 */
private void setImageButton(javax.swing.JButton button, javax.swing.ImageIcon img, LiteYukonImage liteYuk )
{
   if( img == null || liteYuk == null )
   {
      button.setText("Image...");
      button.setIcon( null );
      
      liteYuk = LiteYukonImage.NONE_IMAGE;
   }
	else
   {
      //strange, this will preserve the size of the button
      int width = (int)button.getPreferredSize().getWidth() - 12;
      
      //strange, this will preserve the size of the button
      int height = (int)button.getPreferredSize().getHeight() - 9;
   
      //javax.swing.ImageIcon ico = new javax.swing.ImageIcon(
      img.setImage(
         img.getImage().getScaledInstance( 
               width,
               height,
               java.awt.Image.SCALE_AREA_AVERAGING ) );
   
      button.setText(null);
      button.setIcon( img );
   }

   //store our selected image id with the JButton
   button.putClientProperty(
         "LiteYukonImage",
         (LiteYukonImage)liteYuk );
}
}