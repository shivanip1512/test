package com.cannontech.database.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import com.cannontech.database.cache.functions.StateFuncs;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.db.state.StateGroupUtils;

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
		LiteStateGroup[] stateGroups = StateFuncs.getAllStateGroups();

		DBTreeNode rootNode = (DBTreeNode) getRoot();
		rootNode.removeAllChildren();
		
		for( int i = 0; i < stateGroups.length; i++ )
		{
			LiteStateGroup grp = (LiteStateGroup)stateGroups[i];
			
			//only show the editable states
			if( grp.getStateGroupID() > StateGroupUtils.SYSTEM_STATEGROUPID )
			{
				DBTreeNode stateGroupNode = new DBTreeNode( grp );	
				rootNode.add( stateGroupNode );
			}
			
		}
	}

	reload();
}
}
