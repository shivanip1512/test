package com.cannontech.database.model;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.device.DeviceTypesFuncs;

public class IEDTreeModel extends AbstractDeviceTreeModel 
{
/**
 * IEDTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public IEDTreeModel() 
{
	super( new DBTreeNode("IEDs") );
}

/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 4:11:23 PM)
 * @param deviceType int
 */
public boolean isDeviceValid( PaoCategory paoCategory, PaoClass paoClass, PaoType paoType )
{
	return( DeviceTypesFuncs.isMeter(paoType.getDeviceTypeId()) 
		 	  || paoType == PaoType.DAVISWEATHER
			  && paoCategory == PaoCategory.DEVICE );
}
}
