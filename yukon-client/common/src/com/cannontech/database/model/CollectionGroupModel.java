package com.cannontech.database.model;

import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.yukon.IDatabaseCache;

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
 * update method comment.
 */
public void update()
{
    IDatabaseCache cache = DefaultDatabaseCache.getInstance();

	synchronized(cache)
	{
	    //Returns a List of Strings
		java.util.List collGroups = cache.getAllDMG_CollectionGroups();
		java.util.Collections.sort( collGroups);
		
		DBTreeNode rootNode = (DBTreeNode) getRoot();
		rootNode.removeAllChildren();
		
		for( int i = 0; i < collGroups.size(); i++ )
		{
			DBTreeNode groupNode = new DBTreeNode( collGroups.get(i) );
			rootNode.add( groupNode );
		}
	}
	
	reload();
}
}
