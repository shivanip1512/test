package com.cannontech.dbeditor.wizard.config;

import java.awt.Dimension;

import com.cannontech.database.data.config.ConfigTwoWay;

/**
 * This type was created in VisualAge.
 */

public class Series300ConfigPanel extends com.cannontech.common.gui.util.DataInputPanel {
	private javax.swing.JLabel ivjNameLabel = null;
	private javax.swing.JTextField ivjNameTextField = null;
	private javax.swing.JRadioButton ivjMinMaxModeButton = null;
	private javax.swing.JRadioButton ivjpeakModeButton = null;
	private javax.swing.JRadioButton ivjKY2WireButton = null;
	private javax.swing.JRadioButton ivjKYZ3WireButton = null;
	private javax.swing.JLabel ivjMpLabel = null;
	private javax.swing.JPanel ivjMultiplierPanel = null;
	private javax.swing.JLabel ivjTimesLabel = null;
	private javax.swing.JLabel ivjMpLabel2 = null;
	private javax.swing.JLabel ivjKeLabel = null;
	private javax.swing.JLabel ivjKeLabel2 = null;
	private javax.swing.JLabel ivjKeLabel3 = null;
	private javax.swing.JTextField ivjKeTextField = null;
	private javax.swing.JTextField ivjKeTextField2 = null;
	private javax.swing.JTextField ivjKeTextField3 = null;
	private javax.swing.JLabel ivjKhLabel = null;
	private javax.swing.JLabel ivjKhLabel2 = null;
	private javax.swing.JLabel ivjKhLabel3 = null;
	private javax.swing.JTextField ivjKhTextField = null;
	private javax.swing.JTextField ivjKhTextField2 = null;
	private javax.swing.JTextField ivjKhTextField3 = null;
	private javax.swing.JRadioButton ivjKY2WireButton2 = null;
	private javax.swing.JRadioButton ivjKY2WireButton3 = null;
	private javax.swing.JRadioButton ivjKYZ3WireButton2 = null;
	private javax.swing.JRadioButton ivjKYZ3WireButton3 = null;
	private javax.swing.JLabel ivjMpLabel3 = null;
	private javax.swing.JTextField ivjMpTextField = null;
	private javax.swing.JTextField ivjMpTextField2 = null;
	private javax.swing.JTextField ivjMpTextField3 = null;
	private javax.swing.JPanel ivjMultiplierPanel2 = null;
	private javax.swing.JPanel ivjMultiplierPanel3 = null;
	private javax.swing.JLabel ivjTimesLabel2 = null;
	private javax.swing.JLabel ivjTimesLabel3 = null;
	javax.swing.ButtonGroup modeButtonGroup = new javax.swing.ButtonGroup();
	javax.swing.ButtonGroup channel1ButtonGroup = new javax.swing.ButtonGroup();
	javax.swing.ButtonGroup channel2ButtonGroup = new javax.swing.ButtonGroup();
	javax.swing.ButtonGroup channel3ButtonGroup = new javax.swing.ButtonGroup();
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JCheckBox ivjChannel1EnableJCheckBox = null;
	private javax.swing.JCheckBox ivjChannel2EnableJCheckBox = null;
	private javax.swing.JCheckBox ivjChannel3EnableJCheckBox1 = null;
	private javax.swing.JButton ivjRecalculateJButton1 = null;
	private javax.swing.JButton ivjRecalculateJButton2 = null;
	private javax.swing.JButton ivjRecalculateJButton3 = null;

class IvjEventHandler implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == Series300ConfigPanel.this.getMinMaxModeButton()) 
				connEtoC2(e);
			if (e.getSource() == Series300ConfigPanel.this.getpeakModeButton()) 
				connEtoC3(e);
			if (e.getSource() == Series300ConfigPanel.this.getKY2WireButton()) 
				connEtoC4(e);
			if (e.getSource() == Series300ConfigPanel.this.getKYZ3WireButton()) 
				connEtoC5(e);
			if (e.getSource() == Series300ConfigPanel.this.getKY2WireButton2()) 
				connEtoC6(e);
			if (e.getSource() == Series300ConfigPanel.this.getKYZ3WireButton2()) 
				connEtoC7(e);
			if (e.getSource() == Series300ConfigPanel.this.getKYZ3WireButton3()) 
				connEtoC8(e);
			if (e.getSource() == Series300ConfigPanel.this.getKY2WireButton3()) 
				connEtoC9(e);
			if (e.getSource() == Series300ConfigPanel.this.getChannel2EnableJCheckBox()) 
				connEtoC13(e);
			if (e.getSource() == Series300ConfigPanel.this.getChannel3EnableJCheckBox1()) 
				connEtoC14(e);
			if (e.getSource() == Series300ConfigPanel.this.getChannel1EnableJCheckBox()) 
				connEtoC15(e);
			if (e.getSource() == Series300ConfigPanel.this.getRecalculateJButton1()) 
				connEtoC16(e);
			if (e.getSource() == Series300ConfigPanel.this.getRecalculateJButton2()) 
				connEtoC17(e);
			if (e.getSource() == Series300ConfigPanel.this.getRecalculateJButton3()) 
				connEtoC18(e);
			if (e.getSource() == Series300ConfigPanel.this.getEqualsJButton1()) 
				connEtoC19(e);
			if (e.getSource() == Series300ConfigPanel.this.getEqualsJButton2()) 
				connEtoC20(e);
			if (e.getSource() == Series300ConfigPanel.this.getEqualsJButton3()) 
				connEtoC21(e);
		};
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == Series300ConfigPanel.this.getNameTextField()) 
				connEtoC1(e);
			if (e.getSource() == Series300ConfigPanel.this.getKeTextField()) 
				connEtoC10(e);
			if (e.getSource() == Series300ConfigPanel.this.getKeTextField2()) 
				connEtoC11(e);
			if (e.getSource() == Series300ConfigPanel.this.getKeTextField3()) 
				connEtoC12(e);
		};
	};
	private javax.swing.JButton ivjEqualsJButton1 = null;
	private javax.swing.JButton ivjEqualsJButton2 = null;
	private javax.swing.JButton ivjEqualsJButton3 = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public Series300ConfigPanel() {
	super();
	initialize();
}
/**
 * Comment
 */
public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
	return;
}
/**
 * Comment
 */
public void channel1EnableJCheckBox_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	boolean isSelected = getChannel1EnableJCheckBox().isSelected();
	getKeTextField().setEnabled(isSelected);
	getMpTextField().setEnabled(isSelected);
	getKhTextField().setEnabled(isSelected);
	getEqualsJButton1().setEnabled(isSelected);
	getKY2WireButton().setEnabled(isSelected);
	getKYZ3WireButton().setEnabled(isSelected);
	getRecalculateJButton1().setEnabled(isSelected);
	return;
}
/**
 * Comment
 */
public void channel2EnableJCheckBox_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	boolean isSelected = getChannel2EnableJCheckBox().isSelected();
	getKeTextField2().setEnabled(isSelected);
	getMpTextField2().setEnabled(isSelected);
	getKhTextField2().setEnabled(isSelected);
	getEqualsJButton2().setEnabled(isSelected);
	getKY2WireButton2().setEnabled(isSelected);
	getKYZ3WireButton2().setEnabled(isSelected);
	getRecalculateJButton2().setEnabled(isSelected);
	return;
}
/**
 * Comment
 */
public void channel3EnableJCheckBox1_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	boolean isSelected = getChannel3EnableJCheckBox1().isSelected();
	getKeTextField3().setEnabled(isSelected);
	getMpTextField3().setEnabled(isSelected);
	getKhTextField3().setEnabled(isSelected);
	getEqualsJButton3().setEnabled(isSelected);
	getKY2WireButton3().setEnabled(isSelected);
	getKYZ3WireButton3().setEnabled(isSelected);
	getRecalculateJButton3().setEnabled(isSelected);
	return;
}
/**
 * connEtoC1:  (NameTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> Series300ConfigPanel.nameTextField_CaretUpdate(Ljavax.swing.event.CaretEvent;)V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.nameTextField_CaretUpdate(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC10:  (KeTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> Series300ConfigPanel.keTextField_CaretUpdate(Ljavax.swing.event.CaretEvent;)V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC10(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.keTextField_CaretUpdate(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC11:  (KeTextField2.caret.caretUpdate(javax.swing.event.CaretEvent) --> Series300ConfigPanel.keTextField2_CaretUpdate(Ljavax.swing.event.CaretEvent;)V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC11(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.keTextField2_CaretUpdate(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC12:  (KeTextField3.caret.caretUpdate(javax.swing.event.CaretEvent) --> Series300ConfigPanel.keTextField3_CaretUpdate(Ljavax.swing.event.CaretEvent;)V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC12(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.keTextField3_CaretUpdate(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC13:  (Channel2EnableJCheckBox.action.actionPerformed(java.awt.event.ActionEvent) --> Series300ConfigPanel.channel2EnableJCheckBox_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC13(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.channel2EnableJCheckBox_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC14:  (Channel3EnableJCheckBox1.action.actionPerformed(java.awt.event.ActionEvent) --> Series300ConfigPanel.channel3EnableJCheckBox1_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC14(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.channel3EnableJCheckBox1_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC15:  (Channel1EnableJCheckBox.action.actionPerformed(java.awt.event.ActionEvent) --> Series300ConfigPanel.channel1EnableJCheckBox_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC15(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.channel1EnableJCheckBox_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC16:  (RecalculateJButton1.action.actionPerformed(java.awt.event.ActionEvent) --> Series300ConfigPanel.recalculateJButton1_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC16(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.recalculateJButton1_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC17:  (RecalculateJButton2.action.actionPerformed(java.awt.event.ActionEvent) --> Series300ConfigPanel.recalculateJButton2_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC17(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.recalculateJButton2_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC18:  (RecalculateJButton3.action.actionPerformed(java.awt.event.ActionEvent) --> Series300ConfigPanel.recalculateJButton3_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC18(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.recalculateJButton3_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC19:  (EqualsJButton1.action.actionPerformed(java.awt.event.ActionEvent) --> Series300ConfigPanel.equalsJButton1_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC19(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.equalsJButton1_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (MinMaxModeButton.action.actionPerformed(java.awt.event.ActionEvent) --> Series300ConfigPanel.minMaxModeButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.minMaxModeButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC20:  (EqualsJButton2.action.actionPerformed(java.awt.event.ActionEvent) --> Series300ConfigPanel.equalsJButton2_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC20(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.equalsJButton2_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC21:  (EqualsJButton3.action.actionPerformed(java.awt.event.ActionEvent) --> Series300ConfigPanel.equalsJButton3_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC21(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.equalsJButton3_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (peakModeButton.action.actionPerformed(java.awt.event.ActionEvent) --> Series300ConfigPanel.peakModeButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.peakModeButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (KY2WireButton.action.actionPerformed(java.awt.event.ActionEvent) --> Series300ConfigPanel.kY2WireButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.kY2WireButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (KYZ3WireButton.action.actionPerformed(java.awt.event.ActionEvent) --> Series300ConfigPanel.kYZ3WireButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.kYZ3WireButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC6:  (KY2WireButton2.action.actionPerformed(java.awt.event.ActionEvent) --> Series300ConfigPanel.kY2WireButton2_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.kY2WireButton2_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC7:  (KYZ3WireButton2.action.actionPerformed(java.awt.event.ActionEvent) --> Series300ConfigPanel.kYZ3WireButton2_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.kYZ3WireButton2_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC8:  (KYZ3WireButton3.action.actionPerformed(java.awt.event.ActionEvent) --> Series300ConfigPanel.kYZ3WireButton3_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.kYZ3WireButton3_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC9:  (KY2WireButton3.action.actionPerformed(java.awt.event.ActionEvent) --> Series300ConfigPanel.kY2WireButton3_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.kY2WireButton3_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Comment
 */
public void equalsJButton1_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	
	Double mpValue = new Double(getMpTextField().getText());
	Double khValue = new Double(getKhTextField().getText());
	String result = new Double(mpValue.doubleValue() * khValue.doubleValue()).toString();
	double resultDouble = new Double(result).doubleValue();
    if(resultDouble <10.0) {
    	if(result.substring(result.indexOf(".") + 1).length() > 2)
    	{
    		Integer temp = new Integer(result.substring(result.indexOf(".") + 3, result.indexOf(".") + 4));
    		Integer use = new Integer(result.substring(result.indexOf(".") + 1, result.indexOf(".") + 3));
    		if(temp.intValue() >= 5)
    		{
    			use = new Integer(use.intValue() + 1);
    			getKeTextField().setText(result.substring(0, result.indexOf(".") + 1) + use.toString());
    		}
    		else
    			getKeTextField().setText(result.substring(0, result.indexOf(".") + 1) + use.toString());
    	}else {
            getKeTextField().setText(result);
        }
    
    	getMpTextField().setVisible(false);
    	getMpLabel().setVisible(false);
    	getKhLabel().setVisible(false);
    	getKhTextField().setVisible(false);
    	getTimesLabel().setVisible(false);
    	getEqualsJButton1().setVisible(false);
    	getRecalculateJButton1().setVisible(true);
    	fireInputUpdate();
    }else {
        javax.swing.JOptionPane.showMessageDialog( this, 
                                                "The final value must be less than 10.0 for a 300 series MCT.", 
                                                "Channel 1 Input Error", 
                                                javax.swing.JOptionPane.WARNING_MESSAGE );
        getKeTextField().setText("");
    }
	return;
}
/**
 * Comment
 */
