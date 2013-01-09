package com.cannontech.dbeditor.wizard.customer;
/**
 * This type was created in VisualAge.
 */
import java.util.concurrent.Executor;

import javax.swing.JTextField;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.common.gui.unchanging.StringRangeDocument;
import com.cannontech.common.gui.util.DataInputPanelListener;
import com.cannontech.common.gui.util.OkCancelDialog;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.common.util.SwingUtil;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.customer.CICustomerBase;
import com.cannontech.database.data.customer.Contact;
import com.cannontech.database.data.customer.Customer;
import com.cannontech.database.data.customer.CustomerTypes;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.dbeditor.wizard.contact.QuickContactPanel;
import com.cannontech.spring.YukonSpringHook;

public class CustomerBasePanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, DataInputPanelListener, CaretListener 
{
	private javax.swing.JButton ivjJButtonNewContact = null;
	private javax.swing.JComboBox ivjJComboBoxPrimaryContact = null;
	private javax.swing.JLabel ivjJLabelPrimaryContact = null;
	private javax.swing.JLabel ivjJLabelTimeZone = null;
	private javax.swing.JComboBox ivjJComboBoxTimeZone = null;
	private javax.swing.JPanel ivjJPanelCustomerInfo = null;
	private CICustomerBasePanel ivjCICustomerPanel = null;

	//manually added GUI components
	private javax.swing.JTextField jTextFieldCustomerNumber = null;
	private javax.swing.JLabel jLabelCustNumber = null;

