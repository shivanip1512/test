package com.cannontech.database.model;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteTypes;

/**
 * This type was created in VisualAge.
 */
public class ContactNotificationTreeModel extends DBTreeModel 
{
/**
 * MeterTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public ContactNotificationTreeModel() {
	super( new DBTreeNode("Contact Notifications") );
}
/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 2:05:03 PM)
 * @return com.cannontech.database.data.lite.LiteBase[]
 */
public boolean isLiteTypeSupported( int liteType )
{
	return ( liteType == LiteTypes.CONTACT_NOTIFICATION );
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return "Contact Notifications";
}
/**
 * This method was created in VisualAge.
 */
public void update() 
{
/*
	com.cannontech.database.cache.DefaultDatabaseCache cache =
					com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

	synchronized(cache)
	{
		java.util.List cntNotifs = cache.getAllContactNotifications();

		java.util.Collections.sort( cntNotifs, com.cannontech.database.data.lite.LiteComparators.liteStringComparator );

		DBTreeNode rootNode = (DBTreeNode) getRoot();
		rootNode.removeAllChildren();
		
		for( int i = 0; i < cntNotifs.size(); i++ )
		{
			DBTreeNode notifGroupNode = new DBTreeNode( cntNotifs.get(i));

			if( ((LiteContactNotification)cntNotifs.get(i)).getContactID() != CtiUtilities.NONE_ID )
				rootNode.add( notifGroupNode );
		}
	}

	reload();
*/
}
}
