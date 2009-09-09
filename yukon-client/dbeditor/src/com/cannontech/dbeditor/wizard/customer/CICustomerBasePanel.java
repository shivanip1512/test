package com.cannontech.dbeditor.wizard.customer;
/**
 * This type was created in VisualAge.
 */
import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.database.data.customer.CICustomerBase;

public class CICustomerBasePanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
	private javax.swing.JLabel ivjJLabelName = null;
	private javax.swing.JTextField ivjJTextFieldCompanyName = null;
	private javax.swing.JLabel ivjJLabelKw = null;
	private javax.swing.JLabel ivjJLabelCurtailAmount = null;
	private javax.swing.JLabel ivjJLabelFPL = null;
	private javax.swing.JLabel ivjJLabelKw1 = null;
	private javax.swing.JTextField ivjJTextFieldCurtailAmount = null;
	private javax.swing.JTextField ivjJTextFieldFPL = null;
	private javax.swing.JLabel ivjJLabelWebHome = null;
	private javax.swing.JTextField ivjJTextFieldWebHome = null;
	private javax.swing.JComboBox ivjJComboBoxEnergyCompany = null;
	private javax.swing.JLabel ivjJLabelEnergyCmpy = null;

/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public CICustomerBasePanel() {
	super();
	initialize();
}


/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJComboBoxEnergyCompany()) 
		connEtoC7(e);
	// user code begin {2}
	// user code end
}


/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void caretUpdate(javax.swing.event.CaretEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJTextFieldCompanyName()) 
		connEtoC1(e);
	if (e.getSource() == getJTextFieldFPL()) 
		connEtoC4(e);
	if (e.getSource() == getJTextFieldCurtailAmount()) 
		connEtoC5(e);
	if (e.getSource() == getJTextFieldWebHome()) 
		connEtoC6(e);
	// user code begin {2}
	// user code end
}


