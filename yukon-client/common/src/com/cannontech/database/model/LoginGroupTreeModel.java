package com.cannontech.database.model;

import java.util.Collections;
import java.util.List;

import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.db.user.YukonGroup;
import com.cannontech.yukon.IDatabaseCache;

/**
 * This type was created in VisualAge.
 */
public class LoginGroupTreeModel extends DBTreeModel 
{
/**
 * MeterTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public LoginGroupTreeModel() {
	super( new DBTreeNode("Login Groups") );
}
/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 2:05:03 PM)
 * @return com.cannontech.database.data.lite.LiteBase[]
 */
public boolean isLiteTypeSupported( int liteType )
{
	return ( liteType == com.cannontech.database.data.lite.LiteTypes.YUKON_GROUP );
}
/**
 * This method was created in VisualAge.
 */
public void update() {

	IDatabaseCache cache =
					com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

	synchronized(cache)
	{
		List<LiteYukonGroup> loginGroups = cache.getAllYukonGroups();
		Collections.sort( loginGroups, com.cannontech.database.data.lite.LiteComparators.liteStringComparator );

		DBTreeNode rootNode = (DBTreeNode) getRoot();
		rootNode.removeAllChildren();
		
		for( int i = 0; i < loginGroups.size(); i++ )
		{
			DBTreeNode lginGroupNode = new DBTreeNode( loginGroups.get(i) );

			lginGroupNode.setIsSystemReserved( 
					((LiteYukonGroup)loginGroups.get(i)).getGroupID() < YukonGroup.EDITABLE_MIN_GROUP_ID );

			rootNode.add( lginGroupNode );
		}
	}

	reload();
}
}
