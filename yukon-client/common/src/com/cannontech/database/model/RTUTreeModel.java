package com.cannontech.database.model;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.database.data.pao.PAOGroups;

public class RTUTreeModel extends DeviceTreeModel 
{
/**
 * RtuTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public RTUTreeModel() {
	super( new DBTreeNode("RTUs") );
}
/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 4:11:23 PM)
 * @param deviceType int
 */
public boolean isDeviceValid( int category_, int class_, int type_ )
{
	return (class_ == com.cannontech.database.data.pao.DeviceClasses.LOADMANAGEMENT
				|| class_ == com.cannontech.database.data.pao.DeviceClasses.IED
				|| class_ == com.cannontech.database.data.pao.DeviceClasses.METER
				|| class_ == com.cannontech.database.data.pao.DeviceClasses.RTU
				|| class_ == com.cannontech.database.data.pao.DeviceClasses.TRANSMITTER
				|| class_ == com.cannontech.database.data.pao.DeviceClasses.VIRTUAL
				|| class_ == com.cannontech.database.data.pao.DeviceClasses.RTU
				|| class_ == com.cannontech.database.data.pao.DeviceClasses.SYSTEM)
			 &&
			 (type_ == PAOGroups.RTUILEX 
            || type_ == PAOGroups.RTU_DNP
				|| type_ == PAOGroups.RTUWELCO 
				|| type_ == PAOGroups.DAVISWEATHER)
			 && category_ == PAOGroups.CAT_DEVICE;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return "RTU";
}
}
