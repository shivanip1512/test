package com.cannontech.database.model;

/**
 * This type was created in VisualAge.
 */
public class NotificationRecipientTreeModel extends DBTreeModel 
{
/**
 * MeterTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public NotificationRecipientTreeModel() {
	super( new DBTreeNode("Notification Locations") );
}
/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 2:05:03 PM)
 * @return com.cannontech.database.data.lite.LiteBase[]
 */
public boolean isLiteTypeSupported( int liteType )
{
	return ( liteType == com.cannontech.database.data.lite.LiteTypes.GROUP_RECIPIENT );
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return "Notification Recipients";
}
/**
 * This method was created in VisualAge.
 */
public void update() {

	com.cannontech.database.cache.DefaultDatabaseCache cache =
					com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

	synchronized(cache)
	{
		java.util.List notifLocations = cache.getAllNotificationRecipients();

		java.util.Collections.sort( notifLocations, com.cannontech.database.data.lite.LiteComparators.liteStringComparator );

		DBTreeNode rootNode = (DBTreeNode) getRoot();
		rootNode.removeAllChildren();
		
		for( int i = 0; i < notifLocations.size(); i++ )
		{
			DBTreeNode notifGroupNode = new DBTreeNode( notifLocations.get(i));

			if( ((com.cannontech.database.data.lite.LiteNotificationRecipient)notifLocations.get(i)).getRecipientID() != com.cannontech.database.db.point.PointAlarming.NONE_LOCATIONID )
				rootNode.add( notifGroupNode );
		}
	}

	reload();
}
}
