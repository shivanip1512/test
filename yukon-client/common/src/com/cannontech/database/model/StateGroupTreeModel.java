package com.cannontech.database.model;

/**
 * This type was created in VisualAge.
 */
public class StateGroupTreeModel extends DBTreeModel 
{
/**
 * MeterTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public StateGroupTreeModel() {
	super( new DBTreeNode("State Groups") );
}
/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 2:05:03 PM)
 * @return com.cannontech.database.data.lite.LiteBase[]
 */
public boolean isLiteTypeSupported( int liteType )
{
	return ( liteType == com.cannontech.database.data.lite.LiteTypes.STATEGROUP );
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return "State Group";
}
/**
 * This method was created in VisualAge.
 */
public void update() {

	com.cannontech.database.cache.DefaultDatabaseCache cache =
					com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

	synchronized(cache)
	{
		java.util.List stateGroups = cache.getAllStateGroups();

		java.util.Collections.sort( stateGroups, com.cannontech.database.data.lite.LiteComparators.liteStringComparator );

		DBTreeNode rootNode = (DBTreeNode) getRoot();
		rootNode.removeAllChildren();
		
		for( int i = 0; i < stateGroups.size(); i++ )
		{
			DBTreeNode stateGroupNode = new DBTreeNode( stateGroups.get(i));	
			rootNode.add( stateGroupNode );
		}
	}

	reload();
}
}