public void equalsJButton2_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	
	Double mpValue = new Double(getMpTextField2().getText());
	Double khValue = new Double(getKhTextField2().getText());
	String result = new Double(mpValue.doubleValue() * khValue.doubleValue()).toString();
    double resultDouble = new Double(result).doubleValue();
    if(resultDouble <10.0) {
    	if(result.substring(result.indexOf(".") + 1).length() > 2)
    	{
    		Integer temp = new Integer(result.substring(result.indexOf(".") + 3, result.indexOf(".") + 4));
    		Integer use = new Integer(result.substring(result.indexOf(".") + 1, result.indexOf(".") + 3));
    		if(temp.intValue() >= 5)
    		{
    			use = new Integer(use.intValue() + 1);
    			getKeTextField2().setText(result.substring(0, result.indexOf(".") + 1) + use.toString());
    		}
    		else
    			getKeTextField2().setText(result.substring(0, result.indexOf(".") + 1) + use.toString());
        }else {
            getKeTextField2().setText(result);
        }
    
    	getMpTextField2().setVisible(false);
    	getMpLabel2().setVisible(false);
    	getKhLabel2().setVisible(false);
    	getKhTextField2().setVisible(false);
    	getTimesLabel2().setVisible(false);
    	getEqualsJButton2().setVisible(false);
    	getRecalculateJButton2().setVisible(true);
    	fireInputUpdate();
    }else {
        javax.swing.JOptionPane.showMessageDialog( this, 
                                                "The final value must be less than 10.0 for a 300 series MCT.", 
                                                "Channel 2 Input Error", 
                                                javax.swing.JOptionPane.WARNING_MESSAGE );
        getKeTextField2().setText("");
    }
	return;
}
/**
 * Comment
 */
