package com.cannontech.dbeditor.wizard.device.lmgroup;

import javax.swing.JComboBox;

import com.cannontech.common.util.SwingUtil;
import com.cannontech.database.data.device.lm.LMGroupSADigital;
/**
 * Insert the type's description here.
 * Creation date: (2/25/2004 10:52:28 AM)
 * @author: 
 */
public class SADigitalEditorPanel extends com.cannontech.common.gui.util.DataInputPanel {
	private javax.swing.JPanel ivjAddressPanel = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JLabel ivjHyphen1 = null;
	private javax.swing.JTextField ivjOpAddress1JTextField = null;
	private javax.swing.JTextField ivjOpAddress2JTextField = null;
	private javax.swing.JLabel ivjOpAddressJLabel = null;
	private javax.swing.JPanel ivjTimeoutPanel = null;
	private javax.swing.JLabel ivjNominalTimeoutJLabel = null;
	private javax.swing.JLabel ivjJLabelMarkIndex = null;
	private javax.swing.JLabel ivjJLabelSpaceIndex = null;
	private javax.swing.JPanel ivjJPanelIndexing = null;
	private javax.swing.JTextField ivjJTextFieldMarkIndex = null;
	private javax.swing.JTextField ivjJTextFieldSpaceIndex = null;

class IvjEventHandler implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == SADigitalEditorPanel.this.getNominalTimeoutJComboBox()) 
				connEtoC3(e);
		};
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == SADigitalEditorPanel.this.getOpAddress1JTextField()) 
				connEtoC1(e);
			if (e.getSource() == SADigitalEditorPanel.this.getOpAddress2JTextField()) 
				connEtoC2(e);
			if (e.getSource() == SADigitalEditorPanel.this.getJTextFieldMarkIndex()) 
				connEtoC4(e);
			if (e.getSource() == SADigitalEditorPanel.this.getJTextFieldSpaceIndex()) 
				connEtoC5(e);
		};
	};
	private JComboBox<String> ivjNominalTimeoutJComboBox = null;
/**
 * SADigitalEditorPanel constructor comment.
 */
