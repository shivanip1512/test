package com.cannontech.database.model;

import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;

public class MeterTreeModel extends AbstractDeviceTreeModel 
{
/**
 * MeterTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public MeterTreeModel() {
	super( new DBTreeNode("Meters") );
}

/**
 * Returns true if device isMeter (MCT, IED Meters, RFN meters, for example)
 */
@Override
public boolean isDeviceValid( PaoCategory paoCategory, PaoClass paoClass, PaoType paoType )
{
	return paoType.isMeter();
}
}