/**
 * connEtoC1:  (JTextFieldCompanyName.caret.caretUpdate(javax.swing.event.CaretEvent) --> CICustomerBasePanel.fireInputUpdate()V)
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
 * connEtoC2:  (JTextFieldPhoneNumber.caret.caretUpdate(javax.swing.event.CaretEvent) --> CICustomerBasePanel.fireInputUpdate()V)
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
 * connEtoC3:  (JTextField1.caret.caretUpdate(javax.swing.event.CaretEvent) --> CICustomerBasePanel.fireInputUpdate()V)
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
 * connEtoC4:  (JTextField2.caret.caretUpdate(javax.swing.event.CaretEvent) --> CICustomerBasePanel.fireInputUpdate()V)
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
 * connEtoC5:  (JTextFieldCurtailAmount.caret.caretUpdate(javax.swing.event.CaretEvent) --> CICustomerBasePanel.fireInputUpdate()V)
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
 * connEtoC6:  (JTextFieldCurtailWebHome.caret.caretUpdate(javax.swing.event.CaretEvent) --> CICustomerBasePanel.fireInputUpdate()V)
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
 * connEtoC7:  (JComboBoxEnergyCompany.action.actionPerformed(java.awt.event.ActionEvent) --> CICustomerBasePanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.awt.event.ActionEvent arg1) {
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
 * Return the JComboBoxEnergyCompany property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxEnergyCompany() {
	if (ivjJComboBoxEnergyCompany == null) {
		try {
			ivjJComboBoxEnergyCompany = new javax.swing.JComboBox();
			ivjJComboBoxEnergyCompany.setName("JComboBoxEnergyCompany");
			ivjJComboBoxEnergyCompany.setToolTipText("What energy company owns this customer");
			// user code begin {1}
			
			getJComboBoxEnergyCompany().addItem( com.cannontech.common.util.CtiUtilities.STRING_NONE );

            com.cannontech.database.db.company.EnergyCompany[] companies = 
                com.cannontech.database.db.company.EnergyCompany.getEnergyCompanies();
            
            for( int i = 0; i < companies.length; i++ )
                getJComboBoxEnergyCompany().addItem( companies[i] );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxEnergyCompany;
}


/**
 * Return the JLabelCurtailAmount property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelCurtailAmount() {
	if (ivjJLabelCurtailAmount == null) {
		try {
			ivjJLabelCurtailAmount = new javax.swing.JLabel();
			ivjJLabelCurtailAmount.setName("JLabelCurtailAmount");
			ivjJLabelCurtailAmount.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelCurtailAmount.setText("Default Curtailment Amount:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelCurtailAmount;
}


/**
 * Return the JLabelEnergyCmpy property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelEnergyCmpy() {
	if (ivjJLabelEnergyCmpy == null) {
		try {
			ivjJLabelEnergyCmpy = new javax.swing.JLabel();
			ivjJLabelEnergyCmpy.setName("JLabelEnergyCmpy");
			ivjJLabelEnergyCmpy.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelEnergyCmpy.setText("Owner Energy Company:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelEnergyCmpy;
}


/**
 * Return the JLabelPDA property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelFPL() {
	if (ivjJLabelFPL == null) {
		try {
			ivjJLabelFPL = new javax.swing.JLabel();
			ivjJLabelFPL.setName("JLabelFPL");
			ivjJLabelFPL.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelFPL.setText("Demand Power Level:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelFPL;
}


/**
 * Return the JLabelKwh property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelKw() {
	if (ivjJLabelKw == null) {
		try {
			ivjJLabelKw = new javax.swing.JLabel();
			ivjJLabelKw.setName("JLabelKw");
			ivjJLabelKw.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelKw.setText("kW");
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
 * Return the JLabelKw1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelKw1() {
	if (ivjJLabelKw1 == null) {
		try {
			ivjJLabelKw1 = new javax.swing.JLabel();
			ivjJLabelKw1.setName("JLabelKw1");
			ivjJLabelKw1.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelKw1.setText("kW");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelKw1;
}


/**
 * Return the JLabelType property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelName() {
	if (ivjJLabelName == null) {
		try {
			ivjJLabelName = new javax.swing.JLabel();
			ivjJLabelName.setName("JLabelName");
			ivjJLabelName.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelName.setText("Company Name:");
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
 * Return the JLabelWebHome property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelWebHome() {
	if (ivjJLabelWebHome == null) {
		try {
			ivjJLabelWebHome = new javax.swing.JLabel();
			ivjJLabelWebHome.setName("JLabelWebHome");
			ivjJLabelWebHome.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelWebHome.setText("Home Directory:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelWebHome;
}


/**
 * Return the JTextFieldThreshold property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JTextField getJTextFieldCompanyName() {
	if (ivjJTextFieldCompanyName == null) {
		try {
			ivjJTextFieldCompanyName = new javax.swing.JTextField();
			ivjJTextFieldCompanyName.setName("JTextFieldCompanyName");
			ivjJTextFieldCompanyName.setDocument( new TextFieldDocument( TextFieldDocument.STRING_LENGTH_80 ) );
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldCompanyName;
}


/**
 * Return the JTextFieldCurtailAmount property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldCurtailAmount() {
	if (ivjJTextFieldCurtailAmount == null) {
		try {
			ivjJTextFieldCurtailAmount = new javax.swing.JTextField();
			ivjJTextFieldCurtailAmount.setName("JTextFieldCurtailAmount");
			// user code begin {1}

			ivjJTextFieldCurtailAmount.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument( 0.0, 99999999.9999, 4) );

						
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldCurtailAmount;
}


/**
 * Return the JTextField2 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldFPL() {
	if (ivjJTextFieldFPL == null) {
		try {
			ivjJTextFieldFPL = new javax.swing.JTextField();
			ivjJTextFieldFPL.setName("JTextFieldFPL");
			// user code begin {1}

			ivjJTextFieldFPL.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument( 0.0, 99999999.9999, 4) );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldFPL;
}


/**
 * Return the JTextFieldCurtailWebHome property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldWebHome() {
	if (ivjJTextFieldWebHome == null) {
		try {
			ivjJTextFieldWebHome = new javax.swing.JTextField();
			ivjJTextFieldWebHome.setName("JTextFieldWebHome");
			ivjJTextFieldWebHome.setToolTipText("Directory used by the server for this customers parameters");
			ivjJTextFieldWebHome.setText("/default");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldWebHome;
}


/**
 * getValue method comment.
 */
