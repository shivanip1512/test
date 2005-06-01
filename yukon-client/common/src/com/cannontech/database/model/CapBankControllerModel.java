package com.cannontech.database.model;

/**
 * This type was created in VisualAge.
 */

public class CapBankControllerModel extends DeviceTreeModel
{
/**
 * IEDTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public CapBankControllerModel() {
	super( new DBTreeNode("Cap Bank Controllers") );
}
/**
 * IEDTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public CapBankControllerModel( boolean showPointNodes)
{
	super( showPointNodes,
			new DBTreeNode("Cap Bank Controllers") );
}
/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 4:11:23 PM)
 * @param deviceType int
 */
public boolean isDeviceValid( int category_, int class_, int type_ )
{
	return( com.cannontech.database.data.device.DeviceTypesFuncs.isCapBankController(type_)
			  && class_ == com.cannontech.database.data.pao.PAOGroups.CLASS_CAPCONTROL
			  && category_ == com.cannontech.database.data.pao.PAOGroups.CAT_DEVICE );
}
}