	private int customerId = -1;
	private LiteContact newContact = null; 
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public CustomerBasePanel() {
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

	if (e.getSource() == getJComboBoxTimeZone()) 
		fireInputUpdate();

	// user code end
	if (e.getSource() == getJComboBoxPrimaryContact()) 
		connEtoC7(e);
	if (e.getSource() == getJButtonNewContact()) 
		connEtoC2(e);
	// user code begin {2}
	// user code end
}

/**
 * connEtoC2:  (JButtonNewContact.action.actionPerformed(java.awt.event.ActionEvent) --> CustomerBasePanel.jButtonNewContact_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonNewContact_ActionPerformed(arg1);
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
 * Return the CICustomerPanel2 property value.
 * @return com.cannontech.dbeditor.wizard.device.customer.CICustomerBasePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private CICustomerBasePanel getCICustomerPanel() {
	if (ivjCICustomerPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Commercial / Industrial Customer Information");
			ivjCICustomerPanel = new com.cannontech.dbeditor.wizard.customer.CICustomerBasePanel();
			ivjCICustomerPanel.setName("CICustomerPanel");
			ivjCICustomerPanel.setBorder(ivjLocalBorder);
			// user code begin {1}
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCICustomerPanel;
}

/**
 * Return the JButtonNewContact property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonNewContact() {
	if (ivjJButtonNewContact == null) {
		try {
			ivjJButtonNewContact = new javax.swing.JButton();
			ivjJButtonNewContact.setName("JButtonNewContact");
			ivjJButtonNewContact.setFont(new java.awt.Font("Arial", 1, 10));
			ivjJButtonNewContact.setMnemonic('n');
			ivjJButtonNewContact.setText("New Contact...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonNewContact;
}


/**
 * Return the JComboBoxPrimaryContact property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxPrimaryContact() {
	if (ivjJComboBoxPrimaryContact == null) {
		try {
			ivjJComboBoxPrimaryContact = new javax.swing.JComboBox();
			ivjJComboBoxPrimaryContact.setName("JComboBoxPrimaryContact");
			ivjJComboBoxPrimaryContact.setToolTipText("Who will be the primary contact responsible for this customer");
			ivjJComboBoxPrimaryContact.setEnabled(true);
			// user code begin {1}
			
			refillContactComboBox();
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxPrimaryContact;
}

/**
 * Return the JLabelEnergyCmpy property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelPrimaryContact() {
	if (ivjJLabelPrimaryContact == null) {
		try {
			ivjJLabelPrimaryContact = new javax.swing.JLabel();
			ivjJLabelPrimaryContact.setName("JLabelPrimaryContact");
			ivjJLabelPrimaryContact.setToolTipText("Enter the contact in charge of this customer here");
			ivjJLabelPrimaryContact.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelPrimaryContact.setText("Primary Contact:");
			ivjJLabelPrimaryContact.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelPrimaryContact;
}


/**
 * Return the JLabelPDA property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelTimeZone() {
	if (ivjJLabelTimeZone == null) {
		try {
			ivjJLabelTimeZone = new javax.swing.JLabel();
			ivjJLabelTimeZone.setName("JLabelTimeZone");
			ivjJLabelTimeZone.setToolTipText("Time zone this customer is located in");
			ivjJLabelTimeZone.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelTimeZone.setText("Time Zone:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelTimeZone;
}


/**
 * Return the JPanelTrigger property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelCustomerInfo() {
	if (ivjJPanelCustomerInfo == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder1.setTitle("Customer Information");
			ivjJPanelCustomerInfo = new javax.swing.JPanel();
			ivjJPanelCustomerInfo.setName("JPanelCustomerInfo");
			ivjJPanelCustomerInfo.setBorder(ivjLocalBorder1);
			ivjJPanelCustomerInfo.setLayout(new java.awt.GridBagLayout());
			ivjJPanelCustomerInfo.setFont(new java.awt.Font("dialog", 0, 14));

			java.awt.GridBagConstraints constraintsJLabelPrimaryContact = new java.awt.GridBagConstraints();
			constraintsJLabelPrimaryContact.gridx = 1; constraintsJLabelPrimaryContact.gridy = 1;
			constraintsJLabelPrimaryContact.gridwidth = 2;
			constraintsJLabelPrimaryContact.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelPrimaryContact.ipadx = 7;
			constraintsJLabelPrimaryContact.ipady = -2;
			constraintsJLabelPrimaryContact.insets = new java.awt.Insets(4, 8, 5, 1);
			getJPanelCustomerInfo().add(getJLabelPrimaryContact(), constraintsJLabelPrimaryContact);

			java.awt.GridBagConstraints constraintsJComboBoxPrimaryContact = new java.awt.GridBagConstraints();
			constraintsJComboBoxPrimaryContact.gridx = 3; constraintsJComboBoxPrimaryContact.gridy = 1;
			constraintsJComboBoxPrimaryContact.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxPrimaryContact.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxPrimaryContact.weightx = 1.0;
			//constraintsJComboBoxPrimaryContact.insets = new java.awt.Insets(1, 1, 2, 4);
			constraintsJComboBoxPrimaryContact.insets = new java.awt.Insets(0, 4, 0, 0);
			getJPanelCustomerInfo().add(getJComboBoxPrimaryContact(), constraintsJComboBoxPrimaryContact);

			java.awt.GridBagConstraints constraintsJButtonNewContact = new java.awt.GridBagConstraints();
			constraintsJButtonNewContact.gridx = 4; constraintsJButtonNewContact.gridy = 1;
			constraintsJButtonNewContact.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJButtonNewContact.ipadx = 9;
			constraintsJButtonNewContact.ipady = 4;
			constraintsJButtonNewContact.insets = new java.awt.Insets(0, 4, 1, 11);
			getJPanelCustomerInfo().add(getJButtonNewContact(), constraintsJButtonNewContact);

			java.awt.GridBagConstraints constraintsJLabelTimeZone = new java.awt.GridBagConstraints();
			constraintsJLabelTimeZone.gridx = 1; constraintsJLabelTimeZone.gridy = 2;
			constraintsJLabelTimeZone.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelTimeZone.ipadx = 8;
			constraintsJLabelTimeZone.ipady = -2;
			constraintsJLabelTimeZone.insets = new java.awt.Insets(4, 8, 8, 3);
			getJPanelCustomerInfo().add(getJLabelTimeZone(), constraintsJLabelTimeZone);

			java.awt.GridBagConstraints constraintsJTextFieldTimeZone = new java.awt.GridBagConstraints();
			constraintsJTextFieldTimeZone.gridx = 2; constraintsJTextFieldTimeZone.gridy = 2;
			constraintsJTextFieldTimeZone.gridwidth = 3;
			constraintsJTextFieldTimeZone.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldTimeZone.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldTimeZone.weightx = 1.0;
			constraintsJTextFieldTimeZone.insets = new java.awt.Insets(0, 4, 0, 0);
			getJPanelCustomerInfo().add(getJComboBoxTimeZone(), constraintsJTextFieldTimeZone);
			// user code begin {1}
			
			
			java.awt.GridBagConstraints constraintsJLabelCustNumber = new java.awt.GridBagConstraints();
			constraintsJLabelCustNumber.gridx = 1; constraintsJLabelCustNumber.gridy = 3;
			constraintsJLabelCustNumber.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelCustNumber.ipadx = 8;
			constraintsJLabelCustNumber.ipady = -2;
			constraintsJLabelCustNumber.insets = new java.awt.Insets(4, 8, 8, 3);
			getJPanelCustomerInfo().add(getJLabelCustomerNumber(), constraintsJLabelCustNumber);

			java.awt.GridBagConstraints constraintsJTextFieldCustNumber = new java.awt.GridBagConstraints();
			constraintsJTextFieldCustNumber.gridx = 2; constraintsJTextFieldCustNumber.gridy = 3;
			constraintsJTextFieldCustNumber.gridwidth = 3;
			constraintsJTextFieldCustNumber.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldCustNumber.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldCustNumber.weightx = 1.0;
			constraintsJTextFieldCustNumber.insets = new java.awt.Insets(0, 4, 0, 0);
			getJPanelCustomerInfo().add(getJTextFieldCustomerNumber(), constraintsJTextFieldCustNumber);
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelCustomerInfo;
}

/**
 * Return the JTextField2 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxTimeZone() {
	if (ivjJComboBoxTimeZone == null) {
		try {
			ivjJComboBoxTimeZone = new javax.swing.JComboBox();
			ivjJComboBoxTimeZone.setName("JTextFieldTimeZone");
			// user code begin {1}

			//this may change some day in a new release of Java
			if( ivjJComboBoxTimeZone.getEditor().getEditorComponent() instanceof JTextField )
			{			
				((JTextField)ivjJComboBoxTimeZone.getEditor().getEditorComponent()).setDocument( 
					new com.cannontech.common.gui.unchanging.StringRangeDocument(40) );
			}

			ivjJComboBoxTimeZone.setEditable( true );
			
			String[] tz = CtiUtilities.getTimeZones();
			for( int i = 0; i < tz.length; i++ )
				ivjJComboBoxTimeZone.addItem( tz[i] );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxTimeZone;
}

private javax.swing.JTextField getJTextFieldCustomerNumber() {
	if (jTextFieldCustomerNumber == null) {
		jTextFieldCustomerNumber = new javax.swing.JTextField();
		jTextFieldCustomerNumber.setName("jTextFieldCustomerNumber");
		
		jTextFieldCustomerNumber.setDocument( new StringRangeDocument(64) );
	}
	return jTextFieldCustomerNumber;
}

private javax.swing.JLabel getJLabelCustomerNumber() {
	if (jLabelCustNumber== null) {
		jLabelCustNumber = new javax.swing.JLabel();
		jLabelCustNumber.setName("jLabelCustNumber");
		jLabelCustNumber.setToolTipText("A mapping number assigned to the customer");
		jLabelCustNumber.setFont(new java.awt.Font("dialog", 0, 14));
		jLabelCustNumber.setText("Customer Number:");
	}
	return jLabelCustNumber;
}

/**
 * getValue method comment.
 */
public Object getValue(Object o)
{
	Customer customer = (Customer)o;
	if(customer == null)
		customer = new CICustomerBase();
	//set the primary contact here
	if( getJComboBoxPrimaryContact().getSelectedItem() instanceof LiteContact )
	{
		LiteContact cnt = (LiteContact)getJComboBoxPrimaryContact().getSelectedItem();
		customer.getCustomer().setPrimaryContactID( new Integer(cnt.getContactID()) ); 
	} else {
	    LiteContact blankContact = new LiteContact(-1, 
	                                               "",
	                                               "",
	                                               -9999);
	    ContactDao contactDao = YukonSpringHook.getBean("contactDao", ContactDao.class);
	    contactDao.saveContact(blankContact);
	    
	    // Set the contactId that has been updated through the saveContact method.
	    customer.getCustomer().setPrimaryContactID(blankContact.getContactID());
	}

	//get the selected Time Zone if there is one
	if( getJComboBoxTimeZone().getSelectedItem() != null
		 && getJComboBoxTimeZone().getSelectedItem().toString().length() > 0 )
	{
		customer.getCustomer().setTimeZone( getJComboBoxTimeZone().getSelectedItem().toString() );
	}

	//only get the set values for the CICustomer if it is one
	if( customer instanceof CICustomerBase )
		customer = (CICustomerBase)getCICustomerPanel().getValue( customer );

	if( getJTextFieldCustomerNumber().getText() == null
		 || getJTextFieldCustomerNumber().getText().length() <= 0 )
	{
		customer.getCustomer().setCustomerNumber( CtiUtilities.STRING_NONE );
	}
	else
		customer.getCustomer().setCustomerNumber( getJTextFieldCustomerNumber().getText() );



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
	
	getCICustomerPanel().addDataInputPanelListener( this );
	getJComboBoxTimeZone().addActionListener(this);
	getJTextFieldCustomerNumber().addCaretListener(this);

	// user code end
	getJComboBoxPrimaryContact().addActionListener(this);
	getJButtonNewContact().addActionListener(this);
}

public void inputUpdate( PropertyPanelEvent event )
{
	this.fireInputUpdate();
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
		setSize(412, 346);

		java.awt.GridBagConstraints constraintsCICustomerPanel = new java.awt.GridBagConstraints();
		constraintsCICustomerPanel.gridx = 1; constraintsCICustomerPanel.gridy = 1;
		constraintsCICustomerPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsCICustomerPanel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsCICustomerPanel.weightx = 1.0;
		constraintsCICustomerPanel.weighty = 1.0;
		constraintsCICustomerPanel.ipadx = -15;
		constraintsCICustomerPanel.ipady = -21;
		constraintsCICustomerPanel.insets = new java.awt.Insets(14, 5, 4, 10);
		add(getCICustomerPanel(), constraintsCICustomerPanel);

		java.awt.GridBagConstraints constraintsJPanelCustomerInfo = new java.awt.GridBagConstraints();
		constraintsJPanelCustomerInfo.gridx = 1; constraintsJPanelCustomerInfo.gridy = 2;
		constraintsJPanelCustomerInfo.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJPanelCustomerInfo.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelCustomerInfo.weightx = 1.0;
		constraintsJPanelCustomerInfo.weighty = 1.0;
		constraintsJPanelCustomerInfo.ipadx = -15;
		constraintsJPanelCustomerInfo.ipady = 3;
		constraintsJPanelCustomerInfo.insets = new java.awt.Insets(4, 5, 95, 10);
		add(getJPanelCustomerInfo(), constraintsJPanelCustomerInfo);
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
	if( getCICustomerPanel().isVisible()
		 && !getCICustomerPanel().isInputValid() )
	{
		setErrorString( getCICustomerPanel().getErrorString() );
		return false;
	}


	return true;
}


/**
 * Comment
 */
public void jButtonNewContact_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	QuickContactPanel qPanel = new QuickContactPanel();

