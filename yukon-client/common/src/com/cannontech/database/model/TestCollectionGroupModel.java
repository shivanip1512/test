package com.cannontech.database.model;

import com.cannontech.database.cache.DefaultDatabaseCache;

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
 * update method comment.
 */
public void update()
{
    DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();

	synchronized(cache)
	{
	    //Returns a List of Strings
		java.util.List testCollGroups = cache.getAllDMG_AlternateGroups();
		java.util.Collections.sort( testCollGroups);
		
		DBTreeNode rootNode = (DBTreeNode) getRoot();
		rootNode.removeAllChildren();
		
		for( int i = 0; i < testCollGroups.size(); i++ )
		{
			DBTreeNode groupNode = new DBTreeNode( testCollGroups.get(i) );
			rootNode.add( groupNode );
		}
	}
	
	reload();
}
}
