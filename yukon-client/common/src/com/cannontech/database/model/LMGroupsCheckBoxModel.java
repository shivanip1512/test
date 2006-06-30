package com.cannontech.database.model;

import com.cannontech.common.gui.tree.CheckNode;
import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.yukon.IDatabaseCache;


/**
 * This type was created in VisualAge.
 */

public class LMGroupsCheckBoxModel extends DeviceCheckBoxTreeModel
{
	public LMGroupsCheckBoxModel()
	{
		super( new CheckNode(ModelFactory.getModelString(ModelFactory.LMGROUP_CHECKBOX) ));
	}
	public LMGroupsCheckBoxModel( boolean showPointNodes ) 
	{
		super( showPointNodes, new CheckNode(ModelFactory.getModelString(ModelFactory.LMGROUP_CHECKBOX)) );
	}
	public synchronized java.util.List getCacheList( IDatabaseCache cache ) 
	{
		return cache.getAllLoadManagement();
	}
	public boolean isDeviceValid( int category_, int class_, int type_ )
	{
		return( class_ == DeviceClasses.GROUP && category_ == PAOGroups.CAT_DEVICE );
	}
}
