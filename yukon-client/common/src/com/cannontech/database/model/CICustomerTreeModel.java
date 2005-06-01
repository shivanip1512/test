package com.cannontech.database.model;

/**
 * This type was created in VisualAge.
 */
import javax.swing.tree.TreePath;

import com.cannontech.database.cache.functions.ContactFuncs;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteContact;


/* *******
 * This model is good now, it was slow.
 * 
 * 
*/
public class CICustomerTreeModel extends DBTreeModel 
{
/**
 * CICustomerTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public CICustomerTreeModel() {
	super( new DBTreeNode("CI Customers") );
}
/**
 * Insert the method's description here.
 * Creation date: (4/17/2002 1:58:45 PM)
 * @param lite com.cannontech.database.data.lite.LiteBase
 */
public boolean insertTreeObject( LiteBase lb ) 
{
	if( lb == null || !isLiteTypeSupported(lb.getLiteType()) )
		return false;

	DBTreeNode rootNode = (DBTreeNode) getRoot();
		
	if( lb instanceof LiteContact )
	{
		int contactID = ((LiteContact)lb).getContactID();

		LiteCICustomer ownerCst = ContactFuncs.getOwnerCICustomer( contactID );

		rootNode = findLiteObject( null, ownerCst );

		if( rootNode != null )
		{

			//this will force us to reload ALL the Contacts for this CICustomer
			rootNode.setWillHaveChildren(true);
			TreePath rootPath = new TreePath( rootNode );
			treePathWillExpand( rootPath );

			updateTreeNodeStructure( rootNode );

			return true;
		}

	}
	else if ( lb instanceof LiteCICustomer )
	{
		LiteCICustomer liteCst = (LiteCICustomer)lb;

		DBTreeNode node = new DBTreeNode(lb);

		//add all new tree nodes to the top, for now
		int[] ind = { 0 };
		
		rootNode.insert( node, ind[0] );
		
		nodesWereInserted(
			rootNode,
			ind );

		node.setWillHaveChildren(true);

		return true;
	}

	return false;
}

/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 2:05:03 PM)
 * @return com.cannontech.database.data.lite.LiteBase[]
 */
public boolean isLiteTypeSupported( int liteType )
{
	return ( liteType == com.cannontech.database.data.lite.LiteTypes.CUSTOMER_CI
		  		 || liteType == com.cannontech.database.data.lite.LiteTypes.CONTACT );
}


public synchronized void treePathWillExpand(javax.swing.tree.TreePath path)
{
	//Watch out, this reloads the contacts every TIME!!!
	DBTreeNode node = (DBTreeNode)path.getLastPathComponent();

	if( node.willHaveChildren() &&
		 node.getUserObject() instanceof LiteCICustomer )
	{
		LiteCICustomer ciCust = (LiteCICustomer)node.getUserObject();
		
		node.removeAllChildren();
		for( int i = 0; i < ciCust.getAdditionalContacts().size(); i++ )
			node.add( new DBTreeNode(ciCust.getAdditionalContacts().get(i)) );
	}

	node.setWillHaveChildren(false);
}

/*
 * Insert the method's description here.
 * Creation date: (4/17/2002 1:58:45 PM)
 * @param lite com.cannontech.database.data.lite.LiteBase
 *
public boolean updateTreeObject(LiteBase lb) 
{
	if( lb == null || !isLiteTypeSupported(lb.getLiteType()) )
		return false;

java.util.Date s = new java.util.Date();
	DBTreeNode node = findLiteObject( null, lb );

com.cannontech.clientutils.CTILogger.info("*** !!! UPDATE Took " + 
	(new java.util.Date().getTime() - s.getTime()) * .001 + 
	" seconds to find node in DBtreeModel, node = " + node);


	if( node != null )
	{
		node.setWillHaveChildren( true );
		treePathWillExpand( new TreePath(node) );
		nodeStructureChanged( node );
		
		
		return true;
	}

	return false;
}
*/

/**
 * Insert the method's description here.
 * Creation date: (4/17/2002 1:58:45 PM)
 * @param lite com.cannontech.database.data.lite.LiteBase
 */
public boolean updateTreeObject(LiteBase lb) 
{
	if( lb == null || !isLiteTypeSupported(lb.getLiteType()) )
		return false;

	DBTreeNode node = findLiteObject( null, lb );

	if( node != null )
	{
		//slightyly different from the SUPER
		node.setWillHaveChildren( true );
		treePathWillExpand( new TreePath(node) );
		nodeStructureChanged( node );

		return true;			
	}

	return false;
}
/**
 * This method was created in VisualAge.
 */
public void update() 
{
	com.cannontech.database.cache.DefaultDatabaseCache cache =
					com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

	synchronized(cache)
	{
		java.util.List ciCustomers = cache.getAllCICustomers();
		java.util.Collections.sort( ciCustomers, com.cannontech.database.data.lite.LiteComparators.liteStringComparator );
		
		DBTreeNode rootNode = (DBTreeNode) getRoot();
		rootNode.removeAllChildren();
		
		for( int i = 0; i < ciCustomers.size(); i++ )
		{
			DBTreeNode custNode = new DBTreeNode( ciCustomers.get(i) );
			rootNode.add( custNode );

			LiteCICustomer ltCust = (LiteCICustomer)ciCustomers.get(i);

			custNode.setWillHaveChildren(true);
		}
	}

	reload();
}

}
