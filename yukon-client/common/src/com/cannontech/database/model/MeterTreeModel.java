package com.cannontech.database.model;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.device.DeviceTypesFuncs;

public class MeterTreeModel extends AbstractDeviceTreeModel 
{
/**
 * MeterTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public MeterTreeModel() {
	super( new DBTreeNode("Meters") );
}

/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 4:11:23 PM)
 * @param deviceType int
 */
public boolean isDeviceValid( PaoCategory paoCategory, PaoClass paoClass, PaoType paoType )
{
	return( DeviceTypesFuncs.isMCT(paoType.getDeviceTypeId())
			  || DeviceTypesFuncs.isMeter(paoType.getDeviceTypeId())
			  && paoCategory == PaoCategory.DEVICE );
}
}