public void equalsJButton3_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	Double mpValue = new Double(getMpTextField3().getText());
	Double khValue = new Double(getKhTextField3().getText());
	String result = new Double(mpValue.doubleValue() * khValue.doubleValue()).toString();
    double resultDouble = new Double(result).doubleValue();
    if(resultDouble <10.0) {
    	if(result.substring(result.indexOf(".") + 1).length() > 2)
    	{
    		Integer temp = new Integer(result.substring(result.indexOf(".") + 3, result.indexOf(".") + 4));
    		Integer use = new Integer(result.substring(result.indexOf(".") + 1, result.indexOf(".") + 3));
    		if(temp.intValue() >= 5)
    		{
    			use = new Integer(use.intValue() + 1);
    			getKeTextField3().setText(result.substring(0, result.indexOf(".") + 1) + use.toString());
    		}
    		else
    			getKeTextField3().setText(result.substring(0, result.indexOf(".") + 1) + use.toString());
    	}else {
            getKeTextField3().setText(result);
        } 
    	
    	getMpTextField3().setVisible(false);
    	getMpLabel3().setVisible(false);
    	getKhLabel3().setVisible(false);
    	getKhTextField3().setVisible(false);
    	getTimesLabel3().setVisible(false);
    	getEqualsJButton3().setVisible(false);
    	getRecalculateJButton3().setVisible(true);
    	fireInputUpdate();
    }else {
        javax.swing.JOptionPane.showMessageDialog( this, 
                                                "The final value must be less than 10.0 for a 300 series MCT.", 
                                                "Channel 3 Input Error", 
                                                javax.swing.JOptionPane.WARNING_MESSAGE );
        getKeTextField3().setText("");
    }
	return;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G21F3F9B0GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E16DFD8FD8D45735AFEDCDEAEEECE2921290E392E2CCEB9212E0CA921B1054A676DD521806266606575A1634B671B6A625AD6F165EEB5B6308028A0A0A713F2222C2C47E2BC8C081D18151209285A41101B9B0A3434CB8B348E8B05E357F2CFD4E1CB94370C76F773D6FBDBF97676C3557DE7B3756DEE71F3377597B0C143EB9F441BCC33AA44DDBA071FF7B66C9D272631274683B9DBB11171C51EC10D47F
	36GAD170CE509F0DC83F9A52FB59B52241F25A6C3BA87522DBFECB62C067315523F25E6BE84A7FE6A197B20A4559DFFF35E40757CFEB75613C4530B4CA9F0DC8A148F3CC620508CD27F96739A4E77C0FAA9BFA7B48F3436EDA1B25B7B5628783338DCB16455GB5ECA5B2E5668DBCFF9270268225GBFE3AB13AD0163A20A6E265292AE57FA47A4A97EA76B8D2924AE15FE2490DEB6129FB324C728A366CB2FDC2B02E3D9C3F757018EFBCFEE0751E6335B5CF207B95754AE1BACEE3BB3374FF24568B4651A64CB
	168E3934341A9ECF98FB64CA1349A93BDC8DC61BEC853FEFA3580F75A5D33D12948FF5CD213CD77B083F9CF0BE9AA8F49B627FD9EA9947975CBDC90AFDE429DF7CF337F15C198A7E58101B97C4E7927DB2ED8FD22E8D52A1C04C36703A0AC15A56E137F5DA3ACACBCF4BCE0B6CAA4A4FBFC49954B2E65713E634ABFDBB5A753FE4D2EFBD24CDC07D027F1DCE428F06F49850346DE8EF79CE3437692B672504768306E5FE6C1D391D591938C331B7E4763805EBE97C756711982A00BC931087E8BCD0A450CC2075B4
	6EF3F2BF01E3E53B7B103D2747EE2BF4B82A2CA667F13BC93642F9E9A964F05EA939436EB4C9920B6D063E9C9DBCC871BB3C631A90336D2C043198F66AA9496DAE26ED2035AD626D0D06641DCC47061D0AED63F7923F7D51C37CD68667245CAEA046CF3DCC7831AABEEB7757E8DC11F4C59678A09B7D5C34943154A7CE10528AAA68F52B4592724A9EC342A78CC35AAE858B737119826263B260B781B900C683CD820A5A05BE3E14DB9448473590EFCE1B51FA3A375DE1F43A2BFAADD6136C643E6EB68F64EBBDE2
	65AE8F399C734B24A2F59B4D7C7EC1DD3F4C1C14E918457DB1D76597D677B3FE6B5E10639B9BCBC59B1652EBA2BF073561FC412F267C8A951F356DF4FAED11F48364C5GB1799FF4917E1A5D5A98F90772BD3B35B1F2D7A746C88C649567B59BD87B469712BEF6ECBEDC23C05300E6824D85DAD350EC48A940B67ED1EE5EA02F2386D57B3232B765FD8CC75963F69AEBEDAE375156A11FB1BAAD46F62B6C12384CEDF9442F6DDE656E2257F78754F1D43649CE235BE23701F668F1A118DCCCE6FC5EF2A26314E9EE
	2325C7068AFA9C926A1D1837920ED6234B7D3943E4F44B0A844B8F4BA3B1F149F886B40282B86FAFA07E6A227DF7BD1C1B8B50D7DB0A89EFAA24E304DFF6662F0063B188EE598A7784CB8701E1F4DE46987DBF1EC6B84E817931054D063222E643AC20B1455086FB4057C023774279BEB8876AAFE9B6EC588FFD981027946E671FB69B6201A615413D3C9CF09D00BCA073C13C46BFEA42D82ECEFDD24AF97B00EEAC93CA2FE0F119D3210D6515178946C6603B010201A200E6G458325D782BE20C6A0B7D0B0D084
	508C20F82074AA4887EA84F2838583C5G4D28426704B77B9A66B18C44AEB5A6E22F96A7710B5E7503F67B3B3609FF711C780BDE0B7B7565094FFD6EB57B949EE9AFFF754CD461A3ED234EA36DD37229FED95266017AA7524664C85A1E9CC92CE09E091DE295C6665B9D9E746DFA0D2E55BA7AD37D541B4D7B86FF3896284E377163DABF324BF5F82BD467738728E7196AFC0BA60FBC84EF76D3EEC9G3F925A4E0F9BA565DE207665CAD51ADD6F376FA1FDE6BC44F15A21E6C37EA13C665B73B3A45A97D699DDB2
	76FE1FB9216312CDCA8F40E44F97901B64CB324DDDADBBE41B89CEDC3C6F69ACA098F83F58E917304F32D2BE39CF9E020E4BA5784E02650C8FBA543DAC66DF2179C6375BE9E96FF54B27E5375BE26BD24AFF13E2F1F95D02DD5C56BB7692DBEB4046B6A00730F5C9A1718B76EF75F6FBF72F2311745C3C1FFC0DBE8F0E8279710770FA2F68A4630429A2DDC753CA7EDBF21326ED08EC20769BAC31BABE7029E320389BDE9DBF6CA27DA65E2F97F07EA29E0F29BC1E177358A25740EA9EDB6BF99CEE66F13E0D47A7
	0971EC217FD7D4FFAC78A6399A628F2889A888E892D0B4D082D046E198B381B900C20026824D86CABB82FD91D09B50E820F02098A069A83643FD1D246F3891782F171F7FEFA4665BB86A5B6B6D57017A687F970875C97FA8745F27AEA8526FD317946977294B0B866EB35796697519ACAF2308342D3D4F266A0337971167C198C75BE4FAA390C5A4B6967F281158E131D95CE7AD26AE59FD5AF2C506E77B2324AF7D1ABEF3E641F959D1FC3E6B5B064FF719D79E13261EDF2BBB1E8CF9344490FC147521130EAA4F
	F78C530EE252374BD639879CF5485EEBF37331B1FD1E65FC57FC4A3B12F7B7A91BC566B59A410DC7AC3255145381779CF7AE7D4B9E2333AE98AF99BD392EBEF0D6AEED095CC3A477B0B94D4E583D7BC0066E9C02CD2D767CA115427A01B4FE1AFDA6BBDFE25753CA69F70ED00A713845F65C68A1E3102ADE375BEE4BB1D2176732C3036C6C34BBFBE493451AEB6CF3F33515B4BB90529E0D7273159A2D757E559238858CEB531671942BC3B6F65FA0245E2A070A331061BC1EBD0E62BC56DCF856623C91B03B551A
	070A32885BFED25DF80E3325680681D52B9EAA52E29E2373467B7A337046B934F028B877B01C2BF6BC28634F6B8D54CB77D0116E45367F3BCD8FEB518D44DAB4D42C7B906B38F5BA6DFF3D21960EGE989433945F58743F9A3FA7B6E6176757BC5DFFFF2F93096DF618D81D8B8B40485798C5F376CBE780AEE883E22A16263771F5FFF7DB445F748EC04C792EB61E19B993C559DB24B9D5DD5F64FF58D488E7DBA06FC67AC64ED7F49A71355580BB4FA8BEE84F81FCA060C3EC844EEC91A176F8B7E87FC2F2DE348
	680BB976FB1A3EC7311F12BB0C560EDEAB84DD9D6BE92E2F5B1D3E7A070CF98F477C54F2FF182F6F7D4C9773106FE985FBB966AB157EB0DF5F1B05AF66A15FB18A76F14C9F55BDC0B19F3E58EB343AEEC8D848DE2A070C34C4DC0321DBF530DE5FF07042BA74C8D06EF58F6D5641FAFD43408B6B10A320100C4158FC6134FDE4CC9985E34D19C071C7F13EF0D2B1494779C2BEA9FA48EE353261B064B5551031EE5365F45578B525062D9FD8AA72C785488F8F10BFB3C0FE721501730B83643785489F93A0FFCA00
	7CD98172533E9AB83FACC03EB9C0FEF0007C298172678448DF53BFF0FEF500FCC7007C718172237C6633B83EFBCF9A9CDDF03D48368E195CD30F4917711D631E951A3CB3C6EB2F72AEF3629EB24FE7D54D15B37E3DFB7567E19E58CB66473B057E0777AE1448BBC926132F61B0921D8E272547683CCCA67F79BA1AD833FA9DCDA858C1565C4442B19E0875ABD11F91DB757A0328DA22E32BD8E7138269CC206ADA26A728167904497DDE482557B59BF2006A6B185C18BA355CE396140B81FE1CCAAE6D18DA6E0B8B
	A8578AFC77B1C5AE58CBEEF9B74A4D84FE3CCAAE3DDEAD77E3A15786FCCF3DA29762A5F7590AF23300BFD7A5376138DAEEF78F4A19019FF4DC118B7512FBD7484D81FE6289C5AE7304DA6EEE9B4AB900BF7A046267C9DEF235F6218F78091FA9F2191F29657EAE642C409F25121B68A5B759A17481BF318174B4B0B9D303DA2E77A24ACD81FE34CAAE4ECBAE5B09F255A71B8D2DA795397E13EA39DF8939E8604FD649A51FD24B5D61C239A660BBCEA9F2E33D648E3BD1AE967889AA39B52755F2FF93F2E6608781
	051E665705175CCB3DA81753883EEBD464CC0DEA390B17D0AE9C78D1AA3959DC2E78F35A3EE24DD71E6005D15E9F13A46901C4DCFFF2BCCD3DDE4D64AA501D53A564791C699F7339F6DD4E5FCA085EB060C7FE0E775E757B546F6AAADDAEFB0705AE0B607D4FCF762967FB996F4DFD0B6158E9F13A5C35B6136C41BE6C6DFD84ABD9B741593C9FFAE2D739643B8E64B0DDC5062FC28F7551A706C67BA13BC92E01EE7010188EBFE3117BA4744355A36AF5EF114BF698167871C3B437BFCE650756596396B2CC7715
	12B1175E7A368E8AB2372B57125B6B3610859AC63731566668F53345FAECCDDB1DA5195B17FF86572E2D33123E5A8C69A02090A0360E6698DDE3C7E44301B79B085DCF7E38DFFDBF49B84B5E07561C45BE7899B30909A1CE8CF01F972FAE056370274E515F93D6D6F0FC531F1AA015DDCB52DDF7B7FA59F2C36BD96667D1E795FF075DFA4B4229C04C8FCBAD6807E810C9EEC2BF18B354FE28E8E2FE30B6219FFAAD05F09C7AA3AD465F9BE58761B84249F93EA66AADE1C390601BA2ECF8B8C74F0638A6DCCF7953
	BAF28D0CE85ADD42F5D9B7DFC08C094D4D0632E644F00FC30B418DF9A14D086139DD645A99491CBA2B3F349B6B8F835D33C47D4BB769553F21856B1FF6261C56BF0229F261030F8406FC50EFEEC18C4B16E9B104C25EB401A1AE3BDC6B03218E3F840689D64490837A534EA10648ACBD8CB5679043686C83BEFE98BA880661CC8FE2E8857D418243AFFD62F1BA6445898C4F6BE0986A588EB1AC3609F8EC05F8ECC58CDF676BE1F0370A36F07B344550A1F08CAF8B8C23C17FD401E1DE37360FA5B119A0B0FCFFBB
	392F8EF75219556D34E35DA9DF40B34197A2964FEB6BA6F1507F8556FD5A335F3B6E214DA78B5BF7B8307E3120BBCA547FEB2336FE62FF69CBD1FF37267E214D970B7AFFA16ACF875D75DFE27DEF1854750F7A127509E1DFE23FDEF9F1E47D3A756291F2FF3CAEB348E84B039751162900B3C158323ECA4F167C73684B0225C795ACA31D9066D8CEB991CBB55453FF9E31B4D66AE10990D82C1BD5FE99616CAE7AE5218B31C4C1BDD29BE2F1FA7537A5ED6C7AEAEBC3ACEFEDA4FBA4C6BEE30BB8DE90B85CD0C718
	4091D12945C12E35D9824781BFB8065B6F5A5C08A3816A48B7A20E7496AD8EF24D190D08A3518F0E61763DDBFB91C7BF5491A1F07CB4D72FDD6698715A3B25F7E4575E6B4D246F9E51142B68BF5E93B6C86D4D0612F634E1F605DE1BBA5A51172F677BE098763DFC5CA544B08A74C78A8C9F6B36E73C0221588743B06EE1FC9FC821FA0CB7B5F63B5FB1DEF287DF2B5C219D637D2A140CCBCA00DF5F01E33C069232DE8947F827E0D4E534F5D9E5C92C0D226FF53B654BBCED58CF6A25F3C9924E5D2EABA3FEE883
	1D7DC041C0E1C053C59D376C8F381F6330E7007D9C1FD934FBA366F4A86BBBA5136FDACF36F761957A8C16EEE2FB97D6897E5B14DF236233F64E20FC126E073CAC9957B51E74591BB1C3762EB3CE13E6654E7B145B24C927F7FA17E3F133BD0B4C8F5071276AB6DBDADA273CD227E34A9D34BDC9DDB91D382740D1CE4A1AA09D84948294819483948F34268B5BA4E63F5E385B3BCD7C8D3A15F5424C4EF41F76991CFF367A14537A2724CB4FBF9FD7ED52774FA92349E2E7979A6E1BFBA88347628DDD581EB14344
	7BDEC03C23B5F89906189CEC27D001E14C0561E19867D3CE0BA1C603014D6B4E29D04F6B46F5316702F4B344B99043790683FDD5E196EB2F3B07077304CFB9ADCE0FD94FD7B51B50D7E3850678E1E2B0D883E108B26B79AAA4C94FD731A09BAFFCB5BD9DFD15E2C11C13FC6A9B9C4E6782622C3668796AAFD9682BD6012135E7F898AE781453E2883668792AA9D54FD7E1A0BB55023EDA319BF146881CE93661615C64D3CE0BB36D021E2F1E4D47764A3A0098268D9343DB81B13469E2A8694679DC0F4060318F8F
	434DBE65349842F5B19CB90F982284069C4770B0147A1453E200813C8E06D56D08E1CDB7E2089DA6060F82E2E850E0604F7CE748FDD0679E02E3A10EB5D1A6D849BC32D9E01DF8F1F8D81F71A927459A2A6B2FD3D508E10A4050649C9E06CE1FF2DA8CF13A98EE2DA49848DE03E4ABE2C8F18D8F43BA1FF2DA8CD62BDE1BADBB3DD63F4D70164F31BE368C310E92D8278E936BCF83E21DED5573578742DFD28FE2F03B0707611BBE653498CCBDFA980E35A006FE01A12BF7F898767A1453E208554530A08F7B7E84
	01E14EB0B17CE7C08C79B6BD8C1BAB91C30D8DB104DE9A9E0627FC4AE9B18469E2B85E0298C2EC782C9EFE007022A19DEB436772BD25243DF0BC56E037585C920E45FEDBCA5E635B1D16AE0B8DF96FD3DE075DC9B70DB05E56B27F7BD9324B7477336415696FE759D3B6709E4172B2FF7BDD58FE3ECE7AAEAC8E6CCB3443781072EE39C07725C03A420E6D91E54DA2F1CCE400C200A200220166GA5BB1A8D19C0B5C0D6871679A5ADB3564156984471B584E4AE034C851031B8F9171C485FBD737D2D56D0D55B90
	3DF8CE6AD337DB3FA62DC7080F1C2B71E965C28621CBDC4B42A7A73D302A7732AE5214D9B0883D38D7EC29643DD7CCAB17B4C81C0448BDFCA3BD3E7671B29AA7AE176C2283C772E614673D6FDEA5696CE1A2DB02A4DC2BB65A3C084B9CF65B8F95086BEDD7297AFA8B5778F61AA69D63406B68CA397FEB609B657A5740374B09FFDC76DEE7072C0D735B4A5708BCE5A4DC6B16FB0899FCBD8EAD6FB6BA3BE41F6B64C1DA1E6579AB7F3DF2FF57927351B10B5AC70542C793B3743E9DB1077BC63A689BFBCC5F328B
	EAFDC5C25F2FF2083EF4CD39AC1EAE732B6FD5AFFD45925E377F12CD3F7961FB5FD6FD87A633D20C53AF62BB73640DE4BEE6B424C3896F22586F6B734CC72FC50EEB16DFFCFD5DEAFCFB043D47B791FD891AF2A9CEBEFF667427EF17D52DEF2F04FDFDF4925AD26CC4DB3E3F1358E20574A82071CE3465E769FA36C470BA237C567D9B2F3A7789DB9232083E191AF2F1BC1D6872276F2E9E353E92E14B58D434E50D8BEDF9EB9731258152E620A0975A32FAB729BBC4D3C798CFCF75DB77D11B3A6E7D4216977275
	FCB3036B196DD75FFF59962B749564233EF21FF984B54D752B6FE93B173E8254F76C7CC07A525C7E74F5B83C74950A6762760174E57A55377A22173EA25437C8D66BAB61656B79310D9F5DF09C4D4F193EDFFA6BABC6FDE72B547A4673B2617CB80D9FE360B847CB5F6DCEAFFDFBD05F58CA6D1EFA5ADF75FA5B192ECAE77173B20DCC83CF1BD4FCD677A117D75DFB45F35CB23D3ABD3C7C98B806767A7ABBC20797853C199ABE2B7B2F5EF56F93FB39F56D0E63BA92AFB59B56DC722DA7C707D7813C46CBFAF5BF
	61762A3BC4FC072AC52FEEB357518F4731BA75CC5461CD815EF45D3A4F77FA553D9F6BDE1C27975F31DCC7BCBFA677E95BF05BA5353EC2F1BDE7D66A695B404B6773E3350F3EF73C75096B79D40B1E3EA6DE5E4A0FC1AAFD6C3E7D6C81729E41A352BF20694B223E2B96720CEEB5DE367742437C6CBE3A9E5A221EB78B86DEA4D0B4574D6E1B137B48B3DB27535883374B538E39C31A66A1E5C3AFE059FCC8D7831900C681CD7460BB3C7BF2C91FCA1EAD6A0D6D64B3F29C4B645CD44E97AF93C45E022DC42777BA
	A44C5B30142CDF566E10E6F9FF226B3E0E77DA5D960755A2BB59DAB816976DCA734D63FB049B576A6495313C472A48B78C7C6CF4623AC7ADD249E85F71F319836DEB94991FF76F6CBD5E2A9E2F355B17797B7B4BB8DE29226FD92AA15DA8F8499E9C4318856F881D671F8269690277A2FD7F908B69B8415B30854B4E953CF8DA367ECA33216D8A724AF7E3D93760A567E15920ABF85F4D76398E222FA8B1BA6B0A6F354B5E03C552672B04AB6CBD58EA410F267C7C2F94BEDF23C779A4ED063C4CFE44746F743DC1
	AC2463842F6E2230D170CCF4EE3F7EAA58F895F9CFEE4232EE41DB31D958F895EDFCBB4947462BAA9B2F7A3331AB0B5AF8D5EB63D7141F7F3556469F6C94B6C2DE66B544B41B4E7747C2BACE70EAD7899B85EFD9B635B188EC8CC2DED3B616F58B5ECB594246A034716ED49F9B03D4B686793371669CEAE3105646F1141F7F8D2D0DB13B040D101779CD44144B628D52710257C7F9495F02FB523710B7F3B7E24F943C1F65231CDB708A8B040D0257580D3CE841EBBF0F3C0CD148ABE8C71EC370B268DC6CD4C847
	881E137266C0FA2E603DA3E359641BC49DF4FC5184E90F60BDCBF9E3A09DAEF8B6BA57BA8D52B385EFAA6545C1DA3A99F95B8FE19DA90217CA6BEDA5E941EB233C31100E903CE7E83DD1100E913C15340ED910CE7EB6723AA8EF83247385AF1C722AA15DAAF8860145A1F8E693759524E385AFFA99569BAFF8FB5288CF9A5DECC89F0D3CEB141785698A41DB381F709AA16D903C6DA5D86F28515EBE188169B9023702E216EE0173DBC4DCB15BA01DAFF8BF2A42FA2B852F5C0475B6895E68C62C57AAF8C9225EC8
	41F352B937E8C8478A5EBB14B78752523FA0EF4BB92CB7DD705E580B75E6895E371B315EB241BB36976B9D236823FDF4A82443856FE651EFC78ADEA19DA7265C8ABE3895F91F52582806F4236005D6E259B6417BF38A6105C3FA0660D928ED33A11DA0F8B3443510B886F99FD5D05BA06D99A3562835F92DB9A08BD2CACB0FBAAD26AAE3D7BDFDF2E163419768FC9E69CF460C417160EC3A6EF99A24E782450F41EF4DF8F660FA7C306FCE00BE71206EF7E8E39BF798D23E431F5978112E8F1FBDEEA11F9B6846BA
	2D10BF71BB741D08575ACDF22FFF5DA0C939F0E479233C72493C8A797611127F1CD7BE417B12D77959DE7944C79BF9F9E65B6B5B5136392F07CB56E7F77B7D466E9CEED35AED6CD87346BAA93D6ADF78DE033FF47A199B210FED4C7E7A9EB1FFFD9BBE5FDD6BA1FB3CBA6DB677545B08CCB7FDC6890273B1C0A53753F9EFCF358E9E35CD73D547C4A83386E89567654079D555773CD6CADF64908C9DF6EBEF0F4DA531FA5D7439054CE7875D0E589A69F71D5D70FCB976F6725D67943264D0E2180A368C04A9C905
	073E9FD4E1A2B8E65C4E667794CCCB683D48A5137D203209E15A3A9B5FAF4602BC43D0FDD44130B9EFA08C681385FEB0445D4E6E27045875D4C12F4F39C477D83C46BA2A703AAB9E0B7777D7B6127B7BA8C80782C5824DG1A8314F8875481148FD48D54846439834B7D9BAD974F77348583BF628E365FEB869C6301526F04FA6EE4FB22487EA43EAEE7E33846B6D2A7516F4F7641D0A247C730EB73728368DE234AA7F6939BC3D5FAC689DF9D72412E7D2662F008DC23249D35736758C78CD4D6BD7FCE46E52BA5
	6FF9F70C59155234CD5E715FD523441E7162C03137046BD864A7769A210D4B3D627F2BCD3E71FF67A60C7F36BBB17E1F2ED5B0E4EF8E947F83E1F0004EEC2F781FC36FE37D242EBB45F370A20C7F08BB31CDFB685CE462DD701C87D4865488E4859A859482948EB48DA886A861AEAC778DDAEEAE5F03199E8C319E4C76B1B642518D948C94914C767A11FDF77CFB0ABB35B12426C60E01540B3CF0DE2FD73C73F48CBFE65CB53C6B474A4B0D9A207CB4E173C783E2A7ED1324094965126FB7DA719B15781EE7B06F
	097C45A5DEBF7EE4D60A1834E7FB477F9F6A1458EBDBB512781F912C0D7F9D593E71DF1EAD7A7FE00C7F54FA85430C6C1144FFDC30B67E43689C79DC60275D0D717F66D2517F5FAD567C5177AA23A09D8A9489B483E88ED062BDD087D0BED0B5D09310679E712E1A168B62FB0103019FF18F5B1FBB830E71C06977C2BD7732BD2CD1A27EFF3DCB9DC324AE221354BF58F6A7F888D6020D242B6F71AD5B2462C52AF44F99E4BD099A1D8CFBEA0E9A7BA29E5BC379666AF29E6FF80D685DBBD6E96A5546FBA052DE8F
	ACA68F6BBC13340AE71246FB71B969B64DF3D27EF1A5C67F32FBA4B1EA3AD709D126BB6404223B36E0A4572063DE659AE47A3A3B8752076D00187B6F75FE4E9C8B69AF3C74DDBEBF94FD63B57A42FD7455348FC5DF24C6DF34CA9FEB37FDB219BFB6DAADDDB6327339C9624F421F513156CC100F93EDFC0B6C5D46511FA96D70119C280D318D966834C3C6C833E10B972E4B15A4464DF627658A0C8B0C56CAC448715DA55E316604A03E69159FFB15A133428DF61705FDDC0515FBDF14EB9265EE2A527741837439
	F79448058859F72A3CFD7017864587E5078279A0D555B6DA9FC47978A0D39E588787E434E5064057A78776411D221CF49F16FBFF1937DDB927A40163132A40BA8FD7097690BA3F2E726DBB6E2E423E23C9487D2B9F7F7F064A1AC12E7FBE9C872E8D68632481FC9CF61F564757CC837B78BE935AB565BE3C7FFEF6DA71CDFD9A296F1245E5A15FB2E1E5BA52F00CBFCB58F8CF9A690BFA0C9ECBCFEF8F7D30AD13FD0A4AA600DC62B8145D381F440943A9F74ACE27ECE21F416572FB695CD5BE48160DC31F14158C
	E68C186A47A766F14A9890630EE23558B4D84BE85C38C1BEC8E04D56DC8F4EC645B737D5797A6651AA74CD9868E03CBFD05ED4C8CF973CCE6A7784C8A75E0FF6BE54B892BB736FD76CE4F564D3FB4A00DFF33F6863DA3C6D897DDC31672396DFFB961CC3FB5C77A376BA4A9B8D69E0417B8FBA37B695525305BD8FB60F441E849FFB5C340E4471F0BE9E6DA949733667D5328D0B77D78F8CB8F710AA8DBCFFD186F510F9243F7E731F7FE4F555A6856EA7CCE2BDC90D40F82A529B63FBE7950C4FD70E84235B8763
	22544098B345BA11A001B1C89397471B950C9F8CF86F8B04B14C876302C15C47B244FDEC4AF8B1C67459539308583B15D67A1D05D96315F8E2FC197263D4FC7ECD82BAC7CA52F3A12F6D81DC831A5FC2667F26C1BA96A881A86DC19887G55BF08EB598E9CA45FC99BF02FD6235DD1AFDF122D2AA51A7C5D6000EBDE74084DEF7EA88967B7A3DEF8DC1AF27307737CFDCB256DC17EDE1C9FE3DFDFC7794C3ED74F917B42A1EFBAD09CD072C34D068DC0458F21FDB79F4CE176114F3ED43A5D468EB37D228C9AC7BE
	722230490BF03EBE52EB4FCA4DC32C7ED6FEF4BC64BB7EE0F13845A8C9E22DD09F799E3ECD6E4B907BB00FC0GE5EDF599EDAE176C34F4EE4D49484F49A8A84E963E896DD36F45ACD93C4B6FDE4CD19CC388BFC67CE39D7D96BF39FF52F7A274DF5CC1DF9EE29DCBDE4F73FA5FCD41B6597044A3D275BB9F4E5B66275EE8DEDFC628B21F1EBC7E66854C66DBF3480F7111F5C7CC6F1B675476240DDBA5E2D72B37096BB32974B21C87BB951F6EE7639AE72F4C725A3B143C33153EEBA1EC155A3590A120BF0C5741
	7A0BABD503F9D6466718E9D0B6A6147597A4BDA7D4FB3FBE391214737E72B366DDD8C957E02B3FBDA14A9DB1136B6038C3D9BD417B3AF642BFE656727FFC1172E5AD7FC1EB925543D6E770B58FDF13E7BA7A0BD2FC5186639B1A1228EE35ECC3816549EA5E4AB292C3BADFD845750FBB094D5EDF59446F57C5FC2E7BAD0CD05C6B1CD027171F72B0667F6E30F68D85494F90790FAF574BAF96793B0B7572EBC47E6AEDFA792D8F637365CDE6A5469E5B649DFF13ACCA5E7CE45F78DB172C0DBF87689D7530BA7EF6
	AC8A94FFCBD471B79E4A06BF2C445F34073571775B454BFC628F3FD31C33E229B67E8A315C584C946F7863EBE966EFCA710EBF4E1FF0BA45BB7EB87F45CB49CA7CF1DECF7F922F78C3BCB75379E49EFFDC76EE7A4E19479F673DEE570FBF54D35B6093FF0598FFC9A5FA5FC309966D3FFE15DE7EAC11EF2C55468749CF90799FF46B6527CC407CB70F6B65E7087C4D65FA794593B07EDE3F2044D8E336F77CFD542D643D1A6A9BFF3FC85546DF8D68ED1D200E3F77168EE61C0C7167063223A7A871B7FE02B67EF6
	AC720D3FA21EA7F578441F783D04BFEC52449FDF2F55552B09BF4EDF7205A67EB87F7EDED57CF11E6528F77CA19E67AED57CF15946052A78633C1AF57A7107FA7E3E52A77E0AB07E463569FD8FE712E87F3BCE6A65C70A7C54835A78A07951A2FF79B53D7CD9A27F437DFA7989A2BF28D5AFBF6591FC56F6E96E394BFBC84C3173E2F27B52FD6EE079E76D8367DFF18C1CBF6922FEBE46CC27CC668F447395BC45BA6CB6722D0B7A43C79A4FD756579EBDF17C7009C60177F527D276714A4D3CACFEED68B479FB48
	CEFF78516D22BA386CBF86A56B9FE7C45550F0FE64D24AFEAE8F9E67E3C310ED2D9A3CECD8954600FF9B7FE308B6AE6FD54A061B860F65CA9AF9A66E04ABCD963AEAAEA93A7E2B046872FA6E2E068B5FC6FEB4B32B30G1E388B77E673F22F75A965568D215C9C0FF28DAC3B2268181CBB989DCCB6324A570E422F94DD05264163793CDFA9F735F17065BCD795BBF6DDD3F43CDEB1F8BBB61C73356304AAAEDE59BBF8BC9767A9657A1B87DFAE582058717CFCC5475F0620639709CA392F1286930F2C5C5F97A865
	BECD9DFC390CA4255C3EC175AFECEC5ACE1F8758F9376ABC5E22377E7EFA91FFF7F661C65641466C7BDB7C6F2F2BE8515FDFF724C5FF0FE9FD4B403F195658627FB7534E3528FFB30D4DB944A511FBE5865C9B4B005A005CC0239F417906FFEF59AA5179861D39064326AE999B946B1FFE4E3F2DBFBF97783708E71EA3633AF6BBBCF074907DE2BF6FB952DEEE3B03B279FE5EF344C7CECB17594DC4B97FC3BABFEA15BBA91B2F71246B42483A74090F60FB67674E905E94C8C7895E7A3378AEFA46A3B86F14CA57
	E7E4CEECB6D4CCC43FF49A496F811EB1DE707D8AAFB09B1CF6076CF4DFE6DC7A05F26E6F5A456459348D7474830582CD811A8EB48BA881A865D1F81686AA81EAGB281F90046G0D878A871A76A862F85B14BDG8E3EA811B612534876C7B39CAF1047C76E2F7572F6FFBA20156B609A7E2C7D025C61EEB4124989364EAE38926725E39E45350D2FD4126757346FB69B3200AA0086B7FF034FC66B68A7G78B781BE47F5FCCD5F45EF23649DC35ED841DBF78467A923846F5D65627D537710E7AAC6DE09601D58C6
	F8745B6E02D7110CF6CD7A9E5AE5CBA6F645C2BA81A8ED52D06DAADF2A58F52084ED481A04F51EDA053CB6418B29C53CA10237550A3CE841DBF882F9490FA16FCCB95AD5A1F852D2344B73985A153B1458958E69A820584706EAD75BB2452E1F35218D89224E1F1CC4DE7E63482BBE00F84D02D7F38DF9210217D90A3C9841FB3E1570C81024BD01711F10643F3F79E7D2607EA61936378C7DC463E5875FDB1CCA67BD6C1D1DAED974EBAB1253842F560533657C1DA36DDB5C00A968899CE33CF146FBBE7FDB24B8
	9F1F961E9D4CFA85FF737961CFA863D30EED91A9A76E892C3B415F50DCC47AF7721B17962355F205FE127CB4885A3AD87E970B7D7B4F34B8307FFA966B77572E453E7D35E731FEFF7D75E2F5FF4DF85FDACC5A3E8B7A2FA6A1B7DA701A856FBBD4A7611D35185CE6413F0B62A27C9A19D4087C7BA82EBE4AEB427D66AADEB367BD321844G18EC360BEFC5CCDACC5791DB2CD67E7B75D446E20310907E18CCB1DA9CC6956E67842FD942670E7BF477638DC46C1D495EAAF24DC6BE21FD17F21072231E503ECB092A
	44F7A9B3A02FA48C5F6544FD2ED77F78B03ECF950E534274569A959F53AB9743E56740D1FAD22F5C66A3FA655239EC56133E6F9F78F764166B15AB63728DFE4BD90A754A19383C47EF39535B305C9815CCA8BF0F00E39450CCD59EABF7B8F9F86D79DBFAEF09FBD25B1E9FD07E5CA7356D69CE4676CCFE2A59E0FDCA2C81AE515D677F945F678F470427745A656CAA3DF2A9CF73F796F0ACFEDA2F5C8335FA65EA38EC6B537E7C3B53677BED049CDCFE145FF2CBCE68158B6172E1FE4B1DAB47F2D3D5B2B37879EC
	B84E85CA8B573667022543EB4F89741E1A992EED4FA7A8BFBFDC5B1E45CB31BD4BA0AFB4DC2CE3EF532BBFF1B24339860EB913755A654D13FA65AA38ECA39C4D3A65EA8F68156B6732E3A77B736F71EBFA65A6F279A9FE4BE5176A151B4E65E37D160BEC559B2F45F37964E758F143B35E65731F7155D78D3CA68D1F5D2BFFC3FEE28867544E10710F4EBBCF935CCF6DDD2A971E2C2C015CEEB95632AA657C306A7C273AFB5A2F9731BA5E3F21F570FDE4F47CE4859F76BF0363A76B999CBF0D963C71F504978369
	B902F76C9816153E0F3C30E3D8B6C5709E3F00E5DB85EFAFFD66A7FBFD9D02F76F91AC5BAFF8FFDF0EF253856F4B95A897A3F8C1F4EC109581774788647D3E98652A856F829DAF84C3BADC705E5F0BF5CC933C1AFDD8B6DA70F651F5D8994FC29D4FA26FCF74B93D9E5226E7713A1F1D2C44491FD467D712EFFC4C5CF5C36BE0B63714E03BFB049FDED476C68A5E34F4EC779902E7DB05E5E78B5E1BE2DFF13C60CD79924BD6BF073C88BA1EEA0234D9705656E1D937609D30225CB441FB48AA5AD3703652F1D746
	734D067C67451E4793A8D7A6F84EF20C19B102B7259B6B88953C35220EB0417BF38B4647DC417B1D8963A8ED0A78166AD2A5CEA62B4ECB175E7818B9FDC36BE0B6FF58066D1EB9857DF042087E4A973C3213586EC102775EC9AC9BACF8C7CFE159D0417B36914B4689DE9B9D2BA67E2059107E83647D78G164D90BC53B514F3A8F257C4FFA4F895F4CCBB9D52B3856F51D2141BADF83FEEC5FDA5AFA06F6097988B5502F70DDEB7CDA4ADF8BFAF41F89BABF8274FE19D93852FF09F46D17C8BB8E65D249933EEA7
	C3CEBEE67D5619C07BA355EB5012DF74DE973E8152E4B13772CE757BF5A4FEE47A0D8993DFF37613BA1C2348FA915F9557FC2CE0F87558C09896E9F014E9B0B428B0307AFEFE816BEB9575F52FD66AFB6A48E056CF7A9B27BBC0A77E469353FD5B9AC5F76F0A116C6D6C875D38460C79735EE26F361B3CD629EBEE71E07C662F2E51AFAA672C2EEFEC733FFEF89C3BC7C219D06153736B95AC67778D04E5D9GAC53DF543E1B2FCA53DFCB5CC26F37B1A0BF6BC50C6F64837AEB24B368B51D9659EC589009183F39
	C1417C56006B1F3531DF93699DF72D115A387BB09D634EAC6AFBF423D25FFB836E7B5246F93F263E31BE753D7BA556B7BE92FD71FA2DF73CC4EDD26A4F2F9BC95C07C7EA637E034D0A6E672CA3097B48C8ED5C6FBF61ED4752ADCADD8F1E98C95CC7C5AA674C0F7753FB798C60470AF6AB582A725B0036850A6D1417346B2D5753FEFB8D70B3DF42F55EA9F4CDF98324DBDFC28C0D5B948C32F1A831BA7AA56F58997F12B6F66413E2BEDC54571729547765292144EA24263EE81F7A420DD85F4C17441E13835EED
	7C68F6257E1987C6922BF1AFE9E3B5EA07CA773511446A5C173431FA36545B0E8FF6AAF5C5160EA4D613DFD64E79F5C0DE8BF11F1A6933C3BA48E43CAC760B1484DE83BE398467606BDF46781BCBF99EC80FFA997799DCB8B712388FFFD99B77676833C9A47023DE56FFA7412F3DCFA4615BF76B546F6C7D17E95C25147943855F777C7DE479B8BFFF28C0B9FF25D0B90FABD2F44C5CCBBACEFF6F6377F94D8D2C5C2368583BD75171DE7960F5B46FD34AFD3EECB0657C7B223FC4511538EAF07EFBE83FD2A661CB
	DF7F3DD72A5818DE261C6FABD74EDB8EA8BA1E6C9E3C6DD78FAABAC2AB949DB126416BF835D2A9F7EF65487C77DE15222B7F64607C7713EA254C3F9AFD7D7761E1454634234AF9D10DF2FE26D65161AE990C8DCCB62AE470EBB9AE57A9F5541C9B3CFF479FD3F07E30DE517157664168D8CDE543CAFE8A9DDE580752823714F25CE24BBBEE74E410AD9CEEE955E7365D8D3231BB6F334ECEFA4279CB8AF713851899D9471A3321D011C835E487489CD0F6917F98DEC6216A3CC8F5DEGA5D40BA887CE4DABC7F2B9
	25D42F79449FBF3C673D693FBE631AAFAD2C155612CF55101F8CA7EF6CE49379BA749959C9BCF5C4DA64A01FB9A78F2F35D2220B9E96385C172DF23D145EF90A7CAC3A4BAD1BAA71DD208BDCC6FFB5AB0F7C86EFDE0D51E5F613A1572775D2A279D1DB373402E5DB0D362EBC76E270D38FD943B82F320D86AE6942A5403B1262A5DAF2051615AFAE5B3E25EE5614791261083458EAB71A0E98BB5CF6E72D3448EDF64AAE335DEA72B88E52CBD5CA6E43375273C15DFAE1BEB1B6175CFE8468EE4F83E3D2E7279EA1
	4B391CD29E15A4F74DDCA1E1827FBB21AA59D98BE404AAB3C55EB7B639F73CF1F4F52BC11A470D3CA8FDAD2D74B85C5242CEE30F45FA995ACF6CD8F1114C5540F1BB01C5FEE78C40E5E840E11628A3772E57726FFE6CBFDBE89DCAD1373419F9930A66F549761E3C4A4ECE0BCD968A403520FE23D0CFE4F2B5B2ABBCF72C2AFB7B1F6D908A55527C1E7CFC7A37007EADA4FF8BA82700F28A8A413FF152007F34799E3419468F60D9C671108848BD820D687E45072FCD78E5D95C9C306E0834007EFE9934C31AA2F5
	223727DDF65E9CF67746A541313712E6F210E871903D6BBDC2A33130163E9D0D3DD4D4BA7FF70F4CB7C8093552C276369862173C72057825EF04EB2579EE3B03069EF92BCA1E1448D34FB28AB68B4E8C3CBFA58F5C8B3D3864C9A24907CB5E7F124EBE49C7F6318FF7A8320355102A12DFB8AC6EE1EE97F9CCBCA23847D4DCC9F0A59517DCDFABFC3B594A5B7E77BBE1672B538D12819A41ACB32FAF68A36F4D295F57813DC5B516F8253278F53B067766869D1901D27F936555DEB4687AF6B0DC520A8BF8744D73
	72ADF2978BEE120A1B6E16D642ED345352556B24CBACB248D7E3CB29ACD92F675D19926EAA5FDE113057C2A7455EAC115486AA3A097E5DB2644B1C145AC64BEE97D767890F40AF6A85DA6E06F18979BF7F382394EED0CDF0F64C8CE7094764520C61E7FA399B87C25E0D304127910843D2389149DE5CC4AE6B4D1D4F8DC996E1C15C3C5EC7E61E9F995F927F9F6DE0508E89BB6E1F4C3E265EBF19F0D30A5F7D207D2F71F74F2FA97BA7595A45A6BA8FB39D7833DF43F5E0134AF6CB7E57B01E6EB1DA2DB04A6D2E
	341948FA462A4BEED9D9B37B85DD2067C2991E4957C0515F3F3820AA735FD0CB8788CE08950054AEGGB02AGGD0CB818294G94G88G88G21F3F9B0CE08950054AEGGB02AGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG8EAFGGGG
