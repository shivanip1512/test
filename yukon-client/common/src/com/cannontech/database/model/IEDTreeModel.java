package com.cannontech.database.model;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.pao.PAOGroups;

public class IEDTreeModel extends DeviceTreeModel 
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
public boolean isDeviceValid( int category_, int class_, int type_ )
{
	return( DeviceTypesFuncs.isMeter(type_) 
		 	  || type_ == PAOGroups.DAVISWEATHER
			  && category_ == PAOGroups.CAT_DEVICE );
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return "IED";
}
}
