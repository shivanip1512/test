package com.cannontech.database.model;

/**
 * This type was created in VisualAge.
 */
public class CapControlFeederModel extends DeviceTreeModel
{
/**
 * IEDTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public CapControlFeederModel() {
	super(true, new DBTreeNode("Cap Feeders") );
}
/**
 * Insert the method's description here.
 * Creation date: (4/24/2002 9:24:01 AM)
 * @return java.util.List
 */
public synchronized java.util.List getCacheList(
		com.cannontech.database.cache.DefaultDatabaseCache cache ) 
{
	return cache.getAllCapControlFeeders();
}
/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 4:11:23 PM)
 * @param deviceType int
 */
public boolean isDeviceValid( int category_, int class_, int type_ )
{
	return( type_ == com.cannontech.database.data.pao.PAOGroups.CAP_CONTROL_FEEDER
			  && category_ == com.cannontech.database.data.pao.PAOGroups.CAT_CAPCONTROL );
}
}
