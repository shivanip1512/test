package com.cannontech.database.model;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.database.data.pao.PAOGroups;

public class LMGroupEmetconModel extends DeviceTreeModel 
{
/**
 * LMGroupEmetconModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public LMGroupEmetconModel() {
	super( new DBTreeNode("EMETCON Group") );
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
	return( type_ == PAOGroups.LM_GROUP_EMETCON
			  && class_ == com.cannontech.database.data.pao.DeviceClasses.GROUP
			  && category_ == PAOGroups.CAT_DEVICE );
}
}
