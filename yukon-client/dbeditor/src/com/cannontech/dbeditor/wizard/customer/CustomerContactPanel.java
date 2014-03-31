package com.cannontech.dbeditor.wizard.customer;
/**
 * This type was created in VisualAge.
 */
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;

import org.apache.commons.lang.ArrayUtils;

import com.cannontech.core.dao.ContactDao;
import com.cannontech.database.data.customer.Customer;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.spring.YukonSpringHook;

public class CustomerContactPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener {
	private javax.swing.JButton ivjJButtonAdd = null;
	private javax.swing.JButton ivjJButtonRemove = null;
	private javax.swing.JScrollPane ivjJScrollPaneContactTable = null;
	private javax.swing.JTable ivjJTableContact = null;
	private javax.swing.JLabel ivjJLabelAssignedContacts = null;
	private javax.swing.JComboBox ivjJComboBoxContacts = null;
	private javax.swing.JLabel ivjJLabelAvaillContacts = null;
	
	private static final String STR_NONE_AVAIL = "  (none available)";

	private javax.swing.JLabel jLabel = null;
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
@Override
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
private javax.swing.JComboBox getJComboBoxContacts() {
	if (ivjJComboBoxContacts == null) {
		try {
			ivjJComboBoxContacts = new javax.swing.JComboBox();
			ivjJComboBoxContacts.setName("JComboBoxContacts");
			// user code begin {1}

			ivjJComboBoxContacts.setToolTipText("Contacts that are not already assigned to an existing customer");

			//only make the unassigned contacts available
			List<LiteContact> contacts = YukonSpringHook.getBean(ContactDao.class).getUnassignedContacts();
			for (LiteContact contact : contacts) {
			    getJComboBoxContacts().addItem(contact);
			}
			
			setEnableContactList();
			

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxContacts;
}

/**
 * Toggles our list to enabled/disabled and inserts a description string
 *
 */
private void setEnableContactList()
{
	boolean isEnabled = getJComboBoxContacts().getItemCount() > 0;
	getJComboBoxContacts().setEnabled( isEnabled );

	if( isEnabled )
		getJComboBoxContacts().removeItem( STR_NONE_AVAIL );
	else
		getJComboBoxContacts().addItem( STR_NONE_AVAIL );
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
			ivjJLabelAssignedContacts.setText("Assigned Contacts:");
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
 * Return the JLabelPoint property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelAvaillContacts() {
	if (ivjJLabelAvaillContacts == null) {
		try {
			ivjJLabelAvaillContacts = new javax.swing.JLabel();
			ivjJLabelAvaillContacts.setName("JLabelAvaillContacts");
			ivjJLabelAvaillContacts.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelAvaillContacts.setText("Available Additional Contacts:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelAvaillContacts;
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
			ivjJTableContact = new DAndDContactTable();
			ivjJTableContact.setName("JTableContact");
			getJScrollPaneContactTable().setColumnHeaderView(ivjJTableContact.getTableHeader());
			ivjJTableContact.setBounds(0, 0, 200, 200);
			// user code begin {1}

			ivjJTableContact.getSelectionModel().setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
			ivjJTableContact.setModel( new CustomerContactTableModel() );
			ivjJTableContact.setRowHeight( 20 );

			//do any column specific things here
			javax.swing.table.TableColumn name = ivjJTableContact.getColumnModel().getColumn(CustomerContactTableModel.COLUMN_NAME);
			name.setPreferredWidth(20);
			javax.swing.table.TableColumn login = ivjJTableContact.getColumnModel().getColumn(CustomerContactTableModel.COLUMN_LOGIN);
			login.setPreferredWidth(20);
			javax.swing.table.TableColumn notif = ivjJTableContact.getColumnModel().getColumn(CustomerContactTableModel.COLUMN_NOTIFICATION);
			notif.setPreferredWidth(80);

			
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
 * getValue method comment.
 */
@Override
public Object getValue(Object o) 
{
	Customer customer = (Customer)o;
	ContactDao contactDao = YukonSpringHook.getBean("contactDao", ContactDao.class);
	
	//create some storage for the IDs
	List<Integer> contactIds = new ArrayList<Integer>();
	List<Integer> rowsToFix = new ArrayList<Integer>();
	for( int i = 0; i < getJTableModel().getRowCount(); i++ )
	{
		//build a totally new CustomerContact object
		LiteContact cnt = getJTableModel().getLiteContactAt(i);
		LiteCICustomer liteCICustomer = contactDao.getCICustomer(cnt.getContactID());
		if(liteCICustomer != null &&
		   liteCICustomer.getCustomerID() != customer.getCustomerID()){
		    
			JOptionPane.showMessageDialog(this, cnt.getContLastName()+ ", "
							+ cnt.getContFirstName() 
							+ " is already used by another customer.", "Contact already in use.", 
					JOptionPane.INFORMATION_MESSAGE);
			rowsToFix.add(i);
		}else{
			contactIds.add(cnt.getContactID());
		}
	}
	for(Integer row : rowsToFix){
		getJTableModel().removeRow(row);
	}
	customer.setCustomerContactIDs( ArrayUtils.toPrimitive(contactIds.toArray(new Integer[contactIds.size()])));
		
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
		java.awt.GridBagConstraints consGridBagConstraints2 = new java.awt.GridBagConstraints();
		java.awt.GridBagConstraints consGridBagConstraints3 = new java.awt.GridBagConstraints();
		java.awt.GridBagConstraints consGridBagConstraints4 = new java.awt.GridBagConstraints();
		java.awt.GridBagConstraints consGridBagConstraints5 = new java.awt.GridBagConstraints();
		java.awt.GridBagConstraints consGridBagConstraints6 = new java.awt.GridBagConstraints();
		java.awt.GridBagConstraints consGridBagConstraints7 = new java.awt.GridBagConstraints();
		java.awt.GridBagConstraints consGridBagConstraints1 = new java.awt.GridBagConstraints();
		consGridBagConstraints4.insets = new java.awt.Insets(19,10,7,1);
		consGridBagConstraints4.ipady = -2;
		consGridBagConstraints4.ipadx = 7;
		consGridBagConstraints4.gridy = 0;
		consGridBagConstraints4.gridx = 0;
		consGridBagConstraints2.insets = new java.awt.Insets(3,44,5,10);
		consGridBagConstraints2.ipadx = 16;
		consGridBagConstraints2.gridy = 2;
		consGridBagConstraints2.gridx = 2;
		consGridBagConstraints2.anchor = java.awt.GridBagConstraints.EAST;
		consGridBagConstraints5.insets = new java.awt.Insets(16,1,2,10);
		consGridBagConstraints5.ipadx = 169;
		consGridBagConstraints5.fill = java.awt.GridBagConstraints.HORIZONTAL;
		consGridBagConstraints5.weightx = 1.0;
		consGridBagConstraints5.gridwidth = 2;
		consGridBagConstraints5.gridy = 0;
		consGridBagConstraints5.gridx = 1;
		consGridBagConstraints3.insets = new java.awt.Insets(9,10,1,14);
		consGridBagConstraints3.ipady = 7;
		consGridBagConstraints3.ipadx = 46;
		consGridBagConstraints3.gridy = 2;
		consGridBagConstraints3.gridx = 0;
		consGridBagConstraints6.insets = new java.awt.Insets(3,44,3,10);
		consGridBagConstraints6.ipadx = 40;
		consGridBagConstraints6.gridy = 1;
		consGridBagConstraints6.gridx = 2;
		consGridBagConstraints6.anchor = java.awt.GridBagConstraints.EAST;
		consGridBagConstraints3.anchor = java.awt.GridBagConstraints.WEST;
		consGridBagConstraints1.insets = new java.awt.Insets(1,8,1,8);
		consGridBagConstraints1.ipady = -206;
		consGridBagConstraints1.ipadx = -68;
		consGridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
		consGridBagConstraints1.weighty = 1.0;
		consGridBagConstraints1.weightx = 1.0;
		consGridBagConstraints1.gridwidth = 3;
		consGridBagConstraints1.gridy = 3;
		consGridBagConstraints1.gridx = 0;
		consGridBagConstraints7.insets = new java.awt.Insets(1,8,7,43);
		consGridBagConstraints7.ipady = 2;
		consGridBagConstraints7.ipadx = 8;
		consGridBagConstraints7.gridwidth = 2;
		consGridBagConstraints7.gridy = 4;
		consGridBagConstraints7.gridx = 0;
		consGridBagConstraints7.anchor = java.awt.GridBagConstraints.WEST;
		consGridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
		setLayout(new java.awt.GridBagLayout());
		this.add(getJScrollPaneContactTable(), consGridBagConstraints1);
		this.add(getJButtonRemove(), consGridBagConstraints2);
		this.add(getJLabelAssignedContacts(), consGridBagConstraints3);
		this.add(getJLabelAvaillContacts(), consGridBagConstraints4);
		this.add(getJComboBoxContacts(), consGridBagConstraints5);
		this.add(getJButtonAdd(), consGridBagConstraints6);
		this.add(getJLabel(), consGridBagConstraints7);
		setSize(416, 348);
		getJTableContact().setFocusable(false);
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
@Override
public boolean isInputValid() 
{

	return true;
}


/**
 * Comment
 */
public void jButtonAdd_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( getJComboBoxContacts().getSelectedItem() instanceof LiteContact )
	{
		LiteContact selCont = (LiteContact)getJComboBoxContacts().getSelectedItem();

		//add the select Contact to our table
		getJTableModel().addRow( selCont );
		
		//remove it from our list of available Contacts
		getJComboBoxContacts().removeItem( selCont );
		setEnableContactList();		
	}
	
		
	fireInputUpdate();
	return;
}


/**
 * Comment
 */
public void jButtonRemove_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( getJTableContact().getSelectedRow() >= 0 ) {				
		for( int i = (getJTableContact().getSelectedRows().length-1); i >= 0; i-- ) {
			
			//add the row to our unassigned list of Contacts
			int selRow = getJTableContact().getSelectedRows()[i];
			getJComboBoxContacts().addItem( getJTableModel().getLiteContactAt(selRow) );

			//remove it from our table
			getJTableModel().removeRow( selRow );
			setEnableContactList();
		}
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
			@Override
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
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}


/**
 * setValue method comment.
 */
@Override
public void setValue(Object o) 
{
	if( o == null )
		return;

	Customer customer = (Customer)o;

	for( int i = 0; i < customer.getCustomerContactIDs().length; i++ )
	{
		LiteContact liteContact = YukonSpringHook.getBean(ContactDao.class).getContact( customer.getCustomerContactIDs()[i] );
		
		getJTableModel().addRow( liteContact );
	}

}

@Override
public void setFirstFocus() 
{
    // Make sure that when its time to display this panel, the focus starts in the top component
    javax.swing.SwingUtilities.invokeLater( new Runnable() 
        { 
        @Override
        public void run() 
            { 
            
            if(getJComboBoxContacts().isEnabled())
            {
                getJComboBoxContacts().requestFocus();
            }else
            {
                getJButtonAdd().requestFocus();
            }
             
        } 
    });    
}

	/**
	 * This method initializes jLabel
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel() {
		if(jLabel == null) {
			jLabel = new javax.swing.JLabel();
			jLabel.setText("*(Drag and drop to reorder contacts in table)");
			jLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 10));
			jLabel.setName("DragAndDropLabel");
		}
		return jLabel;
	}
}