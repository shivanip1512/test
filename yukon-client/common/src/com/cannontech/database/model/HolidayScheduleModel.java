package com.cannontech.database.model;

import com.cannontech.database.data.lite.LiteHolidaySchedule;

/**
 * Insert the type's description here.
 * Creation date: (8/24/2001 10:45:17 AM)
 * @author: 
 */
public class HolidayScheduleModel extends DBTreeModel 
{
/**
 * HolidayScheduleModel constructor comment.
 */
public HolidayScheduleModel() {
	super(new DBTreeNode("Holiday Schedule"));
}
/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 2:05:03 PM)
 * @return com.cannontech.database.data.lite.LiteBase[]
 */
public boolean isLiteTypeSupported( int liteType )
{
	return ( liteType == com.cannontech.database.data.lite.LiteTypes.HOLIDAY_SCHEDULE );
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 11:27:44 AM)
 * @return java.lang.String
 */
public String toString() {
	return "Holiday Schedule";
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
		java.util.List holidaySchedules = cache.getAllHolidaySchedules();

		java.util.Collections.sort(holidaySchedules, com.cannontech.database.data.lite.LiteComparators.liteStringComparator);

		DBTreeNode rootNode = (DBTreeNode) getRoot();
		rootNode.removeAllChildren();

		for (int i = 0; i < holidaySchedules.size(); i++)
		{
			DBTreeNode holidayScheduleNode = new DBTreeNode(holidaySchedules.get(i));
			if(((LiteHolidaySchedule)holidaySchedules.get(i)).getHolidayScheduleID() == 0)
				holidayScheduleNode.setIsSystemReserved(true);

			rootNode.add(holidayScheduleNode);
		}
	}

	reload();
}
}
