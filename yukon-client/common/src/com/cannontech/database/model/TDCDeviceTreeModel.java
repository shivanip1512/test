package com.cannontech.database.model;

import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.database.data.pao.PAOGroups;

/**
 * Insert the type's description here.
 * Creation date: (4/18/00 11:39:02 AM)
 * @author: 
 * @Version: <version>
 */
public class TDCDeviceTreeModel extends DeviceTreeModel
{
/**
 * NoEmptyDeviceTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public TDCDeviceTreeModel() 
{
	super( true, new DBTreeNode("Object Types") );
}
/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 4:11:23 PM)
 * @param deviceType int
 */
public boolean isDeviceValid( int category_, int class_, int type_ )
{
	return class_ != DeviceClasses.SYSTEM
			&& class_ != PAOGroups.INVALID
			&& type_ != PAOGroups.INVALID
			&& category_ != PAOGroups.INVALID ;
}

protected synchronized java.util.List getCacheList(
        com.cannontech.database.cache.DefaultDatabaseCache cache ) 
{
    return cache.getAllYukonPAObjects();
}

// Override me if you want a sub class to do something different.
protected synchronized void runUpdate() 
{
	DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
	synchronized (cache)
	{
		java.util.List paos = getCacheList(cache);
		java.util.Collections.sort( paos, LiteComparators.litePaoTypeComparator );

		DBTreeNode rootNode = (DBTreeNode)getRoot();
		rootNode.removeAllChildren();
		
		int currType = Integer.MIN_VALUE;
		DummyTreeNode devTypeNode = null;
		for( int i = 0; i < paos.size(); i++ )
		{
			LiteYukonPAObject litPAO = (LiteYukonPAObject)paos.get(i);

			if( isDeviceValid(
					litPAO.getCategory(),
					litPAO.getPaoClass(),
					litPAO.getType() ) )
			{
				if( currType != litPAO.getType() )
				{
					devTypeNode = new DummyTreeNode(
						PAOGroups.getPAOTypeString(litPAO.getType()) );

					rootNode.add(devTypeNode);
				}

				DBTreeNode deviceNode = getNewNode(litPAO);
				devTypeNode.add(deviceNode);					
				deviceNode.setWillHaveChildren(true);

				currType = litPAO.getType();
			}


		} //for loop
		

	} //synch

	reload();	
}

}