public Object getValue(Object o)
{
	CICustomerBase customer = (CICustomerBase)o;

	customer.getCiCustomerBase().setCompanyName( getJTextFieldCompanyName().getText() );

	if( getJTextFieldFPL().getText() != null && getJTextFieldFPL().getText().length() > 0 )
		customer.getCiCustomerBase().setCustDmdLevel( 
				Double.valueOf(getJTextFieldFPL().getText()) );

	if( getJTextFieldCurtailAmount().getText() != null && getJTextFieldCurtailAmount().getText().length() > 0 )
		customer.getCiCustomerBase().setCurtailAmount(
				Double.valueOf( getJTextFieldCurtailAmount().getText()) );


/*FIXFIX
	//WebSettingsDefaults, only used if we do not have a CustomerWebSettings row yet!
	if( customer.getCustomerWebSettings().getLogo() == null )
		customer.getCustomerWebSettings().setLogo( com.cannontech.common.util.CtiUtilities.STRING_NONE );

	if( customer.getCustomerWebSettings().getDatabaseAlias() == null )
		customer.getCustomerWebSettings().setDatabaseAlias( com.cannontech.common.util.CtiUtilities.STRING_NONE );

	String home = getJTextFieldWebHome().getText();
	if (home.length() == 0)
		customer.getCustomerWebSettings().setHomeURL("/default");
	else
		customer.getCustomerWebSettings().setHomeURL(home);
*/
	//set the EnergyCompany only if one is selected
	if( !(getJComboBoxEnergyCompany().getSelectedItem() instanceof String) )
	{
		customer.setEnergyCompany( 
			(com.cannontech.database.db.company.EnergyCompany)
				getJComboBoxEnergyCompany().getSelectedItem() );
	}
	else
		customer.setEnergyCompany( null ); //doe not have an EnergyCompany

	return customer;
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
	getJTextFieldCompanyName().addCaretListener(this);
	getJTextFieldFPL().addCaretListener(this);
	getJTextFieldCurtailAmount().addCaretListener(this);
	getJTextFieldWebHome().addCaretListener(this);
	getJComboBoxEnergyCompany().addActionListener(this);
}


