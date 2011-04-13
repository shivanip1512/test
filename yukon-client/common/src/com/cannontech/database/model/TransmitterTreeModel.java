package com.cannontech.database.model;

import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.pao.DeviceClasses;

/**
 * This type was created in VisualAge.
 */
public class TransmitterTreeModel extends AbstractDeviceTreeModel 
{
/**
 * TransmitterTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public TransmitterTreeModel() {
	this( true );
}
/**
 * TransmitterTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public TransmitterTreeModel( boolean showPointNodes)
{
	this( showPointNodes, new DBTreeNode("Transmitters") );
}
/**
 * TransmitterTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public TransmitterTreeModel( boolean showPointNodes, DBTreeNode rootNode_ )
{
	super( showPointNodes, rootNode_ );
}
/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 4:11:23 PM)
 * @param deviceType int
 */
public boolean isDeviceValid( PaoCategory paoCategory, PaoClass paoClass, PaoType paoType )
{
	return( DeviceTypesFuncs.isTransmitter(paoType.getDeviceTypeId())
			  && DeviceClasses.isMeterClass(paoClass.getPaoClassId())
			  && paoCategory == PaoCategory.DEVICE);
}
}
