/*
 * Created on May 4, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.model;

import com.cannontech.database.data.lite.LiteSeasonSchedule;
/**
 * @author jdayton
 */

public class SeasonModel extends DBTreeModel 
{
/**
 * SeasonModel constructor comment.
 */
public SeasonModel() {
	super(new DBTreeNode("Season Schedules"));
}
/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 2:05:03 PM)
 * @return com.cannontech.database.data.lite.LiteBase[]
 */
public boolean isLiteTypeSupported( int liteType )
{
	return ( liteType == com.cannontech.database.data.lite.LiteTypes.SEASON_SCHEDULE );
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 11:27:44 AM)
 * @return java.lang.String
 */
public String toString() {
	return "Season Schedule";
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 10:45:51 AM)
 */
public void update()
{

	com.cannontech.database.cache.DefaultDatabaseCache cache =
		com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

	synchronized (cache)
	{
		java.util.List seasons = cache.getAllSeasonSchedules();

		java.util.Collections.sort(seasons, com.cannontech.database.data.lite.LiteComparators.liteStringComparator);

		DBTreeNode rootNode = (DBTreeNode) getRoot();
		rootNode.removeAllChildren();

		for (int i = 0; i < seasons.size(); i++)
		{
			DBTreeNode seasonNode = new DBTreeNode(seasons.get(i));
			if(((LiteSeasonSchedule)seasons.get(i)).getScheduleID() != 0)
				rootNode.add(seasonNode);
		}
	}

	reload();
}
}
