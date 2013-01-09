package com.cannontech.dbeditor.wizard.device.lmgroup;

import javax.swing.JComboBox;

import com.cannontech.common.util.SwingUtil;
import com.cannontech.database.data.device.lm.LMGroupGolay;
/**
 * Insert the type's description here.
 * Creation date: (2/19/2004 4:54:55 PM)
 * @author: jdayton
 */
public class GolayEditorPanel extends com.cannontech.common.gui.util.DataInputPanel {
	private javax.swing.JPanel ivjAddressPanel = null;
	private javax.swing.JPanel ivjTimeoutPanel = null;
	private javax.swing.JLabel ivjHyphen1 = null;
	private javax.swing.JLabel ivjHyphen11 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JLabel ivjOpAddressJLabel = null;
	private javax.swing.JTextField ivjOpAddressJTextField1 = null;
	private javax.swing.JTextField ivjOpAddressJTextField2 = null;
	private javax.swing.JTextField ivjOpAddressJTextField3 = null;
	private javax.swing.JLabel ivjNominalTimeoutJLabel = null;
	private JComboBox<String> ivjJComboBoxNominalTimeout = null;

class IvjEventHandler implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == GolayEditorPanel.this.getJComboBoxNominalTimeout()) 
				connEtoC5(e);
		};
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == GolayEditorPanel.this.getOpAddressJTextField1()) 
				connEtoC1(e);
			if (e.getSource() == GolayEditorPanel.this.getOpAddressJTextField2()) 
				connEtoC2();
			if (e.getSource() == GolayEditorPanel.this.getOpAddressJTextField3()) 
				connEtoC3(e);
		};
	};

/**
 * GolayEditorPanel constructor comment.
 */
public GolayEditorPanel() {
	super();
	initialize();
}
/**
 * connEtoC1:  (JTextField1.caret.caretUpdate(javax.swing.event.CaretEvent) --> GolayEditorPanel.fireInputUpdate()V)
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
 * connEtoC2:  (JTextField11.caret. --> GolayEditorPanel.fireInputUpdate()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2() {
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
 * connEtoC3:  (JTextField12.caret.caretUpdate(javax.swing.event.CaretEvent) --> GolayEditorPanel.fireInputUpdate()V)
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
 * connEtoC5:  (JCheckBoxRelay1.action.actionPerformed(java.awt.event.ActionEvent) --> GolayEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
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
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Addressing");
			ivjAddressPanel = new javax.swing.JPanel();
			ivjAddressPanel.setName("AddressPanel");
			ivjAddressPanel.setPreferredSize(new java.awt.Dimension(346, 160));
			ivjAddressPanel.setBorder(ivjLocalBorder);
			ivjAddressPanel.setLayout(new java.awt.GridBagLayout());
			ivjAddressPanel.setMinimumSize(new java.awt.Dimension(346, 160));

			java.awt.GridBagConstraints constraintsOpAddressJLabel = new java.awt.GridBagConstraints();
			constraintsOpAddressJLabel.gridx = 1; constraintsOpAddressJLabel.gridy = 1;
			constraintsOpAddressJLabel.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsOpAddressJLabel.ipadx = 9;
			constraintsOpAddressJLabel.insets = new java.awt.Insets(85, 35, 63, 4);
			getAddressPanel().add(getOpAddressJLabel(), constraintsOpAddressJLabel);

			java.awt.GridBagConstraints constraintsOpAddressJTextField1 = new java.awt.GridBagConstraints();
			constraintsOpAddressJTextField1.gridx = 2; constraintsOpAddressJTextField1.gridy = 1;
			constraintsOpAddressJTextField1.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsOpAddressJTextField1.weightx = 1.0;
			constraintsOpAddressJTextField1.ipadx = 20;
			constraintsOpAddressJTextField1.insets = new java.awt.Insets(81, 5, 61, 1);
			getAddressPanel().add(getOpAddressJTextField1(), constraintsOpAddressJTextField1);

			java.awt.GridBagConstraints constraintsOpAddressJTextField2 = new java.awt.GridBagConstraints();
			constraintsOpAddressJTextField2.gridx = 4; constraintsOpAddressJTextField2.gridy = 1;
			constraintsOpAddressJTextField2.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsOpAddressJTextField2.weightx = 1.0;
			constraintsOpAddressJTextField2.ipadx = 20;
			constraintsOpAddressJTextField2.insets = new java.awt.Insets(81, 1, 61, 1);
			getAddressPanel().add(getOpAddressJTextField2(), constraintsOpAddressJTextField2);

			java.awt.GridBagConstraints constraintsOpAddressJTextField3 = new java.awt.GridBagConstraints();
			constraintsOpAddressJTextField3.gridx = 6; constraintsOpAddressJTextField3.gridy = 1;
			constraintsOpAddressJTextField3.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsOpAddressJTextField3.weightx = 1.0;
			constraintsOpAddressJTextField3.ipadx = 20;
			constraintsOpAddressJTextField3.insets = new java.awt.Insets(81, 1, 61, 82);
			getAddressPanel().add(getOpAddressJTextField3(), constraintsOpAddressJTextField3);

			java.awt.GridBagConstraints constraintsHyphen11 = new java.awt.GridBagConstraints();
			constraintsHyphen11.gridx = 5; constraintsHyphen11.gridy = 1;
			constraintsHyphen11.insets = new java.awt.Insets(82, 1, 66, 1);
			getAddressPanel().add(getHyphen11(), constraintsHyphen11);

			java.awt.GridBagConstraints constraintsHyphen1 = new java.awt.GridBagConstraints();
			constraintsHyphen1.gridx = 3; constraintsHyphen1.gridy = 1;
			constraintsHyphen1.insets = new java.awt.Insets(82, 1, 66, 1);
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
 * Return the Hyphen11 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getHyphen11() {
	if (ivjHyphen11 == null) {
		try {
			ivjHyphen11 = new javax.swing.JLabel();
			ivjHyphen11.setName("Hyphen11");
			ivjHyphen11.setText("-");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjHyphen11;
}
/**
 * Return the NominalTimeoutTextField property value.
 * @return javax.swing.JComboBox
 */