/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("CICustomerBasePanel");
		setToolTipText("");
		setLayout(new java.awt.GridBagLayout());
		setSize(384, 144);

		java.awt.GridBagConstraints constraintsJLabelName = new java.awt.GridBagConstraints();
		constraintsJLabelName.gridx = 1; constraintsJLabelName.gridy = 1;
		constraintsJLabelName.gridwidth = 2;
		constraintsJLabelName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelName.ipadx = 9;
		constraintsJLabelName.ipady = -2;
		constraintsJLabelName.insets = new java.awt.Insets(14, 6, 2, 31);
		add(getJLabelName(), constraintsJLabelName);

		java.awt.GridBagConstraints constraintsJTextFieldCompanyName = new java.awt.GridBagConstraints();
		constraintsJTextFieldCompanyName.gridx = 3; constraintsJTextFieldCompanyName.gridy = 1;
		constraintsJTextFieldCompanyName.gridwidth = 4;
		constraintsJTextFieldCompanyName.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldCompanyName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldCompanyName.weightx = 1.0;
		constraintsJTextFieldCompanyName.ipadx = 225;
		constraintsJTextFieldCompanyName.insets = new java.awt.Insets(12, 0, 1, 5);
		add(getJTextFieldCompanyName(), constraintsJTextFieldCompanyName);

		java.awt.GridBagConstraints constraintsJLabelEnergyCmpy = new java.awt.GridBagConstraints();
		constraintsJLabelEnergyCmpy.gridx = 1; constraintsJLabelEnergyCmpy.gridy = 5;
		constraintsJLabelEnergyCmpy.gridwidth = 3;
		constraintsJLabelEnergyCmpy.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelEnergyCmpy.ipadx = 4;
		constraintsJLabelEnergyCmpy.ipady = -2;
		constraintsJLabelEnergyCmpy.insets = new java.awt.Insets(6, 6, 15, 1);
		add(getJLabelEnergyCmpy(), constraintsJLabelEnergyCmpy);

		java.awt.GridBagConstraints constraintsJComboBoxEnergyCompany = new java.awt.GridBagConstraints();
		constraintsJComboBoxEnergyCompany.gridx = 4; constraintsJComboBoxEnergyCompany.gridy = 5;
		constraintsJComboBoxEnergyCompany.gridwidth = 3;
		constraintsJComboBoxEnergyCompany.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxEnergyCompany.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxEnergyCompany.weightx = 1.0;
		constraintsJComboBoxEnergyCompany.ipadx = 82;
		constraintsJComboBoxEnergyCompany.insets = new java.awt.Insets(4, 2, 11, 5);
		add(getJComboBoxEnergyCompany(), constraintsJComboBoxEnergyCompany);

		java.awt.GridBagConstraints constraintsJLabelWebHome = new java.awt.GridBagConstraints();
		constraintsJLabelWebHome.gridx = 1; constraintsJLabelWebHome.gridy = 4;
		constraintsJLabelWebHome.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelWebHome.ipadx = 5;
		constraintsJLabelWebHome.ipady = -2;
		constraintsJLabelWebHome.insets = new java.awt.Insets(3, 6, 4, 3);
		add(getJLabelWebHome(), constraintsJLabelWebHome);

		java.awt.GridBagConstraints constraintsJLabelCurtailAmount = new java.awt.GridBagConstraints();
		constraintsJLabelCurtailAmount.gridx = 1; constraintsJLabelCurtailAmount.gridy = 3;
		constraintsJLabelCurtailAmount.gridwidth = 4;
		constraintsJLabelCurtailAmount.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelCurtailAmount.ipadx = 4;
		constraintsJLabelCurtailAmount.ipady = -2;
		constraintsJLabelCurtailAmount.insets = new java.awt.Insets(5, 6, 2, 0);
		add(getJLabelCurtailAmount(), constraintsJLabelCurtailAmount);

		java.awt.GridBagConstraints constraintsJLabelFPL = new java.awt.GridBagConstraints();
		constraintsJLabelFPL.gridx = 1; constraintsJLabelFPL.gridy = 2;
		constraintsJLabelFPL.gridwidth = 2;
		constraintsJLabelFPL.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelFPL.ipadx = 6;
		constraintsJLabelFPL.ipady = -2;
		constraintsJLabelFPL.insets = new java.awt.Insets(4, 6, 4, 0);
		add(getJLabelFPL(), constraintsJLabelFPL);

		java.awt.GridBagConstraints constraintsJTextFieldFPL = new java.awt.GridBagConstraints();
		constraintsJTextFieldFPL.gridx = 5; constraintsJTextFieldFPL.gridy = 2;
		constraintsJTextFieldFPL.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldFPL.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldFPL.weightx = 1.0;
		constraintsJTextFieldFPL.ipadx = 71;
		constraintsJTextFieldFPL.insets = new java.awt.Insets(2, 1, 3, 3);
		add(getJTextFieldFPL(), constraintsJTextFieldFPL);

		java.awt.GridBagConstraints constraintsJLabelKw = new java.awt.GridBagConstraints();
		constraintsJLabelKw.gridx = 6; constraintsJLabelKw.gridy = 2;
		constraintsJLabelKw.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelKw.ipadx = 21;
		constraintsJLabelKw.ipady = -2;
		constraintsJLabelKw.insets = new java.awt.Insets(5, 4, 6, 76);
		add(getJLabelKw(), constraintsJLabelKw);

		java.awt.GridBagConstraints constraintsJLabelKw1 = new java.awt.GridBagConstraints();
		constraintsJLabelKw1.gridx = 6; constraintsJLabelKw1.gridy = 3;
		constraintsJLabelKw1.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelKw1.ipadx = 21;
		constraintsJLabelKw1.ipady = -2;
		constraintsJLabelKw1.insets = new java.awt.Insets(6, 4, 4, 76);
		add(getJLabelKw1(), constraintsJLabelKw1);

		java.awt.GridBagConstraints constraintsJTextFieldCurtailAmount = new java.awt.GridBagConstraints();
		constraintsJTextFieldCurtailAmount.gridx = 5; constraintsJTextFieldCurtailAmount.gridy = 3;
		constraintsJTextFieldCurtailAmount.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldCurtailAmount.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldCurtailAmount.weightx = 1.0;
		constraintsJTextFieldCurtailAmount.ipadx = 71;
		constraintsJTextFieldCurtailAmount.insets = new java.awt.Insets(3, 1, 1, 3);
		add(getJTextFieldCurtailAmount(), constraintsJTextFieldCurtailAmount);

		java.awt.GridBagConstraints constraintsJTextFieldWebHome = new java.awt.GridBagConstraints();
		constraintsJTextFieldWebHome.gridx = 2; constraintsJTextFieldWebHome.gridy = 4;
		constraintsJTextFieldWebHome.gridwidth = 5;
		constraintsJTextFieldWebHome.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldWebHome.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldWebHome.weightx = 1.0;
		constraintsJTextFieldWebHome.ipadx = 257;
		constraintsJTextFieldWebHome.insets = new java.awt.Insets(1, 3, 3, 5);
		add(getJTextFieldWebHome(), constraintsJTextFieldWebHome);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	//for now do not show the Curtailment kW or Demand Level kW	
	getJLabelCurtailAmount().setVisible( false );
	getJLabelKw().setVisible( false );
	getJTextFieldCurtailAmount().setVisible( false );
	
	getJLabelFPL().setVisible( false );
	getJLabelKw1().setVisible( false );
	getJTextFieldFPL().setVisible( false );

	
	boolean isEComp = getJComboBoxEnergyCompany().getItemCount() >= 1;
	getJLabelEnergyCmpy().setEnabled( isEComp );
	getJComboBoxEnergyCompany().setEnabled( isEComp );



	// user code end
}

/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() 
{
	if( getJTextFieldCompanyName().getText() == null || getJTextFieldCompanyName().getText().length() <= 0 )
	{
		setErrorString("The Company Name text field must be filled in");
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
		CICustomerBasePanel aCICustomerBasePanel;
		aCICustomerBasePanel = new CICustomerBasePanel();
		frame.setContentPane(aCICustomerBasePanel);
		frame.setSize(aCICustomerBasePanel.getSize());
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
	if( o == null )
		return;

	CICustomerBase customer = (CICustomerBase)o;

	getJTextFieldCompanyName().setText( customer.getCiCustomerBase().getCompanyName() );

	getJTextFieldFPL().setText( customer.getCiCustomerBase().getCustDmdLevel().toString() );

	getJTextFieldCurtailAmount().setText( customer.getCiCustomerBase().getCurtailAmount().toString() );

/*FIXFIX
	String home = customer.getCustomerWebSettings().getHomeURL();
	if( home != null )
		getJTextFieldWebHome().setText(home);
*/

	if( customer.getEnergyCompany() != null )
		getJComboBoxEnergyCompany().setSelectedItem( customer.getEnergyCompany() );
}
}