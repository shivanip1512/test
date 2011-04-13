package com.cannontech.database.model;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.yukon.IDatabaseCache;

public class LMProgramModel extends AbstractDeviceTreeModel
{
/**
 * LMGroupEmetconModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public LMProgramModel() {
	super( new DBTreeNode("Load Program") );
}
/**
 * Insert the method's description here.
 * Creation date: (4/24/2002 9:24:01 AM)
 * @return java.util.List
 */
public synchronized java.util.List getCacheList(IDatabaseCache cache ) 
{
	return cache.getAllLoadManagement();
}
/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 4:11:23 PM)
 * @param deviceType int
 */
public boolean isDeviceValid( PaoCategory paoCategory, PaoClass paoClass, PaoType paoType )
{
	return( DeviceTypesFuncs.isLMProgram(paoType.getDeviceTypeId())
			  && paoCategory == PaoCategory.LOADMANAGEMENT);
}
}
