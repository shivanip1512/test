package com.cannontech.database.model;

import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.device.DeviceTypesFuncs;


/**
 * This type was created in VisualAge.
 */
public class MCTTreeModel extends AbstractDeviceTreeModel
{
/**
 * MCTTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public MCTTreeModel() {
	super( new DBTreeNode("MCTs") );
}

/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 4:11:23 PM)
 * @param deviceType int
 */
public boolean isDeviceValid( PaoCategory paoCategory, PaoClass paoClass, PaoType paoType )
{
	return ( DeviceTypesFuncs.isMCT(paoType.getDeviceTypeId())
			  && paoCategory == PaoCategory.DEVICE);
}
}