private JComboBox<String> getJComboBoxNominalTimeout() {
	if (ivjJComboBoxNominalTimeout == null) {
		try {
			ivjJComboBoxNominalTimeout = new JComboBox<>();
			ivjJComboBoxNominalTimeout.setName("JComboBoxNominalTimeout");
			// user code begin {1}
			ivjJComboBoxNominalTimeout.addItem("7.5 minutes");
			ivjJComboBoxNominalTimeout.addItem("15 minutes");
			ivjJComboBoxNominalTimeout.addItem("30 minutes");
			ivjJComboBoxNominalTimeout.addItem("60 minutes");
			ivjJComboBoxNominalTimeout.addItem("2 hours");
			ivjJComboBoxNominalTimeout.addItem("4 hours");
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxNominalTimeout;
}
/**
 * Return the NominalTimeoutJComboBox property value.
 * @return javax.swing.JComboBox
 */

/**
 * Return the NominalTimeoutJLabel property value.
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
 * Return the JLabel1 property value.
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
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getOpAddressJTextField1() {
	if (ivjOpAddressJTextField1 == null) {
		try {
			ivjOpAddressJTextField1 = new javax.swing.JTextField();
			ivjOpAddressJTextField1.setName("OpAddressJTextField1");
			// user code begin {1}
			ivjOpAddressJTextField1.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(0, 99) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOpAddressJTextField1;
}
/**
 * Return the JTextField11 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getOpAddressJTextField2() {
	if (ivjOpAddressJTextField2 == null) {
		try {
			ivjOpAddressJTextField2 = new javax.swing.JTextField();
			ivjOpAddressJTextField2.setName("OpAddressJTextField2");
			// user code begin {1}
			ivjOpAddressJTextField2.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(0, 99) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOpAddressJTextField2;
}
/**
 * Return the JTextField12 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getOpAddressJTextField3() {
	if (ivjOpAddressJTextField3 == null) {
		try {
			ivjOpAddressJTextField3 = new javax.swing.JTextField();
			ivjOpAddressJTextField3.setName("OpAddressJTextField3");
			// user code begin {1}
			ivjOpAddressJTextField3.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(0, 99) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOpAddressJTextField3;
}
/**
 * Return the TimeoutPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getTimeoutPanel() {
	if (ivjTimeoutPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder1.setTitle("Timing");
			ivjTimeoutPanel = new javax.swing.JPanel();
			ivjTimeoutPanel.setName("TimeoutPanel");
			ivjTimeoutPanel.setPreferredSize(new java.awt.Dimension(346, 196));
			ivjTimeoutPanel.setBorder(ivjLocalBorder1);
			ivjTimeoutPanel.setLayout(new java.awt.GridBagLayout());
			ivjTimeoutPanel.setMinimumSize(new java.awt.Dimension(346, 196));

			java.awt.GridBagConstraints constraintsJComboBoxNominalTimeout = new java.awt.GridBagConstraints();
			constraintsJComboBoxNominalTimeout.gridx = 2; constraintsJComboBoxNominalTimeout.gridy = 1;
			constraintsJComboBoxNominalTimeout.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxNominalTimeout.weightx = 1.0;
			constraintsJComboBoxNominalTimeout.ipadx = -26;
			constraintsJComboBoxNominalTimeout.insets = new java.awt.Insets(69, 2, 104, 89);
			getTimeoutPanel().add(getJComboBoxNominalTimeout(), constraintsJComboBoxNominalTimeout);

			java.awt.GridBagConstraints constraintsNominalTimeoutJLabel = new java.awt.GridBagConstraints();
			constraintsNominalTimeoutJLabel.gridx = 1; constraintsNominalTimeoutJLabel.gridy = 1;
			constraintsNominalTimeoutJLabel.ipadx = 6;
			constraintsNominalTimeoutJLabel.insets = new java.awt.Insets(75, 45, 107, 2);
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
public Object getValue(Object o) {
	
	LMGroupGolay golay = null;
	
	if( o instanceof com.cannontech.database.data.multi.MultiDBPersistent )
	{
		golay = (LMGroupGolay)
				com.cannontech.database.data.multi.MultiDBPersistent.getFirstObjectOfType(
				LMGroupGolay.class,
				(com.cannontech.database.data.multi.MultiDBPersistent)o );
	}
	else if( o instanceof com.cannontech.database.data.multi.SmartMultiDBPersistent )
		golay = (LMGroupGolay)
				((com.cannontech.database.data.multi.SmartMultiDBPersistent)o).getOwnerDBPersistent();
	
	
	if( o instanceof LMGroupGolay || golay != null )
	{
		if( golay == null )
			golay = (LMGroupGolay) o;
		
		//some annoying checks to verify that the user hasn't messed up the six digit address
		StringBuffer opAddress = new StringBuffer();
		if(getOpAddressJTextField1().getText().length() < 2)
			opAddress.append( (getOpAddressJTextField1().getText().length()==0 ? "00" : "0") );
		opAddress.append(getOpAddressJTextField1().getText());

		if(getOpAddressJTextField2().getText().length() < 2)
			opAddress.append( (getOpAddressJTextField2().getText().length()==0 ? "00" : "0") );
		opAddress.append(getOpAddressJTextField2().getText());

		if(getOpAddressJTextField3().getText().length() < 2)
			opAddress.append( (getOpAddressJTextField3().getText().length()==0 ? "00" : "0") );
		opAddress.append(getOpAddressJTextField3().getText());

		golay.getLMGroupSASimple().setOperationalAddress(opAddress.toString());			
		golay.getLMGroupSASimple().setNominalTimeout(com.cannontech.common.util.CtiUtilities.getIntervalSecondsValueFromDecimal((String)getJComboBoxNominalTimeout().getSelectedItem()));
						
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
	getOpAddressJTextField1().addCaretListener(ivjEventHandler);
	getOpAddressJTextField2().addCaretListener(ivjEventHandler);
	getOpAddressJTextField3().addCaretListener(ivjEventHandler);
	getJComboBoxNominalTimeout().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("GolayEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(350, 360);

		java.awt.GridBagConstraints constraintsAddressPanel = new java.awt.GridBagConstraints();
		constraintsAddressPanel.gridx = 1; constraintsAddressPanel.gridy = 1;
		constraintsAddressPanel.fill = java.awt.GridBagConstraints.NONE;
		constraintsAddressPanel.weightx = 1.0;
		constraintsAddressPanel.weighty = 1.0;
		constraintsAddressPanel.insets = new java.awt.Insets(1, 2, 1, 2);
		add(getAddressPanel(), constraintsAddressPanel);

		java.awt.GridBagConstraints constraintsTimeoutPanel = new java.awt.GridBagConstraints();
		constraintsTimeoutPanel.gridx = 1; constraintsTimeoutPanel.gridy = 2;
		constraintsTimeoutPanel.fill = java.awt.GridBagConstraints.NONE;
		constraintsTimeoutPanel.weightx = 1.0;
		constraintsTimeoutPanel.weighty = 1.0;
		constraintsTimeoutPanel.ipadx = -1;
		constraintsTimeoutPanel.insets = new java.awt.Insets(1, 2, 1, 3);
		add(getTimeoutPanel(), constraintsTimeoutPanel);
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
		GolayEditorPanel aGolayEditorPanel;
		aGolayEditorPanel = new GolayEditorPanel();
		frame.setContentPane(aGolayEditorPanel);
		frame.setSize(aGolayEditorPanel.getSize());
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
	
	if(o instanceof LMGroupGolay)
	{
		LMGroupGolay golay = (LMGroupGolay) o;
		
		StringBuffer address = new StringBuffer(golay.getLMGroupSASimple().getOperationalAddress());
		if(address.length() < 6)
		{
			address.reverse();
			for(int j = 6 - address.length(); j > 0; j--)
			{
				address.append("0");
			}
			address.reverse();
		}
		getOpAddressJTextField1().setText(address.substring(0,2));
		getOpAddressJTextField2().setText(address.substring(2,4));
		getOpAddressJTextField3().setText(address.substring(4,6));

        SwingUtil.setIntervalComboBoxSelectedItem(getJComboBoxNominalTimeout(),
            golay.getLMGroupSASimple().getNominalTimeout().intValue());
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
