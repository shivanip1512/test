package com.cannontech.database.model;

/**
 * This type was created in VisualAge.
 */

public class TestCollectionGroupModel extends DBTreeModel 
{
/**
 * LMGroupEmetconModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public TestCollectionGroupModel() {
	super( new DBTreeNode("Test Collection Group") );
}
/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 2:05:03 PM)
 * @return com.cannontech.database.data.lite.LiteBase[]
 */
public boolean isLiteTypeSupported( int liteType )
{
	return false;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return "Test Collection Group";
}
/**
 * update method comment.
 */
public void update()
{
	String availableTestCollectionGroupsArray[] = null;
	
	try
	{
		availableTestCollectionGroupsArray = com.cannontech.database.db.device.DeviceMeterGroup.getDeviceTestCollectionGroups();
	}
	catch(java.sql.SQLException e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}

	DBTreeNode rootNode = (DBTreeNode) getRoot();
	rootNode.removeAllChildren();

	for (int i = 0; i < availableTestCollectionGroupsArray.length; i++)
	{
		DBTreeNode areaCodeGroupNode = new DBTreeNode( availableTestCollectionGroupsArray[i] );
		rootNode.add(areaCodeGroupNode);
	}
}
}
