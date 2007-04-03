package com.cannontech.dbeditor.editor.notification.group;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.gui.tree.CTITreeModel;
import com.cannontech.common.gui.tree.CheckNode;
import com.cannontech.common.gui.tree.CheckNodeSelectionListener;
import com.cannontech.common.gui.tree.CheckRenderer;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.notification.ContactNotifGroupMap;
import com.cannontech.database.data.notification.CustomerNotifGroupMap;
import com.cannontech.database.data.notification.NotifDestinationMap;
import com.cannontech.database.data.notification.NotifMap;
import com.cannontech.database.data.notification.NotificationGroup;
import com.cannontech.database.db.contact.Contact;
import com.cannontech.database.model.DummyTreeNode;
import com.cannontech.dbeditor.editor.user.LiteBaseNode;
import com.cannontech.yukon.IDatabaseCache;


public class NotificationPanel extends com.cannontech.common.gui.util.DataInputPanel implements ActionListener {
	private javax.swing.JTree ivjJTreeNotifis = null;
	private javax.swing.JScrollPane ivjJScrollJTree = null;
	private NotifcationAddressTableModel notifTableModel = null;
	private CheckNodeSelectionListener nodeListener = null;
	private javax.swing.JPanel ivjJPanelLoginDescription = null;
	private javax.swing.JCheckBox ivjJCheckBoxPhoneCall = null;

	private final DummyTreeNode unassignContactsNode = new DummyTreeNode("Unassigned Contacts");
	
