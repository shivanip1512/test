package com.cannontech.database.model;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.database.data.device.DeviceTypesFuncs;

public class MeterTreeModel extends DeviceTreeModel 
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
public boolean isDeviceValid( int category_, int class_, int type_ )
{
	return( DeviceTypesFuncs.isMCT(type_)
			  || DeviceTypesFuncs.isMeter(type_)
			  && category_ == com.cannontech.database.data.pao.PAOGroups.CAT_DEVICE );
}
}