public SADigitalEditorPanel() {
	super();
	initialize();
}
/**
 * connEtoC1:  (OpAddress1JTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> SADigitalEditorPanel.fireInputUpdate()V)
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
 * connEtoC2:  (OpAddress2JTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> SADigitalEditorPanel.fireInputUpdate()V)
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
 * connEtoC3:  (TimeoutJComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> SADigitalEditorPanel.fireInputUpdate()V)
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
 * connEtoC4:  (JTextFieldMarkIndex.caret.caretUpdate(javax.swing.event.CaretEvent) --> SADigitalEditorPanel.fireInputUpdate()V)
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
 * connEtoC5:  (JTextFieldSpaceIndex.caret.caretUpdate(javax.swing.event.CaretEvent) --> SADigitalEditorPanel.fireInputUpdate()V)
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
 * Return the AddressPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getAddressPanel() {
	if (ivjAddressPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder1.setTitle("Addressing");
			ivjAddressPanel = new javax.swing.JPanel();
			ivjAddressPanel.setName("AddressPanel");
			ivjAddressPanel.setPreferredSize(new java.awt.Dimension(344, 154));
			ivjAddressPanel.setBorder(ivjLocalBorder1);
			ivjAddressPanel.setLayout(new java.awt.GridBagLayout());
			ivjAddressPanel.setMinimumSize(new java.awt.Dimension(344, 154));

			java.awt.GridBagConstraints constraintsOpAddressJLabel = new java.awt.GridBagConstraints();
			constraintsOpAddressJLabel.gridx = 1; constraintsOpAddressJLabel.gridy = 1;
			constraintsOpAddressJLabel.ipadx = 9;
			constraintsOpAddressJLabel.insets = new java.awt.Insets(72, 49, 68, 4);
			getAddressPanel().add(getOpAddressJLabel(), constraintsOpAddressJLabel);

			java.awt.GridBagConstraints constraintsOpAddress1JTextField = new java.awt.GridBagConstraints();
			constraintsOpAddress1JTextField.gridx = 2; constraintsOpAddress1JTextField.gridy = 1;
			constraintsOpAddress1JTextField.weightx = 1.0;
			constraintsOpAddress1JTextField.ipadx = 25;
			constraintsOpAddress1JTextField.insets = new java.awt.Insets(68, 5, 66, 2);
			getAddressPanel().add(getOpAddress1JTextField(), constraintsOpAddress1JTextField);

			java.awt.GridBagConstraints constraintsOpAddress2JTextField = new java.awt.GridBagConstraints();
			constraintsOpAddress2JTextField.gridx = 4; constraintsOpAddress2JTextField.gridy = 1;
			constraintsOpAddress2JTextField.weightx = 1.0;
			constraintsOpAddress2JTextField.ipadx = 25;
			constraintsOpAddress2JTextField.insets = new java.awt.Insets(68, 2, 66, 85);
			getAddressPanel().add(getOpAddress2JTextField(), constraintsOpAddress2JTextField);

			java.awt.GridBagConstraints constraintsHyphen1 = new java.awt.GridBagConstraints();
			constraintsHyphen1.gridx = 3; constraintsHyphen1.gridy = 1;
			constraintsHyphen1.insets = new java.awt.Insets(69, 2, 71, 1);
			getAddressPanel().add(getHyphen1(), constraintsHyphen1);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddressPanel;
}

/**
 * Return the Hyphen1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getHyphen1() {
	if (ivjHyphen1 == null) {
		try {
			ivjHyphen1 = new javax.swing.JLabel();
			ivjHyphen1.setName("Hyphen1");
			ivjHyphen1.setText("-");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjHyphen1;
}
/**
 * Return the JLabelMarkIndex property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMarkIndex() {
	if (ivjJLabelMarkIndex == null) {
		try {
			ivjJLabelMarkIndex = new javax.swing.JLabel();
			ivjJLabelMarkIndex.setName("JLabelMarkIndex");
			ivjJLabelMarkIndex.setText("Mark Index: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMarkIndex;
}
/**
 * Return the JLabelSpaceIndex property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelSpaceIndex() {
	if (ivjJLabelSpaceIndex == null) {
		try {
			ivjJLabelSpaceIndex = new javax.swing.JLabel();
			ivjJLabelSpaceIndex.setName("JLabelSpaceIndex");
			ivjJLabelSpaceIndex.setText("Space Index: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelSpaceIndex;
}
/**
 * Return the JPanelIndexing property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelIndexing() {
	if (ivjJPanelIndexing == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder2;
			ivjLocalBorder2 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder2.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder2.setTitle("Indexing");
			ivjJPanelIndexing = new javax.swing.JPanel();
			ivjJPanelIndexing.setName("JPanelIndexing");
			ivjJPanelIndexing.setBorder(ivjLocalBorder2);
			ivjJPanelIndexing.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJTextFieldMarkIndex = new java.awt.GridBagConstraints();
			constraintsJTextFieldMarkIndex.gridx = 2; constraintsJTextFieldMarkIndex.gridy = 1;
			constraintsJTextFieldMarkIndex.weightx = 1.0;
			constraintsJTextFieldMarkIndex.insets = new java.awt.Insets(34, 8, 3, 145);
			getJPanelIndexing().add(getJTextFieldMarkIndex(), constraintsJTextFieldMarkIndex);

			java.awt.GridBagConstraints constraintsJTextFieldSpaceIndex = new java.awt.GridBagConstraints();
			constraintsJTextFieldSpaceIndex.gridx = 2; constraintsJTextFieldSpaceIndex.gridy = 2;
			constraintsJTextFieldSpaceIndex.weightx = 1.0;
			constraintsJTextFieldSpaceIndex.insets = new java.awt.Insets(4, 8, 30, 145);
			getJPanelIndexing().add(getJTextFieldSpaceIndex(), constraintsJTextFieldSpaceIndex);

			java.awt.GridBagConstraints constraintsJLabelMarkIndex = new java.awt.GridBagConstraints();
			constraintsJLabelMarkIndex.gridx = 1; constraintsJLabelMarkIndex.gridy = 1;
			constraintsJLabelMarkIndex.ipadx = 19;
			constraintsJLabelMarkIndex.insets = new java.awt.Insets(37, 54, 6, 8);
			getJPanelIndexing().add(getJLabelMarkIndex(), constraintsJLabelMarkIndex);

			java.awt.GridBagConstraints constraintsJLabelSpaceIndex = new java.awt.GridBagConstraints();
			constraintsJLabelSpaceIndex.gridx = 1; constraintsJLabelSpaceIndex.gridy = 2;
			constraintsJLabelSpaceIndex.ipadx = 12;
			constraintsJLabelSpaceIndex.insets = new java.awt.Insets(8, 54, 32, 8);
			getJPanelIndexing().add(getJLabelSpaceIndex(), constraintsJLabelSpaceIndex);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelIndexing;
}
/**
 * Return the JTextFieldMarkIndex property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldMarkIndex() {
	if (ivjJTextFieldMarkIndex == null) {
		try {
			ivjJTextFieldMarkIndex = new javax.swing.JTextField();
			ivjJTextFieldMarkIndex.setName("JTextFieldMarkIndex");
			ivjJTextFieldMarkIndex.setPreferredSize(new java.awt.Dimension(39, 20));
			ivjJTextFieldMarkIndex.setMinimumSize(new java.awt.Dimension(39, 20));
			// user code begin {1}
			ivjJTextFieldMarkIndex.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(0, 9999) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldMarkIndex;
}
/**
 * Return the JTextFieldSpaceIndex property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldSpaceIndex() {
	if (ivjJTextFieldSpaceIndex == null) {
		try {
			ivjJTextFieldSpaceIndex = new javax.swing.JTextField();
			ivjJTextFieldSpaceIndex.setName("JTextFieldSpaceIndex");
			ivjJTextFieldSpaceIndex.setPreferredSize(new java.awt.Dimension(39, 20));
			ivjJTextFieldSpaceIndex.setMinimumSize(new java.awt.Dimension(39, 20));
			// user code begin {1}
			ivjJTextFieldSpaceIndex.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(0, 9999) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldSpaceIndex;
}
/**
 * Return the TimeoutJComboBox property value.
 * @return javax.swing.JComboBox
 */