**end of data**/
}
/**
 * Return the Channel1EnableJCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getChannel1EnableJCheckBox() {
	if (ivjChannel1EnableJCheckBox == null) {
		try {
			ivjChannel1EnableJCheckBox = new javax.swing.JCheckBox();
			ivjChannel1EnableJCheckBox.setName("Channel1EnableJCheckBox");
			ivjChannel1EnableJCheckBox.setText("Channel 1");
			ivjChannel1EnableJCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			ivjChannel1EnableJCheckBox.setSelected(true);
			ivjChannel1EnableJCheckBox.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjChannel1EnableJCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			ivjChannel1EnableJCheckBox.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjChannel1EnableJCheckBox;
}
/**
 * Return the Channel2EnableJCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getChannel2EnableJCheckBox() {
	if (ivjChannel2EnableJCheckBox == null) {
		try {
			ivjChannel2EnableJCheckBox = new javax.swing.JCheckBox();
			ivjChannel2EnableJCheckBox.setName("Channel2EnableJCheckBox");
			ivjChannel2EnableJCheckBox.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjChannel2EnableJCheckBox.setText("Channel 2");
			ivjChannel2EnableJCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			ivjChannel2EnableJCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjChannel2EnableJCheckBox;
}
/**
 * Return the Channel3EnableJCheckBox1 property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getChannel3EnableJCheckBox1() {
	if (ivjChannel3EnableJCheckBox1 == null) {
		try {
			ivjChannel3EnableJCheckBox1 = new javax.swing.JCheckBox();
			ivjChannel3EnableJCheckBox1.setName("Channel3EnableJCheckBox1");
			ivjChannel3EnableJCheckBox1.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjChannel3EnableJCheckBox1.setText("Channel 3");
			ivjChannel3EnableJCheckBox1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			ivjChannel3EnableJCheckBox1.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjChannel3EnableJCheckBox1;
}
/**
 * Return the EqualsJButton1 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getEqualsJButton1() {
	if (ivjEqualsJButton1 == null) {
		try {
			ivjEqualsJButton1 = new javax.swing.JButton();
			ivjEqualsJButton1.setName("EqualsJButton1");
			ivjEqualsJButton1.setText("=");
			ivjEqualsJButton1.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEqualsJButton1;
}
/**
 * Return the EqualsJButton2 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getEqualsJButton2() {
	if (ivjEqualsJButton2 == null) {
		try {
			ivjEqualsJButton2 = new javax.swing.JButton();
			ivjEqualsJButton2.setName("EqualsJButton2");
			ivjEqualsJButton2.setText("=");
			ivjEqualsJButton2.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEqualsJButton2;
}
/**
 * Return the EqualsJButton3 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getEqualsJButton3() {
	if (ivjEqualsJButton3 == null) {
		try {
			ivjEqualsJButton3 = new javax.swing.JButton();
			ivjEqualsJButton3.setName("EqualsJButton3");
			ivjEqualsJButton3.setText("=");
			ivjEqualsJButton3.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEqualsJButton3;
}
/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getKeLabel() {
	if (ivjKeLabel == null) {
		try {
			ivjKeLabel = new javax.swing.JLabel();
			ivjKeLabel.setName("KeLabel");
			ivjKeLabel.setPreferredSize(new java.awt.Dimension(27, 14));
			ivjKeLabel.setText("Ke: ");
			ivjKeLabel.setMinimumSize(new java.awt.Dimension(27, 14));
			ivjKeLabel.setMaximumSize(new java.awt.Dimension(27, 14));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKeLabel;
}
/**
 * Return the KeLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getKeLabel2() {
	if (ivjKeLabel2 == null) {
		try {
			ivjKeLabel2 = new javax.swing.JLabel();
			ivjKeLabel2.setName("KeLabel2");
			ivjKeLabel2.setPreferredSize(new java.awt.Dimension(27, 14));
			ivjKeLabel2.setText("Ke: ");
			ivjKeLabel2.setMaximumSize(new java.awt.Dimension(27, 14));
			ivjKeLabel2.setMinimumSize(new java.awt.Dimension(27, 14));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKeLabel2;
}
/**
 * Return the KeLabel3 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getKeLabel3() {
	if (ivjKeLabel3 == null) {
		try {
			ivjKeLabel3 = new javax.swing.JLabel();
			ivjKeLabel3.setName("KeLabel3");
			ivjKeLabel3.setText("Ke: ");
			ivjKeLabel3.setMaximumSize(new java.awt.Dimension(27, 14));
			ivjKeLabel3.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			ivjKeLabel3.setPreferredSize(new java.awt.Dimension(27, 14));
			ivjKeLabel3.setMinimumSize(new java.awt.Dimension(27, 14));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKeLabel3;
}
/**
 * Return the JTextField12 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getKeTextField() {
	if (ivjKeTextField == null) {
		try {
			ivjKeTextField = new javax.swing.JTextField();
			ivjKeTextField.setName("KeTextField");
			// user code begin {1}
			ivjKeTextField.setDocument(new com.cannontech.common.gui.unchanging.DoubleRangeDocument(0.00, 10.00, 2) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKeTextField;
}
/**
 * Return the KeTextField2 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getKeTextField2() {
	if (ivjKeTextField2 == null) {
		try {
			ivjKeTextField2 = new javax.swing.JTextField();
			ivjKeTextField2.setName("KeTextField2");
			// user code begin {1}
			ivjKeTextField2.setDocument(new com.cannontech.common.gui.unchanging.DoubleRangeDocument(0.00, 10.00, 2) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKeTextField2;
}
/**
 * Return the KeTextField3 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getKeTextField3() {
	if (ivjKeTextField3 == null) {
		try {
			ivjKeTextField3 = new javax.swing.JTextField();
			ivjKeTextField3.setName("KeTextField3");
			// user code begin {1}
			ivjKeTextField3.setDocument(new com.cannontech.common.gui.unchanging.DoubleRangeDocument(0.00, 10.00, 2) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKeTextField3;
}
/**
 * Return the MpLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getKhLabel() {
	if (ivjKhLabel == null) {
		try {
			ivjKhLabel = new javax.swing.JLabel();
			ivjKhLabel.setName("KhLabel");
			ivjKhLabel.setText("Kh:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKhLabel;
}
/**
 * Return the KhLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getKhLabel2() {
	if (ivjKhLabel2 == null) {
		try {
			ivjKhLabel2 = new javax.swing.JLabel();
			ivjKhLabel2.setName("KhLabel2");
			ivjKhLabel2.setText("Kh:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKhLabel2;
}
/**
 * Return the KhLabel3 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getKhLabel3() {
	if (ivjKhLabel3 == null) {
		try {
			ivjKhLabel3 = new javax.swing.JLabel();
			ivjKhLabel3.setName("KhLabel3");
			ivjKhLabel3.setText("Kh:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKhLabel3;
}
/**
 * Return the JTextField11 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getKhTextField() {
	if (ivjKhTextField == null) {
		try {
			ivjKhTextField = new javax.swing.JTextField();
			ivjKhTextField.setName("KhTextField");
			ivjKhTextField.setText("");
			// user code begin {1}
			ivjKhTextField.setDocument(new com.cannontech.common.gui.unchanging.DoubleRangeDocument(0.00, 10.00, 2)  );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKhTextField;
}
/**
 * Return the KhTextField2 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getKhTextField2() {
	if (ivjKhTextField2 == null) {
		try {
			ivjKhTextField2 = new javax.swing.JTextField();
			ivjKhTextField2.setName("KhTextField2");
			ivjKhTextField2.setText("");
			// user code begin {1}
			ivjKhTextField2.setDocument(new com.cannontech.common.gui.unchanging.DoubleRangeDocument(0.00, 10.00, 2)  );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKhTextField2;
}
/**
 * Return the KhTextField3 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getKhTextField3() {
	if (ivjKhTextField3 == null) {
		try {
			ivjKhTextField3 = new javax.swing.JTextField();
			ivjKhTextField3.setName("KhTextField3");
			ivjKhTextField3.setText("");
			// user code begin {1}
			ivjKhTextField3.setDocument(new com.cannontech.common.gui.unchanging.DoubleRangeDocument(0.00, 10.00, 2)  );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKhTextField3;
}
/**
 * Return the KY2WireButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getKY2WireButton() {
	if (ivjKY2WireButton == null) {
		try {
			ivjKY2WireButton = new javax.swing.JRadioButton();
			ivjKY2WireButton.setName("KY2WireButton");
			ivjKY2WireButton.setText("2-Wire (KY)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKY2WireButton;
}
/**
 * Return the KY2WireButton2 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getKY2WireButton2() {
	if (ivjKY2WireButton2 == null) {
		try {
			ivjKY2WireButton2 = new javax.swing.JRadioButton();
			ivjKY2WireButton2.setName("KY2WireButton2");
			ivjKY2WireButton2.setText("2-Wire (KY)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKY2WireButton2;
}
/**
 * Return the KY2WireButton3 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getKY2WireButton3() {
	if (ivjKY2WireButton3 == null) {
		try {
			ivjKY2WireButton3 = new javax.swing.JRadioButton();
			ivjKY2WireButton3.setName("KY2WireButton3");
			ivjKY2WireButton3.setText("2-Wire (KY)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKY2WireButton3;
}
/**
 * Return the KYZ3WireButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getKYZ3WireButton() {
	if (ivjKYZ3WireButton == null) {
		try {
			ivjKYZ3WireButton = new javax.swing.JRadioButton();
			ivjKYZ3WireButton.setName("KYZ3WireButton");
			ivjKYZ3WireButton.setSelected(true);
			ivjKYZ3WireButton.setText("3-Wire (KYZ)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKYZ3WireButton;
}
/**
 * Return the KYZ3WireButton2 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getKYZ3WireButton2() {
	if (ivjKYZ3WireButton2 == null) {
		try {
			ivjKYZ3WireButton2 = new javax.swing.JRadioButton();
			ivjKYZ3WireButton2.setName("KYZ3WireButton2");
			ivjKYZ3WireButton2.setSelected(true);
			ivjKYZ3WireButton2.setText("3-Wire (KYZ)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKYZ3WireButton2;
}
/**
 * Return the KYZ3WireButton3 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getKYZ3WireButton3() {
	if (ivjKYZ3WireButton3 == null) {
		try {
			ivjKYZ3WireButton3 = new javax.swing.JRadioButton();
			ivjKYZ3WireButton3.setName("KYZ3WireButton3");
			ivjKYZ3WireButton3.setSelected(true);
			ivjKYZ3WireButton3.setText("3-Wire (KYZ)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKYZ3WireButton3;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getMinimumSize() {
	return getPreferredSize();
}
/**
 * Return the MinMaxModeButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getMinMaxModeButton() {
	if (ivjMinMaxModeButton == null) {
		try {
			ivjMinMaxModeButton = new javax.swing.JRadioButton();
			ivjMinMaxModeButton.setName("MinMaxModeButton");
			ivjMinMaxModeButton.setText("Min/Max Mode");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMinMaxModeButton;
}
/**
 * Return the MpLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getMpLabel() {
	if (ivjMpLabel == null) {
		try {
			ivjMpLabel = new javax.swing.JLabel();
			ivjMpLabel.setName("MpLabel");
			ivjMpLabel.setText("Mp:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMpLabel;
}
/**
 * Return the MpLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getMpLabel2() {
	if (ivjMpLabel2 == null) {
		try {
			ivjMpLabel2 = new javax.swing.JLabel();
			ivjMpLabel2.setName("MpLabel2");
			ivjMpLabel2.setText("Mp:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMpLabel2;
}
/**
 * Return the MpLabel3 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getMpLabel3() {
	if (ivjMpLabel3 == null) {
		try {
			ivjMpLabel3 = new javax.swing.JLabel();
			ivjMpLabel3.setName("MpLabel3");
			ivjMpLabel3.setText("Mp:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMpLabel3;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getMpTextField() {
	if (ivjMpTextField == null) {
		try {
			ivjMpTextField = new javax.swing.JTextField();
			ivjMpTextField.setName("MpTextField");
			ivjMpTextField.setText("");
			// user code begin {1}
			ivjMpTextField.setDocument(new com.cannontech.common.gui.unchanging.DoubleRangeDocument(0.00, 10.00, 2)  );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMpTextField;
}
/**
 * Return the MpTextField2 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getMpTextField2() {
	if (ivjMpTextField2 == null) {
		try {
			ivjMpTextField2 = new javax.swing.JTextField();
			ivjMpTextField2.setName("MpTextField2");
			ivjMpTextField2.setText("");
			// user code begin {1}
			ivjMpTextField2.setDocument(new com.cannontech.common.gui.unchanging.DoubleRangeDocument(0.00, 10.00, 2)  );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMpTextField2;
}
/**
 * Return the MpTextField3 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getMpTextField3() {
	if (ivjMpTextField3 == null) {
		try {
			ivjMpTextField3 = new javax.swing.JTextField();
			ivjMpTextField3.setName("MpTextField3");
			ivjMpTextField3.setText("");
			// user code begin {1}
			ivjMpTextField3.setDocument(new com.cannontech.common.gui.unchanging.DoubleRangeDocument(0.00, 10.00, 2)  );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMpTextField3;
}
/**
 * Return the MultiplierPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getMultiplierPanel() {
	if (ivjMultiplierPanel == null) {
		try {
			ivjMultiplierPanel = new javax.swing.JPanel();
			ivjMultiplierPanel.setName("MultiplierPanel");
			ivjMultiplierPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsKYZ3WireButton = new java.awt.GridBagConstraints();
			constraintsKYZ3WireButton.gridx = 1; constraintsKYZ3WireButton.gridy = 1;
			constraintsKYZ3WireButton.gridwidth = 4;
			constraintsKYZ3WireButton.ipadx = 20;
			constraintsKYZ3WireButton.insets = new java.awt.Insets(7, 21, 2, 10);
			getMultiplierPanel().add(getKYZ3WireButton(), constraintsKYZ3WireButton);

			java.awt.GridBagConstraints constraintsKY2WireButton = new java.awt.GridBagConstraints();
			constraintsKY2WireButton.gridx = 5; constraintsKY2WireButton.gridy = 1;
			constraintsKY2WireButton.gridwidth = 4;
			constraintsKY2WireButton.ipadx = 30;
			constraintsKY2WireButton.insets = new java.awt.Insets(7, 10, 2, 65);
			getMultiplierPanel().add(getKY2WireButton(), constraintsKY2WireButton);

			java.awt.GridBagConstraints constraintsMpTextField = new java.awt.GridBagConstraints();
			constraintsMpTextField.gridx = 2; constraintsMpTextField.gridy = 2;
			constraintsMpTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsMpTextField.weightx = 1.0;
			constraintsMpTextField.ipadx = 39;
			constraintsMpTextField.insets = new java.awt.Insets(6, 1, 13, 3);
			getMultiplierPanel().add(getMpTextField(), constraintsMpTextField);

			java.awt.GridBagConstraints constraintsKhTextField = new java.awt.GridBagConstraints();
			constraintsKhTextField.gridx = 4; constraintsKhTextField.gridy = 2;
			constraintsKhTextField.gridwidth = 2;
			constraintsKhTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsKhTextField.weightx = 1.0;
			constraintsKhTextField.ipadx = 39;
			constraintsKhTextField.insets = new java.awt.Insets(6, 28, 13, 2);
			getMultiplierPanel().add(getKhTextField(), constraintsKhTextField);

			java.awt.GridBagConstraints constraintsKeTextField = new java.awt.GridBagConstraints();
			constraintsKeTextField.gridx = 8; constraintsKeTextField.gridy = 2;
			constraintsKeTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsKeTextField.weightx = 1.0;
			constraintsKeTextField.ipadx = 46;
			constraintsKeTextField.insets = new java.awt.Insets(6, 2, 13, 21);
			getMultiplierPanel().add(getKeTextField(), constraintsKeTextField);

			java.awt.GridBagConstraints constraintsMpLabel = new java.awt.GridBagConstraints();
			constraintsMpLabel.gridx = 1; constraintsMpLabel.gridy = 2;
			constraintsMpLabel.ipadx = 7;
			constraintsMpLabel.insets = new java.awt.Insets(8, 12, 17, 0);
			getMultiplierPanel().add(getMpLabel(), constraintsMpLabel);

			java.awt.GridBagConstraints constraintsTimesLabel = new java.awt.GridBagConstraints();
			constraintsTimesLabel.gridx = 3; constraintsTimesLabel.gridy = 2;
			constraintsTimesLabel.ipadx = 5;
			constraintsTimesLabel.insets = new java.awt.Insets(8, 4, 17, 2);
			getMultiplierPanel().add(getTimesLabel(), constraintsTimesLabel);

			java.awt.GridBagConstraints constraintsKhLabel = new java.awt.GridBagConstraints();
			constraintsKhLabel.gridx = 4; constraintsKhLabel.gridy = 2;
			constraintsKhLabel.ipadx = 10;
			constraintsKhLabel.insets = new java.awt.Insets(8, 3, 17, 11);
			getMultiplierPanel().add(getKhLabel(), constraintsKhLabel);

			java.awt.GridBagConstraints constraintsKeLabel = new java.awt.GridBagConstraints();
			constraintsKeLabel.gridx = 7; constraintsKeLabel.gridy = 2;
			constraintsKeLabel.insets = new java.awt.Insets(8, 3, 17, 1);
			getMultiplierPanel().add(getKeLabel(), constraintsKeLabel);

			java.awt.GridBagConstraints constraintsRecalculateJButton1 = new java.awt.GridBagConstraints();
			constraintsRecalculateJButton1.gridx = 1; constraintsRecalculateJButton1.gridy = 2;
			constraintsRecalculateJButton1.gridwidth = 6;
			constraintsRecalculateJButton1.insets = new java.awt.Insets(2, 24, 11, 3);
			getMultiplierPanel().add(getRecalculateJButton1(), constraintsRecalculateJButton1);

			java.awt.GridBagConstraints constraintsEqualsJButton1 = new java.awt.GridBagConstraints();
			constraintsEqualsJButton1.gridx = 6; constraintsEqualsJButton1.gridy = 2;
			constraintsEqualsJButton1.ipadx = 14;
			constraintsEqualsJButton1.ipady = -5;
			constraintsEqualsJButton1.insets = new java.awt.Insets(6, 2, 13, 2);
			getMultiplierPanel().add(getEqualsJButton1(), constraintsEqualsJButton1);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMultiplierPanel;
}
/**
 * Return the MultiplierPanel2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getMultiplierPanel2() {
	if (ivjMultiplierPanel2 == null) {
		try {
			ivjMultiplierPanel2 = new javax.swing.JPanel();
			ivjMultiplierPanel2.setName("MultiplierPanel2");
			ivjMultiplierPanel2.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsKYZ3WireButton2 = new java.awt.GridBagConstraints();
			constraintsKYZ3WireButton2.gridx = 1; constraintsKYZ3WireButton2.gridy = 1;
			constraintsKYZ3WireButton2.gridwidth = 4;
			constraintsKYZ3WireButton2.ipadx = 20;
			constraintsKYZ3WireButton2.insets = new java.awt.Insets(7, 21, 2, 10);
			getMultiplierPanel2().add(getKYZ3WireButton2(), constraintsKYZ3WireButton2);

			java.awt.GridBagConstraints constraintsKY2WireButton2 = new java.awt.GridBagConstraints();
			constraintsKY2WireButton2.gridx = 5; constraintsKY2WireButton2.gridy = 1;
			constraintsKY2WireButton2.gridwidth = 4;
			constraintsKY2WireButton2.ipadx = 30;
			constraintsKY2WireButton2.insets = new java.awt.Insets(7, 10, 2, 65);
			getMultiplierPanel2().add(getKY2WireButton2(), constraintsKY2WireButton2);

			java.awt.GridBagConstraints constraintsMpTextField2 = new java.awt.GridBagConstraints();
			constraintsMpTextField2.gridx = 2; constraintsMpTextField2.gridy = 2;
			constraintsMpTextField2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsMpTextField2.weightx = 1.0;
			constraintsMpTextField2.ipadx = 39;
			constraintsMpTextField2.insets = new java.awt.Insets(6, 1, 13, 3);
			getMultiplierPanel2().add(getMpTextField2(), constraintsMpTextField2);

			java.awt.GridBagConstraints constraintsKhTextField2 = new java.awt.GridBagConstraints();
			constraintsKhTextField2.gridx = 4; constraintsKhTextField2.gridy = 2;
			constraintsKhTextField2.gridwidth = 2;
			constraintsKhTextField2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsKhTextField2.weightx = 1.0;
			constraintsKhTextField2.ipadx = 39;
			constraintsKhTextField2.insets = new java.awt.Insets(6, 28, 13, 2);
			getMultiplierPanel2().add(getKhTextField2(), constraintsKhTextField2);

			java.awt.GridBagConstraints constraintsKeTextField2 = new java.awt.GridBagConstraints();
			constraintsKeTextField2.gridx = 8; constraintsKeTextField2.gridy = 2;
			constraintsKeTextField2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsKeTextField2.weightx = 1.0;
			constraintsKeTextField2.ipadx = 46;
			constraintsKeTextField2.insets = new java.awt.Insets(6, 2, 13, 21);
			getMultiplierPanel2().add(getKeTextField2(), constraintsKeTextField2);

			java.awt.GridBagConstraints constraintsMpLabel2 = new java.awt.GridBagConstraints();
			constraintsMpLabel2.gridx = 1; constraintsMpLabel2.gridy = 2;
			constraintsMpLabel2.ipadx = 7;
			constraintsMpLabel2.insets = new java.awt.Insets(8, 12, 17, 0);
			getMultiplierPanel2().add(getMpLabel2(), constraintsMpLabel2);

			java.awt.GridBagConstraints constraintsTimesLabel2 = new java.awt.GridBagConstraints();
			constraintsTimesLabel2.gridx = 3; constraintsTimesLabel2.gridy = 2;
			constraintsTimesLabel2.ipadx = 5;
			constraintsTimesLabel2.insets = new java.awt.Insets(8, 4, 17, 2);
			getMultiplierPanel2().add(getTimesLabel2(), constraintsTimesLabel2);

			java.awt.GridBagConstraints constraintsKhLabel2 = new java.awt.GridBagConstraints();
			constraintsKhLabel2.gridx = 4; constraintsKhLabel2.gridy = 2;
			constraintsKhLabel2.ipadx = 10;
			constraintsKhLabel2.insets = new java.awt.Insets(8, 3, 17, 11);
			getMultiplierPanel2().add(getKhLabel2(), constraintsKhLabel2);

			java.awt.GridBagConstraints constraintsKeLabel2 = new java.awt.GridBagConstraints();
			constraintsKeLabel2.gridx = 7; constraintsKeLabel2.gridy = 2;
			constraintsKeLabel2.insets = new java.awt.Insets(8, 3, 17, 1);
			getMultiplierPanel2().add(getKeLabel2(), constraintsKeLabel2);

			java.awt.GridBagConstraints constraintsRecalculateJButton2 = new java.awt.GridBagConstraints();
			constraintsRecalculateJButton2.gridx = 1; constraintsRecalculateJButton2.gridy = 2;
			constraintsRecalculateJButton2.gridwidth = 6;
			constraintsRecalculateJButton2.insets = new java.awt.Insets(2, 24, 11, 3);
			getMultiplierPanel2().add(getRecalculateJButton2(), constraintsRecalculateJButton2);

			java.awt.GridBagConstraints constraintsEqualsJButton2 = new java.awt.GridBagConstraints();
			constraintsEqualsJButton2.gridx = 6; constraintsEqualsJButton2.gridy = 2;
			constraintsEqualsJButton2.ipadx = 14;
			constraintsEqualsJButton2.ipady = -5;
			constraintsEqualsJButton2.insets = new java.awt.Insets(6, 2, 13, 2);
			getMultiplierPanel2().add(getEqualsJButton2(), constraintsEqualsJButton2);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMultiplierPanel2;
}
/**
 * Return the MultiplierPanel3 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getMultiplierPanel3() {
	if (ivjMultiplierPanel3 == null) {
		try {
			ivjMultiplierPanel3 = new javax.swing.JPanel();
			ivjMultiplierPanel3.setName("MultiplierPanel3");
			ivjMultiplierPanel3.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsKYZ3WireButton3 = new java.awt.GridBagConstraints();
			constraintsKYZ3WireButton3.gridx = 1; constraintsKYZ3WireButton3.gridy = 1;
			constraintsKYZ3WireButton3.gridwidth = 4;
			constraintsKYZ3WireButton3.ipadx = 20;
			constraintsKYZ3WireButton3.insets = new java.awt.Insets(7, 21, 2, 10);
			getMultiplierPanel3().add(getKYZ3WireButton3(), constraintsKYZ3WireButton3);

			java.awt.GridBagConstraints constraintsKY2WireButton3 = new java.awt.GridBagConstraints();
			constraintsKY2WireButton3.gridx = 5; constraintsKY2WireButton3.gridy = 1;
			constraintsKY2WireButton3.gridwidth = 4;
			constraintsKY2WireButton3.ipadx = 30;
			constraintsKY2WireButton3.insets = new java.awt.Insets(7, 10, 2, 65);
			getMultiplierPanel3().add(getKY2WireButton3(), constraintsKY2WireButton3);

			java.awt.GridBagConstraints constraintsMpTextField3 = new java.awt.GridBagConstraints();
			constraintsMpTextField3.gridx = 2; constraintsMpTextField3.gridy = 2;
			constraintsMpTextField3.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsMpTextField3.weightx = 1.0;
			constraintsMpTextField3.ipadx = 39;
			constraintsMpTextField3.insets = new java.awt.Insets(6, 1, 13, 3);
			getMultiplierPanel3().add(getMpTextField3(), constraintsMpTextField3);

			java.awt.GridBagConstraints constraintsKhTextField3 = new java.awt.GridBagConstraints();
			constraintsKhTextField3.gridx = 4; constraintsKhTextField3.gridy = 2;
			constraintsKhTextField3.gridwidth = 2;
			constraintsKhTextField3.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsKhTextField3.weightx = 1.0;
			constraintsKhTextField3.ipadx = 39;
			constraintsKhTextField3.insets = new java.awt.Insets(6, 28, 13, 2);
			getMultiplierPanel3().add(getKhTextField3(), constraintsKhTextField3);

			java.awt.GridBagConstraints constraintsKeTextField3 = new java.awt.GridBagConstraints();
			constraintsKeTextField3.gridx = 8; constraintsKeTextField3.gridy = 2;
			constraintsKeTextField3.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsKeTextField3.weightx = 1.0;
			constraintsKeTextField3.ipadx = 46;
			constraintsKeTextField3.insets = new java.awt.Insets(6, 2, 13, 21);
			getMultiplierPanel3().add(getKeTextField3(), constraintsKeTextField3);

			java.awt.GridBagConstraints constraintsMpLabel3 = new java.awt.GridBagConstraints();
			constraintsMpLabel3.gridx = 1; constraintsMpLabel3.gridy = 2;
			constraintsMpLabel3.ipadx = 7;
			constraintsMpLabel3.insets = new java.awt.Insets(8, 12, 17, 0);
			getMultiplierPanel3().add(getMpLabel3(), constraintsMpLabel3);

			java.awt.GridBagConstraints constraintsTimesLabel3 = new java.awt.GridBagConstraints();
			constraintsTimesLabel3.gridx = 3; constraintsTimesLabel3.gridy = 2;
			constraintsTimesLabel3.ipadx = 5;
			constraintsTimesLabel3.insets = new java.awt.Insets(8, 4, 17, 2);
			getMultiplierPanel3().add(getTimesLabel3(), constraintsTimesLabel3);

			java.awt.GridBagConstraints constraintsKhLabel3 = new java.awt.GridBagConstraints();
			constraintsKhLabel3.gridx = 4; constraintsKhLabel3.gridy = 2;
			constraintsKhLabel3.ipadx = 10;
			constraintsKhLabel3.insets = new java.awt.Insets(8, 3, 17, 11);
			getMultiplierPanel3().add(getKhLabel3(), constraintsKhLabel3);

			java.awt.GridBagConstraints constraintsKeLabel3 = new java.awt.GridBagConstraints();
			constraintsKeLabel3.gridx = 7; constraintsKeLabel3.gridy = 2;
			constraintsKeLabel3.insets = new java.awt.Insets(8, 3, 17, 1);
			getMultiplierPanel3().add(getKeLabel3(), constraintsKeLabel3);

			java.awt.GridBagConstraints constraintsRecalculateJButton3 = new java.awt.GridBagConstraints();
			constraintsRecalculateJButton3.gridx = 1; constraintsRecalculateJButton3.gridy = 2;
			constraintsRecalculateJButton3.gridwidth = 6;
			constraintsRecalculateJButton3.insets = new java.awt.Insets(2, 24, 11, 3);
			getMultiplierPanel3().add(getRecalculateJButton3(), constraintsRecalculateJButton3);

			java.awt.GridBagConstraints constraintsEqualsJButton3 = new java.awt.GridBagConstraints();
			constraintsEqualsJButton3.gridx = 6; constraintsEqualsJButton3.gridy = 2;
			constraintsEqualsJButton3.ipadx = 14;
			constraintsEqualsJButton3.ipady = -5;
			constraintsEqualsJButton3.insets = new java.awt.Insets(6, 2, 13, 2);
			getMultiplierPanel3().add(getEqualsJButton3(), constraintsEqualsJButton3);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMultiplierPanel3;
}
/**
 * Return the NameLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getNameLabel() {
	if (ivjNameLabel == null) {
		try {
			ivjNameLabel = new javax.swing.JLabel();
			ivjNameLabel.setName("NameLabel");
			ivjNameLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjNameLabel.setText("Configuration Name:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNameLabel;
}
/**
 * Return the NameTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getNameTextField() {
	if (ivjNameTextField == null) {
		try {
			ivjNameTextField = new javax.swing.JTextField();
			ivjNameTextField.setName("NameTextField");
			ivjNameTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjNameTextField.setColumns(12);
			// user code begin {1}
			ivjNameTextField.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_DEVICE_NAME_LENGTH));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNameTextField;
}
/**
 * Return the peakModeButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getpeakModeButton() {
	if (ivjpeakModeButton == null) {
		try {
			ivjpeakModeButton = new javax.swing.JRadioButton();
			ivjpeakModeButton.setName("peakModeButton");
			ivjpeakModeButton.setSelected(true);
			ivjpeakModeButton.setText("On-Peak/Off-Peak Mode");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjpeakModeButton;
}
/**
 * Return the peakModeButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getPeakModeButton() {
	if (ivjpeakModeButton == null) {
		try {
			ivjpeakModeButton = new javax.swing.JRadioButton();
			ivjpeakModeButton.setName("peakModeButton");
			ivjpeakModeButton.setSelected(true);
			ivjpeakModeButton.setText("On-Peak/Off-Peak Mode");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjpeakModeButton;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getPreferredSize() {
	return new Dimension( 350, 200);
}
/**
 * Return the RecalculateJButton1 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getRecalculateJButton1() {
	if (ivjRecalculateJButton1 == null) {
		try {
			ivjRecalculateJButton1 = new javax.swing.JButton();
			ivjRecalculateJButton1.setName("RecalculateJButton1");
			ivjRecalculateJButton1.setText("Recalculate");
			ivjRecalculateJButton1.setVisible(false);
			ivjRecalculateJButton1.setMaximumSize(new java.awt.Dimension(210, 26));
			ivjRecalculateJButton1.setPreferredSize(new java.awt.Dimension(210, 26));
			ivjRecalculateJButton1.setMinimumSize(new java.awt.Dimension(210, 26));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRecalculateJButton1;
}
/**
 * Return the RecalculateJButton2 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getRecalculateJButton2() {
	if (ivjRecalculateJButton2 == null) {
		try {
			ivjRecalculateJButton2 = new javax.swing.JButton();
			ivjRecalculateJButton2.setName("RecalculateJButton2");
			ivjRecalculateJButton2.setText("Recalculate");
			ivjRecalculateJButton2.setVisible(false);
			ivjRecalculateJButton2.setMaximumSize(new java.awt.Dimension(210, 26));
			ivjRecalculateJButton2.setPreferredSize(new java.awt.Dimension(210, 26));
			ivjRecalculateJButton2.setMinimumSize(new java.awt.Dimension(210, 26));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRecalculateJButton2;
}
/**
 * Return the RecalculateJButton3 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getRecalculateJButton3() {
	if (ivjRecalculateJButton3 == null) {
		try {
			ivjRecalculateJButton3 = new javax.swing.JButton();
			ivjRecalculateJButton3.setName("RecalculateJButton3");
			ivjRecalculateJButton3.setText("Recalculate");
			ivjRecalculateJButton3.setVisible(false);
			ivjRecalculateJButton3.setMaximumSize(new java.awt.Dimension(210, 26));
			ivjRecalculateJButton3.setPreferredSize(new java.awt.Dimension(210, 26));
			ivjRecalculateJButton3.setMinimumSize(new java.awt.Dimension(210, 26));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRecalculateJButton3;
}
/**
 * Return the TimesLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getTimesLabel() {
	if (ivjTimesLabel == null) {
		try {
			ivjTimesLabel = new javax.swing.JLabel();
			ivjTimesLabel.setName("TimesLabel");
			ivjTimesLabel.setText("X");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTimesLabel;
}
/**
 * Return the TimesLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getTimesLabel2() {
	if (ivjTimesLabel2 == null) {
		try {
			ivjTimesLabel2 = new javax.swing.JLabel();
			ivjTimesLabel2.setName("TimesLabel2");
			ivjTimesLabel2.setText("X");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTimesLabel2;
}
/**
 * Return the TimesLabel3 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getTimesLabel3() {
	if (ivjTimesLabel3 == null) {
		try {
			ivjTimesLabel3 = new javax.swing.JLabel();
			ivjTimesLabel3.setName("TimesLabel3");
			ivjTimesLabel3.setText("X");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTimesLabel3;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public java.lang.Object getValue(java.lang.Object o) {
	ConfigTwoWay conMan;
	
	if( o != null )
		conMan = (ConfigTwoWay)o;
	else
		conMan = new ConfigTwoWay();
	
	conMan.setConfigName(getNameTextField().getText());
		
	conMan.setConfigType(ConfigTwoWay.SERIES_300_TYPE); 
		 
	if(getMinMaxModeButton().isSelected())
		conMan.setConfigMode(ConfigTwoWay.MODE_MINMAX);
	else
		conMan.setConfigMode(ConfigTwoWay.MODE_PEAKOFFPEAK);
	
	//first channel
	if(getKY2WireButton().isSelected())
		conMan.setMCTWire1(ConfigTwoWay.TWOWIRE);
	else
		conMan.setMCTWire1(ConfigTwoWay.THREEWIRE);
	
	conMan.setKe1(new Double( Double.parseDouble(getKeTextField().getText())) );
	
	//second channel
	if(getChannel2EnableJCheckBox().isSelected())
	{
		if(getKY2WireButton2().isSelected())
			conMan.setMCTWire2(ConfigTwoWay.TWOWIRE);
		else
			conMan.setMCTWire2(ConfigTwoWay.THREEWIRE);
	
		conMan.setKe2(new Double( Double.parseDouble(getKeTextField2().getText())) );
	}
		
	//third channel
	if(getChannel3EnableJCheckBox1().isSelected())
	{
		if(getKY2WireButton3().isSelected())
			conMan.setMCTWire3(ConfigTwoWay.TWOWIRE);
		else
			conMan.setMCTWire3(ConfigTwoWay.THREEWIRE);
	
		conMan.setKe3(new Double( Double.parseDouble(getKeTextField3().getText())) );
	}	
	return conMan;
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
	// user code end
	getNameTextField().addCaretListener(ivjEventHandler);
	getMinMaxModeButton().addActionListener(ivjEventHandler);
	getpeakModeButton().addActionListener(ivjEventHandler);
	getKY2WireButton().addActionListener(ivjEventHandler);
	getKYZ3WireButton().addActionListener(ivjEventHandler);
	getKY2WireButton2().addActionListener(ivjEventHandler);
	getKYZ3WireButton2().addActionListener(ivjEventHandler);
	getKYZ3WireButton3().addActionListener(ivjEventHandler);
	getKY2WireButton3().addActionListener(ivjEventHandler);
	getKeTextField().addCaretListener(ivjEventHandler);
	getKeTextField2().addCaretListener(ivjEventHandler);
	getKeTextField3().addCaretListener(ivjEventHandler);
	getChannel2EnableJCheckBox().addActionListener(ivjEventHandler);
	getChannel3EnableJCheckBox1().addActionListener(ivjEventHandler);
	getChannel1EnableJCheckBox().addActionListener(ivjEventHandler);
	getRecalculateJButton1().addActionListener(ivjEventHandler);
	getRecalculateJButton2().addActionListener(ivjEventHandler);
	getRecalculateJButton3().addActionListener(ivjEventHandler);
	getEqualsJButton1().addActionListener(ivjEventHandler);
	getEqualsJButton2().addActionListener(ivjEventHandler);
	getEqualsJButton3().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("Series300ConfigPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(350, 357);

		java.awt.GridBagConstraints constraintsNameLabel = new java.awt.GridBagConstraints();
		constraintsNameLabel.gridx = 1; constraintsNameLabel.gridy = 1;
		constraintsNameLabel.insets = new java.awt.Insets(17, 11, 8, 3);
		add(getNameLabel(), constraintsNameLabel);

		java.awt.GridBagConstraints constraintsNameTextField = new java.awt.GridBagConstraints();
		constraintsNameTextField.gridx = 2; constraintsNameTextField.gridy = 1;
		constraintsNameTextField.gridwidth = 2;
		constraintsNameTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsNameTextField.weightx = 1.0;
		constraintsNameTextField.ipadx = 190;
		constraintsNameTextField.insets = new java.awt.Insets(15, 4, 6, 9);
		add(getNameTextField(), constraintsNameTextField);

		java.awt.GridBagConstraints constraintspeakModeButton = new java.awt.GridBagConstraints();
		constraintspeakModeButton.gridx = 1; constraintspeakModeButton.gridy = 2;
		constraintspeakModeButton.gridwidth = 2;
		constraintspeakModeButton.insets = new java.awt.Insets(6, 21, 2, 7);
		add(getpeakModeButton(), constraintspeakModeButton);

		java.awt.GridBagConstraints constraintsMinMaxModeButton = new java.awt.GridBagConstraints();
		constraintsMinMaxModeButton.gridx = 3; constraintsMinMaxModeButton.gridy = 2;
		constraintsMinMaxModeButton.ipadx = 13;
		constraintsMinMaxModeButton.insets = new java.awt.Insets(6, 8, 2, 34);
		add(getMinMaxModeButton(), constraintsMinMaxModeButton);

		java.awt.GridBagConstraints constraintsMultiplierPanel = new java.awt.GridBagConstraints();
		constraintsMultiplierPanel.gridx = 1; constraintsMultiplierPanel.gridy = 4;
		constraintsMultiplierPanel.gridwidth = 3;
		constraintsMultiplierPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsMultiplierPanel.weightx = 1.0;
		constraintsMultiplierPanel.weighty = 1.0;
		constraintsMultiplierPanel.insets = new java.awt.Insets(1, 5, 10, 4);
		add(getMultiplierPanel(), constraintsMultiplierPanel);

		java.awt.GridBagConstraints constraintsMultiplierPanel2 = new java.awt.GridBagConstraints();
		constraintsMultiplierPanel2.gridx = 1; constraintsMultiplierPanel2.gridy = 5;
		constraintsMultiplierPanel2.gridwidth = 3;
		constraintsMultiplierPanel2.fill = java.awt.GridBagConstraints.BOTH;
		constraintsMultiplierPanel2.weightx = 1.0;
		constraintsMultiplierPanel2.weighty = 1.0;
		constraintsMultiplierPanel2.insets = new java.awt.Insets(10, 5, 0, 4);
		add(getMultiplierPanel2(), constraintsMultiplierPanel2);

		java.awt.GridBagConstraints constraintsMultiplierPanel3 = new java.awt.GridBagConstraints();
		constraintsMultiplierPanel3.gridx = 1; constraintsMultiplierPanel3.gridy = 6;
		constraintsMultiplierPanel3.gridwidth = 3;
		constraintsMultiplierPanel3.fill = java.awt.GridBagConstraints.BOTH;
		constraintsMultiplierPanel3.weightx = 1.0;
		constraintsMultiplierPanel3.weighty = 1.0;
		constraintsMultiplierPanel3.insets = new java.awt.Insets(20, 5, 7, 4);
		add(getMultiplierPanel3(), constraintsMultiplierPanel3);

		java.awt.GridBagConstraints constraintsChannel1EnableJCheckBox = new java.awt.GridBagConstraints();
		constraintsChannel1EnableJCheckBox.gridx = 1; constraintsChannel1EnableJCheckBox.gridy = 3;
		constraintsChannel1EnableJCheckBox.ipadx = 16;
		constraintsChannel1EnableJCheckBox.insets = new java.awt.Insets(3, 7, 0, 39);
		add(getChannel1EnableJCheckBox(), constraintsChannel1EnableJCheckBox);

		java.awt.GridBagConstraints constraintsChannel2EnableJCheckBox = new java.awt.GridBagConstraints();
		constraintsChannel2EnableJCheckBox.gridx = 1; constraintsChannel2EnableJCheckBox.gridy = 4;
constraintsChannel2EnableJCheckBox.gridheight = 2;
		constraintsChannel2EnableJCheckBox.ipadx = 16;
		constraintsChannel2EnableJCheckBox.insets = new java.awt.Insets(70, 7, 69, 39);
		add(getChannel2EnableJCheckBox(), constraintsChannel2EnableJCheckBox);

		java.awt.GridBagConstraints constraintsChannel3EnableJCheckBox1 = new java.awt.GridBagConstraints();
		constraintsChannel3EnableJCheckBox1.gridx = 1; constraintsChannel3EnableJCheckBox1.gridy = 6;
		constraintsChannel3EnableJCheckBox1.ipadx = 16;
		constraintsChannel3EnableJCheckBox1.insets = new java.awt.Insets(0, 7, 75, 39);
		add(getChannel3EnableJCheckBox1(), constraintsChannel3EnableJCheckBox1);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	modeButtonGroup.add( getMinMaxModeButton());
	modeButtonGroup.add( getPeakModeButton());
	channel1ButtonGroup.add( getKY2WireButton());
	channel1ButtonGroup.add( getKYZ3WireButton());
	channel2ButtonGroup.add( getKY2WireButton2());
	channel2ButtonGroup.add( getKYZ3WireButton2());
	channel3ButtonGroup.add( getKY2WireButton3());
	channel3ButtonGroup.add( getKYZ3WireButton3());
	getKeTextField2().setEnabled(false);
	getMpTextField2().setEnabled(false);
	getKhTextField2().setEnabled(false);
	getEqualsJButton2().setEnabled(false);
	getKY2WireButton2().setEnabled(false);
	getKYZ3WireButton2().setEnabled(false);
	getRecalculateJButton2().setEnabled(false);
	getKeTextField3().setEnabled(false);
	getMpTextField3().setEnabled(false);
	getKhTextField3().setEnabled(false);
	getEqualsJButton3().setEnabled(false);
	getKY2WireButton3().setEnabled(false);
	getKYZ3WireButton3().setEnabled(false);
	getRecalculateJButton3().setEnabled(false);
	// user code end
}
/**
 * Comment
 */
