/*
 * Created on Feb 5, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.model;

import com.cannontech.common.gui.tree.CheckNode;


/**
 * @author bjonasson
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CollectionGroupCheckBoxTreeModel extends DeviceCheckBoxTreeModel
{
	public CollectionGroupCheckBoxTreeModel() {
			super( new CheckNode("Collection Group") );
}

public boolean isLiteTypeSupported( int liteType )
{
	return false;
}

public String toString() {
	return "Collection Group";
}

public void update()
{
	String availableCollectionGroupsArray[] = null;
	try
	{
		availableCollectionGroupsArray = com.cannontech.database.db.device.DeviceMeterGroup.getDeviceCollectionGroups();
	}
	catch(java.sql.SQLException e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
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