	private javax.swing.JSplitPane jSplitPane = null;
	private javax.swing.JCheckBox jCheckBoxEmails = null;

/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public NotificationPanel() {
	super();
	initialize();
}

public void actionPerformed(ActionEvent e)
{
	if( e.getSource() == getJCheckBoxPhoneCall() ) {
		doCheckBoxAction( getJCheckBoxPhoneCall() );
	}

	if( e.getSource() == getJCheckBoxEmails() ) {
		doCheckBoxAction( getJCheckBoxEmails() );
	}


}

private void doCheckBoxAction( JCheckBox checkBox )
{
	int selRow = getJTreeNotifs().getMaxSelectionRow();
	if(selRow != -1)
	{
		TreeNode node = 
			(TreeNode)getJTreeNotifs().getPathForRow( selRow ).getLastPathComponent();

		//create a dummy notifmap to use the attribs logic only 
		NotifMap nm = new NotifMap();

		if( node instanceof LiteBaseNode )
		{
			nm.setSendOutboundCalls( getJCheckBoxPhoneCall().isSelected() );				
			nm.setSendEmails( getJCheckBoxEmails().isSelected() );				
			nm.setSendSms( getJCheckBoxEmails().isSelected() );


			//set our attribs inside the user string of the Tree node
			LiteBaseNode lbNode = (LiteBaseNode)node;
			lbNode.setUserValue( nm.getAttribs() );
		}

		fireInputUpdate();              
	}

}

/**
 * Returns all the LiteContactNotifcations for the given list of LiteContact objects
 *
 */
//private void addContactNotifs( List contacts )
//{
//	if( contacts == null ) return;
//
//	boolean hasPhone = false;
//	for( int i = 0; i < contacts.size(); i++)
//	{
//		LiteContact lc = (LiteContact)contacts.get(i);
//		for( int j = 0; j < lc.getLiteContactNotifications().size(); j++ )
//		{
//			LiteContactNotification lcn = (LiteContactNotification)lc.getLiteContactNotifications().get(j);			
//			if( DaoFactory.getYukonListDao().isPhoneNumber(lcn.getNotificationCategoryID())
//				|| DaoFactory.getYukonListDao().isEmail(lcn.getNotificationCategoryID()) )
//			{
//				getJTableNotifTableModel().addRow( lcn );
//	
//				hasPhone |= DaoFactory.getYukonListDao().isPhoneNumber( lcn.getNotificationCategoryID() );
//			}
//		}
//		
//	}
//
//	//if there is a phone number in this list of contacts, make the check box enabled
//	getJCheckBoxPhoneCall().setEnabled( hasPhone );
//}

/**
 * Return the JCheckBoxPhoneCall property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxPhoneCall() {
	if (ivjJCheckBoxPhoneCall == null) {
		try {
			ivjJCheckBoxPhoneCall = new javax.swing.JCheckBox();
			ivjJCheckBoxPhoneCall.setName("JCheckBoxPhoneCall");
			ivjJCheckBoxPhoneCall.setToolTipText("Should the phone numbers in this group be called or not");
			ivjJCheckBoxPhoneCall.setText("Make Phone Calls");
			// user code begin {1}
			
			ivjJCheckBoxPhoneCall.setToolTipText("Should the phone numbers in the NOTIFICATION window be called or not");
			ivjJCheckBoxPhoneCall.setEnabled( false );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxPhoneCall;
}

/**
 * Return the JPanelTrigger property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelLoginDescription() {
	if (ivjJPanelLoginDescription == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Settings");
			ivjJPanelLoginDescription = new javax.swing.JPanel();
			ivjJPanelLoginDescription.setName("JPanelLoginDescription");
			ivjJPanelLoginDescription.setBorder(ivjLocalBorder);
			ivjJPanelLoginDescription.setLayout(new javax.swing.BoxLayout(ivjJPanelLoginDescription, javax.swing.BoxLayout.Y_AXIS));
			ivjJPanelLoginDescription.add(getJCheckBoxEmails(), null);
			ivjJPanelLoginDescription.add(getJCheckBoxPhoneCall(), null);
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelLoginDescription;
}

/**
 * Return the JScrollJTree property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollJTree() {
	if (ivjJScrollJTree == null) {
		try {
			ivjJScrollJTree = new javax.swing.JScrollPane();
			ivjJScrollJTree.setName("JScrollJTree");
			getJScrollJTree().setViewportView(getJTreeNotifs());
			// user code begin {1}
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollJTree;
}


private NotifcationAddressTableModel getJTableNotifTableModel()
{
	if( notifTableModel == null )
		notifTableModel = new NotifcationAddressTableModel();
		
	return notifTableModel;
}


/**
 * This method was created in VisualAge.
 * @return CTITreeMode
 */
private CTITreeModel getJTreeModel() 
{
	return (CTITreeModel)getJTreeNotifs().getModel();
}


/**
 * Return the JTree1 property value.
 * @return javax.swing.JTree
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
@SuppressWarnings("unchecked")
private javax.swing.JTree getJTreeNotifs() {
	if (ivjJTreeNotifis == null) {
		try {
			ivjJTreeNotifis = new javax.swing.JTree();
			ivjJTreeNotifis.setName("JTreeRoles");
			ivjJTreeNotifis.setBounds(0, 0, 165, 243);
			// user code begin {1}
			
			DummyTreeNode root = 
				new DummyTreeNode("CI Customers");

			ivjJTreeNotifis.setModel( new CTITreeModel(root) );			
			ivjJTreeNotifis.setCellRenderer( new CheckRenderer() );
			ivjJTreeNotifis.getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );

			//disable double click expansion of each tree node
			ivjJTreeNotifis.setToggleClickCount(-1);


			IDatabaseCache cache = DefaultDatabaseCache.getInstance();
			synchronized( cache )
			{
			    // Pre load all contacts for use below
                cache.getAllContacts();
                
				List<LiteCICustomer> customers = cache.getAllCICustomers();
				Collections.sort( customers, LiteComparators.liteStringComparator );
				for( int i = 0; i < customers.size(); i++ )
				{
					LiteCICustomer lCust = (LiteCICustomer)customers.get(i);
					LiteBaseNode custNode = new LiteBaseNode( lCust );
					custNode.setUserValue( NotifMap.DEF_ATTRIBS );
					root.add( custNode );


					List tempConts = DaoFactory.getCustomerDao().getAllContacts(lCust.getCustomerID());
					if( tempConts != null )
					{
						Collections.sort( tempConts, LiteComparators.liteStringComparator );
						for( int j = 0; j < tempConts.size(); j++ )
						{
							LiteContact lcont = (LiteContact)tempConts.get(j);
							LiteBaseNode contNode = new LiteBaseNode( lcont );
							contNode.setUserValue( NotifMap.DEF_ATTRIBS );

							custNode.add( contNode );
							
							addContactNotifsToTree( lcont, contNode );
						}		
					}

				}

				int[] contIDs = Contact.getOrphanedContacts();
				for( int i = 0; i < contIDs.length; i++ )
				{
					LiteContact lcont = DaoFactory.getContactDao().getContact( contIDs[i] );
					LiteBaseNode lbNode = new LiteBaseNode( lcont );
					lbNode.setUserValue( NotifMap.DEF_ATTRIBS );	
					
					unassignContactsNode.add( lbNode );
					
					addContactNotifsToTree( lcont, lbNode );
				}
				
			}

			root.add( unassignContactsNode );

			//expand the root
			ivjJTreeNotifis.expandPath( new TreePath(root.getPath()) );			
			ivjJTreeNotifis.addMouseListener( getNodeListener() );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTreeNotifis;
}

/**
 * Adds all the notifications to the JTree under the given parent
 *
 */
private void addContactNotifsToTree( LiteContact contact, LiteBaseNode parent )
{
	if(contact == null || parent == null) return;

	for( int j = 0; j < contact.getLiteContactNotifications().size(); j++ )
	{
		LiteContactNotification lcn = (LiteContactNotification)contact.getLiteContactNotifications().get(j);			
		YukonListDao yukonListDao = DaoFactory.getYukonListDao();
        if( yukonListDao.isPhoneNumber(lcn.getNotificationCategoryID())
			|| yukonListDao.isEmail(lcn.getNotificationCategoryID()) 
            || yukonListDao.isShortEmail(lcn.getNotificationCategoryID()))
		{
			LiteBaseNode notifNode = new LiteBaseNode( lcn );
			notifNode.setUserValue( NotifMap.DEF_ATTRIBS );

			parent.add( notifNode );
		}
	}

}


private CheckNodeSelectionListener getNodeListener()
{
	if( nodeListener == null )
		nodeListener = new CheckNodeSelectionListener( getJTreeNotifs() )
		{
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked( e );
				int row = getJTreeNotifs().getRowForLocation(e.getX(), e.getY());
				
				nodeSelectionChanged(
					isCheckBoxSelected(e.getPoint(), getJTreeNotifs().getRowBounds(row)) );
			}
		};
	
