package com.cannontech.database.model;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.database.data.device.DeviceTypesFuncs;

public class ReceiverTreeModel extends DeviceTreeModel 
{
/**
 * RtuTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public ReceiverTreeModel() {
	super( new DBTreeNode("Receivers") );
}
/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 4:11:23 PM)
 * @param deviceType int
 */
public boolean isDeviceValid( int category_, int class_, int type_ )
{
	return DeviceTypesFuncs.isReceiver(type_);
}
}
