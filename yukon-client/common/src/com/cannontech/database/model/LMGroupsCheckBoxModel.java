package com.cannontech.database.model;

import com.cannontech.common.gui.tree.CheckNode;


/**
 * This type was created in VisualAge.
 */

public class LMGroupsCheckBoxModel extends DeviceCheckBoxTreeModel
{
/**
 * LMGroupEmetconModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public LMGroupsCheckBoxModel() {
	super( new CheckNode("Load Group") );
}
/**
 * LMGroupEmetconModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public LMGroupsCheckBoxModel( boolean showPointNodes ) 
{
	super( showPointNodes, new CheckNode("Load Group") );
}
/**
 * Insert the method's description here.
 * Creation date: (4/24/2002 9:24:01 AM)
 * @return java.util.List
 */
public synchronized java.util.List getCacheList(
		com.cannontech.database.cache.DefaultDatabaseCache cache ) 
{
	return cache.getAllLoadManagement();
}
/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 4:11:23 PM)
 * @param deviceType int
 */
public boolean isDeviceValid( int category_, int class_, int type_ )
{
	return( class_ == com.cannontech.database.data.pao.DeviceClasses.GROUP
			  && category_ == com.cannontech.database.data.pao.PAOGroups.CAT_DEVICE );
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return "Load Group";
}
}
