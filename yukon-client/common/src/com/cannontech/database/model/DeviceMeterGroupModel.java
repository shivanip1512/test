package com.cannontech.database.model;

/**
 * This type was created in VisualAge.
 */

public class DeviceMeterGroupModel extends DBTreeModel 
{
/**
 * DeviceTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public DeviceMeterGroupModel() {
	super( new DBTreeNode("Meter Numbers") );
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
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return "Meter Number";
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
		java.util.List deviceMeterGroupsList = cache.getAllDeviceMeterGroups();
		java.util.Collections.sort( deviceMeterGroupsList, com.cannontech.database.data.lite.LiteComparators.liteStringComparator );
		
		DBTreeNode rootNode = (DBTreeNode) getRoot();
		rootNode.removeAllChildren();
		
		int deviceDevID;
		int deviceClass;
		int deviceType;
		for( int i = 0; i < deviceMeterGroupsList.size(); i++ )
		{
			DBTreeNode deviceNode = new DBTreeNode( deviceMeterGroupsList.get(i));
			rootNode.add( deviceNode );
		}
	}

	reload();
}
}
