package com.cannontech.dbeditor.editor.port;

import javax.swing.JComboBox;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SwingUtil;
import com.cannontech.database.data.port.DirectPort;
import com.cannontech.database.data.port.LocalDialupPortBase;
import com.cannontech.database.data.port.TerminalServerDialupPort;
import com.cannontech.database.db.port.PortDialupModem;
 
public class PortModemEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
	private javax.swing.JLabel ivjInitializationStringLabel = null;
	private javax.swing.JTextField ivjInitializationStringTextField = null;
	private JComboBox<String> ivjModemTypeComboBox = null;
	private javax.swing.JLabel ivjModemTypeLabel = null;
	private javax.swing.JLabel ivjPrefixNumberLabel = null;
	private javax.swing.JTextField ivjPrefixNumberTextField = null;
	private javax.swing.JLabel ivjSuffixNumberLabel = null;
	private javax.swing.JTextField ivjSuffixNumberTextField = null;
	private javax.swing.JPanel ivjDialingPropertiesPanel = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public PortModemEditorPanel() {
	super();
	initialize();
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
@Override
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getModemTypeComboBox()) 
		connEtoC1(e);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
@Override
public void caretUpdate(javax.swing.event.CaretEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getInitializationStringTextField()) 
		connEtoC2(e);
	if (e.getSource() == getPrefixNumberTextField()) 
		connEtoC3(e);
	if (e.getSource() == getSuffixNumberTextField()) 
		connEtoC4(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (ModemTypeComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> PortModemEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
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
 * connEtoC2:  (InitializationStringTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> PortModemEditorPanel.fireInputUpdate()V)
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
 * connEtoC3:  (PrefixNumberTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> PortModemEditorPanel.fireInputUpdate()V)
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
 * connEtoC4:  (SuffixNumberTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> PortModemEditorPanel.fireInputUpdate()V)
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
 * Return the DialingPropertiesPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getDialingPropertiesPanel() {
	if (ivjDialingPropertiesPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("dialog.bold", 1, 14));
			ivjLocalBorder.setTitle("Dialing Properties");
			ivjDialingPropertiesPanel = new javax.swing.JPanel();
			ivjDialingPropertiesPanel.setName("DialingPropertiesPanel");
			ivjDialingPropertiesPanel.setBorder(ivjLocalBorder);
			ivjDialingPropertiesPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsPrefixNumberLabel = new java.awt.GridBagConstraints();
			constraintsPrefixNumberLabel.gridx = 0; constraintsPrefixNumberLabel.gridy = 0;
			constraintsPrefixNumberLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getDialingPropertiesPanel().add(getPrefixNumberLabel(), constraintsPrefixNumberLabel);

			java.awt.GridBagConstraints constraintsPrefixNumberTextField = new java.awt.GridBagConstraints();
			constraintsPrefixNumberTextField.gridx = 1; constraintsPrefixNumberTextField.gridy = 0;
			constraintsPrefixNumberTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPrefixNumberTextField.weightx = 1.0;
			constraintsPrefixNumberTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getDialingPropertiesPanel().add(getPrefixNumberTextField(), constraintsPrefixNumberTextField);

			java.awt.GridBagConstraints constraintsSuffixNumberLabel = new java.awt.GridBagConstraints();
			constraintsSuffixNumberLabel.gridx = 0; constraintsSuffixNumberLabel.gridy = 1;
			constraintsSuffixNumberLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getDialingPropertiesPanel().add(getSuffixNumberLabel(), constraintsSuffixNumberLabel);

			java.awt.GridBagConstraints constraintsSuffixNumberTextField = new java.awt.GridBagConstraints();
			constraintsSuffixNumberTextField.gridx = 1; constraintsSuffixNumberTextField.gridy = 1;
			constraintsSuffixNumberTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsSuffixNumberTextField.weightx = 1.0;
			constraintsSuffixNumberTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getDialingPropertiesPanel().add(getSuffixNumberTextField(), constraintsSuffixNumberTextField);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDialingPropertiesPanel;
}
/**
 * Return the InitializationStringLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getInitializationStringLabel() {
	if (ivjInitializationStringLabel == null) {
		try {
			ivjInitializationStringLabel = new javax.swing.JLabel();
			ivjInitializationStringLabel.setName("InitializationStringLabel");
			ivjInitializationStringLabel.setText("Initialization String:");
			ivjInitializationStringLabel.setMaximumSize(new java.awt.Dimension(117, 16));
			ivjInitializationStringLabel.setPreferredSize(new java.awt.Dimension(117, 16));
			ivjInitializationStringLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjInitializationStringLabel.setMinimumSize(new java.awt.Dimension(117, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjInitializationStringLabel;
}
/**
 * Return the InitializationStringTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getInitializationStringTextField() {
	if (ivjInitializationStringTextField == null) {
		try {
			ivjInitializationStringTextField = new javax.swing.JTextField();
			ivjInitializationStringTextField.setName("InitializationStringTextField");
			ivjInitializationStringTextField.setMaximumSize(new java.awt.Dimension(2147483647, 20));
			ivjInitializationStringTextField.setColumns(25);
			ivjInitializationStringTextField.setPreferredSize(new java.awt.Dimension(275, 20));
			ivjInitializationStringTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjInitializationStringTextField.setMinimumSize(new java.awt.Dimension(275, 20));
			// user code begin {1}
			ivjInitializationStringTextField.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_INITIALIZATION_STRING_LENGTH));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjInitializationStringTextField;
}
/**
 * Return the ModemTypeComboBox property value.
 * @return javax.swing.JComboBox
 */
private JComboBox<String> getModemTypeComboBox() {
	if (ivjModemTypeComboBox == null) {
		try {
			ivjModemTypeComboBox = new JComboBox<>();
			ivjModemTypeComboBox.setName("ModemTypeComboBox");
			ivjModemTypeComboBox.setMaximumSize(new java.awt.Dimension(32767, 25));
			ivjModemTypeComboBox.setSelectedItem("U.S. Robotics Sportster");
			ivjModemTypeComboBox.setPreferredSize(new java.awt.Dimension(179, 25));
			ivjModemTypeComboBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjModemTypeComboBox.setMinimumSize(new java.awt.Dimension(175, 25));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjModemTypeComboBox;
}
/**
 * Return the ModemTypeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getModemTypeLabel() {
	if (ivjModemTypeLabel == null) {
		try {
			ivjModemTypeLabel = new javax.swing.JLabel();
			ivjModemTypeLabel.setName("ModemTypeLabel");
			ivjModemTypeLabel.setText("Modem Type:");
			ivjModemTypeLabel.setMaximumSize(new java.awt.Dimension(86, 16));
			ivjModemTypeLabel.setPreferredSize(new java.awt.Dimension(86, 16));
			ivjModemTypeLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjModemTypeLabel.setMinimumSize(new java.awt.Dimension(86, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjModemTypeLabel;
}
/**
 * Return the PrefixNumberLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPrefixNumberLabel() {
	if (ivjPrefixNumberLabel == null) {
		try {
			ivjPrefixNumberLabel = new javax.swing.JLabel();
			ivjPrefixNumberLabel.setName("PrefixNumberLabel");
			ivjPrefixNumberLabel.setText("Prefix Number:");
			ivjPrefixNumberLabel.setMaximumSize(new java.awt.Dimension(92, 16));
			ivjPrefixNumberLabel.setPreferredSize(new java.awt.Dimension(92, 16));
			ivjPrefixNumberLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPrefixNumberLabel.setMinimumSize(new java.awt.Dimension(92, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPrefixNumberLabel;
}
/**
 * Return the PrefixNumberTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getPrefixNumberTextField() {
	if (ivjPrefixNumberTextField == null) {
		try {
			ivjPrefixNumberTextField = new javax.swing.JTextField();
			ivjPrefixNumberTextField.setName("PrefixNumberTextField");
			ivjPrefixNumberTextField.setMaximumSize(new java.awt.Dimension(2147483647, 20));
			ivjPrefixNumberTextField.setColumns(10);
			ivjPrefixNumberTextField.setPreferredSize(new java.awt.Dimension(110, 20));
			ivjPrefixNumberTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjPrefixNumberTextField.setMinimumSize(new java.awt.Dimension(110, 20));
			// user code begin {1}
			ivjPrefixNumberTextField.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_PREFIX_NUMBER_LENGTH));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPrefixNumberTextField;
}
/**
 * Return the SuffixNumberLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSuffixNumberLabel() {
	if (ivjSuffixNumberLabel == null) {
		try {
			ivjSuffixNumberLabel = new javax.swing.JLabel();
			ivjSuffixNumberLabel.setName("SuffixNumberLabel");
			ivjSuffixNumberLabel.setText("Suffix Number:");
			ivjSuffixNumberLabel.setMaximumSize(new java.awt.Dimension(91, 16));
			ivjSuffixNumberLabel.setPreferredSize(new java.awt.Dimension(91, 16));
			ivjSuffixNumberLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjSuffixNumberLabel.setMinimumSize(new java.awt.Dimension(91, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSuffixNumberLabel;
}
/**
 * Return the SuffixNumberTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getSuffixNumberTextField() {
	if (ivjSuffixNumberTextField == null) {
		try {
			ivjSuffixNumberTextField = new javax.swing.JTextField();
			ivjSuffixNumberTextField.setName("SuffixNumberTextField");
			ivjSuffixNumberTextField.setMaximumSize(new java.awt.Dimension(2147483647, 20));
			ivjSuffixNumberTextField.setColumns(10);
			ivjSuffixNumberTextField.setPreferredSize(new java.awt.Dimension(110, 20));
			ivjSuffixNumberTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjSuffixNumberTextField.setMinimumSize(new java.awt.Dimension(110, 20));
			// user code begin {1}
			ivjSuffixNumberTextField.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_SUFFIX_NUMBER_LENGTH));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSuffixNumberTextField;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
@Override
public Object getValue(Object val) 
{
	String modemType = (String) getModemTypeComboBox().getSelectedItem();
	String initString = getInitializationStringTextField().getText();
	String prefixString = getPrefixNumberTextField().getText();
	String suffixString = getSuffixNumberTextField().getText();
	
	if( val instanceof LocalDialupPortBase
		 || val instanceof TerminalServerDialupPort )
	{
		PortDialupModem pdm;
	
		try
		{
			pdm = ((LocalDialupPortBase) val).getPortDialupModem();
		}
		catch( ClassCastException cce )
		{
			//let this one throw an exception - no try/catch
			pdm = ((TerminalServerDialupPort) val).getPortDialupModem();
		}
		
		pdm.setModemType( modemType );
		pdm.setInitializationString( initString );
		pdm.setPrefixNumber( prefixString );
		pdm.setSuffixNumber( suffixString );		
	}
/*
 	else if( val instanceof PortDialBack )
	{		
		PortDialBack pdb = (PortDialBack)val;
		
		pdb.getPortDialback().setModemType( modemType );
		pdb.getPortDialback().setInitializationString( initString );
	}
*/
	else  //the thing that should not be!
		throw new Error("Unrecognized port type instance, unknown instance is = " 
								+ val.getClass().getName() );
								

	return val;
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
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getModemTypeComboBox().addActionListener(this);
	getInitializationStringTextField().addCaretListener(this);
	getPrefixNumberTextField().addCaretListener(this);
	getSuffixNumberTextField().addCaretListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("PortModemEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(413, 227);

		java.awt.GridBagConstraints constraintsModemTypeLabel = new java.awt.GridBagConstraints();
		constraintsModemTypeLabel.gridx = 0; constraintsModemTypeLabel.gridy = 0;
		constraintsModemTypeLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsModemTypeLabel.anchor = java.awt.GridBagConstraints.WEST;
		add(getModemTypeLabel(), constraintsModemTypeLabel);

		java.awt.GridBagConstraints constraintsInitializationStringLabel = new java.awt.GridBagConstraints();
		constraintsInitializationStringLabel.gridx = 0; constraintsInitializationStringLabel.gridy = 1;
		constraintsInitializationStringLabel.gridwidth = 3;
		constraintsInitializationStringLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsInitializationStringLabel.anchor = java.awt.GridBagConstraints.WEST;
		add(getInitializationStringLabel(), constraintsInitializationStringLabel);

		java.awt.GridBagConstraints constraintsModemTypeComboBox = new java.awt.GridBagConstraints();
		constraintsModemTypeComboBox.gridx = 1; constraintsModemTypeComboBox.gridy = 0;
		constraintsModemTypeComboBox.anchor = java.awt.GridBagConstraints.EAST;
		constraintsModemTypeComboBox.insets = new java.awt.Insets(5, 10, 5, 0);
		add(getModemTypeComboBox(), constraintsModemTypeComboBox);

		java.awt.GridBagConstraints constraintsInitializationStringTextField = new java.awt.GridBagConstraints();
		constraintsInitializationStringTextField.gridx = 0; constraintsInitializationStringTextField.gridy = 2;
		constraintsInitializationStringTextField.gridwidth = 3;
		constraintsInitializationStringTextField.anchor = java.awt.GridBagConstraints.EAST;
		constraintsInitializationStringTextField.insets = new java.awt.Insets(5, 10, 5, 0);
		add(getInitializationStringTextField(), constraintsInitializationStringTextField);

		java.awt.GridBagConstraints constraintsDialingPropertiesPanel = new java.awt.GridBagConstraints();
		constraintsDialingPropertiesPanel.gridx = 0; constraintsDialingPropertiesPanel.gridy = 3;
		constraintsDialingPropertiesPanel.gridwidth = 2;
		constraintsDialingPropertiesPanel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsDialingPropertiesPanel.insets = new java.awt.Insets(5, 0, 0, 0);
		add(getDialingPropertiesPanel(), constraintsDialingPropertiesPanel);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	getModemTypeComboBox().addItem( "U.S. Robotics Sportster" );
	getModemTypeComboBox().addItem( "U.S. Robotics Courier" );
	getModemTypeComboBox().addItem("Motorola");
	getModemTypeComboBox().addItem("Telenetics");
	// user code end
}
/**
 * isDataComplete method comment.
 */
public boolean isDataComplete() {
	return false;
}

/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
@Override
public void setValue(Object val) 
{
	String modemType = null, initString = null;
	PortDialupModem pdm = null;
	
	if( val instanceof LocalDialupPortBase )
	{
		pdm = ((LocalDialupPortBase) val).getPortDialupModem();
		getPrefixNumberTextField().setText( pdm.getPrefixNumber() );

		getSuffixNumberTextField().setText( pdm.getSuffixNumber() );
						
		modemType = pdm.getModemType();
		initString = pdm.getInitializationString();	

		//do not show dial properties if we are dial back
		getDialingPropertiesPanel().setVisible(((DirectPort)val).getPaoType() != PaoType.LOCAL_DIALBACK);
	}	
	else if( val instanceof TerminalServerDialupPort )
	{
		pdm = ((TerminalServerDialupPort) val).getPortDialupModem();
		getPrefixNumberTextField().setText( pdm.getPrefixNumber() );

		getSuffixNumberTextField().setText( pdm.getSuffixNumber() );
						
		modemType = pdm.getModemType();
		initString = pdm.getInitializationString();		
	}	
/*
	else if( val instanceof PortDialBack )
	{		
		modemType = ((PortDialBack)val).getPortDialback().getModemType();
		initString = ((PortDialBack)val).getPortDialback().getInitializationString();
		
		getDialingPropertiesPanel().setVisible( false );		
	}
*/	
	else  //the thing that should not be!
		throw new Error("Unrecognized port type instance, unknown instance is = " 
								+ val.getClass().getName() );
		

    SwingUtil.setSelectedInComboBox(getModemTypeComboBox(), modemType);	
	getInitializationStringTextField().setText( initString );
}

}
