/*
 * Created on Feb 5, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.model;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.gui.tree.CheckNode;
import com.cannontech.database.db.device.DeviceMeterGroup;


/**
 * @author bjonasson
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CollectionGroupCheckBoxTreeModel extends DeviceCheckBoxTreeModel
{
	public static String TITLE_STRING = "Collection Group";
	
	public CollectionGroupCheckBoxTreeModel()
	{
		super( new CheckNode(TITLE_STRING) );
	}

	public boolean isLiteTypeSupported( int liteType )
	{
		return false;
	}
	
	public String toString()
	{
		return TITLE_STRING;
	}

	public void update()
	{
		String availableCollectionGroupsArray[] = null;
		try
		{
			availableCollectionGroupsArray = DeviceMeterGroup.getDeviceCollectionGroups();
		}
		catch(java.sql.SQLException e)
		{
			CTILogger.error( e.getMessage(), e );
		}
	
		CheckNode rootNode = (CheckNode) getRoot();
		rootNode.removeAllChildren();
	
		for (int i = 0; i <  availableCollectionGroupsArray.length; i++)
		{
			CheckNode cycleGroupNode = new CheckNode( availableCollectionGroupsArray[i] );
			rootNode.add(cycleGroupNode);
		}
	
		reload();
	}
}