    OkCancelDialog dialog = new OkCancelDialog(SwingUtil.getParentFrame(this), "New Contact", true, qPanel);

	dialog.setSize(350, 200);
   dialog.setLocationRelativeTo( this );
   dialog.show();
	
	if( dialog.getButtonPressed() == OkCancelDialog.OK_PRESSED )
	{
		
		Object o = qPanel.getValue(null);
		if( o instanceof Contact )
		{
			Contact cnt = (Contact)o;

			fireInputDataPanelEvent( 
				new PropertyPanelEvent(
							this,
							PropertyPanelEvent.EVENT_DB_INSERT,
							cnt) );	
			
			refillContactComboBox();
			
			// Holds on to a temp copy of the new contact 
			// so it can be set as the new primary contact in the panel.
			ContactDao contactDao = DaoFactory.getContactDao();
			newContact = contactDao.getContact(cnt.getContact().getContactID());
			
		}
	}
	
	dialog.dispose();
	
	return;
}


/**
 * Insert the method's description here.
 * Creation date: (8/9/2001 2:54:35 PM)
 */
private void refillContactComboBox()
{
	getJComboBoxPrimaryContact().removeAllItems();

	//gotta have a NONE
	getJComboBoxPrimaryContact().addItem( CtiUtilities.STRING_NONE );

    final ProgressMonitor monitor = new ProgressMonitor(this, "Loading contents", "", 0, 0);

    final ContactDao contactDao = DaoFactory.getContactDao();
    final CustomerDao customerDao = DaoFactory.getCustomerDao();

    SwingWorker<Object, LiteContact> worker = new SwingWorker<Object, LiteContact>() {
        private int count = 0;
        private volatile int contactCount = 0;
        protected Object doInBackground() throws Exception {
            contactCount = contactDao.getAllContactCount();
            contactDao.callbackWithAllContacts(new SimpleCallback<LiteContact>() {
                public void handle(LiteContact item) {
                    publish(item);
                }
            });
            return null;
        };
        
        protected void process(java.util.List<LiteContact> chunks) {
            // if the user canceled, there isn't much we can do about the background thread
            // but we can stop adding stuff to the model (and tying up memory)
            if (monitor.isCanceled() || isCancelled()) return;
            
            for (LiteContact contact : chunks) {
                getJComboBoxPrimaryContact().addItem( contact );
               count++;
            }
            
            // update monitor here on the event dispatch thread
            monitor.setMaximum(contactCount);
            monitor.setNote(count + " out of " + contactCount);
            monitor.setProgress(count);
            
        };
        
        @Override
        protected void done() {
            // this happens automatically, but if we cancel via the worker.cancel() method
            // we want to make sure the dialog goes away
            monitor.close();

            if(newContact == null) {
            	LiteContact primaryContact = customerDao.getPrimaryContact(customerId);
                getJComboBoxPrimaryContact().setSelectedItem(primaryContact);
            } else {
            	getJComboBoxPrimaryContact().setSelectedItem(newContact);
            	newContact = null;
            }

        }
    };
    
    Executor executor = YukonSpringHook.getBean("globalScheduledExecutor", Executor.class);
    executor.execute(worker);
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
 * Insert the method's description here.
 * Creation date: (2/4/2003 10:51:34 AM)
 * @param type_ int
 */
public void setCustomerType(int type_) 
{
	getCICustomerPanel().setVisible( type_ == CustomerTypes.CUSTOMER_CI );
}


/**
 * setValue method comment.
 */
public void setValue(Object o) 
{
	if( o == null )
		return;

	Customer customer = (Customer)o;
	customerId = customer.getCustomerID();

	//try to find our timezone in the combo box
	getJComboBoxTimeZone().setSelectedIndex(-1);
	for( int i = 0; i < getJComboBoxTimeZone().getItemCount(); i++ ) {
		if( customer.getCustomer().getTimeZone().equalsIgnoreCase(
				getJComboBoxTimeZone().getItemAt(i).toString()) ) {
			getJComboBoxTimeZone().setSelectedIndex( i );
			break;
		}
	}

	//select the unique string they have entered	
	if( getJComboBoxTimeZone().getSelectedIndex() <= -1 )
	{
		getJComboBoxTimeZone().addItem( customer.getCustomer().getTimeZone() );
		getJComboBoxTimeZone().setSelectedItem( customer.getCustomer().getTimeZone() );
	}
	
	if( CtiUtilities.STRING_NONE.equals(customer.getCustomer().getCustomerNumber()) )
		getJTextFieldCustomerNumber().setText("");
	else
		getJTextFieldCustomerNumber().setText(
			customer.getCustomer().getCustomerNumber() );

/*FIXFIX
	String home = customer.getCustomerWebSettings().getHomeURL();
	if( home != null )
		getJTextFieldWebHome().setText(home);
*/

	//only set values for the CICustomer if it is one
	if( customer instanceof CICustomerBase )
		getCICustomerPanel().setValue( customer );

	getCICustomerPanel().setVisible( customer instanceof CICustomerBase );
}

public void setFirstFocus() 
{
    // Make sure that when its time to display this panel, the focus starts in the top component
    javax.swing.SwingUtilities.invokeLater( new Runnable() 
        { 
        public void run() 
            { 
            getCICustomerPanel().getJTextFieldCompanyName().requestFocus(); 
        } 
    });    
}

public void caretUpdate( CaretEvent e )
{
	if( e.getSource() == getJTextFieldCustomerNumber() )
		fireInputUpdate();
}

}