package com.cannontech.dbeditor.wizard.notification.recipients;

/**
 * Insert the type's description here.
 * Creation date: (11/21/00 4:08:38 PM)
 * @author: 
 */
import com.cannontech.database.data.lite.LiteNotificationRecipient;

public class NotifRecipientEmailPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener, javax.swing.event.ListSelectionListener {
	private RecipientEmailTableModel tableModel = null;
	private javax.swing.JLabel ivjJLabelName = null;
	private javax.swing.JTextField ivjJTextFieldName = null;
	private javax.swing.JButton ivjJButtonAdd = null;
	private javax.swing.JButton ivjJButtonRemove = null;
	private javax.swing.JLabel ivjJLabelAddress = null;
	private javax.swing.JPanel ivjJPanelEmail = null;
	private javax.swing.JScrollPane ivjJScrollPaneJTableEmail = null;
	private javax.swing.JTable ivjJTableEmail = null;
	private javax.swing.JTextField ivjJTextFieldAddress = null;
	private javax.swing.JLabel ivjJLabelNotifyType = null;
	private javax.swing.JComboBox ivjJComboBoxNotifyType = null;
/**
 * DestinationLocationInfoPanel constructor comment.
 */
public NotifRecipientEmailPanel() {
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
		connEtoC1(e);
	if (e.getSource() == getJButtonRemove()) 
		connEtoC2(e);
	if (e.getSource() == getJComboBoxNotifyType()) 
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
	if (e.getSource() == getJTextFieldName()) 
		connEtoC3(e);
	if (e.getSource() == getJTextFieldAddress()) 
		connEtoC4(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JButtonAdd.action.actionPerformed(java.awt.event.ActionEvent) --> DestinationLocationInfoPanel.jButtonAdd_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
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
 * connEtoC2:  (JButtonRemove.action.actionPerformed(java.awt.event.ActionEvent) --> DestinationLocationInfoPanel.jButtonRemove_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
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
 * connEtoC3:  (JTextFieldName.caret.caretUpdate(javax.swing.event.CaretEvent) --> DestinationLocationInfoPanel.jTextField_CaretUpdate(Ljavax.swing.event.CaretEvent;)V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jTextField_CaretUpdate(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (JTextFieldAddress.caret.caretUpdate(javax.swing.event.CaretEvent) --> DestinationLocationInfoPanel.jTextField_CaretUpdate(Ljavax.swing.event.CaretEvent;)V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jTextField_CaretUpdate(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (JComboBoxSendType.action.actionPerformed(java.awt.event.ActionEvent) --> DestinationLocationInfoPanel.fireInputUpdate()V)
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
			ivjJButtonAdd.setEnabled(false);
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
			ivjJButtonRemove.setEnabled(false);
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
 * Return the JComboBox1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxNotifyType() {
	if (ivjJComboBoxNotifyType == null) {
		try {
			ivjJComboBoxNotifyType = new javax.swing.JComboBox();
			ivjJComboBoxNotifyType.setName("JComboBoxNotifyType");
			// user code begin {1}

			ivjJComboBoxNotifyType.addItem("Email");
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxNotifyType;
}
/**
 * Return the JLabelEmailAddress property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelAddress() {
	if (ivjJLabelAddress == null) {
		try {
			ivjJLabelAddress = new javax.swing.JLabel();
			ivjJLabelAddress.setName("JLabelAddress");
			ivjJLabelAddress.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelAddress.setText("Address:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelAddress;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelName() {
	if (ivjJLabelName == null) {
		try {
			ivjJLabelName = new javax.swing.JLabel();
			ivjJLabelName.setName("JLabelName");
			ivjJLabelName.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelName.setText("Name:");
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
 * Return the JLabel3 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelNotifyType() {
	if (ivjJLabelNotifyType == null) {
		try {
			ivjJLabelNotifyType = new javax.swing.JLabel();
			ivjJLabelNotifyType.setName("JLabelNotifyType");
			ivjJLabelNotifyType.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelNotifyType.setText("Notification Type:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelNotifyType;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelEmail() {
	if (ivjJPanelEmail == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Email");
			ivjJPanelEmail = new javax.swing.JPanel();
			ivjJPanelEmail.setName("JPanelEmail");
			ivjJPanelEmail.setBorder(ivjLocalBorder);
			ivjJPanelEmail.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabelAddress = new java.awt.GridBagConstraints();
			constraintsJLabelAddress.gridx = 1; constraintsJLabelAddress.gridy = 1;
			constraintsJLabelAddress.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelAddress.ipadx = 12;
			constraintsJLabelAddress.ipady = 1;
			constraintsJLabelAddress.insets = new java.awt.Insets(15, 12, 6, 1);
			getJPanelEmail().add(getJLabelAddress(), constraintsJLabelAddress);

			java.awt.GridBagConstraints constraintsJTextFieldAddress = new java.awt.GridBagConstraints();
			constraintsJTextFieldAddress.gridx = 2; constraintsJTextFieldAddress.gridy = 1;
			constraintsJTextFieldAddress.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldAddress.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldAddress.weightx = 1.0;
			constraintsJTextFieldAddress.ipadx = 163;
			constraintsJTextFieldAddress.insets = new java.awt.Insets(15, 2, 6, 5);
			getJPanelEmail().add(getJTextFieldAddress(), constraintsJTextFieldAddress);

			java.awt.GridBagConstraints constraintsJScrollPaneJTableEmail = new java.awt.GridBagConstraints();
			constraintsJScrollPaneJTableEmail.gridx = 1; constraintsJScrollPaneJTableEmail.gridy = 3;
			constraintsJScrollPaneJTableEmail.gridwidth = 3;
			constraintsJScrollPaneJTableEmail.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPaneJTableEmail.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJScrollPaneJTableEmail.weightx = 1.0;
			constraintsJScrollPaneJTableEmail.weighty = 1.0;
			constraintsJScrollPaneJTableEmail.ipadx = 311;
			constraintsJScrollPaneJTableEmail.ipady = 180;
			constraintsJScrollPaneJTableEmail.insets = new java.awt.Insets(7, 12, 13, 12);
			getJPanelEmail().add(getJScrollPaneJTableEmail(), constraintsJScrollPaneJTableEmail);

			java.awt.GridBagConstraints constraintsJButtonAdd = new java.awt.GridBagConstraints();
			constraintsJButtonAdd.gridx = 3; constraintsJButtonAdd.gridy = 1;
			constraintsJButtonAdd.anchor = java.awt.GridBagConstraints.EAST;
			constraintsJButtonAdd.ipadx = 28;
			constraintsJButtonAdd.insets = new java.awt.Insets(15, 5, 3, 12);
			getJPanelEmail().add(getJButtonAdd(), constraintsJButtonAdd);

			java.awt.GridBagConstraints constraintsJButtonRemove = new java.awt.GridBagConstraints();
			constraintsJButtonRemove.gridx = 3; constraintsJButtonRemove.gridy = 2;
			constraintsJButtonRemove.anchor = java.awt.GridBagConstraints.EAST;
			constraintsJButtonRemove.ipadx = 4;
			constraintsJButtonRemove.insets = new java.awt.Insets(3, 5, 6, 12);
			getJPanelEmail().add(getJButtonRemove(), constraintsJButtonRemove);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelEmail;
}
/**
 * Return the JScrollPaneJTableEmail property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneJTableEmail() {
	if (ivjJScrollPaneJTableEmail == null) {
		try {
			ivjJScrollPaneJTableEmail = new javax.swing.JScrollPane();
			ivjJScrollPaneJTableEmail.setName("JScrollPaneJTableEmail");
			ivjJScrollPaneJTableEmail.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			ivjJScrollPaneJTableEmail.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			getJScrollPaneJTableEmail().setViewportView(getJTableEmail());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneJTableEmail;
}
/**
 * Return the JTableEmail property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getJTableEmail() {
	if (ivjJTableEmail == null) {
		try {
			ivjJTableEmail = new javax.swing.JTable();
			ivjJTableEmail.setName("JTableEmail");
			getJScrollPaneJTableEmail().setColumnHeaderView(ivjJTableEmail.getTableHeader());
			getJScrollPaneJTableEmail().getViewport().setBackingStoreEnabled(true);
			ivjJTableEmail.setBounds(0, 0, 200, 200);
			// user code begin {1}

			ivjJTableEmail.setAutoCreateColumnsFromModel(true);
			ivjJTableEmail.setModel( getTableModel() );
			ivjJTableEmail.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
			ivjJTableEmail.setGridColor( java.awt.Color.black );
			//ivjJTableAlarmStates.setDefaultRenderer( Object.class, new ReceiverCellRenderer() );
			ivjJTableEmail.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
			ivjJTableEmail.setRowHeight(20);


			// Do any column specific initialization here
			javax.swing.table.TableColumn nameColumn = ivjJTableEmail.getColumnModel().getColumn(RecipientEmailTableModel.NAME_COLUMN);
			javax.swing.table.TableColumn typeColumn = ivjJTableEmail.getColumnModel().getColumn(RecipientEmailTableModel.TYPE_COLUMN);
			javax.swing.table.TableColumn addressColumn = ivjJTableEmail.getColumnModel().getColumn(RecipientEmailTableModel.ADDRESS_COLUMN);
			nameColumn.setPreferredWidth(110);
			typeColumn.setPreferredWidth(35);
			addressColumn.setPreferredWidth(130);

			javax.swing.ListSelectionModel lsModel = ivjJTableEmail.getSelectionModel();
			lsModel.addListSelectionListener( this );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTableEmail;
}
/**
 * Return the JTextFieldEmailAddress property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldAddress() {
	if (ivjJTextFieldAddress == null) {
		try {
			ivjJTextFieldAddress = new javax.swing.JTextField();
			ivjJTextFieldAddress.setName("JTextFieldAddress");
			// user code begin {1}

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldAddress;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldName() {
	if (ivjJTextFieldName == null) {
		try {
			ivjJTextFieldName = new javax.swing.JTextField();
			ivjJTextFieldName.setName("JTextFieldName");
			// user code begin {1}
			ivjJTextFieldName.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.STRING_LENGTH_30));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldName;
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/00 4:58:59 PM)
 * @return RecipientEmailTableModel
 */
private RecipientEmailTableModel getTableModel() 
{
	if( tableModel == null )
		tableModel = new RecipientEmailTableModel();
		
	return tableModel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object val) 
{
	com.cannontech.database.data.multi.MultiDBPersistent multi = new com.cannontech.database.data.multi.MultiDBPersistent();
	

	java.util.Vector usedIDs = null;
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized(cache)
	{
		java.util.List recipients = cache.getAllNotificationRecipients();
		java.util.Collections.sort(recipients);
		usedIDs = new java.util.Vector(recipients.size());

		for( int i = 0; i < recipients.size(); i++ )
			usedIDs.addElement( new Integer( ((LiteNotificationRecipient)recipients.get(i)).getRecipientID() ) );
	}
	
	// get the next ID in the cache
	Integer recID = com.cannontech.database.db.notification.NotificationRecipient.getNextLocationID();

	for( int i = 0; i < getTableModel().getRowCount(); i++ )
	{
		while( usedIDs.contains(recID) )
		{
			recID= new Integer(recID.intValue() + 1 );
		}
		com.cannontech.database.data.notification.NotificationRecipient n = new com.cannontech.database.data.notification.NotificationRecipient();
		n.getNotificationRecipient().setRecipientID( recID );
		n.getNotificationRecipient().setRecipientName( getTableModel().getLiteRowRecipient(i).getRecipientName() );
		n.getNotificationRecipient().setEmailAddress( getTableModel().getLiteRowRecipient(i).getEmailAddress() );
		n.getNotificationRecipient().setEmailSendType( new Integer(getTableModel().getLiteRowRecipient(i).getEmailSendType()) );
		n.getNotificationRecipient().setPagerNumber( getTableModel().getLiteRowRecipient(i).getPagerNumber() );
		n.getNotificationRecipient().setDisableFlag( "N" );
		n.getNotificationRecipient().setRecipientType( com.cannontech.database.db.notification.NotificationRecipient.TYPE_EMAIL );
		
/*		com.cannontech.database.db.notification.NotificationRecipient nd =	new com.cannontech.database.db.notification.NotificationRecipient(
				recID,
				getTableModel().getLiteRowRecipient(i).getRecipientName(), 
				getTableModel().getLiteRowRecipient(i).getEmailAddress(),			
				new Integer( getTableModel().getLiteRowRecipient(i).getEmailSendType() ), 
				getTableModel().getLiteRowRecipient(i).getPagerNumber(),
				"N",
				com.cannontech.database.db.notification.NotificationRecipient.TYPE_EMAIL );
*/
		usedIDs.addElement(recID);
		
		multi.getDBPersistentVector().addElement( n );
	}	
	
	return multi;	
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

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
	getJTextFieldName().addCaretListener(this);
	getJTextFieldAddress().addCaretListener(this);
	getJComboBoxNotifyType().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("DestinationLocationInfoPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(376, 376);

		java.awt.GridBagConstraints constraintsJLabelName = new java.awt.GridBagConstraints();
		constraintsJLabelName.gridx = 1; constraintsJLabelName.gridy = 1;
		constraintsJLabelName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelName.ipadx = 4;
		constraintsJLabelName.ipady = -5;
		constraintsJLabelName.insets = new java.awt.Insets(19, 13, 6, 18);
		add(getJLabelName(), constraintsJLabelName);

		java.awt.GridBagConstraints constraintsJTextFieldName = new java.awt.GridBagConstraints();
		constraintsJTextFieldName.gridx = 2; constraintsJTextFieldName.gridy = 1;
		constraintsJTextFieldName.gridwidth = 2;
		constraintsJTextFieldName.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldName.weightx = 1.0;
		constraintsJTextFieldName.ipadx = 261;
		constraintsJTextFieldName.insets = new java.awt.Insets(16, 18, 3, 18);
		add(getJTextFieldName(), constraintsJTextFieldName);

		java.awt.GridBagConstraints constraintsJLabelNotifyType = new java.awt.GridBagConstraints();
		constraintsJLabelNotifyType.gridx = 1; constraintsJLabelNotifyType.gridy = 2;
		constraintsJLabelNotifyType.gridwidth = 2;
		constraintsJLabelNotifyType.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelNotifyType.ipadx = 3;
		constraintsJLabelNotifyType.ipady = -2;
		constraintsJLabelNotifyType.insets = new java.awt.Insets(5, 12, 8, 0);
		add(getJLabelNotifyType(), constraintsJLabelNotifyType);

		java.awt.GridBagConstraints constraintsJComboBoxNotifyType = new java.awt.GridBagConstraints();
		constraintsJComboBoxNotifyType.gridx = 3; constraintsJComboBoxNotifyType.gridy = 2;
		constraintsJComboBoxNotifyType.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxNotifyType.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxNotifyType.weightx = 1.0;
		constraintsJComboBoxNotifyType.ipadx = 107;
		constraintsJComboBoxNotifyType.insets = new java.awt.Insets(3, 1, 4, 18);
		add(getJComboBoxNotifyType(), constraintsJComboBoxNotifyType);

		java.awt.GridBagConstraints constraintsJPanelEmail = new java.awt.GridBagConstraints();
		constraintsJPanelEmail.gridx = 1; constraintsJPanelEmail.gridy = 3;
		constraintsJPanelEmail.gridwidth = 3;
		constraintsJPanelEmail.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelEmail.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelEmail.weightx = 1.0;
		constraintsJPanelEmail.weighty = 1.0;
		constraintsJPanelEmail.ipadx = -10;
		constraintsJPanelEmail.ipady = -48;
		constraintsJPanelEmail.insets = new java.awt.Insets(4, 12, 22, 7);
		add(getJPanelEmail(), constraintsJPanelEmail);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
public void jButtonAdd_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{

	getTableModel().addRowValue(
		new com.cannontech.database.data.lite.LiteNotificationRecipient( 
				com.cannontech.database.db.notification.NotificationRecipient.DUMMY_LOCATIONID, 
				getJTextFieldName().getText(), 
				getJTextFieldAddress().getText(), 
				getJComboBoxNotifyType().getSelectedIndex() + 1, 
				com.cannontech.common.util.CtiUtilities.STRING_NONE ) );

	getTableModel().fireTableDataChanged();
	return;
}
/**
 * Comment
 */
public void jButtonRemove_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	// store the previous selected row
	int newSelectedRow = getJTableEmail().getSelectedRow() - 1;

	// delete the row
	getTableModel().removeRowValue( getJTableEmail().getSelectedRow() );
	getTableModel().fireTableDataChanged();

	// if no rows exist, clear the selection
	if( getTableModel().getRowCount() == 0 )
		getJTableEmail().getSelectionModel().clearSelection();
	else
	{
		getJTableEmail().getSelectionModel().setSelectionInterval(newSelectedRow, newSelectedRow);
	}
	
	return;
}
/**
 * Comment
 */
public void jTableEmail_SelectionModel(javax.swing.ListSelectionModel arg1) 
{
	if( arg1.getMinSelectionIndex() >= 0 )
		getJButtonRemove().setEnabled(true);
	else
		getJButtonRemove().setEnabled(false);
		
	return;
}
/**
 * Comment
 */
public void jTextField_CaretUpdate(javax.swing.event.CaretEvent caretEvent) 
{
	if( getJTextFieldName().getText().length() <= 0 || getJTextFieldName().getText().equalsIgnoreCase(com.cannontech.common.util.CtiUtilities.STRING_NONE) ||
	    getJTextFieldAddress().getText().indexOf("@") == -1 || getJTextFieldAddress().getText().length() <= 0 )
	{
		getJButtonAdd().setEnabled(false);
	}
	else
	{
		getJButtonAdd().setEnabled(true);
	}

	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		NotifRecipientEmailPanel aNotifRecipientEmailPanel;
		aNotifRecipientEmailPanel = new NotifRecipientEmailPanel();
		frame.setContentPane(aNotifRecipientEmailPanel);
		frame.setSize(aNotifRecipientEmailPanel.getSize());
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
 * This method was created in VisualAge.
 * @param o java.lang.Object
 */
public void setValue(Object val) 
{
	
}
/**
 * @param event ListSelectionEvent
 */
public void valueChanged(javax.swing.event.ListSelectionEvent event) 
{
	javax.swing.ListSelectionModel lsm = (javax.swing.ListSelectionModel) event.getSource();
	
	//only one should be selected
	int selectedRow = lsm.getMinSelectionIndex();
	
	if( lsm.isSelectionEmpty() )
	{
		getJButtonRemove().setEnabled(false);
		return;
	}
	else
	{
		getJButtonRemove().setEnabled(true);
	}
	
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G9BF954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBB8BD0D55735A421A90926410A868DB1A4B1A9F9752534355324359DC3CC47D45BE7DE4D54C9EC47243E97BAE3B27455CE2D35AF1B2F98FFA8A6458F9183AA3608E0B0626593110FC0EE14912A4466D58E5C83DCBD5CF3BD77A0973D48DBEB6F334EBDDC6E8531631B474C621C3D76DEEB2F3576DEEB2F3D7739EC475E587464149D0CA527B3636FF8B2E3BB6A98BB7AFCE7A2614264048CE6795B8730
	0D1D372541B3926A6645A5E4E4315FEC490472ECA847BD1E1071E7F84FE63683EBAA61A5C4BFCB206E5A4B8F8D0F5D4FBFF2281F753C5CD43F891EDBG62G074F55B276AF5567987845861EA0B90531D55D5846D73F474015C33985A08EE0DE37E83F941E9BF84FDF32159B6D3E18924BD67F78E04AE664E361C9D056A3E85B0DE79E7BC6B26B453E1E64F2A2CDA454AD84C065D3D974FBF6F8D636DC69296ED27B0A3BA417CBF569F2D7EF7100733A24B90ADD2A6E6CF6F6C93AD3F595EBF217536D14DD3A47E6
	BB03956788D15FA7B9159649A5ABBE5E5FDB75191CBFE3F5D04EF4A26E348DE5188F65E5GC9A6FECB85626D706EG70B9C96F2A031506DE6DB73E41667E5C153AB5045ED1CE216FBC27DF6F287E356BF772B11A359347E28D546D391C10D18250862083C4816C66F363491C3F403326D32FD57B7AD4D70D5BFDCAF1E81F298ED901F71B8DEA8C9C282CEA8E464478FF3D3EA808BCE30120FBEE42F442A6F19F51DC18F36EA9367C5B1F262C8FE21365F1A5A9F3AE8BDBAC386C3709687B3FC2763D72724839BA32
	6F8B4C5A776CFB555474A0FD4FFD65D04A9A034F26AB7E3EAB2F60D8FF1F4F01A2F8CFB66607403F4871E1963C18371FF1BC16A321EE75951A9B6DDBC896FB5ACC16D5EA8FEA0768041A14F6C54860D682E5391B4F3B98C574395114A5126367D970C2169D9C0F6565D0F732AFA1C34C2F2F6DC6BF1E8D6579G4BG12G32DC8999C5GA7DDB4475E4EB9F20BF32CC55274D37DCE45A1EBCC50FEED677B70143D3AA6B539BC3A646A124FCB1AD36AD4E48FB35A446FC49BF40E207B8E1F7BDD5047E959A5EB5C0D
	013B54A76B326691EDFE38F39B3651E4DE5B666C132103BEB7A33EBF5A198DCFC57268FFF3BBA4DD76379075BF5D09E3F2CDBA8F9C01813C8B3BAC5D03766AG7D3DG91269D1AF6A13FCFE58D22054DE6D755AB7D6EB65404ADF3211DB7B4E06C08017739AE327112DC446DD193B24AD4629336EB3B05CF1D338B451734C1723167791A5028521CF93B1D66CC5D66A7D951AB4DC1678C1FAB2A18AB91EA206FAC62BCD1AED1EE3535077025C6B538AFCDBCD6A8A17BC87DA779EFB1472AFAAF0ED5BC483C98E015
	E9636F6F4A93F376146411E9161E55E002498E3F45051C9195BB8CFB269A3267B291BBC47DF4DE1F66863FGE88370814CF613ADF7D534DB68C97F8D3728C76EAD5972A35E8772CF3760B75EE29F6349A2EC764D5D38CE4957E0D92C135D324B018B26D1776CEE64E178F137EA527C10635D60773560E89E933FE0B77A26246B1A3333DF17DBE5DDF73AFA7C750BF9DF1E7ECE37BFE4D0DFBF5B0DBA2A836077E67BE53B51E73AD417CB6603E71298752FF21F1595398F042ED57BDD3A402F5803737E0ABCE8440C
	15FBB0B93AA6A97D98FFC4AE91AF219D5D1A338F268406B0D6F2957DB92FA147E22345B05E189325C1DD4ED5724D01FC724D026B0F317917762784DBDB221F2CCB71DE95FEB5792A5FB705CC2F6486177DB59EBB8C3CC7487A1B3398336BA00EB9DDBC6C59D51163602870BCC67845C09E721C83FD4D87D8F6157C624287D8CF3147F008DAD5D104D9CDFB576D4D4271112F764B9032B148FDEA5A70533D5B836A4E9B7694752DFB511E0AA5BE46EBE84FC59514B7A601BC1900AB5404CDFBB5E193516E41CAEA37
	8E4ABBBC7EF6E59EEB3B5AAAEA6783FC9440DC0FE817B422DDC6B535AB5393B26CFA28F65FB35BA950A69CE036AE5A2D54C5BBB1866BAFE19E92AC776C69F796776BCE0C6A128E0E626E57AD39657DFCFDC09EE97D14C34EEE42F12A04F29B0052CF736A654D14C3C6DD09E5AB1E5895B2F78E6F973245757B67D55EBD14C3BE3F8F7BCC043AB57DB487FE3A573A0E55F8BCEA17134FA1E37C06722C73CE606E4EFB971E5DCE4D23B7C1E07052D8C764213C38469AE8A367D84DF38E54F55335040CBA006557907F
	0B4A9D9659FD001B8D3090A015737BFB65D10C9BFCB2960BC70B2CF52BDA1F6CA83CACDD130A25813D184728629ADEDD0F6F07E8EC56961D006765D37D3A8E190A43F129A60007BDB4130AC36799650C0FE71FA43FFFE6A62B9C4ECE891643C7EC4DCDA99890F6378F786D6F7BD6C66A2AFF7CF13D30453315E40BE8E833E400ECB16DC6B0DBE4FAE90FD3DED86957651C5C27DE13A72A8E6FDF29227E77G6FB6AF75BF3D4C5A7FE42F48DB66FAE9ACFED605F9DF172449FAB17FAF52A8BE8C5EE24FG84F42357
	DA2CB5864208D33947D176B6D8B19A1C326238D4EBE1E09F0B839F85AE7711EA12FBA1484486C96E97BE8E149B6DD68E75026ED7A61D9DF061A65DA21F15EE5E2050F765A0695B7C49BFB777969C5AAEFCC4661EEFA80B6A08B57C4106407D4B1AC1FF2E35697A683CCB6475B94D28CF5E7540FC7FG471F3C9E186F7FCB93657B9DD03760BA6522AD79381E955EGFF847882C087088218FB03E2C3563E0F58B839FE9344BA4DA5A92DA396EF4A397721ADE13DB54ABF5E0736616BADA3BB2C2D883443129BA3F5
	DF95D096F4191501F49F841433FCA3694438ECEDC57DF93C36CCBC1B2DD9F896540975BE29523A5E977A447C30433383C044709DD519C60C707AA8D68FB5D8E3753C2DC7D3B6860995FC1DB164DB6273470AA2F33F3F36G571F2CA148B3816AGBAG4281E281968C519835FDD8B55E987109592656822E9156615A403C4934698FC759745646E27138E331F2E8A41D305F629BE43FF5C314BFEBD537A7C358A83AC0994AEE861B8F7B8B77841F8FE63492327E240C4E026CB7C956E35537A7EB6DA83AC0D943EF
	8633575A0FC90698D306DF5F268C3F9FC5D79CD0DE9A54DEBD4A6E6076027957291ED23D062CA9C3A46BAA60A37644CF711C27E0986273B0612EB6A26E8BA86B432453874D981B23219C8F3098A091A0B30C6A0BF8FDDE184873CE06997959B52A0FEF223DA6526293E92D3AC48F875F5746876093033493FD3CE476315EF8866E3FC26DEB53834A4176E34237BF4F8F3D876B488F3E876B4E8F3E87D3723789BC70301E4CD03D1EBF769E4D179FEC0F164B75FC697D2D1C3747A3FBF83041045728FBD3FF0F8559
	2B1D117AE443C6592C34F2035126DED7EBCB4D39765BC3B941671AA50FAE64B1984720A30C72685A8B3866C5C3B99EE021B9EFF60E0A91AB20EE35B1B7B26FCA4858F35748719631782E83B85FCA204E8E60G7081445CC5B1F8FB41386BE43DF70CF5B2EDFF68F15E38BF78B8EF5B0FFBG0F5A2FF54901E31933BF472C73C77BA6DD6EC3330BB67BB83DAEE9BD722839D04869C5DDA87A527D21660B98535F0D985323265F3CFCA3F08C66019D1798F6DF894FB581E3A078BDDDE5657711496F5F4B46BA035EF4
	77E87F957C14EAAB3FB2135FBB9F0F45AFAFA43F3CCF2C7C0E197C568F0D452FACA43F9FB4605904A28D2A7DBAE3F36F46F93D54BC03E804F287C084C04C5D567D68B787B08EF5EBD29F8CC72BDB6EE21177A0EDC4A3512EC3DE00AB81E881F05FC3B96F1F33F1DF57EC17BAE5C5B828404FA8C7BA7F0AE82DFB6AE0161F86EFD3865B865D32283B5F0EFBCBF3E570D79B34278BD1D6614D7C6E05095CDCEA3666664B6E9139573AFBA8F7FF39ACD8FDD6B879796191FEDEG7872F03F1D6D6121F26B6F72385191
	9E18DB3F4871E1610139F553854A2DE321AE6DCB3426157018548BE51F099B6438C8A8471A38D72BE86D1BE362CE701CE89D14336E2578F25F015071A172C07078B07DC070F5A06640586B406C83217CDA4817F803F4AB3817E4AE7211EE65A66E819F69D6E76212CE10EED1A66E57E554AE4E443DFF1CF0EBCC5C87C304EB7CB26D6D56B550FDE0B46016FC19764A259DA3F2BA5C2959EC27B527631454E3679EA5560E3A531986FDE6842D9D5AE93E0F02F2BB00BB0272FA6F814A6B67BCB5135943BE8DFA8633
	226DE8CAC40418DFF3A27CF9FD9C6475224FE79A294FC4284F3B8FF14B9B2D7399FD63B7A9A29F9375DB1A2C75B8BE304D34541F9CD13FE1943DB622BE5DC2AFF4DB341FF4DB37A80EA95FB99172FC2977BE635C52F8361C4FE785777FF13D31DEF707D82FF9F4907AC7B753FA3D60BE5AC7E59EA35C3A7BE83C6B38EF145D8F63FDBF61A65DC41C8265B09377AE23F9B17BFE727F9FDCC0FDD7C3B9EB9244B900B6GE59255AF627591134479DF5CA4F17E08E70B227E6C05D0F795DB8C3E48935F338C5E782ECC
	3A353B9251C76F18FD505D830D97650837FA46EF44D7EE43BE178CC90933EB79A43457BFB86E85004513A86E3FF0984F45F588A2F91371FB013507F1D604CF665FA0D4550D23CB2A859CC003305B18BFDEF8BF417C77A1592C65865248AEBC8AF6B0A1D34F8D72094550DE60367178719F68D30093F2659E714BB55BB71EDC69E34815B4D978B53E0B7EF65AC806F5D0A77AE047466A037853B36B810809G79862E9C5EB726881E025F616337420F206E81A1B371EB077269E4AB3FE3C39332C100FCDEF87F3921
	331813A927EF6FBB002C5B24EB1AB01DD80F6B9AB1C607BFA05663E4937F9947C7DA70E21D701E26F5A2866A58D7688C0C15E03CF2C0B98CA09AA09EE089402A2F505AEBBBD84246496D5BD4379D96E745321C8A7D3E5DF8FB5FC7BC3F1E62715C6FBD4E1E3D778F49214E105803469AE3BC935B72B9DE68B7359475EB01BA85A092A08EE09E405287C93F3B8F96897D6A9DCE3DC657252EDEFE52CF4A1DF74A83FEB4A614C237C71A2D3AA59A7D2FB31ED91121764D1B0F0D484D0CF66546539E044E388B2842FC
	38D355F832A272E12D0AF25A8E2073824483AC0894E781026E711B23736867EE925D72C811C72F06E76694C1A7FCEB9F9B4B37A8BED37C4B835A32A94237305C886FEF3D734EBB0F8188B9761E40B5D52C04B58E07A6FB7CFB25237D38267B736B407A7967703B096656AECDD57877C94D7CA6CD244CC61B25DBF86EEE5EF5907E0D8FB67B71625EG7566F9B4EB8F4C23D17FB610DD1C0196FA2DE7203D06FD226099B7056C5BDF054F0352C1E36E468ED83F73AAFF77F850BBD00479D30CEF440C673CAF679BBC
	7E6D6FD69ED9B37725A4076031692B02B6672B7EDCEBAF170B713C007E447A319737F9C69F2FEFA29F5B737507D95D4F7F101C9F228F3B41BB4652C76265F56B057EB7EF20FE5D1262711FE7947A38CD3403C63F53F1BBE85415582C6DC74B1A74C9E8791F7B1805209175A565E3573762D2EC78E1F3237F3D5572FE7374D8F949BF8B220F076EE89FA22EEDA8893DE74AAA893EE7FA3FA4781EE9CF4958E7E385A5213FDF78EB09757B85911773ED68F7CBE1BEA5816C181A10D188D0B915E272F3A59FB29E1303
	4463FAC78F1E696057D33A3927AB8D2D6B932563FFF3B52794E3C7278AA1228F79937EDB25B8DE3A6A66C8017BEEA95AC8F376746A2229017FC1A94F4D65EE0E963979DC1E6B3703DE3DD3A9DFBFF491F1BEA8CFB6F1DF39C8B9FC3409ABB8C63409A6EE4EE15AE72EB3F15B0A09B6B30A7605B32B68FB3BF640C5C6513E704DDC5C770CFDD75F6654957914314C98E759EFD451DD7D42A85AA7AE9B42BE5834040C9C0012E954776BB7A957E884DC58B45A639C9A267C7259E9446342B036CD4272F448F181EEAD
	9E10BFAF2AE0260C475BE98FD5BE1D746D294FE5C177E37E1544246F70923DFBBA4977768914AF8E4A738116CD273C49DB09ED13203C69A11A33E70E7E951E67254BC6060469042A71D98B4896CDF54B1ABEA8307C8B914397BCF8FDE54C2128237BC37100195F8CAA1C6D3CACF769EDD20F69CB332AF11DAE82B99A819400F000E800F80085G4BGD68124C5A7E46C81A8812883E88768857081C4C6139E0E32C3E368E19CA2F08754A4A73AA07939AD348F9E350D6F0395FC7FA1036F608119401D34E16E22F6
	F7FBE453576BEDD9A62E495322FA1CF8B6AB5616C54D380F6B06B9AD76B42FF37F1B8D3A45C753FAFD7262483D5E7513E8FF1173BCF9F13C1C478A85167D844E6B9551E2CF75FB48F9C47F1FF19FDE8578B55134077BF60D3F3F4262717ACBE77EBDCCE0FF05B344FE7DCF501FD8D7E2C71D6F87C2590CE0F7E14F0CCB97F8BFA7685E64F4ADB3EEED5F6F00F6E21FE267B142BBA3F07F72B70E8F1F9138FF796510795DBB542D1EC16794EF5E3C3DFD576FEE132EF8787676EB0BF81CEB1F99286FD20EF74C8C54
	77FF06C9DFB754AD1DC97AD636DB7BAFFF5858B3984F0E07C36DB9E4AF516950A6A220DDCC90BAA1DFA77FEEBB7E61406F3C95719B0007833F73DEFD02561DA5D0D796C3B1E52892F91A790388FF06BF0773EF201CAEF0E3C9F1DE671FDE35C2C3D70F3186D7070EAB4FD50F9FD7922A032F6D8B2BC72F6DBF298E3E367F2C5A3A369B5FE0D623DF76C0BCECB75B7D4244FDEE625E60BC91F7416950FBCDFCA2178B710DB2F6C8783739DC839C57EE60FEEB41FDEE6056D4E3EC8415FBD50DE87FD44D631FD3D1C4
	B979138CEEDB881526BDB2BEC1991DEE49A277C69307F20BB8356BB4E3143367DE44FC4CA58F64199F7EB4C010AD7C5081E32676FCD814D7D214D70A8B0720ED647934F8DF596C6793FBF8E2FC56634FDA0CFB5479591B39AE0A53A5570AAF61991D497FB74FCF22E13E4547D0DE54414F4B97E0B9067C675CCDE67A445FAD6FAB076EFC4E7F277F03BEDE9E766B742A653DEB784E776D3B23FD08B1EDED271CF825B94EAF6C241CF80D09AB58C5B9F15AA30473717C2E8D4ADE93778A47CD0672DC93F7E410E897
	BEC27326177FDEA0E716790D8EAF17186535A7A622BB721938FD33C37EFEA098CC74B78542BF5F6331CA479DG7AA523336143BC7E35015C9D33A80F9A98CA27360C7C75AF1FB113464747AA925A474C225C277D8C560B5C27E257C4F2ADBA3F265CE771AC110B271A32CF75626F832FC1126D6C129411601F123496D5F1F68D9AB2BD43479B735BE5269E2B3C4887022873BA28BC1A12744964738213371CC7690C2CE1F0A2E7E46DC027BC6ABFA38B03F7B585E2DAB2691050436F03AD07DB629C6A891B759CEA
	7223624EA69E1E8B9EA55BB636B25396EDC378BB47A0F1368696C78542EC2991E60DF1ABBA6F277D6E4E09456833977C34AFFA7100497ABD2BE527D5FC3E7EDCDBD3ED0D7DD2EB6D3933F67B291AF317EADAAF1D292F2F2B2FB34E8887BF9A033E716C39260BE74F340D64E03FD073F96BAD1EA3513ED1C3D7B2726A4BB7C25C5971D190FB4357EC74AD5772C7E9EF78977E9D730E58040CA2003AD80A85ABECE36508DF44064A35E6F1FE6E58409C71EB9C6F0B8D4C91CBAAA9C70C003AD531943FFEEF23785572
	9849B49B0F7A0CB8722F16770FAB6F7C3AF0760E76A13E6D58D2C17E21034E91GF1G8B81929F237307328A732E7BF13AF76E2C883CF71E12EC3DB70E2A883CF7C6FAFF7D0FAA826F1DC7522F29883CF7A6FAE11B93233E7D9D8F44EF20279E4F00D9BC67AD162E33549A07C3E79962E45B23312D265BB6713CEF45350FEC293FFAA2B50525B5318C1187B731B4CCE61BD8BA4FF51BD8AA6441DEB7736FB9F1EEED75DCE5B79984A816E141261AD8EC3B5D002C91EDD30C76FE8EE9066E5BC7F0F85F600956C853
	D9BAFED7E3535976B3161FAB6761F1008D945BBCA29E3D7858317FDC7C4B731ED4165144F662163FD556788EC2F66057F7464F989B5886376A04CA4003569E7EC87768030AECE7BB3A4F614FF6BC3A6C28213D09C7E76F71AF720F60F15811C649532BE3A0283033B4977E6012ED97550A646AB9A2B6AA955CF449B59D68D9FC1898481B4D65C5AE45A6176C6FEF5D3F2FF9653329AC25013D2B281223C16A52D52D09ED50FBB559532BAA8E2F7B13131CD166G4E580D5CE29A5BED2A0F4A9663DD09A974956FA3
	13B7A7EEEEG1BFAB5F6043744930EE233052336D75220ABD9EB527187242AD6E0565D7501FE60A7277F7CC58ACBB6144471596CF543346A167A1C4AA03C5471684FC7EE1B8E52EA124B03BF86G497282A423AA33036229AF15CCFBFA6DC55E011FD4E7FB05A90174C80F2C769D29696E066C5EE4GF6857639A6FBECD39C50E63BF74AFB4D2F3C538973200E25761514707F257C7FD17CDF4AB1259CD3FA940C1B44467C8B2C77124EFC72188E0643DF5CGA3283F7A56CB0F3FD619348A34EBE0697C63FE9804
	ACFF2BB37DFD1D32F66F1CE9399B23D6CC42B1F263D471A25F773CA39CE73361A2599C3BF1542CC26C56515383515966A0E169FD5EB4077F5FBDE11511AA17536EB5A55D6F17B453F0DB7C0388144ED3951DE59A57F6362042A70790BEED2470E46D8C315B95C2A04D1D512A587B7F4F5247C74E89DA667D41F43301B5C227384FBBB4EC3DB3DC73251C10770E6CE911036CF85AFF1FA74E12BF63EBE48B601527696C66919BFE8C936ACC3A35CFD294481A2F54389CF8BEFDEAD0177D77C267AA103F075A981546
	375D95A877E58B4D7F82D0CB8788E3BCE6DD9898GGBCC4GGD0CB818294G94G88G88G9BF954ACE3BCE6DD9898GGBCC4GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGD298GGGG
**end of data**/
}
}
