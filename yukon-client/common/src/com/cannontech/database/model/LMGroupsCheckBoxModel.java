package com.cannontech.database.model;

import com.cannontech.common.gui.tree.CheckNode;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.database.data.pao.PAOGroups;


/**
 * This type was created in VisualAge.
 */

public class LMGroupsCheckBoxModel extends DeviceCheckBoxTreeModel
{
	public static String TITLE_STRING = "Load Group";	

	public LMGroupsCheckBoxModel()
	{
		super( new CheckNode(TITLE_STRING) );
	}
	public LMGroupsCheckBoxModel( boolean showPointNodes ) 
	{
		super( showPointNodes, new CheckNode(TITLE_STRING) );
	}
	public synchronized java.util.List getCacheList( DefaultDatabaseCache cache ) 
	{
		return cache.getAllLoadManagement();
	}
	public boolean isDeviceValid( int category_, int class_, int type_ )
	{
		return( class_ == DeviceClasses.GROUP && category_ == PAOGroups.CAT_DEVICE );
	}

	public String toString()
	{
		return TITLE_STRING;
	}
}
