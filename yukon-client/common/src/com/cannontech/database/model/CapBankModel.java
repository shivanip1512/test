package com.cannontech.database.model;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.database.data.pao.PAOGroups;

public class CapBankModel extends DeviceTreeModel 
{
/**
 * IEDTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public CapBankModel() {
	super( new DBTreeNode("Cap Banks") );
}
/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 4:11:23 PM)
 * @param deviceType int
 */
public boolean isDeviceValid( int category_, int class_, int type_ )
{
	return( type_ == PAOGroups.CAPBANK
			  && category_ == PAOGroups.CAT_DEVICE );
}
}
