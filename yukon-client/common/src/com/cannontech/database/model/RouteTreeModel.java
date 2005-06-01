package com.cannontech.database.model;

/**
 * This type was created in VisualAge.
 */
public class RouteTreeModel extends DeviceTreeModel 
{
/**
 * RouteTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public RouteTreeModel() {
	super( false, new DBTreeNode("Routes") );
}
/**
 * Insert the method's description here.
 * Creation date: (4/24/2002 9:24:01 AM)
 * @return java.util.List
 */
public synchronized java.util.List getCacheList(
		com.cannontech.database.cache.DefaultDatabaseCache cache ) 
{
	return cache.getAllRoutes();
}
/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 4:11:23 PM)
 * @param deviceType int
 */
public boolean isDeviceValid( int category_, int class_, int type_ )
{
	return ( category_ == com.cannontech.database.data.pao.PAOGroups.CAT_ROUTE );
}
/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 2:05:03 PM)
 * @return com.cannontech.database.data.lite.LiteBase[]
 */
public boolean isLiteTypeSupported( int liteType )
{
	return ( liteType == com.cannontech.database.data.lite.LiteTypes.YUKON_PAOBJECT );
}

}