public void keTextField_CaretUpdate(javax.swing.event.CaretEvent caretEvent) {
	fireInputUpdate();
	return;
}
/**
 * Comment
 */
public void keTextField2_CaretUpdate(javax.swing.event.CaretEvent caretEvent) {
	fireInputUpdate();
	return;
}
/**
 * Comment
 */
public void keTextField3_CaretUpdate(javax.swing.event.CaretEvent caretEvent) {
	fireInputUpdate();
	return;
}
/**
 * Comment
 */
public void kY2WireButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	fireInputUpdate();
	return;
}
/**
 * Comment
 */
public void kY2WireButton2_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	fireInputUpdate();
	return;
}
/**
 * Comment
 */
public void kY2WireButton3_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	fireInputUpdate();
	return;
}
/**
 * Comment
 */
public void kYZ3WireButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	fireInputUpdate();
	return;
}
/**
 * Comment
 */
public void kYZ3WireButton2_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	fireInputUpdate();
	return;
}
/**
 * Comment
 */
public void kYZ3WireButton3_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	fireInputUpdate();
	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		Series300ConfigPanel aSeries300ConfigPanel;
		aSeries300ConfigPanel = new Series300ConfigPanel();
		frame.getContentPane().add("Center", aSeries300ConfigPanel);
		frame.setSize(aSeries300ConfigPanel.getSize());
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * Comment
 */