	return nodeListener;
}


/**
 * getValue method comment.
 */
@SuppressWarnings("unchecked")
public Object getValue(Object obj) 
{
	NotificationGroup gn = (NotificationGroup)obj;

	DefaultMutableTreeNode
		root = (DefaultMutableTreeNode)getJTreeNotifs().getModel().getRoot();

	//get all the elements in this tree
	Enumeration allNotifications = root.depthFirstEnumeration();

	// store all of our selected entries in Lists so we can set them below
	// on the NotifcationGroup
	Vector notifDestVector = new Vector(32);
	Vector contVect = new Vector(32);
	Vector custVect = new Vector(32);

	while( allNotifications.hasMoreElements() )
	{
		Object next = allNotifications.nextElement();
		if( next instanceof LiteBaseNode
			 && ((LiteBaseNode)next).isSelected() )
		{
			//treeNode.getUserValue() is the attribs of the checkbox data
			LiteBaseNode treeNode = (LiteBaseNode)next;
			LiteBase notifElem = (LiteBase)treeNode.getUserObject();

			if( notifElem instanceof LiteContactNotification )
			{
				notifDestVector.add(
					new NotifDestinationMap(
						((LiteContactNotification)notifElem).getContactNotifID(),
						treeNode.getUserValue()) );
			}
			else if( notifElem instanceof LiteContact )
			{
				contVect.add(
					new ContactNotifGroupMap(
						((LiteContact)notifElem).getContactID(),
						treeNode.getUserValue()) );
			}
			else if( notifElem instanceof LiteCustomer )
			{
				custVect.add(
					new CustomerNotifGroupMap(
						((LiteCustomer)notifElem).getCustomerID(),
						treeNode.getUserValue()) );
			}
		}

	}


	gn.setNotifDestinationMap( 
		(NotifDestinationMap[])notifDestVector.toArray(new NotifDestinationMap[notifDestVector.size()]) );

	gn.setContactMap(
		(ContactNotifGroupMap[])contVect.toArray(new ContactNotifGroupMap[contVect.size()]) );

	gn.setCustomerMap(
		(CustomerNotifGroupMap[])custVect.toArray(new CustomerNotifGroupMap[custVect.size()]) );
	
	return obj;
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception)
{
	CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	CTILogger.error( exception.getMessage(), exception );;
}


