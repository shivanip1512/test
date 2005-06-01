package com.cannontech.database.model;

/**
 * This type was created in VisualAge.
 */
public class TransmitterTreeModel extends DeviceTreeModel 
{
/**
 * TransmitterTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public TransmitterTreeModel() {
	super( new DBTreeNode("Transmitters") );
}
/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 4:11:23 PM)
 * @param deviceType int
 */
public boolean isDeviceValid( int category_, int class_, int type_ )
{
	return( com.cannontech.database.data.device.DeviceTypesFuncs.isTransmitter(type_)
			  && com.cannontech.database.data.pao.DeviceClasses.isMeterClass(class_)
			  && category_ == com.cannontech.database.data.pao.PAOGroups.CAT_DEVICE );
}
}
