package com.cannontech.database.model;

import com.cannontech.database.cache.DefaultDatabaseCache;

/**
 * This type was created in VisualAge.
 */

public class BillingGroupModel extends DBTreeModel 
{
/**
 * LMGroupEmetconModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public BillingGroupModel() {
	super( new DBTreeNode("Billing Group") );
}
/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 2:05:03 PM)
 * @return com.cannontech.database.data.lite.LiteBase[]
 */
public boolean isLiteTypeSupported( int liteType )
{
	return false;
}
/**
 * update method comment.
 */
public void update()
{
    DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();

	synchronized(cache)
	{
	    //Returns a List of Strings
		java.util.List billingGroups = cache.getAllDMG_BillingGroups();
		java.util.Collections.sort( billingGroups);
		
		DBTreeNode rootNode = (DBTreeNode) getRoot();
		rootNode.removeAllChildren();
		
		for( int i = 0; i < billingGroups.size(); i++ )
		{
			DBTreeNode groupNode = new DBTreeNode( billingGroups.get(i) );
			rootNode.add( groupNode );
		}
	}
	
	reload();
}
}
