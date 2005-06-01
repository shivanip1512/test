package com.cannontech.database.model;


/**
 * This type was created in VisualAge.
 */
public class ContactTreeModel extends DBTreeModel 
{
/**
 * MeterTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public ContactTreeModel() {
	super( new DBTreeNode("Contacts") );
}
/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 2:05:03 PM)
 * @return com.cannontech.database.data.lite.LiteBase[]
 */
public boolean isLiteTypeSupported( int liteType )
{
	return ( liteType == com.cannontech.database.data.lite.LiteTypes.CONTACT );
}/**
 * This method was created in VisualAge.
 */
public void update() 
{
	com.cannontech.database.cache.DefaultDatabaseCache cache =
					com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

	synchronized(cache)
	{
		java.util.List contacts = cache.getAllContacts();

		java.util.Collections.sort( 
					contacts, 
					com.cannontech.database.data.lite.LiteComparators.liteStringComparator );

		DBTreeNode rootNode = (DBTreeNode) getRoot();
		rootNode.removeAllChildren();
		
		for( int i = 0; i < contacts.size(); i++ )
		{
			DBTreeNode stateGroupNode = new DBTreeNode( contacts.get(i) );	
			rootNode.add( stateGroupNode );
		}
	}

	reload();
}
}
