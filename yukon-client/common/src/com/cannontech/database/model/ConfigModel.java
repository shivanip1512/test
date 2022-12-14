/*
 * Created on Dec 18, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.model;

import com.cannontech.yukon.IDatabaseCache;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */

public class ConfigModel extends DBTreeModel 
{
/**
 * HolidayScheduleModel constructor comment.
 */
public ConfigModel() {
	super(new DBTreeNode("MCT Config"));
}
/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 2:05:03 PM)
 * @return com.cannontech.database.data.lite.LiteBase[]
 */
public boolean isLiteTypeSupported( int liteType )
{
	return ( liteType == com.cannontech.database.data.lite.LiteTypes.CONFIG );
}

/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 10:45:51 AM)
 */
public void update()
{

	IDatabaseCache cache =
		com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

	synchronized (cache)
	{
		java.util.List theConfigs = cache.getAllConfigs();

		java.util.Collections.sort(theConfigs, com.cannontech.database.data.lite.LiteComparators.liteStringComparator);

		DBTreeNode rootNode = (DBTreeNode) getRoot();
		rootNode.removeAllChildren();

		for (int i = 0; i < theConfigs.size(); i++)
		{
			DBTreeNode configNode = new DBTreeNode(theConfigs.get(i));

			rootNode.add(configNode);
		}
	}

	reload();
}
}