private void initConnections()
{
	getJCheckBoxPhoneCall().addActionListener( this );
	getJCheckBoxEmails().addActionListener( this );

}


/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("UserRolePanel");
		setToolTipText("");
		setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.X_AXIS));
		this.add(getJSplitPane(), null);
		setSize(405, 364);

	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	initConnections();

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
 * setValue method comment.
 */
public void setValue(Object o) 
{
	if( o == null )
		return;

	//only done if we are an editor
	NotificationGroup notifGrp = (NotificationGroup)o;
	
	//add our individual notifcations
	for( int i = 0; i < notifGrp.getNotifDestinationMap().length; i++ )
	{
		LiteContactNotification lContNotif =
			(LiteContactNotification)DaoFactory.getContactNotificationDao().getContactNotification(
				notifGrp.getNotifDestinationMap()[i].getRecipientID());

		//set the selected node
		DefaultMutableTreeNode tnode = getJTreeModel().findNode( 
			new TreePath(getJTreeModel().getRoot()), lContNotif );
			
		if( tnode != null )
		{
			LiteBaseNode lbNode = (LiteBaseNode)tnode;
			lbNode.setSelected( true );
			lbNode.setUserValue( notifGrp.getNotifDestinationMap()[i].getAttribs() );
		}

	}


	//add all the notifcations from each customer
	for( int i = 0; i < notifGrp.getCustomerMap().length; i++ )
	{
		int custID = notifGrp.getCustomerMap()[i].getCustomerID();

		//set the selected node
		DefaultMutableTreeNode tnode = getJTreeModel().findNode( 
			new TreePath(getJTreeModel().getRoot()), DaoFactory.getCustomerDao().getLiteCICustomer(custID) );
			
		if( tnode != null )
		{
			LiteBaseNode lbNode = (LiteBaseNode)tnode;
			lbNode.setSelected( true );
			lbNode.setUserValue( notifGrp.getCustomerMap()[i].getAttribs() );
			
			setChildNodesEnabled( lbNode, lbNode.isSelected() );
		}

	}

	//add all the notifcations from each contact
	for( int i = 0; i < notifGrp.getContactMap().length; i++ )
	{
		int contID = notifGrp.getContactMap()[i].getContactID();

		//set the selected node
		DefaultMutableTreeNode tnode = getJTreeModel().findNode(
			new TreePath(getJTreeModel().getRoot()), DaoFactory.getContactDao().getContact(contID) );

		if( tnode != null )
		{
			LiteBaseNode lbNode = (LiteBaseNode)tnode;
			lbNode.setSelected( true );
			lbNode.setUserValue( notifGrp.getContactMap()[i].getAttribs() );

			setChildNodesEnabled( lbNode, lbNode.isSelected() );
		}
	}


	getJTreeModel().reload();
	setSelectionCount();
}

/**
 * Updates the text for the given node that shows the number of checked
 * nodes.
 *
 */
