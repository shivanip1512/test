package com.cannontech.database.model;

import com.cannontech.database.data.lite.LiteBase;

/**
 * This type was created in VisualAge.
 */
public class MCTTreeModel extends DeviceTreeModel
{
/**
 * MCTTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public MCTTreeModel() {
	super( new DBTreeNode("MCTs") );
}

@Override
public boolean isTreePrimaryForObject(LiteBase lb) {
    return false;
}

/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 4:11:23 PM)
 * @param deviceType int
 */
public boolean isDeviceValid( int category_, int class_, int type_ )
{
	return ( com.cannontech.database.data.device.DeviceTypesFuncs.isMCT(type_)
			  && category_ == com.cannontech.database.data.pao.PAOGroups.CAT_DEVICE );
}
}
