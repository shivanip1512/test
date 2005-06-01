package com.cannontech.database.model;

/**
 * This type was created in VisualAge.
 */

public class AlarmCategoriesTreeModel extends DBTreeModel 
{
/**
 * MeterTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public AlarmCategoriesTreeModel() {
	super( new DBTreeNode("Alarm Categories") );
}
/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 2:05:03 PM)
 * @return com.cannontech.database.data.lite.LiteBase[]
 */
public boolean isLiteTypeSupported( int liteType )
{
	return ( liteType == com.cannontech.database.data.lite.LiteTypes.ALARM_CATEGORIES );
}
/**
 * This method was created in VisualAge.
 */
public void update() {

	com.cannontech.database.cache.DefaultDatabaseCache cache =
					com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

	synchronized(cache)
	{
		java.util.List alarmStates = cache.getAllAlarmCategories();

		DBTreeNode rootNode = (DBTreeNode) getRoot();
		rootNode.removeAllChildren();

		// We start at 1 to leave out the EVENT alarmCategory
		for( int i = 1; i < alarmStates.size(); i++ )
		{
			DBTreeNode notifGroupNode = new DBTreeNode( alarmStates.get(i) );
			notifGroupNode.setIsSystemReserved( true );
			rootNode.add( notifGroupNode );
		}
	}

	reload();
}
}