private void setSelectionCount()
{
	DefaultMutableTreeNode
		root = (DefaultMutableTreeNode)getJTreeNotifs().getModel().getRoot();

	//get all the elements in this tree
	Enumeration nodes = root.depthFirstEnumeration();

	int selected = 0;
	int endIndx = root.getUserObject().toString().indexOf("\t");
	while( nodes.hasMoreElements() )
	{
		Object next = nodes.nextElement();
		if( next instanceof LiteBaseNode )
		{
			LiteBaseNode nextNode = (LiteBaseNode)next;
			if( nextNode.isSelected() )
				selected ++;
		}

	}

	root.setUserObject(
		root.getUserObject().toString().substring( 0,
			(endIndx >= 0 ? endIndx : root.getUserObject().toString().length()) ) +
		"\t   (" + selected + " Selected)");

	//let the tree repaint itself
	getJTreeModel().nodeChanged( root );

	getJTreeNotifs().invalidate();
	getJTreeNotifs().repaint();

}

/**
 * Forces validation of the the selected nodes to occur along with
 * the appropriate GUI reactions to a newly selected node.
 * 
 */
public void nodeSelectionChanged( boolean checkBoxCliked )
{
	//remove all rows from the table
	getJTableNotifTableModel().clear();

    int selRow = getJTreeNotifs().getMaxSelectionRow();
    if(selRow != -1)
    {
        TreeNode node = 
            (TreeNode)getJTreeNotifs().getPathForRow( selRow ).getLastPathComponent();

        if( node instanceof LiteBaseNode )
        {
			LiteBaseNode lbNode = (LiteBaseNode)node;
            LiteBase lb = (LiteBase)(lbNode).getUserObject();

			//be sure we can set this value
			getJCheckBoxPhoneCall().setEnabled( !lbNode.isSystemReserved() );
			getJCheckBoxEmails().setEnabled( !lbNode.isSystemReserved() );

			if( lbNode.isSystemReserved() )
				return;


			//create a dummy notifmap to use the attribs logic only 
			NotifMap dummyMap = new NotifMap();
			dummyMap.setAttribs( lbNode.getUserValue() );
			getJCheckBoxPhoneCall().setSelected( dummyMap.isSendOutboundCalls() );
			getJCheckBoxEmails().setSelected( dummyMap.isSendEmails() || dummyMap.isSendSms() );

			//see if the check box in the tree was clicked
			if( checkBoxCliked ) {
				if( lb instanceof LiteContactNotification )
				{
					//only allow phone number definitions to be changed
					YukonListEntry entry =
						DaoFactory.getYukonListDao().getYukonListEntry( ((LiteContactNotification)lb).getNotificationCategoryID() );
			
					getJCheckBoxPhoneCall().setEnabled(
						entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_PHONE );
	
					getJCheckBoxEmails().setEnabled(
						entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_EMAIL );
				}
				else if( lb instanceof LiteContact )
				{	
					setChildNodesEnabled( lbNode, lbNode.isSelected() );
				}
				else if( lb instanceof LiteCustomer )
				{
					setChildNodesEnabled( lbNode, lbNode.isSelected() );
				}
			}

        }


		setSelectionCount();
        fireInputUpdate();
    }

}

/**
 * Disables the children node for the given node
 *
 */
private void setChildNodesEnabled( TreeNode node, boolean enabled )
{
	//selected all the children contacts and make them disabled
	if( node instanceof CheckNode ) {
		for( int i = 0; i < node.getChildCount(); i++ ) {
			TreeNode child = node.getChildAt(i);
			if( child instanceof CheckNode ) {
				setChildNodesEnabled( child, enabled );
			}

			((CheckNode)child).setSelected( false );
			((CheckNode)child).setIsSystemReserved( enabled );
		}					

	}

}

/**
 * Returns the selected contacts from our tree
 *
 */
//private List getSelectedContacts( TreeNode parentNode )
//{
//	List allContacts = new Vector(32);
//
//	for( int i = 0; i < parentNode.getChildCount(); i++ )
//	{
//		TreeNode node = parentNode.getChildAt(i);
//		if( node instanceof LiteBaseNode )
//		{
//			LiteBaseNode lbNode = (LiteBaseNode)node;
//			LiteBase lb = (LiteBase)(lbNode).getUserObject();
//			
//			if( lbNode.isSelected()
//				&& lb instanceof LiteContact )
//			{	
//				allContacts.add( lb );
//			}
//			else if( lbNode.isSelected()
//					 && lb instanceof LiteCustomer )
//			{
//				List cstContacts = DaoFactory.getCustomerDao().getAllContacts( ((LiteCustomer)lb).getCustomerID() );
//				allContacts.addAll( cstContacts );
//			}
//		}
//	}
//
//	return allContacts;
//}

