package com.cannontech.dbeditor.editor.device.customercontact;

/**
 * This type was created in VisualAge.
 */
import java.awt.Dimension;
import com.cannontech.database.data.customer.CustomerContact;

public class CustomerContactBasePanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
	private javax.swing.JLabel ivjJLabelFirstName = null;
	private javax.swing.JLabel ivjJLabelLocationRec = null;
	private javax.swing.JLabel ivjJLabelNormalLastName = null;
	private javax.swing.JLabel ivjJLabelPhone1 = null;
	private javax.swing.JLabel ivjJLabelPhone2 = null;
	private javax.swing.JTextField ivjJTextFieldFirstName = null;
	private javax.swing.JTextField ivjJTextFieldLastName = null;
	private javax.swing.JTextField ivjJTextFieldPhone1 = null;
	private javax.swing.JPanel ivjJPanelContact = null;
	private javax.swing.JComboBox ivjJComboBoxRecipient = null;
	private javax.swing.JTextField ivjJTextFieldPhone2 = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public CustomerContactBasePanel() {
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
	if (e.getSource() == getJComboBoxRecipient()) 
		connEtoC5(e);
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
	if (e.getSource() == getJTextFieldFirstName()) 
		connEtoC1(e);
	if (e.getSource() == getJTextFieldLastName()) 
		connEtoC2(e);
	if (e.getSource() == getJTextFieldPhone1()) 
		connEtoC3(e);
	if (e.getSource() == getJTextFieldPhone2()) 
		connEtoC4(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JTextFieldFirstName.caret.caretUpdate(javax.swing.event.CaretEvent) --> CustomerContactBasePanel.fireInputUpdate()V)
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
 * connEtoC2:  (JTextFieldLastName.caret.caretUpdate(javax.swing.event.CaretEvent) --> CustomerContactBasePanel.fireInputUpdate()V)
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
 * connEtoC3:  (JTextFieldPhone1.caret.caretUpdate(javax.swing.event.CaretEvent) --> CustomerContactBasePanel.fireInputUpdate()V)
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
 * connEtoC4:  (JTextFieldPhone2.caret.caretUpdate(javax.swing.event.CaretEvent) --> CustomerContactBasePanel.fireInputUpdate()V)
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
 * connEtoC5:  (JComboBoxRecipient.action.actionPerformed(java.awt.event.ActionEvent) --> CustomerContactBasePanel.fireInputUpdate()V)
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
private javax.swing.JLabel getJLabelPhone1() {
	if (ivjJLabelPhone1 == null) {
		try {
			ivjJLabelPhone1 = new javax.swing.JLabel();
			ivjJLabelPhone1.setName("JLabelPhone1");
			ivjJLabelPhone1.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelPhone1.setText("Phone:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelPhone1;
}
/**
 * Return the JLabelPhone2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelPhone2() {
	if (ivjJLabelPhone2 == null) {
		try {
			ivjJLabelPhone2 = new javax.swing.JLabel();
			ivjJLabelPhone2.setName("JLabelPhone2");
			ivjJLabelPhone2.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelPhone2.setText("Direct Phone:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelPhone2;
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
			ivjLocalBorder.setTitle("Contact Information");
			ivjJPanelContact = new javax.swing.JPanel();
			ivjJPanelContact.setName("JPanelContact");
			ivjJPanelContact.setBorder(ivjLocalBorder);
			ivjJPanelContact.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabelFirstName = new java.awt.GridBagConstraints();
			constraintsJLabelFirstName.gridx = 1; constraintsJLabelFirstName.gridy = 1;
			constraintsJLabelFirstName.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelFirstName.ipadx = 5;
			constraintsJLabelFirstName.ipady = -2;
			constraintsJLabelFirstName.insets = new java.awt.Insets(1, 19, 5, 15);
			getJPanelContact().add(getJLabelFirstName(), constraintsJLabelFirstName);

			java.awt.GridBagConstraints constraintsJLabelLocationRec = new java.awt.GridBagConstraints();
			constraintsJLabelLocationRec.gridx = 1; constraintsJLabelLocationRec.gridy = 5;
			constraintsJLabelLocationRec.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelLocationRec.ipadx = 9;
			constraintsJLabelLocationRec.ipady = -2;
			constraintsJLabelLocationRec.insets = new java.awt.Insets(8, 19, 28, 20);
			getJPanelContact().add(getJLabelLocationRec(), constraintsJLabelLocationRec);

			java.awt.GridBagConstraints constraintsJComboBoxRecipient = new java.awt.GridBagConstraints();
			constraintsJComboBoxRecipient.gridx = 2; constraintsJComboBoxRecipient.gridy = 5;
			constraintsJComboBoxRecipient.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxRecipient.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxRecipient.weightx = 1.0;
			constraintsJComboBoxRecipient.ipadx = 21;
			constraintsJComboBoxRecipient.insets = new java.awt.Insets(6, 2, 24, 140);
			getJPanelContact().add(getJComboBoxRecipient(), constraintsJComboBoxRecipient);

			java.awt.GridBagConstraints constraintsJLabelNormalLastName = new java.awt.GridBagConstraints();
			constraintsJLabelNormalLastName.gridx = 1; constraintsJLabelNormalLastName.gridy = 2;
			constraintsJLabelNormalLastName.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelNormalLastName.ipadx = 5;
			constraintsJLabelNormalLastName.ipady = -2;
			constraintsJLabelNormalLastName.insets = new java.awt.Insets(3, 18, 4, 17);
			getJPanelContact().add(getJLabelNormalLastName(), constraintsJLabelNormalLastName);

			java.awt.GridBagConstraints constraintsJTextFieldPhone1 = new java.awt.GridBagConstraints();
			constraintsJTextFieldPhone1.gridx = 2; constraintsJTextFieldPhone1.gridy = 3;
			constraintsJTextFieldPhone1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldPhone1.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldPhone1.weightx = 1.0;
			constraintsJTextFieldPhone1.ipadx = 91;
			constraintsJTextFieldPhone1.insets = new java.awt.Insets(3, 2, 3, 192);
			getJPanelContact().add(getJTextFieldPhone1(), constraintsJTextFieldPhone1);

			java.awt.GridBagConstraints constraintsJTextFieldFirstName = new java.awt.GridBagConstraints();
			constraintsJTextFieldFirstName.gridx = 2; constraintsJTextFieldFirstName.gridy = 1;
			constraintsJTextFieldFirstName.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldFirstName.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldFirstName.weightx = 1.0;
			constraintsJTextFieldFirstName.ipadx = 269;
			constraintsJTextFieldFirstName.insets = new java.awt.Insets(2, 2, 1, 14);
			getJPanelContact().add(getJTextFieldFirstName(), constraintsJTextFieldFirstName);

			java.awt.GridBagConstraints constraintsJTextFieldLastName = new java.awt.GridBagConstraints();
			constraintsJTextFieldLastName.gridx = 2; constraintsJTextFieldLastName.gridy = 2;
			constraintsJTextFieldLastName.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldLastName.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldLastName.weightx = 1.0;
			constraintsJTextFieldLastName.ipadx = 269;
			constraintsJTextFieldLastName.insets = new java.awt.Insets(2, 2, 2, 14);
			getJPanelContact().add(getJTextFieldLastName(), constraintsJTextFieldLastName);

			java.awt.GridBagConstraints constraintsJLabelPhone1 = new java.awt.GridBagConstraints();
			constraintsJLabelPhone1.gridx = 1; constraintsJLabelPhone1.gridy = 3;
			constraintsJLabelPhone1.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelPhone1.ipadx = 24;
			constraintsJLabelPhone1.ipady = -2;
			constraintsJLabelPhone1.insets = new java.awt.Insets(4, 19, 5, 23);
			getJPanelContact().add(getJLabelPhone1(), constraintsJLabelPhone1);

			java.awt.GridBagConstraints constraintsJLabelPhone2 = new java.awt.GridBagConstraints();
			constraintsJLabelPhone2.gridx = 1; constraintsJLabelPhone2.gridy = 4;
			constraintsJLabelPhone2.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelPhone2.ipadx = 5;
			constraintsJLabelPhone2.ipady = -2;
			constraintsJLabelPhone2.insets = new java.awt.Insets(6, 19, 6, 1);
			getJPanelContact().add(getJLabelPhone2(), constraintsJLabelPhone2);

			java.awt.GridBagConstraints constraintsJTextFieldPhone2 = new java.awt.GridBagConstraints();
			constraintsJTextFieldPhone2.gridx = 2; constraintsJTextFieldPhone2.gridy = 4;
			constraintsJTextFieldPhone2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldPhone2.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldPhone2.weightx = 1.0;
			constraintsJTextFieldPhone2.ipadx = 92;
			constraintsJTextFieldPhone2.insets = new java.awt.Insets(4, 2, 5, 191);
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
	CustomerContact customer = (CustomerContact)o;

	if( getJTextFieldFirstName().getText() != null && getJTextFieldFirstName().getText().length() > 0 )
		customer.getCustomerContact().setContFirstName( getJTextFieldFirstName().getText() );

	if( getJTextFieldLastName().getText() != null && getJTextFieldLastName().getText().length() > 0 )
		customer.getCustomerContact().setContLastName( getJTextFieldLastName().getText() );

	if( getJTextFieldPhone1().getText() != null && getJTextFieldPhone1().getText().length() > 0 )
		customer.getCustomerContact().setContPhone1( getJTextFieldPhone1().getText() );

	if( getJTextFieldPhone2().getText() != null && getJTextFieldPhone2().getText().length() > 0 )
		customer.getCustomerContact().setContPhone2( getJTextFieldPhone2().getText() );


	if( getJComboBoxRecipient().getSelectedItem() != null )
		customer.getCustomerContact().setLocationID( new Integer( ((com.cannontech.database.data.lite.LiteNotificationRecipient)getJComboBoxRecipient().getSelectedItem()).getRecipientID() ) );


	return o;
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
	getJTextFieldFirstName().addCaretListener(this);
	getJTextFieldLastName().addCaretListener(this);
	getJTextFieldPhone1().addCaretListener(this);
	getJTextFieldPhone2().addCaretListener(this);
	getJComboBoxRecipient().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("CustomerContactBasePanel");
		setToolTipText("");
		setLayout(new java.awt.GridBagLayout());
		setSize(416, 348);

		java.awt.GridBagConstraints constraintsJPanelContact = new java.awt.GridBagConstraints();
		constraintsJPanelContact.gridx = 1; constraintsJPanelContact.gridy = 1;
		constraintsJPanelContact.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelContact.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelContact.weightx = 1.0;
		constraintsJPanelContact.weighty = 1.0;
		constraintsJPanelContact.ipadx = -10;
		constraintsJPanelContact.ipady = -26;
		constraintsJPanelContact.insets = new java.awt.Insets(14, 8, 151, 8);
		add(getJPanelContact(), constraintsJPanelContact);
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
	if( (getJTextFieldFirstName().getText() == null || getJTextFieldFirstName().getText().length() <= 0)
		 || (getJTextFieldLastName().getText() == null || getJTextFieldLastName().getText().length() <= 0) )
	{
		setErrorString("The First Name text field must be filled in");
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
		CustomerContactBasePanel aCustomerContactBasePanel;
		aCustomerContactBasePanel = new CustomerContactBasePanel();
		frame.setContentPane(aCustomerContactBasePanel);
		frame.setSize(aCustomerContactBasePanel.getSize());
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

	CustomerContact customer = (CustomerContact)o;

	getJTextFieldFirstName().setText( customer.getCustomerContact().getContFirstName() );
	getJTextFieldLastName().setText( customer.getCustomerContact().getContLastName() );
	getJTextFieldPhone1().setText( customer.getCustomerContact().getContPhone1() );
	getJTextFieldPhone2().setText( customer.getCustomerContact().getContPhone2() );


	com.cannontech.database.data.lite.LiteNotificationRecipient recipient = 
			com.cannontech.database.cache.functions.NotificationGroupRecipientFuncs.getLiteNotificationRecipient(
						customer.getCustomerContact().getLocationID().intValue() );

	if( recipient != null )
		getJComboBoxRecipient().setSelectedItem( recipient );
	else
		getJComboBoxRecipient().setSelectedIndex(0);

}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G67F854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBB8BF4D4C516687093B200881A456849A8B21933B90AAB3B62ECF60749409E9D19954F441599B9AE6EB2AB2B6832BB1CB3F1E4245279A28182C9A01090BB90A4E84C878264538C648392081A011E98D784DEF73FA4CDBA7D1A57AF2483D1765E2AD7552FBB2F93E1F40F9CEE5E2BFB6BFE2A6A562DDBD52FC95EBB3169A916BCC2D252097E6FC88AA1F90D043C316659A40E9BA3A7E690433F9200AD64
	D4CD9ABC2781AD6121440C9CB2F9D39614E7C3F979FD09995B61BD17A8562ADAF80920A78BE82E9C4F4511753C39036B4924657724ECF8EE8208839CBE5748283FC9AA50710BF5BC07948BA12B3A304E97D2110E2B05F2BBC09CC0C2972B1F844F75D473387ADA3D5E79BBE3491A1F6F33ECC4B986199CEA3A99EF277E3412C7D2C88F6A9ACF6DC41EE9C0FB92G9B1FCABA5FBF83CFDBFBEF779F1DCADF05D372FA952FA6BBFBAAE417DBD3548A17FC556D14AB1C7DFECD6913D5A7D0A527D6DF5F2CA31A9922C9
	724B6D12D77610A12A6F1F25ACAA1F1096A8E73991772B43E84382A8AF85D8AD7031C7905F866FAE00A1B7EF7751FDB63DDD1D579FA1EB1F7726EE0E506E99EE565E84F7305DB37AFF1F39030E51CBF9B89649C0AB3A1C18D185E08750GE6G94D25FE85A799EBC9B9DDA33525727F89BFD3EA60FCB7DC8F1499EF82F2F870A0EBBABBB9555C5889B7FA8295C441E1100714D3AE9BE56A7F18739AF441F1DC316BDF64612E952A74B622AAC7117D9DFAC3C9C6C93267B1791F52F389C6A2B213ABFA5C65D2B46AB
	2969A63A67BD375F122C4B496E8D6A367562D8FFC4473A9C5ED3F47FE078CFA9FE0C814F7C76DF294FE0F99A505674F25F685C44EDE9CB1BC5F22A5BCC7BA1BA31515269E1B678BC61366455226C988F5339C158321B62938CF8E64B9CEAA31617816DF8DFE286732FD73661BC1E8D6585GCB81D68364F893B34A810EFB390FCD5ED979B5FD2CDDD23526FE3747A52B04713E12378D1EF2C0D325D62FDF133CCE39C3D25D1243A37B09DE6737F958870E903EE46A7BCE5071216C15D5C9F3ABDE10AE75491A2C7A
	D91D147CADD8C715A9556E6E13C1C11F0FF0396B73F36169117C5A1FFDAEC91303B598FDCBBE0E49D52983A402GF8E77D323DG7B6B9234BFGB0D174C39415F7C6D6A1DC545737A9CAEF3F4F0EADA1CB3D584F2F505891836F733C3C0FF3F7A02EC8C94C28D1381C1F65EFB548E9F1BB51FCC99D64F36C5C910C5527946EB32FF7F21FE9597890A9FF6E34294FA0F89466ABE315703973851509F63172EF8EF75E64BCB603D30A793CE3BA5E7AC6F424C640337E3DEDA70ED5BC5871A440CA5147EF65DB194FE2
	0067DE7A098A8EA63B02BD4E6C5CD91B2777A76ACA576DDFAF5AD1C669441718D186508E90G0875713E0CBF5299017FAFEDFB30AF97D19D6172B7FCC3BAD81F2D59066B14FCD576EAAD32CF763A60452F535E5C067A74F95C25881E146D0877413CEF06096697784C6DB8B7A5CDD35D0EFECDBEA7EB1A5B5B9D24EF5E0E3A7C7D8EDFB0E4F0DD5B36E39B1581187722FE61F61CB3300AFBE5BAF802C5276F22F3D67648FDE0F433526F5598FECF817AFD2FBC28470CFD85189CDD15BC7D98FFD8AEB1D742FE7429
	6EBEF0898CE1247C8A4E6794C701210F1E0471461C08GAD6F8A1F1B83FB785CAC3D7683326062DE0B595A927DD00BC53B42664A44AB413949ECAA58E1EEFBB10D9DBA5E1FCAF177EEC51BA2A5B1ECCEACAAC4F9F120A781A0698A1F9333F6A01D479DFDB2B4AB9E8F6BD2515793F666605848D77AE5885798604E087E1B32F3EB982DC36FCBC63FFBA7762547909B4728B4E72C65B963AA30A78DF025AA6B4FCBAA6B8FD64FDA476BAD06720A087512C43DB4BF64C77EC875A65673FAB628E30FD84F56406BE9D0
	A78AA05E9FDA0F79CA66C54CC14C724E6EFEF7C53F664608AEE9B0C9FC7D9A9D9316D776BA3274F93B4A4F734737E49C27D2ADB14386E05738CFBD3B11670FB3FAE3497287F7C64C1BBD9A33ED1A9674A96B77F87E38F9976ADC8234659A7701AD0546B52C516FD71CEE3AFC6A6377F421516798EEC9615B706CF22BFE2D9502C2000F753F94223D383E6AE8BD5F98CF73906C2FB520373CBFB1E3E1BF626F3B10E7305D8538A80039G4C37DF2B45BC43A92932D6C17F32E53B6C32F4D58AD47887A000D45068D4
	510C5493783ADF9F17057B37B29BE55A73BA677E8E1E83BE10FB1C7B554333086DC62E45ACA71F38F917A57BAA6B4B1A2B41BE9D7A1B0C54D5975E4CE46DEB2D636D9B83F566DF656DCBBBEC563E55C0E7FCFF90FCE983B066831C6F59A6B3BEED0073BDDA4F79A682EE01600B9EB463CB96FCF283674B8EA4E63484B8DF054548B794E0FC3383FC9C96B79C0027C403C285FB344BEA17227649AEBA98955200260FC4A3A56BC3212FD5BE874E117901E0EE3058703EBCB0BCE7E0792B5A05762E89046758C3941F
	B6981EE327F5719CBB8FE801C11EF35DDF0CB1F299141381F22E413A8DF08A20679A1F87970A8E12D1F25AD618572AD7721C8BD924F45F4F5B05FAE1DD514B853B301F693AC2449E2BB6FC3FB276DAE85BE3434A7A9EEB98DFC5D8F9E1989F1B83FB4B0AF8ECB2CC197A7A53F6D837CF3AE50F0B2DEB0FF5995735E557D83ECA2E433A864062D552853E8FAE3F4E635217A70DF1A9E173814B0688F149F5DD4FC32F8767D03958571E32221D8B01369CA099758EA5E65482588182C3FC0C9E58F5F434B1220EE7D7
	1A81F78A561C66707CC07469A7F537B696DF8C638B9F0B6821D0BE56FF8D07F97F4D9D62F9E2EA7D2D5930E598DF388DABCDED48EA4E52FD03FC49EDF87C96EDD8B42A8D35DF1A59704CA03721DD58505BF0EBB68C8E638B37E16217E673225BD3E8BEAF402F9CCA139250ED15AD5C56D8104376F493683A3D944A2B856EF50ADB8B651C2FB86EE99762EA205C7695EFE7208BF3CF1FA85FCE4B98EBD9796D2EEFE2FF34C957910EE3BA76FCE3BA4C76E1EC1E7E30B8723E6161E273FD433CE273FD430F0B37B0BC
	48B01EA6F0FAE271487B0A1F971B6DABF6D17B1F227B9B9F28137DB4F0E0222653DE567209497E62E329CF565B8E7B765A75FA1D931A52DCAD7C6B377B8B4C7D4B107F3D5A45733F282FF87E77018D572F05D0DE8630C678492FAE0CF44616FDE378D833387ACC892E6BD6205BG6CG9E00289BBC1EDEFAE754B56FC4E004B56F2CB572B8FFE6B59F670BD64CDD7DCA3F6A144347324BDAA0E841485D2A49FD586D2C0EC279B5C96D16077942D54A4FE81178EFD8A3798B9B5371F546B1BDA0664D7143A30DC13C
	4998B0F97575C6F987053C0D4DA8EF01899FC2D2C4F92F07482B91721286D15E4A887256C61477C003D15EA1A16F328565650CB117D7EA02E7726EF7E08E20A90A476E76E11EC1EA47607D43E3E5C454C9F7621E5BA38DAA7D9A58BC86FD3F4849F310A1A84FGC8GD8B20665A2ECDEBDBA00323BD4298F066C1CCFF6120537A16F3F39B8EF9514DBGDCGE381E65F46F3D847B5749BB68375DD32E86B1CEE6358E65F1E18E13DBD583E1A5BA365274F5179FA6AF61607668B7CCA0A9FB260D9FEFA106231BC97
	E8673F47570A559778DA11B5164F4D67CBA24F2D97CB4C67560A9273987ACA4948B174FFCAA24D89E65F270739FD1DE3394D25B41708037282019B574C6BAD9638D787F93D22F19C57F60D57AB93385BAC3CDEC060D6DAF83D3163F84E794C6746BB241CD9A5966CE3331CB3E99C9B376563426F10DA1C7C8EA9EFBC2C4F6339FFD5DF8A4917F1D7D3DF7F216AF6B5C95DED54DBD96CFE451925730F9D4FE3F7010B1E35C3B989E055F8EEEF60DD1EA3474F19C55A461CB15D3BAF379F3024CDE0F616CD885A9B87
	B9321E3B49DC278B68B19390F7D8B66E5B504F1F30309C0851FBC268B8463836856933C268E8EFE32A113EB8040EFD34CF67E7EDFBFAAFEF5B5A27630867EF6BA21ECB2C505B14B511BD5BBB761052C9EFE632F17E07EEEC5BF12059A77231FF1966D481A8C7891CDD64D4F3A7723571859B8DACF700FF8158G4E838C81C4834C87D88A309AA0EB9267F91D7214C239E592BB5BF1CDE2E7C6E97A9953D8A84F1E444E0798CF1DAD5624DD548F6F880D8FC5F71847CC5A7EC921B4225B76F5722DE83DDD78BE5F40
	33D4FF4736B3DBDD91ED8DBF03CE9FC1DFFED81945512F179B229F7033684DE1BE08CF34B3F9920EE96645169164246BFAB9AC841E25GFBF45AAAF8AF34B01FE3723C07C713E714F9FCF228BC7B6420BC565EE80B79FA369C2FD9F51DB14D23694CB4683C9426D30372630469E4328B4A03321D57C6133D5EA0FBCC186C99D0FEA2C476BFFD90143D4EB21A6C8D865931E13267C179FDDDB64BB77D989A74B565F7E5FB6159A1DD56334A9374961E271527A13CFE62388C891ADD6AA6FC9D0EF35E5A9D745F5DA2
	5FFF5DB49FDB5F23E82CDFB0192D6FA982BF1362979B70EC3DA9F67175E6A950ECD17CDC6A2ECA4CDBE2211CG1084308AA07B7B30AEFE1F67814776D711D1F2F43B62EB03C441E3D85A759CD33E353B791FE67238BE6F478F1079635FC809F42EE37BBE0BBD677567CA7B9E0AE76DFB379A5B978534B80045GABG1281F2267076CD5FDF4E5A078E5228C1DE56C3CF1AF963BA5C72C0900DC99F1B17C95D18C3BA9415A6B2AC07FC351B670125A05F862081CC1C424EB3995FDB0E6139E73E48BDE327305CF3BE
	BC97CFE1EDE2F32557B552DCC98D0395401BBC054D95ACE7CFCD4CF8ED5D3AF53FGE0F64CBA03F171F41B6410BDA771A41D6EAC996DFC5996C1EBD31C74B87EACE43BBCA65D614033EDF1DA81A4374F8D3D4369F2E31E607FD8D17BA4CF1B4494B0FAC9A15E6784CF815BFB942FDC4DE813DAF3C3E861363D705E5690BA174CF5DF391EADF49B656EAE8D439FE0789D9DA6369CE06752E5C70D79C741D4B60E36297C5CBAB55F0C5EAE687FD9EBC6F789FAEB3F99BDA06877874C68D170FC073035B00B70FF2B0F
	8D4771183B8537FD7A5CFAA91B4F2D223F3E1734BC7FC64A1E88F3ABF6AA733F55D30379604A4BEBB34DF4B3BD57F1BBC8A2536FBFB4B27D73A3A3533F4254DB0F1D53DD4177FB8D6F252EEF6AFCA9F25CBE7A2D6AE0F1A926B2727E6B41CA737D570FAA4D77DF0FD40EFCC675F8E5643B6FFF2CB45EFD33387AF19D7AE559B448DF81FAG06G26CD63B1352DEAB721B155A41E1EF0F563598AFEF923F17D1555115BFA38FA746FF50ED563FD20C351B4258F65F37C09EA9CAFCD71D1A4431D2E46BED25D5DBD9A
	2B2A635B2B515FBCF297C353F5B337075F6744C35BD6CC637B3417F76DA7235D395A5D1AC7EE5217877DBC77739EFE1EDBF4A75F3755B8D0079D4A9E0031F7723B3223E20F98833824BB79BD48B831774B1E4EE5BCC66B36C079BC00B67D6B4687BE2FFED7CF08B8BB6C0AF0F6C8A3A7336162D1F1F6B81D5BD51F4FF18B854EDA47F1EB85EECCBF47555C45F14F85B84EF3974FCDFE53006B5DECA8AFGD8813096A0E78664A6GB600F6G974090408C00B8008400A5G4B8156GE44D64BE59F190474CB49F84
	243E49260E29CAEEF4CD5DAFBA6AA27B6697F5237BE6976D8399FC4ABE68534FGFB6BB087D03A3A7C3218833E3A9C01EB7537ABFEB72E3AAC5E6C6899AD0FAA1AE9F6BF31EFD43E6230B2633BC479EAE7FE3DFDE19B54E379E79C7565CBB3437352C7A95EB3B3BCAF2DF7F09F8FGED59CC3EFFEC3E45FC7ACF3748B769967370FDB42FBEFEF7F8FB2BA83E6D6E7076BEA166F4A750926E967B6523C67D56FB747BFE7DF96A1EC8F7A947720DFC6775FABE7DB9B6A25F5E10BB44E83DDE3C7EDC90116FCC404817
	2457DB29BF57C6647BF588DFCEB42BD72ABFEB23077331B971C6C36439C7EC234F3D75B673F5E133ED783A10E7B3DF97F65A0C6B82433DE343D8598DB123D354AB95384F84EEBF150938CF5DAE2DC760AB29DD08BFA523C20EBFC46D9A2038CE9DD7EF40FD4657BB9B468FE8F20F22F25E969B5E65F539BD9ED6BEC56B383D90CEC4FFFCC4EDF47BA4035DE7858E6DE6F7CF8BF2B7D2DE0F5BABB733AFDE89BF0B9D20EBD89B0C19AB1A1F0BA5506FCC83D00E0A66BE7D30030871FF5C70FE5C716D67E71F7DBF68
	784010F39EB63C7F44796D6BDE7A2D6AE0E3FA1D2E7151B01EF1E21C076C08CB0072E201FB494E4FC4178ADCC7BE67AD7BAB0EDB76B96735895C51731C771440596AB8EF3440BD360573468BDC5ED64EBBDF609CB40F481A1518D1B00B63BE6CC4DCB914ED82975FA9748A5CFF8B5EE801F3895EF801DBA4F8674F62FB7AA9BDB84FB54C78CEAAB4F7E83917FE67CE67CA963C675D4B6B7ED2CA67F5895F1F7D94BFAB51772F530F52F98CE2180CCD47D046C4F81FA1E45CE547BA9A84BF463F620F484F4E89126D
	B7F3CE3054F01E096B52C25001E7ADFFD837EE9D53FF030E6592F8DF864074E59E8F6A331F9FCD5F069174E547A4E664066813697897G3EB4066BFB2FB1286F7D2D2369CB9FC11FBDA658BE56170301507E1ECA7D6092547344707E3E50995A5F9FB5856D79D967E876E40EE0CF8C68288EE97FCEF6DF8578F9227D731A037A160D2AEF7D887AD6985A4FE4A73786FDEF2E043F4D724A83D671D19D7AF3596EC6D8D8BD3B4B2D55703FE6BF61E7893183463B305A370F18FEEB0C30BA062D71E977693FAF1A30C7
	777DB9EDC17DBE7BEEF37DAED02FF40381D53A810C77290F0818C3FB5B03F216FCFEF3F24E7FA9A827449E147343ADB7A727654FC1B9D51DC1B98D1DB7A767768BC1B9AFDE8A4A693CC9B9A9F45DE57B29A768FD168D7A3F73BE3EA7BAFA9A4F4FA36D095AD545A72B5AA043522F2A7919008347D01CA7B012CEDB64403381379736AF1247FD0D1E9D1E030A5EEE765B415C6AE7E0C2443FC652A1CEEAA42F1DFD65EE7561391B555B5F67G1DE45BB95929F8DD4389C745E12395G48DA0F24D95DFEEB3FDFF6D9
	B5458A9B723EFE2F5BA9E932F5402D75C085206A3F36732BE433683AD61A9BAD3FFA30BE755F9FCC351034D61241F245D6120689DFABC92779E0ABC905DCB16053FD5B227B3445FF05FCC552F44CF0EC2A0EDD006F821B2EE3FF0233C1E08BF4EC94C006405EC358EE9DE113C6F2756F862CF8682376512E554886FAC2EA45B350FA0D3CAD7A878BF8B84AA99974E493DEB63638D5992430B2F4494690EFDA7403C37F3178458EFFAA49E8A5BBB0B01E13D5BA16328B3FCF52FF1CF4122C77A930D1BE87F868AFBF
	FD247B35C10F5CC6723A4E628779FECDF6B5F2AF71EBA43F8DB5D56281C965A9495F23E1365E50C6523C78B3AA321511BD12373B1239CC836B74C6BA87E87726003D3954DE14D2A124647E7D663DA527D74CCFA5161364ED0FA239CEC2CFA9EAABD92F7528323FC77138823EE34729202C816C518DF484D4D2A81A0F0D2D4013E0E1F4EF603E280DABB71E0431892824125644F520C254F0B577C8AA281255D68DFF9626282502F6DB31766EAFBE5CFE5EC2D274C662486686FCB09ADDD21F5BB388AFADF46E52B1
	5F8EC516698BC7454E28B8896D51FE7D5AD38F3CE0DB3D8A041DA4697443DEB0A9A7D86BE3BAC546474F5C35E1467249E8318F1B4BA52F071D12570F0E83422DE1ED66A4E1FA45DDCFD54D7C516F7F171A9EE45548BBEC10003532DBD67AAA9B3B3AE017A08440080178DDC2BC5629882B33B5F0E77E696756B94043DAC8EADFD5957DDBCD7F9E403F5594D3CDB15587E058D613917F0553833C4D54ADE92198FDA800EF050C3AA54C31D8BF658F7310D4C46F9063042592DAF58F7D7B6ECD5B06DCE5E1B6FD9088
	098F189151DF83FC77AC2D89DADA24DB0A71BE6B3BE7E9C350D2843CB5C27BD23FFB16B68D9FFDDCAB533EFB163686AD4D50ADCD216FAC9F5CFF43188FA60FAB08188F12B9AC8F4C1B933CDBE2F94A31C35822F640FB6670BCE5EA8DFEB49069AE7FDC1F6471E8122737516B42FB7D26C1CD8E5EED349CC279FEDEC7A7B25A19C3E877E5834F7F81D0CB8788236C9D2DBE96GGBCC1GGD0CB818294G94G88G88G67F854AC236C9D2DBE96GGBCC1GG8CGGGGGGGGGGGGGGGGGE2F5
	E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGF896GGGG
**end of data**/
}
}
