package com.cannontech.dbeditor.wizard.device.customer;

/**
 * This type was created in VisualAge.
 */
import java.awt.Dimension;

public class CICustomerBasePanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
	private javax.swing.JPanel ivjJPanelTrigger = null;
	private javax.swing.JLabel ivjJLabelName = null;
	private javax.swing.JLabel ivjJLabelNormalFax = null;
	private javax.swing.JLabel ivjJLabelPhone = null;
	private javax.swing.JTextField ivjJTextFieldCompanyName = null;
	private javax.swing.JTextField ivjJTextFieldPhoneNumber = null;
	private javax.swing.JTextField ivjJTextFieldFax = null;
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
	if (e.getSource() == getJTextFieldPhoneNumber()) 
		connEtoC2(e);
	if (e.getSource() == getJTextFieldFax()) 
		connEtoC3(e);
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
			ivjJComboBoxEnergyCompany.setEnabled(false);
			// user code begin {1}

			getJComboBoxEnergyCompany().addItem( com.cannontech.common.util.CtiUtilities.STRING_NONE );



			try
			{
				java.sql.Connection conn = com.cannontech.database.PoolManager.getInstance().getConnection(com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
				com.cannontech.database.db.company.EnergyCompany[] companies = 
						com.cannontech.database.db.company.EnergyCompany.getEnergyCompanies( conn );
				conn.close();
					
				getJLabelEnergyCmpy().setEnabled( companies.length > 0 );
				getJComboBoxEnergyCompany().setEnabled( companies.length > 0 );

				for( int i = 0; i < companies.length; i++ )
					getJComboBoxEnergyCompany().addItem( companies[i] );
			}
			catch( java.sql.SQLException e )
			{}
			
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
			ivjJLabelEnergyCmpy.setEnabled(false);
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
			ivjJLabelFPL.setText("Firm Power Level:");
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
 * Return the JLabelNormalStateAndThreshold property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelNormalFax() {
	if (ivjJLabelNormalFax == null) {
		try {
			ivjJLabelNormalFax = new javax.swing.JLabel();
			ivjJLabelNormalFax.setName("JLabelNormalFax");
			ivjJLabelNormalFax.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelNormalFax.setText("Fax Number:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelNormalFax;
}
/**
 * Return the JLabelPoint property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelPhone() {
	if (ivjJLabelPhone == null) {
		try {
			ivjJLabelPhone = new javax.swing.JLabel();
			ivjJLabelPhone.setName("JLabelPhone");
			ivjJLabelPhone.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelPhone.setText("Phone Number:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelPhone;
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
 * Return the JPanelTrigger property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelTrigger() {
	if (ivjJPanelTrigger == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Customer Info");
			ivjJPanelTrigger = new javax.swing.JPanel();
			ivjJPanelTrigger.setName("JPanelTrigger");
			ivjJPanelTrigger.setBorder(ivjLocalBorder);
			ivjJPanelTrigger.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabelName = new java.awt.GridBagConstraints();
			constraintsJLabelName.gridx = 1; constraintsJLabelName.gridy = 1;
			constraintsJLabelName.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelName.ipadx = 9;
			constraintsJLabelName.ipady = -2;
			constraintsJLabelName.insets = new java.awt.Insets(2, 9, 5, 0);
			getJPanelTrigger().add(getJLabelName(), constraintsJLabelName);

			java.awt.GridBagConstraints constraintsJLabelPhone = new java.awt.GridBagConstraints();
			constraintsJLabelPhone.gridx = 1; constraintsJLabelPhone.gridy = 2;
			constraintsJLabelPhone.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelPhone.ipadx = 9;
			constraintsJLabelPhone.ipady = -2;
			constraintsJLabelPhone.insets = new java.awt.Insets(7, 9, 4, 6);
			getJPanelTrigger().add(getJLabelPhone(), constraintsJLabelPhone);

			java.awt.GridBagConstraints constraintsJLabelNormalFax = new java.awt.GridBagConstraints();
			constraintsJLabelNormalFax.gridx = 1; constraintsJLabelNormalFax.gridy = 3;
			constraintsJLabelNormalFax.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelNormalFax.ipadx = 12;
			constraintsJLabelNormalFax.ipady = -2;
			constraintsJLabelNormalFax.insets = new java.awt.Insets(6, 8, 20, 22);
			getJPanelTrigger().add(getJLabelNormalFax(), constraintsJLabelNormalFax);

			java.awt.GridBagConstraints constraintsJTextFieldFax = new java.awt.GridBagConstraints();
			constraintsJTextFieldFax.gridx = 2; constraintsJTextFieldFax.gridy = 3;
			constraintsJTextFieldFax.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldFax.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldFax.weightx = 1.0;
			constraintsJTextFieldFax.ipadx = 103;
			constraintsJTextFieldFax.insets = new java.awt.Insets(4, 1, 19, 170);
			getJPanelTrigger().add(getJTextFieldFax(), constraintsJTextFieldFax);

			java.awt.GridBagConstraints constraintsJTextFieldPhoneNumber = new java.awt.GridBagConstraints();
			constraintsJTextFieldPhoneNumber.gridx = 2; constraintsJTextFieldPhoneNumber.gridy = 2;
			constraintsJTextFieldPhoneNumber.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldPhoneNumber.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldPhoneNumber.weightx = 1.0;
			constraintsJTextFieldPhoneNumber.ipadx = 103;
			constraintsJTextFieldPhoneNumber.insets = new java.awt.Insets(5, 1, 3, 170);
			getJPanelTrigger().add(getJTextFieldPhoneNumber(), constraintsJTextFieldPhoneNumber);

			java.awt.GridBagConstraints constraintsJTextFieldCompanyName = new java.awt.GridBagConstraints();
			constraintsJTextFieldCompanyName.gridx = 2; constraintsJTextFieldCompanyName.gridy = 1;
			constraintsJTextFieldCompanyName.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldCompanyName.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldCompanyName.weightx = 1.0;
			constraintsJTextFieldCompanyName.ipadx = 253;
			constraintsJTextFieldCompanyName.insets = new java.awt.Insets(0, 1, 4, 20);
			getJPanelTrigger().add(getJTextFieldCompanyName(), constraintsJTextFieldCompanyName);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelTrigger;
}
/**
 * Return the JTextFieldThreshold property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldCompanyName() {
	if (ivjJTextFieldCompanyName == null) {
		try {
			ivjJTextFieldCompanyName = new javax.swing.JTextField();
			ivjJTextFieldCompanyName.setName("JTextFieldCompanyName");
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
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldFax() {
	if (ivjJTextFieldFax == null) {
		try {
			ivjJTextFieldFax = new javax.swing.JTextField();
			ivjJTextFieldFax.setName("JTextFieldFax");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldFax;
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
 * Return the JTextFieldPhoneNumber property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldPhoneNumber() {
	if (ivjJTextFieldPhoneNumber == null) {
		try {
			ivjJTextFieldPhoneNumber = new javax.swing.JTextField();
			ivjJTextFieldPhoneNumber.setName("JTextFieldPhoneNumber");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldPhoneNumber;
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
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getPreferredSize() {
	return null;
}
/**
 * getValue method comment.
 */
public Object getValue(Object o)
{
	com.cannontech.database.data.customer.CICustomerBase customer = null;

	if (o == null)
	{
		customer = (com.cannontech.database.data.customer.CICustomerBase) com.cannontech.database.data.customer.CustomerFactory.createCustomer(
							com.cannontech.database.data.pao.CustomerTypes.CI_CUSTOMER);

		customer.setCustomerID( com.cannontech.database.db.pao.YukonPAObject.getNextYukonPAObjectID() );
	}
	else
		customer = (com.cannontech.database.data.customer.CICustomerBase) o;

	customer.setCustomerName( getJTextFieldCompanyName().getText() );

	if (getJTextFieldFPL().getText() != null && getJTextFieldFPL().getText().length() > 0)
		customer.getCiCustomerBase().setCustFPL(Double.valueOf(getJTextFieldFPL().getText()));

	if (getJTextFieldCurtailAmount().getText() != null && getJTextFieldCurtailAmount().getText().length() > 0)
		customer.getCiCustomerBase().setCurtailAmount(Double.valueOf(getJTextFieldCurtailAmount().getText()));

	if (getJTextFieldPhoneNumber().getText() != null && getJTextFieldPhoneNumber().getText().length() > 0)
		customer.getCiCustomerBase().setMainPhoneNumber(getJTextFieldPhoneNumber().getText());

	if (getJTextFieldFax().getText() != null && getJTextFieldFax().getText().length() > 0)
		customer.getCiCustomerBase().setMainFaxNumber(getJTextFieldFax().getText());

	//HACKS FOR NOW!!!!!!!!!!!!!!!!!
	customer.getCiCustomerBase().setCustTimeZone("*BAD*");
	customer.getCiCustomerBase().setPrimeContactID(new Integer(0));
	//END HACKS FOR NOW!!!!!!!!!!!!!!!!!

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

	//set the EnergyCompany only if one is selected
	if( !(getJComboBoxEnergyCompany().getSelectedItem() instanceof String) )
		customer.setEnergyCompany( 
			(com.cannontech.database.db.company.EnergyCompany)getJComboBoxEnergyCompany().getSelectedItem() );
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
	// user code end
	getJTextFieldCompanyName().addCaretListener(this);
	getJTextFieldPhoneNumber().addCaretListener(this);
	getJTextFieldFax().addCaretListener(this);
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
		setSize(416, 348);

		java.awt.GridBagConstraints constraintsJPanelTrigger = new java.awt.GridBagConstraints();
		constraintsJPanelTrigger.gridx = 1; constraintsJPanelTrigger.gridy = 1;
		constraintsJPanelTrigger.gridwidth = 5;
		constraintsJPanelTrigger.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelTrigger.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelTrigger.weightx = 1.0;
		constraintsJPanelTrigger.weighty = 1.0;
		constraintsJPanelTrigger.ipadx = -10;
		constraintsJPanelTrigger.ipady = -3;
		constraintsJPanelTrigger.insets = new java.awt.Insets(13, 8, 11, 8);
		add(getJPanelTrigger(), constraintsJPanelTrigger);

		java.awt.GridBagConstraints constraintsJLabelFPL = new java.awt.GridBagConstraints();
		constraintsJLabelFPL.gridx = 1; constraintsJLabelFPL.gridy = 2;
		constraintsJLabelFPL.gridwidth = 2;
		constraintsJLabelFPL.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelFPL.ipadx = 6;
		constraintsJLabelFPL.ipady = -2;
		constraintsJLabelFPL.insets = new java.awt.Insets(14, 8, 6, 44);
		add(getJLabelFPL(), constraintsJLabelFPL);

		java.awt.GridBagConstraints constraintsJTextFieldFPL = new java.awt.GridBagConstraints();
		constraintsJTextFieldFPL.gridx = 4; constraintsJTextFieldFPL.gridy = 2;
		constraintsJTextFieldFPL.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldFPL.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldFPL.weightx = 1.0;
		constraintsJTextFieldFPL.ipadx = 75;
		constraintsJTextFieldFPL.insets = new java.awt.Insets(12, 1, 5, 3);
		add(getJTextFieldFPL(), constraintsJTextFieldFPL);

		java.awt.GridBagConstraints constraintsJLabelKw = new java.awt.GridBagConstraints();
		constraintsJLabelKw.gridx = 5; constraintsJLabelKw.gridy = 2;
		constraintsJLabelKw.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelKw.ipadx = 21;
		constraintsJLabelKw.ipady = -2;
		constraintsJLabelKw.insets = new java.awt.Insets(15, 4, 8, 101);
		add(getJLabelKw(), constraintsJLabelKw);

		java.awt.GridBagConstraints constraintsJLabelCurtailAmount = new java.awt.GridBagConstraints();
		constraintsJLabelCurtailAmount.gridx = 1; constraintsJLabelCurtailAmount.gridy = 3;
		constraintsJLabelCurtailAmount.gridwidth = 3;
		constraintsJLabelCurtailAmount.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelCurtailAmount.ipadx = 4;
		constraintsJLabelCurtailAmount.ipady = -2;
		constraintsJLabelCurtailAmount.insets = new java.awt.Insets(7, 8, 9, 1);
		add(getJLabelCurtailAmount(), constraintsJLabelCurtailAmount);

		java.awt.GridBagConstraints constraintsJTextFieldCurtailAmount = new java.awt.GridBagConstraints();
		constraintsJTextFieldCurtailAmount.gridx = 4; constraintsJTextFieldCurtailAmount.gridy = 3;
		constraintsJTextFieldCurtailAmount.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldCurtailAmount.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldCurtailAmount.weightx = 1.0;
		constraintsJTextFieldCurtailAmount.ipadx = 75;
		constraintsJTextFieldCurtailAmount.insets = new java.awt.Insets(5, 1, 8, 3);
		add(getJTextFieldCurtailAmount(), constraintsJTextFieldCurtailAmount);

		java.awt.GridBagConstraints constraintsJLabelKw1 = new java.awt.GridBagConstraints();
		constraintsJLabelKw1.gridx = 5; constraintsJLabelKw1.gridy = 3;
		constraintsJLabelKw1.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelKw1.ipadx = 21;
		constraintsJLabelKw1.ipady = -2;
		constraintsJLabelKw1.insets = new java.awt.Insets(8, 4, 11, 101);
		add(getJLabelKw1(), constraintsJLabelKw1);

		java.awt.GridBagConstraints constraintsJLabelWebHome = new java.awt.GridBagConstraints();
		constraintsJLabelWebHome.gridx = 1; constraintsJLabelWebHome.gridy = 4;
		constraintsJLabelWebHome.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelWebHome.ipadx = 5;
		constraintsJLabelWebHome.ipady = -2;
		constraintsJLabelWebHome.insets = new java.awt.Insets(10, 9, 6, 0);
		add(getJLabelWebHome(), constraintsJLabelWebHome);

		java.awt.GridBagConstraints constraintsJTextFieldWebHome = new java.awt.GridBagConstraints();
		constraintsJTextFieldWebHome.gridx = 2; constraintsJTextFieldWebHome.gridy = 4;
		constraintsJTextFieldWebHome.gridwidth = 4;
		constraintsJTextFieldWebHome.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldWebHome.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldWebHome.weightx = 1.0;
		constraintsJTextFieldWebHome.ipadx = 289;
		constraintsJTextFieldWebHome.insets = new java.awt.Insets(8, 0, 5, 8);
		add(getJTextFieldWebHome(), constraintsJTextFieldWebHome);

		java.awt.GridBagConstraints constraintsJLabelEnergyCmpy = new java.awt.GridBagConstraints();
		constraintsJLabelEnergyCmpy.gridx = 1; constraintsJLabelEnergyCmpy.gridy = 5;
		constraintsJLabelEnergyCmpy.gridwidth = 2;
		constraintsJLabelEnergyCmpy.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelEnergyCmpy.ipadx = 4;
		constraintsJLabelEnergyCmpy.ipady = -2;
		constraintsJLabelEnergyCmpy.insets = new java.awt.Insets(8, 9, 78, 0);
		add(getJLabelEnergyCmpy(), constraintsJLabelEnergyCmpy);

		java.awt.GridBagConstraints constraintsJComboBoxEnergyCompany = new java.awt.GridBagConstraints();
		constraintsJComboBoxEnergyCompany.gridx = 3; constraintsJComboBoxEnergyCompany.gridy = 5;
		constraintsJComboBoxEnergyCompany.gridwidth = 3;
		constraintsJComboBoxEnergyCompany.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxEnergyCompany.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxEnergyCompany.weightx = 1.0;
		constraintsJComboBoxEnergyCompany.ipadx = 111;
		constraintsJComboBoxEnergyCompany.insets = new java.awt.Insets(5, 0, 75, 8);
		add(getJComboBoxEnergyCompany(), constraintsJComboBoxEnergyCompany);
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
		exception.printStackTrace(System.out);
	}
}
/**
 * setValue method comment.
 */
public void setValue(Object o) 
{
	if( o == null )
		return;

	com.cannontech.database.data.customer.CICustomerBase customer = (com.cannontech.database.data.customer.CICustomerBase)o;

	getJTextFieldCompanyName().setText( customer.getPAOName() );

	getJTextFieldFax().setText( customer.getCiCustomerBase().getMainFaxNumber() );

	getJTextFieldPhoneNumber().setText( customer.getCiCustomerBase().getMainPhoneNumber() );

	getJTextFieldFPL().setText( customer.getCiCustomerBase().getCustFPL().toString() );

	getJTextFieldCurtailAmount().setText( customer.getCiCustomerBase().getCurtailAmount().toString() );


	String home = customer.getCustomerWebSettings().getHomeURL();
	if( home != null )
		getJTextFieldWebHome().setText(home);


	if( customer.getEnergyCompany() != null )
		getJComboBoxEnergyCompany().setSelectedItem( customer.getEnergyCompany() );
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G83F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8DD8D45715A492E392EDCCC2D2E2CC628FC9C96A1752546C1A2E5BF5D95BA49B53580DE9ED4357B509ED68D7525AADDF57A436DE8684D488A8980D020C7FE8AC02C1D1B40988A288D136A21216D49F4C83C606F9634C8386C55CF36E5FFBB33C81B5DFF643779D5E3B675C73F37F4EBD673E8BA437E05232A4DBAEA1C94B887FA9CBA2A46194A13F3EAFB7D660425458D4E27A5984300ACCBE16824F
	71C00B189E1B1ACDEECFC907F294169F0BCDDD8B6FB9644E1405157092C24FCC201DFE6C782861757CE1035013C64B9787B26079AEC0B460701928227E5B8656F17C9C0E9710E4A3E4C1BB56099A48633883D0EEG188890534E6ABF8B4F65D473AD6587F83D163BA61144FF5FE14BC4B9A619828ABB98EFBDFF5A49371292BBD0578B54CE64890772CCGECFCB2596F7E841E079B3ABABEEE533A0B5A9437DBF36BEADBE7D11F731262F594B954DEE71BDA5456635335EE55DBDEDED9DB495F0FAABE35C1F12BAE
	6269C49DEB2E24D319509EA8AF2138D3FE54BB9D5E67GAC1478777A91FF44991B5A8C60F10A36FE3463B0EFCB7365EF11A5BFF1A7E705E86B98A7EBE30C53E8EBC44F5BE96F53F1591F0F7D1FG346C8B3129DB81EAGBA8146GEC207361233CDD702CE855AB356EEE4DDD61719CF5B93C1FEA8E55856F6565C061383F28ED1A57C1889B731786F6DB58B39CB03ED72F1B0F75C97407E27C277D65D192771DBADB1AC51F44C5975BA6DDE0FDB1630251A7CC777AAB21F44F3F90B8BF83F51FA5E65D89233564E5
	963A273F3C5316406510AEC3F7F1970E753F51B9E00777A4BEBF987E850A57CDF8B6D74FD0BC164301965FA566C6733B42167A1489A43B245E329FA2E3ABEC75AEE6034395EC4B84BA6F463B184EACE94B6394BF4304E73664D1BC1697G2D25BBB6154D2FCF8AF06D4E07F2A2C0069B6698C09540F9G5DAD6658683CBD57B8479A942FFE344769F228DE42F8BF49FF8F1E2ADF77AA35EE1F2E385B54A6456BD4DADD2A0F70BAB57958872D81FC1F52395F86BA0E2BEE552B68CE4D8D5215EED5D73DBED627A9FF
	9556712A145A686CD6C1C137078839FF4F4F0127CB7169E7BC8EC5D70D9A0C5E1E0FE3522BB401C490G6F2CDFDCF66C2F88E87FB40019329FDE25726AD4AF380B7272FACD6B6A71B4E2CBC806067DDCC2FD47DC289F6F96FD5C3989F18DC06F54041CF59BD71B64D4B95B50FC455BAF5658F3FE744F831A18B33FE996F326AA7391327B659316F386E12246664AF4ADF86D3CC4E522DD2C7CB7FF73B0EBF6C033DECF5767AB74E1F5A4FF81796F4A317A71E69C2BB830F7B140BA0F6863539B6DEC4E2203973374
	1497A6186AB0FA1C59B929BF0F77A75A340C5B0FFD3F92A0D336A70656AB869D4DGE1G51GF381968358AF0A3E5D5C6F99C15E97E97770FDDEC1F507521B75A569E6FDDEDB007255DE552DD7299E556D00979F27B594209D5C8F34EB12270962BD60B7AAE1217AA43E25G5736226BDEE7EB0F2E1ED6F55D696EB0688E2A4B57536AB1DC0E5055D500ED577A40EF487A1E82DCF3ED1A5B2D524117AC1C6E25EBDEF5295DE0F425566356995EEF47F5532576F31FB3E047002AD7F175207FE271C70C027D6B71BA
	3BE1CA218BA453AE22BF2839324E54C7B50043B8AA8C1E9197455A6E5BA2567656CB13492CF35BECD6FBD364A3D536440BECCD26FB0D354DEC6A5EE4ED3B0F7A9E0E77A5D35C9B175107D945BFECBDE5EDC5D9052063B0C023D72C275FEEC23A70D9FCA1D5EAAE976BCE594FF11B33F1DC540BBDAA38FAF40EF5326FDE5F3CBA0856447B1151FF3D997B51E5722B44077D78C63F08B1DDE0CFB8C014177565825E972C5E6317C43DFAA8BBFC216A7D7D3228978E6509A16B2D16316DCCA84F8BD92F5EA66AA5C2B9
	C30FCD5D2DDB554BCA9675BA019E86B049325EFF48FAF1C0CF84486D892C4766DD5AB90C072C625E0E9EE7D10F6E445DC551E141F9FAF4BA48AC46FD126ED3689B9AFBC4ACFBAC8947FDAC1423GE67608797923CC914BC6F4CDA28B262E8F99374F6BE136AD6EB16627FD1408E5BF5B06BA8F74C28C5BAB66D47B9673FEDA6173E9EDCE3A15737930E62BF97EB25C7A2DAB60596E747A74DAF0B0FEB1F76CDB51DE5C6BB91A47BE5917B2F8FF35005E88005DFD08FF73E42E4976B900CF84D887B436CE7AAEE14C
	5326F8D53D087EE6A1C4618525D77197797A409995D1CFD7D40954E3783A130F4B6C1D2B190DAA6DF94E3933C94423D3F20E883DCFCCA0072F66582C7203B1D979B63D0F756544BE23CF871ECCCDCE787BBBE92CFDAF497645C39D3BDF346F73BE2B76B5831D715DFAD97039G97A579D6D4DB7145C93EC38302EF91608A7B855F8F7D41FC98EB1C6F97FCBDB6412783EE1A643B334C0AEFE13F9807B85B0D0C8353F9A0D968DC8C720AAF891DDD3DD6BABBAF891D8FA75F384EF7244E8110B7DD6A1CD6EA5619F0
	09694C3DACF436266C0527C21DE991FBB4285EF64D5B2DBA2862A225CF67DAABA8192B657B7EBF525C28703291379DB63DB7DC9E9A4F311C61AD3A6E9D17D94E30D662B369F8CDB461D97B6AE8FDAC4F835A7881916746EF473D2E7E8A74A3C098C0A4408C0079D744FA0F5E7AA199A10F28857F65F5AB2E53819B3B086737E1DF41DE4C4B2D5B301F69DECCC4BF3CF7A9B8C7CC3C9258766C4140B2635BBA042FA828DC9A4447567A36C2745554871B26C7F97909C608F52A1D2A4B41E20113B6F3ACD0BF48465F
	834FF0G6617371E92E78F9387057F3DD2ED763FB333765AD6DA788A6AFF39FD0903062F58AD4F926E26F616DE0DCDEDG688488870886188D90FFD50CD142360FC69AA3BA719A35CA405540DEDD999CD349BE7D698D0E459BA30EC5FAD8A09F6B3F08BE51FF76B0910B0F3BFCE3B6BCB004AF5806E6CB9BAA2A058D9EE9C34510B313EB336154903EE09B22ADEDD856AFEC18A5ED38EC3BB19BEE9B4297EC43E2CB9BFED0A6EC483EC95870F1720D59D0BF04AF58061A1B2CEC1852AB621C73520624149B33A1EB
	88DF308D11B7D979078E972EAB8B7FG6B2BD5BB2A79392D2F178ADBE3C08EBBCF78A10D536221CCEE9638FD03084B0032DD62D69344D5C13965E6514E25C9E8EF9814A782CC87188B3090A0E5946C5BGC7GDAG86GA2C7893E9CBA5E38674F9C45E25EB8F8AE9945625A3CF0488B42D96C5A924EE2D34677BFC9B170C45966BEB8B2EA683EE38542CE344F0C1F4E4BAD57A8070EC3083A587EC293EDC036B90C5A9E1C172F3AC6FDA257DE9904CF634F1CEB140330B6849E7782F36E3E06083877F75BC3675C
	0B37DB675CFF5AEE1DF3276CD84570A043FC12A76899BB064F49F3F7D8656479340F1EFBAF0B4A7679D49F5DC0B0B16134B7F4EC4F105C7C24522D727E4DA11FDEDA4E6B9C53354A92393EFE3FF31D757AB265BB23EDE2ED8D040BFCE777A0BD5307729C000561E2AE6CBCB55C7976127021F30B6D2F6F6F4278AE7B960877G2AGDAG86EE917B6A63BBC60CFD0E790709FDA696069E67290556637CD8A14EE91F5663EDD30347B226F01D2499BBF82D2EF6E33733BACFD3FEDD71F628C36642AC4A4FE821781F
	AB8CB5DF5818E6DFB60F69DEA2FC6D7DFD430D4144DB060E8113B7BBC05E07D2DEF575F072E2C24A738F186515CAF9AB7A0713B7BB243CA7EDE6F97B243C674B0613B7BF243C1EE4333C7DD25EB7FAD1DEC2909F994D4A3923C349BB10E216D7A6654517525C42028F61B0609B02E8CC5EAF2E20CF54B54D556874E0BCCC9C2371FB649F0B092CD3BF0867E9AE25DF6B514136DBF1ED368D0AD8B99A6A4F820887D8B21A454CEC5DFF3B8FE537FB15EE18D227BDEA9BD9C4F9F792415B82E58F40F800980039370A
	DCAB051EE1B08F5168F5F6F450F94DE8D9EB318FCE54AB2D2A2B3A21DE623F7BA96511D13D19764E476912272E0F611EC94E11384A9E2F2EB8DD955DF8B447F91E4ED89BA04F2A4E2B0D4BCDF2CB243E2BA7B3A57E2C5ADA23197C64EBEBD785488D268FFC10ADF90F39D5EFC7FFE537271F513ED9055FEAE4E44249DA37C7F177934272584200F30583E35818340C91F96EDD7B2D689EC93F254C0A9EAE69677CD674C8C97FEFCB7AB4C92FAB35224F9AA3E37100D8F29E524798F3F7491821F31C6543D1F43F48
	3E0D654319927F9D0AAFB561D91E3C1762315C8C34593709D86D48A9912B955FAE7606CD3BC27B7642DD563E3DE817759EDE3AEB78BD7C702ED0BE19599753A76C8B3BDD587CF935404D1538CD7D82E73FC360DEAB93B80F44FD0F46603320BCDF6236D19D8B211CA8F137CB3DE42C40ADAD953C8D92F7FE1F0CF5A5EEB1651D8665D9E3C55E7C625F4C778D32A7EC326178D8664DE35918AF989BFC5F600EC1F15FA0F7DCECEA7DB861FBCA4E87647CF8B2D3DEFE5C6BF49CD5BA6A29A7E3F1C74955F44E9FBECE
	449D672E224C19D01E8710B0CE586B5FAE727CE90FCEA075E1F516672C8B9A775AD23E466CAC7C1AE1EFB46479CC67A9A2F4BA00BE71EB083BC24CEB82577A53B6163FB07A378268B8BF3690B37D55GBA5ADB11EC2667A4196958C7FF667C2CED4FEF93EDDB72FCB4F1BDF5B0649972C25E2674BB593321E98B59FA7BBBE9BC56EB8F916BD1EF465AFF2ECF44FAC76E9467AA4F6E97B83F44BDD3A673AD093B4FAFF07192D754AFF0767182F7B31D1758366671E28EB6A7A3EEBC1423A46E5D0D08FB964AF192B7
	2EC04E733B04CFFA1C6EF78DD06E8488831888B083A0CE56F91A56A1F743FA83B8F2B77BEE015FB870FB882BF3E070BC6FC764375A2B43CC78B853FBA75797AA869150A06DB9ADF5AD8FD1B74D74EE4E2D2CF2A5AB40F99AF1B7765959D3D54354DB466B8AB88FBCBA4096CE9B836F6FA431794664BD53B712BC334C38A0F90B607DC5E21627D70FA4AF4DA4EFC9103C6CFB02659D689FCE5E72A07B72003F789EC3DE953CFFBDC05EDBE5A349B35B5790A44FE11247620417FB2D63BAB7065C3C4E61BE7CEE2A83
	1151E6E0784C6D19BCE27FAF67C04F51026C890777CA9B77F1D476AB7B8C59FD7B06131D46671EE86F78A059D1707E1F81ED6DA8453A2A9BBFC7BA486CFB685E6797BEB6669EF17EBD8643EF3E47D614EC03E713F201E7F9479CCEDD730AB46F84382ED32D97A0E1EAD4BA080831FFBBF8E377317EF803FC7B2FDEAF9F0BF7DE227BD7FA840BF712A4BE0E62F3CDF836072AD7459EEA87DAF804682F68B274E771D0CE39B7B6B58F2094209140F32F080BEEAB41C4E1581C39D17354C36064B205BA2CFD0D64466E
	297DEB1A582B26FFF78A19B17A2D24D0676D6177B2FF99451F710DDBA81E356F50C1EC5FBC20AD82483D8F62D300A3G4D7709767D28E4B7EB9FCE108ADDD75ABA6917CE5138A6275AE720B149E1EDF38464472E7B187EB07E0C38EF289FE7FCAF6CB773C573FAB379F3EEC83EB9817969C2DEEFB1FFE6FCBD945F83FEB31F1D57AB654F1A10FC9F866457AD3C1E07BF43C3725DDAEA660B6475267167ACF84E7B3AD51E2AA5E38E5A2AF9E9204CF2505149A20F0C879E92991BFAG20BE12FD8FE5FC5BB68E4DDD
	8FEF94FC0EC8163B8E40F3BC67E3BEAA2AE0B89FA57C70CA7E0C865E19114CC7E1F9AE3C3F39F46952F7GF8EE7A9EFE93E7D190BBB4E3785A6C15925F502939D50EAFF6E79975B5EF37622AD67C1C1670C1D01E2B78E5BF0D6BDA93C023B2CF76F4371A72676F2B01F5F87A484F72D80E765B41609C0DFEE30E94B9DCFE1395FD1124D754D951D36E0FCDADA06C3CB51D081FC51F8C4509FDE357A1FCE76B7E558C316E733E79G297A49DBC9585FD66BFE5D7DEC8EF55EEF445F71971624D969E6FA9EC651A4B4
	BD7623D07450363F7D7170B21FBAB2BC7DB7954353D761ADF3F1AE64473D31DDF179D459DF137A4C79550195E516771690A671FE1A4E1FB3EF5DA23E8FDEC5C96C7DB553FB5755616E8DFED1E0BA423FD49D4C3FFF3BACF4DEBF234CBA2F7FDE19F5DEBF3BEC783377B9E5216F433DD4E63E8F4776278F2BF08DA5401824CF00F585F08420F102581B14831B895D1BAC7625E30E8EBCB3465B3C3A50DFF5B0F4DB6B8E0EFC873871A07A30D6CD5735EE14AF70FFBD086325EB9E0AE438F387310F3C4E0ECE1DD565
	F875A04EE3175ACE51AC5F3ACC73C18F34ABEC024841F61CC1DC841423A4EE6C99111745C85C427D0217BCA0F0EF3CAFF8F3A52EE63DBC8B113857E9FE6901F218447D0971C2B9CA625AA4EF0C44BD69973CE4224075B48B5EDC097BF13354ABF1A7E83E6A01F2184445B0DEA8C7C95CE1190746C85CC3B42FA58F427B03923796F1DB21DCAAF1496B846F9109EBA895E7B0639F94F7FE1EC896F76023G977720B8BFF9A3FFA799695EDA23D3F729C779164C3F117E016658A8A377A1F11E7237FC54D18FE58740
	40C3C237E32308BBA3GB767A1113BCF5BA8E2771407050C1F513A07215C8860FA78DA779161ABFFF00CC89B37DF956785618F8B1D0DCD82B7D3627CF58217783008092753BCF2772458549AG97C0B8C0A4C08C40EC0079G89GA913A1E686A8862882E886688498G98BFD92CE16D907633E59E82C8FEE0C59732D7F162D2663EE1FD8D7D3BA310B3CB4AEA2A7CE098D98DDE4D23FA75FE062537BA791ABCD4955AAF9C2D9A59AF54D551BB00301E9B7BBD7C3BD2C39546DDDAFB3BCF157E67CCD53644557A9A
	B41F933FC7317D6BEE1A0B36439CE379DBB475898B20ED8913C5AC77711940FC750EDA9CD3962FCDBDB3D23CE6BEB3A8B66514B84F72264426E2CC75C70857187ECC6A9B8A81DF8A4074BDF14250F7EE7DF07AD612407CBFD81F83E4FE407531363DDD96587E6C8354C7C03D01A9227DEB568736BF2E4E3067523076A473F91C96421EE9D3589927517ED728FF1B817859327D151F9A7A96EC9A297D66730AE0FD09C35A2F7487367FE66AA349D470DDD3C57B1FE98EECFF7BC9431E53A3466B4B073167B068D899
	E04FD3250176BCCFFDE78D54EB1476BC35B6501E5BCE99762CDFB712BD66B992ECCF7854607198AC95BEF17CD4F13EB7FD481D1BE0081AEAF5CFA6F6C43E0DC1E546778E651BB5F564B3C804F9D00F1DBB3CC87DFF7C5460730879941FB8B578BCC249977B8209023997A55AFBE1630D1D2378EF106F5B574D475AF532G6DFFB6AA383DE7A9FEDED4F0FB9748FDF081502A9E916D7DA0607CA77A919E1B73675CC7C265581FB5851C8570FA0B79B36351D0FCD76A824E82F83DD27E2C3160E3FE7D596A507B470B
	55A36F9F732B2D634A1FD78F0DABDF2F360EABFFD5ED0EAB996EB755383EBAE08FED1675FEAFF11FC95CE2AA93F1E71D8E3DD3627FC46DC2FC0D0A8A659D1C63E8D79F45B5F35CF2936EB30EFB77B86E015064CE4DABF8F30ED33F62F439D8F9AD2D63F44316A87BA36FB85A6874A8A63BB7C91CE1F7C18D0E014CB758164EE95BEBF229DC58E2951773923D32FF9AAA3ABB182FDAF286179773E39B4EE09E61D67B6C7292F3B5B83E424D8E6055BAB66F36976F3617146E94F9ED41D964E56F74DAAF17734A7B57
	A2E72F149359EE48C96CB4647C57B549B16C796002A16797AEC30E33797A6CC9F79BF2FE65B164C4DD139C431E8C2FA187E70D10F3775A6B13F3C70F41FBEF29B93FE7F732E665E452B1F6B95DEAA57BABCCA23E912F26FF175E82EBD6FFD466BBF9089B8365090F8A9F73B20A626B7F6726770E7CAFBF3F8F7BBF5051D8E03469F3537BAFB7FE793A17FE29BA58189E2539CCB40C678CB94EAB69DD3AB9D00E13B80FCD640371862EC9709E78067CC6EA973C75927734DD70B6CBDCC41D600D1638B19B846FCC09
	7B5586417B6CB7C45C34135EE152B1FB44798F6FAC462920F3F6DDF4EC6A56E8D1776D3CE522AE916B6209DE9CC7C67F6CEAE01C76B24D7D9A017F3C1431599698277D288F79D91CF62B6DDA4EF98568266F02B496839D18EF9AF15A7D34CFA782BEBADA444D09FEC35FDC7B3564A921744D9F226F1F69B8AC847CA229EF6DA5C39F493B163CA014BE7BE3AC6FC5FDCC76DF130C3E7FC99E7E2F8A8BFFD6819B3C8B5CD989F7E7FC5C7EA15960DD1AF4FD3E7027B8047CFDDF1AA1275EFEFDF25A528DB97F324210
	B330617A643C0661BBE17F6FA33262F7A0E95A1BE4994CE51D14D7B93DEA9B64587D769E1F6A3037765B75CE556ED33D3D2A575E2EF92168745945FF3A7059BD72EF66F5123E472136ABBDAEDDA75B4FF6AA3ADD25B73C6CED6C0C5E2E7539FDC17CDE12A5ED2E250144025E8F4B13DF1F1AECA3A935A41585D635A4852323DA320C86CF35A49982ABBF6EF2A9FC30B1E0A03E0BE410E212B976F11C12921BE34226C82CB8D6C61E8C09CDB7E1178554A51C1AAE31F5A62CD1F7252887102913D555CEEF373DC16B
	03EE24DF944BF5E26BBA2B138DD52C4F6C7C069E1E045A59B5BDD1A58BEF58596548GFA4DA9BCEC35339BF4F67E8D249CFAB3B3E086FCFF723EDF4E79F913AF192456127751511CD63DB426D29DF8BF1A7FE302EA325C23B90188F868EA9FFDAC73697DAE351E64367F857F2056272B0E8A912D41D82F29C7CDFB70B0EDCF0D626B54B1AABED4CFD25CB89D48EAC6F6A96E0EBDACF4BBC447AB2962BC6D2BF769F8F9C98E35972594C9A9B97F1435ED530905B312092D1A2CF0E90A23DA4106571265FA27D775F5
	EAAE075F7349915669742C05FE4B82F19BE4733131C57815C89A5D65FFF0ECE6FCE6350D903F176C21B5512F96499A0E4ACE184DEDB019EBF57C17901AF72B245D34D15F7E427135ADB612449B09833D466F51C9EA3B526DF47543CB95DDEFF48A3C2F0335DE456D43BF31834B6CC1168912D4D0F44FF34577BE7676DF298203D5A7852CAB01F5CF072AF56F29E8EF0718C98A00FE857179D2BC56A98A2A335AFF571A93AFAFED05F9D0C5123B0B0B696F927AFBAF7EAE2118920AA9598B1D3B088C7B93CC770BB6
	5349A3D7B58EFFD1B50C207E33B71F1B724A61C589503AEA320C7EC99D8CC236D10BFD669BBD6D5E7C15918B6E40B17260D471235CFC3FDCD529FCC551E40FE2692A2EE013A1C3FA0135C3278722B748C14252C6DA55CEFFEF396EA6A357B64A3B835553721F7D819E841F3875E719ACB54F7F7F87CBC51F169A16AEA7063FCB7B6A7569A1FF00CF95FD1A7E556B53230173F4B92F3B622BE7E92DE169C9132569BA49947F38410E1F8D314CB7EF7C428EBBCFA67DA8EFE74B954AAB2B953FA8D936B3A9C4BBD382
	5BA99CC8AABB06E076A24F1753810D7E2F5E20FCEE8C0A382306BF362F1E258E43D261C0F04227FD75AC6DB2AC459091F53FC74C4BF367D573BD0345372C8BF94FE07EF4F60618B05D3817413EE7C552733B4227E2D32B1E925F33620F619D65D0F774CEF7ABAE172E383AAA5C8E3C2FF734DFD70DEF6D8FD623FC1F2843090C96C54F7BAE18F87E97D0CB87888755B14CA79AGG94D0GGD0CB818294G94G88G88G83F954AC8755B14CA79AGG94D0GG8CGGGGGGGGGGGGGGGGGE2
	F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGE19AGGGG
**end of data**/
}
}
