package com.cannontech.dbeditor.wizard.device.customer;

/**
 * This type was created in VisualAge.
 */
import java.awt.Dimension;
import com.cannontech.database.data.customer.CustomerContact;

public class CustomerContactPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener {
	private javax.swing.JButton ivjJButtonAdd = null;
	private javax.swing.JButton ivjJButtonRemove = null;
	private javax.swing.JLabel ivjJLabelFirstName = null;
	private javax.swing.JLabel ivjJLabelLocationRec = null;
	private javax.swing.JLabel ivjJLabelNormalLastName = null;
	private javax.swing.JScrollPane ivjJScrollPaneContactTable = null;
	private javax.swing.JTable ivjJTableContact = null;
	private javax.swing.JTextField ivjJTextFieldFirstName = null;
	private javax.swing.JTextField ivjJTextFieldLastName = null;
	private javax.swing.JTextField ivjJTextFieldPhone1 = null;
	private javax.swing.JPanel ivjJPanelContact = null;
	private javax.swing.JComboBox ivjJComboBoxRecipient = null;
	private javax.swing.JTextField ivjJTextFieldPhone2 = null;
	private javax.swing.JLabel ivjJLabelAssignedContacts = null;
	private javax.swing.JLabel ivjJLabelPhone = null;
	private javax.swing.JLabel ivjJLabelPhoneDirect = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public CustomerContactPanel() {
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
	if (e.getSource() == getJButtonAdd()) 
		connEtoC3(e);
	if (e.getSource() == getJButtonRemove()) 
		connEtoC4(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC3:  (JButtonAdd.action.actionPerformed(java.awt.event.ActionEvent) --> LMControlAreaTriggerPanel.jButtonAdd_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonAdd_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (JButtonRemove.action.actionPerformed(java.awt.event.ActionEvent) --> LMControlAreaTriggerPanel.jButtonRemove_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonRemove_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the JButtonAdd property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonAdd() {
	if (ivjJButtonAdd == null) {
		try {
			ivjJButtonAdd = new javax.swing.JButton();
			ivjJButtonAdd.setName("JButtonAdd");
			ivjJButtonAdd.setMnemonic('a');
			ivjJButtonAdd.setText("Add");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonAdd;
}
/**
 * Return the JButtonRemove property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonRemove() {
	if (ivjJButtonRemove == null) {
		try {
			ivjJButtonRemove = new javax.swing.JButton();
			ivjJButtonRemove.setName("JButtonRemove");
			ivjJButtonRemove.setMnemonic('r');
			ivjJButtonRemove.setText("Remove");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonRemove;
}
/**
 * Return the JComboBoxPoint property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxRecipient() {
	if (ivjJComboBoxRecipient == null) {
		try {
			ivjJComboBoxRecipient = new javax.swing.JComboBox();
			ivjJComboBoxRecipient.setName("JComboBoxRecipient");
			ivjJComboBoxRecipient.setToolTipText("Recipient entry that is used to communicate with this contact");
			// user code begin {1}

			com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
			synchronized( cache )
			{
				java.util.List recipients = cache.getAllNotificationRecipients();

				java.util.Collections.sort( recipients, com.cannontech.database.data.lite.LiteComparators.liteStringComparator );
				
				for( int i = 0; i < recipients.size(); i++ )
				{
					ivjJComboBoxRecipient.addItem( (com.cannontech.database.data.lite.LiteNotificationRecipient)recipients.get(i) );
				}
			}

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxRecipient;
}
/**
 * Return the JLabelExisTriggers property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelAssignedContacts() {
	if (ivjJLabelAssignedContacts == null) {
		try {
			ivjJLabelAssignedContacts = new javax.swing.JLabel();
			ivjJLabelAssignedContacts.setName("JLabelAssignedContacts");
			ivjJLabelAssignedContacts.setFont(new java.awt.Font("Arial", 1, 14));
			ivjJLabelAssignedContacts.setText("Assigned Contacts");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelAssignedContacts;
}
/**
 * Return the JLabelType property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelFirstName() {
	if (ivjJLabelFirstName == null) {
		try {
			ivjJLabelFirstName = new javax.swing.JLabel();
			ivjJLabelFirstName.setName("JLabelFirstName");
			ivjJLabelFirstName.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelFirstName.setText("First Name:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelFirstName;
}
/**
 * Return the JLabelPoint property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelLocationRec() {
	if (ivjJLabelLocationRec == null) {
		try {
			ivjJLabelLocationRec = new javax.swing.JLabel();
			ivjJLabelLocationRec.setName("JLabelLocationRec");
			ivjJLabelLocationRec.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelLocationRec.setText("Recipient:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelLocationRec;
}
/**
 * Return the JLabelNormalStateAndThreshold property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelNormalLastName() {
	if (ivjJLabelNormalLastName == null) {
		try {
			ivjJLabelNormalLastName = new javax.swing.JLabel();
			ivjJLabelNormalLastName.setName("JLabelNormalLastName");
			ivjJLabelNormalLastName.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelNormalLastName.setText("Last Name:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelNormalLastName;
}
/**
 * Return the JLabelPhone1 property value.
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
 * Return the JLabelPhone2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelPhoneDirect() {
	if (ivjJLabelPhoneDirect == null) {
		try {
			ivjJLabelPhoneDirect = new javax.swing.JLabel();
			ivjJLabelPhoneDirect.setName("JLabelPhoneDirect");
			ivjJLabelPhoneDirect.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelPhoneDirect.setText("Direct Phone Number:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelPhoneDirect;
}
/**
 * Return the JPanelTrigger property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelContact() {
	if (ivjJPanelContact == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Contact Creation");
			ivjJPanelContact = new javax.swing.JPanel();
			ivjJPanelContact.setName("JPanelContact");
			ivjJPanelContact.setBorder(ivjLocalBorder);
			ivjJPanelContact.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabelFirstName = new java.awt.GridBagConstraints();
			constraintsJLabelFirstName.gridx = 1; constraintsJLabelFirstName.gridy = 1;
			constraintsJLabelFirstName.gridwidth = 2;
			constraintsJLabelFirstName.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelFirstName.ipadx = 5;
			constraintsJLabelFirstName.ipady = -2;
			constraintsJLabelFirstName.insets = new java.awt.Insets(7, 5, 4, 0);
			getJPanelContact().add(getJLabelFirstName(), constraintsJLabelFirstName);

			java.awt.GridBagConstraints constraintsJLabelLocationRec = new java.awt.GridBagConstraints();
			constraintsJLabelLocationRec.gridx = 1; constraintsJLabelLocationRec.gridy = 5;
			constraintsJLabelLocationRec.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelLocationRec.ipadx = 9;
			constraintsJLabelLocationRec.ipady = -2;
			constraintsJLabelLocationRec.insets = new java.awt.Insets(9, 5, 15, 4);
			getJPanelContact().add(getJLabelLocationRec(), constraintsJLabelLocationRec);

			java.awt.GridBagConstraints constraintsJComboBoxRecipient = new java.awt.GridBagConstraints();
			constraintsJComboBoxRecipient.gridx = 2; constraintsJComboBoxRecipient.gridy = 5;
			constraintsJComboBoxRecipient.gridwidth = 3;
			constraintsJComboBoxRecipient.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxRecipient.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxRecipient.weightx = 1.0;
			constraintsJComboBoxRecipient.ipadx = 26;
			constraintsJComboBoxRecipient.insets = new java.awt.Insets(6, 1, 12, 46);
			getJPanelContact().add(getJComboBoxRecipient(), constraintsJComboBoxRecipient);

			java.awt.GridBagConstraints constraintsJLabelNormalLastName = new java.awt.GridBagConstraints();
			constraintsJLabelNormalLastName.gridx = 1; constraintsJLabelNormalLastName.gridy = 2;
			constraintsJLabelNormalLastName.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelNormalLastName.ipadx = 5;
			constraintsJLabelNormalLastName.ipady = -2;
			constraintsJLabelNormalLastName.insets = new java.awt.Insets(4, 5, 3, 0);
			getJPanelContact().add(getJLabelNormalLastName(), constraintsJLabelNormalLastName);

			java.awt.GridBagConstraints constraintsJTextFieldPhone1 = new java.awt.GridBagConstraints();
			constraintsJTextFieldPhone1.gridx = 4; constraintsJTextFieldPhone1.gridy = 3;
			constraintsJTextFieldPhone1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldPhone1.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldPhone1.weightx = 1.0;
			constraintsJTextFieldPhone1.ipadx = 108;
			constraintsJTextFieldPhone1.insets = new java.awt.Insets(3, 0, 1, 17);
			getJPanelContact().add(getJTextFieldPhone1(), constraintsJTextFieldPhone1);

			java.awt.GridBagConstraints constraintsJButtonAdd = new java.awt.GridBagConstraints();
			constraintsJButtonAdd.gridx = 5; constraintsJButtonAdd.gridy = 5;
			constraintsJButtonAdd.anchor = java.awt.GridBagConstraints.SOUTHEAST;
			constraintsJButtonAdd.ipadx = 18;
			constraintsJButtonAdd.insets = new java.awt.Insets(5, 18, 11, 14);
			getJPanelContact().add(getJButtonAdd(), constraintsJButtonAdd);

			java.awt.GridBagConstraints constraintsJTextFieldFirstName = new java.awt.GridBagConstraints();
			constraintsJTextFieldFirstName.gridx = 3; constraintsJTextFieldFirstName.gridy = 1;
			constraintsJTextFieldFirstName.gridwidth = 3;
			constraintsJTextFieldFirstName.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldFirstName.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldFirstName.weightx = 1.0;
			constraintsJTextFieldFirstName.ipadx = 286;
			constraintsJTextFieldFirstName.insets = new java.awt.Insets(7, 1, 1, 13);
			getJPanelContact().add(getJTextFieldFirstName(), constraintsJTextFieldFirstName);

			java.awt.GridBagConstraints constraintsJTextFieldLastName = new java.awt.GridBagConstraints();
			constraintsJTextFieldLastName.gridx = 3; constraintsJTextFieldLastName.gridy = 2;
			constraintsJTextFieldLastName.gridwidth = 3;
			constraintsJTextFieldLastName.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldLastName.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldLastName.weightx = 1.0;
			constraintsJTextFieldLastName.ipadx = 286;
			constraintsJTextFieldLastName.insets = new java.awt.Insets(2, 1, 2, 13);
			getJPanelContact().add(getJTextFieldLastName(), constraintsJTextFieldLastName);

			java.awt.GridBagConstraints constraintsJLabelPhone = new java.awt.GridBagConstraints();
			constraintsJLabelPhone.gridx = 1; constraintsJLabelPhone.gridy = 3;
			constraintsJLabelPhone.gridwidth = 3;
			constraintsJLabelPhone.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelPhone.ipadx = 47;
			constraintsJLabelPhone.ipady = -2;
			constraintsJLabelPhone.insets = new java.awt.Insets(5, 5, 2, 0);
			getJPanelContact().add(getJLabelPhone(), constraintsJLabelPhone);

			java.awt.GridBagConstraints constraintsJLabelPhoneDirect = new java.awt.GridBagConstraints();
			constraintsJLabelPhoneDirect.gridx = 1; constraintsJLabelPhoneDirect.gridy = 4;
			constraintsJLabelPhoneDirect.gridwidth = 3;
			constraintsJLabelPhoneDirect.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelPhoneDirect.ipadx = 6;
			constraintsJLabelPhoneDirect.ipady = -2;
			constraintsJLabelPhoneDirect.insets = new java.awt.Insets(4, 5, 5, 0);
			getJPanelContact().add(getJLabelPhoneDirect(), constraintsJLabelPhoneDirect);

			java.awt.GridBagConstraints constraintsJTextFieldPhone2 = new java.awt.GridBagConstraints();
			constraintsJTextFieldPhone2.gridx = 4; constraintsJTextFieldPhone2.gridy = 4;
			constraintsJTextFieldPhone2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldPhone2.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldPhone2.weightx = 1.0;
			constraintsJTextFieldPhone2.ipadx = 108;
			constraintsJTextFieldPhone2.insets = new java.awt.Insets(2, 0, 4, 17);
			getJPanelContact().add(getJTextFieldPhone2(), constraintsJTextFieldPhone2);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelContact;
}
/**
 * Return the JScrollPaneTriggerTable property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneContactTable() {
	if (ivjJScrollPaneContactTable == null) {
		try {
			ivjJScrollPaneContactTable = new javax.swing.JScrollPane();
			ivjJScrollPaneContactTable.setName("JScrollPaneContactTable");
			ivjJScrollPaneContactTable.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPaneContactTable.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			getJScrollPaneContactTable().setViewportView(getJTableContact());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneContactTable;
}
/**
 * Return the JTableTrigger property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getJTableContact() {
	if (ivjJTableContact == null) {
		try {
			ivjJTableContact = new javax.swing.JTable();
			ivjJTableContact.setName("JTableContact");
			getJScrollPaneContactTable().setColumnHeaderView(ivjJTableContact.getTableHeader());
			getJScrollPaneContactTable().getViewport().setBackingStoreEnabled(true);
			ivjJTableContact.setBounds(0, 0, 200, 200);
			// user code begin {1}

			ivjJTableContact.getSelectionModel().setSelectionMode( javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
			ivjJTableContact.setModel( new CustomerContactTableModel() );

			//do any column specific things here
			javax.swing.table.TableColumn name = ivjJTableContact.getColumnModel().getColumn(CustomerContactTableModel.COLUMN_NAME);
			name.setPreferredWidth(50);
			javax.swing.table.TableColumn phone1 = ivjJTableContact.getColumnModel().getColumn(CustomerContactTableModel.COLUMN_PHONE1);
			phone1.setPreferredWidth(10);
			javax.swing.table.TableColumn phone2 = ivjJTableContact.getColumnModel().getColumn(CustomerContactTableModel.COLUMN_PHONE2);
			phone2.setPreferredWidth(10);
			javax.swing.table.TableColumn rec = ivjJTableContact.getColumnModel().getColumn(CustomerContactTableModel.COLUMN_RECIPIENT);
			rec.setPreferredWidth(50);

			
/*			javax.swing.table.TableColumn primeContact = ivjJTableContact.getColumnModel().getColumn(CustomerContactTableModel.COLUMN_PRIME_CONTACT);
			primeContact.setPreferredWidth(40);
			
			// Create and add the column renderers	
			com.cannontech.common.gui.util.CheckBoxTableRenderer cbRender = new com.cannontech.common.gui.util.CheckBoxTableRenderer();
			cbRender.setHorizontalAlignment(javax.swing.JCheckBox.CENTER);
			primeContact.setCellRenderer(cbRender);

			// Create and add the column CellEditors
			javax.swing.JCheckBox chkBox = new javax.swing.JCheckBox();
			chkBox.setHorizontalAlignment(javax.swing.JCheckBox.CENTER);
			chkBox.setBackground(ivjJTableContact.getBackground());
			primeContact.setCellEditor( new javax.swing.DefaultCellEditor(chkBox) );

		
			//javax.swing.ButtonGroup group = new javax.swing.ButtonGroup();
			//group.add(chkBox);
*/			
	
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTableContact;
}
/**
 * Insert the method's description here.
 * Creation date: (3/19/2001 5:10:19 PM)
 * @return com.cannontech.dbeditor.wizard.device.lmcontrolarea.TriggerTableModel
 */
private CustomerContactTableModel getJTableModel() 
{
	return (CustomerContactTableModel)getJTableContact().getModel();
}
/**
 * Return the JTextFieldFirstName property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldFirstName() {
	if (ivjJTextFieldFirstName == null) {
		try {
			ivjJTextFieldFirstName = new javax.swing.JTextField();
			ivjJTextFieldFirstName.setName("JTextFieldFirstName");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldFirstName;
}
/**
 * Return the JTextFieldLastName property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldLastName() {
	if (ivjJTextFieldLastName == null) {
		try {
			ivjJTextFieldLastName = new javax.swing.JTextField();
			ivjJTextFieldLastName.setName("JTextFieldLastName");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldLastName;
}
/**
 * Return the JTextFieldThreshold property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldPhone1() {
	if (ivjJTextFieldPhone1 == null) {
		try {
			ivjJTextFieldPhone1 = new javax.swing.JTextField();
			ivjJTextFieldPhone1.setName("JTextFieldPhone1");
			ivjJTextFieldPhone1.setToolTipText("Primary phone number");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldPhone1;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldPhone2() {
	if (ivjJTextFieldPhone2 == null) {
		try {
			ivjJTextFieldPhone2 = new javax.swing.JTextField();
			ivjJTextFieldPhone2.setName("JTextFieldPhone2");
			ivjJTextFieldPhone2.setToolTipText("Secondary phone number");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldPhone2;
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
	com.cannontech.database.data.customer.CICustomerBase customer = (com.cannontech.database.data.customer.CICustomerBase)o;

	//get rid of all the previous contacts
	customer.getCustomerContactVector().removeAllElements();

	for( int i = 0; i < getJTableModel().getRowCount(); i++ )
	{
		CustomerContact contact = new CustomerContact();
		
		if( getJTableModel().getRowAt(i).getCustomer() == null )
		{
			//build a totally new CustomerContact object
			CustomerContactTableModel.CustomerContactRow row = getJTableModel().getRowAt(i);
				
			contact.getCustomerContact().setContFirstName( row.getFirstName() );
			contact.getCustomerContact().setContLastName( row.getLastName() );

			if( row.getPhone1() != null )
				contact.getCustomerContact().setContPhone1( row.getPhone1() );

			if( row.getPhone2() != null )	
				contact.getCustomerContact().setContPhone2( row.getPhone2() );
				
			contact.getCustomerContact().setLocationID( new Integer(row.getLiteRecipient().getRecipientID()) );
			//contact.setLogInID();
		}
		else
		{  // we have a CustomerContact already
			contact = ((CustomerContactTableModel.CustomerContactRow)getJTableModel().getRowAt(i)).getCustomer();
		}
		
		customer.getCustomerContactVector().addElement( contact );
	}
		
	return o;
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
	getJButtonAdd().addActionListener(this);
	getJButtonRemove().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("CustomerContactPanel");
		setToolTipText("");
		setLayout(new java.awt.GridBagLayout());
		setSize(416, 348);

		java.awt.GridBagConstraints constraintsJPanelContact = new java.awt.GridBagConstraints();
		constraintsJPanelContact.gridx = 1; constraintsJPanelContact.gridy = 1;
		constraintsJPanelContact.gridwidth = 2;
		constraintsJPanelContact.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelContact.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelContact.weightx = 1.0;
		constraintsJPanelContact.weighty = 1.0;
		constraintsJPanelContact.ipadx = -65;
		constraintsJPanelContact.ipady = -14;
		constraintsJPanelContact.insets = new java.awt.Insets(14, 8, 3, 8);
		add(getJPanelContact(), constraintsJPanelContact);

		java.awt.GridBagConstraints constraintsJScrollPaneContactTable = new java.awt.GridBagConstraints();
		constraintsJScrollPaneContactTable.gridx = 1; constraintsJScrollPaneContactTable.gridy = 3;
		constraintsJScrollPaneContactTable.gridwidth = 2;
		constraintsJScrollPaneContactTable.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPaneContactTable.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJScrollPaneContactTable.weightx = 1.0;
		constraintsJScrollPaneContactTable.weighty = 1.0;
		constraintsJScrollPaneContactTable.ipadx = 378;
		constraintsJScrollPaneContactTable.ipady = 102;
		constraintsJScrollPaneContactTable.insets = new java.awt.Insets(1, 8, 17, 8);
		add(getJScrollPaneContactTable(), constraintsJScrollPaneContactTable);

		java.awt.GridBagConstraints constraintsJButtonRemove = new java.awt.GridBagConstraints();
		constraintsJButtonRemove.gridx = 2; constraintsJButtonRemove.gridy = 2;
		constraintsJButtonRemove.anchor = java.awt.GridBagConstraints.EAST;
		constraintsJButtonRemove.ipadx = 19;
		constraintsJButtonRemove.insets = new java.awt.Insets(3, 61, 2, 10);
		add(getJButtonRemove(), constraintsJButtonRemove);

		java.awt.GridBagConstraints constraintsJLabelAssignedContacts = new java.awt.GridBagConstraints();
		constraintsJLabelAssignedContacts.gridx = 1; constraintsJLabelAssignedContacts.gridy = 2;
		constraintsJLabelAssignedContacts.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelAssignedContacts.ipadx = 46;
		constraintsJLabelAssignedContacts.ipady = 7;
		constraintsJLabelAssignedContacts.insets = new java.awt.Insets(7, 8, 0, 60);
		add(getJLabelAssignedContacts(), constraintsJLabelAssignedContacts);
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

	return true;
}
/**
 * Comment
 */
public void jButtonAdd_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( (getJTextFieldFirstName().getText() == null || getJTextFieldFirstName().getText().length() <= 0)
		 || (getJTextFieldLastName().getText() == null || getJTextFieldLastName().getText().length() <= 0) )
		return;

	String phone1 = ( (getJTextFieldPhone1().getText() == null || getJTextFieldPhone1().getText().length() <= 0)
							? "(none)" : getJTextFieldPhone1().getText());
	
	String phone2 = ( (getJTextFieldPhone2().getText() == null  || getJTextFieldPhone2().getText().length() <= 0)
							? "(none)" : getJTextFieldPhone2().getText());
		
	getJTableModel().addRow( getJTextFieldFirstName().getText(),
			getJTextFieldLastName().getText(), 
			phone1,
			phone2,
			(com.cannontech.database.data.lite.LiteNotificationRecipient)getJComboBoxRecipient().getSelectedItem() );
	
	fireInputUpdate();
	return;
}
/**
 * Comment
 */
public void jButtonRemove_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( getJTableContact().getSelectedRow() >= 0 )
	{
		for( int i = (getJTableContact().getSelectedRows().length-1); i >= 0; i-- )
			getJTableModel().removeRow( getJTableContact().getSelectedRows()[i] );
	}

	getJTableContact().clearSelection();
	fireInputUpdate();
	return;
}
/**
 * Comment
 */
public void jComboBoxPoint_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
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
		CustomerContactPanel aCustomerContactPanel;
		aCustomerContactPanel = new CustomerContactPanel();
		frame.setContentPane(aCustomerContactPanel);
		frame.setSize(aCustomerContactPanel.getSize());
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

	for( int i = 0; i < customer.getCustomerContactVector().size(); i++ )
	{
		CustomerContact contact = (CustomerContact)customer.getCustomerContactVector().get(i);
		
		com.cannontech.database.data.lite.LiteNotificationRecipient recipient = 
				com.cannontech.database.cache.functions.NotificationGroupRecipientFuncs.getLiteNotificationRecipient(
							contact.getCustomerContact().getLocationID().intValue() );
		
		getJTableModel().setCustomerContactRow( contact, recipient );
	}

}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G8CF954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8BF4D55515A09122A2C68D8EA0361186252B2C1A695016E9191626B8250AA3536214D65A620816690AAEE624ADED19F2728F90A0FC0404GFDC89030B484C192A2D4728193F8C2CCD28808C95E67A6F970726E4BFDB7494B87183D4F6F5E77F2DF52D01DA50B4D3DE76F33BF673777BE671E8759724738746414AD04A427937E27B8199077B9C2227F6D51AF8A5C88A5B103187E6C8658C8FE3BB18D
	1EB1C04BCEC84C48A155FDD9D00E47726309995B60FDB3B95FF7358CDEA268B98A340671DF7D6160FAF6DD92FAB2E939BFAF9B1E6B8126818E1F4B95549F1B174B7173B8DEC0F28AA10BDB304E93F9BBB82E844AB5G138192DAD87D3970DCCDB55FD9DA426BDD3DBF0EACFFEADF4ABA14E312A9E0FFAB633573270DBC113C3C95F5AD24F6A2CF9414E781E063D3097F72A9F81E28395E7A97175ADE68F278FC2ACFD7DCED055D1EDE0766AEF4ABDD9E17D2686A8C68EA3B221516D670378A2867F069B58E1F6275
	37218E5B76ACAA935A8365159477C32AF7863C4F83D8A2711336A37E14A7B12381406F91EDBD396F84EFCBC35F93E4650FFC299BA234B55A435A1860B15A9A5B793B4CBCBAAE0D8D587FC9C04B391618D1GD0855086908D10CF6703F269ADF816B9758A353DDD7515797D65DE3776016AD63C70DEDA8A940E2BD5DC2A66A6040D794F728EDA58B398B03E24E17331BE19F6C40C7F745A474822FFBA1712E951A70B26952544DDE3FDB1731A51A7CC775E6D11F4AF3C96BABFC3F5DFA2E65DC9235554F48B5DB31E
	BB10124465106B066E226BB8566F53313641FBB21F9F8C7FA1456BA6BC1B2BBF26F3864BD1C0DBFADD4C0D06754296FB5AA412F358EE598F93924BD26CDEE6035B9BEE4B02C6149D63E5BA5325ADBF2778F8931E5952C271D81E89349D6D0999ECFE35DE4135BB824A1381E6G4C83C8824872C1BD1F18E3DF3EF468EF1CE3B58ECDAF6F74F85D0AC698EFEB43D6F8AAC1DDF3D47B823A4367D2AEBAB40F4369D582045771B7E09FB8C378BA695CF7010E77951F22B9F40F6A83690EF6C5D734G2BF333E1A35651
	94CA2D7734AB20205DCF045C518D1B6169F584743F7A5D8EDDB1EAB07A38869C13AE47C5108882601D75CB6CC758DFA7207D75GEE598F2BAF233CF30A865E2234542E2A57BB7D755892B243077DFC027A0EA03C4778C49F47DCC55C8A242BC24E294B1BCCF2AABDAEB45F217508B57641F6744F852A18B32FB608B9D339EEAAB9785CD94BB9C369AA1BABD7557035F3094AC43BD87967F98DE13C852A759A3A9DE0BAD68C50117A8949DFAF476A8F9F63D8C5016DF1G33E59F77DD3631B9DB6E88A8E216DE50E0
	02A9EE2347191DBFE94C626BA3D35A7E9245AD84F944AF7A4C5BE8EE0F287F4976591DA1BAD2391ECFCA876B1B0EAB980F14AE4527D7AAFE45670617G27852F601C636B35C515BCB7A95E8F6B3B8296D4C062232E609AF4683A66F1F66ACA1D226B9EDF2BC19FCBF585BA1DFE43B588DD8FDE4136295D303EE57D89D7F0ED38D41FCF2103A4D9B8FD92DD1B0AD7E98723AB54CE1F4E70F19F617C3E2E74F05F907F91A6BEDD8EEFA77A9916A7A4B856E29BB4CFBB8CBD3AAAB2560F6B36EEFB2E290F76838E731D
	E6F86AFE31863B7708B5D8507BA8195D34B745AA06CC18DA1932404F5651D23F318619CD632FDA5B7EB075919C9FC825381C77A039A3D6F98A1B7745CDA8AB2B837CB440518EB16F37FD0CF461DB78042FD03DDE561D321F539AF3F0DC140ECE85DCB2BA31F3326F56B6EE8A23DD647D48681B9B319F3DA67F172DE1BFEEEB9239209D6CF183C4F5303E1C53417A0255FBDE56EB0032DFB36A45E82C9E9B2F4CA60C77D6F9DDEB272730D3772077F468B0D17D1D669C6E4856AC3E1E96EAA2D71BC163F1CEA0B1E3
	BF4029009857FFDFA7F235586BF1E4719736C74CCB9B824C367E00B12E36D1A2D79B590CBAE781EDDEC00C4538C6F33CA88B84D4170706AA5E0FA71B4C634EF065CDEB6059625182FAB5AC4C2068732AA63497E399C77358FE2AC944764520B7D7075CDBC7FCF67796136DB5008B82CC86E0736B712637F15D52C9D1489EB50A5622EA6D0AFB7FB5C7172350512D97D29FD1D8C64927717D00981B5F9DBC8E4FEB651D3A8E99015B5DD496A6439ED9483B87AE92111BCD59FC0A5BB86F4B134809DB1BD32CF26568
	8DBBD3D6683C5FBB0D7E6F7FCAC6EAD2636FB3D9DF24B50B3E70C21D78CE51971E6361FD01F1F0D12758BF146CBFE13425D6E9D73B14E1B607796E122DB8DF17F59AF1B42D4BF84F6D9A98DFD95E361F6610873BD85E36DA625F2178AA931E35739BF4AEE3598D3439DDA25748F7229FBB588D7B8400E600A0408400195DE2CE4EEFBAC20648652AE10DE9BE0737AE44E90B1C2AB91579D9DE3E3BB9DD78D9A2E258192670BCFDC1F7E85B17051599DF7DG3EDDE165AD41D0BEB6FE9B6A72041F40D18A96863AA1
	8615161EE5A38AB466675590BFFFB04866C18DBC5DG4CF7D4F48B5F519F94BE624699330F1835616D143591FC448CEE5F0220B1C78F4A7D5C13AE74FBDBFA92B30AG2AG5CG51G71GF3FB4498EDF11C9CEA0C68442BD7ABGD785FE38A2BCDE4ABEDD53FCFBE311BB00AFFCAC16760472317E0BBBAE7A0F74421E0266459728BFAE01F20D44FD0D622EC259AFF1A3F391978B65693DA267AA5C0EF1EABE1413GF27A92B38E76895A3B1406FE2D260F4591DD52E607641E48E33695E5D965C7B9FD837173AD
	6AB29DBF8E5131BA2CCE243C4DAAE7344A0B59DCA8F0C64E557E64344E558EBB2DF335E267C6068799661D1A203F679CBC17FB5FE9154B6D2476FFFF6B86AABB90D082F4F2E2E06634DF682F930B1C6EBCEC66F85BB7139BB43EE11D533ADAF1C42E6D578E64DA2FEDD33CFFFC3B18F331FDA25E175B50C74E0372924449F9D1559DBE2F3774C3CE504F4678C47F4031E6EB572EE08C29813A97A08AE0B2402CFE31E6FF6A9C522F1E8E8E62D71FF5C59E677FF0D90F73A29766AA8135D3F3A961E37922ABD7528C
	6FD02DAB6D586D2CCE9265579DDA2BB2E0AE3CC67999AD927FCAD724794246342C59BC26C7653A19FABCFC8C96C1BFAE63FD1FF6E36098B0F9ADDB516F6A2A6A2D77786B21BFC86E8DBC5FAC7292D967192D1877FB9DBDEA27CE485C9BB89716EC95FE7FAA14F50049G89B74C3966BFF6236C964D518ECD2873ABAE92FF93F9A7648A5E9CA897GD482388122EE0ADC4A590B7D48E6A44F5425BD39BB715C76EC1DCB039C9CEB708AACEFE6F55EAB277C667CC3729F4845731333F60793A6D2A060E975E1BACFC5
	84C4DC2B8C1A63DA52CD9657F2EF012F3CA5F220BF7518F3204ADB2CCE5BAD31AE1C7966B169C77AADE39C62EFC54ADDFECD57594CDBACC759A671EBF6A3FE19894FF217AADA9F4B7BC7405A9CA1FCFF930D3BF1A321BCD26026D15CDCA8AF1438F5C191A3160C94EB2FDE093CF6BED22C570EC33176112D4A60BED2D5A24DF9E65F2C63229DC76F90B613D2510EAA09F3140AF6545FA1720CE7BFB21F1167CC5A1D02FDE715E7445F414F0D6F88BFA34F5EA64E4817GED47A811639EEE465CC37AD14CE4CBCB5F
	57BC6EF2C72B1D2E9666CB3F32AD0B73570F923EF4B115B9964A71G33C689FB03EF0A3CE87AE31308FD44B94B3D53627A37D3960CE2F62E98E558BB8D72A22673DB39C26751A8702FD108CB4AB54FD7DC8B7F1CC2C8A12EEDCA5F15E7266318E02CB168B5A1F43437AC55CC3FA3040EFD340F733336BD3DD734ED65535308772B47A36E8BE7C431B6AD644F1A0BFBC8415D3F4FE47D5EFAFAA7310CDF06C7A0A2F76A8F0AB816F327687B5EE3F48FG65E600601DE2BCDFEB0990972927E0721CBDC2DE4C1DE25E
	756453398865C592772BDA4411510999DBC68B1C33DE4C4F125142C72C34210F0806F2BC409C00A5GE9E392B3ECGA5G8DGC100980004B102AFE7B7724D03F292C0F6B478A5GFBB4BBA37042F3AC40F400F9GC951026F3CAD81677818D05F037A62396E18B156F99CC203852DA48C07ED0844AF40465BA44A51C370B05BEFD05B37055156723906F36C75C1E4EC3440ED3050B3146DB8E7B7072D23416A331853C57DEE3B8FA2114F635AF1974E07FBA96E3BG59F70938776DEEBCF353413932B8DC6243E9
	B40256AF88D68E22E7AD07D48E4D400330132FB19D5E7772F7267FA56AD7DF42398BE5266FA3555097AC9DCCDFBA91F1BED4DFBE5731905E7771F7166FE561E7846E73F5AF5AF251F10DE7F5275D9EDD55C4DAF796165F8567B585E3FA2B9CFF7F365B7B3EF673B6793EBEECBE96276BF3513FAC390B4569E4097F1862171970AC3E3CAC634BCA6C433B4559437E8E74C7F3203C98E085C04EBD304F85305FA36274ACEF9199A2C72ED77DF68864DED368E56D7BEE6E6DFDF77CCE267063B33EB1054C9C7D5B64C8
	FB7766FB583A9E411FCB6B77D0BCEB5FB7B5ECDF82506681AC83489A8B71956068D8513E3476032CFDB8C14AF448515A68491EE85CC50F52ED20B149E4ED9B9994ED2B82D9D747066D2B47C64A3DDE2531A1EAEC78B74995949FBBB67C1B6408E3621BE4BC50326E95FE3635475C375EFBF9BF70E76C3D11727118FC4C351D2AC693A416EBBF1EAFF251E940B79BE085C076B8F67E4878326B866668FB6A84DF41B816231F00E74DB8EE8BDD63BD75032DF1313645BAEF835EFE00BD3C9C836F37D62DDA7592G33
	E352B97429ACE21E4193D73AA3E5342B7BB7CA1ADDF551E35BDA589508365F6544B37233B0131DEA399A8412476F01D195F425EC0B64BF2FEA6D8E2F5D4194B07A6EFCBC3BB80B3B18B39E456B2EE9D3FD4AE1C62B3B04E740E765592A10F9F77566901EF01B1FFFEBD388DDE8947C01743552A62A0F635FD2B70662ABBD1AE25A4364DD343075ED362FB837BBFCDFC14F194709FD4738C6ABFA02245791B3FD8E1FA7C912FE34410A1F5CA7689F941B69DB6EE37493127E33F72C68B5127EE6500A6E16742F9F33
	529F14743916743112DEE1E9DF5CFDE2AD44F763F31FE39F77D7F15D66BDC0491AE24B6FCC541F5D476F5870672CB1FB489F890B77B41F63FFD82C5C58096FCC478B5942A76E7852432472C73FCD5E93C1870961B9ED0C1137AF3D36B213593E72B835DD5B47F50C6EC29666D32E649A6FAE53FB523670EF421FA4B09D69391F268EFE465691F94FD952E13D677CCB07751E3332E370F3B7FBC764EF28F59D66EF28ACD6DD245FFE22E14C62816682AC82D896A362548B9DEF909A27ACE254E9F7AB9ED160CD8DDD
	681F23C5EE6B535A5077BB96E8684F1CAA3833F614AF70BF2131DDD77D9449F0BF57300FB4CFEB1B4E2AF27CCB9A4EE92F52C251EC0FB2016E17326F07BC63FE31EF79EF0A2B0432D7626CBDE2AFB362FE9173169F435CF53EAC7F011613E4F97431614EA114B53CF97A79E16B984E7D8956761B346DB98F409E6D81519FCA39B80FB8AAF17FDAAE7A689460D83E70EC3E4897E2GB7778131777C454E83E4286F32759E5D2B147354006FEF376D9077AC4803721C3696F514C03986206DC1217BDC1D48B3C7GEE
	6603627B4C03F5622CE01914719DDAF7FFAC588E50GB0508FAEFB4F8A477C56FF6167D4EE639D3B451E599FABF4BE59A8F053E2C59F34FA7309657EDBE484DC5E5DC434F93114F73DC1484B9DAF646D5D3F43DA1E0C3A0CB72AD8482B99AF64BDDFA26EACC6CB5C47C1011BADF12F9E93FAD7C85CAA09ABFAC860FA250E3607C4FE7F901DC7F1D01E751070A3DD01BF11887BA2C056E82ADF51749E0625B78838FF7AC1AF66F10BC04E8A009D7FG7390208A2099C0870886188CB083E09E40E20065GD993E06F
	87F090605484E147454E830358418F3928D3539C9EF4EB5C27B4F6C776EBCA7750FE4DC777448A78237A9EBFBF938FF4E3EE2B3634849469BF035DB992D79D28D1839E4CA6D98CFDA208E784DD50D19E17434B8E054A9DDA0D6A7538FA785E77A9BA16FF86281FA072062C9E6C4BB6D5737462E970GCE31EFAE2063065F93E3011765512E1E217268CCE264518B01AFE90211C7130909993FCAA16499A2724313876879B5BD52B64E3F59597421E27359F476C4762D2E881E1593C5DB4677206FED81CEE6731728
	4DD7015EB6D16C753F5C0F2A197DBFAD9F4AFE2B3D3E48F1A6004C0F89BBCFE1FE78397C21761671932D3E113EB8A4DF78B7D34637E387724D1E7837450B85D00F6D6B3EC6FD65520961FB75EFD37C720961FB752ADA61C349A448E9A7095876E15D6D1DB1B45DA65F04E173717B8F75E87F5CC9616D7D8B45AF18945E5E6F49183198E8158F0B760E59ED56BF6DE11EAF73677C07A3FD5FFD2A514C370457DB411F5913A3715DC7104FE6C1C7B89A114F5FE056D7456BDD65CFFFC43E3B0A11AFAA023E89917996
	178CB6AE53A372B90303714D0E48775AB1B35F82DEEFA9FF2E0C4817D6EC664BF90455AB604F12C7A2715D22FCF68BBAC233851E5F476B41F7190F335045E9D368F7B40F8FDC38436B69256782F5D051574A68AF77C60EA92F748E9DD3FE5DEB1DAB2F6C9D18ABA777D9674A19FD66DC1961327B50A736C2ACEC1075B6CA5CE5095BCAE5A26E12472D37C97C0E3EB59CDF2520C201FF238F6D6A2638860E5BE742DD663803FD989721499032649E2A0F7AFD0F574B4A6F50BA9E9F04C9599F2554C60F5FE1327BBD
	09C33BD98CF9A1F8981E265429507405257062695A7A6A0AB2FBD3DDC56D853B3D3C2C36294CFE296CF21D64FF376758A07CD597EA2B2FDCB8DF9FAA212E697C69531527A1988DAF5FBCF993CBACC6DD6B0B709D07C6D3160F35960BDCCEFFC4643CDFABC1DFB79D4AF3G96BDA262640BB4C433B6DDAE475F7B7814EE1B3C3CF686026C7EB74AE052F95FB8E8BB8CFF0FE26E44F8E9AA4A6FEC4C5E3C0E0E0557031F2C69ADFCA27694C7E89E3F9CF4E6FFCE6C9F1E7A90F1B6A89F7D1C703B33EA09DC0BCF1A5E
	4F57FE7AE7G2D7F8FBA5E2DB75AD4E9FAFF266E53577D72272A030D696768DE68940CE70D9C679F6EC41C9B4A3A44156C127BEB09FB21D1704E173867BF903CCBA46E44F9413BCC629E23EB665467E1AF77F9795DBFD954F3CB5C188621E306447DBACF6818ABF117368B5E859297D7ACF4944489DCF20928D7A4F13F1138919257A36FA04C1738B5D41E6DD160FDD460AED2DBAA21DCAFF15F4C933C4D9217AFF993A46EFFA46F9C092B136D18AFF18F49F65826C81B0BC53D03D364EF970A870B597DD3A2FDE3
	F86BB87D2D5114709C6C8845CF1E921E034DAA91B958B420E5FFC16478E7F7E0DC5171D4GFD90917B93A775A5FE2857AF6B3E70E13A28CB04EFBAC0FF474868DF5D9D3ADFB83C87E5CC877E19D246AFF706FE1B3CC42FA4327D4305DD43790E903EFFD88ABA709C7DFFD72DDA45742F21773817831E448BBF7C0DACC3DF5D792174D9FD8B957A0AC066E62E0F356DDF0875BD281F2EB6F496A48F75BDF4AD77A5A873C43C21131EADC0799DE27E7E1AC0575AD5403749B6D291DC1FEF7B5F35A70BB77A14495E13
	ED484EAD99CC76EAD3BB68372D30364C04720F399EA6EF7D71416489D962B9A7CC5EC2A81FCCB14B0B9974DB3758CB8BF9CB42642DB049635F060AC367F7B1DD4FE9D361FD2A185F6DF921737B4CBA233FFE1077376C6F05BD61E3F18AF4B4054C6FCA2A5F8E780629E26CBD6B8DFD57069C7B5503680B86196BC246FE6C86C3767AA147FE35E96CE32706766D34294658B35FB8CA7A4699D305EFAC421FB27038FA5C741ED87269477167BFD59D2CBFEF6016054F2DEFFDB83C1CF045D60377FBBB5F3066F5832B
	5A8A4CC51C197152CF417CBDE127A167598F06A72781D3E94B9C1B517BEC061E7FFC4350734B3C6169C9AF98DC4F7E3D03534F3DB9B85D374F30732983061DE707E967ABEF99F2967C59107372B0657C26F0F0FB3763F58DFE8FF612BC47DB886BA689A07BB148AFG6C0F09F363594562EC5E7B18384777EAF178BD3E7B134D7770ECA5617778105F205716045F638B650FAA893F47A778793EE3503C478A587F3350377EB5E84C74D7C8BA64AABAC9ADF33BF51241AE6786B432C176F0B55D2EAE6EBAD21A7A62
	97D2D3C8DAB549E05B77EA1206FB70EA12CE376855A495366FC13CD70421EE13B0B950C1EE520E0198C08FEAF1231DAA3129FCE890566BE4233884EC933700E54D0D261A49E0633A10D97064236F3CBC6FE7978329A4231A64E1F029D3B4FA062138712EBE7F917499325A2FFA0088F8E8C000BE5283FA0FD73113ADAD35782340002E384B4469C8C0A72F539FC79D428FBF072A9C01B69D372E6F5AC91A8FFF2ECDB6B1325761EBBD440ECA5E253D10DC56CC275BFAEE6FE6EAAFCAA914D2B6FFF3435E5DE7174C
	CCA5A9E7489A2F6AF01F01B62BDAB5D92D37E9CA20CD753A037E77CED1C1D974BC9EBBB78D44654B66E3E38BB1CF12C6DF8FBEB2F65D52F5E7204B039AB9C4EBE2ACAD14B55C95ED8E8DD4A9DA350EBFBFD73582C99B39CBFF73197737DDCDA1493C11B884EB03FE18A1AD0EF60F3787DEAA691A230333810A6CB4C44E9C6C0C42B3509E7DA72FFCFF4A73A716A50130B3A41D7E108ACC4AB1EA1D6FECF7AA5A686963F72E0DDDFC8FDA6C47668A492BF54D618B602F54C038AD2C4D02A4CDAFFC707BC5633F783B
	AB54F403D5A7FFE403842C07DA95357DD0D9CB8B6C7C25G98B1903FD30A47BA05E1F5B6856FFF7D6CF32B1CB043AAC9EAFBD1917D77B07D77ED7C77B0459C261843EF4330ADA3037E892787C51B693424052DB807027836B9A89782610BGEFB9E46AA4072F94DB857F1F82F43216DE1D31616518D21D2C1137F720F0D296ECGDA0FCDEFF368B6CF40569900C5272BB6175A5E5E6973389C3AE26B7668EDD08128AE268304612D9BA1F9CB8D7B31314D0FD7E6ECBEBA14BAD9C7EF50585848E2B5F6614696065E
	DA2700D8F7B8BFED676A818B88316B862E84C4EF147391CBDB701FDC7CA70FB26553FFF78DFBB4104B96B68A07021656118856258E346E1337699DE3E6E04EAC7E431C4C4F1E25A78CCBB7F0CB51625592AB7656985FB2BEFB76D786C342166869B409CD66988CD529A1D8845C85E40604C301CD796C3575FCB044D25156744F1E25FFB5AC95772071CFDA482A40BF9BE9DB589D3183374C7750D65C199B719EDACD827FFFEA920CBBE26CBB7F32CD744EG60E7A5487F7BE493A62C116ED25735BB3CDE5D613DDE
	66F3633D6A729EDDB16EC17DEA934A8F08BA1C483F0FECC23B2F19F87E8FD0CB8788385252AD3C99GG0CCBGGD0CB818294G94G88G88G8CF954AC385252AD3C99GG0CCBGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG7699GGGG
**end of data**/
}
}
