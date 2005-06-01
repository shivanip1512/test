package com.cannontech.database.model;

/**
 * This type was created in VisualAge.
 */
public class CapControlSubBusModel extends DeviceTreeModel 
{
/**
 * IEDTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public CapControlSubBusModel() {
	super( true, new DBTreeNode("Substation Bus") );
}
/**
 * Insert the method's description here.
 * Creation date: (4/24/2002 9:24:01 AM)
 * @return java.util.List
 */
public synchronized java.util.List getCacheList(
		com.cannontech.database.cache.DefaultDatabaseCache cache ) 
{
	return cache.getAllCapControlSubBuses();
}
/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 4:11:23 PM)
 * @param deviceType int
 */
public boolean isDeviceValid( int category_, int class_, int type_ )
{
	return( type_ == com.cannontech.database.data.pao.PAOGroups.CAP_CONTROL_SUBBUS
			  && category_ == com.cannontech.database.data.pao.PAOGroups.CAT_CAPCONTROL );
}
}