private JComboBox<String> getNominalTimeoutJComboBox() {
	if (ivjNominalTimeoutJComboBox == null) {
		try {
			ivjNominalTimeoutJComboBox = new JComboBox<>();
			ivjNominalTimeoutJComboBox.setName("NominalTimeoutJComboBox");
			ivjNominalTimeoutJComboBox.setPreferredSize(new java.awt.Dimension(109, 23));
			ivjNominalTimeoutJComboBox.setMinimumSize(new java.awt.Dimension(109, 23));
			// user code begin {1}
			ivjNominalTimeoutJComboBox.addItem("7.5 minutes");
			ivjNominalTimeoutJComboBox.addItem("15 minutes");
			ivjNominalTimeoutJComboBox.addItem("30 minutes");
			ivjNominalTimeoutJComboBox.addItem("60 minutes");
			ivjNominalTimeoutJComboBox.addItem("2 hours");
			ivjNominalTimeoutJComboBox.addItem("4 hours");
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNominalTimeoutJComboBox;
}
/**
 * Return the TimeoutJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getNominalTimeoutJLabel() {
	if (ivjNominalTimeoutJLabel == null) {
		try {
			ivjNominalTimeoutJLabel = new javax.swing.JLabel();
			ivjNominalTimeoutJLabel.setName("NominalTimeoutJLabel");
			ivjNominalTimeoutJLabel.setText("Nominal Timeout: ");
			ivjNominalTimeoutJLabel.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
			ivjNominalTimeoutJLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNominalTimeoutJLabel;
}
/**
 * Return the OpAddress1JTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getOpAddress1JTextField() {
	if (ivjOpAddress1JTextField == null) {
		try {
			ivjOpAddress1JTextField = new javax.swing.JTextField();
			ivjOpAddress1JTextField.setName("OpAddress1JTextField");
			ivjOpAddress1JTextField.setPreferredSize(new java.awt.Dimension(29, 20));
			// user code begin {1}
			ivjOpAddress1JTextField.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(0, 99) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOpAddress1JTextField;
}
/**
 * Return the OpAddress2JTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getOpAddress2JTextField() {
	if (ivjOpAddress2JTextField == null) {
		try {
			ivjOpAddress2JTextField = new javax.swing.JTextField();
			ivjOpAddress2JTextField.setName("OpAddress2JTextField");
			ivjOpAddress2JTextField.setPreferredSize(new java.awt.Dimension(29, 20));
			// user code begin {1}
			ivjOpAddress2JTextField.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(0, 9) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOpAddress2JTextField;
}
/**
 * Return the OpAddressJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getOpAddressJLabel() {
	if (ivjOpAddressJLabel == null) {
		try {
			ivjOpAddressJLabel = new javax.swing.JLabel();
			ivjOpAddressJLabel.setName("OpAddressJLabel");
			ivjOpAddressJLabel.setText("Operational Address: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOpAddressJLabel;
}
/**
 * Return the TimeoutPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getTimeoutPanel() {
	if (ivjTimeoutPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Timing");
			ivjTimeoutPanel = new javax.swing.JPanel();
			ivjTimeoutPanel.setName("TimeoutPanel");
			ivjTimeoutPanel.setPreferredSize(new java.awt.Dimension(342, 177));
			ivjTimeoutPanel.setBorder(ivjLocalBorder);
			ivjTimeoutPanel.setLayout(new java.awt.GridBagLayout());
			ivjTimeoutPanel.setMinimumSize(new java.awt.Dimension(342, 177));

			java.awt.GridBagConstraints constraintsNominalTimeoutJComboBox = new java.awt.GridBagConstraints();
			constraintsNominalTimeoutJComboBox.gridx = 2; constraintsNominalTimeoutJComboBox.gridy = 1;
			constraintsNominalTimeoutJComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsNominalTimeoutJComboBox.weightx = 1.0;
			constraintsNominalTimeoutJComboBox.insets = new java.awt.Insets(40, 3, 38, 75);
			getTimeoutPanel().add(getNominalTimeoutJComboBox(), constraintsNominalTimeoutJComboBox);

			java.awt.GridBagConstraints constraintsNominalTimeoutJLabel = new java.awt.GridBagConstraints();
			constraintsNominalTimeoutJLabel.gridx = 1; constraintsNominalTimeoutJLabel.gridy = 1;
			constraintsNominalTimeoutJLabel.ipadx = 5;
			constraintsNominalTimeoutJLabel.insets = new java.awt.Insets(46, 47, 41, 2);
			getTimeoutPanel().add(getNominalTimeoutJLabel(), constraintsNominalTimeoutJLabel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTimeoutPanel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object o) 
{
	
	LMGroupSADigital digital = null;
	
	if( o instanceof com.cannontech.database.data.multi.MultiDBPersistent )
	{
		digital = (LMGroupSADigital)
				com.cannontech.database.data.multi.MultiDBPersistent.getFirstObjectOfType(
				LMGroupSADigital.class,
				(com.cannontech.database.data.multi.MultiDBPersistent)o );
	}
	else if( o instanceof com.cannontech.database.data.multi.SmartMultiDBPersistent )
		digital = (LMGroupSADigital)
				((com.cannontech.database.data.multi.SmartMultiDBPersistent)o).getOwnerDBPersistent();
	
	
	if( o instanceof LMGroupSADigital || digital != null )
	{
		if( digital == null )
			digital = (LMGroupSADigital) o;
		
		//some annoying but necessary verification of the address string
		StringBuffer opAddress = new StringBuffer();
		if(getOpAddress1JTextField().getText().length() < 2)
			opAddress.append("0");	
		opAddress.append(getOpAddress1JTextField().getText());
		opAddress.append("-");
		opAddress.append(getOpAddress2JTextField().getText());
		digital.getLMGroupSASimple().setOperationalAddress(opAddress.toString());
			
		digital.getLMGroupSASimple().setNominalTimeout(com.cannontech.common.util.CtiUtilities.getIntervalSecondsValueFromDecimal((String)getNominalTimeoutJComboBox().getSelectedItem()));
		
		if(getJTextFieldMarkIndex().getText().compareTo("") != 0)
			digital.getLMGroupSASimple().setMarkIndex(new Integer(getJTextFieldMarkIndex().getText()));
			
		if(getJTextFieldSpaceIndex().getText().compareTo("") != 0)
			digital.getLMGroupSASimple().setSpaceIndex(new Integer(getJTextFieldSpaceIndex().getText()));
			
	}
	return o;
}

/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getOpAddress1JTextField().addCaretListener(ivjEventHandler);
	getOpAddress2JTextField().addCaretListener(ivjEventHandler);
	getNominalTimeoutJComboBox().addActionListener(ivjEventHandler);
	getJTextFieldMarkIndex().addCaretListener(ivjEventHandler);
	getJTextFieldSpaceIndex().addCaretListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("SADigitalEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(350, 360);

		java.awt.GridBagConstraints constraintsTimeoutPanel = new java.awt.GridBagConstraints();
		constraintsTimeoutPanel.gridx = 1; constraintsTimeoutPanel.gridy = 3;
		constraintsTimeoutPanel.fill = java.awt.GridBagConstraints.NONE;
		constraintsTimeoutPanel.weightx = 1.0;
		constraintsTimeoutPanel.weighty = 1.0;
		constraintsTimeoutPanel.ipady = -76;
		constraintsTimeoutPanel.insets = new java.awt.Insets(4, 5, 20, 3);
		add(getTimeoutPanel(), constraintsTimeoutPanel);

		java.awt.GridBagConstraints constraintsAddressPanel = new java.awt.GridBagConstraints();
		constraintsAddressPanel.gridx = 1; constraintsAddressPanel.gridy = 1;
		constraintsAddressPanel.fill = java.awt.GridBagConstraints.NONE;
		constraintsAddressPanel.weightx = 1.0;
		constraintsAddressPanel.weighty = 1.0;
		constraintsAddressPanel.ipady = -32;
		constraintsAddressPanel.insets = new java.awt.Insets(3, 5, 3, 1);
		add(getAddressPanel(), constraintsAddressPanel);

		java.awt.GridBagConstraints constraintsJPanelIndexing = new java.awt.GridBagConstraints();
		constraintsJPanelIndexing.gridx = 1; constraintsJPanelIndexing.gridy = 2;
		constraintsJPanelIndexing.fill = java.awt.GridBagConstraints.NONE;
		constraintsJPanelIndexing.weightx = 1.0;
		constraintsJPanelIndexing.weighty = 1.0;
		constraintsJPanelIndexing.ipadx = -10;
		constraintsJPanelIndexing.ipady = -36;
		constraintsJPanelIndexing.insets = new java.awt.Insets(3, 5, 3, 3);
		add(getJPanelIndexing(), constraintsJPanelIndexing);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
public boolean isInputValid() 
{
	
	return true;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		SADigitalEditorPanel aSADigitalEditorPanel;
		aSADigitalEditorPanel = new SADigitalEditorPanel();
		frame.setContentPane(aSADigitalEditorPanel);
		frame.setSize(aSADigitalEditorPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		exception.printStackTrace(System.out);
	}
}
/**
 * This method was created in VisualAge.
 * @param o java.lang.Object
 */
public void setValue(Object o) {
	
	if(o instanceof LMGroupSADigital)
	{
		LMGroupSADigital digital = (LMGroupSADigital) o;
		
		StringBuffer address = new StringBuffer(digital.getLMGroupSASimple().getOperationalAddress());
		//check for opcodes that are short of digits; add a zero to the front if they are
		if(address.length() < 4)
		{
			address.reverse();
			for(int j = 4 - address.length(); j > 0; j--)
			{
				address.append("0");
			}
			address.reverse();
		}
		
		getOpAddress1JTextField().setText(address.substring(0,2));
		//skip that hyphen at position 2
		getOpAddress2JTextField().setText(address.substring(3,4));

		SwingUtil.setIntervalComboBoxSelectedItem( 
			getNominalTimeoutJComboBox(), digital.getLMGroupSASimple().getNominalTimeout().intValue() );
			
		if(digital.getLMGroupSASimple().getMarkIndex().intValue() != 0)
			getJTextFieldMarkIndex().setText(digital.getLMGroupSASimple().getMarkIndex().toString());
		
		if(digital.getLMGroupSASimple().getSpaceIndex().intValue() != 0)
			getJTextFieldSpaceIndex().setText(digital.getLMGroupSASimple().getSpaceIndex().toString());
				
	}
}

public void setFirstFocus() 
{
    // Make sure that when its time to display this panel, the focus starts in the top component
    javax.swing.SwingUtilities.invokeLater( new Runnable() 
        { 
        public void run() 
        { 
            getAddressPanel().requestFocus();
        } 
    });    
}

}
