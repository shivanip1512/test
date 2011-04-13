package com.cannontech.database.model;

import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.yukon.IDatabaseCache;

/**
 * This type was created in VisualAge.
 */
public class CapControlSubBusModel extends AbstractDeviceTreeModel 
{
/**
 * IEDTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public CapControlSubBusModel() {
	super( true, new DBTreeNode("Substation Bus") );
}
/**
 * Insert the method's description here.
 * Creation date: (4/24/2002 9:24:01 AM)
 * @return java.util.List
 */
public synchronized java.util.List getCacheList(IDatabaseCache cache ) 
{
	return cache.getAllCapControlSubBuses();
}
/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 4:11:23 PM)
 * @param deviceType int
 */
public boolean isDeviceValid( PaoCategory paoCategory, PaoClass paoClass, PaoType paoType )
{
	return( paoType == PaoType.CAP_CONTROL_SUBBUS
			  && paoCategory == PaoCategory.CAPCONTROL );
}
}
