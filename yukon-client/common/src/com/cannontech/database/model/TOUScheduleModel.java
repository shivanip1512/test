/*
 * Created on Sep 22, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.model;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TOUScheduleModel extends DBTreeModel
{
/**
 * HolidayScheduleModel constructor comment.
 */
public TOUScheduleModel() {
	super(new DBTreeNode("TOU Schedule"));
}
/**
 * Insert the method's description here.
 * Creation date: (9/24/2004 2:05:03 PM)
 * @return com.cannontech.database.data.lite.LiteBase[]
 */
public boolean isLiteTypeSupported( int liteType )
{
	return ( liteType == com.cannontech.database.data.lite.LiteTypes.TOU_SCHEDULE );
}
/**
 * Insert the method's description here.
 * Creation date: (9/24/2004 11:27:44 AM)
 * @return java.lang.String
 */
public String toString() {
	return "TOU Schedule";
}
/**
 * Insert the method's description here.
 * Creation date: (9/24/2004 10:45:51 AM)
 */
public void update()
{

	com.cannontech.database.cache.DefaultDatabaseCache cache =
		com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

	synchronized (cache)
	{
		java.util.List theSchedules = cache.getAllTOUSchedules();

		java.util.Collections.sort(theSchedules, com.cannontech.database.data.lite.LiteComparators.liteStringComparator);

		DBTreeNode rootNode = (DBTreeNode) getRoot();
		rootNode.removeAllChildren();

		for (int i = 0; i < theSchedules.size(); i++)
		{
			DBTreeNode schedNode = new DBTreeNode(theSchedules.get(i));

			rootNode.add(schedNode);
		}
	}

	reload();
}
}
