package com.cannontech.database.model;

import com.cannontech.core.dao.StateDao;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.db.state.StateGroupUtils;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;

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
 */
public void update() {

	IDatabaseCache cache =
					com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

	synchronized(cache)
	{
		LiteStateGroup[] stateGroups = YukonSpringHook.getBean(StateDao.class).getAllStateGroups();

		DBTreeNode rootNode = (DBTreeNode) getRoot();
		rootNode.removeAllChildren();
		
		for( int i = 0; i < stateGroups.length; i++ )
		{
			LiteStateGroup grp = (LiteStateGroup)stateGroups[i];
			
			DBTreeNode stateGroupNode = new DBTreeNode( grp );				
			
			stateGroupNode.setIsSystemReserved( 
				grp.getStateGroupID() <= StateGroupUtils.SYSTEM_STATEGROUPID );

			rootNode.add( stateGroupNode );
		}
	}

	reload();
}
}
