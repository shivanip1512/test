package com.cannontech.database.model;

/**
 * Insert the type's description here.
 * Creation date: (8/24/2001 10:45:17 AM)
 * @author: 
 */
public class BaselineModel extends DBTreeModel 
{
/**
 * HolidayScheduleModel constructor comment.
 */
public BaselineModel() {
	super(new DBTreeNode("Baseline"));
}
/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 2:05:03 PM)
 * @return com.cannontech.database.data.lite.LiteBase[]
 */
public boolean isLiteTypeSupported( int liteType )
{
	return ( liteType == com.cannontech.database.data.lite.LiteTypes.BASELINE );
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 11:27:44 AM)
 * @return java.lang.String
 */
public String toString() {
	return "Baseline";
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
		java.util.List theBaselines = cache.getAllBaselines();

		java.util.Collections.sort(theBaselines, com.cannontech.database.data.lite.LiteComparators.liteStringComparator);

		DBTreeNode rootNode = (DBTreeNode) getRoot();
		rootNode.removeAllChildren();

		for (int i = 0; i < theBaselines.size(); i++)
		{
			DBTreeNode baselineNode = new DBTreeNode(theBaselines.get(i));
			
			if(((com.cannontech.database.data.lite.LiteBaseline)theBaselines.get(i)).getBaselineID() == 1)
				baselineNode.setIsSystemReserved(true);

			rootNode.add(baselineNode);
		}
	}

	reload();
}
}
