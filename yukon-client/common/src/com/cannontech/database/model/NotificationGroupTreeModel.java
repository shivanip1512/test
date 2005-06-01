package com.cannontech.database.model;

/**
 * This type was created in VisualAge.
 */
public class NotificationGroupTreeModel extends DBTreeModel 
{
/**
 * MeterTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public NotificationGroupTreeModel() {
	super( new DBTreeNode("Notification Groups") );
}
/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 2:05:03 PM)
 * @return com.cannontech.database.data.lite.LiteBase[]
 */
public boolean isLiteTypeSupported( int liteType )
{
	return ( liteType == com.cannontech.database.data.lite.LiteTypes.NOTIFICATION_GROUP );
}
/**
 * This method was created in VisualAge.
 */
public void update() {

	com.cannontech.database.cache.DefaultDatabaseCache cache =
					com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

	synchronized(cache)
	{
		java.util.List notifGroups = cache.getAllContactNotificationGroups();

		java.util.Collections.sort( notifGroups, com.cannontech.database.data.lite.LiteComparators.liteStringComparator );

		DBTreeNode rootNode = (DBTreeNode) getRoot();
		rootNode.removeAllChildren();
		
		for( int i = 0; i < notifGroups.size(); i++ )
		{
			DBTreeNode notifGroupNode = new DBTreeNode( notifGroups.get(i) );

			if( ((com.cannontech.database.data.lite.LiteNotificationGroup)notifGroups.get(i)).getNotificationGroupID() != com.cannontech.database.db.point.PointAlarming.NONE_NOTIFICATIONID )
				rootNode.add( notifGroupNode );
		}
	}

	reload();
}
}
