package com.cannontech.database.model;

/**
 * This type was created in VisualAge.
 */

public class CollectionGroupModel extends DBTreeModel 
{
/**
 * LMGroupEmetconModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public CollectionGroupModel() {
	super( new DBTreeNode("Collection Group") );
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
	return "Collection Group";
}
/**
 * update method comment.
 */
public void update()
{
	String availableCollectionGroupsArray[] = null;
	try
	{
		availableCollectionGroupsArray = com.cannontech.database.db.device.DeviceMeterGroup.getDeviceCollectionGroups();
	}
	catch(java.sql.SQLException e)
	{
		e.printStackTrace();
	}

	DBTreeNode rootNode = (DBTreeNode) getRoot();
	rootNode.removeAllChildren();

	for (int i = 0; i <  availableCollectionGroupsArray.length; i++)
	{
		DBTreeNode cycleGroupNode = new DBTreeNode( availableCollectionGroupsArray[i] );
		rootNode.add(cycleGroupNode);
	}

	reload();
}
}
