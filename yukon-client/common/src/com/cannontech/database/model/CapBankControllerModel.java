package com.cannontech.database.model;

import com.cannontech.capcontrol.CBCUtils;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.device.DeviceTypesFuncs;

/**
 * This type was created in VisualAge.
 */

public class CapBankControllerModel extends AbstractDeviceTreeModel
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
public boolean isDeviceValid( PaoCategory paoCategory, PaoClass paoClass, PaoType paoType )
{
	return( CBCUtils.isCapBankController(paoType)
			  && paoClass == PaoClass.CAPCONTROL
			  && paoCategory == PaoCategory.DEVICE );
}
}