/**
 * Sets our individual contact nodes to disabled if the parent Contact
 * or Customer is selected. The "Make Phone Calls" checkbox is  also
 * disabled if List of conatcts does not have a phone number.
 * 
 */
/*
private void validateIndividualNotifs()
{
	// store all of our selected entries in Hashmap so we can set them below
	// on the NotifcationGroup
	HashMap contNotifIDMap = new HashMap(32);	
	HashMap contIDMap = new HashMap(32);
	List allContacts = null;

	allContacts = getSelectedContacts(contactsNode);

	//store the index we started adding customer Notifcations to 
	int startCustConts = allContacts.size();

	allContacts.addAll( getSelectedContacts(customersNode) );


	for( int i = 0; i < allContacts.size(); i++ )
	{
		LiteContact lContact = (LiteContact)allContacts.get(i);

		//only put contact IDs that are owned by a customer into this map
		if( i >= startCustConts )
			contIDMap.put(
				new Integer(lContact.getContactID()), lContact );

		for( int j = 0; j < lContact.getLiteContactNotifications().size(); j++ )
		{
			LiteContactNotification lcn = (LiteContactNotification)lContact.getLiteContactNotifications().get(j);			
			contNotifIDMap.put(
				new Integer(lcn.getContactNotifID()), lcn );				
		}
	}
	

	for( int i = 0; i < contactsNode.getChildCount(); i++ )
		validateNode( contactsNode.getChildAt(i), contIDMap );

	for( int i = 0; i < emailNode.getChildCount(); i++ )
		validateNode( emailNode.getChildAt(i), contNotifIDMap );

	for( int i = 0; i < phoneNode.getChildCount(); i++ )
		validateNode( phoneNode.getChildAt(i), contNotifIDMap );

}
*/

/**
 * Sets a singled node deslected and disabled if its parent object is already
 * selected.
 * 
 */
/*
private void validateNode( TreeNode realNode, HashMap idMap )
{
	if( realNode instanceof LiteBaseNode ) {

		LiteBaseNode treeNode = (LiteBaseNode)realNode;
		LiteBase elem = (LiteBase)treeNode.getUserObject();
		boolean exists = false;

		if( elem instanceof LiteContactNotification ) {

			LiteContactNotification lcn = (LiteContactNotification)elem;
			exists = idMap.get(new Integer(lcn.getContactNotifID())) != null;
		}
		else if( elem instanceof LiteContact ) {

			LiteContact lc = (LiteContact)elem;
			exists = idMap.get(new Integer(lc.getContactID())) != null;
		}
		
						
		if( exists )
			treeNode.setSelected( false );

		treeNode.setIsSystemReserved( exists );		
	}	
}
*/
	
	/**
	 * This method initializes jSplitPane
	 * 
	 * @return javax.swing.JSplitPane
	 */
	private javax.swing.JSplitPane getJSplitPane() {
		if(jSplitPane == null) {
			jSplitPane = new javax.swing.JSplitPane();
			jSplitPane.setLeftComponent(getJScrollJTree());
			jSplitPane.setRightComponent(getJPanelLoginDescription());
			jSplitPane.setDividerLocation(250);
			jSplitPane.setDividerSize(10);
		}
		return jSplitPane;
	}
	/**
	 * This method initializes jCheckBox
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private javax.swing.JCheckBox getJCheckBoxEmails() {
		if(jCheckBoxEmails == null) {
			jCheckBoxEmails = new javax.swing.JCheckBox();
			jCheckBoxEmails.setText("Send Emails");
			jCheckBoxEmails.setEnabled(false);
			jCheckBoxEmails.setToolTipText("Should emails be sent to the email addresses in this notifcation group");
		}
		return jCheckBoxEmails;
	}
}