public void minMaxModeButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	fireInputUpdate();
	return;
}
/**
 * Comment
 */
public void nameTextField_CaretUpdate(javax.swing.event.CaretEvent caretEvent) {
	fireInputUpdate();
	return;
}
/**
 * Comment
 */
public void peakModeButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	fireInputUpdate();
	return;
}
/**
 * Comment
 */
public void recalculateJButton1_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	getRecalculateJButton1().setVisible(false);
	getEqualsJButton1().setVisible(true);
	getMpTextField().setVisible(true);
	getMpLabel().setVisible(true);
	getKhLabel().setVisible(true);
	getKhTextField().setVisible(true);
	getTimesLabel().setVisible(true);
	return;
}
/**
 * Comment
 */
public void recalculateJButton2_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	getRecalculateJButton2().setVisible(false);
	getEqualsJButton2().setVisible(true);
	getMpTextField2().setVisible(true);
	getMpLabel2().setVisible(true);
	getKhLabel2().setVisible(true);
	getKhTextField2().setVisible(true);
	getTimesLabel2().setVisible(true);
	return;
}
/**
 * Comment
 */
public void recalculateJButton3_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	getRecalculateJButton3().setVisible(false);
	getEqualsJButton3().setVisible(true);
	getMpTextField3().setVisible(true);
	getMpLabel3().setVisible(true);
	getKhLabel3().setVisible(true);
	getKhTextField3().setVisible(true);
	getTimesLabel3().setVisible(true);
	return;
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val ) {
	ConfigTwoWay conMan;
	
	if( val != null )
		conMan = (ConfigTwoWay)val;
	else
		conMan = new ConfigTwoWay();
	
	String name = conMan.getConfigName();
	if( name != null )
	{
		getNameTextField().setText(name);
	}
	
	String mode = conMan.getConfigMode();
	if( mode != null)
	{
		if(mode.compareTo(ConfigTwoWay.MODE_MINMAX) == 0)
			getMinMaxModeButton().setSelected(true);
		else
			getPeakModeButton().setSelected(true);
	}
	
	//channel 1
	Integer temp = conMan.getMCTWire1();
	if( temp != null )
	{
		if(temp.compareTo(ConfigTwoWay.TWOWIRE) == 0)
			getKY2WireButton().setSelected(true);
		else
			getKYZ3WireButton().setSelected(true);
		temp = null;
	}		
	
	Double temp2 = conMan.getKe1();
	if( temp2 != null )
	{
		getKeTextField().setText( temp2.toString() );
		getRecalculateJButton1().setVisible(true);
		getMpTextField().setVisible(false);
		getMpLabel().setVisible(false);
		getKhLabel().setVisible(false);
		getKhTextField().setVisible(false);
		getTimesLabel().setVisible(false);
		getEqualsJButton1().setVisible(false);
	}
	
	//channel 2
	temp = conMan.getMCTWire2();
	if(! (temp == null || temp.compareTo(ConfigTwoWay.NONVALUE) == 0) )
	{
		if(temp.compareTo(ConfigTwoWay.TWOWIRE) == 0)
			getKY2WireButton2().setSelected(true);
		else
			getKYZ3WireButton2().setSelected(true);
		temp = null;
	}		
	
	temp2 = conMan.getKe2();
	if(! (temp2 == null || temp2.compareTo(ConfigTwoWay.NOVALUE) == 0) )
	{
		getKeTextField2().setText( temp2.toString() );
		getChannel2EnableJCheckBox().setSelected(true);
		getKY2WireButton2().setEnabled(true);
		getKYZ3WireButton2().setEnabled(true);
		getRecalculateJButton2().setEnabled(true);
		getKeTextField2().setEnabled(true);
		getMpTextField2().setEnabled(true);
		getKhTextField2().setEnabled(true);
		getEqualsJButton2().setEnabled(true);
		getKY2WireButton2().setEnabled(true);
		getKYZ3WireButton2().setEnabled(true);
		
		getRecalculateJButton2().setVisible(true);
		getMpTextField2().setVisible(false);
		getMpLabel2().setVisible(false);
		getKhLabel2().setVisible(false);
		getKhTextField2().setVisible(false);
		getTimesLabel2().setVisible(false);
		getEqualsJButton2().setVisible(false);
		
	}
	  
	//channel 3
	temp = conMan.getMCTWire3();
	if(! (temp == null || temp.compareTo(ConfigTwoWay.NONVALUE) == 0) )
	{
		if(temp.compareTo(ConfigTwoWay.TWOWIRE) == 0)
			  getKY2WireButton3().setSelected(true);
		else
			  getKYZ3WireButton3().setSelected(true);
		temp = null;
    }		
	
	temp2 = conMan.getKe3();
	if(! (temp2 == null || temp2.compareTo(ConfigTwoWay.NOVALUE) == 0) )
	{
		getKeTextField3().setText( temp2.toString() );
		getChannel3EnableJCheckBox1().setSelected(true);
		getKeTextField3().setEnabled(true);
		getMpTextField3().setEnabled(true);
		getKhTextField3().setEnabled(true);
		getEqualsJButton3().setEnabled(true);
		getKY2WireButton3().setEnabled(true);
		getKYZ3WireButton3().setEnabled(true);
		getRecalculateJButton3().setEnabled(true);
		
		getMpTextField3().setVisible(false);
		getMpLabel3().setVisible(false);
		getKhLabel3().setVisible(false);
		getKhTextField3().setVisible(false);
		getTimesLabel3().setVisible(false);
		getEqualsJButton3().setVisible(false);
		getRecalculateJButton3().setVisible(true);

	}
	
	return;
}

public void setFirstFocus() 
{
    // Make sure that when its time to display this panel, the focus starts in the top component
    javax.swing.SwingUtilities.invokeLater( new Runnable() 
        { 
        public void run() 
            { 
            getNameTextField().requestFocus(); 
        } 
    });    
}

}
