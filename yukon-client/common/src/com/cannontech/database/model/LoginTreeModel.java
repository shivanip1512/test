package com.cannontech.database.model;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.UserUtils;

/**
 * This type was created in VisualAge.
 */
public class LoginTreeModel extends DBTreeModel 
{
/**
 * MeterTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public LoginTreeModel() 
{
	super( new DBTreeNode("Logins") );
}

/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 2:05:03 PM)
 * @return com.cannontech.database.data.lite.LiteBase[]
 */
public boolean isLiteTypeSupported( int liteType )
{
	return ( liteType == com.cannontech.database.data.lite.LiteTypes.YUKON_USER );
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return "Login";
}
/**
 * This method was created in VisualAge.
 */
public void update() {

	com.cannontech.database.cache.DefaultDatabaseCache cache =
					com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

	synchronized(cache)
	{
		java.util.List users = cache.getAllYukonUsers();

		java.util.Collections.sort( users, 
				com.cannontech.database.data.lite.LiteComparators.liteStringComparator );

		DBTreeNode rootNode = (DBTreeNode) getRoot();
		rootNode.removeAllChildren();
		
		for( int i = 0; i < users.size(); i++ )
		{
			DBTreeNode userNode = new DBTreeNode( users.get(i));
			
			userNode.setIsSystemReserved( 
				((LiteYukonUser)users.get(i)).getUserID() < 0 );
			//UserUtils.USER_YUKON_ID
			//UserUtils.USER_ADMIN_ID
			//UserUtils.USER_STARS_DEFAULT_ID
			if(((LiteYukonUser)users.get(i)).getUserID() > UserUtils.USER_STARS_DEFAULT_ID)
				rootNode.add( userNode );
		}
	}

	reload();
}
}
